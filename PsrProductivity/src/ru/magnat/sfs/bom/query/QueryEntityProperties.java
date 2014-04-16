package ru.magnat.sfs.bom.query;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface QueryEntityProperties {
	public String name();

	public Class<?> value();
}
