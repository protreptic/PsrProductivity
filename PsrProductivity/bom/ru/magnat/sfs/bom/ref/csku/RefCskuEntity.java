package ru.magnat.sfs.bom.ref.csku;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.brand.RefBrandEntity;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;
import ru.magnat.sfs.bom.ref.mediafiles.RefMediaFilesEntity;

@OrmEntityOwner(owner = RefCsku.class)
public final class RefCskuEntity extends
		RefGenericEntity<RefCsku, RefGenericEntity> {
	@OrmEntityField(DisplayName = "Полное наименование", isPrimary = 0, fields = "Fullname")
	public String Fullname;
	@OrmEntityField(DisplayName = "Ключ сортировки", isPrimary = 0, fields = "OrderKey")
	public String OrderKey;
	@OrmEntityField(DisplayName = "Ключ фильтра", isPrimary = 0, fields = "Fullpath")
	public String Fullpath;
	@OrmEntityField(DisplayName = "Код группы", isPrimary = 0, fields = "Parent")
	public long ParentId;
	@OrmEntityField(DisplayName = "Код поставщика", isPrimary = 0, fields = "ExtCode")
	public String ExtCode;
	@OrmEntityField(DisplayName = "Фото", isPrimary = 0, fields = "PhotoMediaFile")
	public RefMediaFilesEntity PhotoMediaFile;
	@OrmEntityField(DisplayName = "Набор", isPrimary = 0, fields = "IsKit")
	public boolean IsKit;
	@OrmEntityField(DisplayName = "Бренд", isPrimary = 0, fields = "Brand")
	public RefBrandEntity Brand;
}
