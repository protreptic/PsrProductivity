package ru.magnat.sfs.ui.android.doc.order;

import java.io.Closeable;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import ru.magnat.sfs.android.Command;
import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.OnBackPressedListener;
import ru.magnat.sfs.android.OnDismissCloseableListener;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.android.MainActivity.SyncType;
import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.bom.order.DocOrderEntity;
import ru.magnat.sfs.bom.order.DocOrderJournal;
import ru.magnat.sfs.bom.query.order.QueryGetOrders;
import ru.magnat.sfs.bom.query.order.QueryGetOrdersEntity;
import ru.magnat.sfs.bom.task.visit.TaskVisitEntity;
import ru.magnat.sfs.money.Money;
import ru.magnat.sfs.promo.BeginningOrEndingPromotionsView;
import ru.magnat.sfs.ui.android.GenericListItemView;
import ru.magnat.sfs.ui.android.GenericListView;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.SfsListType;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class QueryOrderListView extends GenericListView<QueryGetOrders, QueryGetOrdersEntity, TaskVisitEntity> { 
		
	public QueryOrderListView(Context context) {
		super(context, null, null);
		
		init();
	}
	
	public QueryOrderListView(Context context, TaskVisitEntity owner) {
		super(context, null, owner);
		
		init();
	}
	
	private void init() {
		сatalogInflate(R.layout.doc_order_list_view, "");
	}
	
	@Override
	protected boolean ItemLongClick(QueryGetOrdersEntity item) {
		DocOrderJournal docOrderJournal = new DocOrderJournal(getContext());
		DocOrderEntity order = docOrderJournal.FindById(item.Id);
		docOrderJournal.close();
		docOrderJournal = null;
		
		Dialogs.createDialog("Удовлетворенность заказа", "", new OrderCfrInfoView(getContext(), order).inflate(), null, null, null).show();
		return true;
	}
	
	@Override
	public SfsContentView inflate() {
		return this;
	}
	
	@Override
	protected void requery() {
		if (_catalog != null) {
			_catalog.close();
		}
		if (_owner != null) {
			_catalog = new QueryGetOrders(getContext(), _owner.Outlet.Id, null);
		} else {
			_catalog = new QueryGetOrders(getContext(), null, (mDate != null ? mDate : new DateTime(mCalendar.getTimeInMillis())));
		}
		_catalog.SetListType(SfsListType.EXTENDED_LIST);
		_catalog.Select();
		if (lv != null){
			lv.setAdapter(_catalog);
		}
		_catalog.notifyDataSetChanged();
	}
	
	private void createOrder(TaskVisitEntity visit) {
		DocOrderJournal docs = new DocOrderJournal(getContext());
		docs.NewEntity();
		DocOrderEntity doc = docs.Current();
	
		if (visit == null) {
			Dialogs.MessageBox("Заказы можно создавать только в рамках визита");
		} else {
			doc.setDefaults(getContext(), visit);
			if (docs.save()) {
				doc = (DocOrderEntity) docs.Current();
			} else {
				Dialogs.MessageBox("Не удалось записать созданный заказ");
			}
		}

		OpenEntity(docs, doc);
	}
	
	protected void OpenEntity(DocOrderJournal journal, DocOrderEntity entity) {
		if (entity == null) {
			Dialogs.createDialog("", "Заказ не выбран", Command.NO_OP);
			return;
		}
		SfsContentView v = journal.GetEditView(entity);
		journal.close();
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
	
	@Override
	protected void OpenEntity(QueryGetOrdersEntity entity) {
		DocOrderJournal docs = new DocOrderJournal(getContext());
		DocOrderEntity order = docs.FindById(entity.Id);
		
		OpenEntity(docs, order);
	}
	
	private Command _sync = new Command() {
		public void execute() {
			MainActivity.getInstance().synchronize(SyncType.REGULAR); 
		}
	};
	
	@Override
	protected void createEntity() {
		 offerSync();
	}
	
	private class OnPromoViewDataLoaded extends OnViewDataLoaded {
		
		private String mCaption;
		private Command mNextAction;
		public OnPromoViewDataLoaded(View view, String caption, Command nextAction) {
			super(view);
			
			mCaption = caption;
			mNextAction = nextAction;
		}

		@Override
		public void onViewDataLoaded() {
			if (mView instanceof BeginningOrEndingPromotionsView) {
				BeginningOrEndingPromotionsView v = (BeginningOrEndingPromotionsView) mView;
				if (!((BeginningOrEndingPromotionsView) mView).IsEmpty()){
					AlertDialog dlg = Dialogs.createDialog(mCaption, "", v.inflate(), null, Command.NO_OP, null);
					dlg.setOnDismissListener(new OnDismissCloseableListener((Closeable)v,new OnDismissListener(){
	
						@Override
						public void onDismiss(DialogInterface dialog) {
							mNextAction.execute();
						}
						
					}));
					dlg.show();
				}
				else{
					mNextAction.execute();
				}
				
			}
		}
		
	}
	
	private void showNewPromo(){
		BeginningOrEndingPromotionsView view = new BeginningOrEndingPromotionsView(getContext(), _owner.Outlet.Id, 1000, 14);
		view.setOnViewDataLoaded(new OnPromoViewDataLoaded(view, "Начинающиеся промо-акции", new Command() {
			@Override
			public void execute() {
				showExpiringPromo();
			}
		}));
	}
	
	private void showExpiringPromo(){
		BeginningOrEndingPromotionsView v = new BeginningOrEndingPromotionsView(getContext(), _owner.Outlet.Id, 14, 1000);
		v.setOnViewDataLoaded(new OnPromoViewDataLoaded(v, "Заканчивающиеся промо-акции", Command.NO_OP));
	}
	
	private void offerSync() {
		Dialogs.createDialog("SFS", "Провести обмен?", _sync, new Command() {
			@Override
			public void execute() {
				showNewPromo();
				createOrder(_owner);
			}
		}).show();
	}
	
	public static class QueryOrderListViewItem extends GenericListItemView<QueryGetOrders, QueryGetOrdersEntity> {

		private QueryGetOrdersEntity mEntity;
		private String mUserId;
		
		private TextView mNumber;
		private TextView mAmount;
		private TextView mShipmentDate;
		private TextView mCustomer;
		private TextView mCfr;
		private TextView mAddress;
		private ImageView mStatus;
		
		public QueryOrderListViewItem(Context context, QueryGetOrders list, ListView lv, QueryGetOrdersEntity entity) {
			super(context, list, lv);
			
			layoutInflater.inflate(R.layout.order_item, this);
			mEntity = entity;
			mUserId = String.valueOf(MainActivity.getInstance().mCurrentUser.Id);
		}

		@Override
		public SfsContentView inflate() {
			
			
			return this;
		}

		@Override
		public void fill() {
			mStatus = (ImageView) findViewById(R.id.status_icon);
			mStatus.setClickable(true);
			String message = "Не отправлен";
			switch (mEntity.Draft) {
				case 0: {
					if (mEntity.CancelDate != null) {
						mStatus.setImageDrawable(getResources().getDrawable(R.drawable.canceled));
						message = "Отменен";
					} else if (mEntity.AcceptDate != null) {
						mStatus.setImageDrawable(getResources().getDrawable(R.drawable.approved));
						message = "Проведен в 1С";
					} else if (mEntity.ImportDate != null) {
						mStatus.setImageDrawable(getResources().getDrawable(R.drawable.imported));
						message = "Загружен в 1С";
					} else if (mEntity.ReceiveDate != null) {
						mStatus.setImageDrawable(getResources().getDrawable(R.drawable.sent));
						message = "Отправлен";
					} else {
						mStatus.setImageDrawable(getResources().getDrawable(R.drawable.not_sent));
						message = "Не отправлен";
					}
				} break;
				case 1: {
					mStatus.setImageDrawable(getResources().getDrawable(R.drawable.draft));
					message = "Черновик";
				} break;
				default: {
					mStatus.setImageDrawable(getResources().getDrawable(R.drawable.not_sent)); 
				} break;
			}
			mStatus.setOnClickListener(new OrderStatusClickListener(message));
			mNumber = (TextView) findViewById(R.id.number);
			DateTime createDate = new DateTime(mEntity.CreateDate.getTime());
			mNumber.setText("N" + mUserId + "/" + mEntity.Id + " от " + createDate.toString(DateTimeFormat.forPattern("dd.MM.yyyy")));
			
			mAmount = (TextView) findViewById(R.id.amount);
			mAmount.setText(Money.valueOf(mEntity.mAmount).toSymbolString());
 			
			mCustomer = (TextView) findViewById(R.id.customer);
			mCustomer.setText(mEntity.CustomerLegalName);
			
			mCfr = (TextView) findViewById(R.id.cfr);
			mCfr.setText("");
			if (mEntity.Cfr > 0) {
				if (mEntity.Cfr < 0.95) {
					mCfr.setTextColor(getResources().getColor(R.color.red));
				} else if (mEntity.Cfr < 0.98) {
					mCfr.setTextColor(getResources().getColor(R.color.yellow));
				} else if (mEntity.Cfr < 1.01) {
					mCfr.setTextColor(getResources().getColor(R.color.green));
				} else {
					mCfr.setTextColor(getResources().getColor(R.color.yellow));
				}
				mCfr.setText("КУЗ: " + String.format("%,.0f%%", mEntity.Cfr * 100));
			} 
			
			mAddress = (TextView) findViewById(R.id.address);
			mAddress.setText(Utils.prepareAddress(mEntity.CustomerAddress));
			
			mShipmentDate = (TextView) findViewById(R.id.shipment_date);
			DateTime shipmentDate = new DateTime(mEntity.ShipmentDate.getTime());
			mShipmentDate.setText("доставка на " + shipmentDate.toString(DateTimeFormat.forPattern("dd.MM.yyyy")) + " " + (mEntity.ShipmentTime != null ? mEntity.ShipmentTime : ""));
		} 
		
		private class OrderStatusClickListener implements OnClickListener {
			
			private String  mMessage; 
			
			public OrderStatusClickListener(String message){
				mMessage = message;
			}
			
			@Override
			public void onClick(View view) {	
				Toast.makeText(MainActivity.getInstance(), mMessage, Toast.LENGTH_SHORT).show();
			}
			
		}
	}


	
}