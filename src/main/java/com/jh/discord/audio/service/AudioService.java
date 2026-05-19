package com.jh.discord.audio.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

@Service
@RequiredArgsConstructor
public class AudioService {
	
	private final AudioPlayerManager playerManager;
	private final Map<Long, GuildAudioManager> musicManagers = new ConcurrentHashMap<>();
	
	public GuildAudioManager getGuildAudioPlayer(Guild guild) {
        return musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            GuildAudioManager manager = new GuildAudioManager(playerManager);
            // 디코드 오디오 전송 핸들러 설정
            guild.getAudioManager().setSendingHandler(manager.getSendHandler());
            return manager;
        });
    }
	
	
	public void loadAndPlay(TextChannel channel, String trackUrl) {
		final GuildAudioManager musicManager = getGuildAudioPlayer(channel.getGuild());
		
		playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                musicManager.scheduler.queue(track);
                channel.sendMessage("🎵 **" + track.getInfo().title + "** 재생 목록에 추가!").queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                // 플레이리스트일 경우 첫 번째 곡만 추가 (원하는 대로 변경 가능)
                AudioTrack firstTrack = playlist.getTracks().get(0);
                musicManager.scheduler.queue(firstTrack);
                channel.sendMessage("🎵 **" + firstTrack.getInfo().title + "** (플레이리스트 중 한 곡) 추가!").queue();
            }

            @Override
            public void noMatches() {
                channel.sendMessage("❌ 검색 결과가 없습니다: " + trackUrl).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessage("⚠️ 노래를 불러오지 못했습니다: " + exception.getMessage()).queue();
            }
        });
		
	}
	
	
	public void skip(Guild guild) {
        GuildAudioManager musicManager = musicManagers.get(guild.getIdLong());
        if (musicManager != null) {
            musicManager.scheduler.nextTrack();
        }
    }
	
	
}
