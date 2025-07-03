package us.tx.state.dfps.service.casepackage.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.casepackage.dao.SpecialHandlingCaseDetailFetchDao;
import us.tx.state.dfps.service.casepackage.dto.SpecialHandlingCaseDetailInDto;
import us.tx.state.dfps.service.casepackage.dto.SpecialHandlingCaseDetailOutDto;

@Repository
public class SpecialHandlingCaseDetailFetchDaoImpl implements SpecialHandlingCaseDetailFetchDao {
	@Autowired
	MessageSource messageSource;

	@Value("${SpecialHandlingCaseDetailFetchDaoImpl.specialHandlingCaseDetailFetch}")
	private String specialHandlingCaseDetailFetch;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger("ServiceBusiness-EmployeeDaoLog");

	/**
	 * Method Name: specialHandlingStageDetailFetch Method Description: fetch
	 * case details for special handling, DAM ccmnb1d
	 * 
	 * @param specialHandlingCaseDetailInDto
	 * @return SpecialHandlingCaseDetailOutDto @
	 */
	@Override
	public SpecialHandlingCaseDetailOutDto specialHandlingCaseDetailFetch(
			SpecialHandlingCaseDetailInDto specialHandlingCaseDetailInDto) {
		log.debug("Entering method specialHandlingCaseDetailFetch in SpecialHandlingCaseDetailFetchDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(specialHandlingCaseDetailFetch).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("sysTsLastUpdate2", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdCaseProgram", StandardBasicTypes.STRING)
				.addScalar("cdCaseCounty", StandardBasicTypes.STRING)
				.addScalar("cdCaseSpeclHndlg", StandardBasicTypes.STRING)
				.addScalar("indCaseWorkerSafety", StandardBasicTypes.STRING)
				.addScalar("txtCaseWorkerSafety", StandardBasicTypes.STRING)
				.addScalar("txtCaseSensitiveCmnts", StandardBasicTypes.STRING)
				.addScalar("indCaseSensitive", StandardBasicTypes.STRING)
				.addScalar("indCaseArchived", StandardBasicTypes.STRING)
				.addScalar("dtCaseClosed", StandardBasicTypes.DATE).addScalar("cdCaseRegion", StandardBasicTypes.STRING)
				.addScalar("dtCaseOpened", StandardBasicTypes.DATE).addScalar("nmCase", StandardBasicTypes.STRING)
				.addScalar("indCaseAlert", StandardBasicTypes.STRING)
				.addScalar("txtSpecHandling", StandardBasicTypes.STRING)
				.addScalar("indSafetyCheckList", StandardBasicTypes.STRING)
				.addScalar("indLitigationHold", StandardBasicTypes.STRING)
				.addScalar("txtLitigationHold", StandardBasicTypes.STRING)
				.setParameter("idCase", specialHandlingCaseDetailInDto.getIdCase())
				.setResultTransformer(Transformers.aliasToBean(SpecialHandlingCaseDetailOutDto.class)));

		SpecialHandlingCaseDetailOutDto ccmnb1doDto = (SpecialHandlingCaseDetailOutDto) sQLQuery1.uniqueResult();

		log.debug("Exiting method specialHandlingCaseDetailFetch in SpecialHandlingCaseDetailFetchDaoImpl");

		return ccmnb1doDto;
	}

}
