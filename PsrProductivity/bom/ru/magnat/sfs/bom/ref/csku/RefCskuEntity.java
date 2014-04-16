package ru.magnat.sfs.bom.ref.csku;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.brand.RefBrandEntity;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;
import ru.magnat.sfs.bom.ref.mediafiles.RefMediaFilesEntity;

@OrmEntityOwner(owner = RefCsku.class)
public final class RefCskuEntity extends
		RefGenericEntity<RefCsku, RefGenericEntity> {
	@OrmEntityField(DisplayName = "������ ������������", isPrimary = 0, fields = "Fullname")
	public String Fullname;
	@OrmEntityField(DisplayName = "���� ����������", isPrimary = 0, fields = "OrderKey")
	public String OrderKey;
	@OrmEntityField(DisplayName = "���� �������", isPrimary = 0, fields = "Fullpath")
	public String Fullpath;
	@OrmEntityField(DisplayName = "��� ������", isPrimary = 0, fields = "Parent")
	public long ParentId;
	@OrmEntityField(DisplayName = "��� ����������", isPrimary = 0, fields = "ExtCode")
	public String ExtCode;
	@OrmEntityField(DisplayName = "����", isPrimary = 0, fields = "PhotoMediaFile")
	public RefMediaFilesEntity PhotoMediaFile;
	@OrmEntityField(DisplayName = "�����", isPrimary = 0, fields = "IsKit")
	public boolean IsKit;
	@OrmEntityField(DisplayName = "�����", isPrimary = 0, fields = "Brand")
	public RefBrandEntity Brand;
}
