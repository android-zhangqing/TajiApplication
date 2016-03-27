package com.zhangqing.taji.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.zhangqing.taji.R;

/**
 * Created by Administrator on 2016/3/19.
 */
public class PersonDetailActivity extends AppCompatActivity {

    public static void start(Context context, String id, String name, String sign) {
        Intent intent = new Intent(context, PersonDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("name", name);
        bundle.putString("sign", sign);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    private String mId = "";
    private String mName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);
        setTitle("个人信息");
        ActionBar actionBar = getSupportActionBar();

        Bundle bundle = getIntent().getExtras();
        mId = bundle.getString("id");
        mName = bundle.getString("name");

        ((TextView) findViewById(R.id.my_head_name)).setText(mName);
        ((TextView) findViewById(R.id.my_head_sign)).setText(bundle.getString("sign"));

//        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        actionBar.setDisplayShowCustomEnabled(true);
//        RelativeLayout relativeLayout=new RelativeLayout(this);

    }
}
