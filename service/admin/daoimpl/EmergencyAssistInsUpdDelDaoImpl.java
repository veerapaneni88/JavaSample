package us.tx.state.dfps.service.admin.daoimpl;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.EmergencyAssist;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.service.admin.dao.EmergencyAssistInsUpdDelDao;
import us.tx.state.dfps.service.admin.dto.EmergencyAssistInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.EmergencyAssistInsUpdDelOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This class
 * inserts or updates emergency assistance details Aug 9, 2017- 4:14:28 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Repository
public class EmergencyAssistInsUpdDelDaoImpl implements EmergencyAssistInsUpdDelDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	@Value("${EmergencyAssistInsUpdDelDaoImpl.updateEmergencyAssist}")
	private transient String updateEmergencyAssist;

	@Autowired
	@Value("${EmergencyAssistInsUpdDelDaoImpl.deleteEmergencyAssist}")
	private transient String deleteEmergencyAssist;

	private static final Logger log = Logger.getLogger(EmergencyAssistInsUpdDelDaoImpl.class);

	public EmergencyAssistInsUpdDelDaoImpl() {
		super();
	}

	/**
	 * Method Name: cuEmergencyAssistanceDtls Method Description: This method
	 * inserts or updates emergency assistance details
	 * 
	 * @param pInputDataRec
	 * @return EmergencyAssistInsUpdDelOutDto @
	 */
	@Override
	public EmergencyAssistInsUpdDelOutDto cuEmergencyAssistanceDtls(EmergencyAssistInsUpdDelInDto pInputDataRec) {
		log.debug("Entering method EmergencyAssistInsUpdDelQUERYdam in EmergencyAssistInsUpdDelDaoImpl");
		int rowCount = ServiceConstants.Zero;
		EmergencyAssistInsUpdDelOutDto pOutputDataRec = null;
		boolean successInd = false;
		// If EA Question is ARC setting default as Yes (For first question)
		if (pInputDataRec.getCdEaQuestion().equalsIgnoreCase(ServiceConstants.CD_EA_QUESTION_IS_ARC)) {
			pInputDataRec.setIndEaResponse(ServiceConstants.Y);
		}
		switch (pInputDataRec.getCdReqFunction()) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			if (!TypeConvUtil.isNullOrEmpty(pInputDataRec)) {
				EmergencyAssist emergencyAssist = new EmergencyAssist();
				emergencyAssist.setIndEaResponse(pInputDataRec.getIndEaResponse());
				emergencyAssist.setCdEaQuestion(pInputDataRec.getCdEaQuestion());
				// emergencyAssist.setIdEmergencyAssist(pInputDataRec.getUlIdEmergencyAssist());
				emergencyAssist.setDtLastUpdate(pInputDataRec.getTsLastUpdate());
				if (!TypeConvUtil.isNullOrEmpty(pInputDataRec.getIdEvent())) {
					Event event = (Event) sessionFactory.getCurrentSession().get(Event.class,
							pInputDataRec.getIdEvent());
					emergencyAssist.setEvent(event);
				}
				sessionFactory.getCurrentSession().save(emergencyAssist);
				successInd = true;
			}
			break;
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			SQLQuery sQLQuery2 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(updateEmergencyAssist);
			sQLQuery2.setParameter("hI_bIndEaResponse", pInputDataRec.getIndEaResponse());
			sQLQuery2.setParameter("hI_szCdEaQuestion", pInputDataRec.getCdEaQuestion());
			// sQLQuery2.setParameter("hI_tsLastUpdate",
			// pInputDataRec.getTsLastUpdate());
			sQLQuery2.setParameter("hI_ulIdEmergencyAssist", pInputDataRec.getIdEmergencyAssist());
			sQLQuery2.setParameter("hI_ulIdEvent", pInputDataRec.getIdEvent());
			rowCount = sQLQuery2.executeUpdate();
			if (TypeConvUtil.isNullOrEmpty(rowCount)) {
				throw new DataNotFoundException(
						messageSource.getMessage("Cinv16dDaoImpl.not.found.ulIdOnCall.update", null, Locale.US));
			} else {
				successInd = true;
			}
			break;
		case ServiceConstants.REQ_FUNC_CD_DELETE:
			SQLQuery deleteQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(deleteEmergencyAssist);
			deleteQuery.setParameter("hI_ulIdEmergencyAssist", pInputDataRec.getIdEmergencyAssist());
			rowCount = deleteQuery.executeUpdate();
			if (TypeConvUtil.isNullOrEmpty(rowCount)) {
				throw new DataNotFoundException(
						messageSource.getMessage("Cinv16dDaoImpl.not.found.ulIdOnCall.update", null, Locale.US));
			} else {
				successInd = true;
			}
			break;
		}
		if (successInd) {
			pOutputDataRec = new EmergencyAssistInsUpdDelOutDto();
		}
		log.debug("Exiting method EmergencyAssistInsUpdDelQUERYdam in EmergencyAssistInsUpdDelDaoImpl");
		return pOutputDataRec;
	}
}
