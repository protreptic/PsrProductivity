package ru.magnat.sfs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ru.magnat.sfs.android.Command;
import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.database.UltraliteCursor;
import ru.magnat.sfs.util.Fonts;
import ru.magnat.sfs.util.Text;
import ru.magnat.sfs.view.OutletView;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ListFragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class RouteFragment extends ListFragment {
	
	private VisitAdapter mAdapter;
	private List<Map<String, String>> mData = new ArrayList<Map<String, String>>();
	private ListView mListView;
	
	public RouteFragment() {}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true); 
	}
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        setEmptyText("Нет записей"); 
		mAdapter = new VisitAdapter();
		mListView = getListView();
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {}
		});

		new LoadData().execute(new Date());
    }
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater); 
		
		inflater.inflate(R.menu.route_fragment_menu, menu); 
	}
	
	private Calendar mCalendar = Calendar.getInstance(new Locale("ru", "RU"));
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.filter_date: {
				Dialogs.createDateDialog(getActivity(), new OnDateSetListener() {
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						mCalendar.set(year, monthOfYear, dayOfMonth);
						
						new LoadData().execute(mCalendar.getTime());	
					}
				}, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();
			} break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private static class ViewHolder {
		LinearLayout layout1;
		ImageView image1;
		ImageView image2;
		TextView text1;
		TextView text2;
		TextView text3;
		TextView text4;
		TextView text5;
	}
	
	private class VisitAdapter extends BaseAdapter {
		
		private Typeface mTypeface;

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return Long.parseLong(mData.get(position).get("visit_id"));
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (mTypeface == null) {
				mTypeface = Fonts.getInstance(getActivity()).getTypeface("RobotoCondensed-Light");
			}
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.visit_item, null);
				  
				holder = new ViewHolder();
				holder.layout1 = (LinearLayout) convertView.findViewById(R.id.visit_is_not_complete); 
				holder.image1 = (ImageView) convertView.findViewById(R.id.visit_outlet_golden_mark);
				holder.image2 = (ImageView) convertView.findViewById(R.id.details);
				holder.text1 = (TextView) convertView.findViewById(R.id.visit_outlet_legal_name);
				holder.text2 = (TextView) convertView.findViewById(R.id.visit_outlet_address);
				holder.text3 = (TextView) convertView.findViewById(R.id.visit_start_time_first);
				holder.text4 = (TextView) convertView.findViewById(R.id.visit_outlet_start_time);
				holder.text5 = (TextView) convertView.findViewById(R.id.visit_outlet_end_time);
				
				holder.text1.setTypeface(mTypeface); 
				holder.text2.setTypeface(mTypeface); 
				holder.text3.setTypeface(mTypeface); 
				holder.text4.setTypeface(mTypeface); 
				holder.text5.setTypeface(mTypeface); 
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.text1.setText((String) mData.get(position).get("customer_legal_name"));
			holder.text2.setText((String) mData.get(position).get("customer_legal_address"));
			holder.text3.setText((String) mData.get(position).get("visit_start_time").substring(0, 5));
			if (mData.get(position).get("visit_outlet_golden_mark") != null && (mData.get(position).get("visit_outlet_golden_mark")).equals("\"Золотой\" заказчик P&G")) {
				holder.image1.setVisibility(View.VISIBLE);
			} else {
				holder.image1.setVisibility(View.INVISIBLE);
			}
			 
			String startTime = (String) mData.get(position).get("visit_start_time").substring(0, 5);
			String endTime = (String) mData.get(position).get("visit_end_time");
			
			holder.layout1.setVisibility(View.GONE);
	    	if (startTime != null && endTime != null) {
	    		if ((!startTime.equals("00:00") || !endTime.equals("00:00"))) {
	    			holder.text4.setText(startTime);
	    			holder.text5.setText(endTime.substring(0, 5));
	        		holder.layout1.setVisibility(View.VISIBLE);
	    		}
	    	}
			
	    	Long storeId = Long.valueOf(mData.get(position).get("store_id"));
	    	
	    	holder.image2.setTag(storeId);
	    	holder.image2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) { 
					Dialogs.createDialog(getActivity(), "", "", new OutletView(getActivity(), (Long) view.getTag()), Command.NO_OP, null).show();
				}
			});
	    	
			return convertView;
		}
		
	}
	
	private class LoadData extends AsyncTask<Object, Void, Object> {
		 
		private static final String query = 
			" select " +
				" convert(varchar(20), t.StartDate, 14) as StartTime, " +
				" convert(varchar(20), t.EndDate, 14) as EndTime, " +
				" cast(t.StartDate as date) as StartDate, " +
				" t.IsCompleted, " +
				" o.Descr, " +
				" o.Address, " + 
				" o.LocationLat, " +
				" o.LocationLon, " +
				" t.Id, " + 
				" s.Descr, " + 
				" o.Id " +
			" from " +
				" TaskVisitJournal as t " +
				" inner join RefOutlet as o on " +
					" t.Outlet = o.Id " +
				" left join RefStoreType as s on " +
					" o.StoreType = s.Id " +
			" where " +
				" t.IsMark = 0 " +
				" and o.IsMark = 0 " + 
				" and DateDiff(day, StartDate, ?) = 0 " +
			" order by " +
				" t.StartDate asc ";
		
		@Override
		protected void onPreExecute() {
			setListShown(false);
		}
		
		@Override
		protected Void doInBackground(Object... params) {
			mData.clear();
			
			UltraliteCursor cursor = new UltraliteCursor(getActivity(), query);
			cursor.setParametersAndExecute(params);
			
			while (cursor.moveToNext()) {
				Map<String, String> item = new HashMap<String, String>();
				item.put("customer_legal_name", cursor.getString(5));
				item.put("customer_legal_address", Text.prepareAddress(cursor.getString(6)));
				item.put("visit_start_time", cursor.getString(1));
				item.put("visit_outlet_golden_mark", cursor.getString(10));
				item.put("visit_id", cursor.getString(9));
				item.put("visit_end_time", cursor.getString(2));
				item.put("store_id", String.valueOf(cursor.getLong(11)));
				mData.add(item); 
			}
			
			cursor.close();
			cursor = null;
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) {
			mAdapter.notifyDataSetChanged();
			setListShown(true);
		}
		
	}
}
