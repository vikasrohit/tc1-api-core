package com.appirio.tech.core.identity.util.idgen;

public class Sequence {

	public Sequence() {
	}

	/** id_sequences.name */
	private String name;
	
	/** id_sequences.next_block_start */
	private Long nextVal;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getNextVal() {
		return nextVal;
	}

	public void setNextVal(Long nextVal) {
		this.nextVal = nextVal;
	}
}
