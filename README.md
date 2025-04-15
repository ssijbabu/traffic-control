# Deploy registry

sudo vi /etc/containers/registries.conf

[[registry]]
location = "localhost:5000"
insecure = true

podman pull docker.io/registry:2.8
podman run -d --network=bridge -p 5000:5000 --name kind-registry docker.io/registry:2.8

# Deploy Kind Setup

kind: Cluster
apiVersion: kind.x-k8s.io/v1alpha4
name: traffic-control-cluster
nodes:
  - role: control-plane
    image: docker.io/kindest/node:v1.32.0
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
  [plugins."io.containerd.grpc.v1.cri".registry.mirrors."kind-registry:5000"]
    endpoint = ["http://kind-registry:5000"]
- |-
  [plugins."io.containerd.grpc.v1.cri".registry.configs."kind-registry:5000".tls]
    insecure_skip_verify = true

kind create cluster --config kind/kind-config.yml

# Connect registry to the kind network

podman network connect "kind" "kind-registry"

# Deploy HAProxy

podman pull docker.io/haproxytech/kubernetes-ingress:3.0.9
kind load docker-image docker.io/haproxytech/kubernetes-ingress:3.0.9 -n traffic-control-cluster

podman pull docker.io/jaegertracing/example-hotrod:1.67.0
kind load docker-image docker.io/jaegertracing/example-hotrod:1.67.0 -n traffic-control-cluster

kubectl apply -f kind/ingress/

# Deploy Kafka (standalone)

podman pull docker.io/bitnami/kafka:3.9.0
podman tag docker.io/bitnami/kafka:3.9.0 localhost:5000/bitnami/kafka:3.9.0
podman push localhost:5000/bitnami/kafka:3.9.0

kubectl apply -f kafka/kafka.yml

# Deploy kube-state-metrics
podman pull docker.io/bitnami/kube-state-metrics:2.15.0
podman tag docker.io/bitnami/kube-state-metrics:2.15.0 localhost:5000/bitnami/kube-state-metrics:2.15.0
podman push localhost:5000/bitnami/kube-state-metrics:2.15.0

helm install kube-state-metrics oci://registry-1.docker.io/bitnamicharts/kube-state-metrics -f elk/helm/kubestatemetric-values.yml -n kube-system

# Deploy ELK
helm repo add elastic https://helm.elastic.co

podman pull docker.elastic.co/logstash/logstash:8.5.1
podman tag docker.elastic.co/logstash/logstash:8.5.1 localhost:5000/logstash/logstash:8.5.1 
podman push localhost:5000/logstash/logstash:8.5.1 

helm upgrade --install logstash elastic/logstash -f elk/helm/logstash-values.yml -n logging --create-namespace

podman pull docker.elastic.co/beats/filebeat:8.5.1
podman tag docker.elastic.co/beats/filebeat:8.5.1 localhost:5000/beats/filebeat:8.5.1
podman push localhost:5000/beats/filebeat:8.5.1

kubectl apply -f elk/manifests/filebeat-daemonset.yml

podman pull docker.elastic.co/beats/metricbeat:8.5.1
podman tag docker.elastic.co/beats/metricbeat:8.5.1 localhost:5000/beats/metricbeat:8.5.1
podman push localhost:5000/beats/metricbeat:8.5.1

kubectl apply -f elk/manifests/metricbeat-daemonset.yml

podman pull docker.elastic.co/apm/apm-server:8.5.1
podman tag docker.elastic.co/apm/apm-server:8.5.1 localhost:5000/apm/apm-server:8.5.1
podman push localhost:5000/apm/apm-server:8.5.1

helm upgrade --install apmserver elastic/apm-server -f elk/helm/apmserver-values.yaml -n logging --create-namespace

kubectl apply -f kind/kibana-nodeport.yml

# Deploy Appliation

podman build -t vehicle-registration-service:v1.0.1 apps/vehicle-registration-service/.
podman build -t traffic-control-service:v1.0.1 apps/traffic-control-service/.
podman build -t fine-collection-service:v1.0.1 apps/fine-collection-service/.

podman pull docker.io/bitnami/kafka:3.9.0

kind load docker-image docker.io/bitnami/kafka:3.9.0 -n traffic-control-cluster
kind load docker-image localhost/fine-collection-service:v1.0.1 -n traffic-control-cluster
kind load docker-image localhost/traffic-control-service:v1.0.1 -n traffic-control-cluster
kind load docker-image localhost/vehicle-registration-service:v1.0.1 -n traffic-control-cluster

kubectl apply -f kind/traffic-control-app.yaml

# Deploy kubeshark

podman pull docker.io/kubeshark/worker:v52.3.92
podman tag docker.io/kubeshark/worker:v52.3.92 localhost:5000/kubeshark/worker:v52.3.92
podman push localhost:5000/kubeshark/worker:v52.3.92

podman pull docker.io/kubeshark/hub:v52.3.92
podman tag docker.io/kubeshark/hub:v52.3.92 localhost:5000/kubeshark/hub:v52.3.92
podman push localhost:5000/kubeshark/hub:v52.3.92

podman pull docker.io/kubeshark/front:v52.3.92
podman tag docker.io/kubeshark/front:v52.3.92 localhost:5000/kubeshark/front:v52.3.92
podman push localhost:5000/kubeshark/front:v52.3.92
