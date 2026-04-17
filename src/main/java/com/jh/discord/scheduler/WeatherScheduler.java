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

	@Scheduled(cron = "0 0 6/4 * * *")
	public void sendDailyWeather() {

		String dustData = airService.getDustData();
		String tempData = airService.getTempData();
		
		String message = dustData + "\n\n" + tempData;

		discordService.sendMessage(message);
		
		log.info("날씨 정보 전송 완료 - 메시지 내용: {}", message);
	}

}
