package com.appirio.tech.core.identity.util;

import com.topcoder.security.GeneralSecurityException;
import com.topcoder.security.Util;

public class Utils {

	public static String encodePassword(String password, String alias) {
		try {
			return Util.encodePassword(password, alias);
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e.getMessage(), e); //TODO:
		}
	}
	
	public static String decodePassword(String password, String alias) {
		try {
			return Util.decodePassword(password, alias);
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e.getMessage(), e); //TODO:
		}
	}
	
}
