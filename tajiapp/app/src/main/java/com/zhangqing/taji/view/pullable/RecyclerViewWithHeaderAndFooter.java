package com.zhangqing.taji.view.pullable;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import jp.wasabeef.recyclerview.adapters.SlideInLeftAnimationAdapter;

/**
 * Created by zhangqing on 2016/4/15.
 * 带HeaderView和FooterView的RecyclerView，若需要分页加载，请使用RecyclerViewPullable
 */
public class RecyclerViewWithHeaderAndFooter extends RecyclerView {
    /**
     * item 类型
     */
    public final static int TYPE_NORMAL = 0;
    public final static int TYPE_HEADER = 1;//头部--支持头部增加一个headerView
    public final static int TYPE_FOOTER = 2;//底部--往往是loading_more
    public final static int TYPE_LIST = 3;//代表item展示的模式是list模式
    public final static int TYPE_STAGGER = 4;//代码item展示模式是网格模式

    /**
     * 自定义实现了头部和底部加载更多的adapter
     */
    private AutoLoadAdapter mAutoLoadAdapter;


    private View mHeaderView;
    private View mFooterView;

    public RecyclerViewWithHeaderAndFooter(Context context) {
        super(context);
        init();
    }

    public RecyclerViewWithHeaderAndFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecyclerViewWithHeaderAndFooter(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {

    }

    /**
     *
     */
    class AutoLoadAdapter extends Adapter<ViewHolder> {

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        int type = getItemViewType(position);
                        if (type == TYPE_HEADER || type == TYPE_FOOTER) {
                            return gridManager.getSpanCount();
                        } else {
                            return 1;
                        }
                    }
                });
            }
        }

        /**
         * 数据adapter
         */
        private Adapter mInternalAdapter;


        public AutoLoadAdapter(Adapter adapter) {
            mInternalAdapter = adapter;
        }

        @Override
        public int getItemViewType(int position) {
            int headerPosition = 0;
            int footerPosition = getItemCount() - 1;

            if (headerPosition == position && mHeaderView != null) {
                return TYPE_HEADER;
            }
            if (footerPosition == position && mFooterView != null) {
                return TYPE_FOOTER;
            }
            /**
             * 这么做保证layoutManager切换之后能及时的刷新上对的布局
             */
            if (getLayoutManager() instanceof LinearLayoutManager) {
                return TYPE_LIST;
            } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
                return TYPE_STAGGER;
            } else {
                return TYPE_NORMAL;
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_HEADER) {
                Log.e("onCreateViewHolder", "mHeaderView");
                return new HeaderViewHolder(mHeaderView);
            }
            if (viewType == TYPE_FOOTER) {
//                TextView tv=new TextView(parent.getContext());
//                tv.setText("正在加载");
//                return new FooterViewHolder(tv);
                return new FooterViewHolder(mFooterView);
            } else { // type normal
                return mInternalAdapter.onCreateViewHolder(parent, viewType);
            }
        }

        public class FooterViewHolder extends ViewHolder {
            public FooterViewHolder(View itemView) {
                super(itemView);
            }
        }

        public class HeaderViewHolder extends ViewHolder {
            public HeaderViewHolder(View itemView) {
                super(itemView);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            int type = getItemViewType(position);
            if (type != TYPE_FOOTER && type != TYPE_HEADER) {
                mInternalAdapter.onBindViewHolder(holder, mHeaderView == null ? position : position - 1);
            }
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            mInternalAdapter.onViewRecycled(holder);
            super.onViewRecycled(holder);
        }

        @Override
        public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
            super.registerAdapterDataObserver(observer);
            mInternalAdapter.registerAdapterDataObserver(observer);
        }

        @Override
        public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
            super.unregisterAdapterDataObserver(observer);
            mInternalAdapter.unregisterAdapterDataObserver(observer);
        }


        /**
         * 需要计算上加载更多和添加的头部俩个
         *
         * @return
         */
        @Override
        public int getItemCount() {
            int count = mInternalAdapter.getItemCount();
            if (mHeaderView != null) count++;
            if (mFooterView != null) count++;

            return count;
        }


        public void setHeaderView(View headerView) {
            Log.e("setHeaderView", headerView == null ? "null" : "notnull");
            mHeaderView = headerView;
            notifyItemInserted(0);
        }

        public View getHeaderView() {
            return mHeaderView;
        }

        public void setFooterView(View footerView) {
            mFooterView = footerView;
            //notifyDataSetChanged();
            notifyItemChanged(getItemCount() - 1);
        }

        public View getFooterView() {
            return mFooterView;
        }


    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter != null) {
            mAutoLoadAdapter = new AutoLoadAdapter(adapter);
        }
        super.swapAdapter(mAutoLoadAdapter, true);
    }

    /**
     * 切换layoutManager
     * <p/>
     * 为了保证切换之后页面上还是停留在当前展示的位置，记录下切换之前的第一条展示位置，切换完成之后滚动到该位置
     * 另外切换之后必须要重新刷新下当前已经缓存的itemView，否则会出现布局错乱（俩种模式下的item布局不同），
     * RecyclerView提供了swapAdapter来进行切换adapter并清理老的itemView cache
     *
     * @param layoutManager
     */
    public void switchLayoutManager(LayoutManager layoutManager) {
        int firstVisiblePosition = getFirstVisiblePosition();
//        getLayoutManager().removeAllViews();
        setLayoutManager(layoutManager);
        //super.swapAdapter(mAutoLoadAdapter, true);
        getLayoutManager().scrollToPosition(firstVisiblePosition);
    }

    /**
     * 获取第一条展示的位置
     *
     * @return
     */
    private int getFirstVisiblePosition() {
        int position;
        if (getLayoutManager() instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) getLayoutManager();
            int[] lastPositions = layoutManager.findFirstVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = getMinPositions(lastPositions);
        } else {
            position = 0;
        }
        return position;
    }

    /**
     * 获得当前展示最小的position
     *
     * @param positions
     * @return
     */
    private int getMinPositions(int[] positions) {
        int size = positions.length;
        int minPosition = Integer.MAX_VALUE;
        for (int i = 0; i < size; i++) {
            minPosition = Math.min(minPosition, positions[i]);
        }
        return minPosition;
    }

    /**
     * 获取最后一条展示的位置
     *
     * @return
     */
    public int getLastVisiblePosition() {
        int position;
        if (getLayoutManager() instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) getLayoutManager();
            int[] lastPositions = layoutManager.findLastVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = getMaxPosition(lastPositions);
        } else {
            position = getLayoutManager().getItemCount() - 1;
        }
        return position;
    }

    /**
     * 获得最大的位置
     *
     * @param positions
     * @return
     */
    private int getMaxPosition(int[] positions) {
        int size = positions.length;
        int maxPosition = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            maxPosition = Math.max(maxPosition, positions[i]);
        }
        return maxPosition;
    }

    /**
     * 添加头部view
     *
     * @param view
     */
    public void setHeaderView(View view) {
        mAutoLoadAdapter.setHeaderView(view);
    }


    /**
     * 添加尾部view
     *
     * @param view
     */
    public void setFooterView(View view) {
        mAutoLoadAdapter.setFooterView(view);
    }

    public View getHeaderView() {
        return mAutoLoadAdapter.getHeaderView();
    }

//    /**
//     * 通知更多的数据已经加载
//     * <p/>
//     * 每次加载完成之后添加了Data数据，用notifyItemRemoved来刷新列表展示，
//     * 而不是用notifyDataSetChanged来刷新列表
//     *
//     * @param hasMore
//     */
//    public void notifyMoreFinish(boolean hasMore) {
//        setAutoLoadMoreEnable(hasMore);
//        getAdapter().notifyItemRemoved(mLoadMorePosition);
//        mIsLoadingMore = false;
//    }

    public void notifyDateSetChange() {
        getAdapter().notifyDataSetChanged();
    }
}