/**
 * 
 */
package ru.magnat.sfs.promo;

import ru.magnat.sfs.bom.IEventListener;

/**
 * @author alex_us
 *
 */
public interface OnFormExitListener extends IEventListener {
	public Boolean requestExit(OnFormExitListener sender);
	public void confirmExit();
	

}
