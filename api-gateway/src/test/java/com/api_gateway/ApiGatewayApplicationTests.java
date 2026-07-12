package com.api_gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {
  "spring.cloud.config.enabled=false",
  "eureka.client.enabled=false",
  "spring.main.web-application-type=reactive"
})
class ApiGatewayApplicationTests {

  @MockitoBean
  private DiscoveryClient discoveryClient;

  @Test
  void contextLoads() {
  }
}
