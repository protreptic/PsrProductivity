package ru.magnat.sfs.ui.android.report.target;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.query.targets.QueryGetEmployeeTarget;
import ru.magnat.sfs.bom.query.targets.QueryGetEmployeeTarget.Mode;
import ru.magnat.sfs.bom.ref.employee.RefEmployeeEntity;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.SfsListType;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class SalaryView extends SfsContentView implements Closeable {
	private RefEmployeeEntity _subject;
	private QueryGetEmployeeTarget _adapter;

	public SalaryView(Context context) {
		super(context);

		_subject = Globals.getEmployee();
		_adapter = new QueryGetEmployeeTarget(getContext(), new Date(), Calendar.MONTH);
		_adapter.setMode(Mode.Salary);
		refresh();
	}

	@Override
	public SfsContentView inflate() {
		layoutInflater.inflate(R.layout.list_layout, this);

		ListView lv = (ListView) findViewById(R.id.list);
		if (_adapter != null) {
			lv.setAdapter(_adapter);
			refresh();
		}

		return this;
	}

	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
		if (visibility != VISIBLE) {
			if (_adapter != null) {
				_adapter.close();
			}
		}
	}

	@Override
	public void refresh() {
		Globals.refreshEmployeeTargets();
		if (_adapter != null) {
			_adapter.SetListType(SfsListType.EXTENDED_LIST);
			_adapter.setMode(Mode.Salary);
			_adapter.Select(new ArrayList<SqlCriteria>(), "");
			_adapter.notifyDataSetChanged();
		}
	}

	public View getChildView(int position, View convertView, ViewGroup parent) {
		return _adapter.getView(position, convertView, parent);
	}

	public int getCount() {
		return _adapter.getCount();
	}

	public RefEmployeeEntity getEntity() {
		return _subject;
	}

	public void close() throws IOException {
		if (_adapter != null) {
			_adapter.close();
		}
	}

}
