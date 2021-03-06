package ru.magnat.sfs.ui.android.report.target;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.TextView;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.query.targets.QueryGetTarget;
import ru.magnat.sfs.bom.query.targets.QueryGetTargetEntity;
import ru.magnat.sfs.ui.android.*;

public final class InitiativeTargetListViewItem extends
		GenericListItemView<QueryGetTarget, QueryGetTargetEntity> {

	public InitiativeTargetListViewItem(Context context, QueryGetTarget list,
			ListView lv, QueryGetTargetEntity entity) {
		super(context, list, lv);
		_entity = entity;
	}

	final QueryGetTargetEntity _entity;
	TextView _order_text;

	@Override
	public SfsContentView inflate() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.initiative_target_list_item_view, this);

		return this;
	}

	@Override
	public void fill() {
		String strKpi = "";
		String strFact = "";
		String strPlan = "";
		String strGap = "";
		String strIndex = "";

		if (_entity != null) {
			strKpi = _entity.Initiative.Descr;
			if (_entity.Fact != 0 && Math.abs(_entity.Fact) < 100f && _entity.Fact % (int) _entity.Fact != 0) {
				strFact = String.format("%.2f", _entity.Fact);
			} else {	
				strFact = String.format("%.0f", _entity.Fact);
			}
			
			if (_entity.Target != 0 && Math.abs(_entity.Target) < 100f && _entity.Target % (int) _entity.Target != 0) {
				strPlan = String.format("%.2f", _entity.Target);
			} else {	
				strPlan = String.format("%.0f", _entity.Target);
			}
			
			if (_entity.GAP != 0 && Math.abs(_entity.GAP)< 100f && _entity.GAP % (int) _entity.GAP != 0) {
				strGap = String.format("%.2f", -_entity.GAP);
			} else {	
				strGap = String.format("%.0f", -_entity.GAP);	
			}
			
			strIndex = String.format("%.0f", _entity.Index * 100) + "%";
		}
		
		((TextView) findViewById(R.id.target_kpi)).setText(strKpi);
		((TextView) findViewById(R.id.target_plan)).setText(strPlan);
		((TextView) findViewById(R.id.target_fact)).setText(strFact);
		((TextView) findViewById(R.id.target_gap)).setText(strGap);
		((TextView) findViewById(R.id.target_index)).setText(strIndex);
		
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
