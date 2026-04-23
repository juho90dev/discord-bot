package com.jh.discord.domain.lotto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;


@Service
public class LottoService {

	
	public String lottoNum(){
		int [] lotto = new int[6];
		
		// 1. 번호 생성 및 중복 검사
        for (int i = 0; i < lotto.length; i++) {
            lotto[i] = (int) (Math.random() * 45) + 1;

            // 중복 검사: 현재 뽑은 숫자(lotto[i])가 이전 숫자들과 같은지 확인
            for (int j = 0; j < i; j++) {
                if (lotto[i] == lotto[j]) {
                    i--; // 중복이면 인덱스를 하나 뒤로 미뤄서 다시 뽑게 함
                    break;
                }
            }
        }
        
        // 2. 오름차순 정렬 (temp를 이용한 버블 정렬)
        int temp = 0;
        for (int i = 0; i < lotto.length - 1; i++) {
            for (int j = 0; j < lotto.length - 1 - i; j++) {
                if (lotto[j] > lotto[j + 1]) {
                    // 앞의 숫자가 크면 temp를 이용해서 자리를 바꿈
                    temp = lotto[j];
                    lotto[j] = lotto[j + 1];
                    lotto[j + 1] = temp;
                }
            }
        }
        
        // 3. 배열을 문자열로 변환
        StringBuilder sb = new StringBuilder("**행운의 로또 번호** : ");
        for (int i = 0; i < lotto.length; i++) {
            sb.append(lotto[i]);
            if (i < lotto.length - 1) {
                sb.append(", ");
            }
        }
		
		return sb.toString();
		
	}
}