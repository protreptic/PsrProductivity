package ru.magnat.sfs.util;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import ru.magnat.sfs.android.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TreeNodeView extends RelativeLayout{

	private TreeNode _node;
	public TreeNodeView(Context context,TreeNode node,Boolean hasChild) {
		super(context);
		_node = node;
		
		 LayoutInflater.from(context).inflate(R.layout.tree_node_layout, this, true);
		 if (node==null) return;
		 ((TextView) findViewById(R.id.nodeText)).setText(node.getCaption());
		 ((TextView) findViewById(R.id.nodeFooter)).setText(node.getFooter());
		 if (hasChild){
			 ((ImageButton) findViewById(R.id.imageButtonNext)).setOnClickListener(new OnClickListener(){
	
				public void onClick(View v) {
					fireTreeParentChangedEvent(_node);
					
				}
				 
			 });
		 }
		 else{
			 ((ImageButton) findViewById(R.id.imageButtonNext)).setVisibility(View.INVISIBLE);
		 }
	}
		 private final Set<OnTreeParentChangedListener> _eventTreeParentChangedListeners = new CopyOnWriteArraySet<OnTreeParentChangedListener>();

			public void setOnTreeParentChangedListener(OnTreeParentChangedListener eventListener) {
				if (eventListener == null) {
					throw new NullPointerException();
				}
				_eventTreeParentChangedListeners.clear();
				_eventTreeParentChangedListeners.add(eventListener);
				
			}
			
			public void addOnTreeParentChangedListener(OnTreeParentChangedListener eventListener) {
				if (eventListener == null) {
					throw new NullPointerException();
				}
				_eventTreeParentChangedListeners.add(eventListener);
			}

			public void removeOnTreeParentChangedListener(OnTreeParentChangedListener eventListener) {
				if (eventListener == null) {
					throw new NullPointerException();
				}
				_eventTreeParentChangedListeners.remove(eventListener);
			}

			private void fireTreeParentChangedEvent(TreeNode value) {
				for (OnTreeParentChangedListener eventListener : _eventTreeParentChangedListeners) {
					eventListener.onTreeParentChanged(this,value);
				}
			}
}
