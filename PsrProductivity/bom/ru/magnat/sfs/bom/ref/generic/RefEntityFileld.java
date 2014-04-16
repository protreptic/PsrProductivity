package ru.magnat.sfs.bom.ref.generic;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RefEntityFileld {
	public String DisplayName();

}
