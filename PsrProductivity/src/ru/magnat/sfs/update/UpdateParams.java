package ru.magnat.sfs.update;

public class UpdateParams {
	
	private String mUrl;
	private String mUid;
	private String mVersion;
	private String mUrl2;
	
	public String getUrl() {
		return mUrl;
	}
	
	public String getUid() {
		return mUid;
	}
	
	public String getVersion() {
		return mVersion;
	}
	
	public String getUrl2() {
		return mUrl2;
	}
	
	public void setUrl(String mUrl) {
		this.mUrl = mUrl;
	}
	
	public void setUid(String mUid) {
		this.mUid = mUid;
	}
	
	public void setVersion(String mVersion) {
		this.mVersion = mVersion;
	}
	
	public void setUrl2(String mUrl2) {
		this.mUrl2 = mUrl2;
	}
	
}