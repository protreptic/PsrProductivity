package ru.magnat.sfs.bom.ref.promo;

import ru.magnat.sfs.bom.SfsEnum;

public class TargetStoreType extends SfsEnum {
	public TargetStoreType(int id) {
		super(new String[] { "Golden", "NotGolden", "All" }, id);
	}
}
