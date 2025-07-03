package us.tx.state.dfps.service.utility.dao;

import us.tx.state.dfps.common.domain.EmailLog;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION EJB Service Name: EmailUtilityBean
 * Class Description: This is used to log after emails being sent in the
 * email_log table. Aug 5, 2017 - 3:50:30 PM
 */
public interface EmailUtilityDao {
	/**
	 * 
	 * Method Description: EJB service name - EmailUtilityBean
	 * 
	 * @param emailLog
	 * @return long @
	 */
	public long insertEmailLog(EmailLog emailLog);

}
