package ru.magnat.sfs.bom;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class GenericEntity<C extends OrmObject<? extends GenericEntity<C>>> {

	@OrmEntityField(DisplayName = "Код", isPrimary = 1, fields = "Id")
	public long Id;

	protected C _catalog = null;

	@SuppressWarnings("unchecked")
	public void setCatalog(OrmObject<?> ormObject) {
		// public void setCatalog(C ormObject) {
		_catalog = (C) ormObject;
	}
	C createCatalog(Class<C> clazz)
    {
        try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
        return null;
    }

	
	public Boolean save() {
		Boolean unbound = (_catalog==null);
		if (unbound){
			_catalog = openCatalog();
		}
		_catalog.setCurrent(this);
		Boolean result = _catalog.save();
		if (unbound)
			_catalog.close();
		return result;
	}

	@Override
	public GenericEntity<C> clone() {
		try {
			@SuppressWarnings("unchecked")
			GenericEntity<C> e = this.getClass().newInstance();
			Field[] fs = e.getClass().getFields();
			for (int i = 0; i < fs.length; i++) {
				fs[i].set(e, fs[i].get(this));
			}
			return e;
		} catch (InstantiationException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {

			e.printStackTrace();
		}

		return null;
	}

	private final Set<IEventListener> _onValueChangedListeners = new CopyOnWriteArraySet<IEventListener>();

	public void setOnValueChangedListener(OnValueChangedListener eventListener) {
		EventListenerSubscriber.setListener(_onValueChangedListeners,
				eventListener);
	}

	public void addOnValueChangedListener(OnValueChangedListener eventListener) {
		EventListenerSubscriber.addListener(_onValueChangedListeners,
				eventListener);
	}

	public void removeOnValueChangedListener(
			OnValueChangedListener eventListener) {
		EventListenerSubscriber.removeListener(_onValueChangedListeners,
				eventListener);
	}

	public void onValueChanged(String fieldName) {
		for (IEventListener eventListener : _onValueChangedListeners)
			((OnValueChangedListener) eventListener).onValueChanged(this,
					fieldName);
	}

	@SuppressWarnings("unchecked")
	public C openCatalog() {
		if (_catalog == null) {
			Annotation annotation = this.getClass().getAnnotation(
					OrmEntityOwner.class);
			if (annotation instanceof OrmEntityOwner) {
				OrmEntityOwner props = (OrmEntityOwner) annotation;
				Class<?> cls = props.owner();
				_catalog = (C) Globals.createOrmObject(cls);
			}
		}
		
		return _catalog;
	}

	public C getCatalog() {
		return _catalog;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {
		GenericEntity<C> object = (GenericEntity<C>) o;
		if (object == null) {
			return false;
		}
		return Id == object.Id;
	}

	public List<Map<String, Object>> mFieldList;
	
	public class DataBinding implements List<Map<String, ?>> {

		final GenericEntity<C> _entity;
		private ArrayList<FieldMD> _fields;
		
		public DataBinding(GenericEntity<C> entity) {
			_entity = entity;
			this._fields = new ArrayList<FieldMD>();
			Field[] fs = _entity.getClass().getFields();
			for (Field f : fs) {
				Annotation a = f.getAnnotation(EntityCardField.class);
				if (a instanceof EntityCardField) {
					EntityCardField oef = (EntityCardField) a;
					FieldMD fmd = new FieldMD();
					fmd.field = f;
					fmd.label = oef.DisplayName();
					fmd.sortkey = oef.Sortkey();
					fmd.selectMethod = oef.SelectMethod();
					fmd.formatString = oef.format();
					_fields.add(fmd);
				}
			}
			Collections.sort(_fields);
		}

		public Map<String, ?> get(int location) {
			FieldMD md = _fields.get(location);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("property", md.label);
			try {
				Object o = md.field.get(_entity);
				String value = "";
				if (o != null) {
					if (o instanceof Date) {
						String format = md.formatString;
						SimpleDateFormat formatter = new SimpleDateFormat(
								format, Locale.getDefault());
						value = formatter.format((Date) o);
					} else if (o instanceof Boolean) {
						value = ((Boolean) o) ? "Да" : "Нет";
					} else if (o instanceof Float) {
						value = Globals
								.customFormat(md.formatString, (Float) o);
					} else {
						value = o.toString();
					}
				}
				map.put("value", value);
			} catch (IllegalArgumentException e) {

				e.printStackTrace();
				map.put("value", null);
			} catch (IllegalAccessException e) {

				e.printStackTrace();
				map.put("value", null);
			}
			try {
				map.put("selectMethod", md.selectMethod);
			} catch (Exception e) {
			}

			return map;
		}

		public int size() {
			return _fields.size();
		}

		public void add(int location, Map<String, ?> object) {
		}

		public boolean add(Map<String, ?> object) {
			return false;
		}

		public boolean addAll(int location,
				Collection<? extends Map<String, ?>> collection) {
			return false;
		}

		public boolean addAll(Collection<? extends Map<String, ?>> collection) {
			return false;
		}

		public void clear() {
		}

		public boolean contains(Object object) {
			return false;
		}

		public boolean containsAll(Collection<?> collection) {
			return false;
		}

		public int indexOf(Object object) {
			return 0;
		}

		public boolean isEmpty() {
			return false;
		}

		public Iterator<Map<String, ?>> iterator() {
			return null;
		}

		public int lastIndexOf(Object object) {
			return 0;
		}

		public ListIterator<Map<String, ?>> listIterator() {
			return null;
		}

		public ListIterator<Map<String, ?>> listIterator(int location) {
			return null;
		}

		public Map<String, ?> remove(int location) {
			return null;
		}

		public boolean remove(Object object) {
			return _fields.remove(object);
		}

		public boolean removeAll(Collection<?> collection) {
			return _fields.removeAll(collection);
		}

		public boolean retainAll(Collection<?> collection) {
			return false;
		}

		public Map<String, ?> set(int location, Map<String, ?> object) {
			return null;
		}

		public List<Map<String, ?>> subList(int start, int end) {
			return null;
		}

		public Object[] toArray() {
			return null;
		}

		public <T> T[] toArray(T[] array) {
			return null;
		}
	}

	public DataBinding _binding;

	public List<Map<String, ?>> getProperties() {
		if (_binding == null) {
			_binding = new DataBinding(this);
		}
		return _binding;
	}

}
