package ru.magnat.sfs.ui.android.task.visit;

import java.io.Closeable;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ru.magnat.sfs.android.Command;
import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.android.MainActivity.SyncType;
import ru.magnat.sfs.android.Utils;
import ru.magnat.sfs.bom.order.DocOrderEntity;
import ru.magnat.sfs.bom.order.DocOrderJournal;
import ru.magnat.sfs.bom.task.visit.OnVisitTypeChangedListener;
import ru.magnat.sfs.bom.task.visit.TaskVisitEntity;
import ru.magnat.sfs.bom.task.visit.TaskVisitJournal;
import ru.magnat.sfs.ui.android.GenericEntityView;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.view.BalanceView;
import ru.magnat.sfs.view.GoldenMeasureView;
import ru.magnat.sfs.view.OrdersView;
import ru.magnat.sfs.view.OutletView;
import ru.magnat.sfs.view.TprMeasuresView;
import android.content.Context;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

public class TaskVisitEntityView extends GenericEntityView<TaskVisitJournal, TaskVisitEntity> implements OnTabChangeListener {

	final String TABS_TAG_OUTLET = "outlet_info";
	final String TABS_TAG_TARGET = "target_info";
	final String TABS_TAG_STORECHECK = "visibility_info";
	final String TABS_TAG_ORDERS = "order_info";
	final String TABS_TAG_PAYMENT = "payment_info";
	final String TABS_TAG_TASK = "task_info";
	final String TABS_TAG_TPR = "tpr_info";
	
	private TaskVisitEntity mTaskVisitEntity;

	private Command _sync = new Command() {
		public void execute() {
			MainActivity.getInstance().synchronize(SyncType.REGULAR); 
		}
	};
	
	public TaskVisitEntityView(Context context) {
		super(context);
	}
	
	public TaskVisitEntityView(Context context, TaskVisitJournal tasks, TaskVisitEntity task) {
		super(context, tasks, task);
		tasks.Find(task);
		_tabFactory = new VisitTabContentFactory((TaskVisitEntity) _entity);
	}
	
	@Override
	public Boolean onBackPressed() {
		Command commandOk = new Command() {
			public void execute() {
				MainActivity.getInstance().mCurrentWorkday.startGpsControl();
				Dialogs.createDialog("SFS", "Визит завершен?", new Command() {
					@Override
					public void execute() {
						_entity.IsCompleted = true;
						_entity.TaskEnd = new Date();
						_entity.save();
						closeView();
						Dialogs.createDialog("", "Провести обмен?", _sync,
								new Command() {
									@Override
									public void execute() {
										//forceShowTargets();
									}

								}).show();
					}

				}, new Command() {

					@Override
					public void execute() {
						_entity.IsCompleted = false;
						_entity.TaskEnd = new Date();
						_entity.save();
						closeView();
						Dialogs.createDialog("SFS", "Провести обмен?", _sync,
								new Command() {

									@Override
									public void execute() {
										forceShowTargets();
									}

								}).show();
					}

				}).show();
			}

		};

		Command commandCancel = new Command() {

			public void execute() {

				cancelCloseView();
			}
		};

		Dialogs.createDialog("SFS", "Закрыть визит?", commandOk, commandCancel).show();

		return true;
	}

	private class VisitTabContentFactory implements TabHost.TabContentFactory, Closeable {

		final Map<String, SfsContentView> _tabs;

		public VisitTabContentFactory(TaskVisitEntity visit) {
			_tabs = new HashMap<String, SfsContentView>();
			mTaskVisitEntity = visit;
			MainActivity.getInstance().mCurrentVisit = mTaskVisitEntity;
		}

		public View createTabContent(String tag) {		
			SfsContentView view = null;
			if (mTaskVisitEntity != null) {
				if (tag == TABS_TAG_TASK) {
					view = new VisitTypeTab(getContext(), mTaskVisitEntity);
				} else if (tag == TABS_TAG_TARGET) {
					view = new VisitTargetTab(getContext(), mTaskVisitEntity);
				} else if (tag == TABS_TAG_ORDERS) {
					view = new OrdersView(getContext(), true);
				} else if (tag == TABS_TAG_STORECHECK) {
					view = new GoldenMeasureView(getContext(), true);
				} else if (tag == TABS_TAG_TPR) {
					view = new TprMeasuresView(getContext(), true);
				} else if (tag == TABS_TAG_PAYMENT) {
					view = new BalanceView(getContext());
				} else if (tag == TABS_TAG_OUTLET) {
					view = new OutletView(getContext(), mTaskVisitEntity.Outlet.Id); 
				} 
				if (view != null) {
					view.inflate();
					_tabs.put(tag, view);
				}
				refresh();
			}
			
			return view;
		}

		public void refreshTab(String tag) {
			SfsContentView v = _tabs.get(tag);
			if (v != null) {
				v.refresh();
			}
		}

		@Override
		public void close() throws IOException {
			for (View v : _tabs.values()) {
				if (v instanceof Closeable) {
					((Closeable) v).close();
				}
			}
		}
	}

	private boolean orderPresent() {
		DocOrderJournal j = new DocOrderJournal(getContext());
		j.SetMasterTask(_entity);
		j.Select(null, null, false, true);
		DocOrderEntity doc;
		Boolean found = false;
		while ((doc = j.next()) != null) { // BUGFIX #77 USMANOV 20/09/2013
			if (!doc.IsMark)
				if (doc.getAmount() > 0) {
					found = true;
					break;
				}
		}
		j.close();
		return found;
	}
	
	private void showOrder() {
		mTabHost.getTabWidget().getChildAt(0).setVisibility((orderPresent()) ? View.GONE : View.VISIBLE);
		mTabHost.getTabWidget().getChildAt(1).setVisibility(View.VISIBLE);
		mTabHost.getTabWidget().getChildAt(2).setVisibility(View.VISIBLE);
		mTabHost.getTabWidget().getChildAt(3).setVisibility(View.VISIBLE);
		mTabHost.getTabWidget().getChildAt(4).setVisibility(View.VISIBLE);
		mTabHost.getTabWidget().getChildAt(5).setVisibility(View.VISIBLE);
		mTabHost.setCurrentTabByTag(TABS_TAG_TARGET);
	}

	private void showFinances() {
		mTabHost.getTabWidget().getChildAt(0).setVisibility(View.VISIBLE);
		mTabHost.getTabWidget().getChildAt(1).setVisibility(View.GONE);
		mTabHost.getTabWidget().getChildAt(2).setVisibility(View.GONE);
		mTabHost.getTabWidget().getChildAt(3).setVisibility(View.GONE);
		mTabHost.getTabWidget().getChildAt(4).setVisibility(View.VISIBLE);
		mTabHost.getTabWidget().getChildAt(5).setVisibility(View.VISIBLE);
		mTabHost.setCurrentTabByTag(TABS_TAG_TASK);
	}

	private void showDocuments() {
		mTabHost.getTabWidget().getChildAt(0).setVisibility(View.VISIBLE);
		mTabHost.getTabWidget().getChildAt(1).setVisibility(View.GONE);
		mTabHost.getTabWidget().getChildAt(2).setVisibility(View.GONE);
		mTabHost.getTabWidget().getChildAt(3).setVisibility(View.GONE);
		mTabHost.getTabWidget().getChildAt(4).setVisibility(View.VISIBLE);
		mTabHost.getTabWidget().getChildAt(5).setVisibility(View.VISIBLE);
		mTabHost.setCurrentTabByTag(TABS_TAG_TASK);
	}

	private void showShelving() {
		mTabHost.getTabWidget().getChildAt(0).setVisibility(View.VISIBLE);
		mTabHost.getTabWidget().getChildAt(1).setVisibility(View.GONE);
		mTabHost.getTabWidget().getChildAt(2).setVisibility(View.GONE);
		mTabHost.getTabWidget().getChildAt(3).setVisibility(View.VISIBLE);
		mTabHost.getTabWidget().getChildAt(4).setVisibility(View.VISIBLE);
		mTabHost.getTabWidget().getChildAt(5).setVisibility(View.VISIBLE);
		mTabHost.setCurrentTabByTag(TABS_TAG_STORECHECK);
	}

	@Override
	public void refresh() {
		View v = mTabHost.getCurrentView();
		if (v instanceof SfsContentView) {
			((SfsContentView) v).refresh();
		}
	}

	@Override
	protected void showMenu() {
		MainActivity.getInstance().getMainMenu()
				.findItem(R.id.main_activity_menu_action_sync)
				.setVisible(false);
	}

	@Override
	protected void hideMenu() {
		MainActivity.getInstance().getMainMenu()
				.findItem(R.id.main_activity_menu_action_sync).setVisible(true);
	}

	@Override
	public SfsContentView inflate() {
		layoutInflater.inflate(R.layout.visit_entity_view, this);
		mTabHost = (TabHost) findViewById(R.id.visit_tab);
		mTabHost.setup();
		mTabHost.setOnTabChangedListener(this);
		
		Utils.AddTab(mTabHost, _tabFactory, TABS_TAG_TASK, "Тип визита");
		Utils.AddTab(mTabHost, _tabFactory, TABS_TAG_TARGET, "Цели");
		Utils.AddTab(mTabHost, _tabFactory, TABS_TAG_ORDERS, "Заказы");
		Utils.AddTab(mTabHost, _tabFactory, TABS_TAG_STORECHECK, "Полка");
		Utils.AddTab(mTabHost, _tabFactory, TABS_TAG_TPR, "TPR");
		Utils.AddTab(mTabHost, _tabFactory, TABS_TAG_PAYMENT, "Долги");
		Utils.AddTab(mTabHost, _tabFactory, TABS_TAG_OUTLET, "Точка");
		visibilityControl();
		
		if (_catalog.Find(_entity)) {
			if (_catalog.Current().TaskEnd == null) {
				_catalog.Current().TaskBegin = new Date();
				_catalog.Current().TaskEnd = new Date();
				_catalog.save();
			}
		}
		refresh();

		_entity.setOnVisitTypeChangedListener(new OnVisitTypeChangedListener() {

			public void onVisitTypeChanged(TaskVisitEntity sender, int type) {
				visibilityControl();
			}

		});
		
		MainActivity.sInstance.mCurrentWorkday.stopGpsControl();
		
		return this;
	}

	@Override
	public void onTabChanged(String tabId) {
		super.onTabChanged(tabId);
		((VisitTabContentFactory) _tabFactory).refreshTab(tabId);
	}

	private void visibilityControl() {
		switch (_entity.VisitType) {
			case 0: {
				showOrder();
			} break;
			case 1: {
				showFinances();
			} break;
			case 2: {
				showDocuments();
			} break;
			case 3: {
				showShelving();
			} break;
		}
	}

	@SuppressWarnings("resource")
	protected void forceShowTargets() {
		Dialogs.createDialog("", "", new VisitTargetTab(getContext(), _entity).inflate(), null, Command.NO_OP, null).show();
	}

	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
		visibilityControl();
	}

	@Override
	public void closeView() {
		try {
			((VisitTabContentFactory) _tabFactory).close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.closeView();
	}

//	protected void createSync(TaskWorkdayEntity owner) {
//		if (owner == null) {
//			Dialogs.MessageBox("Обмен можно делать только в рамках рабочего дня");
//			return;
//		}
//
//		TaskSyncJournal cat = new TaskSyncJournal(SFSActivity.getInstance());
//		cat.NewEntity();
//		_currentSync = cat.Current();
//		_currentSync.setDefaults(owner);
//		if (!cat.save()) {
//			Dialogs.MessageBox("Не удалось записать обмен");
//			return;
//		}
//		cat.close();
//		Globals.synchronizeDb(this);
//	}

//	public void onSyncEnd(Boolean state, int authStatus, String message,
//			SyncResult syncResult) {
//
//		if (state)
//			message = "Ok";
//		else
//			switch (authStatus) {
//			case 1:
//				break;
//			case 4:
//				message = "Ошибка авторизации";
//				Dialogs.createDialog("SFS", "Обмен не удался: " + message,
//						Command.NO_OP).show();
//				break;
//			default:
//				Dialogs.createDialog("SFS", "Обмен не удался: " + message,
//						Command.NO_OP).show();
//				break;
//			}
//		saveSyncResult(state, message);
//		forceShowTargets();
//
//	}
//
//	public void saveSyncResult(Boolean completed, String message) {
//		TaskSyncJournal tasks = new TaskSyncJournal(SFSActivity.getInstance());
//		if (tasks.Find(_currentSync)) {
//			TaskSyncEntity task = tasks.Current();
//			task.TaskEnd = new Date();
//			task.IsCompleted = completed;
//			task.Result = message;
//			tasks.save();
//			tasks.close();
//		}
//	}

}
