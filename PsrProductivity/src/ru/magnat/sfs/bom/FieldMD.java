package ru.magnat.sfs.bom;

import java.lang.reflect.Field;

public class FieldMD implements Comparable<FieldMD> {
	public FieldMD() {

	}

	public Field field;
	public String class_name;
	public String[] db_name;
	public Class<?> type;
	public String label = "";
	public Integer sortkey = 0;
	public String selectMethod = "";
	public String formatString = "";

	public int compareTo(FieldMD another) {
		int res = -1;
		if (another != null) {
			FieldMD anotherFMD = (FieldMD) another;
			res = sortkey.compareTo(anotherFMD.sortkey);
			if (res != 0)
				return res;
			res = label.compareTo(anotherFMD.label);
			if (res != 0)
				return res;
			res = class_name.compareTo(anotherFMD.class_name);
		}
		return res;
	}
}
