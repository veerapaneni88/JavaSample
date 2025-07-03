/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Aug 23, 2017- 10:59:50 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.approval.daoimpl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
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
import us.tx.state.dfps.approval.dto.ApprovalStatusFacilityIndicatorDto;
import us.tx.state.dfps.common.domain.Approvers;
import us.tx.state.dfps.common.domain.CapsCase;
import us.tx.state.dfps.common.domain.CaseFileManagement;
import us.tx.state.dfps.common.domain.CpsInvstDetail;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.EventPersonLink;
import us.tx.state.dfps.common.domain.Office;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.SsccList;
import us.tx.state.dfps.common.domain.SsccReferral;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.domain.StagePersonLink;
import us.tx.state.dfps.common.domain.Todo;
import us.tx.state.dfps.common.domain.Unit;
import us.tx.state.dfps.common.domain.Workload;
import us.tx.state.dfps.common.dto.BoardEmailDto;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.dto.EmployeeDetailDto;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtValueDto;
import us.tx.state.dfps.service.approval.dao.ApprovalStatusDao;
import us.tx.state.dfps.service.casepackage.dto.CaseFileManagementDto;
import us.tx.state.dfps.service.casepackage.dto.SSCCListDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.SaveApprovalStatusReq;
import us.tx.state.dfps.service.common.response.ApprovalStatusRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.riskreasmnt.SDMRiskReasmntDto;
import us.tx.state.dfps.service.workload.dto.ApprovalPersonPrintDto;
import us.tx.state.dfps.service.workload.dto.ApproveApprovalDto;
import us.tx.state.dfps.service.workload.dto.ApproversDto;
import us.tx.state.dfps.service.workload.dto.RejectApprovalDto;
import us.tx.state.dfps.service.workload.dto.SecondaryApprovalDto;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.service.workload.dto.TodoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Aug 23, 2017- 10:59:50 AM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class ApprovalStatusDaoImpl implements ApprovalStatusDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ApprovalStatusDaoImpl.getApproval}")
	private String getApprovalSql;

	@Value("${ApprovalStatusDaoImpl.getStageChildAge}")
	private String getStageChildAgeSql;

	@Value("${ApprovalStatusDaoImpl.getFinalRiskLevelForStage}")
	private String getFinalRiskLevelForStageSql;

	@Value("${ApprovalStatusDaoImpl.getLatestSafetyDecision}")
	private String getLatestSafetyDecisionSql;

	@Value("${ApprovalStatusDaoImpl.getDayCareApproval}")
	private String getDayCareApprovalsql;

	@Value("${DayCareRequestDaoImpl.getSSCCReferalForIdPersonDC}")
	private String getSSCCReferalForIdPersonDC;

	@Value("${ApprovalStatusDaoImpl.getSSCCReferralForIdPersonPALorSUB}")
	private String getSSCCReferralForIdPersonPALorSUBSql;

	@Value("${ApprovalStatusDaoImpl.getSSCCReferralFamilyForIdPerson}")
	private String getSSCCReferralFamilyForIdPersonSql;

	@Value("${ApprovalStatusDaoImpl.getVendorId}")
	private String getVendorIdSql;

	@Value("${ApprovalStatusDaoImpl.isVendorIdExistsBatchParameters}")
	private String isVendorIdExistsBatchParametersSql;

	@Value("${ApprovalStatusDaoImpl.updateSSCCReferralFamilySql}")
	private String updateSSCCReferralFamilySql;

	@Value("${ApprovalStatusDaoImpl.updateEmrStatusSql}")
	private String updateEmrStatusSql;

	@Value("${ApprovalStatusDaoImpl.fetchPADStageIdForADOSql}")
	private String fetchPADStageIdForADOSql;

	@Value("${ApprovalStatusDaoImpl.queryStageChildrenDOB}")
	private String querydob;

	@Value("${ApprovalStatusDaoImpl.isChildDeath}")
	private String isChildDeathsql;

	@Value("${ApprovalStatusDaoImpl.getPriorStage}")
	private String getPriorStageSql;

	@Value("${ApprovalStatusDaoImpl.getCcmn56DO}")
	private String getCcmn56DOSql;

	@Value("${ApprovalStatusDaoImpl.getSelectCaseFileManagement}")
	private String getSelectCaseFileManagement;

	@Value("${ApprovalStatusDaoImpl.getARSafetyAssmt}")
	private String getARSafetyAssmtSql;

	@Value("${ApprovalStatusDaoImpl.updateFacilityIndicator}")
	private String updateFacilityIndicatorsql;

	@Value("${ApprovalStatusDaoImpl.getEmployeeInfo}")
	private String getEmployeeInfosql;

	@Value("${ApprovalStatusDaoImpl.updateIndSecondApprover}")
	private String updateIndSecondApproversql;

	@Value("${ApprovalStatusDaoImpl.updateApprovers}")
	private String updateApproverssql;

	@Value("${ApprovalStatusDaoImpl.insertApprovalRejection}")
	private String insertApprovalRejectionSql;

	@Value("${ApprovalStatusDaoImpl.getICPCApprovalLevel}")
	private String getICPCApprovalLevelsql;

	@Value("${ApprovalStatusDaoImpl.getBoardEmail}")
	private String getBoardEmailsql;

	@Value("${ApprovalStatusDaoImpl.fetchPriorOrProgressedStage}")
	private String fetchPriorOrProgressedStage;

	@Value("${ApprovalStatusDaoImpl.retrieveFbssSdmRiskReassessment}")
	private String retrieveFbssSdmRiskReassessment;

	@Value("${ApprovalStatusDaoImpl.deleteAutEventSql}")
	private String deleteAutEventSql;

	@Autowired
	MessageSource messageSource;

	private static final Logger log = Logger.getLogger(ApprovalStatusDaoImpl.class);

	public ApprovalStatusDaoImpl() {

	}

	/**
	 * Method Name: saveApproval Method Description:This method is used to save
	 * the assign staff to print closure notifications in approval status page
	 * 
	 * @param approvalPersonDto
	 * @return @
	 */
	public ApprovalPersonPrintDto saveApproval(ApprovalPersonPrintDto approvalPersonDto) {
		Criteria criteriaEventPersonLink = sessionFactory.getCurrentSession().createCriteria(EventPersonLink.class);
		criteriaEventPersonLink.add(Restrictions.eq("event.idEvent", approvalPersonDto.getIdEvent()));
		criteriaEventPersonLink.add(Restrictions.eq("person.idPerson", approvalPersonDto.getIdPerson()));
		criteriaEventPersonLink.add(Restrictions.eq("idCase", approvalPersonDto.getIdCase()));
		EventPersonLink eventPersonLink = (EventPersonLink) criteriaEventPersonLink.uniqueResult();
		if (eventPersonLink == null) {
			EventPersonLink epl = new EventPersonLink();
			epl.setDtLastUpdate(new Date());
			epl.setIdCase(approvalPersonDto.getIdCase());
			Person p = (Person) sessionFactory.getCurrentSession().get(Person.class, approvalPersonDto.getIdPerson());
			epl.setPerson(p);
			Event e = (Event) sessionFactory.getCurrentSession().get(Event.class, approvalPersonDto.getIdEvent());
			epl.setEvent(e);
			Long idEventPersLink = (Long) sessionFactory.getCurrentSession().save(epl);
			approvalPersonDto.setIdEventPersLink(idEventPersLink);
		}
		return approvalPersonDto;
	}

	/**
	 * Method Name: getApproval Method Description:This method is to retrieve
	 * the name of assign staff to print in approval status page
	 * 
	 * @param approvalPersonDto
	 * @return @
	 */
	@Override
	public ApprovalPersonPrintDto getApproval(ApprovalPersonPrintDto approvalPersonDto) {
		Person person = null;
		Long idEvent = approvalPersonDto.getIdEvent();
		ApprovalPersonPrintDto approvalPersonPrintDto = null;
		if (!TypeConvUtil.isNullOrEmpty(approvalPersonDto.getIdPerson())) {
			approvalPersonPrintDto = (ApprovalPersonPrintDto) sessionFactory.getCurrentSession()
					.createSQLQuery(getApprovalSql).addScalar("nmFullPerson", StandardBasicTypes.STRING)
					.addScalar("idPerson", StandardBasicTypes.LONG)
					.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
					.setResultTransformer(Transformers.aliasToBean(ApprovalPersonPrintDto.class))
					.setParameter("idPerson", approvalPersonDto.getIdPerson()).uniqueResult();
		}
		if (!ObjectUtils.isEmpty(approvalPersonPrintDto)) {
			approvalPersonDto.setNmFullPerson(approvalPersonPrintDto.getNmFullPerson());
			approvalPersonDto.setIdPerson(approvalPersonPrintDto.getIdPerson());
			approvalPersonDto.setDtLastUpdate(approvalPersonPrintDto.getDtLastUpdate());
		} else {
			Criteria criteriaEventPersonLink = sessionFactory.getCurrentSession().createCriteria(EventPersonLink.class);
			criteriaEventPersonLink.add(Restrictions.eq("event.idEvent", idEvent));
			EventPersonLink eventPersonLink = (EventPersonLink) criteriaEventPersonLink.uniqueResult();
			if (eventPersonLink != null) {
				person = (Person) sessionFactory.getCurrentSession().load(Person.class,
						eventPersonLink.getPerson().getIdPerson());
				approvalPersonDto.setNmFullPerson(person.getNmPersonFull());
				approvalPersonDto.setIdPerson(person.getIdPerson());
				approvalPersonDto.setDtLastUpdate(person.getDtLastUpdate());
			}
		}
		return approvalPersonDto;
	}

	/**
	 * *Method Name: getStageChildAgeList Method Description:This method is to
	 * retrieve the information of under aged child for the stage.
	 * 
	 * @param idCase
	 * @param idStage
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Date> getStageChildAgeList(Long idCase, Long idStage) {
		return sessionFactory.getCurrentSession().createSQLQuery(getStageChildAgeSql)
				.addScalar("dob", StandardBasicTypes.DATE).setParameter("idCase", idCase)
				.setParameter("idStage", idStage).setParameter("cdStagePersType", CodesConstant.CPRSNTYP_PRN).list();

	}

	/**
	 * *Method Name: getFinalRiskLevelForStage Method Description:This method is
	 * to retrieve the final risk level for the stage.
	 *
	 * @param idCase
	 * @param idStage
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getFinalRiskLevelForStage(Long idStage) {
		return sessionFactory.getCurrentSession().createSQLQuery(getFinalRiskLevelForStageSql)
				.addScalar("finalRiskLevel", StandardBasicTypes.STRING).setParameter("hI_idStage", idStage).list();

	}

	/**
	 * *Method Name: getLatestSafetyDecision Method Description:This method is
	 * to retrieve the Latest safety decision for Stage.
	 * 
	 * @param idCase
	 * @param idStage
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getLatestSafetyDecision(Long idStage) {
		List<String> safetyDecision = sessionFactory.getCurrentSession().createSQLQuery(getLatestSafetyDecisionSql)
				.addScalar("safetyDecision", StandardBasicTypes.STRING).setParameter("hI_idStage", idStage).list();
		return !ObjectUtils.isEmpty(safetyDecision) ? safetyDecision.get(0) : ServiceConstants.EMPTY_STR;

	}

	/**
	 * Method Name: getDayCareApproval Method Description:This method determine
	 * where it is Day care Request Service Authorization Approval or Regular
	 * Approval
	 * 
	 * @param idEvent
	 * @return Boolean
	 */
	@Override
	public Boolean getDayCareApproval(Long idEvent) {
		Long count = (Long) sessionFactory.getCurrentSession().createSQLQuery(getDayCareApprovalsql)
				.addScalar("count", StandardBasicTypes.LONG).setParameter("idEvent", idEvent).uniqueResult();
		return count > ServiceConstants.ZERO;
	}

	/**
	 * 
	 * Method Name: getSSCCReferalForIdPersonDC Method Description: Get the
	 * Active Placement Referral for Day care Request
	 * 
	 * @param idEvent
	 * @return Long
	 * @throws DataNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public SsccReferral getSSCCReferalForIdPersonDC(Long idEvent) {
		SsccReferral ssccReferral = null;
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getSSCCReferalForIdPersonDC)
				.addScalar("idSSCCReferral", StandardBasicTypes.LONG).setParameter("idEvent", idEvent)
				.setParameter("cdRefStatus", ServiceConstants.CD_STATUS_ACTIVE)
				.setParameter("svcAuthDtl1", ServiceConstants.CSVCCODE_40A)
				.setParameter("svcAuthDtl2", ServiceConstants.CSVCCODE_40B)
				.setParameter("svcAuthDtl3", ServiceConstants.CSVCCODE_40F)
				.setParameter("svcAuthDtl4", ServiceConstants.CSVCCODE_40M)
				.setParameter("svcAuthDtl5", ServiceConstants.CSVCCODE_40R)
				.setParameter("svcAuthDtl6", ServiceConstants.CSVCCODE_40S)
				.setParameter("svcAuthDtl7", ServiceConstants.CSVCCODE_40W)
				.setParameter("svcAuthDtl8", ServiceConstants.CSVCCODE_40Y)
				.setResultTransformer(Transformers.aliasToBean(SsccReferral.class));
		List<SsccReferral> ssccReferralList = (List<SsccReferral>) query.list();
		if(!ObjectUtils.isEmpty(ssccReferralList)){
			ssccReferral = ssccReferralList.get(0);
		}
		return ssccReferral;
	}

	/**
	 * Method Name: updateSSCCReferral Method Description:This method sets
	 * SSCC_REFERRAL table with IND_LINKED_SVC_AUTH_DATA = 'Y',
	 * DT_LINKED_SVC_AUTH_DATA = SYSDATE for the given SSCC Referral Id. and
	 * sets SSCC_REFERRAL_FAMILY table with IND_SVC_AUTH = 'Y'.
	 * 
	 * @param idSSCCReferral
	 * @return updated ssccReferral
	 */
	@Override
	public SsccReferral updateSSCCReferral(Long idSSCCReferral) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccReferral.class);
		criteria.add(Restrictions.eq("idSSCCReferral", idSSCCReferral));

		SsccReferral ssccReferral = (SsccReferral) criteria.uniqueResult();
		if (!TypeConvUtil.isNullOrEmpty(ssccReferral)) {
			ssccReferral.setIndLinkedSvcAuthData(ServiceConstants.STRING_IND_Y);
			ssccReferral.setDtLinkedSvcAuthData(new Date());
			sessionFactory.getCurrentSession().saveOrUpdate(ssccReferral);
		}

		return ssccReferral;
	}

	/**
	 * Method Name: getSSCCReferralForIdPersonPALorSUB Method Description:This
	 * method gets SSCC Referral id for the person id in PAL or SUB stage.
	 * 
	 * @param idEvent
	 * @return Long
	 * @throws DataNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Long getSSCCReferralForIdPersonPALorSUB(Long idEvent) {
		Long idSSCCRererral = ServiceConstants.ZERO_VAL;
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getSSCCReferralForIdPersonPALorSUBSql)
				.addScalar("idSsccReferral", StandardBasicTypes.LONG)
				.setParameter("CD_STATUS_ACTIVE", ServiceConstants.CD_STATUS_ACTIVE).setParameter("idEvent", idEvent)
				.setParameter("CROLES_PC", ServiceConstants.CROLES_PC);
		List<Long> sSCCReferralForIdPersonPALorSUB = query.list();
		if (!ObjectUtils.isEmpty(sSCCReferralForIdPersonPALorSUB)) {
			idSSCCRererral = sSCCReferralForIdPersonPALorSUB.get(0);
		}
		return idSSCCRererral;
	}

	/**
	 * Method Name: updateSSCCList Method Description: Updates a row into the
	 * SSCC_LIST table
	 * 
	 * @param ssccListDto
	 * @return idSsccList of last updated row of SSCC_LIST
	 */
	@Override
	public SSCCListDto updateSSCCList(Long idSSCCRererral) {
		SSCCListDto SsccListDto = new SSCCListDto();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(SsccList.class);
		criteria.add(Restrictions.eq("ssccReferral.idSSCCReferral", idSSCCRererral));
		SsccList ssccList = (SsccList) criteria.uniqueResult();
		if (!TypeConvUtil.isNullOrEmpty(ssccList)) {
			ssccList.setIndNonssccSvcAuth(ServiceConstants.Y);
			sessionFactory.getCurrentSession().saveOrUpdate(ssccList);
		}
		BeanUtils.copyProperties(ssccList, SsccListDto);
		return SsccListDto;
	}

	/**
	 * Method Name: getSSCCReferralFamilyForIdPerson Method Description: This
	 * method gets SSCC Referral id for the person id NOT (SUB or PAL) stage.
	 * 
	 * @param idEvent
	 * @return Long
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Long getSSCCReferralFamilyForIdPerson(Long idEvent) {
		SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(getSSCCReferralFamilyForIdPersonSql);
		sqlQuery.addScalar("IdSSCCReferral", StandardBasicTypes.LONG);
		sqlQuery.setParameter("idEvent", idEvent);
		List<Long> idSSCCReferralList = sqlQuery.list();
		Long idSSCCReferral = ServiceConstants.ZERO;
		for (Long idSSCCRef : idSSCCReferralList) {
			if (idSSCCRef != ServiceConstants.ZERO) {
				idSSCCReferral = idSSCCRef;
			}
		}
		return idSSCCReferral;
	}

	/**
	 * Method Name: getVendorId Method Description:This method gets the Vendor
	 * Id for the given event id
	 * 
	 * @param idEvent
	 * @return String
	 */
	@Override
	public String getVendorId(Integer idEvent) {
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getVendorIdSql)
				.setParameter("idSvcAuthEvent", idEvent);
		String vid = (String) query.uniqueResult();
		if (TypeConvUtil.isNullOrEmpty(vid)) {
			return null;
		}
		return vid;
	}

	/**
	 * Method Name: isVendorIdExistsBatchParameters Method Description:This
	 * method checks whether vendor id exists or not in BATCH_SSCC_PARAMETERS
	 * table
	 * 
	 * @param vid
	 * @return Boolean
	 */
	@Override
	public Boolean isVendorIdExistsBatchParameters(String vid) {
		Boolean vidExists = ServiceConstants.FALSEVAL;
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(isVendorIdExistsBatchParametersSql).setParameter("idVendor", vid);
		String resultValue = (String) sqlQuery.uniqueResult().toString();
		if (resultValue.equals(ServiceConstants.Y)) {
			vidExists = ServiceConstants.TRUEVAL;
		}
		return vidExists;
	}

	/**
	 * MethodName:updateSSCCReferralFamilyDao MethodDescription:This method sets
	 * SSCC_REFERRAL_FAMILY table with IND_SVC_AUTH = 'Y'
	 * 
	 * @param idEvent
	 * @return
	 */
	public long updateSSCCReferralFamilyDao(long idEvent) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(updateSSCCReferralFamilySql);
		query.setParameter("idEvent", idEvent);
		return query.executeUpdate();
	}

	/**
	 * Method Name: fetchOpenTodoForStage Method Description:Fetch the IdTodo of
	 * the open Next review Todo task for the stage. It should have only one
	 * Todo in the list.
	 * 
	 * @param idStage
	 * @return List<TodoDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<TodoDto> fetchOpenTodoForStage(Long idStage) {

		List<TodoDto> todoDtos = new ArrayList<>();
		TodoDto todoDto = new TodoDto();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Todo.class);
		criteria.add(Restrictions.eq("stage.idStage", idStage));
		criteria.add(Restrictions.isNull("dtTodoCompleted"));
		criteria.addOrder(Order.desc("idTodo"));

		List<Todo> listToDo = (List<Todo>) criteria.list();
		if (!TypeConvUtil.isNullOrEmpty(listToDo)) {
			for (Todo todo : listToDo) {
				todoDto.setIdTodo(todo.getIdTodo());
				todoDto.setDtLastUpdate(todo.getDtLastUpdate());
				if (!TypeConvUtil.isNullOrEmpty(todo.getPersonByIdTodoPersAssigned())) {
					todoDto.setIdTodoPersAssigned(todo.getPersonByIdTodoPersAssigned().getIdPerson());
				}
				if (!TypeConvUtil.isNullOrEmpty(todo.getCapsCase())) {
					todoDto.setIdTodoCase(todo.getCapsCase().getIdCase());
				}
				if (!TypeConvUtil.isNullOrEmpty(todo.getEvent())) {
					todoDto.setIdTodoEvent(todo.getEvent().getIdEvent());
				}
				if (!TypeConvUtil.isNullOrEmpty(todo.getPersonByIdTodoPersCreator())) {
					todoDto.setIdTodoPersCreator(todo.getPersonByIdTodoPersCreator().getIdPerson());

				}
				if (!TypeConvUtil.isNullOrEmpty(todo.getPersonByIdTodoPersWorker())) {
					todoDto.setIdTodoPersWorker(todo.getPersonByIdTodoPersWorker().getIdPerson());

				}
				if (!TypeConvUtil.isNullOrEmpty(todo.getStage())) {
					todoDto.setIdTodoStage(todo.getStage().getIdStage());
				}
				todoDto.setDtTodoDue(todo.getDtTodoDue());
				todoDto.setCdTodoTask(todo.getCdTodoTask());

				todoDto.setTodoDesc(todo.getTxtTodoDesc());
				todoDto.setCdTodoType(todo.getCdTodoType());
				todoDto.setTodoLongDesc(todo.getTxtTodoLongDesc());
				todoDto.setDtTodoCreated(todo.getDtTodoCreated());
				todoDto.setDtTodoTaskDue(todo.getDtTodoTaskDue());
				todoDto.setDtTodoCompleted(todo.getDtTodoCompleted());
				todoDto.setNmTodoCreatorInit(todo.getNmTodoCreatorInit());
				if (!TypeConvUtil.isNullOrEmpty(todo.getTodoInfo())) {
					todoDto.setIdTodoInfo(todo.getTodoInfo().getIdTodoInfo());
				}
				todoDtos.add(todoDto);
			}

		}
		return todoDtos;

	}

	/**
	 * Method Name: updateApproversSql Method Description: update approvers
	 * based on approvers id and last update date.
	 * 
	 * @param approversDto
	 * @return Long
	 * @throws DataNotFoundException
	 */
	@Override
	public Long updateApproversSql(ApproversDto approversDto) {
		Timestamp systemTime = new Timestamp(Calendar.getInstance().getTime().getTime());

		Long updateResult = 0l;

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Approvers.class);

		criteria.add(Restrictions.eq("idApprovers", approversDto.getIdApprovers()));
		criteria.add(Restrictions.eq("dtLastUpdate", approversDto.getDtLastUpdate()));

		List<Approvers> approversList = criteria.list();

		if (!TypeConvUtil.isNullOrEmpty(approversList)) {

			for (Approvers approvers : approversList) {

				// dtApproversDetermination
				if (!TypeConvUtil.isNullOrEmpty(approversDto.getDtApproversDetermination())) {
					approvers.setDtApproversDetermination(approversDto.getDtApproversDetermination());
				}

				if (!TypeConvUtil.isNullOrEmpty(approversDto.getIdPerson())) {

					Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
							approversDto.getIdPerson());

					if (!TypeConvUtil.isNullOrEmpty(person)) {
						approvers.setPerson(person);
					}

				}

				// cdApproversStatus
				if (!TypeConvUtil.isNullOrEmpty(approversDto.getCdApproversStatus())) {
					approvers.setCdApproversStatus(approversDto.getCdApproversStatus());
				}

				// txtApproversCmnts
				if (!TypeConvUtil.isNullOrEmpty(approversDto.getApproversCmnts())) {
					approvers.setTxtApproversCmnts(approversDto.getApproversCmnts());
				}

				// dtLastUpdate
				if (!TypeConvUtil.isNullOrEmpty(approversDto.getDtLastUpdate())) {
					approvers.setDtLastUpdate(systemTime);
				}

				sessionFactory.getCurrentSession().saveOrUpdate(approvers);

				updateResult++;

			}

		}
		return updateResult;
	}

	/**
	 * Method Name: updateEventStatus Method Description:Method to update event
	 * status by event id.
	 * 
	 * @param eventId
	 * @param eventStatus
	 * @return Long
	 * @throws DataNotFoundException
	 */
	@Override
	public Long updateEventStatus(Long eventId, String eventStatus) {

		Timestamp systemTime = new Timestamp(Calendar.getInstance().getTime().getTime());

		Long updateResult = 0l;

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Event.class);

		criteria.add(Restrictions.eq("idEvent", eventId));

		Event event = (Event) criteria.uniqueResult();

		if (!TypeConvUtil.isNullOrEmpty(event)) {

			if (!TypeConvUtil.isNullOrEmpty(eventStatus)) {
				event.setCdEventStatus(eventStatus);
			}

			event.setDtLastUpdate(systemTime);

			sessionFactory.getCurrentSession().saveOrUpdate(event);

			updateResult++;

		}
		return updateResult;
	}

	/**
	 * Method Name: deleteTodo Method Description: This method used to delete
	 * todo.
	 * 
	 * @param toDoValueDto
	 * @return Long
	 * @throws DataNotFoundException
	 */
	@Override
	public Long deleteTodo(TodoDto todoDto) {

		Long updateResult = 0l;

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Todo.class);

		criteria.add(Restrictions.eq("idTodo", todoDto.getIdTodo()));

		Todo todo = (Todo) criteria.uniqueResult();

		if (!TypeConvUtil.isNullOrEmpty(todo)) {
			sessionFactory.getCurrentSession().delete(todo);
			updateResult++;
		}
		return updateResult;

	}

	/**
	 * Method Name: getNmCase Method Description: Returns the case name from its
	 * id.
	 * 
	 * @param ulIdCase
	 * @return String.
	 */
	public String getNmCase(Long ulIdCase) {
		String nmCase = new String();

		if (!TypeConvUtil.isNullOrEmpty(ulIdCase)) {

			CapsCase capsCase = (CapsCase) sessionFactory.getCurrentSession().get(CapsCase.class, ulIdCase);

			if (!TypeConvUtil.isNullOrEmpty(capsCase)) {
				nmCase = capsCase.getNmCase();
			}
		}

		return nmCase;
	}

	/**
	 * Method Name: insertTodo. Method Description: insert to do record.
	 * 
	 * @param toDoValueDto.
	 * @return Long.
	 * @throws DataNotFoundException
	 */
	@Override
	public Long insertTodo(TodoDto todoDto) {

		Timestamp systemTime = new Timestamp(Calendar.getInstance().getTime().getTime());

		long updateResult = 0;

		Todo todo = new Todo();

		if (!TypeConvUtil.isNullOrEmpty(todoDto.getIdTodo())) {
			todo.setIdTodo(todoDto.getIdTodo());
		}

		if (!TypeConvUtil.isNullOrEmpty(todoDto.getDtLastUpdate())) {
			todo.setDtLastUpdate(todoDto.getDtLastUpdate());
		} else {
			todo.setDtLastUpdate(systemTime);
		}

		if (!TypeConvUtil.isNullOrEmpty(todoDto.getIdTodoPersAssigned())) {

			Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
					todoDto.getIdTodoPersAssigned());

			if (!TypeConvUtil.isNullOrEmpty(person)) {
				todo.setPersonByIdTodoPersAssigned(person);
			}
		}

		if (!TypeConvUtil.isNullOrEmpty(todoDto.getIdTodoCase())) {

			CapsCase capsCase = (CapsCase) sessionFactory.getCurrentSession().get(CapsCase.class,
					todoDto.getIdTodoCase());
			if (!TypeConvUtil.isNullOrEmpty(capsCase)) {
				todo.setCapsCase(capsCase);
			}
		}

		if (!TypeConvUtil.isNullOrEmpty(todoDto.getIdTodoEvent())) {
			Event event = (Event) sessionFactory.getCurrentSession().get(Event.class, todoDto.getIdTodoEvent());
			if (!TypeConvUtil.isNullOrEmpty(event)) {
				todo.setEvent(event);
			}
		}

		if (!TypeConvUtil.isNullOrEmpty(todoDto.getIdTodoPersCreator())) {

			Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
					todoDto.getIdTodoPersCreator());

			if (!TypeConvUtil.isNullOrEmpty(person)) {
				todo.setPersonByIdTodoPersCreator(person);
			}
		}

		if (!TypeConvUtil.isNullOrEmpty(todoDto.getIdTodoStage())) {
			Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, todoDto.getIdTodoStage());

			if (!TypeConvUtil.isNullOrEmpty(stage)) {
				todo.setStage(stage);
			}
		}

		if (!TypeConvUtil.isNullOrEmpty(todoDto.getIdTodoPersWorker())) {
			Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
					todoDto.getIdTodoPersWorker());

			if (!TypeConvUtil.isNullOrEmpty(person)) {
				todo.setPersonByIdTodoPersWorker(person);
			}
		}

		if (!TypeConvUtil.isNullOrEmpty(todoDto.getDtTodoDue())) {
			todo.setDtTodoDue(systemTime);
		} else {
			todo.setDtTodoDue(systemTime);
		}

		if (!TypeConvUtil.isNullOrEmpty(todoDto.getCdTodoTask())) {
			todo.setCdTodoTask(todoDto.getCdTodoTask());
		}

		if (!TypeConvUtil.isNullOrEmpty(todoDto.getTodoDesc())) {
			todo.setTxtTodoDesc(todoDto.getTodoDesc());
		}

		if (!TypeConvUtil.isNullOrEmpty(todoDto.getCdTodoType())) {
			todo.setCdTodoType(todoDto.getCdTodoType());
		}

		if (!TypeConvUtil.isNullOrEmpty(todoDto.getTodoLongDesc())) {
			todo.setTxtTodoLongDesc(todoDto.getTodoLongDesc());
		}

		if (!TypeConvUtil.isNullOrEmpty(todoDto.getDtTodoCreated())) {
			todo.setDtTodoCreated(systemTime);
		} else {
			todo.setDtTodoCreated(systemTime);
		}

		if (!TypeConvUtil.isNullOrEmpty(todoDto.getDtTodoTaskDue())) {
			todo.setDtTodoTaskDue(todoDto.getDtTodoTaskDue());
		}

		if (!TypeConvUtil.isNullOrEmpty(todoDto.getDtTodoCompleted())) {
			todo.setDtTodoCompleted(systemTime);
		} else {
			todo.setDtTodoCompleted(systemTime);
		}

		if (!TypeConvUtil.isNullOrEmpty(todoDto.getNmTodoCreatorInit())) {
			todo.setNmTodoCreatorInit(todoDto.getNmTodoCreatorInit());
		}

		sessionFactory.getCurrentSession().save(todo);

		updateResult++;

		return updateResult;
	}

	/**
	 * Method Name: fetchPADStageIdForADO Method Description: This method
	 * fetches idStage of PAD which has ADO stage as prior stage
	 * 
	 * @param idStage
	 * @return Long
	 */
	@Override
	public Long fetchPADStageIdForADO(Long idStage) {
		Long padIdStage = 0l;
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchPADStageIdForADOSql)
				.addScalar("idStage", StandardBasicTypes.LONG).setParameter("idPriorStage", idStage);
		List<Long> idStageList = query.list();
		for (Long stageId : idStageList) {
			padIdStage = stageId;
		}
		return padIdStage;
	}

	/**
	 * Returns the ulIdPerson of the Primary Worker of the stage. i;e the Person
	 * with role as PRIMARY ("PR").
	 * 
	 * @param ulIdStage
	 * @return idPersonPrimaryWorker
	 */
	@SuppressWarnings("unchecked")
	public long getPrimaryWorkerIdForStage(Long ulIdStage) {
		long primaryWorkerIdForStage = 0;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Workload.class, "workload");
		criteria.add(Restrictions.eq("workload.id.idWkldStage", ulIdStage));
		criteria.add(Restrictions.eq("workload.id.cdWkldStagePersRole", "PR"));
		criteria.setProjection(Projections.property("workload.id.idWkldPerson"));
		List<Long> primaryWorkerIdForStageList = (List<Long>) criteria.list();
		if (!ObjectUtils.isEmpty(primaryWorkerIdForStageList)) {
			primaryWorkerIdForStage = (long) primaryWorkerIdForStageList.get(0);
		}
		return primaryWorkerIdForStage;
	}

	/**
	 * MethodName: getOpenStagesForCase MethodDdescription:Get the open stages
	 * for the case. This method is called by ApprovalBean to determine if case
	 * would need secondary investigation.
	 * 
	 * @param idCase
	 * @return List<String>
	 */
	@Override
	public List<String> getOpenStagesForCase(Long idCase) {
		List<String> cdStageList = new ArrayList<>();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Stage.class);
		criteria.add(Restrictions.eq("capsCase.idCase", idCase));
		criteria.add(Restrictions.eq("indStageClose", ServiceConstants.N));
		List<Stage> openStageList = criteria.list();
		for (Stage stage : openStageList) {
			if (stage != null) {
				cdStageList.add(stage.getCdStage());
			}
		}
		return cdStageList;
	}

	/**
	 * Get the over all disposition for the investigation conclusion stage
	 * 
	 * @param idCase
	 * @param idStage
	 * @return String
	 */
	@Override
	public String queryOverallDispositionForInvst(long idCase, long idStage) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CpsInvstDetail.class);

		criteria.add(Restrictions.eq("stage.idStage", idStage));
		criteria.add(Restrictions.eq("idCase", idCase));

		CpsInvstDetail cpsInvstDetail = (CpsInvstDetail) criteria.uniqueResult();
		if (cpsInvstDetail != null) {
			return cpsInvstDetail.getCdCpsInvstDtlOvrllDisptn();
		}

		return null;
	}

	/**
	 * Get various details of the stage which are required for secondary
	 * approval determination
	 * 
	 * @param idStage
	 * @return SecondaryApprovalViewDto
	 */
	@Override
	public SecondaryApprovalDto getStageDetails(long idStage) {
		Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, idStage);

		if (stage != null) {
			SecondaryApprovalDto approvalViewDto = new SecondaryApprovalDto();
			approvalViewDto.setDtStageStart(stage.getDtStageStart());
			if (stage.getIndSecondApprover() != null) {
				approvalViewDto.setIndSecondApprover(stage.getIndSecondApprover());
			} else {
				approvalViewDto.setIndSecondApprover(ServiceConstants.N);
			}
			return approvalViewDto;
		}
		return null;
	}

	/**
	 * Find if the investigation is a school related investigation or not.
	 * 
	 * @param idStage
	 * @return boolean
	 */
	@Override
	public boolean isSchoolInvestigation(long idStage) {
		Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, idStage);
		if (stage != null) {
			if (stage.getCdStageReasonClosed() != null) {
				if (ServiceConstants.CLOSE_SCHOOL_INVESTIGATION.equalsIgnoreCase(stage.getCdStageReasonClosed())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check persons in stage to see if AP(DP) has rel/int as "School
	 * Personnel".
	 * 
	 * @param idStage
	 * @return List<SecondaryInvstDto>
	 */
	@Override
	public List<ApprovalCommonInDto> queryStageChildrenDOB(long idCase, long idStage) {
		SQLQuery sqlQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(querydob)
				.addScalar("dtPersonBirth", StandardBasicTypes.DATE).addScalar("idPerson", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(ApprovalCommonInDto.class)));
		sqlQuery.setParameter("ulIdCase", idCase);
		sqlQuery.setParameter("ulIdStage", idStage);
		sqlQuery.setParameter("type", ServiceConstants.CPRSNTYP_PRN);
		List<ApprovalCommonInDto> secondaryInvstDtos = (List<ApprovalCommonInDto>) sqlQuery.list();
		return secondaryInvstDtos;
	}

	/**
	 * Check persons in stage to see if AP(DP) has rel/int as "School
	 * Personnel".
	 * 
	 * @param idStage
	 * @return boolean
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean isSchoolPersonnelInvolved(long idStage) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class);
		List<String> cdStagePersRole = new ArrayList<>();
		cdStagePersRole.add(ServiceConstants.PERSON_ROLE_PERP);
		cdStagePersRole.add(ServiceConstants.CD_STAGE_PERS_ROLE_DP);

		criteria.add(Restrictions.in("cdStagePersRole", cdStagePersRole));
		criteria.add(Restrictions.eq("cdStagePersRelInt", ServiceConstants.CD_STAGE_PERS_RELINT));
		criteria.add(Restrictions.eq("idStage", idStage));
		List<StagePersonLink> stagePersonLinks = criteria.list();
		if (stagePersonLinks.isEmpty()) {
			return false;
		}
		return true;
	}

	/**
	 * Get person info for this stage in the case. This method is called by
	 * ApprovalBean to determine if case would need secondary investigation.
	 * 
	 * @param idStage
	 * @return boolean
	 */
	@Override
	public boolean isChildDeath(long idStage) {
		SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(isChildDeathsql);
		sqlQuery.addScalar("count", StandardBasicTypes.LONG);
		sqlQuery.setParameter("idStage", idStage);
		sqlQuery.setParameter("cdStageType", ServiceConstants.CPRSNTYP_PRN);
		if (ServiceConstants.ZERO != (Long) sqlQuery.uniqueResult()) {
			return true;
		}
		return false;
	}

	/**
	 * This function returns start date of the prior stage.
	 * 
	 * @param idStage
	 * @return Date
	 */
	@Override
	public Date getPriorStage(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPriorStageSql);
		query.setParameter("idStage", idStage);
		return (Date) query.uniqueResult();
	}

	/**
	 * Method Name: getPendingApprovalCount (getCcmn56DO) Method
	 * Description:This function returns Count of the Pending Approvals for the
	 * given Approval Id.
	 * 
	 * @param approvalServiceDto
	 * @return ApprovalServiceOprnDto
	 */
	@Override
	public ApproveApprovalDto getCcmn56DO(SecondaryApprovalDto approvalServiceDto) {

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getCcmn56DOSql)
				.setResultTransformer(Transformers.aliasToBean(ApproversDto.class));
		query.addScalar("cdApproversStatus");
		query.setParameter("idApproval", approvalServiceDto.getUlIdApproval());

		List<ApproversDto> approverDtrmntnDtos = query.list();
		ApproveApprovalDto approvalServiceOprnDto = new ApproveApprovalDto();
		ApprovalCommonInDto pendingApprovalDto = new ApprovalCommonInDto();
		pendingApprovalDto.setRowCcmn56DoList(approverDtrmntnDtos);
		approvalServiceOprnDto.setRowCcmn56DoArrayDto(pendingApprovalDto);

		return approvalServiceOprnDto;
	}

	/**
	 * 
	 * Method Name: getSelectCaseFileManagement Method Description:case file
	 * Management
	 * 
	 * @param caseFileManagementDto
	 * @return
	 * @throws DataNotFoundException
	 */
	@Override
	public CaseFileManagementDto getSelectCaseFileManagement(CaseFileManagementDto caseFileManagementDto) {
		SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getSelectCaseFileManagement)
				.addScalar("idCaseFileCase", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idOffice", StandardBasicTypes.LONG).addScalar("idUnit", StandardBasicTypes.LONG)
				.addScalar("addrCaseFileCity", StandardBasicTypes.STRING)
				.addScalar("addrCaseFileStLn1", StandardBasicTypes.STRING)
				.addScalar("addrCaseFileStLn2", StandardBasicTypes.STRING)
				.addScalar("cdCaseFileOfficeType", StandardBasicTypes.STRING)
				.addScalar("dtCaseFileArchCompl", StandardBasicTypes.DATE)
				.addScalar("dtCaseFileArchElig", StandardBasicTypes.DATE)
				.addScalar("nmCaseFileOffice", StandardBasicTypes.STRING)
				.addScalar("caseFileLocateInfo", StandardBasicTypes.STRING)
				.addScalar("cvsAdop", StandardBasicTypes.STRING).addScalar("trn", StandardBasicTypes.STRING)
				.addScalar("skp", StandardBasicTypes.STRING).addScalar("addSkpTrn", StandardBasicTypes.STRING)
				.setParameter("idCaseFileCase", caseFileManagementDto.getIdCaseFileCase())
				.setResultTransformer(Transformers.aliasToBean(CaseFileManagementDto.class)));
		return (CaseFileManagementDto) sQLQuery.uniqueResult();
	}

	/**
	 * 
	 * Method Name: insertCaseFileManagement Method Description:
	 * 
	 * @return Long
	 * @param caseFileManagementDto
	 * @throws DataNotFoundException
	 */
	@Override
	public Long insertCaseFileManagement(CaseFileManagementDto caseFileManagementDto) {
		if (TypeConvUtil.isNullOrEmpty(caseFileManagementDto))
			return null;
		Office office = (Office) sessionFactory.getCurrentSession().get(Office.class,
				caseFileManagementDto.getIdOffice());
		Unit unit = (Unit) sessionFactory.getCurrentSession().get(Unit.class, caseFileManagementDto.getIdUnit());
		CaseFileManagement caseFileManagement = new CaseFileManagement();
		CapsCase capsCase = new CapsCase();
		capsCase.setIdCase(caseFileManagementDto.getIdCaseFileCase());
		caseFileManagement.setCapsCase(capsCase);

		caseFileManagement.setIdCaseFileCase(caseFileManagementDto.getIdCaseFileCase());
		caseFileManagement.setDtLastUpdate(new Date());
		caseFileManagement.setNmCaseFileOffice(caseFileManagementDto.getNmCaseFileOffice());
		if (!TypeConvUtil.isNullOrEmpty(office)) {
			caseFileManagement.setOffice(office);
		}
		if (!TypeConvUtil.isNullOrEmpty(unit)) {
			caseFileManagement.setUnit(unit);
		}
		caseFileManagement.setAddrCaseFileCity(caseFileManagementDto.getAddrCaseFileCity());
		caseFileManagement.setAddrCaseFileStLn1(caseFileManagementDto.getAddrCaseFileStLn1());
		caseFileManagement.setAddrCaseFileStLn2(caseFileManagementDto.getAddrCaseFileStLn2());
		caseFileManagement.setCdCaseFileOfficeType(caseFileManagementDto.getCdCaseFileOfficeType());
		caseFileManagement.setDtCaseFileArchCompl(caseFileManagementDto.getDtCaseFileArchCompl());
		caseFileManagement.setDtCaseFileArchElig(caseFileManagementDto.getDtCaseFileArchElig());
		caseFileManagement.setTxtCaseFileLocateInfo(caseFileManagementDto.getCaseFileLocateInfo());
		caseFileManagement.setTxtCvsAdop(caseFileManagementDto.getCvsAdop());
		caseFileManagement.setTxtTrn(caseFileManagementDto.getSkp());
		caseFileManagement.setTxtSkp(caseFileManagementDto.getSkp());
		caseFileManagement.setTxtAddSkpTrn(caseFileManagementDto.getAddSkpTrn());

		return (Long) sessionFactory.getCurrentSession().save(caseFileManagement);
	}

	/**
	 * Method Name: getARSafetyAssmt Method Description:This method is called
	 * from display method in SafetyAssmtConversation if the page has been
	 * previously saved. It retrieves back all the responses
	 * 
	 * @param idStage
	 * @param cdAssmtType
	 * @param idUser
	 * @return ARSafetyAssmtValueDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ARSafetyAssmtValueDto getARSafetyAssmt(Integer idStage, String cdAssmtType, Integer idUser) {
		ARSafetyAssmtValueDto arSafetyAssmtValueDto = null;
		List<ARSafetyAssmtValueDto> arSafetyAssmtValueDtoList = (List<ARSafetyAssmtValueDto>) sessionFactory
				.getCurrentSession().createSQLQuery(getARSafetyAssmtSql)
				.addScalar("idArSafetyAssmt", StandardBasicTypes.INTEGER)
				.addScalar("idEvent", StandardBasicTypes.INTEGER).addScalar("idStage", StandardBasicTypes.INTEGER)
				.addScalar("idCase", StandardBasicTypes.INTEGER).addScalar("immediateAction", StandardBasicTypes.STRING)
				.addScalar("furtherAssmt", StandardBasicTypes.STRING).addScalar("version", StandardBasicTypes.INTEGER)
				.addScalar("indAssmtType", StandardBasicTypes.STRING)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setParameter("indAssmtType", cdAssmtType).setParameter("nbrVersion", ServiceConstants.NBR_VERSION)
				.setResultTransformer(Transformers.aliasToBean(ARSafetyAssmtValueDto.class)).list();
		if (!ObjectUtils.isEmpty(arSafetyAssmtValueDtoList)) {
			arSafetyAssmtValueDto = arSafetyAssmtValueDtoList.get(0);
			arSafetyAssmtValueDto.setVersion(ServiceConstants.NBR_VERSION);
		}
		return arSafetyAssmtValueDto;
	}

	/**
	 * This method is used to update CD_EVENT_STATUS with provided idEvent and
	 * cdEventStatus status
	 * 
	 * @param idevent,cdEventStatus
	 * @return long
	 */

	@Override
	public long updateEventStatus(int idEvent, String cdEventStatus) throws DataNotFoundException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Event.class);
		criteria.add(Restrictions.eq(ServiceConstants.IDEVENT, (long) idEvent));

		Event stat = (Event) criteria.uniqueResult();

		stat.setCdEventStatus(cdEventStatus);

		sessionFactory.getCurrentSession().saveOrUpdate(stat);

		return criteria.list().size();

	}

	/**
	 * Method Name: updateFacilityIndicator MethodDescription:This DAM will
	 * update IND_FACIL_SUPERINT_NOTIF to'Y'on the FACILITY_INVST_DTL table
	 * whenever the Investigation Conclusion is approved. This needs to be done
	 * according to the design for the MHMR Enhancement for AFC
	 * Investigation"Waiting for Superintendent Comments". DAM : CAUDC7D
	 * 
	 * @param saveApprovalStatusReq
	 * @throws DataNotFoundException
	 */
	@Override
	public void updateFacilityIndicator(SaveApprovalStatusReq saveApprovalStatusReq) {
		log.debug("Entering method updateFacilityIndicator in ApprovalStatusDaoImpl");

		sessionFactory.getCurrentSession().createSQLQuery(updateFacilityIndicatorsql)
				.setParameter("idFacilInvstStage", saveApprovalStatusReq.getIdStage()).executeUpdate();

		log.debug("Exiting method updateFacilityIndicator in ApprovalStatusDaoImpl");
	}

	/**
	 * Method Name: getEmployeeInfo Method Description: Fetch the employee
	 * information based on the ID_PERSON of Approver We need JobClass and
	 * EmpConfirmedHrmis for storing it into Approver or Approval rejection
	 * table Dam Name: CSES00D for CCMN35S Service
	 * 
	 * @param idPerson
	 * @return EmployeeDetailDto
	 */
	@Override
	public EmployeeDetailDto getEmployeeInfo(Long idPerson) {
		log.info("Entering method getEmployeeInfo in ApprovalStatusDaoImpl");

		SQLQuery sqlQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getEmployeeInfosql)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("tsLastUpdate", StandardBasicTypes.DATE)
				.addScalar("cdEmployeeClass", StandardBasicTypes.STRING).addScalar("dtEmpHire", StandardBasicTypes.DATE)
				.addScalar("idEmpJobHistory", StandardBasicTypes.LONG)
				.addScalar("nbrEmpActivePct", StandardBasicTypes.SHORT).addScalar("idOffice", StandardBasicTypes.LONG)
				.addScalar("idEmployeeLogon", StandardBasicTypes.STRING)
				.addScalar("employeeClass", StandardBasicTypes.STRING)
				.addScalar("cdEmpProgram", StandardBasicTypes.STRING)
				.addScalar("dtEmpLastAssigned", StandardBasicTypes.DATE)
				.addScalar("dtEmpTermination", StandardBasicTypes.DATE)
				.addScalar("indActiveStatus", StandardBasicTypes.STRING)
				.addScalar("indEmpConfirmedHrmis", StandardBasicTypes.STRING)
				.addScalar("indEmpPendingHrmis", StandardBasicTypes.STRING).setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(EmployeeDetailDto.class)));
		log.info("Exiting method getEmployeeInfo in ApprovalStatusDaoImpl");
		return (EmployeeDetailDto) sqlQuery.uniqueResult();
	}

	/**
	 * Method Name:updateIndSecondApprover Method Description : This Method
	 * (CAUDK4D ) if this request is from secondary approver, then we need to
	 * store this information on the STAGE table in IND_SECOND_APPROVER. Once a
	 * stage has been reviewed by second approver, this stage would not require
	 * secondary approval, even if other conditions are met.
	 * 
	 * DAM NAME : CAUDK4D SERVICE NAME : CCMN35S
	 * 
	 * @param saveApprovalStatusReq
	 * @return
	 */
	@Override
	public void updateIndSecondApprover(SaveApprovalStatusReq saveApprovalStatusReq) {
		log.debug("Entering method approvalStatusDaoImpl.updateIndSecondApprover()");

		sessionFactory.getCurrentSession().createSQLQuery(updateIndSecondApproversql)
				.setParameter("idStage", saveApprovalStatusReq.getIdStage())
				.setParameter("indSecondApprover", saveApprovalStatusReq.getIndSecondApprover()).executeUpdate();

		log.debug("Exiting method approvalStatusDaoImp.updateIndSecondApprover()");
	}

	/**
	 * Method Name: updateApprovers Method Description: This metod will save the
	 * approval determination. Dam Name: CCMN61 Service Name: CCMN35S
	 * 
	 * @param saveApprovalStatusReq
	 * @return
	 */
	@Override
	public void updateApprovers(ApprovalStatusFacilityIndicatorDto approvalStatusFacilityIndicatorDto) {
		log.debug("Entering method ApproovalStatusDaoImpl.updateApprovers()");

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Approvers.class);
		criteria.add(Restrictions.eq("idApprovers", approvalStatusFacilityIndicatorDto.getIdApprovers()));
		Approvers approvers = (Approvers) criteria.uniqueResult();

		/*
		 * String approDt =
		 * DateUtils.fullISODateTimeFormat(approvers.getDtLastUpdate()); Date
		 * appDate = DateUtils.stringDateAndTimestamp(approDt);
		 */
		// Date jDate =
		// DateUtils.toJavaDate(approvalStatusFacilityIndicatorDto.getLastUpdate());

		if (!ObjectUtils.isEmpty(approvers)
				&& approvers.getDtLastUpdate().compareTo(approvalStatusFacilityIndicatorDto.getLastUpdate()) == 0) {
			// Populate fields to update
			if (!TypeConvUtil.isNullOrEmpty(approvalStatusFacilityIndicatorDto.getCdStageReasonClosed())) {
				approvers.setCdStageReasonClosed(approvalStatusFacilityIndicatorDto.getCdStageReasonClosed());
			}

			if (!TypeConvUtil.isNullOrEmpty(approvalStatusFacilityIndicatorDto.getIndEmpConfirmedHrmis())) {
				approvers.setIndEmpConfirmedHrmis(approvalStatusFacilityIndicatorDto.getIndEmpConfirmedHrmis());
			}

			if (!TypeConvUtil.isNullOrEmpty(approvalStatusFacilityIndicatorDto.getDtApproversDetermination())) {
				approvers.setDtApproversDetermination(approvalStatusFacilityIndicatorDto.getDtApproversDetermination());
			}

			if (!TypeConvUtil.isNullOrEmpty(approvalStatusFacilityIndicatorDto.getCdJobClass())) {
				approvers.setCdJobClass(approvalStatusFacilityIndicatorDto.getCdJobClass());
			}

			if (!TypeConvUtil.isNullOrEmpty(approvalStatusFacilityIndicatorDto.getDtDeterminationRecorded())) {
				approvers.setDtDeterminationRecorded(approvalStatusFacilityIndicatorDto.getDtDeterminationRecorded());
			}

			if (!TypeConvUtil.isNullOrEmpty(approvalStatusFacilityIndicatorDto.getCdApproversStatus())) {
				approvers.setCdApproversStatus(approvalStatusFacilityIndicatorDto.getCdApproversStatus());
			}

			if (!TypeConvUtil.isNullOrEmpty(approvalStatusFacilityIndicatorDto.getCdOvrllDisptn())) {
				approvers.setCdOvrllDisptn(approvalStatusFacilityIndicatorDto.getCdOvrllDisptn());
			}

			if (!TypeConvUtil.isNullOrEmpty(approvalStatusFacilityIndicatorDto.getIdPerson())) {
				Person person = new Person();
				person.setIdPerson(approvalStatusFacilityIndicatorDto.getIdPerson());
				approvers.setPerson(person);
			}

			if (!TypeConvUtil.isNullOrEmpty(approvalStatusFacilityIndicatorDto.getTxtApproversComments())) {
				approvers.setTxtApproversCmnts(approvalStatusFacilityIndicatorDto.getTxtApproversComments());
			}

			// Contains value Y to indicate the extension reason is under case
			// worker's
			// control, N for outside of case worker's control.
			// indExtnWithinCwCtr
			if (!TypeConvUtil.isNullOrEmpty(approvalStatusFacilityIndicatorDto.getIndWithinWorkerControl())) {
				approvers.setIndExtnWithinCwCtr(approvalStatusFacilityIndicatorDto.getIndWithinWorkerControl());
			}

			// Contains approval duration, codes table column value CAPRVLEN
			if (!TypeConvUtil.isNullOrEmpty(approvalStatusFacilityIndicatorDto.getCdApprovalLength())) {
				approvers.setCdExtnDays(approvalStatusFacilityIndicatorDto.getCdApprovalLength());
			}

			sessionFactory.getCurrentSession().saveOrUpdate(approvers);
		}
		log.debug("Exiting method ApprovalStatusDaoImpl.updateApprovers()");
	}

	/**
	 * Method Name: saveRejectionApproval Method Description: This method will
	 * save the Approval Rejection. Dam Name: ccmni2d Service Name: CCMN35S
	 * 
	 * @param saveRejectionApproval
	 * @return
	 */
	@Override
	public void saveRejectionApproval(RejectApprovalDto rejectApprovalDto) {
		log.debug("Entering method saveRejectionApproval of approvalStatisDaoImpl");

		sessionFactory.getCurrentSession().createSQLQuery(insertApprovalRejectionSql)
				.setString("indMissingEvidAp", rejectApprovalDto.getIndMissingEvidAp())
				.setString("indEvidence", rejectApprovalDto.getIndEvidence())
				.setString("cdStageReasonClosed", rejectApprovalDto.getCdStageReasonClosed())
				.setString("indCPSCriminalHistory", rejectApprovalDto.getIndCPSCriminalHistory())
				.setString("indMissingEvidPhoto", rejectApprovalDto.getIndMissingEvidPhoto())
				.setString("indCCLCitations", rejectApprovalDto.getIndCCLCitations())
				.setString("indAPSRootCauses", rejectApprovalDto.getIndAPSRootCauses())
				.setString("indCCLAllegations", rejectApprovalDto.getIndCCLAllegations())
				.setString("indAFCNotSupported", rejectApprovalDto.getIndAFCNotSupported())
				.setString("indRORAIncorrect", rejectApprovalDto.getIndRORAIncorrect())
				.setString("indAFCEvidence", rejectApprovalDto.getIndAFCEvidence())
				.setString("indCCLEvidence", rejectApprovalDto.getIndCCLEvidence())
				.setString("indCPSHistory", rejectApprovalDto.getIndCPSHistory())
				.setDate("dtRejection", rejectApprovalDto.getDtRejection())
				.setString("indAFCOther", rejectApprovalDto.getIndAFCOther())
				.setString("indDiscretionaryReason", rejectApprovalDto.getIndDiscretionaryReason())
				.setString("indCPSRoles", rejectApprovalDto.getIndCPSRoles())
				.setString("indCPSFactors", rejectApprovalDto.getIndCPSFactors())
				.setString("indCPSAllegations", rejectApprovalDto.getIndCPSAllegations())
				.setString("indCPSHomeVisit", rejectApprovalDto.getIndCPSHomeVisit())
				.setString("indAFCIncomplete", rejectApprovalDto.getIndAFCIncomplete())
				.setString("indCPSPreviousInv", rejectApprovalDto.getIndCPSPreviousInv())
				.setString("indCCLPersonList", rejectApprovalDto.getIndCCLPersonList())
				.setString("indAFCInterviews", rejectApprovalDto.getIndAFCInterviews())
				.setString("indCPSServices", rejectApprovalDto.getIndCPSServices())
				.setString("indCPSInterviews", rejectApprovalDto.getIndCPSInterviews())
				.setString("indCPSDisposition", rejectApprovalDto.getIndCPSDisposition())
				.setString("indMissingEvidMp", rejectApprovalDto.getIndMissingEvidMp())
				.setString("indCCLExternal", rejectApprovalDto.getIndCCLExternal())
				.setString("indCPSSearch", rejectApprovalDto.getIndCPSSearch())
				.setString("indCCLIncomplete", rejectApprovalDto.getIndCCLIncomplete())
				.setString("indMissingEvidCl", rejectApprovalDto.getIndMissingEvidCl())
				.setString("indEmpConfirmedHrmis", rejectApprovalDto.getIndEmpConfirmedHrmis())
				.setString("indAFCFollowUp", rejectApprovalDto.getIndAFCFollowUp())
				.setString("indMissingEvidRptr", rejectApprovalDto.getIndMissingEvidRptr())
				.setString("indCPSPrincipals", rejectApprovalDto.getIndCPSPrincipals())
				.setString("indCCLCollaterals", rejectApprovalDto.getIndCCLCollaterals())
				.setString("indCCLOther", rejectApprovalDto.getIndCCLOther())
				.setDate("dtDeterminationRecorded", rejectApprovalDto.getDtDeterminationRecorded())
				.setString("indAFCNotSummarized", rejectApprovalDto.getIndAFCNotSummarized())
				.setString("indCPSLegalAction", rejectApprovalDto.getIndCPSLegalAction())
				.setLong("idStage", rejectApprovalDto.getIdStage())
				.setString("indCCLAbuse", rejectApprovalDto.getIndCCLAbuse())
				.setString("cdOvrllDisptn", rejectApprovalDto.getCdOvrllDisptn())
				.setString("indCPSLawEnforcement", rejectApprovalDto.getIndCPSLawEnforcement())
				.setString("indCPSPolicy", rejectApprovalDto.getIndCPSPolicy())
				.setString("indMissingEvidDe", rejectApprovalDto.getIndMissingEvidDe())
				.setString("indCCLInterviews", rejectApprovalDto.getIndCCLInterviews())
				.setString("indCPSAddServices", rejectApprovalDto.getIndCPSAddServices())
				.setString("cdJobClass", rejectApprovalDto.getCdJobClass())
				.setString("indCPSDrugTesting", rejectApprovalDto.getIndCPSDrugTesting())
				.setString("indAFCInconsistent", rejectApprovalDto.getIndAFCInconsistent())
				.setString("indICSRecommended", rejectApprovalDto.getIndICSRecommended())
				.setString("indCPSRiskAssessment", rejectApprovalDto.getIndCPSRiskAssessment())
				.setString("indMissingEvidOther", rejectApprovalDto.getIndMissingEvidOther())
				.setString("indCareEntered", rejectApprovalDto.getIndCareEntered())
				.setString("indCPSOther", rejectApprovalDto.getIndCPSOther())
				.setString("indApsEffort", rejectApprovalDto.getIndApsEffort())
				.setString("indCPSCollaterals", rejectApprovalDto.getIndCPSCollaterals())
				.setString("indAFCConcerns", rejectApprovalDto.getIndAFCConcerns())
				.setString("indInCompleteCCLRejection", rejectApprovalDto.getIndInCompleteCCLRejection())
				.setLong("idCase", rejectApprovalDto.getIdCase())
				.setLong("idRejector", rejectApprovalDto.getIdRejector())
				.setString("indInCompleteCCLRejection", rejectApprovalDto.getIndInCompleteCCLRejection())
				.setString("txtApproversComments", rejectApprovalDto.getApproversComments()).executeUpdate();

		log.debug("Exiting method saveRejectionApproval of approvalStatisDaoImpl");
	}

	@Override
	public Long getICPCApprovalLevel(Long idEvent) {
		log.debug("Entering method getICPCApprovalLevel of approvalStatisDaoImpl");
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getICPCApprovalLevelsql)
				.addScalar("EventCount", StandardBasicTypes.LONG).setParameter("idEvent", idEvent);
		Long vid = (Long) query.uniqueResult();
		return vid;
	}

	@SuppressWarnings({ "unused", "unchecked" })
	@Override
	public List<ApproversDto> getapproversdtoList(Long idApproval) {

		List<ApproversDto> approversLists = new ArrayList<>();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Approvers.class);
		criteria.add(Restrictions.eq("idApproval", idApproval));
		criteria.list();

		/*
		 * if (!TypeConvUtil.isNullOrEmpty(approvers)) { for (Approvers
		 * tempApprovers : approvers) { if
		 * (!TypeConvUtil.isNullOrEmpty(approvers.getId_Approval())) {
		 * ApproversDtotemp.setIdApprovers(tempApprovers.getId_Approval()); }
		 * 
		 * if (!TypeConvUtil.isNullOrEmpty(tempApprovers.getDtLastUpdate())) {
		 * ApproversDtotemp.setDtLastUpdate(tempApprovers.getDtLastUpdate()); }
		 * 
		 * if (!TypeConvUtil.isNullOrEmpty(tempApprovers.getPerson())) {
		 * ApproversDtotemp.setIdPerson(tempApprovers.getPerson()); }
		 * 
		 * if (!TypeConvUtil.isNullOrEmpty(tempApprovers.getIdTodo())) {
		 * ApproversDtotemp.setIdTodo(tempApprovers.getIdTodo()); }
		 * 
		 * if
		 * (!TypeConvUtil.isNullOrEmpty(tempApprovers.getCdApproversStatus())) {
		 * ApproversDtotemp.setCdApproversStatus(tempApprovers.
		 * getCdApproversStatus()); }
		 * 
		 * if (!TypeConvUtil.isNullOrEmpty(tempApprovers.
		 * getDtApproversDetermination())) {
		 * ApproversDtotemp.setDtApproversDetermination(tempApprovers.
		 * getDtApproversDetermination()); }
		 * 
		 * if
		 * (!TypeConvUtil.isNullOrEmpty(tempApprovers.getDtApproversRequested())
		 * ) { ApproversDtotemp.setDtApproversRequested(tempApprovers.
		 * getDtApproversRequested()); }
		 * 
		 * if
		 * (!TypeConvUtil.isNullOrEmpty(tempApprovers.getDtApproversRequested())
		 * ) { ApproversDtotemp.setDtApproversRequested(tempApprovers.
		 * getDtApproversRequested()); }
		 * 
		 * if
		 * (!TypeConvUtil.isNullOrEmpty(tempApprovers.getIndApproversHistorical(
		 * ))) { ApproversDtotemp.setIndApproversHistorical(tempApprovers.
		 * getIndApproversHistorical()); }
		 * 
		 * if
		 * (!TypeConvUtil.isNullOrEmpty(tempApprovers.getTxtApproversCmnts())) {
		 * ApproversDtotemp.setApproversCmnts(tempApprovers.getTxtApproversCmnts
		 * ()); }
		 * 
		 * if (!TypeConvUtil.isNullOrEmpty(tempApprovers.getCdOvrllDisptn())) {
		 * ApproversDtotemp.setCdOvrllDisptn(tempApprovers.getCdOvrllDisptn());
		 * }
		 * 
		 * if
		 * (!TypeConvUtil.isNullOrEmpty(tempApprovers.getCdStageReasonClosed()))
		 * { ApproversDtotemp.setCdStageReasonClosed(tempApprovers.
		 * getCdStageReasonClosed()); }
		 * 
		 * if (!TypeConvUtil.isNullOrEmpty(tempApprovers.getCdJobClass())) {
		 * ApproversDtotemp.setCdJobClass(tempApprovers.getCdJobClass()); }
		 * 
		 * if
		 * (!TypeConvUtil.isNullOrEmpty(tempApprovers.getIndEmpConfirmedHrmis())
		 * ) { ApproversDtotemp.setIndEmpConfirmedHrmis(tempApprovers.
		 * getIndEmpConfirmedHrmis()); }
		 * 
		 * if
		 * (!TypeConvUtil.isNullOrEmpty(tempApprovers.getDtDeterminationRecorded
		 * ())) { ApproversDtotemp.setDtDeterminationRecorded(tempApprovers.
		 * getDtDeterminationRecorded()); }
		 * 
		 * if (!TypeConvUtil.isNullOrEmpty(tempApprovers.getCdExtnDays())) {
		 * ApproversDtotemp.setCdExtnDays(tempApprovers.getCdExtnDays()); }
		 * 
		 * if
		 * (!TypeConvUtil.isNullOrEmpty(tempApprovers.getIndExtnWithinCwCtr()))
		 * { ApproversDtotemp.setIndExtnWithinCwCtr(tempApprovers.
		 * getIndExtnWithinCwCtr()); }
		 * 
		 * approversLists.add(ApproversDtotemp);
		 * 
		 * } }
		 */

		return approversLists;
	}

	@SuppressWarnings("unused")
	@Override
	public ApprovalStatusRes isApproverLoggedIn(Long idApproval) {

		ApprovalStatusRes approvalStatusRes = new ApprovalStatusRes();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Approvers.class);
		criteria.add(Restrictions.eq("idApproval", idApproval));
		criteria.add(Restrictions.eq("cdApproversStatus", ServiceConstants.PEND));
		List<Approvers> approvers = (List<Approvers>) criteria.list();
		if (!ObjectUtils.isEmpty(approvers))
			approvalStatusRes.setUlIdPerson(approvers.get(0).getPerson().getIdPerson());

		return approvalStatusRes;
	}

	/**
	 * Method Name: getBoardEmail Method Description: This method retrieves
	 * board member's email addresses based on placement county.
	 * 
	 * @param idCase,
	 *            nmStage
	 * @return List<String>
	 */
	@Override
	public List<String> getBoardEmail(Long idCase, String nmStage) {
		SQLQuery sqlQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getBoardEmailsql)
				.addScalar("emailAddress", StandardBasicTypes.STRING).setParameter("idCase", idCase)
				.setParameter("nmStage", nmStage).setResultTransformer(Transformers.aliasToBean(BoardEmailDto.class)));
		List<BoardEmailDto> boardEmailList = (List<BoardEmailDto>) sqlQuery.list();
		List<String> emailList = new ArrayList<>();
		if (!org.springframework.util.CollectionUtils.isEmpty(boardEmailList)) {
			boardEmailList.stream().forEach(boardEmailDto -> {
				if (!ObjectUtils.isEmpty(boardEmailDto.getEmailAddress())) {
					emailList.add(boardEmailDto.getEmailAddress());
				}
			});
		}
		log.info("Exiting method getBoardEmail in ApprovalStatusDaoImpl");
		return emailList;
	}

	/**
	 * Artifact ID: artf151569
	 * Method Name: sessionFlush
	 * Method Description: This method is used to synchronize the data with the Database
	 *
	 */
	@Override
	public void sessionFlush() {
		sessionFactory.getCurrentSession().flush();
	}

	/**
	 * Artifact ID: artf151569
	 * Method Name: fetchCpsPriorOrProgressedStage
	 * Method Description: This method is used to retrieve the open Prior or Progressed stage based on the given cdStage
	 * and idStage
	 *
	 * @param idStage
	 * @param cdStage
	 * @param retrieveProgressedInv
	 * @return
	 */
	@Override
	public List<StageDto> fetchCpsPriorOrProgressedStage(Long idStage, String cdStage, boolean retrieveProgressedInv) {

		List<String> cdStages = new ArrayList<>();
		StringBuffer fetchQuery = new StringBuffer(fetchPriorOrProgressedStage);

		switch (cdStage) {
			case ServiceConstants.CSTAGES_FPR:
				// Retrieves the INV or A-R Stage from where the FPR has been opened
				cdStages.add(ServiceConstants.CSTAGES_INV);
				cdStages.add(ServiceConstants.CSTAGES_AR);
				fetchQuery.append(" SL.ID_PRIOR_STAGE AND S.CD_STAGE IN :cdStages AND SL.ID_STAGE = :idStageFrom");
				break;
			case ServiceConstants.CSTAGES_AR:
				if (retrieveProgressedInv) {
					// Retrieves the INV stage if stage progressed
					cdStages.add(ServiceConstants.CSTAGES_INV);
				} else {
					// Retrieves the FPR stage if if open from the A-R stage
					cdStages.add(ServiceConstants.CSTAGES_FPR);
				}
				fetchQuery.append(" SL.ID_STAGE AND S.CD_STAGE IN :cdStages AND SL.ID_PRIOR_STAGE = :idStageFrom");
				break;
			case ServiceConstants.CSTAGES_INV:
				// Retrieves the FPR stage if opened from INV stage or preceding A-R stage
				cdStages.add(ServiceConstants.CSTAGES_FPR);
				fetchQuery.append(" SL.ID_STAGE AND S.CD_STAGE IN :cdStages AND (SL.ID_PRIOR_STAGE = :idStageFrom OR " +
						"SL.ID_PRIOR_STAGE = ( SELECT ST.ID_STAGE FROM STAGE ST JOIN STAGE_LINK SLL ON " +
						"ST.ID_STAGE = SLL.ID_PRIOR_STAGE AND SLL.ID_STAGE = :idStageFrom AND ST.CD_STAGE = 'A-R' ))");
				break;
			default:
				// If cdStage is anything else apart from FPR, INV, A-R then returns null
				return null;
		}

		log.info("Query for fetching prior or progressed stage - " + fetchQuery.toString());
		//sessionFactory.getCurrentSession().flush();

		Query query = sessionFactory.getCurrentSession().createSQLQuery(fetchQuery.toString())
				.addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("cdStage", StandardBasicTypes.STRING)
				.addScalar("dtStageClose", StandardBasicTypes.DATE)
				.setParameter("idStageFrom", idStage)
				.setParameterList("cdStages", cdStages)
				.setResultTransformer(Transformers.aliasToBean(StageDto.class));

		return (List<StageDto>) query.list();
	}

	/**
	 * This method will update the EMR Status in DB.
	 *
	 * @param emrStatus
	 * @param idStage
	 * @return
	 */
	public long updateEmrStatus(String emrStatus, long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(updateEmrStatusSql);
		query.setParameter("emrStatus", emrStatus);
		query.setParameter("idApsStage", idStage);
		return query.executeUpdate();
	}

	/**
	 * Artifact ID: artf151569
	 * Method Name: retrieveFbssSdmRiskReassessment
	 * Method Description: This method is used to retrieve the latest SDM Risk Reassessment
	 *
	 * @param idStage
	 * @return
	 */
	@Override
	public SDMRiskReasmntDto retrieveFbssSdmRiskReassessment(Long idStage) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(retrieveFbssSdmRiskReassessment)
				.addScalar("dtAsmnt", StandardBasicTypes.DATE)
				.addScalar("idHshldAssessed", StandardBasicTypes.LONG)
				.addScalar("cdRiskLevelDiscOvrride", StandardBasicTypes.STRING)
				.addScalar("cdPlndActn", StandardBasicTypes.STRING)
				.setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(SDMRiskReasmntDto.class));

		return (SDMRiskReasmntDto) query.uniqueResult();
	}

	/**
	 * Returns the ulIdPerson of the Primary Worker of the case. i;e the Person
	 * with role as PRIMARY ("PR").
	 *
	 * @param caseId
	 * @return idPersonPrimaryWorker
	 */
	@SuppressWarnings("unchecked")
	@Override
	public long getPrimaryWorkerIdForCase(Long caseId) {
		long primaryWorkerIdForStage = 0;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Workload.class, "workload");
		criteria.add(Restrictions.eq("workload.id.idWkldCase", caseId));
		criteria.add(Restrictions.eq("workload.id.cdWkldStagePersRole", "PR"));
		criteria.setProjection(Projections.property("workload.id.idWkldPerson"));
		List<Long> primaryWorkerIdForStageList = (List<Long>) criteria.list();
		if (!ObjectUtils.isEmpty(primaryWorkerIdForStageList)) {
			primaryWorkerIdForStage = (long) primaryWorkerIdForStageList.get(0);
		}
		return primaryWorkerIdForStage;
	}



	/**
	 * Method Name: deleteAutEvent Method Description:Method to delete COMPS events
	 * without SVC Auth Links.
	 *
	 * @param caseId
	 * @return Long
	 * @throws DataNotFoundException
	 */
	@Override
	public void deleteAutEvent(Long caseId) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(deleteAutEventSql)
						.setParameter("idCase", caseId);

		query.executeUpdate();

	}

}
