package ru.magnat.sfs.ui.android.doc.contract;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ru.magnat.sfs.android.Command;
import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.bom.EntityCardField;
import ru.magnat.sfs.bom.FieldMD;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.OrmObject;
import ru.magnat.sfs.bom.contract.DocContractEntity;
import ru.magnat.sfs.bom.contract.DocContractJournal;
import ru.magnat.sfs.bom.ref.generic.RefGeneric.HierarchyMode;
import ru.magnat.sfs.bom.ref.outletservicetype.RefServiceType;
import ru.magnat.sfs.bom.ref.outletservicetype.RefServiceTypeEntity;
import ru.magnat.sfs.bom.ref.storechannel.RefStoreChannel;
import ru.magnat.sfs.bom.ref.storechannel.RefStoreChannelEntity;
import ru.magnat.sfs.location.SfsLocationManager;
import ru.magnat.sfs.ui.android.GenericEntityView;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.util.StateRegistrationCertificate;
import ru.magnat.sfs.util.UniqueTaxPayerId;
import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class DocContractView extends GenericEntityView<DocContractJournal, DocContractEntity> implements OnItemClickListener { 
	
	private static final int DIALOG_THEME = android.R.style.Theme_Holo_Light_Panel;
	private SimpleAdapter mSimpleAdapter;
	private EditText mEditText;
	private RefStoreChannel mStoreChannel;
	private RefServiceType mServiceType;
	private AlertDialog mAlertDialog;
	private Calendar mCalendar = Calendar.getInstance(new Locale("ru", "RU"));
	private ListView mListView;
	private List<Map<String, Object>> mFieldData = new ArrayList<Map<String, Object>>();
	
	public DocContractView(Context context,	DocContractJournal journal, DocContractEntity entity) {
		super(context, journal, entity);
	}
	
	public class CatSelectDialogDismissListener implements OnDismissListener {
		final OrmObject<?> _cat;
		public CatSelectDialogDismissListener(OrmObject<?> cat){
			_cat = cat;
		}
		@Override
		public void onDismiss(DialogInterface dialog) {
			if (_cat != null) {
				_cat.close();
			}
		}
	}
	
	private void updateDataSet() {
		mFieldData.clear();
		Field[] fields = _entity.getClass().getFields();
		for (Field field : fields) {
			Annotation a = field.getAnnotation(EntityCardField.class);
			if (a instanceof EntityCardField) {
				EntityCardField oef = (EntityCardField) a;
				FieldMD fmd = new FieldMD();
				fmd.field = field;
				fmd.label = oef.DisplayName();
				fmd.sortkey = oef.Sortkey();
				fmd.selectMethod = oef.SelectMethod();
				fmd.formatString = oef.format();
				
				if (fmd.label.equals("время создания")) {
					continue;
				}
				if (fmd.label.equals("черновик")) {
					continue;
				}
				if (fmd.label.equals("Договор предан КА\n(Нажмите, когда подписанный договор будет передан контрагенту)")) {
					if (_entity.Approved.equals(new RequestStatusType(6))) {
						if (_entity.AgreementWasGivenToContractorDate == null) {
							_entity.AgreementWasGivenToContractor = false;
						} else {
							_entity.AgreementWasGivenToContractor = true;
						}
					} else if (_entity.Approved.equals(new RequestStatusType(7))) {
						if (_entity.AgreementWasGivenToContractorDate == null) {
							_entity.AgreementWasGivenToContractor = false;
						} else {
							_entity.AgreementWasGivenToContractor = true;
						}
					} else {
						continue;
					}
				}
				
				if (_entity.BusinessStructureType != null && _entity.BusinessStructureType.toString().equals("Физическое лицо")) {
					if (_entity.PaymentTerms != null && _entity.PaymentTerms.equals(new PaymentTerm(2))) {
						if (fmd.label.equals("ТП взял: выписку из ЕГРИП")) {
							continue;
						}
						if (fmd.label.equals("ТП взял: договор аренды/субаренды")) {
							continue;
						}
					}
					
					if (fmd.label.equals("ТП взял: протокол/решение о создании")) {
						continue;
					}
					if (fmd.label.equals("ТП взял: решение организации о назначении руководителя (обязательное при кредитовании)")) {
						continue;
					}
					if (fmd.label.equals("ТП взял: выписку из списка участников Общества")) {
						continue;
					}
					if (fmd.label.equals("ТП взял: копию Устава (обязательное при кредитовании)")) {
						continue;
					}
					if (fmd.label.equals("ТП взял: ОГРН (обязательное)")) {
						continue;
					}
					if (fmd.label.equals("ТП взял: доверенность на лицо подписывающее договор (обязательное при кредитовании)")) {
						continue;
					}
					if (fmd.label.equals("ТП взял: выписку из ЕГРЮЛ")) {
						continue;
					}
				} else if (_entity.BusinessStructureType != null && _entity.BusinessStructureType.toString().equals("Юридическое лицо")) {
					if (fmd.label.equals("Паспорт-серия (обязательное для ИП)")) {
						continue;
					}
					if (fmd.label.equals("Паспорт-номер (обязательное для ИП)")) {
						continue;
					}
					if (fmd.label.equals("Паспорт-кем выдан (обязательное для ИП)")) {
						continue;
					}
					if (fmd.label.equals("Паспорт-когда выдан (обязательное для ИП)")) {
						continue;
					}
					if (fmd.label.equals("ТП взял: копию паспорта ИП (обязательное при кредитовании)")) {
						continue;
					}
					if (fmd.label.equals("ТП взял: копию ОГРНИП (обязательное)")) {
						continue;
					}
					if (fmd.label.equals("ТП взял: выписку из ЕГРИП")) {
						continue;
					}
					if (fmd.label.equals("ТП взял: выписку из единого государственного реестра индивидуальных предпринимателей")) {
						continue;
					}
				}
			
				Object o;
				try {
					o = field.get(_entity);
					String value = "";
					if (o != null) {
						if (o instanceof Date) {
							String format = oef.format();
							SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
							value = formatter.format((Date) o);
						} else if (o instanceof Boolean) {
							value = ((Boolean) o) ? "Да" : "Нет";
						} else if (o instanceof Float) {
							value = Globals.customFormat(oef.format(), (Float) o);
						} else {
							value = o.toString();
						}
					}

					Map<String, Object> item = new HashMap<String, Object>();
					item.put("value", value);
					item.put("property", fmd.label);
					item.put("selectMethod", fmd.selectMethod);
					item.put("sortKey", fmd.sortkey);
					
					mFieldData.add(item);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		Collections.sort(mFieldData, new FieldsCompare());
	}
	
	@Override
	public SfsContentView inflate() {
		MainActivity.getInstance().enableSync(false);
		
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.doc_credit_request_tab, this);

		View controlPanel = (View) findViewById(R.id.control_bar);
		controlPanel.setVisibility(View.GONE);

		updateDataSet();
		
		mListView = (ListView) findViewById(R.id.list);
		mSimpleAdapter = new SimpleAdapter(getContext(), mFieldData, android.R.layout.simple_list_item_2, new String[] { "value", "property" }, new int[] { android.R.id.text1, android.R.id.text2 });
		mListView.setAdapter(mSimpleAdapter);
		mListView.setOnItemClickListener(DocContractView.this);
		
		if (_entity.BusinessStructureType == null) {
			setBusinessStructureType();
		}
		
		return this;
	}
	
	private void setBusinessStructureType() {
		if (_entity.getReadOnly()) {
			Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
			return;
		}
		if (mAlertDialog != null) {
			mAlertDialog.cancel();
		}
		
		mAlertDialog = Dialogs.createSelectFromListDialog(
			new BusinessStructure(0), 
			"Организационно-правовая форма (обязательное)", 
			new android.content.DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					_entity.BusinessStructureType = new BusinessStructure(which + 1);
					updateDataSet();
					mSimpleAdapter.notifyDataSetChanged();
				}
			},
			DIALOG_THEME);
		mAlertDialog.show();
	}
	
	@SuppressWarnings("unused")
	private void setAgreementWasGivenToContractor() {
		if (_entity.Approved.equals(new RequestStatusType(6))) {
			_entity.AgreementWasGivenToContractor = (!_entity.AgreementWasGivenToContractor);
			if (_entity.AgreementWasGivenToContractor) {
				_entity.AgreementWasGivenToContractorDate = new Date();
			} else {
				_entity.AgreementWasGivenToContractorDate = null;
			}
			
			_catalog.save(true);
		} else {
			if (_entity.Approved.equals(new RequestStatusType(7))) {
				Dialogs.createDialog("SFS", "Договор уже передан КА", Command.NO_OP).show();
			} else {
				Dialogs.createDialog("SFS", "Нельзя передать незарегистрированный договор!", Command.NO_OP).show();
			}
		}
		
		updateDataSet();
		mSimpleAdapter.notifyDataSetChanged();
	}
	
	@SuppressWarnings("unused")
	private void setContractorLegalName() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		mEditText = new EditText(getContext());
		mEditText.setHeight(300);
		mEditText.setWidth(350);
		mEditText.setGravity(0);
		mEditText.setText(_entity.ContractorLegalName);
		Dialogs.createDialog("Наименование контрагента", "", mEditText, null, new Command() {
			public void execute() {
				_entity.ContractorLegalName = mEditText.getText().toString();
				updateDataSet();
				mSimpleAdapter.notifyDataSetChanged();
			}
		}, Command.NO_OP).show();
	}
	
	@SuppressWarnings("unused")
	private void setPaymentTerms() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		if (mAlertDialog != null) {
			mAlertDialog.cancel();
		}
		mAlertDialog = Dialogs.createSelectFromListDialog(
				new PaymentTerm(0),
				"", 
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						_entity.PaymentTerms = new PaymentTerm(which + 1);
						updateDataSet();
						mSimpleAdapter.notifyDataSetChanged();
					}
				},
				DIALOG_THEME);
		mAlertDialog.show();
	}
	
	@SuppressWarnings("unused")
	private void setUniqueTaxpayerId() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		mEditText = new EditText(getContext());
		mEditText.setHeight(300);
		mEditText.setWidth(350);
		mEditText.setGravity(0);
		mEditText.setText(_entity.UniqueTaxpayerId);
		mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
		Dialogs.createDialog("ИНН", "", mEditText, null, new Command() {
			public void execute() {
				if (UniqueTaxPayerId.validate(mEditText.getText().toString())) {
					_entity.UniqueTaxpayerId = mEditText.getText().toString();
				} else {				
					Dialogs.createDialog("SFS", "Неправильный ИНН", Command.NO_OP).show();
				}
				updateDataSet();
				mSimpleAdapter.notifyDataSetChanged();
			}
		}, Command.NO_OP).show();
	}
	
	@SuppressWarnings("unused")
	private void setRegistrationCause() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		mEditText = new EditText(getContext());
		mEditText.setHeight(300);
		mEditText.setWidth(350);
		mEditText.setGravity(0);
		mEditText.setText(_entity.RegistrationCause);
		mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
		Dialogs.createDialog("КПП", "", mEditText, null, new Command() {
			public void execute() {
				if (TextUtils.isDigitsOnly(mEditText.getText().toString()) && mEditText.getText().toString().length() == 9) {
					_entity.RegistrationCause = mEditText.getText().toString();
				} else {
					Dialogs.createDialog("SFS", "Неправильный КПП", Command.NO_OP).show();
				}
				updateDataSet();
				mSimpleAdapter.notifyDataSetChanged();
			}
		}, Command.NO_OP).show();
	}
	
	@SuppressWarnings("unused")
	private void setLegalAddress() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		mEditText = new EditText(getContext());
		mEditText.setHeight(300);
		mEditText.setWidth(350);
		mEditText.setGravity(0);
		mEditText.setText(_entity.LegalAddress);
		Dialogs.createDialog("Юридический адрес", "", mEditText, null, new Command() {
			public void execute() {
				_entity.LegalAddress = mEditText.getText().toString();
				updateDataSet();
				mSimpleAdapter.notifyDataSetChanged();
			}
		}, Command.NO_OP).show();
	}
	
	@SuppressWarnings("unused")
	private void setShippingAddress() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		mEditText = new EditText(getContext());
		mEditText.setHeight(300);
		mEditText.setWidth(350);
		mEditText.setGravity(0);
		mEditText.setText(_entity.ShippingAddress);
		Dialogs.createDialog("Адреса доставки", "", mEditText, null, new Command() {
			public void execute() {
				_entity.ShippingAddress = mEditText.getText().toString();
				updateDataSet();
				mSimpleAdapter.notifyDataSetChanged();
			}
		}, Command.NO_OP).show();
	}
	
	@SuppressWarnings("unused")
	private void setBankId() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		mEditText = new EditText(getContext());
		mEditText.setHeight(300);
		mEditText.setWidth(350);
		mEditText.setGravity(0);
		mEditText.setText(_entity.BankId);
		mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
		Dialogs.createDialog("БИК", "", mEditText, null, new Command() {
			public void execute() {
				if (TextUtils.isDigitsOnly(mEditText.getText().toString()) && mEditText.getText().toString().length() == 9) {
					_entity.BankId = mEditText.getText().toString();
				} else {
					Dialogs.createDialog("SFS", "Неправильный БИК", Command.NO_OP).show();
				}
				updateDataSet();
				mSimpleAdapter.notifyDataSetChanged();
			}
		}, Command.NO_OP).show();
	}
	
	@SuppressWarnings("unused")
	private void setBankLegalName() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		mEditText = new EditText(getContext());
		mEditText.setHeight(300);
		mEditText.setWidth(350);
		mEditText.setGravity(0);
		mEditText.setText(_entity.BankLegalName);
		Dialogs.createDialog("Наименование банка", "", mEditText, null, new Command() {
			public void execute() {
				_entity.BankLegalName = mEditText.getText().toString();
				updateDataSet();
				mSimpleAdapter.notifyDataSetChanged();
			}
		}, Command.NO_OP).show();
	}
	
	@SuppressWarnings("unused")
	private void setCurrentAccount() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		mEditText = new EditText(getContext());
		mEditText.setHeight(300);
		mEditText.setWidth(350);
		mEditText.setGravity(0);
		mEditText.setText(_entity.CurrentAccount);
		mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
		Dialogs.createDialog("Расчетный счет", "", mEditText, null, new Command() {
			public void execute() {
				if (TextUtils.isDigitsOnly(mEditText.getText().toString()) && mEditText.getText().toString().length() == 20) {
					_entity.CurrentAccount = mEditText.getText().toString();
				} else {
					Dialogs.createDialog("SFS", "Неправильный номер счета", Command.NO_OP).show();
				}
				updateDataSet();
				mSimpleAdapter.notifyDataSetChanged();
			}
		}, Command.NO_OP).show();
	}
	
	@SuppressWarnings("unused")
	private void setStateRegistrationCertificate() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		mEditText = new EditText(getContext());
		mEditText.setHeight(300);
		mEditText.setWidth(350);
		mEditText.setGravity(0);
		mEditText.setText(_entity.StateRegistrationCertificate);
		mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
		Dialogs.createDialog("Свидетельство о государственной регистрации", "", mEditText, null, new Command() {
			public void execute() {
				if (StateRegistrationCertificate.validate(mEditText.getText().toString())) {
				    _entity.StateRegistrationCertificate = mEditText.getText().toString();
				} else {
				    Dialogs.createDialog("SFS", "Неправильный номер свидетельства", Command.NO_OP).show();
				}
				updateDataSet();
				mSimpleAdapter.notifyDataSetChanged();
			}
		}, Command.NO_OP).show();
	}
	
	@SuppressWarnings("unused")
	private void setPhoneContacts() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		mEditText = new EditText(getContext());
		mEditText.setHeight(300);
		mEditText.setWidth(350);
		mEditText.setGravity(0);
		mEditText.setText(_entity.PhoneContacts);
		mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
		Dialogs.createDialog("Телефоны контактных лиц контрагента", "", mEditText, null, new Command() {
			public void execute() {
				_entity.PhoneContacts = mEditText.getText().toString();
				updateDataSet();
				mSimpleAdapter.notifyDataSetChanged();
			}
		}, Command.NO_OP).show();
	}
	
	@SuppressWarnings("unused")
	private void setVisitDays() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		mEditText = new EditText(getContext());
		mEditText.setHeight(300);
		mEditText.setWidth(350);
		mEditText.setGravity(0);
		mEditText.setText(_entity.VisitDays);
		Dialogs.createDialog("", "Дни посещения", mEditText, null, new Command() {
			public void execute() {
				_entity.VisitDays = mEditText.getText().toString();
				updateDataSet();
				mSimpleAdapter.notifyDataSetChanged();
			}
		}, Command.NO_OP).show();
	}
	
	@SuppressWarnings("unused")
	private void setPsrComment() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		mEditText = new EditText(getContext());
		mEditText.setHeight(300);
		mEditText.setWidth(350);
		mEditText.setGravity(0);
		mEditText.setText(_entity.PsrComment);
		Dialogs.createDialog("Дополнительные комментарии торгового представителя", "", mEditText, null, new Command() {
			public void execute() {
				_entity.PsrComment = mEditText.getText().toString();
				updateDataSet();
				mSimpleAdapter.notifyDataSetChanged();
			}
		}, Command.NO_OP).show();
	}

	@SuppressWarnings("unused")
	private void setSalesChannelType() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		if (mAlertDialog != null) {
			mAlertDialog.cancel();
		}
		mStoreChannel = (RefStoreChannel) Globals.createOrmObject(RefStoreChannel.class);
		mStoreChannel.Select(false, false, HierarchyMode.OnlyEntity);
		
		mAlertDialog = Dialogs.createSelectFromListDialog(mStoreChannel,
				"Канал", new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						_entity.SalesChannelType = (RefStoreChannelEntity) mStoreChannel.getItem(which);
						updateDataSet();
						mSimpleAdapter.notifyDataSetChanged();
					}
				},DIALOG_THEME);
		mAlertDialog.setOnDismissListener(new CatSelectDialogDismissListener(mStoreChannel));
		mAlertDialog.show();
	}
	
	@SuppressWarnings("unused")
	private void setContractorType() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		if (mAlertDialog != null) {
			mAlertDialog.cancel();
		}
		ClientType clientType = _entity.ContractorType;
		if (clientType == null) {
			clientType = new ClientType(0);
		}
		mAlertDialog = Dialogs.createSelectFromListDialog(
				clientType,
				"", 
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						_entity.ContractorType = new ClientType(which + 1);
						updateDataSet();
						mSimpleAdapter.notifyDataSetChanged();
					}
				},
				DIALOG_THEME);
		mAlertDialog.show();
	}
	
	@SuppressWarnings("unused")
	private void setAmountStores() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		mEditText = new EditText(getContext());
		mEditText.setHeight(300);
		mEditText.setWidth(350);
		mEditText.setGravity(0);
		mEditText.setText(String.valueOf(_entity.AmountStores));
		mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
		Dialogs.createDialog("Количество торговых точек", "", mEditText, null, new Command() {
			public void execute() {
				if (TextUtils.isDigitsOnly(mEditText.getText().toString())) {
					_entity.AmountStores = Integer.valueOf(mEditText.getText().toString());
				} else {
					Dialogs.createDialog("SFS", "Неправильное кол-во точек", Command.NO_OP).show();
				}
				updateDataSet();
				mSimpleAdapter.notifyDataSetChanged();
			}
		}, Command.NO_OP).show();
	}
	
	@SuppressWarnings("unused")
	private void setServiceType() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		mServiceType = (RefServiceType) Globals.createOrmObject(RefServiceType.class);
		mServiceType.Select(false, false, HierarchyMode.OnlyEntity);
		mAlertDialog = Dialogs.createSelectFromListDialog(mServiceType,
				"Тип обслуживания",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						_entity.ServiceType = (((RefServiceTypeEntity) mServiceType.getItem(which)));
						updateDataSet();
						mSimpleAdapter.notifyDataSetChanged();
					}
				},DIALOG_THEME);
		mAlertDialog.setOnDismissListener(new CatSelectDialogDismissListener(mServiceType));
		mAlertDialog.show();
	}
	
	@SuppressWarnings("unused")
	private void setRemoteness() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		if (mAlertDialog != null) {
			mAlertDialog.cancel();
		}
		mAlertDialog = Dialogs.createSelectFromListDialog(
				new OutletRemoteness(0),
				"", 
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						_entity.Remoteness = new OutletRemoteness(which + 1);
						updateDataSet();
						mSimpleAdapter.notifyDataSetChanged();
					}
				},
				DIALOG_THEME);
		mAlertDialog.show();
	}
	
	@SuppressWarnings("unused")
	private void setIsHQ() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		_entity.IsHQ = !_entity.IsHQ;
		updateDataSet();
		mSimpleAdapter.notifyDataSetChanged();
	}

	@SuppressWarnings("unused")
	private void setIsDC() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		_entity.IsDC = !_entity.IsDC;
		updateDataSet();
		mSimpleAdapter.notifyDataSetChanged();
	}

	@SuppressWarnings("unused")
	private void setIsShop() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		_entity.IsShop = !_entity.IsShop;
		updateDataSet();
		mSimpleAdapter.notifyDataSetChanged();
	}

	@SuppressWarnings("unused")
	private void setIsOP() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		_entity.IsOP = !_entity.IsOP;
		updateDataSet();
		mSimpleAdapter.notifyDataSetChanged();
	}
	
	@SuppressWarnings("unused")
	private void setPassportN1() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		mEditText = new EditText(getContext());
		mEditText.setHeight(300);
		mEditText.setWidth(350);
		mEditText.setGravity(0);
		mEditText.setText(_entity.PassportN1);
		mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
		Dialogs.createDialog("Паспорт-серия", "", mEditText, null, new Command() {
			public void execute() {
				if (TextUtils.isDigitsOnly(mEditText.getText().toString()) && mEditText.getText().toString().length() == 4) {
					_entity.PassportN1 = mEditText.getText().toString();
				} else {
					Dialogs.createDialog("SFS", "Неправильная серия паспорта", Command.NO_OP).show();
				}
				updateDataSet();
				mSimpleAdapter.notifyDataSetChanged();
			}
		}, Command.NO_OP).show();
	}
	
	@SuppressWarnings("unused")
	private void setPassportN2() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		mEditText = new EditText(getContext());
		mEditText.setHeight(300);
		mEditText.setWidth(350);
		mEditText.setGravity(0);
		mEditText.setText(_entity.PassportN2);
		mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
		Dialogs.createDialog("Паспорт-номер", "", mEditText, null, new Command() {
			public void execute() {
				if (TextUtils.isDigitsOnly(mEditText.getText().toString()) && mEditText.getText().toString().length() == 6) {
					_entity.PassportN2 = mEditText.getText().toString();
				} else {
					Dialogs.createDialog("SFS", "Неправильная серия паспорта", Command.NO_OP).show();
				}
				updateDataSet();
				mSimpleAdapter.notifyDataSetChanged();
			}
		}, Command.NO_OP).show();
	}
	
	@SuppressWarnings("unused")
	private void setPassportN3() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		mEditText = new EditText(getContext());
		mEditText.setHeight(300);
		mEditText.setWidth(350);
		mEditText.setGravity(0);
		mEditText.setText(_entity.PassportN3);
		Dialogs.createDialog("Паспорт-кем выдан", "", mEditText, null, new Command() {
			public void execute() {
				_entity.PassportN3 = mEditText.getText().toString();
				updateDataSet();
				mSimpleAdapter.notifyDataSetChanged();
			}
		}, Command.NO_OP).show();
	}
	
	@SuppressWarnings("unused")
	private void setPassportN4() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		mEditText = new EditText(getContext());
		mEditText.setHeight(300);
		mEditText.setWidth(350);
		mEditText.setGravity(0);
		mEditText.setText(_entity.PassportN4);
		Dialogs.createDateDialog(getContext(), new OnDateSetListener() {
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", new Locale("ru", "RU"));
				mCalendar.set(year, monthOfYear, dayOfMonth);
				_entity.PassportN4 = format.format(mCalendar.getTime()); 
				updateDataSet();
				mSimpleAdapter.notifyDataSetChanged();
			}
		}, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();
	}
	
	@SuppressWarnings("unused")
	private void setIsContractorsRepresentativeAuthorizedToSignContract() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		_entity.IsContractorsRepresentativeAuthorizedToSignContract = !_entity.IsContractorsRepresentativeAuthorizedToSignContract;
		updateDataSet();
		mSimpleAdapter.notifyDataSetChanged();
	}
	
	@SuppressWarnings("unused")
	private void setIsDocumentsFromContractorHasNecessaryStampsAndSignatures() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		_entity.IsDocumentsFromContractorHasNecessaryStampsAndSignatures = (!_entity.IsDocumentsFromContractorHasNecessaryStampsAndSignatures);
		updateDataSet();
		mSimpleAdapter.notifyDataSetChanged();
	}
	
	@SuppressWarnings("unused")
	private void setHasAgreementSigned() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		_entity.HasAgreementSigned = (!_entity.HasAgreementSigned);
		updateDataSet();
		mSimpleAdapter.notifyDataSetChanged();
	}
	
	@SuppressWarnings("unused")
	private void setHasPassportCopy() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		_entity.HasPassportCopy = (!_entity.HasPassportCopy);
		updateDataSet();
		mSimpleAdapter.notifyDataSetChanged();
	}
	
	@SuppressWarnings("unused")
	private void setHasUniqueTaxpayerIdCopy() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		_entity.HasUniqueTaxpayerIdCopy = (!_entity.HasUniqueTaxpayerIdCopy);
		updateDataSet();
		mSimpleAdapter.notifyDataSetChanged();
	}
	
	@SuppressWarnings("unused")
	private void setHasStateRegistrationCertificate() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		_entity.HasStateRegistrationCertificate = (!_entity.HasStateRegistrationCertificate);
		updateDataSet();
		mSimpleAdapter.notifyDataSetChanged();
	}
	
	@SuppressWarnings("unused")
	private void setHasLeaseContract() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		_entity.HasLeaseContract = (!_entity.HasLeaseContract);
		updateDataSet();
		mSimpleAdapter.notifyDataSetChanged();
	}
	
	@SuppressWarnings("unused")
	private void setHasExtractFromUnifiedStateRegisterOfIndividualEntrepreneurs() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		_entity.HasExtractFromUnifiedStateRegisterOfIndividualEntrepreneurs = (!_entity.HasExtractFromUnifiedStateRegisterOfIndividualEntrepreneurs);
		updateDataSet();
		mSimpleAdapter.notifyDataSetChanged();
	}
	
	@SuppressWarnings("unused")
	private void setHasProtocolSolutionsToCreate() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		_entity.HasProtocolSolutionsToCreate = (!_entity.HasProtocolSolutionsToCreate);
		updateDataSet();
		mSimpleAdapter.notifyDataSetChanged();
	}
	
	@SuppressWarnings("unused")
	private void setHasTheOrganizationsDecisionOnTheAppointmentOfTheHead() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		_entity.HasTheOrganizationsDecisionOnTheAppointmentOfTheHead = (!_entity.HasTheOrganizationsDecisionOnTheAppointmentOfTheHead);
		updateDataSet();
		mSimpleAdapter.notifyDataSetChanged();
	}
	
	@SuppressWarnings("unused")
	private void setHasExcerptFromTheListOfMembersOfTheCompany() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		_entity.HasExcerptFromTheListOfMembersOfTheCompany = (!_entity.HasExcerptFromTheListOfMembersOfTheCompany);
		updateDataSet();
		mSimpleAdapter.notifyDataSetChanged();
	}
	
	@SuppressWarnings("unused")
	private void setHasCharterCopy() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		_entity.HasCharterCopy = (!_entity.HasCharterCopy);
		updateDataSet();
		mSimpleAdapter.notifyDataSetChanged();
	}
	
	@SuppressWarnings("unused")
	private void setHasPrimaryStateRegistrationNumber() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		_entity.HasPrimaryStateRegistrationNumber = (!_entity.HasPrimaryStateRegistrationNumber);
		updateDataSet();
		mSimpleAdapter.notifyDataSetChanged();
	}
	
	@SuppressWarnings("unused")
	private void setHasPowerOfAttorneyForThePersonSigningTheContract() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		_entity.HasPowerOfAttorneyForThePersonSigningTheContract = (!_entity.HasPowerOfAttorneyForThePersonSigningTheContract);
		updateDataSet();
		mSimpleAdapter.notifyDataSetChanged();
	}
	
	@SuppressWarnings("unused")
	private void setHasAnExtractFromTheUnifiedStateRegisterOfLegalEntities() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
				return;
			}
		}
		_entity.HasAnExtractFromTheUnifiedStateRegisterOfLegalEntities = (!_entity.HasAnExtractFromTheUnifiedStateRegisterOfLegalEntities);
		updateDataSet();
		mSimpleAdapter.notifyDataSetChanged();
	}
	
	@Override
	protected Boolean onRemove() {
		if (!_entity.Approved.equals(new RequestStatusType(10))) {
			if (_entity.getReadOnly()) {
				_catalog.save();
				MainActivity.getInstance().enableSync(true);
				
				return true;
			}
		}
		Dialogs.createDialog("", "Загрузить документ в 1С?", new Command() {
			@Override
			public void execute() {
				if (checkData()) {
					_entity.IsAccepted = false;
					_entity.Approved = new RequestStatusType(1);
					_entity.IsMark = false;
					saveAndExit();
				} else {
					Dialogs.createDialog("", "Не все поля документа заполнены корректно. Продолжить выход?", new Command() {
						@Override
						public void execute() {
							_entity.IsAccepted = false;
							_entity.Approved = new RequestStatusType(1);
							_entity.IsMark = true;
							saveAndExit();
						}
					},Command.NO_OP).show();
				}
			}
		}, new Command() {
			@Override
			public void execute() {
				_entity.IsAccepted = false;
				_entity.Approved = new RequestStatusType(1);
				_entity.IsMark = true;
				saveAndExit();
			}
		}, Command.NO_OP, 0).show();

		return false;
	}
	
	protected void saveAndExit() {
		double[] points = SfsLocationManager.getInstance(getContext()).getCurrentLocation();
		if (points != null) {
			_entity.Latitude = (float) points[0];
			_entity.Longitude = (float) points[1];
		}
		_catalog.save();
		MainActivity.getInstance().enableSync(true);
		super.closeView();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		@SuppressWarnings("unchecked")
		HashMap<String, ?> menuItem = (HashMap<String, ?>) mSimpleAdapter.getItem(position);
		if (menuItem == null) {
			return;
		}
		String selectMethod = (String) menuItem.get("selectMethod");
		if ((selectMethod == null) || (selectMethod.isEmpty())) {
			return;
		}
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
	
	public boolean checkData() {
		if (_entity.Approved.equals(new RequestStatusType(10))) {
			_entity.Approved = new RequestStatusType(1);
		}
		
		if (!_entity.IsContractorsRepresentativeAuthorizedToSignContract) {
			return false;
		}
		if (!_entity.IsDocumentsFromContractorHasNecessaryStampsAndSignatures) {
			return false;
		}
		if (_entity.Remoteness == null) {
			return false;
		}
		if (!_entity.HasAgreementSigned) {
			return false;
		}
		if (_entity.User == null) {
			return false;
		}
		if (_entity.Employee == null) {
			return false;
		}
		if (_entity.ContractorLegalName == null || _entity.ContractorLegalName.isEmpty()) {
			return false;
		}
		if (_entity.BusinessStructureType == null) {
			return false;
		}
		if (_entity.PaymentTerms == null) {
			return false;
		}
		if (_entity.UniqueTaxpayerId == null || _entity.UniqueTaxpayerId.isEmpty()) {
			return false;
		}
		if (_entity.LegalAddress == null || _entity.LegalAddress.isEmpty()) {
			return false;
		}
		if (_entity.ShippingAddress == null || _entity.ShippingAddress.isEmpty()) {
			return false;
		}
		if (_entity.StateRegistrationCertificate == null || _entity.StateRegistrationCertificate.isEmpty()) {
			return false;
		}
		if (_entity.BusinessType == null) {
			return false;
		}
		if (_entity.SalesChannelType == null) {
			return false;
		}
		if (_entity.SalesChannelType == null) {
			return false;
		}
		
		if (_entity.BusinessStructureType.toString().equals("Физическое лицо")) {
			if (_entity.PassportN1 == null || _entity.PassportN1.isEmpty()) {
				return false;
			}
			if (_entity.PassportN2 == null || _entity.PassportN2.isEmpty()) {
				return false;
			}
			if (_entity.PassportN3 == null || _entity.PassportN3.isEmpty()) {
				return false;
			}
			if (_entity.PassportN4 == null) {
				return false;
			}
			
			if (_entity.PaymentTerms.toString().equals("Предоплата")) {
				if (!_entity.HasUniqueTaxpayerIdCopy) {
					return false;
				}
				if (!_entity.HasStateRegistrationCertificate) {
					return false;
				}
			}
			if (_entity.PaymentTerms.toString().equals("Кредит")) {
				if (!_entity.HasPassportCopy) {
					return false;
				}
				if (!_entity.HasUniqueTaxpayerIdCopy) {
					return false;
				}
				if (!_entity.HasStateRegistrationCertificate) {
					return false;
				}
			}
		}
		if (_entity.BusinessStructureType.toString().equals("Юридическое лицо")) {
		    if (_entity.BankId == null || _entity.BankId.isEmpty()) {
			return false;
		    }
		    if (_entity.BankLegalName == null || _entity.BankLegalName.isEmpty()) {
			return false;
		    }
		    if (_entity.CurrentAccount == null || _entity.CurrentAccount.isEmpty()) {
			return false;
		    }
			if (_entity.PaymentTerms.toString().equals("Предоплата")) {
				if (!_entity.HasUniqueTaxpayerIdCopy) {
					return false;
				}
				if (!_entity.HasPrimaryStateRegistrationNumber) {
					return false;
				}
			}
			if (_entity.PaymentTerms.toString().equals("Кредит")) {
				if (!_entity.HasUniqueTaxpayerIdCopy) {
					return false;
				}
				if (!_entity.HasTheOrganizationsDecisionOnTheAppointmentOfTheHead) {
					return false;
				}
				if (!_entity.HasPowerOfAttorneyForThePersonSigningTheContract) {
					return false;
				}
				if (!_entity.HasPrimaryStateRegistrationNumber) {
					return false;
				}
				if (!_entity.HasCharterCopy) {
					return false;
				}
			}
		}
		
		return true;
	}
	
}
