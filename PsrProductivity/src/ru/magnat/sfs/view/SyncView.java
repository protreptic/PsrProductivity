package ru.magnat.sfs.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.database.UltraliteCursor;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.util.Loader;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SyncView extends SfsContentView {
	
	private SyncListAdapter mAdapter;
	private List<Map<String, String>> mData = new ArrayList<Map<String, String>>();
	private ListView mListView;
	
	public SyncView(Context context) {
		super(context);
		
		layoutInflater.inflate(R.layout.list_layout, this);
		
		mAdapter = new SyncListAdapter();
		
		mListView = (ListView) findViewById(R.id.list);
		mListView.setAdapter(mAdapter);
		
		new LoadData(context).execute(new Date());
	}

	private static class ViewHolder {
		ImageView image1;
		TextView text1;
		TextView text2;
	}
	
	private class SyncListAdapter extends BaseAdapter {
		
		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return mData.get(position);
		} 

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.two_line_list_item_with_icon, null);
				
				holder = new ViewHolder();
				holder.image1 = (ImageView) convertView.findViewById(R.id.icon);
				holder.text1 = (TextView) convertView.findViewById(R.id.first_line);
				holder.text2 = (TextView) convertView.findViewById(R.id.second_line);
				
				holder.text1.setTypeface(typeface);
				holder.text2.setTypeface(typeface);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.text1.setText((String) mData.get(position).get("first_line"));
			holder.text2.setText((String) mData.get(position).get("second_line"));
			
			int icon = (((String) mData.get(position).get("first_line")).equals(getResources().getString(R.string.dialog_sync_success)) ? R.drawable.accepted : R.drawable.cancel);
			
			holder.image1.setImageDrawable(getResources().getDrawable(icon)); 
			
			return convertView;
		}
		
	}
	
	private class LoadData extends Loader {
		
		private static final String query = 
			" select " +
				" Result, " + 
				" CreateDate " + 
			" from " +
				" TaskSyncJournal " +
			" where " +
				" datediff(day, CreateDate, ?) = 0 " + 
			" order by " +
				" CreateDate desc ";
		
		public LoadData(Context context) {
			super(context);
		}
		
		@Override
		protected Void doInBackground(Object... params) {
			mData.clear();
			
			UltraliteCursor cursor = new UltraliteCursor(getContext(), query);
			cursor.setParametersAndExecute(params);
			
			while (cursor.moveToNext()) {	
				Map<String, String> item = new HashMap<String, String>();
				item.put("first_line", ( cursor.getString(1) != null && cursor.getString(1).equals("Ok") ? getResources().getString(R.string.dialog_sync_success) : getResources().getString(R.string.dialog_sync_fail)));
				item.put("second_line", DateFormat.format("kk:mm:ss", cursor.getDate(2)).toString());
				mData.add(item);
			}
			
			cursor.close();
			cursor = null;
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result); 
			
			mAdapter.notifyDataSetChanged();
		}
		
	}

}
