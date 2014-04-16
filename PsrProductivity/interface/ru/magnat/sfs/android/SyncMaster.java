package ru.magnat.sfs.android;

import com.ianywhere.ultralitejni12.SyncResult;

public interface SyncMaster {
	public void onSyncEnd(Boolean state, int authStatus, String message, SyncResult syncResult);
	public void saveSyncResult(Boolean state, String message);
}
