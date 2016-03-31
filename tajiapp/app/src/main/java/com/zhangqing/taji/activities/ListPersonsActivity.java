package com.zhangqing.taji.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.zhangqing.taji.MyApplication;
import com.zhangqing.taji.R;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.util.ImageDownloader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/2/20.
 */

public class ListPersonsActivity extends Activity {
    private int mWhichType;
    private String mTitleString;

    private TextView mTitleTextView;
    private EditText mSearchEditText;
    private ListView mListView;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private PersonsListAdapter mAdapterListView;

    private int start_index,end_index;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_persons);


        mTitleTextView = (TextView) findViewById(R.id.persons_title_tv);
        mSearchEditText = (EditText) findViewById(R.id.persons_search_edit);
        mListView = (ListView) findViewById(R.id.persons_list_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.persons_swipe_refresh);


        Bundle bundleDatas = getIntent().getExtras();
        mWhichType = bundleDatas.getInt("which");
        mTitleString = bundleDatas.getString("title");
        mTitleTextView.setText(mTitleString);
        mSearchEditText.setHint("搜索" + mTitleString);

        mListView.setDivider(getResources().getDrawable(R.drawable.persons_list_divider));
        mListView.setDividerHeight(2);
        TextView t=new TextView(this);
        t.setText("正在加载...");
        mListView.setFooterDividersEnabled(true);
        mListView.addFooterView(t);

        //滑动ListView时暂停加载图片,只加载可视区域内图片
        mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true, new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                isInit = true;
//                switch (scrollState) {
//                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// 滑动停止
//                        for (; start_index < end_index; start_index++) {
//                            ImageView img = (ImageView) mListView.findViewWithTag(start_index);
//                            img.setImageResource(R.drawable.update_log);
//                        }
//                        break;
//
//                    default:
//                        break;
//                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        }));

        mAdapterListView = new PersonsListAdapter(ListPersonsActivity.this, mListView);


        final SwipeRefreshLayout.OnRefreshListener swipeListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                UserClass.getInstance().getPersonsList(mWhichType, new VolleyInterface(ListPersonsActivity.this.getApplicationContext()) {
                    @Override
                    public void onMySuccess(JSONObject jsonObject) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mAdapterListView.initData(jsonObject);
                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        };
        mSwipeRefreshLayout.setOnRefreshListener(swipeListener);
        mSwipeRefreshLayout.setRefreshing(true);
       // mSwipeRefreshLayout.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                swipeListener.onRefresh();
//                mSwipeRefreshLayout.setRefreshing(true);
//            }
//        }, 100);
    }

    public void finishThis(View v) {
        finish();
    }


}

class PersonsListAdapter extends BaseAdapter {
    private static final int USER_ID = 1;
    private static final int USER_NAME = 2;
    private static final int AVATAR = 3;
    private static final int SIGN = 4;


    List<Map<Integer, String>> mapList = new ArrayList<Map<Integer, String>>();

    private Context mContext;

    private ListView mListView;

    /**
     * @param context  必须Activity context
     * @param listView 会自动setAdapter
     */
    public PersonsListAdapter(Context context, ListView listView) {
        this.mContext = context;
        mListView = listView;
        if (listView.getAdapter() == null)
            listView.setAdapter(this);


        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions

    }


    public void initData(JSONObject jsonObject) {
        JSONArray jsonArray;

        mapList.clear();

        try {
            jsonArray = jsonObject.getJSONArray("data");
        } catch (JSONException e) {
            Log.e("ListViewinitDataFail", "|");
            return;
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            Map<Integer, String> map = new HashMap<Integer, String>();
            JSONObject tempJsonObject;
            try {
                tempJsonObject = jsonArray.getJSONObject(i);
                if (tempJsonObject.has("userid"))
                    map.put(USER_ID, tempJsonObject.getString("userid"));
                if (tempJsonObject.has("username"))
                    map.put(USER_NAME, tempJsonObject.getString("username"));
                if (tempJsonObject.has("avatar"))
                    map.put(AVATAR, tempJsonObject.getString("avatar"));
                if (tempJsonObject.has("signature"))
                    map.put(SIGN, tempJsonObject.getString("signature"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mapList.add(map);
        }
        //notifyDataSetChanged();
        //局部更新,更新可视区域,
        notifyDataSetInvalidated();
        // 整体更新,更新所有item对象,如果滑动过,更新后回到初始状态
    }

    @Override
    public int getCount() {
        return mapList.size();
    }

    @Override
    public Object getItem(int position) {
        return mapList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Integer.parseInt(mapList.get(position).get(USER_ID));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.e("getView", position + "|" + mapList.size() + "|" + mapList.get(position).toString());
        ViewHolder viewHolder;
        ImageDownloader a;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.view_persons_listview_item, null, false);
            viewHolder = new ViewHolder();
            viewHolder.imgViewIcon = (ImageView) convertView.findViewById(R.id.persons_item_avatar);
            viewHolder.textViewName = (TextView) convertView.findViewById(R.id.persons_item_name);
            viewHolder.textViewSign = (TextView) convertView.findViewById(R.id.persons_item_sign);
            convertView.setTag(viewHolder);
            Log.e("getView", "viewHolder");
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String name = mapList.get(position).get(USER_NAME);
        if (name.equals("null") || name.equals(""))
            name = "游客" + mapList.get(position).get(USER_ID);
        viewHolder.textViewName.setText(name);
        viewHolder.textViewSign.setText(mapList.get(position).get(SIGN));

        String url = mapList.get(position).get(AVATAR);

        viewHolder.imgViewIcon.setTag(url);
        //这句代码的作用是为了解决convertView被重用的时候，图片预设的问题
        viewHolder.imgViewIcon.setImageResource(R.drawable.ic_launcher);

        ImageLoader.getInstance().displayImage(url, new ImageViewAware(viewHolder.imgViewIcon), MyApplication.getDisplayImageOptions(),
                new ImageSize(100, 100), null, null);


        // viewHolder.imgViewIcon.post(new myRunable(url, viewHolder.imgViewIcon));

//        if (mDownloader == null) {
//            mDownloader = new ImageDownloader();
//        }
//        if (mDownloader != null) {
//            //异步下载图片
//            mDownloader.imageDownload(url, viewHolder.imgViewIcon, "/taji_temp", (Activity) mContext, new OnImageDownload() {
//                @Override
//                public void onDownloadSucc(Bitmap bitmap,
//                                           String c_url, ImageView mimageView) {
//                    ImageView imageView = (ImageView) mListView.findViewWithTag(c_url);
//                    if (imageView != null) {
//                        imageView.setImageBitmap(bitmap);
//                        imageView.setTag("");
//                    }
//                }
//            });
//        }


        return convertView;
    }

    static class ViewHolder {
        ImageView imgViewIcon;
        TextView textViewName;
        TextView textViewSign;
    }
}