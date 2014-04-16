/**
 * 
 */
package ru.magnat.sfs.android;

/**
 * @author alex_us
 * 
 */
public interface Command {
	public void execute();

	public static final Command NO_OP = new Command() {
		public void execute() {
		}

	};
}
