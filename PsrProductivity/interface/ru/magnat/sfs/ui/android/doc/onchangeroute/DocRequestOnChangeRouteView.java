package ru.magnat.sfs.ui.android.doc.onchangeroute;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import ru.magnat.sfs.android.Command;
import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.R;
//import ru.magnat.sfs.bom.doc.requestonchangeroute.DocRequestOnChangeOutletEntity;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.ref.generic.RefGeneric.HierarchyMode;
import ru.magnat.sfs.bom.ref.outlet.RefOutlet;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.requestonchangeroute.DocRequestOnChangeRouteEntity;
import ru.magnat.sfs.bom.requestonchangeroute.DocRequestOnChangeRouteJournal;
import ru.magnat.sfs.bom.task.visit.TaskVisitEntity;
import ru.magnat.sfs.ui.android.GenericEntityView;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioButton;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

public class DocRequestOnChangeRouteView
		extends
		GenericEntityView<DocRequestOnChangeRouteJournal, DocRequestOnChangeRouteEntity>
		implements OnItemClickListener {

	AlertDialog _alertDialog;
	// DocRequestOnChangeRouteEntity _entity;
	TaskVisitEntity _visit;
	OnClickListener radioListener;

	// DocRequestOnChangeRouteJournal _catalog;
	ExpandableListAdapter mAdapter;

	SimpleAdapter _adapter;

	// SDV 2012-09-11
	private RefOutlet _refOutlet;
	final boolean _canSelectOutlet;

	private RadioGroup _radioGroup; // Группа переключателей SDV 2012-09-06
	private RadioButton RB_Order, RB_Financial, RB_Documents, RB_Shelf;

	private EditText _editText; // текстовое поле SDV 2012-09-05
	final int _textHeight = 300; // высота текстового поля SDV 2012-09-12
	final int _textWidth = 350; // ширина текстового поля SDV 2012-09-12

	private EditText _editNumber; // цифровое поле SDV 2012-09-10

	ArrayAdapter<String> str_arr_adapter; // Создю адаптер списочка значений
	final int count_bank_day = 30;
	static final int TIME_DIALOG_ID = 0;
	static final int DATE_DIALOG_ID = 1;
	private int mYear;
	private int mMonth;
	private int mDay;
	private int mHour;
	private int mMinute;

	public DocRequestOnChangeRouteView(Context context,
			DocRequestOnChangeRouteJournal journal,
			DocRequestOnChangeRouteEntity entity) {
		super(context, journal, entity);
		// this._entity = entity;
		// this._catalog = journal;
		// this._visit = visit;
		_canSelectOutlet = (entity.Outlet == null);

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

	protected void updateCaption() {
		if (_entity.Outlet == null) {
			((TextView) findViewById(R.id.caption)).setText(_entity.toString()
					+ " ТОРГОВАЯ ТОЧКА НЕ ВЫБРАНА!");
		} else
			((TextView) findViewById(R.id.caption)).setText(_entity.toString());
	}

	// Торговая точка Added Selyanin D.V. 2012-09-11
	protected void changeOutlet() {
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

	// Регулярность посещений (дни)
	protected void changeVisitCyclicity() {
		_editNumber = new EditText(getContext());
		_editNumber.setInputType(InputType.TYPE_CLASS_NUMBER); // настройки для
																// EditNumbera
																// только числа
		Dialogs.createDialog("Регулярность посещений (дни)", "", _editNumber,
				null, new Command() {
					public void execute() {
						_entity.VisitCyclicity = Integer.parseInt(_editNumber
								.getText().toString());
						_adapter.notifyDataSetChanged();
					}
				}, Command.NO_OP).show();
	}

	protected void changeVisitDate() {
		final Calendar c = Calendar.getInstance();
		Date date = (_entity.VisitDate == null) ? new Date()
				: _entity.VisitDate;
		c.setTime(date);

		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		new DatePickerDialog(getContext(), new OnDateSetListener() {

			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				Calendar c = Calendar.getInstance();
				c.set(year, monthOfYear, dayOfMonth);

				_entity.VisitDate = c.getTime();
				_adapter.notifyDataSetChanged();

			}
		}, mYear, mMonth, mDay).show();

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

	// Время визита
	protected void changeVisitTime() {
		if (_entity.getReadOnly()) {
			Dialogs.createDialog("SFS",
					"Нельзя исправлять отправленный запрос", Command.NO_OP)
					.show();
			return;
		}
		// получаем текущее время
		final Calendar c = Calendar.getInstance();
		Date date = (_entity.VisitTime == null) ? new Date()
				: _entity.VisitTime;

		c.setTime(date);
		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMinute = c.get(Calendar.MINUTE);

		new TimePickerDialog(getContext(), new OnTimeSetListener() {

			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				Calendar c = Calendar.getInstance();
				c.set(0, 0, 0, hourOfDay, minute);

				_entity.VisitTime = c.getTime();
				_adapter.notifyDataSetChanged();

			}
		}, mHour, mMinute, true).show();
	}

	// Вид визита
	protected void changeVisitKind() {
		if (_entity.getReadOnly()) {
			Dialogs.createDialog("SFS",
					"Нельзя исправлять отправленный запрос", Command.NO_OP)
					.show();
			return;
		}
		_radioGroup = new RadioGroup(getContext());

		RB_Order = new RadioButton(_radioGroup.getContext());
		RB_Order.setText("Заказ");
		RB_Order.setOnClickListener(radioListener);
		_radioGroup.addView(RB_Order);

		RB_Financial = new RadioButton(_radioGroup.getContext());
		RB_Financial.setText("Финансовые вопросы");
		RB_Financial.setOnClickListener(radioListener);
		_radioGroup.addView(RB_Financial);

		RB_Documents = new RadioButton(_radioGroup.getContext());
		RB_Documents.setText("Работа с документами");
		RB_Documents.setOnClickListener(radioListener);
		_radioGroup.addView(RB_Documents);

		RB_Shelf = new RadioButton(_radioGroup.getContext());
		RB_Shelf.setText("Работа с полкой");
		RB_Shelf.setOnClickListener(radioListener);
		_radioGroup.addView(RB_Shelf);

		switch (_entity.VisitKindId) {
		case 0:
			RB_Order.setChecked(true);
			break;
		case 1:
			RB_Financial.setChecked(true);
			break;
		case 2:
			RB_Documents.setChecked(true);
			break;
		case 3:
			RB_Shelf.setChecked(true);
			break;

		default:
			RB_Documents.setChecked(true);
			break;
		}

		Dialogs.createDialog("Вид визита", "", _radioGroup, null,
				new Command() {
					int user_choice;
					String user_choice_str;

					public void execute() {
						if (RB_Order.isChecked()) {
							user_choice = 0;
							user_choice_str = RB_Order.getText().toString();
						} else if (RB_Financial.isChecked()) {
							user_choice = 1;
							user_choice_str = RB_Financial.getText().toString();
						} else if (RB_Documents.isChecked()) {
							user_choice = 2;
							user_choice_str = RB_Documents.getText().toString();
						} else if (RB_Shelf.isChecked()) {
							user_choice = 3;
							user_choice_str = RB_Shelf.getText().toString();
						}
						// else {}

						_entity.VisitKindId = user_choice;// _editText.getText().toString();
						_entity.VisitKind = user_choice_str;
						_adapter.notifyDataSetChanged();
					}

				}, Command.NO_OP).show();
	}

	// Цель визита
	protected void changeVisitTarget() {
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

		_editText.setText(_entity.VisitTarget);
		Dialogs.createDialog("Цель визита", "", _editText, null, new Command() {
			public void execute() {

				_entity.VisitTarget = _editText.getText().toString();
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
