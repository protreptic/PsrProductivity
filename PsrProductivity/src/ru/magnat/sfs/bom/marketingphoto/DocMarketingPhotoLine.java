package ru.magnat.sfs.bom.marketingphoto;

import java.util.ArrayList;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.generic.DocGenericEntity;
import ru.magnat.sfs.bom.generic.DocGenericLine;
import ru.magnat.sfs.bom.marketingmeasure.OnDocMarketingMeasureChangedListener.OnLinePhotoChangedListener;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

@SuppressWarnings("rawtypes")
public final class DocMarketingPhotoLine extends
		DocGenericLine<DocMarketingPhotoLineEntity> implements
		 OnLinePhotoChangedListener {

	public DocMarketingPhotoLine(Context context,
			DocMarketingPhotoEntity owner) {
		super(context, DocMarketingPhotoLineEntity.class, owner);

	}

	@Override
	public final DocMarketingPhotoLineEntity getLine(Context context,
			GenericEntity entity) {
		if (entity == null)
			return null;
		DocGenericEntity<?, ?> masterdoc = (DocGenericEntity<?, ?>) this._owner;
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		//criteria.add(new SqlCriteria("MasterDocId", _id));
		//criteria.add(new SqlCriteria("MasterDocAuthor", _author));
		criteria.add(new SqlCriteria("MasterDocId", masterdoc.Id));
		criteria.add(new SqlCriteria("MasterDocAuthor", masterdoc.Author.Id));
		criteria.add(new SqlCriteria("MarketingObject", entity.Id));
		if (this.Select(criteria))
			if (this.Next())
				return this.Current();

		return null;
	}


	

	@Override
	public DocMarketingPhotoLineEntity next() {
		return (DocMarketingPhotoLineEntity) super.next();
	}

	public void onLinePhotoChanged(GenericEntity sender, String old_value,
			String new_value) {
		save();

	}



	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

	public DocMarketingPhotoLineEntity getLine(Context context, String photo) {
		if (photo == null)
			return null;
		DocGenericEntity<?, ?> masterdoc = (DocGenericEntity<?, ?>) this._owner;
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		//criteria.add(new SqlCriteria("MasterDocId", _id));
		//criteria.add(new SqlCriteria("MasterDocAuthor", _author));
		criteria.add(new SqlCriteria("MasterDocId", masterdoc.Id));
		criteria.add(new SqlCriteria("MasterDocAuthor", masterdoc.Author.Id));
		criteria.add(new SqlCriteria("FileName", photo));
		if (this.Select(criteria))
			if (this.Next())
				return this.Current();

		return null;
	}

}
