package com.a10miaomiao.bilimiao.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import com.a10miaomiao.bilimiao.R;
import com.a10miaomiao.bilimiao.utils.SelectorDateUtil;

import java.util.Date;

/**
 * Created by 10喵喵 on 2017/6/25.
 */

public class MonthPickerView extends FrameLayout {

    NumberPicker yearNumberPicker;
    NumberPicker monthNumberPicker;

    public MonthPickerView(Context context) {
        this(context,null);
    }

    public MonthPickerView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MonthPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.layout_month_picker,this);
        monthNumberPicker = (NumberPicker) findViewById(R.id.np_month);
        yearNumberPicker = (NumberPicker) findViewById(R.id.np_year);
        init();
    }
    private void init(){
        Date date = new Date();
        yearNumberPicker.setMinValue(2009);
        monthNumberPicker.setMinValue(1);
        yearNumberPicker.setMaxValue(1900 + date.getYear());
        monthNumberPicker.setMaxValue(12);

        yearNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        monthNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    }

    public void setYear(int value){
        yearNumberPicker.setValue(value);
    }
    public void setMonth(int value){
        monthNumberPicker.setValue(value);
    }
    public void setValue(Date date){
        setYear(date.getYear() + 1900);
        setMonth(date.getMonth() + 1);
    }

    public String getTimeFrom(){
        return String.valueOf(yearNumberPicker.getValue()) +
                (monthNumberPicker.getValue() < 10 ?
                        "0" + monthNumberPicker.getValue() : String.valueOf(monthNumberPicker.getValue()))
                + "01";
    }
    public String getTimeTo() {

        return String.valueOf(yearNumberPicker.getValue()) +
                (monthNumberPicker.getValue() < 10 ?
                        "0" + monthNumberPicker.getValue() : String.valueOf(monthNumberPicker.getValue()))
                + SelectorDateUtil.Companion.getMonthDate(yearNumberPicker.getValue() % 4 == 0
                        , monthNumberPicker.getValue() - 1);
    }
    public void setEnabled(boolean enabled){
        yearNumberPicker.setEnabled(enabled);
        monthNumberPicker.setEnabled(enabled);
    }
}
