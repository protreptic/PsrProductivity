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

import ru.magnat.sfs.android.Command;
import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.OnBackPressedListener;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.android.MainActivity.SyncType;
import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.order.DocOrderEntity;
import ru.magnat.sfs.bom.order.DocOrderJournal;
import ru.magnat.sfs.bom.task.visit.TaskVisitEntity;
import ru.magnat.sfs.database.UltraliteCursor;
import ru.magnat.sfs.money.Money;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.doc.order.OrderCfrInfoView;
import ru.magnat.sfs.util.Loader;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class OrdersView extends SfsContentView implements OnItemClickListener, OnItemLongClickListener {

	private List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
	private OrderListAdapter mAdapter;
	private OnClickListener mDocumentNew = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			offerSync();
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
	
	public OrdersView(Context context, boolean editMode) {
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
		listView.setOnItemLongClickListener(this);  
		
		new LoadData(context).execute(new DateTime(System.currentTimeMillis()).toString(DateTimeFormat.forPattern("yyyy-MM-dd")));
	}
	
	private static class ViewHolder {
		TextView text1;
		TextView text2;
		TextView text3;
		TextView text4;
		TextView text5;
		TextView text6;
		ImageView image1;
	}
	 
	private class LoadData extends Loader {

		private static final String query = 
			" select " +
				" a.Id as document_id, " +
				" a.IsMark as draft, " +
				" b.Descr as customer_legal_name, " +
				" b.Address as customer_address, " +
				" a.ShipmentDate as shipment_date, " +
				" a.ShipmentTime as shipment_time, " +
				" a.CreateDate as create_date, " +
				" a.SaveDate as save_date, " +
				" a.AcceptDate as accept_date, " +
				" a.ImportDate as import_date, " +
				" a.ReceiveDate as sent_date, " +
				" a.CancelDate as cancel_date, " +
				" a.Cfr as cfr, " +
				" a.Amount as amount " +
		    " from " +
		    	" DocOrderJournal as a " +
		    	" inner join RefOutlet as b on " +
		    		" a.Outlet = b.Id " +
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
				item.put("shipment_date", cursor.getDate(5));
				item.put("shipment_time", cursor.getDate(6));
				item.put("create_date", cursor.getDate(7));
				item.put("save_date", cursor.getDate(8));
				item.put("accept_date", cursor.getDate(9));
				item.put("import_date", cursor.getDate(10));
				item.put("sent_date", cursor.getDate(11));
				item.put("cancel_date", cursor.getDate(12));
				item.put("cfr", cursor.getFloat(13));
				item.put("amount", cursor.getFloat(14));
				
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
				convertView = layoutInflater.inflate(R.layout.order_item, null);

				holder = new ViewHolder();
				holder.image1 = (ImageView) convertView.findViewById(R.id.status_icon);
				holder.text1 = (TextView) convertView.findViewById(R.id.number);
				holder.text2 = (TextView) convertView.findViewById(R.id.customer);
				holder.text3 = (TextView) convertView.findViewById(R.id.amount);
				holder.text4 = (TextView) convertView.findViewById(R.id.cfr);
				holder.text5 = (TextView) convertView.findViewById(R.id.shipment_date);
				holder.text6 = (TextView) convertView.findViewById(R.id.address);
				
				holder.text1.setTypeface(typeface);
				holder.text2.setTypeface(typeface);
				holder.text3.setTypeface(typeface);
				holder.text4.setTypeface(typeface);
				holder.text5.setTypeface(typeface);
				holder.text6.setTypeface(typeface);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			long documentId = (Long) mData.get(position).get("document_id");
			boolean isDraft = (Boolean) mData.get(position).get("draft");
			String legalName = (String) mData.get(position).get("customer_legal_name");
			String address = (String) mData.get(position).get("customer_address");
			Date shipmentDate = (Date) mData.get(position).get("shipment_date");
			//Date shipmentTime = (Date) mData.get(position).get("shipment_time");
			Date createDate = (Date) mData.get(position).get("create_date");
			//Date saveDate = (Date) mData.get(position).get("save_date");
			Date acceptDate = (Date) mData.get(position).get("accept_date");
			Date importDate = (Date) mData.get(position).get("import_date");
			Date sentDate = (Date) mData.get(position).get("sent_date");
			Date cancelDate = (Date) mData.get(position).get("cancel_date");
			Float cfr = (Float) mData.get(position).get("cfr");
			Float amount = (Float) mData.get(position).get("amount");
			
			int documentStatusIcon = 0;
			if (isDraft) {
				documentStatusIcon = R.drawable.draft;
			} else {
				if (cancelDate != null) {
					documentStatusIcon = R.drawable.canceled;
				} else if (acceptDate != null) {
					documentStatusIcon = R.drawable.approved;
				} else if (importDate != null) {
					documentStatusIcon = R.drawable.imported;
				} else if (sentDate != null) {
					documentStatusIcon = R.drawable.sent;
				} else {
					documentStatusIcon = R.drawable.not_sent;
				}
			}

			int cfrColor = 0;
			if (cfr > 0) {
				if (cfr < 0.95) {
					cfrColor = R.color.red;
				} else if (cfr < 0.98) {
					cfrColor = R.color.yellow;
				} else if (cfr < 1.01) {
					cfrColor = R.color.green;
				} else {
					cfrColor = R.color.yellow;
				}
			} else {
				cfrColor = R.color.white;
			}
			
			holder.image1.setImageDrawable(getResources().getDrawable(documentStatusIcon));
			holder.text1.setText(String.format("N%d/%d от %s", Globals.getUser().Id, documentId, new SimpleDateFormat("dd.MM.yyyy", new Locale("ru", "RU")).format(createDate)));
			holder.text2.setText(legalName);
			holder.text3.setText(Money.valueOf(amount).toSymbolString());
			holder.text4.setText(String.format("КУЗ: %,.0f%%", cfr * 100));
			holder.text4.setTextColor(getResources().getColor(cfrColor));
			holder.text5.setText(String.format("доставка на %s", new SimpleDateFormat("dd.MM.yyyy", new Locale("ru", "RU")).format(shipmentDate))); 
			holder.text6.setText(Utils.prepareAddress(address));
			
			return convertView;
		}
		
	}
	
	private void offerSync() {
		Dialogs.createDialog("SFS", "Провести обмен?", new Command() {
			
			@Override
			public void execute() {
				MainActivity.getInstance().synchronize(SyncType.REGULAR); 
			}
		}, new Command() {
			@Override
			public void execute() {
				createOrder(MainActivity.getInstance().mCurrentVisit);
			}
		}).show();
	}
	
	private void createOrder(TaskVisitEntity visit) {
		DocOrderJournal docs = new DocOrderJournal(getContext());
		docs.NewEntity();
		DocOrderEntity doc = docs.Current();
		doc.setDefaults(getContext(), visit);
		docs.save();
		
		SfsContentView v = docs.GetEditView(doc);
		docs.close();

		MainActivity.sInstance.addToFlipper(v, this.getClass().getSimpleName());
		MainActivity.sInstance.addOnBackPressedListener((OnBackPressedListener) v);
	}
	
	private void openOrder(Long orderId) {
		DocOrderJournal docs = new DocOrderJournal(getContext());
		DocOrderEntity doc = docs.FindById(orderId);
		
		SfsContentView v = docs.GetEditView(doc);
		docs.close();

		MainActivity.sInstance.addToFlipper(v, this.getClass().getSimpleName());
		MainActivity.sInstance.addOnBackPressedListener((OnBackPressedListener) v);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		openOrder((Long) mData.get(position).get("document_id"));
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		DocOrderJournal docOrderJournal = new DocOrderJournal(getContext());
		DocOrderEntity order = docOrderJournal.FindById((Long) mData.get(position).get("document_id"));
		docOrderJournal.close();
		docOrderJournal = null;
		
		Dialogs.createDialog("Удовлетворенность заказа", "", new OrderCfrInfoView(getContext(), order).inflate(), null, null, null).show();
		
		return true;
	}

}
