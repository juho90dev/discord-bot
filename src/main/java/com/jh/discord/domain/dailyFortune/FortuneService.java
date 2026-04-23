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

//	public String getAllFortune() {
//		try {
//
//			Document doc = fortuneCrawler.getAllFortune();
//			//Elements items = doc.select("ul._list li");
//			Elements items = doc.select("ul.sign_lst li");
//			StringBuilder sb = new StringBuilder("**오늘의 띠별 운세 요약**");
//
//			for (Element item : items) {
//				// 띠 이름
//				String name = item.select("dt.tit").text();
//				// 운세 내용
//				String text = item.select("p").text();
//				if (!name.isEmpty()) {
//					sb.append("**[").append(name).append("]**");
//					sb.append("> ").append(text).append("\n\n");
//				}
//			}
//			
//			if (items.isEmpty()) {
//	            return "운세 데이터를 찾을 수 없습니다. (HTML 구조 확인 필요)";
//	        }
//
//			return sb.toString();
//		} catch (Exception e) {
//			return "오늘의 운세를 가져오는 중 오류가 발생했습니다.";
//		}
//	}
	public String getAllFortune() {
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
	
	
	public String getPartFortune(String data) {

		try {
			Document doc = fortuneCrawler.getPartFortune(data);
			StringBuilder sb = new StringBuilder();
			
			sb.append(data).append(" 오늘의 운세**\n\n");
			
			System.out.println(sb);
			
			// 전체 요약 운세 가져오기 (p 태그)
			//String summary = doc.select("li.lst_r p").first().text();
			Element summaryElement = doc.selectFirst("p._cs_fortune_text");
			System.out.println("dfasfa"+summaryElement);
			
//			if (!summary.isEmpty()) {
//				sb.append("**요약 :** ").append(summary).append("\n");
//				sb.append("──────────────────\n");
//			}
			if (summaryElement != null) {
	            sb.append("**[총평]**\n");
	            sb.append("> ").append(summaryElement.text()).append("\n\n");
	            sb.append("──────────────────\n");
	        }
			
			
			// [연도별] 상세 내용 가져오기 (dl.lst_infor)
			//Elements items = doc.select("dl.lst_infor div");
			Elements items = doc.select("dl._cs_fortune_list div");
			
			for (Element item : items) {
				
				// xx년생
				String year = item.select("dt").text();
				// 운세 내용
				String content = item.select("dd").text(); 
				
				System.out.println(year);
				
//				if (!year.isEmpty() && !content.isEmpty()) {
//					sb.append("-> **").append(year).append("** : ").append(content).append("\n");
//				}
				
				if (!year.isEmpty() && !content.isEmpty()) {
	                sb.append("📌 **").append(year).append("**\n");
	                sb.append("└ ").append(content).append("\n\n");
	            }
			}
			
			if (sb.length() < 20) {
	            return data + " 정보를 찾을 수 없습니다. (HTML 구조가 변경되었을 수 있습니다.)";
	        }
			
			
			return sb.toString();

		} catch (Exception e) {
			return data + " 정보를 가져오지 못했습니다.";
		}
	}

	
	public String getOkkyFortune() {
		System.out.println("서비스 진입");
		try {
			fortuneCrawler.okkyFortune();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
    }
	
	
}
