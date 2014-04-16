package ru.magnat.sfs.ui.android.doc.order;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashMap;

import ru.magnat.sfs.android.Command;
import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.order.DocOrderEntity;
import ru.magnat.sfs.bom.order.DocOrderJournal;
import ru.magnat.sfs.bom.order.OnOrderChangedListener.OnAmountChangedListener;
import ru.magnat.sfs.bom.ref.contractor.RefContractor;
import ru.magnat.sfs.bom.ref.contractor.RefContractorEntity;
import ru.magnat.sfs.bom.ref.generic.RefGeneric.HierarchyMode;
import ru.magnat.sfs.bom.ref.outlet.RefOutlet;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.ref.traderule.RefTradeRuleEntity;
import ru.magnat.sfs.bom.ref.warehouse.RefWarehouse;
import ru.magnat.sfs.bom.ref.warehouse.RefWarehouseEntity;
import ru.magnat.sfs.bom.reg.paymenttype.RegPaymentType;
import ru.magnat.sfs.bom.reg.paymenttype.RegPaymentTypeEntity;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class DocOrderHeaderTab extends SfsContentView implements OnItemClickListener {

	DocOrderEntity _entity;
	DocOrderJournal _catalog;

	AlertDialog _alertDialog;

	public DocOrderHeaderTab(Context context) {
		super(context);
	}

	public DocOrderHeaderTab(Context context, DocOrderJournal catalog, DocOrderEntity entity) {
		super(context);
		
		_entity = entity;
		_catalog = catalog;
	}

	SfsContentView shipmentDateView;
	SimpleAdapter _adapter;
	private RefOutlet _refOutlet;
	private RegPaymentType _regPaymentType;
	private ArrayAdapter<RefTradeRuleEntity> _rules;
	private RefContractor _refContractor;
	private RefWarehouse _refWarehouse;
	private EditText _editText;

	public void refreshFields() {

	}

	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);

		refresh();
	}

	@Override
	public SfsContentView inflate() {
		layoutInflater.inflate(R.layout.doc_order_header_tab2, this);
		if (_entity == null) {
			return null;
		}
		_adapter = new SimpleAdapter(getContext(), _entity.getProperties(), android.R.layout.simple_list_item_2, new String[] { "value", "property" }, new int[] { android.R.id.text1, android.R.id.text2 });
		ListView lv = (ListView) findViewById(R.id.list);
		lv.setAdapter(_adapter);
		lv.setOnItemClickListener(this);
		((TextView) findViewById(R.id.caption)).setText(_entity.toString());
		_entity.addOnAmountChangedListener(new OnAmountChangedListener(){

			@SuppressWarnings("rawtypes")
			@Override
			public void onAmountChanged(GenericEntity sender, float old_value,
					float new_value) {
				_adapter.notifyDataSetChanged();
				
			}
			
		});
		return this;
	}

	@Override
	public void refresh() {
		if (_entity!=null) 
			if (_entity.save())
				_adapter.notifyDataSetChanged();

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

	@SuppressWarnings("unused")
	private void refreshAmount() {
		refresh();
	}

	@SuppressWarnings("unused")
	private void changeShipmentDate() {
		if (_alertDialog != null) {
			_alertDialog.cancel();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(_entity.getShipmentDate());
		Context context = MainActivity.getInstance();
		//context.setTheme(android.R.style.Theme_Light_NoTitleBar_Fullscreen);
		_alertDialog = new DatePickerDialog(context,
				new DatePickerDialog.OnDateSetListener() {

					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						Calendar cal = Calendar.getInstance();
						cal.set(Calendar.YEAR, year);
						cal.set(Calendar.MONTH, monthOfYear);
						cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
						_entity.setShipmentDate(cal.getTime());
						_adapter.notifyDataSetChanged();
					}

				}, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH));
		_alertDialog.show();

	}

	DeliveryTimeChoiseDialogView _shipmentTimeView;

	@SuppressWarnings("unused")
	private void changeShipmentTime() {

		if (_alertDialog != null) {
			_alertDialog.cancel();
		}

		_shipmentTimeView = new DeliveryTimeChoiseDialogView(MainActivity
				.getInstance(),
				_entity.getShipmentTime());

		_alertDialog = Dialogs.createDialog("", "",
				_shipmentTimeView.Inflate(), null, new Command() {

					@Override
					public void execute() {

						if (_entity != null) {

							_shipmentTimeView.update();
							_entity.setShipmentTime(_shipmentTimeView
									.getResult());
							_adapter.notifyDataSetChanged();
						}
					}
				}, Command.NO_OP);
		_alertDialog.show();
	}
	@SuppressWarnings("unused")
	private void changeWelcomeOfferDiscount(){
		if (_entity.WelcomeOfferDiscount!=null && _entity.WelcomeOfferDiscount) {
			_entity.setWelcomeOfferDiscount(false);
			_adapter.notifyDataSetChanged();
		}
		else {
			if (_entity.Outlet.isGolden()){
				Dialogs.createDialog("", "Точка уже является золотой, Welcome скидка не может быть установлена", Command.NO_OP).show();
				return;
			}
			String submitted = Globals.getSubmittedWelcomeOffer(_entity.Outlet);
			if (submitted!=null){
				Dialogs.createDialog("", "Точке уже предоставлялась Welcome скидка: " + submitted, Command.NO_OP).show();
				return;
			}
			_entity.setWelcomeOfferDiscount(true);
			_adapter.notifyDataSetChanged();
		}
	}
	@SuppressWarnings("unused")
	private void changeTprDiscountDisabled(){
		if (_entity.TprDiscountDisabled!=null && _entity.TprDiscountDisabled) {
			_entity.setTprDiscountDisabled(false);
			_adapter.notifyDataSetChanged();
		}
		else {
			_entity.setTprDiscountDisabled(true);
			_adapter.notifyDataSetChanged();
		}
	}
	@SuppressWarnings("unused")
	private void changeOutlet() {
		if (_alertDialog != null) {
			_alertDialog.cancel();
		}
		// if (_refOutlet==null) _refOutlet = new RefOutlet(getContext());
		_refOutlet = (RefOutlet) Globals.createOrmObject(RefOutlet.class);

		_refOutlet.SetOwner(_entity.getOutlet().ParentExt);
		_refOutlet.Select(true, false, HierarchyMode.OnlyEntity);
		_alertDialog = Dialogs.createSelectFromListDialog(_refOutlet,
				"Выбор торговой точки",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						_entity.setOutlet((RefOutletEntity) _refOutlet
								.getItem(which));
						_adapter.notifyDataSetChanged();

					}
				});
		_alertDialog.show();

	}

	@SuppressWarnings("unused")
	private void changePaymentType() {
		if (_alertDialog != null) {
			_alertDialog.cancel();
		}
		_regPaymentType = (RegPaymentType) Globals.createOrmObject(RegPaymentType.class);
		GenericEntity<?> ds[] = { _entity.getContractor(), Globals.getEmployee(), null };

		_regPaymentType.Select(ds);
		_alertDialog = Dialogs.createSelectFromListDialog(_regPaymentType,
				"Выбор типа кредита",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						RegPaymentTypeEntity entity = (RegPaymentTypeEntity) _regPaymentType.getItem(which);
						if (entity != null) {
							_entity.setPaymentType(entity.PaymentType);
							_entity.setPayDelay(entity.Delay);
						}
						_adapter.notifyDataSetChanged();
					}
				});
		_alertDialog.show();
	}

	@SuppressWarnings("unused")
	private void changeIsMark() {
		if (_entity.getReadOnly()) {
			Dialogs.createDialog("SFS", "Нельзя исправлять отправленный заказ",
					Command.NO_OP).show();
			return;
		}
		_entity.setMarked(!_entity.IsMark);
		_adapter.notifyDataSetChanged();
	}

	@SuppressWarnings("unused")
	private void changeTradeRule() {
		if (_alertDialog != null) {
			_alertDialog.cancel();
		}
		
		RefOutletEntity outlet = _entity.getOutlet();
		if (outlet == null)
			return;
		_rules = Globals.getTradeRules(Globals.getEmployee(),outlet.ParentExt);
		_alertDialog = Dialogs.createSelectFromListDialog(_rules,
				"Выбор торговых условий",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						RefTradeRuleEntity entity = _rules.getItem(which);
						if (entity != null) {
							setTradeRule(entity);
						}
					}
				});	
		_alertDialog.show();
	}

	private void setTradeRule(RefTradeRuleEntity entity) {
		if (_entity.Outlet.IsNonPgNotAllowed && entity.IsNonPgRule) {
			Dialogs.createDialog("", "", Command.NO_OP);
		} else {
			_entity.setTradeRule(entity);
			_adapter.notifyDataSetChanged();
			
			// Сохранить торговые условия последний заказ по этой точке
			SharedPreferences settings = MainActivity.getInstance().getSharedPreferences(MainActivity.PREFS_NAME, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putLong(getResources().getString(R.string.options_order_last_used_trade_rule), entity.Id);
			editor.commit(); 
		}
	}
	
	@SuppressWarnings("unused")
	private void changeContractor() {
		if (_alertDialog != null) {
			_alertDialog.cancel();
		}
		_refContractor = (RefContractor) Globals.createOrmObject(RefContractor.class);
		_refContractor.SetOwner(_entity.getOutlet().ParentExt);
		_refContractor.Select(true, false, HierarchyMode.OnlyEntity);
		_alertDialog = Dialogs.createSelectFromListDialog(_refContractor,
				"Выбор контрагента",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						_entity.setContractor((RefContractorEntity) _refContractor
								.getItem(which));
						_adapter.notifyDataSetChanged();
					}
				});
		_alertDialog.show();
	}

	@SuppressWarnings("unused")
	private void changeWarehouse() {
		if (_alertDialog != null) {
			_alertDialog.cancel();
		}
		// if (_refContractor==null) _refContractor = new
		// RefContractor(getContext());
		_refWarehouse = (RefWarehouse) Globals
				.createOrmObject(RefWarehouse.class);
		_refWarehouse.Select(false, false, HierarchyMode.OnlyEntity);
		_alertDialog = Dialogs.createSelectFromListDialog(_refWarehouse,
				"Выбор склада",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						_entity.setWarehouse((RefWarehouseEntity) _refWarehouse
								.getItem(which));
						_adapter.notifyDataSetChanged();
					}
				});
		_alertDialog.show();

	}

	@SuppressWarnings("unused")
	private void changeDocComment() {
		if (_alertDialog != null) {
			_alertDialog.cancel();
		}
		// if (_refContractor==null) _refContractor = new
		// RefContractor(getContext());
		_editText = new EditText(getContext()); // если сделать final то при
												// втором вызове падает
		_editText.setMinLines(1);
		_editText.setMaxLines(5);
		_editText.setMinimumHeight(300);
		_editText.setGravity(Gravity.TOP);

		String comment = "";
		if (_entity.DocComment != null)
			comment = _entity.DocComment;
		_editText.setText(comment);
		_alertDialog = Dialogs.createDialog("", "", _editText, null,
				new Command() {

					public void execute() {
						_entity.DocComment = _editText.getText().toString();
						_adapter.notifyDataSetChanged();
						Utils.hideInput();

					}

				}, new Command() {
					public void execute() {
						Utils.hideInput();

					}
				});
		_alertDialog.show();

	}

	@Override
	public void moveTaskToBack(Boolean handled) {
	}

	public Boolean onBackPressed() {

		return null;
	}

}
