/**
 * 
 */
package ru.magnat.sfs.ui.android;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.OrmObject;

/**
 * @author alex_us
 * 
 */

public interface IBO<C extends OrmObject<E>, E extends GenericEntity<C>, O extends GenericEntity<?>> {
	public GenericListView<C, E, O> GetSelectView(O owner);

	public SfsContentView GetViewView(E entity);

	public GenericListView<C, E, O> GetListView(O owner);

	public SfsContentView GetEditView(E entity);
}
