package ru.magnat.sfs.ui.android.doc.creditrequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import ru.magnat.sfs.android.Command;
import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.NumberPickerDialog;
import ru.magnat.sfs.android.NumberPickerDialog.OnNumberSetListener;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.creditrequest.DocCreditRequestEntity;
import ru.magnat.sfs.bom.creditrequest.DocCreditRequestJournal;
import ru.magnat.sfs.bom.ref.contractor.RefContractor;
import ru.magnat.sfs.bom.ref.contractor.RefContractorEntity;
import ru.magnat.sfs.bom.ref.generic.RefGeneric.HierarchyMode;
import ru.magnat.sfs.bom.ref.paymenttype.RefPaymentType;
import ru.magnat.sfs.bom.ref.paymenttype.RefPaymentTypeEntity;
import ru.magnat.sfs.bom.reg.paymenttype.RegPaymentType;
import ru.magnat.sfs.bom.reg.paymenttype.RegPaymentTypeEntity;
import ru.magnat.sfs.ui.android.GenericEntityView;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class DocRequestOnChangeCreditView extends GenericEntityView<DocCreditRequestJournal, DocCreditRequestEntity> implements OnItemClickListener {
	
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	
	private DocCreditRequestEntity mDocCreditRequestEntity;
	private RefContractor mRefContractor;
	private RefPaymentType mRefPaymentType;
	private RegPaymentType mRegPaymentType;
	
	private Integer _textHeight = 300;
	private Integer _textWidth = 350;
	private EditText _editNumber;
	private String[] mDays = new String[] {
		"По факту", 
		"5 банковских дней", 
		"10 банковских дней", 
		"15 банковских дней", 
		"20 банковских дней", 
		"25 банковских дней", 
		"30 банковских дней", 
		"35 банковских дней", 
		"40 банковских дней", 
		"45 банковских дней"
	};

	private ListView mListView;
	private SimpleAdapter mSimpleAdapter;
	private TextView mTitleView;
	private EditText mCommentEditText;
	
	public DocRequestOnChangeCreditView(Context context, DocCreditRequestJournal journal, DocCreditRequestEntity entity) {
		super(context, journal, entity);
		
		mContext = context;
		mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		mDocCreditRequestEntity = entity;
	}

	@Override
	public SfsContentView inflate() {
		mLayoutInflater.inflate(R.layout.doc_credit_request_tab, this);
		
		mSimpleAdapter = new SimpleAdapter(mContext, mDocCreditRequestEntity.getProperties(), android.R.layout.simple_list_item_2, new String[] { "value", "property" }, new int[] { android.R.id.text1, android.R.id.text2 });
		
		mListView = (ListView) findViewById(R.id.list);
		mListView.setAdapter(mSimpleAdapter);
		mListView.setOnItemClickListener(this);
		
		mTitleView = (TextView) findViewById(R.id.caption);
		mTitleView.setText(mDocCreditRequestEntity.toString());
		
		if (mDocCreditRequestEntity.Contractor == null) {
			changeContractor();
		}
	
		return this;
	}

	protected void changeContractor() {
		if (isReadOnly()) return;

		mRefContractor = new RefContractor(mContext);
		mRefContractor.Select(false, false, HierarchyMode.OnlyEntity);
		
		Dialogs.createSelectFromListDialog(mRefContractor, "Выбор контрагента",	new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				mDocCreditRequestEntity.Contractor = (RefContractorEntity) mRefContractor.getItem(which);
				
				mRegPaymentType = new RegPaymentType(mContext);
				GenericEntity<?> ds[] = { mDocCreditRequestEntity.Contractor, mDocCreditRequestEntity.Employee, null };
				if (mRegPaymentType.Select(ds)) {
					for (RegPaymentTypeEntity entity=(RegPaymentTypeEntity) mRegPaymentType.next();entity!=null;) {
						mDocCreditRequestEntity.CreditType = entity.PaymentType;
						mDocCreditRequestEntity.CreditDepth = entity.Delay;
						mDocCreditRequestEntity.CreditLimit = entity.Limit;
						break;
					}
				}
				mRegPaymentType.close();
				
				mSimpleAdapter.notifyDataSetChanged();
			}
		}).show();
	}
	
	protected void changeCreditType() {
		if (isReadOnly()) return;

		mRefPaymentType = new RefPaymentType(mContext);
		mRefPaymentType.Select(false, false, HierarchyMode.OnlyEntity);
		
		Dialogs.createSelectFromListDialog(mRefPaymentType, "Выбор типа кредита", new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				mDocCreditRequestEntity.CreditType = (RefPaymentTypeEntity) mRefPaymentType.getItem(which);
				mRefPaymentType.close();
				mSimpleAdapter.notifyDataSetChanged();
			}
		}).show();
	}
	
	// глубина кредита
	protected void changeCreditDepth() {
		if (isReadOnly()) return;

		new NumberPickerDialog(mContext, 0, new OnNumberSetListener() {
			@Override
			public void onNumberSet(NumberPicker view, int value) {
				mDocCreditRequestEntity.CreditDepth = value * 5;
				mSimpleAdapter.notifyDataSetChanged();			
			}
		}, mDocCreditRequestEntity.CreditDepth / 5, mDays, "Отмена", "Ok", "Глубина кредита").show();
	}

	// лимит кредита
	protected void changeCreditLimit() {
		if (isReadOnly()) return;
		
		_editNumber = new EditText(mContext);
		_editNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
		Dialogs.createDialog("Лимит кредита", "", _editNumber, null,
				new Command() {
					public void execute() {
						String str = _editNumber.getText().toString();
						if (str.isEmpty()) str = "0";
						mDocCreditRequestEntity.CreditLimit = (float) Integer.parseInt(str);
						mSimpleAdapter.notifyDataSetChanged();
					}
				}, Command.NO_OP).show();
	}

	protected void changeCreditComment() {
		if (isReadOnly()) return;
		
		mCommentEditText = new EditText(mContext);
		mCommentEditText.setHeight(_textHeight);
		mCommentEditText.setMaxHeight(_textHeight);
		mCommentEditText.setWidth(_textWidth);
		mCommentEditText.setMaxWidth(_textWidth);
		mCommentEditText.setGravity(0);

		Dialogs.createDialog("Комментарий", "", mCommentEditText, null, new Command() {
			public void execute() {
				mDocCreditRequestEntity.CreditComment = mCommentEditText.getText().toString();
				mSimpleAdapter.notifyDataSetChanged();
			}
		}, Command.NO_OP).show();
	}

	private Boolean isReadOnly() {
		Boolean result = mDocCreditRequestEntity.getReadOnly();
		if (result) {
			Dialogs.createDialog("SFS", "Нельзя исправлять отправленный запрос", Command.NO_OP).show();
		}
		
		return result;
	}

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
			method = getClass().getDeclaredMethod(selectMethod);
			method.setAccessible(true);
			method.invoke(this);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void fill() {

	}

	@Override
	protected Boolean onRemove() {
		_catalog.save();
		
		return true;
	}

}
