package ru.magnat.sfs.bom.track;

import java.util.ArrayList;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.generic.DocGenericEntity;
import ru.magnat.sfs.bom.generic.DocGenericLine;
import ru.magnat.sfs.bom.marketingmeasure.OnDocMarketingMeasureChangedListener.OnLineValueChangedListener;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

public final class DocTrackLine extends DocGenericLine<DocTrackLineEntity>
		implements OnLineValueChangedListener {

	public DocTrackLine(Context context, DocTrackEntity owner) {
		super(context, DocTrackLineEntity.class, owner);

	}

	public void onLineValueChanged(
			@SuppressWarnings("rawtypes") GenericEntity sender,
			float old_value, float new_value) {
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return null;
	}

	@Override
	public DocTrackLineEntity getLine(Context context,
			@SuppressWarnings("rawtypes") GenericEntity key) {
		if (key == null)
			return null;

		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		criteria.add(new SqlCriteria("Id", key.Id));
		DocGenericEntity<?, ?> masterdoc = (DocGenericEntity<?, ?>) this._owner;
		
//		criteria.add(new SqlCriteria("MasterDocId", _id));
//		criteria.add(new SqlCriteria("MasterDocAuthor", _author));
		criteria.add(new SqlCriteria("MasterDocId", masterdoc.Id));
		criteria.add(new SqlCriteria("MasterDocAuthor", masterdoc.Author.Id));
		if (this.Select(criteria))
			if (this.Next())
				return this.Current();

		return null;
	}

	public DocTrackLineEntity getLastLine(Context context) {

		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		DocGenericEntity<?, ?> masterdoc = (DocGenericEntity<?, ?>) this._owner;
		/*criteria.add(new SqlCriteria("MasterDocId", _id));
		criteria.add(new SqlCriteria("MasterDocAuthor", _author));*/
		criteria.add(new SqlCriteria("MasterDocId", masterdoc.Id));
		criteria.add(new SqlCriteria("MasterDocAuthor", masterdoc.Author));
		if (this.Select(criteria, "ORDER BY Id DESC"))
			if (this.Next())
				return this.Current();

		return null;
	}

}
