package ru.magnat.sfs.bom.marketingphoto;

import java.util.ArrayList;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.generic.DocGeneric;
import ru.magnat.sfs.bom.marketingmeasure.DocMarketingMeasureEntity;
import ru.magnat.sfs.bom.marketingmeasure.OnDocMarketingMeasureChangedListener.OnMarketingMeasureChangedListener;
import ru.magnat.sfs.bom.marketingmeasure.OnDocMarketingMeasureChangedListener.OnOutletChangedListener;
import ru.magnat.sfs.bom.ref.marketingmeasure.RefMarketingMeasureEntity;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.task.visit.TaskVisitEntity;
import ru.magnat.sfs.ui.android.GenericListView;
import ru.magnat.sfs.ui.android.IBO;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

@SuppressWarnings("rawtypes")
public final class DocMarketingPhotoJournal extends
		DocGeneric<DocMarketingPhotoEntity, DocMarketingPhotoLineEntity>
		implements
		IBO<DocMarketingPhotoJournal, DocMarketingPhotoEntity, DocMarketingMeasureEntity> {

	public DocMarketingPhotoJournal(Context context) {
		super(context, DocMarketingPhotoEntity.class);
		
	}

	public DocMarketingPhotoEntity Current() {
		DocMarketingPhotoEntity entity = (DocMarketingPhotoEntity) super
				.Current();
		if (entity != null) {

			entity.setOnMarketingMeasureChangedListener(new OnMarketingMeasureChangedListener() {

				public void onMarketingMeasureChanged(GenericEntity sender,
						RefMarketingMeasureEntity old_value,
						RefMarketingMeasureEntity new_value) {
					save();

				}

			});
			entity.setOnOutletChangedListener(new OnOutletChangedListener() {

				public void onOutletChanged(GenericEntity sender,
						RefOutletEntity old_value, RefOutletEntity new_value) {
					save();

				}

			});

		}
		return entity;
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return null;
		//return new DocMarketingMeasureListViewItem(_context, this, lv,
		//		Current());
	}

	public GenericListView<DocMarketingPhotoJournal, DocMarketingPhotoEntity, TaskVisitEntity> GetSelectView(
			TaskVisitEntity visit) {
		return null;
		//return new DocMarketingMeasureListView(_context, visit);
	}

	public GenericListView<DocMarketingPhotoJournal, DocMarketingPhotoEntity, DocMarketingMeasureEntity> GetListView(
			DocMarketingMeasureEntity measure) {
		return null;
		//return new DocMarketingMeasureListView(_context, visit);
	}

	public SfsContentView GetViewView(DocMarketingPhotoEntity entity) {
		return null;
		//return new DocMarketingMeasureEntityView(_context, this, entity);
	}

	public SfsContentView GetEditView(DocMarketingPhotoEntity entity) {
		return null;// return new DocMarketingMeasureEntityView(_context, this, entity);
	}

	public void selectByOutlet(RefOutletEntity outlet) {

		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		criteria.add(new SqlCriteria("Outlet", outlet.Id));
		super.Select(criteria, "ORDER BY ID DESC");

	}

	@Override
	protected GenericEntity<?> getGenericTaskEntity(String class_name, int id) {
		if (class_name.equals("MasterTask")) {
			return searchGenericEntity(TaskVisitEntity.class, id);
		}
		return null;
	}

	@Override
	public GenericListView<DocMarketingPhotoJournal, DocMarketingPhotoEntity, DocMarketingMeasureEntity> GetSelectView(
			DocMarketingMeasureEntity owner) {
		
		return null;
	}
}
