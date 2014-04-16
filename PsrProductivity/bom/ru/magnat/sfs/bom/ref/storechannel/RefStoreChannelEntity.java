package ru.magnat.sfs.bom.ref.storechannel;

import ru.magnat.sfs.bom.OrmEntityField;
import ru.magnat.sfs.bom.OrmEntityOwner;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;

@OrmEntityOwner(owner = RefStoreChannel.class)
public final class RefStoreChannelEntity extends RefGenericEntity<RefStoreChannel,RefGenericEntity<?,?>> {
	@OrmEntityField(DisplayName = "Код ISIS", isPrimary = 0, fields = "ISISCode")
	public long ISISCode;
	@OrmEntityField(DisplayName = "Тип золотой программы", isPrimary = 0, fields = "GoldenProgramType")
	public GoldenProgramType GoldenProgramType; 
}
