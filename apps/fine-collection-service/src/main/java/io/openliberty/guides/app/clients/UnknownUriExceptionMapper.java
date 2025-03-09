package io.openliberty.guides.app.clients;

import java.util.logging.Logger;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

@Provider
public class UnknownUriExceptionMapper
    implements ResponseExceptionMapper<UnknownUriException> {
  Logger LOG = Logger.getLogger(UnknownUriExceptionMapper.class.getName());

  @Override
  public boolean handles(int status, MultivaluedMap<String, Object> headers) {
    LOG.info("status = " + status);
    return status == 404;
  }

  @Override
  public UnknownUriException toThrowable(Response response) {
    return new UnknownUriException();
  }
}