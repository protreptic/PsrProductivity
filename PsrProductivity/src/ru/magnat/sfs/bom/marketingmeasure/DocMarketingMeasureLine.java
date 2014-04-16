package ru.magnat.sfs.bom.marketingmeasure;

import java.util.ArrayList;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.generic.DocGenericEntity;
import ru.magnat.sfs.bom.generic.DocGenericLine;
import ru.magnat.sfs.bom.marketingmeasure.OnDocMarketingMeasureChangedListener.OnLinePhotoChangedListener;
import ru.magnat.sfs.bom.marketingmeasure.OnDocMarketingMeasureChangedListener.OnLineValueChangedListener;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

@SuppressWarnings("rawtypes")
public final class DocMarketingMeasureLine extends DocGenericLine<DocMarketingMeasureLineEntity> implements OnLineValueChangedListener, OnLinePhotoChangedListener {

	public DocMarketingMeasureLine(Context context, DocMarketingMeasureEntity owner) {
		super(context, DocMarketingMeasureLineEntity.class, owner);
	}

	@Override
	public final DocMarketingMeasureLineEntity getLine(Context context, GenericEntity entity) {
		return getLine(context, entity.Id);
	}
	
	public final DocMarketingMeasureLineEntity getLine(Context context, long id) {
		DocMarketingMeasureLineEntity founded = null;
		DocGenericEntity<?, ?> masterdoc = (DocGenericEntity<?, ?>) _owner;
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		criteria.add(new SqlCriteria("MasterDocId", masterdoc.Id));
		criteria.add(new SqlCriteria("MasterDocAuthor", masterdoc.Author.Id));
		criteria.add(new SqlCriteria("MarketingObject", id));
		if (Select(criteria))
			founded = next();
		return founded;
	}

	@Override
	public DocMarketingMeasureLineEntity Current() {
		DocMarketingMeasureLineEntity entity = (DocMarketingMeasureLineEntity) super.Current();
		entity.setOnLineValueChangedListener(this);

		return entity;
	}

	public void onLineValueChanged(GenericEntity sender, Float old_value, Float new_value) {
		save();
	}

	@Override
	public DocMarketingMeasureLineEntity next() {
		return (DocMarketingMeasureLineEntity) super.next();
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
