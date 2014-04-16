package ru.magnat.sfs.bom.task.generic;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TaskEntityFileld {
	public String DisplayName();

}
