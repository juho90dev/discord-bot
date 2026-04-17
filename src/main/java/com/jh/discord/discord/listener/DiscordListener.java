package com.jh.discord.discord.listener;

import org.springframework.stereotype.Component;

import com.jh.discord.discord.router.CommandRouter;
import com.jh.discord.domain.dailyAir.AirService;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Component
@RequiredArgsConstructor
public class DiscordListener extends ListenerAdapter{

	private final AirService airService;
	private final CommandRouter commandRouter;
	
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		
		if (event.getAuthor().isBot())return;

		String message = event.getMessage().getContentRaw();
		String response = commandRouter.route(message);
		
//		
//		String airData = airService.getDustData();
//		String weather = airService.getTempData();
//
//		// 메세지가 있으면 무조건 실행
//		if (message.contains("오늘")) {
//
//			// 디스코드 채팅으로 응답 (JDA)
//			event.getChannel().sendMessage(airData + "\n\n" + weather).queue();
//		} else if (message.contains("먼지")) {
//			event.getChannel().sendMessage(airData).queue();
//		} else if (message.contains("온도")) {
//			event.getChannel().sendMessage(weather).queue();
//		}
        if (response != null) {
            event.getChannel().sendMessage(response).queue();
        }
		
	}

}
