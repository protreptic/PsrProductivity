package ru.magnat.sfs.update;

import java.util.StringTokenizer;

public class Version implements Comparable<Version> {

	private Integer mMajor;
	private Integer mMinor;
	//private Integer mPatch;
	
	public Version(Integer major, Integer minor, Integer patch) {
		mMajor = major;
		mMinor = minor;
		//mPatch = patch;
	}
	
	public Version(String string) {
		StringTokenizer st = new StringTokenizer(string, ".");

		mMajor = Integer.parseInt(st.nextToken());
		mMinor = Integer.parseInt(st.nextToken());
		//mPatch = Integer.parseInt(st.nextToken());
	}
	
	public Integer getMajor() {
		return mMajor;
	}

	public Integer getMinor() {
		return mMinor;
	}
	
//	public Integer getPatch() {
//		return mPatch;
//	}
	
	public boolean isMore(Version version) {
		if (mMajor < version.mMajor) {
			return true;
		}
		if (mMajor == version.mMajor && mMinor < version.mMinor) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object == null) {
			throw new NullPointerException();
		}
		Version version = (Version) object;
		if (mMajor == version.mMajor && mMinor == version.mMinor) {// && mPatch == version.mPatch) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return String.valueOf(mMajor) + "." + String.valueOf(mMinor);// + "." + String.valueOf(mPatch);
	}

	@Override
	public int compareTo(Version another) {
		if (another == null) {
			throw new NullPointerException();
		}
		
		return 0;
	} 
	
}
