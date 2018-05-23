package com.cittakalpa.flexiblelistcalendar.library.impl;

import android.view.View;
import android.view.ViewGroup;

import com.cittakalpa.flexiblelistcalendar.library.BaseCellView;
import com.cittakalpa.flexiblelistcalendar.library.CalendarView;

public class DateCellViewImpl implements IDateCellViewDrawer {

    private CalendarView.CustomCalendarView calendarView;

    public DateCellViewImpl(CalendarView.CustomCalendarView calendarView) {
        this.calendarView = calendarView;
    }

    @Override
    public void setCalendarView(CalendarView.CustomCalendarView calendarView) {
        this.calendarView = calendarView;
    }

    @Override
    public BaseCellView getCellView(int position, View convertView, ViewGroup parent, @BaseCellView.CellType int cellType) {
        return calendarView.getCellView(position, convertView, parent, cellType);
    }
}
