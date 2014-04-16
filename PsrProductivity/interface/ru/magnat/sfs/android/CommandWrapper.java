/**
 * 
 */
package ru.magnat.sfs.android;

import android.content.DialogInterface;

/**
 * @author alex_us
 * 
 */
public class CommandWrapper implements DialogInterface.OnClickListener {
	private Command command;

	public CommandWrapper(Command command) {
		this.command = command;
	}

	public void onClick(DialogInterface dialog, int which) {
		dialog.dismiss();
		command.execute();
	}
}
