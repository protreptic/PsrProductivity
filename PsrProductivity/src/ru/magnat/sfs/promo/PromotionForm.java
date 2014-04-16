package ru.magnat.sfs.promo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import ru.magnat.sfs.android.Command;
import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.NumberPickerDialog;
import ru.magnat.sfs.android.NumberPickerDialog.OnNumberSetListener;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.OnValueChangedListener;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.cache.promo.Promo;
import ru.magnat.sfs.bom.order.DocOrderEntity;
import ru.magnat.sfs.bom.order.DocOrderLine;
import ru.magnat.sfs.bom.order.DocOrderLineEntity;
import ru.magnat.sfs.bom.order.OnAmountUpdatedListener;
import ru.magnat.sfs.bom.order.OnChangeOrderQuantityListener;
import ru.magnat.sfs.bom.order.OnOrderChangedListener.OnAmountChangedListener;
import ru.magnat.sfs.bom.ref.csku.RefCskuEntity;
import ru.magnat.sfs.bom.ref.outlet.RefOutlet;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.ref.productitem.RefProductItem;
import ru.magnat.sfs.bom.ref.productitem.RefProductItemEntity;
import ru.magnat.sfs.bom.ref.promo.CompensationType;
import ru.magnat.sfs.bom.ref.promo.PromoType;
import ru.magnat.sfs.bom.ref.promo.RefPromoBonusCsku;
import ru.magnat.sfs.bom.ref.promo.RefPromoDetails;
import ru.magnat.sfs.bom.ref.promo.RefPromoDetailsEntity;
import ru.magnat.sfs.bom.ref.promo.RefPromoEntity;
import ru.magnat.sfs.database.UltraliteCursor;
import ru.magnat.sfs.money.Money;
import ru.magnat.sfs.ui.android.controls.NumericPadLayout;
import ru.magnat.sfs.ui.android.controls.OnCommitListener;
import ru.magnat.sfs.ui.android.doc.order.DocOrderLineInfoView;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

public class PromotionForm extends Fragment implements OnChildClickListener, OnItemLongClickListener, OnPromotionStateListener, OnKeyListener, OnFormExitListener, OnAmountChangedListener, OnAmountUpdatedListener {
	
	private Context mContext;
	private Resources mResources;
	private KitListAdapter mAdapter;
	private ExpandableListView mListView;
	
	private List<Map<String, Object>> mGroupData = new ArrayList<Map<String, Object>>();
	private List<List<Map<String, Object>>> mChildData = new ArrayList<List<Map<String, Object>>>();
	private NumericPadLayout mNumericPadLayout;
	
	private DocOrderEntity mOrder;
	private RefPromoEntity mPromo;
	private RefPromoDetailsEntity mPromoDetails;
	private Long mPromotionId;
	private OnPromotionStateListener mPromotionStateListener;
	private Integer mCurrentSelectedChildItem;
	private Integer mCurrentSelectedGroupItem;
	
	private Typeface mTypeface;
	private SimpleDateFormat mDateFormat;
	
	private TextView mPromoTypeView;
	private TextView mPromoApplyPeriodView;
	private TextView mPromoDetailsView;
	private Button mPromoMultiplicity;
	private Switch mAvailabilitySwitcher;
	private Switch mCompensationSwitcher;
	
	private int mPromotionType;
	private int mCompensationType;
	private int mBonusCskuQuantity;
	private float mBonusCskuDiscount;
	@SuppressWarnings("unused")
	private float mBonusCskuPrice;
	
	private Calendar mStartDateOfPromo;
	private Calendar mEndDateOfPromo;
	
	private NumberPickerDialog mNumberPickerDialog;
	private Integer mMultiplicity;
	
	private OnNumberSetListener mNumberPickerDialogCallback = new OnNumberSetListener() {
		
		@Override
		public void onNumberSet(NumberPicker view, int value) {
			mOrder.changePromoCounter(mPromoDetails.Id, value);
		}
	};

	protected boolean mEditing = false;
	private ProgressBar mProgressBar;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.promotion_pick_list, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
		mResources = mContext.getResources();
		
		getView().setOnKeyListener(this);
		
		mOrder = MainActivity.getInstance().mCurrentOrder;
		setPromotionStateListener(mOrder);
		mPromotionId = getArguments().getLong("promotion_id");
		
		if (mOrder.getPromoCompensationType(mPromotionId) == 0 && !mOrder.getPromoAvailabilty(mPromotionId)) {
			firePrefferedCompensationTypeChanged(mPromotionId, 0);
		}
		mOrder.addOnAmountChangedListener(this);
		mOrder.addOnAmountUpdatedListener(this);
		
		RefPromoDetails refPromoDetails = new RefPromoDetails(mContext);
		mPromoDetails = refPromoDetails.FindById(mPromotionId);
		refPromoDetails.close();
		refPromoDetails = null;
		
		mPromo = mPromoDetails.ParentExt;
	
		mTypeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/RobotoCondensed-Regular.ttf");
		mDateFormat = new SimpleDateFormat("dd MMMMM", new Locale("ru", "RU"));
		mStartDateOfPromo = Calendar.getInstance(new Locale("ru", "RU"));
		mEndDateOfPromo = Calendar.getInstance(new Locale("ru", "RU"));
		
		mPromoTypeView = (TextView) getActivity().findViewById(R.id.promo_action_type);
		mPromoTypeView.setTypeface(mTypeface);
		mPromoTypeView.setText(mPromo.PromoType.toString());
		if (mPromo.PromoType.equals(new PromoType(1))) {
			mPromoTypeView.setTextColor(getResources().getColor(android.R.color.holo_orange_light));
		}
		if (mPromo.PromoType.equals(new PromoType(2))) {
			mPromoTypeView.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
		}
		if (mPromo.PromoType.equals(new PromoType(3))) {
			mPromoTypeView.setTextColor(getResources().getColor(android.R.color.holo_green_light));
		}
		if (mPromo.PromoType.equals(new PromoType(4))) {
			mPromoTypeView.setTextColor(getResources().getColor(android.R.color.holo_purple));
		}
		
		mPromoApplyPeriodView = (TextView) getActivity().findViewById(R.id.promo_action_apply_date);
		mPromoApplyPeriodView.setTypeface(mTypeface);

		mStartDateOfPromo.setTime(mPromoDetails.StartOfPromo);
		String startDateString = mDateFormat.format(mStartDateOfPromo.getTime()); 
		mEndDateOfPromo.setTime(mPromoDetails.EndOfPromo);
		String endDateString = mDateFormat.format(mEndDateOfPromo.getTime());
		mPromoApplyPeriodView.setText("действует с " + startDateString + ((mStartDateOfPromo.get(Calendar.YEAR) != mEndDateOfPromo.get(Calendar.YEAR)) ? " " + mStartDateOfPromo.get(Calendar.YEAR) + " года" : "") + " по " + endDateString + " " + mEndDateOfPromo.get(Calendar.YEAR) + " года");
		
		mPromoDetailsView = (TextView) getActivity().findViewById(R.id.promo_action_description); 
		mPromoDetailsView.setTypeface(mTypeface);
		mPromoDetailsView.setText(mPromoDetails.Descr + " | " + mPromo.DescriptionSfa);

		mAvailabilitySwitcher = (Switch) getActivity().findViewById(R.id.switch1);
		mAvailabilitySwitcher.setChecked(mOrder.getPromoAvailabilty(mPromoDetails.Id));
		mAvailabilitySwitcher.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (mPromotionStateListener != null) { 
					mPromotionStateListener.onPromotionAvailabilityChanged(isChecked, mPromoDetails.Id);
				}
			}
		});
		
		mCompensationSwitcher = (Switch) getActivity().findViewById(R.id.switch2);
		mCompensationSwitcher.setVisibility(View.INVISIBLE); 
		
		mBonusCskuInfo = (TextView) getActivity().findViewById(R.id.bonus_info);
		mBonusCskuInfo.setTypeface(mTypeface);
		mBonusCskuInfo.setTextColor(mResources.getColor(android.R.color.holo_blue_light)); 
		mBonusCskuInfo.setVisibility(View.VISIBLE);
		
		mCompensationType = mOrder.getPromoCompensationType(mPromoDetails.Id);
		switch (mCompensationType) {
			case KitListAdapter.COMPENSATION_MONETARY: {
				mCompensationSwitcher.setVisibility(View.INVISIBLE);
			} break;
			case KitListAdapter.COMPENSATION_FREE_PRODUCT: { 
				mCompensationSwitcher.setVisibility(View.INVISIBLE);
			} break;	
			case KitListAdapter.COMPENSATION_CHOICE: { 
				int chosenCompensationType = mOrder.getPromoPrefferedCompensationType(mPromoDetails.Id);
				if (chosenCompensationType != 0) { 
					mCompensationType = chosenCompensationType;
					mCompensationSwitcher.setVisibility(View.VISIBLE);
				} else {
					mCompensationSwitcher.setVisibility(View.INVISIBLE);
				}
				switch (mCompensationType) {
					case KitListAdapter.COMPENSATION_MONETARY: {
						mCompensationSwitcher.setChecked(false);
						int compensationType = mOrder.getPromoCompensationType(mPromoDetails.Id);
						if (compensationType == KitListAdapter.COMPENSATION_CHOICE) {
							mCompensationSwitcher.setEnabled(true);
						} else {
							mCompensationSwitcher.setEnabled(false);
						}
					} break;
					case KitListAdapter.COMPENSATION_FREE_PRODUCT: {
						mCompensationSwitcher.setChecked(true);
						int compensationType = mOrder.getPromoCompensationType(mPromoDetails.Id);
						if (compensationType == KitListAdapter.COMPENSATION_CHOICE) {
							mCompensationSwitcher.setEnabled(true);
						} else {
							mCompensationSwitcher.setEnabled(false);
						}
					} break;
					case KitListAdapter.COMPENSATION_CHOICE: {
						mCompensationSwitcher.setChecked(false);
						mCompensationSwitcher.setEnabled(true);
					} break;
					default: {
						throw new RuntimeException("Unknown chousen compensation type: " + chosenCompensationType);
					}
				}
			} break;
			default: {
				throw new RuntimeException("Unknown compensation type: " + mCompensationType); 
			}
		}
		
		mPromoMultiplicity = (Button) getActivity().findViewById(R.id.change_multiplicity);
		mMultiplicity = mOrder.getPromotionMultiplicity(mPromoDetails.Id);
		mPromoMultiplicity.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mNumberPickerDialog = new NumberPickerDialog(mContext, mNumberPickerDialogCallback, mMultiplicity, 1, 5);
				mNumberPickerDialog.show();
			}
		});
		
		mPromotionType = mPromo.PromoType.getId();
		switch (mPromotionType) {
			case PromoType.PRICING: {
				
			} break;
			case PromoType.BAG_ORDER: {
				readBonusCskuInfo();
			} break;
			case PromoType.BAG_PERIOD: {
				readBonusCskuInfo();
			} break;
			case PromoType.BAG_VOLUME: {
				mCompensationSwitcher.setChecked(false);
				mCompensationSwitcher.setEnabled(false);
				mCompensationSwitcher.setVisibility(View.GONE); 
				
				mPromoMultiplicity.setVisibility(View.GONE);
			} break;
			default: {
				throw new RuntimeException("Unknown promotion type: " + mCompensationType);
			} 
		}
		
		updateBonusInfo();
		
		mCompensationSwitcher.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (mPromotionStateListener != null) {
					mPromotionStateListener.onPromotionCompensationTypeChanged(((isChecked) ? 1 : 2), mPromoDetails.Id);
				}
			}
		});
		

		mOrder.addOnPromoStateChangedListener(this);

		mPromoMultiplicity.setText("" + mMultiplicity); 
		mPromoMultiplicity.setTextSize(22f);
		mPromoMultiplicity.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/RobotoCondensed-Regular.ttf"));
		mPromoMultiplicity.setEnabled(false);
		if (mMultiplicity > 0) {
			mPromoMultiplicity.setEnabled(true);
		} else {
			mPromoMultiplicity.setEnabled(false);
		}
		if (mPromo.PromoType.getId() == 1) {
			mCompensationSwitcher.setVisibility(View.GONE);
			mPromoMultiplicity.setVisibility(View.GONE);
		}
		
		mAdapter = new KitListAdapter();
		mOrder.addOnPromoStateChangedListener(new OnPromotionStateListener(){

			@Override
			public void onPromotionCompensationTypeChanged(int compensationType, Long promotionId) {
				mAdapter.notifyDataSetChanged();
			}

			@Override
			public void onPromotionCounterChanged(Integer counter,Long promotionId) {
				mAdapter.notifyDataSetChanged();
				
			}

			@Override
			public void onPromotionAvailabilityChanged(Boolean availability, Long promotionId) {
				mAdapter.notifyDataSetChanged();
			}

			@Override
			public void onPromotionTypeAvailabilityChanged(Boolean availability, int promotionType) {
				mAdapter.notifyDataSetChanged();
			}
			
		});
		
		mListView = (ExpandableListView) getActivity().findViewById(R.id.promo_action_promo_list_item);
		mListView.setAdapter(mAdapter);
		mListView.setSelector(R.drawable.promo_list_selector);
		mListView.setOnChildClickListener(this);
		mListView.setOnItemLongClickListener(this);
		
		// По умолчанию нет выбранного элемента списка
		mCurrentSelectedGroupItem = -1;
		mCurrentSelectedChildItem = -1;
		
		mNumericPadLayout = (NumericPadLayout) getActivity().findViewById(R.id.num_pad);
		mNumericPadLayout.setOnValueChangedListener(new OnValueChangedListener() {
			@Override
			public void onValueChanged(Object sender, Object value) {
				if (mAvailabilitySwitcher.isChecked() == false) {
					Dialogs.createDialog(mContext, "", mResources.getString(R.string.dialog_promotion_activation), new Command() {
 
						@Override
						public void execute() {
							mAvailabilitySwitcher.setChecked(true);
						}}, new Command() {

						@Override
						public void execute() {
							// Do nothing
						}}).show();
				}
				
				if (mCurrentSelectedGroupItem != -1 && mCurrentSelectedChildItem != -1) {
					// Выделить выбранный элемент в списке
					selectItem(mCurrentSelectedGroupItem, mCurrentSelectedChildItem); 
					mEditing = true;
					mUseRecommended = false;
					
					// Обносить данные в коллекции, данные изменения не были записаны в БД, так как 
					// пользователь еще не подтвердил сохранение данных
					mChildData.get(mCurrentSelectedGroupItem).get(mCurrentSelectedChildItem).put("order_amount", value);
					
					// Обновить элементы в списке после изменения данных в коллекции
					// Для отображение на экране текущего ввода пользователя
					mAdapter.notifyDataSetChanged(); 
				}
			}
		});
		mNumericPadLayout.setOnCommitListener(new OnCommitListener() {
			@Override
			public void onCommit(Object sender, Integer value) {
				if (mCurrentSelectedGroupItem != -1 && mCurrentSelectedChildItem != -1) {
					// Выделить выбранный элемент в списке
					selectItem(mCurrentSelectedGroupItem, mCurrentSelectedChildItem); 
					
					// Сбросить текущее значение
					// Необходимо потому-что 
					mNumericPadLayout.resetValue(0); 
					
					if (value != null) {
						new CommitTask(mCurrentSelectedGroupItem, mCurrentSelectedChildItem, value).execute();
					} else if (mUseRecommended) {
						Integer recommended = (Integer) mChildData.get(mCurrentSelectedGroupItem).get(mCurrentSelectedChildItem).get("recommended");
						if (recommended != null && recommended > 0) {
							new CommitTask(mCurrentSelectedGroupItem, mCurrentSelectedChildItem, recommended).execute();
						}
					}
				}
				
				// Обновить элементы в списке после изменения данных в коллекции
				// Для отображения подтвержденного ввода
				mAdapter.notifyDataSetChanged();
				
				mUseRecommended = true;
				
				// Сбросить выделение текущего элемента
				mCurrentSelectedGroupItem = -1;
				mCurrentSelectedChildItem = -1;
			}
		});
		mProgressBar  = (ProgressBar) getActivity().findViewById(R.id.promo_action_progress);
		PromoListFragment.updateProgress(mContext, mOrder, mProgressBar, mPromotionId);
		setPromotionStateListener(mOrder);

		// Выбрать данные для списков
		new LoadData().execute();
	}
	
	private void readBonusCskuInfo() {
		mPromotionType = mPromo.PromoType.getId();
		switch (mPromotionType) {
			case PromoType.PRICING: {
				
			} break;
			case PromoType.BAG_ORDER: {
				RefPromoBonusCsku refPromoBonusCsku = new RefPromoBonusCsku(mContext);
				ArrayList<SqlCriteria> criterias = new ArrayList<SqlCriteria>();
				criterias.add(new SqlCriteria("ParentExt", mPromoDetails.Id)); 
				refPromoBonusCsku.Select(criterias);
				while(refPromoBonusCsku.Next()) {
					mBonusCsku = refPromoBonusCsku.Current().Csku;
					mBonusCskuQuantity = refPromoBonusCsku.Current().Quantity;
					break;
				}
				refPromoBonusCsku.close();
				refPromoBonusCsku = null;
				
				RefProductItem refProductItem = new RefProductItem(mContext);
				criterias = new ArrayList<SqlCriteria>();
				criterias.add(new SqlCriteria("ParentExt", mBonusCsku.Id));
				refProductItem.Select(criterias);
				while (refProductItem.Next()) {
					mProductItem = refProductItem.Current();
				}
				refProductItem.close();
				refProductItem = null;
				
				mBonusCskuDiscount = mPromoDetails.Discount;
				mBonusCskuPrice = Globals.getPrice(mOrder.TradeRule.PriceType, mProductItem);
			} break;
			case PromoType.BAG_PERIOD: {
				RefPromoBonusCsku refPromoBonusCsku = new RefPromoBonusCsku(mContext);
				ArrayList<SqlCriteria> criterias = new ArrayList<SqlCriteria>();
				criterias.add(new SqlCriteria("ParentExt", mPromoDetails.Id)); 
				refPromoBonusCsku.Select(criterias);
				while(refPromoBonusCsku.Next()) {
					mBonusCsku = refPromoBonusCsku.Current().Csku;
					mBonusCskuQuantity = refPromoBonusCsku.Current().Quantity;
					break;
				}
				refPromoBonusCsku.close();
				refPromoBonusCsku = null;
				
				RefProductItem refProductItem = new RefProductItem(mContext);
				criterias = new ArrayList<SqlCriteria>();
				criterias.add(new SqlCriteria("ParentExt", mBonusCsku.Id));
				refProductItem.Select(criterias);
				while (refProductItem.Next()) {
					mProductItem = refProductItem.Current();
				}
				refProductItem.close();
				refProductItem = null;
				
				mBonusCskuDiscount = mPromoDetails.Discount;
				mBonusCskuPrice = Globals.getPrice(mOrder.TradeRule.PriceType, mProductItem);
			} break;
			case PromoType.BAG_VOLUME: {
				
			} break;
			default: {
				throw new RuntimeException("Unknown promotion type: " + mPromotionType); 
			}
		}
	}
	
	private void updateBonusInfo() {
		if (this.isDetached() 
				|| this.isHidden() 
				|| !this.isVisible()
			) return;
		mPromotionType = mPromo.PromoType.getId();
		switch (mPromotionType) {
			case PromoType.PRICING: {
				SparseArray<Float> totals = mOrder.getPromoTotals(mPromoDetails.Id);
				if (totals.get(DocOrderEntity.PROMO_AMOUNT) > 0) {
					String discount = String.valueOf(mPromoDetails.Discount); 
					String bonus = Money.valueOf(totals.get(DocOrderEntity.PROMO_AMOUNT)).toSymbolString();
					mBonusCskuInfo.setText(String.format("Вы получите скидку %s%% на закупленное количество товара на сумму %s", discount, bonus));
				} else {
					mBonusCskuInfo.setText(getResources().getString(R.string.conditions_of_promotion_are_not_reached));
				}
			} break;
			case PromoType.BAG_ORDER: {
				int multiplicity = mOrder.getPromotionMultiplicity(mPromoDetails.Id);
				if (multiplicity == 0) {
					mBonusCskuInfo.setText(getResources().getString(R.string.conditions_of_promotion_are_not_reached));
				} else {
					int chosenCompensationType = mOrder.getPromoPrefferedCompensationType(mPromoDetails.Id);
					if (chosenCompensationType != 0) { 
						mCompensationType = chosenCompensationType;
					}
					switch (mCompensationType) {
						case KitListAdapter.COMPENSATION_MONETARY: {
							SparseArray<Float> totals = mOrder.getPromoTotals(mPromoDetails.Id);
							float amount = totals.get(DocOrderEntity.PROMO_AMOUNT);
							
							
							String discount = String.valueOf(mBonusCskuDiscount);
							String bonus = Money.valueOf(amount).toSymbolString();
							mBonusCskuInfo.setText(String.format("Вы получите скидку %s%% на закупленное количество товара на сумму %s", discount, bonus));
						} break;
						case KitListAdapter.COMPENSATION_FREE_PRODUCT: {
							String quantity = String.valueOf(mBonusCskuQuantity * multiplicity);
							String description = mProductItem.Descr;
							//String bonus = Money.valueOf(mBonusCskuPrice * mBonusCskuQuantity * multiplicity).toSymbolString();
							SparseArray<Float> totals = mOrder.getPromoTotals(mPromoDetails.Id);
							
							float amount = totals.get(DocOrderEntity.PROMO_AMOUNT);
							String bonus = Money.valueOf(amount).toSymbolString();
							mBonusCskuInfo.setText(String.format("Вы получите бесплатно %s штук %s на сумму %s", quantity, description, bonus)); 
						} break;
						default: {
							mBonusCskuInfo.setVisibility(View.GONE);
							//throw new RuntimeException("Incorrect compensation type for buy and get: " + mCompensationType);
						} 
					}
				}
			} break;
			case PromoType.BAG_PERIOD: {
				int multiplicity = mOrder.getPromotionMultiplicity(mPromoDetails.Id);
				if (multiplicity == 0) {
					mBonusCskuInfo.setText(getResources().getString(R.string.conditions_of_promotion_are_not_reached));
				} else {
					int chosenCompensationType = mOrder.getPromoPrefferedCompensationType(mPromoDetails.Id);
					if (chosenCompensationType != 0) { 
						mCompensationType = chosenCompensationType;
					}
					switch (mCompensationType) {
						case KitListAdapter.COMPENSATION_MONETARY: {
							SparseArray<Float> totals = mOrder.getPromoTotals(mPromoDetails.Id);
							float amount = totals.get(DocOrderEntity.PROMO_AMOUNT);
							
							String discount = String.valueOf(mBonusCskuDiscount);
							String bonus = Money.valueOf(amount).toSymbolString();
							mBonusCskuInfo.setText(String.format("Вы получите скидку %s%% на закупленное количество товара на сумму %s", discount, bonus));
						} break;
						case KitListAdapter.COMPENSATION_FREE_PRODUCT: {
							SparseArray<Float> totals = mOrder.getPromoTotals(mPromoDetails.Id);
							float amount = totals.get(DocOrderEntity.PROMO_AMOUNT);
							
							String discount = String.valueOf(mBonusCskuDiscount);
							String bonus = Money.valueOf(amount).toSymbolString();
							mBonusCskuInfo.setText(String.format("Вы получите скидку %s%% на закупленное количество товара на сумму %s", discount, bonus));
						} break;
						default: {
							mBonusCskuInfo.setVisibility(View.GONE);
						} 
					}
				}
			} break;
			case PromoType.BAG_VOLUME: {
				Promo promo = mOrder.getPromos().getPromo(mPromoDetails.Id);
				if (promo == null) {
					mBonusCskuInfo.setText("Нет данных");
					return;
				}
				
				float fact = promo.getPromoTotalFact();
				float plan = promo.getPromoTarget();
				float bonus = 0f;
				
				int chosenCompensationType = mOrder.getPromoPrefferedCompensationType(mPromoDetails.Id);
				if (chosenCompensationType != 0) { 
					mCompensationType = chosenCompensationType;
				}
				switch (mCompensationType) {
					case KitListAdapter.COMPENSATION_FREE_PRODUCT: {
						if (fact < plan) { 
							bonus = plan * (mPromoDetails.Discount / 100);
							mBonusCskuInfo.setText(String.format("Возможный бонус %s", Money.valueOf(bonus).toSymbolString()));
						} else {
							bonus = fact * (mPromoDetails.Discount / 100);
							mBonusCskuInfo.setText(String.format("Заработанный бонус %s", Money.valueOf(bonus).toSymbolString()));
						}
					} break;
					case KitListAdapter.COMPENSATION_CHOICE: {
						mBonusCskuInfo.setText("Акция отключена");
					} break;
					default: {
						mBonusCskuInfo.setVisibility(View.GONE);
					} 
				}
			} break;
			default: {
				mBonusCskuInfo.setVisibility(View.GONE);
			} 
		}
	}
	
	private RefCskuEntity mBonusCsku;
	private RefProductItemEntity mProductItem;
	private TextView mBonusCskuInfo;	
	private AlertDialog mAlertDialog;
	
	private void setCompensationType() {
		
		mAlertDialog = Dialogs.createSelectFromListDialog(mContext,
			new CompensationType(0), 
			"Выберите тип компенсации",  
			new android.content.DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					firePrefferedCompensationTypeChanged(mPromotionId,  which + 1);
				}
			}, android.R.style.Theme_Holo_Light_Panel);
		mAlertDialog.show();
	}
	
	/**
	 * 
	 * Выделяет элемент в списке 
	 * 
	 * @param group Индекс группы
 	 * @param position Индекс элемента группы
	 */
	private void selectItem(int group, int position) {
		mListView.requestFocusFromTouch();
		mListView.setSelectedChild(group, position, false);
		
		int top = 0;
		View v = mListView.getChildAt(position);
		top = (v == null) ? 0 : v.getTop();
			
		mListView.setTop(top);
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		mCurrentSelectedGroupItem = groupPosition;
		mCurrentSelectedChildItem = childPosition;
		
		mNumericPadLayout.resetValue(0);
		selectItem(groupPosition, childPosition);

		return true;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        long packedPosition = mListView.getExpandableListPosition(arg2);
        if (ExpandableListView.getPackedPositionType(packedPosition) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            mCurrentSelectedGroupItem = ExpandableListView.getPackedPositionGroup(packedPosition);
            mCurrentSelectedChildItem = ExpandableListView.getPackedPositionChild(packedPosition);
            
            showLineInfo(mCurrentSelectedGroupItem, mCurrentSelectedChildItem);
            
            return true;
        }
        
        return false;
	}
	
	private boolean mUseRecommended = true;
	
	private class CommitTask extends AsyncTask<Void, Void, Void> implements OnChangeOrderQuantityListener, OnAmountChangedListener {
		
		private int mValue = 0;
		private int mABC = 0;
		
		private int mUnitLevel = 0;
		private int mGroup = -1;
		private int mPosition = -1;
		private int mUnitFactor = 1;
		
		private Long mProductId;
		private Long mCskuId;
		private Long mBrand;
		
		public CommitTask(int group, int position, int quantity) {
			mValue = quantity;
			mGroup = group;
			mPosition = position;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			if (mPosition == -1) {
				return null;
			}
			mChildData.get(mGroup).get(mPosition).put("order_amount", mValue);
			
			Map<String, Object> item = mChildData.get(mGroup).get(mPosition); 
			mABC = (Integer) item.get("csku_list"); 
			mProductId = (Long) item.get("product_item_id");
			mCskuId =  (Long) item.get("csku_id");
			mBrand = (Long) item.get("brand_id");
			switch (mUnitLevel){
			case 1:
				mUnitFactor = (Integer) item.get("product_item_unit_factor1");
				break;
			case 2:
				mUnitFactor = (Integer) item.get("product_item_unit_factor2");
				break;
			default:
				mUnitFactor = 1;
			}
			return null;
		}
	
		@Override
		protected void onPostExecute(Void param) {
			mOrder.addOnQuantityChangedListener(this);
			mOrder.addOnAmountChangedListener(this);
			mOrder.changeQuantity(mProductId, mValue, mUnitLevel, mABC, mCskuId,mUnitFactor, mBrand);		
			
			updateBonusInfo();
		}

		@Override
		public void onChangeOrderQuantity() {
			mEditing = false;
			mAdapter.notifyDataSetChanged();
			mOrder.removeOnQuantityChangedListener(this);
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void onAmountChanged(GenericEntity sender, float old_value,
				float new_value) {
		
			mAdapter.notifyDataSetChanged();
			mOrder.removeOnAmountChangedListener(this);
			
		} 
	}
	
	private static class PromoItemViewHolder {
		TextView description;
		TextView warehouse;
		TextView history;
		TextView orderAmount;
		TextView price;
		TextView tare;
		
		View promotion;
		
		ImageView initiative;
		ImageView list;
		ImageView historyIndicator;
		ImageView logisticIndicator;
		ImageView picture;
	}
	
	private class KitListAdapter extends BaseExpandableListAdapter {

		private static final int COMPENSATION_FREE_PRODUCT = 1;
		private static final int COMPENSATION_MONETARY = 2;
		private static final int COMPENSATION_CHOICE = 0;
		private static final String FIELD_MUST_HAVE = "must_have";
		//private static final String GCAS_QUOTED = "Q";
		//private static final String GCAS_SELLOUT = "S";
		//private static final String GCAS_REGULAR = "R";
		private static final String FIELD_ID = "id";
		//private static final String FIELD_DESCRIPTION = "description";
		private static final String FIELD_RECOMMENDED = "recommended";
		private static final String FIELD_PRODUCT_ITEM_ID = "product_item_id";
		private static final String FIELD_ORDER_AMOUNT = "order_amount";
		private static final String FIELD_PRODUCT_ITEM_NAME = "product_item_name";
		private static final String FIELD_CSKU_LIST = "csku_list";
		private static final String FIELD_CSKU_STOCK = "csku_stock";
		//private static final String FIELD_LAST_SALE = "last_sale";
		//private static final String FIELD_IS_NOVELTY = "is_novelty";
		private static final String FIELD_PRICE = "price";
		//private static final String FIELD_LOG_STATUS = "log_status";
		//private static final String FIELD_KIT_ID = "kit_id";
		//private static final String FIELD_CSKU_ID = "csku_id";
		private Typeface mTypeface;
		private LayoutInflater mLayoutInflater;
		
		public KitListAdapter() {
			mTypeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/RobotoCondensed-Regular.ttf");
			mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			long id = Long.valueOf((Integer) mChildData.get(groupPosition).get(childPosition).get(FIELD_ID));
			
			RefOutlet refOutlet = new RefOutlet(mContext);
			RefOutletEntity entity = refOutlet.FindById(id);
			refOutlet.close();
			
			return entity;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			PromoItemViewHolder holder;
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(R.layout.expandable_two_line_list_item, null);
				
				holder = new PromoItemViewHolder();
				holder.description = (TextView) convertView.findViewById(R.id.first_line);
				holder.warehouse = (TextView) convertView.findViewById(R.id.second_line);
				holder.description.setTypeface(mTypeface);
				holder.description.setTextSize(20f);
				holder.warehouse.setTypeface(mTypeface);
				
				convertView.setTag(holder);
			} else {
				holder = (PromoItemViewHolder) convertView.getTag();
			}
			
			Map<String, Object> item = mGroupData.get(groupPosition);
			
			Long id = (Long) item.get(FIELD_ID);
			Integer multiplicity = mOrder.getPromotionMultiplicity(mPromotionId, id);
			if (mPromoDetails.ParentExt.PromoType.getId() == 2 || mPromoDetails.ParentExt.PromoType.getId() == 3) {
				String kitDescription = mOrder.getPromoKitDescription(mPromotionId, id);
				holder.description.setText(kitDescription + " ("+((multiplicity==0)?"Не ":"")+"выполнено " + Utils.toTimesString(multiplicity)+")");
				holder.description.setTextColor(mContext.getResources().getColor(android.R.color.holo_blue_light));
				holder.description.setVisibility(View.VISIBLE);
				holder.warehouse.setVisibility(View.GONE);
			} else if (mPromoDetails.ParentExt.PromoType.getId() == 4) {
				Promo promo = mOrder.getPromos().getPromo(mPromoDetails.Id);
				if (promo == null){
					holder.description.setVisibility(View.GONE);
				} else {
					float promoTarget = promo.getPromoTarget();
					float promoFact = promo.getPromoTotalFact();
					int promoIndex = 0;
					if (promoTarget > 0) {
						promoIndex = Math.round(promoFact / promoTarget * 100);
					}
					String firstLine = "Акция выполнена на " + promoIndex + "%";
					String secondLine = "Цель " + Money.valueOf(promoTarget).toSymbolString() + " Продано " + Money.valueOf(promoFact).toSymbolString();
					holder.description.setText(firstLine);
					holder.description.setTextColor(mContext.getResources().getColor(android.R.color.holo_purple));
					holder.description.setVisibility(View.VISIBLE);
					holder.warehouse.setText(secondLine);
					holder.warehouse.setTextColor(mContext.getResources().getColor(android.R.color.holo_purple));
					holder.warehouse.setVisibility(View.VISIBLE);
				}
			} else {
				holder.description.setVisibility(View.GONE);
				holder.warehouse.setVisibility(View.GONE);
			}
			
			return convertView;
		}
		
		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			PromoItemViewHolder holder;
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(R.layout.product_item, null);
				
				holder = new PromoItemViewHolder();
				holder.description = (TextView) convertView.findViewById(R.id.csku_name);
				holder.description.setTypeface(mTypeface);
				holder.warehouse = (TextView) convertView.findViewById(R.id.csku_store_info);
				holder.warehouse.setTypeface(mTypeface);
				holder.history = (TextView) convertView.findViewById(R.id.csku_details);
				holder.history.setTypeface(mTypeface);
				holder.orderAmount = (TextView) convertView.findViewById(R.id.pick_list_order2);
				holder.orderAmount.setTypeface(mTypeface);
				holder.price = (TextView) convertView.findViewById(R.id.csku_price);
				holder.price.setTypeface(mTypeface);
				holder.tare = (TextView) convertView.findViewById(R.id.csku_case);
				holder.tare.setTypeface(mTypeface);
				
				holder.promotion = convertView.findViewById(R.id.promotion_indicator_group);
				
				holder.picture = (ImageView) convertView.findViewById(R.id.picture_empty);
				holder.initiative = (ImageView) convertView.findViewById(R.id.csku_ini_type);
				holder.list = (ImageView) convertView.findViewById(R.id.csku_list_type);
				holder.historyIndicator = (ImageView) convertView.findViewById(R.id.csku_qualifacation_status);
				holder.logisticIndicator = (ImageView) convertView.findViewById(R.id.csku_log_status);
				
				convertView.setTag(holder);
			} else {
				holder = (PromoItemViewHolder) convertView.getTag();
			}
			
			mBonusCskuInfo.setVisibility(View.VISIBLE);
			Map<String, Object> childItem = mChildData.get(groupPosition).get(childPosition);
			
			updateBonusInfo();
			// promotion
			holder.promotion.setVisibility(View.GONE); 		
			// tare
			holder.tare.setVisibility(View.INVISIBLE); 
			// picture
			holder.picture.setVisibility(View.INVISIBLE); 
			// initiative
			holder.initiative.setVisibility(View.INVISIBLE); 
			// log status
			holder.logisticIndicator.setVisibility(View.INVISIBLE);
			// qualification status
			holder.historyIndicator.setVisibility(View.INVISIBLE);
			// history
			holder.history.setVisibility(View.INVISIBLE);
			// description
			String productName = (String) childItem.get(FIELD_PRODUCT_ITEM_NAME);
			if ((Boolean) childItem.get(FIELD_MUST_HAVE)) {
				SpannableString content = new SpannableString(productName);
				content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
				holder.description.setText(content);
			} else {
				holder.description.setText(productName);
			}
			// price
			String priceText = Money.valueOf((Float) childItem.get(FIELD_PRICE)).toSymbolString(new Locale("ru", "RU"));  
			holder.price.setText(priceText);
			//warehouse
			Integer stock = (Integer) childItem.get(FIELD_CSKU_STOCK);
			if (stock > 0) {	
				holder.description.setTextColor(mResources.getColor(R.color.black));
				holder.warehouse.setTextColor(mResources.getColor(R.color.gray));
			} else {
				holder.description.setTextColor(mResources.getColor(R.color.red));
				holder.warehouse.setTextColor(mResources.getColor(R.color.red));
			}
			String warehouseText = String.format("ост.: %d", stock);
			holder.warehouse.setText(warehouseText);
			// list
			switch ((Integer) childItem.get(FIELD_CSKU_LIST)) {
				case 1: {
					holder.list.setVisibility(View.VISIBLE);
					holder.list.setImageDrawable(getResources().getDrawable(R.drawable.list_a_black));
				} break;
				case 2: { 
					holder.list.setVisibility(View.VISIBLE);
					holder.list.setImageDrawable(getResources().getDrawable(R.drawable.list_b_black));
				} break;
				case 3: {
					holder.list.setVisibility(View.VISIBLE);
					holder.list.setImageDrawable(getResources().getDrawable(R.drawable.list_c_black));
				} break;
				default: {
					holder.list.setVisibility(View.INVISIBLE);
				}
			}
			
			int order = 0;
			if (mEditing) {
				if (childItem.get(FIELD_ORDER_AMOUNT) != null)
					order = (Integer) childItem.get(FIELD_ORDER_AMOUNT);
			} else { 
				if (childItem.get(FIELD_PRODUCT_ITEM_ID) != null) {
					DocOrderLineEntity line = mOrder.getOrderLine((Long) childItem.get(FIELD_PRODUCT_ITEM_ID));
					if (line != null) {
						order = line.Quantity;
						switch (line.UnitLevel){
							case 1: {
								order *= line.ProductItem.UnitFactor1;
							} break;
							case 2: {
								order *= line.ProductItem.UnitFactor2;
							} break;
							default: {} break;
						}
					}
				}
			}
			
			if (order > 0) {
				holder.orderAmount.setText(order + " шт.");
				holder.orderAmount.setTextColor(mContext.getResources().getColor(R.color.blue));
				holder.orderAmount.setVisibility(View.VISIBLE);
			} else {
				Integer recommended = (Integer) childItem.get(FIELD_RECOMMENDED);
				if (recommended != null && recommended > 0 && mPromoDetails.ParentExt.PromoType.getId() == 2) {
					holder.orderAmount.setVisibility(View.VISIBLE);
					holder.orderAmount.setTextColor(mContext.getResources().getColor(R.color.light_grey));
					holder.orderAmount.setText(recommended + " шт."); 

				}
				else {
					holder.orderAmount.setVisibility(View.INVISIBLE);
				}
			}
			
			return convertView;
		}
		
		@Override
		public int getChildrenCount(int groupPosition) {
			return mChildData.get(groupPosition).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return null;
		}

		@Override
		public int getGroupCount() {
			return mGroupData.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
		
	}
	
	private class LoadData extends AsyncTask<Object, Void, Void> {

		private static final String FIELD_DESCRIPTION = "description";

		private static final String FIELD_ID = "id";

		private ProgressDialog mProgressDialog;

		private String mQuery1 = 
			 " select distinct "
				+ " ra.Id as assortment_id, "
				+ " i.id as product_item_id, "
				+ " p.Price * (1 + (1 - pt.IncludeVAT) * i.VAT), "
				+ " coalesce(s.ABC, 0), "
				+ " coalesce(s.IsPriority, 0), "
				+ " coalesce(s.IsDrive, 0), "
				+ " coalesce(h.Quantity, 0), "
				+ " coalesce(w.Quantity, 0), "
				+ " c.OrderKey as csku_order_key, "
				+ " c.Id as csku_id, "
				+ " h.LastSale, "
				+ " h.Novetly, "
				+ " coalesce(h.Turnover, 0), "
				+ " coalesce(h.Cyclicity, 0), "
				+ " i.Descr as product_item_description, "
				+ " c.Descr as csku_description, "
				+ " i.UnitFactor1 as product_item_unit_factor1, "
				+ " i.UnitFactor2 as product_item_unit_factor2, "
				+ " i.VAT as product_item_vat, "
				+ " g.Descr as gcas_state_description, "
				+ " i.ProfitDescription as product_item_profit_description, "
				+ " c.ExtCode, "
				+ " coalesce(s.IsNextPriority, 0), "
				+ " coalesce(s.IsNextDrive, 0), "
				+ " c.Brand as brand_id "
			+ " from "
				+ " RefPromo as promo "
				+ " inner join RefPromoBrand as brand on "
					+ " brand.ParentExt = promo.Id "
				+ " inner join RefCsku c on "
					+ " brand.Brand = c.Brand "
				+ " inner join RefProductItem i on "
					+ " i.ParentExt = c.Id "
				+ " inner join RegAssortment ra on "
					+ " ra.ProductItem = i.Id "
				+ " inner join RefGcasState g on "
					+ " i.GcasState = g.Id "
				+ " inner join RegPrice as p on "
					+ " i.Id = p.ProductItem "
				+ " left join RegCskuState as s on "
					+ " c.Id = s.Csku "
					+ " and s.StoreChannel = ? "
				+ " left join RegOrderHelper as h on "
					+ " c.Id = h.Csku "
					+ " and h.Outlet = ? "
					+ " and h.CurrentPeriod = 1 "
				+ " left join RegStock as w on "
					+ " ra.ProductItem = w.ProductItem "
					+ " and w.Warehouse = ? "
				+ " inner join RefPriceType as pt on "
					+ " p.PriceType = pt.Id "
			+ " where "
				+ " ra.Assortment = ? "
				+ " and p.PriceType = ? "
				+ " and not NULLIF(p.Price, 0) is null "
				+ " and promo.Id = ? "
				+ " and i.IsMark = 0 "
			+ " order by "
				+ " c.OrderKey asc, "
				+ " c.Descr asc, "
				+ " coalesce(s.ABC, 4) asc, "
				+ " coalesce(s.IsPriority, 0) desc, " 
				+ " coalesce(s.IsDrive, 0) desc "; 
		
		private String mQuery2 = 
				" select " +
					" kit.Id, " +
					" kit.Descr, " +  
					" kit.MinOrderQuantity, " +
					" kit.MinCskuQuantity " +
				" from " +
					" RefPromoKit as kit " +   
				" where " + 
					" kit.ParentExt = ? " +
				" order by " +
					" kit.Descr asc ";
		
		
		
		private String mQuery = 
				" select distinct " 
						+ " ra.Id as assortment_id, "
						+ " i.id as product_item_id, "
						+ " p.Price * (1 + (1 - pt.IncludeVAT) * i.VAT), "
						+ " coalesce(s.ABC, 0), "
						+ " coalesce(s.IsPriority, 0), "
						+ " coalesce(s.IsDrive, 0), "
						+ " coalesce(h.Quantity, 0), "
						+ " coalesce(w.Quantity, 0), "
						+ " c.OrderKey as csku_order_key, "
						+ " c.Id as csku_id, "
						+ " h.LastSale, "
						+ " h.Novetly, "
						+ " coalesce(h.Turnover, 0), "
						+ " coalesce(h.Cyclicity, 0), "
						+ " i.Descr as product_item_description, "
						+ " c.Descr as csku_description, "
						+ " i.UnitFactor1 as product_item_unit_factor1, "
						+ " i.UnitFactor2 as product_item_unit_factor2, "
						+ " i.VAT as product_item_vat, "
						+ " g.Descr as gcas_state_description, "
						+ " i.ProfitDescription as product_item_profit_description, "
						+ " c.ExtCode, "
						+ " coalesce(s.IsNextPriority, 0), "
						+ " coalesce(s.IsNextDrive, 0), "
						+ " kitl.MustHave, "
						+ " kit.Descr, " 
						+ " kit.Id as kit_id, "
						+ " c.Brand as brand_id "
					+ " from "
						+ " RefPromoKitList as kitl "
						+ " inner join RefPromoKit as kit on "
							+ " kitl.ParentExt = kit.Id " 
						+ " inner join RefCsku c on "
							+ " kitl.Csku = c.Id "
						+ " inner join RefProductItem i on "
							+ " i.ParentExt = c.Id " 
						+ " inner join RegAssortment ra on "
							+ " ra.ProductItem = i.Id "
						+ " inner join RefGcasState g on "
							+ " i.GcasState = g.Id "
						+ " inner join RegPrice as p on "
							+ " i.Id = p.ProductItem "
						+ " left join RegCskuState as s on "
							+ " c.Id = s.Csku "
							+ " and s.StoreChannel = ? "
						+ " left join RegOrderHelper as h on "
							+ " c.Id = h.Csku "
							+ " and h.Outlet = ? "
							+ " and h.CurrentPeriod = 1 "
						+ " left join RegStock as w on "
							+ " ra.ProductItem = w.ProductItem "
							+ " and w.Warehouse = ? "
						+ " inner join RefPriceType as pt on "
							+ " p.PriceType = pt.Id "
					+ " where "
						+ " ra.Assortment = ? " 
						+ " and p.PriceType = ? "
						+ " and not NULLIF(p.Price, 0) is null "
						+ " and kit.ParentExt = ? "
						+ " and kit.Id = ? "
						+ " and i.IsMark = 0 "
					+ " order by "
						+ " kit.Descr asc, "
						+ " kitl.MustHave desc, "
						+ " h.Quantity desc, "
						+ " coalesce(s.ABC, 4) asc, "
						+ " coalesce(s.IsPriority, 0) desc, " 
						+ " coalesce(s.IsDrive, 0) desc "; 
		
		private Object[] mParameters;
		
		public LoadData() {
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setMessage(mContext.getResources().getString(R.string.data_loading));
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.setCancelable(false);
			
			/* Установить параметры запроса выборки данных */
			long outletId = 0;
			long assortmentId = 0;
			long channelId = 0;
			long warehouseId = 0;
			long priceTypeId = 0;
			
			if (mOrder.Outlet != null) {
				outletId = mOrder.Outlet.Id;
				if (mOrder.Outlet.Channel != null) {
					channelId = mOrder.Outlet.Channel.Id;
				}
			}
			if (mOrder.Warehouse != null) {
				warehouseId = mOrder.Warehouse.Id;
			}
			if (mOrder.TradeRule != null) {
				if (mOrder.TradeRule.Assortment != null) {
					assortmentId = mOrder.TradeRule.Assortment.Id;
				} else {
					if (mOrder.Employee != null) {
						if (mOrder.Employee.Assortment != null) {
							assortmentId = mOrder.Employee.Assortment.Id;
						}
					}
				}			
				if (mOrder.TradeRule.PriceType != null) {
					priceTypeId = mOrder.TradeRule.PriceType.Id; 
				}
			}
			
			mParameters = new Object[] {
				channelId, // Канал продаж
				outletId,  // Идентификатор физической торговой точки
				warehouseId, // Идентификатор склада 
				assortmentId, // Идентификатор ассортимента 
				priceTypeId, // Тип цены
				mPromoDetails.Id, // Идентификатор деталей промо-акции
				0 // Номер пакета в промо-акции УСТАНАВЛИВАЕТСЯ ПОЗЖЕ на основании данных запроса перебирающего наборы
			};
		}
		
		@Override
		protected void onPreExecute() {
			mProgressDialog.show();
		}
		
		private void quiery1() {
			// Очистить данные в источнике
			mGroupData.clear();
			mChildData.clear();
			
			// Выбрать наборы для текущей промо-акции
			UltraliteCursor cursor = new UltraliteCursor(mContext, mQuery2);
			cursor.setParametersAndExecute(mPromoDetails.Id);
			
			while (cursor.moveToNext()) {			
				// Установить номер набора для выбора CSKU только из него
				mParameters[6] = cursor.getInt(1);
				
				// Получить минимальное число уникальных CSKU в пакете, 
				// которые необходимо заказать в соответствии с механикой акции
				int counter = cursor.getInt(4);
				 
				// Запросить содержимое набора
				UltraliteCursor cursor2 = new UltraliteCursor(mContext, mQuery);
				cursor2.setParametersAndExecute(mParameters);
				
				//Log.d(SFSActivity.LOG_TAG,"Найдено "+cursor2.getCount()+" элементов для пакета "+cursor.getInt(1));
				
				// 
				Set<Long> cskus = new CopyOnWriteArraySet<Long>();
				
				List<Map<String, Object>> childDataItem = new ArrayList<Map<String, Object>>();
				while (cursor2.moveToNext()) {
					Map<String, Object> item = new HashMap<String, Object>();
					item.put("product_item_id", cursor2.getLong(2));
					item.put("product_item_name", cursor2.getString(15));
					item.put("csku_list", cursor2.getInt(4));
					item.put("csku_stock", cursor2.getInt(8));
					DocOrderLineEntity line = mOrder.getOrderLine(cursor2.getLong(2));
					item.put("order_amount", (line == null ? 0 : line.Quantity));
					item.put("is_novelty", cursor2.getBoolean(12));
					item.put("last_sale", cursor2.getDate(11));
					item.put("price", cursor2.getFloat(3));
					item.put("log_status", cursor2.getString(20));
					item.put("csku_id", cursor2.getLong(10));
					item.put("must_have", cursor2.getBoolean(25));
					item.put("kit_descr", cursor2.getString(26));
					item.put("kit_id", cursor2.getLong(27));
					item.put("brand_id", cursor2.getLong(28));
					
					int stock = cursor2.getInt(8); // Получить остаток на складе
					int recommended = 0; // Рекомендованное кол-во в заказе
					
					if (counter > 0 && stock >= cursor.getInt(3) ) {
						Long cskuId = cursor2.getLong(10);
						if (cskus.add(cskuId)) {
							recommended = cursor.getInt(3);
							counter--;
						}
					}
					item.put("recommended", recommended);
					
					childDataItem.add(item);
				}
				
				mChildData.add(childDataItem);
				
				cursor2.close();
				cursor2 = null;
				
				Map<String, Object> group = new HashMap<String, Object>();
				group.put(FIELD_ID, cursor.getLong(1));
				group.put(FIELD_DESCRIPTION, cursor.getString(2));
				mGroupData.add(group); 
			}
			
			cursor.close();
			cursor = null;
		}
		
		private void quiery2() {
			// Очистить данные в источнике
			mGroupData.clear();
			mChildData.clear();
			
			// Установить номер набора для выбора CSKU только из него
			//mParameters[5] = mPromo.Id;

			Object[] parameters = new Object[6];
			parameters[0] = mParameters[0];
			parameters[1] = mParameters[1];
			parameters[2] = mParameters[2];
			parameters[3] = mParameters[3];
			parameters[4] = mParameters[4];
			parameters[5] = mPromo.Id;
			
			
			// Запросить содержимое набора
			UltraliteCursor cursor2 = new UltraliteCursor(mContext, mQuery1);
			cursor2.setParametersAndExecute(parameters);

			List<Map<String, Object>> childDataItem = new ArrayList<Map<String, Object>>();
			while (cursor2.moveToNext()) {
				Map<String, Object> item = new HashMap<String, Object>();
				item.put("product_item_id", cursor2.getLong(2));
				item.put("product_item_name", cursor2.getString(15));
				item.put("csku_list", cursor2.getInt(4));
				item.put("csku_stock", cursor2.getInt(8));
				DocOrderLineEntity line = mOrder.getOrderLine(cursor2.getLong(2));
				item.put("order_amount", (line == null ? 0 : line.Quantity));
				item.put("is_novelty", cursor2.getBoolean(12));
				item.put("last_sale", cursor2.getDate(11));
				item.put("price", cursor2.getFloat(3));
				item.put("log_status", cursor2.getString(20));
				item.put("csku_id", cursor2.getLong(10));
				item.put("must_have", false); //TODO Это поле нужно брать из Promo
				item.put("brand_id", cursor2.getLong(25));
				item.put("recommended", 0);
				
				childDataItem.add(item);
			}
			
			mChildData.add(childDataItem);
			
			cursor2.close();
			cursor2 = null;
			
			Map<String, Object> group = new HashMap<String, Object>();
			group.put(FIELD_ID, 0l);
			group.put(FIELD_DESCRIPTION, "");
			mGroupData.add(group); 
		}
		
		@Override
		protected Void doInBackground(Object... params) {
			mPromotionType = mPromo.PromoType.getId();
			switch (mPromotionType) {
				case PromoType.PRICING: {
					quiery1();
				} break;
				case PromoType.BAG_ORDER: {
					quiery1();
				} break;
				case PromoType.BAG_PERIOD: {
					quiery1();
				} break;
				case PromoType.BAG_VOLUME: {
					quiery2();
				} break;
				default: {
					throw new RuntimeException("Unknown promotion type: " + mCompensationType);
				} 
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// 
			mAdapter.notifyDataSetChanged();
			
			// Открыть все наборы
			for (int i = 0; i < mGroupData.size(); i++) {
				mListView.expandGroup(i);
			}
			
			// Скрыть диалог
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}

	public void setPromotionStateListener(OnPromotionStateListener listener) {
		mPromotionStateListener = listener;
	}
	protected void firePrefferedCompensationTypeChanged(long promoId,int compensationType){
		if (mPromotionStateListener!=null){
			mPromotionStateListener.onPromotionCompensationTypeChanged(compensationType, promoId);
		}
	}
	public void showLineInfo(int group, int position) {	
		mProductId = (Long) mChildData.get(group).get(position).get("product_item_id");
		new OpenProductDetails().execute();
	}
	
	private Long mProductId;

	private class OpenProductDetails extends AsyncTask<Void, Void, Void> {
		
		private ProgressDialog mProgressDialog;
		private AlertDialog mAlertDialog;
		
		private DocOrderLineEntity mCurrentLine;
		private DocOrderLineInfoView mDocOrderLineInfoView;
		
		private RefProductItemEntity mRefProductItem;
		
		private Float mCurrentBaseprice;
		private Float mCurrentPrice;
		private Float mCurrentDiscount;		
		
		public OpenProductDetails() {
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setMessage(mContext.getResources().getString(R.string.data_loading));
			mProgressDialog.setIndeterminate(true); 
			mProgressDialog.setCancelable(false);
		}
		
		@Override
		protected void onPreExecute() {
			mProgressDialog.show();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			
			DocOrderLine lines = (DocOrderLine) mOrder.getLines(mContext);
			if (lines != null) {
				mCurrentLine = lines.getLine(mContext, Long.valueOf(String.valueOf(mProductId)));
			}
			mCurrentBaseprice = DocOrderEntity.getPrice(mOrder, Long.valueOf(String.valueOf(mProductId)));
			mCurrentPrice = DocOrderEntity.getAmountLine(mOrder, mOrder.AmountBase, mOrder.Delay, mProductId, 0l, "R", mCurrentBaseprice, 0f);
			
			if (mCurrentLine != null) {
				if (mCurrentLine.Quantity > 0) {
					mCurrentPrice = mCurrentLine.Amount / mCurrentLine.Quantity;
				}
			}
			if (mCurrentBaseprice != null && mCurrentPrice != null && mCurrentBaseprice != 0) {
				mCurrentDiscount = mCurrentPrice / mCurrentBaseprice * 100f - 100f;
			}	
			
			Log.d(MainActivity.LOG_TAG,"Скидка: " + mCurrentDiscount);
			
			RefProductItem ref = new RefProductItem(mContext);
			mRefProductItem = (RefProductItemEntity) ref.FindById(mProductId);
			ref.close();
			if (mRefProductItem == null) {
				return null;
			}

			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
			
			mDocOrderLineInfoView = new DocOrderLineInfoView(mContext, mRefProductItem, mOrder, mCurrentLine, mCurrentBaseprice, mCurrentPrice, mCurrentDiscount);
			mAlertDialog = Dialogs.createDialog(mContext, "", "", mDocOrderLineInfoView.inflate(), null, null, null);
			mAlertDialog.setOnDismissListener(new OnDismissListener() {
				public void onDismiss(DialogInterface dialog) {
					mDocOrderLineInfoView.dispose();
					mDocOrderLineInfoView = null;
				}
			});
			mAlertDialog.show();
		}
	}
	
	@Override
	public void onPromotionCompensationTypeChanged(int compensationType, Long promotionId) {
		if (mOrder.getPromoCompensationType(mPromotionId) == 0 
				&& mOrder.getPromoPrefferedCompensationType(mPromotionId) > 0) {
			mCompensationSwitcher.setChecked(compensationType == 1);
			mCompensationSwitcher.setVisibility(View.VISIBLE);
		}
		else {
			mCompensationSwitcher.setVisibility(View.GONE);
		}
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onPromotionAvailabilityChanged(Boolean availability, Long promotionId) {
		if (mOrder.getPromoCompensationType(mPromoDetails.Id) == 0 && mOrder.getPromoPrefferedCompensationType(mPromoDetails.Id) == 0) {
			if (mPromo.PromoType.getId() != 1) {
				setCompensationType();	
			}
		}
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onPromotionCounterChanged(Integer counter, Long promotionId) {
		mPromoMultiplicity.setText("x" + counter); 
		mAdapter.notifyDataSetChanged();
		mMultiplicity = counter;
		PromoListFragment.updateProgress(mContext, mOrder, mProgressBar, mPromotionId);
		if (counter > 0) {
			mPromoMultiplicity.setEnabled(true);
		} else {
			mPromoMultiplicity.setEnabled(false);
		}
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Log.d("","KeyEvent.KEYCODE_BACK");
			
			return true;
		}
		Log.d("","KeyEvent.KEYCODE_BACK2");
		return false;
	}

	private OnFormExitListener mExitFormListener; 
	
	@Override
	public Boolean requestExit(OnFormExitListener sender) {
		mExitFormListener = sender;
		if ((mOrder.getPromotionMultiplicity(mPromoDetails.Id) > 0) && (!mOrder.getPromoAvailabilty(mPromoDetails.Id))) {
			Dialogs.createDialog(mContext, "", "Вы не включили промо-акцию. Продолжить выход?", new Command() {
				@Override
				public void execute() {
					confirmExit();
				}
			}, null, Command.NO_OP,0).show();
			
			return false;
		} else { 
			return true;
		}
	}
	
	@Override
	public void confirmExit() {
		if (mExitFormListener != null) {
			mExitFormListener.confirmExit();
		}
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		mOrder.removeOnPromoStateChangedListener(this); 
		mOrder.removeOnAmountChangedListener(this);
		mOrder.removeOnAmountUpdatedListener(this);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void onAmountChanged(GenericEntity sender, float old_value, float new_value) {
		updateBonusInfo();
		mAdapter.notifyDataSetChanged();
		Log.d(MainActivity.LOG_TAG,"Промо 4 Сумма обновлена");
	}

	@Override
	public void onAmountUpdated() {
		updateBonusInfo();
		
	}

	/* (non-Javadoc)
	 * @see ru.magnat.sfs.newpromo.OnPromotionStateListener#onPromotionTypeAvailabilityChanged(java.lang.Boolean, int)
	 */
	@Override
	public void onPromotionTypeAvailabilityChanged(Boolean availability, int promotionType) {
		
	}

}
