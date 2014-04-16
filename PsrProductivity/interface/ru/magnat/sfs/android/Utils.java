package ru.magnat.sfs.android;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;

public final class Utils {
	private static final MainActivity CONTEXT = MainActivity.sInstance;

	public static Date AddDay(Date date, int offset) {

		Calendar cal = Calendar.getInstance(); // locale-specific
		cal.setTime(date);
		cal.add(Calendar.DATE, offset);
		return new Date(cal.getTimeInMillis());
	}

	public static int MonthToDate(Date from, Date to) {
		if (from == null) from = new Date(System.currentTimeMillis());
		if (to == null) to = new Date(System.currentTimeMillis());
		Calendar cal = Calendar.getInstance();
		cal.setTime(from);
		int lFrom = cal.get(Calendar.MONTH)+cal.get(Calendar.YEAR)*12;
		cal.setTime(to);
		int lTo = cal.get(Calendar.MONTH)+cal.get(Calendar.YEAR)*12;
		
		return (lTo-lFrom);
	}
	
	public static Date RoundDate(Date date) {

		Calendar cal = Calendar.getInstance(); // locale-specific
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new Date(cal.getTimeInMillis());
	}

	public static Date FirstDateOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new Date(cal.getTimeInMillis());
	}

	/**
	 * @param res
	 * @throws NotFoundException
	 */
	public static TabHost.TabSpec AddTab(TabHost tabHost, TabHost.TabContentFactory factory, String UID, Drawable icon, String label) {
		return AddTab(tabHost, factory, UID, icon, label, null) ;
	}
	public static TabHost.TabSpec AddTab(TabHost tabHost, TabHost.TabContentFactory factory, String UID, View indicator) {
		return AddTab(tabHost, factory, UID, null, null,indicator) ;
	}
	public static TabHost.TabSpec AddTab(TabHost tabHost, TabHost.TabContentFactory factory, String UID, String label) {
		return AddTab(tabHost, factory, UID, null, label, null) ;
	}
	public static TabHost.TabSpec AddTab(TabHost tabHost, TabHost.TabContentFactory factory, String UID, Drawable icon, String label,View indicator) {
		TabHost.TabSpec spec;
		spec = tabHost.newTabSpec(UID);
		spec.setContent(factory);
		if (indicator != null) {
			spec.setIndicator(indicator);
		} else {
			if (icon != null) {
				spec.setIndicator(label, icon);
			} else {
				spec.setIndicator(label);
			}
		}
		tabHost.addTab(spec);
		
		return spec;
	}

	public static int GetDipsFromPixel(Context context, float pixels) {
		// Get the screen's density scale
		final float scale = context.getResources().getDisplayMetrics().density;
		// Convert the dps to pixels, based on density scale
		return (int) (pixels * scale + 0.5f);
	}

	public static void setConditionText(TextView tv, String text) {
		SpannableString content = new SpannableString(text);
		//content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		tv.setText(content);
	}

	public static void setEditedText(TextView tv, String text) {
		SpannableString content = new SpannableString(text);
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		tv.setText(content);
		tv.setTextColor(CONTEXT.getResources().getColor(android.R.color.primary_text_light));
	}

	public static GeoPoint getGeoPoint(double lat, double lon) {
		return (new GeoPoint((int) (lat * 1000000.0), (int) (lon * 1000000.0)));
	}

	public static GeoPoint getGeoPointFromLocation(Location location) {
		return getGeoPoint(location.getLatitude(), location.getLongitude());
	}

	public static Location getLocationFromGeoPoint(GeoPoint geopoint,
			String caption) {
		Location location = new Location(caption);
		location.setLatitude(((double) geopoint.getLatitudeE6()) / 1000000.0);
		location.setLongitude(((double) geopoint.getLongitudeE6()) / 1000000.0);
		return location;
	}

	public static String[] splitString(String str) {
		String[] result = null;
		String separator = ";";
		result = str.split(separator);
		return result;
	}

	public static String joinString(String[] arr) {
		String result = "";
		String separator = ";";
		if (arr != null) {
			if (arr.length > 0) {
				result = arr[0]; // start with the first element
				for (int i = 1; i < arr.length; i++) {
					result = result + separator + arr[i];
				}
			}
		}
		return result;
	}

	public static String getFirstFile(String filePath) {
		File dir = new File(filePath);
		File[] files = dir.listFiles();
		if (files == null)
			return "";
		if (files.length == 0)
			return "";
		return files[0].getName();
	}

	public static boolean copyFile(File source, File dest) {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(source));
			bos = new BufferedOutputStream(new FileOutputStream(dest, false));
			byte[] buf = new byte[1024];
			bis.read(buf);
			do {
				bos.write(buf);
			} while (bis.read(buf) != -1);
		} catch (IOException e) {
			return false;
		} finally {
			try {
				if (bis != null) bis.close();
				if (bos != null) bos.close();
			} catch (IOException e) {
				return false;
			}
		}

		return true;
	}

	public static boolean findOrCreateCatalog(String catalog) {
		File file = new File(catalog);
		if (file.exists())
			if (file.isDirectory())
				return true;
		if (file.mkdir())
			return true;
		return false;

	}

	public static String copyFileToCatalog(String sourceFullPath,
			String destCatalog) {
		File source = new File(sourceFullPath);
		if (!source.exists())
			return "";
		if (!findOrCreateCatalog(destCatalog))
			return "";
		String fileName = destCatalog + source.getName();
		File dest = new File(fileName);
		if (dest.exists())
			return fileName;
		if (copyFile(source, dest)) {
			return fileName;
		}
		return "";
	}

	public static String copyBitmapToCatalog(String sourceFullPath,
			String destCatalog) {
		File source = new File(sourceFullPath);
		if (!source.exists())
			return "";
		if (!findOrCreateCatalog(destCatalog))
			return "";
		String fileName = destCatalog + source.getName();
		File dest = new File(fileName);
		if (dest.exists())
			return fileName;
		if (copyAndCompressBitmap(source, dest)) {
			return fileName;
		}
		return "";
	}

	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		if (!file.exists())
			return true;
		return file.delete();
	}
	public static final int IMAGE_MAX_SIZE = 2000000;
	public static int getCompressRate(File source)
	{
		
		int scale = 1;
		BufferedInputStream bis = null;
		try {
			// предварительный запрос чтобы с опциями определиться
			bis = new BufferedInputStream(new FileInputStream(source));
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true; // только размеры
			//Bitmap bitmap = 
			BitmapFactory.decodeStream(bis, null, options);
			bis.close();
			bis = null;
			// вычисляем масштаб
			
			while ((options.outWidth * options.outHeight)
					* (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
				scale++;
			}
			
		}
		catch (Exception e){
			Log.v(MainActivity.LOG_TAG,"Ошибка определения степени сжатия фото:"+e.getMessage());
		}
		finally{
			try{
				if (bis!=null)
					bis.close();
			}
			catch (Exception e){
				Log.v(MainActivity.LOG_TAG,"Ошибка закрытия потока:"+e.getMessage());
			}
		}
		System.gc();
		return scale;
	}
	
	public static Bitmap getRotatedBitmap(Bitmap source) {
		Bitmap bitmap = null;
		
		try {
			Matrix matrix = new Matrix();
			matrix.postRotate(Integer.parseInt("90"));
		
		
			bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
		} catch (OutOfMemoryError e){
			Log.v(MainActivity.LOG_TAG,"Не удалось повернуть фото: " + e.getMessage());
		}
		return bitmap;
	}
	
	public static Bitmap getResizedBitmap(File source) {
		Bitmap bitmap = null;
		Bitmap scaledBitmap = null;
		BufferedInputStream bis = null;
		BitmapFactory.Options options = null;
		
		int scale = 0;
		int height = 0;
		int width = 0;
		
		double y = 0.0d;
		double x = 0.0d;
		
		try {
		
			scale = getCompressRate(source);
			options = new BitmapFactory.Options();
			bis = new BufferedInputStream(new FileInputStream(source));
			
			if (scale > 1) {
				scale--;
	
				options.inSampleSize = scale;
				options.inPurgeable = true;
				bitmap = BitmapFactory.decodeStream(bis, null, options);
	
				height = bitmap.getHeight();
				width = bitmap.getWidth();
	
				y = Math.sqrt(IMAGE_MAX_SIZE / (((double) width) / height));
				x = (y / height) * width;
				
				scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int) x, (int) y, true);
				bitmap.recycle();
				bitmap = null;
				System.gc();
				bitmap = Bitmap.createBitmap(scaledBitmap);
				
				scaledBitmap.recycle();
				scaledBitmap = null;
				System.gc();
			} else {
				bitmap = BitmapFactory.decodeStream(bis, null, options);
			}
			
			bis.close();
			bis = null;
			options = null;
			System.gc();
		} catch (FileNotFoundException e){
			Log.v(MainActivity.LOG_TAG, "Ошибка сжатия изображения " + e.getMessage());
		} catch (IOException e) {
			Log.v(MainActivity.LOG_TAG, "Ошибка сжатия изображения " + e.getMessage());
		} catch (OutOfMemoryError e) {
			Log.v(MainActivity.LOG_TAG, "Ошибка сжатия изображения [OutOfMemoryError] " + e.getMessage());
		}
		return bitmap;
	}
	
	public static boolean copyAndCompressBitmap(File source, File dest) {
		
		BufferedOutputStream bos = null;
		Bitmap bitmap = null;
		Log.v(MainActivity.LOG_TAG,"Копирование начато ");
		try {
			bitmap = getResizedBitmap(source);
			bos = new BufferedOutputStream(new FileOutputStream(dest, false));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos); // compressed
		} catch (IOException e) {
			Log.v(MainActivity.LOG_TAG,"Ошибка копирования: "+e.getMessage());
			return false;
		} finally {
			try {
				if (bos != null)
					bos.close();
			} catch (Exception e) {
				Log.v(MainActivity.LOG_TAG,"Ошибка завершения копирования: "+e.getMessage());
				return false;
			}
			
			bos = null;
			bitmap = null;
			System.gc();
		}

		return true;
	}

	

	public static void hideInput() {
		try {
			InputMethodManager imm = (InputMethodManager) CONTEXT
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(CONTEXT.getWindow().getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
		} catch (Exception e) {
		}

	}

	public static boolean isFirstPartOfMonth(Date date) {
		boolean res = false;
		Calendar cal = Calendar.getInstance(); // locale-specific
		cal.setTime(date);
		res = cal.get(Calendar.DAY_OF_MONTH)<16;
		cal.clear();
		cal  = null;
		return res;
	}

	public static String prepareAddress(String address){
		String[] parts = address.split(",");
		String out = "";
		for (String part: parts){
			part = part.trim();
			if (part.isEmpty()) continue;
			if (!out.isEmpty())
				out+=",";
			out+=part;
		}
		return out;
	}

	public static boolean isNumber(String string) {
		if (string == null) {
			return false;
		}
		return string.matches("^-?\\d+$");
	}

	public static float getBatteryLevel() {
		
		return 0f;
	}

	public static void setInverseText(TextView tv) {
		//tv.setBackgroundColor(SFSActivity.getInstance().getResources().getColor(android.R.color.holo_blue_light));
		tv.setTextColor(MainActivity.getInstance().getResources().getColor(android.R.color.background_light));
		
	}

	public static String toTimesString(int multiplicity) {
		if (multiplicity==0)
			return "ни разу";
		int i = Math.abs(multiplicity);
		i = i % 10;
		
		switch (i){
		case 2:
		case 3:
		case 4:
			if (multiplicity>10 && multiplicity<20)
				return ""+multiplicity+" раз";
			else	
				return ""+multiplicity+" раза";
		default:
			return ""+multiplicity+" раз";
		}
		
	}


	
}
