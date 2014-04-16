package ru.magnat.sfs.ui.android.task.visit;

import java.util.ArrayList;
import java.util.List;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.task.visit.TaskVisitEntity;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.SimpleGenericEntityView;
import ru.magnat.sfs.ui.android.ref.customer.CustomerEntityView;
import ru.magnat.sfs.ui.android.ref.outlet.OutletEntityView;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class VisitCustomerTab extends SfsContentView {

	private TaskVisitEntity mEntity;
	
	public VisitCustomerTab(Context context, TaskVisitEntity visit) {
		super(context);
		
		mEntity = visit;
		
		layoutInflater.inflate(R.layout.expandable_list_layout, this);

		ExpandableListView list = (ExpandableListView) findViewById(R.id.expandable_list);
		list.setAdapter(new VisitContentAdapter(getContext(), mEntity));
		list.expandGroup(0);
		list.setOnChildClickListener(new OnChildClickListener() {

			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				switch (groupPosition) {
					case 0: {
						Intent intent = new Intent(MainActivity.getInstance().getApplicationContext(), StoreInformationChangeRequestActivity.class);
						intent.putExtra("storeId", mEntity.Outlet.Id);
						MainActivity.sInstance.startActivity(intent);
					} break;
					case 1: {
						Globals.openOrCreateChangeCustomerRequest(mEntity.Outlet.ParentExt);
					} break;
					default: {}	break;
				}

				return false;
			}

		});
	}

	@Override
	public SfsContentView inflate() {
		return this;
	}

	private class VisitContentAdapter extends BaseExpandableListAdapter {
		private List<ContentItem> _sections = new ArrayList<ContentItem>();

		public VisitContentAdapter(Context context, TaskVisitEntity entity) {
			_sections.add(new ContentItem("Торговая точка", new OutletEntityView(context, entity.Outlet)));
			_sections.add(new ContentItem("Заказчик", new CustomerEntityView(context, entity.Outlet.ParentExt)));
		}

		private class ContentItem {
			
			public String Caption;
			public SfsContentView View;
			
			public ContentItem(String caption, SfsContentView v) {
				Caption = caption;
				View = v;
			}

		}

		public int getGroupCount() {
			return _sections.size();
		}

		public int getChildrenCount(int groupPosition) {
			View v = _sections.get(groupPosition).View;

			if (v instanceof SimpleGenericEntityView) {
				return ((SimpleGenericEntityView<?, ?>) v).getCount();
			}
			return 1;
		}

		public Object getGroup(int groupPosition) {
			return null;
		}

		public Object getChild(int groupPosition, int childPosition) {
			View v = _sections.get(groupPosition).View;

			if (v instanceof SimpleGenericEntityView) {
				return ((SimpleGenericEntityView<?, ?>) v).getEntity();
			}
			return null;
		}

		public long getGroupId(int groupPosition) {
			GenericEntity<?> e = (GenericEntity<?>) getChild(groupPosition, 0);
			if (e == null) {
				return 0;
			}
			
			return e.Id;
		}

		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		public boolean hasStableIds() {
			return false;
		}

		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			RelativeLayout v = new RelativeLayout(getContext());
			TextView t = new TextView(getContext());
			t.setText(_sections.get(groupPosition).Caption);
			t.setTextAppearance(getContext(), android.R.style.TextAppearance_Large);
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(60, 20, 20, 20);
			t.setLayoutParams(layoutParams);
			v.addView(t);
			
			return v;
		}

		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			SfsContentView v = _sections.get(groupPosition).View;

			if (v instanceof SimpleGenericEntityView) {
				return ((SimpleGenericEntityView<?, ?>) v).getChildView(
						childPosition, convertView, parent);
			}
			return v.inflate();
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}

	}

}
