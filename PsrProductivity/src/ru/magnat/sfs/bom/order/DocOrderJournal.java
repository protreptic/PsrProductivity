package ru.magnat.sfs.bom.order;

import java.util.ArrayList;
import java.util.Date;

import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.generic.DocGeneric;
import ru.magnat.sfs.bom.order.OnOrderChangedListener.OnAmountChangedListener;
import ru.magnat.sfs.bom.order.OnOrderChangedListener.OnContractorChangedListener;
import ru.magnat.sfs.bom.order.OnOrderChangedListener.OnDelayChangedListener;
import ru.magnat.sfs.bom.order.OnOrderChangedListener.OnOutletChangedListener;
import ru.magnat.sfs.bom.order.OnOrderChangedListener.OnPaymentTypeChangedListener;
import ru.magnat.sfs.bom.order.OnOrderChangedListener.OnShipmentDateChangedListener;
import ru.magnat.sfs.bom.order.OnOrderChangedListener.OnShipmentTimeChangedListener;
import ru.magnat.sfs.bom.ref.contractor.RefContractorEntity;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.ref.paymenttype.RefPaymentTypeEntity;
import ru.magnat.sfs.bom.task.visit.TaskVisitEntity;
import ru.magnat.sfs.ui.android.GenericListView;
import ru.magnat.sfs.ui.android.IBO;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.doc.order.DocOrderEntityView;
import android.content.Context;
import android.widget.ListView;

@SuppressWarnings("rawtypes")
public final class DocOrderJournal extends
		DocGeneric<DocOrderEntity, DocOrderLineEntity> implements
		IBO<DocOrderJournal, DocOrderEntity, TaskVisitEntity>,
		OnAmountChangedListener {

	public DocOrderJournal(Context context) {
		super(context, DocOrderEntity.class);
	}

	@Override
	public DocOrderEntity Current() {
		DocOrderEntity entity = super.Current();
		if (entity != null) {
			entity.setOnAmountChangedListener(new OnAmountChangedListener() {
				public void onAmountChanged(GenericEntity sender,
						float old_value, float new_value) {
					save();
				}
			});
			entity.setOnContractorChangedListener(new OnContractorChangedListener() {

				public void onContractorChanged(GenericEntity sender,
						RefContractorEntity old_value,
						RefContractorEntity new_value) {
					save();

				}

			});
			entity.setOnOutletChangedListener(new OnOutletChangedListener() {

				public void onOutletChanged(GenericEntity sender,
						RefOutletEntity old_value, RefOutletEntity new_value) {
					save();

				}

			});
			entity.setOnPaymentTypeChangedListener(new OnPaymentTypeChangedListener() {

				public void onPaymentTypeChanged(GenericEntity sender,
						RefPaymentTypeEntity old_value,
						RefPaymentTypeEntity new_value) {
					save();
				}

			});
			entity.setOnDelayChangedListener(new OnDelayChangedListener() {
				@Override
				public void onDelayChanged(GenericEntity sender,
						Integer old_value, Integer new_value) {

					save();

				}

			});
			entity.setOnShipmentDateChangedListener(new OnShipmentDateChangedListener() {

				public void onShipmentDateChanged(GenericEntity sender,
						Date old_value, Date new_value) {
					save();
				}

			});
			entity.setOnShipmentTimeChangedListener(new OnShipmentTimeChangedListener() {

				public void onShipmentTimeChanged(GenericEntity sender,
						String old_value, String new_value) {
					save();
				}

			});
		}
		return entity;
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return null;//new DocOrderListViewItem(_context, this, lv, Current());
	}
	
	public DocOrderEntityView GetViewView(DocOrderEntity ent) {
		return new DocOrderEntityView(_context, this, ent);
	}

	public DocOrderEntityView GetEditView(DocOrderEntity ent) {
		return new DocOrderEntityView(_context, this, ent);
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
		super.Select(criteria, "ORDER BY ID asc");
	}

	@Override
	public Boolean save() {
		if (this._current!=null){
			if (!this._current.getReadOnly())
				this._current.SaveDate = new Date();
			
		}
		Boolean result = super.save();
		if (this._current!=null && this._current.MasterTask!=null){
			if (!this._current.MasterTask.getReadOnly()) {
				this._current.MasterTask.TaskEnd = new Date();
				this._current.MasterTask.save();
			}
			
		}
		
		if (result) {
			Globals.setOutletTargetUpdated(false);
			Globals.setEmployeeTargetUpdated(false);
		}
		
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
	public GenericListView<DocOrderJournal, DocOrderEntity, TaskVisitEntity> GetSelectView(
			TaskVisitEntity owner) {
		return null;
	}

	@Override
	public GenericListView<DocOrderJournal, DocOrderEntity, TaskVisitEntity> GetListView(
			TaskVisitEntity owner) {
		return null;
	}
}
