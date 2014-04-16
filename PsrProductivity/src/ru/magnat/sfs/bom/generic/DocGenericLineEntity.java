package ru.magnat.sfs.bom.generic;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.OrmEntityField;

public abstract class DocGenericLineEntity<T extends DocGenericLine<? extends DocGenericLineEntity<T>>>
		extends GenericEntity<T> {
	@OrmEntityField(DisplayName = "", isPrimary = 1, fields = "MasterDocId")
	public long MasterDocId;
	@OrmEntityField(DisplayName = "", isPrimary = 1, fields = "MasterDocAuthor")
	public long MasterDocAuthor;

}
