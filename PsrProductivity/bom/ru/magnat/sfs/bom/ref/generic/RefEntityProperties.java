package ru.magnat.sfs.bom.ref.generic;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RefEntityProperties {
	public String name();

	@SuppressWarnings("rawtypes")
	public Class value();
}
