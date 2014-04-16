package ru.magnat.sfs.bom.generic;

import java.util.ArrayList;

import ru.magnat.sfs.bom.FieldMD;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.InternalStorage;
import ru.magnat.sfs.bom.OrmObject;
import ru.magnat.sfs.bom.SqlCriteria;
import android.content.Context;
import ru.magnat.sfs.android.Log;
import ru.magnat.sfs.android.MainActivity;

;

public abstract class DocGenericLine<E extends DocGenericLineEntity<? extends DocGenericLine<E>>>
		extends OrmObject<E> {
	private static final String SFS_DOC_CORE_TAG = "DOC GENERIC";
	private boolean _insertable = false;
	private boolean _updateable = false;
	private boolean _deleteable = false;
	private boolean _prepared = false;
	//final protected long _author;
	//final protected long _id;

	/*
	 * public void SetMasterDoc(DocGenericEntity doc){ _masterdoc = doc; }
	 */

	public DocGenericLine(Context context, Class<?> entityType,
			DocGenericEntity<?, ?> owner) {
		super(context, entityType, owner);
		//_author = owner.Author.Id;
		//_id = owner.Id;
		// _insertable = PrepareInsertStatement();
		// _updateable = PrepareUpdateStatement();

	}

	private Boolean PrepareUpdateStatement() {
		String colPart = "";
		for (FieldMD f : _fields) {
			if (f.db_name.length == 0)
				continue;
			if (f.class_name.equalsIgnoreCase("Id"))
				continue;// it is primary
			if (f.class_name.equalsIgnoreCase("MasterDocAuthor"))
				continue;// it is primary
			if (f.class_name.equalsIgnoreCase("MasterDocId"))
				continue;// it is primary
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
				+ " WHERE Id=? AND MasterDocAuthor=? AND MasterDocId=?";
		try {
			this._update_statement =

			InternalStorage.getConnection().prepareStatement(query);

		} catch (Exception e) {
			return false;
		}
		return true;

	}

	private Boolean PrepareDeleteStatement() {

		String query = "DELETE FROM " + _tableName
				+ " WHERE Id=? AND MasterDocAuthor=? AND MasterDocId=?";
		try {
			this._delete_statement =

			InternalStorage.getConnection().prepareStatement(query);
		} catch (Exception e) {
			return false;
		}
		return true;

	}

	private Boolean PrepareInsertStatement() {
		String colPart = "";
		String valPart = "";
		for (FieldMD f : _fields) {
			if (f.db_name.length == 0)
				continue;
			if (f.class_name.equalsIgnoreCase("Id"))
				continue;// it is autoincremented
			if (f.class_name.equalsIgnoreCase("MasterDocAuthor"))
				continue;// it is primary
			if (f.class_name.equalsIgnoreCase("MasterDocId"))
				continue;// it is primary
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
		}
		String query = "INSERT INTO " + _tableName + "(" + colPart
				+ ", MasterDocAuthor" + ", MasterDocId" + ") VALUES ("
				+ valPart + ", ?" + ", ?" + ")";
		try {
			this._insert_statement =

			InternalStorage.getConnection().prepareStatement(query);

		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public Boolean Find(E entity) {

		return (FindById(entity.Id) != null);

	}

	public Boolean Select() {
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		// if (this._owner!=null){
		criteria.add(new SqlCriteria("MasterDocId", this._owner.Id));
		criteria.add(new SqlCriteria("MasterDocAuthor",
				((DocGenericEntity<?, ?>) this._owner).Author.Id));
		// }
		return super.Select(criteria);
	}

	abstract public E getLine(Context context, GenericEntity<?> entity);

	public Boolean delete(DocGenericLineEntity<?> entity) {
		if (!_prepared) {
			_insertable = PrepareInsertStatement();
			_updateable = PrepareUpdateStatement();
			_deleteable = PrepareDeleteStatement();
			_prepared = true;
		}
		if (!this._deleteable)
			return false;
		DocGenericEntity<?, ?> doc = (DocGenericEntity<?, ?>) _owner;
		if (doc.getReadOnly())
			return false;
		try {
			_delete_statement.set(1, entity.Id);
			_delete_statement.set(2, entity.MasterDocAuthor);
			_delete_statement.set(3, entity.MasterDocId);

			_delete_statement.execute();

			InternalStorage.getConnection().commit();
			InternalStorage.getConnection().checkpoint();

			this._current = null;
			ChangeContext(OperationContext.FETCHING);

		} catch (Exception e) {
			Log.v(MainActivity.LOG_TAG, "Ошибка удаления строки документа: "+e.getMessage());
			return false;
		}

		return true;
	}

	@Override
	public Boolean save() {
		if (!_prepared) {
			_insertable = PrepareInsertStatement();
			_updateable = PrepareUpdateStatement();
			_deleteable = PrepareDeleteStatement();
			_prepared = true;
		}
		DocGenericEntity<?, ?> doc = (DocGenericEntity<?, ?>) _owner;
		if (doc.getReadOnly())
			return false;
		
		if (doc.Author == null) {
			ru.magnat.sfs.logging.PsrProductivityLogger.Log.i("У документа не задан автор! Номер документа: " + doc.Id);
			return false;
		}

		this._current.MasterDocAuthor = doc.Author.Id;
		this._current.MasterDocId = doc.Id;

		try {
			if (_currentContext == OperationContext.INSERTING) {
				if (!_insertable)
					return false;
				int i = 0;
				for (FieldMD f : _fields) {
					if (f.class_name.equalsIgnoreCase("Id"))
						continue;// it is autoincremented
					if (f.class_name.equalsIgnoreCase("MasterDocAuthor"))
						continue;// it is primary
					if (f.class_name.equalsIgnoreCase("MasterDocId"))
						continue;// it is primary
					if (!setCommandParameterValue(_insert_statement,
							this._current, f, ++i))
						return false;

				}
				//_insert_statement.set(++i, this._author);
				//_insert_statement.set(++i, this._id);
				DocGenericEntity<?, ?> masterdoc = (DocGenericEntity<?, ?>) this._owner;
				_insert_statement.set(++i, masterdoc.Author.Id);
				_insert_statement.set(++i, masterdoc.Id);

				_insert_statement.execute();

				InternalStorage.getConnection().commit();
				InternalStorage.getConnection().checkpoint();
				this._current.Id = InternalStorage.getConnection()
						.getLastIdentity();

				ChangeContext(OperationContext.EDITING);
			} else {
				// обновляем
				if (!this._updateable)
					return false;
				int i = 0;
				for (FieldMD f : _fields) {
					if (f.class_name.equalsIgnoreCase("Id"))
						continue;// it is primary key
					if (f.class_name.equalsIgnoreCase("MasterDocAuthor"))
						continue;// it is primary key
					if (f.class_name.equalsIgnoreCase("MasterDocId"))
						continue;// it is primary key
					if (!setCommandParameterValue(_update_statement,
							this._current, f, ++i))
						return false;
				}
				// this is primary key
				_update_statement.set(++i, this._current.Id);
				//_update_statement.set(++i, this._author);
				//_update_statement.set(++i, this._id);
				DocGenericEntity<?, ?> masterdoc = (DocGenericEntity<?, ?>) this._owner;
				_update_statement.set(++i, masterdoc.Author.Id);
				_update_statement.set(++i, masterdoc.Id);
				
				_update_statement.execute();
				if (_cursor!=null){
					_cursor.beforeFirst();
					_cursor.relative((int) this._abs_position + 1);
				}
				InternalStorage.getConnection().commit();
				InternalStorage.getConnection().checkpoint();

				ChangeContext(OperationContext.EDITING);
			}
		} catch (Exception e) {
			Log.v(SFS_DOC_CORE_TAG, this.getClass().getSimpleName() +": Can't save doc");
			Log.v(SFS_DOC_CORE_TAG, e.getMessage());
			return false;
		}
		return true;
	}
}
