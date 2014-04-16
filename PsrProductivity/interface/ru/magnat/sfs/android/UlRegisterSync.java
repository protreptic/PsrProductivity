package ru.magnat.sfs.android;

import android.content.Context;
import ru.magnat.sfs.bom.InternalStorage;

public final class UlRegisterSync extends UlSync {

	/**
	 * @param context
	 */
	public UlRegisterSync(Context context) {
		super(context);
		
	}
 
	@Override
	protected void ActivityPostAction() {
		MainActivity.getInstance().onRegisterEnd(true, InternalStorage.getConnection().getSyncResult().getAuthStatus(), mResult);
	}

}