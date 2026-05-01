package com.jh.discord.audio.handler;

import java.nio.ByteBuffer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;

import net.dv8tion.jda.api.audio.AudioSendHandler;

public class AudioPlayerSendHandler implements AudioSendHandler{
	
	private final AudioPlayer audioPlayer;
	// private AudioFrame lastFrame;
	private final ByteBuffer buffer;
	private final MutableAudioFrame frame;
	
	public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
		this.audioPlayer = audioPlayer;
        // 1024바이트 공간 확보
        this.buffer = ByteBuffer.allocate(1024);
        // 재사용 가능한 프레임 생성 및 버퍼 연결
        this.frame = new MutableAudioFrame();
        this.frame.setBuffer(buffer);
    }

	
	@Override
    public boolean canProvide() {
		return this.audioPlayer.provide(this.frame);
    }
	
	@Override
    public ByteBuffer provide20MsAudio() {
		this.buffer.flip();
	    return this.buffer;
    }

	@Override
    public boolean isOpus() {
        // LavaPlayer는 기본적으로 Opus 인코딩을 사용
        return true;
    }

}
