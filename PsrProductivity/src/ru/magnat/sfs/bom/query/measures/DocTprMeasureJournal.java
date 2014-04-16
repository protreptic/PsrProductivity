package ru.magnat.sfs.bom.query.measures;

import java.util.ArrayList;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.generic.DocGeneric;
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
public class DocTprMeasureJournal extends DocGeneric<DocTprMeasureEntity, DocTprMeasureLineEntity> implements IBO<DocTprMeasureJournal, DocTprMeasureEntity, TaskVisitEntity> {

	public DocTprMeasureJournal(Context context) {
		super(context, DocTprMeasureEntity.class);
	}

	public DocTprMeasureEntity Current() {
		DocTprMeasureEntity entity = (DocTprMeasureEntity) super.Current();
		if (entity != null) {
			entity.setOnMarketingMeasureChangedListener(new OnMarketingMeasureChangedListener() {
				public void onMarketingMeasureChanged(GenericEntity sender, RefMarketingMeasureEntity oldEntity, RefMarketingMeasureEntity newEntity) {
					save();
				}
			});
			entity.setOnOutletChangedListener(new OnOutletChangedListener() {
				public void onOutletChanged(GenericEntity sender, RefOutletEntity old_value, RefOutletEntity new_value) {
					save();
				}
			});
		}
		
		return entity;
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return new DocTprMeasureListViewItem(_context, this, lv, Current());
	}

	public GenericListView<DocTprMeasureJournal, DocTprMeasureEntity, TaskVisitEntity> GetSelectView(TaskVisitEntity visit) {
		//return new DocTprMeasureListView(_context, visit);
		return null;
	}

	public GenericListView<DocTprMeasureJournal, DocTprMeasureEntity, TaskVisitEntity> GetListView(TaskVisitEntity visit) {
		//return new DocTprMeasureListView(_context, visit);
		return null;
	}
	
	@Override
	public SfsContentView GetEditView(DocTprMeasureEntity entity) {
		return new DocTprMeasureEntityView(_context, this, entity);
	}
	
	public SfsContentView GetViewView(DocTprMeasureEntity entity) {
		return new DocTprMeasureEntityView(_context, this, entity);
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

}
