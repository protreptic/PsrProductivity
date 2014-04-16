package ru.magnat.sfs.bom.query.targets;

import java.util.ArrayList;

import com.ianywhere.ultralitejni12.ULjException;

import ru.magnat.sfs.bom.InternalStorage;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.query.QueryGeneric;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.report.target.InitiativeTargetListViewItem;
import ru.magnat.sfs.ui.android.report.target.TargetListViewItem;
import android.content.Context;
import android.widget.ListView;

public abstract class QueryGetTarget extends QueryGeneric<QueryGetTargetEntity> {

	public QueryGetTarget(Context context, String query) {
		super(context, QueryGetTargetEntity.class, query);
		if (prepareSelect(null, null))
			setSelectParameters(null);

	}

	protected boolean _prepared = false;

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		if (this.Current()==null)
			return new TargetListViewItem(_context, this, lv, this.Current());
		else if (this.Current().Initiative==null)
			return new TargetListViewItem(_context, this, lv, this.Current());
		return new InitiativeTargetListViewItem(_context, this, lv, this.Current());
	}
	@Override
	public void close(){
		super.close();
		_prepared = false;
	} 
	@Override
	abstract public Boolean setSelectParameters(ArrayList<SqlCriteria> criteria);

	@Override
	public Boolean prepareSelect(ArrayList<SqlCriteria> criteria,
			String orderFactor) {
		try {

			ChangeContext(OperationContext.FETCHING);
			if (_prepared)
				return true;

			if (InternalStorage.getConnection() == null)
				return false;
			if (_cursor != null)
				this.close();

			if (_select_statement != null)
				try {
					_select_statement.close();
				} catch (ULjException e) {
				}

			this._select_statement = InternalStorage.getConnection()

			.prepareStatement(_query);

		} catch (ULjException e) {
			e.getStackTrace();
			return false;
		}
		_prepared = true;
		return true;
	}
}
