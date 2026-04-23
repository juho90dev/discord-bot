package com.jh.discord.domain.dailyFortune;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class FortuneCrawler {

	private final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Whale/4.36.368.16 Safari/537.36";
	private final String[] NAMES = {"쥐띠", "소띠", "호랑이띠", "토끼띠", "용띠", "뱀띠", "말띠", "양띠", "원숭이띠", "닭띠", "개띠", "돼지띠"};
	
	
	// ================== 띠별 운세 =======================

//	// 전체 운세
//	public Document getAllFortune() throws Exception {
//		String query = "띠별 운세";
//		String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
//
//		String url = "https://search.naver.com/search.naver?query=" + encoded;
//
//		Document doc = Jsoup.connect(url).userAgent(USER_AGENT).get();
//		return doc;
//	}
	// 전체 운세
	public String getAllFortune() {
        StringBuilder totalResult = new StringBuilder();

        try {
            for (String name : NAMES) {
                // 상세 페이지 URL 접속
                String url = "https://search.naver.com/search.naver?query=" + URLEncoder.encode(name + " 운세", StandardCharsets.UTF_8);
                Document doc = Jsoup.connect(url)
                				.userAgent(USER_AGENT)
                				.timeout(3000)
                				.get();

                // 전체 요약 텍스트 추출
                Element detailText = doc.selectFirst("p.text._cs_fortune_text");

                if (detailText != null) {
                    totalResult.append("[").append(name).append("] : ");
                    totalResult.append(detailText.text()).append("\n");
                }

                
                Thread.sleep(100); 
            }
        } catch (Exception e) {
            return "크롤링 중 오류 발생: " + e.getMessage();
        }

        return totalResult.toString();
    }

	// 띠별로 가져오기
	public Document getPartFortune(String data) throws Exception {
		String encodedData = URLEncoder.encode(data + " 운세", StandardCharsets.UTF_8);
		String url = "https://search.naver.com/search.naver?query=" + encodedData;
		Document doc = Jsoup.connect(url).userAgent(USER_AGENT).get();
		return doc;
	}

	// ================== OKKY 옥희보살 개발자 운세 =======================

	public void okkyFortune() throws Exception{
		System.out.println("크롤러 진입");
		
    
	}

}
