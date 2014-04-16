package ru.magnat.sfs.database;

import java.io.InputStream;
import java.io.Reader;
import java.util.Date;

import ru.magnat.sfs.android.Log;
import ru.magnat.sfs.android.MainActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;

import com.ianywhere.ultralitejni12.Connection;
import com.ianywhere.ultralitejni12.DecimalNumber;
import com.ianywhere.ultralitejni12.PreparedStatement;
import com.ianywhere.ultralitejni12.ResultSet;
import com.ianywhere.ultralitejni12.ResultSetMetadata;
import com.ianywhere.ultralitejni12.ULjException;
import com.ianywhere.ultralitejni12.UUIDValue;

public class UltraliteCursor implements Cursor {
	
    private PreparedStatement mPreparedStatement;
    private ResultSet mResultSet;
    private ResultSetMetadata mResultSetMetadata;
    private String mQuery;
    private Connection mConnection;
	private int mPosition = -1;
    
    private boolean mIsLast;
    private boolean mIsFirst;
    private boolean mIsClosed;

	private boolean mIsAfterLast;
	private boolean mIsBeforeFirst;
	private int mCount;
    
    public UltraliteCursor(Context context, String query, Object...args) {
		this (InternalStorage.getInstance(context).getConnection(), query, args);
    }
    
    public UltraliteCursor(Connection connection, String query, Object...args) {
    	mQuery = query;
    	mConnection = connection;
    }
    
    public void setParametersAndExecute(Object...args) {
    	try {
    		if (mPreparedStatement == null) {
    			mPreparedStatement = mConnection.prepareStatement(mQuery);
    		}
        	
        	int col = 0;
    		if (args != null) {
				for (Object arg : args) {
					if (arg == null) {
						mPreparedStatement.setNull(++col);
					} else if (arg instanceof Integer) {
						mPreparedStatement.set(++col, (Integer) arg);
					} else if (arg instanceof Long) {
						mPreparedStatement.set(++col, (Long) arg);
					} else if (arg instanceof Float) {
						mPreparedStatement.set(++col, (Float) arg);
					} else if (arg instanceof Double) {
						mPreparedStatement.set(++col, (Double) arg);
    				} else if (arg instanceof Date) {
						mPreparedStatement.set(++col, (Date) arg);
    				} else if (arg instanceof String) {
						mPreparedStatement.set(++col, (String) arg);
    				} else if (arg instanceof Boolean) {
						mPreparedStatement.set(++col, (Boolean) arg);
    				} else {
    					throw new ClassCastException();
    				}
				}
			}

			mPreparedStatement.execute();
			mResultSet = mPreparedStatement.getResultSet();
			mResultSetMetadata = mResultSet.getResultSetMetadata();
			mCount = (int) mResultSet.getRowCount(0);
			mIsClosed = false;
    	} catch (ULjException e) {
    		Log.e(MainActivity.LOG_TAG,"UltraliteCursor: Ошибка выполнения запроса (" + e.getMessage() + ")");
    	}
    }
    
    @Override
    public void close() {
		try {
		    mResultSet.close();
		    mPreparedStatement.close();
		    
		    mResultSet = null;
		    mPreparedStatement = null;
		    mCount = -1;
		    mIsClosed = true;
		} catch (ULjException e) {
			Log.v(MainActivity.LOG_TAG, "UltraliteCursor: " + e.getMessage());
		}
    }
    
    @Override
    public void copyStringToBuffer(int arg0, CharArrayBuffer arg1) {
    	try {
			arg1.data = mResultSet.getString(arg0).toCharArray();
		} catch (ULjException e) {
			e.printStackTrace();
		}
    }
    
    @Override
    @Deprecated
    public void deactivate() {}
    
    @Override
    public byte[] getBlob(int arg0) {
		byte[] result = null;
		try {
		    result = mResultSet.getBytes(arg0);
		} catch (ULjException e) {
		    e.printStackTrace();
		}
		
		return result;
    }

    public byte[] getBlob(String arg0) {
		byte[] result = null;
		try {
		    result = mResultSet.getBytes(arg0);
		} catch (ULjException e) {
		    e.printStackTrace();
		}
		
		return result;
    }
    
    @Override
    public int getColumnCount() {
		int result = 0;
		try {
		    result = mResultSetMetadata.getColumnCount();
		} catch (ULjException e) {
		    e.printStackTrace();
		}
		
		return result;
    }

    public Date getDate(int arg0) {
    	Date result = null;
    	try {
    		result = mResultSet.getDate(arg0);
    	} catch (ULjException e) {
    		e.printStackTrace();
    	}
    	
    	return result;
    }
    
    public Date getDate(String arg0) {
    	Date result = null;
    	try {
    		result = mResultSet.getDate(arg0);
    	} catch (ULjException e) {
    		e.printStackTrace();
    	}
    	
    	return result;
    }
    
    @Override
    public int getColumnIndex(String arg0) {
    	int result = -1;
    	try {
    		result = mResultSet.getOrdinal(arg0);
    	} catch (ULjException e) {
    		e.printStackTrace();
    	}
    	
    	return result;
    } 

    @Override
    public int getColumnIndexOrThrow(String arg0) throws IllegalArgumentException {
		int result = 0;
		if (arg0.equals("id")) {
		    result = 1;
		}
		
		return result;
    }

    @Override
    public String getColumnName(int arg0) {
		String result = null;
		try {
			result = mResultSetMetadata.getAliasName(arg0);
		    if (result == null) {
		    	result = mResultSetMetadata.getTableColumnName(arg0);
		    }
		} catch (ULjException e) {
		    e.printStackTrace();
		}
		
		return result;
    }

    @Override
    public String[] getColumnNames() {
    	String[] result = null;
    	try {
    		int columnCount = getColumnCount();
    		int count = 0;
    		result = new String[columnCount];
    		for (int i = 1; i <= columnCount; i++) {
    			result[count] = mResultSetMetadata.getAliasName(i);
    		    if (result[count] == null) {
    		    	result[count] = mResultSetMetadata.getTableColumnName(i);
    		    }
    			count++;
    		}
    	} catch (ULjException e) {
    		e.printStackTrace();
    	}
    	
		return result;
    }

    public short getColumnDataType(int arg0) {
    	short result = -1;
    	try {
    		result = mResultSetMetadata.getDomainType(arg0);
    	} catch (ULjException e) {
    		e.printStackTrace();
    	}
    	
		return result;
    }
    
    @Override
    public int getCount() {
		return mCount;
    }

    @Override
    public double getDouble(int arg0) {
		double result = 0;
		try {
		    result = mResultSet.getDouble(arg0);
		} catch (ULjException e) {
		    e.printStackTrace();
		}
		
		return result;
    }

    public double getDouble(String arg0) {
		double result = 0;
		try {
		    result = mResultSet.getDouble(arg0);
		} catch (ULjException e) {
		    e.printStackTrace();
		}
		
		return result;
    }
    
    @Override
    public Bundle getExtras() {
		throw new UnsupportedOperationException();
    }

    @Override
    public float getFloat(int arg0) {
		float result = 0;
		try {
		    result = mResultSet.getFloat(arg0);
		} catch (ULjException e) {
		    e.printStackTrace();
		}
		
		return result;
    }

    public float getFloat(String arg0) {
		float result = 0;
		try {
		    result = mResultSet.getFloat(arg0);
		} catch (ULjException e) {
		    e.printStackTrace();
		}
		
		return result;
    }
    
    @Override
    public int getInt(int arg0) {
		int result = 0;
		try {
		    result = mResultSet.getInt(arg0);
		} catch (ULjException e) {
		    e.printStackTrace();
		}
		
		return result;
    }
    
    public int getInt(String arg0) {
		int result = 0;
		try {
		    result = mResultSet.getInt(arg0);
		} catch (ULjException e) {
		    e.printStackTrace();
		}
		
		return result;
    }
    
    @Override
    public long getLong(int arg0) {
		long result = 0;
		try {
		    result = mResultSet.getLong(arg0);
		} catch (ULjException e) {
		    e.printStackTrace();
		}
		
		return result;
    }

    public long getLong(String arg0) {
		long result = 0;
		try {
		    result = mResultSet.getLong(arg0);
		} catch (ULjException e) {
		    e.printStackTrace();
		}
		
		return result;
    }
    
    public boolean getBoolean(int arg0) {
    	boolean value = false;
		try {
		    value = mResultSet.getBoolean(arg0);
		} catch (ULjException e) {
		    e.printStackTrace();
		}
		return value;
    }
    
    public boolean getBoolean(String arg0) {
    	boolean result = false;
		try {
		    result = mResultSet.getBoolean(arg0);
		} catch (ULjException e) {
		    e.printStackTrace();
		}
		
		return result;
    }
    
    public InputStream getBlobInputStream(int arg0) {
    	InputStream result = null;
    	try {
    		result = mResultSet.getBlobInputStream(arg0);
    	} catch (ULjException e) {
    		e.printStackTrace();
    	}
    	
    	return result;
    }
    
    public InputStream getBlobInputStream(String arg0) {
    	InputStream result = null;
    	try {
    		result = mResultSet.getBlobInputStream(arg0);
    	} catch (ULjException e) {
    		e.printStackTrace();
    	}
    	
    	return result;
    }
    
    public byte[] getBytes(int arg0) {
    	byte[] value = null;
    	try {
    		value = mResultSet.getBytes(arg0);
    	} catch (ULjException e) {
    		e.printStackTrace();
    	}
    	return value;
    }
    
    public byte[] getBytes(final String arg0) {
    	byte[] value = null;
    	try {
    		value = mResultSet.getBytes(arg0);
    	} catch (ULjException e) {
    		e.printStackTrace();
    	}
    	return value;
    }
    
    public Reader getClobReader(int arg0) {
    	Reader value = null;
    	try {
    		value = mResultSet.getClobReader(arg0);
    	} catch (ULjException e) {
    		e.printStackTrace();
    	}
    	return value;
    }
    
    public Reader getClobReader(final String arg0) {
    	Reader value = null;
    	try {
    		value = mResultSet.getClobReader(arg0);
    	} catch (ULjException e) {
    		e.printStackTrace();
    	}
    	return value;
    }
    
    public DecimalNumber getDecimalNumber(int arg0) {
    	DecimalNumber result = null;
    	try {
    		result = mResultSet.getDecimalNumber(arg0);
    	} catch (ULjException e) {
    		e.printStackTrace();
    	}
    	
    	return result;
    }
    
    public DecimalNumber getDecimalNumber(String arg0) {
    	DecimalNumber result = null;
    	try {
    		result = mResultSet.getDecimalNumber(arg0);
    	} catch (ULjException e) {
    		e.printStackTrace();
    	}
    	
    	return result;
    }

	@Override
	public short getShort(int arg0) {
		short result = 0;
    	try {
    		result = (short) mResultSet.getInt(arg0);
    	} catch (ULjException e) {
    		e.printStackTrace();
    	}
    	
    	return result;
	}
	
	public short getShort(String arg0) {
		short result = 0;
    	try {
    		result = (short) mResultSet.getInt(arg0);
    	} catch (ULjException e) {
    		e.printStackTrace();
    	}
    	
    	return result;
	}
    
    public int getColumnValueSize(int arg0) {
    	int value = 0;
    	try {
    		value = mResultSet.getSize(arg0);
    	} catch (ULjException e) {
    		e.printStackTrace();
    	}
    	return value;
    }
    
    public int getColumnValueSize(String arg0) {
    	int result = 0;
    	try {
    		result = mResultSet.getSize(arg0);
    	} catch (ULjException e) {
    		e.printStackTrace();
    	}
    	
    	return result;
    }
    
	@Override
    public String getString(int arg0) {
		String result = null;
		try {
		    result = mResultSet.getString(arg0);
		} catch (ULjException e) {
		    e.printStackTrace();
		}
		
		return result;
    }

    public String getString(String arg0) {
		String result = null;
		try {
		    result = mResultSet.getString(arg0);
		} catch (ULjException e) {
		    e.printStackTrace();
		}
		
		return result;
    }
    
    public UUIDValue getUUIDValue(int arg0) {
    	UUIDValue result = null;
    	try {
    		result = mResultSet.getUUIDValue(arg0);
    	} catch (ULjException e) {
    		e.printStackTrace();
    	}
    	
    	return result;
    }
    
    public UUIDValue getUUIDValue(String arg0) {
    	UUIDValue result = null;
    	try {
    		result = mResultSet.getUUIDValue(arg0);
    	} catch (ULjException e) {
    		e.printStackTrace();
    	}
    	
    	return result;
    }
    
    @Override
    public int getType(int arg0) {
    	throw new UnsupportedOperationException();
    }

    @Override
    public boolean getWantsAllOnMoveCalls() {
		throw new UnsupportedOperationException();
    }

    @Override
    public int getPosition() {
    	return mPosition;
    }
    
    @Override
    public boolean isAfterLast() {
    	return mIsAfterLast;
    }

    @Override
    public boolean isBeforeFirst() {
    	return mIsBeforeFirst;
    }

    @Override
    public boolean isClosed() {
		return mIsClosed;
    }

    @Override
    public boolean isFirst() {
    	return mIsFirst;
    }

    @Override
    public boolean isLast() {
    	return mIsLast;
    }

    @Override
    public boolean isNull(int arg0) {
		boolean result = false;
		try {
		    result = mResultSet.isNull(arg0);
		} catch (ULjException e) {
		    e.printStackTrace();
		}
		
		return result;
    }

    public boolean isNull(String arg0) {
		boolean result = false;
		try {
		    result = mResultSet.isNull(arg0);
		} catch (ULjException e) {
		    e.printStackTrace();
		}
		
		return result;
    }
    
    @Override
    public boolean move(int pos) {
    	if (mResultSet==null) return false;
		boolean result = false;
		try {
		    result = mResultSet.relative(pos);
		    if (result) {
		    	mPosition += pos;
		    }
		} catch (ULjException e) {
		    e.printStackTrace();
		}
		
		return result;
    }

    public boolean moveToAfterLast() {
    	if (mResultSet == null) {
    		return false;
    	}
		boolean result = false;
		try {
		    result = mResultSet.afterLast();
		    if (result) {
		    	mIsAfterLast = true;
		    }
		} catch (ULjException e) {
		    e.printStackTrace();
		}
		
		return result;
    }
    
    @Override
    public boolean moveToFirst() {
    	if (mResultSet==null) return false;
		boolean result = false;
		try {
		    result = mResultSet.first();
		    if (result) {
		    	mIsFirst = true;
		    	mPosition = 1;
		    }
		} catch (ULjException e) {
		    e.printStackTrace();
		}
		
		return result;
    }

    @Override
    public boolean moveToLast() {
    	if (mResultSet==null) return false;
		boolean result = false;
		try {
		    result = mResultSet.last();
		    if (result) {
		    	mIsLast = true;
		    	mPosition = getCount();
		    }
		} catch (ULjException e) {
		    e.printStackTrace();
		}
		
		return result;
    }

    @Override
    public boolean moveToNext() {
    	if (mResultSet==null) return false;
		boolean value = false;
		try {
		    value = mResultSet.next();
		    if (value) {
		    	mPosition++;
		    }
		} catch (ULjException e) {
		    e.printStackTrace();
		}
		
		return value;
    }

    @Override
    public boolean moveToPosition(int pos) {
    	if (mResultSet==null) return false;
		boolean result = false;
		try {
		    result = mResultSet.first();
		    if (result) {
		    	result = mResultSet.relative(pos);
		    	if (result) {
		    		mPosition = pos;
		    	}
		    }
		} catch (ULjException e) {
		    e.printStackTrace();
		}
		
		return result;
    }

    @Override
    public boolean moveToPrevious() {
    	if (mResultSet==null) return false;
		boolean result = false;
		try {
		    result = mResultSet.previous();
		    if (result) {
		    	mPosition--;
		    }
		} catch (ULjException e) {
		    e.printStackTrace();
		}
		
		return result;
    }

    @Override
    public void registerContentObserver(ContentObserver arg0) {}

    @Override
    public void registerDataSetObserver(DataSetObserver arg0) {}

    @Override
    @Deprecated
    public boolean requery() {
    	throw new UnsupportedOperationException();
    }

    @Override
    public Bundle respond(Bundle arg0) {
		throw new UnsupportedOperationException();
    }

    @Override
    public void setNotificationUri(ContentResolver arg0, Uri arg1) {}

    @Override
    public void unregisterContentObserver(ContentObserver arg0) {}

    @Override
    public void unregisterDataSetObserver(DataSetObserver arg0) {}
	
}
