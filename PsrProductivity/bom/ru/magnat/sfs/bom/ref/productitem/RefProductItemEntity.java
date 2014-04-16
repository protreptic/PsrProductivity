package ru.magnat.sfs.bom.ref.productitem;

import ru.magnat.sfs.bom.EntityCardField;
import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.csku.RefCskuEntity;
import ru.magnat.sfs.bom.ref.gcasstate.RefGcasStateEntity;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;

@OrmEntityOwner(owner = RefProductItem.class)
public final class RefProductItemEntity extends RefGenericEntity<RefProductItem, RefCskuEntity> {
	
	@OrmEntityField(DisplayName = "������ ������������", isPrimary = 0, fields = "Fullname")
	@EntityCardField(DisplayName = "������ ������������", Sortkey = 1, SelectMethod = "showFullname")
	public String Fullname;
	
	@EntityCardField(DisplayName = "�������", Sortkey = 2, SelectMethod = "")
	@OrmEntityField(DisplayName = "�������", isPrimary = 0, fields = "Article")
	public String Article;
	
	@EntityCardField(DisplayName = "���� � �������", Sortkey = 3, SelectMethod = "")
	@OrmEntityField(DisplayName = "�������� 1", isPrimary = 0, fields = "UnitFactor1")
	public long UnitFactor1;
	
	
	@OrmEntityField(DisplayName = "�������� 2", isPrimary = 0, fields = "UnitFactor2")
	public long UnitFactor2;
	
	@OrmEntityField(DisplayName = "������", isPrimary = 0, fields = "GcasState")
	public RefGcasStateEntity GcasState;
	
	@EntityCardField(DisplayName = "���", Sortkey = 5, SelectMethod = "", format = "##,###,###.##%")
	@OrmEntityField(DisplayName = "���", isPrimary = 0, fields = "VAT")
	public float VAT;
	
	@EntityCardField(DisplayName = "SU � �����", Sortkey = 6, SelectMethod = "", format = "##,###,###.###")
	@OrmEntityField(DisplayName = "SU", isPrimary = 0, fields = "SuFactor")
	public float SuFactor;
	
	@EntityCardField(DisplayName = "������", Sortkey = 7, SelectMethod = "showProfit")
	@OrmEntityField(DisplayName = "������", isPrimary = 0, fields = "ProfitDescription")
	public String ProfitDescription;

	@EntityCardField(DisplayName = "EAN-��� �����", Sortkey = 7, SelectMethod = "")
	@OrmEntityField(DisplayName = "EAN-��� �����", isPrimary = 0, fields = "EanCodeItem")
	public String EanCodeItem;
	
	@EntityCardField(DisplayName = "EAN-��� �������", Sortkey = 7, SelectMethod = "")
	@OrmEntityField(DisplayName = "EAN-��� �������", isPrimary = 0, fields = "EanCodeBox")
	public String EanCodeBox;
	
	public boolean isRegular() {
		if (GcasState == null) {
			return true;
		}
		
		return GcasState.Descr.equalsIgnoreCase("R");
	}
	
	public static boolean isRegular(String gcasstate) {
		if (gcasstate == null) {
			return true;
		}
		
		return gcasstate.equalsIgnoreCase("R");
	}
	
}
