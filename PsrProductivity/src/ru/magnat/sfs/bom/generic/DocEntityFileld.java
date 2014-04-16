package ru.magnat.sfs.bom.generic;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DocEntityFileld {
	public String DisplayName();

}
