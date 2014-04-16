package ru.magnat.sfs.bom.ref.promo;

import ru.magnat.sfs.bom.SfsEnum;

public class TargetServiceType extends SfsEnum {
	public TargetServiceType(int id) {
		super(new String[] { "SelfService", "Traditional", "All" }, id);
	}
}
