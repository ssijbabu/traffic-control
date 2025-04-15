#!/bin/sh
set -o errexit


# 1. Create registry container unless it already exists
reg_name='kind-registry'
reg_port='5001'
if [ "$(docker inspect -f '{{.State.Running}}' "${reg_name}" 2>/dev/null || true)" != 'true' ]; then
  docker run \
    -d --restart=always -p "127.0.0.1:${reg_port}:5000" --network bridge --name "${reg_name}" \
    registry:2.8
  echo "--> Local registry created."
else
  echo "--> Local registry already exists."
fi

# 2. Create kind cluster with containerd registry config dir enabled
#
# NOTE: the containerd config patch is not necessary with images from kind v0.27.0+
# It may enable some older images to work similarly.
# If you're only supporting newer relases, you can just use `kind create cluster` here.
#
# See:
# https://github.com/kubernetes-sigs/kind/issues/2875
# https://github.com/containerd/containerd/blob/main/docs/cri/config.md#registry-configuration
# See: https://github.com/containerd/containerd/blob/main/docs/hosts.md
if ! kind get clusters | grep -q "kind"; then
  cat <<EOF | kind create cluster --config=-
kind: Cluster
apiVersion: kind.x-k8s.io/v1alpha4
nodes:
  - role: control-plane
    image: kindest/node:v1.32.0
    extraPortMappings:
      - containerPort: 31386
        hostPort: 31386
        protocol: TCP
      - containerPort: 31014
        hostPort: 31014
        protocol: TCP
      - containerPort: 32496
        hostPort: 32496
        protocol: TCP
      - containerPort: 9092 
        hostPort: 30092 
        protocol: TCP
containerdConfigPatches:
- |-
  [plugins."io.containerd.grpc.v1.cri".registry]
    config_path = "/etc/containerd/certs.d"
EOF
  echo "--> Kind cluster created."
else
  echo "--> Kind cluster already exists."
fi

# 3. Add the registry config to the nodes
#
# This is necessary because localhost resolves to loopback addresses that are
# network-namespace local.
# In other words: localhost in the container is not localhost on the host.
#
# We want a consistent name that works from both ends, so we tell containerd to
# alias localhost:${reg_port} to the registry container when pulling images
REGISTRY_DIR="/etc/containerd/certs.d/localhost:${reg_port}"
for node in $(kind get nodes); do
  docker exec "${node}" mkdir -p "${REGISTRY_DIR}"
  cat <<EOF | docker exec -i "${node}" cp /dev/stdin "${REGISTRY_DIR}/hosts.toml"
[host."http://${reg_name}:5000"]
EOF
  echo "--> Registry config added to node ${node}."
done

# 4. Connect the registry to the cluster network if not already connected
# This allows kind to bootstrap the network but ensures they're on the same network
if [ "$(docker inspect -f='{{json .NetworkSettings.Networks.kind}}' "${reg_name}")" = 'null' ]; then
  docker network connect "kind" "${reg_name}"
  echo "--> Registry connected to kind network."
fi

# 5. Document the local registry
# https://github.com/kubernetes/enhancements/tree/master/keps/sig-cluster-lifecycle/generic/1755-communicating-a-local-registry
cat <<EOF | kubectl apply -f -
apiVersion: v1
kind: ConfigMap
metadata:
  name: local-registry-hosting
  namespace: kube-public
data:
  localRegistryHosting.v1: |
    host: "localhost:${reg_port}"
    help: "https://kind.sigs.k8s.io/docs/user/local-registry/"
EOF

# 6. Setup Kafka
if ! docker image inspect "localhost:5001/bitnami/kafka:3.9.0" >/dev/null 2>&1; then
  if ! docker image inspect "docker.io/bitnami/kafka:3.9.0" >/dev/null 2>&1; then
    docker pull docker.io/bitnami/kafka:3.9.0
  fi
  docker tag docker.io/bitnami/kafka:3.9.0 localhost:5001/bitnami/kafka:3.9.0
  docker push localhost:5001/bitnami/kafka:3.9.0
  echo "--> Kafka image published to local registry."
else
  echo "--> Kafka image already exist in local registry"
fi

if [ -z "$(kubectl get deployment kafka -n kafka --ignore-not-found -o name)" ]; then
  kubectl apply -f kafka/kafka.yml
  echo "--> Kafka deployed."
else
  echo "--> Kafka already deployed."
fi

# 7. Setup kube-state-metrics
if ! docker image inspect "localhost:5001/bitnami/kube-state-metrics:2.15.0" >/dev/null 2>&1; then
  if ! docker image inspect "docker.io/bitnami/kube-state-metrics:2.15.0" >/dev/null 2>&1; then
    docker pull docker.io/bitnami/kube-state-metrics:2.15.0
  fi
  docker tag docker.io/bitnami/kube-state-metrics:2.15.0 localhost:5001/bitnami/kube-state-metrics:2.15.0
  docker push localhost:5001/bitnami/kube-state-metrics:2.15.0
  echo "--> kube-state-metrics image published to local registry."
else
  echo "--> kube-state-metrics image already exist in local registry"
fi

if ! helm list -n kube-system | grep -q "kube-state-metrics"; then
  helm install kube-state-metrics oci://registry-1.docker.io/bitnamicharts/kube-state-metrics -f elk/helm/kubestatemetric-values.yml -n kube-system
  echo "kube-state-metrics deployed."
  if helm list -n kube-system | grep -q "kube-state-metrics"; then
    echo "--> kube-state-metrics helm chart is installed."
  else
    echo "--> Error: kube-state-metrics helm chart is not installed."
  fi
else
  echo "--> kube-state-metrics already deployed."
fi

# 6. Add elastic helm charts repo
if ! helm repo list | grep -q "elastic"; then
  helm repo add elastic https://helm.elastic.co
  helm repo update
  echo "--> Elastic helm repo added."
else
  echo "--> Elastic helm repo already exists."
fi

# 7. Setup Logstash
if ! docker image inspect localhost:5001/logstash/logstash:8.5.1 >/dev/null 2>&1; then
  if ! docker image inspect docker.elastic.co/logstash/logstash:8.5.1 >/dev/null 2>&1; then
    docker pull docker.elastic.co/logstash/logstash:8.5.1
  fi
  docker tag docker.elastic.co/logstash/logstash:8.5.1 localhost:5001/logstash/logstash:8.5.1
  docker push localhost:5001/logstash/logstash:8.5.1
  echo "--> Logstash image published to local registry."
else
  echo "--> Logstash image already exist in local registry"
fi

if ! helm list -n logging | grep -q "logstash"; then  
  helm upgrade --install logstash elastic/logstash -f elk/helm/logstash-values.yml -n logging --create-namespace
  echo "--> Logstash deployed."
  if helm list -n logging | grep -q "logstash"; then
    echo "--> Logstash helm chart is installed."
  else
    echo "--> Error: Logstash helm chart is not installed."
  fi
else
  echo "--> Logstash helm chart already installed."
fi

# 8. Setup Metricbeat
if ! docker image inspect localhost:5001/beats/metricbeat:8.5.1 >/dev/null 2>&1; then
  if ! docker image inspect docker.elastic.co/beats/metricbeat:8.5.1 >/dev/null 2>&1; then
    docker pull docker.elastic.co/beats/metricbeat:8.5.1
  fi
  docker tag docker.elastic.co/beats/metricbeat:8.5.1 localhost:5001/beats/metricbeat:8.5.1
  docker push localhost:5001/beats/metricbeat:8.5.1
  echo "--> Metricbeat image published to local registry."
else
  echo "--> Metricbeat image already exist in local registry"
fi

if [ -z "$(kubectl get daemonset metricbeat -n kube-system --ignore-not-found -o name)" ]; then
  kubectl apply -f elk/manifests/metricbeat-daemonset.yml
  echo "--> Metricbeat deployed."
else
  echo "--> Metricbeat already deployed."
fi

# 9. Setup Filebeat 
if ! docker image inspect localhost:5001/beats/filebeat:8.5.1 >/dev/null 2>&1; then
  if ! docker image inspect docker.elastic.co/beats/filebeat:8.5.1 >/dev/null 2>&1; then
    docker pull docker.elastic.co/beats/filebeat:8.5.1
  fi
  docker tag docker.elastic.co/beats/filebeat:8.5.1 localhost:5001/beats/filebeat:8.5.1
  docker push localhost:5001/beats/filebeat:8.5.1
  echo "--> Filebeat image published to local registry."
else
  echo "--> Filebeat image already exist in local registry"
fi

if [ -z "$(kubectl get daemonset filebeat -n kube-system --ignore-not-found -o name)" ]; then
  kubectl apply -f elk/manifests/filebeat-daemonset.yml
  echo "--> Filebeat deployed."
else
  echo "--> Filebeat already deployed."
fi
