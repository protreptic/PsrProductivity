package ru.magnat.sfs.ui.android.doc.requestonchangeoutlet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import ru.magnat.sfs.android.Command;
import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.ref.generic.RefGeneric.HierarchyMode;
import ru.magnat.sfs.bom.ref.outlet.RefOutlet;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.ref.outletservicetype.RefServiceType;
import ru.magnat.sfs.bom.ref.outletservicetype.RefServiceTypeEntity;
import ru.magnat.sfs.bom.ref.storechannel.RefStoreChannel;
import ru.magnat.sfs.bom.ref.storechannel.RefStoreChannelEntity;
import ru.magnat.sfs.bom.ref.storetype.RefStoreType;
import ru.magnat.sfs.bom.ref.storetype.RefStoreTypeEntity;
import ru.magnat.sfs.bom.requestonchangeoutlet.DocRequestOnChangeOutletEntity;
import ru.magnat.sfs.bom.requestonchangeoutlet.DocRequestOnChangeOutletJournal;
import ru.magnat.sfs.bom.task.visit.TaskVisitEntity;
import ru.magnat.sfs.ui.android.GenericEntityView;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public final class DocRequestOnChangeOutletView
		extends
		GenericEntityView<DocRequestOnChangeOutletJournal, DocRequestOnChangeOutletEntity>
		implements OnItemClickListener {

	AlertDialog _alertDialog;
	private RefStoreChannel _refStoreChannel;
	private RefServiceType _refServiceType;
	private RefStoreType _refStoreType;
	private RefOutlet _refOutlet;
	final boolean _canSelectOutlet;

	TaskVisitEntity _visit;
	OnClickListener radioListener;

	ExpandableListAdapter mAdapter;

	SimpleAdapter _adapter;

	private EditText _editText; // текстовое поле SDV 2012-09-05
	final int _textHeight = 300; // высота текстового поля SDV 2012-09-12
	final int _textWidth = 350; // ширина текстового поля SDV 2012-09-12

	ArrayAdapter<String> str_arr_adapter; // Создаю адаптер списочка значений
	final int count_bank_day = 30;
	static final int TIME_DIALOG_ID = 0;
	static final int DATE_DIALOG_ID = 1;

	public DocRequestOnChangeOutletView(Context context,
			DocRequestOnChangeOutletJournal journal,
			DocRequestOnChangeOutletEntity entity) {
		super(context, journal, entity);
		_canSelectOutlet = (entity.Outlet == null);

	}

	protected void updateCaption() {

		((TextView) findViewById(R.id.caption)).setText(_entity.toString());
	}

	@Override
	public SfsContentView inflate() {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.doc_credit_request_tab, this);
		_adapter = new SimpleAdapter(getContext(), _entity.getProperties(),
				android.R.layout.simple_list_item_2, new String[] { "value",
						"property" }, new int[] { android.R.id.text1,
						android.R.id.text2 });
		ListView lv = (ListView) findViewById(R.id.list);
		lv.setAdapter(_adapter);
		lv.setOnItemClickListener(this);
		updateCaption();

		return this;

		// *************************************
		// Конец куска как в DocOrderEntity
		// *************************************
	}

	@SuppressWarnings("unused")
	private void changeIsMark() {
		if (_entity.getReadOnly()) {
			Dialogs.createDialog("SFS",
					"Нельзя исправлять отправленный запрос", Command.NO_OP)
					.show();
			return;
		}
		_entity.setMarked(!_entity.IsMark);
		_adapter.notifyDataSetChanged();
	}

	// Торговая точка SDV 2012-09-11
	protected void changeOutlet() {
		if (_entity.getReadOnly()) {
			Dialogs.createDialog("SFS",
					"Нельзя исправлять отправленный запрос", Command.NO_OP)
					.show();
			return;
		}
		if (_canSelectOutlet) {
			_refOutlet = (RefOutlet) Globals.createOrmObject(RefOutlet.class);

			_refOutlet.Select(false, false, HierarchyMode.OnlyEntity);
			_alertDialog = Dialogs.createSelectFromListDialog(_refOutlet,
					"Выбор торговой точки",
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							_entity.Outlet = (RefOutletEntity) ((RefOutletEntity) _refOutlet
									.getItem(which)).clone();
							_adapter.notifyDataSetChanged();
							updateCaption();
							_entity.setContent(_entity.Outlet);
						}
					});
			_alertDialog.show();
		}
	}

	// Наименование
	protected void changeDescr() {
		if (_entity.getReadOnly()) {
			Dialogs.createDialog("SFS",
					"Нельзя исправлять отправленный запрос", Command.NO_OP)
					.show();
			return;
		}
		_editText = new EditText(getContext());
		// инициализирую поле комментов
		_editText.setHeight(_textHeight);
		_editText.setMaxHeight(_textHeight);
		_editText.setWidth(_textWidth);
		_editText.setMaxWidth(_textWidth);
		_editText.setGravity(0);

		_editText.setText(_entity.Descr);
		Dialogs.createDialog("Наименование", "", _editText, null,
				new Command() {
					public void execute() {
						_entity.Descr = _editText.getText().toString();
						_adapter.notifyDataSetChanged();
					}
				}, Command.NO_OP).show();
	}

	// Адресс
	protected void changeAddress() {
		if (_entity.getReadOnly()) {
			Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
			return;
		}
		_editText = new EditText(getContext());
		// инициализирую поле комментов
		// _editText.setMinLines(3);
		// _editText.setMaxLines(6);

		// _editText.setHeight(300);
		// _editText.setWidth(350);
		// _editText.setMaxHeight(300);
		// _editText.setMaxWidth(350);
		// _editText.setGravity(0);

		_editText.setHeight(_textHeight);
		_editText.setMaxHeight(_textHeight);
		_editText.setWidth(_textWidth);
		_editText.setMaxWidth(_textWidth);
		_editText.setGravity(0);

		// _editText.setMinEms(10);
		// _editText.setMaxEms(10);

		_editText.setText(_entity.Address);
		Dialogs.createDialog("Адрес", "", _editText, null, new Command() {
			public void execute() {
				_entity.Address = _editText.getText().toString();
				_adapter.notifyDataSetChanged();
			}
		}, Command.NO_OP).show();
	}

	// Центральный офис
	protected void changeHQ() {
		if (_entity.getReadOnly()) {
			Dialogs.createDialog("SFS",
					"Нельзя исправлять отправленный запрос", Command.NO_OP)
					.show();
			return;
		}
		_entity.IsHQ = (_entity.IsHQ == null) ? true : !_entity.IsHQ;
		_adapter.notifyDataSetChanged();
	}

	// Распределительный центр
	protected void changeDC() {
		if (_entity.getReadOnly()) {
			Dialogs.createDialog("SFS",
					"Нельзя исправлять отправленный запрос", Command.NO_OP)
					.show();
			return;
		}
		_entity.IsDC = (_entity.IsDC == null) ? true : !_entity.IsDC;
		_adapter.notifyDataSetChanged();
	}

	// Магазин
	protected void changeShop() {
		if (_entity.getReadOnly()) {
			Dialogs.createDialog("SFS",
					"Нельзя исправлять отправленный запрос", Command.NO_OP)
					.show();
			return;
		}
		_entity.IsShop = (_entity.IsShop == null) ? true : !_entity.IsShop;
		_adapter.notifyDataSetChanged();
	}

	// Точка принятия заказа
	protected void changeOP() {
		if (_entity.getReadOnly()) {
			Dialogs.createDialog("SFS",
					"Нельзя исправлять отправленный запрос", Command.NO_OP)
					.show();
			return;
		}
		_entity.IsOP = (_entity.IsOP == null) ? true : !_entity.IsOP;
		_adapter.notifyDataSetChanged();
	}

	// Канал
	protected void changeChannel() {
		if (_entity.getReadOnly()) {
			Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
			return;
		}
		if (_alertDialog != null) {
			_alertDialog.cancel();
		}
		_refStoreChannel = (RefStoreChannel) Globals.createOrmObject(RefStoreChannel.class);
		_refStoreChannel.Select(false, false, HierarchyMode.OnlyEntity);
		_alertDialog = Dialogs.createSelectFromListDialog(_refStoreChannel,
				"Канал", new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						_entity.Channel = (((RefStoreChannelEntity) _refStoreChannel
								.getItem(which)));
						_adapter.notifyDataSetChanged();
					}
				});
		_alertDialog.show();
	}

	// Тип обслуживания
	protected void changeServiceType() {
		if (_entity.getReadOnly()) {
			Dialogs.createDialog("SFS",
					"Нельзя исправлять отправленный запрос", Command.NO_OP)
					.show();
			return;
		}
		_refServiceType = (RefServiceType) Globals
				.createOrmObject(RefServiceType.class);

		_refServiceType.Select(false, false, HierarchyMode.OnlyEntity);
		_alertDialog = Dialogs.createSelectFromListDialog(_refServiceType,
				"Тип обслуживания",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						_entity.ServiceType = (((RefServiceTypeEntity) _refServiceType
								.getItem(which)));
						_adapter.notifyDataSetChanged();
					}
				});
		_alertDialog.show();
	}

	// Тип точки
	protected void changeStoreType() {
		if (_entity.getReadOnly()) {
			Dialogs.createDialog("SFS",
					"Нельзя исправлять отправленный запрос", Command.NO_OP)
					.show();
			return;
		}
		_refStoreType = (RefStoreType) Globals.createOrmObject(RefStoreType.class);

		_refStoreType.Select(false, false, HierarchyMode.OnlyEntity);
		_alertDialog = Dialogs.createSelectFromListDialog(_refStoreType,
				"Тип точки",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						_entity.StoreType = (((RefStoreTypeEntity) _refStoreType
								.getItem(which)));
						_adapter.notifyDataSetChanged();
					}
				});
		_alertDialog.show();
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		@SuppressWarnings("unchecked")
		HashMap<String, ?> menuItem = (HashMap<String, ?>) _adapter
				.getItem(position);
		if (menuItem == null)
			return;
		String selectMethod = (String) menuItem.get("selectMethod");
		if ((selectMethod == null) || (selectMethod.isEmpty()))
			return;
		Method method = null;
		try {
			method = this.getClass().getDeclaredMethod(selectMethod);
			method.setAccessible(true);
		} catch (SecurityException e) {

			e.printStackTrace();
			return;
		} catch (NoSuchMethodException e) {

			e.printStackTrace();
			return;
		}
		try {
			method.invoke(this);
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
	}

	@Override
	public void fill() {
		
	}

	@Override
	protected Boolean onRemove() {

		this._catalog.save();
		return true;

	}
}
