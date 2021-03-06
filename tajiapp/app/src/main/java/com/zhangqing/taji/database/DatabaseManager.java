package com.zhangqing.taji.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.android.volley.VolleyError;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.bean.PersonInfoBean;

import org.json.JSONException;
import org.json.JSONObject;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * 数据库管理类
 */
public class DatabaseManager {

    /**
     * 获取单例部分
     */
    private static DatabaseManager databaseManager;

    public static synchronized void init(Context context) {
        if (databaseManager == null) {
            databaseManager = new DatabaseManager(context);
        }
    }

    public static DatabaseManager getInstance() {
        return databaseManager;
    }

    private MySQLiteOpenHelper dbHelper;

    public DatabaseManager(Context context) {
        dbHelper = new MySQLiteOpenHelper(context);
    }


    private boolean isLoading = false;

    public void insert(String userid, Context context) {
        //if (isLoading) return;
        Log.e("insertById", "start" + userid);
        isLoading = true;
        UserClass.getInstance().getOthersAvatar(userid, new VolleyInterface(context.getApplicationContext()) {
            @Override
            public void onMySuccess(JSONObject jsonObject) {
                Log.e("insertById", "onMySuccess");
                try {
                    String name = jsonObject.getString("username");
                    String avatar = jsonObject.getString("avatar");
                    String uid = jsonObject.getString("uid");

                    if (name.equals("") || name.equals("null"))
                        name = "游客" + uid;
                    if (avatar.indexOf("http") == -1) return;
                    UserInfo userInfo = new UserInfo(uid, name, Uri.parse(avatar));
                    RongIM.getInstance().refreshUserInfoCache(userInfo);
                    insert(uid, name, avatar);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                isLoading = false;
                return;
            }

            @Override
            public void onMyError(VolleyError error) {
                isLoading = false;
            }
        });
    }

    /**
     * 插入一条数据
     *
     * @param person 插入的人
     * @return 变化条数
     */
    public int insert(PersonInfoBean person) {
        return insert(person.userid, person.username, person.avatar);
    }

    public int insert(String userid, String username, String avatar) {
        int count = 0;
        Log.e("SQLite", "----insert----");
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("insert into " + MySQLiteOpenHelper.TABLE_NAME
                            + " (userid,username, avatar) values (?,?,?)",
                    new Object[]{userid, username, avatar});
            db.setTransactionSuccessful();
            count++;
        } catch (Exception e) {
        } finally {
            db.endTransaction();
        }
        db.close();
        return count;
    }


    /**
     * 删除一条记录
     *
     * @param id 记录的id，通过查询返回
     * @return 变化条数
     */
    public int delete(int id) {
        int count = 0;
        Log.e("SQLite", "----delete----");
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("delete from " + MySQLiteOpenHelper.TABLE_NAME +
                    " where id = ?", new Object[]{id});
            db.setTransactionSuccessful();
            count++;
        } catch (Exception e) {
        } finally {
            db.endTransaction();
        }
        db.close();
        return count;
    }

//    // 更新记录
//    public int update(WifiType wifiType) {
//        Log.e("SQLite", "----update----");
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        db.beginTransaction();
//        try {
//            db.execSQL("update " + WifiType.TABLE_NAME
//                    + " set namedevice=?, nameplace=?, collapsing=?  " +
//                    "where mac = ? and ip=? and switchip=? and acid=?", new Object[]{
//                    wifiType.nameDevice, wifiType.namePlace, wifiType.collapsing,
//                    wifiType.mac, wifiType.ip, wifiType.switchip, wifiType.acid});
//            db.setTransactionSuccessful();
//        } catch (Exception e) {
//            return 0;
//        } finally {
//            db.endTransaction();
//        }
//        db.close();
//        return 1;
//    }

    public UserInfo queryUserInfoById(String userid) {
        PersonInfoBean p = queryPersonInfoById(userid);
        UserInfo userInfo = null;
        if (p != null) {
            userInfo = new UserInfo(p.userid, p.username, Uri.parse(p.avatar));
        }
        return userInfo;
    }


    public synchronized PersonInfoBean queryPersonInfoById(String userid) {
        Log.e("SQLite", "----query----");
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;
        String sql = "select * from " + MySQLiteOpenHelper.TABLE_NAME +
                " where userid=" + userid;
        cursor = db.rawQuery(sql, null);

        PersonInfoBean personInfo = null;
        if (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex("userid"));

            try {
                personInfo = PersonInfoBean.getInstance(id, null);
                personInfo.username = cursor.getString(cursor.getColumnIndex("username"));
                personInfo.avatar = cursor.getString(cursor.getColumnIndex("avatar"));
                int matching = cursor.getInt(cursor.getColumnIndex("matching"));
                personInfo.hasMatching = (matching == 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            Log.e("SQLite", personInfo.toString());
        } else {
            Log.e("SQLite", "****表中无数据****");
        }
        cursor.close();
        db.close();
        return personInfo;

    }
}  