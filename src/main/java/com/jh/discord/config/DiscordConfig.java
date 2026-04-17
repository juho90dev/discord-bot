package com.jh.discord.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jh.discord.discord.listener.DiscordListener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

@Configuration
public class DiscordConfig {

	// 전체 흐름 정리
	// 유저 메시지 흐름
	// Discord → Listener → Router → Service → API Client → 응답
	
	// 자동 알림 흐름
	//Scheduler → Service → Webhook → Discord 채널
	
	// Listener는 “입력만 받는다”
	// Router가 “명령 분기 담당”
	// Service는 “기능 로직”
	// Client는 “외부 API”
	// Webhook은 “자동 알림 전용”
	
	
	
	@Value("${discord.bot.token}")
	private String token;

	@Bean
	public JDA jda(DiscordListener discordListener) throws InterruptedException {

		try {

			JDA jda = JDABuilder.createDefault(token).addEventListeners(discordListener)
					.enableIntents(GatewayIntent.MESSAGE_CONTENT).build();

			jda.awaitReady();
			return jda;
		} catch (InterruptedException e) {
			throw new RuntimeException("JDA 초기화 실패", e);
		}
	}

}
