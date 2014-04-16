package ru.magnat.sfs.bom.query.measures;

import java.util.ArrayList;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.generic.DocGenericEntity;
import ru.magnat.sfs.bom.generic.DocGenericLine;
import ru.magnat.sfs.bom.marketingmeasure.OnDocMarketingMeasureChangedListener.OnLinePhotoChangedListener;
import ru.magnat.sfs.bom.marketingmeasure.OnDocMarketingMeasureChangedListener.OnLineValueChangedListener;
import ru.magnat.sfs.bom.ref.csku.RefCskuEntity;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

@SuppressWarnings("rawtypes")
public class DocTprMeasureLine extends DocGenericLine<DocTprMeasureLineEntity> implements OnLineValueChangedListener, OnLinePhotoChangedListener {

	public DocTprMeasureLine(Context context, DocTprMeasureEntity owner) {
		super(context, DocTprMeasureLineEntity.class, owner);
	}

	@Override
	public DocTprMeasureLineEntity getLine(Context context, GenericEntity entity) {
		if (entity == null) {
			return null;
		}
		DocGenericEntity<?, ?> masterdoc = (DocGenericEntity<?, ?>) _owner;
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		criteria.add(new SqlCriteria("MasterDocId", masterdoc.Id));
		criteria.add(new SqlCriteria("MasterDocAuthor", masterdoc.Author.Id));
		criteria.add(new SqlCriteria("TprSubject", entity.Id));
		if (this.Select(criteria)) {
			if (this.Next()) {
				return this.Current();
			}
		}
		
		return null;
	}

	public DocTprMeasureLineEntity findByTprSubject(RefCskuEntity entity) {
		
		
		return null;
	}
	
	@Override
	public DocTprMeasureLineEntity Current() {
		DocTprMeasureLineEntity entity = (DocTprMeasureLineEntity) super.Current();
		entity.setOnLineValueChangedListener(this);

		return entity;
	}

	public void onLineValueChanged(GenericEntity sender, Float old_value, Float new_value) {
		save();
	}

	@Override
	public DocTprMeasureLineEntity next() {
		return (DocTprMeasureLineEntity) super.next();
	}

	public void onLinePhotoChanged(GenericEntity sender, String old_value, String new_value) {
		save();
	}

	public void onLineValueChanged(GenericEntity sender, float old_value, float new_value) {
		
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return null;
	}

}
