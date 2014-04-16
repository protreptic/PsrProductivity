package ru.magnat.sfs.bom.contract;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.generic.DocGeneric;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayEntity;
import ru.magnat.sfs.ui.android.GenericListView;
import ru.magnat.sfs.ui.android.IBO;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.doc.contract.DocContractView;
import android.content.Context;
import android.widget.ListView;

public class DocContractJournal extends DocGeneric<DocContractEntity, DocContractLineEntity> implements IBO<DocContractJournal, DocContractEntity, TaskWorkdayEntity> {
	
	public DocContractJournal(Context context) {
		super(context, DocContractEntity.class);
	}

	@Override
	protected GenericEntity<?> getGenericTaskEntity(String class_name, int id) {
		if (class_name.equals("MasterTask")) {
			return searchGenericEntity(TaskWorkdayEntity.class, id);
		}
		return null;
	}

	@Override
	public SfsContentView GetViewView(DocContractEntity entity) {
		return null;
	}

	@Override
	public SfsContentView GetEditView(DocContractEntity entity) {
		return new DocContractView(getContext(), this, entity);
	}

	@Override
	public GenericListView<DocContractJournal, DocContractEntity, TaskWorkdayEntity> GetListView(TaskWorkdayEntity owner) {
		return null;
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return null;
	}

	@Override
	public GenericListView<DocContractJournal, DocContractEntity, TaskWorkdayEntity> GetSelectView(TaskWorkdayEntity owner) {
		return null;
	}
}
