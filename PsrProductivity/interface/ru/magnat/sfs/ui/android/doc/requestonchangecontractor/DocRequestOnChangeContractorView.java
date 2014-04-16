package ru.magnat.sfs.ui.android.doc.requestonchangecontractor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import ru.magnat.sfs.android.Command;
import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.ref.contractor.RefContractor;
import ru.magnat.sfs.bom.ref.contractor.RefContractorEntity;
import ru.magnat.sfs.bom.ref.generic.RefGeneric.HierarchyMode;
import ru.magnat.sfs.bom.requestonchangecontractor.DocRequestOnChangeContractorEntity;
import ru.magnat.sfs.bom.requestonchangecontractor.DocRequestOnChangeContractorJournal;
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
//import ru.magnat.sfs.bom.doc.requestonchangeroute.DocRequestOnChangeOutletEntity;

public class DocRequestOnChangeContractorView extends GenericEntityView<DocRequestOnChangeContractorJournal, DocRequestOnChangeContractorEntity> implements OnItemClickListener {

	AlertDialog _alertDialog;
	TaskVisitEntity _visit;
	OnClickListener radioListener;
	ExpandableListAdapter mAdapter;

	SimpleAdapter _adapter;

	private EditText _editText; // текстовое поле SDV 2012-09-05
	final int _textHeight = 300; // высота текстового поля SDV 2012-09-12
	final int _textWidth = 350; // ширина текстового поля SDV 2012-09-12

	ArrayAdapter<String> str_arr_adapter; // Создю адаптер списочка значений
	final int count_bank_day = 30;
	static final int TIME_DIALOG_ID = 0;
	static final int DATE_DIALOG_ID = 1;

	// SDV 2012-09-11
	private RefContractor _refContractor;
	final boolean _canSelectContractor;

	public DocRequestOnChangeContractorView(Context context,
			DocRequestOnChangeContractorJournal journal,
			DocRequestOnChangeContractorEntity entity) {
		super(context, journal, entity);

		_canSelectContractor = (entity.Contractor == null);

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

	// SDV 2012-09-11
	protected void сhangeContractor() {
		if (_entity.getReadOnly()) {
			Dialogs.createDialog("SFS",
					"Нельзя исправлять отправленный запрос", Command.NO_OP)
					.show();
			return;
		}
		if (_canSelectContractor) {
			_refContractor = (RefContractor) Globals
					.createOrmObject(RefContractor.class);

			_refContractor.Select(false, false, HierarchyMode.OnlyEntity);
			_alertDialog = Dialogs.createSelectFromListDialog(_refContractor,
					"Выбор контрагента",
					new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							_entity.Contractor = (RefContractorEntity) _refContractor
									.getItem(which);
							_adapter.notifyDataSetChanged();
							updateCaption();
							_entity.setContent(_entity.Contractor);
						}
					});
			_alertDialog.show();
		}
	}

	protected void updateCaption() {
		((TextView) findViewById(R.id.caption)).setText(_entity.toString());
	}

	protected void changeAddress() // Адрес
	{
		if (_entity.getReadOnly()) {
			Dialogs.createDialog("SFS",
					"Нельзя исправлять отправленный запрос", Command.NO_OP)
					.show();
			return;
		}
		_editText = new EditText(getContext());
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
		}, Command.NO_OP).show();
	}

	protected void changeDeliveryAddress() // Адрес доставки
	{
		if (_entity.getReadOnly()) {
			Dialogs.createDialog("SFS",
					"Нельзя исправлять отправленный запрос", Command.NO_OP)
					.show();
			return;
		}
		_editText = new EditText(getContext());
		_editText.setHeight(_textHeight);
		_editText.setMaxHeight(_textHeight);
		_editText.setWidth(_textWidth);
		_editText.setMaxWidth(_textWidth);
		_editText.setGravity(0);

		_editText.setText(_entity.DeliveryAddress);
		Dialogs.createDialog("Адрес доставки", "", _editText, null,
				new Command() {
					public void execute() {

						_entity.DeliveryAddress = _editText.getText()
								.toString();
						_adapter.notifyDataSetChanged();

					}
				}, Command.NO_OP).show();

	}

	protected void changeBic() {
		if (_entity.getReadOnly()) {
			Dialogs.createDialog("SFS",
					"Нельзя исправлять отправленный запрос", Command.NO_OP)
					.show();
			return;
		}
		_editText = new EditText(getContext());
		_editText.setHeight(_textHeight);
		_editText.setMaxHeight(_textHeight);
		_editText.setWidth(_textWidth);
		_editText.setMaxWidth(_textWidth);
		_editText.setGravity(0);

		_editText.setText(_entity.Bic);
		Dialogs.createDialog("БИК", "", _editText, null, new Command() {
			public void execute() {

				_entity.Bic = _editText.getText().toString();
				_adapter.notifyDataSetChanged();

			}
		}, Command.NO_OP).show();
	}

	protected void changeINN_KPP() {
		if (_entity.getReadOnly()) {
			Dialogs.createDialog("SFS",
					"Нельзя исправлять отправленный запрос", Command.NO_OP)
					.show();
			return;
		}
		_editText = new EditText(getContext());
		_editText.setHeight(_textHeight);
		_editText.setMaxHeight(_textHeight);
		_editText.setWidth(_textWidth);
		_editText.setMaxWidth(_textWidth);
		_editText.setGravity(0);

		_editText.setText(_entity.INN_KPP);
		Dialogs.createDialog("ИНН/КПП", "", _editText, null, new Command() {
			public void execute() {

				_entity.INN_KPP = _editText.getText().toString();
				_adapter.notifyDataSetChanged();

			}
		}, Command.NO_OP).show();
	}

	protected void changeName() {
		if (_entity.getReadOnly()) {
			Dialogs.createDialog("SFS",
					"Нельзя исправлять отправленный запрос", Command.NO_OP)
					.show();
			return;
		}
		_editText = new EditText(getContext());
		_editText.setHeight(_textHeight);
		_editText.setMaxHeight(_textHeight);
		_editText.setWidth(_textWidth);
		_editText.setMaxWidth(_textWidth);
		_editText.setGravity(0);

		_editText.setText(_entity.Name);
		Dialogs.createDialog("Наименование", "", _editText, null,
				new Command() {
					public void execute() {

						_entity.Name = _editText.getText().toString();
						_adapter.notifyDataSetChanged();

					}
				}, Command.NO_OP).show();
	}

	protected void changeNumberAccount() {
		if (_entity.getReadOnly()) {
			Dialogs.createDialog("SFS",
					"Нельзя исправлять отправленный запрос", Command.NO_OP)
					.show();
			return;
		}
		_editText = new EditText(getContext());
		_editText.setHeight(_textHeight);
		_editText.setMaxHeight(_textHeight);
		_editText.setWidth(_textWidth);
		_editText.setMaxWidth(_textWidth);
		_editText.setGravity(0);

		_editText.setText(_entity.NumberAccount);
		Dialogs.createDialog("Номер счета", "", _editText, null, new Command() {
			public void execute() {

				_entity.NumberAccount = _editText.getText().toString();
				_adapter.notifyDataSetChanged();

			}
		}, Command.NO_OP).show();
	}

	protected void changeOKPO() {
		if (_entity.getReadOnly()) {
			Dialogs.createDialog("SFS",
					"Нельзя исправлять отправленный запрос", Command.NO_OP)
					.show();
			return;
		}
		_editText = new EditText(getContext());
		_editText.setHeight(_textHeight);
		_editText.setMaxHeight(_textHeight);
		_editText.setWidth(_textWidth);
		_editText.setMaxWidth(_textWidth);
		_editText.setGravity(0);

		_editText.setText(_entity.OKPO);
		Dialogs.createDialog("ОКПО", "", _editText, null, new Command() {
			public void execute() {

				_entity.OKPO = _editText.getText().toString();
				_adapter.notifyDataSetChanged();

			}
		}, Command.NO_OP).show();
	}

	protected void changePassportData() {
		if (_entity.getReadOnly()) {
			Dialogs.createDialog("SFS",
					"Нельзя исправлять отправленный запрос", Command.NO_OP)
					.show();
			return;
		}
		_editText = new EditText(getContext());
		_editText.setHeight(_textHeight);
		_editText.setMaxHeight(_textHeight);
		_editText.setWidth(_textWidth);
		_editText.setMaxWidth(_textWidth);
		_editText.setGravity(0);

		_editText.setText(_entity.PassportData);
		Dialogs.createDialog("Паспортные данные", "", _editText, null,
				new Command() {
					public void execute() {

						_entity.PassportData = _editText.getText().toString();
						_adapter.notifyDataSetChanged();

					}
				}, Command.NO_OP).show();
	}

	protected void changeFEcertificate() {
		if (_entity.getReadOnly()) {
			Dialogs.createDialog("SFS",
					"Нельзя исправлять отправленный запрос", Command.NO_OP)
					.show();
			return;
		}
		_editText = new EditText(getContext());
		_editText.setHeight(_textHeight);
		_editText.setMaxHeight(_textHeight);
		_editText.setWidth(_textWidth);
		_editText.setMaxWidth(_textWidth);
		_editText.setGravity(0);

		_editText.setText(_entity.FEcertificate);
		Dialogs.createDialog("Свидетельство ИП", "", _editText, null,
				new Command() {
					public void execute() {

						_entity.FEcertificate = _editText.getText().toString();
						_adapter.notifyDataSetChanged();

					}
				}, Command.NO_OP).show();
	}

	protected void changePhone() {
		if (_entity.getReadOnly()) {
			Dialogs.createDialog("SFS",
					"Нельзя исправлять отправленный запрос", Command.NO_OP)
					.show();
			return;
		}
		_editText = new EditText(getContext());
		_editText.setHeight(_textHeight);
		_editText.setMaxHeight(_textHeight);
		_editText.setWidth(_textWidth);
		_editText.setMaxWidth(_textWidth);
		_editText.setGravity(0);

		_editText.setText(_entity.Phone);
		Dialogs.createDialog("Телефон", "", _editText, null, new Command() {
			public void execute() {

				_entity.Phone = _editText.getText().toString();
				_adapter.notifyDataSetChanged();

			}
		}, Command.NO_OP).show();
	}

	protected void changeEmail() {
		if (_entity.getReadOnly()) {
			Dialogs.createDialog("SFS",
					"Нельзя исправлять отправленный запрос", Command.NO_OP)
					.show();
			return;
		}
		_editText = new EditText(getContext());
		_editText.setHeight(_textHeight);
		_editText.setMaxHeight(_textHeight);
		_editText.setWidth(_textWidth);
		_editText.setMaxWidth(_textWidth);
		_editText.setGravity(0);

		_editText.setText(_entity.Email);
		Dialogs.createDialog("Эл. почта", "", _editText, null, new Command() {
			public void execute() {

				_entity.Email = _editText.getText().toString();
				_adapter.notifyDataSetChanged();

			}
		}, Command.NO_OP).show();
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
