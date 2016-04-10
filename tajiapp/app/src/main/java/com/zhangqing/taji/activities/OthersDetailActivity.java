package com.zhangqing.taji.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zhangqing.taji.R;
import com.zhangqing.taji.adapter.DongTaiAdapter;
import com.zhangqing.taji.adapter.LoadMoreRecyclerView;
import com.zhangqing.taji.base.PersonInfo;
import com.zhangqing.taji.base.UserClass;
import com.zhangqing.taji.base.VolleyInterface;
import com.zhangqing.taji.view.PersonInfoView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/3/19.
 */
public class OthersDetailActivity extends AppCompatActivity {

    public static void start(Context context, String id, String name) {
        Intent intent = new Intent(context, OthersDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("name", name);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    private String mId = "";
    private String mName = "";

    private PersonInfo mPersonInfo;
    private PersonInfoView mPersonInfoView;

    private LoadMoreRecyclerView mRecyclerView;
    private DongTaiAdapter mRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);

        mRecyclerView= (LoadMoreRecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mRecyclerViewAdapter=new DongTaiAdapter(this));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        Bundle bundle = getIntent().getExtras();
        mId = bundle.getString("id");
        mName = bundle.getString("name");

        ((TextView) findViewById(R.id.title)).setText(mName);

        UserClass.getInstance().getOthersInfo(mId, new VolleyInterface(this) {
            @Override
            public void onMySuccess(JSONObject jsonObject) {

                try {
                    mPersonInfo=new PersonInfo(mId,jsonObject);
                    mPersonInfoView=new PersonInfoView(OthersDetailActivity.this,mPersonInfo);

                    mRecyclerView.setHeaderView(mPersonInfoView);
                    //mRecyclerViewAdapter.notifyItemRangeChanged(-1,3);


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSONException","getOthersInfo");
                }

            }

            @Override
            public void onMyError(VolleyError error) {

            }
        });
//        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        actionBar.setDisplayShowCustomEnabled(true);
//        RelativeLayout relativeLayout=new RelativeLayout(this);

    }

    public void onClickBtnFinish(View v) {
        finish();
    }
}
