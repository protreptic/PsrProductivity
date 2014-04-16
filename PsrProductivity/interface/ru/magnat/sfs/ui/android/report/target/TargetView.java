package ru.magnat.sfs.ui.android.report.target;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.query.targets.QueryGetEmployeeTarget;
import ru.magnat.sfs.bom.query.targets.QueryGetOutletTarget;
import ru.magnat.sfs.bom.query.targets.QueryGetTarget;
import ru.magnat.sfs.bom.ref.employee.RefEmployeeEntity;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.SfsListType;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class TargetView<T extends RefGenericEntity<?, ?>> extends SfsContentView implements Closeable {
	private T _subject;
	private QueryGetTarget _adapter;

	public TargetView(Context context, T entity, int horizon) {
		super(context);

		_subject = entity;
		if (_subject instanceof RefEmployeeEntity) {
			_adapter = new QueryGetEmployeeTarget(getContext(), new Date(), horizon);
		} else if (_subject instanceof RefOutletEntity) {
			_adapter = new QueryGetOutletTarget(getContext(), new Date(), horizon, (RefOutletEntity) _subject);
		} else {
			_adapter = null;
		}
		
		refresh();
	}

	@Override
	public SfsContentView inflate() {
		layoutInflater.inflate(R.layout.doc_order_list_view, this);
		((TextView) findViewById(R.id.caption_text)).setVisibility(GONE);
		((ImageButton) findViewById(R.id.caption_action)).setVisibility(GONE);

		ListView lv = (ListView) findViewById(R.id.list);
		if (_adapter != null) {
			lv.setAdapter(_adapter);
			refresh();
		}

		return this;
	}
	
	@Override
	public void refresh() {
		if (_subject instanceof RefEmployeeEntity) {
			Globals.refreshEmployeeTargets();
		} else if (_subject instanceof RefOutletEntity) {
			Globals.refreshOutletTargets((RefOutletEntity) _subject);
		}

		if (_adapter != null) {
			_adapter.SetListType(SfsListType.EXTENDED_LIST);
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

	public T getEntity() {
		return _subject;
	}

	public void close() throws IOException {
		if (_adapter != null)
			_adapter.close();
	}

}
