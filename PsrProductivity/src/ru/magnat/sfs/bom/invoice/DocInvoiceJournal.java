package ru.magnat.sfs.bom.invoice;

import java.util.ArrayList;
import java.util.Date;

import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.generic.DocGeneric;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.task.visit.TaskVisitEntity;
import ru.magnat.sfs.ui.android.GenericListView;
import ru.magnat.sfs.ui.android.IBO;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.widget.ListView;

@SuppressWarnings("rawtypes")
public final class DocInvoiceJournal extends
		DocGeneric<DocInvoiceEntity, DocInvoiceLineEntity> implements
		IBO<DocInvoiceJournal, DocInvoiceEntity, TaskVisitEntity> {

	public DocInvoiceJournal(Context context) {
		super(context, DocInvoiceEntity.class);
		
	}

	
	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return null;
	}

	public void onAmountChanged(GenericEntity sender, float old_value,
			float new_value) {
		try {
			save();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public void selectByOutlet(RefOutletEntity outlet) {

		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		criteria.add(new SqlCriteria("Outlet", outlet.Id));
		criteria.add(new SqlCriteria("CreateDate",Utils.AddDay(Utils.RoundDate(new Date()), -31),">"));
		super.Select(criteria, "ORDER BY ID DESC");

	}

	@Override
	public Boolean save() {
		if (this._current!=null){
			if (!this._current.getReadOnly())
				this._current.SaveDate = new Date();
		}
		Boolean result = super.save();
		return result;
	}

	@Override
	protected GenericEntity<?> getGenericTaskEntity(String class_name, int id) {
		if (class_name.equals("MasterTask")) {
			return searchGenericEntity(TaskVisitEntity.class, id);
		}
		return null;
	}

	@Override
	public GenericListView<DocInvoiceJournal, DocInvoiceEntity, TaskVisitEntity> GetSelectView(
			TaskVisitEntity owner) {
		
		return null;
	}

	@Override
	public SfsContentView GetViewView(DocInvoiceEntity entity) {
		return null;
	}

	@Override
	public GenericListView<DocInvoiceJournal, DocInvoiceEntity, TaskVisitEntity> GetListView(
			TaskVisitEntity owner) {
		return null;
	}

	@Override
	public SfsContentView GetEditView(DocInvoiceEntity entity) {
		return null;
	}
}
