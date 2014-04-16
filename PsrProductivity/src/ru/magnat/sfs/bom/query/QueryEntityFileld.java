package ru.magnat.sfs.bom.query;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface QueryEntityFileld {
	public String DisplayName();

}
