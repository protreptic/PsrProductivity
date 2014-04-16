package ru.magnat.sfs.ui.android.doc.requestoutlet;

import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.requestoutlet.DocRequestOutletEntity;
import ru.magnat.sfs.bom.requestoutlet.DocRequestOutletJournal;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayEntity;
import ru.magnat.sfs.ui.android.GenericListView;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.SfsListType;
import android.content.Context;
import android.location.Location;

public class DocRequestOutletListView extends GenericListView<DocRequestOutletJournal, DocRequestOutletEntity, TaskWorkdayEntity> {

	public DocRequestOutletListView(Context context, TaskWorkdayEntity Workday) {
		super(context, null, Workday);
	}

	@Override
	public SfsContentView inflate() {
		requery();
		super.�atalogInflate(R.layout.doc_order_list_view, "������� �� ���������� ������������� ��");
		showAddButton();
		
		return this;
	}

	protected void CreateRequest(TaskWorkdayEntity Workday) {
		Location loc = Globals.getCurrentLocation();
		if (loc == null){
			loc = Globals.getLastLocation();
		}
		if (loc == null) {
			Dialogs.MessageBox("���������� ��� �� ����������, ���������� ������� �� ����� � ��������� ����� ��������� ����������� ����� ��������������.");
			return;
		}
		
		DocRequestOutletJournal docs = new DocRequestOutletJournal(getContext());
		docs.NewEntity();
		DocRequestOutletEntity doc = docs.Current();

		if (Workday == null) {
			Dialogs.MessageBox("������ �� ���������� ������������� �������� ����� ����� ��������� ������ � ������ �������� ���");
			return;
		}

		doc.setDefaults(getContext(), Workday);
		doc.LocationLat = (float) loc.getLatitude();
		doc.LocationLon = (float) loc.getLongitude();
		if (!docs.save()) {
			Dialogs.MessageBox("�� ������� �������� ��������� ������");
			return;
		}
		docs.close();
		OpenEntity(doc);
	}

	@Override
	protected void createEntity() {
		CreateRequest(_owner);
	}

	@Override
	protected void requery() {
		if (_catalog == null) {
			_catalog = new DocRequestOutletJournal(getContext());
		}
		_catalog.SetListType(SfsListType.EXTENDED_LIST);
		_catalog.Select(null, "ORDER BY Id DESC");
		_catalog.notifyDataSetChanged();
	}

}
