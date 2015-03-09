package com.appirio.tech.core.service.identity.dao;

import org.skife.jdbi.v2.TransactionIsolationLevel;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.Transaction;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import com.appirio.tech.core.api.v3.TCID;
import com.appirio.tech.core.api.v3.controller.ResourceFactory;
import com.appirio.tech.core.api.v3.util.jdbi.TCBeanMapperFactory;
import com.appirio.tech.core.service.identity.model.User;
import com.appirio.tech.core.service.identity.util.Utils;
import com.appirio.tech.core.service.identity.util.idgen.SequenceDAO;
import com.appirio.tech.core.service.identity.util.jdbi.BindUser;

@UseStringTemplate3StatementLocator
@RegisterMapperFactory(TCBeanMapperFactory.class)
public abstract class UserDAO implements Transactional<UserDAO> {

	@SqlQuery(
			"SELECT u.user_id as id, u.first_name AS firstName, u.last_name AS lastName, u.handle, " +
			"DECODE(u.status, 'A', 1, 0) AS active, " +
			"(SELECT e.address FROM email AS e WHERE e.user_id = :id AND e.email_type_id = 1 AND e.primary_ind = 1) AS email " +
			"FROM user AS u WHERE u.user_id = :id"
	)
	public abstract User findUserById(@Bind("id") long id);
	
	@SqlQuery(
			"SELECT u.user_id as id, u.first_name AS firstName, u.last_name AS lastName, u.handle, " +
			"DECODE(u.status, 'A', 1, 0) AS active, " +
			"(SELECT e.address FROM user AS u JOIN email AS e ON e.user_id = u.user_id WHERE u.handle_lower = LOWER(:handle) AND e.email_type_id = 1 AND e.primary_ind = 1) AS email " +
			"FROM user AS u WHERE u.handle_lower = LOWER(:handle)"
	)
	public abstract User findUserByHandle(@Bind("handle") String handle);
	
	@SqlQuery(
			"SELECT u.user_id as id, u.first_name AS firstName, u.last_name AS lastName, u.handle, " +
			"DECODE(u.status, 'A', 1, 0) AS active,  " +
			"e.address AS email " +
			"FROM user AS u  JOIN email AS e ON e.user_id = u.user_id WHERE e.address = :email"
	)
	public abstract User findUserByEmail(@Bind("email") String email);
	
	
	
	@SqlQuery("SELECT invalid_handle from invalid_handles WHERE invalid_handle = :handle")
	public abstract String findInvalidHandle(@Bind("handle") String handle);
	
	// TODO: activation code
	@SqlUpdate(
			"INSERT INTO user " +
			"(user_id, first_name, last_name, handle, status, activation_code) VALUES " +
			"(:id, :firstName, :lastName, :handle, :status, 'ABCDEFG')")
			//"(:u.id, :u.firstName, :u.lastName, :u.handle, :u.status, :activationCode)")
	@Transaction(TransactionIsolationLevel.REPEATABLE_READ)
	public abstract long createUser(@BindUser User user);

	@SqlUpdate(
			"INSERT INTO security_user" +
			"(login_id, user_id, password) VALUES " +
			"(:loginId, :userId, :password)")
	@Transaction(TransactionIsolationLevel.REPEATABLE_READ)
	public abstract long createSecurityUser(@Bind("loginId") long loginId, @Bind("userId") String userId, @Bind("password") String password);

	@SqlUpdate(
			"INSERT INTO <database_prefix>coder" +
			"(coder_id, quote, coder_type_id, comp_country_code, display_quote, quote_location, quote_color, display_banner, banner_style) VALUES " +
            "(:coderId, '', null, null,  1, 'md', '#000000', 1, 'bannerStyle4')")
	@Transaction(TransactionIsolationLevel.REPEATABLE_READ)
	public abstract long createCoder(
			@Define("database_prefix") String database_prefix, @Bind("coderId") long coderId);

	@SqlUpdate(
			"INSERT INTO email " +
			"(user_id, email_id, email_type_id, address, primary_ind, status_id) VALUES " +
			"(:userId, :emailId, 1, :email, 1, 2)")
	@Transaction(TransactionIsolationLevel.REPEATABLE_READ)
	public abstract long registerEMail(@Bind("userId") long userId, @Bind("emailId") long emailId, @Bind("email") String email);
	
	@Transaction(TransactionIsolationLevel.REPEATABLE_READ)
	public TCID create(User user) {

		SequenceDAO seqDao = (SequenceDAO)ResourceFactory.instance().getObject(SequenceDAO.class);
		Long userId = seqDao.nextVal("sequence_user_seq");

		user.setId(new TCID(userId));
		createUser(user);
		createSecurityUser(
			userId, user.getHandle(),
			Utils.encodePassword(user.getCredential().getPassword(), "users")); // TODO: password
		
		Long emailId = seqDao.nextVal("sequence_email_seq");
		registerEMail(userId, emailId, user.getEmail());
		
		// TODO: does not work (need patch to fix JDBI)
		//createCoder("'informixoltp':", userId);
		
		return user.getId();
	}
	
	public boolean handleExists(String handle) {
		User user = findUserByHandle(handle);
		return user != null;
	}
	
/*
 * SQL_INSERT_USER:
INSERT INTO user(user_id, first_name, last_name, handle, status, "
            + "activation_code, reg_source, utm_source, utm_medium, utm_campaign) " +
            "VALUES (?, ?, ?, ?, 'U', ?, ?, ?, ?, ?)"
 */
/*
 * SQL_INSERT_CODER:
"INSERT INTO 'informixoltp':coder(coder_id, quote, coder_type_id,"
            + " comp_country_code, display_quote, quote_location, quote_color, display_banner, banner_style) "
            + "VALUES(?, '', null, ?,  1, 'md', '#000000', 1, 'bannerStyle4')"
 */
/*
 * SQL_INSERT_SECURITY_USER
"INSERT INTO security_user(login_id, user_id, password, "
            + "create_user_id) VALUES(?, ?, ?, ?)"
 */
/*
 * SQL_INSERT_EMAIL
"INSERT INTO email (user_id, email_id, email_type_id, address, "
            + "primary_ind, status_id) VALUES(?, ?, 1, ?, 1, 2)"
 */
/*
 * addTopCoderMemberProfile
            ldapClient.addTopCoderMemberProfile(userId, handle, password, "U");
 */
/*
 * addUserToGroups
            List<Map<String, Object>> existingGroupIds = jdbcTemplate.queryForList(SQL_QUERY_GROUP_IDS
                    + convertArrayToSqlList(groupIds));
            if (existingGroupIds.size() < groupIds.length) {
                throw new PersistenceException("At least one of the groupIds is invalid");
            }
            List<Long> joinedGroups = jdbcTemplate.queryForList(SQL_QUERY_PERTAINED_GROUP_IDS, Long.class, userId);
            IDGenerator userGroupIDGenerator = IDGeneratorFactory.getIDGenerator("USER_GROUP_SEQ");
            for (long groupId : groupIds) {
                if (!joinedGroups.contains(groupId)) {
                    long userGroupId = userGroupIDGenerator.getNextID();
                    jdbcTemplate.update(SQL_ADD_USER_TO_GROUPS, userGroupId, userId, groupId);
                }
            }
 */
/*
            long userId = IDGeneratorFactory.getIDGenerator("USER_SEQ").getNextID();
            user.setUserId(userId);
            user.setActivationCode(StringUtils.getActivationCode(userId));
            jdbcTemplate.update(SQL_INSERT_USER, userId, user.getFirstName(), user.getLastName(), user.getHandle(),
                    user.getActivationCode(), user.getSource(), user.getUtm_source(), user.getUtm_medium(),
                    user.getUtm_campaign());
            jdbcTemplate.update(SQL_INSERT_CODER, userId, user.getCountry());
            jdbcTemplate.update(SQL_INSERT_SECURITY_USER, userId, user.getHandle(),
                    Util.encodePassword(user.getPassword(), "users"), null);
            long emailId = IDGeneratorFactory.getIDGenerator("EMAIL_SEQ").getNextID();
            jdbcTemplate.update(SQL_INSERT_EMAIL, userId, emailId, user.getEmail());

            addTopCoderMemberProfile(userId, user.getHandle(), user.getPassword());

            addUserToGroups(userId, new long[] { ANONYMOUS_GROUP_ID, USERS_GROUP_ID });
            //LoggingWrapperUtility.logExit(logger, signature, new Object[] { userId });

 */
}
