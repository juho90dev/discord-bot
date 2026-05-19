package com.jh.discord.router;

import org.springframework.stereotype.Service;

import com.jh.discord.audio.service.AudioService;
import com.jh.discord.dailyAir.service.AirService;
import com.jh.discord.dailyFortune.service.FortuneService;
import com.jh.discord.lotto.service.LottoService;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

@Service
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
			
			case "노래" -> {
				OptionMapping musicOpt = event.getOption("노래");
				
				if (musicOpt == null) {
                    result = "재생할 곡 이름이나 링크를 입력해주세요!";
                } else {
                	// 사용자가 입력한 텍스트(URL 또는 검색어)를 문자열로 추출
                	String query = musicOpt.getAsString();
                	
                	// [유튜브 검색 최적화]
                    // 사용자가 입력한 값이 링크(http...)가 아니라 단순 단어(예: "출첵")이라면,
                    // LavaPlayer가 유튜브에서 검색 결과를 찾아올 수 있도록 "ytsearch:" 접두사를 붙여준다
                    if (!query.startsWith("http")) {
                        query = "ytsearch:" + query;
                    }

                    // [음성 채널 연결 로직]
                    // 명령어를 입력한 유저가 현재 어느 음성 채널에 들어가 있는지 확인
                    if (event.getMember().getVoiceState().inAudioChannel()) {
                    	// 유저가 속해 있는 채널 정보를 가져온다
                    	AudioChannelUnion memberChannel = event.getMember().getVoiceState().getChannel();
                        // 봇을 해당 채널로 연결 (AudioManager 호출)
                        event.getGuild().getAudioManager().openAudioConnection(memberChannel);
                        
                        // [재생 수행]
                        // 텍스트 채널 정보와 처리된 쿼리(URL/검색어)를 AudioService에 넘긴다
                        audioService.loadAndPlay(event.getChannel().asTextChannel(), query);
                        result = "노래를 찾는 중입니다: " + musicOpt.getAsString();
                    } else {
                        result = "먼저 음성 채널에 들어가주세요!";
                    }
                }
				
				
				
			}
			
			
			case "스킵" -> {
			    // 현재 서버(Guild) 정보를 넘겨주어, 해당 서버의 재생 목록에서 다음 곡으로 넘기도록 지시.
			    audioService.skip(event.getGuild());
			    
			    // 스킵 명령 성공 메시지를 결과
			    result = "⏭️ 다음 곡으로 넘어갑니다.";
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
