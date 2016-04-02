package com.zhangqing.taji.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.zhangqing.taji.R;

/**
 * 用于实现九宫格效果的GridView
 */
public class AdvancedGridView extends TableLayout {

    private int rowNum = 0; // row number
    private int colNum = 0; // col number

    private BaseAdapter adapter = null;

    private Context context = null;

    public AdvancedGridView(Context context) {
        super(context);
        initThis(context, null);
    }

    public AdvancedGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initThis(context, attrs);
    }

    private void initThis(Context context, AttributeSet attrs) {
        this.context = context;

        // 取xml属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.gridview);
        rowNum = typedArray.getInt(R.styleable.gridview_count_rows, 3);
        colNum = typedArray.getInt(R.styleable.gridview_count_columns, 3);

        if (this.getTag() != null) {
            String atb = (String) this.getTag();
            int ix = atb.indexOf(',');
            if (ix > 0) {
                rowNum = Integer.parseInt(atb.substring(0, ix));
                colNum = Integer.parseInt(atb.substring(ix + 1, atb.length()));
            }
        }
        if (rowNum <= 0)
            rowNum = 3;
        if (colNum <= 0)
            colNum = 3;

        if (this.isInEditMode()) {
            this.removeAllViews();
            for (int y = 0; y < rowNum; ++y) {
                TableRow row = new TableRow(context);
                row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));
                for (int x = 0; x < colNum; ++x) {
                    View button = new Button(context);
                    row.addView(button, new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));
                }
                this.addView(row);
            }
        }
    }

    public BaseAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(BaseAdapter adapter) {
        if (adapter != null) {
            if (adapter.getCount() < this.rowNum * this.colNum) {
                throw new IllegalArgumentException("The view count of adapter is less than this gridview's items");
            }
            this.removeAllViews();
            for (int y = 0; y < rowNum; ++y) {
                TableRow row = new TableRow(context);
                row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));
                for (int x = 0; x < colNum; ++x) {
                    View view = adapter.getView(y * colNum + x, this, row);
                    row.addView(view, new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));
                }
                this.addView(row);
            }
        }
        this.adapter = adapter;
    }

    public int getRowNum() {
        return rowNum;
    }

    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }

    public int getColNum() {
        return colNum;
    }

    public void setColNum(int colNum) {
        this.colNum = colNum;
    }
}
