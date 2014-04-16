package ru.magnat.sfs.ui.android.extras;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.ui.android.GenericListView;
import ru.magnat.sfs.ui.android.SfsContentView;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import ru.magnat.sfs.android.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

public class SfsExtrasActivity extends Activity {
	TabHost _tabHost = null;
	final String TABS_TAG_PREV = "1";
	final String TABS_TAG_CURR = "2";
	final String TABS_TAG_NEXT = "3";
	private ActionBar mActionBar;
	final String CURR_TAB = "CurrentTab";
	String _currentTab = TABS_TAG_CURR;
	ExtrasTabContentFactory _tabFactory = new ExtrasTabContentFactory();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabbed_view_horizontal);

		mActionBar = getActionBar();
		mActionBar.setBackgroundDrawable(getResources().getDrawable(
				R.color.magnat_color));
		mActionBar.setDisplayUseLogoEnabled(true);
		mActionBar.setLogo(getResources().getDrawable(R.drawable.logotype));
		mActionBar.setTitle("");

		// Приложение запущено впервые или восстановлено из памяти?
		if (savedInstanceState == null) // приложение запущено впервые
		{
			_currentTab = TABS_TAG_CURR;
		} else // приложение восстановлено из памяти
		{
			_currentTab = savedInstanceState.getString(CURR_TAB);
		}

		String months[] = { "Январь", "Февраль", "Март", "Апрель", "Май",
				"Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь",
				"Декабрь" };
		_tabHost = (TabHost) findViewById(R.id.tabHost);
		_tabHost.setup();

		Resources res = getResources();

		Configuration cfg = res.getConfiguration();
		boolean hor = cfg.orientation == Configuration.ORIENTATION_LANDSCAPE;

		if (hor) {
			TabWidget tw = _tabHost.getTabWidget();
			tw.setOrientation(LinearLayout.VERTICAL);
		}

		Calendar c = GregorianCalendar.getInstance();
		c.setTime(new Date());
		int m1 = c.get(Calendar.MONTH);
		int m0 = (m1 == 0) ? 11 : m1 - 1;
		int m2 = (m1 == 11) ? 0 : m1 + 1;
		c = null;

		Utils.AddTab(_tabHost, _tabFactory, TABS_TAG_PREV, createIndicatorView(months[m0], null));
		Utils.AddTab(_tabHost, _tabFactory, TABS_TAG_CURR, createIndicatorView(months[m1], null));
		Utils.AddTab(_tabHost, _tabFactory, TABS_TAG_NEXT, createIndicatorView(months[m2], null));

		_tabHost.setCurrentTabByTag(_currentTab);

		Log.v(MainActivity.LOG_TAG, "Current tab: " + _currentTab);
	}

	private View createIndicatorView(CharSequence label, Drawable icon) {

		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

		View tabIndicator = inflater.inflate(R.layout.tab_indicator,
				_tabHost.getTabWidget(), // tab widget is the parent
				false); // no inflate params

		final TextView tv = (TextView) tabIndicator.findViewById(R.id.title);
		tv.setText(label);
		final ImageView iconView = (ImageView) tabIndicator
				.findViewById(R.id.icon);
		if (icon != null) {

			iconView.setImageDrawable(icon);
		} else {
			iconView.setVisibility(View.GONE);
		}

		return tabIndicator;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void onStart() {
		super.onStart();
		for (SfsContentView v : _tabFactory.tabs.values()) {
			if (v instanceof GenericListView) {
				((GenericListView) v).attachToSource();
			}
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void onStop() {
		super.onStop();
		for (SfsContentView v : _tabFactory.tabs.values()) {
			if (v instanceof GenericListView)
				((GenericListView) v).detachFromSource();
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString(CURR_TAB, _tabHost.getCurrentTabTag());
		Log.v(MainActivity.LOG_TAG, "Current tab: " + _currentTab);
	}

	private class ExtrasTabContentFactory implements TabHost.TabContentFactory {
		public HashMap<String, SfsContentView> tabs = new HashMap<String, SfsContentView>();

		public View createTabContent(String tag) {
			SfsContentView v = null;
			if (tag == TABS_TAG_PREV)
				v = new ExtrasTypeListView(SfsExtrasActivity.this, -1, tag);
			else if (tag == TABS_TAG_CURR) {
				v = new ExtrasTypeListView(SfsExtrasActivity.this, 0, tag);
			} else if (tag == TABS_TAG_NEXT) {
				v = new ExtrasTypeListView(SfsExtrasActivity.this, 1, tag);
			}
			if (v != null) {
				tabs.put(tag, v);
				v.inflate();
			}
			return v;
		}

	}

}
