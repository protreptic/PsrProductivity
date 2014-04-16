package ru.magnat.sfs;

import java.util.Date;

import org.acra.ACRA;

import ru.magnat.sfs.android.Command;
import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.SyncMaster;
import ru.magnat.sfs.android.UlRegisterSync;
import ru.magnat.sfs.android.UlRegularSync;
import ru.magnat.sfs.android.UlSync;
import ru.magnat.sfs.bom.Globals;
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
import ru.magnat.sfs.update.UpdateAsyncTask;
import ru.magnat.sfs.update.UpdateParams;
import ru.magnat.sfs.util.Apps;
import ru.magnat.sfs.util.Fonts;
import ru.magnat.sfs.util.Network;
import ru.magnat.sfs.view.AboutView;
import ru.magnat.sfs.view.SyncView;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ViewFlipper;

import com.ianywhere.ultralitejni12.SyncResult;
import com.ianywhere.ultralitejni12.SyncResult.AuthStatusCode;
import com.ianywhere.ultralitejni12.ULjException;

public class MainActivity extends Activity implements SyncMaster {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private ActionBar mActionBar;
	
	public static final String PREFS_NAME = "SFSActivity";
	
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
    
	public float getBatteryLevel() {
		return mBatteryLevel;
	} 
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case PHOTO_ACTIVITY_CODE: {
				if (data != null) {
					//String[] params = data.getExtras().getStringArray("INPUT_PARAMS");
					//onPhotoReceived(params);
				}
			} break;
			case GPS_REQUEST: {
				mLocationManager.isGpsEnabled();
			} break;
			default: {} break;
		}
	}
    
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		// Показать SplashScreen
		setContentView(R.layout.splash_screen);
	
		mActionBar = getActionBar();
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
		
		defineUser();
    }
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		Log.i("Программа остановлена");
		
		// Отмена регистрации ресиверов
		unregisterReceiver(mBatteryChangedReceiver);
		
		InternalStorage.getInstance().release();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
    
    private Resources mResources;
	
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
		
        setContentView(R.layout.activity_main);
        
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, new String[] { "Маршрут", "Точки", "Данные" }));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
		mActionBar = getActionBar();
		mActionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_striped));
		mActionBar.setIcon(null);
		mActionBar.setTitle("");
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setHomeButtonEnabled(false);
		mActionBar.show();
		
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            
        	public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }

        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
        selectItem(0);
		
		// Проверка доступного обновления
		checkUpdate(true);
	}
	
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
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_menu, menu);
	    
	    return true;
	}
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
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
//								Log.i("Перерегистрация приложения");
//								InternalStorage.deleteDatabase();
//								MainActivity.getInstance().stopActivity();
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
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	return super.onMenuItemSelected(featureId, item);
    }
    
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
	
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    private void selectItem(int position) {
        Fragment fragment = new RouteFragment();
        
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        mActionBar.setTitle("");
         
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }
    
    @Override
    public void setTitle(CharSequence title) {
        mActionBar.setTitle(title);
    }
   
}
