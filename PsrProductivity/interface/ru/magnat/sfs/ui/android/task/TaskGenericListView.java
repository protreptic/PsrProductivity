package ru.magnat.sfs.ui.android.task;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.task.generic.TaskGeneric;
import ru.magnat.sfs.bom.task.generic.TaskGenericEntity;
import ru.magnat.sfs.ui.android.GenericListView;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TaskGenericListView<C extends TaskGeneric<E>, E extends TaskGenericEntity<C, O>, O extends TaskGenericEntity<?, ?>> extends GenericListView<C, E, O> {

	public TaskGenericListView(Context context, C tasks, O owner) {
		super(context, tasks, owner);
	}

	@Override
	public SfsContentView inflate() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.list_item_view, this);
		ListView lv = (ListView) findViewById(R.id.list);
		lv.setAdapter(new TaskGenericListAdapter(getContext(), _catalog));
		
		return this;
	}

	public class TaskGenericListAdapter extends BaseAdapter {
		protected LayoutInflater mInflater;

		protected C list;

		public TaskGenericListAdapter(Context context, C tasks) {
			mInflater = LayoutInflater.from(context);
			this.list = tasks;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.data_item, parent,
						false);
			}
			TextView title = (TextView) convertView
					.findViewById(R.id.item_title);
			TextView sub = (TextView) convertView
					.findViewById(R.id.item_subtitle);

			if (!list.To(position))
				return null;

			title.setText(list.Current().toString());
			sub.setText((list.Current().IsCompleted) ? "Завершено"
					: "Не завершено");
			return convertView;
		}

		public int getCount() {
			return (int) list.Count();
		}

		public E getItem(int position) {
			
			if (!list.To(position))
				return null;
			return list.Current();
		}

		public long getItemId(int position) {
			if (!list.To(position))
				return 0;
			return list.Current().Id;
		}

	}

	public Boolean onBackPressed() {
		
		return null;
	}

	@Override
	protected void createEntity() {
		

	}

}
