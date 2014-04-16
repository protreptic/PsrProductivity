package ru.magnat.sfs.ui.android.doc.order;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArraySet;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.order.DocOrderEntity;
import ru.magnat.sfs.bom.order.DocOrderLineEntity;
import ru.magnat.sfs.bom.query.order.PickFilter;
import ru.magnat.sfs.bom.query.order.QueryGetOrderPickListEntity;
import ru.magnat.sfs.bom.query.order.QueryGetOrderPickListOrdered;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author petr_bu
 */
public class NotSoldPrioritiesDialog extends SfsContentView {

	private Context mContext;
	
	public NotSoldPrioritiesDialog(Context context, List<Map<String, Object>> data) {
		super(context);
		
		mContext = context;
		mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				
		mAdapter = new Adapter();
		mData = data;
		
	}

	
	final private List<Map<String, Object>> mData;
	
	private LayoutInflater mLayoutInflater;
	private ListView mListView;
	private Adapter mAdapter;
	
	private static class ViewHolder {
		public TextView text1;
		public TextView text2;
	}
	
	public class Adapter extends BaseAdapter {

		private Typeface mTypeface;
		
		public Adapter() {
			mTypeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/RobotoCondensed-Regular.ttf");
		}
		
		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		} 

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(R.layout.two_line_list_item, null);
				
				holder = new ViewHolder();

				holder.text1 = (TextView) convertView.findViewById(R.id.first_line);
				holder.text2 = (TextView) convertView.findViewById(R.id.second_line);
				
				init(holder);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.text1.setText((String) mData.get(position).get("first_line"));
			holder.text2.setVisibility(View.GONE);
			
			return convertView;
		}
		
		private void init(ViewHolder holder) {
			Field[] fields = ViewHolder.class.getFields();
			for (Field field : fields) {
				if (field.getType().equals(TextView.class)) {
					try {
						((TextView) field.get(holder)).setTypeface(mTypeface);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}
	
	@Override
	public SfsContentView inflate() {
		mLayoutInflater.inflate(R.layout.list_item_view, this);
		
		mListView = (ListView) findViewById(R.id.list);
		mListView.setAdapter(mAdapter); 
		
		mAdapter.notifyDataSetChanged();
		
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

}
