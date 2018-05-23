package com.cittakalpa.flexiblelistcalender;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.cittakalpa.flexiblelistcalendar.library.BaseCellView;
import com.cittakalpa.flexiblelistcalendar.library.CalendarView;
import com.cittakalpa.flexiblelistcalender.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CalendarView.OnDateClickListener {

    private ActivityMainBinding binding;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mContext = this;
        initCalendarView();
    }

    private void initCalendarView() {
        Calendar minMonthCalendar = Calendar.getInstance();
        minMonthCalendar.set(Calendar.YEAR, minMonthCalendar.get(Calendar.YEAR) - 1);
        binding.calendarView.setMinMonthCalendar(minMonthCalendar);

        Calendar maxMonthCalendar = Calendar.getInstance();
        maxMonthCalendar.set(Calendar.YEAR, maxMonthCalendar.get(Calendar.YEAR) + 2);
        binding.calendarView.setMaxMonthCalendar(maxMonthCalendar);

        binding.calendarView.setMaxDateCalendar(Calendar.getInstance());

        binding.calendarView.setOnDateClickListener(this);

        binding.calendarView.setCalendarView(new CalendarView.CustomCalendarView() {

            @Override
            public BaseCellView getCellView(int position, View convertView, ViewGroup parent, int cellType) {
                BaseCellView cellView = (BaseCellView) convertView;
                if (cellView == null) {
                    cellView = new BaseCellView(mContext);
                }
                if (cellType == BaseCellView.TODAY) {
                    cellView.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
                }
                if (cellType == BaseCellView.OUTSIDE_MAX_DATE || cellType == BaseCellView.OUTSIDE_MONTH) {
                    cellView.setTextColor(ContextCompat.getColor(mContext, R.color.grey_350));
                }
                return cellView;
            }

            @Override
            public BaseCellView getWeekdayCellView(int position, View convertView, ViewGroup parent) {
                return null;
            }

            @Override
            public String getDayOfWeekDisplayValue(int dayOfWeek, String defaultValue) {
                return String.valueOf(defaultValue.charAt(0));
            }
        });
        updateCalendarInfo();
    }

    private void updateCalendarInfo() {
        binding.calendarView.setEventDataProvider((year, month, day) -> {
            if (year == 2018 && month == 4 && day == 2) {
                List<CustomEvent> colorList = new ArrayList<>();
                colorList.add(new CustomEvent(android.R.color.holo_red_dark));
                return colorList;
            }
            if (year == 2018 && month == 4 && day == 12) {
                List<CustomEvent> colorList = new ArrayList<>();
                colorList.add(new CustomEvent(android.R.color.holo_red_dark));
                colorList.add(new CustomEvent(android.R.color.holo_green_dark));
                return colorList;
            }
            return null;
        });
        binding.calendarView.refresh();
    }

    @Override
    public void onDateClick(int year, int month, int day) {
        Log.d("----", "onDateClick() called with: year = [" + year + "], month = [" + month + "], day = [" + day + "]");
    }
}
