package ru.magnat.sfs.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ru.magnat.sfs.android.Command;
import ru.magnat.sfs.android.CommandWithParameters;
import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.OnBackPressedListener;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.ref.outlet.RefOutlet;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.task.visit.TaskVisitEntity;
import ru.magnat.sfs.bom.task.visit.TaskVisitJournal;
import ru.magnat.sfs.bom.track.DocTrackEntity;
import ru.magnat.sfs.database.UltraliteCursor;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.util.Loader;
import ru.magnat.sfs.util.Text;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.location.Location;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class TaskView extends SfsContentView {
	
	private ImageButton mImageButton;
	private Button mDateButton;
	private Calendar mCalendar = Calendar.getInstance(new Locale("ru", "RU"));
	
	private VisitAdapter mAdapter;
	private List<Map<String, String>> mData = new ArrayList<Map<String, String>>();
	private ListView mListView;
	
	public TaskView(Context context) {
		super(context);

		Log.d("TaskView", "TaskView created");
		
		layoutInflater.inflate(R.layout.generic_journal_view, this);
		
		mAdapter = new VisitAdapter();
		mListView = (ListView) findViewById(R.id.list);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TaskVisitJournal journal = new TaskVisitJournal(getContext());
				TaskVisitEntity entity = journal.FindById((Long) mAdapter.getItem(position));
				journal.close();
				OpenEntity(entity);
			}

		});
		
		mImageButton = (ImageButton) findViewById(R.id.caption_action);
		mImageButton.setVisibility(GONE);
		mDateButton = (Button) findViewById(R.id.date_button);
		mDateButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Dialogs.createDateDialog(getContext(), new OnDateSetListener() {
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						mCalendar.set(year, monthOfYear, dayOfMonth);
						mDateButton.setText((String) DateFormat.format("dd MMMM", mCalendar.getTime()));
						
						new LoadData(getContext()).execute(mCalendar.getTime());	
					}
				}, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
		mDateButton.setText((String) DateFormat.format("dd  MMMM", mCalendar));

		new LoadData(context).execute(new Date());
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
			Log.d("TaskView", "tag:TaskView getView: " + position);
			
			ViewHolder holder;
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.visit_item, null);
				
				holder = new ViewHolder();
				holder.layout1 = (LinearLayout) convertView.findViewById(R.id.visit_is_not_complete); 
				holder.image1 = (ImageView) convertView.findViewById(R.id.visit_outlet_golden_mark);
				holder.image2 = (ImageView) convertView.findViewById(R.id.details);
				holder.text1 = (TextView) convertView.findViewById(R.id.visit_outlet_legal_name);
				holder.text2 = (TextView) convertView.findViewById(R.id.visit_outlet_address);
				holder.text3 = (TextView) convertView.findViewById(R.id.visit_start_time_first);
				holder.text4 = (TextView) convertView.findViewById(R.id.visit_outlet_start_time);
				holder.text5 = (TextView) convertView.findViewById(R.id.visit_outlet_end_time);
				
				holder.text1.setTypeface(typeface); 
				holder.text2.setTypeface(typeface); 
				holder.text3.setTypeface(typeface); 
				holder.text4.setTypeface(typeface); 
				holder.text5.setTypeface(typeface); 
				
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
					Dialogs.createDialog(getContext(), "", "", new OutletView(getContext(), (Long) view.getTag()), Command.NO_OP, null).show();
				}
			});
	    	
			return convertView;
		}
		
	}
	
	private class LoadData extends Loader {
		
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
		
		public LoadData(Context context) {
			super(context);
		}
		
		@Override
		protected Void doInBackground(Object... params) {
			mData.clear();
			
			UltraliteCursor cursor = new UltraliteCursor(getContext(), query);
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
			
			super.onPostExecute(result); 
		}
		
	}
	
	private class ChangeOutletLocationCommand implements Command {
		
		private RefOutletEntity mOutlet;
		private Location mLocation;
		
		public ChangeOutletLocationCommand(RefOutletEntity outlet, Location location){
			mOutlet = outlet;
			mLocation = location;
		}
		
		@Override
		public void execute() {
			RefOutlet ref = new RefOutlet(getContext());
			if (ref.Find(mOutlet)) {
				ref.Current().LocationLat = (float) mLocation.getLatitude();
				ref.Current().LocationLon = (float) mLocation.getLongitude();
			}
			ref.save();
			ref.close();
			ref = null;
		}
	
	}
	
	private void OpenVisit(TaskVisitEntity entity) {
		Location outletLocation = entity.Outlet.getLocation();
		Location currentLocation  = Globals.getCurrentLocation();
		if (currentLocation == null){
			currentLocation = DocTrackEntity.getLastPosition();
		}
		if (entity.Latitude == 0) {
			if (currentLocation == null) {
				Dialogs.createDialog(
						"SFS",
						"Координаты еще не определены. Проверьте текущее местопололжение на карте.",
						Command.NO_OP).show();
				return;
			}
			
			if (outletLocation == null) {
				Dialogs.createDialog("", "Присвоить точке текущие координаты?", new ChangeOutletLocationCommand(entity.Outlet,currentLocation), Command.NO_OP).show();
				outletLocation = currentLocation;
			}
	
			float[] results = { 0f, 0f, 0f };
			Location.distanceBetween(currentLocation.getLatitude(),
					currentLocation.getLongitude(), outletLocation.getLatitude(),
					outletLocation.getLongitude(), results);
			int accuracy = (int) results[0];
	
			entity.Latitude = (float) currentLocation.getLatitude();
			entity.Longitude = (float) currentLocation.getLongitude();
			entity.Distance = accuracy;
			entity.TaskBegin = new Date();
			entity.save();
			if (accuracy > 150f) {
				CommandWithParameters cmd1 = new CommandWithParameters() {
					private Map<String, Object> mParams = new HashMap<String, Object>();
	
					public void execute() {
						openEntity((TaskVisitEntity) mParams.get("visit"));
					}
					public Map<String, Object> getParameters() {
						return mParams;
					}
				};
				cmd1.getParameters().put("visit", entity);
				Dialogs.createDialog(
						"SFS",
						"Вы находитесь слишком далеко от точки. Начать визит с нарушением регламента?",
						cmd1, Command.NO_OP).show();
			} else {
				gotoVisit(entity);
			}
		} else {
			gotoVisit(entity);
		}
	}
	
	private void gotoVisit(TaskVisitEntity entity) {
		TaskVisitJournal cat = new TaskVisitJournal(getContext());
		SfsContentView v = cat.GetEditView(entity);
		cat.close();

		v.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {

			public void onViewAttachedToWindow(View v) {
				new LoadData(getContext()).execute(new Date());
			}
 
			public void onViewDetachedFromWindow(View v) {
				new LoadData(getContext()).execute(new Date());
			}

		});
		
		MainActivity.sInstance.addToFlipper(v, getClass().getSimpleName());
		MainActivity.sInstance.addOnBackPressedListener((OnBackPressedListener) v);
	}
	
	public void openVisit(TaskVisitEntity entity) {
		OpenEntity(entity);
	}
	
	protected void openEntity(TaskVisitEntity entity) {
		gotoVisit(entity);
	}
	
	private void OpenEntity(TaskVisitEntity entity) {
		if (!Utils.RoundDate(entity.TaskBegin).equals(Utils.RoundDate(MainActivity.getInstance().mCurrentWorkday.TaskBegin))) {
			Dialogs.createDialog("SFS","Можно начинать визиты только текущего рабочего дня",Command.NO_OP).show();
			return;
		}
		CommandWithParameters cmd = new CommandWithParameters() {
			private Map<String, Object> mParams = new HashMap<String, Object>();

			public void execute() {
				OpenVisit((TaskVisitEntity) mParams.get("visit"));
			}

			public Map<String, Object> getParameters() {
				return mParams;
			}

		};
		cmd.getParameters().put("visit", entity.clone());

		if (entity.Outlet == null) {
			Dialogs.createDialog("SFS", "Данные об этом клиенте еще не загружены.", Command.NO_OP).show();
			return;
		}
		String offer = (entity.TaskEnd == null) ? "Начать визит в точку " + entity.Outlet.toString() + "?" : "Продолжить визит в точку " + entity.Outlet.toString() + "?";
		Dialogs.createDialog("SFS", offer, cmd, Command.NO_OP).show();
	}

}
