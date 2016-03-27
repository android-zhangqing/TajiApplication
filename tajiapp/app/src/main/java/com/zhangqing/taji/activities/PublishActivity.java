package com.zhangqing.taji.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.text.InputType;
import android.util.Log;
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
import android.widget.Toast;

import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.zhangqing.taji.MyApplication;
import com.zhangqing.taji.R;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.util.UploadUtil;
import com.zhangqing.taji.view.ResizeLayout;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/2/10.
 */
public class PublishActivity extends FragmentActivity implements View.OnClickListener, EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener, UploadUtil.OnUploadProcessListener {
    private static final int BIGGER = 1;
    private static final int SMALLER = 2;
    private static final int MSG_RESIZE = 1;

    /***
     * 使用照相机拍照获取图片
     */
    public static final int SELECT_PIC_BY_TACK_PHOTO = 1;

    private static final int HEIGHT_THREADHOLD = 30;

    private ScrollView scrollView;
    private ImageView faceToggle;

    private LinearLayout parentViewEdittext;
    private FrameLayout parentViewFaceGrid;
    private LinearLayout parentViewPublishBtn;

    // private ViewPager viewPagerFace;
    private EmojiconEditText editText;

    private boolean isImmShowing = false;

    private List<ImageView> pointList;


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_RESIZE: {
                    if (msg.arg1 == BIGGER) {
                        // findViewById(R.id.publish_parentview_cover).setVisibility(View.VISIBLE);
                        parentViewPublishBtn.setVisibility(View.VISIBLE);
                        isImmShowing = false;
                        //parentViewFaceGrid.setVisibility(View.VISIBLE);
                    } else {
                        // findViewById(R.id.publish_parentview_cover).setVisibility(View.GONE);
                        isImmShowing = true;
                        parentViewPublishBtn.setVisibility(View.GONE);

                        hideFaceGrid();

                        scrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("top2", parentViewEdittext.getTop() + "|" + parentViewEdittext.getBottom() + "|" + scrollView.getHeight());
                                scrollView.smoothScrollTo(0, parentViewEdittext.getBottom() - scrollView.getHeight());
                            }
                        });
                    }
                }
                break;

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


    /**
     * 拍照获取图片
     */
    private void takePhoto() {
//执行拍照前，应该先判断SD卡是否存在
        String SDState = Environment.getExternalStorageState();
        if (SDState.equals(Environment.MEDIA_MOUNTED)) {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//"android.media.action.IMAGE_CAPTURE"
/***
 * 需要说明一下，以下操作使用照相机拍照，拍照后的图片会存放在相册中的
 * 这里使用的这种方式有一个好处就是获取的图片是拍照后的原图
 * 如果不实用ContentValues存放照片路径的话，拍照后获取的图片为缩略图不清晰
 */
            ContentValues values = new ContentValues();
            photoUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
/**-----------------*/
            startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
        } else {
            Toast.makeText(this, "内存卡不存在", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        doPhoto(requestCode,data);
    }

    /**
     * 选择图片后，获取图片的路径
     * @param requestCode
     * @param data
     */
    private void doPhoto(int requestCode,Intent data)
    {

        String[] pojo = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(photoUri, pojo, null, null,null);
        if(cursor != null )
        {
            int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
            cursor.moveToFirst();
            picPath = cursor.getString(columnIndex);
            cursor.close();
        }
        Log.i("doPhoto", "imagePath = "+picPath);
        if(picPath != null && ( picPath.endsWith(".png") || picPath.endsWith(".PNG") ||picPath.endsWith(".jpg") ||picPath.endsWith(".JPG") ))
        {
            UploadUtil uploadUtil = UploadUtil.getInstance();
            ;
            uploadUtil.setOnUploadProcessListener(this); //设置监听器监听上传状态

            Map<String, String> params = new HashMap<String, String>();
            params.put("userid", UserClass.getInstance().userId);
            params.put("openid", UserClass.getInstance().openId);
            uploadUtil.uploadFile(picPath,
                    "file", "http://taji.whutech.com/Upload/uploadImg", params);
            //"/storage/emulated/0/123.png",
        }else{
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

        takePhoto();

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
        parentViewPublishBtn = (LinearLayout) findViewById(R.id.publish_parentview_publish_btn);


//        pointList = new ArrayList<ImageView>();
//        ImageView iv1 = (ImageView) findViewById(R.id.publish_face_point1);
//        pointList.add(iv1);
//        ImageView iv2 = (ImageView) findViewById(R.id.publish_face_point2);
//        pointList.add(iv2);
//        ImageView iv3 = (ImageView) findViewById(R.id.publish_face_point3);
//        pointList.add(iv3);


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
//        viewPagerFace.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                for (int i = 0; i < pointList.size(); i++) {
//                    if (i == position) {
//                        pointList.get(i).setImageResource(R.drawable.icon_viewpager_point_selected);
//                    } else {
//                        pointList.get(i).setImageResource(R.drawable.icon_viewpager_point_unselected);
//                    }
//                }
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//
//        MyFacePagerAdapter myFacePagerAdapter=new MyFacePagerAdapter(this);
//        //myFacePagerAdapter.setOnClickListener(this);
//        viewPagerFace.setAdapter(myFacePagerAdapter);

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
//            parentViewPublishBtn.setVisibility(View.GONE);
//        } else {
//            parentViewPublishBtn.setVisibility(View.VISIBLE);
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
    public void onUploadDone(int responseCode, String message) {
        Log.e("onUploadDone", message);
    }

    @Override
    public void onUploadProcess(int uploadSize) {
        Log.e("onUploadProcess", uploadSize + "|");
    }

    @Override
    public void initUpload(int fileSize) {
        Log.e("initUpload", fileSize + "|");
    }


    class MyFacePagerAdapter extends PagerAdapter {
        private final int ROW_NUM = 3;
        private final int COLUMN_NUM = 7;
        List<View> mviewList;
        Context mContext;

        View.OnClickListener clickListener = null;


        public MyFacePagerAdapter(View.OnClickListener listener) {
            this.mContext = PublishActivity.this;
            this.clickListener = listener;

            mviewList = new ArrayList<View>();


            int i = 1;

            while (i <= 56) {
                LinearLayout rootLayout = new LinearLayout(mContext);
                rootLayout.setOrientation(LinearLayout.VERTICAL);

                for (int row = 0; row < ROW_NUM; row++) {
                    LinearLayout rowLayout = new LinearLayout(mContext);
                    rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                    outer:
                    for (int column = 0; column < COLUMN_NUM; column++) {
                        if (column == COLUMN_NUM - 1 && row == ROW_NUM - 1) {
                            ImageView iv = new ImageView(mContext);
                            iv.setScaleType(ImageView.ScaleType.CENTER);
                            iv.setImageResource(R.drawable.icon_register_first_inputcha);
                            rowLayout.addView(iv, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));


                        } else {

                            int resourceId = R.drawable.ic_launcher;
                            try {
                                Field field = R.drawable.class.getDeclaredField("emoji_" + i);
                                resourceId = Integer.parseInt(field.get(null).toString());
                            } catch (NoSuchFieldException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            ImageView iv = new ImageView(mContext);
                            iv.setScaleType(ImageView.ScaleType.CENTER);
                            iv.setImageResource(resourceId);
                            iv.setTag(R.id.tagkey_which, i + "");
                            iv.setOnClickListener(clickListener);

                            rowLayout.addView(iv, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
                            i++;
                        }

                    }

                    rootLayout.addView(rowLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

                }
                mviewList.add(rootLayout);


            }


        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mviewList.get(position), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return mviewList.get(position);
        }

        @Override
        public int getCount() {
            return mviewList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mviewList.get(position));
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

}
