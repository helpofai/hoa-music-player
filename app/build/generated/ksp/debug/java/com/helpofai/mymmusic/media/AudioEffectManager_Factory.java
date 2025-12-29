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
public final class AudioEffectManager_Factory implements Factory<AudioEffectManager> {
  @Override
  public AudioEffectManager get() {
    return newInstance();
  }

  public static AudioEffectManager_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AudioEffectManager newInstance() {
    return new AudioEffectManager();
  }

  private static final class InstanceHolder {
    private static final AudioEffectManager_Factory INSTANCE = new AudioEffectManager_Factory();
  }
}
