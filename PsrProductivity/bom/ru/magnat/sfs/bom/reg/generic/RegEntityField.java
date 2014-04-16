package ru.magnat.sfs.bom.reg.generic;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RegEntityField {
	public enum FieldRole {
		DIMENSION, RESOURCE, PROPERTY
	}

	public FieldRole Role();

}
