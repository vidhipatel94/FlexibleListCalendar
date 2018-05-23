package com.cittakalpa.flexiblelistcalendar.library;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.GridView;

import com.cittakalpa.flexiblelistcalendar.library.impl.IDateCellViewDrawer;

import java.util.Calendar;

/**
 * Created by Vidhi on 22/05/2018.
 */

public class MonthView extends GridView {
    private static final int TOTAL_WEEK_DAYS = 7;

    public MonthView(Context context) {
        super(context);
    }

    public MonthView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MonthView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MonthView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void init(int year, int month, boolean showDatesOutsideMonth, boolean decorateDatesOutsideMonth,
                     int startDayOfTheWeek, Calendar maxDateCalendar,
                     FlexibleCalendarGridAdapter.OnDateCellItemClickListener onDateCellItemClickListener,
                     FlexibleCalendarGridAdapter.MonthEventFetcher monthEventFetcher, IDateCellViewDrawer cellViewDrawer) {

        FlexibleCalendarGridAdapter adapter = new FlexibleCalendarGridAdapter(getContext(), year, month,
                showDatesOutsideMonth, decorateDatesOutsideMonth, startDayOfTheWeek, maxDateCalendar);
        adapter.setOnDateClickListener(onDateCellItemClickListener);
        adapter.setMonthEventFetcher(monthEventFetcher);
        adapter.setCellViewDrawer(cellViewDrawer);

        setNumColumns(TOTAL_WEEK_DAYS);

        setAdapter(adapter);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSpec;

        if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {

            // The two leftmost bits in the height measure spec have
            // a special meaning, hence we can't use them to describe height.
            heightSpec = MeasureSpec.makeMeasureSpec(
                    Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        } else {
            // Any other height should be respected as is.
            heightSpec = heightMeasureSpec;
        }

        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}
