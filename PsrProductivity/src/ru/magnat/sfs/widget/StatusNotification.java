package ru.magnat.sfs.widget;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class StatusNotification {

	private int id;
	
	private Context context;
	
	private NotificationManager notificationManager;
	private NotificationCompat.Builder notification;
	
	private int icon = android.R.drawable.ic_menu_add;
	private String title = "";
	private String text = "";
	
	private PendingIntent pendingIntent = null;
	
	@SuppressWarnings("unused")
	private StatusNotification() {}
	
	public StatusNotification(Context context, int id) {
		
		this.id = id;
		
		this.context = context;
		this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		this.notificationManager.cancel(this.id);
		
		this.notification = new NotificationCompat.Builder(this.context);
		this.notification.setSmallIcon(this.icon);
		this.notification.setAutoCancel(true);

		if (this.pendingIntent != null) this.notification.setContentIntent(this.pendingIntent);
	}
	
	public StatusNotification(Context context, int id, Intent intent, int icon, String title, String text) {
		
		this.id = id;
		
		this.context = context;
		this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		this.notificationManager.cancel(this.id);
		
		this.notification = new NotificationCompat.Builder(this.context);
		
		this.icon = icon;
		this.title = title;
		this.text = text;
		
		this.notification.setSmallIcon(this.icon);
		this.notification.setContentTitle(this.title);
		this.notification.setContentText(this.text);
		this.notification.setAutoCancel(true);
		
		if (intent != null) {
		
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(this.context);
			stackBuilder.addNextIntent(intent);
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

			this.pendingIntent = resultPendingIntent;
			this.notification.setContentIntent(this.pendingIntent);
		}
	}
	
	public void setIcon(final int icon) {
		
		this.icon = icon;
		
		this.notification.setSmallIcon(this.icon);
	}
	
	public void setTitle(final String title) {
		
		this.title = title;
		
		this.notification.setContentTitle(this.title);
	}
	
	public void setMessage(final String text) {
		
		this.text = text;
		
		this.notification.setContentText(this.text);
	}
	
	public void setPendingIntent(Intent intent) {
		
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this.context);
		stackBuilder.addNextIntent(intent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		
		this.pendingIntent = resultPendingIntent;
		
		this.notification.setContentIntent(this.pendingIntent);
	}
	
	public void show() {
		
		this.notificationManager.notify(id, notification.build());
	}
	
	public void dismiss() {
		
		this.notificationManager.cancel(this.id);
	}
}
