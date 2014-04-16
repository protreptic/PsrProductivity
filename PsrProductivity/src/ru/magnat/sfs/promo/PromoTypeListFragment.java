package ru.magnat.sfs.promo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.magnat.sfs.android.Command;
import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.InternalStorage;
import ru.magnat.sfs.bom.order.DocOrderEntity;
import ru.magnat.sfs.bom.order.OnOrderChangedListener.OnAmountChangedListener;
import ru.magnat.sfs.bom.query.updatepromoavailability.QueryUpdatePromoTypeAvailability;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

public class PromoTypeListFragment extends ListFragment implements OnItemClickListener, OnPromotionStateListener  {

	private Context mContext;
	private UltraliteCursor mCursor;
	private List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
	private PromoTypeListAdapter mAdapter;
	private Long mStoreId;
	private String mQuery = 
		" select " +
			" c.PromoType, " +
			" count(c.PromoType), " +
			" max(a.IsAvailable) " +
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
			" and a.IsAvailable is not null " + 
		" group by " + 
			" c.PromoType " +
		" order by " + 
			" c.PromoType desc ";
	
	private OnPromotionStateListener mPromotionStateListener;
	private DocOrderEntity mOrder;
	private Date mOnDate;
	
	public void setPromotionStateListener(OnPromotionStateListener listener) {
		mPromotionStateListener = listener;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
		mAdapter = new PromoTypeListAdapter(mContext);
		mOrder = MainActivity.getInstance().mCurrentOrder;
		mStoreId = getArguments().getLong("store_id");
		long ondate = getArguments().getLong("on_date");
		if (ondate==0)
			mOnDate = new Date();
		else
			mOnDate = new Date(ondate);
		
		setEmptyText("Нет доступных акций");
		setListShown(false);
		setHasOptionsMenu(true);
		setListAdapter(mAdapter);
		getListView().setOnItemClickListener(this);
		mOrder.addOnAmountChangedListener(new OnAmountChangedListener(){

			@SuppressWarnings("rawtypes")
			@Override
			public void onAmountChanged(GenericEntity sender, float old_value, float new_value) {
				mAdapter.notifyDataSetChanged();
			}
			
		});
		mOrder.addOnPromoStateChangedListener(this);
		
		mDataLoader = new DataLoader();
		mDataLoader.execute(mOnDate, mOnDate, mStoreId);
	}
	
	private DataLoader mDataLoader;
	
	private static class ViewHolder {
		TextView text1;
		TextView text2;
		TextView text3;
		
		Switch switch1;
	}
	
	private class PromoTypeListAdapter extends BaseAdapter {
		
		private LayoutInflater mLayoutInflater;
		private Typeface mTypeface;
		
		public PromoTypeListAdapter(Context context) {
			mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoCondensed-Regular.ttf");
		}
		
		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return mData.get(position).get("id");
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
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.text1.setText(((PromoType) mData.get(position).get("type")).toString());
			if (((PromoType) mData.get(position).get("type")).toString().equals("Снижение цены")) {
				holder.text1.setTextColor(getResources().getColor(android.R.color.holo_orange_light));
			}
			if (((PromoType) mData.get(position).get("type")).toString().equals("Купи и получи")) {
				holder.text1.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
			}
			if (((PromoType) mData.get(position).get("type")).toString().equals("Купи и получи (на период)")) {
				holder.text1.setTextColor(getResources().getColor(android.R.color.holo_green_light));
			}
			if (((PromoType) mData.get(position).get("type")).toString().equals("Купи и получи (объемная)")) {
				holder.text1.setTextColor(getResources().getColor(android.R.color.holo_purple));
			}
			
			SparseArray<Float> totals = MainActivity.getInstance().mCurrentOrder.getPromosTotals((Integer) mData.get(position).get("type_id"));
			
			int availablePromotions = Math.round(totals.get(DocOrderEntity.PROMO_COUNT));
			int soldPromotions = Math.round(totals.get(DocOrderEntity.PROMO_SOLD));
			float amount = totals.get(DocOrderEntity.PROMO_AMOUNT);
			
			holder.text2.setText("Доступно акций " + availablePromotions + " из них продано " + soldPromotions + " бонус на сумму " + Money.valueOf(amount).toSymbolString());

			holder.switch1.setChecked((Boolean) mData.get(position).get("available"));
			holder.switch1.setTag((Integer) mData.get(position).get("type_id"));
			holder.switch1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked){
						if (buttonView instanceof Switch){
							Switch sw = (Switch) buttonView;
							if ((Integer) sw.getTag() == 2){
								sw.setChecked(false);
								notifyDataSetChanged();
								Dialogs.createDialog(mContext,"", "Массовое включение акций Купи-Получи не допускается. Пожалуйста, зайдите в конкретную акцию.", Command.NO_OP,null,null).show();
								  
								return;
							}
						}
					}
					new QueryUpdatePromoTypeAvailability(InternalStorage.getConnection(), mStoreId, (Integer) buttonView.getTag(), isChecked).execute();
					if (mPromotionStateListener == null) {
						return;
					}
					mPromotionStateListener.onPromotionTypeAvailabilityChanged(isChecked, (Integer) buttonView.getTag() - 1);
				}
			});
			
			holder.text3.setVisibility(View.GONE);
			
			return convertView;
		}
	}
	
	private class DataLoader extends AsyncTask<Object, Void, Void> {

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
					item.put("type_id", mCursor.getInt(1));
					item.put("type", new PromoType(mCursor.getInt(1)));
					item.put("count", mCursor.getInt(2));
					item.put("available", mCursor.getBoolean(3));
					
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		PromoListFragment fragment = new PromoListFragment();
		String tag = Integer.toString(fragment.hashCode());
		Bundle bundle = new Bundle();
		bundle.putLong("store_id", mStoreId);
		bundle.putLong("promotion_id", Long.valueOf((Integer) mData.get(position).get("type_id")));
		bundle.putLong("on_date", getArguments().getLong("on_date"));
		fragment.setArguments(bundle);
		transaction.replace(R.id.fragment_container, fragment, tag);
		transaction.addToBackStack(tag);
		transaction.commit();
	}

	@Override
	public void onPromotionCompensationTypeChanged(int compensationType, Long promotionId) {
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onPromotionTypeAvailabilityChanged(Boolean availability, int promotionType) {
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onPromotionAvailabilityChanged(Boolean availability, Long promotionId) {
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onPromotionCounterChanged(Integer counter, Long promotionId) {
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onDetach() {
		mDataLoader.cancel(true);
		if (mOrder != null) mOrder.removeOnPromoStateChangedListener(this); 
		super.onDetach();
	}
	
}
