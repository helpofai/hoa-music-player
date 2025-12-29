package com.helpofai.mymmusic.media;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class StereoAudioProcessor_Factory implements Factory<StereoAudioProcessor> {
  @Override
  public StereoAudioProcessor get() {
    return newInstance();
  }

  public static StereoAudioProcessor_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static StereoAudioProcessor newInstance() {
    return new StereoAudioProcessor();
  }

  private static final class InstanceHolder {
    private static final StereoAudioProcessor_Factory INSTANCE = new StereoAudioProcessor_Factory();
  }
}
