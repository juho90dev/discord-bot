package com.jh.discord.audio.service;

import com.jh.discord.audio.handler.AudioPlayerSendHandler;
import com.jh.discord.audio.scheduler.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

public class GuildAudioManager {
	
	public final AudioPlayer player;
	
	// 음악 재생 목록(Queue)을 관리하고, 노래가 끝나면 다음 노래를 틀어주는 역할
    public final TrackScheduler scheduler;
    // LavaPlayer가 만든 소리 데이터를 디스코드로 전송 가능한 형태로 변환해주는 "송신기"
    public final AudioPlayerSendHandler sendHandler;

    public GuildAudioManager(AudioPlayerManager manager) {
    	
    	// manager를 통해 이 서버에서만 사용할 새로운 오디오 플레이어를 생성
        this.player = manager.createPlayer();
        
        // 위에서 만든 플레이어를 사용하는 스케줄러를 생성
        this.scheduler = new TrackScheduler(player);
        
        // 플레이어의 소리를 디스코드 서버로 보내줄 핸들러를 연결
        this.sendHandler = new AudioPlayerSendHandler(player);
        
        // 플레이어에서 발생하는 이벤트(노래 시작, 종료 등)를 
        // 스케줄러가 감지할 수 있도록 리스너로 설정
        player.addListener(scheduler);
    }
    
    public AudioPlayerSendHandler getSendHandler() {
        return sendHandler;
    }
    
}
