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
public final class VisualizerManager_Factory implements Factory<VisualizerManager> {
  @Override
  public VisualizerManager get() {
    return newInstance();
  }

  public static VisualizerManager_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static VisualizerManager newInstance() {
    return new VisualizerManager();
  }

  private static final class InstanceHolder {
    private static final VisualizerManager_Factory INSTANCE = new VisualizerManager_Factory();
  }
}
