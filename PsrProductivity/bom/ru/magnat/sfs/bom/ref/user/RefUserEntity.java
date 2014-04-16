package ru.magnat.sfs.bom.ref.user;

import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;

@OrmEntityOwner(owner = RefUser.class)
public final class RefUserEntity extends RefGenericEntity {
	@OrmEntityField(DisplayName = "Ключ", isPrimary = 0, fields = "PassKey")
	public String PassKey;

	@Override
	public String toString() {
		return Descr;
	}
}
