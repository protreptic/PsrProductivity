package ru.magnat.sfs.bom.reg.docstage;

import ru.magnat.sfs.bom.reg.generic.RegGeneric;
import android.content.Context;

;

public abstract class RegDocStage extends RegGeneric {

	public RegDocStage(Context context, Class<?> entityType) {
		super(context, RegDocStageEntity.class);

	}

	public RegDocStageEntity Current() {
		return (RegDocStageEntity) super.Current();
	}

}
