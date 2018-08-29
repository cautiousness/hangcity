package com.fuj.hangcity.utils;

public class ValidUtils {
	/**
	 * 检验邮箱格式是否正确
	 * @param target
	 * @return
	 */
	public static boolean isValidEmail(CharSequence target) {
		return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
	}
}
