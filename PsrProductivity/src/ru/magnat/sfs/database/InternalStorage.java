package ru.magnat.sfs.database;

import java.io.File;

import ru.magnat.sfs.util.Apps;
import android.content.Context;

import com.ianywhere.ultralitejni12.ConfigFileAndroid;
import com.ianywhere.ultralitejni12.Connection;
import com.ianywhere.ultralitejni12.DatabaseManager;
import com.ianywhere.ultralitejni12.ULjException;

public class InternalStorage {

    private static InternalStorage sInstance;
    private ConfigFileAndroid mConfigFileAndroid;
    private Context mApplicationContext;
    private Connection mConnection;
    
	private Context mContext;
	private String mApplicationVersion;
	private String mDataBaseFilePath;
	private String mDataBaseFileName;
    private File mDatabaseFile;
    
    private InternalStorage() {}
    private InternalStorage(Context context) {
    	mContext = context;
    	mApplicationVersion = Apps.getVersionName(mContext);
    	mDataBaseFilePath = mContext.getFilesDir().getAbsolutePath() + File.separator;
    	mDataBaseFileName = mDataBaseFilePath + "sfs_remote-" + mApplicationVersion + ".udb";
    	
		mApplicationContext = context;
		mDatabaseFile = new File(mDataBaseFileName);
		try {
		    mConfigFileAndroid = DatabaseManager.createConfigurationFileAndroid(mDataBaseFileName, mApplicationContext);
		    if (mDatabaseFile.exists()) {
		    	mConnection = DatabaseManager.connect(mConfigFileAndroid);
		    } else {
		    	mConnection = DatabaseManager.createDatabase(mConfigFileAndroid);
		    }
		} catch (ULjException e) {

		}
    }
    public static InternalStorage getInstance(final Context context) {
		if (InternalStorage.sInstance == null) {
		    InternalStorage.sInstance = new InternalStorage(context);
		}
		return InternalStorage.sInstance;
    }
    public Connection getConnection() {
    	return mConnection;	
    }
}