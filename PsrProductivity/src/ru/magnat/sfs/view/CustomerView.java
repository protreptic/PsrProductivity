package ru.magnat.sfs.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.ref.customer.RefCustomer;
import ru.magnat.sfs.bom.ref.customer.RefCustomerEntity;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.util.Loader;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class CustomerView extends SfsContentView {

	private ListView mListView;
	private OutletAdapter mAdapter;
	private List<Map<String, ?>> mData = new ArrayList<Map<String, ?>>();
	
	public CustomerView(Context context, Long customerId) {
		super(context);
		    	
    	mAdapter = new OutletAdapter();
		
		layoutInflater.inflate(R.layout.list_layout, this);
		mListView = (ListView) findViewById(R.id.list);
		mListView.setAdapter(mAdapter); 
		
		new LoadData(context).execute(customerId);
	}
	
	private static class ViewHolder {
		public TextView text1;
		public TextView text2;
	}
	
	private class OutletAdapter extends BaseAdapter {

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
			return position;
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

			holder.text1.setText((String) mData.get(position).get("value"));
			holder.text2.setText((String) mData.get(position).get("property"));

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
			
			RefCustomer refCustomer = new RefCustomer(getContext());
			RefCustomerEntity store = refCustomer.FindById((Long) params[0]);
			refCustomer.close();
			refCustomer = null;
			
	    	for (int i = 0; i < store.getProperties().size(); i++) {
	    		mData.add(store.getProperties().get(i)); 
	    	}
	    	
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result); 
			
			mAdapter.notifyDataSetChanged();
		}
		
	}
	
}
