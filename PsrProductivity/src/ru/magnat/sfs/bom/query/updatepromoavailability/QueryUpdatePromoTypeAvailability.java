package ru.magnat.sfs.bom.query.updatepromoavailability;

import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.android.Log;

import com.ianywhere.ultralitejni12.Connection;
import com.ianywhere.ultralitejni12.PreparedStatement;

public final class QueryUpdatePromoTypeAvailability {

	
	boolean _prepared = false;
	final Connection _connection;
	
	final long _outlet;
	final Integer _promotype;
	final Boolean _availability;
	
	static final String query = 
			" update RegPromoAvailability"
	+ " set IsAvailable = ? "
    + " where "
	+ " PromoType = ? "
   + " and Outlet = ? "
   + " and not IsAvailable is null "
   + " and IsAvailable <> ? ";
	
	public QueryUpdatePromoTypeAvailability(Connection connection,
			long outlet, Integer promotype, Boolean availability) {
		_connection = connection;
		_outlet = outlet;
		_promotype = promotype;
		_availability = availability;
	
	}
	
	
	public Boolean execute() {
		boolean result = false;
		int param = 0;
		try {
			PreparedStatement ps = _connection.prepareStatement(query);

			ps.set(++param, _availability);
			if (_promotype==null) ps.setNull(++param);
			else ps.set(++param, (int)_promotype);
			ps.set(++param, _outlet);
			ps.set(++param, _availability);		
		
		
			result = ps.execute();
			Log.d(MainActivity.LOG_TAG, "Updated records: " + ps.getUpdateCount());
			
			ps.close();
			_connection.commit();
			_connection.checkpoint();
		} catch (Exception e) {
			Log.e(MainActivity.LOG_TAG, "Ошибка обновления доступности промо : "+e.getMessage());
		}
		return result;
	}

}
