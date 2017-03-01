package org.foree.bookreader.data.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by foree on 2016/8/6.
 * 数据库创建升级的帮助类
 */
public class BookSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = BookSQLiteOpenHelper.class.getSimpleName();
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "bookReader.db";

    public BookSQLiteOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
        onUpgrade(db, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        for (int version = oldVersion + 1; version <= newVersion; version++) {
            onUpgradeTo(db, version);
        }
    }

    private void onUpgradeTo(SQLiteDatabase db, int version) {
        switch (version) {
            case 1:
                createEntriesTable(db);
                break;
            default:
                throw new IllegalStateException("Don't known to upgrade to " + version);
        }
    }

    private void createEntriesTable(SQLiteDatabase db) {
        db.execSQL("create table " + BookReaderContract.BooksEntry.TABLE_NAME + "(" +
                BookReaderContract.BooksEntry._ID + " integer primary key," +
                BookReaderContract.BooksEntry.COLUMN_NAME_BOOK_URL + " varchar unique," +
                BookReaderContract.BooksEntry.COLUMN_NAME_CONTENT_URL + " varchar," +
                BookReaderContract.BooksEntry.COLUMN_NAME_BOOK_NAME + " varchar," +
                BookReaderContract.BooksEntry.COLUMN_NAME_COVER_URL + " varchar," +
                BookReaderContract.BooksEntry.COLUMN_NAME_UPDATE_TIME + " varchar," +
                BookReaderContract.BooksEntry.COLUMN_NAME_PAGE_INDEX + " integer," +
                BookReaderContract.BooksEntry.COLUMN_NAME_RECENT_ID + " integer," +
                BookReaderContract.BooksEntry.COLUMN_NAME_CATEGORY + " varchar," +
                BookReaderContract.BooksEntry.COLUMN_NAME_DESCRIPTION + " varchar," +
                BookReaderContract.BooksEntry.COLUMN_NAME_AUTHOR + " varchar" +
                ")"
        );

        //章节有对应的book_url, chapter_url的md5为章节内容的缓存文件名称
        // chapter_id 用于章节排序和获取前后章节
        db.execSQL("create table " + BookReaderContract.ChaptersEntry.TABLE_NAME + "(" +
                BookReaderContract.ChaptersEntry._ID + " integer primary key," +
                BookReaderContract.ChaptersEntry.COLUMN_NAME_CHAPTER_URL + " varchar unique," +
                BookReaderContract.ChaptersEntry.COLUMN_NAME_CHAPTER_ID + " integer unique," +
                BookReaderContract.ChaptersEntry.COLUMN_NAME_BOOK_URL + " varchar," +
                BookReaderContract.ChaptersEntry.COLUMN_NAME_CHAPTER_TITLE + " varchar," +
                BookReaderContract.ChaptersEntry.COLUMN_NAME_CHAPTER_CONTENT + " varchar," +
                BookReaderContract.ChaptersEntry.COLUMN_NAME_CACHED + " integer" +
                ")"
        );
    }

}
