package ru.magnat.sfs.ui.android.doc.contract;

import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.contract.DocContractEntity;
import ru.magnat.sfs.bom.contract.DocContractJournal;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayEntity;
import ru.magnat.sfs.ui.android.GenericListView;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.SfsListType;
import android.content.Context;

public class DocContractListView extends GenericListView<DocContractJournal, DocContractEntity, TaskWorkdayEntity> {
	
	public DocContractListView(Context context, TaskWorkdayEntity Workday) {
		super(context, null, Workday);
	}

	@Override
	public SfsContentView inflate() {
		requery();
		сatalogInflate(R.layout.doc_order_list_view, "Стандартный договор КА");
		showAddButton();
		return this;
	}

	protected void CreateOrder(TaskWorkdayEntity workday) {
		DocContractJournal docs = new DocContractJournal(getContext());
		docs.NewEntity();
		DocContractEntity doc = docs.Current();

		doc.setDefaults(getContext(), null);

		if (!docs.save()) {
			Dialogs.MessageBox("Не удалось записать созданного контрагента");
			return;
		}
		docs.close();
		OpenEntity(doc);
	}

	@Override
	protected void createEntity() {
		CreateOrder(_owner);
	}

	@Override
	protected void requery() {
		if (_catalog == null) {
			_catalog = new DocContractJournal(getContext());
		}
		_catalog.SetListType(SfsListType.EXTENDED_LIST);
		_catalog.Select(null, "ORDER BY Id DESC");
		_catalog.notifyDataSetChanged();
	}
	
}
