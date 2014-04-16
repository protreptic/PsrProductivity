package ru.magnat.sfs.bom.query.updatepromoavailability;

import ru.magnat.sfs.android.Log;
import ru.magnat.sfs.android.MainActivity;

import com.ianywhere.ultralitejni12.Connection;
import com.ianywhere.ultralitejni12.PreparedStatement;

public final class QueryUpdatePromoAvailability {

	
	boolean _prepared = false;
	final Connection _connection;
	
	final long _outlet;
	final long _promo;
	final Boolean _availability;
	
	static final String query = "update RegPromoAvailability "
			+ " set IsAvailable =  ? "
			+ " where Outlet=? " 
			+ " and Promo=? " 
			+ " and not IsAvailable is null "
			;

	public QueryUpdatePromoAvailability(Connection connection,
			long outlet, long promo, Boolean availability) {
		_connection = connection;
		_outlet = outlet;
		_promo = promo;
		_availability = availability;
	
	}
	
	
	public Boolean execute() {
		boolean result = false;
		int param = 0;
		try {
			PreparedStatement ps = _connection.prepareStatement(query);
			
			ps.set(++param, _availability);
			ps.set(++param, _outlet);
			ps.set(++param, _promo);
			
			result = ps.execute();
			Log.d(MainActivity.LOG_TAG, "��������� "+ps.getUpdateCount()+" ������� � ����������� �����");
			ps.close();
			_connection.commit();
			_connection.checkpoint();
		} catch (Exception e) {
			Log.e(MainActivity.LOG_TAG, "������ ���������� ����������� ����� : "+e.getMessage());
		}
		return result;
	}

}
