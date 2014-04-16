package ru.magnat.sfs.util;



import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import ru.magnat.sfs.android.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class TreeControlView extends RelativeLayout {
	final Context _context;
	
	final ListView _list;

	private BaseTreeListAdapter _tree;
	
	public TreeControlView(Context context) {
		super(context);
		
		_context = null;
		_list = null;
	}
	
	public TreeControlView(Context context,BaseTreeListAdapter tree) {
		super(context);
		_context = context;
		_tree = tree;
		 LayoutInflater.from(context).inflate(R.layout.tree_level_layout, this, true);
		 _list = ((ListView) findViewById(R.id.listViewLevel));
		 _list.setAdapter(_tree);
		 ((ImageButton) findViewById(R.id.imageButtonBack)).setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				TreeNode p = _tree.getParent();
				if (p==null) return;
				_tree.changeParent(p.getParent());
				
			}
			 
		 });
		 /*
		 ((ImageButton) findViewById(R.id.imageButtonClear)).setOnClickListener(new OnClickListener(){

				public void onClick(View v) {
					fireTreeNodeSelectedEvent(null); 
					
				}
				 
			 });
			 */
		 ((ImageButton) findViewById(R.id.imageButtonCancel)).setOnClickListener(new OnClickListener(){

				public void onClick(View v) {
					//fireCancelEvent();
					fireTreeNodeSelectedEvent(null); 
					
					
				}
				 
			 });
		 _list.setOnItemLongClickListener(new OnItemLongClickListener(){

			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (!_tree.hasChild(position)) return false;
				TreeNode p = _tree.getNode(position);
				if (p==null) return false;
				_tree.changeParent(p);
				
				return false;
			}
			 
		 });
		 _list.setOnItemClickListener(new OnItemClickListener(){

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				fireTreeNodeSelectedEvent( _tree.getNode(position)); 
				
			}

				
				 
			 });
		
		 
	}
	public void inflate(){
		_list.setAdapter(_tree);
	}
	
	 private final Set<OnTreeControlEventsListener> _eventTreeControlListeners = new CopyOnWriteArraySet<OnTreeControlEventsListener>();

		public void setOnTreeControlEventsListener(OnTreeControlEventsListener eventListener) {
			if (eventListener == null) {
				throw new NullPointerException();
			}
			_eventTreeControlListeners.clear();
			_eventTreeControlListeners.add(eventListener);
			
		}
		
		public void addOnTreeControlEventsListener(OnTreeControlEventsListener eventListener) {
			if (eventListener == null) {
				throw new NullPointerException();
			}
			_eventTreeControlListeners.add(eventListener);
		}

		public void removeOnTreeControlEventsListener(OnTreeControlEventsListener eventListener) {
			if (eventListener == null) {
				throw new NullPointerException();
			}
			_eventTreeControlListeners.remove(eventListener);
		}

		private void fireTreeNodeSelectedEvent(TreeNode value) {
			for (OnTreeControlEventsListener eventListener : _eventTreeControlListeners) {
				eventListener.onTreeNodeSelected(this,value);
			}
		}
//		private void fireCancelEvent() {
//			for (OnTreeControlEventsListener eventListener : _eventTreeControlListeners) {
//				eventListener.onCancel(this);
//			}
//			
//		}
		
}
