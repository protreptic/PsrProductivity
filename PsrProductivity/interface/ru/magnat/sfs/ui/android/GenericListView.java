package ru.magnat.sfs.ui.android;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArraySet;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import ru.magnat.sfs.android.Command;
import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.EntityActionCommand;
import ru.magnat.sfs.android.OnBackPressedListener;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.bom.EventListenerSubscriber;
import ru.magnat.sfs.bom.FieldMD;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.IEventListener;
import ru.magnat.sfs.bom.OrmObject;
import ru.magnat.sfs.ui.android.OnGenericListListener.OnGlItemLongClickListener;
import ru.magnat.sfs.ui.android.OnGenericListListener.OnGlItemShortClickListener;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public abstract class GenericListView<C extends OrmObject<E>, E extends GenericEntity<C>, O extends GenericEntity<?>> extends SfsContentView implements Closeable {

	private List<DataObjectProperty> list = new ArrayList<DataObjectProperty>();
	private DataObjectAdapter _ada;
	protected ListAdapter _adapter;
	protected E _entity;
	protected O _owner;
	protected C _catalog;
	
	public EntityActionCommand<E> openEntity = new EntityActionCommand<E>() {
		private E _entity;

		public void execute() {
			OpenEntity(_entity);
		}

		public void setEntity(E entity) {
			_entity = entity;
		}
	};
	
	public Command createEntity = new Command() {
		public void execute() {
			createEntity();
		}
	};
	
	public GenericListView(Context context, C ormObject, O owner) {
		super(context);
		
		_owner = owner;
		_adapter = ormObject;
		_catalog = ormObject;
		if (_catalog != null && !_catalog.isClosed() && !_catalog.isEmpty()) {
			_entity = _catalog.Current();
		}
	}

	protected void OpenEntity(E entity) {

		@SuppressWarnings("unchecked")
		SfsContentView v = ((IBO<?, E, ?>) _catalog).GetEditView(entity);
		if (v == null) {
			return;
		}
		v.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
			public void onViewAttachedToWindow(View v) {
				suspend();
			}

			public void onViewDetachedFromWindow(View v) {
				wakeup();
				requery();
			}
		});

		MainActivity.sInstance.addToFlipper(v, this.getClass().getSimpleName());
		MainActivity.sInstance.addOnBackPressedListener((OnBackPressedListener) v);
	}

	abstract protected void createEntity();

	protected void showAddButton() {
		ImageButton b = (ImageButton) findViewById(R.id.add_button);
		if (b == null)
			return;
		b.setVisibility(VISIBLE);
		b.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Dialogs.createDialog("", "Ñîçäàòü íîâûé çàïðîñ?", createEntity,
						Command.NO_OP).show();
			}
		});
		((ImageButton) findViewById(R.id.caption_action)).setVisibility(GONE);
	}

	protected GenericListView<C, E, O> ñatalogInflate(int layout, String caption) {

		layoutInflater.inflate(layout, this);
		requery();
		TextView tv = (TextView) findViewById(R.id.caption_text);
		tv.setGravity(Gravity.CENTER_VERTICAL);
		tv.setTextSize(25l);
		tv.setText(caption);

		lv = (ListView) findViewById(R.id.list);

		lv.setAdapter(this._catalog);
		lv.setDividerHeight(1);
		lv.setItemsCanFocus(false);
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				openEntity.setEntity(_catalog.getItem(position));
				openEntity.execute();
			}
		});

		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				return ItemLongClick(_catalog.getItem(position));
			}
		});
		if (this._owner != null) {
			((ImageButton) findViewById(R.id.caption_action)).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					createEntity.execute();
				}
			});
		} else {
			//((ImageButton) findViewById(R.id.caption_action)).setVisibility(GONE);
			mCalendar.setTime(new Date());
			mDate = new DateTime(mCalendar.getTimeInMillis());
			Button dateButton = ((Button) findViewById(R.id.date_button1));
			if (dateButton == null) {
				return this;
			}
			((Button) findViewById(R.id.date_button1)).setText(mDate.toString(DateTimeFormat.forPattern("dd.MM.yyyy")));
			((Button) findViewById(R.id.date_button1)).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Dialogs.createDateDialog(getContext(), new OnDateSetListener() {
						public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
							mCalendar.set(year, monthOfYear, dayOfMonth);
							mDate = new DateTime(mCalendar.getTimeInMillis());						
							if (!mDate.toString(DateTimeFormat.forPattern("dd.MM.yyyy")).equals(((Button) findViewById(R.id.date_button1)).getText())) {
								((Button) findViewById(R.id.date_button1)).setText(mDate.toString(DateTimeFormat.forPattern("dd.MM.yyyy")));
								requery();
							}
						}
					}, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();
				}
			});
			((Button) findViewById(R.id.date_button1)).setVisibility(View.VISIBLE);
		}
		return this;
	}

	protected Calendar mCalendar = Calendar.getInstance(new Locale("ru", "RU")); 
	protected DateTime mDate;
	
	protected boolean ItemLongClick(E item) {
		return false; 
	} 

	public SfsContentView inflate() {
		layoutInflater.inflate(R.layout.list_layout, this);
		ListView lv = (ListView) findViewById(R.id.list);
		_ada = new DataObjectAdapter(this.getContext(), list);
		lv.setAdapter(_ada);
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				fireItemShortClick((GenericEntity<?>) _ada.getItem(position));

			}

		});

		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				fireItemLongClick((GenericEntity<?>) _ada.getItem(position));
				return false;
			}
		});
		return this;
	}

	@Override
	public void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
	}

	@Override
	public void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
	}

	protected void suspend() {
		lv.setAdapter(null);
	}

	protected void wakeup() {
		lv.setAdapter(_catalog);
	}

	protected void requery() {
		_catalog.Select();
	}

	public class DataObjectProperty {
		public DataObjectProperty(String property, String value) {
			this.property = property;
			this.value = value;
		}

		public String property = "";
		public String value = "";
	}

	class DataObjectAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		private List<DataObjectProperty> list;

		public DataObjectAdapter(Context context, List<DataObjectProperty> list) {
			mInflater = LayoutInflater.from(context);
			this.list = list;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.data_item, parent,
						false);
			}
			TextView title = (TextView) convertView
					.findViewById(R.id.item_title);
			TextView sub = (TextView) convertView
					.findViewById(R.id.item_subtitle);

			DataObjectProperty info = list.get(position);
			title.setText(info.value);
			sub.setText(info.property);
			return convertView;
		}

		public int getCount() {
			return list.size();
		}

		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		public long getItemId(int arg0) {
			return arg0;
		}
	}

	@Override
	public void refresh(){
		requery();
	}
	
	@Override
	public void fill() {

		if (_catalog._fields == null)
			return;
		Vector<FieldMD> fmds = _catalog._fields;

		// listItems = (String[])Array.newInstance(String.class, fs.length);
		try {
			for (int i = 0; i < fmds.size(); i++) {

				FieldMD fmd = fmds.elementAt(i);
				String p = fmd.label;
				String v = "";
				Object o = fmd.field.get(_entity);
				if (o != null)
					v = o.toString();

				list.add(new DataObjectProperty(p, v));
				// listItems[i] = v;

			}
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	@Override
	public void moveTaskToBack(Boolean handled) {
	}

	private final Set<IEventListener> _eventItemShortClickListeners = new CopyOnWriteArraySet<IEventListener>();

	public void setOnItemShortClickListener(
			OnGlItemShortClickListener eventListener) {
		EventListenerSubscriber.setListener(_eventItemShortClickListeners,
				eventListener);
	}

	public void addOnItemShortClickListener(
			OnGlItemShortClickListener eventListener) {
		EventListenerSubscriber.addListener(_eventItemShortClickListeners,
				eventListener);
	}

	public void fireItemShortClick(GenericEntity<?> entity) {
		for (IEventListener eventListener : _eventItemShortClickListeners)
			((OnGlItemShortClickListener) eventListener).onItemShortClick(this,
					entity);
	}

	private final Set<IEventListener> _eventItemLongClickListeners = new CopyOnWriteArraySet<IEventListener>();
	protected ListView lv;

	public void setOnItemLongClickListener(
			OnGlItemLongClickListener eventListener) {
		EventListenerSubscriber.setListener(_eventItemLongClickListeners,
				eventListener);
	}

	public void addOnItemLongClickListener(
			OnGlItemLongClickListener eventListener) {
		EventListenerSubscriber.addListener(_eventItemLongClickListeners,
				eventListener);
	}

	public void fireItemLongClick(GenericEntity<?> entity) {
		for (IEventListener eventListener : _eventItemLongClickListeners)
			((OnGlItemLongClickListener) eventListener).onItemLongClick(this,
					entity);
	}

	public void detachFromSource() {
		if (this._catalog != null) {
			this._catalog.close();
		}
	}

	public void attachToSource() {
		requery();
		
	}
	
	@Override
	public void close() throws IOException {
		detachFromSource();
	}
}
