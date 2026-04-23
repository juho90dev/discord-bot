package com.jh.discord.discord.router;

import org.springframework.stereotype.Service;

import com.jh.discord.domain.dailyAir.AirService;
import com.jh.discord.domain.dailyFortune.FortuneService;
import com.jh.discord.domain.lotto.LottoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommandRouter {

	private final AirService airService;
	private final LottoService lottoService;
	private final FortuneService fortuneService;

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
					result = fortuneService.getPartFortune(fortune);
				} else {
					result = fortuneService.getAllFortune();
				}
			}
			
//			case "옥희" -> {
//				System.out.println("옥희 들어옴");
//				result = fortuneService.getOkkyFortune();
//			}
//			
			default -> {
				result = null;
			}
		}
//
//		if (message.contains("날씨"))
//			return airService.getDustData() + "\n\n" + airService.getTempData();
//
//		if (message.contains("먼지")) {
//			return airService.getDustData();
//		}
//
//		if (message.contains("온도")) {
//			return airService.getTempData();
//		}


		return result;
	}

}
