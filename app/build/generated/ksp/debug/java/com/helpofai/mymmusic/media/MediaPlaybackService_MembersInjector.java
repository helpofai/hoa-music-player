package com.helpofai.mymmusic.media;

import com.helpofai.mymmusic.data.repository.AudioOutputRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MediaPlaybackService_MembersInjector implements MembersInjector<MediaPlaybackService> {
  private final Provider<AudioEffectManager> audioEffectManagerProvider;

  private final Provider<AudioOutputRepository> audioOutputRepositoryProvider;

  private final Provider<VisualizerManager> visualizerManagerProvider;

  private final Provider<StereoAudioProcessor> stereoAudioProcessorProvider;

  public MediaPlaybackService_MembersInjector(
      Provider<AudioEffectManager> audioEffectManagerProvider,
      Provider<AudioOutputRepository> audioOutputRepositoryProvider,
      Provider<VisualizerManager> visualizerManagerProvider,
      Provider<StereoAudioProcessor> stereoAudioProcessorProvider) {
    this.audioEffectManagerProvider = audioEffectManagerProvider;
    this.audioOutputRepositoryProvider = audioOutputRepositoryProvider;
    this.visualizerManagerProvider = visualizerManagerProvider;
    this.stereoAudioProcessorProvider = stereoAudioProcessorProvider;
  }

  public static MembersInjector<MediaPlaybackService> create(
      Provider<AudioEffectManager> audioEffectManagerProvider,
      Provider<AudioOutputRepository> audioOutputRepositoryProvider,
      Provider<VisualizerManager> visualizerManagerProvider,
      Provider<StereoAudioProcessor> stereoAudioProcessorProvider) {
    return new MediaPlaybackService_MembersInjector(audioEffectManagerProvider, audioOutputRepositoryProvider, visualizerManagerProvider, stereoAudioProcessorProvider);
  }

  @Override
  public void injectMembers(MediaPlaybackService instance) {
    injectAudioEffectManager(instance, audioEffectManagerProvider.get());
    injectAudioOutputRepository(instance, audioOutputRepositoryProvider.get());
    injectVisualizerManager(instance, visualizerManagerProvider.get());
    injectStereoAudioProcessor(instance, stereoAudioProcessorProvider.get());
  }

  @InjectedFieldSignature("com.helpofai.mymmusic.media.MediaPlaybackService.audioEffectManager")
  public static void injectAudioEffectManager(MediaPlaybackService instance,
      AudioEffectManager audioEffectManager) {
    instance.audioEffectManager = audioEffectManager;
  }

  @InjectedFieldSignature("com.helpofai.mymmusic.media.MediaPlaybackService.audioOutputRepository")
  public static void injectAudioOutputRepository(MediaPlaybackService instance,
      AudioOutputRepository audioOutputRepository) {
    instance.audioOutputRepository = audioOutputRepository;
  }

  @InjectedFieldSignature("com.helpofai.mymmusic.media.MediaPlaybackService.visualizerManager")
  public static void injectVisualizerManager(MediaPlaybackService instance,
      VisualizerManager visualizerManager) {
    instance.visualizerManager = visualizerManager;
  }

  @InjectedFieldSignature("com.helpofai.mymmusic.media.MediaPlaybackService.stereoAudioProcessor")
  public static void injectStereoAudioProcessor(MediaPlaybackService instance,
      StereoAudioProcessor stereoAudioProcessor) {
    instance.stereoAudioProcessor = stereoAudioProcessor;
  }
}
