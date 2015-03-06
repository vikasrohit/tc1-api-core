package com.appirio.tech.core.identity.util.idgen;

import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

@UseStringTemplate3StatementLocator
//@RegisterMapperFactory(TCBeanMapperFactory.class)
public abstract class SequenceDAO {

	@SqlQuery(
			"SELECT <sequenceName>.nextval AS nextVal FROM systables WHERE tabid = 1"
		)
	public abstract Long nextVal(@Define("sequenceName") String sequenceName);
	
	
	/*
	@SqlQuery(
		"SELECT next_block_start AS nextVal FROM id_sequences WHERE name = :name FOR UPDATE"
	)
	public abstract Sequence getSequenceByName(@Bind("name") String name);
	

    @Transaction(TransactionIsolationLevel.REPEATABLE_READ)
    @SqlUpdate("UPDATE id_sequences SET next_block_start = :nextVal WHERE name = :name")
    public abstract int updateNextVal(@Bind("nextVal") final long nextVal, @Bind("name") final String name);
    
    @Transaction(TransactionIsolationLevel.REPEATABLE_READ)
    public long nextVal(final String name) {
    	if(name == null || name.length()==0)
    		throw new IllegalArgumentException();
    	Sequence seq = getSequenceByName(name);
    	Long nextval = seq.getNextVal();
    	if (nextval==null) {
    		nextval = 0L;
    	}
    	
    	updateNextVal(nextval+1, name);
    	return nextval;
    }
    */
    
}
