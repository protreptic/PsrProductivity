/**
 * 
 */
package ru.magnat.sfs.bom;

/**
 * @author alex_us
 * 
 */
public interface OnValueChangedListener extends IEventListener {
	public abstract void onValueChanged(Object sender, Object value);
}
