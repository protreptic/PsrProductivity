package ru.magnat.sfs.bom.requestoutlet;

import ru.magnat.sfs.bom.SfsEnum;

public class OutletSizeVariant extends SfsEnum  {
	public OutletSizeVariant(int selectedId) {
		super(new String[]{	"����� 50 ��.�"
								,"�� 50 �� 100 ��.�"
								,"����� 100 ��.�"}
			,selectedId);
	}
}
