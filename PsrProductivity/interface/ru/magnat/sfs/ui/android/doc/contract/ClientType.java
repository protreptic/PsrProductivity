package ru.magnat.sfs.ui.android.doc.contract;

import ru.magnat.sfs.bom.SfsEnum;

public class ClientType extends SfsEnum  {
	public ClientType(int id) {
		super(new String[] { "NA", "HFS", "KBD", "WH" }, id);
	}
}
