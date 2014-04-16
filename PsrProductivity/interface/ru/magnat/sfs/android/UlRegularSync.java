package ru.magnat.sfs.android;

import android.content.Context;
import ru.magnat.sfs.bom.Globals;

public class UlRegularSync extends UlSync {
	
	/**
	 * @param context
	 */
	public UlRegularSync(Context context) {
		super(context); 
		
	}

	public Command uploadFiles = new Command() {
		@Override
		public void execute() {
			Globals.uploadFiles(false);
			offerDownloadFiles.execute();
		}
	};
	
	public Command downloadFiles = new Command() {
		@Override
		public void execute() {
			Globals.downloadFiles(false);
		}
	};

	public Command offerUploadDownloadFiles = new Command() {
		@Override
		public void execute() {
			float totalSize = Globals.fileUploadWanted() / 1024f / 1024f;
			if (totalSize > 0) {
				Dialogs.createDialog("", "Имеются неотправленные файлы ("
								+ String.format("%.2f", totalSize)
								+ " МБ). Отправить их сейчас?", uploadFiles,
						offerDownloadFiles).show();
			} else {
				offerDownloadFiles.execute();
			}
		}
	};

	public Command offerDownloadFiles = new Command() {
		@Override
		public void execute() {
			float totalSize = Globals.fileDownloadWanted() / 1024f / 1024f;
			if (totalSize > 0) {
				Dialogs.createDialog("", "На сервере имеются файлы (" + String.format("%.2f", totalSize) + " МБ), ожидающие загрузки. Загрузить их сейчас?", downloadFiles, Command.NO_OP).show();
			}
		}
	};

	@Override
	protected void ActivityPostAction() {
		if (mResult.equals("Ok")) {
			mSyncMaster.onSyncEnd(true, mSyncResult.getAuthStatus(), mResult, mSyncResult);
		} else {
			mSyncMaster.onSyncEnd(false, mSyncResult.getAuthStatus(), mResult, mSyncResult);
		}
		
		if (mSyncResult.getStreamErrorCode() > -1) {
			offerUploadDownloadFiles.execute();
		}
	}

}