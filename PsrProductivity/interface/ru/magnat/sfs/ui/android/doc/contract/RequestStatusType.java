package ru.magnat.sfs.ui.android.doc.contract;

import ru.magnat.sfs.bom.SfsEnum;

public class RequestStatusType extends SfsEnum  {
	public RequestStatusType(int id) {
		super(new String[] { 
				"����� �������", 
				"���������",
				"����� �� ��������",
				"����� ���������� �� �� ��������",
				"������� �������� ��",
				"������� ���������������",
				"������� ������� ��",
				"��������� ��",
				"��������� ����",
				"������������ ������"
		}, id);
	}
}
