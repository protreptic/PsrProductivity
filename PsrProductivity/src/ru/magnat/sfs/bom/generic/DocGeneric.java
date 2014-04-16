package ru.magnat.sfs.bom.generic;

import java.util.ArrayList;
import java.util.Date;

import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.bom.FieldMD;
import ru.magnat.sfs.bom.InternalStorage;
import ru.magnat.sfs.bom.OrmObject;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.task.generic.TaskGenericEntity;
import android.content.Context;
import android.util.Log;

import com.ianywhere.ultralitejni12.ULjException;

public abstract class DocGeneric<E extends DocGenericEntity<? extends DocGeneric<E, L>, L>, L extends DocGenericLineEntity<? extends DocGenericLine<L>>>
		extends OrmObject<E> {

	private DocGenericEntity<?, ?> _masterdoc;
	private TaskGenericEntity<?, ?> _mastertask;
	private boolean _prepared = false;
	
	@SuppressWarnings("unused")
	private void readProperties(){
		
	}
	
	public void SetMasterDoc(DocGenericEntity<?, ?> doc) {
		_masterdoc = doc;
	}

	public void SetMasterTask(TaskGenericEntity<?, ?> task) {
		_mastertask = task;
	}

	@Override
	public final Boolean Find(E entity) {
		return (FindById(entity.Id) != null);
	}

	public DocGeneric(Context context, Class<E> entityType) {
		super(context, entityType);
		_prepared = PrepareInsertStatement() && PrepareUpdateStatement();
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
			this._insert_statement = InternalStorage.getConnection().prepareStatement(query);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	protected DocGenericLine<L> _lines;

	public DocGenericLine<L> getLines() {
		return _lines;
	}

	@Override
	public E Current() {
		E entity = super.Current();
		// _lines = entity.getLines(_context);

		return entity;
	}

	public void Select(Date begin, Date end, Boolean byMasterDoc, Boolean byMasterTask) {

		close();
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();

		if (byMasterDoc) {
			if (this._masterdoc != null) {
				criteria.add(new SqlCriteria("MasterDoc", this._masterdoc.Id));
			}
		}
		if (byMasterTask) {
			if (this._mastertask != null) {
				criteria.add(new SqlCriteria("MasterTask", this._mastertask.Id));
			}
		}
		if (begin != null) {
			criteria.add(new SqlCriteria("DocDate", Utils.RoundDate(begin),
					">="));
		}
		if (end != null) {
			criteria.add(new SqlCriteria("DocDate", Utils.AddDay(
					Utils.RoundDate(end), 1), "<"));
		}
		super.Select(criteria);
	}

	public Boolean save() {
		return save(false);
	}
	
	public Boolean save(boolean force) {
		if (!_prepared)
			return false;
		if (_current == null)
			return false;
		if (!force && _current.getReadOnly())
			return false;
		if (_current.MasterTask == null) {
			Dialogs.MessageBox("Не задана задача. Сохранение документа не возможно.");
			return false;
		}

		try {
			if (_currentContext == OperationContext.INSERTING) {
				int i = 0;
				for (FieldMD f : _fields) {
					if (f.class_name.equalsIgnoreCase("Id"))
						continue;// it is autoincremented
					if (!setCommandParameterValue(_insert_statement, this._current, f, ++i))
						return false;
				}

				_insert_statement.execute();

				InternalStorage.getConnection().commit();
				InternalStorage.getConnection().checkpoint();
				this._current.Id = InternalStorage.getConnection().getLastIdentity();

				ChangeContext(OperationContext.EDITING);
			} else {
				// обновляем
				int i = 0;
				for (FieldMD f : _fields) {
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
				_update_statement.set(++i, this._current.Author.Id);

				_update_statement.execute();

				InternalStorage.getConnection().commit();
				InternalStorage.getConnection().checkpoint();

				ChangeContext(OperationContext.EDITING);
			}
		} catch (ULjException e) {
			Log.d("SFS" , "" + e.getMessage());
			return false;
		}

		return true;
	}

	@Override
	public void close() {
		if (_current != null)
			_current.onClose();
		super.close();
	}
}