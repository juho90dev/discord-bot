package com.jh.discord.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class WebClientConfig {
	
	
	@Bean
	public WebClient webClient() {
		DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
		factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
		
		return WebClient.builder() 
        		.uriBuilderFactory(factory)
        		.build(); 
	}

}
