package ru.magnat.sfs.ui.android.task.workday;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.TargetContainerAdapter;
import ru.magnat.sfs.bom.ref.employee.RefEmployeeEntity;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.util.Loader;
import android.content.Context;
import android.widget.ExpandableListView;

public class WorkdayTargetTab extends SfsContentView {

	private ExpandableListView mExpandableListView;
	private TargetContainerAdapter<RefEmployeeEntity> mAdapter;
	
	public WorkdayTargetTab(Context context) {
		super(context);

		layoutInflater.inflate(R.layout.expandable_list_layout, this);
		mExpandableListView = (ExpandableListView) findViewById(R.id.expandable_list);

		new LoadData(context).execute();
	}
	
	private class LoadData extends Loader {

		public LoadData(Context context) {
			super(context);
		}
		
		@Override
		protected Void doInBackground(Object... params) {
			mAdapter = new TargetContainerAdapter<RefEmployeeEntity>(getContext());
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) {
			mExpandableListView.setAdapter(mAdapter);

			for (int i = 0; i < mAdapter.getGroupCount(); i++) {
				mExpandableListView.expandGroup(i);
			}
			
			super.onPostExecute(result);
		}
		
	}

}
