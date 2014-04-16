package ru.magnat.sfs.view;

import android.content.Context;
import android.view.View;
import android.widget.TabHost;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.task.workday.WorkdayTargetTab;

public class VisitView extends SfsContentView {
	
	private static final String VIEW_VISIT = "1";
	private static final String VIEW_STORE = "2";
	private static final String VIEW_DATA = "3";
	private static final String VIEW_TARGET = "4";

	private TabHost mTabHost;
	private WorkdayTabContentFactory mTabFactory;

	public VisitView(Context context) {
		super(context);

		layoutInflater.inflate(R.layout.tabbed_view, this);
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();

		mTabFactory = new WorkdayTabContentFactory();

		Utils.AddTab(mTabHost, mTabFactory, VIEW_VISIT, "Маршрут");
		Utils.AddTab(mTabHost, mTabFactory, VIEW_STORE, "Точки");
		Utils.AddTab(mTabHost, mTabFactory, VIEW_DATA, "Данные");
		Utils.AddTab(mTabHost, mTabFactory, VIEW_TARGET, "Цели");
	}

	public static TaskView mVisitListView;

	private class WorkdayTabContentFactory implements TabHost.TabContentFactory {

		public View createTabContent(String tag) {
			SfsContentView view = null;
			if (tag == VIEW_VISIT) {
				view = mVisitListView = new TaskView(getContext());
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
