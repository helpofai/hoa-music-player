package com.helpofai.mymmusic.di;

import com.helpofai.mymmusic.data.local.HistoryDao;
import com.helpofai.mymmusic.data.local.MusicDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideHistoryDaoFactory implements Factory<HistoryDao> {
  private final Provider<MusicDatabase> databaseProvider;

  public DatabaseModule_ProvideHistoryDaoFactory(Provider<MusicDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public HistoryDao get() {
    return provideHistoryDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideHistoryDaoFactory create(
      Provider<MusicDatabase> databaseProvider) {
    return new DatabaseModule_ProvideHistoryDaoFactory(databaseProvider);
  }

  public static HistoryDao provideHistoryDao(MusicDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideHistoryDao(database));
  }
}
