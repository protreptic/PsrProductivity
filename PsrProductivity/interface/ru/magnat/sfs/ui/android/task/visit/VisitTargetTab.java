package ru.magnat.sfs.ui.android.task.visit;

import java.io.Closeable;
import java.io.IOException;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.TargetContainerAdapter;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.task.visit.TaskVisitEntity;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.util.Loader;
import android.content.Context;
import android.view.View;
import android.widget.ExpandableListView;

public class VisitTargetTab extends SfsContentView implements Closeable {

	private TaskVisitEntity _entity;
	private TargetContainerAdapter<RefOutletEntity> mAdapter;
	private ExpandableListView mListView;
	
	public VisitTargetTab(Context context, TaskVisitEntity entity) {
		super(context);
		
		_entity = entity;
		
		layoutInflater.inflate(R.layout.expandable_list_layout, this);
		mListView = (ExpandableListView) findViewById(R.id.expandable_list);
		
		new LoadData(getContext()).execute();
	} 

	private class LoadData extends Loader {
		
		public LoadData(Context context) {
			super(context);
		}
		
		@Override
		protected Void doInBackground(Object... params) {
			mAdapter = new TargetContainerAdapter<RefOutletEntity>(getContext(), _entity.Outlet);

			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) {
			mListView.setAdapter(mAdapter);
			mAdapter.notifyDataSetChanged();
			
			for (int i = 0; i < mAdapter.getGroupCount(); i++) {
				mListView.expandGroup(i);
			}
			
			super.onPostExecute(result); 
		}
		
	}
	
	@Override
	public SfsContentView inflate() {
		return this;
	}

	@Override
	public void refresh() {
		if (mAdapter != null) {
			mAdapter.refresh();
		}
	}
	
	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
		if (visibility == VISIBLE) {
			refresh();
		}
	}

	public void dismiss() {
		if (mAdapter != null) {
			mAdapter.close();
		}

	}

	@Override
	public void close() throws IOException {
		if (mAdapter != null) {
			mAdapter.close();
		}
		
	}

}
