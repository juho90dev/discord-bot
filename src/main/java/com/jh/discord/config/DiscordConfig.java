package com.jh.discord.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jh.discord.discord.listener.DiscordListener;
import com.jh.discord.discord.router.CommandRouter;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
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

			
			// .setActivity(Activity.competing("봇만들기"))
			// 봇 상태창 변경 가능
			
			
			JDA jda = JDABuilder.createDefault(token)
					//.setActivity(Activity.competing("명령어 기다리는 중..."))
					.addEventListeners(discordListener)
					.enableIntents(GatewayIntent.MESSAGE_CONTENT)
					.build();

			jda.awaitReady();
			
			
			// 디스코드 서버에 명령어 리스트를 등록
	        // updateCommands()를 호출해야 유저가 /를 쳤을 때 메뉴가 나온다.
			jda.updateCommands()
				.addCommands(
					Commands.slash("날씨", "온도와 먼지 정보를 통합해서 알려줍니다"),
					Commands.slash("먼지", "미세먼지 및 초미세먼지 수치를 알려줍니다")
						.addOptions(new OptionData(OptionType.STRING, "지역", "확인할 지역명을 입력하세요 (예:영통, 강남)", false)),
			        Commands.slash("기상", "현재 기온 정보를 알려줍니다")
			            .addOptions(new OptionData(OptionType.STRING, "지역", "확인할 지역명을 입력하세요 (예:영통, 강남)", false)),
					Commands.slash("로또", "로또 번호를 랜덤으로 알려줍니다"),
					//Commands.slash("운세", "오늘의 전체 띠별 운세 요약을 알려줍니다"),
					Commands.slash("운세", "오늘의 운세를 알려줍니다 (띠를 입력하지 않으면 전체 요약)")
						.addOptions(new OptionData(OptionType.STRING, "name", "확인할 띠를 입력하세요 (예: 말띠)", false)),
					Commands.slash("옥희", "옥희보살의 개발자 운세를 알려줍니다 (띠를 입력하지 않으면 전체 요약)")
						.addOptions(new OptionData(OptionType.STRING, "okky", "확인할 띠를 입력하세요 (예: 말띠)", false))
				).queue();
			
			return jda;
		} catch (InterruptedException e) {
			throw new RuntimeException("JDA 초기화 실패", e);
		}
	}

}
