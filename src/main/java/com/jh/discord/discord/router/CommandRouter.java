package com.jh.discord.discord.router;

import org.springframework.stereotype.Service;

import com.jh.discord.audio.service.AudioService;
import com.jh.discord.dailyAir.service.AirService;
import com.jh.discord.dailyFortune.service.FortuneService;
import com.jh.discord.lotto.service.LottoService;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

//@Service
@RequiredArgsConstructor
public class CommandRouter {

	private final AirService airService;
	private final LottoService lottoService;
	private final FortuneService fortuneService;
	private final AudioService audioService;
	
	
	
	public String router(SlashCommandInteractionEvent event) {
		String command = event.getName();
	    String result;
		
		
		switch(command) {
			case "날씨" -> {
				String dust = airService.getDustData();
                String temp = airService.getTempData();
                result = dust + "\n\n" + temp;
			}
			
			case "먼지" -> {
				OptionMapping dustOpt = event.getOption("지역");
				String location =(dustOpt == null)? "영통" : dustOpt.getAsString();
				result = airService.getDustLocation(location);
			}
			
			case "온도" -> {
				// "지역"이라는 이름의 옵션을 가져오고, 없으면 "영통" 사용
	            OptionMapping locOpt = event.getOption("지역");
	            String location = (locOpt == null) ? "영통" : locOpt.getAsString();
	            
	            result = airService.getTempLocation(location);
			}
			
			case "로또" -> {
				result = lottoService.lottoNum();
			}
			
			case "운세" -> {
	            OptionMapping nameOpt = event.getOption("name");
	            if (nameOpt != null) {
	                result = fortuneService.naverDetail(nameOpt.getAsString());
	            } else {
	                result = fortuneService.naverSummary();
	            }
			}
			
			case "옥희" -> {
				 OptionMapping nameOpt = event.getOption("okky");
		            if (nameOpt != null) {
		                result = fortuneService.okkyDetail(nameOpt.getAsString());
		            } else {
		                result = fortuneService.okkySummary();
		            }
			}
			
			default -> {
				result = null;
			}
		}
		
		return result;
	}
	public String route(String message) {
		// 메세지가 없으면 종료
		if (message == null || message.isBlank()) return null;
		
		String cleanMessage = message.startsWith("/") 
				? message.substring(1).trim() 
						: message.trim();
		
		// 공백 기준으로 분리
		String[] part = cleanMessage.split(" ");
		String command = part[0];
		
		String result;
		
		
		switch(command) {
			case "날씨" -> {
				String dust = airService.getDustData();
				String temp = airService.getTempData();
				result = dust + "\n\n" + temp;
			}
			
			case "먼지" -> {
				result = airService.getDustData();
			}
			
			case "온도" -> {
				result = airService.getTempData();
			}
			
			case "로또" -> {
				result = lottoService.lottoNum();
			}
			
			case "운세" -> {
				if(part.length > 1) {
					String fortune = part[1];
					result = fortuneService.naverDetail(fortune);
				} else {
					result = fortuneService.naverSummary();
				}
			}
			
			case "옥희" -> {
				if(part.length > 1) {
					String fortune = part[1];
					result = fortuneService.okkyDetail(fortune);
				}else {
					result = fortuneService.okkySummary();
					
				}
			}
			
			default -> {
				result = null;
			}
		}
		
		
		
		return result;
	}

}
