package com.zhangqing.taji.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.util.MD5Util;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.zhangqing.taji.MyApplication;
import com.zhangqing.taji.R;
import com.zhangqing.taji.activities.PhotoViewerActivity;
import com.zhangqing.taji.adapter.listener.AvatarClickListener;
import com.zhangqing.taji.adapter.listener.DongTaiClickListener;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.bean.DongTaiBean;
import com.zhangqing.taji.util.PathUtil;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangqing on 2016/4/8.
 */
public class DongTaiListAdapter extends RecyclerView.Adapter<DongTaiListAdapter.MyViewHolder> {

    private Context mContext;
    private boolean isToEchoTime = false;

    private List<DongTaiBean> mDongTaiList = new ArrayList<DongTaiBean>();

    public DongTaiListAdapter(Context context) {
        mContext = context;
    }


    /**
     * 该静态方法同时还供【动态详情】页面进行代码复用
     *
     * @param mContext
     * @param holder
     * @param dongTaiClass
     * @param adapter      不为null表示供bindView调用， 为null表示供外界复用
     */
    public static void updateViewHolder(final Context mContext, final MyViewHolder holder, final DongTaiBean dongTaiClass, final RecyclerView.Adapter adapter) {
        holder.tv_name.setText(dongTaiClass.mPersonInfo.username);
        holder.tv_content.setText(dongTaiClass.mContent);
        holder.tv_label_parent.setText(dongTaiClass.mLabelParentSkill);
        holder.tv_label_child.setText(dongTaiClass.mLabelChildTag.replaceAll("\\.", "  "));

        holder.tv_count_forward.setText(dongTaiClass.mCountForward);
        holder.tv_count_comment.setText(dongTaiClass.mCountComment);
        holder.tv_count_like.setText(dongTaiClass.mCountLike);

        holder.iv_count_like_icon.setImageResource(dongTaiClass.isLike ?
                R.drawable.icon_tab_home_hot_favor_selelct2 : R.drawable.icon_tab_home_hot_favor);

        holder.tv_delete.setVisibility(View.INVISIBLE);
        if (dongTaiClass.mPersonInfo.userid.equals(UserClass.getInstance().userId)) {
            holder.tv_follow.setVisibility(View.INVISIBLE);

            if (adapter == null) {
                holder.tv_delete.setVisibility(View.VISIBLE);
                holder.tv_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserClass.getInstance().dongTaiDoDelete(dongTaiClass.mTid, new VolleyInterface(mContext.getApplicationContext()) {
                            @Override
                            public void onMySuccess(JSONObject jsonObject) {
                                Toast.makeText(mContext.getApplicationContext(),
                                        jsonObject.optString("msg", "删除成功") + "，请自行刷新", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onMyError(VolleyError error) {

                            }
                        });
                    }
                });
            }
        } else {
            holder.tv_follow.setVisibility(View.VISIBLE);
            updateFollowButton(holder.tv_follow, dongTaiClass.mPersonInfo.is_follow);
        }

        holder.iv_avatar.setOnClickListener(new AvatarClickListener(mContext, dongTaiClass.mUserId, dongTaiClass.mPersonInfo.username));


        initPartViewUniversal(mContext, holder, dongTaiClass);
        if (adapter != null) {
            View.OnClickListener onClickListener = new DongTaiClickListener(mContext,
                    dongTaiClass.mTid);
            holder.cmv_media.setOnClickListener(onClickListener);
            holder.ll_count_comment_container.setOnClickListener(onClickListener);
        } else {
            initPartViewDetail(mContext, holder, dongTaiClass);
        }

        /**
         * 订阅、关注 按钮
         */
        holder.tv_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserClass.getInstance().dongTaiDoFollow(dongTaiClass.mUserId,
                        !dongTaiClass.mPersonInfo.is_follow, new VolleyInterface(mContext.getApplicationContext()) {
                            @Override
                            public void onMySuccess(JSONObject jsonObject) {
                                Log.e("json", jsonObject.toString());
                                if (jsonObject.has("msg"))
                                    dongTaiClass.mPersonInfo.is_follow = !dongTaiClass.mPersonInfo.is_follow;
                                Toast.makeText(mContext, jsonObject.optString("msg", "操作失败"), Toast.LENGTH_SHORT).show();

                                if (adapter != null) {
                                    //供RecyclerView调用，以实现联动
                                    adapter.notifyDataSetChanged();
                                } else {
                                    //供动态详情页面调用，只需更新当前的View的按钮即可
                                    updateFollowButton(holder.tv_follow, dongTaiClass.mPersonInfo.is_follow);
                                }
                            }

                            @Override
                            public void onMyError(VolleyError error) {

                            }
                        });

            }
        });

        /**
         * 转发 按钮
         */
        holder.ll_count_forward_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * 社会化分享，暂时先不用这个，先采用内部转发方式
                 */
//                final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
//                        {
//                                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
//                                SHARE_MEDIA.QQ, SHARE_MEDIA.SMS
//                        };
//                new ShareAction((Activity) mContext).setDisplayList(displaylist)
//                        .withText(dongTaiClass.mContent)
//                        .withTitle("复制链接打开[Ta技]客户端，你懂的")
//                        .withTargetUrl("http://doc.whutech.com/taji.html?tid=" + dongTaiClass.mTid)
//                        .withMedia(new UMImage(mContext, dongTaiClass.mCoverUrl))
//                        .setListenerList(new UMShareListener() {
//                            @Override
//                            public void onResult(SHARE_MEDIA share_media) {
//
//                            }
//
//                            @Override
//                            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
//
//                            }
//
//                            @Override
//                            public void onCancel(SHARE_MEDIA share_media) {
//
//                            }
//                        })
//                        .open();
//                if (true) return;


                holder.ll_count_forward_container.setEnabled(false);
                UserClass.getInstance().dongTaiDoForward(dongTaiClass.mTid, new VolleyInterface(mContext.getApplicationContext()) {
                    @Override
                    public void onMySuccess(JSONObject jsonObject) {
                        holder.ll_count_forward_container.setEnabled(true);
                        holder.tv_count_forward.setText("" + (Integer.valueOf(dongTaiClass.mCountForward) + 1));
                        Toast.makeText(mContext.getApplicationContext(), "转发成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        holder.ll_count_forward_container.setEnabled(true);
                    }
                });
            }
        });

        /**
         * 赞 按钮
         */
        holder.ll_count_like_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ll_count_like_container.setEnabled(false);
                UserClass.getInstance().dongTaiDoLike(dongTaiClass.mTid, new VolleyInterface(mContext.getApplicationContext()) {
                    @Override
                    public void onMySuccess(JSONObject jsonObject) {

                        holder.ll_count_like_container.setEnabled(true);
                        try {
                            String msg = jsonObject.getString("msg");
                            if (msg.equals("点赞成功")) {
                                if (!dongTaiClass.isLike) {
                                    dongTaiClass.mCountLike = "" + (Integer.valueOf(dongTaiClass.mCountLike) + 1);
                                }
                                dongTaiClass.isLike = true;
                            } else if (msg.equals("取消点赞成功")) {
                                if (dongTaiClass.isLike) {
                                    dongTaiClass.mCountLike = "" + (Integer.valueOf(dongTaiClass.mCountLike) - 1);
                                }
                                dongTaiClass.isLike = false;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            dongTaiClass.isLike = !dongTaiClass.isLike;
                        }

                        holder.iv_count_like_icon.setImageResource(dongTaiClass.isLike ?
                                R.drawable.icon_tab_home_hot_favor_selelct2 : R.drawable.icon_tab_home_hot_favor);
                        holder.tv_count_like.setText(dongTaiClass.mCountLike);

                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        holder.ll_count_like_container.setEnabled(true);
                    }
                });
            }
        });

        ImageLoader.getInstance().displayImage(dongTaiClass.mAvatarUrl, new ImageViewAware(holder.iv_avatar), MyApplication.getCircleDisplayImageOptions());

    }

    private static void initPartViewUniversal(final Context mContext, final MyViewHolder holder, final DongTaiBean dongTaiClass) {
        if (holder instanceof MyHolderSingle) {//动态详情页下的单图模式
            MyHolderSingle myHolderSingle = (MyHolderSingle) holder;
            ImageLoader.getInstance().displayImage(dongTaiClass.mCoverUrl, new ImageViewAware(myHolderSingle.extra_iv_cover), MyApplication.getNormalDisplayImageOptions());

        } else if (holder instanceof MyHolderVideo) {
            final MyHolderVideo myHolderVideo = (MyHolderVideo) holder;
            //展示视频封面
            ImageLoader.getInstance().displayImage(dongTaiClass.mCoverUrl, new ImageViewAware(myHolderVideo.extra_cover_iv), MyApplication.getNormalDisplayImageOptions());

        }
    }


    private static void initPartViewDetail(final Context mContext, final MyViewHolder holder, final DongTaiBean dongTaiClass) {


        if (holder instanceof MyHolderSingle) {//动态详情页下的单图模式
            MyHolderSingle myHolderSingle = (MyHolderSingle) holder;
            myHolderSingle.extra_iv_cover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PhotoViewerActivity.startPhotoView(mContext, Uri.parse(dongTaiClass.mCoverUrl), null);
                }
            });

        } else if (holder instanceof MyHolderVideo) {//动态详情页下的视频模式
            final MyHolderVideo myHolderVideo = (MyHolderVideo) holder;

            //监听点击播放视频
            myHolderVideo.extra_ll_play_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!PathUtil.hasSDCard()) {
                        Toast.makeText(mContext, "内存卡不存在", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    final String path = PathUtil.getExtPath() + "/taji_temp/" +
                            MD5Util.str2MD5(dongTaiClass.mVideoUrl);

                    if (new File(path).exists()) {
                        //Toast.makeText(mContext, "ok", Toast.LENGTH_SHORT).show();
                        playVideo(mContext, myHolderVideo, path);
                        return;
                    }

                    FinalHttp fh = new FinalHttp();
                    myHolderVideo.extra_play_loading.setVisibility(View.VISIBLE);
                    fh.download(dongTaiClass.mVideoUrl, path + "_download", new AjaxCallBack<File>() {
                        ClipDrawable clipDrawable = new ClipDrawable(myHolderVideo.extra_play_loading.getDrawable(), Gravity.LEFT, ClipDrawable.HORIZONTAL);

                        @Override
                        public void onStart() {
                            myHolderVideo.extra_play_loading.setImageDrawable(clipDrawable);
                            clipDrawable.setLevel(0);
                            super.onStart();
                        }

                        @Override
                        public void onLoading(long count, long current) {
                            clipDrawable.setLevel((int) (current * 10000 / count));
                            //myHolderVideo.extra_play_loading.invalidate();
                        }

                        @Override
                        public void onSuccess(File t) {
                            t.renameTo(new File(path));
                            clipDrawable.setLevel(10000);
                            Log.e("onSuccess", t == null ? "null" : t.getAbsoluteFile().toString());

                            playVideo(mContext, myHolderVideo, path);
                        }

                    });

                }
            });

        } else {

        }


//        holder.cmv_media.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (dongTaiClass.mVideoUrl.equals("")) {
//
//                } else {
//
//                    VideoView videoView = holder.cmv_media.video_vv_video;
//                    videoView.setVisibility(View.VISIBLE);
//                    videoView.setMediaController(new MediaController(mContext));
//                    videoView.setVideoURI(Uri.parse(dongTaiClass.mVideoUrl));
//                    videoView.start();
//                    videoView.requestFocus();
////                        Uri uri = Uri.parse(dongTaiClass.mVideoUrl);
////                        VideoPlayerActivity.startVideoPlayer(mContext, uri);
//                }
//            }
//        });
    }

    private static void playVideo(Context context, final MyHolderVideo holderVideo, String path) {

        holderVideo.extra_ll_play_container.setVisibility(View.INVISIBLE);
        VideoView videoView = holderVideo.extra_vv_video;
        videoView.setMediaController(new MediaController(context));
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                holderVideo.extra_ll_play_container.setVisibility(View.VISIBLE);
            }

        });
        videoView.setVideoURI(Uri.parse(path));
        videoView.start();
        //videoView.requestFocus();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View main = createViewByType(mContext, parent, viewType);
        return createHolderByView(main, viewType);
    }

    /**
     * 这是createView的1/2步，通过type来inflate出view
     *
     * @param mContext
     * @param parent
     * @param viewType
     * @return
     */
    public static ViewGroup createViewByType(Context mContext, ViewGroup parent, int viewType) {
        Log.e("createViewByType", "start|" + viewType);
        ViewGroup main = (ViewGroup) LayoutInflater.from(mContext).
                inflate(R.layout.view_home_hot_then_listview_item, parent, false);

        //装载多形式展示区的容器
        LinearLayout part = (LinearLayout) main.findViewById(R.id.home_hot_then_media);
        part.removeAllViews();

        switch (viewType) {
            //单图模式
            case TYPE_SINGLE: {
                part.addView(LayoutInflater.from(mContext).
                        inflate(R.layout.view_media_pic_single, part, false));
            }
            //视频模式
            case TYPE_VIDEO: {
                part.addView(LayoutInflater.from(mContext).
                        inflate(R.layout.view_media_video, part, false));
            }
        }
        return main;
    }

    /**
     * 这是createView的2/2步，通过inflate出的view得到holder
     *
     * @param main
     * @param viewType
     * @return
     */
    public static MyViewHolder createHolderByView(View main, int viewType) {
        switch (viewType) {
            //单图模式
            case TYPE_SINGLE: {
                return new MyHolderSingle(main);
            }
            //视频模式
            case TYPE_VIDEO: {
                return new MyHolderVideo(main);
            }
        }
        return null;
    }

    private static final int TYPE_SINGLE = 4;
    private static final int TYPE_MULTI = 5;
    private static final int TYPE_VIDEO = 6;

    @Override
    public int getItemViewType(int position) {
        DongTaiBean dongTaiBean = mDongTaiList.get(position);
        Log.e("getItemViewType", position + "|" + dongTaiBean.toString());
        if (!dongTaiBean.mVideoUrl.equals("")) {
            return TYPE_VIDEO;
        } else {
            return TYPE_SINGLE;
        }
    }

    public static int getViewTypeByDongTai(DongTaiBean dongTaiBean) {
        if (!dongTaiBean.mVideoUrl.equals("")) {
            return TYPE_VIDEO;
        } else {
            return TYPE_SINGLE;
        }
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final DongTaiBean dongTaiClass = mDongTaiList.get(position);

        updateViewHolder(mContext, holder, dongTaiClass, this);

        if (isToEchoTime) {
            holder.tv_follow.setVisibility(View.VISIBLE);
            holder.tv_follow.setText(dongTaiClass.mTime.substring(dongTaiClass.mTime.indexOf("-") + 1, dongTaiClass.mTime.lastIndexOf(":")));
            holder.tv_follow.setTextColor(Color.parseColor("#A861B3"));
            holder.tv_follow.setBackgroundResource(0);
        }

    }

    public void setToEchoTime(boolean isToEchoTime) {
        this.isToEchoTime = isToEchoTime;
    }

    private static void updateFollowButton(TextView tv_follow, boolean isToFollow) {
        tv_follow.setText(isToFollow ? "√ 已订阅" : "+订阅");
        tv_follow.setTextColor(isToFollow ? Color.parseColor("#9F61AA") : Color.parseColor("#16FBCC"));
        tv_follow.setBackgroundResource(isToFollow ? R.drawable.home_hot_btn_concern_bg_reverse : R.drawable.home_hot_btn_concern_bg);
    }


    @Override
    public int getItemCount() {
        return mDongTaiList.size();
    }

    /**
     * 清空所有原数据
     */
    public void clearData() {
        boolean willNotifyData = false;
        if (mDongTaiList.size() != 0) willNotifyData = true;
        mDongTaiList.clear();
        if (willNotifyData)
            notifyDataSetChanged();
    }


    public int addData(JSONArray jsonArray) {
        int count = 0;
        int insert_position = mDongTaiList.size() - 1;
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                //notifyItemChanged(lastCount+i);
                mDongTaiList.add(DongTaiBean.getInstance(jsonArray.getJSONObject(i)));
                insert_position++;
                notifyItemChanged(insert_position);
                //notifyItemChanged(lastCount+i);
                //notifyItemRangeChanged(lastCount+i, getItemCount());
                count++;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    public static class MyHolderSingle extends MyViewHolder {
        ImageView extra_iv_cover;

        public MyHolderSingle(View itemView) {
            super(itemView);
            extra_iv_cover = (ImageView) itemView.findViewById(R.id.media_single_image_view);
        }
    }

    public static class MyHolderVideo extends MyViewHolder {
        VideoView extra_vv_video;
        ImageView extra_cover_iv;
        ImageView extra_play_btn;
        ImageView extra_play_loading;
        RelativeLayout extra_ll_play_container;

        public MyHolderVideo(View layoutVideo) {
            super(layoutVideo);

            extra_vv_video = (VideoView) layoutVideo.findViewById(R.id.media_video_view);
            extra_play_btn = (ImageView) layoutVideo.findViewById(R.id.media_video_play_iv);
            extra_cover_iv = (ImageView) layoutVideo.findViewById(R.id.media_video_cover_view);
            extra_play_loading = (ImageView) layoutVideo.findViewById(R.id.media_video_loading_tv);
            extra_ll_play_container = (RelativeLayout) layoutVideo.findViewById(R.id.media_video_cover_container);

        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        ImageView iv_avatar;
        LinearLayout cmv_media;
        TextView tv_content;

        TextView tv_label_parent;
        TextView tv_label_child;

        TextView tv_delete;

        TextView tv_count_forward;
        TextView tv_count_comment;
        TextView tv_count_like;

        LinearLayout ll_count_forward_container;
        LinearLayout ll_count_comment_container;
        LinearLayout ll_count_like_container;
        ImageView iv_count_like_icon;

        TextView tv_follow;


        public MyViewHolder(View itemView) {
            super(itemView);

            tv_name = (TextView) itemView.findViewById(R.id.home_hot_then_name);
            iv_avatar = (ImageView) itemView.findViewById(R.id.home_hot_then_avatar);
            cmv_media = (LinearLayout) itemView.findViewById(R.id.home_hot_then_media);
            tv_content = (TextView) itemView.findViewById(R.id.home_hot_then_content);

            tv_label_parent = (TextView) itemView.findViewById(R.id.home_hot_then_label_parent);
            tv_label_child = (TextView) itemView.findViewById(R.id.home_hot_then_label_child);

            tv_delete = (TextView) itemView.findViewById(R.id.home_hot_then_delete);

            tv_count_forward = (TextView) itemView.findViewById(R.id.home_hot_then_count_forward);
            tv_count_comment = (TextView) itemView.findViewById(R.id.home_hot_then_count_comment);
            tv_count_like = (TextView) itemView.findViewById(R.id.home_hot_then_count_like);

            ll_count_forward_container = (LinearLayout) itemView.findViewById(R.id.home_hot_then_count_forward_container);
            ll_count_comment_container = (LinearLayout) itemView.findViewById(R.id.home_hot_then_count_comment_container);
            ll_count_like_container = (LinearLayout) itemView.findViewById(R.id.home_hot_then_count_like_container);

            iv_count_like_icon = (ImageView) itemView.findViewById(R.id.home_hot_then_like_icon);

            tv_follow = (TextView) itemView.findViewById(R.id.home_hot_then_follow);
        }
    }
}
