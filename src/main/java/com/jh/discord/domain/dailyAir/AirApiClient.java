package com.jh.discord.domain.dailyAir;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AirApiClient {
	
	@Value("${api.weather.service-key}")
	private String SERVICE_KEY;

	private final WebClient webClient;
	
	private final String DUST_URL = "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty";

	private final String TEMP_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst";
	
	public String dustApi(String stationName) {
		
		URI uri = UriComponentsBuilder.fromUriString(DUST_URL)
                .queryParam("serviceKey", SERVICE_KEY)
                .queryParam("returnType", "json")
                .queryParam("numOfRows", "1")
                .queryParam("pageNo", "1")
                .queryParam("stationName", stationName)
                .queryParam("dataTerm", "DAILY")
                .queryParam("ver", "1.0")
                .build(true).toUri();
		
		String response = webClient.get().uri(uri).retrieve().bodyToMono(String.class).block();
		
        return response;
		
	}

	public String tempApi(String baseDate, String baseTime) {
		URI uri = UriComponentsBuilder.fromUriString(TEMP_URL)
                .queryParam("serviceKey", SERVICE_KEY)
                .queryParam("numOfRows", "15")
                .queryParam("pageNo", "1")
                .queryParam("dataType", "JSON")
                .queryParam("base_date", baseDate)
                .queryParam("base_time", baseTime)
                .queryParam("nx", "62")
                .queryParam("ny", "120")
                .build(true).toUri();
		String response = webClient.get().uri(uri).retrieve().bodyToMono(String.class).block();
		return response;
	}
	
	
}
