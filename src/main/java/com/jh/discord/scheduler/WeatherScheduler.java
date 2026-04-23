package com.jh.discord.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jh.discord.domain.dailyAir.AirService;
import com.jh.discord.service.DiscordService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class WeatherScheduler {

	private final AirService airService;
	private final DiscordService discordService;

	//@Scheduled(cron = "0 5 10/4 * * *", zone = "Asia/Seoul")
	public void sendDailyWeather() {

		String dustData;
		String tempData;

		try {
			
			dustData = airService.getDustData();
			
		} catch (Exception e) {
			log.error("미세먼지 조회 실패", e);
			dustData = "미세먼지 정보 조회 실패";
		}
		
		try {
			tempData = airService.getTempData();
			
		}catch(Exception e) {
			log.error("기온 조회 실패", e);
			tempData = "기온 정보 조회 실패";
		}

		String message = dustData + "\n\n" + tempData;

		discordService.sendMessage(message);

		log.info("날씨 정보 전송 완료 - 메시지 내용: {}", message);
	}

	// 오전 10시 5분
	@Scheduled(cron = "0 5 10 * * *", zone = "Asia/Seoul")
	public void weatherMorning() {
		sendDailyWeather();
	}
	
	// 오후 1시	
	@Scheduled(cron = "0 0 13 * * *", zone = "Asia/Seoul")
	public void weatherAfternoon() {
		sendDailyWeather();
	}
	
	// 오후 5시
	@Scheduled(cron = "0 0 17 * * *", zone = "Asia/Seoul")
	public void weatherEvening() {
		sendDailyWeather();
	}
	
	
}
