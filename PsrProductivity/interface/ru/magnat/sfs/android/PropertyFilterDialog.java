package ru.magnat.sfs.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;

public class PropertyFilterDialog {
	
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private AlertDialog.Builder mDialogBuilder;
	private AlertDialog mAlertDialog;
	private View mDialogView;
	private int mLayoutId;
	private String mPositiveButtonText = "Фильтровать";
	
	@SuppressWarnings("unused")
	private PropertyFilterDialog() {}
	
	public PropertyFilterDialog(final Context context) {
		this.mContext = context;
		this.mLayoutInflater = LayoutInflater.from(this.mContext);
		this.mLayoutId = R.layout.property_filter_dialog;
		this.mDialogView = this.mLayoutInflater.inflate(this.mLayoutId, null);
		this.mDialogBuilder = new AlertDialog.Builder(this.mContext, android.R.style.Theme_Holo_Light_Dialog);
		this.mDialogBuilder.setCancelable(true);
		this.mDialogBuilder.setView(this.mDialogView);
		this.mDialogBuilder.setPositiveButton(this.mPositiveButtonText, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				//Toast.makeText(PropertyFilterDialog.this.mContext, "Фильтровать!", Toast.LENGTH_LONG).show();
				//SFSActivity.Me.onFilterChangedReceived(FilterType.RECOMMENDED);
			}
		});
		this.mAlertDialog = this.mDialogBuilder.create();
	}
	
	public void show() {
		this.mAlertDialog.show();
	}
	
}
