package ru.magnat.sfs.bom;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityCardField {
	public int Sortkey();

	public String DisplayName();

	public String SelectMethod();

	public String format() default "dd MMMM yyyy kk:mm";
}