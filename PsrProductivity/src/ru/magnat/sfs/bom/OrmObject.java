package ru.magnat.sfs.bom;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArraySet;

import ru.magnat.sfs.MainActivity;
import ru.magnat.sfs.android.Log;
import ru.magnat.sfs.bom.generic.DocGenericEntity;
import ru.magnat.sfs.bom.ref.generic.RefGenericEntity;
import ru.magnat.sfs.bom.task.generic.TaskGenericEntity;
import ru.magnat.sfs.ui.android.GenericListItemView;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.SfsListType;
import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.ianywhere.ultralitejni12.PreparedStatement;
import com.ianywhere.ultralitejni12.ResultSet;
import com.ianywhere.ultralitejni12.ULjException;

@SuppressWarnings("rawtypes")
public abstract class OrmObject<E extends GenericEntity<? extends OrmObject<E>>> extends BaseAdapter {
	
	private final DataSetObservable _dataSetObservable = new DataSetObservable();
	private static final int MAX_ENTITY_PER_REF = 100000;
	protected static final String TAG = "SFS";
	private static final String SFS_CORE_TAG = "SFS CORE";
	private static final boolean CURSOR_CONTROL = Log.LOG;
	protected OperationContext _currentContext;
	protected InternalStorage _storage;
	protected int _abs_position = -1;
	protected int _count = -1;
	protected ResultSet _cursor;
	protected boolean _invalid;
	protected String _tableName;
	protected String _query;
	protected PreparedStatement _select_statement;
	protected PreparedStatement _insert_statement;
	protected PreparedStatement _update_statement;
	protected GenericEntity<?> _owner;
	final protected Class<?> _entityType;
	final protected Context _context;
	protected E _current ;
	public Vector<FieldMD> _fields;
	protected SfsListType _listType = SfsListType.SIMPLE_LIST;
	private boolean _select_statement_prepared = false;
	
	public OrmObject(Context context, Class<?> entityType, GenericEntity owner) {
		
		if (owner == null) {
			_owner = null;
		} else {
			_owner = owner;
		}
		
		_invalid = false;
		_context = context;
		_tableName = getClass().getSimpleName();
		_query = "SELECT * FROM " + _tableName; // в запросах
		_entityType = entityType;
		
		readFieldMetaData();
	}
	
	protected OrmObject(Context context, Class<?> entityType) {
		this(context, entityType, null);
	}

	protected Context getContext() {
		return _context;
	}

	public void SetListType(SfsListType listType) {
		_listType = listType;
	}
	
	public E getCurrent() {
		return _current;
	}

	@SuppressWarnings("unchecked")
	public void setCurrent(GenericEntity<?> entity) {
		entity.setCatalog(this);
		_current = (E) entity;
	}

	public GenericEntity<?> getOwner() {
		return _owner;
	}

	public void close() {
		_close();
	}

	abstract public Boolean Find(E entity);

	private void readFieldMetaData() {
		_fields = new Vector<FieldMD>();
		Field[] fs = _entityType.getFields();
		for (Field f : fs) {
			OrmEntityField oef = f.getAnnotation(OrmEntityField.class);
			if (oef != null) {
				FieldMD fmd = new FieldMD();
				fmd.field = f;
				fmd.class_name = f.getName();
				fmd.type = f.getType();
				fmd.db_name = oef.fields();
				fmd.label = oef.DisplayName();
				_fields.add(fmd);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void NewEntity() {
 		ChangeContext(OperationContext.INSERTING);
		try {
			_current = (E) _entityType.newInstance();
			_current.setCatalog(this);
			_current.Id = Long.MIN_VALUE;
			//_abs_position = Long.MIN_VALUE;
			_invalid = false;
		} catch (Exception e) {
			Log.v(OrmObject.SFS_CORE_TAG, getClass().getSimpleName() +": Can't create new entity for query "+_query+" return error");
			Log.v(OrmObject.SFS_CORE_TAG, e.getMessage());
			_current = null;
		} 
	}
	SparseArray<E> _entityCache = new SparseArray<E>();

	
	@SuppressWarnings("unchecked")
	public E Current() {
		try {
			if (isClosed() && (_current == null || _invalid)) {
				Log.e(SFS_CORE_TAG, getClass().getSimpleName() +": Запрос текущего элемента у закрытого курсора", null);
				for (StackTraceElement trace:Thread.currentThread().getStackTrace()){
					Log.e(MainActivity.LOG_TAG, trace.toString());
				}
				return null;
			}
			if (_invalid || _current == null) {

				_current = (E) _entityType.newInstance();
				_current.setCatalog(this);

				if (_cursor != null) {
					if (_abs_position != Long.MIN_VALUE && _abs_position < 0) {
						_current = null;
					} else if (_abs_position > _count - 1){
						_current = null;
					} else if (!ReadEntity()) {
						_current = null; //BUGFIX #232,243,244 USMANOV недочитанные записи приводят к непредсказуеым последствиям
					} else {
						_invalid = false;
					}
				}
			}
		} catch (Exception e) {
			Log.e(SFS_CORE_TAG, getClass().getSimpleName() +": "+e.getMessage(), e);
		}
		return _current;
	}

	public enum OperationContext {
		FETCHING, INSERTING, EDITING, DELETING
	}

	public boolean ChangeContext(OperationContext newContext) {
		_currentContext = newContext;
		return true;
	}

	public boolean BeforeFirst() {
		ChangeContext(OperationContext.FETCHING);

		if (_cursor == null)
			return false;
		try {
			_abs_position = -1;
			_invalid = true;
			return _cursor.beforeFirst();
		} catch (Exception e) {
			Log.v(OrmObject.SFS_CORE_TAG, getClass().getSimpleName() +": Can't move before first for query "+_query+" return error");
			Log.v(OrmObject.SFS_CORE_TAG, e.getMessage());
			e.getStackTrace();
		}
		return false;
	}

	public boolean AfterLast() {
		ChangeContext(OperationContext.FETCHING);

		if (_cursor == null)
			return false;
		try {
			_abs_position = _count;
			_invalid = true;
			return _cursor.afterLast();
		} catch (Exception e) {
			Log.v(OrmObject.SFS_CORE_TAG, getClass().getSimpleName() +": Can't move after last for query "+_query+" return error");
			Log.v(OrmObject.SFS_CORE_TAG, e.getMessage());
			e.getStackTrace();
		}
		return false;
	}

	public boolean Next() {
		boolean fetched = false;
		try {
			ChangeContext(OperationContext.FETCHING);
			if (_cursor == null)
				return false;

			fetched = _cursor.next();

			if (fetched) {
				_invalid = true;
				_abs_position++;
			}

		} catch (ULjException e) {
			Log.v(OrmObject.SFS_CORE_TAG, getClass().getSimpleName() +": Can't move to next for query "+_query+" return error");
			Log.v(OrmObject.SFS_CORE_TAG, e.getMessage());
			e.printStackTrace();
		}
		return fetched;
	}

	public boolean Prev() {

		boolean fetched = false;
		try {
			ChangeContext(OperationContext.FETCHING);
			if (_cursor == null)
				return false;

			fetched = _cursor.previous();

			if (fetched) {
				_invalid = true;
				_abs_position--;
			}

		} catch (ULjException e) {
			Log.v(OrmObject.SFS_CORE_TAG, getClass().getSimpleName() +": Can't move to  previous for query "+_query+" return error");
			Log.v(OrmObject.SFS_CORE_TAG, e.getMessage());
			e.printStackTrace();
		}
		return fetched;
	}

	public boolean To(int to) {
		ChangeContext(OperationContext.FETCHING);
		if (_cursor == null) {
			return false;
		}

		if (to < 0)
			return false;
		_invalid = true;
		if (to < Count()) {
			if (to == _abs_position)
				return true;
			try {
				boolean fetched = _cursor
						.relative((int) (to - _abs_position));
				_invalid = true;
				if (fetched) {

					_abs_position = to;
				} else {
					_cursor.beforeFirst();
					_abs_position = -1;
				}

				return fetched;
			} catch (Exception e) {
				Log.v(OrmObject.SFS_CORE_TAG, getClass().getSimpleName() +": Can't move to  "+ to +" for query "+_query+" return error");
				Log.v(OrmObject.SFS_CORE_TAG, e.getMessage());
				return false;
			}
		}
		return false;
	}

	public long Count() {
		if (_count == -1) {
			try {
				_count = (int) _cursor.getRowCount(MAX_ENTITY_PER_REF);
			} catch (Exception e) {
				return -1;
			}
		}
		return _count;
	}

	public Boolean Select(ArrayList<SqlCriteria> criteria) {
		return Select(criteria, "");
	}
	
	public Boolean setSelectParameters(ArrayList<SqlCriteria> criteria) {
		try {
			int col = 0;
			// Log.v(TAG, "Подготовка запроса "+_query);
			if (criteria == null)
				return true;
			for (SqlCriteria c : criteria) {
				col++;
				// Log.v(TAG, "Параметр: "+c.Field+"="+c.Value.toString());
				if (c.Value==null){
					_select_statement.setNull(col);
				} else {
					if (c.Value instanceof Integer)
						_select_statement.set(col, (Integer) c.Value);
					else if (c.Value instanceof Long)
						_select_statement.set(col, (Long) c.Value);
					else if (c.Value instanceof Float)
						_select_statement.set(col, (Float) c.Value);
					else if (c.Value instanceof Double)
						_select_statement.set(col, (Double) c.Value);
					else if (c.Value instanceof Date)
						_select_statement.set(col, (Date) c.Value);
					else if (c.Value instanceof String)
						_select_statement.set(col, (String) c.Value);
					else if (c.Value instanceof Boolean)
						_select_statement.set(col, (Boolean) c.Value);
				}
				
			
			}
		} catch (ULjException e) {
			Log.e(OrmObject.SFS_CORE_TAG, getClass().getSimpleName() +": Can't set criteria for query "+_query+" return error");
			Log.e(OrmObject.SFS_CORE_TAG, e.getMessage());
			return false;
		}
		return true;
	}

	public Boolean prepareSelect(ArrayList<SqlCriteria> criteria,
			String orderFactor) {
		String query = _query;
		try {
			
			ChangeContext(OperationContext.FETCHING);
			// _lastCriteria = (ArrayList<SqlCriteria>) criteria.clone();
			if (InternalStorage.getConnection() == null)
				return false;
			if (_cursor != null)
				close();
			String where = "";
			if (criteria != null) {

				for (SqlCriteria c : criteria) {
					if (c.OrNull)
						where += ((where == "") ? "where " : " and ")
								+ " NULLIF(" + c.Field + ",?) " + c.Compare
								+ " NULL";
					else
						where += ((where == "") ? "where " : " and ") + " "
								+ c.Field + " " + c.Compare + " ?";
				}
			}
			if (where != "")
				query += " " + where;
			if (orderFactor != "")
				query += " " + orderFactor;
			_select_statement = InternalStorage.getConnection()
					.prepareStatement(query);
			_select_statement_prepared  = true;
		} catch (ULjException e) {
			Log.e(OrmObject.SFS_CORE_TAG, getClass().getSimpleName() +": Can't prepare query "+query+" return error");
			Log.e(OrmObject.SFS_CORE_TAG, e.getMessage());
			
			return false;
		}

		return true;
	}
	private Boolean _closed = true;
	public Boolean isClosed(){
		return _closed;
	}
	private void _close() {
		try {

			if (_cursor != null && !_closed) {
			//	_cursor.close();
				_closed = true;
				_select_statement.close();
	
				removeCursorFromTracker(this); 
			}
		} catch (Exception e) {
			Log.v(SFS_CORE_TAG, getClass().getName() + ":error on close cursor: "
					+ e.getMessage());
			// e.printStackTrace();
		}
		_entityCache.clear();
		_count = 0;
		_cursor = null;
		_abs_position = -1;
		_select_statement_prepared = false;
		//notifyDataSetInvalidated();
		notifyDataSetChanged();

	}
	public static Set<OrmObject> _cursorTracker;

	public static void addCursorToTracker(OrmObject o) {
		if (!CURSOR_CONTROL) return;
		if (_cursorTracker==null) _cursorTracker = new CopyOnWriteArraySet<OrmObject>();
		_cursorTracker.add(o);
	}

	public static void removeCursorFromTracker(OrmObject o) {
		if (!CURSOR_CONTROL) return;
		if (_cursorTracker==null) _cursorTracker = new CopyOnWriteArraySet<OrmObject>();
		_cursorTracker.remove(o);
	}

	public static int getCursorCount(){
		if (!CURSOR_CONTROL) return 0;
		if (_cursorTracker==null) _cursorTracker = new CopyOnWriteArraySet<OrmObject>();
		return _cursorTracker.size();
	}

	public static void printCursors(){
		if (!CURSOR_CONTROL) return;
		if (_cursorTracker==null) _cursorTracker = new CopyOnWriteArraySet<OrmObject>();
		Log.v(MainActivity.LOG_TAG,"**********************************************");
		Log.v(MainActivity.LOG_TAG,"Всего курсоров " + Integer.toString(getCursorCount()));
		for (OrmObject o: _cursorTracker){
			Log.v(MainActivity.LOG_TAG,"Курсор для " + o.getClass().getName());
		}
		Log.v(MainActivity.LOG_TAG,"**********************************************");
		
	}
	private Boolean _select() {
		// close();

		try {
			_invalid = true;
			_abs_position = -1;
			_count = 0;
			_entityCache.clear();
			boolean result = _select_statement.execute();
			_closed = !result;
			if (result && _select_statement.hasResultSet()) {
				_cursor = _select_statement.getResultSet();
				_count = (int) _cursor.getRowCount(MAX_ENTITY_PER_REF);
				_cursor.beforeFirst();
				
			}
			addCursorToTracker(this); 
			notifyDataSetChanged();
			return result;
		} catch (Exception e) {
			Log.e(SFS_CORE_TAG, getClass().getName() + ":error on open cursor: "
					+ e.getMessage());
			return false;
		}
	}

	public Boolean Select() {
		if (!_select_statement_prepared) 
			_select_statement_prepared = prepareSelect(new ArrayList<SqlCriteria>(), "");
		if (_select_statement_prepared) {
			return _select();
		}
		return false;
		
	}

	public Boolean Select(ArrayList<SqlCriteria> criteria, String orderFactor) {
		if (!prepareSelect(criteria, orderFactor))
			return false;
		if (!setSelectParameters(criteria))
			return false;
		return _select();
	}

	public void refresh() {
		close();
		_select();
		notifyDataSetChanged();
	}

	protected GenericEntity<?> castGenericEntity(Class<?> type, int ord) {
		try {
			if (!_cursor.isNull(ord)) {
				return searchGenericEntity(type, _cursor.getLong(ord));
			}
		} 
		catch (ULjException e){
			if (e.getErrorCode()==-197){
				Log.e(OrmObject.SFS_CORE_TAG, getClass().getSimpleName() +": Can't cast field["+ord +"] for query "+_query+" нет текущей строки");
			}
			else {
				Log.e(OrmObject.SFS_CORE_TAG, getClass().getSimpleName() +": Can't cast field["+ord +"] for query "+_query+" return error");
				Log.e(OrmObject.SFS_CORE_TAG, e.getMessage());
			}
			
		}
		catch (Exception e) {
			Log.e(OrmObject.SFS_CORE_TAG, getClass().getSimpleName() +": Can't cast field["+ord +"] for query "+_query+" return error");
			Log.e(OrmObject.SFS_CORE_TAG, e.getMessage());
			
		}
		return null;
	}
/*
	protected GenericEntity<?> searchGenericEntity(Class<?> type, long id) {
		GenericEntity<?> result = null;

		Annotation annotation = type.getAnnotation(OrmEntityOwner.class);
		if (annotation instanceof OrmEntityOwner) {
			OrmObject<?> c = Globals.createRelatedClass(_context,
					((OrmEntityOwner) annotation).owner(), this);

			GenericEntity<?> ge = c.FindById(id);
			if (ge != null) {
				result = ge;
			}
			c.close();

		}

		return result;
	}
*/
	protected GenericEntity<?> searchGenericEntity(Class<?> type, long id) {
		GenericEntity<?> result = null;

		Annotation annotation = type.getAnnotation(OrmEntityOwner.class);
		if (annotation instanceof OrmEntityOwner) {
			OrmObject<?> c = Globals.createRelatedClass(MainActivity.getInstance(), ((OrmEntityOwner) annotation).owner(), false, null);
			GenericEntity<?> ge = c.FindById(id);
			if (ge != null) {
				result = ge;
			}
			c.close();
		}

		return result;
	}
	@SuppressWarnings("unchecked")
	protected Boolean ReadEntity() {
	_current = _entityCache.get(_abs_position);
	if (_current == null) {
			try {
				_current = (E) _entityType.newInstance();
				_current.setCatalog(this);
				_entityCache.put(_abs_position, _current);
				
			} catch (InstantiationException e1) {
				Log.e(MainActivity.LOG_TAG, getClass().getSimpleName() +": Невозможно создать текущий элемент (InstantiationException)");
			} catch (IllegalAccessException e1) {
				Log.e(MainActivity.LOG_TAG, getClass().getSimpleName() +": Невозможно создать текущий элемент (IllegalAccessException)");
			}
			
		}
		else {
			return true;
		}
		E entity = _current;
		if (_cursor == null){
			Log.v(SFS_CORE_TAG, getClass().getSimpleName() +": Попытка чтения из несуществующего курсора");
			return false;
		}
		if (_closed){
			Log.v(SFS_CORE_TAG, getClass().getSimpleName() +": Попытка чтения из закрытого курсора");
			return false;
		}
		// FieldMD fmd;
		// for (int i = 0; i < _fields.size(); i++) {
		for (FieldMD fmd : _fields) {
			int ord = 0;
			// fmd = _fields.elementAt(i);
			try {
				ord = _cursor.getOrdinal(fmd.db_name[0]);

				if (_cursor.isNull(ord))
					continue;
			} 
			catch (ULjException e){
				if (e.getErrorCode()==-197){
					Log.e(OrmObject.SFS_CORE_TAG, getClass().getSimpleName() +": Can't cast field["+ord +"] for query "+_query+" нет текущей строки");
				}
				else {
					Log.e(OrmObject.SFS_CORE_TAG, getClass().getSimpleName() +": Can't cast field["+ord +"] for query "+_query+" return error");
					Log.e(OrmObject.SFS_CORE_TAG, e.getMessage());
				}
				return false;
			}
			catch (Exception e) {
				Log.e(OrmObject.SFS_CORE_TAG, getClass().getSimpleName() +": Reading from query "+_query+" return error");
				Log.e(OrmObject.SFS_CORE_TAG, e.getMessage());
				
				continue;
			}
			if (	   (fmd.type.getSuperclass() == RefGenericEntity.class)
					|| (fmd.type.getSuperclass() == TaskGenericEntity.class)
					|| (fmd.type.getSuperclass() == DocGenericEntity.class)) {
				// составные типы (ВНИМАНИЕ! тут по идее одному пою класса
				// могут соотвествовать несколько полей в базе, пока зашито
				// одно!)
				try {

					GenericEntity<?> ge = castGenericEntity(fmd.type, ord);
					if (ge != null)
						fmd.field.set(entity, ge);
				} 
				catch (Exception e) {
					Log.e(OrmObject.SFS_CORE_TAG,getClass().getSimpleName() +": Reading from query "+_query+" return error");
					Log.e(OrmObject.SFS_CORE_TAG, e.getMessage());
					
					return false;
				}

			}else if (fmd.type.getSuperclass() == SfsEnum.class) {
				
				try {
					Class<?> partypes[] = {int.class };
					Object arglist[] = { _cursor.getInt(ord) };
					Constructor<?> ct = fmd.type.getConstructor(partypes);
					Object o =  ct.newInstance(arglist);
					if (o!=null) fmd.field.set(entity, o);
					
				} 
				catch (Exception e) {
					Log.e(OrmObject.SFS_CORE_TAG,getClass().getSimpleName() +": Reading from query "+_query+" return error");
					Log.e(OrmObject.SFS_CORE_TAG, e.getMessage());
					
					return false;
				}

			}  
			else if (fmd.type == RefGenericEntity.class) {
				try {

					GenericEntity<?> ge = getGenericRefEntity(fmd.class_name,
							_cursor.getInt(ord));
					if (ge != null)
						fmd.field.set(entity, ge);
				}
				catch (ULjException e){
					if (e.getErrorCode()==-197){
						Log.e(OrmObject.SFS_CORE_TAG, getClass().getSimpleName() +": Can't cast field["+ord +"] for query "+_query+" нет текущей строки");
					}
					else {
						Log.e(OrmObject.SFS_CORE_TAG, getClass().getSimpleName() +": Can't cast field["+ord +"] for query "+_query+" return error");
						Log.e(OrmObject.SFS_CORE_TAG, e.getMessage());
					}
					return false;
				}
				catch (Exception e) {
					Log.v(OrmObject.SFS_CORE_TAG, getClass().getSimpleName() +": Reading from query  "+_query+" return error");
					Log.v(OrmObject.SFS_CORE_TAG, e.getMessage());
				
					return false;
				}
			} else if (fmd.type == TaskGenericEntity.class) {
				try {

					GenericEntity<?> ge = getGenericTaskEntity(fmd.class_name,
							_cursor.getInt(ord));
					if (ge != null)
						fmd.field.set(entity, ge);
				}
				catch (ULjException e){
					if (e.getErrorCode()==-197){
						Log.e(OrmObject.SFS_CORE_TAG, getClass().getSimpleName() +": Can't cast field["+ord +"] for query "+_query+" нет текущей строки");
					}
					else {
						Log.e(OrmObject.SFS_CORE_TAG, getClass().getSimpleName() +": Can't cast field["+ord +"] for query "+_query+" return error");
						Log.e(OrmObject.SFS_CORE_TAG, e.getMessage());
					}
					return false;
				}catch (Exception e) {
					Log.v(OrmObject.SFS_CORE_TAG, "Reading from query  "+_query+" return error");
					Log.v(OrmObject.SFS_CORE_TAG, e.getMessage());
					e.getStackTrace();
					return false;
				}
			} else if (fmd.type == DocGenericEntity.class) {
				try {

					GenericEntity<?> ge = getGenericDocEntity(fmd.class_name,
							_cursor.getInt(ord));
					if (ge != null)
						fmd.field.set(entity, ge);
				} 
				catch (ULjException e){
					if (e.getErrorCode()==-197){
						Log.e(OrmObject.SFS_CORE_TAG, getClass().getSimpleName() +": Can't cast field["+ord +"] for query "+_query+" нет текущей строки");
					}
					else {
						Log.e(OrmObject.SFS_CORE_TAG, getClass().getSimpleName() +": Can't cast field["+ord +"] for query "+_query+" return error");
						Log.e(OrmObject.SFS_CORE_TAG, e.getMessage());
					}
					return false;
				}
				catch (Exception e) {
					Log.v(OrmObject.SFS_CORE_TAG, getClass().getSimpleName() +": Reading from query  "+_query+" return error");
					Log.v(OrmObject.SFS_CORE_TAG, e.getMessage());
					e.getStackTrace();
					return false;
				}
			} else {
				try {
					if (fmd.type == Integer.class || fmd.type == Integer.TYPE) {
						fmd.field.setInt(entity, _cursor.getInt(ord));
					} else if (fmd.type == Long.class || fmd.type == Long.TYPE) {
						fmd.field.setLong(entity, _cursor.getLong(ord));
					} else if (fmd.type == Float.class
							|| fmd.type == Float.TYPE) {
						fmd.field.setFloat(entity, _cursor.getFloat(ord));
					} else if (fmd.type == Double.class
							|| fmd.type == Double.TYPE) {
						fmd.field.setDouble(entity, _cursor.getDouble(ord));
					} else if (fmd.type == String.class) {
						fmd.field.set(entity, _cursor.getString(ord));
					} else if (fmd.type == Boolean.class
							|| fmd.type == Boolean.TYPE) {
						fmd.field.set(entity, _cursor.getBoolean(ord));
					} else if (fmd.type == Date.class) {
						fmd.field.set(entity, _cursor.getDate(ord));
					}
				} catch (ULjException e){
					if (e.getErrorCode()==-197){
						Log.e(OrmObject.SFS_CORE_TAG, getClass().getSimpleName() +": Can't cast field["+ord +"] for query "+_query+" нет текущей строки");
					} else {
						Log.e(OrmObject.SFS_CORE_TAG, getClass().getSimpleName() +": Can't cast field["+ord +"] for query "+_query+" return error");
						Log.e(OrmObject.SFS_CORE_TAG, e.getMessage());
					}
					
					return false;
				} catch (Exception e) {
					Log.v(OrmObject.SFS_CORE_TAG, getClass().getSimpleName() +": Reading from query  "+_query+" return error");
					Log.v(OrmObject.SFS_CORE_TAG, e.getMessage());
					e.getStackTrace();
					
					return false;
				}
			}
		}
		
		return true;
	}

	protected GenericEntity<?> getGenericDocEntity(String class_name, int id) {
		return null;
	}

	protected GenericEntity<?> getGenericTaskEntity(String class_name, int id) {
		return null;
	}

	protected GenericEntity<?> getGenericRefEntity(String class_name, int id) {
		return null;
	}

	public E FindById(long id) {
		_invalid = true;
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		criteria.add(new SqlCriteria("Id", id));
		E result = null;
		if (Select(criteria)) {
			try {
				if (_cursor.first()) {
					_abs_position = 0;
					result = (E) Current();
				}
			} catch (Exception e) {
				Log.v(OrmObject.SFS_CORE_TAG, getClass().getSimpleName() +": Can't find by Id = " + id + " for query " + _query + " return error: " + e.getMessage());
			}
		}
		close();
		
		return result;
	}

	public E FindById(long author, long id) {
		_invalid = true;
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		criteria.add(new SqlCriteria("Id", id));
		criteria.add(new SqlCriteria("Author", author));
		E result = null;
		if (Select(criteria)) {
			try {
				if (_cursor.first()) {
					_abs_position = 0;
					result = Current();
				}
			} catch (Exception e) {
				Log.v(OrmObject.SFS_CORE_TAG, getClass().getSimpleName() +": Can't find by Id = " + id + " for query " + _query + " return error: " + e.getMessage());
			}
		}
		
		return result;
	}

	public static Boolean setCommandParameterValue(PreparedStatement statement, Object entity, FieldMD f, int colId) {
		try {
			Object object = f.field.get(entity);
			if (object == null) {
				statement.setNull(colId);
				return true;
			}
			if (f.type == Integer.TYPE) {
				statement.set(colId, (Integer) object);
				return true;
			} else if (f.type == Long.TYPE) {
				statement.set(colId, (Long) object);
				return true;
			} else if (f.type == String.class) {
				statement.set(colId, (String) object);
				return true;
			} else if (f.type == Float.TYPE || f.type == Float.class) {
				statement.set(colId, (Float) object);
				return true;
			} else if (f.type == Double.TYPE || f.type == Double.class) {
				statement.set(colId, (Double) object);
				return true;
			} else if (f.type == Boolean.class) {
				statement.set(colId, (Boolean) object);
				return true;
			} else if (f.type == Date.class) {
				statement.set(colId, (Date) object);
				return true;
			} else if (f.type.getSuperclass() == RefGenericEntity.class) {
				// Составной тип!
				statement.set(colId, ((RefGenericEntity) object).Id);
				return true;
			} else if ((f.type == TaskGenericEntity.class)
					|| (f.type.getSuperclass() == TaskGenericEntity.class)) {
				// составной тип!
				statement.set(colId, ((TaskGenericEntity) object).Id);
				return true;
			} else if ((f.type == DocGenericEntity.class)
					|| (f.type.getSuperclass() == DocGenericEntity.class)) {
				// составной тип!
				statement.set(colId, ((DocGenericEntity) object).Id);
				return true;
			}  else if (f.type.getSuperclass() == SfsEnum.class) {
				statement.set(colId, ((SfsEnum) object).getId());
				return true;
			}
			
			return false;
		}

		catch (Exception e) {
			Log.v(OrmObject.SFS_CORE_TAG, "Can't set parameter[" + colId + "] for statement " + statement.toString());
			Log.v(OrmObject.SFS_CORE_TAG, e.getMessage());
			e.getStackTrace();
			return false;
		}

	}

	@SuppressWarnings("unchecked")
	public SfsContentView GetSimpleListItemView(ListView lv) {
		return new GenericListItemView(_context, this, lv);
	}

	abstract public SfsContentView GetExtendedListItemView(ListView lv);

	// LIST ADAPTER

	protected int _selected_position = -1;
	protected PreparedStatement _delete_statement;

	public void setSelection(int position) {
		_selected_position = position;
		notifyDataSetInvalidated();

	}

	public E getSelection() {
		// return _selected_entity;
		if (_selected_position == -1)
			return null;
		return getItem(_selected_position);
	}

	public int getCount() {

		return (int) Count();
	}

	public E getItem(int at) {

		if (To(at))
			return Current();
		return null;
	}

	public long getItemId(int at) {
		if (To(at)) {
			E current = Current(); //BUGFIX #226,231,124,159 USMANOV
			if (current == null) {
				return 0; 
			} else {
				return current.Id;
			}
		}
		
		return 0;
	}

	public int getItemViewType(int arg0) {
		return IGNORE_ITEM_VIEW_TYPE;
	}

	protected ViewGroup _currentViewGroup = null;
	
	public View getView(int position, View convertView, ViewGroup parent) {
		SfsContentView v = null;
		Boolean neednew = false;
		if (convertView == null) {
			_currentViewGroup = parent;
			neednew = true;
		} else {
			if (!parent.equals(_currentViewGroup)) {
				neednew = true;
			}
		}
		neednew = true;
		Boolean fetched = To(position);
		if (fetched){
			_invalid = true;
			ReadEntity();
		}
		if (neednew) {
			switch (_listType) {
				case SIMPLE_LIST: {
					v = GetSimpleListItemView((ListView) parent);
				} break;
				case EXTENDED_LIST: {
					v = GetExtendedListItemView((ListView) parent);
				} break;
			}
			v.inflate();
		} else {
			v = (SfsContentView) convertView;
		}

		if (fetched) {
			if (v != null) {
				v.fill();
			}
		}

		return v;
	}

	public boolean isEmpty() {
		return false;
	}

	public void registerDataSetObserver(DataSetObserver observer) {
		_dataSetObservable.registerObserver(observer);
	}

	public void unregisterDataSetObserver(DataSetObserver observer) {
		_dataSetObservable.unregisterObserver(observer);

	}

	public void notifyDataSetChanged() {
		MainActivity.sInstance.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				_dataSetObservable.notifyChanged();
			}
		});
	}

	public void notifyDataSetInvalidated() {
		MainActivity.sInstance.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				_dataSetObservable.notifyInvalidated();	
			}
		});
	}

	public boolean areAllItemsEnabled() {
		return true;
	}

	public boolean isEnabled(int arg0) {
		return true;
	}

	public Boolean save() {
		return false;
	}

	public boolean hasNext() {
		return (_selected_position + 1) < getCount();
	}

	public E next() {
		if (Next())
			return Current();
		return null;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}
