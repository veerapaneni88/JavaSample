package us.tx.state.dfps.service.familyplan.daoimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.ApprovalEventLink;
import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.ChildPlan;
import us.tx.state.dfps.common.domain.CpsFsna;
import us.tx.state.dfps.common.domain.CpsFsnaDomainLookup;
import us.tx.state.dfps.common.domain.Employee;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.EventPersonLink;
import us.tx.state.dfps.common.domain.EventPlanLink;
import us.tx.state.dfps.common.domain.FamPlnAocGoalLink;
import us.tx.state.dfps.common.domain.FamPlnTaskGoalLink;
import us.tx.state.dfps.common.domain.FamilyAssmt;
import us.tx.state.dfps.common.domain.FamilyChildCandidacy;
import us.tx.state.dfps.common.domain.FamilyPlan;
import us.tx.state.dfps.common.domain.FamilyPlanActnRsrc;
import us.tx.state.dfps.common.domain.FamilyPlanCost;
import us.tx.state.dfps.common.domain.FamilyPlanEval;
import us.tx.state.dfps.common.domain.FamilyPlanEvalItem;
import us.tx.state.dfps.common.domain.FamilyPlanGoal;
import us.tx.state.dfps.common.domain.FamilyPlanItem;
import us.tx.state.dfps.common.domain.FamilyPlanNeed;
import us.tx.state.dfps.common.domain.FamilyPlanPartcpnt;
import us.tx.state.dfps.common.domain.FamilyPlanReqrdActn;
import us.tx.state.dfps.common.domain.FamilyPlanRole;
import us.tx.state.dfps.common.domain.FamilyPlanTask;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.PersonEligibility;
import us.tx.state.dfps.common.domain.ServicePlan;
import us.tx.state.dfps.common.domain.ServicePlanEvalDtl;
import us.tx.state.dfps.common.domain.SsccChildPlan;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.domain.StagePersonLink;
import us.tx.state.dfps.common.domain.Todo;
import us.tx.state.dfps.common.dto.EmailDetailsDto;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.dto.FamAssmtDto;
import us.tx.state.dfps.common.dto.FamAssmtFactorDto;
import us.tx.state.dfps.common.dto.ServPlanEvalRecDto;
import us.tx.state.dfps.common.dto.ServicePlanDto;
import us.tx.state.dfps.common.dto.ServicePlanEvalDto;
import us.tx.state.dfps.familyplan.dto.FamilyPlanActnRsrcDto;
import us.tx.state.dfps.familyplan.dto.FamilyPlanNarrDto;
import us.tx.state.dfps.familyplan.dto.FamilyPlanNeedsDto;
import us.tx.state.dfps.familyplan.dto.FamilyPlanPartcpntDto;
import us.tx.state.dfps.familyplan.dto.FamilyPlanReqrdActnsDto;
import us.tx.state.dfps.familyplan.request.FamilyPlanDtlEvalReq;
import us.tx.state.dfps.fsna.dto.CpsFsnaDto;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.casepackage.dto.CapsEmailDto;
import us.tx.state.dfps.service.childplan.dao.ChildPlanBeanDao;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.request.FamilyPlanReq;
import us.tx.state.dfps.service.common.response.CommonIdRes;
import us.tx.state.dfps.service.common.response.FamilyPlanRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.utils.EventUtil;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.familyTree.bean.FamilyPlanGoalValueDto;
import us.tx.state.dfps.service.familyTree.bean.TaskGoalValueDto;
import us.tx.state.dfps.service.familyplan.dao.FamilyPlanDao;
import us.tx.state.dfps.service.familyplan.dto.FamilyPlanEvalItemDto;
import us.tx.state.dfps.service.familyplan.dto.PrincipalParticipantDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanEvalDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanGoalDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanItemDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanTaskDto;
import us.tx.state.dfps.service.forms.dto.RiskAreaLookUpDto;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.PersonValueDto;
import us.tx.state.dfps.service.workload.dao.WorkLoadDao;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.EventPlanLinkDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:FamilyPlanDaoImpl will implemented all operation defined in
 * FamilyPlanDao Interface related FamilyPlan module. Mar 8, 2018- 2:02:21 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Repository
public class FamilyPlanDaoImpl implements FamilyPlanDao {

	private static final String ASSIGN = "assign";
	private static final String APPROVAL = "approval";
	private static final Logger log = Logger.getLogger(FamilyPlanDaoImpl.class);

	/** The message source. */
	@Autowired
	MessageSource messageSource;

	/** The work load dao. */
	@Autowired
	WorkLoadDao workLoadDao;

	@Autowired
	PersonDao personDao;
	
	/** The event util */
	@Autowired
	EventUtil eventUtil;

	/** The session factory. */
	@Autowired
	private SessionFactory sessionFactory;

	/** The get serv plan eval rec sql. */
	@Value("${FamilyPlanDaoImpl.getServPlanEvalRec}")
	private String getServPlanEvalRecSql;

	/** The get fam assmt factors sql. */
	@Value("${FamilyPlanDaoImpl.getFamAssmtFactors}")
	private String getFamAssmtFactorsSql;

	/** The get risk area look up sql. */
	@Value("${FamilyPlanPlanDao.getRiskAreaLookUp}")
	private String getRiskAreaLookUpSql;

	/** The get family plan item goal sql. */
	@Value("${FamilyPlanPlanDao.getFamilyPlanItemGoal}")
	private String getFamilyPlanItemGoalSql;

	/** The get family plan items sql. */
	@Value("${FamilyPlanPlanDao.getFamilyPlanItems}")
	private String getFamilyPlanItemsSql;

	/** The get task for spec risk area sql. */
	@Value("${FamilyPlanPlanDao.getTaskForSpecRiskArea}")
	private String getTaskForSpecRiskAreaSql;

	/** The query approved goals. */
	@Value("${FamilyPlanPlanDao.queryApprovedGoals}")
	private String queryApprovedGoals;

	/** The query approved tasks. */
	@Value("${FamilyPlanPlanDao.queryApprovedTasks}")
	private String queryApprovedTasks;

	/** The get event info. */
	@Value("${FamilyPlanDao.getEventInfo}")
	private String getEventInfo;

	/** The get event id from app event link. */
	@Value("${FamilyPlanDao.getEventIdFromAppEventLink}")
	private String getEventIdFromAppEventLink;

	/** The is event id family plan. */
	@Value("${FamilyPlanDao.isEventIdFamilyPlan}")
	private String isEventIdFamilyPlan;

	/** The get family plan event id. */
	@Value("${FamilyPlanDao.getFamilyPlanEventId}")
	private String getFamilyPlanEventId;

	/** The query family plan items. */
	@Value("${FamilyPlanDao.queryFamilyPlanItems}")
	private String queryFamilyPlanItems;

	/** The update family plan items. */
	@Value("${FamilyPlanDao.updateFamilyPlanItems}")
	private String updateFamilyPlanItems;

	/** The query all goals. */
	@Value("${FamilyPlanDao.queryAllGoals}")
	private String queryAllGoals;

	/** The query tasks not approved. */
	@Value("${FamilyPlanDao.queryTasksNotApproved}")
	private String queryTasksNotApproved;

	/** The query workload for secondary worker. */
	@Value("${FamilyPlanDao.queryWorkloadForSecondaryWorker}")
	private String queryWorkloadForSecondaryWorker;

	/** The fetch child not in subcare sql. */
	@Value("${FamilyPlanDaoImpl.fetchChildNotInSubcare}")
	private String fetchChildNotInSubcareSql;

	/** The fetch all child candidacy sql. */
	@Value("${FamilyPlanDaoImpl.fetchAllChildCandidacy}")
	private String fetchAllChildCandidacySql;

	/** The query legacy events. */
	@Value("${FamilyPlanDaoImpl.queryLegacyEvents}")
	private String queryLegacyEvents;

	/** The query event. */
	@Value("${FamilyPlanDaoImpl.queryEvent}")
	private String queryEvent;

	/** The query event approval dtl. */
	@Value("${FamilyPlanDaoImpl.queryEventApprovalDtl}")
	private String queryEventApprovalDtl;

	/** The query event nm dtl. */
	@Value("${FamilyPlanDaoImpl.queryEventNmDtl}")
	private String queryEventNmDtl;

	/** The check if event is legacy. */
	@Value("${FamilyPlanDaoImpl.checkIfEventIsLegacy}")
	private String checkIfEventIsLegacy;

	/** The query family plan. */
	@Value("${FamilyPlanDaoImpl.queryFamilyPlan}")
	private String queryFamilyPlan;

	/** The query family plan item. */
	@Value("${FamilyPlanDaoImpl.queryFamilyPlanItem}")
	private String queryFamilyPlanItem;

	/** The query family plan task. */
	@Value("${FamilyPlanTaskDaoImpl.queryFamilyPlanTask}")
	private String queryFamilyPlanTask;

	/** The query family plan eval items. */
	@Value("${FamilyPlanDaoImpl.queryFamilyPlanEvalItems}")
	private String queryFamilyPlanEvalItems;

	/** The query open family plan task. */
	@Value("${FamilyPlanDaoImpl.queryOpenFamilyPlanTask}")
	private String queryOpenFamilyPlanTask;

	/** The query close family plan task. */
	@Value("${FamilyPlanDaoImpl.queryCloseFamilyPlanTask}")
	private String queryCloseFamilyPlanTask;

	/** The query primary worker for stage. */
	@Value("${FamilyPlanDaoImpl.queryPrimaryWorkerForStage}")
	private String queryPrimaryWorkerForStage;

	/** The query family plan eval. */
	@Value("${FamilyPlanDaoImpl.queryFamilyPlanEval}")
	private String queryFamilyPlanEval;

	/** The query principals for stage. */
	@Value("${FamilyPlanDaoImpl.queryPrincipalsForStage}")
	private String queryPrincipalsForStage;

	/** The query family plan event id. */
	@Value("${FamilyPlanDaoimpl.queryFamilyPlanEventId}")
	private String queryFamilyPlanEventId;

	/** The query investigation stage id. */
	@Value("${FamilyPlanDaoimpl.queryInvestigationStageId}")
	private String queryInvestigationStageId;

	/** The query family plan role. */
	@Value("${FamilyPlanDaoimpl.queryFamilyPlanRole}")
	private String queryFamilyPlanRole;

	/** The query selected participants. */
	@Value("${FamilyPlanDaoimpl.querySelectedParticipants}")
	private String querySelectedParticipants;

	/** The query selected principals. */
	@Value("${FamilyPlanDaoimpl.querySelectedPrincipals}")
	private String querySelectedPrincipals;

	/** The query event assessment. */
	@Value("${FamilyPlanDaoImpl.queryeventAssessment}")
	private String queryEventAssessment;

	/** The query family plan goal sql. */
	@Value("${FamilyPlanTaskDaoImpl.queryFamilyPlanGoal}")
	private String queryFamilyPlanGoalSql;

	/** The query approved active tasks sql. */
	@Value("${FamilyPlanTaskDaoImpl.queryApprovedActiveTasks}")
	private String queryApprovedActiveTasksSql;

	/** The query principals for event. */
	@Value("${FamilyPlanDaoImpl.queryPrincipalsForEvent}")
	private String queryPrincipalsForEvent;

	/** The query principals selected for event. */
	@Value("${FgdmDaoImpl.queryPrincipalsSelectedForEvent}")
	private String queryPrincipalsSelectedForEvent;

	/** The query approved in active tasks sql. */
	@Value("${FamilyPlanTaskDaoImpl.queryApprovedInActiveTasks}")
	private String queryApprovedInActiveTasksSql;

	/** The query children in case in subcare. */
	@Value("${FamilyPlanDaoImpl.queryChildrenInCaseInSubcare}")
	private String queryChildrenInCaseInSubcare;

	/** The get family plan narr list. */
	@Value("${FamilyPlanDaoImpl.getFamilyPlanNarrList}")
	private String getFamilyPlanNarrList;

	/** The fetch principals risk of removal. */
	@Value("${FamilyPlanDaoImpl.fetchPrincipalsRiskOfRemoval}")
	private String fetchPrincipalsRiskOfRemoval;

	/** The fcc person in complete count. */
	@Value("${FamilyPlanDaoImpl.fccPersonInCompleteCount}")
	private String fccPersonInCompleteCount;

	/** The get active task goals. */
	@Value("${FamilyPlanDaoImpl.getActiveTaskGoals}")
	private String getActiveTaskGoals;

	/** The get family plan request. */
	@Value("${FamilyPlanDaoImpl.getFormFamilyPlanDetails}")
	private String getFamilyPlanRequest;

	/** The get family plan eval request. */
	@Value("${FamilyPlanDaoImpl.getFormFamilyPlanEvalDetails}")
	private String getFamilyPlanEvalRequest;

	/** The check if intepreter translator is needed. */
	@Value("${FamilyPlanDaoImpl.checkIfIntepreterTranslatorIsNeeded}")
	private String checkIfIntepreterTranslatorIsNeeded;

	/** The get all tasks not linked to goals. */
	@Value("${FamilyPlanDaoImpl.getAllTasksNotLinkedToGoals}")
	private String getAllTasksNotLinkedToGoals;

	@Value("${FamilyPlanDaoImpl.getEmailAddress}")
	private String getEmailAddressSql;

	@Value("${FamilyPlanDaoImpl.getEmailAddressForFSUFRE}")
	private String getEmailAddressForFSUFRESql;
	
	/** delete CP narratives when event is deleted. */
	@Value("${FamilyPlanDaoImpl.deleteCpNarr}")
	private String deleteCpNarr;

	@Value("${FamilyPlanDaoImpl.getFamilyPlanVersions}")
	private String getFamilyPlanVersions;
	
	/** The event dao. */
	@Autowired
	EventDao eventDao;

	@Autowired ChildPlanBeanDao childPlanDao;
	
	/** The event id. */
	private static String EVENT_ID = "event.idEvent";

	/** The Constant FAMILY_PLAN_REVIEW_FOR. */
	private static final String FAMILY_PLAN_REVIEW_FOR = " Family Plan Review for ";

	/** The Constant DUE_ON. */
	private static final String DUE_ON = " due on ";

	/** The Constant FAMILY_PLAN_VERSION. */
	private static final String FAMILY_PLAN_VERSION = "3";
	
	public static final List<String> CP_NARR_TABLES = Collections.unmodifiableList(Arrays.asList("CP_ISH_NARR",
			"CP_SSC_NARR", "CP_SEN_NARR", "CP_TRM_NARR", "CP_DVL_NARR", "CP_PLS_NARR", "CP_SAE_NARR", "CP_DVP_NARR",
			"CP_PHP_NARR", "CP_TPL_NARR", "CP_AOP_NARR", "CP_PCH_NARR", "CP_CHG_NARR", "CP_CONCURRENT_GOAL",
			"CP_ASF_NARR", "CP_PVP_NARR", "CP_ICH_NARR", "CP_EDP_NARR", "CP_MDP_NARR", "CP_WOR_NARR", "CP_DVN_NARR",
			"CP_PSP_NARR", "CP_PSY_NARR", "CP_CPL_NARR", "CP_PRA_NARR", "CP_EDN_NARR", "CP_REP_NARR", "CP_EOC_NARR",
			"CP_APP_NARR", "CP_PLP_NARR", "CP_IBP_NARR", "CP_PAL_NARR", "CP_SSF_NARR", "CP_TRV_NARR", "CP_DSC_NARR",
			"CP_REC_NARR", "CP_FMP_NARR", "CP_PER_NARR", "CP_CNP_NARR", "CP_OOP_NARR", "CP_VIS_NARR", "CP_PFC_NARR",
			"CP_APA_NARR", "CP_PDO_NARR", "CP_SUP_NARR", "CP_FAN_NARR", "CP_APR_NARR", "CP_CTP_NARR", "CP_IGH_NARR",
			"CP_SEP_NARR", "CP_PHY_NARR", "CP_MDN_NARR", "CP_FAM_NARR"));

	/**
	 * Method Name: getFamilyAssmt Method Description:This method retrieves a
	 * full row from the Family_Assmt table DAM Name : CSES05D.
	 *
	 * @param idEvent
	 *            the id event
	 * @return @
	 */
	@Override
	public FamAssmtDto getFamilyAssmt(Long idEvent) {

		Criteria cr = sessionFactory.getCurrentSession().createCriteria(FamilyAssmt.class).createAlias("stage", "stage")
				.add(Restrictions.eq("idEvent", idEvent))
				.setProjection(Projections.projectionList().add(Projections.property("idEvent"), "idEvent")
						.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
						.add(Projections.property("stage.idStage"), "idFamAssmtStage")
						.add(Projections.property("dtFamAssmtComplt"), "dtFamAssmtComplt"))
				.setResultTransformer(Transformers.aliasToBean(FamAssmtDto.class));

		return (FamAssmtDto) cr.uniqueResult();
	}

	/**
	 * Method Name: getFamAssmtFactors Method Description: This method selects n
	 * full rows from the Fam_Assmt_Fctrs table using Id_Fam_Assmt_Event and
	 * Cd_Fam_Assmt_Subject DAM Name: CLSS17D.
	 *
	 * @param idFamAssmtEvent
	 *            the id fam assmt event
	 * @param cdFamAssmtSubject
	 *            the cd fam assmt subject
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FamAssmtFactorDto> getFamAssmtFactors(Long idFamAssmtEvent, String cdFamAssmtSubject) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getFamAssmtFactorsSql)
				.addScalar("idFamAssmtFactr", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idFamAssmtEvent", StandardBasicTypes.LONG)
				.addScalar("idFamAssmtPrincipal", StandardBasicTypes.LONG)
				.addScalar("indFamAssmtResponse", StandardBasicTypes.STRING)
				.addScalar("cdFamAssmtFactr", StandardBasicTypes.STRING)
				.addScalar("cdFamAssmtSubject", StandardBasicTypes.STRING)
				.addScalar("cdFamAssmtCategory", StandardBasicTypes.STRING)
				.addScalar("cdFamAssmtFactrType", StandardBasicTypes.STRING).setParameter("idEvent", idFamAssmtEvent)
				.setParameter("cdFamAssmtSubject", cdFamAssmtSubject)
				.setResultTransformer(Transformers.aliasToBean(FamAssmtFactorDto.class));

		return query.list();
	}

	/**
	 * Method Name: getServicePlanEvalDtl Method Description:This method does a
	 * retrieval of the SERVICE_PLAN_EVAL_DTL table given an event id for the
	 * Family Plan Eval Event DAM Name : CSVC23D.
	 *
	 * @param idEvent
	 *            the id event
	 * @return @
	 */
	@Override
	public ServicePlanEvalDto getServicePlanEvalDtl(Long idEvent) {

		Criteria cr = sessionFactory.getCurrentSession().createCriteria(ServicePlanEvalDtl.class)
				.createAlias("servicePlan", "servicePlan").add(Restrictions.eq("idSvcPlanEvalEvent", idEvent))
				.setProjection(Projections.projectionList()
						.add(Projections.property("servicePlan.idSvcPlanEvent"), "idSvcPlanEvent")
						.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
						.add(Projections.property("cdSvcPlanEvalDtlType"), "cdSvcPlanEvalDtlType")
						.add(Projections.property("dtSvcPlanEvalDtlCmpl"), "dtSvcPlanEvalDtlCmpl"))
				.setResultTransformer(Transformers.aliasToBean(ServicePlanEvalDto.class));

		return (ServicePlanEvalDto) cr.uniqueResult();
	}

	/**
	 * Method Name: getServicePlanByIdEvent Method Description:This method does
	 * a full row retrieval of SERVICE_PLAN table based on ID_SVC_PLAN_EVENT DAM
	 * Name : CSVC04D.
	 *
	 * @param idEvent
	 *            the id event
	 * @return @
	 */
	@Override
	public ServicePlanDto getServicePlanByIdEvent(Long idEvent) {
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(ServicePlan.class)
				.createAlias("familyAssmt", "familyAssmt").add(Restrictions.eq("idSvcPlanEvent", idEvent))
				.setProjection(Projections.projectionList().add(Projections.property("cdSvcPlanType"), "cdSvcPlanType")
						.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
						.add(Projections.property("dtSvcPlanComplt"), "dtSvcPlanComplt")
						.add(Projections.property("dtSvcPlanPartcp"), "dtSvcPlanPartcp")
						.add(Projections.property("dtSvcPlanNextRevw"), "dtSvcPlanNextRevw")
						.add(Projections.property("dtSvcPlanSgndParent1"), "dtSvcPlanSgndParent1")
						.add(Projections.property("dtSvcPlanSgndParent2"), "dtSvcPlanSgndParent2")
						.add(Projections.property("dtSvcPlanSgndSupv"), "dtSvcPlanSgndSupv")
						.add(Projections.property("dtSvcPlanSgndWorker"), "dtSvcPlanSgndWorker")
						.add(Projections.property("dtSvcPlanGivenClients"), "dtSvcPlanGivenClients")
						.add(Projections.property("indSvcPlanClientCmnt"), "indSvcPlanClientCmnt")
						.add(Projections.property("txtSvcPlanPartcp"), "txtSvcPlanPartcp")
						.add(Projections.property("txtSvcPlanRsnInvlvmnt"), "txtSvcPlanRsnInvlvmnt")
						.add(Projections.property("txtSvcPlnStrnthsRsrcs"), "txtSvcPlnStrnthsRsrcs")
						.add(Projections.property("familyAssmt.idEvent"), "idFamAssmtEvent"))
				.setResultTransformer(Transformers.aliasToBean(ServicePlanDto.class));

		return (ServicePlanDto) cr.uniqueResult();
	}

	/**
	 * Method Name: getServPlanEvalRec Method Description: Retrieve Service Plan
	 * Eval Item records for a given PLAN EVENT DAM name : CLSC07D.
	 *
	 * @param idEvent
	 *            the id event
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ServPlanEvalRecDto> getServPlanEvalRec(Long idEvent) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getServPlanEvalRecSql)
				.addScalar("idSvcPlanEvent", StandardBasicTypes.LONG)
				.addScalar("cdSvcPlanTask", StandardBasicTypes.STRING)
				.addScalar("cdSvcPlanProblem", StandardBasicTypes.STRING)
				.addScalar("cdSvcPlanSvc", StandardBasicTypes.STRING)
				.addScalar("indSvcPlnSvcCrtOrdr", StandardBasicTypes.STRING)
				.addScalar("indSvcPlnTaskCrtOrdr", StandardBasicTypes.STRING)
				.addScalar("txtSvcPlanMethodEval", StandardBasicTypes.STRING)
				.addScalar("cdSvcPlanGoal", StandardBasicTypes.STRING)
				.addScalar("txtSvcPlanTaskFreq", StandardBasicTypes.STRING)
				.addScalar("txtSvcPlanProblem", StandardBasicTypes.STRING)
				.addScalar("txtSvcPlanTask", StandardBasicTypes.STRING)
				.addScalar("txtSvcPlanGoal", StandardBasicTypes.STRING)
				.addScalar("txtSvcPlanSvc", StandardBasicTypes.STRING)
				.addScalar("txtSvcPlanSvcFreq", StandardBasicTypes.STRING)
				.addScalar("idSvcPlanEvalItem", StandardBasicTypes.LONG)
				.addScalar("idSvcPlanEvalEvent", StandardBasicTypes.LONG)
				.addScalar("idSvcPlnItem", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("txtSvcPlanEvalTask", StandardBasicTypes.STRING)
				.addScalar("txtSvcPlanEvalGoal", StandardBasicTypes.STRING).setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(ServPlanEvalRecDto.class));
		return query.list();
	}

	/**
	 * Method Name: getRiskAreaLookUp DAM Name = CSVC42D Method Description:Full
	 * Row retrieval from RISK_AREA_LOOKUP RAL and FAMILY_PLAN_ITEM FPI based on
	 * ID_EVENT.
	 *
	 * @param idEvent
	 *            the id event
	 * @return @
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<RiskAreaLookUpDto> getRiskAreaLookUp(Long idEvent) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getRiskAreaLookUpSql)
				.addScalar("cdArea", StandardBasicTypes.STRING).addScalar("txtArea", StandardBasicTypes.STRING)
				.addScalar("nbrAreaOrder", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(RiskAreaLookUpDto.class));

		return query.list();
	}

	/**
	 * Method Name: getFamilyPlanItemGoal DAM Name : CSVC39D Method Description:
	 * Queries the FAMILY_PLAN_ITEM table for the goals.
	 *
	 * @param idEvent
	 *            the id event
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FamilyPlanItemDto> getFamilyPlanItemGoal(Long idEvent) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getFamilyPlanItemGoalSql)
				.addScalar("idFamilyPlanItem", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("cdAreaConcern", StandardBasicTypes.STRING)
				.addScalar("cdInitialLevelConcern", StandardBasicTypes.STRING)
				.addScalar("cdCurrentLevelConcern", StandardBasicTypes.STRING)
				.addScalar("txtItemGoals", StandardBasicTypes.STRING)
				.addScalar("dtInitiallyAddressed", StandardBasicTypes.DATE)
				.addScalar("indIdentifiedInRiskAssmnt", StandardBasicTypes.STRING).setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(FamilyPlanItemDto.class));
		return query.list();
	}

	/**
	 * Method Name: getFamilyPlanItems DAM Name: CSVC44D Method Description:
	 * Queries FAMILY_PLAN_ITEM table.
	 *
	 * @param idEvent
	 *            the id event
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FamilyPlanItemDto> getFamilyPlanItems(Long idEvent) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getFamilyPlanItemsSql)
				.addScalar("cdArea", StandardBasicTypes.STRING).addScalar("idFamilyPlanTask", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idFamilyPlanItem", StandardBasicTypes.LONG).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("indCourtOrdered", StandardBasicTypes.STRING)
				.addScalar("txtTask", StandardBasicTypes.STRING).addScalar("dtCreated", StandardBasicTypes.DATE)
				.addScalar("dtCompleted", StandardBasicTypes.DATE).addScalar("dtApproved", StandardBasicTypes.DATE)
				.addScalar("txtAreaOfConcern", StandardBasicTypes.STRING)
				.addScalar("nbrAreaOrder", StandardBasicTypes.LONG).setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(FamilyPlanItemDto.class));
		return query.list();
	}

	/**
	 * Method Name: getTaskForSpecRiskArea Method Description:Queries the
	 * FAMILY_PLAN_TASK table to determine if there are any task for the
	 * specific Risk Area DAM Name: CSVC45D.
	 *
	 * @param idEvent
	 *            the id event
	 * @return @
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<FamilyPlanItemDto> getTaskForSpecRiskArea(Long idEvent) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getTaskForSpecRiskAreaSql)
				.addScalar("cdAreaConcern", StandardBasicTypes.STRING)
				.addScalar("idFamilyPlanItem", StandardBasicTypes.LONG).setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(FamilyPlanItemDto.class));

		return query.list();
	}

	/**
	 * Method Name: updateFamilyPlanItems Method Description: This method will
	 * update Family Plan Item Table.
	 *
	 * @param familyPlanItemDtoList
	 *            the family plan item dto list
	 * @param mode
	 *            the mode
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void updateFamilyPlanItems(List<FamilyPlanItemDto> familyPlanItemDtoList, int mode) {
		for (FamilyPlanItemDto familyPlanItemDto : familyPlanItemDtoList) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FamilyPlanItem.class);
			criteria.add(Restrictions.eq(EVENT_ID, familyPlanItemDto.getIdEvent()));
			criteria.add(Restrictions.eq("capsCase.idCase", familyPlanItemDto.getIdCase()));
			criteria.add(Restrictions.ge("dtLastUpdate", familyPlanItemDto.getFamilyPlanItemDateLastUpdate()));
			// criteria.add(Restrictions.lt("dtLastUpdate",
			// DateUtils.addDays(familyPlanItemDto.getFamilyPlanItemDateLastUpdate(),
			// 1)));

			List<FamilyPlanItem> familyPlanItemList = criteria.list();
			for (FamilyPlanItem familyPlanItem : familyPlanItemList) {
				if (mode == ServiceConstants.APPROVE) {
					familyPlanItem.setCdInitialLevelConcern(familyPlanItemDto.getInitialLevelOfConcernScale());
				} else {
					familyPlanItem.setCdInitialLevelConcern(null);
				}
				sessionFactory.getCurrentSession().saveOrUpdate(familyPlanItem);
			}
		}
	}

	/**
	 * Method Name: updateGoals Method Description: Update the goals table with
	 * DT_APPROVED.
	 *
	 * @param familyPlanGoalValueDtoList
	 *            the family plan goal value dto list
	 * @param mode
	 *            the mode
	 */
	@Override
	public void updateGoals(List<FamilyPlanGoalValueDto> familyPlanGoalValueDtoList, int mode) {

		for (FamilyPlanGoalValueDto familyPlanGoalValueDto : familyPlanGoalValueDtoList) {

			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FamilyPlanGoal.class);

			if (familyPlanGoalValueDto.getDateApproved() == null) {

				criteria.add(Restrictions.eq("idFamilyPlanGoal", familyPlanGoalValueDto.getFamilyPlanGoalId()));
				criteria.add(Restrictions.ge("dtLastUpdate", familyPlanGoalValueDto.getDateLastUpdate()));
				// criteria.add(Restrictions.lt("dtLastUpdate",
				// DateUtils.addDays(familyPlanGoalValueDto.getDateLastUpdate(),
				// 1)));

				FamilyPlanGoal familyPlanGoal = (FamilyPlanGoal) criteria.uniqueResult();

				if (!TypeConvUtil.isNullOrEmpty(familyPlanGoal)) {
					if (mode == ServiceConstants.APPROVE) {
						familyPlanGoal.setDtApproved(new Date());
					} else {
						familyPlanGoal.setDtApproved(null);
					}
					sessionFactory.getCurrentSession().saveOrUpdate(familyPlanGoal);
				}

			} else if (familyPlanGoalValueDto.getDateApproved() != null) {

				criteria.add(Restrictions.eq("idFamilyPlanGoal", familyPlanGoalValueDto.getFamilyPlanGoalId()));
				criteria.add(Restrictions.ge("dtLastUpdate", familyPlanGoalValueDto.getDateLastUpdate()));
				// criteria.add(Restrictions.lt("dtLastUpdate",
				// DateUtils.addDays(familyPlanGoalValueDto.getDateLastUpdate(),
				// 1)));

				FamilyPlanGoal familyPlanGoal = (FamilyPlanGoal) criteria.uniqueResult();

				if (!TypeConvUtil.isNullOrEmpty(familyPlanGoal)) {
					if (mode == ServiceConstants.APPROVE) {
						familyPlanGoal.setDtApproved(familyPlanGoalValueDto.getDateApproved());
					} else {
						familyPlanGoal.setDtApproved(null);
					}
					sessionFactory.getCurrentSession().saveOrUpdate(familyPlanGoal);
				}
			}
		}
	}

	/**
	 * Method Name: updateTasks Method Description: Update Tasks for DT_APPROVED
	 * column.
	 *
	 * @param taskGoalValueDtoList
	 *            the task goal value dto list
	 * @param mode
	 *            the mode
	 * @return Long
	 */
	@Override
	public Long updateTasks(List<TaskGoalValueDto> taskGoalValueDtoList, int mode) {
		Long familyPlanTaskId = ServiceConstants.LongZero;

		for (TaskGoalValueDto taskGoalValueDto : taskGoalValueDtoList) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FamilyPlanTask.class);
			criteria.add(Restrictions.eq("idFamilyPlanTask", taskGoalValueDto.getFamilyPlanTaskId()));
			criteria.add(Restrictions.ge("dtLastUpdate", taskGoalValueDto.getDateLastUpdate()));
			// criteria.add(Restrictions.lt("dtLastUpdate",
			// DateUtils.addDays(taskGoalValueDto.getDateLastUpdate(), 1)));
			FamilyPlanTask familyPlanTask = (FamilyPlanTask) criteria.uniqueResult();

			if (mode == ServiceConstants.APPROVE) {
				familyPlanTask.setDtApproved(new Date());
			} else {
				familyPlanTask.setDtApproved(null);
			}

			sessionFactory.getCurrentSession().saveOrUpdate(familyPlanTask);
			familyPlanTaskId = familyPlanTask.getIdFamilyPlanTask();
		}
		return familyPlanTaskId;
	}

	/**
	 * Method Name: isEventIdFamilyPlanEval Method Description: Returns true is
	 * the input event corresponds to a Family Plan Evaluation.
	 *
	 * @param idEvent
	 *            the id event
	 * @return boolean @
	 */
	@Override
	public boolean isEventIdFamilyPlanEval(Long idEvent) {

		boolean isFamilyPlanEval = false;

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FamilyPlanEval.class);
		criteria.add(Restrictions.eq("eventByIdEvent.idEvent", idEvent));

		FamilyPlanEval familyPlanEval = (FamilyPlanEval) criteria.uniqueResult();

		if (!TypeConvUtil.isNullOrEmpty(familyPlanEval)) {
			isFamilyPlanEval = true;
		}
		return isFamilyPlanEval;
	}

	/**
	 * Method Name: queryApprovedGoals Method Description: Fetches all approved
	 * family plan goals.
	 *
	 * @param idEvent
	 *            the id event
	 * @return List<FamilyPlanGoalValueDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FamilyPlanGoalValueDto> queryApprovedGoals(Long idEvent) {

		SQLQuery sqlQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(queryApprovedGoals)
				.setResultTransformer(Transformers.aliasToBean(FamilyPlanGoalValueDto.class));

		sqlQuery1.addScalar("goalTxt", StandardBasicTypes.STRING);
		sqlQuery1.addScalar("dateLastUpdate", StandardBasicTypes.DATE);
		sqlQuery1.addScalar("dateApproved", StandardBasicTypes.DATE);
		sqlQuery1.addScalar("familyPlanGoalId", StandardBasicTypes.LONG);
		sqlQuery1.addScalar("caseId", StandardBasicTypes.LONG);
		sqlQuery1.addScalar("eventId", StandardBasicTypes.LONG);

		sqlQuery1.setParameter("idEvent", idEvent);

		List<FamilyPlanGoalValueDto> familyPlanGoalValueDtoList = sqlQuery1.list();

		return familyPlanGoalValueDtoList;
	}

	/**
	 * Method Name: queryApprovedTasks Method Description: Fetch all approved
	 * tasks associated with a event id.
	 *
	 * @param idEvent
	 *            the id event
	 * @return List<TaskGoalValueDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<TaskGoalValueDto> queryApprovedTasks(Long idEvent) {

		SQLQuery sqlQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(queryApprovedTasks)
				.setResultTransformer(Transformers.aliasToBean(TaskGoalValueDto.class));

		sqlQuery1.addScalar("familyPlanTaskId", StandardBasicTypes.LONG);
		sqlQuery1.addScalar("dateCreated", StandardBasicTypes.DATE);
		sqlQuery1.addScalar("taskTxt", StandardBasicTypes.STRING);
		sqlQuery1.addScalar("cpsAssignedInd", StandardBasicTypes.BOOLEAN);
		sqlQuery1.addScalar("familyAssignedInd", StandardBasicTypes.BOOLEAN);
		sqlQuery1.addScalar("parentsAssignedInd", StandardBasicTypes.BOOLEAN);
		sqlQuery1.addScalar("cpsCourtOrderedInd", StandardBasicTypes.BOOLEAN);
		sqlQuery1.addScalar("familyCourtOrderedInd", StandardBasicTypes.BOOLEAN);
		sqlQuery1.addScalar("parentsCourtOrderedInd", StandardBasicTypes.BOOLEAN);
		sqlQuery1.addScalar("dateLastUpdate", StandardBasicTypes.DATE);
		sqlQuery1.addScalar("dateNoLongerNeeded", StandardBasicTypes.DATE);
		sqlQuery1.addScalar("dateCompleted", StandardBasicTypes.DATE);

		sqlQuery1.setParameter("idEvent", idEvent);

		List<TaskGoalValueDto> taskGoalValueDtoList = sqlQuery1.list();

		return taskGoalValueDtoList;
	}

	/**
	 * Method Name: getEventInfo Method Description: checking eventId is family
	 * plan in Event table.
	 *
	 * @param eventId
	 *            the event id
	 * @return EventValueDto
	 */
	@Override
	public Event getEventInfo(Long eventId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Event.class);
		criteria.add(Restrictions.eq("idEvent", eventId));
		Event event = (Event) criteria.uniqueResult();
		if (!ObjectUtils.isEmpty(event)) {
			return event;
		} else {
			throw new DataLayerException(messageSource.getMessage(ServiceConstants.DATA_NOT_FOUND, null, Locale.US));
		}
	}

	/**
	 * Method Name: getEventIdFromAppEventLink Method Description: This method
	 * will get details from Event table for the given approval Id.
	 *
	 * @param approvalId
	 *            the approval id
	 * @return List of Event id's
	 */
	@SuppressWarnings("unchecked")
	public List<Long> getEventIdFromAppEventLink(Long approvalId) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getEventIdFromAppEventLink)
				.addScalar("idEvent", StandardBasicTypes.LONG).setParameter("approvalId", approvalId);
		return query.list();
	}

	/**
	 * Method Name: isEventIdFamilyPlan Method Description: This method will
	 * determine if a family plan exist for the given event id.
	 *
	 * @param eventId
	 *            the event id
	 * @return Boolean @
	 */
	public Boolean isEventIdFamilyPlan(Long eventId) {
		Integer count = 0;
		if (null != eventId) {
			Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(isEventIdFamilyPlan)
					.addScalar("EVENTCOUNT", StandardBasicTypes.INTEGER)

					.setParameter("eventId", eventId);
			count = (Integer) query.uniqueResult();
		}
		return (count > 0) ? ServiceConstants.TRUEVAL : ServiceConstants.FALSEVAL;
	}

	/**
	 * Method Name: getFamilyPlanEventId Method Description: This method will
	 * get idFamilyPlanEvent for the given eventId.
	 *
	 * @param eventId
	 *            the event id
	 * @return idFamilyPlanEvent
	 */
	public Long getFamilyPlanEventId(Long eventId) {
		if (null != eventId) {
			Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getFamilyPlanEventId)
					.addScalar("idFamilyPlanEvent", StandardBasicTypes.LONG).setParameter("eventId", eventId);
			return (Long) query.uniqueResult();
		}
		return null;
	}

	/**
	 * Method Name: getEventIdFromAppEventLink Method Description: This method
	 * will get details from Event table for the given approval Id.
	 *
	 * @param eventId
	 *            the event id
	 * @param caseId
	 *            the case id
	 * @return List of Event id's @
	 */
	public List<FamilyPlanItemDto> queryFamilyPlanItems(Long eventId, Long caseId) {
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(queryFamilyPlanItems)
				.setResultTransformer(Transformers.aliasToBean(FamilyPlanItemDto.class));

		sqlQuery.addScalar("idFamilyPlanItem", StandardBasicTypes.LONG);
		sqlQuery.addScalar("dtLastUpdate", StandardBasicTypes.DATE);
		sqlQuery.addScalar("idEvent", StandardBasicTypes.LONG);
		sqlQuery.addScalar("idCase", StandardBasicTypes.LONG);
		sqlQuery.addScalar("cdAreaConcern", StandardBasicTypes.STRING);
		sqlQuery.addScalar("cdInitialLevelConcern", StandardBasicTypes.STRING);
		sqlQuery.addScalar("cdCurrentLevelConcern", StandardBasicTypes.STRING);
		sqlQuery.addScalar("cdPrevLevelConcern", StandardBasicTypes.STRING);
		sqlQuery.addScalar("txtInitialConcerns", StandardBasicTypes.STRING);
		sqlQuery.addScalar("dtInitiallyAddressed", StandardBasicTypes.DATE);
		sqlQuery.addScalar("indIdentifiedInRiskAssmnt", StandardBasicTypes.STRING);
		sqlQuery.setParameter("eventId", eventId);
		sqlQuery.setParameter("caseId", caseId);

		return sqlQuery.list();
	}

	/**
	 * Method Name: queryAllGoals Method Description: This method will get all
	 * goals for the given event id and case id.
	 *
	 * @param eventId
	 *            the event id
	 * @param caseId
	 *            the case id
	 * @return List of Event id's @
	 */
	public List<FamilyPlanGoalValueDto> queryAllGoals(Long eventId, Long caseId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FamilyPlanGoal.class);
		criteria.add(Restrictions.eq(EVENT_ID, eventId));
		criteria.add(Restrictions.eq("capsCase.idCase", caseId));
		List<FamilyPlanGoal> familyPlanGoalList = criteria.list();
		List<FamilyPlanGoalValueDto> familyPlanGoalValueDtoList = new ArrayList<>();
		for (FamilyPlanGoal familyPlanGoal : familyPlanGoalList) {
			FamilyPlanGoalValueDto familyPlanGoalValueDto = new FamilyPlanGoalValueDto();
			familyPlanGoalValueDto.setFamilyPlanGoalId(familyPlanGoal.getIdFamilyPlanGoal());
			familyPlanGoalValueDto.setEventId(familyPlanGoal.getEvent().getIdEvent());
			familyPlanGoalValueDto.setCaseId(familyPlanGoal.getCapsCase().getIdCase());
			familyPlanGoalValueDto.setDateLastUpdate(familyPlanGoal.getDtLastUpdate());
			familyPlanGoalValueDto.setGoalTxt(familyPlanGoal.getTxtGoal());
			familyPlanGoalValueDto.setDateApproved(familyPlanGoal.getDtApproved());
			familyPlanGoalValueDtoList.add(familyPlanGoalValueDto);
		}
		return familyPlanGoalValueDtoList;
	}

	/**
	 * Method Name: queryTasksNotApproved Method Description: This method will
	 * get all the task for the given list of Family Plan Goals.
	 *
	 * @param familyPlanGoalValueDtoList
	 *            the family plan goal value dto list
	 * @param caseId
	 *            the case id
	 * @return List of TaskGoalValueDto @
	 */
	public List<TaskGoalValueDto> queryTasksNotApproved(List<FamilyPlanGoalValueDto> familyPlanGoalValueDtoList,
			Long caseId) {
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(queryTasksNotApproved)
				.setResultTransformer(Transformers.aliasToBean(TaskGoalValueDto.class));

		sqlQuery.addScalar("familyPlanTaskId", StandardBasicTypes.LONG);
		sqlQuery.addScalar("dateLastUpdate", StandardBasicTypes.DATE);
		sqlQuery.addScalar("dateCreated", StandardBasicTypes.DATE);
		sqlQuery.addScalar("parentsCourtOrderedInd", StandardBasicTypes.BOOLEAN);
		sqlQuery.addScalar("familyCourtOrderedInd", StandardBasicTypes.BOOLEAN);
		sqlQuery.addScalar("cpsCourtOrderedInd", StandardBasicTypes.BOOLEAN);
		sqlQuery.addScalar("parentsAssignedInd", StandardBasicTypes.BOOLEAN);
		sqlQuery.addScalar("familyAssignedInd", StandardBasicTypes.BOOLEAN);
		sqlQuery.addScalar("cpsAssignedInd", StandardBasicTypes.BOOLEAN);
		sqlQuery.addScalar("dateNoLongerNeeded", StandardBasicTypes.DATE);
		sqlQuery.addScalar("dateCompleted", StandardBasicTypes.DATE);
		sqlQuery.addScalar("taskTxt", StandardBasicTypes.STRING);
		sqlQuery.setParameterList("goalsINList", buildGoalsINList(familyPlanGoalValueDtoList));
		sqlQuery.setParameter("idCase", caseId);

		return sqlQuery.list();
	}

	/**
	 * Builds the goals IN list.
	 *
	 * @param familyPlanGoalValueDtoList
	 *            the family plan goal value dto list
	 * @return the list
	 */
	private List<Long> buildGoalsINList(List<FamilyPlanGoalValueDto> familyPlanGoalValueDtoList) {
		List<Long> familyPlanGoalIdList = new ArrayList<>();
		for (FamilyPlanGoalValueDto familyPlanGoalValueDto : familyPlanGoalValueDtoList) {
			familyPlanGoalIdList.add(familyPlanGoalValueDto.getFamilyPlanGoalId());
		}
		return familyPlanGoalIdList;
	}

	/**
	 * Method Name: queryWorkloadForSecondaryWorker Method Description: This
	 * method will get the worker ids for given family plan event id.
	 *
	 * @param familyPlanEventId
	 *            the family plan event id
	 * @return List of worker id's @
	 */
	public List<Long> queryWorkloadForSecondaryWorker(Long familyPlanEventId) {
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(queryWorkloadForSecondaryWorker).addScalar("idWkldperson", StandardBasicTypes.LONG);
		sqlQuery.setParameter("role", ServiceConstants.SECONDARY_WORKER);
		sqlQuery.setParameter("eventId", familyPlanEventId);
		return sqlQuery.list();
	}

	/**
	 * Method Name: queryToDo Method Description: This method will get the
	 * worker ids for given family plan event id.
	 *
	 * @param eventIdToBeAproved
	 *            the event id to be aproved
	 * @return Todo @
	 */
	public Todo queryToDo(Long eventIdToBeAproved) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Todo.class);
		criteria.add(Restrictions.eq(EVENT_ID, eventIdToBeAproved));
		List<Todo> todoList = criteria.list();
		return todoList.get(todoList.size() - 1);
	}

	/**
	 * Method Name: queryToDo Method Description: This method will get the
	 * Worker.Employee details by worker/person id
	 *
	 * @param secondaryWorkerId
	 *            the secondary worker id
	 * @return Employee @
	 */
	public String getWorkersName(Long secondaryWorkerId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Employee.class);
		criteria.add(Restrictions.eq("idPerson", secondaryWorkerId));
		Employee employee = (Employee) criteria.uniqueResult();
		return employee.getNmEmployeeFirst() + ", " + employee.getNmEmployeeMiddle() + ", "
				+ employee.getNmEmployeeLast();
	}

	/**
	 * Method Name: addToDoAlertForSecondaryWorker Method Description: This
	 * method will insert a todo Object.
	 *
	 * @param todoToAdd
	 *            the todo to add
	 * @param workerId
	 *            the worker id
	 * @param familyPlanEventId
	 *            the family plan event id
	 * @return void
	 */
	public void addToDoAlertForSecondaryWorker(Todo todoToAdd, Long workerId, Long familyPlanEventId) {
		Criteria personCriteria = sessionFactory.getCurrentSession().createCriteria(Person.class);
		personCriteria.add(Restrictions.eq("idPerson", workerId));
		todoToAdd.setPersonByIdTodoPersAssigned((Person) personCriteria.uniqueResult());

		Criteria eventCriteria = sessionFactory.getCurrentSession().createCriteria(Event.class);
		eventCriteria.add(Restrictions.eq("idEvent", familyPlanEventId));
		todoToAdd.setEvent((Event) eventCriteria.uniqueResult());

		sessionFactory.getCurrentSession().saveOrUpdate(todoToAdd);
	}

	/**
	 * Method Name: deleteFamilyPlan Method Description: Delete family plan
	 * based on Event ID.
	 *
	 * @param idEvent
	 *            the id event
	 */
	@Override
	public void deleteFamilyPlan(Long idEvent) {
		// delete family plan
		Criteria familyPlanCriteria = sessionFactory.getCurrentSession().createCriteria(FamilyPlan.class);
		familyPlanCriteria.add(Restrictions.eq(EVENT_ID, idEvent));
		List<FamilyPlan> familyPlanList = familyPlanCriteria.list();
		if (CollectionUtils.isNotEmpty(familyPlanList))
			familyPlanList.stream().forEach(familyPlan -> {
				// delete family plan participant
				Criteria familyPlanPartcpntCriteria = sessionFactory.getCurrentSession()
						.createCriteria(FamilyPlanPartcpnt.class);
				familyPlanPartcpntCriteria
						.add(Restrictions.eq("familyPlan.idFamilyPlan", familyPlan.getIdFamilyPlan()));
				List<FamilyPlanPartcpnt> familyPlanPartcpntList = familyPlanPartcpntCriteria.list();
				if (CollectionUtils.isNotEmpty(familyPlanPartcpntList))
					familyPlanPartcpntList.stream().forEach(
							familyPlanPartcpnt -> sessionFactory.getCurrentSession().delete(familyPlanPartcpnt));
				// delete family plan need
				Criteria familyPlanNeedCriteria = sessionFactory.getCurrentSession()
						.createCriteria(FamilyPlanNeed.class);
				familyPlanNeedCriteria.add(Restrictions.eq("familyPlan.idFamilyPlan", familyPlan.getIdFamilyPlan()));
				List<FamilyPlanNeed> familyPlanNeedList = familyPlanNeedCriteria.list();
				if (CollectionUtils.isNotEmpty(familyPlanNeedList)) {
					familyPlanNeedList.stream().forEach(familyPlanNeed -> {
						// delete family plan reqrd actns
						Criteria familyPlanReqrdActnCriteria = sessionFactory.getCurrentSession()
								.createCriteria(FamilyPlanReqrdActn.class);
						familyPlanReqrdActnCriteria.add(Restrictions.eq("familyPlanNeed.idFamilyPlanNeeds",
								familyPlanNeed.getIdFamilyPlanNeeds()));
						List<FamilyPlanReqrdActn> familyPlanReqrdActnList = familyPlanReqrdActnCriteria.list();
						if (CollectionUtils.isNotEmpty(familyPlanReqrdActnList)) {
							familyPlanReqrdActnList.stream().forEach(familyPlanReqrdActn -> {
								// delete family plan action resource
								Criteria familyPlanActnRsrcCriteria = sessionFactory.getCurrentSession()
										.createCriteria(FamilyPlanActnRsrc.class);
								familyPlanActnRsrcCriteria
										.add(Restrictions.eq("familyPlanReqrdActn.idFamilyPlanReqrdActns",
												familyPlanReqrdActn.getIdFamilyPlanReqrdActns()));
								List<FamilyPlanActnRsrc> familyPlanActnRsrcList = familyPlanActnRsrcCriteria.list();
								familyPlanActnRsrcList.stream().forEach(familyPlanActnRsrc -> sessionFactory
										.getCurrentSession().delete(familyPlanActnRsrc));
								sessionFactory.getCurrentSession().delete(familyPlanReqrdActn);
							});
						}
						sessionFactory.getCurrentSession().delete(familyPlanNeed);
					});

				}
				sessionFactory.getCurrentSession().delete(familyPlan);
			});
		// delete family plan cost
		Criteria familyPlanCostCriteria = sessionFactory.getCurrentSession().createCriteria(FamilyPlanCost.class);
		familyPlanCostCriteria.add(Restrictions.eq(EVENT_ID, idEvent));
		List<FamilyPlanCost> familyPlanCostList = familyPlanCriteria.list();
		if (CollectionUtils.isNotEmpty(familyPlanCostList))
			familyPlanCostList.stream()
					.forEach(familyPlanCost -> sessionFactory.getCurrentSession().delete(familyPlanCost));
		// delete family plan evaluation
		Criteria familyPlanEvalCriteria = sessionFactory.getCurrentSession().createCriteria(FamilyPlanEval.class);
		familyPlanEvalCriteria.add(Restrictions.eq("eventByIdEvent.idEvent", idEvent));
		List<FamilyPlanEval> familyPlanEvalList = familyPlanEvalCriteria.list();
		if (CollectionUtils.isNotEmpty(familyPlanEvalList))
			familyPlanEvalList.stream().forEach(familyPlanEval -> {
				// delete family plan participant
				Criteria familyPlanPartcpntCriteria = sessionFactory.getCurrentSession()
						.createCriteria(FamilyPlanPartcpnt.class);
				familyPlanPartcpntCriteria.add(Restrictions.eq("familyPlanEval.idFamilyPlanEvaluation",
						familyPlanEval.getIdFamilyPlanEvaluation()));
				List<FamilyPlanPartcpnt> familyPlanPartcpntList = familyPlanPartcpntCriteria.list();
				if (CollectionUtils.isNotEmpty(familyPlanPartcpntList))
					familyPlanPartcpntList.stream().forEach(
							familyPlanPartcpnt -> sessionFactory.getCurrentSession().delete(familyPlanPartcpnt));
				// delete family plan need
				Criteria familyPlanNeedCriteria = sessionFactory.getCurrentSession()
						.createCriteria(FamilyPlanNeed.class);
				familyPlanNeedCriteria.add(Restrictions.eq("familyPlanEval.idFamilyPlanEvaluation",
						familyPlanEval.getIdFamilyPlanEvaluation()));
				List<FamilyPlanNeed> familyPlanNeedList = familyPlanNeedCriteria.list();
				if (CollectionUtils.isNotEmpty(familyPlanNeedList)) {
					familyPlanNeedList.stream().forEach(familyPlanNeed -> {
						// delete family plan reqrd actns
						Criteria familyPlanReqrdActnCriteria = sessionFactory.getCurrentSession()
								.createCriteria(FamilyPlanReqrdActn.class);
						familyPlanReqrdActnCriteria.add(Restrictions.eq("familyPlanNeed.idFamilyPlanNeeds",
								familyPlanNeed.getIdFamilyPlanNeeds()));
						List<FamilyPlanReqrdActn> familyPlanReqrdActnList = familyPlanReqrdActnCriteria.list();
						if (CollectionUtils.isNotEmpty(familyPlanReqrdActnList)) {
							familyPlanReqrdActnList.stream().forEach(familyPlanReqrdActn -> {
								// delete family plan action resource
								Criteria familyPlanActnRsrcCriteria = sessionFactory.getCurrentSession()
										.createCriteria(FamilyPlanActnRsrc.class);
								familyPlanActnRsrcCriteria
										.add(Restrictions.eq("familyPlanReqrdActn.idFamilyPlanReqrdActns",
												familyPlanReqrdActn.getIdFamilyPlanReqrdActns()));
								List<FamilyPlanActnRsrc> familyPlanActnRsrcList = familyPlanActnRsrcCriteria.list();
								familyPlanActnRsrcList.stream().forEach(familyPlanActnRsrc -> sessionFactory
										.getCurrentSession().delete(familyPlanActnRsrc));
								sessionFactory.getCurrentSession().delete(familyPlanReqrdActn);
							});
						}
						sessionFactory.getCurrentSession().delete(familyPlanNeed);
					});

				}
				sessionFactory.getCurrentSession().delete(familyPlanEval);
			});
		// delete family plan eval item
		Criteria familyPlanEvalItemCriteria = sessionFactory.getCurrentSession()
				.createCriteria(FamilyPlanEvalItem.class);
		familyPlanEvalItemCriteria.add(Restrictions.eq(EVENT_ID, idEvent));
		List<FamilyPlanEvalItem> familyPlanEvalItemList = familyPlanEvalItemCriteria.list();
		if (CollectionUtils.isNotEmpty(familyPlanEvalItemList))
			familyPlanEvalItemList.stream()
					.forEach(familyPlanEvalItem -> sessionFactory.getCurrentSession().delete(familyPlanEvalItem));
		// delete FAM_PLN_AOC_GOAL_LINK
		Criteria famPlnAocGoalLinkCriteria = sessionFactory.getCurrentSession().createCriteria(FamPlnAocGoalLink.class);
		famPlnAocGoalLinkCriteria.add(Restrictions.eq(EVENT_ID, idEvent));
		List<FamPlnAocGoalLink> famPlnAocGoalLinkList = famPlnAocGoalLinkCriteria.list();
		if (CollectionUtils.isNotEmpty(famPlnAocGoalLinkList))
			famPlnAocGoalLinkList.stream()
					.forEach(famPlnAocGoalLink -> sessionFactory.getCurrentSession().delete(famPlnAocGoalLink));
		// delete FAM_PLN_TASK_GOAL_LINK
		Criteria famPlnTaskGoalLinkCriteria = sessionFactory.getCurrentSession()
				.createCriteria(FamPlnTaskGoalLink.class);
		famPlnTaskGoalLinkCriteria.add(Restrictions.eq(EVENT_ID, idEvent));
		List<FamPlnTaskGoalLink> famPlnTaskGoalLinkList = famPlnTaskGoalLinkCriteria.list();
		if (CollectionUtils.isNotEmpty(famPlnTaskGoalLinkList))
			famPlnTaskGoalLinkList.stream()
					.forEach(famPlnTaskGoalLink -> sessionFactory.getCurrentSession().delete(famPlnTaskGoalLink));

		// delete family plan goal
		Criteria familyPlanGoalCriteria = sessionFactory.getCurrentSession().createCriteria(FamilyPlanGoal.class);
		familyPlanGoalCriteria.add(Restrictions.eq(EVENT_ID, idEvent));
		List<FamilyPlanGoal> familyPlanGoalList = familyPlanGoalCriteria.list();
		if (CollectionUtils.isNotEmpty(familyPlanGoalList))
			familyPlanGoalList.stream()
					.forEach(familyPlanGoal -> sessionFactory.getCurrentSession().delete(familyPlanGoal));
		// delete family plan item
		Criteria familyPlanItemCriteria = sessionFactory.getCurrentSession().createCriteria(FamilyPlanItem.class);
		familyPlanItemCriteria.add(Restrictions.eq(EVENT_ID, idEvent));
		List<FamilyPlanItem> familyPlanItemList = familyPlanItemCriteria.list();
		if (CollectionUtils.isNotEmpty(familyPlanItemList))
			familyPlanItemList.stream()
					.forEach(familyPlanItem -> sessionFactory.getCurrentSession().delete(familyPlanItem));

		// delete family plan role
		Criteria familyPlanRoleCriteria = sessionFactory.getCurrentSession().createCriteria(FamilyPlanRole.class);
		familyPlanRoleCriteria.add(Restrictions.eq(EVENT_ID, idEvent));
		List<FamilyPlanRole> familyPlanRoleList = familyPlanRoleCriteria.list();
		if (CollectionUtils.isNotEmpty(familyPlanRoleList))
			familyPlanRoleList.stream()
					.forEach(familyPlanRole -> sessionFactory.getCurrentSession().delete(familyPlanRole));
		// delete family plan task
		Criteria familyPlanTaskCriteria = sessionFactory.getCurrentSession().createCriteria(FamilyPlanTask.class);
		familyPlanTaskCriteria.add(Restrictions.eq(EVENT_ID, idEvent));
		List<FamilyPlanTask> familyPlanTaskList = familyPlanTaskCriteria.list();
		if (CollectionUtils.isNotEmpty(familyPlanTaskList))
			familyPlanTaskList.stream()
					.forEach(familyPlanTask -> sessionFactory.getCurrentSession().delete(familyPlanTask));

		// delete CP dependent NARRATIVES
		Map<String, String> cpNarrTables = new HashMap<>();
		eventUtil.getCpNarrTables(cpNarrTables);
		cpNarrTables.forEach((k,v)->{
			Criteria domainCriteria = sessionFactory.getCurrentSession().createCriteria(v);
			if(k.equals("CP_CONCURRENT_GOAL")){
				domainCriteria.add(Restrictions.eq("childPlan.idChildPlanEvent", idEvent));
			}else{
				domainCriteria.add(Restrictions.eq("idEvent", idEvent));
			}
			List<Object> list = domainCriteria.list();
			if (CollectionUtils.isNotEmpty(list)){
				list.stream().forEach(obj -> sessionFactory.getCurrentSession().delete(obj));
			}
		});

		
		// delete event
		Criteria eventCriteria = sessionFactory.getCurrentSession().createCriteria(Event.class);
		eventCriteria.add(Restrictions.eq("idEvent", idEvent));
		List<Event> eventList = eventCriteria.list();
		if (CollectionUtils.isNotEmpty(eventList))
			eventList.stream().forEach(event -> sessionFactory.getCurrentSession().delete(event));
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccChildPlan.class);
		//deleting sscc childplan 
		criteria.add(Restrictions.eq("idChildPlanEvent", idEvent));
		List<SsccChildPlan> ssccChildPlans = criteria.list();

		if (!CollectionUtils.isEmpty(ssccChildPlans)) {
			ssccChildPlans.forEach(ssccChildPlan -> {
				sessionFactory.getCurrentSession().delete(ssccChildPlan);
			});
			
			criteria = sessionFactory.getCurrentSession().createCriteria(ChildPlan.class);
			criteria.add(Restrictions.eq("idChildPlanEvent", idEvent));
			ChildPlan childPlan = (ChildPlan) criteria.uniqueResult();
			if (!ObjectUtils.isEmpty(childPlan)) {
				sessionFactory.getCurrentSession().delete(childPlan);

			}
		}
		
	}

	/**
	 * Method Name: fetchPrincipalsForPlan Method Description:Fetches the
	 * principals for the family plan from EVENT_PERSON_LINK based on Event id.
	 *
	 * @param idEvent
	 *            the id event
	 * @return the array list
	 */
	public ArrayList<Long> fetchPrincipalsForPlan(Long idEvent) {
		ArrayList<Long> personIdList = new ArrayList<Long>();
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(EventPersonLink.class)
				.createAlias("event", "event").createAlias("person", "person")
				.add(Restrictions.and(Restrictions.eq(EVENT_ID, idEvent),
						Restrictions.eq("indFamPlanPrincipal", ServiceConstants.STRING_IND_Y)));
		cr.addOrder(Order.asc("person.idPerson"));
		List<EventPersonLink> eventPersonLinkList = (List<EventPersonLink>) cr.list();
		for (EventPersonLink eventPersonLink : eventPersonLinkList) {
			personIdList.add(eventPersonLink.getPerson().getIdPerson());
		}
		return personIdList;

	}

	/**
	 * Method Name: fetchChildNotInSubcare Method Description: Fetches the child
	 * 1)Whose marital status is "Child Not applicable" 2)Who may have a
	 * placement but with living arrangement of "Return Home" or "Non custodial"
	 * 3)Who does not have date of death 4)Age < 18 5)Should not be a birth
	 * parent.
	 *
	 * @param idPerson
	 *            the id person
	 * @param idCase
	 *            the id case
	 * @param idStage
	 *            the id stage
	 * @return FamilyPlanDto
	 */
	@Override
	public FamilyPlanDto fetchChildNotInSubcare(Long idPerson, Long idCase, Long idStage) {
		FamilyPlanDto familyPlanDto = new FamilyPlanDto();
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(fetchChildNotInSubcareSql)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("dtPersonBirth", StandardBasicTypes.DATE).setParameter("idPerson", idPerson)
				.setParameter("idCase", idCase).setResultTransformer(Transformers.aliasToBean(Person.class));
		Person person = (Person) query.uniqueResult();
		if (!ObjectUtils.isEmpty(person)) {
			Long idperson = person.getIdPerson();
			String nmPersonFull = person.getNmPersonFull();
			Date dob = person.getDtPersonBirth();
			int age = DateUtils.getAge(dob);
			// check the age. Fetch only those children who are less than 18
			// years of age.
			if (age < 18) {
				Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class);
				criteria.add(Restrictions.eq("idStage", idStage));
				criteria.add(Restrictions.eq("idPerson", idPerson));
				criteria.add(Restrictions.not(Restrictions.in("cdStagePersRelInt", new String[] { "PA", "PB" })));

				StagePersonLink stagePersonLink = (StagePersonLink) criteria.uniqueResult();
				familyPlanDto.setIdPerson(idPerson);
				familyPlanDto.setNmPersonFull(nmPersonFull);
			}
		}
		return familyPlanDto;
	}

	/**
	 * Method Name: fetchChildCandidacyRS Method Description: Fetches the risk
	 * of removal and child status from FAMILYCHILDCANDIDACY.
	 *
	 * @param idPerson
	 *            the id person
	 * @param idEvent
	 *            the id event
	 * @return FamilyPlanDto
	 */
	@Override
	public FamilyPlanDto fetchChildCandidacyRS(Long idPerson, Long idEvent) {
		FamilyPlanDto familyPlanDto = new FamilyPlanDto();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FamilyChildCandidacy.class)
				.createAlias("event", "event").createAlias("person", "person");
		criteria.add(Restrictions.eq("person.idPerson", idPerson));
		criteria.add(Restrictions.eq(EVENT_ID, idEvent));
		FamilyChildCandidacy familyChildCandidacy = (FamilyChildCandidacy) criteria.uniqueResult();
		if (!ObjectUtils.isEmpty(familyChildCandidacy)) {
			if(!ObjectUtils.isEmpty(familyChildCandidacy.getIndRiskRemoval())){
				familyPlanDto.setIndRiskOfRemoval(familyChildCandidacy.getIndRiskRemoval().toString());
			}
			if(!ObjectUtils.isEmpty(familyChildCandidacy.getTxtSafetyDecision())){
				familyPlanDto.setTxtSafetyDecision(familyChildCandidacy.getTxtSafetyDecision());
			}
			
			if(!ObjectUtils.isEmpty(familyChildCandidacy.getTxtFinalRiskLvl())){
				familyPlanDto.setTxtFinalRiskLvl(familyChildCandidacy.getTxtFinalRiskLvl());
			}
			
			if(!ObjectUtils.isEmpty(familyChildCandidacy.getTxtFccType())){
				familyPlanDto.setTxtFccType(familyChildCandidacy.getTxtFccType());
			}
			familyPlanDto.setCdCitizenShipCode(familyChildCandidacy.getCdPersonCitizenship());
			familyPlanDto.setDtLastUpdate(familyChildCandidacy.getDtLastUpdate());
		}
		return familyPlanDto;
	}

	/**
	 * Method Name: fetchChildCandidacyRS Method Description: Fetches the risk
	 * of removal and child status from FAMILYCHILDCANDIDACY.
	 *
	 * @param idEvent
	 *            the id event
	 * @return FamilyPlanDto
	 */
	public List<FamilyPlanDto> fetchAllChildCandidacy(Long idEvent) {

		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchAllChildCandidacySql)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("indRiskRemoval", StandardBasicTypes.STRING)
				.addScalar("cdPersonCitizenship", StandardBasicTypes.STRING)
				.addScalar("dtDetermination", StandardBasicTypes.DATE)
				.addScalar("dtReDetermination", StandardBasicTypes.DATE)
				.addScalar("txtSafetyDecision", StandardBasicTypes.STRING)
				.addScalar("txtFinalRiskLvl", StandardBasicTypes.STRING)
				.addScalar("txtFccType", StandardBasicTypes.STRING).setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(FamilyPlanDto.class));

		List<FamilyPlanDto> FamilyChildCandidacyList = (List<FamilyPlanDto>) sQLQuery1.list();
		return FamilyChildCandidacyList;
	}

	/**
	 * Method Name: saveChildCandidacy Method Description: This method will Save
	 * the Child Candidacy details.
	 *
	 * @param familyPlanReq
	 *            the family plan req
	 * @return FamilyPlanRes
	 */
	@Override
	public FamilyPlanRes saveChildCandidacy(FamilyPlanReq familyPlanReq) {
		FamilyPlanRes familyPlanRes = new FamilyPlanRes();
		// fetch the existing child candidacy records for a event id
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(FamilyChildCandidacy.class)
				.createAlias("event", "event").createAlias("person", "person")
				.add(Restrictions.eq(EVENT_ID, familyPlanReq.getIdEvent()));
		cr.addOrder(Order.asc("person.idPerson"));
		cr.setProjection(Projections.projectionList().add(Projections.property("person.idPerson")));
		List<Long> childCndRecsList = cr.list();
		List<FamilyPlanDto> ChildCandidacyList = familyPlanReq.getFamilyPlanDtoList();
		for (FamilyPlanDto dto : ChildCandidacyList) {
			// If the child candidacy row exists then update the row else insert
			// a new row
			if (CollectionUtils.isEmpty(childCndRecsList)
					|| (!CollectionUtils.isEmpty(childCndRecsList) && !childCndRecsList.contains(dto.getIdPerson()))) {
				FamilyChildCandidacy entity = new FamilyChildCandidacy();
				if (dto.getIdPerson() != 0) {
					Person personEntity = (Person) sessionFactory.getCurrentSession().get(Person.class,
							dto.getIdPerson());
					if (!ObjectUtils.isEmpty(personEntity)) {
						entity.setPerson(personEntity);
					}
				}
				if (!ObjectUtils.isEmpty(dto.getIdStage()))
					entity.setIdStage(dto.getIdStage());

				if (!ObjectUtils.isEmpty(dto.getIdEvent())) {
					Event eventEntity = (Event) sessionFactory.getCurrentSession().get(Event.class, dto.getIdEvent());
					if (!ObjectUtils.isEmpty(eventEntity)) {
						entity.setEvent(eventEntity);
					}
				}
				//Defect#13320 - Removed the character conversion 
				if(!ObjectUtils.isEmpty(dto.getIndRiskOfRemoval())){
					entity.setIndRiskRemoval(dto.getIndRiskOfRemoval());
				}
				
				if(!ObjectUtils.isEmpty(dto.getTxtSafetyDecision())){
					entity.setTxtSafetyDecision(dto.getTxtSafetyDecision());
				}
				
				if(!ObjectUtils.isEmpty(dto.getTxtFinalRiskLvl())){
					entity.setTxtFinalRiskLvl(dto.getTxtFinalRiskLvl());
				}
				
				if(!ObjectUtils.isEmpty(dto.getTxtFccType())){
					entity.setTxtFccType(dto.getTxtFccType());
				}
				
				entity.setDtDetermination(dto.getDtDetermination());
				entity.setDtRedetermination(dto.getDtReDetermination());
				// insert the details about child candidacy
				sessionFactory.getCurrentSession().save(entity);
			} else {
				List<FamilyChildCandidacy> childCandidacyList = sessionFactory.getCurrentSession()
						.createCriteria(FamilyChildCandidacy.class).createAlias("event", "event")
						.createAlias("person", "person").add(Restrictions.eq(EVENT_ID, dto.getIdEvent()))
						.add(Restrictions.eq("person.idPerson", dto.getIdPerson())).list();
				if (!CollectionUtils.isEmpty(childCandidacyList)) {
					childCandidacyList.stream().forEach(entity -> {
						//Defect#13320 - Removed the character conversion 
						entity.setIndRiskRemoval(dto.getIndRiskOfRemoval());
						entity.setTxtSafetyDecision(dto.getTxtSafetyDecision());
						entity.setTxtFinalRiskLvl(dto.getTxtFinalRiskLvl());
						entity.setTxtFccType(dto.getTxtFccType());
						entity.setCdPersonCitizenship(dto.getCdCitizenShipCode());
						entity.setDtLastUpdate(Calendar.getInstance().getTime());
						// insert or update the details about child candidacy
						sessionFactory.getCurrentSession().saveOrUpdate(entity);
					});

				}
			}

		}
		return familyPlanRes;
	}

	/**
	 * Method Name: queryLegacyEvents Method Description: Given the event id of
	 * a legacy family plan, family plan evaluation, or family assessment,
	 * queries all family plan, family plan evaluation and family assessment
	 * events for the same stage.
	 *
	 * @param idEvent
	 *            the id event
	 * @return List<EventValueBeanDto> @
	 */
	@Override
	public List<EventValueDto> queryLegacyEvents(Long idEvent) {
		List<EventValueDto> legacyEventsVector = new ArrayList<EventValueDto>();
		SQLQuery sql = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(queryLegacyEvents)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("cdEventType", StandardBasicTypes.STRING)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdEventTask", StandardBasicTypes.STRING).addScalar("eventDescr", StandardBasicTypes.STRING)
				.addScalar("dtEventOccurred", StandardBasicTypes.DATE)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING)
				.addScalar("dtEventCreated", StandardBasicTypes.DATE)
				.addScalar("dtEventModified", StandardBasicTypes.DATE).setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(EventValueDto.class));
		List<EventValueDto> liEvent = sql.list();

		Long legacyEventId;
		for (EventValueDto beanDto : liEvent) {
			if (beanDto.getIdEvent() > ServiceConstants.ZERO_VAL) {
				legacyEventId = beanDto.getIdEvent();
				EventValueDto eventValueDto = queryEvent(legacyEventId);
				legacyEventsVector.add(eventValueDto);
			}
		}
		return legacyEventsVector;
	}

	/**
	 * Method Name: queryEvent Method Description: Queries event information for
	 * the given event id.
	 *
	 * @param idEvent
	 *            the id event
	 * @return EventValueBeanDto @
	 */
	@Override
	public EventValueDto queryEvent(Long idEvent) {
		SQLQuery sql = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(queryEvent)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("cdEventType", StandardBasicTypes.STRING)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdEventTask", StandardBasicTypes.STRING).addScalar("eventDescr", StandardBasicTypes.STRING)
				.addScalar("dtEventOccurred", StandardBasicTypes.DATE)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING).setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(EventValueDto.class));
		EventValueDto event = (EventValueDto) sql.uniqueResult();

		if (!TypeConvUtil.isNullOrEmpty(event)) {
			SQLQuery sql2 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(queryEventApprovalDtl)
					.addScalar("idApproval", StandardBasicTypes.LONG)
					.addScalar("dtApprovalLastUpdate", StandardBasicTypes.DATE)
					.addScalar("idApprovalPerson", StandardBasicTypes.LONG)
					.addScalar("approvalTopic", StandardBasicTypes.STRING)
					.addScalar("dtApprovalDate", StandardBasicTypes.DATE).setParameter("idCase", event.getIdCase())
					.setParameter("idEvent", idEvent)
					.setResultTransformer(Transformers.aliasToBean(EventValueDto.class));
			List<EventValueDto> eventList = sql2.list();

			if (eventList.size() > ServiceConstants.Zero) {
				EventValueDto finalEvent = eventList.get(ServiceConstants.Zero);
				event.setDtApprovalDate(finalEvent.getDtApprovalDate());
				event.setDtApprovalLastUpdate(finalEvent.getDtApprovalLastUpdate());
				event.setIdApproval(finalEvent.getIdApproval());
				event.setIdApprovalPerson(finalEvent.getIdApprovalPerson());
				event.setApprovalTopic(finalEvent.getApprovalTopic());
			}
			if (!TypeConvUtil.isNullOrEmpty(event.getIdPerson())) {
				SQLQuery sql3 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(queryEventNmDtl)
						.addScalar("nmCreatorsFirst", StandardBasicTypes.STRING)
						.addScalar("nmCreatorsLast", StandardBasicTypes.STRING)
						.setParameter("idPerson", event.getIdPerson())
						.setResultTransformer(Transformers.aliasToBean(EventValueDto.class));
				EventValueDto eventFinal = (EventValueDto) sql3.uniqueResult();

				if (!TypeConvUtil.isNullOrEmpty(eventFinal)) {
					event.setNmCreatorsFirst(eventFinal.getNmCreatorsFirst());
					event.setNmCreatorsLast(eventFinal.getNmCreatorsLast());
				}
			}
		}
		return event;
	}

	/**
	 * Method Name: checkIfEventIsLegacy Method Description: This method checks
	 * the given event id to determine whether or not the event was created
	 * before the initial launch of IMPACT.
	 *
	 * @param idEvent
	 *            the id event
	 * @return boolean @
	 */
	@Override
	public boolean checkIfEventIsLegacy(Long idEvent) {

		boolean isLegacyEvent = true;

		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(checkIfEventIsLegacy)
				.setResultTransformer(Transformers.aliasToBean(EventPlanLinkDto.class));

		sqlQuery.addScalar("idEventFamilyPlanLink", StandardBasicTypes.LONG);
		sqlQuery.addScalar("dtLastUpdate", StandardBasicTypes.DATE);
		sqlQuery.addScalar("idEvent", StandardBasicTypes.LONG);
		sqlQuery.addScalar("indImpactCreated", StandardBasicTypes.STRING);
		sqlQuery.setParameter("idEvent", idEvent);

		List<EventPlanLinkDto> eventPlanLinkDtos = sqlQuery.list();

		if (!TypeConvUtil.isNullOrEmpty(eventPlanLinkDtos)) {
			for (EventPlanLinkDto eventPlanLinkDto : eventPlanLinkDtos) {
				if (eventPlanLinkDto.getIndImpactCreated() != null
						&& eventPlanLinkDto.getIndImpactCreated().equals(ServiceConstants.Y)) {
					isLegacyEvent = false;
				}
			}
		}

		return isLegacyEvent;
	}

	/**
	 * Method Name: queryFamilyPlan Method Description: Query the family plan
	 * details from the database.
	 *
	 * @param familyPlanDto
	 *            the family plan dto
	 * @return FamilyPlanDto
	 */
	@Override
	public FamilyPlanDto queryFamilyPlan(FamilyPlanDto familyPlanDto) {
		List<FamilyPlanItemDto> familyPlanItems = new ArrayList<FamilyPlanItemDto>();

		// Queries the Family Plan Details from FAMILY_PLAN Table
		SQLQuery queryFamilyPlansql = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(queryFamilyPlan)
				.addScalar("idFamilyPlan", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("dtCompleted", StandardBasicTypes.DATE).addScalar("cdPlanType", StandardBasicTypes.STRING)
				.addScalar("dtNextDue", StandardBasicTypes.DATE)
				.addScalar("reasonForCPSInvolvement", StandardBasicTypes.STRING)
				.addScalar("indClientComments", StandardBasicTypes.STRING)
				.addScalar("txtStrngthsRsrcs", StandardBasicTypes.STRING)
				.addScalar("txtNotParticipate", StandardBasicTypes.STRING)
				.addScalar("permanencyGoalsComment", StandardBasicTypes.STRING)
				.addScalar("txtCommunitySupports", StandardBasicTypes.STRING)
				.addScalar("txtHopesDreams", StandardBasicTypes.STRING)
				.addScalar("txtOtherParticipants", StandardBasicTypes.STRING)
				.addScalar("txtRespChildsEducation", StandardBasicTypes.STRING)
				.addScalar("cdFgdmConference", StandardBasicTypes.STRING)
				.addScalar("txtCelebration", StandardBasicTypes.STRING)
				.addScalar("txtPurposeReconference", StandardBasicTypes.STRING)
				.addScalar("nbrFamPlanVersion", StandardBasicTypes.STRING)
				.addScalar("idFamilyPlanItem", StandardBasicTypes.LONG)
				.addScalar("dtFamilyPlanItemLastUpdate", StandardBasicTypes.DATE)
				.addScalar("cdAreaConcern", StandardBasicTypes.STRING)
				.addScalar("cdInitialLevelConcern", StandardBasicTypes.STRING)
				.addScalar("cdCurrentLevelConcern", StandardBasicTypes.STRING)
				.addScalar("txtItemGoals", StandardBasicTypes.STRING)
				.addScalar("dtInitiallyAddressed", StandardBasicTypes.DATE)
				.addScalar("indIdentifiedInRiskAssmnt", StandardBasicTypes.STRING)
				.addScalar("nbrAreaOrder", StandardBasicTypes.LONG).addScalar("txtArea", StandardBasicTypes.STRING)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("dtStageStarted", StandardBasicTypes.DATE)
				.setParameter("idEvent", familyPlanDto.getFamilyPlanEvent().getIdEvent())
				.setParameter("idCase", familyPlanDto.getIdCase())
				.setResultTransformer(Transformers.aliasToBean(FamilyPlanDto.class));
		@SuppressWarnings("unchecked")
		List<FamilyPlanDto> familyPlanDtoList = (List<FamilyPlanDto>) queryFamilyPlansql.list();

		// Queries the Family Plan Event Details from the EVENT Table
		Boolean firstRow = ServiceConstants.TRUEVAL;
		if (!TypeConvUtil.isNullOrEmpty(familyPlanDtoList)) {
			for (FamilyPlanDto planDto : familyPlanDtoList) {
				if (firstRow) {
					firstRow = ServiceConstants.FALSEVAL;

					// Setting the Family Plan Details fetched from FAMILY_PLAN
					// Table
					familyPlanDto.setIndClientComments(planDto.getIndClientComments());
					familyPlanDto.setDtNextDue(planDto.getDtNextDue());
					familyPlanDto.setDtCompleted(planDto.getDtCompleted());
					familyPlanDto.setTxtNotParticipate(planDto.getTxtNotParticipate());
					familyPlanDto.setExplanationOfClientNonParticipation(planDto.getTxtNotParticipate());
					familyPlanDto.setPermanencyGoalsComment(planDto.getPermanencyGoalsComment());
					familyPlanDto.setDtLastUpdate(planDto.getDtLastUpdate());
					familyPlanDto.setIdFamilyPlan(planDto.getIdFamilyPlan());
					familyPlanDto.setCdPlanType(planDto.getCdPlanType());
					familyPlanDto.setReasonForCPSInvolvement(planDto.getReasonForCPSInvolvement());
					familyPlanDto.setStrengthsAndResources(planDto.getTxtStrngthsRsrcs());
					familyPlanDto.setDtStageStarted(planDto.getDtStageStarted());
					familyPlanDto.setNbrFamPlanVersion(planDto.getNbrFamPlanVersion());
					familyPlanDto.setIdStage(planDto.getIdStage());
					familyPlanDto.setTxtCommunitySupports(planDto.getTxtCommunitySupports());
					familyPlanDto.setTxtHopesDreams(planDto.getTxtHopesDreams());
					familyPlanDto.setTxtOtherParticipants(planDto.getTxtOtherParticipants());
					familyPlanDto.setTxtRespChildsEducation(planDto.getTxtRespChildsEducation());
					familyPlanDto.setTxtCelebration(planDto.getTxtCelebration());
					familyPlanDto.setTxtPurposeReconference(planDto.getTxtPurposeReconference());
					familyPlanDto.setCdFgdmConference(planDto.getCdFgdmConference());

				}

				FamilyPlanItemDto familyPlanItem = new FamilyPlanItemDto();
				familyPlanItem.setCaseId(planDto.getIdCase());
				familyPlanItem.setIdFamilyPlanItem(planDto.getIdFamilyPlanItem());
				// Queries the Details from the FAMILY_PLAN_ITEM
				familyPlanItem = queryFamilyPlanItem(familyPlanItem);
				familyPlanItems.add(familyPlanItem);

			}
		}
		// Setting the Family Plan Item List Details
		familyPlanDto.setFamilyPlanItemList(familyPlanItems);

		// Queries the Details from FAMILY_PLAN_EVAL Table
		familyPlanDto = queryFamilyPlanEvaluations(familyPlanDto);

		// Queries the Primary Worker Details from the WORKLOAD Table
		Long idPrimaryWorker = workLoadDao.getPersonIdByRole(familyPlanDto.getIdStage(),
				ServiceConstants.stagePersonRoleOpen);

		// Setting the Primary Worker Details
		if (idPrimaryWorker > ServiceConstants.ZERO_VAL) {
			familyPlanDto.setIdPrimaryWorker(idPrimaryWorker);
		}
		// set family plan cost
		familyPlanDto = queryFamilyPlanCost(familyPlanDto);
		// Returning the FamilyPlanDto
		return familyPlanDto;
	}

	/**
	 * Method Name: queryFamilyPlanEvalItems Method Description: Query the
	 * Family Plan evaluation items from the database.
	 *
	 * @param familyPlanItemDto
	 *            the family plan item dto
	 * @return FamilyPlanItemDto
	 */

	@SuppressWarnings("unchecked")
	@Override
	public FamilyPlanItemDto queryFamilyPlanEvalItems(FamilyPlanItemDto familyPlanItemDto) {

		List<FamilyPlanEvalItemDto> familyPlanEvalItems = new ArrayList<FamilyPlanEvalItemDto>();
		SQLQuery queryFamilyPlanEvalItemssql = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(queryFamilyPlanEvalItems).addScalar("idFamilyPlanEvalItem", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idFamilyPlanEvalEvent", StandardBasicTypes.LONG)
				.addScalar("idFamilyPlanItem", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("txtItemEvaluation", StandardBasicTypes.STRING)
				.addScalar("dtCompleted", StandardBasicTypes.DATE).setParameter("idCase", familyPlanItemDto.getCaseId())
				.setParameter("idFamilyPlanItem", familyPlanItemDto.getIdFamilyPlanItem())
				.setResultTransformer(Transformers.aliasToBean(FamilyPlanEvalItemDto.class));
		familyPlanEvalItems = (List<FamilyPlanEvalItemDto>) queryFamilyPlanEvalItemssql.list();

		// Setting the Family Plan Evaluation Item Details fetched from
		// FAMILY_PLAN_EVAL_ITEM Table
		if (familyPlanEvalItems.size() > ServiceConstants.Zero) {
			familyPlanItemDto.setFamilyPlanEvalItemDtoList(familyPlanEvalItems);
		}

		return familyPlanItemDto;
	}

	/**
	 * Method Name: queryFamilyPlanItem Method Description: Query the family
	 * plan item details from the database.
	 *
	 * @param familyPlanItemDto
	 *            the family plan item dto
	 * @return FamilyPlanItemDto
	 */

	@SuppressWarnings("unchecked")
	@Override
	public FamilyPlanItemDto queryFamilyPlanItem(FamilyPlanItemDto familyPlanItemDto) {

		// Fetch the Family Plan Item Details queried from FAMILY_PLAN_ITEM
		SQLQuery queryFamilyPlanItemsql = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(queryFamilyPlanItem).addScalar("idFamilyPlanItem", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("cdAreaConcern", StandardBasicTypes.STRING)
				.addScalar("cdInitialLevelConcern", StandardBasicTypes.STRING)
				.addScalar("cdCurrentLevelConcern", StandardBasicTypes.STRING)
				.addScalar("txtItemGoals", StandardBasicTypes.STRING)
				.addScalar("dtInitiallyAddressed", StandardBasicTypes.TIMESTAMP)
				.addScalar("indIdentifiedInRiskAssmnt", StandardBasicTypes.STRING)
				.addScalar("cdArea", StandardBasicTypes.STRING).setParameter("idCase", familyPlanItemDto.getCaseId())
				.setParameter("idFamilyPlanItem", familyPlanItemDto.getIdFamilyPlanItem())
				.setResultTransformer(Transformers.aliasToBean(FamilyPlanItemDto.class));
		FamilyPlanItemDto familyPlanItem = (FamilyPlanItemDto) queryFamilyPlanItemsql.uniqueResult();

		// Setting the Family Plan Item Details queried from FAMILY_PLAN_ITEM
		if (!TypeConvUtil.isNullOrEmpty(familyPlanItem)) {
			familyPlanItemDto.setCdAreaConcern(familyPlanItem.getCdAreaConcern());
			familyPlanItemDto.setCdArea(familyPlanItem.getCdArea());
			familyPlanItemDto.setIdCase(familyPlanItem.getIdCase());
			familyPlanItemDto.setCdCurrentLevelConcern(familyPlanItem.getCdCurrentLevelConcern());
			familyPlanItemDto.setDtInitiallyAddressed(familyPlanItem.getDtInitiallyAddressed());
			familyPlanItemDto.setIdEvent(familyPlanItem.getIdEvent());
			familyPlanItemDto.setDtLastUpdate(familyPlanItem.getDtLastUpdate());
			familyPlanItemDto.setIdFamilyPlanItem(familyPlanItem.getIdFamilyPlanItem());
			familyPlanItemDto.setTxtItemGoals(familyPlanItem.getTxtItemGoals());
			familyPlanItemDto.setIndIdentifiedInRiskAssmnt(familyPlanItem.getIndIdentifiedInRiskAssmnt());
			familyPlanItemDto.setCdInitialLevelConcern(familyPlanItem.getCdInitialLevelConcern());
		}

		// Fetch the Family Plan Task Details which are in Open Status queried
		// from
		// FAMILY_PLAN_TASK
		List<FamilyPlanTaskDto> familyPlanTasksVector = new ArrayList<FamilyPlanTaskDto>();

		SQLQuery queryOpenFamilyPlanTasksql = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(queryOpenFamilyPlanTask).addScalar("idFamilyPlanTask", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idFamilyPlanItem", StandardBasicTypes.LONG).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("indCourtOrdered", StandardBasicTypes.STRING)
				.addScalar("txtTask", StandardBasicTypes.STRING).addScalar("dtCreated", StandardBasicTypes.DATE)
				.addScalar("dtCompleted", StandardBasicTypes.DATE).setParameter("idCase", familyPlanItemDto.getCaseId())
				.setParameter("idFamilyPlanItem", familyPlanItemDto.getIdFamilyPlanItem())
				.setResultTransformer(Transformers.aliasToBean(FamilyPlanTaskDto.class));
		List<FamilyPlanTaskDto> openFamilyPlanTaskDtoList = (List<FamilyPlanTaskDto>) queryOpenFamilyPlanTasksql.list();

		// Setting the Family Plan Task Details which are in Open Status queried
		// from
		// FAMILY_PLAN_TASK
		if (!TypeConvUtil.isNullOrEmpty(openFamilyPlanTaskDtoList)) {
			for (FamilyPlanTaskDto familyPlanTaskDto : openFamilyPlanTaskDtoList) {
				familyPlanTasksVector.add(familyPlanTaskDto);
			}
		}

		// Fetch the Family Plan Task Details which are in Closed Status queried
		// from
		// FAMILY_PLAN_TASK
		SQLQuery queryCloseFamilyPlanTasksql = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(queryCloseFamilyPlanTask).addScalar("idFamilyPlanTask", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idFamilyPlanItem", StandardBasicTypes.LONG).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("indCourtOrdered", StandardBasicTypes.STRING)
				.addScalar("txtTask", StandardBasicTypes.STRING).addScalar("dtCreated", StandardBasicTypes.DATE)
				.addScalar("dtCompleted", StandardBasicTypes.DATE).setParameter("idCase", familyPlanItemDto.getCaseId())
				.setParameter("idFamilyPlanItem", familyPlanItemDto.getIdFamilyPlanItem())
				.setResultTransformer(Transformers.aliasToBean(FamilyPlanTaskDto.class));
		List<FamilyPlanTaskDto> closeFamilyPlanTaskDtoList = (List<FamilyPlanTaskDto>) queryCloseFamilyPlanTasksql
				.list();

		// Setting the Family Plan Task Details which are in Closed Status
		// queried from
		// FAMILY_PLAN_TASK
		if (!TypeConvUtil.isNullOrEmpty(closeFamilyPlanTaskDtoList)) {
			for (FamilyPlanTaskDto familyPlanTaskDto : closeFamilyPlanTaskDtoList) {
				familyPlanTasksVector.add(familyPlanTaskDto);
			}
		}

		// Setting the queries Family Plan Task List to the FamilyPlanDto
		if (familyPlanTasksVector.size() > ServiceConstants.Zero) {
			familyPlanItemDto.setFamilyPlanTaskDtoList(familyPlanTasksVector);
		}

		// Query the Family Plan Evaluation Item Details
		familyPlanItemDto = queryFamilyPlanEvalItems(familyPlanItemDto);

		return familyPlanItemDto;
	}

	/**
	 * Method Name: queryFamilyPlanTask Method Description: This method will
	 * query the Family Plan task table to retrieve the task.
	 *
	 * @param eventId
	 *            the event id
	 * @param caseId
	 *            the case id
	 * @return List<TaskGoalValueDto> @
	 */
	@Override
	public List<TaskGoalValueDto> queryFamilyPlanTask(Long eventId, Long caseId) {

		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(queryFamilyPlanTask)
				.setResultTransformer(Transformers.aliasToBean(TaskGoalValueDto.class));

		sqlQuery.addScalar("familyPlanTaskId", StandardBasicTypes.LONG);
		sqlQuery.addScalar("dateLastUpdate", StandardBasicTypes.TIMESTAMP);
		sqlQuery.addScalar("familyPlanItemId", StandardBasicTypes.LONG);
		sqlQuery.addScalar("courtOrderedInd", StandardBasicTypes.BOOLEAN);
		sqlQuery.addScalar("taskTxt", StandardBasicTypes.STRING);
		sqlQuery.addScalar("dateCreated", StandardBasicTypes.DATE);
		sqlQuery.addScalar("dateCompleted", StandardBasicTypes.DATE);
		sqlQuery.addScalar("dateApproval", StandardBasicTypes.DATE);
		sqlQuery.addScalar("dateNoLongerNeeded", StandardBasicTypes.DATE);
		sqlQuery.addScalar("cpsAssignedInd", StandardBasicTypes.BOOLEAN);
		sqlQuery.addScalar("familyAssignedInd", StandardBasicTypes.BOOLEAN);
		sqlQuery.addScalar("parentsAssignedInd", StandardBasicTypes.BOOLEAN);
		sqlQuery.addScalar("cpsCourtOrderedInd", StandardBasicTypes.BOOLEAN);
		sqlQuery.addScalar("familyCourtOrderedInd", StandardBasicTypes.BOOLEAN);
		sqlQuery.addScalar("parentsCourtOrderedInd", StandardBasicTypes.BOOLEAN);

		sqlQuery.setParameter("idEvent", eventId);
		sqlQuery.setParameter("idCase", caseId);

		List<TaskGoalValueDto> taskGoalValueDtoList = sqlQuery.list();

		if (TypeConvUtil.isNullOrEmpty(taskGoalValueDtoList)) {
			throw new DataNotFoundException(messageSource
					.getMessage("FamilyPlanTaskDaoImpl.queryFamilyPlanTask.idEvent.idCase.mandatory", null, Locale.US));
		}

		for (TaskGoalValueDto goalValueDto : taskGoalValueDtoList) {

			if ((goalValueDto.getCourtOrderedInd())
					&& (!TypeConvUtil.isNullOrEmpty(goalValueDto.getCourtOrderedInd()))) {
				goalValueDto.setFamilyCourtOrderedInd(ServiceConstants.TRUEVAL);
			}

			if ((goalValueDto.getCpsAssignedInd()) && (!TypeConvUtil.isNullOrEmpty(goalValueDto.getCpsAssignedInd()))) {
				goalValueDto.setCpsAssignedInd(ServiceConstants.TRUEVAL);
			}
			if ((goalValueDto.getFamilyAssignedInd())
					&& (!TypeConvUtil.isNullOrEmpty(goalValueDto.getFamilyAssignedInd()))) {
				goalValueDto.setFamilyAssignedInd(ServiceConstants.TRUEVAL);
			}
			if ((goalValueDto.getParentsAssignedInd())
					&& (!TypeConvUtil.isNullOrEmpty(goalValueDto.getParentsAssignedInd()))) {
				goalValueDto.setParentsAssignedInd(ServiceConstants.TRUEVAL);
			}
			if ((goalValueDto.getCpsCourtOrderedInd())
					&& (!TypeConvUtil.isNullOrEmpty(goalValueDto.getCpsCourtOrderedInd()))) {
				goalValueDto.setCpsCourtOrderedInd(ServiceConstants.TRUEVAL);
			}
			if ((goalValueDto.getFamilyCourtOrderedInd())
					&& (!TypeConvUtil.isNullOrEmpty(goalValueDto.getFamilyCourtOrderedInd()))) {
				goalValueDto.setFamilyCourtOrderedInd(ServiceConstants.TRUEVAL);
			}
			if ((goalValueDto.getParentsCourtOrderedInd())
					&& (!TypeConvUtil.isNullOrEmpty(goalValueDto.getParentsCourtOrderedInd()))) {
				goalValueDto.setParentsCourtOrderedInd(ServiceConstants.TRUEVAL);
			}

			if (!TypeConvUtil.isNullOrEmpty(goalValueDto.getDateCompleted())) {
				goalValueDto.setTaskCompleted(ServiceConstants.TRUEVAL);
			}

			if (!TypeConvUtil.isNullOrEmpty(goalValueDto.getDateNoLongerNeeded())) {
				goalValueDto.setNoLongerNeeded(ServiceConstants.TRUEVAL);
			}
		}

		if (!taskGoalValueDtoList.isEmpty()) {
			taskGoalValueDtoList = queryTaskGoalLink(taskGoalValueDtoList, eventId, caseId);
		}
		return taskGoalValueDtoList;
	}

	/**
	 * Method Name: queryFamilyPlanGoal Method Description: Select List of all
	 * Goals associated with a particular Family Plan event.
	 *
	 * @param eventId
	 *            the event id
	 * @param caseId
	 *            the case id
	 * @return familyPlanGoalValueDtoList @
	 */
	@Override
	public List<FamilyPlanGoalValueDto> queryFamilyPlanGoal(Long eventId, Long caseId) {

		List<FamilyPlanGoalValueDto> familyPlanGoalValueDtos = new ArrayList<>();

		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(queryFamilyPlanGoalSql)
				.setResultTransformer(Transformers.aliasToBean(FamilyPlanGoalValueDto.class));

		sqlQuery.addScalar("familyPlanGoalId", StandardBasicTypes.LONG);
		sqlQuery.addScalar("dateLastUpdate", StandardBasicTypes.DATE);
		sqlQuery.addScalar("goalTxt", StandardBasicTypes.STRING);
		sqlQuery.addScalar("dateApproved", StandardBasicTypes.DATE);

		sqlQuery.setParameter("idEvent", eventId);
		sqlQuery.setParameter("idCase", caseId);

		familyPlanGoalValueDtos = sqlQuery.list();

		return familyPlanGoalValueDtos;
	}

	/**
	 * Method Name: queryTaskGoalLink Method Description: This method will query
	 * the task goal link table.
	 *
	 * @param taskGoalValueDtos
	 *            the task goal value dtos
	 * @param eventId
	 *            the event id
	 * @param caseId
	 *            the case id
	 * @return List<TaskGoalValueDto>
	 */
	@Override
	public List<TaskGoalValueDto> queryTaskGoalLink(List<TaskGoalValueDto> taskGoalValueDtos, Long eventId,
			Long caseId) {

		List<TaskGoalValueDto> taskGoalValueDtoList = new ArrayList<TaskGoalValueDto>();

		for (TaskGoalValueDto taskGoalValueDto : taskGoalValueDtos) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FamPlnTaskGoalLink.class);
			TaskGoalValueDto outTaskGoalValueDto = new TaskGoalValueDto();
			outTaskGoalValueDto = taskGoalValueDto;

			Long idFamilyPlanTask = outTaskGoalValueDto.getFamilyPlanTaskId();

			criteria.add(Restrictions.eq("familyPlanTask.idFamilyPlanTask", idFamilyPlanTask));
			criteria.add(Restrictions.eq(EVENT_ID, eventId));
			criteria.add(Restrictions.eq("capsCase.idCase", caseId));

			List<FamPlnTaskGoalLink> famPlnTaskGoalLinks = criteria.list();

			if (TypeConvUtil.isNullOrEmpty(famPlnTaskGoalLinks)) {
				throw new DataNotFoundException(messageSource.getMessage(
						"FamilyPlanTaskDaoImpl.queryTaskGoalLink.idFamilyPlanGoal.mandatory", null, Locale.US));
			}

			List<Long> mappedGoalsList = new ArrayList<>();
			for (FamPlnTaskGoalLink famPlnTaskGoalLink : famPlnTaskGoalLinks) {
				mappedGoalsList.add(famPlnTaskGoalLink.getIdFamilyPlanGoal());
			}
			outTaskGoalValueDto.setGoalArray(mappedGoalsList);

			taskGoalValueDtoList.add(outTaskGoalValueDto);
		}

		return taskGoalValueDtoList;

	}

	/**
	 * Method Name: queryApprovedActiveTasks Method Description: This method
	 * will query the Family Plan task table to retrieve the approved active
	 * task list.
	 *
	 * @param eventId
	 *            the event id
	 * @param caseId
	 *            the case id
	 * @param currentIdEvent
	 *            the current id event
	 * @return List<TaskGoalValueDto>
	 */
	@Override
	public List<TaskGoalValueDto> queryApprovedActiveTasks(Long eventId, Long caseId, Long currentIdEvent) {

		List<TaskGoalValueDto> taskGoalValueDtos = new ArrayList<>();

		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(queryApprovedActiveTasksSql)
				.setResultTransformer(Transformers.aliasToBean(TaskGoalValueDto.class));

		sqlQuery.addScalar("familyPlanTaskId", StandardBasicTypes.LONG);
		sqlQuery.addScalar("dateLastUpdate", StandardBasicTypes.TIMESTAMP);
		sqlQuery.addScalar("familyPlanItemId", StandardBasicTypes.LONG);
		sqlQuery.addScalar("taskTxt", StandardBasicTypes.STRING);
		sqlQuery.addScalar("courtOrderedInd", StandardBasicTypes.BOOLEAN);
		sqlQuery.addScalar("cpsCourtOrderedInd", StandardBasicTypes.BOOLEAN);
		sqlQuery.addScalar("familyCourtOrderedInd", StandardBasicTypes.BOOLEAN);
		sqlQuery.addScalar("parentsCourtOrderedInd", StandardBasicTypes.BOOLEAN);
		sqlQuery.addScalar("cpsAssignedInd", StandardBasicTypes.BOOLEAN);
		sqlQuery.addScalar("familyAssignedInd", StandardBasicTypes.BOOLEAN);
		sqlQuery.addScalar("parentsAssignedInd", StandardBasicTypes.BOOLEAN);
		sqlQuery.addScalar("dateCreated", StandardBasicTypes.DATE);
		sqlQuery.addScalar("dateCompleted", StandardBasicTypes.DATE);
		sqlQuery.addScalar("dateNoLongerNeeded", StandardBasicTypes.DATE);
		sqlQuery.addScalar("dateApproval", StandardBasicTypes.DATE);

		sqlQuery.setParameter("idEvent", eventId);
		sqlQuery.setParameter("idCase", caseId);
		sqlQuery.setParameter("currentIdEvent", currentIdEvent);

		taskGoalValueDtos = sqlQuery.list();
		if (!taskGoalValueDtos.isEmpty()) {
			taskGoalValueDtos = queryTaskGoalLink(taskGoalValueDtos, eventId, caseId);
		}
		return taskGoalValueDtos;
	}

	/**
	 * Method Name: queryApprovedInActiveTasks Method Description: This method
	 * will query the Family Plan task table to retrieve the approved list of
	 * inactive tasks.
	 *
	 * @param eventId
	 *            the event id
	 * @param caseId
	 *            the case id
	 * @param currentIdEvent
	 *            the current id event
	 * @return List<TaskGoalValueDto>
	 */
	@Override
	public List<TaskGoalValueDto> queryApprovedInActiveTasks(Long eventId, Long caseId, Long currentIdEvent) {
		List<TaskGoalValueDto> taskGoalValueDtos = new ArrayList<>();
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(queryApprovedInActiveTasksSql)
				.setResultTransformer(Transformers.aliasToBean(TaskGoalValueDto.class));

		sqlQuery.addScalar("familyPlanTaskId", StandardBasicTypes.LONG);
		sqlQuery.addScalar("dateLastUpdate", StandardBasicTypes.TIMESTAMP);
		sqlQuery.addScalar("familyPlanItemId", StandardBasicTypes.LONG);
		sqlQuery.addScalar("taskTxt", StandardBasicTypes.STRING);
		sqlQuery.addScalar("courtOrderedInd", StandardBasicTypes.BOOLEAN);
		sqlQuery.addScalar("cpsCourtOrderedInd", StandardBasicTypes.BOOLEAN);
		sqlQuery.addScalar("familyCourtOrderedInd", StandardBasicTypes.BOOLEAN);
		sqlQuery.addScalar("parentsCourtOrderedInd", StandardBasicTypes.BOOLEAN);
		sqlQuery.addScalar("cpsAssignedInd", StandardBasicTypes.BOOLEAN);
		sqlQuery.addScalar("familyAssignedInd", StandardBasicTypes.BOOLEAN);
		sqlQuery.addScalar("parentsAssignedInd", StandardBasicTypes.BOOLEAN);
		sqlQuery.addScalar("dateCreated", StandardBasicTypes.DATE);
		sqlQuery.addScalar("dateCompleted", StandardBasicTypes.DATE);
		sqlQuery.addScalar("dateNoLongerNeeded", StandardBasicTypes.DATE);
		sqlQuery.addScalar("dateApproval", StandardBasicTypes.DATE);

		sqlQuery.setParameter("idEvent", eventId);
		sqlQuery.setParameter("idCase", caseId);
		sqlQuery.setParameter("currentIdEvent", currentIdEvent);

		taskGoalValueDtos = sqlQuery.list();
		if (!taskGoalValueDtos.isEmpty()) {
			taskGoalValueDtos = queryTaskGoalLink(taskGoalValueDtos, eventId, caseId);
		}
		return taskGoalValueDtos;
	}

	/**
	 * method name: queryFamilyPlanCost Method description: This method is used
	 * to get the family plan cost for particular family plan.
	 *
	 * @param familyPlanDto
	 *            the family plan dto
	 * @return the family plan dto
	 */
	private FamilyPlanDto queryFamilyPlanCost(FamilyPlanDto familyPlanDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FamilyPlanCost.class);
		criteria.add(Restrictions.eq("capsCase.idCase", familyPlanDto.getIdCase()));
		criteria.add(Restrictions.eq("event.idEvent", familyPlanDto.getFamilyPlanEvent().getIdEvent()));
		FamilyPlanCost familyPlanCost = (FamilyPlanCost) criteria.uniqueResult();
		if (!ObjectUtils.isEmpty(familyPlanCost)) {
			familyPlanDto.setTravelCost(familyPlanCost.getCdAmtTravel());
			familyPlanDto.setChildCareCost(familyPlanCost.getCdAmtChildCare());
			familyPlanDto.setFoodCost(familyPlanCost.getCdAmtFood());
			familyPlanDto.setFacilityCost(familyPlanCost.getCdAmtFacility());
			familyPlanDto.setFgdmTotalCost(familyPlanCost.getAmtFgdmTotal());
		}
		return familyPlanDto;
	}

	/**
	 * Method Name: queryFamilyPlanEvaluations Method Description: Query the
	 * Family Plan evaluations from the database.
	 *
	 * @param familyPlanDto
	 *            the family plan dto
	 * @return FamilyPlanDto @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public FamilyPlanDto queryFamilyPlanEvaluations(FamilyPlanDto familyPlanDto) {

		// Queries the details from the FAMILY_PLAN_EVAL TABLE
		List<FamilyPlanEvalDto> familyPlanEvalItems = new ArrayList<FamilyPlanEvalDto>();
		SQLQuery queryFamilyPlanEvalsql = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(queryFamilyPlanEval).addScalar("idFamilyPlanEvaluation", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idFamilyPlanEvent", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("dtCompleted", StandardBasicTypes.DATE).addScalar("dtNextDue", StandardBasicTypes.DATE)
				.addScalar("nbrFamPlanEvalVersion", StandardBasicTypes.LONG)
				.addScalar("cdFgdmConference", StandardBasicTypes.STRING)
				.setParameter("idCase", familyPlanDto.getIdCase())
				.setParameter("idFamilyPlanEvent", familyPlanDto.getFamilyPlanEvent().getIdEvent())
				.setResultTransformer(Transformers.aliasToBean(FamilyPlanEvalDto.class));
		familyPlanEvalItems = (List<FamilyPlanEvalDto>) queryFamilyPlanEvalsql.list();

		// Setting the Family Plan Evaluation Details from the FAMILY_PLAN_EVAL
		// TABLE
		if (!ObjectUtils.isEmpty(familyPlanEvalItems) && familyPlanEvalItems.size() > ServiceConstants.Zero) {
			familyPlanEvalItems.forEach(familyPlanEval -> {
				familyPlanEval.setCdEventStatus(eventDao.getEventStatus(familyPlanEval.getIdEvent()));
				familyPlanEval.setEvalEvent(eventDao.getEventByid(familyPlanEval.getIdEvent()));
			});
			familyPlanDto.setFamilyPlanEvaluationList(familyPlanEvalItems);
		}
		return familyPlanDto;
	}

	/**
	 * Method Name: queryPrincipalsForStage Method Description: Query all
	 * principals (PRN) associated with this Stage.
	 *
	 * @param mostRecentEvent
	 *            the most recent event
	 * @return List<PrincipalParticipantDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PrincipalParticipantDto> queryPrincipalsForStage(EventDto mostRecentEvent) {

		// Fetching the Last Update Date for the sql query
		/*
		 * SimpleDateFormat dateFormat = new
		 * SimpleDateFormat(ServiceConstants.DDMMMYYYY); String fr =
		 * dateFormat.format(mostRecentEvent.getDtLastUpdate()); Date
		 * dateLastUpdate = null; try { dateLastUpdate =
		 * dateFormat.parse(fr.toUpperCase()); } catch (ParseException e) {
		 * e.printStackTrace(); }
		 */

		// Queries the Principals and Collaterals Information for a Stage
		SQLQuery queryPrincipalsForStagesql = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(queryPrincipalsForStage).addScalar("personId", StandardBasicTypes.LONG)
				.addScalar("firstName", StandardBasicTypes.STRING).addScalar("middleName", StandardBasicTypes.STRING)
				.addScalar("lastName", StandardBasicTypes.STRING).addScalar("age", StandardBasicTypes.INTEGER)
				.addScalar("dateOfBirth", StandardBasicTypes.DATE).addScalar("dateOfDeath", StandardBasicTypes.DATE)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
				.addScalar("personType", StandardBasicTypes.STRING).addScalar("isReporter", StandardBasicTypes.STRING)
				.setParameter("idStage", mostRecentEvent.getIdStage())
				.setParameter("dateLastUpdate", mostRecentEvent.getDtLastUpdate())
				.setResultTransformer(Transformers.aliasToBean(PersonValueDto.class));
		List<PersonValueDto> principalPersonDtlList = (List<PersonValueDto>) queryPrincipalsForStagesql.list();

		// Fetch the Selected Principal List
		List<PersonDto> selectedPrincipalList = querySelectedPrincipals(mostRecentEvent.getIdEvent());

		// Fetch the selected Participant List
		List<PersonDto> selectedParticipantList = querySelectedParticipants(mostRecentEvent.getIdEvent());

		// List to store the Participant/Principal List
		List<PrincipalParticipantDto> principalParticipantList = new ArrayList<PrincipalParticipantDto>();

		// Iterating through the Principal/Collateral List retrieved for the
		// Stage
		for (PersonValueDto personDto : principalPersonDtlList) {

			// Creating the PrincipalParticipantDto to the List
			PrincipalParticipantDto principalParticipantDto = new PrincipalParticipantDto();

			// Setting the PersonName
			String personFullName = ServiceConstants.LT_UNKNOWN;
			if (!TypeConvUtil.isNullOrEmpty(personDto.getLastName())) {
				personFullName = personDto.getLastName();
			}
			if (!TypeConvUtil.isNullOrEmpty(personDto.getFirstName())) {
				personFullName = personFullName + ServiceConstants.COMMA + personDto.getFirstName();
			}
			if (!TypeConvUtil.isNullOrEmpty(personDto.getMiddleName())) {
				personFullName = personFullName + ServiceConstants.SPACE + personDto.getMiddleName().substring(0, 1);
			}
			principalParticipantDto.setTxtPersonName(personFullName);

			// Setting the Relationship
			principalParticipantDto.setCdRelationship(personDto.getCdStagePersRelInt());

			// Updating the Selected Principal Indicator
			for (PersonDto selectedPrincipal : selectedPrincipalList) {
				if (selectedPrincipal.getIdPerson().equals(personDto.getPersonId())) {
					principalParticipantDto.setIndPrincipal(ServiceConstants.Y);
				}
			}

			// Updating the Selected Participant Indicator
			for (PersonDto selectedParticipant : selectedParticipantList) {
				if (selectedParticipant.getIdPerson().equals(personDto.getPersonId())) {
					principalParticipantDto.setIndParticipant(ServiceConstants.Y);
				}
			}
			principalParticipantDto.setIdPerson(personDto.getPersonId());

			Boolean indSelected = Boolean.FALSE;
			Boolean indParticipantSelected = Boolean.FALSE;
			Boolean disableForCollateral = Boolean.FALSE;
			Boolean dontShowCollateral = Boolean.FALSE;

			if (ServiceConstants.PERSON_COLLATERAL.equals(personDto.getPersonType())) {
				indSelected = Boolean.FALSE;
				disableForCollateral = Boolean.TRUE;
				if (ServiceConstants.Y.equals(personDto.getIsReporter())) {
					dontShowCollateral = Boolean.TRUE;
				}
			}
			principalParticipantDto.setIndSelected(indSelected);
			principalParticipantDto.setIndParticipantSelected(indParticipantSelected);
			principalParticipantDto.setDisableForCollateral(disableForCollateral);
			principalParticipantDto.setDontShowCollateral(dontShowCollateral);
			principalParticipantDto.setPersonType(personDto.getPersonType());
			// Adding the PrincipalParticipantDto to the List
			if (!dontShowCollateral)
				principalParticipantList.add(principalParticipantDto);
		}

		return principalParticipantList;
	}

	/**
	 * Method Name: queryInvestigationStageId Method Description: Query the
	 * ID_STAGE of the investigation stage that led to the creation of the given
	 * family plan stage.
	 *
	 * @param idstage
	 *            the idstage
	 * @return Long
	 */
	@Override
	public Long queryInvestigationStageId(Long idstage) {
		Long investigationStageId = ServiceConstants.Zero_Value;
		SQLQuery sql = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(queryInvestigationStageId)
				.addScalar("resultId", StandardBasicTypes.LONG).setParameter("idStage", idstage)
				.setResultTransformer(Transformers.aliasToBean(CommonIdRes.class));
		CommonIdRes commonIdRes = (CommonIdRes) sql.uniqueResult();
		if (!ObjectUtils.isEmpty(commonIdRes)) {
			investigationStageId = commonIdRes.getResultId();
		}
		return investigationStageId;
	}

	/**
	 * Method Name: queryFamilyPlanEventId Method Description: Queries the event
	 * id of the family plan to which the given evaluation belongs.
	 *
	 * @param idEvent
	 *            the id event
	 * @return Long @
	 */
	@Override
	public Long queryFamilyPlanEventId(Long idEvent) {
		SQLQuery sql = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(queryFamilyPlanEventId)
				.addScalar("idFamilyPlanEvent", StandardBasicTypes.LONG).setParameter("eventId", idEvent)
				.setResultTransformer(Transformers.aliasToBean(FamilyPlanDto.class));
		List<FamilyPlanDto> familyPlanEvalDtos = sql.list();

		FamilyPlanDto valueBeanDto = familyPlanEvalDtos.get(ServiceConstants.Zero);
		return valueBeanDto.getIdFamilyPlanEvent();
	}

	/**
	 * Method Name: queryFamilyPlanRole Method Description: Queries the Family
	 * Plan Role for the EventId.
	 *
	 * @param idEvent
	 *            the id event
	 * @return Long @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> queryFamilyPlanRole(Long idEvent) {
		SQLQuery queryFamilyPlanRolesql = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(queryFamilyPlanRole).setParameter("idEvent", idEvent);
		List<String> familyPlanRole = (List<String>) queryFamilyPlanRolesql.list();
		return familyPlanRole;
	}

	/**
	 * Method Name: getFirstParticipant Method Description: get primary
	 * participant id for family plan.
	 *
	 * @param id
	 *            the id
	 * @param isFamilyPlan
	 *            the is family plan
	 * @return PersonValueDto
	 */
	@Override
	public PersonValueDto getFirstParticipant(Long id, Boolean isFamilyPlan) {
		// Get Primary Participant
		PersonValueDto participantDto = null;
		FamilyPlanPartcpnt partcpnt = null;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FamilyPlanPartcpnt.class);
		if (isFamilyPlan) {
			FamilyPlan familyPlan = (FamilyPlan) sessionFactory.getCurrentSession().createCriteria(FamilyPlan.class)
					.add(Restrictions.eq(EVENT_ID, id)).uniqueResult();
			if (ObjectUtils.isEmpty(familyPlan)) {
				familyPlan = new FamilyPlan();
				familyPlan.setIdFamilyPlan(0L);
			}
			criteria.add(Restrictions.eq("familyPlan.idFamilyPlan", familyPlan.getIdFamilyPlan()));
		} else {
			FamilyPlanEval familyPlanEval = (FamilyPlanEval) sessionFactory.getCurrentSession()
					.createCriteria(FamilyPlanEval.class).add(Restrictions.eq("eventByIdEvent.idEvent", id))
					.uniqueResult();
			criteria.add(Restrictions.eq("familyPlanEval.idFamilyPlanEvaluation",
					familyPlanEval.getIdFamilyPlanEvaluation()));
		}
		List<FamilyPlanPartcpnt> partcpntList = criteria.list();
		if (CollectionUtils.isNotEmpty(partcpntList)) {
			if (partcpntList.size() == 1)
				partcpnt = partcpntList.get(0);
			else {
				Optional<FamilyPlanPartcpnt>	optPartcpnt = partcpntList.stream().filter(
							participant -> ServiceConstants.PARENT_PERSON_CHAR.equals(participant.getIndPartcpntType()))
							.findFirst();
				if (optPartcpnt.isPresent()){ // 12089 - Check for existance of record
					partcpnt = optPartcpnt.get();
				}
			}
			if (!ObjectUtils.isEmpty(partcpnt)){
				Long idPartcpntPerson = partcpnt.getIdPerson();
				Person person = (Person) sessionFactory.getCurrentSession().createCriteria(Person.class)
						.add(Restrictions.eq("idPerson", idPartcpntPerson)).uniqueResult();
				participantDto = new PersonValueDto();
				participantDto.setFullName(person.getNmPersonFull());
				participantDto.setNmSuffix(person.getCdPersonSuffix());
				participantDto.setPersonId(person.getIdPerson());
			}
		}
		return participantDto;
	}

	/**
	 * Method Name: querySelectedParticipants Method Description: Queries the
	 * Selected Family Participants for the EventId.
	 *
	 * @param idEvent
	 *            the id event
	 * @return Long @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PersonDto> querySelectedParticipants(Long idEvent) {
		SQLQuery querySelectedParticipantssql = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(querySelectedParticipants).addScalar("idPerson", StandardBasicTypes.LONG)
				.setParameter("idEvent", idEvent).setResultTransformer(Transformers.aliasToBean(PersonDto.class));
		List<PersonDto> querySelectedParticipants = (List<PersonDto>) querySelectedParticipantssql.list();
		return querySelectedParticipants;
	}

	/**
	 * Method Name: querySelectedPrincipals Method Description: Queries the
	 * Selected Family Principals for the EventId.
	 *
	 * @param idEvent
	 *            the id event
	 * @return Long
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PersonDto> querySelectedPrincipals(Long idEvent) {
		SQLQuery querySelectedPrincipalssql = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(querySelectedPrincipals).addScalar("idPerson", StandardBasicTypes.LONG)
				.setParameter("idEvent", idEvent).setResultTransformer(Transformers.aliasToBean(PersonDto.class));
		List<PersonDto> querySelectedPrincipals = (List<PersonDto>) querySelectedPrincipalssql.list();

		return querySelectedPrincipals;
	}

	/**
	 * This method gets the event assessment.
	 */
	@Override
	public EventDto getEventAssessment(Long idEventStage, String Operation) {
		Query queryEventAssessmentsql = (Query) sessionFactory.getCurrentSession().createSQLQuery(queryEventAssessment)
				.addScalar("idEvent", StandardBasicTypes.LONG).addScalar("cdEventStatus", StandardBasicTypes.STRING)
				.addScalar("cdTask", StandardBasicTypes.STRING).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.setParameter("idEventStage", idEventStage).setParameter("cdTask", Operation)
				.setResultTransformer(Transformers.aliasToBean(EventDto.class));
		EventDto event = (EventDto) queryEventAssessmentsql.uniqueResult();
		return event;
	}

	/**
	 * Method Name: saveFamilyPlanActnRsrc Method Description: saves the
	 * FamilyPlanActnRsrc
	 * 
	 * @param FamilyPlanActnRsrcDto
	 * 
	 * @return void
	 * 
	 */
	public void saveFamilyPlanActnRsrc(FamilyPlanActnRsrcDto familyPlanActnRsrcDto) {

		FamilyPlanActnRsrc familyPlanActnRsrc = new FamilyPlanActnRsrc();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FamilyPlanReqrdActn.class);
		criteria.add(Restrictions.eq("idFamilyPlanReqrdActns", familyPlanActnRsrcDto.getIdFamilyPlanReqrdActns()));
		FamilyPlanReqrdActn familyPlanReqrdActn = (FamilyPlanReqrdActn) criteria.uniqueResult();
		familyPlanActnRsrc.setFamilyPlanReqrdActn(familyPlanReqrdActn);
		familyPlanActnRsrc.setAddrRsrcStLn1(familyPlanActnRsrcDto.getAddrRsrcStLn1());
		familyPlanActnRsrc.setAddrRsrcStLn2(familyPlanActnRsrcDto.getAddrRsrcStLn2());
		familyPlanActnRsrc.setAddrRsrcCity(familyPlanActnRsrcDto.getAddrRsrcCity());
		familyPlanActnRsrc.setAddrRsrcZip(familyPlanActnRsrcDto.getAddrRsrcZip());
		familyPlanActnRsrc.setNbrRsrcPhone(familyPlanActnRsrcDto.getNbrRsrcPhone());
		familyPlanActnRsrc.setNbrRsrcPhoneExt(familyPlanActnRsrcDto.getNbrRsrcPhoneExt());
		familyPlanActnRsrc.setNmRsrc(familyPlanActnRsrcDto.getNmRsrc());
		familyPlanActnRsrc.setIdCreatedPerson(familyPlanActnRsrcDto.getIdCreatedPerson());
		// familyPlanActnRsrc.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		familyPlanActnRsrc.setIdLastUpdatePerson(familyPlanActnRsrcDto.getIdLastUpdatePerson());
		sessionFactory.getCurrentSession().save(familyPlanActnRsrc);

	}

	/**
	 * Method Name: saveFamilyPlanNeed Method Description: saves the Family Plan
	 * Needs
	 * 
	 * @param FamilyPlanActnRsrcDto
	 * 
	 * @return void
	 * 
	 */
	public void saveFamilyPlanNeed(FamilyPlanNeedsDto familyPlanNeedsDto) {

		FamilyPlanNeed familyPlanNeed = new FamilyPlanNeed();

		familyPlanNeed.setIndPriorNeeds(familyPlanNeedsDto.getIndPriorNeeds());
		familyPlanNeed.setTxtCurrAssmntNeed(familyPlanNeedsDto.getTxtCurrAssmntNeed());
		familyPlanNeed.setTxtEvalProgress(familyPlanNeedsDto.getTxtEvalProgress());
		familyPlanNeed.setTxtNeedDescription(familyPlanNeedsDto.getTxtNeedDescription());
		familyPlanNeed.setNbrSortOrder(familyPlanNeedsDto.getNbrSortOrder());
		familyPlanNeed.setCdRsnForAdtn(familyPlanNeedsDto.getCdRsnForAdtn());
		familyPlanNeed.setIdPerson(familyPlanNeedsDto.getIdPerson());
		familyPlanNeed.setIdCreatedPerson(familyPlanNeedsDto.getIdCreatedPerson());
		Criteria cpsFsnaDomainLookupcriteria = sessionFactory.getCurrentSession()
				.createCriteria(CpsFsnaDomainLookup.class);
		cpsFsnaDomainLookupcriteria
				.add(Restrictions.eq("idCpsFsnaDomainLookup", familyPlanNeedsDto.getIdCpsFsnaDomainlookup()));
		CpsFsnaDomainLookup cpsFsnaDomainLookup = (CpsFsnaDomainLookup) cpsFsnaDomainLookupcriteria.uniqueResult();
		familyPlanNeed.setCpsFsnaDomainLookup(cpsFsnaDomainLookup);

		Criteria familyPlancriteria = sessionFactory.getCurrentSession().createCriteria(FamilyPlan.class);
		familyPlancriteria.add(Restrictions.eq("idFamilyPlan", familyPlanNeedsDto.getIdFamilyPlan()));
		FamilyPlan familyPlan = (FamilyPlan) familyPlancriteria.uniqueResult();
		familyPlanNeed.setFamilyPlan(familyPlan);

		familyPlanNeed.setIdCreatedPerson(familyPlanNeedsDto.getIdCreatedPerson());
		// familyPlanNeed.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		familyPlanNeed.setIdLastUpdatePerson(familyPlanNeedsDto.getIdLastUpdatePerson());

		sessionFactory.getCurrentSession().save(familyPlanNeed);

	}

	/**
	 * Method Name: saveFamilyPlanReqrdActn Method Description: saves the Family
	 * Plan Required Action.
	 *
	 * @param familyPlanReqrdActnsDto
	 *            the family plan reqrd actns dto
	 * @return void
	 */
	@Override
	public void saveFamilyPlanReqrdActn(FamilyPlanReqrdActnsDto familyPlanReqrdActnsDto) {

		FamilyPlanReqrdActn familyPlanReqrdActn = new FamilyPlanReqrdActn();

		familyPlanReqrdActn.setIndCourtOrdrd(familyPlanReqrdActnsDto.getIndCourtOrdrd());
		familyPlanReqrdActn.setIndMnullyAdded(familyPlanReqrdActnsDto.getIndMnullyAdded());
		familyPlanReqrdActn.setIndRsrcUtlzd(familyPlanReqrdActnsDto.getIndRsrcUtlzd());

		Criteria familyPlanNeedcriteria = sessionFactory.getCurrentSession().createCriteria(FamilyPlanNeed.class);
		familyPlanNeedcriteria
				.add(Restrictions.eq("idFamilyPlanNeeds", familyPlanReqrdActnsDto.getIdFamilyPlanNeeds()));
		FamilyPlanNeed familyPlanNeed = (FamilyPlanNeed) familyPlanNeedcriteria.uniqueResult();
		familyPlanReqrdActn.setFamilyPlanNeed(familyPlanNeed);

		familyPlanReqrdActn.setTxtReqrdActn(familyPlanReqrdActnsDto.getTxtReqrdActn());
		familyPlanReqrdActn.setDtTrgtCmpltn(familyPlanReqrdActnsDto.getDtTrgtCmpltn());
		familyPlanReqrdActn.setCdPriorty(familyPlanReqrdActnsDto.getCdPriorty());
		familyPlanReqrdActn.setIdCreatedPerson(familyPlanReqrdActnsDto.getIdCreatedPerson());
		// familyPlanReqrdActn.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
		familyPlanReqrdActn.setIdLastUpdatePerson(familyPlanReqrdActnsDto.getIdLastUpdatePerson());

		sessionFactory.getCurrentSession().save(familyPlanReqrdActn);

	}

	/**
	 * Method Name: saveFamilyPlan Method Description: saves the Family Plan.
	 *
	 * @param familyPlanDtlEvalReq
	 *            the family plan dtl eval req
	 * @return idFamilyPlanDtlEval - Id of the respective Family Plan or
	 *         Evaluation saved/updated
	 */

	@Override
	public Long saveFamilyPlan(FamilyPlanDtlEvalReq familyPlanDtlEvalReq) {
		Long idFamilyPlanDtlEval = null;
		Long idUser = familyPlanDtlEvalReq.getIdUser();
		if (familyPlanDtlEvalReq.getFamilyPlanDtlInd().equals(ServiceConstants.TRUE_VALUE)) {

			FamilyPlan familyPlan = new FamilyPlan();
			BeanUtils.copyProperties(familyPlanDtlEvalReq.getFamilyPlanDtlEvalDto(), familyPlan);
			familyPlan.setNbrFamPlanVersion(FAMILY_PLAN_VERSION);
			Criteria eventcriteria = sessionFactory.getCurrentSession().createCriteria(Event.class);
			eventcriteria.add(Restrictions.eq("idEvent", familyPlanDtlEvalReq.getIdFamilyPlanEvent()));
			Event event = (Event) eventcriteria.uniqueResult();
			familyPlan.setEvent(event);

			Criteria capsCasecriteria = sessionFactory.getCurrentSession().createCriteria(CapsCase.class);
			capsCasecriteria.add(Restrictions.eq("idCase", familyPlanDtlEvalReq.getIdCase()));
			CapsCase capsCase = (CapsCase) capsCasecriteria.uniqueResult();
			familyPlan.setCapsCase(capsCase);

			CpsFsna cpsFsna = (CpsFsna) sessionFactory.getCurrentSession().get(CpsFsna.class,
					familyPlanDtlEvalReq.getFamilyPlanDtlEvalDto().getIdCpsFsna());
			familyPlan.setCpsFsna(cpsFsna);

			List<FamilyPlanPartcpntDto> familyPlanPartcpntDtoList = familyPlanDtlEvalReq.getFamilyPlanPartcpnts();
			List<FamilyPlanPartcpnt> familyPlanPartcpntList = new ArrayList<FamilyPlanPartcpnt>();
			List<FamilyPlanNeed> familyPlanNeedsList = new ArrayList<FamilyPlanNeed>();

			prepareDataForSave(familyPlanPartcpntDtoList, familyPlanPartcpntList, familyPlanNeedsList, familyPlan, null,
					idUser, event.getIdEvent(), capsCase.getIdCase());
			familyPlan.setFamilyPlanNeeds(familyPlanNeedsList);
			familyPlan.setFamilyPlanPartcpnts(familyPlanPartcpntList);

			sessionFactory.getCurrentSession().save(familyPlan);
			idFamilyPlanDtlEval = familyPlan.getIdFamilyPlan();

		} else {
			FamilyPlanEval familyPlanEval = new FamilyPlanEval();
			BeanUtils.copyProperties(familyPlanDtlEvalReq.getFamilyPlanDtlEvalDto(), familyPlanEval);
			// familyPlanEval.setDtLastUpdate(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
			familyPlanEval.setNbrFamPlanEvalVersion(FAMILY_PLAN_VERSION);
			Event eventByIdEvent = (Event) sessionFactory.getCurrentSession().get(Event.class,
					familyPlanDtlEvalReq.getIdFamilyPlanEvalEvent());
			familyPlanEval.setEventByIdEvent(eventByIdEvent);

			Event eventByIdFamilyPlanEvent = (Event) sessionFactory.getCurrentSession().get(Event.class,
					familyPlanDtlEvalReq.getIdFamilyPlanEvent());
			familyPlanEval.setEventByIdFamilyPlanEvent(eventByIdFamilyPlanEvent);

			CapsCase capsCase = (CapsCase) sessionFactory.getCurrentSession().get(CapsCase.class,
					familyPlanDtlEvalReq.getIdCase());
			familyPlanEval.setCapsCase(capsCase);

			CpsFsna cpsFsna = (CpsFsna) sessionFactory.getCurrentSession().get(CpsFsna.class,
					familyPlanDtlEvalReq.getFamilyPlanDtlEvalDto().getIdCpsFsna());
			familyPlanEval.setCpsFsna(cpsFsna);

			List<FamilyPlanPartcpntDto> familyPlanPartcpntDtoList = familyPlanDtlEvalReq.getFamilyPlanPartcpnts();
			List<FamilyPlanPartcpnt> familyPlanPartcpntList = new ArrayList<FamilyPlanPartcpnt>();
			List<FamilyPlanNeed> familyPlanNeedsList = new ArrayList<FamilyPlanNeed>();

			prepareDataForSave(familyPlanPartcpntDtoList, familyPlanPartcpntList, familyPlanNeedsList, null,
					familyPlanEval, idUser, eventByIdEvent.getIdEvent(), capsCase.getIdCase());
			familyPlanEval.setFamilyPlanNeeds(familyPlanNeedsList);
			familyPlanEval.setFamilyPlanPartcpnts(familyPlanPartcpntList);

			sessionFactory.getCurrentSession().save(familyPlanEval);
			idFamilyPlanDtlEval = familyPlanEval.getIdFamilyPlanEvaluation();
		}

		return idFamilyPlanDtlEval;
	}

	/**
	 * Method Name: prepareDataForSave Method Description: This is a common
	 * method for populating the FP participant, Needs, required actions and
	 * action resources for both Family Plan Detail and Evaluation insertions.
	 *
	 * @param familyPlanPartcpntDtoList
	 *            the family plan partcpnt dto list
	 * @param familyPlanPartcpntList
	 *            the family plan partcpnt list
	 * @param familyPlanNeedsList
	 *            the family plan needs list
	 * @param familyPlan
	 *            the family plan
	 * @param familyPlanEval
	 *            the family plan eval
	 * @param idUser
	 *            the id user
	 */
	private void prepareDataForSave(List<FamilyPlanPartcpntDto> familyPlanPartcpntDtoList,
			List<FamilyPlanPartcpnt> familyPlanPartcpntList, List<FamilyPlanNeed> familyPlanNeedsList,
			FamilyPlan familyPlan, FamilyPlanEval familyPlanEval, Long idUser, Long idEvent, Long idCase) {
		List<FamilyPlanActnRsrc> familyPlanActnRsrcList = new ArrayList<FamilyPlanActnRsrc>();

		if (CollectionUtils.isNotEmpty(familyPlanPartcpntDtoList)) {
			for (FamilyPlanPartcpntDto familyPlanPartcpntDto : familyPlanPartcpntDtoList) {
				FamilyPlanPartcpnt familyPlanPartcpnt = new FamilyPlanPartcpnt();
				BeanUtils.copyProperties(familyPlanPartcpntDto, familyPlanPartcpnt);
				familyPlanPartcpnt.setIdCreatedPerson(idUser);
				familyPlanPartcpnt.setIdLastUpdatePerson(idUser);
				if (!ObjectUtils.isEmpty(familyPlan)) {
					familyPlanPartcpnt.setFamilyPlan(familyPlan);
				} else {
					familyPlanPartcpnt.setFamilyPlanEval(familyPlanEval);
				}
				familyPlanPartcpntList.add(familyPlanPartcpnt);

				if (familyPlanPartcpnt.getIndPartcpntType().equals(ServiceConstants.CHILD_PARTICIPANT)
						|| familyPlanPartcpnt.getIndPartcpntType().equals(ServiceConstants.PRIMARY_PARTICIPANT)) {
					addEventPersonLinkOnPlan(familyPlanPartcpntDto.getIdPerson(), idEvent, idCase, ServiceConstants.Y,
							ServiceConstants.N);
				} else {
					addEventPersonLinkOnPlan(familyPlanPartcpntDto.getIdPerson(), idEvent, idCase, ServiceConstants.N,
							ServiceConstants.Y);
				}

				List<FamilyPlanNeedsDto> familyPlanNeedsDtoList = familyPlanPartcpntDto.getFamilyPlanNeedsDtoList();
				if (CollectionUtils.isNotEmpty(familyPlanNeedsDtoList)) {

					for (FamilyPlanNeedsDto familyPlanNeedsDto : familyPlanNeedsDtoList) {
						FamilyPlanNeed familyPlanNeed = new FamilyPlanNeed();
						BeanUtils.copyProperties(familyPlanNeedsDto, familyPlanNeed);
						if (familyPlanNeed.getFamilyPlanReqrdActns() == null)
							familyPlanNeed.setFamilyPlanReqrdActns(new ArrayList<>());
						familyPlanNeed.setIdCreatedPerson(idUser);
						familyPlanNeed.setIdLastUpdatePerson(idUser);
						CpsFsnaDomainLookup cpsFsnaDomainLookup = (CpsFsnaDomainLookup) sessionFactory
								.getCurrentSession()
								.get(CpsFsnaDomainLookup.class, familyPlanNeedsDto.getIdCpsFsnaDomainlookup());
						familyPlanNeed.setCpsFsnaDomainLookup(cpsFsnaDomainLookup);
						if (!ObjectUtils.isEmpty(familyPlan)) {
							familyPlanNeed.setFamilyPlan(familyPlan);
						} else {
							familyPlanNeed.setFamilyPlanEval(familyPlanEval);
						}
						familyPlanNeed.setIdPerson(familyPlanPartcpntDto.getIdPerson());
						// familyPlanNeed.setDtLastUpdate(
						// Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
						// familyPlanNeed.setDtCreated(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
						List<FamilyPlanReqrdActnsDto> familyPlanReqrdActnsDtoList = familyPlanNeedsDto
								.getFamilyPlanReqrdActnsDtoList();

						if (CollectionUtils.isNotEmpty(familyPlanReqrdActnsDtoList)) {

							for (FamilyPlanReqrdActnsDto familyPlanReqrdActnsDto : familyPlanReqrdActnsDtoList) {

								FamilyPlanReqrdActn familyPlanReqrdActn = new FamilyPlanReqrdActn();
								BeanUtils.copyProperties(familyPlanReqrdActnsDto, familyPlanReqrdActn);
								familyPlanReqrdActn.setIdCreatedPerson(idUser);
								familyPlanReqrdActn.setIdLastUpdatePerson(idUser);
								familyPlanReqrdActn.setFamilyPlanNeed(familyPlanNeed);
								if(CollectionUtils.isNotEmpty(familyPlanReqrdActnsDto.getTffEvidenceBasedServices())){
									familyPlanReqrdActn.setTffevidenceBasedSvcs(familyPlanReqrdActnsDto.getTffEvidenceBasedServices().stream().map(String::valueOf).collect(Collectors.joining(",")));
								}
								if (familyPlanReqrdActn.getFamilyPlanActnRsrcs() == null)
									familyPlanReqrdActn.setFamilyPlanActnRsrcs(new ArrayList<>());
								// familyPlanReqrdActn.setDtLastUpdate(
								// Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
								//

								List<FamilyPlanActnRsrcDto> familyPlanActnRsrcDtoList = familyPlanReqrdActnsDto
										.getFamilyPlanActnRsrcDtoList();

								if (CollectionUtils.isNotEmpty(familyPlanActnRsrcDtoList)) {
									for (FamilyPlanActnRsrcDto familyPlanActnRsrcDto : familyPlanActnRsrcDtoList) {

										FamilyPlanActnRsrc familyPlanActnRsrc = new FamilyPlanActnRsrc();
										BeanUtils.copyProperties(familyPlanActnRsrcDto, familyPlanActnRsrc);
										familyPlanActnRsrc.setIdCreatedPerson(idUser);
										familyPlanActnRsrc.setIdLastUpdatePerson(idUser);
										familyPlanActnRsrcList.add(familyPlanActnRsrc);
										familyPlanActnRsrc.setFamilyPlanReqrdActn(familyPlanReqrdActn);
										familyPlanReqrdActn.getFamilyPlanActnRsrcs().add(familyPlanActnRsrc);
									}
								}
								familyPlanNeed.getFamilyPlanReqrdActns().add(familyPlanReqrdActn);

							}

						}
						familyPlanNeedsList.add(familyPlanNeed);

					}

				}

			}

		}

	}

	/**
	 * Method Name: updateFamilyPlan Method Description: updates the Family
	 * Plan.
	 *
	 * @param familyPlanDtlEvalReq
	 *            the family plan dtl eval req
	 * @return idFamilyPlanDtlEval - Id of the respective Family Plan or
	 *         Evaluation saved/updated
	 */
	@Override
	public Long updateFamilyPlan(FamilyPlanDtlEvalReq familyPlanDtlEvalReq) {

		Long idFamilyPlanDtlEval = null;
		Long idUser = familyPlanDtlEvalReq.getIdUser();

		// Fetching the Family Plan Participants from the Input
		List<FamilyPlanPartcpntDto> familyPlanPartcpntDtoList = familyPlanDtlEvalReq.getFamilyPlanPartcpnts();
		List<FamilyPlanPartcpnt> familyPlanPartcpntList = new ArrayList<FamilyPlanPartcpnt>();
		List<FamilyPlanNeed> familyPlanNeedsList = new ArrayList<FamilyPlanNeed>();

		if (familyPlanDtlEvalReq.getFamilyPlanDtlInd().equals(ServiceConstants.TRUE_VALUE)) {

			// Fetching the FamilyPlan saved in the Database
			FamilyPlan familyPlan = (FamilyPlan) sessionFactory.getCurrentSession().get(FamilyPlan.class,
					familyPlanDtlEvalReq.getFamilyPlanDtlEvalDto().getIdFamilyPlan());
			BeanUtils.copyProperties(familyPlanDtlEvalReq.getFamilyPlanDtlEvalDto(), familyPlan);
			familyPlan.setNbrFamPlanVersion(FAMILY_PLAN_VERSION);
			// Fetching the Family Plan Participants from the Database
			familyPlanPartcpntList = familyPlan.getFamilyPlanPartcpnts();
			// Retrieving the Family Plan Needs Saved in the Database
			familyPlanNeedsList = familyPlan.getFamilyPlanNeeds();

			prepareDataForUpdate(familyPlanPartcpntDtoList, familyPlanPartcpntList, familyPlanNeedsList, familyPlan,
					null, idUser, familyPlan.getEvent().getIdEvent(), familyPlan.getCapsCase().getIdCase());

			// Merge the updated FamilyPlan
			sessionFactory.getCurrentSession().merge(familyPlan);
			idFamilyPlanDtlEval = familyPlan.getIdFamilyPlan();

		} else {

			// Fetching the FamilyPlan Eval saved in the Database
			FamilyPlanEval familyPlanEval = (FamilyPlanEval) sessionFactory.getCurrentSession().get(
					FamilyPlanEval.class, familyPlanDtlEvalReq.getFamilyPlanDtlEvalDto().getIdFamilyPlanEvaluation());
			BeanUtils.copyProperties(familyPlanDtlEvalReq.getFamilyPlanDtlEvalDto(), familyPlanEval);
			familyPlanEval.setNbrFamPlanEvalVersion(FAMILY_PLAN_VERSION);

			// Fetching the Family Plan Participants from the Database
			familyPlanPartcpntList = familyPlanEval.getFamilyPlanPartcpnts();
			// Retrieving the Family Plan Needs Saved in the Database
			familyPlanNeedsList = familyPlanEval.getFamilyPlanNeeds();

			prepareDataForUpdate(familyPlanPartcpntDtoList, familyPlanPartcpntList, familyPlanNeedsList, null,
					familyPlanEval, idUser, familyPlanEval.getEventByIdEvent().getIdEvent(),
					familyPlanEval.getCapsCase().getIdCase());

			// merge the Family Plan Evaluation
			sessionFactory.getCurrentSession().merge(familyPlanEval);
			idFamilyPlanDtlEval = familyPlanEval.getIdFamilyPlanEvaluation();

		}
		return idFamilyPlanDtlEval;
	}

	/**
	 * Method Name: prepareDataForUpdate Method Description: This is a common
	 * method for populating the FP participant, Needs, required actions and
	 * action resources updates, deletions and new insertions for both Family
	 * Plan Detail and Evaluation.
	 *
	 * @param familyPlanPartcpntDtoList
	 *            the family plan partcpnt dto list
	 * @param familyPlanPartcpntList
	 *            the family plan partcpnt list
	 * @param familyPlanNeedsList
	 *            the family plan needs list
	 * @param familyPlan
	 *            the family plan
	 * @param familyPlanEval
	 *            the family plan eval
	 * @param idUser
	 *            the id user
	 */
	private void prepareDataForUpdate(List<FamilyPlanPartcpntDto> familyPlanPartcpntDtoList,
			List<FamilyPlanPartcpnt> familyPlanPartcpntList, List<FamilyPlanNeed> familyPlanNeedsList,
			FamilyPlan familyPlan, FamilyPlanEval familyPlanEval, Long idUser, Long idEvent, Long idCase) {
		// Initialize the Lists
		// Family Plan Participant Add List
		List<FamilyPlanPartcpnt> familyPlanPartcpntAddList = new ArrayList<FamilyPlanPartcpnt>();
		// Family Plan Needs Add List
		List<FamilyPlanNeed> familyPlanNeedAddList = new ArrayList<FamilyPlanNeed>();
		// Delete Family Plan Need List
		List<FamilyPlanNeed> familyPlanNeedDeleteList = new ArrayList<FamilyPlanNeed>();
		// Family Plan Participant Delete List
		List<FamilyPlanPartcpnt> familyPlanPartcpntDeleteList = new ArrayList<FamilyPlanPartcpnt>();
		if (!ObjectUtils.isEmpty(familyPlanPartcpntDtoList)) {
			for (FamilyPlanPartcpntDto familyPlanPartcpntDto : familyPlanPartcpntDtoList) {
				// Add New Participant or Update Existing Participant
				EventPersonLink eventPersonLink = (EventPersonLink) sessionFactory.getCurrentSession()
						.createCriteria(EventPersonLink.class).add(Restrictions.eq("event.idEvent", idEvent))
						.add(Restrictions.eq("person.idPerson", familyPlanPartcpntDto.getIdPerson())).uniqueResult();
				if (ObjectUtils.isEmpty(eventPersonLink)) {
					// Newly added participant
					if (familyPlanPartcpntDto.getIndPartcpntType().equals(ServiceConstants.CHILD_PARTICIPANT)
							|| familyPlanPartcpntDto.getIndPartcpntType()
									.equals(ServiceConstants.PRIMARY_PARTICIPANT)) {
						// For Principle participant
						addEventPersonLinkOnPlan(familyPlanPartcpntDto.getIdPerson(), idEvent, idCase,
								ServiceConstants.Y, ServiceConstants.N);
					} else {
						// For additional participant
						addEventPersonLinkOnPlan(familyPlanPartcpntDto.getIdPerson(), idEvent, idCase,
								ServiceConstants.N, ServiceConstants.Y);
					}
				} else {
					// Update existing record
					if (familyPlanPartcpntDto.getIndPartcpntType().equals(ServiceConstants.CHILD_PARTICIPANT)
							|| familyPlanPartcpntDto.getIndPartcpntType()
									.equals(ServiceConstants.PRIMARY_PARTICIPANT)) {
						saveParticpantsOnPlan(eventPersonLink, ServiceConstants.Y, ServiceConstants.N);
					} else
						saveParticpantsOnPlan(eventPersonLink, ServiceConstants.N, ServiceConstants.Y);
				}
				updateFamilyPlanPartcpnt(familyPlanPartcpntDto, familyPlanPartcpntAddList, familyPlanPartcpntList,
						familyPlan, familyPlanEval, idUser);

				// Retrieving the FamilyPlanNeedsDtoList from the Input Request
				List<FamilyPlanNeedsDto> familyPlanNeedsDtoList = familyPlanPartcpntDto.getFamilyPlanNeedsDtoList();
				if (!ObjectUtils.isEmpty(familyPlanNeedsDtoList)) {

					for (FamilyPlanNeedsDto familyPlanNeedsDto : familyPlanNeedsDtoList) {
						// Add New Family Plan Needs or Update Family Plan Needs
						updateFamilyPlanNeed(familyPlanNeedsDto, familyPlanNeedAddList, familyPlanNeedsList, familyPlan,
								familyPlanEval, idUser);

					}
				}
				// Populating the Family Plan Needs to be deleted
				if (!ObjectUtils.isEmpty(familyPlanNeedsList)) {
					for (FamilyPlanNeed familyPlanNeed : familyPlanNeedsList) {
						if (!ObjectUtils.isEmpty(familyPlanNeedsDtoList)) {
							if (familyPlanNeed.getIdPerson().equals(familyPlanPartcpntDto.getIdPerson())) {
								// Populate the Family Plan Needs Delete List
								familyPlanNeedDeleteList = populateFamilyPlanNeedsDeleteList(familyPlanNeedsDtoList,
										familyPlanNeed, familyPlanNeedDeleteList);
							}

						}

						// If the Input FamilyPlanNeedDto is empty and Family
						// Plan Need exists in the
						// Database
						// else if (!ObjectUtils.isEmpty(familyPlanNeed)) {
						else {
							// Populate the Family Plan Needs Delete List
							if (familyPlanNeed.getIdPerson().equals(familyPlanPartcpntDto.getIdPerson())) {
								familyPlanNeedDeleteList.add(familyPlanNeed);
							}

						}

					}

				}

			}
		}

		// Iterating the Family Plan Participants from the Database to check
		// whether
		// they match the
		// Family Plan participants in the Input to populate the Delete List
		if (!ObjectUtils.isEmpty(familyPlanPartcpntList)) {

			// Populating the Family Plan Participants Delete List
			familyPlanPartcpntDeleteList = populateFamilyPlanPartcpntDeleteList(familyPlanPartcpntDtoList,
					familyPlanPartcpntList);
			List<Long> prtcpIds = new ArrayList<>();
			familyPlanPartcpntDeleteList.forEach(prtcp -> {
				prtcpIds.add(prtcp.getIdPerson());
				// Check if the deleted Participant type is same as the value in
				// event_person_link table
				EventPersonLink eventPersonLink = (EventPersonLink) sessionFactory.getCurrentSession()
						.createCriteria(EventPersonLink.class).add(Restrictions.eq("event.idEvent", idEvent))
						.add(Restrictions.eq("person.idPerson", prtcp.getIdPerson())).uniqueResult();
				if (!ObjectUtils.isEmpty(eventPersonLink)) {
					if (((prtcp.getIndPartcpntType().equals(ServiceConstants.CHILD_PARTICIPANT)
							|| prtcp.getIndPartcpntType().equals(ServiceConstants.PRIMARY_PARTICIPANT))
							&& ServiceConstants.Y.equals(eventPersonLink.getIndFamPlanPrincipal()))
							|| (ServiceConstants.ADDITIONAL_PARTICIPANT.equals(prtcp.getIndPartcpntType())
									&& ServiceConstants.Y.equals(eventPersonLink.getIndFamPlanPart()))) {
						deleteParticpantsOnPlan(eventPersonLink);
					}
				}
			});
			for (FamilyPlanNeed familyPlanNeed : familyPlanNeedsList) {
				if (prtcpIds.contains(familyPlanNeed.getIdPerson())) {
					familyPlanNeedDeleteList.add(familyPlanNeed);
				}
			}
		}

		// delete family plan participants
		familyPlanPartcpntDeleteList.forEach(partcpnt -> familyPlanPartcpntList.remove(partcpnt));
		// add family plan participants
		familyPlanPartcpntAddList.forEach(partcpnt -> familyPlanPartcpntList.add(partcpnt));
		// delete family plan needs
		familyPlanNeedDeleteList.forEach(planneed -> familyPlanNeedsList.remove(planneed));
		// add family plan needs
		familyPlanNeedAddList.forEach(planneed -> familyPlanNeedsList.add(planneed));
	}

	/**
	 * Method Name: queryPrincipalsForEvent Method Description: Query all
	 * principals (PRN) associated with this event.
	 *
	 * @param mostRecentEvent
	 *            the most recent event
	 * @return List<PersonValueDto>
	 * @throws HibernateException
	 *             the hibernate exception
	 * @throws DataNotFoundException
	 *             the data not found exception
	 */

	@Override
	public List<PersonValueDto> queryPrincipalsForEvent(EventDto mostRecentEvent) {
		List<PersonValueDto> principalsForEvent = new ArrayList<PersonValueDto>();
		SQLQuery sql = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(queryPrincipalsForEvent)
				.addScalar("personId", StandardBasicTypes.LONG).addScalar("firstName", StandardBasicTypes.STRING)
				.addScalar("middleName", StandardBasicTypes.STRING).addScalar("lastName", StandardBasicTypes.STRING)
				.addScalar("ageLong", StandardBasicTypes.LONG).addScalar("dateOfBirth", StandardBasicTypes.DATE)
				.addScalar("dateOfDeath", StandardBasicTypes.DATE).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
				.addScalar("permanencyGoalCode", StandardBasicTypes.STRING)
				.addScalar("permanencyGoalTargetDate", StandardBasicTypes.DATE)
				.addScalar("eventPersonLinkDateLastUpdate", StandardBasicTypes.TIMESTAMP)
				.setParameter("idStage", mostRecentEvent.getIdStage())
				.setParameter("idEvent", mostRecentEvent.getIdEvent()).setParameter("prn", ServiceConstants.PRINCIPAL)
				.setParameter("dateLastUpdate", mostRecentEvent.getDtLastUpdate())
				.setParameter("servicey", ServiceConstants.STRING_IND_Y)
				.setResultTransformer(Transformers.aliasToBean(PersonValueDto.class));
		List<PersonValueDto> personValueDtoList = sql.list();
		if (!TypeConvUtil.isNullOrEmpty(personValueDtoList)) {
			for (PersonValueDto nameStagePersonEventDto : personValueDtoList) {
				String personFullName = ServiceConstants.LT_UNKNOWN;
				PersonValueDto personBean = new PersonValueDto();
				personBean.setLastName(nameStagePersonEventDto.getLastName());
				personFullName = nameStagePersonEventDto.getNmLast();
				personBean.setFirstName(nameStagePersonEventDto.getFirstName());
				personFullName = personFullName + ServiceConstants.COMMA + nameStagePersonEventDto.getNmFirst();
				personBean.setMiddleName(nameStagePersonEventDto.getMiddleName());
				personBean.setAge(nameStagePersonEventDto.getAge());
				personBean.setPersonId(nameStagePersonEventDto.getPersonId());
				personBean.setDateOfBirth(nameStagePersonEventDto.getDateOfBirth());
				personBean.setDateOfDeath(nameStagePersonEventDto.getDateOfDeath());
				personBean.setPersonTableDateLastUpdate(nameStagePersonEventDto.getPersonTableDateLastUpdate());
				personBean.setRelationshipInterestCode(nameStagePersonEventDto.getCdStagePersRelInt());
				personBean.setEventPersonLinkDateLastUpdate(nameStagePersonEventDto.getEventPersonLinkDateLastUpdate());
				personBean.setPermanencyGoalCode(nameStagePersonEventDto.getPermanencyGoalCode());
				personBean.setPermanencyGoalTargetDate(nameStagePersonEventDto.getPermanencyGoalTargetDate());
				principalsForEvent.add(personBean);
			}
		}

		return principalsForEvent;
	}

	/**
	 * Method Name: queryPrincipalsSelectedForEvent Method Description: Query
	 * all principals (PRN) associated with this event for which
	 * IND_FAM_PLAN_PART is Y.
	 *
	 * @param mostRecentEvent
	 *            the most recent event
	 * @return List<PersonValueDto>
	 */
	@Override
	public List<PersonValueDto> queryPrincipalsSelectedForEvent(EventDto mostRecentEvent) {
		List<PersonValueDto> principalsSelectedForEvent = new ArrayList<PersonValueDto>();
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(queryPrincipalsSelectedForEvent).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("nmPersonFirst", StandardBasicTypes.STRING)
				.addScalar("nmPersonMiddle", StandardBasicTypes.STRING)
				.addScalar("nmPersonLast", StandardBasicTypes.STRING)
				.addScalar("nbrPersonAge", StandardBasicTypes.SHORT).addScalar("dtPersonBirth", StandardBasicTypes.DATE)
				.addScalar("dtPersonDeath", StandardBasicTypes.DATE).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
				.addScalar("indStagePersInLaw", StandardBasicTypes.STRING)
				.addScalar("tsSysTsLastUpdate2", StandardBasicTypes.DATE)
				.addScalar("tsSysTsLastUpdate3", StandardBasicTypes.TIMESTAMP)
				.setParameter("idStage", mostRecentEvent.getIdStage())
				.setParameter("idEvent", mostRecentEvent.getIdEvent()).setParameter("prn", ServiceConstants.PRINCIPAL)
				.setParameter("servicey", ServiceConstants.YES)
				.setParameter("dateLastUpdate", mostRecentEvent.getDtLastUpdate())
				.setResultTransformer(Transformers.aliasToBean(PersonDto.class));
		List<PersonDto> personDtoList = (List<PersonDto>) sqlQuery.list();
		if (!TypeConvUtil.isNullOrEmpty(personDtoList)) {
			for (PersonDto personDto : personDtoList) {
				String personFullName = ServiceConstants.LT_UNKNOWN;
				PersonValueDto personValueDto = new PersonValueDto();
				personValueDto.setLastName(personDto.getNmPersonLast());
				personFullName = personDto.getNmPersonLast();
				personValueDto.setFirstName(personDto.getNmPersonFirst());
				personFullName = personFullName + ServiceConstants.COMMA + personDto.getNmPersonFirst();
				personValueDto.setMiddleName(personDto.getNmPersonMiddle());
				personValueDto.setPersonId(personDto.getIdPerson());
				personValueDto.setDateOfBirth(personDto.getDtPersonBirth());
				personValueDto.setDateOfDeath(personDto.getDtPersonDeath());
				personValueDto.setPersonTableDateLastUpdate(personDto.getDtLastUpdate());
				personValueDto.setRelationshipInterestCode(personDto.getCdStagePersRelInt());
				personValueDto.setEventPersonLinkDateLastUpdate(personDto.getTsSysTsLastUpdate3());
				personValueDto.setPermanencyGoalCode(personDto.getIndStagePersInLaw());
				personValueDto.setPermanencyGoalTargetDate(personDto.getTsSysTsLastUpdate2());
				principalsSelectedForEvent.add(personValueDto);
			}
		}
		return principalsSelectedForEvent;
	}

	/**
	 * Method Name: updateFamilyPlanPartcpnt Method Description:This method is
	 * used to add or update the Family Plan Participant.
	 *
	 * @param familyPlanPartcpntDto
	 *            the family plan partcpnt dto
	 * @param familyPlanPartcpntAddList
	 *            the family plan partcpnt add list
	 * @param familyPlanPartcpntList
	 *            the family plan partcpnt list
	 * @param familyPlan
	 *            the family plan
	 * @param familyPlanEval
	 *            the family plan eval
	 * @param idUser
	 *            the id user
	 * @return void
	 */

	public void updateFamilyPlanPartcpnt(FamilyPlanPartcpntDto familyPlanPartcpntDto,
			List<FamilyPlanPartcpnt> familyPlanPartcpntAddList, List<FamilyPlanPartcpnt> familyPlanPartcpntList,
			FamilyPlan familyPlan, FamilyPlanEval familyPlanEval, Long idUser) {

		if (TypeConvUtil.isNullOrEmpty(familyPlanPartcpntDto.getIdFamilyPlanPartcpnt())) {
			FamilyPlanPartcpnt familyPlanPartcpnt = new FamilyPlanPartcpnt();
			BeanUtils.copyProperties(familyPlanPartcpntDto, familyPlanPartcpnt);
			familyPlanPartcpnt.setIdCreatedPerson(idUser);
			familyPlanPartcpnt.setIdLastUpdatePerson(idUser);
			if (!ObjectUtils.isEmpty(familyPlan)) {
				familyPlanPartcpnt.setFamilyPlan(familyPlan);
			}
			if (!ObjectUtils.isEmpty(familyPlanEval)) {
				familyPlanPartcpnt.setFamilyPlanEval(familyPlanEval);
			}
			familyPlanPartcpntAddList.add(familyPlanPartcpnt);
		} else
		// update existing participants
		{
			Optional<FamilyPlanPartcpnt> optFamilyPlanPartcpnt = familyPlanPartcpntList.stream()
					.filter(partcpnt -> partcpnt.getIdFamilyPlanPartcpnt().longValue() == familyPlanPartcpntDto
							.getIdFamilyPlanPartcpnt().longValue())
					.findAny();
			if (optFamilyPlanPartcpnt.isPresent()) {
				FamilyPlanPartcpnt familyPlanPartcpnt = optFamilyPlanPartcpnt.get();
				BeanUtils.copyProperties(familyPlanPartcpntDto, familyPlanPartcpnt);
				familyPlanPartcpnt.setIdLastUpdatePerson(idUser);
			}
		}

	}

	/**
	 * Method Name: updateFamilyPlanNeed Method Description:This method is used
	 * to add or update Family Plan Needs.
	 *
	 * @param familyPlanNeedsDto
	 *            the family plan needs dto
	 * @param familyPlanNeedAddList
	 *            the family plan need add list
	 * @param familyPlanNeedsList
	 *            the family plan needs list
	 * @param familyPlan
	 *            the family plan
	 * @param familyPlanEval
	 *            the family plan eval
	 * @param idUser
	 *            the id user
	 * @return void
	 */

	public void updateFamilyPlanNeed(FamilyPlanNeedsDto familyPlanNeedsDto, List<FamilyPlanNeed> familyPlanNeedAddList,
			List<FamilyPlanNeed> familyPlanNeedsList, FamilyPlan familyPlan, FamilyPlanEval familyPlanEval,
			Long idUser) {

		FamilyPlanNeed familyPlanNeed = new FamilyPlanNeed();

		// add new Family Plan Needs
		if (TypeConvUtil.isNullOrEmpty(familyPlanNeedsDto.getIdFamilyPlanNeeds())) {
			// familyPlanNeed = new FamilyPlanNeed();
			BeanUtils.copyProperties(familyPlanNeedsDto, familyPlanNeed);
			familyPlanNeed.setIdCreatedPerson(idUser);
			familyPlanNeed.setIdLastUpdatePerson(idUser);
			CpsFsnaDomainLookup cpsFsnaDomainLookup = (CpsFsnaDomainLookup) sessionFactory.getCurrentSession()
					.get(CpsFsnaDomainLookup.class, familyPlanNeedsDto.getIdCpsFsnaDomainlookup());
			familyPlanNeed.setCpsFsnaDomainLookup(cpsFsnaDomainLookup);
			if (!ObjectUtils.isEmpty(familyPlan)) {
				familyPlanNeed.setFamilyPlan(familyPlan);
			}
			if (!ObjectUtils.isEmpty(familyPlanEval)) {
				familyPlanNeed.setFamilyPlanEval(familyPlanEval);
			}
			familyPlanNeedAddList.add(familyPlanNeed);
		}
		// update existing Family Plan Need
		else {
			Optional<FamilyPlanNeed> optFamilyPlanNeed = familyPlanNeedsList.stream().filter(planneed -> planneed
					.getIdFamilyPlanNeeds() == familyPlanNeedsDto.getIdFamilyPlanNeeds().longValue()).findAny();
			if (optFamilyPlanNeed.isPresent()) {
				familyPlanNeed = optFamilyPlanNeed.get();
				BeanUtils.copyProperties(familyPlanNeedsDto, familyPlanNeed);
				familyPlanNeed.setIdLastUpdatePerson(idUser);
			}
		}

		// Retrieve the Family Plan Required Action from the Input Request
		List<FamilyPlanReqrdActnsDto> familyPlanReqrdActnsDtoList = familyPlanNeedsDto.getFamilyPlanReqrdActnsDtoList();

		List<FamilyPlanReqrdActn> familyPlanReqrdActnList = familyPlanNeed.getFamilyPlanReqrdActns();

		if (!ObjectUtils.isEmpty(familyPlanReqrdActnsDtoList)) {
			// Add New Family Plan Required Action or Update Existing Family
			// Plan Required
			// Action
			updateFamilyPlanReqrdActn(familyPlanReqrdActnsDtoList, familyPlanReqrdActnList, familyPlanNeed, idUser);

		}

	}

	/**
	 * Method Name: updateFamilyPlanReqrdActn Method Description:This method is
	 * used to add or update Family Plan Required Action.
	 *
	 * @param familyPlanReqrdActnsDtoList
	 *            the family plan reqrd actns dto list
	 * @param familyPlanReqrdActnList
	 *            the family plan reqrd actn list
	 * @param familyPlanNeed
	 *            the family plan need
	 * @param idUser
	 *            the id user
	 * @return void
	 */

	private void updateFamilyPlanReqrdActn(List<FamilyPlanReqrdActnsDto> familyPlanReqrdActnsDtoList,
			List<FamilyPlanReqrdActn> familyPlanReqrdActnList, FamilyPlanNeed familyPlanNeed, Long idUser) {

		FamilyPlanReqrdActn familyPlanReqrdActn = new FamilyPlanReqrdActn();

		// Family Plan Required Action Add List
		ArrayList<FamilyPlanReqrdActn> familyPlanReqrdActnAddList = new ArrayList<FamilyPlanReqrdActn>();
		for (FamilyPlanReqrdActnsDto familyPlanReqrdActnsDto : familyPlanReqrdActnsDtoList) {
			// add new Family Plan Required Action
			if (TypeConvUtil.isNullOrEmpty(familyPlanReqrdActnsDto.getIdFamilyPlanReqrdActns())) {
				familyPlanReqrdActn = new FamilyPlanReqrdActn();
				BeanUtils.copyProperties(familyPlanReqrdActnsDto, familyPlanReqrdActn);
				if(CollectionUtils.isNotEmpty(familyPlanReqrdActnsDto.getTffEvidenceBasedServices())){
					familyPlanReqrdActn.setTffevidenceBasedSvcs(familyPlanReqrdActnsDto.getTffEvidenceBasedServices().stream().map(String::valueOf).collect(Collectors.joining(",")));
				}
				familyPlanReqrdActn.setIdCreatedPerson(idUser);
				familyPlanReqrdActn.setIdLastUpdatePerson(idUser);
				familyPlanReqrdActn.setFamilyPlanNeed(familyPlanNeed);
				familyPlanReqrdActnAddList.add(familyPlanReqrdActn);
			}
			// update Family Plan Required Action
			else {

				if (!ObjectUtils.isEmpty(familyPlanReqrdActnList)) {
					Optional<FamilyPlanReqrdActn> optFamilyPlanReqrdActn = familyPlanReqrdActnList.stream()
							.filter(reqrdactn -> reqrdactn.getIdFamilyPlanReqrdActns() == familyPlanReqrdActnsDto
									.getIdFamilyPlanReqrdActns().longValue())
							.findAny();
					if (optFamilyPlanReqrdActn.isPresent()) {
						familyPlanReqrdActn = optFamilyPlanReqrdActn.get();
						BeanUtils.copyProperties(familyPlanReqrdActnsDto, familyPlanReqrdActn);
						if(CollectionUtils.isNotEmpty(familyPlanReqrdActnsDto.getTffEvidenceBasedServices())){
							familyPlanReqrdActn.setTffevidenceBasedSvcs(familyPlanReqrdActnsDto.getTffEvidenceBasedServices().stream().map(String::valueOf).collect(Collectors.joining(",")));
						}
						familyPlanReqrdActn.setIdLastUpdatePerson(idUser);

					}

				}
			}

			// Retrieve the Family Plan Action from the Input Request
			List<FamilyPlanActnRsrcDto> familyPlanActnRsrcDtoList = familyPlanReqrdActnsDto
					.getFamilyPlanActnRsrcDtoList();
			// Retrieve the Family Plan Action Resource from Database
			if (CollectionUtils.isNotEmpty(familyPlanActnRsrcDtoList)) {
				// Add or Modify existing New Family Plan Action Resource
				updateFamilyPlanActnRsrc(familyPlanActnRsrcDtoList, familyPlanReqrdActn, idUser);
			}

		}
		// Add the New Required Action if there exists one action plan in
		// Database
		if (!ObjectUtils.isEmpty(familyPlanReqrdActnList)) {
			familyPlanReqrdActnAddList.forEach(reqrdactn -> familyPlanReqrdActnList.add(reqrdactn));

		}
		// Add the New Required Action if there exists no action plan in
		// Database
		else {
			if (familyPlanNeed.getFamilyPlanReqrdActns() == null)
				familyPlanNeed.setFamilyPlanReqrdActns(new ArrayList());
			familyPlanNeed.getFamilyPlanReqrdActns().addAll(familyPlanReqrdActnAddList);
		}

	}

	/**
	 * Method Name: updateFamilyPlanActnRsrc Method Description:This method is
	 * used to add or update Family Plan Required Action Resource.
	 *
	 * @param familyPlanActnRsrcDtoList
	 *            the family plan actn rsrc dto list
	 * @param familyPlanReqrdActn
	 *            the family plan reqrd actn
	 * @param idUser
	 *            the id user
	 * @return void
	 */

	private void updateFamilyPlanActnRsrc(List<FamilyPlanActnRsrcDto> familyPlanActnRsrcDtoList,
			FamilyPlanReqrdActn familyPlanReqrdActn, Long idUser) {

		// Family Plan Action Resource Add List
		ArrayList<FamilyPlanActnRsrc> familyPlanActnRsrcAddList = new ArrayList<FamilyPlanActnRsrc>();
		for (FamilyPlanActnRsrcDto familyPlanActnRsrcDto : familyPlanActnRsrcDtoList) {
			// add new Family Plan Required Action Resource
			if (TypeConvUtil.isNullOrEmpty(familyPlanActnRsrcDto.getIdFamilyPlanActnRsrc())) {

				FamilyPlanActnRsrc familyPlanActnRsrc = new FamilyPlanActnRsrc();
				BeanUtils.copyProperties(familyPlanActnRsrcDto, familyPlanActnRsrc);
				familyPlanActnRsrc.setIdCreatedPerson(idUser);
				familyPlanActnRsrc.setIdLastUpdatePerson(idUser);
				familyPlanActnRsrc.setFamilyPlanReqrdActn(familyPlanReqrdActn);
				familyPlanActnRsrcAddList.add(familyPlanActnRsrc);
			} else {
				// update Family Plan Required Action Resource
				if (CollectionUtils.isNotEmpty(familyPlanReqrdActn.getFamilyPlanActnRsrcs())) {
					Optional<FamilyPlanActnRsrc> optFamilyPlanActnRsrc = familyPlanReqrdActn.getFamilyPlanActnRsrcs()
							.stream().filter(actnrsrc -> actnrsrc.getIdFamilyPlanActnRsrc() == familyPlanActnRsrcDto
									.getIdFamilyPlanActnRsrc().longValue())
							.findAny();
					if (optFamilyPlanActnRsrc.isPresent()) {
						FamilyPlanActnRsrc familyPlanActnRsrc = optFamilyPlanActnRsrc.get();
						BeanUtils.copyProperties(familyPlanActnRsrcDto, familyPlanActnRsrc);
						familyPlanActnRsrc.setIdLastUpdatePerson(idUser);
						familyPlanActnRsrc.setFamilyPlanReqrdActn(familyPlanReqrdActn);

					}
				}
			}
		}
		// Add the New Required Action Resource if there exists one resource in
		// Database
		if (CollectionUtils.isNotEmpty(familyPlanReqrdActn.getFamilyPlanActnRsrcs())) {
			familyPlanActnRsrcAddList.forEach(actnrsrc -> familyPlanReqrdActn.getFamilyPlanActnRsrcs().add(actnrsrc));
		}
		// Add the New Required Action Resource if there exists no resource in
		// Database
		else {
			if (familyPlanReqrdActn.getFamilyPlanActnRsrcs() == null)
				familyPlanReqrdActn.setFamilyPlanActnRsrcs(new ArrayList());
			familyPlanReqrdActn.getFamilyPlanActnRsrcs().addAll(familyPlanActnRsrcAddList);
		}

	}

	/**
	 * Method Name: populateFamilyPlanNeedsDeleteList Method Description:This
	 * method populates the Family Plan Delete List.
	 *
	 * @param familyPlanNeedsDtoList
	 *            the family plan needs dto list
	 * @param familyPlanNeed
	 *            the family plan need
	 * @param familyPlanNeedDeleteList
	 *            the family plan need delete list
	 * @return void
	 */

	public List<FamilyPlanNeed> populateFamilyPlanNeedsDeleteList(List<FamilyPlanNeedsDto> familyPlanNeedsDtoList,
			FamilyPlanNeed familyPlanNeed, List<FamilyPlanNeed> familyPlanNeedDeleteList) {

		// Delete Family Plan Need List
		// ArrayList<FamilyPlanNeed> familyPlanNeedDeleteList = new
		// ArrayList<FamilyPlanNeed>();

		// Populate the Family Plan Needs Delete List
		Optional<FamilyPlanNeedsDto> optfamilyPlanNeedsDto = familyPlanNeedsDtoList.stream()
				.filter(planneed -> planneed.getIdFamilyPlanNeeds() == familyPlanNeed.getIdFamilyPlanNeeds()).findAny();
		if (!optfamilyPlanNeedsDto.isPresent() || ObjectUtils.isEmpty(optfamilyPlanNeedsDto)) {
			if (familyPlanNeed.getIdFamilyPlanNeeds() != null)
				familyPlanNeedDeleteList.add(familyPlanNeed);

		} else {
			checkReqdActions(familyPlanNeed.getFamilyPlanReqrdActns(),
					optfamilyPlanNeedsDto.get().getFamilyPlanReqrdActnsDtoList());
		}

		return familyPlanNeedDeleteList;
	}

	/**
	 * Method Name: checkReqdActions Method Description:This method Populates
	 * the Delete list for Family Plan Required Action and deletes the Family
	 * Plan Required Action.
	 *
	 * @param familyPlanReqrdActns
	 *            the family plan reqrd actns
	 * @param familyPlanReqrdActnsDtoList
	 *            the family plan reqrd actns dto list
	 * @return void
	 */

	private void checkReqdActions(List<FamilyPlanReqrdActn> familyPlanReqrdActns,
			List<FamilyPlanReqrdActnsDto> familyPlanReqrdActnsDtoList) {

		List<FamilyPlanReqrdActn> familyPlanReqrdActnDeleteList = new ArrayList<FamilyPlanReqrdActn>();
		if (familyPlanReqrdActnsDtoList == null) {
			familyPlanReqrdActns.forEach(reqdActns -> {
				if (reqdActns.getIdFamilyPlanReqrdActns() != null)
					familyPlanReqrdActnDeleteList.add(reqdActns);
			});
		} else {
			// Populates the Delete list for Family Plan Required Action
			familyPlanReqrdActns.forEach(reqdActns -> {
				Optional<FamilyPlanReqrdActnsDto> optFamilyPlanReqrdActnsDto = familyPlanReqrdActnsDtoList.stream()
						.filter(dto -> dto.getIdFamilyPlanReqrdActns() != null
								&& dto.getIdFamilyPlanReqrdActns().equals(reqdActns.getIdFamilyPlanReqrdActns()))
						.findAny();
				if (!optFamilyPlanReqrdActnsDto.isPresent() || ObjectUtils.isEmpty(optFamilyPlanReqrdActnsDto)) {
					if (reqdActns.getIdFamilyPlanReqrdActns() != null)
						familyPlanReqrdActnDeleteList.add(reqdActns);
				} else {
					checkResources(reqdActns.getFamilyPlanActnRsrcs(),
							optFamilyPlanReqrdActnsDto.get().getFamilyPlanActnRsrcDtoList());
				}
			});
		}

		// Delete family plan Required Action
		familyPlanReqrdActnDeleteList.forEach(reqractn -> familyPlanReqrdActns.remove(reqractn));

	}

	/**
	 * Method Name: checkResources Method Description:This method Populates the
	 * Delete list for Family Plan Action Resource and deletes the Family Plan
	 * Action Resource.
	 *
	 * @param familyPlanActnRsrcs
	 *            the family plan actn rsrcs
	 * @param familyPlanActnRsrcDtoList
	 *            the family plan actn rsrc dto list
	 * @return void
	 */
	private void checkResources(List<FamilyPlanActnRsrc> familyPlanActnRsrcs,
			List<FamilyPlanActnRsrcDto> familyPlanActnRsrcDtoList) {

		List<FamilyPlanActnRsrc> familyPlanActnRsrcDeleteList = new ArrayList<FamilyPlanActnRsrc>();
		if (familyPlanActnRsrcDtoList == null) {
			familyPlanActnRsrcs.forEach(actnRsrcs -> {
				if (actnRsrcs.getIdFamilyPlanActnRsrc() != null)
					familyPlanActnRsrcDeleteList.add(actnRsrcs);
			});
		} else {
			// Populate the Delete list for Family Plan Action Resource
			familyPlanActnRsrcs.forEach(actnRsrcs -> {
				Optional<FamilyPlanActnRsrcDto> optFamilyPlanActnRsrcDto = familyPlanActnRsrcDtoList.stream()
						.filter(dto -> dto.getIdFamilyPlanActnRsrc() != null
								&& dto.getIdFamilyPlanActnRsrc().equals(actnRsrcs.getIdFamilyPlanActnRsrc()))
						.findAny();
				if ((!optFamilyPlanActnRsrcDto.isPresent() || null == optFamilyPlanActnRsrcDto)
						&& actnRsrcs.getIdFamilyPlanActnRsrc() != null) {
					familyPlanActnRsrcDeleteList.add(actnRsrcs);

				}
			});
		}

		// Delete the Family Plan Action Resource
		familyPlanActnRsrcDeleteList.forEach(actnRsrcs -> familyPlanActnRsrcs.remove(actnRsrcs));

	}

	/**
	 * Method Name: populateFamilyPlanPartcpntDeleteList Method Description:This
	 * method Populates the Delete list for Family Plan Participants.
	 *
	 * @param familyPlanPartcpntDtoList
	 *            the family plan partcpnt dto list
	 * @param familyPlanPartcpntList
	 *            the family plan partcpnt list
	 * @return void
	 */

	public ArrayList<FamilyPlanPartcpnt> populateFamilyPlanPartcpntDeleteList(
			List<FamilyPlanPartcpntDto> familyPlanPartcpntDtoList, List<FamilyPlanPartcpnt> familyPlanPartcpntList) {

		// Family Plan Participant Delete List
		ArrayList<FamilyPlanPartcpnt> familyPlanPartcpntDeleteList = new ArrayList<FamilyPlanPartcpnt>();

		for (FamilyPlanPartcpnt familyPlanPartcpnt : familyPlanPartcpntList) {

			// If the Input Family Plan Participant List is not empty
			if (!ObjectUtils.isEmpty(familyPlanPartcpntDtoList)) {
				Optional<FamilyPlanPartcpntDto> optFamilyPlanPartcpntDto = familyPlanPartcpntDtoList.stream().filter(
						partcpnt -> partcpnt.getIdFamilyPlanPartcpnt() != null && partcpnt.getIdFamilyPlanPartcpnt()
								.longValue() == familyPlanPartcpnt.getIdFamilyPlanPartcpnt().longValue())
						.findAny();
				if (!optFamilyPlanPartcpntDto.isPresent() || ObjectUtils.isEmpty(optFamilyPlanPartcpntDto)) {
					// Populate the Family Plan Participant Delete List
					familyPlanPartcpntDeleteList.add(familyPlanPartcpnt);
				}

			}
			// If the Input Family Plan Participant List is empty
			else {
				// Populate the Family Plan Participant Delete List
				familyPlanPartcpntDeleteList.add(familyPlanPartcpnt);

			}
		}
		return familyPlanPartcpntDeleteList;
	}

	/**
	 * Method Name: queryChildrenInCaseInSubcare Method Description: Query all
	 * children in the case that are currently in substitute care.
	 *
	 * @param caseId
	 *            the case id
	 * @return List<Long>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Long> queryChildrenInCaseInSubcare(Long caseId) {
		List<Long> childrenInCaseInSubcare = new ArrayList<Long>();
		SQLQuery sql = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(queryChildrenInCaseInSubcare)
				.addScalar("idPerson", StandardBasicTypes.LONG).setParameter("idCase", caseId)
				.setParameter("cdStagePersRole", ServiceConstants.PRIMARY_CHILD)
				.setParameter("cdStage", ServiceConstants.CSTAGES_SUB)
				.setResultTransformer(Transformers.aliasToBean(StagePersonLinkDto.class));
		List<StagePersonLinkDto> stagePersonLinkDtos = sql.list();
		if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkDtos)) {
			for (StagePersonLinkDto stagePersonLinkDto : stagePersonLinkDtos) {
				if (stagePersonLinkDto.getIdPerson() != ServiceConstants.ZERO_VAL) {
					childrenInCaseInSubcare.add(stagePersonLinkDto.getIdPerson());
				}
			}
		}
		return childrenInCaseInSubcare;
	}

	/**
	 * This method fetches the family plan narrative list.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FamilyPlanNarrDto> getFamilyPlanNarrList(Long idStage, Long idEvent) {
		SQLQuery sql = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getFamilyPlanNarrList)
				.addScalar("idFamilyPlanNarr", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP).setParameter("idStage", idStage)
				.setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(FamilyPlanNarrDto.class));
		List<FamilyPlanNarrDto> familyPlanNarrList = sql.list();
		return familyPlanNarrList;
	}

	@Override
	public List<FamilyPlanNarrDto> getFamilyPlanVersions(Long idEvent) {
		// Modified the code to get the idEvent, idCreated and idLastUpdated
		// person value - Warranty defect 11390
		SQLQuery sql = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getFamilyPlanVersions)
				.addScalar("idFamilyPlanNarr", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idHwdFamilyPlanNarr", StandardBasicTypes.LONG)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("idEvent", StandardBasicTypes.LONG)
				.setParameter("idEvent", idEvent)
				.setResultTransformer(Transformers.aliasToBean(FamilyPlanNarrDto.class));
		List<FamilyPlanNarrDto> familyPlanVersions = sql.list();
		return familyPlanVersions;
	}
	/**
	 * This method fetches the principals for risk of removal.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Long> fetchPrincipalsRiskOfRemoval(Long idEvent) {
		SQLQuery sqlQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchPrincipalsRiskOfRemoval)
				.addScalar("idPerson", StandardBasicTypes.LONG).setParameter("idEvent", idEvent));

		return (List<Long>) sqlQuery.list();
	}

	/**
	 * gets the count of FCC completed.
	 */
	@Override
	public Long getCountFCCComplete(Long idEvent) {
		SQLQuery sqlQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fccPersonInCompleteCount)
				.setParameter("idEvent", idEvent));

		BigDecimal count = (BigDecimal) sqlQuery.uniqueResult();
		if (!ObjectUtils.isEmpty(count))
			return count.longValue();
		else
			return ServiceConstants.ZERO;
	}

	/**
	 * Method Name: getFamilyPlanFormDtl Method Description: This method fetches
	 * the FamilyPlan or Family Plan Evaluation Details for the idEvent.
	 *
	 * @param familyPlanDtlEvalReq
	 *            the family plan dtl eval req
	 * @return Object
	 */
	public Object getFamilyPlanFormDtl(FamilyPlanDtlEvalReq familyPlanDtlEvalReq) {
		Event event = (Event) sessionFactory.getCurrentSession().createCriteria(Event.class)
				.add(Restrictions.eq("idEvent", familyPlanDtlEvalReq.getIdEvent())).uniqueResult();

		// Fetching the FamilyPlan saved in the Database
		FamilyPlan familyPlan = (FamilyPlan) sessionFactory.getCurrentSession().createCriteria(FamilyPlan.class)
				.add(Restrictions.eq("event", event)).uniqueResult();

		// Fetching the FamilyPlan Evaluation saved in the Database
		FamilyPlanEval familyPlanEval = (FamilyPlanEval) sessionFactory.getCurrentSession()
				.createCriteria(FamilyPlanEval.class).add(Restrictions.eq("eventByIdEvent", event)).uniqueResult();

		Object obj = new Object();

		if (!ObjectUtils.isEmpty(familyPlan)) {
			obj = familyPlan;
			familyPlanDtlEvalReq.setFamilyPlanDtlInd(ServiceConstants.TRUEVAL);
		} else if (!ObjectUtils.isEmpty(familyPlanEval)) {
			obj = familyPlanEval;
			familyPlanDtlEvalReq.setFamilyPlanDtlInd(ServiceConstants.FALSEVAL);
		}
		return obj;
	}

	/**
	 * Method Name: updateFamilyPlanEntity Method Description: This method
	 * updates the family plan table.
	 *
	 * @param familyPlanDto
	 *            the family plan dto
	 * @param indFamilyPlanEval
	 *            the ind family plan eval
	 * @return the long
	 */
	@Override
	public Long updateFamilyPlanEntity(FamilyPlanDto familyPlanDto, boolean indFamilyPlanEval) {
		Criteria familyPlanCriteria = sessionFactory.getCurrentSession().createCriteria(FamilyPlan.class);
		familyPlanCriteria.add(Restrictions.eq(EVENT_ID, familyPlanDto.getFamilyPlanEvent().getIdEvent()));
		familyPlanCriteria.add(Restrictions.eq("capsCase.idCase", familyPlanDto.getIdCase()));
		FamilyPlan familyPlan = (FamilyPlan) familyPlanCriteria.uniqueResult();
		if(!ObjectUtils.isEmpty(familyPlanDto.getDtCompleted())) {
			familyPlan.setDtCompleted(familyPlanDto.getDtCompleted());
		}
		if(!ObjectUtils.isEmpty(familyPlanDto.getDtNextDue())) {
			familyPlan.setDtNextReview(familyPlanDto.getDtNextDue());
		}
		if(!ObjectUtils.isEmpty(familyPlanDto.getReasonForCPSInvolvement())) {
			familyPlan.setTxtRsnCpsInvlvmnt(familyPlanDto.getReasonForCPSInvolvement());
		}
		if(!ObjectUtils.isEmpty(familyPlanDto.getStrengthsAndResources())) {
			familyPlan.setTxtStrngthsRsrcs(familyPlanDto.getStrengthsAndResources());
		}
		if(!ObjectUtils.isEmpty(familyPlanDto.getExplanationOfClientNonParticipation())) {
			familyPlan.setTxtNotParticipate(familyPlanDto.getExplanationOfClientNonParticipation());
		}
		if(!ObjectUtils.isEmpty(familyPlanDto.getIndClientComments())) {
			familyPlan.setIndClientComments((!ServiceConstants.N.equals(familyPlanDto.getIndClientComments()))
				? ServiceConstants.Y : ServiceConstants.N);
		}
		if (!indFamilyPlanEval && !ObjectUtils.isEmpty(familyPlanDto.getCdFgdmConference())) {
			familyPlan.setCdFgdmConference(familyPlanDto.getCdFgdmConference());
		}
		if(!ObjectUtils.isEmpty(familyPlanDto.getTxtCommunitySupports())) {
			familyPlan.setTxtCommunitySupports(familyPlanDto.getTxtCommunitySupports());
		}
		if(!ObjectUtils.isEmpty(familyPlanDto.getTxtHopesDreams())) {
			familyPlan.setTxtHopesDreams(familyPlanDto.getTxtHopesDreams());
		}
		if(!ObjectUtils.isEmpty(familyPlanDto.getTxtOtherParticipants())) {
			familyPlan.setTxtOtherParticipants(familyPlanDto.getTxtOtherParticipants());
		}
		if(!ObjectUtils.isEmpty(familyPlanDto.getTxtRespChildsEducation())) {
			familyPlan.setTxtRespChildsEducation(familyPlanDto.getTxtRespChildsEducation());
		}
		if(!ObjectUtils.isEmpty(familyPlanDto.getTxtCelebration())) {
			familyPlan.setTxtCelebration(familyPlanDto.getTxtCelebration());
		}
		if(!ObjectUtils.isEmpty(familyPlanDto.getTxtPurposeReconference())) {
			familyPlan.setTxtPurposeReconference(familyPlanDto.getTxtPurposeReconference());
		}
		sessionFactory.getCurrentSession().update(familyPlan);
		return familyPlan.getEvent().getIdEvent();
	}

	/**
	 * Method Name: updateFamilyPlanCost Method Description: This method is used
	 * update the family plan cost table.
	 *
	 * @param familyPlanDto
	 *            the family plan dto
	 * @return the long
	 */
	@Override
	public Long updateFamilyPlanCost(FamilyPlanDto familyPlanDto) {
		Criteria familyPlanCostCriteria = sessionFactory.getCurrentSession().createCriteria(FamilyPlanCost.class);
		familyPlanCostCriteria.add(Restrictions.eq("event.idEvent", familyPlanDto.getFamilyPlanEvent().getIdEvent()));
		familyPlanCostCriteria.add(Restrictions.eq("capsCase.idCase", familyPlanDto.getIdCase()));
		FamilyPlanCost familyPlanCost = (FamilyPlanCost) familyPlanCostCriteria.uniqueResult();
		familyPlanCost.setCdAmtChildCare(familyPlanDto.getChildCareCost());
		familyPlanCost.setCdAmtFood(familyPlanDto.getFoodCost());
		familyPlanCost.setCdAmtTravel(familyPlanDto.getTravelCost());
		familyPlanCost.setCdAmtFacility(familyPlanDto.getFacilityCost());
		familyPlanCost.setAmtFgdmTotal(familyPlanDto.getFgdmTotalCost());
		sessionFactory.getCurrentSession().update(familyPlanCost);
		return familyPlanCost.getIdFamilyPlanCost();
	}

	/**
	 * Method Name: updateFamilyPlanRole Method Description: This method deletes
	 * the roles for the event id and add based on the role selection.
	 *
	 * @param idEvent
	 *            the id event
	 * @param roles
	 *            the roles
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void updateFamilyPlanRole(Long idEvent, List<String> roles) {
		Criteria familyPlanRoleCriteria = sessionFactory.getCurrentSession().createCriteria(FamilyPlanRole.class);
		familyPlanRoleCriteria.add(Restrictions.eq(EVENT_ID, idEvent));
		List<FamilyPlanRole> familyPlanRoleList = familyPlanRoleCriteria.list();
		// deletes the roles associated with event
		familyPlanRoleList.forEach(r -> sessionFactory.getCurrentSession().delete(r));
		if (!ObjectUtils.isEmpty(roles) && !roles.isEmpty()) {
			Event event = (Event) sessionFactory.getCurrentSession().load(Event.class, idEvent);
			// adds the roles associated with the event.
			roles.forEach(o -> {
				FamilyPlanRole familyPlanRole = new FamilyPlanRole();
				familyPlanRole.setCdRole(o);
				familyPlanRole.setEvent(event);
				sessionFactory.getCurrentSession().save(familyPlanRole);
			});
		}
	}

	/**
	 * Method Name: addFamilyPlanEval Method Description: This method adds the
	 * family plan evaluation.
	 *
	 * @param familyPlanDto
	 *            the family plan dto
	 * @param familyPlanEvalEventId
	 *            the family plan eval event id
	 * @return the long
	 */
	@Override
	public Long updateFamilyPlanEval(FamilyPlanDto familyPlanDto, Long familyPlanEvalEventId) {
		// updates family plan
		Long familyPlanEventId = updateFamilyPlanEntity(familyPlanDto, true);
		// updates family plan cost
		updateFamilyPlanCost(familyPlanDto);
		EventPlanLink eventPlanLink;
		Event event = (Event) sessionFactory.getCurrentSession().load(Event.class, familyPlanEvalEventId);
		Criteria eventPlanLinkCriteria = sessionFactory.getCurrentSession().createCriteria(EventPlanLink.class);
		eventPlanLinkCriteria.add(Restrictions.eq("event.idEvent", familyPlanEvalEventId));
		eventPlanLink = (EventPlanLink) eventPlanLinkCriteria.uniqueResult();
		if (ObjectUtils.isEmpty(eventPlanLink)) {
			eventPlanLink = new EventPlanLink();
		}
		eventPlanLink.setEvent(event);
		eventPlanLink.setIndImpactCreated(ServiceConstants.Y);
		eventPlanLink.setDtLastUpdate(new Date());
		// adds eventPlanLink
		sessionFactory.getCurrentSession().save(eventPlanLink);
		FamilyPlanEval familyPlanEval = null;
		// adds FamilyPlanEval
		if (!ObjectUtils.isEmpty(familyPlanDto.getIdEvalEvent())) {
			Criteria familyPlanEvalCriteria = sessionFactory.getCurrentSession().createCriteria(FamilyPlanEval.class);
			familyPlanEvalCriteria.add(Restrictions.eq("eventByIdEvent.idEvent", familyPlanDto.getIdEvalEvent()));
			familyPlanEvalCriteria.add(Restrictions.eq("capsCase.idCase", familyPlanDto.getIdCase()));
			familyPlanEval = (FamilyPlanEval) familyPlanEvalCriteria.uniqueResult();
		} else {
			familyPlanEval = new FamilyPlanEval();
		}
		familyPlanEval.setEventByIdEvent(event);
		Event familyPlanEvent = (Event) sessionFactory.getCurrentSession().load(Event.class, familyPlanEventId);
		familyPlanEval.setEventByIdFamilyPlanEvent(familyPlanEvent);
		if (!ObjectUtils.isEmpty(familyPlanDto.getDtNextReview())) {
			familyPlanEval.setDtCompleted(familyPlanDto.getDtNextReview());
		}
		if (!ObjectUtils.isEmpty(familyPlanDto.getDtNextDue())) {
			familyPlanEval.setDtNextReview(familyPlanDto.getDtNextDue());
		}
		if (!ObjectUtils.isEmpty(familyPlanDto.getCdFgdmConference())) {
			familyPlanEval.setCdFgdmConference(familyPlanDto.getCdFgdmConference());
		}
		familyPlanEval.setNbrFamPlanEvalVersion(ServiceConstants.FGDM_VERSION.toString());
		if (!ObjectUtils.isEmpty(familyPlanDto.getTxtOtherParticipants())) {
			familyPlanEval.setTxtOtherParticipants(familyPlanDto.getTxtOtherParticipants());
		}
		if (!ObjectUtils.isEmpty(familyPlanDto.getTxtOtherParticipants())) {
			familyPlanEval.setTxtOtherParticipants(familyPlanDto.getTxtOtherParticipants());
		}
		if (!ObjectUtils.isEmpty(familyPlanDto.getCelebrationOfSuccess())) {
			familyPlanEval.setTxtCelebration(familyPlanDto.getCelebrationOfSuccess());
		}
		if (!ObjectUtils.isEmpty(familyPlanDto.getTxtPurposeReconference())) {
			familyPlanEval.setTxtPurposeReconference(familyPlanDto.getTxtPurposeReconference());
		}
		CapsCase capsCase = (CapsCase) sessionFactory.getCurrentSession().load(CapsCase.class,
				familyPlanDto.getIdCase());
		familyPlanEval.setCapsCase(capsCase);
		sessionFactory.getCurrentSession().saveOrUpdate(familyPlanEval);
		return familyPlanEval.getEventByIdEvent().getIdEvent();
	}

	/**
	 * Method Name: updateFamilyplanitem Method Description: updates the
	 * FAMILY_PLAN_ITEM.
	 *
	 * @param idEvent
	 *            the id event
	 * @param idCase
	 *            the id case
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void updateFamilyplanitem(Long idEvent, Long idCase) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FamilyPlanItem.class);
		criteria.add(Restrictions.eq(EVENT_ID, idEvent));
		criteria.add(Restrictions.eq("capsCase.idCase", idCase));
		List<FamilyPlanItem> familyPlanItemList = criteria.list();
		familyPlanItemList.forEach(o -> {
			o.setCdPrevLevelConcern(o.getCdCurrentLevelConcern());
			sessionFactory.getCurrentSession().save(o);
		});
	}

	/**
	 * Method Name: updateToDoforNextReview Method Description:Inserts a row
	 * into the TODO table to create a Task To-Do for the primary worker to
	 * create the family plan review.
	 *
	 * @param familyPlanDto
	 *            the family plan dto
	 */
	@Override
	public void updateToDoforNextReview(FamilyPlanDto familyPlanDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Todo.class);
		criteria.add(Restrictions.eq("capsCase.idCase", familyPlanDto.getIdCase()));
		criteria.add(Restrictions.eq(EVENT_ID, familyPlanDto.getFamilyPlanEvent().getIdEvent()));
		criteria.add(Restrictions.eq("cdTodoType", CodesConstant.CTODOTYP_A));
		Todo todoToBeDeleted = (Todo) criteria.uniqueResult();
		if (!ObjectUtils.isEmpty(todoToBeDeleted)) {
			sessionFactory.getCurrentSession().delete(todoToBeDeleted);
		}
		Todo todo = new Todo();
		todo.setCdTodoType(CodesConstant.CTODOTYP_A);
		if (CodesConstant.CSTAGES_FPR.equals(familyPlanDto.getCdStage())) {
			todo.setCdTodoTask(ServiceConstants.CD_TASK_FPR_FAM_PLAN);
		} else if (CodesConstant.CSTAGES_FRE.equals(familyPlanDto.getCdStage())) {
			todo.setCdTodoTask(ServiceConstants.CD_TASK_FRE_FAM_PLAN);
		} else if (CodesConstant.CSTAGES_FSU.equals(familyPlanDto.getCdStage())) {
			todo.setCdTodoTask(ServiceConstants.CD_TASK_FSU_FAM_PLAN);
		}
		todo.setDtTodoCreated(new Date());
		todo.setDtTodoDue(familyPlanDto.getDtNextDue());
		CapsCase capsCase = (CapsCase) sessionFactory.getCurrentSession().load(CapsCase.class,
				familyPlanDto.getIdCase());
		todo.setCapsCase(capsCase);
		Person todoPersonCreator = (Person) sessionFactory.getCurrentSession().load(Person.class,
				familyPlanDto.getIdUser());
		todo.setPersonByIdTodoPersCreator(todoPersonCreator);
		Person todoPersonAssigned = (Person) sessionFactory.getCurrentSession().load(Person.class,
				familyPlanDto.getIdPrimaryWorker());
		todo.setPersonByIdTodoPersAssigned(todoPersonAssigned);
		Person todoPersonWorker = (Person) sessionFactory.getCurrentSession().load(Person.class,
				familyPlanDto.getIdPrimaryWorker());
		todo.setPersonByIdTodoPersWorker(todoPersonWorker);
		Stage stage = (Stage) sessionFactory.getCurrentSession().load(Stage.class, familyPlanDto.getIdStage());
		todo.setStage(stage);
		Event event = (Event) sessionFactory.getCurrentSession().load(Event.class,
				familyPlanDto.getFamilyPlanEvent().getIdEvent());
		todo.setEvent(event);
		String desc = familyPlanDto.getCdStage() + FAMILY_PLAN_REVIEW_FOR + familyPlanDto.getNmStage() + DUE_ON
				+ DateUtils.stringDt(familyPlanDto.getDtNextDue());
		todo.setTxtTodoDesc(desc);
		todo.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().save(todo);
	}

	/**
	 * Method Name: deleteChildCandidacy Method Description: Deletes the rows in
	 * Child candidacy table.
	 *
	 * @param idEvent
	 *            the id event
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void deleteChildCandidacy(Long idEvent) {
		ArrayList<Long> principals = fetchPrincipalsForPlan(idEvent);
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FamilyChildCandidacy.class);
		criteria.add(Restrictions.eq("event.idEvent", idEvent));
		criteria.add(Restrictions.not(Restrictions.in("person.idPerson", principals)));
		List<FamilyChildCandidacy> familyChildCandidacyList = criteria.list();
		if (!ObjectUtils.isEmpty(familyChildCandidacyList)) {
			familyChildCandidacyList.forEach(o -> sessionFactory.getCurrentSession().delete(o));
		}
	}

	/**
	 * Method Name: updateChildCandidacyDeterminations Method Description:
	 * Update the Determination and Redetermination dates in the
	 * FAMILY_CHILD_CANDIDACY table.
	 *
	 * @param idEvent
	 *            the id event
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void updateChildCandidacyDeterminations(Long idEvent) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FamilyChildCandidacy.class);
		criteria.add(Restrictions.eq("event.idEvent", idEvent));
		List<FamilyChildCandidacy> familyChildCandidacyList = criteria.list();
		if (!ObjectUtils.isEmpty(familyChildCandidacyList)) {
			familyChildCandidacyList.forEach(o -> {
				o.setDtDetermination(new Date());
				o.setDtRedetermination(DateUtils.getSixMonthsFromCurrentDate());
				sessionFactory.getCurrentSession().update(o);
			});
		}
	}

	/**
	 * Method Name: fetchAndInsertRecsChildCandidacy Method Description: Fetches
	 * the all the fields child candidacy from FAMILY_CHILD_CANDIDACY and
	 * inserts the records in PERSON_ELIGIBILITY table.
	 *
	 * @param idEvent
	 *            the id event
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void fetchAndInsertRecsChildCandidacy(Long idEvent) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FamilyChildCandidacy.class);
		criteria.add(Restrictions.eq("event.idEvent", idEvent));
		List<FamilyChildCandidacy> familyChildCandidacyList = criteria.list();
		if (!ObjectUtils.isEmpty(familyChildCandidacyList)) {
			familyChildCandidacyList.forEach(o -> {
				Criteria criteria2 = sessionFactory.getCurrentSession().createCriteria(PersonEligibility.class);
				criteria2.add(Restrictions.eq("person.idPerson", o.getPerson().getIdPerson()));
				criteria2.add(Restrictions.eq("cdPersEligEligType", CodesConstant.CCLIELIG_CND));
				criteria2.add(Restrictions.isNull("dtDeny"));
				List<PersonEligibility> personEligibilityList = criteria2.list();
				personEligibilityList.forEach(p -> {
					// If there a record in the person eligibility table, then
					// that row
					// should be end dated by subtracting one day from the day
					// the new plan
					// is saved with date plan completed/Current Review date.
					p.setDtDeny(DateUtils.yesterday());
					p.setCdPersEligPrgClosed(ServiceConstants.PERSELIG_PRG_OPEN_C);
					sessionFactory.getCurrentSession().saveOrUpdate(o);
				});
				if (ServiceConstants.Y.equals(o.getIndRiskRemoval())) {
					PersonEligibility personEligibility = new PersonEligibility();
					personEligibility.setDtLastUpdate(new Date());
					Person person = (Person) sessionFactory.getCurrentSession().load(Person.class,
							o.getPerson().getIdPerson());
					personEligibility.setPerson(person);
					personEligibility.setCdPersEligEligType(CodesConstant.CCLIELIG_CND);
					personEligibility.setDtPersEligStart(o.getDtDetermination());
					personEligibility.setDtPersEligEnd(o.getDtRedetermination());
					personEligibility.setCdPersEligPrgStart(ServiceConstants.PERSELIG_PRG_OPEN_C);
					personEligibility.setCdPersEligPrgOpen(ServiceConstants.PERSELIG_PRG_OPEN_C);
					sessionFactory.getCurrentSession().save(personEligibility);
				}
			});
		}
	}

	/**
	 * This gets the version of family plan.
	 */
	@Override
	public String getFamilyPlanVersion(Long idEvent) {
		Criteria familyPlanCriteria = sessionFactory.getCurrentSession().createCriteria(FamilyPlan.class);
		familyPlanCriteria.add(Restrictions.eq(EVENT_ID, idEvent));
		FamilyPlan familyPlan = (FamilyPlan) familyPlanCriteria.uniqueResult();
		if (!ObjectUtils.isEmpty(familyPlan))
			return familyPlan.getNbrFamPlanVersion();
		else
			return ServiceConstants.EMPTY_STRING;
	}

	/**
	 * This fetches the family plan evaluation version.
	 */
	@Override
	public String getFamilyPlanEvalVersion(Long idEvent) {
		Criteria familyPlanCriteria = sessionFactory.getCurrentSession().createCriteria(FamilyPlanEval.class);
		familyPlanCriteria.add(Restrictions.eq("eventByIdEvent.idEvent", idEvent));
		FamilyPlanEval familyPlanEval = (FamilyPlanEval) familyPlanCriteria.uniqueResult();
		if (!ObjectUtils.isEmpty(familyPlanEval))
			return familyPlanEval.getNbrFamPlanEvalVersion();
		else
			return ServiceConstants.EMPTY_STRING;
	}

	/**
	 * gets the fsna information using family plan event id.
	 */
	@Override
	public List<CpsFsnaDto> getFamilyPlanRequest(FamilyPlanDtlEvalReq familyPlanReq) {
		SQLQuery sql = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getFamilyPlanRequest)
				.addScalar("idCpsFsna", StandardBasicTypes.LONG).addScalar("idPrmryCrgvrPrnt", StandardBasicTypes.LONG)
				.addScalar("idSecndryCrgvrPrnt", StandardBasicTypes.LONG)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.setParameter("idEvent", familyPlanReq.getIdFamilyPlanEvent())
				.setResultTransformer(Transformers.aliasToBean(CpsFsnaDto.class));
		List<CpsFsnaDto> cpsFsnaDtoList = sql.list();
		return cpsFsnaDtoList;
	}

	/**
	 * gets the fsna information using family plan evaluation event id.
	 */
	@Override
	public List<CpsFsnaDto> getFamilyPlanEvalRequest(FamilyPlanDtlEvalReq familyPlanReq) {
		SQLQuery sql = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getFamilyPlanEvalRequest)
				.addScalar("idCpsFsna", StandardBasicTypes.LONG).addScalar("idPrmryCrgvrPrnt", StandardBasicTypes.LONG)
				.addScalar("idSecndryCrgvrPrnt", StandardBasicTypes.LONG)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.setParameter("idEvent", familyPlanReq.getIdFamilyPlanEvalEvent())
				.setResultTransformer(Transformers.aliasToBean(CpsFsnaDto.class));
		List<CpsFsnaDto> cpsFsnaDtoList = sql.list();
		return cpsFsnaDtoList;
	}

	/**
	 * gets the active tasks for family plan.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void getActiveTaskGoals(FamilyPlanItemDto familyPlanItemDto) {
		List<Long> idGoals = new ArrayList<>();
		List<FamilyPlanGoalDto> familyPlanGoalDtoList = familyPlanItemDto.getFamilyPlanGoalDtoList();

		if (!ObjectUtils.isEmpty(familyPlanGoalDtoList) && familyPlanGoalDtoList.size() > 0) {
			for (FamilyPlanGoalDto familyPlanGoalDto : familyPlanGoalDtoList) {
				idGoals.add(familyPlanGoalDto.getIdFamilyPlanGoal());
			}
		}
		if (!ObjectUtils.isEmpty(idGoals)) {
			SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getActiveTaskGoals)
					.addScalar("idFamilyPlanTask", StandardBasicTypes.LONG)
					.addScalar("dtCreated", StandardBasicTypes.DATE).addScalar("txtTask", StandardBasicTypes.STRING)
					.addScalar("indCpsAssigned", StandardBasicTypes.STRING)
					.addScalar("indFamilyAssigned", StandardBasicTypes.STRING)
					.addScalar("indParentsAssigned", StandardBasicTypes.STRING)
					.addScalar("indCpsOrdered", StandardBasicTypes.STRING)
					.addScalar("indFamilyOrdered", StandardBasicTypes.STRING)
					.addScalar("indParentsOrdered", StandardBasicTypes.STRING)
					.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
					.addScalar("dtNoLongerNeeded", StandardBasicTypes.DATE)
					.addScalar("dtCompleted", StandardBasicTypes.DATE).addScalar("dtApproved", StandardBasicTypes.DATE)
					.setParameterList("idGoals", idGoals).setParameter("idCase", familyPlanItemDto.getIdCase())
					.setResultTransformer(Transformers.aliasToBean(FamilyPlanTaskDto.class));
			List<FamilyPlanTaskDto> activeTasksList = sqlQuery.list();

			familyPlanItemDto.setFamilyPlanTaskDtoList(activeTasksList);
		}
	}

	/**
	 * gets the tasks which are linked to goals.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void setAllTasksNotLinkedToGoals(FamilyPlanItemDto familyPlanItemDto) {
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getAllTasksNotLinkedToGoals)
				.addScalar("idFamilyPlanTask", StandardBasicTypes.LONG)
				.setParameter("idEvent", familyPlanItemDto.getIdEvent())
				.setParameter("idCase", familyPlanItemDto.getIdCase());
		List<Long> tasksNotLinkedToGoals = sqlQuery.list();

		familyPlanItemDto.setTasksNotLinkedToGoals(tasksNotLinkedToGoals);
	}

	/**
	 * checks if the interpreter translator is needed for family plan.
	 */
	@Override
	public void checkIfIntepreterTranslatorIsNeeded(FamilyPlanDto familyPlanDto) {
		familyPlanDto.setInterpreterTranslatorIsNeeded(Boolean.FALSE);
		List<PersonValueDto> principals = familyPlanDto.getPrincipalsForEventList();
		List<Long> idPersons = new ArrayList<Long>();

		if (!ObjectUtils.isEmpty(principals) && principals.size() > 0) {
			for (PersonValueDto personValueDto : principals) {
				idPersons.add(personValueDto.getPersonId());
			}
		}

		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(checkIfIntepreterTranslatorIsNeeded).addScalar("count", StandardBasicTypes.LONG)
				.setParameterList("idPersons", idPersons);

		Long characteristicsCount = (Long) sqlQuery.uniqueResult();

		if (!ObjectUtils.isEmpty(characteristicsCount) && characteristicsCount > 0) {
			familyPlanDto.setInterpreterTranslatorIsNeeded(Boolean.TRUE);
		}
	}

	/**
	 * Method Name: updateChildPermanencyInfo Method Description: Method updates
	 * the Event Person Link Table for Family Plan Event when Permanency Goal is
	 * added for child.
	 * 
	 * @param idEvent
	 * @param person
	 */
	@Override
	public void updateChildPermanencyInfo(Long idEvent, PersonValueDto person) {
		EventPersonLink eventPersonLink = (EventPersonLink) sessionFactory.getCurrentSession()
				.createCriteria(EventPersonLink.class).add(Restrictions.eq("event.idEvent", idEvent))
				.add(Restrictions.eq("person.idPerson", person.getPersonId())).uniqueResult();
		if (!ObjectUtils.isEmpty(eventPersonLink)) {
			eventPersonLink.setCdFamPlanPermGoal(person.getPermanencyGoalCode());
			eventPersonLink.setDtFamPlanPermGoalTarget(person.getPermanencyGoalTargetDate());
			sessionFactory.getCurrentSession().update(eventPersonLink);
		}
	}

	/**
	 * Method Name: updatePermanencyGoalComment Method Description: Method
	 * updates the Permanency Goal comments in Family Plan
	 * 
	 * @param familyPlanDto
	 */
	@Override
	public void updatePermanencyGoalComment(FamilyPlanDto familyPlanDto) {
		FamilyPlan familyPlan = (FamilyPlan) sessionFactory.getCurrentSession().get(FamilyPlan.class,
				familyPlanDto.getIdFamilyPlan());
		familyPlan.setTxtPermGoalComments(familyPlanDto.getTxtPermGoalComments());
		sessionFactory.getCurrentSession().update(familyPlan);
	}

	/**
	 * Method Name: saveFamilyPlanTask Method Description: Saves the data into
	 * the family_plan_task,FAM_PLN_TASK_GOAL_LINK tables.
	 *
	 * @param taskGoalValueDtos
	 *            - list of tasks with goals to save
	 * @param currentEventId
	 *            - event id
	 * @throws DataNotFoundException
	 *             - record not found exception
	 */
	@Override
	public FamilyPlanRes saveFamilyPlanTask(List<TaskGoalValueDto> taskGoalValueDtos, Long currentEventId)
			throws DataNotFoundException {

		FamilyPlanRes familyPlanRes = new FamilyPlanRes();
		/*
		 * inserts a complete set of records for adding a Family Plan Tasks to
		 * the database.
		 */
		for (TaskGoalValueDto taskGoalValueDto : taskGoalValueDtos) {

			if (!ServiceConstants.EMPTY_STRING.equals(taskGoalValueDto.getTaskTxt())) {

				// Insert in to FAMILY_PLAN_TASK
				FamilyPlanTask familyPlanTask = new FamilyPlanTask();
				if (!TypeConvUtil.isNullOrEmpty(taskGoalValueDto.getEventId())) {
					Event event = new Event();
					event.setIdEvent(taskGoalValueDto.getEventId());
					familyPlanTask.setEvent(event);
				}
				if (!TypeConvUtil.isNullOrEmpty(taskGoalValueDto.getCaseId())) {
					CapsCase capsCase = new CapsCase();
					capsCase.setIdCase(taskGoalValueDto.getCaseId());
					familyPlanTask.setCapsCase(capsCase);
				}
				if (taskGoalValueDto.getFamilyCourtOrderedInd()) {
					familyPlanTask.setIndCourtOrdered(ServiceConstants.YES);
				} else {
					familyPlanTask.setIndCourtOrdered(ServiceConstants.NO);
				}

				familyPlanTask.setTxtTask(taskGoalValueDto.getTaskTxt());
				if (taskGoalValueDto.getCpsAssignedInd()) {
					familyPlanTask.setIndCpsAssigned(ServiceConstants.YES);
				} else {
					familyPlanTask.setIndCpsAssigned(ServiceConstants.NO);
				}
				if (taskGoalValueDto.getFamilyAssignedInd()) {
					familyPlanTask.setIndFamilyAssigned(ServiceConstants.YES);
				} else {
					familyPlanTask.setIndFamilyAssigned(ServiceConstants.NO);
				}
				if (taskGoalValueDto.getParentsAssignedInd()) {
					familyPlanTask.setIndParentsAssigned(ServiceConstants.YES);
				} else {
					familyPlanTask.setIndParentsAssigned(ServiceConstants.NO);
				}
				if (taskGoalValueDto.getCpsCourtOrderedInd()) {
					familyPlanTask.setIndCpsOrdered(ServiceConstants.YES);
				} else {
					familyPlanTask.setIndCpsOrdered(ServiceConstants.NO);
				}
				if (taskGoalValueDto.getFamilyCourtOrderedInd()) {
					familyPlanTask.setIndFamilyOrdered(ServiceConstants.YES);
				} else {
					familyPlanTask.setIndFamilyOrdered(ServiceConstants.NO);
				}
				if (taskGoalValueDto.getParentsCourtOrderedInd()) {
					familyPlanTask.setIndParentsOrdered(ServiceConstants.YES);
				} else {
					familyPlanTask.setIndParentsOrdered(ServiceConstants.NO);
				}
				if (taskGoalValueDto.getDateCreated() != null) {
					familyPlanTask.setDtCreated(taskGoalValueDto.getDateCreated());
				}
				if (taskGoalValueDto.getTaskCompleted()) {
					familyPlanTask.setDtCompleted(new Date());
				}

				if (taskGoalValueDto.getNoLongerNeeded()) {
					familyPlanTask.setDtNoLongerNeeded(new Date());
				}

				if (taskGoalValueDto.getTaskCompleted()) {
					familyPlanTask.setIdCompletedEvent(currentEventId);
				}

				if (taskGoalValueDto.getNoLongerNeeded()) {
					familyPlanTask.setIdCompletedEvent(currentEventId);
				}

				if (taskGoalValueDto.getTaskCompleted() == false && taskGoalValueDto.getNoLongerNeeded() == false) {
					familyPlanTask.setIdCompletedEvent(null);
				}
				familyPlanTask.setDtLastUpdate(new Date());
				sessionFactory.getCurrentSession().saveOrUpdate(familyPlanTask);
				sessionFactory.getCurrentSession().flush();
				List<FamilyPlanGoalValueDto> familyPlanGoalValueDtos = taskGoalValueDto.getFamilyPlanGoalValueDtos();
				// insert into the goals link table(FAM_PLN_TASK_GOAL_LINK)
				if (familyPlanGoalValueDtos != null) {
					for (FamilyPlanGoalValueDto familyPlanGoalValueDto : familyPlanGoalValueDtos) {
						FamPlnTaskGoalLink famPlnTaskGoalLink = new FamPlnTaskGoalLink();
						if (!TypeConvUtil.isNullOrEmpty(familyPlanGoalValueDto.getSelected())
								&& familyPlanGoalValueDto.getSelected()) {
							if (!TypeConvUtil.isNullOrEmpty(familyPlanGoalValueDto.getEventId())) {
								Event event = new Event();
								event.setIdEvent(taskGoalValueDto.getEventId());
								famPlnTaskGoalLink.setEvent(event);
							}
							if (!TypeConvUtil.isNullOrEmpty(familyPlanGoalValueDto.getCaseId())) {
								CapsCase capsCase = new CapsCase();
								capsCase.setIdCase(taskGoalValueDto.getCaseId());
								famPlnTaskGoalLink.setCapsCase(capsCase);
							}
							if (!TypeConvUtil.isNullOrEmpty(familyPlanTask)) {
								famPlnTaskGoalLink.setFamilyPlanTask(familyPlanTask);
							}
							famPlnTaskGoalLink.setIdFamilyPlanGoal(familyPlanGoalValueDto.getFamilyPlanGoalId());
							sessionFactory.getCurrentSession().saveOrUpdate(famPlnTaskGoalLink);
						}
					}
				}
			}
		}

		return familyPlanRes;

	}

	/**
	 * Method Name: updateFamilyPlanTask Method Description: Updates information
	 * in the FAMILY_PLAN_TASK table
	 *
	 * @param taskGoalValueDtos
	 *            - list of tasks with goals to update
	 * @param currentEventId
	 *            - event id
	 * @throws DataNotFoundException
	 *             - record not found exception
	 */
	@Override
	public FamilyPlanRes updateFamilyPlanTask(List<TaskGoalValueDto> taskGoalValueDtos, Long currentEventId)
			throws DataNotFoundException {
		FamilyPlanRes familyPlanRes = new FamilyPlanRes();

		for (TaskGoalValueDto taskGoalValueDto : taskGoalValueDtos) {

			FamilyPlanTask familyPlanTask;
			Criteria cr = sessionFactory.getCurrentSession().createCriteria(FamilyPlanTask.class);
			cr.add(Restrictions.eq("idFamilyPlanTask", taskGoalValueDto.getFamilyPlanTaskId()));
			familyPlanTask = (FamilyPlanTask) cr.uniqueResult();

			if (!ObjectUtils.isEmpty(familyPlanTask)) {
				if (!familyPlanTask.getDtLastUpdate().equals(taskGoalValueDto.getDateLastUpdate())) {
					ErrorDto errorDto = new ErrorDto();
					errorDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
					familyPlanRes.setErrorDto(errorDto);
				}
				if (taskGoalValueDto.getFamilyCourtOrderedInd()) {
					familyPlanTask.setIndCourtOrdered(ServiceConstants.YES);
				} else {
					familyPlanTask.setIndCourtOrdered(ServiceConstants.NO);
				}

				familyPlanTask.setTxtTask(taskGoalValueDto.getTaskTxt());

				if (taskGoalValueDto.getDateCreated() != null) {
					familyPlanTask.setDtCreated(taskGoalValueDto.getDateCreated());
				}

				if (taskGoalValueDto.getCpsAssignedInd()) {
					familyPlanTask.setIndCpsAssigned(ServiceConstants.YES);
				} else {
					familyPlanTask.setIndCpsAssigned(ServiceConstants.NO);
				}
				if (taskGoalValueDto.getFamilyAssignedInd()) {
					familyPlanTask.setIndFamilyAssigned(ServiceConstants.YES);
				} else {
					familyPlanTask.setIndFamilyAssigned(ServiceConstants.NO);
				}

				if (taskGoalValueDto.getParentsAssignedInd()) {
					familyPlanTask.setIndParentsAssigned(ServiceConstants.YES);
				} else {
					familyPlanTask.setIndParentsAssigned(ServiceConstants.NO);
				}

				if (taskGoalValueDto.getCpsCourtOrderedInd()) {
					familyPlanTask.setIndCpsOrdered(ServiceConstants.YES);
				} else {
					familyPlanTask.setIndCpsOrdered(ServiceConstants.NO);
				}

				if (taskGoalValueDto.getFamilyCourtOrderedInd()) {
					familyPlanTask.setIndFamilyOrdered(ServiceConstants.YES);
				} else {
					familyPlanTask.setIndFamilyOrdered(ServiceConstants.NO);
				}

				if (taskGoalValueDto.getParentsCourtOrderedInd()) {
					familyPlanTask.setIndParentsOrdered(ServiceConstants.YES);
				} else {
					familyPlanTask.setIndParentsOrdered(ServiceConstants.NO);
				}

				if (!ObjectUtils.isEmpty(taskGoalValueDto.getTaskCompleted()) && 
						taskGoalValueDto.getTaskCompleted()) {
					familyPlanTask.setDtCompleted(new Date());
					familyPlanTask.setDtNoLongerNeeded(null);
				}

				if (!ObjectUtils.isEmpty(taskGoalValueDto.getNoLongerNeeded())
						&& taskGoalValueDto.getNoLongerNeeded()) {
					familyPlanTask.setDtNoLongerNeeded(new Date());
					familyPlanTask.setDtCompleted(null);
				}

				if (!ObjectUtils.isEmpty(taskGoalValueDto.getTaskCompleted())
						&& taskGoalValueDto.getTaskCompleted()) {
					familyPlanTask.setIdCompletedEvent(currentEventId);
				}
				if (!ObjectUtils.isEmpty(taskGoalValueDto.getNoLongerNeeded())
						&& taskGoalValueDto.getNoLongerNeeded()) {
					familyPlanTask.setIdCompletedEvent(currentEventId);
				}

				if (!ObjectUtils.isEmpty(taskGoalValueDto.getTaskCompleted()) && !ObjectUtils.isEmpty(taskGoalValueDto.getNoLongerNeeded()) && 
						taskGoalValueDto.getTaskCompleted() == false && taskGoalValueDto.getNoLongerNeeded() == false) {
					familyPlanTask.setIdCompletedEvent(null);
					familyPlanTask.setDtNoLongerNeeded(null);
					familyPlanTask.setDtCompleted(null);
				}
				sessionFactory.getCurrentSession().saveOrUpdate(familyPlanTask);
			}
		}
		return familyPlanRes;

	}

	/**
	 * Method Name: updateTaskGoalLink Method Description: Updates information
	 * in the FAM_PLN_TASK_GOAL_LINK table
	 *
	 * @param taskGoalValueDtos
	 *            - list of tasks with goals to update
	 * @throws DataNotFoundException
	 *             - record not found exception
	 */
	@Override
	public FamilyPlanRes updateTaskGoalLink(List<TaskGoalValueDto> taskGoalValueDtos) throws DataNotFoundException {
		FamilyPlanRes familyPlanRes = new FamilyPlanRes();
		for (TaskGoalValueDto taskGoalValueDto : taskGoalValueDtos) {
			deleteTaskGoalLink(taskGoalValueDto.getFamilyPlanTaskId());
			List<FamilyPlanGoalValueDto> familyPlanGoalValueDtos = taskGoalValueDto.getFamilyPlanGoalValueDtos();
			for (FamilyPlanGoalValueDto familyPlanGoalValueDto : familyPlanGoalValueDtos) {
				if (!TypeConvUtil.isNullOrEmpty(familyPlanGoalValueDto.getSelected())
						&& familyPlanGoalValueDto.getSelected()) {
					FamPlnTaskGoalLink famPlnTaskGoalLink = new FamPlnTaskGoalLink();
					if (!TypeConvUtil.isNullOrEmpty(familyPlanGoalValueDto.getEventId())) {
						Event event = new Event();
						event.setIdEvent(taskGoalValueDto.getEventId());
						famPlnTaskGoalLink.setEvent(event);
					}
					if (!TypeConvUtil.isNullOrEmpty(familyPlanGoalValueDto.getCaseId())) {
						CapsCase capsCase = new CapsCase();
						capsCase.setIdCase(taskGoalValueDto.getCaseId());
						famPlnTaskGoalLink.setCapsCase(capsCase);
					}
					if (!TypeConvUtil.isNullOrEmpty(taskGoalValueDto.getFamilyPlanTaskId())) {
						FamilyPlanTask familyPlanTask;
						Criteria cr = sessionFactory.getCurrentSession().createCriteria(FamilyPlanTask.class);
						cr.add(Restrictions.eq("idFamilyPlanTask", taskGoalValueDto.getFamilyPlanTaskId()));
						familyPlanTask = (FamilyPlanTask) cr.uniqueResult();
						famPlnTaskGoalLink.setFamilyPlanTask(familyPlanTask);
					}
					famPlnTaskGoalLink.setIdFamilyPlanGoal(familyPlanGoalValueDto.getFamilyPlanGoalId());
					sessionFactory.getCurrentSession().saveOrUpdate(famPlnTaskGoalLink);
				}
			}
		}

		return familyPlanRes;

	}

	/**
	 * Method Name: updateFamilPlanActiveTasks Method Description: Updates
	 * information in the FAMILY_PLAN_TASK table
	 *
	 * @param activeUpdateTaskGoalDtos
	 *            - list of tasks with goals to update
	 * @param currentEventId
	 *            - event id
	 * @throws DataNotFoundException
	 *             - record not found exception
	 */
	@Override
	public FamilyPlanRes updateFamilPlanActiveTasks(List<TaskGoalValueDto> familyPlanTaskList, Long currentEventId)
			throws DataNotFoundException {
		Iterator<TaskGoalValueDto> itr = familyPlanTaskList.iterator();
		FamilyPlanRes familyPlanRes = new FamilyPlanRes();
		while (itr.hasNext()) {
			TaskGoalValueDto taskGoalValueDto = (TaskGoalValueDto) itr.next();
			FamilyPlanTask familyPlanTask;
			Criteria cr = sessionFactory.getCurrentSession().createCriteria(FamilyPlanTask.class);
			cr.add(Restrictions.eq("idFamilyPlanTask", taskGoalValueDto.getFamilyPlanTaskId()))
					.add(Restrictions.eq("dtLastUpdate", taskGoalValueDto.getDateLastUpdate()));
			familyPlanTask = (FamilyPlanTask) cr.uniqueResult();
			if (!ObjectUtils.isEmpty(familyPlanTask)) {
				if (!ObjectUtils.isEmpty(taskGoalValueDto.getNoLongerNeeded())
						&& taskGoalValueDto.getNoLongerNeeded()) {
					familyPlanTask.setDtNoLongerNeeded(new Date());
				} else {
					familyPlanTask.setDtNoLongerNeeded(null);
				}
				if (!ObjectUtils.isEmpty(taskGoalValueDto.getTaskCompleted()) && taskGoalValueDto.getTaskCompleted()) {
					familyPlanTask.setDtCompleted(new Date());
				} else {
					familyPlanTask.setDtCompleted(null);
				}
				if ((!ObjectUtils.isEmpty(taskGoalValueDto.getNoLongerNeeded())
						&& !ObjectUtils.isEmpty(taskGoalValueDto.getTaskCompleted()))
						&& taskGoalValueDto.getNoLongerNeeded() == false
						&& taskGoalValueDto.getTaskCompleted() == false) {
					familyPlanTask.setIdCompletedEvent(null);
				} else {
					familyPlanTask.setIdCompletedEvent(currentEventId);
				}
				sessionFactory.getCurrentSession().saveOrUpdate(familyPlanTask);

			}
		}
		return familyPlanRes;

	}

	/**
	 * Method Name: deleteTaskGoalLink Method Description: Deletes a row from
	 * the FAM_PLN_TASK_GOAL_LINK table
	 *
	 * @param idFamilyPlanTask
	 *            id - of FAMILY_PLAN_TASK
	 * @return FamilyPlanRes - FamilyPan response
	 * @throws DataNotFoundException
	 *             - record not found exception
	 */
	@Override
	public FamilyPlanRes deleteTaskGoalLink(Long idFamilyPlanTask) throws DataNotFoundException {
		FamilyPlanRes familyPlanRes = new FamilyPlanRes();
		List<FamPlnTaskGoalLink> famPlnTaskGoalLinks;
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(FamPlnTaskGoalLink.class);
		cr.add(Restrictions.eq("familyPlanTask.idFamilyPlanTask", idFamilyPlanTask));
		famPlnTaskGoalLinks = (List<FamPlnTaskGoalLink>) cr.list();
		famPlnTaskGoalLinks.stream().forEach(famPlnTaskGoalLink -> {
			if (!ObjectUtils.isEmpty(famPlnTaskGoalLink)) {
				sessionFactory.getCurrentSession().delete(famPlnTaskGoalLink);
			} else {
				ErrorDto errorDto = new ErrorDto();
				errorDto.setErrorMsg(messageSource.getMessage("common.data.emptyset", null, Locale.US));
				familyPlanRes.setErrorDto(errorDto);
			}
		});
		return familyPlanRes;
	}

	/**
	 * Method Name: deleteNewFamilyPlanTask Method Description: Deletes a row
	 * from the FAMILY_PLAN_TASK table
	 *
	 * @param idFamilyPlanTask
	 *            - primary key of FAMILY_PLAN_TASK
	 * @throws DataNotFoundException
	 *             - record not found exception
	 */
	@Override
	public FamilyPlanRes deleteNewFamilyPlanTask(Long idFamilyPlanTask) throws DataNotFoundException {
		FamilyPlanRes familyPlanRes = new FamilyPlanRes();
		if (idFamilyPlanTask != 0) {
			FamilyPlanTask familyPlanTask;
			Criteria cr = sessionFactory.getCurrentSession().createCriteria(FamilyPlanTask.class);
			cr.add(Restrictions.eq("idFamilyPlanTask", idFamilyPlanTask));
			familyPlanTask = (FamilyPlanTask) cr.uniqueResult();
			if (!ObjectUtils.isEmpty(familyPlanTask)) {
				sessionFactory.getCurrentSession().delete(familyPlanTask);
			} else {
				ErrorDto errorDto = new ErrorDto();
				errorDto.setErrorMsg(messageSource.getMessage("common.data.emptyset", null, Locale.US));
				familyPlanRes.setErrorDto(errorDto);
			}
		}
		return familyPlanRes;
	}

	/**
	 * Method Name: deleteFamilyPlanTask Method Description: Deletes the
	 * specified Family Plan Task from the database.
	 * 
	 * @param familyPlanTaskDto
	 *            - FamilyPlanTaskDto input dto
	 * @return FamilyPlanRes - FamilyPan response
	 */
	@Override
	public FamilyPlanRes deleteFamilyPlanTask(FamilyPlanTaskDto familyPlanTaskDto) {
		FamilyPlanRes familyPlanRes = new FamilyPlanRes();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FamilyPlanTask.class);
		criteria.add(Restrictions.eq("idFamilyPlanTask", familyPlanTaskDto.getIdFamilyPlanTask()));
		criteria.add(Restrictions.eq("dtLastUpdate", familyPlanTaskDto.getDtLastUpdate()));
		FamilyPlanTask familyPlanTask = (FamilyPlanTask) criteria.uniqueResult();
		if (!ObjectUtils.isEmpty(familyPlanTask)) {
			sessionFactory.getCurrentSession().delete(familyPlanTask);
		} else {
			ErrorDto errorDto = new ErrorDto();
			errorDto.setErrorMsg(messageSource.getMessage("common.data.emptyset", null, Locale.US));
			familyPlanRes.setErrorDto(errorDto);
		}
		return familyPlanRes;

	}

	/**
	 * 
	 * Method Name: fetchEmployeeEmail Method Description:This Method is used
	 * for fetching the primary and secondary case-workers employee email
	 * addresses based on the event id List
	 * 
	 * @param idEventList
	 * @return List<EmailDetailsDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EmailDetailsDto> fetchEmployeeEmail(Long idEvent, String cdStage) {
		// Creating the New List of EmailDetailsDto to be sent as response

		List<EmailDetailsDto> emailDetailsDtoList = new ArrayList<EmailDetailsDto>();
		// creating the SQL query to fetch the list of emailDTO's
		if (ServiceConstants.CSTAGES_FPR.equals(cdStage)) {
			emailDetailsDtoList = (List<EmailDetailsDto>) ((SQLQuery) sessionFactory.getCurrentSession()
					.createSQLQuery(getEmailAddressSql).setParameter("idEvent", idEvent))
							.addScalar("emailAddress", StandardBasicTypes.STRING)
							.addScalar("stageName", StandardBasicTypes.STRING)
							.addScalar("idStage", StandardBasicTypes.LONG).addScalar("idEvent", StandardBasicTypes.LONG)
							.setResultTransformer(Transformers.aliasToBean(EmailDetailsDto.class)).list();
		} else if (ServiceConstants.FSU_FRE.equals(cdStage) || ServiceConstants.CSTAGES_FRE.equals(cdStage)
				|| ServiceConstants.CSTAGES_FSU.equals(cdStage)) {
			// creating the SQL query to fetch the list of emailDTO's
			emailDetailsDtoList = (List<EmailDetailsDto>) ((SQLQuery) sessionFactory.getCurrentSession()
					.createSQLQuery(getEmailAddressForFSUFRESql).setParameter("idEvent", idEvent))
							.addScalar("emailAddress", StandardBasicTypes.STRING)
							.addScalar("stageName", StandardBasicTypes.STRING)
							.addScalar("idStage", StandardBasicTypes.LONG).addScalar("idEvent", StandardBasicTypes.LONG)
							.setResultTransformer(Transformers.aliasToBean(EmailDetailsDto.class)).list();
		}

		return emailDetailsDtoList;
	}

	/**
	 * 
	 * Method Name: getFamilyPlanReviewDate Method Description:This Method is
	 * used for fetching the Next ReviewDate for Family plan if not exist then
	 * from Family Plan Evaluation.
	 * 
	 * @param idEvent
	 * @return reviewDate
	 */
	@Override
	public CapsEmailDto getFamilyPlanReviewDetail(Long idEvent, String trigger) {
		Long idParticipant = null;
		CapsEmailDto capsEmailDto = new CapsEmailDto();
		Long idEventFamilyPlan = null;
		// Getting the Approval Event Link record to fetch the Id Event of that
		// approval.
		if (APPROVAL.equalsIgnoreCase(trigger)) {
			ApprovalEventLink approvalEventLink = (ApprovalEventLink) sessionFactory.getCurrentSession()
					.createCriteria(ApprovalEventLink.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.add(Restrictions.eq("idApproval", idEvent)).uniqueResult();
			idEventFamilyPlan = approvalEventLink.getIdEvent();
		} else if (ASSIGN.equalsIgnoreCase(trigger)) {
			idEventFamilyPlan = idEvent;
		}
		// Checking if the Event belongs to Family Plan
		FamilyPlan familyPlan = (FamilyPlan) sessionFactory.getCurrentSession().createCriteria(FamilyPlan.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.add(Restrictions.eq("event.idEvent", idEventFamilyPlan)).uniqueResult();

		if (ObjectUtils.isEmpty(familyPlan)) {
			// If the Family plan is coming empty for the given id Event means
			// this belongs to
			FamilyPlanEval familyPlanEval = (FamilyPlanEval) sessionFactory.getCurrentSession()
					.createCriteria(FamilyPlanEval.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.add(Restrictions.eq("eventByIdEvent.idEvent", idEventFamilyPlan)).uniqueResult();
			if (!ObjectUtils.isEmpty(familyPlanEval)) {
				capsEmailDto.setDtNxtReviewDate(familyPlanEval.getDtNextReview());
				// Getting the primary participant details for the family plan
				if (!ObjectUtils.isEmpty(familyPlanEval.getCpsFsna())
						&& !ObjectUtils.isEmpty(familyPlanEval.getCpsFsna().getIdPrmryCrgvrPrnt())) {
					// If the primary care giver parent id is available then
					// thats the primary participant
					idParticipant = familyPlanEval.getCpsFsna().getIdPrmryCrgvrPrnt();
				} else if (!ObjectUtils.isEmpty(familyPlanEval.getCpsFsna())) {
					// Else getting the secondary care giver parent id.
					idParticipant = familyPlanEval.getCpsFsna().getIdSecndryCrgvrPrnt();
				}
			}
		} else {
			capsEmailDto.setDtNxtReviewDate(familyPlan.getDtNextReview());
			// Getting the primary participant details for the family plan
			if (!ObjectUtils.isEmpty(familyPlan.getCpsFsna())
					&& !ObjectUtils.isEmpty(familyPlan.getCpsFsna().getIdPrmryCrgvrPrnt())) {
				// If the primary care giver parent id is available then thats
				// the primary participant
				idParticipant = familyPlan.getCpsFsna().getIdPrmryCrgvrPrnt();
			} else if (!ObjectUtils.isEmpty(familyPlan.getCpsFsna())) {
				// Else getting the secondary care giver parent id.
				idParticipant = familyPlan.getCpsFsna().getIdSecndryCrgvrPrnt();
			}
		}
		// Getting the primary participant care giver detail to get the Full
		// name.
		if (!ObjectUtils.isEmpty(idParticipant)) {
			Person person = personDao.getPersonByPersonId(idParticipant);
			capsEmailDto.setNmParticipant(person.getNmPersonFull());
		}
		log.info("getFamilyPlanReviewDetail Method in CapsCaseDaoImpl : Returned Next Review Date");
		return capsEmailDto;
	}

	/**
	 * 
	 * Method Name: addEventPersonLinkOnPlan Method Description: Adds
	 * IND_FAM_PLAN_PRINCIPAL and/or IND_FAM_PLAN_PART into the event person
	 * link table
	 * 
	 * @param idPerson
	 * @param idEvent
	 * @param idCase
	 * @param indPrn
	 * @param indPart
	 */
	public void addEventPersonLinkOnPlan(Long idPerson, Long idEvent, Long idCase, String indPrn, String indPart) {
		Event event = (Event) sessionFactory.getCurrentSession().createCriteria(Event.class)
				.add(Restrictions.eq("idEvent", idEvent)).uniqueResult();
		EventPersonLink eventPersonLink = new EventPersonLink();
		Person person = (Person) sessionFactory.getCurrentSession().createCriteria(Person.class)
				.add(Restrictions.eq("idPerson", idPerson)).uniqueResult();
		eventPersonLink.setPerson(person);
		eventPersonLink.setEvent(event);
		eventPersonLink.setIdCase(idCase);
		eventPersonLink.setIndFamPlanPrincipal(indPrn);
		eventPersonLink.setIndFamPlanPart(indPart);
		eventPersonLink.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().save(eventPersonLink);
	}

	/**
	 * 
	 * Method Name: saveParticpantsOnPlan Method Description: Update
	 * IND_FAM_PLAN_PRINCIPAL and/or IND_FAM_PLAN_PART into the event person
	 * link table
	 * 
	 * @param eventPersonLink
	 * @param indPrn
	 * @param indPart
	 */
	public void saveParticpantsOnPlan(EventPersonLink eventPersonLink, String indPrn, String indPart) {
		eventPersonLink.setIndFamPlanPrincipal(indPrn);
		eventPersonLink.setIndFamPlanPart(indPart);
		eventPersonLink.setDtLastUpdate(new Date());
		sessionFactory.getCurrentSession().save(eventPersonLink);
	}

	/**
	 * 
	 * Method Name: deleteParticpantsOnPlan Method Description: Delete
	 * Particpant from EVENT_PERSON_LINK table
	 * 
	 * @param eventPersonLink
	 */
	public void deleteParticpantsOnPlan(EventPersonLink eventPersonLink) {
		sessionFactory.getCurrentSession().delete(eventPersonLink);
	}

	@Override
	public FamilyPlan getFamilyPlan(Long idFamilyPlan) {
		FamilyPlan familyPlan = (FamilyPlan) sessionFactory.getCurrentSession().createCriteria(FamilyPlan.class)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).add(Restrictions.eq("event.idEvent", idFamilyPlan))
				.uniqueResult();
		return familyPlan;
	}

	@Override
	public FamilyPlanEval getFamilyPlanEval(Long idFamilyPlanEval) {
		FamilyPlanEval familyPlanEval = (FamilyPlanEval) sessionFactory.getCurrentSession()
				.createCriteria(FamilyPlanEval.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.add(Restrictions.eq("eventByIdEvent.idEvent", idFamilyPlanEval)).uniqueResult();
		return familyPlanEval;
	}

	/* (non-Javadoc)
	 * @see us.tx.state.dfps.service.familyplan.dao.FamilyPlanDao#geteventPersonLinkDtl(java.lang.Long, java.lang.Long)
	 */
	@Override
	public EventPersonLink geteventPersonLinkDtl(Long idEvent, Long personId) {
		EventPersonLink eventPersonLink = (EventPersonLink) sessionFactory.getCurrentSession()
		.createCriteria(EventPersonLink.class).add(Restrictions.eq("event.idEvent", idEvent))
		.add(Restrictions.eq("person.idPerson", personId)).uniqueResult();
		return eventPersonLink;
		
	}
	
	/**
	 * 
	 *Method Name:	getFamilyplanDtLastUpdate
	 *Method Description: This method gets the last update date for family plan
	 *@param idEvent
	 *@return
	 */
	@Override
	public Date getFamilyplanDtLastUpdate(Long idEvent) {
		FamilyPlan familyPlan = (FamilyPlan) sessionFactory.getCurrentSession().createCriteria(FamilyPlan.class)
				.add(Restrictions.eq("event.idEvent", idEvent)).uniqueResult();
		return familyPlan.getDtLastUpdate();
	}
	
	/**
	 * 
	 *Method Name:	getFamilyplanEvalDtLastUpdate
	 *Method Description: This method gets the last update date for family plan Eval
	 *@param idEvent
	 *@return
	 */
	@Override
	public Date getFamilyplanEvalDtLastUpdate(Long idEvent) {
		FamilyPlanEval familyPlanEval = (FamilyPlanEval) sessionFactory.getCurrentSession().createCriteria(FamilyPlanEval.class)
				.add(Restrictions.eq("eventByIdEvent.idEvent", idEvent)).uniqueResult();
		return familyPlanEval.getDtLastUpdate();
	}
}
