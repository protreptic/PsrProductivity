package ru.magnat.sfs.ui.android;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.bom.FieldMD;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.OrmObject;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;

public abstract class GenericEntityView<C extends OrmObject<E>, E extends GenericEntity<C>> extends SfsContentView implements OnTabChangeListener {

	protected E _entity;
	protected C _catalog;
	List<DataObjectProperty> list = new ArrayList<DataObjectProperty>();
	String listItems[];
	
	public static final int MASTER_DOC_MARKETING_MEASURE = 44;
	
	public GenericEntityView(Context context) {
		super(context);
	}
	
	public GenericEntityView(Context context, C ormObject, E entity) {
		super(context);
		 
		_catalog = ormObject;
		if (_catalog.Find(entity)) {
			_catalog.setCurrent(entity);
			_entity = _catalog.getCurrent();
		}
		showMenu();
	}

	protected TabContentFactory _tabFactory;
	protected TabHost mTabHost;

	public SfsContentView inflate() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.list_item_view, this);
		ListView lv = (ListView) findViewById(R.id.list);
		
		lv.setAdapter(new DataObjectAdapter(this.getContext(), list));
		return this;

	}

	private class DataObjectProperty {
		public String property;
		public String value;
		
		public DataObjectProperty(String property, String value) {
			this.property = property;
			this.value = value;
		}
	}

	@Override
	public Boolean onBackPressed() {
		moveTaskToBack(true); 
		
		return true;
	}
	
	private class DataObjectAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private List<DataObjectProperty> list;

		public DataObjectAdapter(Context context, List<DataObjectProperty> list) {
			mInflater = LayoutInflater.from(context);
			this.list = list;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.data_item, parent, false);
			}
			TextView title = (TextView) convertView.findViewById(R.id.item_title);
			TextView sub = (TextView) convertView.findViewById(R.id.item_subtitle);

			DataObjectProperty info = list.get(position);
			title.setText(info.value);
			sub.setText(info.property);
			return convertView;
		}

		public int getCount() {
			return list.size();
		}

		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		public long getItemId(int arg0) {
			return arg0;
		}
	}

	@Override
	public void refresh() {}

	@Override
	public void fill() {
		if (_catalog._fields == null)
			return;
		Vector<FieldMD> fmds = _catalog._fields;
		try {
			for (int i = 0; i < fmds.size(); i++) {
				FieldMD fmd = fmds.elementAt(i);
				String p = fmd.label;
				String v = "";
				Object o = fmd.field.get(_entity);
				if (o != null)
					v = o.toString();
				list.add(new DataObjectProperty(p, v));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void moveTaskToBack(Boolean handled) {
		RelativeLayout page2layout = (RelativeLayout) findViewById(R.id.relativeLayout);

		if (page2layout.getVisibility() == View.VISIBLE) {
			handled = false;
			if (!onRemove())
				return;
			handled = true;
			closeView();

			return;
		}

		handled = false;
	}

	public void closeView() {
		if (_catalog != null) {
			_catalog.close();
		}
		hideMenu();
		MainActivity.getInstance().removeFromFlipper(this);
		MainActivity.getInstance().removeOnBackPressedListener(this);
	}
	
	public void onTabChanged(String tabId) {}
	protected void showMenu() {}
	protected void hideMenu() {}

	public void cancelCloseView() {
		MainActivity.getInstance().addOnBackPressedListener(this);
	}

	protected Boolean onRemove() {
		return true;
	}

}
