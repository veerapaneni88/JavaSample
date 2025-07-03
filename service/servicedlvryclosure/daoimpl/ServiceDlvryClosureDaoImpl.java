package us.tx.state.dfps.service.servicedlvryclosure.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.service.admin.dto.EventPersonLinkMergeViewCountOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.response.CommonBooleanRes;
import us.tx.state.dfps.service.servicedlvryclosure.dao.ServiceDlvryClosureDao;
import us.tx.state.dfps.service.workload.dto.GuardianDetailsDto;
import us.tx.state.dfps.service.workload.dto.GuardianshipDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.service.servicedlvryclosure.dto.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Service
 * Dlvery Closure daoImpl May 18, 2018- 3:24:46 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class ServiceDlvryClosureDaoImpl implements ServiceDlvryClosureDao {

	@Autowired
	SessionFactory sessionFactory;

	@Value("${ServiceDlvryClosureDaoImpl.getPersonCount}")
	private String getPersonCountSql;

	@Value("${ServiceDlvryClosureDaoImpl.getInvOutcomeMatrix}")
	private String getInvOutcomeMatrixSql;

	@Value("${ServiceDlvryClosureDaoImpl.getSvcOutcomeMatrix}")
	private String getSvcOutcomeMatrixSql;

	@Value("${ServiceDlvryClosureDaoImpl.updateOutcomeMatrix}")
	private String updateOutcomeMatrixSql;

	@Value("${ServiceDlvryClosureDaoImpl.eventStatus}")
	private String eventStatusSql;

	@Value("${ServiceDlvryClosureDaoImpl.eventStatusAprv}")
	private String eventStatusAprvSql;

	@Value("${ServiceDlvryClosureDaoImpl.updateEvent}")
	private String updateEventSql;

	@Value("${ServiceDlvryClosureDaoImpl.getApproversList}")
	private String getApproversListSql;

	@Value("${ServiceDlvryClosureDaoImpl.getGuardianshipList}")
	private String getGuardianshipListSql;

	@Value("${ServiceDlvryClosureDaoImpl.getLegalActionCount}")
	private String getLegalActionCountSql;

	@Value("${ServiceDlvryClosureDaoImpl.getGuardianshipStatus}")
	private String getGuardianshipStatusSql;

	@Value("${ServiceDlvryClosureDaoImpl.retrieveCaseId}")
	private String retrieveCaseIdSql;

	@Value("${ServiceDlvryClosureDaoImpl.getPersonIdInFDTC}")
	private String getPersonIdInFDTCsql;

	@Value("${StageClosureDaoImpl.getMostRecentFDTCSubtype}")
	private String getMostRecentFDTCSubtypeSql;

	@Value("${StageClosureDaoImpl.getClosureNotificationLetters}")
	private String getClosureNotificationLettersSql;

	@Value("${StageClosureDaoImpl.getSafetyAssessmentStatus}")
	private String getSafetyAssessmentStatusSql;

	@Value("${TododaoImpl.getPrintTaskExists}")
	private String getPrintTaskExistsSql;
	
	@Value("${StageClosureDaoImpl.getPCHasOpenSUBStage}")
	private String getPCHasOpenSUBStageSql;

	@Value("${ServiceDlvryClosureDaoImpl.getGuardianshipIdListSql}")
	private String getGuardianshipIdListSql;

	private static final Logger log = Logger.getLogger(ServiceDlvryClosureDaoImpl.class);

	public static final String INV_VALIDATION = "I";
	public static final String SVC_VALIDATION = "S";
	public static final String FPR_PRINT_TASKCODE = "9989";

	/**
	 * Method Name: (CSVC20D) getPersonCount Method Description:This method
	 * counts the number of people on the Person table where the date of death
	 * is NULL based on the stage id
	 * 
	 * @param idStage
	 * @return count
	 */
	@Override
	public int getPersonCount(Long idStage) {
		int count = 0;
		SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPersonCountSql)
				.addScalar("sysNbrNumberOfRows", StandardBasicTypes.INTEGER)
				.setResultTransformer(Transformers.aliasToBean(EventPersonLinkMergeViewCountOutDto.class)));
		query.setParameter("idStage", idStage);
		query.setParameter("stagePersRole", "DV");

		EventPersonLinkMergeViewCountOutDto eventPersonLinkMergeViewCountOutDto = (EventPersonLinkMergeViewCountOutDto) query
				.uniqueResult();
		if (!ObjectUtils.isEmpty(eventPersonLinkMergeViewCountOutDto)) {
			count = eventPersonLinkMergeViewCountOutDto.getSysNbrNumberOfRows();
		}

		return count;
	}

	/**
	 * Method Name: (CINV96Ddam) getOutcomeMatrixDetails Method Description:
	 * This method retrieves all the Outcome Matrix Information based on EventId
	 * for INV and SVS
	 * 
	 * @param idEvent
	 * @return List
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<OutcomeMatrixDto> getOutcomeMatrixDetails(Long idEvent, String cdReqFunction) {
		log.debug("Entering method cinv96dQUERYdam(getOutcomeMatrixDetails) in DlvryClosureValidationDaoImpl");
		List<OutcomeMatrixDto> outcomeMatrixList = new ArrayList<>();
		if (INV_VALIDATION.equals(cdReqFunction)) {
			SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getInvOutcomeMatrixSql)
					.addScalar("idApsCltFactor").addScalar("idApsOutcomeMatrix").addScalar("idEvent")
					.addScalar("cdApsOutcomeAction").addScalar("cdApsOutcomeActnCateg").addScalar("cdApsOutcomeResult")
					.addScalar("dtApsOutcomeAction").addScalar("dtApsOutcomeRecord").addScalar("txtApsOutcomeResult")
					.addScalar("txtApsOutcomeAction").addScalar("lastUpdate").setParameter("idEvent", idEvent)
					.setResultTransformer(Transformers.aliasToBean(OutcomeMatrixDto.class)));
			outcomeMatrixList = (List<OutcomeMatrixDto>) sQLQuery1.list();
		} else if (SVC_VALIDATION.equals(cdReqFunction)) {
			SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getSvcOutcomeMatrixSql)
					.addScalar("idApsCltFactor").addScalar("idApsOutcomeMatrix").addScalar("idEvent")
					.addScalar("cdApsOutcomeAction").addScalar("cdApsOutcomeActnCateg").addScalar("cdApsOutcomeResult")
					.addScalar("dtApsOutcomeAction").addScalar("dtApsOutcomeRecord").addScalar("txtApsOutcomeResult")
					.addScalar("txtApsOutcomeAction").addScalar("lastUpdate").setParameter("idEvent", idEvent)
					.setResultTransformer(Transformers.aliasToBean(OutcomeMatrixDto.class)));
			outcomeMatrixList = (List<OutcomeMatrixDto>) sQLQuery1.list();
		}
		return outcomeMatrixList;
	}

	@Override
	public boolean updateOutcomeMatrix(DlvryClosureValidationDto dlvryClosureValidationDto, String reqFunction) {
		log.debug("Entering method csvc26dAUDdam(updateOutcomeMatrix) in DlvryClosureValidationDaoImpl");
		int rowCount = 0;
		if (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(reqFunction)) {
			SQLQuery sQLQuery1 = (SQLQuery) (sessionFactory.getCurrentSession().createSQLQuery(updateOutcomeMatrixSql)
					.setParameter("dtApsOutcomeRecord",
							dlvryClosureValidationDto.getOutcomeMatrixDto().getDtApsOutcomeRecord())
					.setParameter("idSituation", dlvryClosureValidationDto.getIdSituation())
					.setParameter("cdStageReasonClosed",
							dlvryClosureValidationDto.getOutcomeMatrixDto().getCdApsOutcomeResult()));
			rowCount = sQLQuery1.executeUpdate();
		}
		if (rowCount > 0) {
			return ServiceConstants.TRUE_VALUE;
		}
		return ServiceConstants.FALSEVAL;
	}

	/**
	 * Method Name: (CSVC19Ddam) getEventStatus Method Description: This DAM
	 * selects the event status from the Event table based on the contact type
	 * and event status passed
	 * 
	 * @param dlvryClosureValidationDto
	 * @return
	 */
	@Override
	public String getEventStatus(DlvryClosureValidationDto dlvryValidationDto, String contactType) {

		String reqFuncCdUpdate = dlvryValidationDto.getCdReqFunction();
		if ("BCLS".equals(contactType)) {
			reqFuncCdUpdate = ServiceConstants.REQ_FUNC_CD_UPDATE;
		}

		log.debug("Entering method csvc19dQUERYdam in DlvryClosureValidationDaoImpl");
		String eventStatus = null;
		SQLQuery sQLQuery1 = null;
		if (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(reqFuncCdUpdate)) {
			sQLQuery1 = (SQLQuery) (sessionFactory.getCurrentSession().createSQLQuery(eventStatusSql)
					.addScalar("cdEventStatus")
					.setParameter("idContactStage", dlvryValidationDto.getRgStageDto().getIdStage())
					.setParameter("cdContactType", contactType)
					.setResultTransformer(Transformers.aliasToBean(DlvryClosureValidationDto.class)));
		} else {
			sQLQuery1 = (SQLQuery) (sessionFactory.getCurrentSession().createSQLQuery(eventStatusAprvSql)
					.addScalar("cdEventStatus")
					.setParameter("idContactStage", dlvryValidationDto.getRgStageDto().getIdStage())
					.setParameter("cdContactType", contactType)
					.setParameter("cdEventStatus", dlvryValidationDto.getRgPostEventDto().getCdEventStatus())
					.setResultTransformer(Transformers.aliasToBean(DlvryClosureValidationDto.class)));
		}

		if (!ObjectUtils.isEmpty(sQLQuery1.list())) {
			DlvryClosureValidationDto dlvryClosureValidationDto = (DlvryClosureValidationDto) sQLQuery1.list().get(0);
			eventStatus = dlvryClosureValidationDto.getCdEventStatus();
		}
		return eventStatus;
	}

	/**
	 * Method Name: (CSVC27Ddam) updateEvent Method Description: This DAM
	 * updates the event status on the Event table based on the stage Id and
	 * event type
	 * 
	 * @param dlvryClosureValidationDto
	 */
	@Override
	public boolean updateEvent(DlvryClosureValidationDto dlvryValidationDto, String reqFunction) {
		log.debug("Entering method updateEvent in DlvryClosureValidationDaoImpl");
		int rowCount = 0;
		if (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(reqFunction)) {
			SQLQuery sQLQuery1 = (SQLQuery) (sessionFactory.getCurrentSession().createSQLQuery(updateEventSql)
					.setParameter("cdEventStatus", dlvryValidationDto.getRgPostEventDto().getCdEventStatus())
					.setParameter("idEventStage", dlvryValidationDto.getIdStage())
					.setParameter("cdEventType", dlvryValidationDto.getRgPostEventDto().getCdEventType()));
			rowCount = sQLQuery1.executeUpdate();
		}
		if (rowCount > 0) {
			return ServiceConstants.TRUE_VALUE;
		}
		return ServiceConstants.FALSEVAL;
	}

	/**
	 * Method Name: (CCMN56D) getApproversList Method Description: to get
	 * approvallist based on eventId
	 * 
	 * @param idApproval
	 * @return List
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<DlvryClosureApproversDto> getApproversList(long idApproval) {
		log.debug("Entering method getApproversList in DlvryClosureValidationDaoImpl");
		List<DlvryClosureApproversDto> approversList;
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getApproversListSql)
				.addScalar("dtApproversDetermination", StandardBasicTypes.DATE)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("approversStatus", StandardBasicTypes.STRING)
				.addScalar("approversComments", StandardBasicTypes.STRING)
				.addScalar("approvalLength", StandardBasicTypes.STRING)
				.addScalar("indWithinWorkerControl", StandardBasicTypes.LONG)
				.addScalar("idToDo", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idApprovers", StandardBasicTypes.LONG).addScalar("lastUpdate", StandardBasicTypes.DATE)
				.addScalar("ovrllDisptn", StandardBasicTypes.STRING)
				.addScalar("stageReasonClosed", StandardBasicTypes.STRING)
				.addScalar("dtDeterminationRecorded", StandardBasicTypes.DATE)
				.addScalar("jobClass", StandardBasicTypes.STRING)
				.addScalar("indEmpConfirmedHrmis", StandardBasicTypes.STRING).setParameter("idApproval", idApproval)
				.setResultTransformer(Transformers.aliasToBean(DlvryClosureApproversDto.class)));
		approversList = sQLQuery1.list();
		return approversList;
	}

	/**
	 * Method Name: (CLSCB6D) guardianshipList Method Description: DAM call to
	 * reterive the guradianship list
	 *
	 * @param cdEventType
	 * @param idEventStage
	 * @return List
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<GuardianshipDto> guardianshipList(String cdEventType, Long idEventStage) {
		log.debug("Entering method guardianshipList in DlvryClosureValidationDaoImpl");
		List<GuardianshipDto> guardianshipList;
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getGuardianshipListSql)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("dtLastUpdate").addScalar("idStage").addScalar("cdEventType")
				.addScalar("idEventPerson").addScalar("cdTask").addScalar("txtEventDescr").addScalar("dtEventOccurred")
				.addScalar("cdEventStatus").addScalar("idGuardEvent").addScalar("idGuardPers").addScalar("idGuardRsrc")
				.addScalar("addrGuardCity").addScalar("addrGuardCnty").addScalar("addrGuardLn1")
				.addScalar("addrGuardLn2").addScalar("addrGuardSt").addScalar("addrGuardZip")
				.addScalar("cdGuardCloseReason").addScalar("cdGuardGuardianType").addScalar("cdGuardType")
				.addScalar("dtGuardCloseDate").addScalar("dtGuardLetterIssued").addScalar("dtGuardOrdered")
				.addScalar("indGuardAgedOut").addScalar("nbrGuardPhone").addScalar("nbrGuardPhoneExt")
				.addScalar("sdsGuardName").addScalar("txtGuardAddrComment").addScalar("txtGuardComments")
				.setParameter("cdEventType", cdEventType).setParameter("idEventStage", idEventStage)
				.setResultTransformer(Transformers.aliasToBean(GuardianshipDto.class)));
		guardianshipList = sQLQuery1.list();
		return guardianshipList;
	}

	/**
	 * Method Name: (CSVC48D) getLegalActionCount Method Description: fetch
	 * legal action count based on idperson and idcase
	 * 
	 * @param idPerson
	 * @param idCase
	 * @return int
	 */
	@Override
	public int getLegalActionCount(Long idPerson, Long idCase) {
		log.debug("Entering method getLegalActionCount in DlvryClosureValidationDaoImpl");
		int rowCount = 0;
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getLegalActionCountSql)
				.addScalar("rowCount").setParameter("idPerson", idPerson).setParameter("idCase", idCase));
		rowCount = (int) sQLQuery1.uniqueResult();
		return rowCount;
	}

	/**
	 * Method Name: (CSVC49D) getGuardianshipStatus Method Description: to get
	 * guardianship status based on idcase
	 * 
	 * @param idCase
	 * @return List
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<GuardianshipDto> getGuardianshipStatus(Long idCase) {
		log.debug("Entering method getGuardianshipStatus in DlvryClosureValidationDaoImpl");
		List<GuardianshipDto> guardianshipList;
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getGuardianshipStatusSql)
				.addScalar("idGuardEvent").addScalar("cdGuardCloseReason").addScalar("dtGuardCloseDate")
				.addScalar("dtGuardLetterIssued").addScalar("cdGuardGuardianType").setParameter("idCase", idCase)
				.setResultTransformer(Transformers.aliasToBean(GuardianshipDto.class)));
		guardianshipList = sQLQuery1.list();
		return guardianshipList;
	}

	/**
	 * Method Name: (CCMNB6D) retrieveIdCase Method Description:retrieve
	 * DlvryClosureValidationDto based on id stage
	 * 
	 * @param idStage
	 * @returnDlvryClosureValidationDto
	 */
	@Override
	public DlvryClosureValidationDto retrieveIdCase(Long idStage) {
		DlvryClosureValidationDto response = new DlvryClosureValidationDto();
		Stage stage = (Stage) sessionFactory.getCurrentSession().load(Stage.class, idStage);
		response.getRgStageDto().setIdCase(stage.getCapsCase().getIdCase());
		response.setIndScreened(stage.getIndScreened());
		return response;
	}

	@Override
	public CommonBooleanRes getPrintTaskExistsFPR(Long idCase, Long idStage, Long idEvent) {
		CommonBooleanRes resp = new CommonBooleanRes();
		Long count = (Long) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPrintTaskExistsSql)
				.setParameter("idCase", idCase).setParameter("idStage", idStage).setParameter("idEvent", idEvent)
				.setParameter("cdTask", FPR_PRINT_TASKCODE).setParameter("cdTodoType", ServiceConstants.TASK_TODO))
						.addScalar("count", StandardBasicTypes.LONG).uniqueResult();
		if (count > 0) {
			resp.setExists(Boolean.TRUE);
		} else {
			resp.setExists(Boolean.FALSE);
		}
		return resp;
	}

	/**
	 * Method Name: getPersonIdInFDTC Method Description: retrieve persons in
	 * fdtc based on id case
	 * 
	 * @param caseId
	 * @return getPersonIdInFDTC
	 */
	@SuppressWarnings({ "unchecked" })
	@Override
	public HashMap<Integer, String> getPersonIdInFDTC(Long caseId) {

		HashMap<Integer, String> hashMap = new HashMap<>();
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPersonIdInFDTCsql)
				.setResultTransformer(Transformers.aliasToBean(PersonDto.class)));
		sQLQuery1.addScalar("idPerson", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("nmPersonFull", StandardBasicTypes.STRING);
		sQLQuery1.setParameter("idCase", caseId);
		sQLQuery1.setParameter("cdLegalActAction", ServiceConstants.CLEGCPS_CFDT);
		List<PersonDto> personList = sQLQuery1.list();
		if (!CollectionUtils.isEmpty(personList) && !personList.isEmpty()) {
			for (PersonDto personDto : personList) {
				hashMap.put(personDto.getIdPerson().intValue(), personDto.getNmPersonFull());
			}
		}
		return hashMap;
	}

	/**
	 * Method Name: getMostRecentFDTCSubtype Method Description: retrieve recent
	 * FDTC based on id person
	 * 
	 * @param personId
	 * @return getMostRecentFDTCSubtype
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public HashMap<String, String> getMostRecentFDTCSubtype(Long personId) {
		HashMap<String, String> hashMap = new HashMap();
		List<String> legalActionSubtype = new ArrayList<String>();
		legalActionSubtype.add(ServiceConstants.CFDT_010);
		legalActionSubtype.add(ServiceConstants.CFDT_020);
		legalActionSubtype.add(ServiceConstants.CFDT_030);
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getMostRecentFDTCSubtypeSql)
				.addScalar("legalActActnSubtype", StandardBasicTypes.STRING)
				.addScalar("idEventStage", StandardBasicTypes.STRING);
		query.setParameter("idPerson", personId);
		query.setParameterList("legalActionSubtype", legalActionSubtype);
		query.setParameter("legalAct", ServiceConstants.CLEGCPS_CFDT)
				.setResultTransformer(Transformers.aliasToBean(ServiceDlvryClosureDto.class));
		List<ServiceDlvryClosureDto> serviceDlvryClosure = query.list();
		if (serviceDlvryClosure.size() > ServiceConstants.Zero)
			// Modified the code to set the most recent FDTC Subtype and Outcome
			// date for a Person in a given case id - Warranty defect 11594
			for (ServiceDlvryClosureDto dlvryClosureDto : serviceDlvryClosure) {
			hashMap.put(ServiceConstants.CD_LEGAL_ACT_ACTN_SUBTYPE, dlvryClosureDto.getLegalActActnSubtype());
			hashMap.put(ServiceConstants.ID_EVENT_STAGE, dlvryClosureDto.getIdEventStage());
			break;
			}
		return hashMap;
	}

	/**
	 * Method Name: getClosureNotificationletters Method Description: This
	 * method is to retrieve all records from Database ADS Changes
	 * 
	 * @param idStage
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ClosureNotificationLettersDto> getClosureNotificationletters(Long idStage) {
		List<ClosureNotificationLettersDto> finalList;
		SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getClosureNotificationLettersSql).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("cdPersonLanguage", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRole", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdStagePersType", StandardBasicTypes.STRING)
				.addScalar("cdStageReasonClosed", StandardBasicTypes.STRING)
				.addScalar("cdNoticeSelected",StandardBasicTypes.STRING)
				.addScalar("idConclusionNotifInfo",StandardBasicTypes.LONG)
				.setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(ClosureNotificationLettersDto.class)));
		finalList = sQLQuery.list();
		return finalList;
	}

	/**
	 * Method Name: getSDMAssessmentCount Method Description: get Safety
	 * Assessment Count from Database ADS Changes
	 * 
	 * @param idStage
	 * @return
	 */
	@Override
	public boolean getSDMAssessmentCount(Long idStage) {
		log.debug("Entering method getSDMAssessmentCount in DlvryClosureValidationDaoImpl");
		SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getSafetyAssessmentStatusSql)
				.setParameter("idStage", idStage));
		if (null != query.uniqueResult() && ((BigDecimal) query.uniqueResult()).longValue() > 0) {
			return ServiceConstants.TRUE_VALUE;
		}
		return ServiceConstants.FALSEVAL;
	}
	
	/**
	 * Method Name: getPCHasOpenSUBStage Method Description: This method is to check
	 * whether the Principal child has an open SUB stage on the case
	 * 
	 * @param idCase
	 *            return: boolean
	 */
	@Override
	public boolean getPCHasOpenSUBStage(Long idCase) {
		boolean isPCHasOpenSUBStage = ServiceConstants.FALSEVAL;
		SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPCHasOpenSUBStageSql)
				.setParameter("idCase", idCase));
		if (null != query.uniqueResult() && ((BigDecimal) query.uniqueResult()).longValue() > 0) {
			isPCHasOpenSUBStage = ServiceConstants.TRUE_VALUE;
		}
		return isPCHasOpenSUBStage;
	}


	public List<GuardianDetailsDto> getGuardianDetailsbyEventStage(String cdEventType, Long idEventStage) {
		log.debug("Entering method guardianshipIdList in DlvryClosureValidationDaoImpl");
		List<GuardianDetailsDto> guardianshipIdList = null;
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getGuardianshipIdListSql)
				.addScalar("guardFullName", StandardBasicTypes.STRING)
				.addScalar("addrPersAddrStLn1", StandardBasicTypes.STRING)
				.addScalar("addrPersAddrStLn2", StandardBasicTypes.STRING)
				.addScalar("addrPersonAddrCity", StandardBasicTypes.STRING)
				.addScalar("cdPersonAddrState", StandardBasicTypes.STRING)
				.addScalar("cdPersonAddrCounty", StandardBasicTypes.STRING)
				.addScalar("addrPersonAddrZip", StandardBasicTypes.STRING)
				.addScalar("nbrPersonPhone", StandardBasicTypes.STRING)
				.addScalar("nbrPersonPhoneExtension", StandardBasicTypes.STRING)
				.setParameter("cdEventType", cdEventType)
				.setParameter("idEventStage", idEventStage)
				.setResultTransformer(Transformers.aliasToBean(GuardianDetailsDto.class)));
		guardianshipIdList =sQLQuery1.list();

		return guardianshipIdList;
	}

}
