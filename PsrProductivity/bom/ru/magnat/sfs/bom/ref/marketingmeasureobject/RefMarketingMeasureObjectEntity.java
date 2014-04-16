package ru.magnat.sfs.bom.ref.marketingmeasureobject;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.csku.RefCsku;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;

@OrmEntityOwner(owner = RefMarketingMeasureObject.class)
public final class RefMarketingMeasureObjectEntity extends RefGenericEntity<RefMarketingMeasureObject, RefGenericEntity> {
	
	@OrmEntityField(DisplayName = "Полное наименование", isPrimary = 0, fields = "Fullname") 
	public String Fullname;
	@OrmEntityField(DisplayName = "Ссылка", isPrimary = 0, fields = "Referer")
	public long Referer;
	@OrmEntityField(DisplayName = "Ключ сортировки", isPrimary = 0, fields = "OrderKey")
	public String OrderKey;

	public GenericEntity getReferer() {
		RefCsku ref = (RefCsku) Globals.createOrmObject(RefCsku.class);
		return ref.FindById(Referer);
	}
}
