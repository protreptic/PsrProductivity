package ru.magnat.sfs.ui.android.task.workday;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayEntity;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayJournal;
import ru.magnat.sfs.ui.android.GenericEntityView;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.view.DataView;
import ru.magnat.sfs.view.ErrorView;
import ru.magnat.sfs.view.StoreView;
import ru.magnat.sfs.view.TaskView;
import android.content.Context;
import android.view.View;
import android.widget.TabHost;

public class TaskWorkdayEntityView extends GenericEntityView<TaskWorkdayJournal, TaskWorkdayEntity> {
	
	private static final String VIEW_VISIT = "1";
	private static final String VIEW_STORE = "2";
	private static final String VIEW_DATA = "3";
	private static final String VIEW_TARGET = "4";

	public TaskWorkdayEntityView(Context context, TaskWorkdayJournal tasks, TaskWorkdayEntity workday) {
		super(context, tasks, workday);
		
		layoutInflater.inflate(R.layout.tabbed_view, this);
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		mTabHost.setOnTabChangedListener(this);
		
		_tabFactory = new WorkdayTabContentFactory();

		Utils.AddTab(mTabHost, _tabFactory, VIEW_VISIT, "Маршрут");
		Utils.AddTab(mTabHost, _tabFactory, VIEW_STORE, "Точки");
		Utils.AddTab(mTabHost, _tabFactory, VIEW_DATA, "Данные");
		Utils.AddTab(mTabHost, _tabFactory, VIEW_TARGET, "Цели");
	}
	
	private class WorkdayTabContentFactory implements TabHost.TabContentFactory {

		public View createTabContent(String tag) {
			SfsContentView view = null; 
			if (tag == VIEW_VISIT) {
				view = new TaskView(getContext());
			} else if (tag == VIEW_STORE) {
				view = new StoreView(getContext());
			} else if (tag == VIEW_DATA) {
				view = new DataView(getContext());
			} else if (tag == VIEW_TARGET) {
				view = new WorkdayTargetTab(getContext());
			} else {
				view = new ErrorView(getContext());
			}
			
			return view;
		}
	}

}
