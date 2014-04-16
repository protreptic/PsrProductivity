package ru.magnat.sfs.ui.android.doc.marketingmeasure;

import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.marketingmeasure.DocMarketingMeasureEntity;
import ru.magnat.sfs.bom.marketingmeasure.DocMarketingMeasureJournal;
import ru.magnat.sfs.bom.ref.generic.RefGeneric.HierarchyMode;
import ru.magnat.sfs.bom.ref.marketingmeasure.RefMarketingMeasure;
import ru.magnat.sfs.bom.ref.marketingmeasure.RefMarketingMeasureEntity;
import ru.magnat.sfs.bom.task.visit.TaskVisitEntity;
import ru.magnat.sfs.ui.android.GenericListView;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.SfsListType;
import android.content.Context;
import android.content.DialogInterface;

public final class DocMarketingMeasureListView extends GenericListView<DocMarketingMeasureJournal, DocMarketingMeasureEntity, TaskVisitEntity> {
	
	public DocMarketingMeasureListView(Context context, TaskVisitEntity visit) {
		super(context, null, visit);
	}

	protected void CreateMarketingMeasure(RefMarketingMeasureEntity measure, TaskVisitEntity visit) {
		DocMarketingMeasureJournal docs = _catalog;
		docs.NewEntity();
		DocMarketingMeasureEntity doc = docs.Current();
		if (doc == null) {
			Dialogs.MessageBox("Не удалось создать измерение");
			return;
		}
		if (visit == null) {
			Dialogs.MessageBox("Измерения можно создавать только в рамках визита");
			return;
		}
		doc.setDefaults(getContext(), visit);
		doc.MarketingMeasure = (RefMarketingMeasureEntity) measure.clone();
		doc.MasterTask = visit;
		if (!docs.save()) {
			Dialogs.MessageBox("Не удалось записать созданное измерение");
			return;
		}
		OpenEntity(doc);
	}

	@Override
	protected void createEntity() {
		RefMarketingMeasure ref = (RefMarketingMeasure) Globals.createOrmObject(RefMarketingMeasure.class);
		ref.Select(false, false, HierarchyMode.OnlyEntity);
		int i = ref.getCount();
		switch (i) {
			case 0:
				Dialogs.MessageBox("Измерения для пользователя не назначены");
				return;
			case 1:
				CreateMarketingMeasure((RefMarketingMeasureEntity) ref.getItem(0), _owner);
				return;
		default:
			Dialogs.createSelectFromListDialog(ref, "Выбор измерения", new android.content.DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					RefMarketingMeasure ref = (RefMarketingMeasure) Globals.createOrmObject(RefMarketingMeasure.class);
					ref.Select(false, false, HierarchyMode.OnlyEntity);
					RefMarketingMeasureEntity measure = null;
					try {
						measure = (RefMarketingMeasureEntity) ref.getItem(which);
					} catch (Exception e) {}
					if (measure == null) {
						Dialogs.MessageBox("Не выбрано измерение");
						return;
					}
					CreateMarketingMeasure(measure, _owner);
				}
			}).show();
		}
	}

	@Override
	protected void requery() {
		if (_catalog == null) {
			_catalog = new DocMarketingMeasureJournal(getContext());
		}
		_catalog.SetListType(SfsListType.EXTENDED_LIST);
		if (_owner != null) {
			_catalog.selectByOutlet(this._owner.Outlet);
		} else {
			_catalog.Select(null, "ORDER BY Id DESC");
		}
		_catalog.notifyDataSetChanged();
	}

	@Override
	public SfsContentView inflate() {
		requery();
		
		return super.сatalogInflate(R.layout.doc_marketing_measure_list_view, "ИЗМЕРЕНИЯ");
	}
	
}
