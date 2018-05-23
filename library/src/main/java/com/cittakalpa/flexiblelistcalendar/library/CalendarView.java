package com.cittakalpa.flexiblelistcalendar.library;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.cittakalpa.flexiblelistcalendar.library.databinding.LayoutMonthYearTextBinding;
import com.cittakalpa.flexiblelistcalendar.library.impl.DateCellViewImpl;
import com.cittakalpa.flexiblelistcalendar.library.impl.IDateCellViewDrawer;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Vidhi on 22/05/2018.
 */

public class CalendarView extends RecyclerView implements FlexibleCalendarGridAdapter.MonthEventFetcher,
        FlexibleCalendarGridAdapter.OnDateCellItemClickListener {
    private OnDateClickListener onDateClickListener;
    private EventDataProvider eventDataProvider;
    private IDateCellViewDrawer cellViewDrawer;
    private MonthsAdapter mAdapter;
    private boolean showDatesOutsideMonth;
    private boolean decorateDatesOutsideMonth;
    private int startDayOfTheWeek;
    private Calendar minMonthCalendar;
    private Calendar maxMonthCalendar;
    private Calendar currentDateCalendar;
    private CustomCalendarView calendarView;

    public CalendarView(Context context) {
        super(context);
        init();
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        startDayOfTheWeek = Calendar.SUNDAY;
        minMonthCalendar = FlexibleCalendarHelper.getLocalizedCalendar(getContext());
        minMonthCalendar.set(Calendar.YEAR, minMonthCalendar.get(Calendar.YEAR) - 5);
        maxMonthCalendar = FlexibleCalendarHelper.getLocalizedCalendar(getContext());
        maxMonthCalendar.set(Calendar.YEAR, minMonthCalendar.get(Calendar.YEAR) + 10);
        currentDateCalendar = FlexibleCalendarHelper.getLocalizedCalendar(getContext());

        mAdapter = new MonthsAdapter();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        setLayoutManager(mLayoutManager);
        setItemAnimator(new DefaultItemAnimator());
        setAdapter(mAdapter);

        mAdapter.setMonthEventFetcher(this);
        mAdapter.setOnDateCellItemClickListener(this);
        mAdapter.setMaxDateCalendar(FlexibleCalendarHelper.getLocalizedCalendar(getContext()));

        if (calendarView == null) {
            calendarView = new DefaultCalendarView();
        }
        setCellViewDrawer(new DateCellViewImpl(calendarView));

        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);

                View v = mLayoutManager.getChildAt(0);
                int top = v == null ? 0 : (v.getTop() - mLayoutManager.getPaddingTop());
                mLayoutManager.scrollToPositionWithOffset(mAdapter.getCurrentMonthPosition(), top);
            }
        });
    }

    public void setShowDatesOutsideMonth(boolean showDatesOutsideMonth) {
        this.showDatesOutsideMonth = showDatesOutsideMonth;
    }

    public void setDecorateDatesOutsideMonth(boolean decorateDatesOutsideMonth) {
        this.decorateDatesOutsideMonth = decorateDatesOutsideMonth;
    }

    public void setStartDayOfTheWeek(int startDayOfTheWeek) {
        this.startDayOfTheWeek = startDayOfTheWeek;
    }

    public void setMaxDateCalendar(Calendar maxDateCalendar) {
        if (mAdapter != null) {
            mAdapter.setMaxDateCalendar(maxDateCalendar);
        }
    }

    public void setMinMonthCalendar(Calendar minMonthCalendar) {
        this.minMonthCalendar = minMonthCalendar;
    }

    public void setMaxMonthCalendar(Calendar maxMonthCalendar) {
        this.maxMonthCalendar = maxMonthCalendar;
    }

    public void setOnDateClickListener(OnDateClickListener onDateClickListener) {
        this.onDateClickListener = onDateClickListener;
    }

    public void setEventDataProvider(EventDataProvider eventDataProvider) {
        this.eventDataProvider = eventDataProvider;
    }

    private void setCellViewDrawer(IDateCellViewDrawer cellViewDrawer) {
        this.cellViewDrawer = cellViewDrawer;
    }

    public void setCalendarView(CustomCalendarView calendarView) {
        this.calendarView = calendarView;
        setCellViewDrawer(new DateCellViewImpl(calendarView));
    }

    @Override
    public List<? extends Event> getEventsForTheDay(int year, int month, int day) {
        return this.eventDataProvider == null ? null : this.eventDataProvider.getEventsForTheDay(year, month, day);
    }

    @Override
    public void onDateClick(SelectedDateItem selectedItem) {
        if (onDateClickListener != null) {
            onDateClickListener.onDateClick(selectedItem.getYear(), selectedItem.getMonth(), selectedItem.getDay());
        }
    }

    public void refresh() {
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Default calendar view for internal usage
     */
    private class DefaultCalendarView implements CustomCalendarView {

        @Override
        public BaseCellView getCellView(int position, View convertView, ViewGroup parent,
                                        int cellType) {
            BaseCellView cellView = (BaseCellView) convertView;
            if (cellView == null) {
                cellView = new BaseCellView(getContext());
            }
            return cellView;
        }

        @Override
        public BaseCellView getWeekdayCellView(int position, View convertView, ViewGroup parent) {
            BaseCellView cellView = (BaseCellView) convertView;
            if (cellView == null) {
                cellView = new BaseCellView(getContext());
            }
            return cellView;
        }

        @Override
        public String getDayOfWeekDisplayValue(int dayOfWeek, String defaultValue) {
            return null;
        }
    }

    private class MonthTitleViewHolder extends RecyclerView.ViewHolder {
        public LayoutMonthYearTextBinding binding;

        public MonthTitleViewHolder(LayoutMonthYearTextBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private class MonthViewHolder extends RecyclerView.ViewHolder {
        public MonthView monthView;

        public MonthViewHolder(MonthView view) {
            super(view);
            monthView = view;
        }
    }

    private class MonthsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int VIEW_TYPE_MONTH_HEADER = 1;
        private static final int VIEW_TYPE_MONTH_CALENDAR = 2;
        private FlexibleCalendarGridAdapter.MonthEventFetcher monthEventFetcher;
        private FlexibleCalendarGridAdapter.OnDateCellItemClickListener onDateCellItemClickListener;
        private Calendar maxDateCalendar;

        public void setMonthEventFetcher(FlexibleCalendarGridAdapter.MonthEventFetcher monthEventFetcher) {
            this.monthEventFetcher = monthEventFetcher;
        }

        public void setOnDateCellItemClickListener(FlexibleCalendarGridAdapter.OnDateCellItemClickListener onDateCellItemClickListener) {
            this.onDateCellItemClickListener = onDateCellItemClickListener;
        }

        public void setMaxDateCalendar(Calendar maxDateCalendar) {
            this.maxDateCalendar = maxDateCalendar;
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            if (position % 2 == 0) {
                return VIEW_TYPE_MONTH_HEADER;
            } else {
                return VIEW_TYPE_MONTH_CALENDAR;
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_MONTH_HEADER) {
                return new MonthTitleViewHolder(LayoutMonthYearTextBinding.inflate(LayoutInflater.from(getContext())));
            } else {
                return new MonthViewHolder(new MonthView(getContext()));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Calendar calendar = getMonthCalendar(position);

            int viewType = getItemViewType(position);
            if (viewType == VIEW_TYPE_MONTH_HEADER) {
                MonthTitleViewHolder monthTitleViewHolder = (MonthTitleViewHolder) holder;
                monthTitleViewHolder.binding.monthYearText.setText(FlexibleCalendarHelper.monthYearFormat(calendar.getTimeInMillis()));
            } else {
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                MonthViewHolder monthViewHolder = (MonthViewHolder) holder;
                monthViewHolder.monthView.init(year, month, showDatesOutsideMonth,
                        decorateDatesOutsideMonth, startDayOfTheWeek, maxDateCalendar,
                        onDateCellItemClickListener, monthEventFetcher, cellViewDrawer);
            }
        }

        private Calendar getMonthCalendar(int position) {
            int monthPos = position / 2;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(minMonthCalendar.getTimeInMillis());
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + monthPos);
            return calendar;
        }

        @Override
        public int getItemCount() {
            return getTotalMonths() * 2;
        }

        private int getTotalMonths() {
            int diffYear = maxMonthCalendar.get(Calendar.YEAR) - minMonthCalendar.get(Calendar.YEAR);
            return diffYear * 12 + maxMonthCalendar.get(Calendar.MONTH) - minMonthCalendar.get(Calendar.MONTH) + 1;
        }

        public int getCurrentMonthPosition() {
            int diffYear = currentDateCalendar.get(Calendar.YEAR) - minMonthCalendar.get(Calendar.YEAR);
            int diffMonth = diffYear * 12 + currentDateCalendar.get(Calendar.MONTH) - minMonthCalendar.get(Calendar.MONTH);
            return diffMonth * 2;
        }
    }

    /**
     * Customize Calendar using this interface
     */
    public interface CustomCalendarView {
        /**
         * Cell view for the month
         *
         * @param position
         * @param convertView
         * @param parent
         * @param cellType
         * @return
         */
        BaseCellView getCellView(int position, View convertView, ViewGroup parent, @BaseCellView.CellType int cellType);

        /**
         * Cell view for the weekday in the header
         *
         * @param position
         * @param convertView
         * @param parent
         * @return
         */
        BaseCellView getWeekdayCellView(int position, View convertView, ViewGroup parent);

        /**
         * Get display value for the day of week
         *
         * @param dayOfWeek    the value of day of week where 1 is SUNDAY, 2 is MONDAY ... 7 is SATURDAY
         * @param defaultValue the default value for the day of week
         * @return
         */
        String getDayOfWeekDisplayValue(int dayOfWeek, String defaultValue);
    }

    public interface EventDataProvider {
        List<? extends Event> getEventsForTheDay(int year, int month, int day);
    }

    /**
     * Click listener for date cell
     */
    public interface OnDateClickListener {
        /**
         * Called whenever a date cell is clicked
         *
         * @param day   selected day
         * @param month selected month
         * @param year  selected year
         */
        void onDateClick(int year, int month, int day);
    }
}
