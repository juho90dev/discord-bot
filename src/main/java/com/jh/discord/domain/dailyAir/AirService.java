package com.jh.discord.domain.dailyAir;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class AirService {

	private final WebClient webClient;

	@Value("${api.weather.service-key}")
	private String SERVICE_KEY;

	private final String DUST_URL = "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty";

	private final String TEMP_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst";

	private final AirApiClient airApiClient;

	private final ObjectMapper mapper = new ObjectMapper();

	public String getDustData() {
		try {

			// 그냥 넣으면 인식을 못하니 인코딩해줘야함
			String encodedStation = URLEncoder.encode("영통동", StandardCharsets.UTF_8);

			String response = airApiClient.dustApi(encodedStation);

			// JSON 파싱

			JsonNode root = mapper.readTree(response);
			// 데이터 위치
			JsonNode item = root.path("response").path("body").path("items").get(0);

			// pm10 → 미세먼지
			// pm25 → 초미세먼지
			// time → 측정 시간
			String pm10 = item.path("pm10Value").asString();
			String pm25 = item.path("pm25Value").asString();
			String time = item.path("dataTime").asString();

			// "-"는 값 없음 의미 → 숫자로 변환하면 에러 방지
			if ("-".equals(pm10) || "-".equals(pm25)) {
				log.warn("미세먼지 데이터 없음");

				return "영통구 미세먼지 측정 데이터가 현재 없습니다. (시간: %s)".formatted(time);
			}

			// 문자열 -> 숫자열
			int pm10Val = Integer.parseInt(pm10);
			int pm25Val = Integer.parseInt(pm25);
			int worst = Math.max(pm10Val, pm25Val);
			String status = "";

			if (worst <= 30) {
				status = "좋음";
			} else if (worst < 81) {
				status = "보통";
			} else if (worst < 151) {
				status = "나쁨";
			} else {
				status = "매우 나쁨";
			}

			log.info("미세먼지: {}, 초미세먼지: {}, 상태: {}", pm10, pm25, status);
			return """
					영통구 미세먼지 정보
					측정시간: %s

					미세먼지: %s
					초미세먼지: %s
					대기질 상태는 [%s] 입니다.
					""".formatted(time, pm10, pm25, status);

		} catch (Exception e) {
			log.error("미세먼지 데이터 처리 오류", e);
			return "데이터를 가져오지 못했습니다: " + e.getMessage();
		}
	}

	
	// 온도
	public String getTempData() {
		try {
			LocalDateTime now = LocalDateTime.now();

			// 기상청 실황 데이터는 매시 40분 이후에 생성된다고한다.
			// 40분 전이라면 '이전 시간' 데이터를 조회
			if (now.getMinute() < 40) {
				now = now.minusHours(1);
			}

			// 날짜 포맷 (YYYYMMDD)
			String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
			// 시간 포맷 (HH00 - 실황은 항상 정각 단위로 호출)
			String baseTime = now.format(DateTimeFormatter.ofPattern("HH00"));
			// 화면 출력용 데이터 (새로 추가)
			String displayTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00"));

			// 클라이언트를 통해 데이터 호출
			String response = airApiClient.tempApi(baseDate, baseTime);
			
			JsonNode root = mapper.readTree(response);
			JsonNode items = root.path("response").path("body").path("items").path("item");

			String temp = "";
			String rain = "0";
			String humidity = "";
			String pty = "0";

			for (JsonNode item : items) {
				String category = item.path("category").asString();
				String value = item.path("obsrValue").asString();

				switch (category) {
					case "T1H" -> temp = value;
					case "RN1" -> rain = value;
					case "REH" -> humidity = value;
					case "PTY" -> pty = value;
				}
			}

			// 강수 형태 해석
			String weatherStatus = switch (pty) {
				case "1" -> "비";
				case "2" -> "비/눈";
				case "3" -> "눈";
				case "5" -> "빗방울";
				case "6" -> "빗방울/눈날림";
				case "7" -> "눈날림";
				default -> "맑음";
			};

			String time = baseDate + " " + baseTime;
			log.info("기온: {}, 강수량: {}, 날씨: {}", temp, rain, weatherStatus);
			return """
					영통구 날씨 정보
					측정시간: %s

					기온: %s℃
					강수량: %smm
					날씨: %s
					""".formatted(displayTime, temp, rain, weatherStatus);
		} catch (Exception e) {
			return "날씨 데이터를 불러오는 데 실패했습니다.";
		}
	}
}
