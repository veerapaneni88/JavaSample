package us.tx.state.dfps.service.legal.daoimpl;

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

import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.legal.dao.FetchLegalActionDao;
import us.tx.state.dfps.service.legal.dto.CaseDbDto;
import us.tx.state.dfps.service.legal.dto.FetchLegalActionInDto;
import us.tx.state.dfps.service.legal.dto.FetchLegalActionOutDto;
import us.tx.state.dfps.service.legal.dto.PersonInfoDbDto;
import us.tx.state.dfps.service.sdmriskassessment.dto.StageDBDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Fetches the
 * Legal Action for the event Nov 1, 2017- 10:19:38 AM © 2017 Texas Department
 * of Family and Protective Services
 */
@Repository
public class FetchLegalActionDaoImpl implements FetchLegalActionDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${FetchLegalActionDaoImpl.selectLegalActionSql}")
	private transient String selectLegalActionSql;

	@Value("${FetchLegalActionDaoImpl.selectStageListSql}")
	private transient String selectStageListSql;

	@Value("${FetchLegalActionDaoImpl.getcaseSql}")
	private transient String getcaseSql;

	@Value("${FetchLegalActionDaoImpl.getpersonSql}")
	private transient String getpersonSql;

	private static final Logger log = Logger.getLogger(FetchLegalActionDaoImpl.class);

	/**
	 * Method Name: cses06dQueryDao Method Description:Retrieves the Legal
	 * Action for the event
	 * 
	 * @param cses06di
	 * @return FetchLegalActionOutDto @
	 */
	@Override
	public FetchLegalActionOutDto fetchLegalActionForEvent(FetchLegalActionInDto fetchLegalActionInDto) {
		log.debug("Entering method fetchLegalActionForEvent in FetchLegalActionDaoImpl");
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectLegalActionSql)
				.addScalar("idLegalActEvent", StandardBasicTypes.LONG)
				.addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdLegalActAction", StandardBasicTypes.STRING)
				.addScalar("cdLegalActActnSubtype", StandardBasicTypes.STRING)
				.addScalar("cdQRTPCourtStatus", StandardBasicTypes.STRING)
				.addScalar("cdLegalActOutcome", StandardBasicTypes.STRING)
				.addScalar("dtLegalActDateFiled", StandardBasicTypes.DATE)
				.addScalar("dtLegalActOutcomeDate", StandardBasicTypes.DATE)
				.addScalar("indLegalActDocsNCase", StandardBasicTypes.STRING)
				.addScalar("indLegalActActionTkn", StandardBasicTypes.STRING)
				.addScalar("txtLegalActComment", StandardBasicTypes.STRING)
				.addScalar("cdLegalActOutSub", StandardBasicTypes.STRING)
				.addScalar("indFDTCGraduated", StandardBasicTypes.STRING)
				.addScalar("cdFDTCEndReason", StandardBasicTypes.STRING)
				.setParameter("idLegalActEvent", fetchLegalActionInDto.getIdLegalActEvent())
				.setResultTransformer(Transformers.aliasToBean(FetchLegalActionOutDto.class));
		FetchLegalActionOutDto fetchLegalActionOutDto = (FetchLegalActionOutDto) sQLQuery1.uniqueResult();

		if (TypeConvUtil.isNullOrEmpty(fetchLegalActionOutDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("fetchLegalActionOutDto.not.found", null, Locale.US));
		}
		log.debug("Exiting method fetchLegalActionForEvent in FetchLegalActionDaoImpl");
		return fetchLegalActionOutDto;
	}

	/**
	 * Method Name: getStageList Method Description:The getStageList method
	 * finds all the stage information related to the user.
	 * 
	 * @param caseDbDto
	 * @return List<StageDBDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<StageDBDto> getStageList(CaseDbDto caseDbDto) {
		log.debug("Entering method getStageList in FetchLegalActionDaoImpl");
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectStageListSql)
				.addScalar("stageName", StandardBasicTypes.STRING).addScalar("cdStage", StandardBasicTypes.STRING)
				.addScalar("cdStageType", StandardBasicTypes.STRING).addScalar("dtStageStarts", StandardBasicTypes.DATE)
				.addScalar("dtStageCloses", StandardBasicTypes.DATE)
				.addScalar("cdStageRegion", StandardBasicTypes.STRING)
				.addScalar("cdStageProgram", StandardBasicTypes.STRING)
				.addScalar("situationId", StandardBasicTypes.LONG).addScalar("stageId", StandardBasicTypes.LONG)
				.addScalar("cdStageReasonClosed", StandardBasicTypes.STRING)
				.addScalar("personId", StandardBasicTypes.LONG)
				.addScalar("cdInvstDetailOverallDisposition", StandardBasicTypes.STRING)
				.addScalar("dtMultiRefs", StandardBasicTypes.DATE).addScalar("dtStagesCreated", StandardBasicTypes.DATE)
				.addScalar("indSecondApprover", StandardBasicTypes.STRING)
				.addScalar("indScreened", StandardBasicTypes.STRING).setParameter("caseId", caseDbDto.getCaseId())
				.setResultTransformer(Transformers.aliasToBean(StageDBDto.class));
		List<StageDBDto> stageDBDtoList = sQLQuery1.list();

		if (TypeConvUtil.isNullOrEmpty(stageDBDtoList)) {
			throw new DataNotFoundException(messageSource.getMessage("stageDBDtoList.not.found", null, Locale.US));
		}
		log.debug("Exiting method getStageList in FetchLegalActionDaoImpl");
		return stageDBDtoList;
	}

	/**
	 * Method Name: getCase Method Description:The getCase method selects the
	 * case information related to the user.
	 * 
	 * @param caseDbDto
	 * @return CaseDbDto @
	 */
	@Override
	public CaseDbDto getCase(CaseDbDto caseDbDto) {
		log.debug("Entering method getCase in FetchLegalActionDaoImpl");
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getcaseSql)
				.addScalar("caseId", StandardBasicTypes.LONG)
				.addScalar("indCaseWorkerSafety", StandardBasicTypes.STRING)
				.addScalar("indCaseSensitive", StandardBasicTypes.STRING)
				.addScalar("indCaseArchived", StandardBasicTypes.STRING)
				.addScalar("dtCaseClosed", StandardBasicTypes.DATE).addScalar("dtCaseOpened", StandardBasicTypes.DATE)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("cdCaseCounty", StandardBasicTypes.STRING)
				.addScalar("cdCaseProgram", StandardBasicTypes.STRING)
				.addScalar("cdCaseRegion", StandardBasicTypes.STRING)
				.addScalar("cdCaseSpecialHandling", StandardBasicTypes.STRING)
				.addScalar("txtCaseWorkerSafety", StandardBasicTypes.STRING)
				.addScalar("txtCaseSensitiveComents", StandardBasicTypes.STRING)
				.addScalar("nmCase", StandardBasicTypes.STRING).setParameter("caseId", caseDbDto.getCaseId())
				.setResultTransformer(Transformers.aliasToBean(CaseDbDto.class));
		CaseDbDto caseDbDto1 = (CaseDbDto) sQLQuery1.uniqueResult();

		if (TypeConvUtil.isNullOrEmpty(caseDbDto1)) {
			throw new DataNotFoundException(messageSource.getMessage("caseDbDto1.not.found", null, Locale.US));
		}
		log.debug("Exiting method getCase in FetchLegalActionDaoImpl");
		return caseDbDto1;
	}

	/**
	 * Method Name: getPerson Method Description:The getPerson method selects
	 * the id, full name, and phone number of the person who is assigned to the
	 * case.
	 * 
	 * @param stageDBDto
	 * @return PersonInfoDbDto @
	 */
	@Override
	public PersonInfoDbDto getPerson(StageDBDto stageDBDto) {
		log.debug("Entering method getPerson in FetchLegalActionDaoImpl");
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getpersonSql)
				.addScalar("personId", StandardBasicTypes.LONG).addScalar("personName", StandardBasicTypes.STRING)
				.addScalar("personPhone", StandardBasicTypes.STRING).setParameter("idPerson", stageDBDto.getPersonId())
				.setResultTransformer(Transformers.aliasToBean(PersonInfoDbDto.class));
		PersonInfoDbDto personInfoDbDto = (PersonInfoDbDto) sQLQuery1.uniqueResult();

		if (TypeConvUtil.isNullOrEmpty(personInfoDbDto)) {
			throw new DataNotFoundException(messageSource.getMessage("personInfoDbDto.not.found", null, Locale.US));
		}
		log.debug("Exiting method getPerson in FetchLegalActionDaoImpl");
		return personInfoDbDto;
	}
}
