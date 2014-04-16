package ru.magnat.sfs.ui.android;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.OnValueChangedListener;
import ru.magnat.sfs.bom.OrmObject;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class GenericListItemView<C extends OrmObject<E>, E extends GenericEntity<C>>
		extends SfsContentView implements OnValueChangedListener {
	protected final C _orm;
	protected E _current;
	protected ListView _lv;
	TextView _firstLine;
	protected int _foreColor = android.R.color.primary_text_light;

	public GenericListItemView(Context context, C orm, ListView lv) {
		super(context);
		this._orm = orm;
		this._lv = lv;
	}

	@Override
	public SfsContentView inflate() {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		inflater.inflate(R.layout.simple_list_item_view, this);
		{
			_firstLine = (TextView) findViewById(R.id.first_line);

		}

		return this;
	}

	protected void setBackgroundColor() {
		// this.setBackgroundColor((_orm.Current().equals(_lv.getSelectedItem()))?android.R.color.darker_gray:android.R.color.transparent);
	}

	public void onValueChanged(Object sender, Object value) {
	}

	@Override
	public void refresh() {
	}

	@Override
	public void fill() {
		_current = _orm.Current();

		_firstLine.setText(_orm.Current().toString());
		setBackgroundColor();
	}

	@Override
	public void moveTaskToBack(Boolean handled) {
	}

	public Boolean onBackPressed() {

		return null;
	}

	protected void setForeColor(ViewGroup parent) {

		for (int i = 0; i < parent.getChildCount(); i++) {
			Object v = parent.getChildAt(i);

			if (v instanceof TextView) {
				((TextView) v).setTextColor(getContext().getResources()
						.getColor(_foreColor));
			} else if (v instanceof ViewGroup) {
				setForeColor((ViewGroup) v);
			}

		}
	}

}
