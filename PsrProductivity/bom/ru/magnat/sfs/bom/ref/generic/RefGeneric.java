package ru.magnat.sfs.bom.ref.generic;

import java.lang.annotation.Annotation;
import java.util.ArrayList;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.OrmObject;
import ru.magnat.sfs.bom.OrmOwner;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayJournal;
import ru.magnat.sfs.util.BaseTreeListAdapter;
import ru.magnat.sfs.util.OnTreeParentChangedListener;
import ru.magnat.sfs.util.TreeNode;
import ru.magnat.sfs.util.TreeNodeView;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

@SuppressWarnings("rawtypes")
public abstract class RefGeneric<E extends RefGenericEntity<? extends RefGeneric<E>, ?>>
		extends OrmObject<E> implements BaseTreeListAdapter {

	private RefGenericEntity _parent;
	private RefGenericEntity _owner;
	protected Class<?> _ownerType;

	public void SetParent(RefGenericEntity entity) {
		_parent = entity;
	}

	public void SetOwner(RefGenericEntity entity) {
		_owner = entity;
	}

	public RefGeneric(Context context, Class<?> entityType) {
		super(context, entityType);
		Annotation a = this.getClass().getAnnotation(OrmOwner.class);
		if (a instanceof OrmOwner) {
			_ownerType = ((OrmOwner) a).owner();
		}
	}

	@Override
	protected Boolean ReadEntity() {
		if (!super.ReadEntity())
			return false;

		if (_ownerType != null) {
			return ReadOwner((RefGenericEntity) this._current);
		}

		return true;
	}

	@Override
	protected GenericEntity<?> getGenericRefEntity(String class_name, int id) {
		// if (class_name.equals("ParentExt")){
		// return searchGenericEntity(_ownerType, id);
		// } else if (class_name.equals("Parent")){
		// return searchGenericEntity(_entityType, id);
		// }
		return null;
	}

	protected Boolean ReadOwner(RefGenericEntity entity) {
		try {
			if (this._cursor.isNull("ParentExt"))
				return false;
			int ord = this._cursor.getOrdinal("ParentExt");
			entity.ParentExt = (RefGenericEntity) castGenericEntity(_ownerType,
					ord);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/*
	 * public RefGenericEntity Current() { return
	 * (RefGenericEntity)super.Current(); }
	 */

	@Override
	public final Boolean Find(E entity) {

		return (FindById(entity.Id) != null);

	}

	public enum HierarchyMode {
		OnlyEntity, OnlyGroup, Both
	}

	public Boolean Select(Boolean byOwner, Boolean byParent,
			HierarchyMode hierarchyMode) {
		return Select(byOwner, byParent, hierarchyMode, " ORDER BY DESCR ");
	}
	
	public Boolean Select(Boolean byOwner, Boolean byParent,
			HierarchyMode hierarchyMode, String sort) {
		try {
			ChangeContext(OperationContext.FETCHING);
			if (_cursor != null)
				_cursor.close();
			ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();

			if (byOwner) {
				if (_owner != null) {
					criteria.add(new SqlCriteria("ParentExt", this._owner.Id));
				}
			}
			if (byParent) {
				if (_parent != null) {
					criteria.add(new SqlCriteria("Parent", this._parent.Id));
				} else {
					criteria.add(new SqlCriteria("COALESCE(Parent,0)", 0));
				}
			}
			switch (hierarchyMode) {
			case OnlyEntity:
				criteria.add(new SqlCriteria("IsGroup", 0));
				break;
			case OnlyGroup:
				criteria.add(new SqlCriteria("IsGroup", 1));
				break;
			}

			super.Select(criteria, sort);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public TreeNodeView createNodeItemView(int position) {
		return new TreeNodeView(_context, this.getNode(position),
				this.hasChild(position));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = createNodeItemView(position);
		((TreeNodeView) convertView)
				.setOnTreeParentChangedListener(new OnTreeParentChangedListener() {

					public void onTreeParentChanged(Object sender,
							TreeNode value) {
					
						changeParent(value);

					}

				});

		return convertView;

	}

	public void changeParent(TreeNode parent) {
		_parent = (RefGenericEntity) parent;
		this.Select(false, true, HierarchyMode.OnlyGroup);

		notifyDataSetChanged();
		notifyDataSetInvalidated();

	}

	public TreeNode getParent() {

		return _parent;
	}

	public boolean hasChild(int position) {

		return true;
	}

	public TreeNode getNode(int position) {
		
		return (TreeNode) getItem(position);
	}

}
