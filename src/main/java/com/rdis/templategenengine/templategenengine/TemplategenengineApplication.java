package com.rdis.templategenengine.templategenengine;

import java.time.Duration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
// @SpringBootApplication(exclude = { DataSourceAutoConfiguration.class,
// JndiDataSourceAutoConfiguration.class })
public class TemplategenengineApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(TemplategenengineApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(TemplategenengineApplication.class);
	}

	@Bean
	RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder
				.setConnectTimeout(Duration.ofSeconds(30))
				.setReadTimeout(Duration.ofSeconds(30))
				.build();
	}
}
