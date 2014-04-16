package ru.magnat.sfs.ui.android.task.sync;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.task.sync.*;

import ru.magnat.sfs.ui.android.GenericListItemView;
import ru.magnat.sfs.ui.android.SfsContentView;

public class TaskSyncExtendedListItemView extends GenericListItemView {

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	
	private TextView mFirstLine;
	private TextView mSecondLine;
	private ImageView mSyncStatus;
	
	public TaskSyncExtendedListItemView(Context context, TaskSyncJournal orm, ListView lv) {
		super(context, orm, lv);
		
		mContext = context;
		mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public SfsContentView inflate() {
		mLayoutInflater.inflate(R.layout.two_line_list_item_with_icon, this);
		
		Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Light.ttf");
		
		mFirstLine = (TextView) findViewById(R.id.first_line);
		mFirstLine.setTypeface(tf);
		mSecondLine = (TextView) findViewById(R.id.second_line);
		mSecondLine.setTypeface(tf);
		mSyncStatus = (ImageView) findViewById(R.id.icon);
		mSyncStatus.setVisibility(View.INVISIBLE);
		
		return this;
	}

	@Override
	public void fill() {
		TaskSyncEntity entity = (TaskSyncEntity) _orm.Current();
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		if (entity != null && entity.TaskBegin != null && entity.TaskEnd != null) {
			if (entity.IsCompleted != null && entity.IsCompleted) {
				mFirstLine.setText("Выполнено");
				mSecondLine.setText(format.format(entity.TaskBegin));
				mSyncStatus.setVisibility(View.VISIBLE);
			} else {
				mFirstLine.setText("Не выполнено");
				mSecondLine.setText(format.format(entity.TaskBegin));
				mSyncStatus.setImageResource(R.drawable.cancel);
				mSyncStatus.setVisibility(View.VISIBLE);
			}
		} else {
			mFirstLine.setText("Не завершено");
			mSecondLine.setText(format.format(entity.TaskBegin));
			mSyncStatus.setImageResource(R.drawable.warning);
			mSyncStatus.setVisibility(View.VISIBLE);
		}
	}
}
