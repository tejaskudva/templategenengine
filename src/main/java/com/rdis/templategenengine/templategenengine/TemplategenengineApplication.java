package com.rdis.templategenengine.templategenengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
//@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, JndiDataSourceAutoConfiguration.class })
public class TemplategenengineApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(TemplategenengineApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(TemplategenengineApplication.class);
	}

}
