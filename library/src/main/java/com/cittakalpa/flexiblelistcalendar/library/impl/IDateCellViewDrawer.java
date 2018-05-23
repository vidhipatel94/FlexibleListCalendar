package com.cittakalpa.flexiblelistcalendar.library.impl;

import android.view.View;
import android.view.ViewGroup;

import com.cittakalpa.flexiblelistcalendar.library.BaseCellView;

public interface IDateCellViewDrawer extends ICellViewDrawer {
    /**
     * Date Cell view
     *
     * @param position
     * @param convertView
     * @param parent
     * @param cellType
     * @return
     */
    BaseCellView getCellView(int position, View convertView, ViewGroup parent,
                             @BaseCellView.CellType int cellType);
}
