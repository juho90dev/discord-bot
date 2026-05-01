package com.jh.discord.dailyAir.domain;

import java.util.Arrays;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LocationCode {
	
	YEONGTONG("영통", "62", "120", "수원시 영통구 영통동","영통동"),
	GANGNAM("강남", "61", "126", "서울시 강남구","강남구"),
	GEUMCHEON("금천", "58", "125", "서울시 금천구","금천구"),
	JONGRO("종로", "60", "127", "서울시 종로구","종로구"),
	DEFAULT("기본", "62", "120", "수원시 영통구 영통동","영통동");
	
	private final String keyword;
    private final String nx;
    private final String ny;
    private final String fullName;
    private final String stationName;
	
	// 입력받은 지역명이 포함된 Enum 상수를 찾는 로직
    public static LocationCode findByKeyword(String locationName) {
        return Arrays.stream(values())
                .filter(code -> locationName != null && locationName.contains(code.keyword))
                .findFirst()
                .orElse(DEFAULT);
    }


}
