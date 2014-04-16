package ru.magnat.sfs.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class PropertyFilterDialogDefault {
	
	private Context mContext;
	private AlertDialog.Builder mDialogBuilder;
	private AlertDialog mAlertDialog;
	private String mPositiveButtonText = "Фильтровать";
	private String[] mItemsText = new String[2];
	private boolean[] mItemsChecked = new boolean[2];
	private String mDialogTitle = "Фильтрация списка";
	private DialogInterface.OnMultiChoiceClickListener multiChoiceClickListener = new DialogInterface.OnMultiChoiceClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which, boolean isChecked) {
			PropertyFilterDialogDefault.this.mItemsChecked[which] = isChecked;
		}
	};
	
	@SuppressWarnings("unused")
	private PropertyFilterDialogDefault() {}
	
	public PropertyFilterDialogDefault(final Context context) {
		
		this.mContext = context;
		this.mDialogBuilder = new AlertDialog.Builder(this.mContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		this.mDialogBuilder.setCancelable(false);
		this.mDialogBuilder.setTitle(this.mDialogTitle);
		this.mItemsText[0] = " Рекомендованые";
		this.mItemsText[1] = " Есть на складе";
		this.mDialogBuilder.setMultiChoiceItems(this.mItemsText, this.mItemsChecked, this.multiChoiceClickListener);
		this.mDialogBuilder.setPositiveButton(this.mPositiveButtonText, new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {

//				Toast.makeText(PropertyFilterDialogDefault.this.mContext, PropertyFilterDialogDefault.this.mDialogTitle, Toast.LENGTH_LONG).show();
				MainActivity.sInstance.onFilterChangedReceived(PropertyFilterDialogDefault.this.mItemsChecked);
			}
		});
		this.mDialogBuilder.setNegativeButton("Закрыть", new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {}
		});
		this.mAlertDialog = this.mDialogBuilder.create();
	}
	
	public void show() {
		this.mAlertDialog.show();
	}	
}