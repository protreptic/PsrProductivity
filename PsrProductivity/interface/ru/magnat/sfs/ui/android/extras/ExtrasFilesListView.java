/**
 * 
 */
package ru.magnat.sfs.ui.android.extras;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageButton;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.query.extras.QueryGetExtrasFiles;
import ru.magnat.sfs.bom.query.extras.QueryGetExtrasFilesEntity;
import ru.magnat.sfs.ui.android.GenericListView;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.SfsListType;

/**
 * @author alex_us
 * 
 */
@SuppressWarnings("rawtypes")
public final class ExtrasFilesListView
		extends
		GenericListView<QueryGetExtrasFiles, QueryGetExtrasFilesEntity, GenericEntity> {
	final int _monthOffset;
	final long _extrasType;
	final String _extrasName;
	final String _currentTab;
	public ExtrasFilesListView(Context context, int  monthOffset,long extrasType,String extrasName, String currentTab) {
		super(context, null, null);
		_monthOffset = monthOffset;
		_extrasType = extrasType;
		_extrasName = extrasName;
		_currentTab = currentTab;
	}
	
	@Override
	protected void requery() {

		if (this._catalog == null)
			this._catalog = new  QueryGetExtrasFiles(getContext(),_monthOffset,_extrasType);
		this._catalog.SetListType(SfsListType.SIMPLE_LIST);
		this._catalog.Select();
		this._catalog.notifyDataSetChanged();

	}
	@Override
	protected void OpenEntity(QueryGetExtrasFilesEntity entity) {
		Globals.openExtrasFile((Activity) getContext(),
				entity.FileName,false);
	}
	@Override
	public SfsContentView inflate() {
		//requery();
		
		SfsContentView v =super.ñatalogInflate(R.layout.doc_extra_files_list_view,
				_extrasName);
		ImageButton b = (ImageButton) findViewById(R.id.caption_action);
		b.setVisibility(GONE);
		return v;
	
	}

	@Override
	protected void createEntity() {}

}
