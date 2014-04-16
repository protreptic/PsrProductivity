package ru.magnat.sfs.bom.task.generic;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TaskEntityProperties {
	public String name();

	@SuppressWarnings("rawtypes")
	public Class value();
}
