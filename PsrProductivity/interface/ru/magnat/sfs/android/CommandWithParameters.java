package ru.magnat.sfs.android;

import java.util.Map;

public interface CommandWithParameters extends Command {
	public Map<String, Object> getParameters();
}
