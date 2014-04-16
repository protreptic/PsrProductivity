package ru.magnat.sfs.promo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.bom.order.DocOrderEntity;
import ru.magnat.sfs.bom.order.DocOrderLineEntity;
import ru.magnat.sfs.bom.ref.promo.PromoType;
import ru.magnat.sfs.database.UltraliteCursor;
import ru.magnat.sfs.money.Money;
import ru.magnat.sfs.widget.ExpandableListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Switch;
import android.widget.TextView;

public class PromotionSummaryReport extends ExpandableListFragment {
	
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	
	private List<Map<String, Object>> mGroupData = new ArrayList<Map<String, Object>>();
	private List<List<Map<String, Object>>> mChildData = new ArrayList<List<Map<String, Object>>>();
	
	private PromotionSummaryReportAdapter mAdapter;
	private ExpandableListView mExpandableListView;
	
	private DocOrderEntity mOrderEntity;
	
	private View mHeaderView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.expandable_list_view, container, false);
	}
	
	private Typeface mTypeface;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mContext = getActivity();
		
		mTypeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/RobotoCondensed-Regular.ttf");
		
		mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mHeaderView = mLayoutInflater.inflate(R.layout.promotion_summary_header_view, null);

		TextView label1 = (TextView) mHeaderView.findViewById(R.id.textView1);
		TextView title = (TextView) mHeaderView.findViewById(R.id.textView2);
		label1.setTypeface(mTypeface);
		title.setTypeface(mTypeface);
		
		TextView bonus = (TextView) mHeaderView.findViewById(R.id.totals);
		bonus.setTypeface(mTypeface);
		
		mOrderEntity = MainActivity.getInstance().mCurrentOrder;
		
		SparseArray<Float> totals1 = mOrderEntity.getPromosTotals(1);
		float amount1 = totals1.get(DocOrderEntity.PROMO_AMOUNT);
		
		SparseArray<Float> totals2 = mOrderEntity.getPromosTotals(2);
		float amount2 = totals2.get(DocOrderEntity.PROMO_AMOUNT);
		
		SparseArray<Float> totals3 = mOrderEntity.getPromosTotals(3);
		float amount3 = totals3.get(DocOrderEntity.PROMO_AMOUNT);
		
		SparseArray<Float> totals4 = mOrderEntity.getPromosTotals(4);
		float amount4 = totals4.get(DocOrderEntity.PROMO_AMOUNT);
		
		bonus.setText(Money.valueOf(amount1 + amount2 + amount3 + amount4).toSymbolString());
		
		
		
		mAdapter = new PromotionSummaryReportAdapter();
		mExpandableListView = getExpandableListView();
		mExpandableListView.addHeaderView(mHeaderView, null, false);
		mExpandableListView.setAdapter(mAdapter);
		
		
		
		new LoadData().execute();
	}
	
	private static class GroupViewHolder {
		TextView text1;
		TextView text2;
		TextView text3;

		Switch switch1;
	}

	private static class ChildViewHolder {
		TextView text1;
		TextView text2;
		TextView text3;
	}
	
	private class PromotionSummaryReportAdapter extends BaseExpandableListAdapter {
	
		private LayoutInflater mLayoutInflater;
		private Typeface mTypeface;
		private Calendar mStartDateOfPromo;
		private Calendar mEndDateOfPromo;
		private SimpleDateFormat mDateFormat;
		
		public PromotionSummaryReportAdapter() {
			mTypeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/RobotoCondensed-Regular.ttf");
			mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			mStartDateOfPromo = Calendar.getInstance(new Locale("ru", "RU"));
			mEndDateOfPromo = Calendar.getInstance(new Locale("ru", "RU"));
			mDateFormat = new SimpleDateFormat("dd MMMMM", new Locale("ru", "RU"));
		}
		
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return mChildData.get(groupPosition).get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0; 
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			ChildViewHolder holder;
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(R.layout.summary_list_item, null);
				
				holder = new ChildViewHolder();
				holder.text1 = (TextView) convertView.findViewById(R.id.summary_product_item_description);
				holder.text1.setTypeface(mTypeface);
				holder.text2 = (TextView) convertView.findViewById(R.id.summary_product_item_amount);
				holder.text2.setTypeface(mTypeface);
				holder.text3 = (TextView) convertView.findViewById(R.id.summary_description);
				holder.text3.setTypeface(mTypeface);
				
				convertView.setTag(holder);
			} else {
				holder = (ChildViewHolder) convertView.getTag();
			}
			
			Map<String, Object> item = mChildData.get(groupPosition).get(childPosition);
			
			holder.text1.setText((String) item.get("summary_product_item_description"));
			holder.text2.setText((String) item.get("summary_product_item_amount"));
			holder.text3.setText((String) item.get("summary_description"));
			
			return convertView;
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
		public int getGroupCount() {			
			return mGroupData.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return (Long) mGroupData.get(groupPosition).get("id"); 
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			GroupViewHolder holder;
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(R.layout.promo_list_item, null);
				
				holder = new GroupViewHolder();
				holder.text1 = (TextView) convertView.findViewById(R.id.promo_action_type);
				holder.text2 = (TextView) convertView.findViewById(R.id.promo_action_description);
				holder.text3 = (TextView) convertView.findViewById(R.id.promo_action_apply_date);
				holder.text1.setTypeface(mTypeface);
				holder.text2.setTypeface(mTypeface);
				holder.text3.setTypeface(mTypeface);
				
				holder.switch1 = (Switch) convertView.findViewById(R.id.switch1);
				
				convertView.setTag(holder);
			} else {
				holder = (GroupViewHolder) convertView.getTag();
			}
			
			holder.switch1.setVisibility(View.INVISIBLE);

			SparseArray<Float> totals = mOrderEntity.getPromoTotals((Long) mGroupData.get(groupPosition).get("id"));
			
			int soldPromotions = Math.round(totals.get(DocOrderEntity.PROMO_SOLD));
			float amount = totals.get(DocOrderEntity.PROMO_AMOUNT);
			
			//holder.text1.setText((String) mGroupData.get(groupPosition).get("type"));
			if (mGroupData.get(groupPosition).get("type").equals("Снижение цены")) {
				holder.text1.setTextColor(getResources().getColor(android.R.color.holo_orange_light));
				holder.text1.setText((String) mGroupData.get(groupPosition).get("type") + " / " + Money.valueOf(amount).toSymbolString());
			}
			if (mGroupData.get(groupPosition).get("type").equals("Купи и получи")) {
				holder.text1.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
				holder.text1.setText((String) mGroupData.get(groupPosition).get("type") + " / " + Money.valueOf(amount).toSymbolString() + " / X" + soldPromotions);
			}
			if (mGroupData.get(groupPosition).get("type").equals("Купи и получи (на период)")) {
				holder.text1.setTextColor(getResources().getColor(android.R.color.holo_green_light));
				holder.text1.setText((String) mGroupData.get(groupPosition).get("type") + " / " + Money.valueOf(amount).toSymbolString());
			}
			if (mGroupData.get(groupPosition).get("type").equals("Купи и получи (объемная)")) {
				holder.text1.setTextColor(getResources().getColor(android.R.color.holo_purple));
				holder.text1.setText((String) mGroupData.get(groupPosition).get("type") + " / " + Money.valueOf(amount).toSymbolString());
			}
			
			holder.text2.setText((String) mGroupData.get(groupPosition).get("description"));
			
			mStartDateOfPromo.setTime((Date) mGroupData.get(groupPosition).get("start_date"));
			String startDateString = mDateFormat.format(mStartDateOfPromo.getTime()); 
			mEndDateOfPromo.setTime((Date) mGroupData.get(groupPosition).get("end_date"));
			String endDateString = mDateFormat.format(mEndDateOfPromo.getTime());

			String line2 = "действует с " + startDateString + ((mStartDateOfPromo.get(Calendar.YEAR) != mEndDateOfPromo.get(Calendar.YEAR)) ? " " + mStartDateOfPromo.get(Calendar.YEAR) + " года" : "") + " по " + endDateString + " " + mEndDateOfPromo.get(Calendar.YEAR) + " года";
			
			holder.text3.setText(line2); 
			
			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}
		
	}
	
	private class LoadData extends AsyncTask<Object, Void, Void> {

		/**
		 * 
		 */
		private static final String SUMMARY_DESCRIPTION = "summary_description";

		/**
		 * 
		 */
		private static final String SUMMARY_PRODUCT_ITEM_AMOUNT = "summary_product_item_amount";

		/**
		 * 
		 */
		private static final String SUMMARY_PRODUCT_ITEM_DESCRIPTION = "summary_product_item_description";

		private String mGroupQuery = 
				" select " +
					" a.Promo as id, " + 
					" c.DescriptionSfa, " +
					" c.PromoType, " +
					" b.StartOfPromo, " +
					" b.EndOfPromo, " +
					" a.IsAvailable " +
				" from " +
					" RegPromoAvailability as a  " +
					" inner join RefPromoDetails as b on " +
						" a.Promo = b.Id " +
					" inner join RefPromo as c on " +
						" b.ParentExt = c.Id " +
				" where " +
					" ? between b.StartOfPromo and b.EndOfPromo " +
					" and b.IsMark = 0 " +
					" and a.Outlet = ? " +
					" and a.IsAvailable is not null " + 
				" order by " + 
					" c.PromoType desc";
		
		private ProgressDialog mProgressDialog;
		
		public LoadData() {
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setMessage(getResources().getString(R.string.data_loading)); 
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.setCancelable(false);
		}
		
		@Override
		protected void onPreExecute() {
			mProgressDialog.show();
		}
		
		@Override
		protected Void doInBackground(Object... params) {
			mGroupData.clear();
			mChildData.clear();
			
			UltraliteCursor cursor = new UltraliteCursor(mContext, mGroupQuery);
			cursor.setParametersAndExecute(mOrderEntity.ShipmentDate, mOrderEntity.Outlet.Id);
			
			while (cursor.moveToNext()) {
				Long promotionid = cursor.getLong(1);
				List<Map<String, Object>> childDataItem = new ArrayList<Map<String, Object>>();
				HashMap<Long, Set<DocOrderLineEntity>> summary = mOrderEntity.getPromoBonuses();
				
				Set<DocOrderLineEntity> lines = summary.get(promotionid);
				if (lines == null) continue;
				for (DocOrderLineEntity line : lines) {
					Map<String, Object> item = new HashMap<String, Object>();
					item.put(SUMMARY_PRODUCT_ITEM_DESCRIPTION, line.ProductItem.Descr);
					String[] units = {"шт.","кор.","бл."};
					if (line.Quantity != 0) {
						item.put(SUMMARY_PRODUCT_ITEM_AMOUNT, line.Quantity + " "+units[line.UnitLevel]);
					} else {
						item.put(SUMMARY_PRODUCT_ITEM_AMOUNT, "");
					}

					String description = "";
					switch (cursor.getInt(3)) {
						case PromoType.PRICING: {
							description = "Бонус: " + Money.valueOf(line.Bonus1Amount).toSymbolString() + " Скидка: " + line.Bonus1Discount + "%";
						} break;
						case PromoType.BAG_ORDER: {
							if (line.Bonus2Amount == 0) {
								continue; // Показываем только товары которые из бонуса
							}
							if (line.Bonus2Discount < 100) {
								description = "Бонус: " + Money.valueOf(line.Bonus2Amount).toSymbolString() + " (cкидка " + line.Bonus2Discount + "% на " + line.Bonus2Count + " шт.)";
							} else {
								description = "Бонус: " + Money.valueOf(line.Bonus2Amount).toSymbolString() + " (бесплатно " + line.Bonus2Count + " шт.)";
							}
						} break;
						case PromoType.BAG_PERIOD: {
							// TODO Добавить описание для этой акции
							description = "Сумма продажи: " + Money.valueOf(line.Amount).toSymbolString();
						} break;
						case PromoType.BAG_VOLUME: {
							description = "Сумма продажи: " + Money.valueOf(line.Amount).toSymbolString();
						} break;
						default: {
							
						} break;
					}
					
					item.put(SUMMARY_DESCRIPTION, description);
					
					childDataItem.add(item);
				}
				mChildData.add(childDataItem);
				
				Map<String, Object> group = new HashMap<String, Object>();
				group.put("id", cursor.getLong(1));
				group.put("description", cursor.getString(2));
				group.put("type", new PromoType(cursor.getInt(3)).toString());
				group.put("start_date", cursor.getDate(4));
				group.put("end_date", cursor.getDate(5));
				group.put("available", cursor.getBoolean(6));
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
		}
	}
	
}
