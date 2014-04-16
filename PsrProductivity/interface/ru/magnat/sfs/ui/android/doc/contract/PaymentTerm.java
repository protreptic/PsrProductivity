package ru.magnat.sfs.ui.android.doc.contract;

import ru.magnat.sfs.bom.SfsEnum;

public class PaymentTerm extends SfsEnum  {
	public PaymentTerm(int id) {
		super(new String[] { "Предоплата", "Кредит" }, id);
	}
}
