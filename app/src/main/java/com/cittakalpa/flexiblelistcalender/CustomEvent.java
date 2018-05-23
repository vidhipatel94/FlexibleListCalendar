package com.cittakalpa.flexiblelistcalender;

import com.cittakalpa.flexiblelistcalendar.library.Event;

/**
 * Created by Vidhi on 23/05/2018.
 */

public class CustomEvent implements Event {
    int color;

    public CustomEvent(int color) {
        this.color = color;
    }

    @Override
    public int getColor() {
        return color;
    }
}
