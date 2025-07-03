package us.tx.state.dfps.service.admin.daoimpl;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dao.EventRiskAssessmentDao;
import us.tx.state.dfps.service.admin.dto.EventRiskAssessmentInDto;
import us.tx.state.dfps.service.admin.dto.EventRiskAssessmentOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:DAO Impl
 * class for fetching event details Aug 4, 2017- 11:11:49 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class EventRiskAssessmentDaoImpl implements EventRiskAssessmentDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${EventRiskAssessmentDaoImpl.getEventDetails}")
	private transient String getEventDetails;

	@Value("${EventRiskAssessmentDaoImpl.getEventOrdered}")
	private transient String getEventOrdered;

	@Value("${EventRiskAssessmentDaoImpl.getEventOrderedDesc}")
	private transient String getEventOrderedDesc;

	private static final Logger log = Logger.getLogger(EventRiskAssessmentDaoImpl.class);

	public EventRiskAssessmentDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getEvent Method Description: This method will get data from
	 * Event table.
	 * 
	 * @param pInputDataRec
	 * @return List<EventRiskAssessmentOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EventRiskAssessmentOutDto> getEvent(EventRiskAssessmentInDto pInputDataRec) {
		log.debug("Entering method EventRiskAssessmentQUERYdam in EventRiskAssessmentDaoImpl");
		String strCCMNB4D1_CURSORQuery = null;
		String strCCMNB4D_CURSORQuery = null;
		List<EventRiskAssessmentOutDto> liCcmnb4doDto = null;
		if ((pInputDataRec.getCdTask().equalsIgnoreCase(ServiceConstants.RISK_ASSMNT_TASK))
				|| (pInputDataRec.getCdTask().equalsIgnoreCase(ServiceConstants.RISK_ASSESSMENT_SUB))
				|| (pInputDataRec.getCdTask().equalsIgnoreCase(ServiceConstants.RISK_ASSESSMENT_ADO))
				|| (pInputDataRec.getCdTask().equalsIgnoreCase(ServiceConstants.RISK_ASSESSMENT_FPR))) {
			strCCMNB4D1_CURSORQuery = this.getEventDetails;
		} else if (!pInputDataRec.getCdTask().equalsIgnoreCase(ServiceConstants.ELIG_DETERM_SUB)
				|| !pInputDataRec.getCdTask().equalsIgnoreCase(ServiceConstants.ELIG_DETERM_ADO)
				|| !pInputDataRec.getCdTask().equalsIgnoreCase(ServiceConstants.ELIG_DETERM_PAD)) {
			strCCMNB4D_CURSORQuery = this.getEventOrdered;
		} else {
			strCCMNB4D_CURSORQuery = this.getEventOrderedDesc;
		}
		if (pInputDataRec.getCdTask().equalsIgnoreCase(ServiceConstants.RISK_ASSMNT_TASK)) {
			SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strCCMNB4D1_CURSORQuery)
					.setResultTransformer(Transformers.aliasToBean(EventRiskAssessmentOutDto.class)));
			sQLQuery1.addScalar("idEvent", StandardBasicTypes.LONG)
					.addScalar("cdEventStatus", StandardBasicTypes.STRING)
					.addScalar("indRiskAssess", StandardBasicTypes.STRING)
					.setParameter("hI_ulIdStage", pInputDataRec.getIdStage())
					.setParameter("hI_szCdTask", pInputDataRec.getCdTask());
			liCcmnb4doDto = (List<EventRiskAssessmentOutDto>) sQLQuery1.list();
			if (TypeConvUtil.isNullOrEmpty(liCcmnb4doDto)) {
				throw new DataNotFoundException(
						messageSource.getMessage("Ccmnb4dDaoImpl.not.found.event", null, Locale.US));
			}
		} else {
			SQLQuery sQLQuery2 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strCCMNB4D_CURSORQuery)
					.setResultTransformer(Transformers.aliasToBean(EventRiskAssessmentOutDto.class)));
			sQLQuery2.addScalar("idEvent", StandardBasicTypes.LONG)
					.addScalar("cdEventStatus", StandardBasicTypes.STRING)
					.setParameter("hI_ulIdStage", pInputDataRec.getIdStage())
					.setParameter("hI_szCdTask", pInputDataRec.getCdTask());
			liCcmnb4doDto = (List<EventRiskAssessmentOutDto>) sQLQuery2.list();
			if (TypeConvUtil.isNullOrEmpty(liCcmnb4doDto)) {
				throw new DataNotFoundException(
						messageSource.getMessage("Ccmnb4dDaoImpl.not.found.event", null, Locale.US));
			}
		}
		log.debug("Exiting method EventRiskAssessmentQUERYdam in EventRiskAssessmentDaoImpl");
		return liCcmnb4doDto;
	}
}
