/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.magnat.sfs.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;

/**
 * A simple dialog containing an {@link android.widget.DatePicker}.
 *
 * <p>See the <a href="{@docRoot}resources/tutorials/views/hello-datepicker.html">Date Picker
 * tutorial</a>.</p>
 */
public class NumberPickerDialog extends AlertDialog implements OnClickListener,
        OnValueChangeListener {

  
	private static final String VALUE = "value";
	
    private final NumberPicker mNumberPicker;
    private final OnNumberSetListener mCallBack;

    /**
     * The callback used to indicate the user is done filling in the date.
     */
    public interface OnNumberSetListener {

        /**
         * @param view The view associated with this listener.
         * @param year The year that was set.
         * @param monthOfYear The month that was set (0-11) for compatibility
         *  with {@link java.util.Calendar}.
         * @param dayOfMonth The day of the month that was set.
         */
        void onNumberSet(NumberPicker view, int value);
    }

    /**
     * @param context The context the dialog is to run in.
     * @param callBack How the parent is notified that the date is set.
     * @param year The initial year of the dialog.
     * @param monthOfYear The initial month of the dialog.
     * @param dayOfMonth The initial day of the dialog.
     */
    public NumberPickerDialog(Context context,
            OnNumberSetListener callBack,
            int value, int minValue, int maxValue) {
        this(context, 0, callBack, value,minValue,maxValue, "Отмена","Ok","");
    }
    public NumberPickerDialog(Context context,
            int theme,
            OnNumberSetListener callBack,
            int value,
            String[] values,
            String negativeButtonText,
            String positiveButtonText,
            String dialogTitle){
    	this(context, theme,callBack,value,0,values.length-1,values,negativeButtonText,positiveButtonText,dialogTitle);
    }
    public NumberPickerDialog(Context context,
            int theme,
            OnNumberSetListener callBack,
            int value,
            int minValue,
            int maxValue,
            String negativeButtonText,
            String positiveButtonText,
            String dialogTitle){
    	this(context, theme,callBack,value,minValue,maxValue,null,negativeButtonText,positiveButtonText,dialogTitle);
    }
    /**
     * @param context The context the dialog is to run in.
     * @param theme the theme to apply to this dialog
     * @param callBack How the parent is notified that the date is set.
     * @param year The initial year of the dialog.
     * @param monthOfYear The initial month of the dialog.
     * @param dayOfMonth The initial day of the dialog.
     */
    public NumberPickerDialog(Context context,
            int theme,
            OnNumberSetListener callBack,
            int value,
            int minValue,
            int maxValue,
            String[] values,
            String negativeButtonText,
            String positiveButtonText,
            String dialogTitle) {
        super(context, theme);

        mCallBack = callBack;

        Context themeContext = getContext();
        setButton(BUTTON_POSITIVE, positiveButtonText, this);
        setButton(BUTTON_NEGATIVE, negativeButtonText, (OnClickListener) null);
        setIcon(0);
        setTitle(dialogTitle);

        LayoutInflater inflater =
                (LayoutInflater) themeContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.number_picker_dialog, null);
        setView(view);
        int color = getContext().getResources().getColor(android.R.color.transparent);
        view.setBackgroundColor(color);
      
        mNumberPicker = (NumberPicker) view.findViewById(R.id.number_picker_dialog_number_picker);
        mNumberPicker.setMinValue(minValue);
        mNumberPicker.setMaxValue(maxValue);
        mNumberPicker.setValue(value);
        mNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        if (values!=null) mNumberPicker.setDisplayedValues(values);
        //mNumberPicker.ini
    }

    public void onClick(DialogInterface dialog, int which) {
        if (mCallBack != null) {
            mNumberPicker.clearFocus();
            mCallBack.onNumberSet(mNumberPicker, mNumberPicker.getValue());
        }
    }
    @Override
    public void onValueChange(NumberPicker view, int oldVal, int newVal) {
        mNumberPicker.setValue(newVal);
    }
	
	

    /**
     * Gets the {@link NumberPicker} contained in this dialog.
     *
     * @return The number picker view.
     */
    public NumberPicker getNumberPicker() {
        return mNumberPicker;
    }

    /**
     * Sets the value.
     *
     * @param value Requested value.
     */
    public void setValue(int value) {
        mNumberPicker.setValue(value);
    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(VALUE, mNumberPicker.getValue());
       
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int value = savedInstanceState.getInt(VALUE);
        mNumberPicker.setValue(value);
    }
}


