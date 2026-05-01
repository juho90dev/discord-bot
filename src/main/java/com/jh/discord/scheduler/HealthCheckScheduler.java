package com.jh.discord.scheduler;

import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.jh.discord.service.DiscordService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class HealthCheckScheduler {
	
	private final WebClient webClient;
	private final DiscordService discordService;
	
	

//	@Scheduled(cron = "0 30 9 * * *", zone = "Asia/Seoul")
//	public void checkServer() {
//		webClient.get()
//				.uri("/actuator/health")
//				.retrieve()
//				.bodyToMono(Map.class)
//				.map(response -> response.get("status").toString())
//				.subscribe(
//						staus -> {
//							if(!"UP".equals(staus)) {
//								discordService.sendMessage("Music Converter 서버가 죽었다!!!! 상태:"+ staus);
//							}
//						},
//						error -> {
//							discordService.sendMessage("Music Converter 죽었다! 서버가 응답하지 않음.");
//						}
//					);
//				
//	}
}
