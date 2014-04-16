package ru.magnat.sfs.util;

import android.widget.ListAdapter;


public interface BaseTreeListAdapter extends ListAdapter {
	
	public void changeParent(TreeNode parent);
	public TreeNode getParent();
	public boolean hasChild(int position);
	public TreeNode getNode(int position);
}
