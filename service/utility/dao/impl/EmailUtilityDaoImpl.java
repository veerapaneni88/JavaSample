package us.tx.state.dfps.service.utility.dao.impl;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.EmailLog;
import us.tx.state.dfps.service.utility.dao.EmailUtilityDao;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION EJB Service Name: EmailUtilityBean
 * Class Description: This is used to log after emails being sent in the
 * email_log table. Aug 5, 2017 - 3:50:30 PM
 */
@Repository
public class EmailUtilityDaoImpl implements EmailUtilityDao {

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(EmailUtilityDaoImpl.class);

	public EmailUtilityDaoImpl() {
	}

	/**
	 * 
	 * Method Description: EJB service name - EmailUtilityBean
	 * 
	 * @param emailLog
	 * @return int
	 */
	public long insertEmailLog(EmailLog emailLog) {

		sessionFactory.getCurrentSession().save("EmailLog", emailLog);
		log.debug("Email Log Id: " + emailLog.getIdEmailLog());
		return emailLog.getIdEmailLog();
	}

}
