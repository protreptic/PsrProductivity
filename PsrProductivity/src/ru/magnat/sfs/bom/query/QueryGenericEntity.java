package ru.magnat.sfs.bom.query;

import ru.magnat.sfs.bom.GenericEntity;

public abstract class QueryGenericEntity<T extends QueryGeneric<? extends QueryGenericEntity<T>>>
		extends GenericEntity<T> {
	// ������ ����������� ������ ��������� ��� ������� ������� Id !!!

}
