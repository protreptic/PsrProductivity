/**
 * 
 */
package ru.magnat.sfs.android;

import ru.magnat.sfs.bom.GenericEntity;

/**
 * @author alex_us
 * 
 */
public interface EntityActionCommand<T extends GenericEntity<?>> {
	public void setEntity(T entity);

	public void execute();

}
