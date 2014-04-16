package ru.magnat.sfs.update;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class StatusNotification {

	private int mId;

	private Context mContext;

	private NotificationManager mNotificationManager;
	private NotificationCompat.Builder mNotification;

	private int mIcon = android.R.drawable.ic_menu_add;
	private String mTitle;
	private String mText;

	private PendingIntent mPendingIntent;

	public StatusNotification(Context context, int id) {
		mContext = context;
		mId = id;
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(mId);

		mTitle = "";
		mText = "";
		
		mNotification = new NotificationCompat.Builder(mContext);
		mNotification.setSmallIcon(mIcon);
		mNotification.setAutoCancel(true);

		if (mPendingIntent != null) {
			mNotification.setContentIntent(mPendingIntent);
		}
	}

	public StatusNotification(Context context, int id, Intent intent, int icon, String title, String text) {
		mContext = context;
		mId = id;
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(mId);
		mNotification = new NotificationCompat.Builder(mContext);

		mIcon = icon;
		mTitle = title;
		mText = text;

		mNotification.setSmallIcon(mIcon);
		mNotification.setContentTitle(mTitle);
		mNotification.setContentText(mText);
		mNotification.setAutoCancel(true);

		if (intent != null) {
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
			stackBuilder.addNextIntent(intent);
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
			mPendingIntent = resultPendingIntent;
			mNotification.setContentIntent(mPendingIntent);
		}
	}

	public void setIcon(int icon) {
		mIcon = icon;
		mNotification.setSmallIcon(mIcon);
	}

	public void setTitle(String title) {
		mTitle = title;
		mNotification.setContentTitle(mTitle);
	}

	public void setMessage(String text) {
		mText = text;
		mNotification.setContentText(mText);
	}

	public void setPendingIntent(Intent intent) {
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
		stackBuilder.addNextIntent(intent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

		mPendingIntent = resultPendingIntent;
		mNotification.setContentIntent(mPendingIntent);
	}

	public void show() {
		mNotificationManager.notify(mId, mNotification.build());
	}

	public void dismiss() {
		mNotificationManager.cancel(mId);
	}
}
