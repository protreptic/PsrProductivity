package ru.magnat.sfs.util;

import android.text.TextUtils;

public class UniqueTaxPayerId {
	public static final int TYPE_NATURAL = 12;
	public static final int TYPE_LEGAL = 10;
	
	public static boolean validate(String value) {
		if (value == null) {
			return false;
		}
		if (!TextUtils.isDigitsOnly(value)) {
			return false;
		}
		int[] digits;
		if (value.length() == TYPE_LEGAL) {
			digits = new int[TYPE_LEGAL];
			for (int i = 0; i < value.length(); i++) {
				digits[i] = Integer.valueOf(value.substring(i, (i+1)));
			}
			int check = ((2 * digits[0] + 4 * digits[1] + 10 * digits[2] + 3 * digits[3] + 5 * digits[4] + 9 * digits[5] + 4 * digits[6] + 6 * digits[7] + 8 * digits[8]) % 11) % 10;
			if (check != digits[9]) {
				return false;
			}
		} else if (value.length() == TYPE_NATURAL) {
			digits = new int[TYPE_NATURAL];
			for (int i = 0; i < value.length(); i++) {
				digits[i] = Integer.valueOf(value.substring(i, (i+1)));
			}
			int check1 = (((7 * digits[0]) + (2 * digits[1]) + (4 * digits[2]) + (10 * digits[3]) + (3 * digits[4]) + (5 * digits[5]) + (9 * digits[6]) + (4 * digits[7]) + (6 * digits[8]) + (8 * digits[9])) % 11) % 10;
			int check2 = (((3 * digits[0]) + (7 * digits[1]) + (2 * digits[2]) + (4 * digits[3]) + (10 * digits[4]) + (3 * digits[5]) + (5 * digits[6]) + (9 * digits[7]) + (4 * digits[8]) + (6 * digits[9]) + (8 * digits[10])) % 11) % 10;
			System.out.println("" + check1 + " " + check2);
			if (check1 != digits[10]) {
				return false;
			}
			if (check2 != digits[11]) {
				return false;
			}
		} else {
			return false;
		}
		
		return true;
	}
}
