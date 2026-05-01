package com.jh.discord.audio.scheduler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

public class TrackScheduler extends AudioEventAdapter{
	
	private final AudioPlayer player;
	// 래 목록을 순서대로 담아둔다
    private final BlockingQueue<AudioTrack> queue;
    
    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        // 대기열을 담을 큐 (먼저 들어온 노래가 먼저 나가는 방식)
        this.queue = new LinkedBlockingQueue<>();
    }

    // 노래를 대기열에 넣는 메서드
    public void queue(AudioTrack track) {
        // 현재 노래가 재생 중이 아니면 바로 틀고, 재생 중이면 대기열에 넣음
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }
    
    // 다음 곡으로 넘어가는 메서드
    public void nextTrack() {
        // 큐에서 다음 곡을 꺼내서 재생 (false는 현재 곡을 강제로 끊고 재생한다는 뜻)
    	// 노래가 없을 때만 즉시 재생되고, 노래가 있으면 false를 반환
        player.startTrack(queue.poll(), false);
    }
    
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // 노래가 정상적으로 끝났을 때만 다음 곡을 자동으로 재생
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }

}
