package ru.magnat.sfs.android;

import java.util.Date;
import java.util.Set;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArraySet;

import org.acra.ACRA;
import org.joda.time.DateTime;

import ru.magnat.sfs.bom.EventListenerSubscriber;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.IEventListener;
import ru.magnat.sfs.bom.InternalStorage;
import ru.magnat.sfs.bom.marketingphoto.DocMarketingPhotoEntity;
import ru.magnat.sfs.bom.order.DocOrderEntity;
import ru.magnat.sfs.bom.order.DocOrderJournal;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.ref.user.RefUserEntity;
import ru.magnat.sfs.bom.task.sync.TaskSyncEntity;
import ru.magnat.sfs.bom.task.sync.TaskSyncJournal;
import ru.magnat.sfs.bom.task.visit.TaskVisitEntity;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayEntity;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayJournal;
import ru.magnat.sfs.location.SfsLocationManager;
import ru.magnat.sfs.logging.PsrProductivityLogger.Log;
import ru.magnat.sfs.preference.SettingsActivity;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.update.UpdateAsyncTask;
import ru.magnat.sfs.update.UpdateParams;
import ru.magnat.sfs.util.Apps;
import ru.magnat.sfs.util.Fonts;
import ru.magnat.sfs.util.Network;
import ru.magnat.sfs.view.AboutView;
import ru.magnat.sfs.view.SyncView;
import ru.magnat.sfs.view.WorkdayView;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ViewFlipper;

import com.ianywhere.ultralitejni12.SyncResult;
import com.ianywhere.ultralitejni12.SyncResult.AuthStatusCode;
import com.ianywhere.ultralitejni12.ULjException;

public final class MainActivity extends Activity implements SyncMaster {
	
	public static final String PREFS_NAME = "SFSActivity";
	
	private ActionBar mActionBar;
	public static MainActivity sInstance;
	
	public static final int GPS_REQUEST = 111;
	public static final int PHOTO_ACTIVITY_CODE = 434;
	
	public static final String LOG_TAG = "SFS";
	public DocMarketingPhotoEntity mCurrentPhotoMeasure;
	public TaskWorkdayEntity mCurrentWorkday;
	public TaskVisitEntity mCurrentVisit;
	public RefOutletEntity mCurrentOutlet;
	public DocOrderEntity mCurrentOrder;
	public DocOrderJournal mCurrentCatalog;
	public RefUserEntity mCurrentUser;
	
	private ViewFlipper mViewFlipper;
	private float mBatteryLevel;
	
	private SfsLocationManager mLocationManager;
	
	/**
	 * Данный ресивер используется для сбора данных о состоянии батареи
	 */
	private BroadcastReceiver mBatteryChangedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			mBatteryLevel = intent.getIntExtra("level", 0);
			Log.i("Battery charge level is " + mBatteryLevel + "%");
		}
	};
	
	/**
	 *  Данный ресивер используется для завершения работы программы 
	 *  после 00:00, то есть если пользователь забыл выйти сам
	 *  при наступлении временного интервала 00:00\00:10 
	 *  программа сама завершит свою работу 
	 */
	private BroadcastReceiver mTimeTickReceiver = new BroadcastReceiver() {
		
		private AlertDialog mExitByTimerDialog;
		private Timer mTimer;
		private static final long TIMER_DELAY = 10000l;
		
		@Override
		public void onReceive(Context context, Intent intent) {			
			DateTime currentTime = new DateTime();
			Integer hour = currentTime.getHourOfDay();
			Integer minute = currentTime.getMinuteOfDay();
			
			if (hour == 0 && minute == 0) {
				if (mTimer == null) {
					mTimer = new Timer();
					mTimer.schedule(new TimerTask() {
						@Override
						public void run() {
							runOnUiThread(new Runnable() {
								public void run() {
									android.util.Log.e("", "Программа остановлена");
									
									// Отмена регистрации ресиверов
									unregisterReceiver(mBatteryChangedReceiver);
									unregisterReceiver(mTimeTickReceiver);
									
									InternalStorage.getInstance().release();
									android.os.Process.sendSignal(android.os.Process.myPid(), android.os.Process.SIGNAL_QUIT); 
								}
							});
						}
					}, TIMER_DELAY);					
				}

				if (mExitByTimerDialog == null) {
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setCancelable(false);
					builder.setMessage(mResources.getString(R.string.dialog_exit_by_timer));
					builder.setPositiveButton(mResources.getString(R.string.btn_cancel), new android.content.DialogInterface.OnClickListener() {
						@Override  
						public void onClick(DialogInterface arg0, int arg1) {
							mTimer.cancel();
						}
					});
					mExitByTimerDialog = builder.create();
				}
				
				if (!mExitByTimerDialog.isShowing()) {
					mExitByTimerDialog.show();
				}
			}
		}
	};
	
	private Stack<IEventListener> mBackPressedListeners = new Stack<IEventListener>();
	private Set<IEventListener> mPhotoReceivedListeners = new CopyOnWriteArraySet<IEventListener>();
	private Set<IEventListener> mFilterReceivedListeners = new CopyOnWriteArraySet<IEventListener>();
	private Set<IEventListener> mOrderMenuEventListener = new CopyOnWriteArraySet<IEventListener>();
	
	/**
	 * Функция возвращает текущее состояние батареи, пришедшее из ресивера
	 *
	 * @return значение состояния батареи
	 * 
	 * @author petr_bu
	 */
	public float getBatteryLevel() {
		return mBatteryLevel;
	} 
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case PHOTO_ACTIVITY_CODE: {
				if (data != null) {
					String[] params = data.getExtras().getStringArray("INPUT_PARAMS");
					onPhotoReceived(params);
				}
			} break;
			case GPS_REQUEST: {
				mLocationManager.isGpsEnabled();
			} break;
			default: {} break;
		}
	}
	
	/**
	 * Функция запускает процесс обновления программы
	 * 
	 * @author petr_bu
	 */
	private void checkUpdate(boolean mode) {
		UpdateParams updateParams = new UpdateParams();
		updateParams.setUid((mCurrentUser != null) ? String.valueOf(mCurrentUser.Id) : "0");
		updateParams.setVersion(Apps.getVersionName(this));
		updateParams.setUrl("http://mob1.magnat.ru:8081/sfs_versions.xml");
		updateParams.setUrl2("http://mob1.magnat.ru:8081/download/sfs.apk");
		new UpdateAsyncTask(this, mode).execute(updateParams);
	}
	
	private Fonts mFonts;
	public Fonts getFonts() {
		return mFonts;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		
		// Показать SplashScreen
		setContentView(R.layout.splash_screen);
	
		mActionBar = getActionBar();
		mActionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_background));
		mActionBar.setIcon(getResources().getDrawable(R.drawable.logotype_small));
		mActionBar.setTitle("");
		mActionBar.hide();
		
		MainActivity.sInstance = this;
		
		// Загрузка шрифтов
		mFonts = Fonts.getInstance(this);
		
		mResources = getResources();
		
		// Менеджер синхронизации
		//mSyncManager = UlSyncManager.getInstance(mContext);
		
		mLocationManager = SfsLocationManager.getInstance(this);
		mLocationManager.start();
		
		// Регистрация ресивера для отслеживания состояния батареи
		registerReceiver(mBatteryChangedReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

		// Регистрация ресивера для отслеживания состояния времени
		registerReceiver(mTimeTickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
		
		defineUser();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_menu, menu);
	    
	    mMainMenu = menu;
	    
	    return true;
	}
	
	private Menu mMainMenu;
	
	/**
	 * Функция возвращает ссылку на меню из главной активности
	 *
	 * @return ссылка на меню главной активности
	 * 
	 * @author petr_bu
	 */
	public Menu getMainMenu() {
		return mMainMenu;
	}
	
	public void enableSync(Boolean enabled) {
		mMainMenu.findItem(R.id.main_activity_menu_action_sync).setVisible(enabled);
	}
	
	public void enablePromo(Boolean enabled) {
		mMainMenu.findItem(R.id.main_order_menu_action_show_promo).setVisible(enabled);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
			case R.id.main_order_menu_action_show_promo: {
				fireOnOrderMenuEventListener(itemId);
				return true;
			}
			case R.id.main_order_menu_action_show_properties: {
				fireOnOrderMenuEventListener(itemId);
				return true;
			}
			case R.id.main_order_menu_action_show_filters: {
				fireOnOrderMenuEventListener(itemId);
				return true;
			}
			case R.id.main_order_menu_action_show_targets: {
				fireOnOrderMenuEventListener(itemId);
				return true;
			}
			case R.id.main_order_menu_action_product_filter: {
				fireOnOrderMenuEventListener(itemId);
				return true;
			}
			case R.id.main_order_menu_action_recalc_order: {
				fireOnOrderMenuEventListener(itemId);
				return true;
			}
			case R.id.main_activity_menu_action_show_track: {
				Globals.showTrack();
				return true;
			}
			case R.id.main_activity_menu_action_sync: {
				synchronize(SyncType.REGULAR); 
				
				return true; 
			} 	
			case R.id.main_activity_menu_action_wipe: {
				Dialogs.createDialog("", mResources.getString(R.string.dialog_wipe), 
						new Command() { 
							public void execute() {
								Log.i("Перерегистрация приложения");
								InternalStorage.deleteDatabase();
								MainActivity.getInstance().stopActivity();
							}
						}, Command.NO_OP).show();
				return true;
			} 	
			case R.id.main_activity_menu_action_check_update: {
				checkUpdate(false);
				ru.magnat.sfs.logging.PsrProductivityLogger.Log.i("Запрошено обновление");
				 
				return true;
			} 	
			case R.id.main_activity_menu_action_user_guide: {
				Globals.showUserGuide();
				
				return true;
			}
			case R.id.main_activity_menu_action_unload_data: {
				InternalStorage.getInstance().backup();
				ru.magnat.sfs.logging.PsrProductivityLogger.Log.i("Выгрузка данных приложения");
				return true;
			}
			case R.id.main_activity_menu_action_settings: {
				Intent intent = new Intent(this, SettingsActivity.class);
				startActivity(intent);
				
				return true;
			} 
			case R.id.main_activity_menu_action_about: {
				Dialogs.createDialog("", "", new AboutView(this), null, Command.NO_OP, null).show();
				
				return true; 
			}
			case R.id.main_activity_menu_action_show_sync_journal: {
				Dialogs.createDialog("", "", new SyncView(this), null, Command.NO_OP, null).show();
				
				return true;
			} 
			case R.id.main_activity_menu_action_show_extras: {
				Globals.openExtras();
				return true;
			} 
			default: {
				return super.onOptionsItemSelected(item);
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		Log.i("Программа остановлена");
		
		// Отмена регистрации ресиверов
		unregisterReceiver(mBatteryChangedReceiver);
		unregisterReceiver(mTimeTickReceiver);
		
		InternalStorage.getInstance().release();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	/**
	 * Функция возвращает ссылку на экземпляр главной активности
	 *
	 * @return ссылка на главную активность
	 * 
	 * @author petr_bu
	 */
	public static MainActivity getInstance() {
		return sInstance;
	}

	public void synchronize(SyncType type) {
		// Проверим доступен ли Интернет
		if (!Network.isNetworkConnectionAvailable(this)) {
			Log.i("Сеть Интернет не доступна");
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setCancelable(false);
			builder.setIcon(R.drawable.ic_launcher);
			builder.setMessage(mResources.getString(R.string.dialog_internet_unavailable));
			if (type == SyncType.REGULAR) {
				builder.setPositiveButton(mResources.getString(R.string.btn_repeat), new android.content.DialogInterface.OnClickListener() {
					@Override  
					public void onClick(DialogInterface arg0, int arg1) {
						synchronize(SyncType.REGULAR); 
					}
				});
			}
			builder.setNegativeButton(mResources.getString(R.string.btn_exit), new android.content.DialogInterface.OnClickListener() {
				@Override 
				public void onClick(DialogInterface arg0, int arg1) {
					android.os.Process.killProcess(android.os.Process.myPid());
				}
			});
			builder.show();
			
			return;
		}
		
		// Проверим доступен ли сервер
		if (!Network.isSynchronizationServerAvailable(getResources().getString(R.string.ml_primary_host), Integer.valueOf(getResources().getString(R.string.ml_port)))) {
			Log.i("Сеть Интернет не доступна");
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setCancelable(false);
			builder.setIcon(R.drawable.ic_launcher);
			builder.setMessage(mResources.getString(R.string.dialog_sync_server_unavailable));
			if (type == SyncType.REGULAR) {
				builder.setPositiveButton(mResources.getString(R.string.btn_repeat), new android.content.DialogInterface.OnClickListener() {
					@Override  
					public void onClick(DialogInterface arg0, int arg1) {
						synchronize(SyncType.REGULAR); 
					}
				});
			}
			builder.setNegativeButton(mResources.getString(R.string.btn_exit), new android.content.DialogInterface.OnClickListener() {
				@Override 
				public void onClick(DialogInterface arg0, int arg1) {
					android.os.Process.killProcess(android.os.Process.myPid());
				}
			});
			builder.show();

			return; 
		}
		
		RefUserEntity user = Globals.getUser();
		
		UlSync sync = null;
		Long userId = null;
		String version = null;
		String publication = null;
		String primaryServer = getResources().getString(R.string.ml_primary_host);
		int port = Integer.valueOf(getResources().getString(R.string.ml_port));
		String password = getResources().getString(R.string.ml_pwd);
		
		switch (type) {
			case INITIAL: {
				sync = new UlRegisterSync(this);
				userId = 0l;
				version = "SFSINI";
				publication = "INITIAL";
			} break;
			case UPLOAD: {
				sync = new UlRegularSync(this);
				userId = user.Id;
				version = getResources().getString(R.string.ml_version);
				publication = null;
			} break;
			case REGULAR: {
				TaskSyncJournal taskSyncJournal = new TaskSyncJournal(this);
				taskSyncJournal.NewEntity();
				mTaskSyncEntity = taskSyncJournal.Current();
				mTaskSyncEntity.setDefaults(mCurrentWorkday);
				taskSyncJournal.save();
				taskSyncJournal.close();
				
				sync = new UlRegularSync(this);
				userId = user.Id;
				version = getResources().getString(R.string.ml_version);
				publication = null;
			} break;
			default: {
				throw new RuntimeException();
			}
		}

		sync.setSyncMaster(this);	
		sync.execute(new String[] {
			version,
			primaryServer,
			String.valueOf(port),
			String.valueOf(userId),
			password, 
			publication,
			Globals.getDeviceId() 
		});
	}

	private TaskSyncEntity mTaskSyncEntity;
	
	public enum SyncType {
		INITIAL, UPLOAD, REGULAR
	}
	
	public void saveSyncResult(Boolean completed, String message) {
		if (mTaskSyncEntity == null) {
			return;
		}
		
		TaskSyncJournal taskSyncJournal = new TaskSyncJournal(this);
		if (taskSyncJournal.Find(mTaskSyncEntity)) {
			TaskSyncEntity task = taskSyncJournal.Current();
			task.TaskEnd = new Date();
			task.IsCompleted = completed;
			task.Result = message;
			taskSyncJournal.save();
		} 
		taskSyncJournal.close();
		
		mTaskSyncEntity = null;
	}

	private Command mExitCommand = new Command() {
		@Override
		public void execute() {
			MainActivity.getInstance().stopActivity();
		}
	};
	
	public void onRegisterEnd(Boolean state, int authStatusCode, String message) {
		switch (authStatusCode) {
			case AuthStatusCode.EXPIRED: {
				message = "Срок действия учетной записи истек";
				Dialogs.createDialog("SFS", message, mExitCommand).show();
			} break;
			case AuthStatusCode.IN_USE: {
				message = "Учетная запись уже используется";
				Dialogs.createDialog("SFS", message, mExitCommand).show();
			} break;
			case AuthStatusCode.INVALID: {
				message = "Учетная запись заблокирована";
				Dialogs.createDialog("SFS", message, mExitCommand).show();
			} break;
			case AuthStatusCode.UNKNOWN: {
				message = "Неизвестная учетная запись";
				Dialogs.createDialog("SFS", message, mExitCommand).show();
			} break;
			case AuthStatusCode.VALID: {
				message = "Ok";
				synchronize(SyncType.UPLOAD);
			} break;
			case AuthStatusCode.VALID_BUT_EXPIRES_SOON: {
				message = "Срок действия учетной записи скоро истекает";
				Dialogs.createDialog("SFS", message, mExitCommand).show();
			} break;
			case 4001: {
				message = "Технические работы на сервере, попробуйте позже";
				Dialogs.createDialog("SFS", message, mExitCommand).show();
			} break;
			default: {
				throw new RuntimeException("Unkonwn AuthStatusCode: " + authStatusCode);
			}
		}
	}

	public void onSyncEnd(Boolean state, int authStatusCode, String message, SyncResult syncResult) {
		switch (authStatusCode) {
			case AuthStatusCode.EXPIRED: {
				message = "Срок действия учетной записи истек";
				Dialogs.createDialog("SFS", message, Command.NO_OP).show();
			} break;
			case AuthStatusCode.IN_USE: {
				message = "Учетная запись уже используется";
				Dialogs.createDialog("SFS", message, Command.NO_OP).show();
			} break;
			case AuthStatusCode.INVALID: {
				if (syncResult.getAuthValue() == 4001) {
					message = "Активирован сервисный режим, синхронизация временно не доступна!";
				} else {
					message = "Учетная запись заблокирована";
				}
				
				Dialogs.createDialog("SFS", message, Command.NO_OP).show();
			} break;
			case AuthStatusCode.UNKNOWN: {
				message = "Неизвестная учетная запись";
				Dialogs.createDialog("SFS", message, Command.NO_OP).show();
			} break;
			case AuthStatusCode.VALID: {
				if (state) {
					message = "Ok";
				} else {
					message = "Error";
				}
			} break;
			case AuthStatusCode.VALID_BUT_EXPIRES_SOON: {
				message = "Срок действия учетной записи скоро истекает";
				Dialogs.createDialog("SFS", message, Command.NO_OP).show();
			} break;
			default: {
				throw new RuntimeException("Unkonwn AuthStatusCode: " + authStatusCode);
			}
		}
		saveSyncResult(state, message);
		
		defineUser();
		Dialogs.createDialog("", "", new SyncView(this), null, Command.NO_OP, null).show();
		
		//refreshViews();
	}

	public void refreshViews() {
		// Обновить все данные на формах
		mViewFlipper.invalidate();
	}
	
	private void doWork() {
		mCurrentWorkday = Globals.FindWorkdayTask();
		if (mCurrentWorkday == null) {
			TaskWorkdayJournal workdays = new TaskWorkdayJournal(this);
			workdays.NewEntity();
			mCurrentWorkday = workdays.Current();
			mCurrentWorkday.TaskDate = new Date();
			mCurrentWorkday.TaskBegin = mCurrentWorkday.TaskDate;
			mCurrentWorkday.TaskEnd = mCurrentWorkday.TaskBegin;
			mCurrentWorkday.Author = mCurrentUser;
			mCurrentWorkday.IsMark = false;
			mCurrentWorkday.IsCompleted = false;
			mCurrentWorkday.startGpsControl();
			workdays.save();
			workdays.close();
		}
		
		// Покажем интерфейс
		mActionBar.show();
		setContentView(R.layout.main);
		mViewFlipper = (ViewFlipper) findViewById(R.id.flipper);
		mViewFlipper.addView(new WorkdayView(this));  
		
		// Проверка доступного обновления
		checkUpdate(true);
	}
	
	private void defineUser() {
		mCurrentUser = Globals.getUser();
		if (mCurrentUser == null) {
			synchronize(SyncType.INITIAL);
			
			return;
		}
		
		ACRA.getErrorReporter().putCustomData("user_id", String.valueOf(mCurrentUser.Id));
		
		Globals.showSummaryNotes(this);
		if (Globals.getEmployee() != null) {
			doWork();
		} else {
			Dialogs.createDialog("", mResources.getString(R.string.dialog_sync_error), new Command() {
				
				@Override
				public void execute() {
					try { 
						InternalStorage.getConnection().dropDatabase();
						android.os.Process.killProcess(android.os.Process.myPid());
					} catch (ULjException e) {
						e.printStackTrace();
					}
				}
			}).show();
		}
	}

	public void stopActivity() {
		super.onBackPressed();
	}

	private Resources mResources;
	
	@Override
	public void onBackPressed() {
		if (mBackPressedListeners.empty()) {
			Dialogs.createDialog("", mResources.getString(R.string.dialog_exit), new Command() {
				public void execute() {	 
					stopActivity();
				}
			}, Command.NO_OP).show();
			
			return;
		}
		if (((OnBackPressedListener) mBackPressedListeners.peek()).onBackPressed()) {
			//refreshViews();
			mBackPressedListeners.pop();
		}
	}

	public void addToFlipper(SfsContentView v, String tag) {
		v.setTag(tag);
		v.inflate();

		mViewFlipper.addView(v);
		mViewFlipper.setDisplayedChild(mViewFlipper.getChildCount() - 1);
	}

	public void removeFromFlipper(View v) {
		mViewFlipper.removeView(v);
	}
	
	public void addOnOrderMenuEventListener(OnOrderMenuEventListener eventListener) {
		EventListenerSubscriber.addListener(mOrderMenuEventListener, eventListener);
	}
	
	public void removeOnOrderMenuEventListener(OnOrderMenuEventListener eventListener) {
		EventListenerSubscriber.removeListener(mOrderMenuEventListener, eventListener);
	}

	public void setOnOrderMenuEventListener(OnOrderMenuEventListener eventListener) {
		EventListenerSubscriber.setListener(mOrderMenuEventListener, eventListener);
	}
	
	private void fireOnOrderMenuEventListener(int menuItem){
		for (IEventListener ilistener:mOrderMenuEventListener){
			if (ilistener==null) continue;
			OnOrderMenuEventListener listener  = (OnOrderMenuEventListener) ilistener; 
			switch (menuItem){
			case  R.id.main_order_menu_action_show_properties: 
				listener.showOrderInfo();
				break;
			case  R.id.main_order_menu_action_product_filter:
				listener.showProductFilter();
				break;
			case  R.id.main_order_menu_action_show_filters:
				listener.showFilter();
				break;
			case  R.id.main_order_menu_action_show_targets: {
				listener.showTarget();
			}
				break;
			case  R.id.main_order_menu_action_show_promo:
				listener.showPromo();
				break;
			case R.id.main_order_menu_action_recalc_order:
				listener.recalcOrder();
				break;
			}
		}
	}
	
	public void addOnBackPressedListener(OnBackPressedListener eventListener) {
		if (eventListener == null) {
			return;
		}
		if (mBackPressedListeners.contains(eventListener)) {
			return;
		}
		mBackPressedListeners.push(eventListener);
	}
	
	public void removeOnBackPressedListener(OnBackPressedListener eventListener) {
		if (eventListener == null) {
			return;
		}
		if (!mBackPressedListeners.contains(eventListener)) {
			return;
		}
		mBackPressedListeners.remove(eventListener);
	}

	public void setOnPhotoReceived(OnPhotoReceivedListener eventListener) {
		EventListenerSubscriber.setListener(mPhotoReceivedListeners, eventListener);
	}
	
	public void addOnPhotoReceived(OnPhotoReceivedListener eventListener) {
		EventListenerSubscriber.addListener(mPhotoReceivedListeners, eventListener);
	}

	public void onPhotoReceived(String[] photos) {
		for (IEventListener eventListener : mPhotoReceivedListeners) {
			((OnPhotoReceivedListener) eventListener).onPhotoReceived(this, photos);
		}
	}

	public void setOnFilterChangedReceived(PropertyFilterChangeListener eventListener) {
		EventListenerSubscriber.setListener(mFilterReceivedListeners, eventListener);
	}

	public void addOnFilterChangedReceived(PropertyFilterChangeListener eventListener) {
		EventListenerSubscriber.addListener(mFilterReceivedListeners, eventListener);
	}

	public void onFilterChangedReceived(boolean[] filters) {
		for (IEventListener eventListener : mFilterReceivedListeners) {
			((PropertyFilterChangeListener) eventListener).onPropertyFilterChangeReceived(getApplicationContext(), filters);
		}
	}
}