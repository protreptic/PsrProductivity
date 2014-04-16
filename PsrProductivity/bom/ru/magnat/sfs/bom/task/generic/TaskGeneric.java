package ru.magnat.sfs.bom.task.generic;

import java.util.ArrayList;
import java.util.Date;

import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.bom.FieldMD;
import ru.magnat.sfs.bom.InternalStorage;
import ru.magnat.sfs.bom.OrmObject;
import ru.magnat.sfs.bom.SqlCriteria;
import android.content.Context;

public abstract class TaskGeneric<E extends TaskGenericEntity<? extends TaskGeneric<E>, ?>>
		extends OrmObject<E> {

	private TaskGenericEntity<?, ?> _mastertask;

	public void SetMasterTask(TaskGenericEntity<?, ?> task) {
		_mastertask = task;
	}

	Boolean prepared = false;
	Boolean reverseOrder = false;
	public TaskGeneric(Context context, Class<?> entityType) {
		super(context, entityType);
		prepared = PrepareInsertStatement() && PrepareUpdateStatement();
	}

	private Boolean PrepareUpdateStatement() {
		String colPart = "";
		for (int i = 0; i < _fields.size(); i++) {
			FieldMD f = _fields.elementAt(i);
			if (f.db_name.length == 0)
				continue;
			if (f.class_name.equalsIgnoreCase("Id"))
				continue;// it is autoincremented
			if (f.class_name.equalsIgnoreCase("Author"))
				continue;// it is autoincremented
			for (int j = 0; j < f.db_name.length; j++) {
				String dbFieldName = f.db_name[j];
				if (dbFieldName == "")
					continue;
				if (colPart.length() > 0)
					colPart += ",";
				colPart += f.db_name[j] + "=?";
			}
		}
		String query = "UPDATE " + _tableName + " SET " + colPart
				+ " WHERE Id=? AND Author=?";
		try {
			this._update_statement = InternalStorage.getConnection()
					.prepareStatement(query);

		} catch (Exception e) {
			return false;
		}
		return true;

	}

	private Boolean PrepareInsertStatement() {
		String colPart = "";
		String valPart = "";
		for (int i = 0; i < _fields.size(); i++) {
			FieldMD f = _fields.elementAt(i);
			if (f.db_name.length == 0)
				continue;
			if (f.class_name.equalsIgnoreCase("Id"))
				continue;// it is autoincremented
			for (int j = 0; j < f.db_name.length; j++) {
				String dbFieldName = f.db_name[j];
				if (dbFieldName == "")
					continue;
				if (colPart.length() > 0) {
					colPart += ",";
					valPart += ",";
				}
				colPart += f.db_name[j];
				valPart += "?";
			}
			// }

		}
		String query = "INSERT INTO " + _tableName + "(" + colPart
				+ ") VALUES (" + valPart + ")";
		try {
			this._insert_statement = InternalStorage.getConnection()
					.prepareStatement(query);

		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public Boolean Find(E entity) {

		return (FindById(entity.Author.Id, entity.Id) != null);

	}

	public static enum DateVariant {
		CREATE, BEGIN, END
	}
	public void setReverseOrder(Boolean reverseOrder){
		this.reverseOrder = reverseOrder;
	}
	public Boolean Select(Date begin, Date end, DateVariant variant,
			Boolean byMasterTask) {
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();

		if (byMasterTask) {
			if (this._mastertask != null) {
				criteria.add(new SqlCriteria("MasterTask", this._mastertask.Id));
			}
		}
		String dateField = "CreateDate";
		switch (variant) {
		case BEGIN:
			dateField = "StartDate";
			break;
		case END:
			dateField = "EndDate";
			break;
		case CREATE:
			break;
		default:
			break;

		}

		if (begin != null) {
			criteria.add(new SqlCriteria(dateField, Utils.RoundDate(begin),
					">="));
		}
		if (end != null) {

			criteria.add(new SqlCriteria(dateField, Utils.AddDay(
					Utils.RoundDate(end), 1), "<"));
		}
		return super.Select(criteria, "ORDER BY " + dateField + ((reverseOrder)?" DESC":"")+", ID");
	}

	/*
	 * public void NewEntity(){ super.ChangeContext(OperationContext.INSERTING);
	 * this.current=(DocGenericEntity)entityType.newInstance(); }
	 */
	@Override
	public Boolean save() {
		if (!prepared)
			return false;
		try {
			if (_currentContext == OperationContext.INSERTING) {

				int i = 0;
				for (int j = 0; j < _fields.size(); j++) {
					FieldMD f = _fields.elementAt(j);
					if (f.class_name.equalsIgnoreCase("Id"))
						continue;// it is autoincremented
					if (!setCommandParameterValue(_insert_statement,
							this._current, f, ++i))
						return false;
				}

				_insert_statement.execute();

				InternalStorage.getConnection().commit();
				this._current.Id = InternalStorage.getConnection()
						.getLastIdentity();

				ChangeContext(OperationContext.EDITING);
			} else {
				// обновляем
				int i = 0;
				for (int j = 0; j < _fields.size(); j++) {
					FieldMD f = _fields.elementAt(j);

					if (f.class_name.equalsIgnoreCase("Id"))
						continue;// it is primary key
					if (f.class_name.equalsIgnoreCase("Author"))
						continue;// it is primary key
					if (!setCommandParameterValue(_update_statement,
							this._current, f, ++i))
						return false;
				}
				// this is primary key
				_update_statement.set(++i, this._current.Id);
				_update_statement.set(++i, this.Current().Author.Id);

				_update_statement.execute();

				InternalStorage.getConnection().commit();
				// _storage.Connection.commit();

				ChangeContext(OperationContext.EDITING);
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
