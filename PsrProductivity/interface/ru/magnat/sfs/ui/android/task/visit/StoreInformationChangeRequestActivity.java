package ru.magnat.sfs.ui.android.task.visit;

import java.util.ArrayList;
import java.util.List;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
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
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle; 
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class StoreInformationChangeRequestActivity extends Activity {

	@SuppressWarnings("unused")
	private static final String LOG_TAG = "StoreInformationChangeRequestActivity";
	
	private Context mActivityContext;
	@SuppressWarnings("unused")
	private Context mApplicationContext;
	
	private ActionBar mActionBar;
	private String mActionBarTitle;
	//private String mActioBarSubTitle;
	
	private Long mStoreId;
	private RefOutletEntity mOutletEntity;
	
	private EditText mStoreLegalName;
	private EditText mStoreAddress;
	private Spinner mStoreChannel;
	private Spinner mStoreServiceType;
	private Spinner mStoreType;
	private CheckBox mHeadOffice;
	private CheckBox mDistributionCenter;
	private CheckBox mStore;
	private CheckBox mPointOfOrderTaking;
	private CheckBox mWellcomeOfferSeparatePaymentDiscount;
	private CheckBox mGoldenOfferSeparatePaymentDiscount;
	private CheckBox mInactive;
	
	private final String mUnselectedItem = "Не задано";
	private Integer mPosition;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store_information_change_request);
		
		// Context settings
		this.mApplicationContext = this.getApplicationContext();
		this.mActivityContext = this;
		
		// Action bar settings
		this.mActionBar = this.getActionBar();
		this.mActionBarTitle = this.getResources().getString(R.string.title_request_on_change_store_info);
		this.mActionBar.setTitle(this.mActionBarTitle);
		//this.mActioBarSubTitle = this.getResources().getString(R.string.subtitle_request_on_change_store_info);
		//this.mActionBar.setSubtitle(this.mActioBarSubTitle);
		
		this.mStoreLegalName = (EditText) this.findViewById(R.id.edit_store_legal_name);
		this.mStoreAddress = (EditText) this.findViewById(R.id.edit_store_address);
		this.mStoreChannel = (Spinner) findViewById(R.id.spinner_store_channel);
		this.mStoreServiceType = (Spinner) findViewById(R.id.spinner_store_service_type);
		this.mStoreType = (Spinner) findViewById(R.id.spinner_store_type);
		this.mHeadOffice = (CheckBox) findViewById(R.id.checkBox_head_office);
		this.mDistributionCenter = (CheckBox) findViewById(R.id.checkBox_distribution_center);
		this.mStore = (CheckBox) findViewById(R.id.checkBox_store);
		this.mPointOfOrderTaking = (CheckBox) findViewById(R.id.checkBox_point_of_order_taking);
		this.mWellcomeOfferSeparatePaymentDiscount = (CheckBox) findViewById(R.id.checkBox_wellcome_offer_separate_payment_discount);
		this.mGoldenOfferSeparatePaymentDiscount = (CheckBox) findViewById(R.id.checkBox_golden_offer_separate_payment_discount);
		this.mInactive = (CheckBox) findViewById(R.id.checkBox_inactive);
		
		// Store Id
		this.mStoreId = this.getIntent().getExtras().getLong("storeId");
		
		RefOutlet outlet = new RefOutlet(MainActivity.getInstance()); 
		this.mOutletEntity = outlet.FindById(this.mStoreId);
		
		this.mStoreLegalName.setText(this.mOutletEntity.Descr);
		this.mStoreAddress.setText(this.mOutletEntity.Address);
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, this.getStoreChannels());
		this.mStoreChannel.setAdapter(dataAdapter);
		this.mStoreChannel.setSelection(this.mPosition);
		//this.mStoreChannel.requestFocus();
		
		ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, this.getStoreServiceType());
		this.mStoreServiceType.setAdapter(dataAdapter2);
		this.mStoreServiceType.setSelection(this.mPosition);
		
		ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, this.getStoreType());
		this.mStoreType.setAdapter(dataAdapter3);
		this.mStoreType.setSelection(this.mPosition);
		
		this.mHeadOffice.setSelected(this.mOutletEntity.IsHQ);
		this.mDistributionCenter.setSelected(this.mOutletEntity.IsDC);
		this.mStore.setSelected(this.mOutletEntity.IsShop);
		this.mPointOfOrderTaking.setSelected(this.mOutletEntity.IsOP);
		this.mWellcomeOfferSeparatePaymentDiscount.setSelected(this.mOutletEntity.WelcomeOfferDiscountByPayment);
		this.mGoldenOfferSeparatePaymentDiscount.setSelected(this.mOutletEntity.GoldenDiscountByPayment);
		this.mInactive.setSelected(this.mOutletEntity.IsMark);
		
		// free resourses
		outlet.close();
		outlet = null;
	}
	
	private ArrayList<String> getStoreChannels() {
		List<String> result = new ArrayList<String>();
		
		result.add(this.mUnselectedItem);
		
		this.mPosition = 0;
		int count = 1;
		
		RefStoreChannel storeChannel = (RefStoreChannel) Globals.createOrmObject(RefStoreChannel.class);
		storeChannel.Select(false, false, HierarchyMode.OnlyEntity);
		while (storeChannel.Next()) {
			if (this.mOutletEntity.Channel != null && storeChannel.Current().Id == this.mOutletEntity.Channel.Id) {
				this.mPosition = count;
			}
			count++;
			result.add(storeChannel.Current().Descr);
		} 
		
		// Если значение параметра не задано, то есть равно null, 
		// выбираем по умолчанию первый пункт = "Не задано"
		if (this.mOutletEntity.Channel == null) {
			this.mPosition = 0;
		}
		
		storeChannel.close();
		storeChannel = null;
		
		return (ArrayList<String>) result;
	}
	
	private ArrayList<String> getStoreServiceType() {
		List<String> result = new ArrayList<String>();
		
		result.add(this.mUnselectedItem);
		
		this.mPosition = 0;
		int count = 1;
		
		RefServiceType serviceType = (RefServiceType) Globals.createOrmObject(RefServiceType.class);
		serviceType.Select(false, false, HierarchyMode.OnlyEntity);
		while (serviceType.Next()) {
			if (this.mOutletEntity.ServiceType != null && serviceType.Current().Descr.equals(this.mOutletEntity.ServiceType.Descr)) {
				this.mPosition = count;
			}
			count++;
			result.add(serviceType.Current().Descr);
		}
		
		// Если значение параметра не задано, то есть равно null, 
		// выбираем по умолчанию первый пункт = "Не задано"
		if (this.mOutletEntity.ServiceType == null) {
			this.mPosition = 0;
		}
		
		serviceType.close();
		serviceType = null;
		
		return (ArrayList<String>) result;
	}

	private ArrayList<String> getStoreType() {
		List<String> result = new ArrayList<String>();
		
		result.add(this.mUnselectedItem);
		
		this.mPosition = 0;
		int count = 1;
		
		RefStoreType storeType = (RefStoreType) Globals.createOrmObject(RefStoreType.class);
		storeType.Select(false, false, HierarchyMode.OnlyEntity);
		while (storeType.Next()) {
			if (this.mOutletEntity.StoreType != null && storeType.Current().Descr.equals(this.mOutletEntity.StoreType.Descr)) {
				this.mPosition = count;
			}
			count++;
			result.add(storeType.Current().Descr);
		}
		
		// Если значение параметра не задано, то есть равно null, 
		// выбираем по умолчанию первый пункт = "Не задано"
		if (this.mOutletEntity.StoreType == null) {
			this.mPosition = 0;
		}
		
		storeType.close();
		storeType = null;
		
		return (ArrayList<String>) result;
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this.mActivityContext);
		dialog.setTitle(R.string.dialog_confirmation_title);
		dialog.setMessage(R.string.dialog_confirmation_message);
		dialog.setPositiveButton(R.string.button_title_create, new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if (StoreInformationChangeRequestActivity.this.hasChanged() && StoreInformationChangeRequestActivity.this.validateChanges()) {
					if (StoreInformationChangeRequestActivity.this.saveChanges()) {
						Toast.makeText(MainActivity.getInstance(), "Запрос успешно создан!", Toast.LENGTH_LONG).show();
						StoreInformationChangeRequestActivity.this.finish();
					} else {
						Toast.makeText(MainActivity.getInstance(), "Запрос не создан!", Toast.LENGTH_LONG).show();
						StoreInformationChangeRequestActivity.this.finish();
					}
				} else {
					Toast.makeText(MainActivity.getInstance(), "Данные не были изменены! Измените данные или отмените запрос!", Toast.LENGTH_LONG).show();
				}
			}
		});
		dialog.setNegativeButton(R.string.button_title_cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				//Toast.makeText(SFSActivity.getInstance(), "Запрос отменен!", Toast.LENGTH_LONG).show();
				StoreInformationChangeRequestActivity.this.finish();
			}
		});
		dialog.setCancelable(true);
		dialog.show();
	}
	
	private boolean hasChanged() {
		boolean result = false;
			
		if (!this.mOutletEntity.Descr.equals(this.mStoreLegalName.getText().toString())) return true;
		if (!this.mOutletEntity.Address.equals(this.mStoreAddress.getText().toString())) return true;
		
		if (this.mOutletEntity.Channel != null && !this.mOutletEntity.Channel.Descr.equals(this.mStoreChannel.getSelectedItem().toString())) return true;
		if (this.mOutletEntity.Channel == null && !this.mStoreChannel.getSelectedItem().toString().equals(this.mUnselectedItem)) return true;
		
		if (this.mOutletEntity.ServiceType != null && !this.mOutletEntity.ServiceType.Descr.equals(this.mStoreServiceType.getSelectedItem().toString())) return true;
		if (this.mOutletEntity.ServiceType == null && !this.mStoreServiceType.getSelectedItem().toString().equals(this.mUnselectedItem)) return true;
		
		if (this.mOutletEntity.StoreType != null && !this.mOutletEntity.StoreType.Descr.equals(this.mStoreType.getSelectedItem().toString())) return true;
		if (this.mOutletEntity.StoreType == null && !this.mStoreType.getSelectedItem().toString().equals(this.mUnselectedItem)) return true;
		
		if (this.mOutletEntity.IsHQ != this.mHeadOffice.isChecked()) return true; 
		if (this.mOutletEntity.IsDC != this.mDistributionCenter.isChecked()) return true;
		if (this.mOutletEntity.IsShop != this.mStore.isChecked()) return true;
		if (this.mOutletEntity.IsOP != this.mPointOfOrderTaking.isChecked()) return true;
		
		//if (this.mOutletEntity.WelcomeOfferDiscountByPayment != this.mWellcomeOfferSeparatePaymentDiscount.isChecked()) return true;
		//if (this.mOutletEntity.GoldenDiscountByPayment != this.mGoldenOfferSeparatePaymentDiscount.isChecked()) return true;
		//if (this.mOutletEntity.IsMark != this.mInactive.isChecked()) return true;
		
		return result;
	}
	
	private boolean validateChanges() {
		return true;
	}
	
	private boolean saveChanges() {
		DocRequestOnChangeOutletJournal docRequestOnChangeOutletJournal = new DocRequestOnChangeOutletJournal(MainActivity.getInstance());
		docRequestOnChangeOutletJournal.NewEntity(); 
		DocRequestOnChangeOutletEntity docRequestOnChangeOutletEntity = docRequestOnChangeOutletJournal.Current();
		docRequestOnChangeOutletEntity.setDefaults(MainActivity.getInstance(), MainActivity.getInstance().mCurrentWorkday);
 
		docRequestOnChangeOutletEntity.Outlet = this.mOutletEntity;
		docRequestOnChangeOutletEntity.Descr = this.mStoreLegalName.getText().toString();
		docRequestOnChangeOutletEntity.Address = this.mStoreAddress.getText().toString();
		
		docRequestOnChangeOutletEntity.IsDC = this.mDistributionCenter.isChecked();
		docRequestOnChangeOutletEntity.IsHQ = this.mHeadOffice.isChecked(); 
		docRequestOnChangeOutletEntity.IsOP = this.mPointOfOrderTaking.isChecked();
		docRequestOnChangeOutletEntity.IsShop = this.mStore.isChecked();
		
		//docRequestOnChangeOutletEntity.LocationLat = 0f;
		//docRequestOnChangeOutletEntity.LocationLon = 0f;
		
		docRequestOnChangeOutletEntity.Channel = getStoreChannel(this.mStoreChannel.getSelectedItem().toString());
		docRequestOnChangeOutletEntity.ServiceType = getStoreServiceType(this.mStoreServiceType.getSelectedItem().toString());
		docRequestOnChangeOutletEntity.StoreType = getStoreType(this.mStoreType.getSelectedItem().toString());

		return docRequestOnChangeOutletJournal.save();
	}

	private RefStoreChannelEntity getStoreChannel(final String channelName) {
		if (channelName.equals(this.mUnselectedItem)) {
			return null;
		}
		
		RefStoreChannelEntity result = null;
		
		RefStoreChannel storeChannel = (RefStoreChannel) Globals.createOrmObject(RefStoreChannel.class);
		storeChannel.Select(false, false, HierarchyMode.OnlyEntity);
		while (storeChannel.Next()) {
			if (storeChannel.Current().Descr.equals(channelName)) {
				result = storeChannel.Current();
				return result;
			}
		} 
		storeChannel.close();
		storeChannel = null;
		
		return result;
	}
	
	private RefServiceTypeEntity getStoreServiceType(final String serviceTypeName) {
		if (serviceTypeName.equals(this.mUnselectedItem)) {
			return null;
		}
		
		RefServiceTypeEntity result = null;
		
		RefServiceType serviceType = (RefServiceType) Globals.createOrmObject(RefServiceType.class);
		serviceType.Select(false, false, HierarchyMode.OnlyEntity);
		while (serviceType.Next()) {
			if (serviceType.Current().Descr.equals(serviceTypeName)) {
				result = serviceType.Current();
				return result;
			}
		} 
		serviceType.close();
		serviceType = null;
		
		return result;
	}
	
	private RefStoreTypeEntity getStoreType(final String storeTypeName) {
		if (storeTypeName.equals(this.mUnselectedItem)) {
			return null;
		}
		
		RefStoreTypeEntity result = null;
		
		RefStoreType storeType = (RefStoreType) Globals.createOrmObject(RefStoreType.class);
		storeType.Select(false, false, HierarchyMode.OnlyEntity);
		while (storeType.Next()) {
			if (storeType.Current().Descr.equals(storeTypeName)) {
				result = storeType.Current();
				return result;
			}
		} 
		storeType.close();
		storeType = null;
		
		return result;
	}
}
