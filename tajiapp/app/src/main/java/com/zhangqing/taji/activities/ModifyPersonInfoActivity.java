package com.zhangqing.taji.activities;

import android.content.Intent;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.sdk.android.media.upload.UploadListener;
import com.alibaba.sdk.android.media.upload.UploadTask;
import com.alibaba.sdk.android.media.utils.FailReason;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.zhangqing.taji.BaseActivity;
import com.zhangqing.taji.MyApplication;
import com.zhangqing.taji.R;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.util.CameraUtil;
import com.zhangqing.taji.util.OneSdkUtil;

import org.json.JSONObject;

/**
 * Created by zhangqing on 2016/4/26.
 * 我的个人信息设置界面
 */
public class ModifyPersonInfoActivity extends BaseActivity {
    private static final int REQUEST_NAME = 1;
    private static final int REQUEST_SEX = 2;
    private static final int REQUEST_SIGN = 3;
    private static final int REQUEST_SCHOOL = 4;
    private static final int REQUEST_AVATAR_SELECT = 5;
    private static final int REQUEST_AVATAR_CROP = 6;

    private TextView tv_name;
    private TextView tv_sex;
    private TextView tv_sign;
    private TextView tv_school;

    private ImageView iv_avatar;

    private String avatar_url;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_person_info);

        tv_name = (TextView) findViewById(R.id.modify_person_name);
        tv_sex = (TextView) findViewById(R.id.modify_person_sex);
        tv_sign = (TextView) findViewById(R.id.modify_person_sign);
        tv_school = (TextView) findViewById(R.id.modify_person_school);

        iv_avatar = (ImageView) findViewById(R.id.modify_person_avatar);

        tv_name.setText(UserClass.getInstance().getStringByKey("username"));
        tv_sex.setText(UserClass.getInstance().getStringByKey("sex"));
        tv_sign.setText(UserClass.getInstance().getStringByKey("signature"));
        tv_school.setText(UserClass.getInstance().getStringByKey("school"));

        avatar_url = UserClass.getInstance().getStringByKey("avatar");
        ImageLoader.getInstance().displayImage(avatar_url, new ImageViewAware(iv_avatar), MyApplication.getCircleDisplayImageOptions());

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.modify_person_name_container: {
                Intent intent = new Intent(this, EditActivity.class);
                intent.putExtra("title", "设置昵称");
                intent.putExtra("default", tv_name.getText().toString());
                startActivityForResult(intent, REQUEST_NAME);
                break;
            }
            case R.id.modify_person_sex_container: {
                Intent intent = new Intent(this, EditActivity.class);
                intent.putExtra("title", "设置性别");
                intent.putExtra("default", tv_sex.getText().toString());
                startActivityForResult(intent, REQUEST_SEX);
                break;
            }
            case R.id.modify_person_sign_container: {
                Intent intent = new Intent(this, EditActivity.class);
                intent.putExtra("title", "设置签名");
                intent.putExtra("default", tv_sign.getText().toString());
                startActivityForResult(intent, REQUEST_SIGN);
                break;
            }
            case R.id.modify_person_school_container: {
                Intent intent = new Intent(this, EditActivity.class);
                intent.putExtra("title", "设置学校");
                intent.putExtra("default", tv_school.getText().toString());
                startActivityForResult(intent, REQUEST_SCHOOL);
                break;
            }
            case R.id.modify_person_avatar: {
                startActivityForResult(CameraUtil.Picture.choosePicture2(), REQUEST_AVATAR_SELECT);
                break;
            }
            case R.id.modify_commit: {
                UserClass.getInstance().doModifyMemberDetail(
                        tv_name.getText().toString(),
                        tv_sex.getText().toString(),
                        tv_school.getText().toString(),
                        tv_sign.getText().toString(),
                        avatar_url,
                        new VolleyInterface(getApplicationContext()) {
                            @Override
                            public void onMySuccess(JSONObject jsonObject) {
                                setResult(RESULT_OK);
                                finish();
                            }

                            @Override
                            public void onMyError(VolleyError error) {

                            }
                        });

                break;
            }


        }
    }

    private Uri mOutputCropUri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case REQUEST_NAME: {
                tv_name.setText(data.getStringExtra("data"));
                break;
            }
            case REQUEST_SEX: {
                tv_sex.setText(data.getStringExtra("data"));
                break;
            }
            case REQUEST_SIGN: {
                tv_sign.setText(data.getStringExtra("data"));
                break;
            }
            case REQUEST_SCHOOL: {
                tv_school.setText(data.getStringExtra("data"));
                break;
            }
            case REQUEST_AVATAR_SELECT: {
                mOutputCropUri = CameraUtil.Picture.createOutputUri(this);
                startActivityForResult(CameraUtil.Picture.cropPicture(data.getData(), mOutputCropUri), REQUEST_AVATAR_CROP);
                break;
            }
            case REQUEST_AVATAR_CROP: {
                String path = CameraUtil.uri2filePath(this, mOutputCropUri);
                Log.e("REQUEST_AVATAR_CROP", path);
                OneSdkUtil.upLoadFile(path, new UploadListener() {
                    @Override
                    public void onUploading(UploadTask uploadTask) {

                    }

                    @Override
                    public void onUploadFailed(UploadTask uploadTask, FailReason failReason) {

                    }

                    @Override
                    public void onUploadComplete(UploadTask uploadTask) {
                        avatar_url = uploadTask.getResult().url;
                        ImageLoader.getInstance().displayImage(avatar_url, new ImageViewAware(iv_avatar), MyApplication.getCircleDisplayImageOptions());
                    }

                    @Override
                    public void onUploadCancelled(UploadTask uploadTask) {

                    }
                });
            }
        }
    }

    public void finishThis(View v) {
        finish();
    }
}
