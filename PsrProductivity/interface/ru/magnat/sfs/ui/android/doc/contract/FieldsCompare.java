package ru.magnat.sfs.ui.android.doc.contract;

import java.util.Comparator;
import java.util.Map;

public class FieldsCompare implements Comparator<Map<String, Object>> {

	@Override
	public int compare(Map<String, Object> lhs, Map<String, Object> rhs) {
		Integer sortKeyL = (Integer) lhs.get("sortKey");
		Integer sortKeyR = (Integer) rhs.get("sortKey");
		
		if (sortKeyL == sortKeyR) {
			return 0;
		}
		if (sortKeyL < sortKeyR) {
			return -1;
		}
		if (sortKeyL > sortKeyR) {
			return 1;
		}
		
		return 0;
	}
	
}
