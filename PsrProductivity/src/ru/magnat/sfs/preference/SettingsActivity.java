package ru.magnat.sfs.preference;

import ru.magnat.sfs.android.R;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;

public class SettingsActivity extends Activity {
	
	private ActionBar mActionBar;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		mActionBar = getActionBar();
		mActionBar.setBackgroundDrawable(getResources().getDrawable(R.color.magnat_color));
		mActionBar.setDisplayUseLogoEnabled(true); 
		mActionBar.setLogo(getResources().getDrawable(R.drawable.logotype));
		mActionBar.setTitle("");
        
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
    
}
