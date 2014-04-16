package ru.magnat.sfs.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.magnat.sfs.android.Command;
import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.ref.outlet.RefOutlet;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.task.visit.TaskVisitEntity;
import ru.magnat.sfs.bom.task.visit.TaskVisitJournal;
import ru.magnat.sfs.database.UltraliteCursor;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.util.Loader;
import ru.magnat.sfs.util.Text;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class StoreView extends SfsContentView {

	private StoreAdapter mAdapter;
	private ExpandableListView mExpandableListView;
	private List<Map<String, String>> mGroupData = new ArrayList<Map<String, String>>();
	private List<List<Map<String, String>>> mChildData = new ArrayList<List<Map<String, String>>>();
	
	private RefOutletEntity mOutletEntity;
	
	public StoreView(Context context) {
		super(context);

		layoutInflater.inflate(R.layout.expandable_list_layout, this);	
		
		mAdapter = new StoreAdapter();
		
		mExpandableListView = (ExpandableListView) findViewById(R.id.expandable_list);
		mExpandableListView.setAdapter(mAdapter);
		mExpandableListView.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				mOutletEntity = (RefOutletEntity) mAdapter.getChild(groupPosition, childPosition);
				
				Dialogs.createDialog("", getResources().getString(R.string.dialog_visit_store_add) + mOutletEntity.Descr + " " + Text.prepareAddress(mOutletEntity.Address) + "?", new Command() {
					public void execute() { 
						TaskVisitJournal journal = new TaskVisitJournal(getContext());
						journal.NewEntity();
						TaskVisitEntity entity = journal.Current();
						entity.TaskDate = new Date(System.currentTimeMillis());
						entity.TaskBegin = new Date(System.currentTimeMillis());
						entity.IsCompleted = false;
						entity.Author = Globals.getUser();
						entity.IsMark = false;
						entity.Outlet = mOutletEntity;
						
						if (!journal.save()) {
							Dialogs.MessageBox(getResources().getString(R.string.dialog_visit_save_error));
							journal.close(); 
							return;
						}
						journal.close();

						if (MainActivity.getInstance().mCurrentWorkday != null){
							MainActivity.getInstance().mCurrentWorkday.openVisitRequest(entity);
						}
						
						WorkdayView.mVisitListView.openVisit(entity);
					}
				}, Command.NO_OP).show();
				
				return false;
			}
		});
		
		new LoadData(context).execute();
	}
	
	private class LoadData extends Loader {
		
		public LoadData(Context context) {
			super(context);
		}

		private static final String query1 = 
			" select " +
				" Id, " +
				" Descr " +
			" from " + 
				" RefCustomer " +
			" order by " +
				" Descr asc "; 
		
		private static final String query2 = 
			" select " +
				" b.Id, " +
				" b.Address, " +
				" if (c.Descr like '%Обычный%' or c.Descr is null) then '0' else '1' endif as golden_status " +
			" from " + 
				" RefCustomer as a " +
				" inner join RefOutlet as b on " + 
					" a.Id = b.ParentExt " +
				" left join RefStoreType as c on " +
					" b.StoreType = c.Id " +
			" where " +
				" a.Id = ? " +
			" order by " +
				" c.Descr asc ";
		
		@Override
		protected Void doInBackground(Object... params) {
			mGroupData.clear();
			mChildData.clear();
			
			UltraliteCursor cursor1 = new UltraliteCursor(getContext(), query1);
			cursor1.setParametersAndExecute(new Object[] {}); 
			
			while (cursor1.moveToNext()) {
				UltraliteCursor cursor2 = new UltraliteCursor(getContext(), query2);
				cursor2.setParametersAndExecute(cursor1.getLong(1)); 

				List<Map<String, String>> childDataItem = new ArrayList<Map<String, String>>();
				while (cursor2.moveToNext()) {
					Map<String, String> item = new HashMap<String, String>();
					item.put("store_id", String.valueOf(cursor2.getLong(1)));
					item.put("second_line", Text.prepareAddress(cursor2.getString(2))); 
					item.put("golden_status", cursor2.getString(3));
					childDataItem.add(item);   
				}
				mChildData.add(childDataItem);
				
				cursor2.close();
				cursor2 = null;
				
				Map<String, String> group = new HashMap<String, String>();
				group.put("first_line", cursor1.getString(2)); 
				mGroupData.add(group); 
			}
			
			cursor1.close();
			cursor1 = null;
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) {
			mAdapter.notifyDataSetChanged();
			
			super.onPostExecute(result); 
		}
		
	}
	
	private static class ViewHolder {
		ImageView image1;
		TextView text1;
		TextView text2;
	}
	
	private class StoreAdapter extends BaseExpandableListAdapter {

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			long id = Long.valueOf(mChildData.get(groupPosition).get(childPosition).get("store_id"));
			
			RefOutlet refOutlet = new RefOutlet(getContext());
			RefOutletEntity entity = refOutlet.FindById(id);
			refOutlet.close();
			
			return entity;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.expandable_two_line_list_item, null);
				
				holder = new ViewHolder();
				holder.text1 = (TextView) convertView.findViewById(R.id.first_line);
				holder.text2 = (TextView) convertView.findViewById(R.id.second_line);

				holder.text1.setTypeface(typeface);
				holder.text2.setTypeface(typeface);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.text2.setVisibility(View.GONE); 
			
			holder.text1.setText(mGroupData.get(groupPosition).get("first_line"));
			
			return convertView;
		}
		
		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.two_line_list_item_with_icon, null);
				 
				holder = new ViewHolder();
				holder.image1 = (ImageView) convertView.findViewById(R.id.icon);
				holder.image1.setLayoutParams(new LayoutParams(32, 32));
				holder.text1 = (TextView) convertView.findViewById(R.id.first_line);
				holder.text2 = (TextView) convertView.findViewById(R.id.second_line);

				holder.text1.setTypeface(typeface);
				holder.text2.setTypeface(typeface);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.image1.setVisibility(View.GONE);
			holder.text1.setVisibility(View.GONE);
			
			holder.text2.setText(mChildData.get(groupPosition).get(childPosition).get("second_line"));
			
			if (mChildData.get(groupPosition).get(childPosition).get("golden_status").equals("1")) {
				holder.image1.setImageDrawable(getContext().getResources().getDrawable(R.drawable.cup_gold));
				holder.image1.setVisibility(View.VISIBLE); 
			}
			
			return convertView;
		}
		
		@Override
		public int getChildrenCount(int groupPosition) {
			return mChildData.get(groupPosition).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return null;
		}

		@Override
		public int getGroupCount() {
			return mGroupData.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
		
	}

}
