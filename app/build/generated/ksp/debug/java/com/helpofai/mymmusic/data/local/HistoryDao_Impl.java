package com.helpofai.mymmusic.data.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class HistoryDao_Impl implements HistoryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RecentTrack> __insertionAdapterOfRecentTrack;

  public HistoryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRecentTrack = new EntityInsertionAdapter<RecentTrack>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `playback_history` (`trackId`,`lastPlayedTimestamp`) VALUES (?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RecentTrack entity) {
        statement.bindLong(1, entity.getTrackId());
        statement.bindLong(2, entity.getLastPlayedTimestamp());
      }
    };
  }

  @Override
  public Object insertOrUpdate(final RecentTrack track,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfRecentTrack.insert(track);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<RecentTrack>> getRecentTrackIds() {
    final String _sql = "SELECT * FROM playback_history ORDER BY lastPlayedTimestamp DESC LIMIT 20";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"playback_history"}, new Callable<List<RecentTrack>>() {
      @Override
      @NonNull
      public List<RecentTrack> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfTrackId = CursorUtil.getColumnIndexOrThrow(_cursor, "trackId");
          final int _cursorIndexOfLastPlayedTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "lastPlayedTimestamp");
          final List<RecentTrack> _result = new ArrayList<RecentTrack>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RecentTrack _item;
            final long _tmpTrackId;
            _tmpTrackId = _cursor.getLong(_cursorIndexOfTrackId);
            final long _tmpLastPlayedTimestamp;
            _tmpLastPlayedTimestamp = _cursor.getLong(_cursorIndexOfLastPlayedTimestamp);
            _item = new RecentTrack(_tmpTrackId,_tmpLastPlayedTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
