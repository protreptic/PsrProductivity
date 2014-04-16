package ru.magnat.sfs.android;

import java.io.Closeable;

import ru.magnat.sfs.util.BaseTreeListAdapter;
import ru.magnat.sfs.util.OnTreeControlEventsListener;
import ru.magnat.sfs.util.TreeControlView;
import ru.magnat.sfs.util.TreeNode;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.view.Gravity;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.Toast;

public class Dialogs {

	@SuppressWarnings("unused")
	private static final CommandWrapper DISMISS = new CommandWrapper(
			Command.NO_OP);

	final static Context sContext = ru.magnat.sfs.MainActivity.getInstance();

	public static DatePickerDialog createDateDialog(final Context context,
			DatePickerDialog.OnDateSetListener listener, int year, int month,
			int day) {
		return new DatePickerDialog(context, listener, year, month, day);
	}

	/**
	 * Вызов диалога с одной кнопкой и обработчиком ее нажатия
	 * 
	 * @param sContext
	 * @param title
	 * @param message
	 * @param command
	 * @return
	 */
	public static AlertDialog createDialog(final String title,
			final String message, final Command command) {
		return createDialog(title, message, command, null, null,0);
	}

	/**
	 * Вызов диалога с двумя кнопками (Да+Нет) и обработчиками их нажатия
	 * 
	 * @param sContext
	 * @param title
	 * @param message
	 * @param commandYes
	 * @param commandNo
	 * @return
	 */
	public static AlertDialog createDialog(final String title, final String message, final Command commandYes, Command commandNo) {
		return createDialog(title, message, commandYes, commandNo, null,0);
	}

	public static AlertDialog createDialog(Context context, String title, String message, Command yes, Command no) {
		return createDialog(context, title, message, yes, no, null, 0);
	}
	
	/**
	 * Вызов диалога, который может до трех кнопок (Да|Ok+Нет+Отмена) и
	 * обработчиками их нажатия (если обработчик не задан кнопка не выводится)
	 * 
	 * @param sContext
	 * @param title
	 * @param message
	 * @param commandYes
	 * @param commandNo
	 * @param commandCancel
	 * @return
	 */
	public static AlertDialog createDialog(final String title, final String message, final Command commandYes, Command commandNo, final Command commandCancel, final int theme) {
		
			return createDialog(sContext, title, message, commandYes, commandNo, commandCancel, theme);
	} 
	public static AlertDialog createDialog(Context context1, final String title,
			final String message, final Command commandYes,
			final Command commandNo, final Command commandCancel) {
		return  createDialog(context1, title,
				message,  commandYes,
				 commandNo,  commandCancel,0);
			
		
	}
	
	
	public static AlertDialog createDialog(Context context1, final String title,
			final String message, final Command commandYes,
			final Command commandNo, final Command commandCancel, final int theme) {
		AlertDialog.Builder builder = (theme==0)?new AlertDialog.Builder(context1):new AlertDialog.Builder(context1,theme);
		builder.setCancelable(true);
		builder.setIcon(0);
		builder.setTitle(title);
		builder.setMessage(message);
		
		if (commandYes != null) {
			builder.setPositiveButton(
					(commandNo == null && commandCancel == null) ? "Ok" : "Да",
					new CommandWrapper(commandYes));
		}

		if (commandNo != null) {
			builder.setNegativeButton("Нет", new CommandWrapper(commandNo));
		}

		if (commandCancel != null) {
			builder.setNeutralButton("Отмена",
					new CommandWrapper(commandCancel));
		}

		return builder.create();
	}
	
	public static AlertDialog createDialog(Context context, String title, String message, View view, Command yes, Command no) {
		return createDialog(context, title, message, view, no, yes, null, 0);
	}
	
	public static AlertDialog createDialog(Context context, final String title, final String message, final View view, final Command commandNo, final Command commandYes, final Command commandCancel){
		return createDialog(context, title, message, view, commandNo, commandYes, commandCancel, 0);
	}
	
	public static AlertDialog createDialog(String title,
			final String message, final View view, final Command commandNo,
			final Command commandYes, final Command commandCancel){
		return createDialog(sContext, title, message, view, commandNo, commandYes, commandCancel, 0);
	}
	
	public static AlertDialog createDialog(String title,
			final String message, final View view, final Command commandNo,
			final Command commandYes, final Command commandCancel, int theme){
		return createDialog(sContext, title, message, view, commandNo, commandYes, commandCancel, theme);
	}
	
	/**
	 * Вызов диалога, который может до трех кнопок (Да|Ok+Нет+Отмена) и
	 * обработчиками их нажатия (если обработчик не задан кнопка не выводится)
	 * 
	 * @param sContext
	 * @param title
	 * @param message
	 * @param commandYes
	 * @param commandNo
	 * @param commandCancel
	 * @return
	 */
	public static AlertDialog createDialog(Context context, String title, String message, final View view, final Command commandNo,
			final Command commandYes, final Command commandCancel, int theme) {

		AlertDialog.Builder builder = (theme==0)? new AlertDialog.Builder(context):new AlertDialog.Builder(context,theme);
		builder.setCancelable(true);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle(title);
		builder.setView(view);
		builder.setInverseBackgroundForced(true);

		if (commandYes != null)
			builder.setPositiveButton(
					(commandNo == null && commandCancel == null) ? "Ok" : "Да",
					new CommandWrapper(commandYes));
		if (commandNo != null)
			builder.setNegativeButton("Нет", new CommandWrapper(commandNo));
		if (commandCancel != null)
			builder.setNeutralButton("Отмена",
					new CommandWrapper(commandCancel));
		AlertDialog dialog = builder.create();
		if (view instanceof Closeable){
			OnDismissCloseableView listener = new OnDismissCloseableView((Closeable) view);
			dialog.setOnDismissListener(listener);
		}
		return dialog;
	}

	/**
	 * Используется для оповещений, которые не требуют никакой постобработки
	 * 
	 * @param sContext
	 * @param message
	 */
	public static void MessageBox(final String message) {
		MessageBox(message, Toast.LENGTH_LONG);
	}
	

	/**
	 * Используется для оповещений, которые не требуют никакой постобработки
	 * 
	 * @param sContext
	 * @param message
	 * @param length
	 */
	public static void MessageBox(final String message, int length) {

		Toast toast = Toast.makeText(sContext, message, length);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	
	static public final AlertDialog createSelectFromListDialog(ListAdapter adapter, String title, OnClickListener listener) {
		return createSelectFromListDialog(MainActivity.getInstance(), adapter, title, listener, 0);
	}
	
	static public final AlertDialog createSelectFromListDialog(ListAdapter adapter, String title, OnClickListener listener, int theme) {
		return createSelectFromListDialog(MainActivity.getInstance(), adapter, title, listener, theme);
	}
	
	static public final AlertDialog createSelectFromListDialog(Context context, ListAdapter adapter, String title, OnClickListener listener, int theme) {
		AlertDialog.Builder builder;
		if (theme!= 0)
			builder = new AlertDialog.Builder(context, theme);
		else
			builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setAdapter(adapter, listener);
		
		return builder.create();
	}

	static AlertDialog selectFromTreeDialog = null;
	static OnTreeControlEventsListener selectFromTreeDialogListener = null;

	static public final AlertDialog createSelectFromTreeDialog(
			BaseTreeListAdapter adapter, String title,
			OnTreeControlEventsListener listener) {

		selectFromTreeDialogListener = listener;
		AlertDialog.Builder builder = new AlertDialog.Builder(sContext);
		TreeControlView ctrl = new TreeControlView(sContext, adapter);
		ctrl.setOnTreeControlEventsListener(new OnTreeControlEventsListener() {

			public void onTreeNodeSelected(Object sender, TreeNode value) {
				if (selectFromTreeDialog != null) {
					if (selectFromTreeDialogListener != null)
						selectFromTreeDialogListener.onTreeNodeSelected(sender,
								value);
					selectFromTreeDialog.dismiss();
				}

			}

			public void onCancel(Object sender) {
				if (selectFromTreeDialog != null) {
					if (selectFromTreeDialogListener != null)
						selectFromTreeDialogListener.onCancel(sender);
					selectFromTreeDialog.cancel();
				}

			}

		});
		builder.setView(ctrl);
		selectFromTreeDialog = builder.create();
		return selectFromTreeDialog;
	}
	
	
}
