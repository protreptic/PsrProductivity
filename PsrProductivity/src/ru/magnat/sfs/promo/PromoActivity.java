package ru.magnat.sfs.promo;

import ru.magnat.sfs.android.R;
import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.bom.order.DocOrderEntity;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager.BackStackEntry;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.app.FragmentTransaction;
import android.os.Bundle;


public class PromoActivity extends Activity implements OnBackStackChangedListener, OnFormExitListener {

	private ActionBar mActionBar;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
		setContentView(R.layout.main_activity_container);
				
		mActionBar = getActionBar();
		mActionBar.setBackgroundDrawable(getResources().getDrawable(R.color.magnat_color));
		mActionBar.setDisplayUseLogoEnabled(true); 
		mActionBar.setLogo(getResources().getDrawable(R.drawable.logotype));
		mActionBar.setTitle("");

		Long storeId = getIntent().getExtras().getLong("store_id");
		long ondate = getIntent().getExtras().getLong("on_date");
		
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		
		boolean showReport = getIntent().getExtras().getBoolean("promotion_summary_report");
		
		Fragment fragment;
	
		if (showReport) {
			fragment = new PromotionSummaryReport();
		} else {
			fragment = new PromoTypeListFragment(); 
			Bundle arguments = new Bundle();
			arguments.putLong("store_id", storeId);
			arguments.putLong("on_date", ondate);
			fragment.setArguments(arguments);
			DocOrderEntity order = MainActivity.getInstance().mCurrentOrder;
			if (order!=null)
				((PromoTypeListFragment) fragment).setPromotionStateListener(order);
		}
		
		getFragmentManager().addOnBackStackChangedListener(this);
		String tag = Integer.toString(fragment.hashCode());
		transaction.add(R.id.fragment_container, fragment,tag);
		transaction.addToBackStack(tag);
		transaction.commit();
	}
	
	@Override
	public void onBackPressed() {
		
		if (getFragmentManager().getBackStackEntryCount() == 1) {
		    finish();
		} else {
			BackStackEntry entry = getFragmentManager().getBackStackEntryAt(getFragmentManager().getBackStackEntryCount()-1);
			Fragment fragment = getFragmentManager().findFragmentByTag(entry.getName());
			Boolean handled = false;
			if (fragment!=null && (fragment instanceof OnFormExitListener)){
				OnFormExitListener listener = (OnFormExitListener) fragment;
				if (!listener.requestExit(this)){
					handled = true;
				}
			}
		
			if (!handled)
				super.onBackPressed();
		}
		
	}
	
	@Override
	public void onBackStackChanged() {
		
	}

	@SuppressWarnings("unused")
	private OnFormExitListener mFormExitListener; 
	
	@Override
	public Boolean requestExit(OnFormExitListener sender) {
		mFormExitListener = sender;
		//не принципиально (хотя еслии ее саму кто-то спросит, то можно и обработать, тот кто спрашивал, должен вызывать)
		return true;
	}

	@Override
	public void confirmExit() {
		super.onBackPressed();	
	}
	
	
	

}
