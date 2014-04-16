package ru.magnat.sfs.ui.android.doc.requestoutlet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import ru.magnat.sfs.android.Command;
import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.AddressResolver;
import ru.magnat.sfs.android.AddressResolver.OnAddressResolvedListener;
import ru.magnat.sfs.android.NumberPickerDialog;
import ru.magnat.sfs.android.NumberPickerDialog.OnNumberSetListener;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.OrmObject;
import ru.magnat.sfs.bom.SfsEnum;
import ru.magnat.sfs.bom.ref.generic.RefGeneric.HierarchyMode;
import ru.magnat.sfs.bom.ref.outlet.RefOutlet;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.ref.outletservicetype.RefServiceType;
import ru.magnat.sfs.bom.ref.outletservicetype.RefServiceTypeEntity;
import ru.magnat.sfs.bom.ref.storechannel.RefStoreChannel;
import ru.magnat.sfs.bom.ref.storetype.RefStoreType;
import ru.magnat.sfs.bom.ref.storetype.RefStoreTypeEntity;
import ru.magnat.sfs.bom.requestoutlet.DocRequestOutletEntity;
import ru.magnat.sfs.bom.requestoutlet.DocRequestOutletJournal;
import ru.magnat.sfs.bom.requestoutlet.OutletSizeVariant;
import ru.magnat.sfs.bom.task.visit.TaskVisitEntity;
import ru.magnat.sfs.ui.android.GenericEntityView;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public final class DocRequestOutletView
		extends
		GenericEntityView<DocRequestOutletJournal, DocRequestOutletEntity>
		implements OnItemClickListener, OnAddressResolvedListener {

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
	private NumberPicker _numberPicker;
	final int _textHeight = 300; // высота текстового поля SDV 2012-09-12
	final int _textWidth = 350; // ширина текстового поля SDV 2012-09-12

	
	final int count_bank_day = 30;
	static final int TIME_DIALOG_ID = 0;
	static final int DATE_DIALOG_ID = 1;
	private static final int DIALOG_THEME = android.R.style.Theme_Holo_Light_Panel;
	final Boolean isReadOnly;
	public DocRequestOutletView(Context context,
			DocRequestOutletJournal journal,
			DocRequestOutletEntity entity) {
		super(context, journal, entity);
		_canSelectOutlet = (entity.Outlet == null);
		isReadOnly = entity.getReadOnly();
		if (!isReadOnly) {	
			entity.IsAccepted = false;
			entity.save();
		}
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
		
		if (!isReadOnly && (_entity.City==null || _entity.City.trim().length()==0) && Globals.getLastLocation()!=null){
			AddressResolver addressresolver = new AddressResolver(this);
			addressresolver.execute(Globals.getLastLocation());
		}
		return this;

	}

	@SuppressWarnings("unused")
	private void changeIsMark() {
		if (isReadOnly) {
			Dialogs.createDialog("SFS",
					"Нельзя исправлять отправленный запрос", Command.NO_OP,null,null,DIALOG_THEME)
					.show();
			return;
		}
		_entity.setMarked(!_entity.IsMark);
		_adapter.notifyDataSetChanged();
	}

	// Торговая точка SDV 2012-09-11
	protected void changeOutlet() {
		if (isReadOnly) {
			Dialogs.createDialog("SFS",
					"Нельзя исправлять отправленный запрос", Command.NO_OP,null,null,DIALOG_THEME)
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
							//_entity.setContent(_entity.Outlet);
						}
					},DIALOG_THEME);
			_alertDialog.show();
		}
	}

	// Наименование
	protected void changeDescr() {
		if (isReadOnly) {
			Dialogs.createDialog("SFS",
					"Нельзя исправлять отправленный запрос", Command.NO_OP,null,null,DIALOG_THEME)
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
				}, Command.NO_OP,DIALOG_THEME).show();
	}

	
		protected void changeChainSize() {
			if (isReadOnly) {
				Dialogs.createDialog("SFS",
						"Нельзя исправлять отправленный запрос", Command.NO_OP,null,null,DIALOG_THEME)
						.show();
				return;
			}
			new NumberPickerDialog(
					getContext()
					,DIALOG_THEME
					,new OnNumberSetListener(){
						@Override
						public void onNumberSet(NumberPicker view, int value) {
							_entity.ChainSize = value;
							_adapter.notifyDataSetChanged();
							
						}
					}
					,_entity.ChainSize
					,1
					,1000
					,"Отмена"
					,"Ok"
					,"Количество магазинов в сети").show();
			
			 _numberPicker = new NumberPicker(getContext());
		}

		protected void changeLcbShare() {
			if (isReadOnly) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP, null, null, DIALOG_THEME).show();
				return;
			}
			
			String[] values = new String[]{"Нет", "Да"};
			new NumberPickerDialog(getContext(), DIALOG_THEME, new OnNumberSetListener() {
				
				@Override
				public void onNumberSet(NumberPicker view, int value) {
					_entity.LcbShare = value;
					_adapter.notifyDataSetChanged();
					
				}
				
			}, _entity.LcbShare, values, "Отмена", "Ok", "Наличие бытовой химии").show();
		}
		
		private class Test1 extends SfsEnum {
			public Test1(String[] objects, int selectedId) {
				super(objects, selectedId);
			}
		}
		
		// Адрес
		protected void changeCurrentSupplier() {
			if (isReadOnly) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP,null,null,DIALOG_THEME).show();
				return;
			}
			_editText = new EditText(getContext());
			// инициализирую поле комментов
			
			_editText.setHeight(_textHeight);
			_editText.setMaxHeight(_textHeight);
			_editText.setWidth(_textWidth);
			_editText.setMaxWidth(_textWidth);
			_editText.setGravity(0);


			_editText.setText(_entity.CurrentSupplier);
			Dialogs.createDialog("Текущий поставщик", "", _editText, null, new Command() {
				public void execute() {
					_entity.CurrentSupplier = _editText.getText().toString();
					_adapter.notifyDataSetChanged();
				}
			}, Command.NO_OP,DIALOG_THEME).show();
		}
		
	// Адрес
	protected void changeAddress() {
		if (isReadOnly) {
			Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP,null,null,DIALOG_THEME).show();
			return;
		}
		_editText = new EditText(getContext());
		// инициализирую поле комментов
		
		_editText.setHeight(_textHeight);
		_editText.setMaxHeight(_textHeight);
		_editText.setWidth(_textWidth);
		_editText.setMaxWidth(_textWidth);
		_editText.setGravity(0);


		_editText.setText(_entity.Address);
		Dialogs.createDialog("Адрес", "", _editText, null, new Command() {
			public void execute() {
				_entity.Address = _editText.getText().toString();
				_adapter.notifyDataSetChanged();
			}
		}, Command.NO_OP,DIALOG_THEME).show();
	}
	
	protected void changeLegalAddress() {
		if (isReadOnly) {
			Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP,null,null,DIALOG_THEME).show();
			return;
		}
		_editText = new EditText(getContext());
		// инициализирую поле комментов
		
		_editText.setHeight(_textHeight);
		_editText.setMaxHeight(_textHeight);
		_editText.setWidth(_textWidth);
		_editText.setMaxWidth(_textWidth);
		_editText.setGravity(0);


		_editText.setText(_entity.LegalAddress);
		Dialogs.createDialog("Юридическое лицо", "", _editText, null, new Command() {
			public void execute() {
				_entity.LegalAddress = _editText.getText().toString();
				_adapter.notifyDataSetChanged();
			}
		}, Command.NO_OP,DIALOG_THEME).show();
	}
	protected void changeKeyPerson() {
		if (isReadOnly) {
			Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP,null,null,DIALOG_THEME).show();
			return;
		}
		_editText = new EditText(getContext());
		// инициализирую поле комментов
		
		_editText.setHeight(_textHeight);
		_editText.setMaxHeight(_textHeight);
		_editText.setWidth(_textWidth);
		_editText.setMaxWidth(_textWidth);
		_editText.setGravity(0);


		_editText.setText(_entity.KeyPerson);
		Dialogs.createDialog("Ключевое лицо", "", _editText, null, new Command() {
			public void execute() {
				_entity.KeyPerson = _editText.getText().toString();
				_adapter.notifyDataSetChanged();
			}
		}, Command.NO_OP,DIALOG_THEME).show();
	}
	
	protected void changeCity() {
		if (isReadOnly) {
			Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP,null,null,DIALOG_THEME).show();
			return;
		}
		_editText = new EditText(getContext());
		// инициализирую поле комментов
		
		_editText.setHeight(_textHeight);
		_editText.setMaxHeight(_textHeight);
		_editText.setWidth(_textWidth);
		_editText.setMaxWidth(_textWidth);
		_editText.setGravity(0);


		_editText.setText(_entity.City);
		Dialogs.createDialog("Город", "", _editText, null, new Command() {
			public void execute() {
				_entity.City = _editText.getText().toString();
				_adapter.notifyDataSetChanged();
			}
		}, Command.NO_OP,DIALOG_THEME).show();
	}
	
	// Центральный офис
	protected void changeHQ() {
		if (isReadOnly) {
			Dialogs.createDialog("SFS",
					"Нельзя исправлять отправленный запрос", Command.NO_OP,null,null,DIALOG_THEME)
					.show();
			return;
		}
		_entity.IsHQ = (_entity.IsHQ == null) ? true : !_entity.IsHQ;
		_adapter.notifyDataSetChanged();
	}

	// Распределительный центр
	protected void changeDC() {
		if (isReadOnly) {
			Dialogs.createDialog("SFS",
					"Нельзя исправлять отправленный запрос", Command.NO_OP,null,null,DIALOG_THEME)
					.show();
			return;
		}
		_entity.IsDC = (_entity.IsDC == null) ? true : !_entity.IsDC;
		_adapter.notifyDataSetChanged();
	}

	// Магазин
	protected void changeShop() {
		if (isReadOnly) {
			Dialogs.createDialog("SFS",
					"Нельзя исправлять отправленный запрос", Command.NO_OP,null,null,DIALOG_THEME)
					.show();
			return;
		}
		_entity.IsShop = (_entity.IsShop == null) ? true : !_entity.IsShop;
		_adapter.notifyDataSetChanged();
	}

	// Точка принятия заказа
	protected void changeOP() {
		if (isReadOnly) {
			Dialogs.createDialog("SFS",
					"Нельзя исправлять отправленный запрос", Command.NO_OP,null,null,DIALOG_THEME)
					.show();
			return;
		}
		_entity.IsOP = (_entity.IsOP == null) ? true : !_entity.IsOP;
		_adapter.notifyDataSetChanged();
	}

	// Канал
	protected void changeSizeVariant() {
		if (isReadOnly) {
			Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP,null,null,DIALOG_THEME).show();
			return;
		}
		if (_alertDialog != null) {
			_alertDialog.cancel();
		}
		
		OutletSizeVariant variant = _entity.SizeVariant;
		if (variant==null) variant = new OutletSizeVariant(0);
		_alertDialog = Dialogs.createSelectFromListDialog(variant,
				"Размер магазина", new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						_entity.SizeVariant = new OutletSizeVariant(which + 1);
						_adapter.notifyDataSetChanged();
					}
				},DIALOG_THEME);
		_alertDialog.show();
	}

	// Канал
		protected void changeChannel() {
			if (isReadOnly) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP,null,null,DIALOG_THEME).show();
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
							_entity.Channel = _refStoreChannel.getItem(which);
							_adapter.notifyDataSetChanged();
						}
					},DIALOG_THEME);
			_alertDialog.setOnDismissListener(new CatSelectDialogDismissListener(_refStoreChannel));
			_alertDialog.show();
		}

	// Тип обслуживания
	protected void changeServiceType() {
		if (isReadOnly) {
			Dialogs.createDialog("SFS",
					"Нельзя исправлять отправленный запрос", Command.NO_OP,null,null,DIALOG_THEME)
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
				},DIALOG_THEME);
		_alertDialog.setOnDismissListener(new CatSelectDialogDismissListener(_refServiceType));
		_alertDialog.show();
	}
	class CatSelectDialogDismissListener implements OnDismissListener{
		final OrmObject<?> _cat;
		public CatSelectDialogDismissListener(OrmObject<?> cat){
			_cat = cat;
		}
		@Override
		public void onDismiss(DialogInterface dialog) {
			if (_cat!=null) _cat.close();
			
		}
	}
	// Тип точки
	protected void changeStoreType() {
		if (isReadOnly) {
			Dialogs.createDialog("SFS",
					"Нельзя исправлять отправленный запрос", Command.NO_OP,null,null,DIALOG_THEME)
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
				},DIALOG_THEME);
		_alertDialog.setOnDismissListener(new CatSelectDialogDismissListener(_refStoreType));
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
		if (this.isReadOnly) return true;
		
		Dialogs.createDialog("", "Загрузить данные о точке в 1С?", new Command(){

			@Override
			public void execute() {
				if (_entity.checkData()) {
					_entity.IsMark = false;
					saveAndExit();
				}
				else {
					Dialogs.createDialog("", "Заполнены не все поля документа. Продолжить выход?", new Command(){

						@Override
						public void execute() {
							_entity.IsMark = true;
							saveAndExit();
						}
						
					},Command.NO_OP).show();
				}
				
			}
			
		}, new Command(){

			@Override
			public void execute() {
				_entity.IsMark = true;
				saveAndExit();
				
			}
			
		}, Command.NO_OP, 0).show();
		
		return false;

	}

	protected void saveAndExit() {
		this._catalog.save();
		super.closeView();
	}

	@Override
	public void onAddressResolved(Address address) {
		if (address!=null){
			_entity.City = address.getLocality();
			_adapter.notifyDataSetChanged();
		}
		
	}
	
}
