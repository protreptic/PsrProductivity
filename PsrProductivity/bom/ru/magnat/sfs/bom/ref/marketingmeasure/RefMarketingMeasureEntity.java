package ru.magnat.sfs.bom.ref.marketingmeasure;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;

@OrmEntityOwner(owner = RefMarketingMeasure.class)
public final class RefMarketingMeasureEntity extends RefGenericEntity {
	@OrmEntityField(DisplayName = "Единица измерения", isPrimary = 0, fields = "UnitName")
	public String UnitName;
	@OrmEntityField(DisplayName = "Формат единицы", isPrimary = 0, fields = "UnitKind")
	public int UnitKind;
	@OrmEntityField(DisplayName = "Требуется фото", isPrimary = 0, fields = "Pictured")
	public Boolean Pictured;

	@Override
	public String toString() {
		return this.Descr;
	}

}
