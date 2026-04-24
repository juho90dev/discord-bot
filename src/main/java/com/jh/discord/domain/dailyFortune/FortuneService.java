package com.jh.discord.domain.dailyFortune;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FortuneService {

	private final FortuneCrawler fortuneCrawler;
	private final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Whale/4.36.368.16 Safari/537.36";
	
	public String naverSummary() {
        String data = fortuneCrawler.getAllFortune();

        if (data == null || data.isEmpty()) {
            return "❌ 운세 데이터를 불러오는 데 실패했습니다.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("오늘의 네이버 띠별 운세 요약\n");
        sb.append("```\n"); // 디스코드 코드 블록 (md 스타일)
        sb.append(data);
        sb.append("```\n");

        return sb.toString();
	}
	
	
	public String naverDetail(String name) {
		System.out.println(">>> 네이버에서 운세를 불러오는 중...");
		String searchName = name.endsWith("띠") ? name : name + "띠";
		String data = fortuneCrawler.detailFortune(searchName);
		
		if (data.isEmpty()) {
	        System.out.println("데이터를 찾을 수 없습니다. 띠 이름을 정확히 입력해주세요 (예: 쥐띠)");
	        return null;
	    }
		StringBuilder sb = new StringBuilder();
		sb.append("--------------------------------------\n");
	    sb.append("        [" + searchName + " 연도별 운세]       \n");
	    sb.append("--------------------------------------\n");
	    sb.append(data).append("\n");
	    sb.append("--------------------------------------");

		return sb.toString();
	}
	
	// okky 옥희보살 개발자 운세
	public String okkySummary() {
	    // System.out.println(">>> 옥희보살 개발자 운세를 불러오는 중...");

		// 최신 글 찾기
	    String articleId = fortuneCrawler.lastPost();
	    // 해당 글에서 운세 데이터 크롤링
	    String data = fortuneCrawler.getOkkySummary(articleId);

	    if (data == null || data.isEmpty()) {
	        return "옥희보살 운세 데이터를 불러오는 데 실패했습니다.";
	    }

	    
	    StringBuilder sb = new StringBuilder();
	    sb.append("--------------------------------------\n");
	    sb.append("      [옥희보살's 띠별 요약 운세]       \n");
	    sb.append("--------------------------------------\n");
	    sb.append(data);
	    sb.append("--------------------------------------");
        return sb.toString();
	}
	
	public String okkyDetail(String name) {
		String articleId = fortuneCrawler.lastPost();
		
		String searchName = name.endsWith("띠") ? name : name + "띠";
		String data = fortuneCrawler.getOkkyDetail(articleId, searchName);
		
		if (data.isEmpty()) {
	        System.out.println("데이터를 찾을 수 없습니다. 띠 이름을 정확히 입력해주세요 (예: 쥐띠)");
	        return null;
	    }
		
		
		StringBuilder sb = new StringBuilder();
		sb.append("--------------------------------------\n");
	    sb.append("        [" + searchName + " 연도별 운세]       \n");
	    sb.append("--------------------------------------\n");
	    sb.append(data);
	    sb.append("--------------------------------------");
		return sb.toString();
	}
}
