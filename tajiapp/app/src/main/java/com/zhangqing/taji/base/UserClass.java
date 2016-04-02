package com.zhangqing.taji.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.zhangqing.taji.R;
import com.zhangqing.taji.activities.login.Test;
import com.zhangqing.taji.util.Md5Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UserClass {
    public static final int Request_Main = 101;
    public static final int Request_Register_First = 102;
    public static final int Request_Register_Second = 103;
    public static final int Request_Register_Third = 104;
    public static final int Request_Setting_Lable = 105;

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

    public void doGetDongTai(VolleyInterface vif) {

        String url = URLHEAD + "/DongTai?" + "userid=" + userId + "&openid=" + openId;
        VolleyRequest.RequestGet(url, "doGetDongTai", vif);

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


    public void getUserInfo(VolleyInterface vif) {
        VolleyRequest.RequestGet(URLHEAD + "/User/userInfo?userid=" + userId + "&openid=" + openId,
                "getUserInfo", vif);
    }

    public void getSkillListAll(VolleyInterface vif) {
        VolleyRequest.RequestGet(URLHEAD + "/Skill/skill?userid=" + userId + "&openid=" + openId, "getSkillListAll", vif);
    }

    public void getPersonsList(int whichPersonsButton, int page, VolleyInterface vif) {
        String url = "";
        switch (whichPersonsButton) {
            case Persons_Button_tudi:
                url = URLHEAD + "/Master/tudiList?userid=" + userId + "&openid=" + openId + "&count=20&page=" + page;
                break;
            case Persons_Button_shifu:
                url = URLHEAD + "/Master/masterList?userid=" + userId + "&openid=" + openId + "&count=20&page=" + page;
                break;
            case Persons_Button_dingyue:
                url = URLHEAD + "/Follow/followList?userid=" + userId + "&openid=" + openId + "&count=20&page=" + page;
                break;
            case Persons_Button_beidingyue:
                url = URLHEAD + "/Follow/fansList?userid=" + userId + "&openid=" + openId + "&count=20&page=" + page;
                break;
        }
        VolleyRequest.RequestGet(url, "getFollowList", vif);
    }

    public void getOthersUserInfo(String userid, VolleyInterface vif) {
        String url = URLHEAD + "/User/getAvatar?uid=" + userid + "&userid=" + userId + "&openid=" + openId;
        VolleyRequest.RequestGet(url, "getOthersUserInfo" + userid, vif);
    }

    public void searchForPerson(String word, VolleyInterface vif) {
        String url = "http://www.baidu.com";
        try {
            url = URLHEAD + "/User/search?wd=" + URLEncoder.encode(word, "utf-8") + "&userid=" + userId + "&openid=" + openId;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        VolleyRequest.RequestGet(url, "searchForPerson", vif);
    }


    public String uploadFile3(String fileName) {
        String result = "";
        String BOUNDARY = "---------------------------7db1c523809b2";//数据分割线
        File file = new File(fileName);   // 要上传的文件
        String host = "http://taji.whutech.com/Upload/uploadImg?userid=" + userId + "&openid=" + openId; // 这个字符串就是要上传的带参数的服务器地址

        try {
            byte[] after = ("--" + BOUNDARY + "--\r\n").getBytes("UTF-8");

            // 构造URL和Connection
            URL url = new URL(host);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // 设置HTTP协议的头属性
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            conn.setRequestProperty("Content-Length", String.valueOf(file.length()));
            conn.setRequestProperty("HOST", url.getHost());
            conn.setDoOutput(true);

            // 得到Connection的OutputStream流，准备写数据
            OutputStream out = conn.getOutputStream();
            InputStream in = new FileInputStream(file);


            // 写文件数据。因为服务器地址已经带有参数了，所以这里只要直接写入文件部分就可以了。
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }

            // 数据结束标志，整个HTTP报文就构造结束了。
            //out.write(after);

            in.close();
            out.close();

            Log.e("carter", "queryParam 返回码为: " + conn.getResponseCode());
            Log.e("carter", "queryParam 返回信息为: " + conn.getResponseMessage());

            InputStream is = conn.getInputStream();
            int ch;
            StringBuffer b = new StringBuffer();
            Log.e("uploadFile", "3");
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }

            result = b.toString();
            Log.e("re", b + "|");
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;
    }

    public String uploadFile2(String uploadFile) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("userid", userId);
        map.put("openid", openId);

        try {
            new Test().uploadForm(map, "files", new File(uploadFile), "1rwrs1.jpg", "http://taji.whutech.com/Upload/uploadImg");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("IOException", "1");
        }

        return "a";

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

    public String uploadFile(String uploadFile) {
        Log.e("uploadFile", "start");
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "WebKitFormBoundaryNXRvAQmIgK6GYVKS";
        String actionUrl = "http://taji.whutech.com/Upload/uploadImg";

        String props[] = {"userid", "openid", "submit"};
        String values[] = new String[3];
        values[0] = userId;
        values[1] = openId;
        values[2] = "上传文件";

        byte[] file = File2byte(uploadFile);
        Log.e("file", file.length + "|");


        StringBuffer sb = new StringBuffer();
// 发送每个字段:
        sb.append("\r\n");
        for (int i = 0; i < 3; i++) {
            sb = sb.append(twoHyphens);
            sb = sb.append(boundary);
            sb = sb.append("\r\n");
            sb = sb.append("Content-Disposition: form-data; name=\"" + props[i] + "\"\r\n\r\n");
            try {
                sb = sb.append(URLEncoder.encode(values[i], "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            sb = sb.append("\r\n");
        }
        sb = sb.append(twoHyphens);
        sb = sb.append(boundary);
        sb = sb.append("\r\n");
        sb = sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"1.jpg\"");
        sb = sb.append("Content-Type: image/jpeg\r\n\r\n");
        byte[] data = sb.toString().getBytes();
        byte[] end_data = ("\r\n" + twoHyphens + boundary + "--\r\n").getBytes();


        try

        {
            Log.e("uploadFile", "1");
            URL url = new URL(actionUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
          /* 允许Input、Output，不使用Cache */
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
          /* 设置传送的method=POST */
            con.setRequestMethod("POST");
          /* setRequestProperty */
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");
            con.setRequestProperty("Content-Length", String.valueOf(data.length + file.length + end_data.length));
            con.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
          /* 设置DataOutputStream */
            OutputStream os = con.getOutputStream();
            os.write(data);
            os.write(file);
            os.write(end_data);
            Log.e("data", new String(data, "utf-8") + "|");
            Log.e("file", "mm");
            Log.e("end_data", new String(end_data, "utf-8") + "|");
            os.flush();
            os.close();

//            DataOutputStream ds =
//                    new DataOutputStream(con.getOutputStream());
////                        ds.write(data);
////            os.write(file);
//
//
//            ds.writeBytes(twoHyphens + boundary + end);
//            ds.writeBytes("Content-Disposition: form-data; " +
//                    "name=\"userid\"" + end + end + userId + end);
//
//            ds.writeBytes(twoHyphens + boundary + end);
//            ds.writeBytes("Content-Disposition: form-data; " +
//                    "name=\"openid\"" + end + end + openId + end);
//
//            ds.writeBytes(twoHyphens + boundary + end);
//            ds.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"1.jpg\"\r\n" +
//                    "Content-Type: image/jpeg" + end + end);
//

//            ds.writeBytes("userid=" + userId + "&openid=" +
//                    openId );
//          /* 取得文件的FileInputStream */
//            FileInputStream fStream = new FileInputStream(uploadFile);
//          /* 设置每次写入1024bytes */
//            int bufferSize = 1024;
//            byte[] buffer = new byte[bufferSize];
//            int length = -1;
//          /* 从文件读取数据至缓冲区 */
//            Log.e("uploadFile", "2");
//            while ((length = fStream.read(buffer)) != -1) {
//            /* 将资料写入DataOutputStream中 */
//                ds.write(buffer, 0, length);
//            }
//
//            ds.writeBytes(end + twoHyphens + boundary + end);
//            ds.writeBytes("Content-Disposition: form-data; " +
//                    "name=\"submit\"" + end + end + URLEncoder.encode("上传文件", "utf-8"));
//
//            ds.writeBytes(end);
//            ds.writeBytes(twoHyphens + boundary + "--" + end);
//          /* close streams */
//            fStream.close();
//            ds.flush();
          /* 取得Response内容 */
            InputStream is = con.getInputStream();

            String result = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf8"));
            try {

                result = result + br.readLine();
                Log.e("uploadFile", "3" + "|" + result + "|" + userId + "|" + openId);
            } catch (IOException io) {

            }
            int ch;

            Log.e("uploadFile", "3" + "|" + result);

          /* 将Response显示于Dialog */
            //   showDialog("上传成功" + b.toString().trim());
          /* 关闭DataOutputStream */
            //         ds.close();
            //os.close();
            return result;
        } catch (Exception e) {
            Log.e("uploadFile", "failupload");
            return "failupload";
            // showDialog("上传失败" + e);
        }

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
