package ru.magnat.sfs.android;

import java.io.File;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import ru.magnat.sfs.util.Device;
import ru.magnat.sfs.util.Network;

import android.app.Application;
import android.os.Environment;

@ReportsCrashes(
	formKey = "", // This is required for backward compatibility but not used
	formUri = "http://mob1.magnat.ru:8081/ws_acra_submit_crash_report",
	mode = ReportingInteractionMode.TOAST,
    resToastText = R.string.crash_toast_text,
	socketTimeout = 5000
)
public class SalesForceSolution extends Application {
    public static final String EXTERNAL_STORAGE_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/ru.magnat.sfs/";
	public static final String SUMMARY_FILE = "summary.doc";

	@Override
    public void onCreate() {
        super.onCreate();

        ACRA.init(this);
        ACRA.getErrorReporter().putCustomData("device_id", Device.getDeviceId(this));
        ACRA.getErrorReporter().putCustomData("mac_wlan0", Network.getMACAddress("wlan0"));
        ACRA.getErrorReporter().putCustomData("mac_eth0", Network.getMACAddress("eth0"));
        ACRA.getErrorReporter().putCustomData("ip_v4", Network.getIPAddress(true));
        ACRA.getErrorReporter().putCustomData("ip_v6", Network.getIPAddress(false));
        
		// Создание структуры каталогов программы
		makeDirectories();
    }
	
	private void makeDirectories() {
		new File(EXTERNAL_STORAGE_DIRECTORY + "logs").mkdirs();
		new File(EXTERNAL_STORAGE_DIRECTORY + "backup").mkdirs();
		new File(EXTERNAL_STORAGE_DIRECTORY + "outbox").mkdirs();
		new File(EXTERNAL_STORAGE_DIRECTORY + "inbox").mkdirs();
		new File(EXTERNAL_STORAGE_DIRECTORY + "images").mkdirs();
		new File(EXTERNAL_STORAGE_DIRECTORY + "update").mkdirs();
	}
}
