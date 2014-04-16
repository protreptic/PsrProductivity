package ru.magnat.sfs.bom.ref.storechannel;

import ru.magnat.sfs.bom.SfsEnum;

public class GoldenProgramType extends SfsEnum  {
	public GoldenProgramType(int selectedId) {
		super(new String[]{	"Golden"
								,"Golden Light"
								,"Innovation Golden"}
			,selectedId);
	}
	
	public static int GOLDEN = 1;
	public static int GOLDEN_LIGHT = 2;
	public static int GOLDEN_INNOVATION = 3;
}
