package com.zhangqing.taji.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * 数据库操作助手类
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    // 数据库名称
    public static final String DB_NAME = "persons.db";
    public static final String TABLE_NAME = "persons";

    public static final int VERSION = 1;
    private static final String CREATETABLE = "create table " + TABLE_NAME + " (" +
            "[id] integer PRIMARY KEY" +
            ",[matching] integer" +
            ",[userid] varchar(10)" +
            ",[username] varchar(30)" +
            ",[avatar] varchar(100)" +
            ",[createtime] timestamp NOT NULL default CURRENT_TIMESTAMP" +
            ",UNIQUE (userid)" +
            ")";

    public MySQLiteOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    // 创建表
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATETABLE);
    }

    // 更新表
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.deleteDB(db);
        this.onCreate(db);
    }

    // 删除表
    private void deleteDB(SQLiteDatabase db) {
        db.execSQL("drop table if exists " + TABLE_NAME);
    }
}