package ru.magnat.sfs.bom.query.extras;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.query.QueryGenericEntity;

public final class QueryGetExtrasFilesEntity extends
		QueryGenericEntity<QueryGetExtrasFiles> {
	@OrmEntityField(DisplayName = "Наименование", isPrimary = 0, fields = "Descr")
	public String Descr;
	@OrmEntityField(DisplayName = "Имя файла", isPrimary = 0, fields = "FileName")
	public String FileName;
	@Override
	public String toString() {
		return Descr;
	}
	}
