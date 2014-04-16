package ru.magnat.sfs.bom;

public class SqlCriteria {
	public SqlCriteria(String field, Object value) {
		Field = field;
		Value = value;
	}

	public SqlCriteria(String field, Object value, Boolean orNull) {
		Field = field;
		Value = value;
		OrNull = orNull;
	}

	public SqlCriteria(String field, Object value, String compare) {
		Field = field;
		Value = value;
		Compare = compare;
	}

	public SqlCriteria(String field, Object value, String compare,
			Boolean orNull) {
		this.Field = field;
		this.Value = value;
		this.Compare = compare;
		this.OrNull = orNull;
	}

	public Boolean OrNull = false;
	public String Field;
	public Object Value;
	public String Compare = "=";
}
