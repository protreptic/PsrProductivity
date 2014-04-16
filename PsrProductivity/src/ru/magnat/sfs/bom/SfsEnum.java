package ru.magnat.sfs.bom;

import ru.magnat.sfs.android.MainActivity;
import android.widget.ArrayAdapter;

public abstract class SfsEnum extends ArrayAdapter<String> {

	public SfsEnum(String[] objects, int selectedId) {
		super(MainActivity.getInstance(),  android.R.layout.simple_list_item_1, objects);
		_selectedId = selectedId;
	}
	  @Override
	    public long getItemId(int position) {
	      
	      return position+1;
	    }

	    @Override
	    public boolean hasStableIds() {
	      return true;
	    }
	    
	    int _selectedId = 0;
	    public void setSelectedId(int id){
	    	_selectedId = id;
	    }
	    public int getId(){
	    	return _selectedId;
	    }
	    @Override
	    public String toString(){
	    	if (_selectedId>0){
	    		return this.getItem(_selectedId-1);
	    	}
	    	return "";
	    }
	    public static int NOTHING = 0;
	    @Override
	    public boolean equals(Object o) {
	    	if (o==null) return false;
	    	if  (o instanceof Integer) {
	    		return _selectedId==((Integer)o);
	    	}
	    	else if (o instanceof SfsEnum){
	    		return _selectedId==((SfsEnum)o).getId();
	    	}
	    	return false;
	    	
	    	
	    }
}
