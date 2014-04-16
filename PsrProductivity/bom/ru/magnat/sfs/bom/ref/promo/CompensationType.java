package ru.magnat.sfs.bom.ref.promo;

import ru.magnat.sfs.bom.SfsEnum;

public class CompensationType extends SfsEnum {
	public CompensationType(int id) {
		super(new String[] { "Товарный", "Монетарный" }, id);
	}
}
