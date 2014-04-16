package ru.magnat.sfs.update;

public class UpdateEntry {
	
	private String mName;
	private String mDescription;
	private Version mVersion;
	private String mHash;
	private String mDate;
	private String mSize;

	public String getName() {
		return mName;
	}

	public String getDescription() {
		return mDescription;
	}

	public Version getVersion() {
		return mVersion;
	}

	public String getHash() {
		return mHash;
	}

	public String getDate() {
		return mDate;
	}

	public String getSize() {
		return mSize;
	}
	
    public UpdateEntry(String name, String description, Version version, String hash, String date, String size) {
    	mName = name;
    	mDescription = description;
    	mVersion = version;
    	mHash = hash;
    	mDate = date;
    	mSize = size;
    }
    
}