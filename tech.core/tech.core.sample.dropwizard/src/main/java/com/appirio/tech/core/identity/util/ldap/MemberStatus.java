package com.appirio.tech.core.identity.util.ldap;

public enum MemberStatus {
	/** registered but not activated */
	UNACTIVATED("U"),
	/** activated */
	ACTIVATED("A"),
	/** unknown */
	UNKNOWN("");
	
	private String internalLabel;
	MemberStatus(String internalLabel) {
		this.internalLabel = internalLabel;
	}
	public String toInternalLabel() {
		return this.internalLabel;
	}
}
