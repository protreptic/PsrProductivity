package ru.magnat.sfs.bom.reg.generic;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RegEntityProperties {
	public String name();

	@SuppressWarnings("rawtypes")
	public Class value();
}
