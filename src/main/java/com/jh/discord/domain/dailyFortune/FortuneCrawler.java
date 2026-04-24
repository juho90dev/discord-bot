package com.jh.discord.domain.dailyFortune;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                				.referrer("https://www.naver.com")
                				.userAgent(USER_AGENT)
                				.timeout(5000)
                				.get();

                // 전체 요약 텍스트 추출
                Element detailText = doc.selectFirst("p.text._cs_fortune_text");

                if (detailText != null) {
                    totalResult.append("**[").append(name).append("]**\n");
                    totalResult.append(detailText.text()).append("\n");
                }

                
                // 차단 방지를 위해 대기시간 0.8로 설정
                Thread.sleep(700); 
            }
        } catch (Exception e) {
            return "크롤링 중 오류 발생: " + e.getMessage();
        }

        return totalResult.toString();
    }

//	// 띠별로 가져오기
//	public Document getPartFortune(String data) throws Exception {
//		String encodedData = URLEncoder.encode(data + " 운세", StandardCharsets.UTF_8);
//		String url = "https://search.naver.com/search.naver?query=" + encodedData;
//		Document doc = Jsoup.connect(url).userAgent(USER_AGENT).get();
//		return doc;
//	}
	public String detailFortune(String name) {
		
		String searchName = name.endsWith("띠") ? name : name + "띠";
		try {
			String encoded = URLEncoder.encode(searchName + " 운세", StandardCharsets.UTF_8);
			String url = "https://search.naver.com/search.naver?query=" + encoded;
			Document doc = Jsoup.connect(url).userAgent(USER_AGENT).get();


			
			Element todayPanel = doc.selectFirst("div._resultPanel");
			if (todayPanel == null) return searchName + "의 정보를 찾을 수 없습니다.";
			
			StringBuilder sb = new StringBuilder();

			if (todayPanel != null) {
				// 개띠 운세 리스트를 추출 (연도별 운세는 _cs_fortune_list 안의 div)
				Elements items = todayPanel.select("dl._cs_fortune_list div");

				for (Element item : items) {
					String year = item.select("dt").text().trim();
					String content = item.select("dd").text().trim();

					if (!year.isEmpty()) {
						sb.append(year).append(" : ").append(content).append("\n");
					}
				}
			}

			return sb.length() == 0 ? searchName + "의 상세 정보를 찾을 수 없습니다." : sb.toString();

		} catch (Exception e) {
			return name + " 정보를 가져오는 데 실패했습니다.";
		}
	}
	// ================== OKKY 옥희보살 개발자 운세 =======================

	public String lastPost() {
		try {
			// 일반 주소로는 next.js라서 데이터가 없다(html-> 그 후에 js로 데이터 뿌리는 형식)
			// 개발자모드 - network에서 해당 주소로 보면 데이터를 받을 수 있다.
			String url = "https://okky.kr/users/194430/articles?_rsc=1jhc3";
			
			// 데이터 가져오기 
			String body = Jsoup.connect(url)
							.userAgent(USER_AGENT)
							// 서버가 보내는 응답의 Content-Type(콘텐츠 형식)을 검사하지 않고, 어떤 형식의 데이터든 무시하고 무조건 가져오도록 설정하는 옵션
							.ignoreContentType(true)
							// 버의 응답 결과를 HTML 객체가 아닌, 하나의 거대한 문자열(String)로 가져와야한다.
							.execute()
							.body();
			
			// 가져온 전체 텍스으 중에 "운세"가 있는지 확인
			if (!body.contains("운세")) return null;
			
			// 운세가 시작되는 위치 탐색
			int index = body.indexOf("운세");
			
			// href="/articles/1555853">[오늘의 개발자 운세] 2026년 4월 23일 목요일</a>
			// 게시글의 ID(1555853)가 제목([오늘의 운세...])보다 먼저 나오기 때문에
			// 운세"라는 글자 바로 앞 150자만 자른다
			// Math.max(0, ...) 인덱스가 -가 되는것 방지
            String searchArea = body.substring(Math.max(0, index - 150), index);
            
            // ID 추출
            // \d → [0-9] (0, 1, 2, 3, 4, 5, 6, 7, 8, 9 중 하나)
            // 숫자(\d)가 최소 6번에서 최대 8번 연속되는 패턴
            Matcher m = Pattern.compile("\\d{6,8}").matcher(searchArea);
            String id = "";
            
            // 탐색범위에서 찾기
            while (m.find()) {
            	id = m.group();
            }
            
            System.out.println("최근 게시글 : "+id);
            return id;
			
		}catch(Exception e) {
			System.err.println("크롤링 중 오류 발생: " + e.getMessage());
			
		}
		return null;
	}
	
	public String getOkkySummary(String articleId) {
		// 게시글 Id 가 없는 경우 
	    if (articleId == null) return "ID가 없습니다.";
	    
	    StringBuilder sb = new StringBuilder();
	    try {
	    	// OKKY 상세 페이지 접속 URL 생성 (Next.js 데이터를 받기 위해 _rsc 파라미터 사용)
	        String url = "https://okky.kr/articles/" + articleId + "?_rsc=1jhc3";
	        
	        // ID찾을때랑 같다
	        String body = Jsoup.connect(url)
	        				.userAgent(USER_AGENT)
	        				.timeout(5000)
	        				.ignoreContentType(true)
	        				.execute()
	        				.body();
	        
	        // 데이터 정리 (Next.js 특유의 이스케이프 문자 제거)
	        String html = body.replace("\\\"", "\"").replace("\\n", "\n");
	        Document doc = Jsoup.parse(html);

	        // 미리 정의된 띠 이름 배열(NAMES)을 순회하며 데이터 추출
	        for (String name : NAMES) {
	        	
	            // HTML의 data-label 값은 "쥐", "소" 형태이므로 "띠" 글자를 제거
	            String label = name.replace("띠", "");
	            
	            // 해당 띠의 정보를 담고 있는 특정 <div> 블록을 선택
	            Element block = doc.selectFirst("div[data-label=" + label + "]");

	            if (block != null) {
	            	// 띠 요약 내용이 들어있는 내부 div 선택
	                Element detailText = block.selectFirst("div.mt-1");
	                
	                if (detailText != null) {
	                	// 결과 문자열 조립
	                	sb.append("**[").append(name).append("]**\n");
	                    // ownText() : 자식 태그의 텍스트는 버리고, 딱 그 부모 태그가 직접 가지고 있는 텍스트만 가져오는 함수
	                	// text() : 자식 태그의 글자까지 모두 가져온다
	                	sb.append(detailText.ownText().trim()).append("\n\n");
	                }
	            }
	        }
	    } catch (Exception e) {
	        return "크롤링 중 오류 발생: " + e.getMessage();
	    }

	    return sb.toString();
	}

	
	public String getOkkyDetail(String articleId, String name) {
		
		String searchName = name.endsWith("띠") ? name : name + "띠";
		if (articleId == null) return "ID가 없습니다.";
		
		StringBuilder sb = new StringBuilder();
		
		try {
	        String url = "https://okky.kr/articles/" + articleId + "?_rsc=1jhc3";
	        
	        String body = Jsoup.connect(url)
	        					.userAgent(USER_AGENT)
	        					.ignoreContentType(true)
	        					.execute()
	        					.body();
	        
	        // JSON 문자열 속에 숨겨진 HTML 태그들을 깨끗하게
	        String html = body.replace("\\\"", "\"").replace("\\n", "\n");
	        Document doc = Jsoup.parse(html);
	        
	        // 파라미터로 받은 텍스트로 위치 찾기 
	        Element block = doc.selectFirst("span:containsOwn(" + searchName + ")");

	        if (block != null) {
	            // span의 부모 div로 이동
	            Element parentDiv = block.parent();
	            
	            // 부모 div 안에서 <ul>과 <li>를 찾아서 크롤링
	            Elements items = parentDiv.select("ul li");

	            for (Element item : items) {
	                // 년생 부분만 추출
	                String year = item.select("strong").text().trim();
	                // 년생을 제외한 나머지 텍스트 추출 (ownText 활용)
	                String desc = item.ownText().replace(":", "").trim(); 
	                
	                if (!year.isEmpty()) {
	                    // 3. 네이버와 똑같은 형식으로 조립
	                    sb.append(year).append(" : ").append(desc).append("\n");
	                }
	            }
	        }
	        // 최종 결과 반환
	        return sb.length() == 0 ? searchName + "의 상세 정보를 찾을 수 없습니다." : sb.toString();

	    } catch (Exception e) {
	        return "크롤링 중 오류 발생: " + e.getMessage();
	    }
	}

}
