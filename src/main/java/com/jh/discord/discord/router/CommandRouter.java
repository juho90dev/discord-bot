package com.jh.discord.discord.router;

import org.springframework.stereotype.Service;

import com.jh.discord.domain.dailyAir.AirService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommandRouter {

	private final AirService airService;

	public String route(String message) {

		if (message.contains("오늘"))
			return airService.getDustData() + "\n\n" + airService.getTempData();

		if (message.contains("먼지")) {
			return airService.getDustData();
		}

		if (message.contains("온도")) {
			return airService.getTempData();
		}


		return null;
	}

}
