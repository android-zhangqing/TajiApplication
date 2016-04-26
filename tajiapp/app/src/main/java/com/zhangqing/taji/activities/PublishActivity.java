package com.zhangqing.taji.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.media.upload.UploadListener;
import com.alibaba.sdk.android.media.upload.UploadTask;
import com.alibaba.sdk.android.media.utils.FailReason;
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
import com.zhangqing.taji.util.ImmUtil;
import com.zhangqing.taji.util.LocationUtil;
import com.zhangqing.taji.util.OneSdkUtil;
import com.zhangqing.taji.view.ResizeLayout;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2016/2/10.
 * 底部中间图标点进来的分享技能秀Activity
 */
public class PublishActivity extends BaseActivity implements View.OnClickListener, EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {
    private static final int BIGGER = 1;
    private static final int SMALLER = 2;

    private static final int MSG_RESIZE = 1;
    private static final int MSG_UPLOAD_ING = 2;
    private static final int MSG_UPLOAD_INIT = 3;
    private static final int MSG_UPLOAD_DONE = 4;


    public static final int TODO_SELECT_PIC = 1;
    public static final int TODO_CAPTURE_PIC = 2;
    public static final int TODO_SELECT_VIDEO = 3;
    public static final int TODO_CAPTURE_VIDEO = 4;
    public static final int TODO_SELECT_COVER = 5;

    public static final int MODE_PICTURE = 1;
    public static final int MODE_VIDEO = 2;
    private int mActivityMode = MODE_VIDEO;

    public String mPathVideo = "";
    public String mPathCover = "";

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
                    clipDrawable.setLevel(msg.arg1);
                    Log.e("arg1:", msg.arg1 + "|");
                    break;
                }
                case MSG_UPLOAD_INIT: {
                    clipDrawable = new ClipDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeFile(mPathCover)), Gravity.LEFT, ClipDrawable.HORIZONTAL);
                    mCoverView.setImageDrawable(clipDrawable);
                    clipDrawable.setLevel(1);
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
        if (data != null && data.getData() != null) {
            Log.e("real_uri=", CameraUtil.uri2filePath(this, data.getData()));
        }
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case TODO_CAPTURE_VIDEO: {
                if (data == null) return;
                mPathVideo = CameraUtil.uri2filePath(getApplicationContext(), data.getData());
                Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(mPathVideo, MediaStore.Video.Thumbnails.MINI_KIND);
                uploadFile(mPathVideo);
                break;
            }
            case TODO_SELECT_VIDEO: {
                break;
            }
        }
    }

    private void uploadPicture(Uri uri) {
        String real_path = CameraUtil.uri2filePath(this, uri);

        Log.e("doPhoto", "real_path = " + real_path);
        if (real_path != null && CameraUtil.isPictureFilePath(real_path)) {
            real_path_scaled = CameraUtil.Picture.getAppendPath(real_path, "scaled");
            try {
                ImageUtil.ratioAndGenThumb(real_path, real_path_scaled, 600, 800, false);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("compressAndGenImage", "OnError");
                real_path_scaled = real_path;
            }
            Log.e("real_path_scaled", real_path_scaled + "|");

            //UserClass.getInstance().doUploadPhoto(real_path_scaled, this);
            uploadFile(real_path_scaled);
            // /storage/emulated/0/DCIM/Camera/IMG_20160303_140435.jpg
        } else {
            Toast.makeText(this, "选择图片文件不正确", Toast.LENGTH_LONG).show();
        }
    }

    private void uploadFile(String real_path) {
        sendMessage(MSG_UPLOAD_INIT, 0);
        OneSdkUtil.upLoadFile(real_path, new UploadListener() {

            @Override
            public void onUploading(UploadTask uploadTask) {
                Log.e("OneSdkUtil", "onUploading|" + uploadTask.getCurrent() + "|" + uploadTask.getTotal());

                sendMessage(MSG_UPLOAD_ING, (int) (10000 * uploadTask.getCurrent() / uploadTask.getTotal()));

            }

            @Override
            public void onUploadFailed(UploadTask uploadTask, FailReason failReason) {
                Log.e("OneSdkUtil", "onUploadFailed|");
                Toast.makeText(getApplicationContext(), "上传失败：" + failReason.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUploadComplete(UploadTask uploadTask) {
                Log.e("OneSdkUtil", "onUploadComplete|" + uploadTask.getResult().url);
                Toast.makeText(getApplicationContext(), "上传成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUploadCancelled(UploadTask uploadTask) {
                Log.e("OneSdkUtil", "onUploadCancelled|");
            }
        });
    }


    private Uri mOutputUri = null;

    public void onClickUpload(View v) {

        switch (v.getId()) {
            case R.id.publish_upload_video:
                startActivityForResult(CameraUtil.Video.captureVideo(), TODO_CAPTURE_VIDEO);
                break;
            case R.id.publish_upload_cover:
                startActivityForResult(CameraUtil.Picture.choosePicture(), TODO_SELECT_COVER);
                break;
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

        ImmUtil.disableSoftInputMethod(editText);

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

                if (mCoverUrl == null || mCoverUrl.equals("")) {
                    Toast.makeText(getApplicationContext(), "请上传图片", Toast.LENGTH_SHORT).show();
                    return;
                } else if (editText.getText() == null || editText.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "说点什么吧", Toast.LENGTH_SHORT).show();
                    return;
                }
                UserClass.getInstance().doUploadDongTai(mCoverUrl,
                        editText.getText().toString(),
                        mLocationTextView.getText().toString(),
                        isMasterCircle,
                        new VolleyInterface(getApplicationContext()) {
                            @Override
                            public void onMySuccess(JSONObject jsonObject) {
                                Toast.makeText(getApplicationContext(), jsonObject.toString(), Toast.LENGTH_LONG).show();
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

        mLocationTextView.setText("");
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
