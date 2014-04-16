package ru.magnat.sfs.ui.android.extras;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.query.extras.QueryGetExtrasType;
import ru.magnat.sfs.bom.query.extras.QueryGetExtrasTypeEntity;
import ru.magnat.sfs.ui.android.GenericListView;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.SfsListType;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

@SuppressWarnings("rawtypes")
public final class ExtrasTypeListView extends GenericListView<QueryGetExtrasType, QueryGetExtrasTypeEntity, GenericEntity> {
	
	final int _monthOffset;
	final String _currentTab;
	
	public ExtrasTypeListView(Context context, int  monthOffset, String currentTab) {
		super(context, null, null);
		_monthOffset = monthOffset;
		_currentTab = currentTab;
	}

	@Override
	protected void requery() {
		if (this._catalog == null) {
			this._catalog = new  QueryGetExtrasType(getContext(), _monthOffset);
		}
		this._catalog.SetListType(SfsListType.SIMPLE_LIST);
		this._catalog.Select();
		this._catalog.notifyDataSetChanged();
	}
	
	@Override
	protected void OpenEntity(QueryGetExtrasTypeEntity entity) {
		  Intent intent = new Intent(getContext(), SfsExtrasFilesActivity.class);
		  intent.putExtra(SfsExtrasFilesActivity.CURR_TAB, _currentTab); 
		  intent.putExtra(SfsExtrasFilesActivity.TYPE_ID, entity.Id); 
		  intent.putExtra(SfsExtrasFilesActivity.TYPE_NAME, entity.Descr);
		  
		  ((Activity) getContext()).startActivity(intent);
	}
	
	@Override
	public SfsContentView inflate() {	
		SfsContentView v = super.сatalogInflate(R.layout.doc_extra_files_list_view, "Дополнительные материалы");
		return v;	
	}

	@Override
	protected void createEntity() {}

}