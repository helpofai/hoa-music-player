package com.helpofai.mymmusic.data.repository;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class AudioRepository_Factory implements Factory<AudioRepository> {
  private final Provider<Context> contextProvider;

  public AudioRepository_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AudioRepository get() {
    return newInstance(contextProvider.get());
  }

  public static AudioRepository_Factory create(Provider<Context> contextProvider) {
    return new AudioRepository_Factory(contextProvider);
  }

  public static AudioRepository newInstance(Context context) {
    return new AudioRepository(context);
  }
}
