package ru.magnat.sfs.bom.requestoutlet;

import ru.magnat.sfs.bom.SfsEnum;

public class OutletSizeVariant extends SfsEnum  {
	public OutletSizeVariant(int selectedId) {
		super(new String[]{	"менее 50 кв.м"
								,"от 50 до 100 кв.м"
								,"более 100 кв.м"}
			,selectedId);
	}
}
