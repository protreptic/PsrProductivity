package ru.magnat.sfs.widget;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

public class ProgressNotification {

	private Context mActivityContext;
	private Integer mId;

	private NotificationManager mNotificationManager;
	private NotificationCompat.Builder mNotification;
	
	private Integer mIcon;
	private String mTitle;
	private String mMessage;
	
	private Integer mMaxProgress;
	private Boolean mIndeterminate;
	private PendingIntent mPendingIntent;
	
	@SuppressWarnings("unused")
	private ProgressNotification() {}
	
	public ProgressNotification(final Context context, final Integer id) {
		mActivityContext = context;
		mId = id;

		mNotificationManager = (NotificationManager) mActivityContext.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotification = new NotificationCompat.Builder(mActivityContext);
	}
	
	public Integer getId() {
		return mId;
	}

	public void setId(final Integer id) {
		this.mId = id;
	}
	
	public Integer getIcon() {
		return mIcon;
	}
	
	public void setIcon(final Integer icon) {
		mIcon = icon;
	}
	
	public String getTitle() {
		return mTitle;
	}
	
	public void setTitle(final String title) {
		mTitle = title;
	}

	public String getMessage() {
		return mMessage;
	}
	
	public void setMessage(final String message) {
		mMessage = message;
	}
	
	public Boolean getIndeterminate() {
		return mIndeterminate;
	}
	
	public void setIndeterminate(final Boolean indeterminate) {
		mIndeterminate = indeterminate;
	}
	
	public PendingIntent getIntent() {
		return mPendingIntent;
	}
	
	public void setPendingIntent(final PendingIntent intent) {
		mPendingIntent = intent;
	}
	
	public Integer getMaxProgress() {
		return mMaxProgress;
	}
	
	public void setMaxProgress(final Integer maxProgress) {
		mMaxProgress = maxProgress;
	}
	
	public void progress(final String message, final Integer progress) {
		mNotification.setContentText(mMessage);
		if (mIndeterminate) {
			mNotification.setProgress(0, 0, mIndeterminate);
		} else {
			mNotification.setProgress(mMaxProgress, progress, mIndeterminate);
		}
		mNotificationManager.notify(mId, mNotification.build());
	}
	
	public void dismiss() {
		mNotificationManager.cancel(mId);
	}
}
