package ru.magnat.sfs.bom.reg.goldenprogram;

import ru.magnat.sfs.bom.reg.generic.RegGeneric;
import android.content.Context;

;

public abstract class RegGoldenProgram extends RegGeneric {

	public RegGoldenProgram(Context context) {
		super(context, RegGoldenProgramEntity.class);

	}

	public RegGoldenProgramEntity Current() {
		return (RegGoldenProgramEntity) super.Current();
	}

}
