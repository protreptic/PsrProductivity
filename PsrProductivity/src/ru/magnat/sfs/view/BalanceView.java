package ru.magnat.sfs.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.ref.contractor.RefContractorEntity;
import ru.magnat.sfs.database.UltraliteCursor;
import ru.magnat.sfs.money.Money;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class BalanceView extends SfsContentView {

	private ExpandableListView mExpandableListView;
	private List<Map<String, Object>> mGroupData = new ArrayList<Map<String, Object>>();
	private List<List<Map<String, Object>>> mChildData = new ArrayList<List<Map<String, Object>>>();
	private BalanceAdapter mAdapter;
	
	public BalanceView(Context context) {
		super(context);

		init();
	}

	public BalanceView(Context context, RefContractorEntity entity) {
		super(context);
		
		init();
	}
	
	private void init() {
		layoutInflater.inflate(R.layout.expandable_list_layout, this); 
		
		mAdapter = new BalanceAdapter();
		
		mExpandableListView = (ExpandableListView) findViewById(R.id.expandable_list);	
		mExpandableListView.setAdapter(mAdapter); 
		
		new LoadData().execute();
	}
	
	private static class GroupViewHolder {
		TextView text1;
		TextView text2;
		TextView text3;
		TextView text4;
	}
	
	private static class ChildViewHolder {
		TextView text1;
		TextView text2;
		TextView text3;
		TextView text4;
		TextView text5;
		TextView text6;
	}
	
	private class BalanceAdapter extends BaseExpandableListAdapter {

		@Override
		public int getGroupCount() {
			return mGroupData.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return mChildData.get(groupPosition).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return mGroupData.get(groupPosition); 
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return mChildData.get(groupPosition).get(childPosition); 
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
				convertView = layoutInflater.inflate(R.layout.balance_total_item_view1, null);
				
				holder = new GroupViewHolder();
				holder.text1 = (TextView) convertView.findViewById(R.id.customer_name);
				holder.text2 = (TextView) convertView.findViewById(R.id.amount_take);
				holder.text3 = (TextView) convertView.findViewById(R.id.amount_debt);
				holder.text4 = (TextView) convertView.findViewById(R.id.amount_delayed);
				
				holder.text1.setTypeface(typeface);
				holder.text2.setTypeface(typeface);
				holder.text3.setTypeface(typeface);
				holder.text4.setTypeface(typeface);
				
				convertView.setTag(holder);
			} else {
				holder = (GroupViewHolder) convertView.getTag();
			}
			
			holder.text1.setText((String) mGroupData.get(groupPosition).get("customer_name"));
			holder.text2.setText((String) mGroupData.get(groupPosition).get("collect_debt"));
			holder.text3.setText((String) mGroupData.get(groupPosition).get("debt"));
			holder.text4.setText((String) mGroupData.get(groupPosition).get("amount"));
			
			return convertView;
		}
		
		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			ChildViewHolder holder;
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.balance_detail_item_view, null);
				
				holder = new ChildViewHolder();
				holder.text1 = (TextView) convertView.findViewById(R.id.balance_doc);
				holder.text2 = (TextView) convertView.findViewById(R.id.balance_type);
				holder.text3 = (TextView) convertView.findViewById(R.id.balance_term);
				holder.text4 = (TextView) convertView.findViewById(R.id.balance_debt);
				holder.text5 = (TextView) convertView.findViewById(R.id.balance_shipped);
				holder.text6 = (TextView) convertView.findViewById(R.id.balance_sum);
				
				holder.text1.setTypeface(typeface);
				holder.text2.setTypeface(typeface);
				holder.text3.setTypeface(typeface);
				holder.text4.setTypeface(typeface);
				holder.text5.setTypeface(typeface);
				holder.text6.setTypeface(typeface);
				
				convertView.setTag(holder);
			} else {
				holder = (ChildViewHolder) convertView.getTag();
			}
			
			holder.text1.setText((String) mChildData.get(groupPosition).get(childPosition).get("document_number"));
			holder.text2.setText((String) mChildData.get(groupPosition).get(childPosition).get("payment_type"));
			holder.text3.setText((String) mChildData.get(groupPosition).get(childPosition).get("payment_date"));
			holder.text4.setText((String) mChildData.get(groupPosition).get(childPosition).get("debt"));
			holder.text5.setText((String) mChildData.get(groupPosition).get(childPosition).get("document_date"));
			holder.text6.setText((String) mChildData.get(groupPosition).get(childPosition).get("amount"));

			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}

	}
	
	private class LoadData extends AsyncTask<Object, Void, Void> {

		private ProgressDialog mProgressDialog;
		
		private static final String query1 = 
			" select " +
				" c.Id as customer_id, " +
				" c.Descr as customer_name, " +
				" r.CollectDebt, " +
				" r.Debt, " +
				" r.DocSum " +
			" from " +
				" RegBalance as r " +
				" inner join RefContractor as c on " +
					" r.Contractor = c.Id " +
			" where " + 
				" coalesce(r.PaymentType, 0) = 0 " +
				" and coalesce(r.DocSum, 0) > 0 " +
			" order by " +
				" c.Descr asc ";
		
		private static final String query2 = 
			" select " +
				" r.CreditInfo, " +
				" coalesce(pt.Descr, '') as payment_type, " +
				" coalesce(r.Debt, 0) as debt, " +
				" coalesce(r.DocSum, 0) as amount, " +
				" r.PaymentData, " +
				" r.DocDate " +
			" from " +
				" RegBalance as r " +
				" left join RefPaymentType as pt on " +
					" r.PaymentType = pt.Id " +
			" where " + 
				" coalesce(r.Contractor, 0) = ? " +
				" and coalesce(r.PaymentType, 0) <> 0 " +
				" and coalesce(r.DocSum, 0) <> 0 " +
			" order by " +
				" PaymentData asc ";
		
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
		
		@Override
		protected Void doInBackground(Object... params) {
			// Очистить данные в источнике
			mGroupData.clear();
			mChildData.clear();
			
			UltraliteCursor cursor = new UltraliteCursor(getContext(), query1);
			cursor.setParametersAndExecute(new Object[] {});
			
			while (cursor.moveToNext()) {			
				UltraliteCursor cursor2 = new UltraliteCursor(getContext(), query2);
				cursor2.setParametersAndExecute(cursor.getLong(1)); 
				
				List<Map<String, Object>> childDataItem = new ArrayList<Map<String, Object>>();
				while (cursor2.moveToNext()) {
					Map<String, Object> item = new HashMap<String, Object>();
					item.put("document_number", cursor2.getString(1));
					item.put("payment_type", cursor2.getString(2));
					item.put("debt", Money.valueOf(cursor.getFloat(3)).toSymbolString());
					item.put("amount", Money.valueOf(cursor.getFloat(4)).toSymbolString());
					
					Date paymentDate = cursor2.getDate(5);
					Date documentDate = cursor2.getDate(6);
					
					SimpleDateFormat format = new SimpleDateFormat("dd.MM", new Locale("ru", "RU"));
					
					if (paymentDate == null) {
						item.put("payment_date", "");
					} else {
						item.put("payment_date", format.format(paymentDate)); 
					}
					if (documentDate == null) {
						item.put("document_date", "");
					} else {
						item.put("document_date", format.format(documentDate)); 
					}
					
					childDataItem.add(item);
				}
				
				mChildData.add(childDataItem);
				
				cursor2.close();
				cursor2 = null;
				
				Map<String, Object> group = new HashMap<String, Object>();
				group.put("customer_id", cursor.getLong(1));
				group.put("customer_name", cursor.getString(2));
				group.put("collect_debt", Money.valueOf(cursor.getFloat(3)).toSymbolString());
				group.put("debt", Money.valueOf(cursor.getFloat(4)).toSymbolString());
				group.put("amount", Money.valueOf(cursor.getFloat(5)).toSymbolString());
				
				mGroupData.add(group); 
			}
			
			cursor.close();
			cursor = null;
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mAdapter.notifyDataSetChanged();
			
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}
	
}
