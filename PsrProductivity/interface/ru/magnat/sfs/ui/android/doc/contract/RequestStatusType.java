package ru.magnat.sfs.ui.android.doc.contract;

import ru.magnat.sfs.bom.SfsEnum;

public class RequestStatusType extends SfsEnum  {
	public RequestStatusType(int id) {
		super(new String[] { 
				"Новый договор", 
				"Отправлен",
				"Адрес ТТ проверен",
				"Пакет документов от КА проверен",
				"Договор подписан ДФ",
				"Договор зарегистрирован",
				"Договор передан КА",
				"Отклонено СЛ",
				"Отклонено СРДЗ",
				"Некорректные данные"
		}, id);
	}
}
