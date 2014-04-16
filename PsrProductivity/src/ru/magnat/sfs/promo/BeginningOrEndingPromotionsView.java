package ru.magnat.sfs.promo;

import java.io.Closeable;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.ref.outlet.RefOutlet;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.ref.promo.PromoType;
import ru.magnat.sfs.database.UltraliteCursor;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.doc.order.OnViewDataLoaded;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

public class BeginningOrEndingPromotionsView extends SfsContentView implements
		Closeable {

	final private int mToEnd;
	final private int mFromBegin;
	final static private String QUERY_PROMOS = " select distinct " + " b.Id, "
			+ " c.DescriptionSfa as Descr, " + " c.PromoType, "
			+ " b.StartOfPromo as BeginOfAction, "
			+ " b.EndOfPromo as EndOfAction " + " from "
			+ " RegPromoAvailability a " + " inner join RefPromoDetails b on "
			+ " a.Promo = b.Id " + " inner join RefPromo c on "
			+ " b.ParentExt = c.Id " + " where " + " a.Outlet = ? "
			+ " and c.PromoType = ? " + " and b.IsMark = 0 "
			+ " and a.IsAvailable is not null "
			+ " and datediff(day, a.StartOfPromo, now()) <= ? "
			+ " and datediff(day, now(), a.EndOfPromo) <= ? " + " order by "
			+ " 3, 2 ";

	final static private String QUERY_PROMO_TYPES = " select "
			+ " c.PromoType, " + " count(c.PromoType), "
			+ " max(a.IsAvailable) " + " from "
			+ " RegPromoAvailability as a  "
			+ " inner join RefPromoDetails as b on " + " a.Promo = b.Id "
			+ " inner join RefPromo as c on " + " b.ParentExt = c.Id "
			+ " where " + " now() between b.StartOfPromo and b.EndOfPromo "
			+ " and b.IsMark = 0 " + " and a.Outlet = ? "
			+ " and a.IsAvailable is not null " + " group by "
			+ " c.PromoType " + " order by " + " c.PromoType desc ";

	private Context mContext;
	private ListAdapter mAdapter;
	private ExpandableListView mExpandableListView;
	private List<Map<String, Object>> mGroupData = new ArrayList<Map<String, Object>>();
	private List<List<Map<String, Object>>> mChildData = new ArrayList<List<Map<String, Object>>>();

	private Long mOutletId;

	public BeginningOrEndingPromotionsView(Context context, Long storeId,
			int toEnd, int fromBegin) {
		super(context);
		mToEnd = toEnd;
		mFromBegin = fromBegin;
		mContext = context;
		mOutletId = storeId;
		mAdapter = new ListAdapter();
		new LoadData().execute();
	}

	private class LoadData extends AsyncTask<Object, Void, Void> {

		private ProgressDialog mProgressDialog;

		public LoadData() {
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setMessage(mContext.getResources().getString(
					R.string.data_loading));
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

			UltraliteCursor cursor1 = new UltraliteCursor(mContext,
					QUERY_PROMO_TYPES);
			cursor1.setParametersAndExecute(mOutletId);
			if (cursor1.getCount()>0){
				while (cursor1.moveToNext()) {
					Long promotionType = cursor1.getLong(1);
	
					UltraliteCursor cursor2 = new UltraliteCursor(mContext,
							QUERY_PROMOS);
					cursor2.setParametersAndExecute(mOutletId, promotionType,
							mFromBegin, mToEnd);
	
					List<Map<String, Object>> childDataItem = new ArrayList<Map<String, Object>>();
					while (cursor2.moveToNext()) {
						Map<String, Object> item = new HashMap<String, Object>();
						item.put("description", cursor2.getString(2));
						item.put("type",
								new PromoType(cursor2.getInt(3)).toString());
						item.put("start_date", cursor2.getDate(4));
						item.put("end_date", cursor2.getDate(5));
						childDataItem.add(item);
					}
					mChildData.add(childDataItem);
	
					cursor2.close();
					cursor2 = null;
	
					Map<String, Object> group = new HashMap<String, Object>();
					group.put("type", new PromoType(cursor1.getInt(1)));
					mGroupData.add(group);
				}
			}
			cursor1.close();
			cursor1 = null;

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			//
			mAdapter.notifyDataSetChanged();

			// Скрыть диалог
			mProgressDialog.dismiss();
			mProgressDialog = null;
			onViewDataLoaded();
		}
	}

	private static class GroupViewHolder {
		TextView text1;
		TextView text2;
		TextView text3;

		Switch switch1;

		ProgressBar progress1;
	}

	private static class ChildViewHolder {
		TextView text1;
		TextView text2;
		TextView text3;

		Switch switch1;

		ProgressBar progress1;
	}

	private class ListAdapter extends BaseExpandableListAdapter {

		private Typeface mTypeface;
		private Calendar mStartDateOfPromo;
		private Calendar mEndDateOfPromo;
		private SimpleDateFormat mDateFormat;

		public ListAdapter() {
			mTypeface = Typeface.createFromAsset(mContext.getAssets(),
					"fonts/RobotoCondensed-Regular.ttf");
			mStartDateOfPromo = Calendar.getInstance(new Locale("ru", "RU"));
			mEndDateOfPromo = Calendar.getInstance(new Locale("ru", "RU"));
			mDateFormat = new SimpleDateFormat("dd MMMMM", new Locale("ru",
					"RU"));
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			long id = (Long) mChildData.get(groupPosition).get(childPosition)
					.get("store_id");

			RefOutlet refOutlet = new RefOutlet(mContext);
			RefOutletEntity entity = refOutlet.FindById(id);
			refOutlet.close();

			return entity;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			GroupViewHolder holder;
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.promo_list_item,
						null);

				holder = new GroupViewHolder();
				holder.text1 = (TextView) convertView
						.findViewById(R.id.promo_action_type);
				holder.text2 = (TextView) convertView
						.findViewById(R.id.promo_action_description);
				holder.text3 = (TextView) convertView
						.findViewById(R.id.promo_action_apply_date);
				holder.text1.setTypeface(mTypeface);
				holder.text2.setTypeface(mTypeface);
				holder.text3.setTypeface(mTypeface);

				holder.switch1 = (Switch) convertView
						.findViewById(R.id.switch1);

				holder.progress1 = (ProgressBar) convertView
						.findViewById(R.id.promo_action_progress);

				convertView.setTag(holder);
			} else {
				holder = (GroupViewHolder) convertView.getTag();
			}

			holder.text1.setText(((PromoType) mGroupData.get(groupPosition)
					.get("type")).toString());
			if (((PromoType) mGroupData.get(groupPosition).get("type"))
					.toString().equals("Снижение цены")) {
				holder.text1.setTextColor(getResources().getColor(
						android.R.color.holo_orange_light));
			}
			if (((PromoType) mGroupData.get(groupPosition).get("type"))
					.toString().equals("Купи и получи")) {
				holder.text1.setTextColor(getResources().getColor(
						android.R.color.holo_blue_light));
			}
			if (((PromoType) mGroupData.get(groupPosition).get("type"))
					.toString().equals("Купи и получи (на период)")) {
				holder.text1.setTextColor(getResources().getColor(
						android.R.color.holo_green_light));
			}
			if (((PromoType) mGroupData.get(groupPosition).get("type"))
					.toString().equals("Купи и получи (объемная)")) {
				holder.text1.setTextColor(getResources().getColor(
						android.R.color.holo_purple));
			}

			holder.text2.setVisibility(View.GONE);
			holder.text3.setVisibility(View.GONE);

			holder.switch1.setVisibility(View.GONE);
			holder.progress1.setVisibility(View.GONE);

			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			ChildViewHolder holder;
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.promo_list_item,
						null);

				holder = new ChildViewHolder();
				holder.text1 = (TextView) convertView
						.findViewById(R.id.promo_action_type);
				holder.text2 = (TextView) convertView
						.findViewById(R.id.promo_action_description);
				holder.text3 = (TextView) convertView
						.findViewById(R.id.promo_action_apply_date);
				holder.text1.setTypeface(mTypeface);
				holder.text2.setTypeface(mTypeface);
				holder.text3.setTypeface(mTypeface);

				holder.switch1 = (Switch) convertView
						.findViewById(R.id.switch1);

				holder.progress1 = (ProgressBar) convertView
						.findViewById(R.id.promo_action_progress);

				convertView.setTag(holder);
			} else {
				holder = (ChildViewHolder) convertView.getTag();
			}

			holder.text1.setVisibility(View.GONE);
			holder.text2.setText((String) mChildData.get(groupPosition)
					.get(childPosition).get("description"));

			mStartDateOfPromo.setTime((Date) mChildData.get(groupPosition)
					.get(childPosition).get("start_date"));
			String startDateString = mDateFormat.format(mStartDateOfPromo
					.getTime());
			mEndDateOfPromo.setTime((Date) mChildData.get(groupPosition)
					.get(childPosition).get("end_date"));
			String endDateString = mDateFormat
					.format(mEndDateOfPromo.getTime());

			String line2 = String
					.format(new Locale("ru", "RU"),
							"действует с %s по %s года",
							(startDateString + ((mStartDateOfPromo
									.get(Calendar.YEAR) != mEndDateOfPromo
									.get(Calendar.YEAR)) ? " "
									+ mStartDateOfPromo.get(Calendar.YEAR)
									+ " года" : "")),
							(endDateString + " " + mEndDateOfPromo
									.get(Calendar.YEAR)));
			holder.text3.setText(line2);

			holder.switch1.setVisibility(View.GONE);
			holder.progress1.setVisibility(View.GONE);

			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return mChildData.get(groupPosition).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return null;
		}

		@Override
		public int getGroupCount() {
			return mGroupData.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}

	@Override
	public SfsContentView inflate() {
		layoutInflater.inflate(R.layout.expandable_list_view111, this);
		mExpandableListView = (ExpandableListView) findViewById(R.id.expandable_list);
		mExpandableListView.setAdapter(mAdapter);
		// Открыть все наборы
		for (int i = 0; i < mGroupData.size(); i++) {
			mExpandableListView.expandGroup(i);
		}
		return this;
	}

	@Override
	public void fill() {
	}

	@Override
	public void refresh() {
	}

	@Override
	public void moveTaskToBack(Boolean handled) {
	}

	@Override
	public void close() throws IOException {

	}

	private OnViewDataLoaded mHandler;

	public void setOnViewDataLoaded(OnViewDataLoaded handler) {
		mHandler = handler;

	}

	private void onViewDataLoaded() {
		if (mHandler != null) {
			mHandler.onViewDataLoaded();
		}
	}

	public boolean IsEmpty() {
		int counter = 0;
		for (int i = 0; i < mAdapter.getGroupCount(); i++) {
			counter += mAdapter.getChildrenCount(i);
		}
		return counter == 0;
	}

}
