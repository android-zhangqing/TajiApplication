package com.zhangqing.taji.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.zhangqing.taji.MyApplication;
import com.zhangqing.taji.R;
import com.zhangqing.taji.util.Md5Util;
import com.zhangqing.taji.util.UploadUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 所有网络获取操作都在这里面，采用单例模式，为当前用户对象。
 */
public class UserClass {
    public static final int Page_Per_Count = 6;

    public static final int Request_Main = 101;
    public static final int Request_Register_First = 102;
    public static final int Request_Register_Second = 103;
    public static final int Request_Register_Third = 104;


    public static final int Persons_Button_tudi = 1;
    public static final int Persons_Button_shifu = 2;
    public static final int Persons_Button_dingyue = 3;
    public static final int Persons_Button_beidingyue = 4;


    private final String URLHEAD = "http://taji.whutech.com";
    public SharedPreferences sharedPreferences;
    public String userId = "";
    public String openId = "";
    public int schoolId = 0;
    public String mobile = "";
    public String schoolName = "";

    public String interest;
    public String skill;


    private static UserClass userClass;

    public static synchronized void init(SharedPreferences s) {
        userClass = new UserClass(s);
    }

    public static UserClass getInstance() {
//        if(userClass==null){
//            synchronized (UserClass.class){
//                if(userClass==null){
//                    userClass=new UserClass();
//                }
//
//            }
//        }
        return userClass;
    }


    private UserClass(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        reLoadSharedPreferences();
    }

    public String getStringByKey(String key) {

        String string = sharedPreferences.getString(key, "");
        if (string.equals("null")) string = "";
        return string;
    }

    public void setStringByKey(String key, String value) {
        sharedPreferences.edit().putString(key, value).commit();
    }

    class SchoolType {
        int id;
        String name;
        double latitude;
        double longitude;
        String city;
        String region;

        public SchoolType(int id, String name, double latitude, double longitude, String city, String region) {
            this.city = city;
            this.id = id;
            this.latitude = latitude;
            this.longitude = longitude;
            this.name = name;
            this.region = region;
        }

        public double getDistance(double latitude, double longitude) {
            double decLatitude = latitude - this.latitude;
            double decLongitude = longitude - this.longitude;
            return decLatitude * decLatitude + decLongitude * decLongitude;
        }

        public String toString() {
            return name + "|" + latitude + "|" + longitude + "|" + region;
        }

    }

    public boolean getSchoolByLocation(Context context, double latitude, double longitude, String city) {
        if (city.equals("")) return false;
        city = "武汉";

        String result = "";
        try {
            InputStream in = context.getResources().openRawResource(R.raw.school);
            //获取文件的字节数
            int lenght = in.available();
            //创建byte数组
            byte[] buffer = new byte[lenght];
            //将文件中的数据读到byte数组中
            in.read(buffer);
            result = new String(buffer, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        result = result.trim();

        //result = "{\"result\":\"ok\",\"msg\":\"操作成功\",\"data\":[],\"rescode\":200}";

        JSONObject jsonObject2;
        List<SchoolType> citySchoolsList = new ArrayList<SchoolType>();

        try {
            jsonObject2 = new JSONObject(result);
            Log.e("getSchoolByJSONObject", jsonObject2.length() + "|" + jsonObject2.toString());
            JSONArray jsonArray = jsonObject2.getJSONArray("data");
            Log.e("getSchoolBygetJSONArray", jsonArray.length() + "|" + jsonArray.toString());


            JSONArray myCitySchoolsJsonArray = null;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject cityJsonObject = jsonArray.getJSONObject(i);

                Iterator<String> it = cityJsonObject.keys();
                String showString = "";
                while (it.hasNext()) {
                    showString = showString + it.next() + "|";
                }
                showString = showString + cityJsonObject.get("name");


                if ((!city.equals("")) && cityJsonObject.get("name").equals(city)) {
                    myCitySchoolsJsonArray = cityJsonObject.getJSONArray("districts");
                }


                Log.e("getSchool", cityJsonObject.length() + "|" + showString);
            }
            Log.e("myCitySchoolsJsonArray", myCitySchoolsJsonArray + "|");
            if (myCitySchoolsJsonArray == null) return false;


            for (int i = 0; i < myCitySchoolsJsonArray.length(); i++) {
                JSONArray tempArray = myCitySchoolsJsonArray.getJSONObject(i).getJSONArray("places");
                for (int j = 0; j < tempArray.length(); j++) {
                    JSONObject tempObject = tempArray.getJSONObject(j);
                    citySchoolsList.add(new SchoolType(
                            tempObject.getInt("id"),
                            tempObject.getString("name"),
                            tempObject.getDouble("latitude"),
                            tempObject.getDouble("longitude"),
                            tempObject.getString("city"),
                            tempObject.getString("region")
                    ));
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("getSchoolByLocation", "fail");
        }

        SchoolType mySchool;
        {
            int mySchoolNum = 0;
            double minDistance = 100;

            for (int i = 0; i < citySchoolsList.size(); i++) {
                SchoolType schoolType = citySchoolsList.get(i);
                Log.e("School", schoolType.latitude + "|" + schoolType.longitude + "|" + schoolType.name);
                double distance = schoolType.getDistance(latitude, longitude);
                if (distance < minDistance) {
                    mySchoolNum = i;
                    minDistance = distance;
                }
            }
            Log.e("school##", citySchoolsList.get(mySchoolNum).toString());
            mySchool = citySchoolsList.get(mySchoolNum);
        }


        citySchoolsList.clear();
        Editor editor = sharedPreferences.edit();
        editor.putInt("school_id", mySchool.id);
        editor.putString("school_name", mySchool.name);
        editor.putString("school_region", mySchool.region);
        editor.commit();
        reLoadSharedPreferences();
        return true;


//        JSONObject jsonObject = null;
//        try {
//            jsonObject = JSONObject.fromString(result);
//        } catch (Exception e) {
//            Log.e("getSchoolByLocation", "fail" + "|" + result.substring(result.length() - 20, result.length()));
//            e.printStackTrace();
//        }


//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putFloat("latitude", (float) latitude);
//        editor.putFloat("longitude", (float) longitude);
//        editor.commit();
    }

    public boolean reLoadSharedPreferences() {
        this.userId = sharedPreferences.getString("userid", "");
        this.openId = sharedPreferences.getString("openid", "");
        this.schoolId = sharedPreferences.getInt("school_id", 0);
        this.schoolName = sharedPreferences.getString("school_name", "");
        this.mobile = sharedPreferences.getString("mobile", "");
        this.skill = sharedPreferences.getString("skill", "");
        this.interest = sharedPreferences.getString("interest", "");

        if (userId.equals("") || openId.equals("")) {
            return false;
        }
        return true;
    }


    public boolean saveSharedPreference(JSONObject jsonObject) {
        Iterator keysIterator = jsonObject.keys();
        Editor editor = sharedPreferences.edit();
        while (keysIterator.hasNext()) {
            String key = (String) keysIterator.next();
            try {
                Log.e("keysIterator", key + "|" + jsonObject.getString(key));
                editor.putString(key, jsonObject.getString(key));
            } catch (JSONException e) {
                Log.e("BigBug", "UserClass.java saveSharedPreference 解析异常");
                e.printStackTrace();
            }
        }
        editor.commit();
        return reLoadSharedPreferences();
    }

    public void clear() {
        sharedPreferences.edit().clear().commit();
    }

    public void doModifyInterestSkill(String interest, String skill,
                                      VolleyInterface vif) {
        String url = "http://www.baidu.com/?";
        try {
            url = URLHEAD + "/Skill/intadskill?" +
                    "userid=" + userId + "&openid=" + openId +
                    "&interest=" + URLEncoder.encode(interest, "utf-8") +
                    "&skill=" + URLEncoder.encode(skill, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        VolleyRequest.RequestGet(url, "doRegisterSmsSend", vif);

    }

    public void doGetDongTai(String tag, int page, VolleyInterface vif) {
        String _tag = "";
        try {
            _tag = URLEncoder.encode(tag, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = URLHEAD + "/DongTai?" + "userid=" + userId + "&openid=" + openId + "&tag=" + _tag + "&page=" + page + "&count=" + Page_Per_Count;
        VolleyRequest.RequestGet(url, "doGetDongTai", vif);

    }

    public void doGetDongTaiBanner(VolleyInterface vif) {

        String url = URLHEAD + "/DongTai/banner?" + "userid=" + userId + "&openid=" + openId;
        VolleyRequest.RequestGet(url, "doGetDongTaiBanner", vif);

    }

    public void doGetDongTaiMyFollow(int page, VolleyInterface vif) {
        String url = URLHEAD + "/DongTai/myFollow?" + "userid=" + userId + "&openid=" + openId +
                "&page=" + page + "&count=" + Page_Per_Count;
        VolleyRequest.RequestGet(url, "doGetDongTaiMyFollow", vif);
    }

    public void getDongTaiComment(String tid, int page, VolleyInterface vif) {
        String url = URLHEAD + "/DongTai/commentList?" +
                "page=" + page + "&count=" + Page_Per_Count +
                "&tid=" + tid + "&userid=" + userId + "&openid=" + openId;
        VolleyRequest.RequestGet(url, "getDongTaiComment", vif);
    }

    /**
     * 上传图片
     *
     * @param picPath 待上传图片的路径
     * @param l       上传监听
     */
    public void doUploadPhoto(String picPath, UploadUtil.OnUploadProcessListener l) {

        if (null != l)
            UploadUtil.getInstance().setOnUploadProcessListener(l); //设置监听器监听上传状态

        Map<String, String> params = new HashMap<String, String>();
        params.put("userid", userId);
        params.put("openid", openId);
        UploadUtil.getInstance().uploadFile(picPath, "file", "http://taji.whutech.com/Upload", params);

    }

    public void doUploadDongTai(final String media, final String video, final String content, final String location, final boolean isMasterCircle, VolleyInterface vif) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://taji.whutech.com/DongTai/publish",
                vif.loadingListener(), vif.errorListener()) {
            @Override
            protected Map<String, String> getParams() {
                //在这里设置需要post的参数
                Map<String, String> map = new HashMap<String, String>();
                map.put("userid", userId);
                map.put("openid", openId);
                try {
                    map.put("media", media);
                    if (!(video == null || video.equals("")))
                        map.put("video", video);
                    //map.put("content", URLEncoder.encode(content, "utf-8"));
                    map.put("content", content);

                    map.put("loc", URLEncoder.encode(location, "utf-8"));
                    map.put("mastercircle", (isMasterCircle ? "1" : "0"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return map;
            }
        };
        MyApplication.getRequestQeuee().add(stringRequest);

//        String url = null;
//        try {
//            url = URLHEAD + "/DongTai/publish?userid=" + userId + "&openid=" + openId
//                    + "&media=" + URLEncoder.encode(media, "gb2312") +
//                    "&content=" + URLEncoder.encode(content, "utf-8") +
//                    "&tag=" +
//                    "&at=" +
//                    "&loc=" + URLEncoder.encode(location, "utf-8") +
//                    "&mastercircle=" + (isMasterCircle ? "1" : "0") +
//                    "&status=";
//            VolleyRequest.RequestGet(url, "doUploadDongTai", vif);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

    }

    public void doModifyMemberDetail(String name, String sex, String school, String sign,
                                     VolleyInterface vif) {
        String url = "http://www.baidu.com/?";
        try {
            url = URLHEAD + "/User/updateUserInfo?" +
                    "userid=" + userId + "&openid=" + openId +
                    "&username=" + URLEncoder.encode(name, "utf-8") +
                    "&sex=" + URLEncoder.encode(sex, "utf-8") +
                    "&school=" + URLEncoder.encode(school, "utf-8") +
                    "&signature=" + URLEncoder.encode(sign, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        VolleyRequest.RequestGet(url, "doRegisterSmsSend", vif);
    }

    public void doRegisterSmsSend(String mobile,
                                  VolleyInterface vif) {
        String url = URLHEAD + "/Sms/get_code?mobile=" + mobile;
        VolleyRequest.RequestGet(url, "doRegisterSmsSend", vif);

    }

    public void doResetPassSmsSend(String mobile,
                                   VolleyInterface vif) {
        String url = URLHEAD + "/Sms/resetPassword?mobile=" + mobile;
        VolleyRequest.RequestGet(url, "doResetPassSmsSend", vif);

    }

    public void doRegisterSmsCheck(String mobile,
                                   String smsCode, VolleyInterface vif) {
        VolleyRequest.RequestGet(URLHEAD + "/Sms/verify_code?mobile="
                + mobile + "&code=" + smsCode, "doRegisterSmsCheck", vif);
    }

    public void doRegisterDone(String mobile, String password,
                               String smsCode, VolleyInterface vif) {
        VolleyRequest.RequestGet(URLHEAD + "/User/register?mobile=" + mobile
                        + "&code=" + smsCode + "&password=" + Md5Util.getMd5(password),
                "doRegisterDone", vif);
    }

    public void doResetPasswordBySms(String mobile, String password,
                                     String smsCode, VolleyInterface vif) {
        VolleyRequest.RequestGet(URLHEAD + "/User/resetPassword?mobile=" + mobile
                        + "&code=" + smsCode + "&password=" + Md5Util.getMd5(password),
                "doResetPasswordBySms", vif);
    }

    public void doResetPasswordByOldPass(String oldpass, String password,
                                         VolleyInterface vif) {
        VolleyRequest.RequestGet(URLHEAD + "/User/modifyPassword?userid="
                        + userId + "&openid=" + openId + "&oldpass=" + oldpass +
                        "&newpass=" + Md5Util.getMd5(password),
                "doResetPasswordByOldPass", vif);
    }


    public void doLogin(String mobile, String password, VolleyInterface vif) {
        VolleyRequest.RequestGet(URLHEAD + "/User/login?mobile=" + mobile
                        + "&password=" + Md5Util.getMd5(password),
                "doLogin", vif);
    }


    public void doFollow(String uid, boolean isToFollow, VolleyInterface vif) {
        String url;
        String tag;
        if (isToFollow) {
            url = URLHEAD + "/Follow/follow?uid=" + uid
                    + "&userid=" + userId + "&openid=" + openId;
            tag = "follow";
        } else {
            url = URLHEAD + "/Follow/unfollow?uid=" + uid
                    + "&userid=" + userId + "&openid=" + openId;
            tag = "unfollow";
        }
        VolleyRequest.RequestGet(url, tag, vif);
    }

    public void doComment(String tid, String content, VolleyInterface vif) {
        String url = "";
        try {
            url = URLHEAD + "/DongTai/comment?userid=" + userId + "&openid=" + openId +
                    "&tid=" + tid + "&content=" + URLEncoder.encode(content, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        VolleyRequest.RequestGet(url, "doComment", vif);

    }


    public void getMyUserInfo(VolleyInterface vif) {
        VolleyRequest.RequestGet(URLHEAD + "/User/userInfo?userid=" + userId + "&openid=" + openId,
                "getMyUserInfo", vif);
    }

    public void getOthersInfo(String uid, VolleyInterface vif) {
        VolleyRequest.RequestGet(URLHEAD + "/User/getUserInfo?userid=" + userId + "&openid=" + openId + "&uid=" + uid,
                "getOthersInfo", vif);
    }

    public void getSkillListAll(VolleyInterface vif) {
        VolleyRequest.RequestGet(URLHEAD + "/Skill/skill?userid=" + userId + "&openid=" + openId, "getSkillListAll", vif);
    }

    public void getPersonsList(int whichPersonsButton, int page, VolleyInterface vif) {
        String url = "";
        switch (whichPersonsButton) {
            case Persons_Button_tudi:
                url = URLHEAD + "/Master/tudiList?userid=" + userId + "&openid=" + openId + "&count=" + Page_Per_Count + "&page=" + page;
                break;
            case Persons_Button_shifu:
                url = URLHEAD + "/Master/masterList?userid=" + userId + "&openid=" + openId + "&count=" + Page_Per_Count + "&page=" + page;
                break;
            case Persons_Button_dingyue:
                url = URLHEAD + "/Follow/followList?userid=" + userId + "&openid=" + openId + "&count=" + Page_Per_Count + "&page=" + page;
                break;
            case Persons_Button_beidingyue:
                url = URLHEAD + "/Follow/fansList?userid=" + userId + "&openid=" + openId + "&count=" + Page_Per_Count + "&page=" + page;
                break;
        }
        VolleyRequest.RequestGet(url, "getFollowList", vif);
    }

    public void chatRoomGetRoomList(int page, VolleyInterface vif) {
        String url = URLHEAD + "/Chatroom/getRoomList?userid=" + userId + "&openid=" + openId +
                "&page=" + page + "&count=" + Page_Per_Count;
        VolleyRequest.RequestGet(url, "chatRoomGetRoomList", vif);
    }

    public void chatRoomGetMyRoomList(int page, VolleyInterface vif) {
        String url = URLHEAD + "/Chatroom/getMyRoom?userid=" + userId + "&openid=" + openId +
                "&page=" + page + "&count=" + Page_Per_Count;
        VolleyRequest.RequestGet(url, "chatRoomGetMyRoomList", vif);
    }

    public void chatRoomAddDelMyRoom(String rid, boolean isToAdd, VolleyInterface vif) {

        String url = URLHEAD + "/Chatroom/" + (isToAdd ? "add" : "del") + "MyRoom?userid=" + userId + "&openid=" + openId +
                "&rid=" + rid;

        VolleyRequest.RequestGet(url, "chatRoomAddDelMyRoom", vif);
    }

    public void getOthersAvatar(String userid, VolleyInterface vif) {
        String url = URLHEAD + "/User/getAvatar?uid=" + userid + "&userid=" + userId + "&openid=" + openId;
        VolleyRequest.RequestGet(url, "getOthersAvatar" + userid, vif);
    }

    public void getDynamicMine(int page, VolleyInterface vif) {
        String url = URLHEAD + "/DongTai/myDynamic?userid=" + userId + "&openid=" + openId +
                "&page=" + page + "&count=" + Page_Per_Count;
        VolleyRequest.RequestGet(url, "getDynamicMine", vif);
    }

    public void getOthersDongTai(String uid, int page, VolleyInterface vif) {
        String url = URLHEAD + "/DongTai/userDynamic?uid=" + uid +
                "&userid=" + userId + "&openid=" + openId + "&page=" + page + "&count=" + Page_Per_Count;
        VolleyRequest.RequestGet(url, "getOthersDongTai" + uid, vif);
    }

    public void getSkillMatching(int page, VolleyInterface vif) {
        String url = URLHEAD + "/Skill/match?" + "&userid=" + userId + "&openid=" + openId + "&page=" + page + "&count=" + Page_Per_Count;
        VolleyRequest.RequestGet(url, "getSkillMatching", vif);
    }

    public void doBaishi(String uid, VolleyInterface vif) {
        String url = URLHEAD + "/Master/baishi?userid=" + userId + "&openid=" + openId + "&uid=" + uid;
        VolleyRequest.RequestGet(url, "doBaishi", vif);
    }

    public void searchForPerson(String word, int page, VolleyInterface vif) {
        String url = "http://www.baidu.com";
        try {
            url = URLHEAD + "/User/search?wd=" + URLEncoder.encode(word, "utf-8") +
                    "&userid=" + userId + "&openid=" + openId + "&page=" + page + "&count=" + Page_Per_Count;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        VolleyRequest.RequestGet(url, "searchForPerson", vif);
    }


    public static byte[] File2byte(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }


    /**
     * /User/login?mobile=&password=(md5)
     /User/register?mobile=&code=&password=(md5)
     /user/userinfo?userid=&openid=
     /Sms/get_code?mobile=
     /Sms/verify_code?mobile=&code=
     /Skill/intadskill?userid=&openid=&intesrst=&skill=
     /Skill/daka?userid=&openid=
     /Follow/follow?userid=&openid=&follow=
     /Follow/unfollow?userid=&openid=&unfollow=
     /Follow/fansList?userid=&openid=
     /Follow/followList?userid=&openid=
     /User/getAvatar?userid=&openid=&uid=
     /User/search?wd=

     *
     */
    /**
     * 登录:taji.whutech.com/User/login?mobile=&password=(md5)
     * 注册:taji.whutech.com/User/register?mobile=&code=&password=(md5)
     * 短信验证码:taji.whutech.com/Sms/get_code?mobile=
     * 校验验证码:taji.whutech.com/Sms/verify_code?mobile=&code=
     */


	/* 17091647364
     * {"status":"200","id":"1005","open_id":"a945852413f99f52bc6f2dabf969da8f"}
	 * http://taji.whutech.com/sms.php?mobile=17072750175
	 * http://taji.whutech.com/sms_verify?mobile=15207131254&code=8674
	 * http://taji.whutech.com/register?mobile=15207131254&code=&password=xxx
	 * http://taji.whutech.com/login?mobile=&password=
	 * 获取技能标签的接口：http://taji.whutech.com/skill
	 * 用户增加或更新兴趣和技能标签的接口：http://taji.whutech.com/intadskill?user=用户ID&interest=兴趣1.兴趣2.兴趣3....&skill=技能1.技能2.技能3...
	 * 用户关注大咖接口http://taji.whutech.com/follow?user=用户ID&follow=大咖ID
	 */


}
