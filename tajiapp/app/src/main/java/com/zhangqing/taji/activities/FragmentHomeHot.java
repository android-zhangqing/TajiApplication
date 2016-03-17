package com.zhangqing.taji.activities;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhangqing.taji.R;
import com.zhangqing.taji.view.FragmentHomeHotViewFirst;
import com.zhangqing.taji.view.FragmentHomeHotViewThen;

import java.util.ArrayList;
import java.util.List;

public class FragmentHomeHot extends Fragment implements OnClickListener {
    ViewPager viewPager;
    HorizontalScrollView horizontalScrollView;

    List<View> viewList;

    int currenCategory = 0;
    List<Category> categoryList;
    List<TextView> textViewList;
    private SwipeRefreshLayout swipeRefreshLayout;


    public FragmentHomeHot(){
        Log.e("FragmentHomeHot","###构造 ");
        categoryList = getCategoryList();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("FragmentHomeHot","onCreateView ");
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.home_viewpager);
        horizontalScrollView = (HorizontalScrollView) v
                .findViewById(R.id.home_horizon_scrollview_category);

        LinearLayout categoryLayout = (LinearLayout) v
                .findViewById(R.id.home_category_title_linearlayout);


        textViewList = new ArrayList<TextView>();
        // init linearLayout
        for (int i = 0; i < categoryList.size(); i++) {
            TextView tempTextView = new TextView(getActivity());
            tempTextView.setTag(categoryList.get(i));
            tempTextView.setText(categoryList.get(i).getCategoryName());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.FILL_PARENT);
            // params.setMargins(20, 0, 20, 0);
            if (i == currenCategory) {
                tempTextView
                        .setBackgroundResource(R.drawable.home_hot_category_bar_indicator);
                tempTextView.setTextColor(getActivity().getResources()
                        .getColor(R.color.textcolor_bar_second_select));

            } else {
                tempTextView.setBackgroundResource(0);
                tempTextView.setTextColor(getActivity().getResources()
                        .getColor(R.color.textcolor_bar_second_unselect));
            }
            tempTextView.setTextSize(16);

            tempTextView.setGravity(Gravity.CENTER);

            tempTextView.setOnClickListener(this);
            textViewList.add(tempTextView);
            categoryLayout.addView(tempTextView, params);

        }

        // LayoutInflater flater = (LayoutInflater) getActivity()
        // .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewList = new ArrayList<View>();

        for (int i = 0; i < categoryList.size(); i++) {

            View view1;
            //
            if (i == 0) {
                view1 = new FragmentHomeHotViewFirst(getActivity());
            } else {
                view1 = new FragmentHomeHotViewThen(getActivity());
            }

            //Log.e("aaaaaaaaaaaa", "2");
            view1.setTag(categoryList.get(i));
            viewList.add(view1);

        }

        PagerAdapter adpater = new MyPagerAdapter(viewList);
        //Log.e("aaaaaaaaaaaa", adpater.toString());
        viewPager.setAdapter(adpater);

        viewPager.addOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                Log.e("HomeHot","onPageSelected "+arg0);
                currenCategory = arg0;
//                Toast.makeText(getActivity(),
//                        arg0 + "|||" + getActivity().toString(), Toast.LENGTH_SHORT).show();
                updateCategoryView();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        updateCategoryView();
        return v;
    }

    private List<Category> getCategoryList() {
        String[] categoryArray = new String[]{"唱歌", "体育", "游戏", "美妆", "搭配", "美甲", "绘画", "艺术", "音乐", "健身", "搞笑", "舞蹈"};
        List<Category> categoryList = new ArrayList<Category>();
        categoryList.add(new Category(0, "0", "广场"));
        for (int i = 0; i < categoryArray.length; i++) {
            categoryList.add(new Category(i + 1, i + 1 + "", categoryArray[i]));
        }
        return categoryList;

    }

    private void updateCategoryView() {
        Log.e("HomeHot","updateCategoryView size="+textViewList.size()+" currenCategory="+currenCategory);
        for (int i = 0; i < textViewList.size(); i++) {

            if (i == currenCategory) {
                textViewList.get(i).setBackgroundResource(
                        R.drawable.home_hot_category_bar_indicator);
                textViewList.get(i).setTextColor(
                        getActivity().getResources().getColor(
                                R.color.textcolor_bar_second_select));

            } else {
                textViewList.get(i).setBackgroundResource(0);
                textViewList.get(i).setTextColor(
                        getActivity().getResources().getColor(
                                R.color.textcolor_bar_second_unselect));
            }
            textViewList.get(i).setPadding(40, 15, 40, 15);
        }

        TextView currentTextView = textViewList.get(currenCategory);
        int textViewLeft = currentTextView.getLeft();
        int textViewWidth = currentTextView.getWidth();
        if (textViewLeft - horizontalScrollView.getScrollX() < 0) {
            horizontalScrollView.smoothScrollTo(textViewLeft, 0);
        } else if (textViewLeft + textViewWidth > horizontalScrollView
                .getScrollX() + horizontalScrollView.getMeasuredWidth()) {
            horizontalScrollView.smoothScrollTo(textViewLeft + textViewWidth
                    - horizontalScrollView.getMeasuredWidth(), 0);
        }
        if (currenCategory == 0) {
            ((FragmentHomeHotViewFirst) (viewList.get(currenCategory)))
                    .perfromOnPageSelected();
        }// Toast.makeText(getActivity(), textViewLeft +
        // "q"+scrollView.getScrollX()+"a"+scrollView.getWidth()+"b"+textViewWidth,
        // 1000).show();

    }

    @Override
    public void onClick(View v) {
        if (v.getTag().equals(categoryList.get(currenCategory)))
            return;
        currenCategory = ((Category) v.getTag()).getCategoryNum();
        viewPager.setCurrentItem(currenCategory, true);

        // updateCategoryView();

    }

}

class MyPagerAdapter extends PagerAdapter {
    private List<View> views;

    public MyPagerAdapter(List<View> views) {
        super();
        this.views = views;

    }

    @Override
    public int getCount() {
        //Log.e("aaaaaaaaaaaaaaaaaa", "1" + views.size());
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(views.get(position));
        return views.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }

}

class AdsType {
    private int adsNum;
    private int adsId;
    private String adsUrl;
    private String adsClickUrl;

    public int getAdsNum() {
        return adsNum;
    }

    public void setAdsNum(int adsNum) {
        this.adsNum = adsNum;
    }

    public int getAdsId() {
        return adsId;
    }

    public void setAdsId(int adsId) {
        this.adsId = adsId;
    }

    public String getAdsUrl() {
        return adsUrl;
    }

    public void setAdsUrl(String adsUrl) {
        this.adsUrl = adsUrl;
    }

    public String getAdsClickUrl() {
        return adsClickUrl;
    }

    public void setAdsClickUrl(String adsClickUrl) {
        this.adsClickUrl = adsClickUrl;
    }

    public AdsType(int adsNum, String adsUrl) {
        this(adsNum, 0, adsUrl, "");
    }

    public AdsType(int adsNum, int adsId, String adsUrl, String adsClickUrl) {
        super();
        this.adsNum = adsNum;
        this.adsId = adsId;
        this.adsUrl = adsUrl;
        this.adsClickUrl = adsClickUrl;
    }

}

class Category {
    private int categoryNum;
    private String categoryId;
    private String categoryName;

    public int getCategoryNum() {
        return categoryNum;
    }

    public void setCategoryNum(int categoryNum) {
        this.categoryNum = categoryNum;
    }

    public Category(int categoryNum, String categoryId, String categoryName) {
        super();
        this.categoryNum = categoryNum;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

}