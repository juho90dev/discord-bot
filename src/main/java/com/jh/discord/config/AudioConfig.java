package com.jh.discord.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

@Configuration
public class AudioConfig {
	
	@Bean
    public AudioPlayerManager audioPlayerManager() {
        // LavaPlayer의 기본 매니저 생성
        AudioPlayerManager manager = new DefaultAudioPlayerManager();
        
        // 소스 등록 (이걸 여기서 미리 해두면 서비스가 깔끔해집니다)
        AudioSourceManagers.registerRemoteSources(manager);
        AudioSourceManagers.registerLocalSource(manager);
        
        return manager;
    }

}
