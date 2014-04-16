package ru.magnat.sfs.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.ref.employee.RefEmployeeEntity;
import ru.magnat.sfs.bom.ref.user.RefUserEntity;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.util.Apps;
import ru.magnat.sfs.util.Device;
import ru.magnat.sfs.util.Loader;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class AboutView extends SfsContentView {

	private AboutListAdapter mAdapter;
	private List<Map<String, String>> mData = new ArrayList<Map<String, String>>();
	private ListView mListView;

	public AboutView(Context context) {
		super(context);

		layoutInflater.inflate(R.layout.list_layout, this);
		
		mAdapter = new AboutListAdapter();
		
		mListView = (ListView) findViewById(R.id.list);
		mListView.setAdapter(mAdapter);
		
		new LoadData(context).execute();
	}

	private static class ViewHolder {
		public TextView text1;
		public TextView text2;
	}

	private class AboutListAdapter extends BaseAdapter {

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
				convertView = layoutInflater.inflate(R.layout.two_line_list_item, null);

				holder = new ViewHolder();
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

			return convertView;
		}
	}

	private class LoadData extends Loader {
		
		public LoadData(Context context) {
			super(context);
		}

		@Override
		protected Void doInBackground(Object... params) {
			mData.clear();

			Map<String, String> serviceTelNum = new HashMap<String, String>();
			serviceTelNum.put("first_line", getResources().getString(R.string.about_hotline_number));
			serviceTelNum.put("second_line", getResources().getString(R.string.about_hotline_label));
			mData.add(serviceTelNum);

			Map<String, String> appVersion = new HashMap<String, String>();
			appVersion.put("first_line", Apps.getVersionName(getContext()));
			appVersion.put("second_line", getResources().getString(R.string.about_version_label));
			mData.add(appVersion);

			RefUserEntity user = Globals.getUser();
			Map<String, String> userName = new HashMap<String, String>();
			userName.put("first_line", (user != null ? (user.Descr)
					: (getResources().getString(R.string.about_unknown_label))));
			userName.put("second_line", getResources().getString(R.string.about_user_label)); 
			mData.add(userName);

			Map<String, String> userId = new HashMap<String, String>();
			userId.put("first_line", (user != null ? String.valueOf(user.Id)
					: (getResources().getString(R.string.about_unknown_label))));
			userId.put("second_line", getResources().getString(R.string.about_id_label)); 
			mData.add(userId);

			RefEmployeeEntity employee = Globals.getEmployee();
			Map<String, String> agentId = new HashMap<String, String>();
			agentId.put("first_line", (employee != null ? (employee.Descr)
					: (getResources().getString(R.string.about_unknown_label))));
			agentId.put("second_line", getResources().getString(R.string.about_agent_label));
			mData.add(agentId);

			Map<String, String> branch = new HashMap<String, String>();
			branch.put("first_line", (employee != null ? (employee.Business.Descr)
					: (getResources().getString(R.string.about_unknown_label))));
			branch.put("second_line", getResources().getString(R.string.about_business_label));
			mData.add(branch);

			Map<String, String> imei = new HashMap<String, String>();
			imei.put("first_line", Device.getDeviceId(getContext()));
			imei.put("second_line", getResources().getString(R.string.about_imei_label));
			mData.add(imei);
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			
			mAdapter.notifyDataSetChanged();
		}
		
	}

}
