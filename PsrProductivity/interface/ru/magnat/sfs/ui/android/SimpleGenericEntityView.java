/**
 * 
 */
package ru.magnat.sfs.ui.android;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.OrmObject;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * @author alex_us
 * 
 */
public class SimpleGenericEntityView<C extends OrmObject<E>, E extends GenericEntity<C>>
		extends SfsContentView implements OnItemClickListener {
	final E _entity;
	final SimpleAdapter _adapter;

	public SimpleGenericEntityView(Context context, E entity) {
		super(context);
		_entity = entity;
		_adapter = new SimpleAdapter(getContext(), _entity.getProperties(),
				android.R.layout.simple_list_item_2, new String[] { "value",
						"property" }, new int[] { android.R.id.text1,
						android.R.id.text2 });
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
	}

	@Override
	public SfsContentView inflate() {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.doc_order_header_tab2, this);
		ListView lv = (ListView) findViewById(R.id.list);

		lv.setAdapter(_adapter);
		lv.setOnItemClickListener(this);
		_adapter.notifyDataSetChanged();

		return this;
	}

	public View getChildView(int position, View convertView, ViewGroup parent) {
		return _adapter.getView(position, convertView, parent);
	}

	public int getCount() {
		return _adapter.getCount();
	}

	public E getEntity() {
		return _entity;
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
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
		if (visibility == VISIBLE)
			_adapter.notifyDataSetChanged();
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		@SuppressWarnings("unchecked")
		HashMap<String, ?> menuItem = (HashMap<String, ?>) _adapter
				.getItem(position);
		if (menuItem == null)
			return;
		String selectMethod = (String) menuItem.get("selectMethod");
		if ((selectMethod == null) || (selectMethod.isEmpty()))
			return;
		Method method = null;
		try {
			method = this.getClass().getDeclaredMethod(selectMethod);
			method.setAccessible(true);
		} catch (SecurityException e) {
			
			e.printStackTrace();
			return;
		} catch (NoSuchMethodException e) {
			
			e.printStackTrace();
			return;
		}
		try {
			method.invoke(this);
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}

	}
}
