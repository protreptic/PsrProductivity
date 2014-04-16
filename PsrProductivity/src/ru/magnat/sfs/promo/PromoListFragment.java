package ru.magnat.sfs.promo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ru.magnat.sfs.android.OnBackPressedListener;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.order.DocOrderEntity;
import ru.magnat.sfs.bom.order.OnOrderChangedListener.OnAmountChangedListener;
import ru.magnat.sfs.bom.ref.promo.PromoType;
import ru.magnat.sfs.database.UltraliteCursor;
import ru.magnat.sfs.money.Money;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

public class PromoListFragment extends ListFragment implements OnItemClickListener, OnAmountChangedListener,OnBackPressedListener {
	
	private Context mContext;
	private UltraliteCursor mCursor;
	private PromoListAdapter mAdapter;
	private String mQuery = 
		" select distinct " +
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
			" datediff(day,b.StartOfPromo,?)>=0 " +
			" and datediff(day,?,b.EndOfPromo)>=0 " +
			" and b.IsMark = 0 " +
			" and a.Outlet = ? " +
			" and c.PromoType = ? " +
			" and a.IsAvailable is not null " + 
		" order by " + 
			" c.PromoType desc";
	private List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
	private Long mStoreId;
	private Long mPromotionType;
	private DocOrderEntity mOrder;
	
	private LoadData mLoadData;
	private Date mOnDate;

	@Override
	public void onDetach() {
		mLoadData.cancel(true);
		super.onDetach();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mContext = getActivity();
		mAdapter = new PromoListAdapter(mContext, mData);
		mStoreId = getArguments().getLong("storeId");
		mPromotionType = getArguments().getLong("promoType");
		mStoreId = getArguments().getLong("store_id");
		mPromotionType = getArguments().getLong("promotion_id");
		long ondate = getArguments().getLong("on_date");
		if (ondate==0)
			mOnDate = new Date();
		else
			mOnDate = new Date(ondate);
		mOrder = MainActivity.getInstance().mCurrentOrder;
		mOrder.addOnAmountChangedListener(this);
		
		setPromotionStateListener(mOrder);
		
		setEmptyText("Нет доступных акций");
		setListShown(false);
		setHasOptionsMenu(true);
		setListAdapter(mAdapter);
		getListView().setOnItemClickListener(this);
		mLoadData = new LoadData();
		mLoadData.execute(mOnDate, mOnDate, mStoreId, mPromotionType);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		PromotionForm fragment = new PromotionForm();
		Bundle bundle = new Bundle();
		String tag = Integer.toString(fragment.hashCode());
		bundle.putLong("promotion_id", (Long) mData.get(position).get("id"));
		fragment.setArguments(bundle);
		fragment.setPromotionStateListener(mPromotionStateListener);
		transaction.replace(R.id.fragment_container, fragment,tag);
		transaction.addToBackStack(tag);
		transaction.commit();
	}
	
	private static class ViewHolder {
		TextView text1;
		TextView text2;
		TextView text3;
		
		Switch switch1;
		
		ProgressBar progress1;
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	    menu.setHeaderTitle("");
	    menu.add(0, 1, 0, "Отключить");
	}
	
	private class PromoListAdapter extends BaseAdapter {
		
		private static final String FIELD_TYPE = "type";
		private static final String FIELD_DESCRIPTION = "description";
		private static final String FIELD_END_DATE = "end_date";
		private static final String FIELD_START_DATE = "start_date";
		private static final String FIELD_ID = "id";
		private static final String FIELD_TYPE_ID = "typeId";
		private static final String FIELD_AVAILABLE = "available";
		private LayoutInflater mLayoutInflater;
		private List<Map<String, Object>> mData;
		private Typeface mTypeface;
		private Calendar mStartDateOfPromo;
		private Calendar mEndDateOfPromo;
		private SimpleDateFormat mDateFormat;
		
		public PromoListAdapter(Context context, List<Map<String, Object>> data) {
			mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoCondensed-Regular.ttf");
			mData = data;
			mStartDateOfPromo = Calendar.getInstance(new Locale("ru", "RU"));
			mEndDateOfPromo = Calendar.getInstance(new Locale("ru", "RU"));
			mDateFormat = new SimpleDateFormat("dd MMMMM", new Locale("ru", "RU"));
		}
		
		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return mData.get(position).get(FIELD_ID);
		} 

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(R.layout.promo_list_item, null);
				
				holder = new ViewHolder();
				holder.text1 = (TextView) convertView.findViewById(R.id.promo_action_type);
				holder.text2 = (TextView) convertView.findViewById(R.id.promo_action_description);
				holder.text3 = (TextView) convertView.findViewById(R.id.promo_action_apply_date);
				holder.text1.setTypeface(mTypeface);
				holder.text2.setTypeface(mTypeface);
				holder.text3.setTypeface(mTypeface);
				
				holder.switch1 = (Switch) convertView.findViewById(R.id.switch1); 
				
				holder.progress1 = (ProgressBar) convertView.findViewById(R.id.promo_action_progress);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.progress1.setVisibility(View.GONE);

			Map<String, Object> currentItem = mData.get(position);
			if ((Integer)currentItem.get(FIELD_TYPE_ID)!=PromoType.PRICING) {
				updateProgress(mContext, mOrder, holder.progress1, (Long) currentItem.get(FIELD_ID));
			}
			
			holder.text1.setText((String) currentItem.get(FIELD_TYPE));
			switch ((Integer)currentItem.get(FIELD_TYPE_ID)){
				case PromoType.PRICING:
					holder.text1.setTextColor(getResources().getColor(android.R.color.holo_orange_light));
					break;
				case PromoType.BAG_ORDER:
					holder.text1.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
					break;
				case PromoType.BAG_VOLUME:
					holder.text1.setTextColor(getResources().getColor(android.R.color.holo_purple));
					break;
				case PromoType.BAG_PERIOD:
					holder.text1.setTextColor(getResources().getColor(android.R.color.holo_green_light));
					break;
			}
			holder.text2.setText((String) currentItem.get(FIELD_DESCRIPTION));
			
			mStartDateOfPromo.setTime((Date) currentItem.get(FIELD_START_DATE));
			String startDateString = mDateFormat.format(mStartDateOfPromo.getTime()); 
			mEndDateOfPromo.setTime((Date) currentItem.get(FIELD_END_DATE));
			String endDateString = mDateFormat.format(mEndDateOfPromo.getTime());
			
			SparseArray<Float> totals = MainActivity.getInstance().mCurrentOrder.getPromoTotals((Long) currentItem.get(FIELD_ID));
			
			int soldPromotions = Math.round(totals.get(DocOrderEntity.PROMO_SOLD));
			float amount = totals.get(DocOrderEntity.PROMO_AMOUNT);
			
			String line2 = "действует с " + startDateString + ((mStartDateOfPromo.get(Calendar.YEAR) != mEndDateOfPromo.get(Calendar.YEAR)) ? " " + mStartDateOfPromo.get(Calendar.YEAR) + " года" : "") + " по " + endDateString + " " + mEndDateOfPromo.get(Calendar.YEAR) + " года, " +
			" бонус: " + Money.valueOf(amount).toSymbolString() + ((((Integer) currentItem.get(FIELD_TYPE_ID))==PromoType.PRICING) ? "" : ", кратность: " + soldPromotions);
			
			holder.text3.setText(line2);	
			
			holder.switch1.setChecked((Boolean) currentItem.get(FIELD_AVAILABLE));
			holder.switch1.setTag((Long) currentItem.get(FIELD_ID));
			holder.switch1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					
					if (mPromotionStateListener != null) {
						mPromotionStateListener.onPromotionAvailabilityChanged(isChecked, (Long) buttonView.getTag());
					}
				}
			});
			
			return convertView;
		}


	}
	public static void updateProgress(Context context, DocOrderEntity order, ProgressBar progressBar, long promoId) {
		int progress = order.getPromoProgress(promoId);
		int progress1 = progress/100;
		int progress2 = progress % 100;
		progressBar.setMax(100);
		progressBar.setProgress(progress2);
		
		if (progress == 0 ) {
			progressBar.setVisibility(View.GONE);
		} else {
			progressBar.setVisibility(View.VISIBLE);
			
			{
				if (progress2 < 50) 
						progressBar.setProgressDrawable((progress1 > 0)?
								context.getResources().getDrawable(R.drawable.progress_bar_primary_red_green)
								:context.getResources().getDrawable(R.drawable.progress_bar_primary_red)
								);
				else if (progress2 >= 50 && progress2 < 100) 
						progressBar.setProgressDrawable((progress1 > 0)?
								context.getResources().getDrawable(R.drawable.progress_bar_primary_yellow_green)
								:
								context.getResources().getDrawable(R.drawable.progress_bar_primary_yellow)
								);
				else if (progress2 >= 100) 
						progressBar.setProgressDrawable((progress1 > 0)?
								context.getResources().getDrawable(R.drawable.progress_bar_primary_green_green)
								:
								context.getResources().getDrawable(R.drawable.progress_bar_primary_green)
								);
			}
		
			
		}

	}	
	public void setPromotionStateListener(OnPromotionStateListener listener) {
		mPromotionStateListener = listener;
	}
	
	private OnPromotionStateListener mPromotionStateListener;
	
	private class LoadData extends AsyncTask<Object, Void, Void> {

		@Override
		protected void onPreExecute() {
			setListShown(false);
		}
		
		@Override
		protected Void doInBackground(Object... params) {
			mData.clear();
			
			mCursor = new UltraliteCursor(mContext, mQuery, params);
			mCursor.setParametersAndExecute(params);
			
			for (int i = 0; i < mCursor.getCount(); i++) {
				if (mCursor.moveToNext()) {
					Map<String, Object> item = new HashMap<String, Object>();
					item.put("id", mCursor.getLong(1));
					item.put("description", mCursor.getString(2));
					item.put("type", new PromoType(mCursor.getInt(3)).toString());
					item.put("start_date", mCursor.getDate(4));
					item.put("end_date", mCursor.getDate(5));
					item.put("available", mCursor.getBoolean(6));
					item.put("typeId", mCursor.getInt(3));
					mData.add(item);
				}
			}
			
			mCursor.close();
			mCursor = null;
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mAdapter.notifyDataSetChanged();
			setListShown(true);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void onAmountChanged(GenericEntity sender, float old_value, float new_value) {
		mAdapter.notifyDataSetChanged(); 
	}

	@Override
	public Boolean onBackPressed() {

		return true;
	}

}
