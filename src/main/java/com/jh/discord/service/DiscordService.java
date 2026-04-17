package com.jh.discord.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DiscordService {
	
	@Value("${discord.webhook.url}")
	private String WEBHOOK_URL;
	
	public void sendMessage(String message) {

		try {
			RestTemplate restTemplate = new RestTemplate();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			Map<String, String> body = Map.of("content", message);

			HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

			restTemplate.postForObject(WEBHOOK_URL, request, String.class);

		} catch (Exception e) {
			System.out.println("디스코드 전송 실패: " + e.getMessage());
		}
	}
	

}
