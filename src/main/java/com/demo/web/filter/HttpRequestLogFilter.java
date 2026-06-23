package com.demo.web.filter;

import static com.demo.web.util.Constants.MDC_REQUEST_ID;

import com.demo.annotation.Component;
import com.demo.web.model.HttpRequest;
import com.demo.web.model.HttpResponse;
import java.util.UUID;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@Component
public class HttpRequestLogFilter extends HttpRequestFilter {

  private static final Logger LOG = LoggerFactory.getLogger(HttpRequestLogFilter.class);

  @Getter
  private final int priority = Integer.MIN_VALUE; // should be executed first

  @Override
  public HttpResponse filter(HttpRequest request,
      HttpRequestFilterChain chain) {
    var reqId = getRequestId(request);
    long startTime = System.currentTimeMillis();
    try {
      MDC.put(MDC_REQUEST_ID, reqId);
      LOG.info("Received request {}", request);
      return chain.doNext(request);
    } finally {
      LOG.info("Request complete in {} mls", System.currentTimeMillis() - startTime);
      MDC.remove(MDC_REQUEST_ID);
    }
  }

  private String getRequestId(HttpRequest request) {
    return request.getHeaders().getOne("x-request-id").orElse(UUID.randomUUID().toString());
  }
}
