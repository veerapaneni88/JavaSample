package us.tx.state.dfps.service.casemanagement.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.arinvconclusion.dto.ArEaEligibilityDto;
import us.tx.state.dfps.arinvconclusion.dto.ArInvCnclsnDto;
import us.tx.state.dfps.common.domain.ApprovalEventLink;
import us.tx.state.dfps.common.domain.AssessmentHouseholdLink;
import us.tx.state.dfps.common.domain.CpsArCnclsnDetail;
import us.tx.state.dfps.common.domain.EmergencyAssist;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.dto.CpsArInvCnclsnDto;
import us.tx.state.dfps.common.dto.ServiceReferralDto;
import us.tx.state.dfps.service.casemanagement.dao.ArHelperDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.person.dto.CriminalHistoryDto;
import us.tx.state.dfps.service.safetyassessment.dto.SafetyFactorDto;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

@Repository
public class ArHelperDaoImpl implements ArHelperDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ArHelperDaoImpl.isRiskIndicated}")
	private String getIsRiskIndicatedSql;

	@Value("${ArHelperDaoImpl.hasBeenSubmittedForApproval}")
	private String hasBeenSubmittedForApprovalsql;

	@Value("${ArHelperDaoImpl.getContactPurposeFamAssmtRScheduling}")
	private String getContactPurposeFamAssmtRSchedulingSql;

	@Value("${ArHelperDaoImpl.isLegalActionExists}")
	private String isLegalActionExistsSql;

	@Value("${ArHelperDaoImpl.isLegalActionPending}")
	private String isLegalActionPendingSql;

	@Value("${ArHelperDaoImpl.isInitialSftyAssmntRejectedOrPending}")
	private String isInitialSftyAssmntRejectedOrPendingSql;

	@Value("${ArHelperDaoImpl.isPendingDayCareApprvalExists}")
	private String isPendingDayCareApprvalExistsSql;

	@Value("${ArHelperDaoImpl.isPendingSeviceAuthApprvalExists}")
	private String isPendingSeviceAuthApprvalExistsSql;

	@Value("${ArHelperDaoImpl.isPendingIntlSafetyAssmtApprvalExists}")
	private String isPendingIntlSafetyAssmtApprvalExistsSql;

	@Value("${ArHelperDaoImpl.isExtensionRequestPending}")
	private String isExtensionRequestPendingSql;

	@Value("${ArHelperDaoImpl.isMoreReferenceChildExists}")
	private String isMoreReferenceChildExistsSql;

	@Value("${ArHelperDaoImpl.getSafetyAssessmentInfo}")
	private String getSafetyAssessmentInfoSql;

	@Value("${ArHelperDaoImpl.getApprovalEvents}")
	private String getApprovalEventsSql;

	@Value("${ArHelperDaoImpl.getEventByStageAndEventType}")
	private String getEventByStageAndEventTypeSql;

	@Value("${ArHelperDaoImpl.getPersonChar}")
	private String getPersonCharSql;

	@Value("${ArHelperDaoImpl.getArServiceRefrl}")
	private String getArServiceReferlSql;

	@Value("${ArHelperDaoImpl.getidAssessmentHousehold}")
	private String getIdAssmntHsHldSql;

	@Value("${ArHelperDaoImpl.updIdAssmtHsHld}")
	private String updIdAssmntHsHldSql;

	@Value("${ArHelperDaoImpl.isInitialSDMsafetycomplete}")
	private String isInitSdmComplete;

	@Value("${ArHelperDaoImpl.isSAQuestionAnswered}")
	private String isSAQuestionAnswered;

	@Value("${ArHelperDaoImpl.isFsuOpened}")
	private String isFsuOpenedSql;

	@Value("${ArHelperDaoImpl.isSdmRaComplete}")
	private String isSdmRaCompleteSql;

	@Value("${ArHelperDaoImpl.isPcpComplete}")
	private String isPcspCompleteSql;

	@Value("${ArHelperDaoImpl.isPcpCompletewith060}")
	private String isPcspCompletewith060Sql;

	@Value("${ArHelperDaoImpl.CdSafetyDecn}")
	private String cdSafetyDecnSql;

	@Value("${ArHelperDaoImpl.isPrintNotification}")
	private String isPrintNotification;

	@Value("${ArHelperDaoImpl.isPcspProc}")
	private String isPcspProc;

	@Value("${ArHelperDaoImpl.isSdmSaProc}")
	private String isSdmSaProc;

	@Value("${ArHelperDaoImpl.getConclusionEventId}")
	private String getConclusionEventId;

	@Value("${ArHelperDaoImpl.checkCrimHistPersonDPSPostWSRelease}")
	private String checkCrimHistPersonDPSPostWSRelease;

	@Value("${ArHelperDaoImpl.checkCrimHistPersonDPSPreWSRelease}")
	private String checkCrimHistPersonDPSPreWSRelease;

	@Value("${ArHelperDaoImpl.checkCrimHistPersonExceptDPS}")
	private String checkCrimHistPersonExceptDPS;

	@Value("${ArHelperDaoImpl.selectARConclusion}")
	private String selectARConclusion;

	@Value("${ArHelperDaoImpl.getMethamphetamineFromIntake}")
	private String getMethamphetamineFromIntake;

	@Value("${ArHelperDaoImpl.isFbssReferralApproved}")
	private String isFbssReferralApproved;

	@Autowired
	TodoDao todoDao;

	@Autowired
	EventDao eventDao;

	public ArHelperDaoImpl() {

	}

	public Long isRiskIndicated(Long idCase) {

		Long count = 0l;
		count = (Long) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getIsRiskIndicatedSql)
				.setParameter("idCase", idCase)).addScalar("count", StandardBasicTypes.LONG).uniqueResult();
		return count;
	}

	@Override
	public Long getContactPurposeFamAssmtRScheduling(Long idCase) {

		Long count = 0l;
		count = (Long) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getContactPurposeFamAssmtRSchedulingSql).setParameter("idCase", idCase))
						.addScalar("COUNT", StandardBasicTypes.LONG).uniqueResult();
		return count;
	}

	@Override
	public Long isLegalActionExists(Long idCase) {
		Long count = 0l;
		count = (Long) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(isLegalActionExistsSql)
				.setParameter("idCase", idCase)).addScalar("COUNT", StandardBasicTypes.LONG).uniqueResult();
		return count;
	}

	@Override
	public Long isPendingDayCareApprvalExists(Long idStage) {
		Long count = 0l;
		count = (Long) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(isPendingDayCareApprvalExistsSql)
				.setParameter("idStage", idStage)).addScalar("COUNT", StandardBasicTypes.LONG).uniqueResult();
		return count;
	}

	@Override
	public Long isPendingSeviceAuthApprvalExists(Long idStage) {
		Long count = 0l;
		count = (Long) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(isPendingSeviceAuthApprvalExistsSql).setParameter("idStage", idStage))
						.addScalar("count", StandardBasicTypes.LONG).uniqueResult();
		return count;
	}

	public Long isPendingIntlSafetyAssmtApprvalExists(Long idStage) {
		Long count = 0l;
		count = (Long) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(isPendingIntlSafetyAssmtApprvalExistsSql).setParameter("idStage", idStage))
						.addScalar("count", StandardBasicTypes.LONG).uniqueResult();
		return count;
	}

	@Override
	public Long isInitialSftyAssmntRejectedOrPending(Long idStage) {
		Long count = 0l;
		count = (Long) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(isInitialSftyAssmntRejectedOrPendingSql).setParameter("idStage", idStage))
						.addScalar("COUNT", StandardBasicTypes.LONG).uniqueResult();
		return count;
	}

	@Override
	public Long isExtensionRequestPending(Long idStage) {
		Long count = 0l;
		count = (Long) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(isExtensionRequestPendingSql)
				.setParameter("idStage", idStage)).addScalar("count", StandardBasicTypes.LONG).uniqueResult();
		return count;
	}

	@Override
	public Long isMoreReferenceChildExists(Long idStage) {
		Long count = 0l;
		count = (Long) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(isMoreReferenceChildExistsSql)
				.setParameter("idStage", idStage)).addScalar("COUNT", StandardBasicTypes.LONG).uniqueResult();
		return count;
	}

	public Long getApprovalEvents(Long idEvent) {
		Long count = 0l;
		count = (Long) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getIsRiskIndicatedSql)
				.setParameter("idEvent", idEvent)).addScalar("count", StandardBasicTypes.LONG).uniqueResult();
		return count;
	}

	@Override
	public String isLegalActionPending(Long idStage) {
		String status = null;
		status = (String) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(isLegalActionPendingSql)
				.setParameter("idStage", idStage)).addScalar("STATUS", StandardBasicTypes.STRING).uniqueResult();
		return status;
	}

	@Override
	public Long getServiceReferral(Long idStage) {
		return null;
	}

	public Long getEventByStageAndEventType(Long idStage, String evntType) {

		Long idEvent = (Long) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getEventByStageAndEventTypeSql).setParameter("idStage", idStage))
						.addScalar("idEvent", StandardBasicTypes.LONG).uniqueResult();

		return idEvent;

	}

	@Override
	public Long hasBeenSubmittedForApproval(Long ulIdEvent) {
		Long count = 0l;
		count = (Long) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(hasBeenSubmittedForApprovalsql)
				.setParameter("idEvent", ulIdEvent)).addScalar("count", StandardBasicTypes.LONG).uniqueResult();
		return count;
	}

	@SuppressWarnings("unchecked")
	public List<PersonDto> getPersonCharacteristics(Long idStage) {

		List<PersonDto> personCharacteristics = new ArrayList<>();

		personCharacteristics = (List<PersonDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getPersonCharSql).setParameter("idStage", idStage)).addScalar("nmPersonFull")
						.addScalar("cdPersonSuffix").addScalar("personAge", StandardBasicTypes.INTEGER)
						.addScalar("cdPersonSex").addScalar("idPerson", StandardBasicTypes.LONG)
						.addScalar("indPersonDobApprox").addScalar("dtPersonBirth", StandardBasicTypes.TIMESTAMP)
						.addScalar("cdPersonLivArr").addScalar("cdPersonChar").addScalar("cdPersonMaritalStatus")
						.addScalar("dtPersonDeath", StandardBasicTypes.TIMESTAMP).addScalar("nmPersonFirst")
						.addScalar("nmPersonLast").addScalar("cdStagePersSearchInd").addScalar("cdStagePersType")
						.addScalar("cdStagePersRelInt").addScalar("cdStagePersRole").addScalar("indStagePersReporter")
						.addScalar("dtLastUpdate").addScalar("idStagePersonLink", StandardBasicTypes.LONG)
						.addScalar("cdPersonEthnicGroup")
						.setResultTransformer(Transformers.aliasToBean(PersonDto.class)).list();

		return personCharacteristics;

	}

	@SuppressWarnings("unchecked")
	public List<ServiceReferralDto> getServiceReferrals(Long idStage) {

		List<ServiceReferralDto> serviceReferralDto = new ArrayList<>();

		serviceReferralDto = (List<ServiceReferralDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getArServiceReferlSql).setParameter("idStage", idStage))
						.addScalar("idRfrl", StandardBasicTypes.LONG).addScalar("idEvent", StandardBasicTypes.LONG)
						.addScalar("idStage", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
						.addScalar("indrfrl", StandardBasicTypes.STRING).addScalar("cdPersRef").addScalar("cdSrType")
						.addScalar("cdSrSubtype").addScalar("dtRefrl", StandardBasicTypes.TIMESTAMP)
						.addScalar("txtComments").addScalar("cdFinalOutcome")
						.addScalar("dtCreated", StandardBasicTypes.TIMESTAMP)
						.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
						.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
						.setResultTransformer(Transformers.aliasToBean(ServiceReferralDto.class)).list();

		return serviceReferralDto;

	}

	public Long getIdAssessmentHousehold(Long idEvent) {

		Long idAssmntHsHld = (Long) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getIdAssmntHsHldSql)
				.setParameter("idEvent", idEvent)).addScalar("idAssmntHsHld", StandardBasicTypes.LONG).uniqueResult();

		return idAssmntHsHld;

	}

	public void updIdAssessmentHousehold(Long idEvent, String idAssmntHousehold) {

		sessionFactory.getCurrentSession().createSQLQuery(updIdAssmntHsHldSql)
				.setParameter("idAssmntHousehold", idAssmntHousehold).setParameter("idEvent", idEvent).executeUpdate();

	}

	/**
	 * Method Description: Method to get ArConclusionDetail characteristics.
	 * 
	 * @param commonHelperReq
	 * @return
	 */
	public CpsArInvCnclsnDto getArinvCnclsnDetail(CommonHelperReq commonHelperReq) {

		CpsArInvCnclsnDto cpsArInvCnclsnDto = new CpsArInvCnclsnDto();

		CpsArCnclsnDetail cpsArCnclsnDetail = (CpsArCnclsnDetail) sessionFactory.getCurrentSession()
				.load(CpsArCnclsnDetail.class, (commonHelperReq.getIdEvent()));
		BeanUtils.copyProperties(cpsArCnclsnDetail, cpsArInvCnclsnDto);
		cpsArInvCnclsnDto.setImmAction(cpsArCnclsnDetail.getTxtImmAction());
		cpsArInvCnclsnDto.setChildSafety(cpsArCnclsnDetail.getTxtChildSafety());
		cpsArInvCnclsnDto.setFinalAssmtCnclsn(cpsArCnclsnDetail.getTxtFinalAssmtCnclsn());
		cpsArInvCnclsnDto.setRiskFindReasons(cpsArCnclsnDetail.getTxtRiskFindReasons());
		cpsArInvCnclsnDto.setRsnArClsd(cpsArCnclsnDetail.getTxtRsnArClsd());
		cpsArInvCnclsnDto.setRsnArOpnSrvcs(cpsArCnclsnDetail.getTxtRsnArOpnSrvcs());
		return cpsArInvCnclsnDto;

	}

	/**
	 * Method Description: Method to save ArConclusionDetail characteristics.
	 * 
	 * @param CpsArCnclsnDetail
	 * @return
	 */

	public CpsArInvCnclsnDto saveArinvCnclsnDetail(CpsArCnclsnDetail cpsArCnclsnDetail) {

		CpsArInvCnclsnDto cpsArInvCnclsnDto = new CpsArInvCnclsnDto();

		cpsArCnclsnDetail.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().saveOrUpdate(cpsArCnclsnDetail);

		BeanUtils.copyProperties(cpsArCnclsnDetail, cpsArInvCnclsnDto);
		return cpsArInvCnclsnDto;

	}

	/**
	 * Method Description: Method to fetch if initialSafety assessment is
	 * complete.
	 * 
	 * @param CpsArCnclsnDetail
	 * @return
	 */

	public Long isInitialSafetyassessmentComplete(Long idEvent) {

		Long count = (Long) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(isInitSdmComplete)
				.setParameter("idEvent", idEvent)).addScalar("count", StandardBasicTypes.LONG).uniqueResult();

		return count;

	}

	/**
	 * Method Description: Method to fetch if Question is answered on
	 * assessment.
	 * 
	 * @param CpsArCnclsnDetail
	 * @return
	 */

	public String isSaQuestionAnswered(Long idCpsSa, Long question) {

		String answer = (String) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(isSAQuestionAnswered)
				.setParameter("idCpsSa", idCpsSa).setParameter("question", question))
						.addScalar("answer", StandardBasicTypes.STRING).uniqueResult();

		return answer;

	}

	/**
	 * Method Description: Method toCheck if an FSU stage has been opened for a
	 * case
	 * 
	 * @param CpsArCnclsnDetail
	 * @return
	 */

	public Long isFsuStageOpened(Long idCase) {

		Long count = (Long) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(isFsuOpenedSql)
				.setParameter("idCase", idCase)).addScalar("count", StandardBasicTypes.LONG).uniqueResult();

		return count;

	}

	/**
	 * Method Description: Method toCheck if SDM Risk Assessment is complete
	 * 
	 * @param CpsArCnclsnDetail
	 * @return
	 */

	public Long isSdmRaComplete(Long idCpsSa) {

		Long count = (Long) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(isSdmRaCompleteSql)
				.setParameter("idCpsSa", idCpsSa)).addScalar("count", StandardBasicTypes.LONG).uniqueResult();

		return count;

	}

	/**
	 * Method Description: Method toCheck if PCSP is complete
	 * 
	 * @param CpsArCnclsnDetail
	 * @return
	 */

	public Long isPcspComplete(Long idStage) {

		Long count = (Long) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(isPcspCompleteSql)
				.setParameter("idStage", idStage).setParameter("pcspDesc", ServiceConstants.PCSP_EVENT))
						.addScalar("count", StandardBasicTypes.LONG).uniqueResult();

		return count;

	}

	/**
	 * Method Description: Method toCheck if PCSP is complete
	 * 
	 * @param CpsArCnclsnDetail
	 * @return
	 */

	public Long isPcspCompletewith060(Long idStage) {

		Long count = (Long) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(isPcspCompletewith060Sql)
				.setParameter("idStage", idStage)).addScalar("count", StandardBasicTypes.LONG).uniqueResult();

		return count;

	}

	/**
	 * Method Description: Method to fetch decision of Safety assessment.
	 * 
	 * @param CpsArCnclsnDetail
	 * @return
	 */

	public String getCdSafetyDecn(Long idCpsSa) {

		String decision = (String) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(cdSafetyDecnSql)
				.setParameter("idCpsSa", idCpsSa)).addScalar("decision", StandardBasicTypes.STRING).uniqueResult();

		return decision;

	}

	/**
	 * Method Description: Method to fetch print notification
	 * 
	 * @param idEvent
	 * @return Boolean
	 * 
	 */
	public Boolean getPrintNotificationFlag(Long idEvent) {
		Long count = (Long) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(isPrintNotification)
				.setParameter("idEvent", idEvent)).addScalar("count", StandardBasicTypes.LONG).uniqueResult();

		return count == 0;

	}

	/**
	 * Method Description: Method to check if pcsp in proc
	 * 
	 * @param idEvent
	 * @return Boolean
	 * 
	 */
	public String isPcspProc(Long idStage) {

		String count = (String) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(isPcspProc)
				.setParameter("idStage", idStage)).addScalar("ASMN_EXISTS", StandardBasicTypes.STRING).uniqueResult();

		return count;

	}

	/**
	 * Method Description: Method to check if SDM in proc
	 * 
	 * @param idEvent
	 * @return Boolean
	 */
	public Long isSdmSaProc(Long idStage) {

		Long count = (Long) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(isSdmSaProc)
				.setParameter("idStage", idStage)).addScalar("count", StandardBasicTypes.LONG).uniqueResult();

		return count;

	}

	/**
	 * getConclusionEventId getting Conclusion EventId using approval event Id
	 * 
	 */

	public Long getConclusionEventId(Long idEvent, String cdTask) {
		Long eventId = 0l;
		Long conclusionIdEvent = (Long) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getConclusionEventId).setParameter("idEvent", idEvent).setParameter("cdTask", cdTask))
						.addScalar("IdEvent", StandardBasicTypes.LONG).uniqueResult();

		if (!TypeConvUtil.isNullOrEmpty(conclusionIdEvent)) {
			return conclusionIdEvent;
		}
		return eventId;

	}

	/**
	 * Method Name: checkCrimHistPerson Method Description: This method returns
	 * true if the criminal history action is null, else will return false
	 * 
	 * @param idPerson
	 * @return boolean
	 */
	@Override
	public boolean checkCrimHistPerson(long idPerson) {
		List<CriminalHistoryDto> checkCrimHistPersonList = new ArrayList<CriminalHistoryDto>();
		boolean hasCriminalHistory = false;
		SQLQuery sqlQuery;

		sqlQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(checkCrimHistPersonExceptDPS)
				.setResultTransformer(Transformers.aliasToBean(CriminalHistoryDto.class)));
		sqlQuery.addScalar(ServiceConstants.IDRECCHECKPERSON, StandardBasicTypes.LONG);
		sqlQuery.setParameter(ServiceConstants.IDRECCHECKPERSONR, idPerson);

		checkCrimHistPersonList = sqlQuery.list();
		if (!checkCrimHistPersonList.isEmpty()) {
			hasCriminalHistory = true;
		}

		if (!hasCriminalHistory) {
			sqlQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(checkCrimHistPersonDPSPreWSRelease)
					.setResultTransformer(Transformers.aliasToBean(CriminalHistoryDto.class)));
			sqlQuery.addScalar(ServiceConstants.IDRECCHECKPERSON, StandardBasicTypes.LONG);
			sqlQuery.setParameter(ServiceConstants.IDRECCHECKPERSONR, idPerson);
			checkCrimHistPersonList = sqlQuery.list();
			if (!checkCrimHistPersonList.isEmpty()) {
				hasCriminalHistory = true;
			}
		}

		if (!hasCriminalHistory) {
			sqlQuery = ((SQLQuery) sessionFactory.getCurrentSession()
					.createSQLQuery(checkCrimHistPersonDPSPostWSRelease)
					.setResultTransformer(Transformers.aliasToBean(CriminalHistoryDto.class)));
			sqlQuery.addScalar(ServiceConstants.IDRECCHECKPERSON, StandardBasicTypes.LONG);
			sqlQuery.setParameter(ServiceConstants.IDRECCHECKPERSONR, idPerson);
			checkCrimHistPersonList = sqlQuery.list();
			if (!checkCrimHistPersonList.isEmpty()) {
				hasCriminalHistory = true;
			}
		}
		return hasCriminalHistory;

	}

	@Override
	public AssessmentHouseholdLink getIdHouseholdFromAssessmentHousehold(Long idAssessmentHouseholdLink) {
		AssessmentHouseholdLink assessmentHousehold = (AssessmentHouseholdLink) sessionFactory.getCurrentSession()
				.load(AssessmentHouseholdLink.class, idAssessmentHouseholdLink);
		return assessmentHousehold;
	}

	@Override
	public CpsArCnclsnDetail getArinvCnclsnDetail(Long idStage) {
		CpsArCnclsnDetail cpsArCnclsnDetail = new CpsArCnclsnDetail();
		Criteria criteriaArDetail = sessionFactory.getCurrentSession().createCriteria(CpsArCnclsnDetail.class)
				.add(Restrictions.eq("idStage", idStage));
		cpsArCnclsnDetail = (CpsArCnclsnDetail) criteriaArDetail.uniqueResult();
		return cpsArCnclsnDetail;
	}

	/**
	 * Method Name: selectARConclusion Method Description: get the AR Conclusion
	 * Detail for the current Stage.
	 * 
	 * @param idStage
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ArInvCnclsnDto selectARConclusion(Long idStage) {

		ArInvCnclsnDto arInvCnclsnDto = new ArInvCnclsnDto();

		// Query the AR Conclusion Details
		Query arInvCnclsnsqlQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectARConclusion)
				.setParameter("idStage", idStage)).addScalar("idCase", StandardBasicTypes.LONG)
						.addScalar("idStage", StandardBasicTypes.LONG).addScalar("idEvent", StandardBasicTypes.LONG)
						.addScalar("dtBegun", StandardBasicTypes.DATE).addScalar("dtCompleted", StandardBasicTypes.DATE)
						.addScalar("cdAROverallDisposition", StandardBasicTypes.STRING)
						.addScalar("indParentGivenGuide", StandardBasicTypes.STRING)
						.addScalar("indParentsLivingOutside", StandardBasicTypes.STRING)
						.addScalar("indAbsentParentGuide", StandardBasicTypes.STRING)
						.addScalar("indMultiplePersonsFound", StandardBasicTypes.STRING)
						.addScalar("indMultiplePersonsMerged", StandardBasicTypes.STRING)
						.addScalar("indVrblWrtnNotifRights", StandardBasicTypes.STRING)
						.addScalar("indCopyGuideCpi", StandardBasicTypes.STRING)
						.addScalar("indNotifRightsUpld", StandardBasicTypes.STRING)
						.addScalar("indMeth", StandardBasicTypes.STRING)
						.addScalar("indFTMOffered", StandardBasicTypes.STRING)
						.addScalar("indFTMOccured", StandardBasicTypes.STRING)
						.addScalar("indLegalOrdinanceRequired", StandardBasicTypes.STRING)
						.addScalar("cdFamilyIncome", StandardBasicTypes.STRING)
						.addScalar("dtCreated", StandardBasicTypes.DATE)
						.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
						.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.addScalar("cdEventStatus", StandardBasicTypes.STRING)
						.addScalar("indArEaEligible", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(ArInvCnclsnDto.class));
		arInvCnclsnDto = (ArInvCnclsnDto) arInvCnclsnsqlQuery.uniqueResult();

		// Setting the infor methamphetamine manufacturing alleged at intake
		String IndMethAllgdLongake = (String) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getMethamphetamineFromIntake).setParameter("idStage", idStage))
						.addScalar("indIncmgSuspMeth", StandardBasicTypes.STRING).uniqueResult();
		if (!ObjectUtils.isEmpty(IndMethAllgdLongake)) {
			arInvCnclsnDto.setIndMethAllgdLongake(IndMethAllgdLongake);
		}

		// Query the Safety Assessment Info
		if (!ObjectUtils.isEmpty(arInvCnclsnDto)) {
			Query getSafetyAssessmentInfosqlQuery = ((SQLQuery) sessionFactory.getCurrentSession()
					.createSQLQuery(getSafetyAssessmentInfoSql).setParameter("idCase", arInvCnclsnDto.getIdCase()))
							.addScalar("idSafetyFactor", StandardBasicTypes.LONG)
							.addScalar("cdSafetyFactorResponse", StandardBasicTypes.STRING)
							.setResultTransformer(Transformers.aliasToBean(SafetyFactorDto.class));
			List<SafetyFactorDto> safetyFactorDtoList = (List<SafetyFactorDto>) getSafetyAssessmentInfosqlQuery.list();

			if (!ObjectUtils.isEmpty(safetyFactorDtoList)) {
				for (SafetyFactorDto safetyFactorDto : safetyFactorDtoList) {
					if (safetyFactorDto.getIdSafetyFactor().equals(ServiceConstants.ID_FACTOR_76)) {
						arInvCnclsnDto.setImmediateAction(safetyFactorDto.getCdSafetyFactorResponse());
					} else if (safetyFactorDto.getIdSafetyFactor().equals(ServiceConstants.ID_FACTOR_77)) {
						arInvCnclsnDto.setChildSafety(safetyFactorDto.getCdSafetyFactorResponse());
					} else if (safetyFactorDto.getIdSafetyFactor().equals(ServiceConstants.ID_FACTOR_78)) {
						arInvCnclsnDto.setFinalAssessmentConclusion(safetyFactorDto.getCdSafetyFactorResponse());
					} else if (safetyFactorDto.getIdSafetyFactor().equals(ServiceConstants.ID_FACTOR_79)) {
						arInvCnclsnDto.setReasonRiskFindings(safetyFactorDto.getCdSafetyFactorResponse());
					}
				}
			}
		}
		return arInvCnclsnDto;

	}

	/**
	 * Method Name: updateARConclusion Method Description: update the AR
	 * Conclusion and AR EA Eligibility Details
	 * 
	 * @param ArInvCnclsnDto
	 * @return
	 */
	public void updateARConclusion(ArInvCnclsnDto arInvCnclsnDto) {

		// Update the AR Conclusion Details
		CpsArCnclsnDetail cpsArCnclsnDetailForUpdate = (CpsArCnclsnDetail) sessionFactory.getCurrentSession()
				.get(CpsArCnclsnDetail.class, arInvCnclsnDto.getIdEvent());
		cpsArCnclsnDetailForUpdate.setDtBegun(arInvCnclsnDto.getDtBegun());
		cpsArCnclsnDetailForUpdate.setDtComplt(arInvCnclsnDto.getDtCompleted());
		cpsArCnclsnDetailForUpdate.setCdArOvrllDisptn(arInvCnclsnDto.getCdAROverallDisposition());
		cpsArCnclsnDetailForUpdate.setIndParentGivenGuide(arInvCnclsnDto.getIndParentGivenGuide());
		cpsArCnclsnDetailForUpdate.setIndParentLivingOutsideHome(arInvCnclsnDto.getIndParentsLivingOutside());
		cpsArCnclsnDetailForUpdate.setIndAbsParentGivenGuide(arInvCnclsnDto.getIndAbsentParentGuide());
		cpsArCnclsnDetailForUpdate.setIndMultPersFound(arInvCnclsnDto.getIndMultiplePersonsFound());
		cpsArCnclsnDetailForUpdate.setIndMultPersMerged(arInvCnclsnDto.getIndMultiplePersonsMerged());
		cpsArCnclsnDetailForUpdate.setIndMeth(arInvCnclsnDto.getIndMeth());
		cpsArCnclsnDetailForUpdate.setIndFtmOccurred(arInvCnclsnDto.getIndFTMOccured());
		cpsArCnclsnDetailForUpdate.setIndFtmOffered(arInvCnclsnDto.getIndFTMOffered());
		cpsArCnclsnDetailForUpdate.setCdFamIncm(arInvCnclsnDto.getCdFamilyIncome());
		cpsArCnclsnDetailForUpdate.setIndLegOrdReq(arInvCnclsnDto.getIndLegalOrdinanceRequired());
		cpsArCnclsnDetailForUpdate.setIndArEaEligible(arInvCnclsnDto.getIndArEaEligible());
		cpsArCnclsnDetailForUpdate.setDtCreated(arInvCnclsnDto.getDtCreated());
		cpsArCnclsnDetailForUpdate.setDtLastUpdate(new Date());
		cpsArCnclsnDetailForUpdate.setIdCreatedPerson(arInvCnclsnDto.getIdCreatedPerson());
		cpsArCnclsnDetailForUpdate.setIdLastUpdatePerson(arInvCnclsnDto.getIdLastUpdatePerson());
		sessionFactory.getCurrentSession().saveOrUpdate(cpsArCnclsnDetailForUpdate);

		// Insert or Update the AR EA Eligibility
		List<ArEaEligibilityDto> ArEaEligibilityDtoList = arInvCnclsnDto.getArEaEligibilityDtoList();

		if (!ObjectUtils.isEmpty(ArEaEligibilityDtoList)) {
			for (ArEaEligibilityDto arEaEligibilityDto : ArEaEligibilityDtoList) {

				if (arEaEligibilityDto.getIdEmergencyAssist().equals(ServiceConstants.ZERO)) {
					insertEaEligibility(arEaEligibilityDto);
				} else {
					updateEaEligibility(arEaEligibilityDto);
				}

			}
		}

	}

	/**
	 * Method Name: insertEaEligibility Method Description: insert the AR EA
	 * Eligibility Details
	 * 
	 * @param ArEaEligibilityDto
	 * @return
	 */
	private void insertEaEligibility(ArEaEligibilityDto arEaEligibilityDto) {
		EmergencyAssist emergencyAssist = new EmergencyAssist();
		emergencyAssist.setIdCase(arEaEligibilityDto.getIdCase());
		emergencyAssist.setIndEaResponse(arEaEligibilityDto.getIndEaResponse());
		emergencyAssist.setCdEaQuestion(arEaEligibilityDto.getCdEaQuestion());
		Event event = (Event) sessionFactory.getCurrentSession().get(Event.class, arEaEligibilityDto.getIdEaEvent());
		emergencyAssist.setEvent(event);
		emergencyAssist.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().save(emergencyAssist);

	}

	/**
	 * Method Name: updateEaEligibility Method Description: update the AR EA
	 * Eligibility Details
	 * 
	 * @param ArInvCnclsnDto
	 * @return
	 */
	private void updateEaEligibility(ArEaEligibilityDto arEaEligibilityDto) {
		EmergencyAssist emergencyAssist = (EmergencyAssist) sessionFactory.getCurrentSession()
				.get(EmergencyAssist.class, arEaEligibilityDto.getIdEmergencyAssist());
		emergencyAssist.setIndEaResponse(arEaEligibilityDto.getIndEaResponse());
		//artf158432 AR Report not displaying correct answers for the Eligibility Questions section
		emergencyAssist.setCdEaQuestion(arEaEligibilityDto.getCdEaQuestion());
		sessionFactory.getCurrentSession().saveOrUpdate(emergencyAssist);

	}

	/**
	 * Method Name: getArEaEligibilityDetails Method Description: retrieves the
	 * AR EA Eligibility Details
	 * 
	 * @param ArInvCnclsnDto
	 * @return
	 */

	@SuppressWarnings("unchecked")
	public List<ArEaEligibilityDto> getArEaEligibilityDetails(ArInvCnclsnDto arInvCnclsnDto) {

		List<ArEaEligibilityDto> ArEaEligibilityDtoList = new ArrayList<ArEaEligibilityDto>();
		Event event = new Event();
		event.setIdEvent(arInvCnclsnDto.getIdEvent());

		List<EmergencyAssist> emergencyAssistList = (List<EmergencyAssist>) sessionFactory.getCurrentSession()
				.createCriteria(EmergencyAssist.class).add(Restrictions.eq("event", event))
				.list();

		if (!ObjectUtils.isEmpty(emergencyAssistList)) {
			for (EmergencyAssist emergencyAssist : emergencyAssistList) {
				ArEaEligibilityDto arEaEligibilityDto = new ArEaEligibilityDto();
				arEaEligibilityDto.setCdEaQuestion(emergencyAssist.getCdEaQuestion());
				arEaEligibilityDto.setDtLastUpdate(emergencyAssist.getDtLastUpdate());
				arEaEligibilityDto.setIdCase(emergencyAssist.getIdCase());
				arEaEligibilityDto.setIdEaEvent(emergencyAssist.getEvent().getIdEvent());
				arEaEligibilityDto.setIdEmergencyAssist(emergencyAssist.getIdEmergencyAssist());
				arEaEligibilityDto.setIndEaResponse(emergencyAssist.getIndEaResponse());
				ArEaEligibilityDtoList.add(arEaEligibilityDto);

			}
		}

		return ArEaEligibilityDtoList;

	}

	/**
	 * Method Name: invalidateApprovalStatus Method Description: Retrieve the
	 * Approval Id's and Delete the To-Do/Approval Event
	 * 
	 * @param ArInvCnclsnDto
	 */

	@SuppressWarnings("unchecked")
	public void invalidateApprovalStatus(ArInvCnclsnDto arInvCnclsnDto) {
		List<ApprovalEventLink> approvalEventLinkList = (List<ApprovalEventLink>) sessionFactory.getCurrentSession()
				.createCriteria(ApprovalEventLink.class).add(Restrictions.eq("idEvent", arInvCnclsnDto.getIdEvent()))
				.list();

		if (!ObjectUtils.isEmpty(approvalEventLinkList)) {
			for (ApprovalEventLink approvalEventLink : approvalEventLinkList) {
				// Delete the To-Do
				todoDao.deleteTodosForAEvent(approvalEventLink.getIdApproval());

				// Delete the Approval Event
				eventDao.deleteEventById(approvalEventLink.getIdApproval());

			}
		}

	}

	/**
	 * Method Name: isFbssReferralApproved
	 * Method Desc: Checks with the FBSS Referral is approved in the case if idHouseHoldPerson not null then
	 * selected for house hold else for any house hold
	 *
	 * @param idCase
	 * @param taskCodes
	 * @return
	 */
	@Override
	public List<EventDto> isFbssReferralApproved(Long idCase, List<String> taskCodes) {
		// returns Event status if the fbss referral approval available
		List<EventDto> eventDtoList =   (List<EventDto>) ((SQLQuery)sessionFactory.getCurrentSession().createSQLQuery(isFbssReferralApproved)
				.setParameter("idcase", idCase).setParameterList("taskcodes",taskCodes))
				.addScalar("cdEventStatus", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idStage",StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(EventDto.class)).list();
		return eventDtoList;
	}
}
