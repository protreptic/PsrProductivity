package ru.magnat.sfs.android;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.ref.employee.RefEmployeeEntity;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.money.Money;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.SimpleGenericEntityView;
import ru.magnat.sfs.ui.android.report.target.SalaryView;
import ru.magnat.sfs.ui.android.report.target.TargetView;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TargetContainerAdapter<T extends RefGenericEntity<?, ?>> extends BaseExpandableListAdapter {
	
	final Context _context;
	final T _entity;
	final List<ContentItem> _sections = new ArrayList<ContentItem>();

	public TargetContainerAdapter(Context context, T entity) {
		_context = context;
		_entity = entity;
		
		if (entity instanceof RefOutletEntity) {
			_sections.add(new ContentItem("Цели визита", new TargetView<RefOutletEntity>(context, (RefOutletEntity) _entity, Calendar.DAY_OF_MONTH)));
			_sections.add(new ContentItem("Цели точки на месяц", new TargetView<RefOutletEntity>(context, (RefOutletEntity) _entity, Calendar.MONTH)));
			_sections.add(new ContentItem("Цели агента на день", new TargetView<RefEmployeeEntity>(context, Globals.getEmployee(), Calendar.DAY_OF_MONTH)));
			_sections.add(new ContentItem("Цели агента на месяц", new TargetView<RefEmployeeEntity>(context, Globals.getEmployee(), Calendar.MONTH)));
		} else {
			_sections.add(new ContentItem("Цели на день", new TargetView<T>(context, _entity, Calendar.DAY_OF_MONTH)));
			_sections.add(new ContentItem("Цели на месяц", new TargetView<T>(context, _entity, Calendar.MONTH)));
		}
	}
	
	@SuppressWarnings("unchecked")
	public TargetContainerAdapter(Context context) {
		_context = context;
		_entity = (T) Globals.getEmployee();
		_sections.add(new ContentItem("Цели на день", new TargetView<T>(context, _entity, Calendar.DAY_OF_MONTH)));
		_sections.add(new ContentItem("Цели на месяц", new TargetView<T>(context, _entity, Calendar.MONTH)));
		_sections.add(new ContentItem("Зарплата", new SalaryView(context)));
	}

	public static final int TARGET_FOR_DAY = 1;
	
	ProgressDialog _progressDialog;

	public void refresh() {
		_progressDialog = new ProgressDialog(MainActivity.getInstance());
		_progressDialog.setMessage("Обновление целей...");
		Runnable cmd = new Runnable() {
			public void run() {
				if (_progressDialog.isShowing()) {
					_progressDialog.show();
				}
				for (ContentItem item : _sections) {
					item.View.refresh();	
				}
				if (_progressDialog.isShowing()) {
					_progressDialog.dismiss();
				}
				notifyDataSetInvalidated();
			}
		};
		MainActivity.getInstance().runOnUiThread(cmd);
	}

	protected Context getContext() {
		return _context;
	}

	class ContentItem {
		public ContentItem(String caption, SfsContentView v) {
			Caption = caption;
			View = v;
		}

		public String Caption;
		public SfsContentView View;
	}

	public int getGroupCount() {
		return _sections.size();
	}

	public int getChildrenCount(int groupPosition) {
		View v = _sections.get(groupPosition).View;

		if (v instanceof TargetView) {
			return ((TargetView<?>) v).getCount();
		} else if (v instanceof SalaryView) {
			return ((SalaryView) v).getCount();
		}

		return 1;
	}

	public Object getGroup(int groupPosition) {
		return null;
	}
 
	public Object getChild(int groupPosition, int childPosition) {
		View v = _sections.get(groupPosition).View;
		if (v instanceof SimpleGenericEntityView) {
			return ((TargetView<?>) v).getEntity();
		} else if (v instanceof SalaryView) {
			return ((SalaryView) v).getEntity();
		}
		
		return null;
	}

	public long getGroupId(int groupPosition) {
		GenericEntity<?> e = (GenericEntity<?>) getChild(groupPosition, 0);
		if (e == null)
			return 0;
		return e.Id;
	}

	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	public boolean hasStableIds() {
		return false;
	}

	private String getSalary(int percent, int amount, int bonus) {
		return Money.valueOf(amount).add(Money.valueOf(bonus).interest(percent)).toSymbolString(Locale.getDefault());
	}
	
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		RelativeLayout v = new RelativeLayout(getContext());
		TextView t = new TextView(getContext());
		String strText = _sections.get(groupPosition).Caption;
		
		if (strText.equalsIgnoreCase("Зарплата")) { 
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.getInstance());
			String targetLevelOfBonuses = sharedPref.getString(MainActivity.getInstance().getResources().getString(R.string.options_salary_target_level_of_bonuses), "0");
			String fixedLevel = sharedPref.getString(MainActivity.getInstance().getResources().getString(R.string.options_salary_fixed_part), "0");

			int integralIndex = (int) Globals.getIntegralIndex();
			
			strText += " (прогноз " + Integer.toString(integralIndex) + "% от премии)";
			
			if (!isExpanded) {
				if (Utils.isNumber(targetLevelOfBonuses) && Utils.isNumber(fixedLevel)) {
					strText += " " + this.getSalary(integralIndex, Integer.valueOf(fixedLevel), Integer.valueOf(targetLevelOfBonuses));
				} else {
					strText += " Задайте правильные настройки оплаты труда";
				}
			}
		}
		t.setText(strText); 
		t.setTextAppearance(getContext(), android.R.style.TextAppearance_Large);
		v.addView(t);

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(60, 20, 20, 20);
		t.setLayoutParams(layoutParams);
		
		return v;
	}

	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		SfsContentView v = _sections.get(groupPosition).View;
		if (v instanceof TargetView) {
			return ((TargetView<?>) v).getChildView(childPosition, convertView, parent);
		} else if (v instanceof SalaryView) {
			return ((SalaryView) v).getChildView(childPosition, convertView, parent);
		}
		
		return v.inflate();
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public void close() {
		for (int i = 0; i < _sections.size(); i++) {
			SfsContentView v = _sections.get(i).View;
			if (v instanceof Closeable) {
				try {
					((Closeable) v).close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
