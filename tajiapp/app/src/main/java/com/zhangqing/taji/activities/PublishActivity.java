package com.zhangqing.taji.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.PagerAdapter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.zhangqing.taji.BaseActivity;
import com.zhangqing.taji.R;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.util.CameraUtil;
import com.zhangqing.taji.util.ImageUtil;
import com.zhangqing.taji.util.LocationUtil;
import com.zhangqing.taji.util.UploadUtil;
import com.zhangqing.taji.view.ResizeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/2/10.
 * 底部中间图标点进来的分享技能秀Activity
 */
public class PublishActivity extends BaseActivity implements View.OnClickListener, EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener, UploadUtil.OnUploadProcessListener {
    private static final int BIGGER = 1;
    private static final int SMALLER = 2;

    private static final int MSG_RESIZE = 1;
    private static final int MSG_UPLOAD_ING = 2;
    private static final int MSG_UPLOAD_INIT = 3;
    private static final int MSG_UPLOAD_DONE = 4;

    /***
     * 使用照相机拍照获取图片
     */
    public static final int SELECT_PIC_BY_TACK_PHOTO = 1;

    private static final int HEIGHT_THREADHOLD = 30;

    private ScrollView scrollView;
    private ImageView faceToggle;

    private LinearLayout parentViewEdittext;
    private FrameLayout parentViewFaceGrid;
    private TextView PublishBtn;

    // private ViewPager viewPagerFace;
    private EmojiconEditText editText;

    private boolean isImmShowing = false;

    private List<ImageView> pointList;

    private String real_path_scaled;
    private ImageView mCoverView;

    private String mCoverUrl = null;

    private TextView mLocationTextView;

    private ImageView mMasterCircleImageView;
    private boolean isMasterCircle = false;


    Handler mHandler = new Handler() {
        private int MAX_UPLOAD_SIZE;
        private ClipDrawable clipDrawable;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_RESIZE: {
                    if (msg.arg1 == BIGGER) {
                        // findViewById(R.id.publish_parentview_cover).setVisibility(View.VISIBLE);
                        PublishBtn.setVisibility(View.VISIBLE);
                        isImmShowing = false;
                        //parentViewFaceGrid.setVisibility(View.VISIBLE);
                    } else {
                        // findViewById(R.id.publish_parentview_cover).setVisibility(View.GONE);
                        isImmShowing = true;
                        PublishBtn.setVisibility(View.GONE);

                        hideFaceGrid();

                        scrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("top2", parentViewEdittext.getTop() + "|" + parentViewEdittext.getBottom() + "|" + scrollView.getHeight());
                                scrollView.smoothScrollTo(0, parentViewEdittext.getBottom() - scrollView.getHeight());
                            }
                        });
                    }
                    break;
                }
                case MSG_UPLOAD_ING: {
                    clipDrawable.setLevel(10000 * msg.arg1 / MAX_UPLOAD_SIZE);
                    break;
                }
                case MSG_UPLOAD_INIT: {
                    clipDrawable = new ClipDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeFile(real_path_scaled)), Gravity.LEFT, ClipDrawable.HORIZONTAL);
                    mCoverView.setImageDrawable(clipDrawable);
                    clipDrawable.setLevel(0);
                    MAX_UPLOAD_SIZE = msg.arg1;
                    break;
                }
                case MSG_UPLOAD_DONE: {
                    clipDrawable.setLevel(10000);
                    Toast.makeText(PublishActivity.this, "上传成功\r\n" + mCoverUrl, Toast.LENGTH_LONG).show();
                }
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private TranslateAnimation mShowAction;
    private TranslateAnimation mHiddenAction;
    private Uri photoUri;
    private String picPath;

    public void disableSoftInputMethod(EditText ed) {
        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        int currentVersion = android.os.Build.VERSION.SDK_INT;
        String methodName = null;
        if (currentVersion >= 16) {
            // 4.2
            methodName = "setShowSoftInputOnFocus";
        } else if (currentVersion >= 14) {
            // 4.0
            methodName = "setSoftInputShownOnFocus";
        }

        if (methodName == null) {
            ed.setInputType(InputType.TYPE_NULL);
        } else {
            Class<EditText> cls = EditText.class;
            Method setShowSoftInputOnFocus;
            try {
                setShowSoftInputOnFocus = cls.getMethod(methodName, boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(ed, false);
            } catch (NoSuchMethodException e) {
                ed.setInputType(InputType.TYPE_NULL);
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onUploadDone(int responseCode, String message) {
        Log.e("onUploadDone", message);
        try {
            JSONObject jsonObject = new JSONObject(message);
            mCoverUrl = jsonObject.getJSONObject("data").getString("url");
        } catch (JSONException e) {
            mCoverUrl = null;
        }
        sendMessage(MSG_UPLOAD_DONE, 0);
        //Toast.makeText(PublishActivity.this,message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUploadProcess(int uploadSize) {

        Log.e("onUploadProcess", uploadSize + "|");
        sendMessage(MSG_UPLOAD_ING, uploadSize);
    }

    @Override
    public void initUpload(int fileSize) {
        Log.e("initUpload", fileSize + "|");
        sendMessage(MSG_UPLOAD_INIT, fileSize);

    }

    private void sendMessage(int what, int arg1) {
        Message msg = new Message();
        msg.what = what;
        msg.arg1 = arg1;
        mHandler.sendMessage(msg);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        Log.e("onActivityResult", requestCode + "|" + resultCode + "|" + (data == null ? "null" : (data)));

        if (data == null || data.getData() == null) return;
        Uri uri = data.getData();
        Log.e("uri", uri.getPath());

        // UserClass.getInstance().doUploadPhoto(uri.getPath(), this);
        doPhoto(uri);
        //doPhoto(requestCode, data);
    }

    /**
     * 选择图片后，获取图片的路径
     *
     * @param uri
     */
    private void doPhoto(Uri uri) {
        File uploadFile = null;
        String real_path = uri.getPath();
        if (real_path == null || !((real_path.endsWith(".png") || real_path.endsWith(".PNG") || real_path.endsWith(".jpg") || real_path.endsWith(".JPG")))) {
            String[] pojo = {MediaStore.Images.Media.DATA};
            Cursor cursor = new CursorLoader(this, uri, pojo, null, null, null).loadInBackground();
            //Cursor cursor = managedQuery(uri, pojo, null, null, null);
            if (cursor != null) {
                int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
                cursor.moveToFirst();
                real_path = cursor.getString(columnIndex);
                cursor.close();
            }
        }

        Log.e("doPhoto", "imagePath = " + real_path);
        if (real_path != null && (real_path.endsWith(".png") || real_path.endsWith(".PNG") || real_path.endsWith(".jpg") || real_path.endsWith(".JPG"))) {
            real_path_scaled = real_path.substring(0, real_path.lastIndexOf("/") + 1) + "DCIM" + System.currentTimeMillis() + real_path.substring(real_path.lastIndexOf("."), real_path.length());
            Log.e("newname", real_path_scaled + "|");
            try {
                ImageUtil.ratioAndGenThumb(real_path, real_path_scaled, 600, 800, false);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("compressAndGenImage", "OnError");
                real_path_scaled = real_path;
            }
            UserClass.getInstance().doUploadPhoto(real_path_scaled, this);


            // /storage/emulated/0/DCIM/Camera/IMG_20160303_140435.jpg
        } else {
            Toast.makeText(this, "选择图片文件不正确", Toast.LENGTH_LONG).show();
        }
    }


    public void onClickUpload(View v) {
//        new AsyncTask<Void, Void, String>() {
//
//            @Override
//            protected String doInBackground(Void... params) {
//                String result = UserClass.getInstance().uploadFile("/storage/emulated/0/DCIM/Camera/1.jpg");
//                return result;
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
//                super.onPostExecute(s);
//                Toast.makeText(PublishActivity.this, s, Toast.LENGTH_SHORT).show();
//            }
//        }.execute(new Void[]{});

        Intent intent = CameraUtil.selectPhoto();
        if (null != intent) {

            startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setContentView(R.layout.activity_publish);

        scrollView = (ScrollView) findViewById(R.id.publish_scrollview);
        //   viewPagerFace = (ViewPager) findViewById(R.id.publish_viewpager);
        editText = (EmojiconEditText) findViewById(R.id.publish_edittext);
        faceToggle = (ImageView) findViewById(R.id.publish_face_toggle_btn);

        mCoverView = (ImageView) findViewById(R.id.publish_cover);
        mLocationTextView = (TextView) findViewById(R.id.publish_location_text);

        mMasterCircleImageView = (ImageView) findViewById(R.id.publish_master_circle);
        mMasterCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMasterCircle) {
                    isMasterCircle = false;
                    mMasterCircleImageView.setImageResource(R.drawable.icon_tab_publish_shitu_unselect);
                } else {
                    isMasterCircle = true;
                    mMasterCircleImageView.setImageResource(R.drawable.icon_tab_publish_shitu_select);
                }
            }
        });

        disableSoftInputMethod(editText);

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.e("onFocusChange", "z" + hasFocus);
                onClickEditText(editText);
            }
        });

        parentViewEdittext = (LinearLayout) findViewById(R.id.publish_parentview_edittext);
        parentViewFaceGrid = (FrameLayout) findViewById(R.id.publish_container_facegrid);
        PublishBtn = (TextView) findViewById(R.id.publish_publish_btn);

        PublishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserClass.getInstance().doUploadDongTai(mCoverUrl,
                        editText.getText().toString(),
                        mLocationTextView.getText().toString(),
                        isMasterCircle,
                        new VolleyInterface(getApplicationContext()) {
                            @Override
                            public void onMySuccess(JSONObject jsonObject) {

                            }

                            @Override
                            public void onMyError(VolleyError error) {

                            }
                        });
            }
        });

        ResizeLayout resizeLayout = (ResizeLayout) findViewById(R.id.publish_rootview);
        resizeLayout.setOnResizeListener(new ResizeLayout.OnResizeListener() {
            @Override
            public void OnResize(int w, int h, int oldw, int oldh) {
                int change = BIGGER;
                if (h < oldh) {
                    change = SMALLER;
                }

                Log.e("setOnResizeListener", "" + change);

//                Message msg = new Message();
//                msg.what = 1;
//                msg.arg1 = change;
//                mHandler.sendMessage(msg);
            }
        });
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.publish_container_facegrid, EmojiconsFragment.newInstance(false))
                .commit();

        parentViewFaceGrid.setVisibility(View.GONE);
        faceToggle.setTag(R.drawable.icon_tab_publish_face);

        LocationUtil.getLocation(this, new LocationUtil.OnLocatedListener() {
            @Override
            public void onLocatedSuccess(String location) {
                mLocationTextView.setText(location);
            }
        });
    }


    @Override
    public void finish() {
        // TODO Auto-generated method stub
        super.finish();
        //关闭窗体动画显示
        this.overridePendingTransition(0, R.anim.activity_open_bottom_out);
    }

    public void onClickBtnFinish(View v) {
        finish();
    }


    public void onClickBtnShowFaceGrid(View v) {

        Integer currentResInteger = (Integer) faceToggle.getTag();
        int currentResId = currentResInteger == null ? R.drawable.icon_tab_publish_imm : currentResInteger.intValue();

        if (currentResId == R.drawable.icon_tab_publish_face) {
            toggleFaceImm(true, false);
        } else {
            toggleFaceImm(false, true);
        }
    }


    public void onClickEditText(View v) {
        Log.e("onClickEditText", "a");
        if (parentViewFaceGrid.getVisibility() == View.GONE) {
            Log.e("onClickEditText", "requestFocus");
            editText.requestFocus();
            showImm(false);
        } else {
            Log.e("onClickEditText", "donotFocus");
        }
    }


    private void toggleFaceImm(boolean isShowFaceGrid, boolean isShowImm) {
        boolean ifWait = false;

//        if (isShowFaceGrid | isShowImm) {
//            PublishBtn.setVisibility(View.GONE);
//        } else {
//            PublishBtn.setVisibility(View.VISIBLE);
//        }


        if (!isShowFaceGrid) {
            ifWait = hideFaceGrid();
        }
        if (!isShowImm) {
            ifWait = ifWait | hideImm();
        }
        if (isShowFaceGrid) {
            showFaceGrid(ifWait);
        }
        if (isShowImm) {
            showImm(ifWait);
        }


    }

    private void showImm(boolean postDelay) {
        isImmShowing = true;
        if (postDelay == false) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, 0);

        } else {
            editText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText, 0);
                }
            }, 500);
        }
    }

    private boolean hideImm() {

        if (isImmShowing) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            isImmShowing = false;
            return true;
        } else {
            return false;
        }
    }

    /**
     * showFaceGrid
     *
     * @return if need wait for animation
     */
    private void showFaceGrid(boolean postDelay) {

        faceToggle.setImageResource(R.drawable.icon_tab_publish_imm);
        faceToggle.setTag(R.drawable.icon_tab_publish_imm);
        if (parentViewFaceGrid.getVisibility() == View.GONE) {
            mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            //mShowAction.setInterpolator(new BounceInterpolator());
            mShowAction.setDuration(500);
            if (postDelay) {
                parentViewFaceGrid.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        parentViewFaceGrid.setAnimation(mShowAction);
                        parentViewFaceGrid.setVisibility(View.VISIBLE);
                    }
                }, 500);

            } else {
                parentViewFaceGrid.setAnimation(mShowAction);
                parentViewFaceGrid.setVisibility(View.VISIBLE);
            }//return true;
        }
    }

    /**
     * hideFaceGrid
     *
     * @return if need wait for animation
     */
    private boolean hideFaceGrid() {
        faceToggle.setImageResource(R.drawable.icon_tab_publish_face);
        faceToggle.setTag(R.drawable.icon_tab_publish_face);

        if (parentViewFaceGrid.getVisibility() == View.VISIBLE) {

            mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                    0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    1.0f);
            mHiddenAction.setDuration(500);

            parentViewFaceGrid.setAnimation(mHiddenAction);

            parentViewFaceGrid.setVisibility(View.GONE);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        //Log.e("onClickFace",v.getTag(R.id.tagkey_which)+"|");
        //((MyEditText)editText).insertDrawable((String) v.getTag(R.id.tagkey_which));
        Log.e("onClickFace", editText.getText().toString());
    }

    @Override
    public void onBackPressed() {
        if (parentViewFaceGrid.getVisibility() == View.GONE) {
            super.onBackPressed();
        } else {
            hideFaceGrid();
        }
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(editText);
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(editText, emojicon);
    }

    @Override
    public void onPause() {
        if (LocationUtil.mlocationClient != null) {
            LocationUtil.mlocationClient.stopLocation();//停止定位
            LocationUtil.mlocationClient.onDestroy();
        }
        super.onPause();

    }
}
