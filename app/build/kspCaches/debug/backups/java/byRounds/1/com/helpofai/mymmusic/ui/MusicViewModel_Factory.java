package com.helpofai.mymmusic.ui;

import com.helpofai.mymmusic.data.local.HistoryDao;
import com.helpofai.mymmusic.data.repository.AudioOutputRepository;
import com.helpofai.mymmusic.data.repository.AudioRepository;
import com.helpofai.mymmusic.data.repository.LyricsRepository;
import com.helpofai.mymmusic.data.repository.SmartPlaylistRepository;
import com.helpofai.mymmusic.media.AudioEffectManager;
import com.helpofai.mymmusic.media.MusicController;
import com.helpofai.mymmusic.media.StereoAudioProcessor;
import com.helpofai.mymmusic.media.VisualizerManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class MusicViewModel_Factory implements Factory<MusicViewModel> {
  private final Provider<AudioRepository> repositoryProvider;

  private final Provider<MusicController> musicControllerProvider;

  private final Provider<AudioEffectManager> audioEffectManagerProvider;

  private final Provider<SmartPlaylistRepository> smartPlaylistRepositoryProvider;

  private final Provider<LyricsRepository> lyricsRepositoryProvider;

  private final Provider<AudioOutputRepository> audioOutputRepositoryProvider;

  private final Provider<VisualizerManager> visualizerManagerProvider;

  private final Provider<StereoAudioProcessor> stereoAudioProcessorProvider;

  private final Provider<HistoryDao> historyDaoProvider;

  public MusicViewModel_Factory(Provider<AudioRepository> repositoryProvider,
      Provider<MusicController> musicControllerProvider,
      Provider<AudioEffectManager> audioEffectManagerProvider,
      Provider<SmartPlaylistRepository> smartPlaylistRepositoryProvider,
      Provider<LyricsRepository> lyricsRepositoryProvider,
      Provider<AudioOutputRepository> audioOutputRepositoryProvider,
      Provider<VisualizerManager> visualizerManagerProvider,
      Provider<StereoAudioProcessor> stereoAudioProcessorProvider,
      Provider<HistoryDao> historyDaoProvider) {
    this.repositoryProvider = repositoryProvider;
    this.musicControllerProvider = musicControllerProvider;
    this.audioEffectManagerProvider = audioEffectManagerProvider;
    this.smartPlaylistRepositoryProvider = smartPlaylistRepositoryProvider;
    this.lyricsRepositoryProvider = lyricsRepositoryProvider;
    this.audioOutputRepositoryProvider = audioOutputRepositoryProvider;
    this.visualizerManagerProvider = visualizerManagerProvider;
    this.stereoAudioProcessorProvider = stereoAudioProcessorProvider;
    this.historyDaoProvider = historyDaoProvider;
  }

  @Override
  public MusicViewModel get() {
    return newInstance(repositoryProvider.get(), musicControllerProvider.get(), audioEffectManagerProvider.get(), smartPlaylistRepositoryProvider.get(), lyricsRepositoryProvider.get(), audioOutputRepositoryProvider.get(), visualizerManagerProvider.get(), stereoAudioProcessorProvider.get(), historyDaoProvider.get());
  }

  public static MusicViewModel_Factory create(Provider<AudioRepository> repositoryProvider,
      Provider<MusicController> musicControllerProvider,
      Provider<AudioEffectManager> audioEffectManagerProvider,
      Provider<SmartPlaylistRepository> smartPlaylistRepositoryProvider,
      Provider<LyricsRepository> lyricsRepositoryProvider,
      Provider<AudioOutputRepository> audioOutputRepositoryProvider,
      Provider<VisualizerManager> visualizerManagerProvider,
      Provider<StereoAudioProcessor> stereoAudioProcessorProvider,
      Provider<HistoryDao> historyDaoProvider) {
    return new MusicViewModel_Factory(repositoryProvider, musicControllerProvider, audioEffectManagerProvider, smartPlaylistRepositoryProvider, lyricsRepositoryProvider, audioOutputRepositoryProvider, visualizerManagerProvider, stereoAudioProcessorProvider, historyDaoProvider);
  }

  public static MusicViewModel newInstance(AudioRepository repository,
      MusicController musicController, AudioEffectManager audioEffectManager,
      SmartPlaylistRepository smartPlaylistRepository, LyricsRepository lyricsRepository,
      AudioOutputRepository audioOutputRepository, VisualizerManager visualizerManager,
      StereoAudioProcessor stereoAudioProcessor, HistoryDao historyDao) {
    return new MusicViewModel(repository, musicController, audioEffectManager, smartPlaylistRepository, lyricsRepository, audioOutputRepository, visualizerManager, stereoAudioProcessor, historyDao);
  }
}
