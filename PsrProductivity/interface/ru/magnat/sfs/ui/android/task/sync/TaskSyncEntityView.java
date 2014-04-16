package ru.magnat.sfs.ui.android.task.sync;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.task.sync.TaskSyncEntity;
import ru.magnat.sfs.bom.task.sync.TaskSyncJournal;
import ru.magnat.sfs.ui.android.GenericEntityView;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class TaskSyncEntityView extends GenericEntityView<TaskSyncJournal, TaskSyncEntity> {

	AlertDialog _alertDialog;

	public TaskSyncEntityView(Context context, TaskSyncJournal catalog, TaskSyncEntity entity) {
		super(context, catalog, entity);
	}

	SfsContentView shipmentDateView;
	SimpleAdapter _adapter;

	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
		refresh();
	}

	@Override
	public SfsContentView inflate() {

		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.doc_order_header_tab2, this);
		_adapter = new SimpleAdapter(getContext(), _entity.getProperties(),
				android.R.layout.simple_list_item_2, new String[] { "value",
						"property" }, new int[] { android.R.id.text1,
						android.R.id.text2 });
		ListView lv = (ListView) findViewById(R.id.list);
		lv.setAdapter(_adapter);

		((TextView) findViewById(R.id.caption)).setText(_entity.toString());
		return this;
	}

	@Override
	public void refresh() {

		_adapter.notifyDataSetChanged();

	}

}
