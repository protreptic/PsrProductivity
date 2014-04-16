package ru.magnat.sfs.bom.query.updatepromoavailability;

import ru.magnat.sfs.android.MainActivity;
import ru.magnat.sfs.android.Log;

import com.ianywhere.ultralitejni12.Connection;
import com.ianywhere.ultralitejni12.PreparedStatement;

public final class QueryUpdatePromoCompensationType {

	
	boolean _prepared = false;
	final Connection _connection;
	
	final long _outlet;
	final long _promo;
	final int _compensationType;
	
	static final String query = "update RegPromoAvailability "
			+ "set PrefferedCompensationType =  ? "
			+ " where Outlet=? " 
			+ " and Promo=? " 
			+ " and not IsAvailable is null "
			;

	public QueryUpdatePromoCompensationType(Connection connection,
			long outlet, long promo, int compensationType) {
		_connection = connection;
		_outlet = outlet;
		_promo = promo;
		_compensationType = compensationType;
	
	}
	
	
	public Boolean execute() {
		boolean result = false;
		int param = 0;
		try {
			PreparedStatement ps = _connection.prepareStatement(query);
			
			ps.set(++param, _compensationType);
			ps.set(++param, _outlet);
			ps.set(++param, _promo);
			
						
		
		
			result = ps.execute();
			Log.d(MainActivity.LOG_TAG, "Обновлено "+ps.getUpdateCount()+" записей типа компенсации промо");
			ps.close();
			_connection.commit();
		} catch (Exception e) {
			Log.e(MainActivity.LOG_TAG, "Ошибка обновления типа компенсации промо : "+e.getMessage());
		}
		return result;
	}

}
