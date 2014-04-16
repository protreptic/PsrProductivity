package ru.magnat.sfs.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import ru.magnat.sfs.android.Dialogs; 
import ru.magnat.sfs.android.OnBackPressedListener;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.query.measures.DocTprMeasureEntity;
import ru.magnat.sfs.bom.query.measures.DocTprMeasureJournal;
import ru.magnat.sfs.bom.task.visit.TaskVisitEntity;
import ru.magnat.sfs.database.UltraliteCursor;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.util.Loader;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class TprMeasuresView extends SfsContentView implements OnItemClickListener {
	
	private List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
	private OrderListAdapter mAdapter;
	private OnClickListener mDocumentNew = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			createTprMeasure(MainActivity.getInstance().mCurrentVisit);
		}
	};
	private OnClickListener mDocumentDate = new OnClickListener() {
		
		private Calendar mCalendar = Calendar.getInstance(new Locale("ru", "RU")); 
		private DateTime mNewDate;
		private DateTime mOldDate = new DateTime();
		
		@Override
		public void onClick(View v) {
			Dialogs.createDateDialog(getContext(), new OnDateSetListener() {
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					mCalendar.set(year, monthOfYear, dayOfMonth);
					mNewDate = new DateTime(mCalendar.getTimeInMillis());						
					if (!mNewDate.toString(DateTimeFormat.forPattern("dd.MM.yyyy")).equals(mOldDate.toString(DateTimeFormat.forPattern("dd.MM.yyyy")))) {						
						mOldDate = mNewDate;
						new LoadData(getContext()).execute(mNewDate.toString(DateTimeFormat.forPattern("yyyy-MM-dd")));
					}
				}
			}, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();
		}
	};
	
	public TprMeasuresView(Context context, boolean editMode) {
		super(context);
		
		mAdapter = new OrderListAdapter();
		
		layoutInflater.inflate(R.layout.document_layout, this);
		
		ImageButton dateButton = (ImageButton) findViewById(R.id.document_date);
		dateButton.setOnClickListener(mDocumentDate);

		ImageButton newButton = (ImageButton) findViewById(R.id.document_new);
		newButton.setOnClickListener(mDocumentNew); 
		
		if (editMode == false) {
			newButton.setVisibility(View.INVISIBLE); 
		}

		ListView listView = (ListView) findViewById(R.id.list); 
		listView.setAdapter(mAdapter); 
		listView.setOnItemClickListener(this);
		
		new LoadData(context).execute(new DateTime(System.currentTimeMillis()).toString(DateTimeFormat.forPattern("yyyy-MM-dd")));
	}
	
	private static class ViewHolder {
		TextView text1;
		TextView text2;
		TextView text3;
		ImageView image1;
	}
	 
	private class LoadData extends Loader {

		private static final String query = 
			" select " +
				" a.Id, " +
				" a.IsMark as draft, " +
				" b.Descr as customer_legal_name, " +
				" b.Address as customer_address, " +
				" a.CreateDate as create_date, " +
				" a.IsAccepted as is_accepted " +
		    " from " +
		    	" DocTprMeasureJournal as a " +
		    	" inner join RefOutlet as b on " +
		    		" a.Outlet = b.Id" +
		    " where " +
	    		" datediff(day, a.CreateDate, ?) = 0 " +
	    	" order by " +
	    		" a.CreateDate desc ";
		
		public LoadData(Context context) {
			super(context);
		}
		
		@Override
		protected Void doInBackground(Object... params) {
			mData.clear();
			
			UltraliteCursor cursor = new UltraliteCursor(getContext(), query);
			cursor.setParametersAndExecute(params);
			
			while (cursor.moveToNext()) {
				Map<String, Object> item = new HashMap<String, Object>();
				item.put("document_id", cursor.getLong(1));
				item.put("draft", cursor.getBoolean(2));
				item.put("customer_legal_name", cursor.getString(3));
				item.put("customer_address", cursor.getString(4));
				item.put("create_date", cursor.getDate(5));
				item.put("is_accepted", cursor.getBoolean(6));
				
				mData.add(item); 
			}
			
			cursor.close();
			cursor = null;
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) {
			mAdapter.notifyDataSetChanged();
			
			super.onPostExecute(result);
		}
		
	}

	private class OrderListAdapter extends BaseAdapter {

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
				convertView = layoutInflater.inflate(R.layout.marketing_measure_item, null);

				holder = new ViewHolder();
				holder.image1 = (ImageView) convertView.findViewById(R.id.status_icon);
				holder.text1 = (TextView) convertView.findViewById(R.id.number);
				holder.text2 = (TextView) convertView.findViewById(R.id.customer);
				holder.text3 = (TextView) convertView.findViewById(R.id.address);
				
				holder.text1.setTypeface(typeface);
				holder.text2.setTypeface(typeface);
				holder.text3.setTypeface(typeface);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			long documentId = (Long) mData.get(position).get("document_id");
			boolean isDraft = (Boolean) mData.get(position).get("draft");
			String legalName = (String) mData.get(position).get("customer_legal_name");
			String address = (String) mData.get(position).get("customer_address");
			Date createDate = (Date) mData.get(position).get("create_date");
			boolean isAccepted = (Boolean) mData.get(position).get("is_accepted");
			
			int documentStatusIcon = 0;			
			if (isDraft) {
				documentStatusIcon = R.drawable.draft;
			} else {
				if (isAccepted) {
					documentStatusIcon = R.drawable.approved; 
				} else {
					documentStatusIcon = R.drawable.not_sent; 
				}
			}
			
			holder.image1.setImageDrawable(getResources().getDrawable(documentStatusIcon));
			holder.text1.setText(String.format("N%d/%d от %s", Globals.getUser().Id, documentId, new SimpleDateFormat("dd.MM.yyyy", new Locale("ru", "RU")).format(createDate)));
			holder.text2.setText(legalName);
			holder.text3.setText(Utils.prepareAddress(address));
			
			return convertView;
		}
		
	}
	
	private void createTprMeasure(TaskVisitEntity visit) {
		DocTprMeasureJournal docs = new DocTprMeasureJournal(getContext());
		docs.NewEntity();
		DocTprMeasureEntity doc = docs.Current();
		
		doc.setDefaults(getContext(), visit);
		docs.save();
		
		SfsContentView v = docs.GetEditView(doc);
		docs.close();
		docs = null;		
		
		MainActivity.sInstance.addToFlipper(v, this.getClass().getSimpleName());
		MainActivity.sInstance.addOnBackPressedListener((OnBackPressedListener) v);
	}
	
	private void openGoldenMeasure(Long measureId) {	
		DocTprMeasureJournal docs = new DocTprMeasureJournal(getContext());
		DocTprMeasureEntity doc = docs.FindById(measureId);
		
		SfsContentView v = docs.GetEditView(doc);
		docs.close();
		docs = null;
		
		MainActivity.sInstance.addToFlipper(v, this.getClass().getSimpleName());
		MainActivity.sInstance.addOnBackPressedListener((OnBackPressedListener) v);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		openGoldenMeasure((Long) mData.get(position).get("document_id"));
	}
	
}
