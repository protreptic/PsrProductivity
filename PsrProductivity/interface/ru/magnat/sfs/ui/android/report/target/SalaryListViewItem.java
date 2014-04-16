package ru.magnat.sfs.ui.android.report.target;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.TextView;
import ru.magnat.sfs.android.Log;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.query.targets.QueryGetTarget;
import ru.magnat.sfs.bom.query.targets.QueryGetTargetEntity;
import ru.magnat.sfs.ui.android.*;

public final class SalaryListViewItem extends
		GenericListItemView<QueryGetTarget, QueryGetTargetEntity> {

	public SalaryListViewItem(Context context, QueryGetTarget list,
			ListView lv, QueryGetTargetEntity entity) {
		super(context, list, lv);
		_entity = entity;
	}

	final QueryGetTargetEntity _entity;
	TextView _order_text;

	@Override
	public SfsContentView inflate() {

		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		inflater.inflate(R.layout.salary_list_item_view_ii, this);

		return this;
	}

	@Override
	public void fill() {

		String strKpi = "";
		String strWeight = "";
		String strForecast = "";
		String strTarget = "";
		String strPlanfact = "";
		String strTotal = "";
		float forecast = _entity.Fact;
		float index = 0;
		float workdayProgress = 1;
		if (_entity != null) {
			workdayProgress = Globals.getWorkdaysProgress(_entity.KpiKind);	
			if (workdayProgress != 0)
				forecast = forecast /workdayProgress;
			index = _entity.Index;
			if (_entity.Target != 0) {
				index = forecast / _entity.Target;
			}
			float kpiValue = Globals.recalcKpiValue(
					_entity.KpiMatrix, _entity.KPI, index, _entity.Weight);
			
			Log.i(MainActivity.LOG_TAG, ""+ _entity.KPI.toString()+" = "+Float.toString(kpiValue)+"(Id="+_entity.KPI.Id+", Weight="+_entity.Weight+")");
			strForecast = String.format("%.0f", forecast);
			strTarget = String.format("%.0f", _entity.Target);
			strKpi = _entity.Descr;
			strPlanfact = String.format("%.0f", index * 100) + "%";
			strWeight = String.format("%.0f", _entity.Weight) + "%";
			
			strTotal = String.format("%.0f", kpiValue)
					+ "%";
			
		

		}
		((TextView) findViewById(R.id.salary_kpi)).setText(strKpi);
		((TextView) findViewById(R.id.salary_planfact)).setText(strPlanfact);
		((TextView) findViewById(R.id.salary_target)).setText(strTarget);
		((TextView) findViewById(R.id.salary_total)).setText(strTotal);
		((TextView) findViewById(R.id.salary_kpi_label)).setText("вес: "+strWeight);
		((TextView) findViewById(R.id.salary_forecast)).setText(strForecast);
		if (_entity != null) {
			if (_entity.Target == 0)
				_foreColor = R.color.grey;
			else
				_foreColor = (_entity.GAP > 0) ? R.color.red
						: android.R.color.primary_text_light;
			// setForeColor(this);
		}
	}

}
