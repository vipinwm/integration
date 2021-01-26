package hcl.graphql.services.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RootConfig {
		@Bean("restTemplate")
	   public RestTemplate getRestTemplate() {
	      return new RestTemplate();
	   }

}
