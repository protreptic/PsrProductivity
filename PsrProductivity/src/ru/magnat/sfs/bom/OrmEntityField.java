package ru.magnat.sfs.bom;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OrmEntityField {
	public String[] fields();

	public int isPrimary();

	public String DisplayName();

	public String format() default "dd MMMM yyyy kk:mm";
}
