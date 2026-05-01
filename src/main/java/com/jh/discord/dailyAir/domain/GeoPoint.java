package com.jh.discord.dailyAir.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GeoPoint {
	
	private final String nx;
	private final String ny;
	private final String fullName;

}
