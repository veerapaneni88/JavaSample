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

import us.tx.state.dfps.common.domain.Eligibility;
import us.tx.state.dfps.service.admin.dao.EligibilityDao;
import us.tx.state.dfps.service.admin.dto.EligibilityInDto;
import us.tx.state.dfps.service.admin.dto.EligibilityOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This DAO
 * retrieves the currently active record of ELIGIBILITY and is passed back to
 * main function Aug 9, 2017- 2:45:12 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Repository
public class EligibilityDaoImpl implements EligibilityDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${EligibilityDaoImpl.getEligibilityRecord}")
	private String getEligibilityRecord;

	private static final Logger log = Logger.getLogger(EligibilityDaoImpl.class);

	public EligibilityDaoImpl() {
		super();
	}

	/**
	 * Method Name: getEligibilityRecord Method Description: It retrieves the
	 * currently active record of ELIGIBILITY
	 * 
	 * @param pInputDataRec
	 * @param pOutputDataRec
	 * @return List<Cses38doDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EligibilityOutDto> getEligibilityRecord(EligibilityInDto pInputDataRec) {
		log.debug("Entering method EligibilityQUERYdam in EligibilityDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getEligibilityRecord)
				.addScalar("idEligibilityEvent", StandardBasicTypes.LONG)
				.addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idPersonUpdate", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("cdEligActual", StandardBasicTypes.STRING)
				.addScalar("cdEligCsupQuest1", StandardBasicTypes.STRING)
				.addScalar("cdEligCsupQuest2", StandardBasicTypes.STRING)
				.addScalar("cdEligCsupQuest3", StandardBasicTypes.STRING)
				.addScalar("cdEligCsupQuest4", StandardBasicTypes.STRING)
				.addScalar("cdEligCsupQuest5", StandardBasicTypes.STRING)
				.addScalar("cdEligCsupQuest6", StandardBasicTypes.STRING)
				.addScalar("cdEligCsupQuest7", StandardBasicTypes.STRING)
				.addScalar("cdEligMedEligGroup", StandardBasicTypes.STRING)
				.addScalar("cdEligSelected", StandardBasicTypes.STRING)
				.addScalar("dtEligCsupReferral", StandardBasicTypes.DATE)
				.addScalar("dtEligEnd", StandardBasicTypes.DATE).addScalar("dtEligReview", StandardBasicTypes.DATE)
				.addScalar("dtEligStart", StandardBasicTypes.DATE)
				.addScalar("indEligCsupSend", StandardBasicTypes.STRING)
				.addScalar("indEligWriteHistory", StandardBasicTypes.STRING)
				.addScalar("eligComment", StandardBasicTypes.STRING)
				.setParameter("hI_dtScrDtCurrentDate", pInputDataRec.getDtScrDtCurrentDate())
				.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson())
				.setResultTransformer(Transformers.aliasToBean(EligibilityOutDto.class)));
		List<EligibilityOutDto> liCses38doDto = (List<EligibilityOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liCses38doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Cses38dDaoImpl.no.active.eligibiliy.record", null, Locale.US));
		}
		log.debug("Exiting method EligibilityQUERYdam in EligibilityDaoImpl");
		return liCses38doDto;
	}

	@Override
	public Eligibility getEligibilityByEligibilityEventId(Long idEligibilityEvent) {
		// TODO Auto-generated method stub
		return null;
	}
}
