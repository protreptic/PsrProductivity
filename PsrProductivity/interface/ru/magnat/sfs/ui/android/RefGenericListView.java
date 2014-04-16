package ru.magnat.sfs.ui.android;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.ref.generic.RefGeneric;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;
import android.R.color;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;

public class RefGenericListView<C extends RefGeneric<E>, E extends RefGenericEntity<C, O>, O extends RefGenericEntity<?, ?>> extends GenericListView<C, E, O> implements OnClickListener, OnItemSelectedListener, OnItemClickListener {

	public RefGenericListView(Context context, C ref, O owner) {
		super(context, ref, owner);
	}

	private int _last_position = -1;

	@Override
	public SfsContentView inflate() {
		if (_inflated) {
			return this;
		}
		
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.generic_list_view, this);
		lv = (ListView) findViewById(R.id.list);
		lv.setAdapter(this._catalog);
		lv.setItemsCanFocus(false);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("rawtypes")
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				fireItemShortClick((GenericEntity) _catalog.getItem(position));
			}

		});

		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@SuppressWarnings("rawtypes")
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				fireItemLongClick((GenericEntity) _catalog.getItem(position));
				return false;
			}

		});

		return this;

	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (_last_position == position)
			return;
		_last_position = position;

	}

	public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
		view.setBackgroundColor(color.background_dark);
	}

	public void onNothingSelected(AdapterView<?> parent) {

	}

	public void onClick(View v) {}

	public Boolean onBackPressed() {
		return null;
	}

	@Override
	protected void createEntity() {}

}
