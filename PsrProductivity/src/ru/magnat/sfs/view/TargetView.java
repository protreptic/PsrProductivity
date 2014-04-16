package ru.magnat.sfs.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class TargetView extends SfsContentView {

	private TargetAdapter mAdapter;
	private ExpandableListView mExpandableListView;
	private List<Map<String, String>> mGroupData = new ArrayList<Map<String, String>>();
	private List<List<Map<String, String>>> mChildData = new ArrayList<List<Map<String, String>>>();
	
	public TargetView(Context context) {
		super(context);
		
		layoutInflater.inflate(R.layout.expandable_list_layout, this);
		
		mAdapter = new TargetAdapter();
		mExpandableListView = (ExpandableListView) findViewById(R.id.expandable_list);
		mExpandableListView.setAdapter(mAdapter);

		new LoadData().execute();
	}

	private static class GroupViewHolder {
		TextView text1;
		TextView text2;
	}
	
	@SuppressWarnings("unused")
	private static class TargetViewHolder {
		TextView text1;
		TextView text2;
		TextView text3;
		TextView text4;
		TextView text5;
	}
	
	@SuppressWarnings("unused")
	private static class InitiativeViewHolder {
		TextView text1;
		TextView text2;
		TextView text3;
		TextView text4;
		TextView text5;
	}
	
	@SuppressWarnings("unused")
	private static class SalaryViewHolder {
		TextView text1;
		TextView text2;
		TextView text3;
		TextView text4;
		TextView text5;
		TextView text6;
	}
	
	private class TargetAdapter extends BaseExpandableListAdapter {

		@Override
		public int getGroupCount() {
			return mGroupData.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return 0;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return mGroupData.get(groupPosition); 
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return null; 
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}
		
		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			GroupViewHolder holder;
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.expandable_two_line_list_item, null);
				
				holder = new GroupViewHolder();
				holder.text1 = (TextView) convertView.findViewById(R.id.first_line);
				holder.text1.setTypeface(typeface);
				holder.text2 = (TextView) convertView.findViewById(R.id.second_line);
				holder.text2.setTypeface(typeface);
				
				convertView.setTag(holder);
			} else {
				holder = (GroupViewHolder) convertView.getTag();
			}
			
			holder.text1.setText(mGroupData.get(groupPosition).get("description"));
			holder.text2.setVisibility(View.GONE);
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			return null;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}
		
	}
	
	private class LoadData extends AsyncTask<Object, Void, Void> {

		private ProgressDialog mProgressDialog;
		
		@SuppressWarnings("unused")
		private static final String query1 = 
			" select " +
				" 1 as Id, " +
				" k.Descr + ' ' + COALESCE(i.Descr, '') as Descr, " + 
				" r.Kpi, " + 
				" r.KpiMatrix, " +
				" coalesce(r.Target, 0) as Target, " +
				" coalesce(r.Fact, 0) as Fact, " +
				" (coalesce(r.Target, 0) - coalesce(r.Fact,0)) as GAP, " + 
				" (case when coalesce(r.Target, 0) = 0 then 0 else r.KpiIndex * 0.01 end) as TargetIndex, " +
				" k.KpiKind as KpiKind, " +
				" coalesce(r.KpiShare, 0) as KpiShare, " +
				" r.Initiative as Initiative " +
			" from " +
				" RegKpiMatrix as r " +
				" inner join RefKpi as k on " +
					" r.KPI = k.Id " +
				" left join RefInitiative as i on " + 
					" r.Initiative = i.Id " +
			" where " +
				" r.KpiMatrix = ? " + 
				" and r.Horizon = ? " +
				" and r.Period = ? " + 
				" and r.Employee = ? " +
				" and coalesce(r.KpiShare, 0) > ? " + 
			" order by " +
				" r.Kpi, " +
				" i.Descr ";
		
		@SuppressWarnings("unused")
		private static final String query2 = 
			" select " +
				" 1 as Id, " +
				" k.DESCR + ' ' + coalesce(i.Descr, '') as Descr, " + 
				" r.Kpi, " +
				" r.KpiMatrix, " + 
				" coalesce(r.Target, 0) as Target, " +
				" coalesce(r.Fact, 0) as Fact, " +
				" (coalesce(r.Target,0) - coalesce(r.Fact, 0)) as GAP, " +
				" r.KpiIndex * 0.01 as TargetIndex, " + 
				" coalesce(r.KpiShare, 0) as KpiShare, " + 
				" k.KpiKind as KpiKind, " + 
				" r.Initiative as Initiative " + 
			" from " + 
				" RegKpiMatrix as r " +
				" inner join RefKpi as k on " +
					" r.KPI = k.Id " +
				" left join RefInitiative i on " + 
					" r.Initiative = i.Id " +
			" where " + 
				" r.KpiMatrix = coalesce(?, r.KpiMatrix) " +
				" and r.Horizon = ? " + 
				" and r.Period = ? " +
				" and r.Employee = ? " +
				" and r.Outlet  = ? " +
			" order by " + 
				" r.Kpi, " + 
				" i.Descr ";
		
		public LoadData() {
			mProgressDialog = new ProgressDialog(getContext());
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setMessage(getContext().getResources().getString(R.string.data_loading));
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.setCancelable(false);
		}
		
		@Override
		protected void onPreExecute() {
			mProgressDialog.show();
		}
		
		private String[] mArticles = {
			"Цели на день",
			"Цели на месяц",
			"Зарплата",
		};
		
		@Override
		protected Void doInBackground(Object... params) {
			mGroupData.clear();
			mChildData.clear();

			Map<String, String> group = new HashMap<String, String>();
			group.put("description", mArticles[0]);
			mGroupData.add(group); 
			
			
			
			group = new HashMap<String, String>();
			group.put("description", mArticles[1]);
			mGroupData.add(group); 
			
			group = new HashMap<String, String>();
			group.put("description", mArticles[2]);
			mGroupData.add(group); 
			
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mAdapter.notifyDataSetChanged();
			
			for (int i = 0; i < mAdapter.getGroupCount(); i++) {
				mExpandableListView.expandGroup(i);
			}
			
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
		
	}
	
	@Override
	public SfsContentView inflate() {
		return this;
	}


}
