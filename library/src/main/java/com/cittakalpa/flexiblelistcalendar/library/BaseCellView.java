package com.cittakalpa.flexiblelistcalendar.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Vidhi on 22/05/2018.
 */

public class BaseCellView extends AppCompatTextView {

    public static final int REGULAR = 0;
    public static final int TODAY = 1;
    public static final int OUTSIDE_MONTH = 2;
    public static final int OUTSIDE_MAX_DATE = 3;

    public static final int STATE_REGULAR = R.attr.state_date_regular;
    public static final int STATE_TODAY = R.attr.state_date_today;
    public static final int STATE_OUTSIDE_MONTH = R.attr.state_date_outside_month;
    public static final int STATE_OUTSIDE_MAX_DATE = R.attr.state_date_outside_max_date;

    private Set<Integer> stateSet;

    private int eventCircleY;
    private int radius = 10;
    private int padding = 1;
    private int leftMostPosition = Integer.MIN_VALUE;
    private List<Paint> paintList;

    public BaseCellView(Context context) {
        super(context);
        init();
    }

    public BaseCellView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseCellView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setGravity(Gravity.CENTER);

        stateSet = new HashSet<>(3);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        Set<Integer> stateSet = getStateSet();

        //initialize paint objects only if there is no state or just one state i.e. the regular day state
        if ((stateSet == null || stateSet.isEmpty()
                || (stateSet.size() == 1 && stateSet.contains(STATE_REGULAR))) && paintList != null) {
            int num = paintList.size();

            Paint p = new Paint();
            p.setTextSize(getTextSize());

            Rect rect = new Rect();
            p.getTextBounds("31", 0, 1, rect); // measuring using fake text

            eventCircleY = (3 * getHeight() + rect.height()) / 4;

            //calculate left most position for the circle
            if (leftMostPosition == Integer.MIN_VALUE) {
                leftMostPosition = (getWidth() / 2) - (num / 2) * 2 * (padding + radius);
                if (num % 2 == 0) {
                    leftMostPosition = leftMostPosition + radius + padding;
                }
            }

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Set<Integer> stateSet = getStateSet();

        // draw only if there is no state or just one state i.e. the regular day state
        if ((stateSet == null || stateSet.isEmpty() || (stateSet.size() == 1
                && stateSet.contains(STATE_REGULAR))) && paintList != null) {
            int num = paintList.size();
            for (int i = 0; i < num; i++) {
                canvas.drawCircle(calculateStartPoint(i), eventCircleY, radius, paintList.get(i));
            }
        }
    }

    private int calculateStartPoint(int offset) {
        return leftMostPosition + offset * (2 * (radius + padding));
    }

    public void addState(int state) {
        stateSet.add(state);
    }

    public void clearAllStates() {
        stateSet.clear();
    }

    public Set<Integer> getStateSet() {
        return this.stateSet;
    }

    public void setEvents(List<? extends Event> colorList) {
        if (colorList != null) {
            paintList = new ArrayList<>(colorList.size());
            for (Event e : colorList) {
                Paint eventPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                eventPaint.setStyle(Paint.Style.FILL);
                eventPaint.setColor(getContext().getResources().getColor(e.getColor()));
                paintList.add(eventPaint);
            }
            invalidate();
            requestLayout();
        }
    }

    @IntDef({TODAY, REGULAR, OUTSIDE_MONTH, OUTSIDE_MAX_DATE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CellType {
    }
}
