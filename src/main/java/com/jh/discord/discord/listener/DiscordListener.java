package com.jh.discord.discord.listener;

import org.springframework.stereotype.Component;

import com.jh.discord.discord.router.CommandRouter;
import com.jh.discord.domain.dailyAir.AirService;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
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

        if (response != null) {
            event.getChannel().sendMessage(response).queue();
        }
		
	}
	

	
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
	    // 모든 명령어에 대해 지연 응답 보내기
	    event.deferReply().queue(); 

	    // 비동기 쓰레드에서 로직 처리
	    new Thread(() -> {
	        try {
	            String command = event.getName();
	            
	            // 옵션값 가져오기
	            String option = "";
	            if (event.getOption("name") != null) {
	                option = " " + event.getOption("name").getAsString();
	            }
	            
	            String fullCommand = command + option;
	            
	            // 크롤링
	            String response = commandRouter.route(fullCommand);
	            
	            if (response == null || response.isEmpty()) {
	                response = "명령어를 처리하는 중 문제가 발생했거나 결과가 없습니다.";
	            }

	            // 데이터가 2000자를 넘는지 체크
	            if (response.length() > 2000) {
	                // 너무 길면 잘라서 보내거나 요약본으로 보냄
	                event.getHook().sendMessage("내용이 너무 길어서 앞부분만 보여드릴게요!\n\n" 
	                                            + response.substring(0, 1900) + "...").queue();
	            } else {
	                // getHook 사용
	            	// 지연응답으로 이미 응답을 한번 보낸 상태니 getHook()으로 보내야 한다
	                event.getHook().sendMessage(response).queue();
	            }

	        } catch (Exception e) {
	            // 예외 발생 시 사용자에게 알림
	            event.getHook().sendMessage("명령어 처리 중 오류가 발생했습니다: " + e.getMessage()).queue();
	        }
	    }).start();
	}
	

	
	
}
