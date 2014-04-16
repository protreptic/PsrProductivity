package ru.magnat.sfs.ui.android.doc.order;

import java.util.Set;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.query.order.PickFilter;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public final class DocOrderFilterView extends SfsContentView {
	final PickFilter _filter;
	Set<Integer> _contextfilter = null;
	public DocOrderFilterView(Context context, PickFilter filter) {
		super(context);
		_filter = filter.clone();
	}

	public PickFilter getFilter(){
		return _filter;
	}
	@Override
	public SfsContentView inflate() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.order_filter_view, this);
		applyContext();
		initRecommendedFilter();
		initInstockFilter();
		initOnlyListAFilter();
		initOnlyListCFilter();
		initOnlyPriorityFilter();
		initOnlyInitiativeFilter();
		initOnlyTprFilter();
		initTemporaryItems();
		initKitsOnlyFilter();
		return this;
	}

	private void applyContext() {
		if (_contextfilter == null) return;
		for (Integer id: _contextfilter){
			View v =  findViewById(id);
			if (v!=null) v.setVisibility(VISIBLE);
		}
		
	}

	private void initKitsOnlyFilter() {
		CheckBox cb = (CheckBox) findViewById(R.id.checkBoxOnlyKits);
		cb.setChecked(_filter.getKits());
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				_filter.setKits(isChecked);
			}
		});
	}

	
	private void initTemporaryItems() {
		CheckBox cb = (CheckBox) findViewById(R.id.checkBoxOnlyTemporary);
		cb.setChecked(_filter.getTemporary());
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				_filter.setTemporary(isChecked);
			}
		});
	}
	
	private void initInstockFilter() {
				
		CheckBox cb = (CheckBox) findViewById(R.id.checkBoxInStock);
		cb.setChecked(_filter.getInStock());
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				_filter.setInStock(isChecked);
				
			}
			
		});
	}

	private void initRecommendedFilter() {
		
		CheckBox cb = (CheckBox) findViewById(R.id.checkBoxRecommended);
		cb.setChecked(_filter.getRecommended());
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				_filter.setRecommnded(isChecked);
				
			}
			
		});
	}

	private void initOnlyListAFilter() {
		
		CheckBox cb = (CheckBox) findViewById(R.id.checkBoxOnlyListA);
		cb.setChecked(_filter.getOnlyListA());
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked){
					markCheckbox(R.id.checkBoxOnlyListC, false);
					_filter.setOnlyListC(false);
				}
				_filter.setOnlyListA(isChecked);
			}
			
		});
	}
	
	private void initOnlyListCFilter() {
		
		CheckBox cb = (CheckBox) findViewById(R.id.checkBoxOnlyListC);
		cb.setChecked(_filter.getOnlyListC());
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked){
					markCheckbox(R.id.checkBoxOnlyListA, false);
					
					_filter.setOnlyListA(false);
					
					
				}
				_filter.setOnlyListC(isChecked);
			}
			
		});
	}
private void markCheckbox(int id, boolean isChecked)
{
	CheckBox cb = (CheckBox) findViewById(id);
	cb.setChecked(isChecked);
	
}
private void initOnlyPriorityFilter() {
		
		CheckBox cb = (CheckBox) findViewById(R.id.checkBoxOnlyPriority);
		cb.setChecked(_filter.getOnlyPriority());
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked){
					
					markCheckbox(R.id.checkBoxOnlyInitiative, false);
					
					_filter.setOnlyInitiative(false);
					
				}
				_filter.setOnlyPriority(isChecked);
			
			}
			
		});
	}
	
	private void initOnlyInitiativeFilter() {
		
		CheckBox cb = (CheckBox) findViewById(R.id.checkBoxOnlyInitiative);
		cb.setChecked(_filter.getOnlyInitiative());
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked){
					
					markCheckbox(R.id.checkBoxOnlyPriority, false);
					
					_filter.setOnlyPriority(false);
					
				}
				_filter.setOnlyInitiative(isChecked);
			}
			
		});
	}
	private void initOnlyTprFilter() {
		
		CheckBox cb = (CheckBox) findViewById(R.id.checkBoxOnlyTpr);
		cb.setChecked(_filter.getOnlyTpr());
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				_filter.setOnlyTpr(isChecked);
				
			}
			
		});
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

	public void setContextFilter(Set<Integer> contextfilter) {
		_contextfilter = contextfilter;
		
	}

}
