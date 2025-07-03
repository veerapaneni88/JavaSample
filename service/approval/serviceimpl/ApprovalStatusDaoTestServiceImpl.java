package us.tx.state.dfps.service.approval.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.approval.dto.ApprovalStatusFacilityIndicatorDto;
import us.tx.state.dfps.approval.dto.ApprovalStatusResourceHistyReq;
import us.tx.state.dfps.approval.dto.ApprovalStatusResourceHistyRes;
import us.tx.state.dfps.approval.dto.AprvlStatusUpdateCapsResourceReq;
import us.tx.state.dfps.approval.dto.ClosePlacementReq;
import us.tx.state.dfps.approval.dto.GetResourceHstryReq;
import us.tx.state.dfps.approval.dto.GetResourceHstryRes;
import us.tx.state.dfps.approval.dto.SaveApprovalStatusGetCapsRscReq;
import us.tx.state.dfps.approval.dto.SaveApprovalStatusGetCapsRscRes;
import us.tx.state.dfps.approval.dto.SaveApprovalStatusNmStageReq;
import us.tx.state.dfps.approval.dto.SaveApprovalStatusNmStageRes;
import us.tx.state.dfps.common.domain.IncomingDetail;
import us.tx.state.dfps.common.domain.PersonEligibility;
import us.tx.state.dfps.common.dto.CommonDto;
import us.tx.state.dfps.common.dto.ContractDto;
import us.tx.state.dfps.common.dto.SafetyAssmtDto;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.common.dto.StagePersonValueDto;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dto.EmployeeDetailDto;
import us.tx.state.dfps.service.admin.dto.SvcAuthEventLinkInDto;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtValueDto;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.approval.dao.ApprovalStatusDao;
import us.tx.state.dfps.service.approval.dao.ApprovalStatusResourceHistyDao;
import us.tx.state.dfps.service.approval.dao.ApprovalStatusUpdateCapsResourceDao;
import us.tx.state.dfps.service.approval.dao.AprvlStatusGetNmStageDao;
import us.tx.state.dfps.service.approval.dao.ClosePlacementDao;
import us.tx.state.dfps.service.approval.dao.GetCapsResourcesDao;
import us.tx.state.dfps.service.approval.dao.GetResourceCountDao;
import us.tx.state.dfps.service.approval.service.ApprovalStatusDaoTestService;
import us.tx.state.dfps.service.arreport.dao.ArReportDao;
import us.tx.state.dfps.service.arstageprog.dao.ArStageProgDao;
import us.tx.state.dfps.service.casepackage.dao.ServiceAuthorizationDao;
import us.tx.state.dfps.service.casepackage.dto.CaseFileManagementDto;
import us.tx.state.dfps.service.casepackage.dto.StageValueBeanDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.IncomingDetailDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.SaveApprovalStatusReq;
import us.tx.state.dfps.service.cpsinv.dto.RiskAssmtValueDto;
import us.tx.state.dfps.service.financial.dao.SvcAuthDetailDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dao.PersonEligibilityDao;
import us.tx.state.dfps.service.person.dto.AllegationDto;
import us.tx.state.dfps.service.person.dto.EventPersonDto;
import us.tx.state.dfps.service.person.dto.PersonEligibilityValueDto;
import us.tx.state.dfps.service.placement.dao.ContractDao;
import us.tx.state.dfps.service.riskassessment.dao.RiskAssessmentDao;
import us.tx.state.dfps.service.stageprogression.service.StageProgressionService;
import us.tx.state.dfps.service.workload.dao.StageProgDao;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dao.WorkLoadDao;

@Service
@Transactional
public class ApprovalStatusDaoTestServiceImpl implements ApprovalStatusDaoTestService {

	@Autowired
	TodoDao todoDao;

	@Autowired
	ApprovalStatusUpdateCapsResourceDao approvalStatusUpdateCapsResourceDao;

	@Autowired
	ArStageProgDao arStageProgDao;

	@Autowired
	StageProgDao stageProgDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	SvcAuthDetailDao svcAuthDetailDao;

	@Autowired
	ServiceAuthorizationDao serviceAuthorizationDao;

	@Autowired
	IncomingDetailDao incomingDetailDao;

	@Autowired
	ArReportDao arReportDao;

	@Autowired
	StageProgressionService stageProgressionService;

	@Autowired
	RiskAssessmentDao riskAssessmentDao;

	@Autowired
	ApprovalStatusDao approvalStatusDao;

	@Autowired
	EmployeeDao employeeDao;

	@Autowired
	WorkLoadDao workLoadDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	ClosePlacementDao ClosePlacementDao;

	@Autowired
	ContractDao contractDao;

	@Autowired
	PersonEligibilityDao personEligibilityDao;

	@Autowired
	GetCapsResourcesDao GetCapsResourcesDao;

	@Autowired
	ApprovalStatusResourceHistyDao approvalStatusResourceHistyDao;

	@Autowired
	AprvlStatusGetNmStageDao AprvlStatusGetNmStageDao;

	@Autowired
	GetResourceCountDao GetResourceCountDao;

	/**
	 * 
	 * Method Name: getSSCCReferalForIdPersonDC Method Description: Get the
	 * Active Placement Referral for Day care Request
	 * 
	 * @param idEvent
	 * @return Long
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long deleteIncompleteTodos(int idFromStage) {
		todoDao.deleteIncompleteTodos(idFromStage);
		return null;
	}

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void closeStage(StageValueBeanDto commonDto) {
		arStageProgDao.closeStage(commonDto);
	}

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<StagePersonValueDto> selectStagePersonLink(int idFromStage, List<String> invRolesList) {
		List<StagePersonValueDto> StagePersonValueDto;
		StagePersonValueDto = stageProgDao.selectStagePersonLink(idFromStage, invRolesList);
		return StagePersonValueDto;
	}

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void updateStagePersonLink(StagePersonValueDto stagePersonValueDto) {
		arStageProgDao.updateStagePersonLink(stagePersonValueDto);
	}

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void deleteStagePersonLink(StagePersonValueDto stagePersonValueDto) {
		stageDao.deleteStagePersonLink(stagePersonValueDto);
	}

	/**
	 * public Map<Integer, Integer> fetchForwardPersonsForStagePersons(Long
	 * idStage) throws DataNotFoundException {
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void fetchForwardPersonsForStagePersons(Long idStage) {
		arStageProgDao.fetchForwardPersonsForStagePersons(idStage);
	}

	/**
	 * public Integer updateStagePersonLinks(List<StagePersonValueDto>
	 * spLinkBeans) {
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Integer updateStagePersonLinks(List<StagePersonValueDto> spLinkBeans) {
		return stageProgDao.updateStagePersonLinks(spLinkBeans);
	}

	/**
	 * insertIntoStagePersonLinks
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Integer insertIntoStagePersonLinks(List<StagePersonValueDto> stagePersonValueDtoList) {
		return stageProgDao.updateStagePersonLinks(stagePersonValueDtoList);
	}

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<AllegationDto> fetchIntakeAllegations(StagePersonValueDto stagePersonValueDtoList) {
		return arStageProgDao.fetchIntakeAllegations(stagePersonValueDtoList.getIdStage().longValue());
	}

	/**
	 * public Long[] createInvestigationAllegations(Long idToStage, Long idCase,
	 * List<AllegationDto> allegationDtoList)
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long[] createInvestigationAllegations(Long idToStage, Long idCase, List<AllegationDto> allegationDtoList) {
		return arStageProgDao.createInvestigationAllegations(idToStage, idCase, allegationDtoList);
	}

	/**
	 * public int createEvent(EventValueDto eventValueDto)
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public int createEvent(EventValueDto eventValueDto) {
		return arStageProgDao.createEvent(eventValueDto);
	}

	/**
	 * public List<SvcAuthEventLinkInDto>
	 * getServiceAuthorizationEventDetails(Long idCase, Long idStage, String
	 * eventTypeCode)
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<SvcAuthEventLinkInDto> getServiceAuthorizationEventDetails(EventValueDto eventValueDto) {
		return svcAuthDetailDao.getServiceAuthorizationEventDetails(eventValueDto.getIdCase(),
				eventValueDto.getIdStage(), ServiceConstants.SUB_SVC_AUTH_TASK);
	}

	/**
	 * public List<Long> insertIntoServiceAuthorizationEventLinks(
	 * List<SvcAuthEventLinkInDto> serviceAuthEventLinkValueBeans)
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<Long> insertIntoServiceAuthorizationEventLinks(
			List<SvcAuthEventLinkInDto> serviceAuthEventLinkValueBeans) {
		return serviceAuthorizationDao.insertIntoServiceAuthorizationEventLinks(serviceAuthEventLinkValueBeans);
	}

	/**
	 * public List<Long> insertIntoEventPersonLinks(List<EventPersonDto>
	 * spLinkBeans) throws DataNotFoundException {
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<Long> insertIntoEventPersonLinks(List<EventPersonDto> spLinkBeans) {
		return serviceAuthorizationDao.insertIntoEventPersonLinks(spLinkBeans);
	}

	/**
	 * public IncomingDetail getincomingDetailbyId(Long idStage)
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public IncomingDetail getincomingDetailbyId(CommonDto commonDto) {
		return incomingDetailDao.getincomingDetailbyId(commonDto.getIdStage());
	}

	/**
	 * public void updatePriorStageAndIncomingCallDate(Long idNewStage, Long
	 * idCase, Long idPriorStage)
	 */

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void updatePriorStageAndIncomingCallDate(CommonDto commonDto) {
		arStageProgDao.updatePriorStageAndIncomingCallDate(commonDto.getIdStage(), commonDto.getIdCase(),
				commonDto.getIdStage(), new java.sql.Timestamp(commonDto.getDtStart().getTime()));
		return;
	}

	/**
	 * public StageValueBeanDto retrieveStageInfo(Long idStage)
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public StageValueBeanDto retrieveStageInfo(CommonDto commonDto) {
		StageValueBeanDto StageValueBeanDto = new StageValueBeanDto();
		StageValueBeanDto = stageDao.retrieveStageInfo(commonDto.getIdStage());
		return StageValueBeanDto;
	}

	/**
	 * public ARSafetyAssmtValueDto getARSafetyAssmt(Integer idStage, String
	 * cdAssmtType, Integer idUser)
	 * 
	 * arReportDao.getARSafetyAssmt(idFromStage,ServiceConstants.AR_INITIAL_SAFETY_ASSESSMENT_INDICATOR,
	 * idUser)
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ARSafetyAssmtValueDto getARSafetyAssmt(CommonDto commonDto) {
		ARSafetyAssmtValueDto aRSafetyAssmtValueDto = new ARSafetyAssmtValueDto();
		aRSafetyAssmtValueDto = arReportDao.getARSafetyAssmt(commonDto.getIdStage().intValue(),
				ServiceConstants.AR_INITIAL_SAFETY_ASSESSMENT_INDICATOR, commonDto.getIdUser().intValue());
		return aRSafetyAssmtValueDto;
	}

	/**
	 * public Long updateINVSafetyAssignmentWithARSafetyAssignment(Long
	 * idARSafetyAssessment, Long idCase, Long idStage)
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long updateINVSafetyAssignmentWithARSafetyAssignment(CommonDto commonDto) {
		Long size = stageProgDao.updateINVSafetyAssignmentWithARSafetyAssignment(commonDto.getIdNewEvent(),
				commonDto.getIdCase(), commonDto.getIdStage());
		return size;
	}

	/**
	 * riskAssessmentDao.populateRiskData(baseAddRiskAssmtValueDto.getIdCase(),
	 * baseAddRiskAssmtValueDto.getIdStage()) service
	 * 
	 * public RiskAssmtValueDto populateRiskData(long idCase, long idStage) dao
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public RiskAssmtValueDto populateRiskData(CommonDto commonDto) {
		return riskAssessmentDao.populateRiskData(commonDto.getIdCase(), commonDto.getIdStage());
	}

	/**
	 * public long addRiskAssmtDetails(RiskAssmtValueDto riskAssmtValueDto)
	 * 
	 * riskAssessmentDao.addRiskAssmtDetails(riskAssmtValueBean);
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public long addRiskAssmtDetails(RiskAssmtValueDto riskAssmtValueDto) {
		return riskAssessmentDao.addRiskAssmtDetails(riskAssmtValueDto);
	}

	/**
	 * riskAssessmentDao.addAreaDetails(idRiskEvent, riskAssmtValueBean);
	 * 
	 * public long addAreaDetails(long idRiskEvent, RiskAssmtValueDto
	 * formFactorBean)
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public long addAreaDetails(long idRiskEvent, RiskAssmtValueDto formFactorBean) {
		return riskAssessmentDao.addAreaDetails(idRiskEvent, formFactorBean);
	}

	/**
	 * riskAssessmentDao.addCategoryDetails(idRiskEvent, newRiskAreaId,
	 * riskAssmtValueBean)
	 * 
	 * public long addCategoryDetails(long idRiskEvent, long newRiskAreaId,
	 * RiskAssmtValueDto formFactorBean)
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public long addCategoryDetails(long idRiskEvent, long newRiskAreaId, RiskAssmtValueDto formFactorBean) {
		return riskAssessmentDao.addCategoryDetails(idRiskEvent, newRiskAreaId, formFactorBean);
	}

	/**
	 * riskAssessmentDao.addFactorDetails(idRiskEvent, newRiskAreaId,
	 * newRiskCategoryId, riskAssmtValueBean)
	 * 
	 * public long addFactorDetails(long idRiskEvent, long newRiskAreaId, long
	 * newRiskCategoryId, RiskAssmtValueDto formFactorBean)
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public long addFactorDetails(long idRiskEvent, long newRiskAreaId, long newRiskCategoryId,
			RiskAssmtValueDto formFactorBean) {
		return riskAssessmentDao.addFactorDetails(idRiskEvent, newRiskAreaId, newRiskCategoryId, formFactorBean);
	}

	/**
	 * stageProgDao.updateStageLink(stageCreationValBean, newStageId);
	 * 
	 * 
	 * public void updateStageLink(StageValueBeanDto stageValueBeanDto, Long
	 * newStageId)
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void addFactorDetails(StageValueBeanDto stageValueBeanDto, Long newStageId) {
		stageProgDao.updateStageLink(stageValueBeanDto, newStageId);
		return;
	}

	/**
	 * approvalStatusDao.getSelectCaseFileManagement(inputCaseFileMgmtValueBean)
	 * 
	 * public CaseFileManagementDto
	 * getSelectCaseFileManagement(CaseFileManagementDto caseFileManagementDto)
	 * 
	 * @return
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CaseFileManagementDto getSelectCaseFileManagement(CaseFileManagementDto caseFileManagementDto) {
		CaseFileManagementDto caseFileManagementDto1 = new CaseFileManagementDto();
		caseFileManagementDto1 = approvalStatusDao.getSelectCaseFileManagement(caseFileManagementDto);
		return caseFileManagementDto1;
	}

	/**
	 * employeeDao.getEmployeeOfficeIdentifier(Long.valueOf(idUser))
	 * 
	 * public Long getEmployeeOfficeIdentifier(Long idEmployee)
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public long getEmployeeOfficeIdentifier(CommonDto commonDto) {
		long res = 0L;
		res = employeeDao.getEmployeeOfficeIdentifier(commonDto.getIdPerson());
		return res;
	}

	/**
	 * approvalStatusDao.insertCaseFileManagement(caseFileMgmtValueBean); public
	 * Long insertCaseFileManagement(CaseFileManagementDto
	 * caseFileManagementDto) throws DataNotFoundException {
	 * 
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public long insertCaseFileManagement(CaseFileManagementDto caseFileManagementDto) {
		long res = 0L;
		res = approvalStatusDao.insertCaseFileManagement(caseFileManagementDto);
		return res;
	}

	/**
	 * todoDao.deleteTodosForACase (Long.valueOf(idCase));
	 * 
	 * public Long deleteTodosForACase(Long idCase) throws DataNotFoundException
	 * {
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public long deleteTodosForACase(CommonDto commonDto) {
		long res = 0L;
		res = todoDao.deleteTodosForACase(commonDto.getIdCase());
		return res;
	}

	/**
	 * workLoadDao.getActiveStagesForPerson(arPersonValueBean.getIdPerson())
	 * 
	 * public List<Long> getActiveStagesForPerson(Long idPerson) {
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<Long> getActiveStagesForPerson(CommonDto commonDto) {
		List<Long> res = null;
		res = workLoadDao.getActiveStagesForPerson(commonDto.getIdPerson());
		return res;
	}

	/**
	 * personDao.makePersonsInactive(inactiveList);
	 * 
	 * public List<Long> makePersonsInactive(List<Long> persons)
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<Long> makePersonsInactive(List<Long> id) {
		List<Long> res = null;
		res = personDao.makePersonsInactive(id);
		return res;
	}

	/**
	 * arReportDao.getARSafetyAssmt(idFromStage,
	 * ServiceConstants.AR_INITIAL_SAFETY_ASSESSMENT_INDICATOR, idUser)
	 * 
	 * public ARSafetyAssmtValueDto getARSafetyAssmt(Integer idStage, String
	 * cdAssmtType, Integer idUser)
	 *//*
		 * @Override
		 * 
		 * @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED,
		 * propagation = Propagation.REQUIRED) public ARSafetyAssmtValueDto
		 * getARSafetyAssmt(CommonDto commonDto) {
		 * 
		 * return
		 * arReportDao.getARSafetyAssmt(commonDto.getIdStage().intValue(),
		 * ServiceConstants.AR_INITIAL_SAFETY_ASSESSMENT_INDICATOR,
		 * commonDto.getIdUser().intValue()); }
		 */

	@SuppressWarnings("null")
	/**
	 * approvalStatusDao.updateEventStatus(arSafetyAssmtValueDto.getIdEvent(),
	 * ServiceConstants.CEVTSTAT_COMP);
	 * 
	 * public long updateEventStatus(int idEvent, String cdEventStatus) throws
	 * DataNotFoundException {
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long updateEventStatus(CommonDto commonDto) {
		long res = (Long) null;
		res = approvalStatusDao.updateEventStatus(commonDto.getIdEvent(), commonDto.getCdEventStatus());
		return res;
	}

	/**
	 * stageDao.retrieveStageInfoList(stageValueBeanDto.getIdStage()
	 * 
	 * public StageValueBeanDto retrieveStageInfoList(long idARStage)
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public StageValueBeanDto retrieveStageInfoList(CommonDto commonDto) {
		StageValueBeanDto res = new StageValueBeanDto();
		res = stageDao.retrieveStageInfoList(commonDto.getIdStage());
		return res;
	}

	/**
	 * stageProgDao.getSubStageOpen(returnSftAssmtBean)
	 * 
	 * public Boolean getSubStageOpen(SafetyAssmtDto safetyAssmtDto)
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Boolean getSubStageOpen(SafetyAssmtDto safetyAssmtDto) {
		return stageProgDao.getSubStageOpen(safetyAssmtDto);
	}

	/**
	 * (stageProgDao.getTodoId(returnSftAssmtBean.getEventId()) public Long
	 * getTodoId(Long eventId)
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long getTodoId(CommonDto commonDto) {
		return stageProgDao.getTodoId(commonDto.getIdEvent());
	}

	/**
	 * approvalStatusDao.getPrimaryWorkerIdForStage(Long.valueOf(idFromStage);
	 * public long getPrimaryWorkerIdForStage(Long ulIdStage)
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public long getPrimaryWorkerIdForStage(CommonDto commonDto) {
		Long res;
		res = approvalStatusDao.getPrimaryWorkerIdForStage(commonDto.getIdEvent());
		return res;
	}

	// //public void updateIndSecondApprover(SaveApprovalStatusReq
	// saveApprovalStatusReq) {
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void updateIndSecondApprover(SaveApprovalStatusReq saveApprovalStatusReq) {
		approvalStatusDao.updateIndSecondApprover(saveApprovalStatusReq);
		return;
	}

	// public List<ApprovalCommonInDto> getEmployeeInfo(SaveApprovalStatusReq
	// saveApprovalStatusReq)

	/*
	 * @Override
	 * 
	 * @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED,
	 * propagation = Propagation.REQUIRED) public List<ApprovalCommonInDto>
	 * getEmployeeInfo(SaveApprovalStatusReq saveApprovalStatusReq) { return
	 * approvalStatusDao.getEmployeeInfo(saveApprovalStatusReq); }
	 */

	// public void updateApprovers(SaveApprovalStatusReq saveApprovalStatusReq)
	// {
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void updateApprovers(SaveApprovalStatusReq saveApprovalStatusReq) {
		// approvalStatusDao.updateApprovers(saveApprovalStatusReq);
		return;
	}

	// public void closePlacement(ClosePlacementReq closePlacementReq);
	/**
	 * testing Dao Call for tuxedo CCMN35S
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void closePlacement(ClosePlacementReq closePlacementReq) {
		ClosePlacementDao.closePlacement(closePlacementReq);
		return;
	}

	// public void updatePersonEligibility(PersonEligibilityValueDto
	// personEligibilityValueDto)
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void updatePersonEligibility(PersonEligibilityValueDto personEligibilityValueDto) {
		arStageProgDao.updatePersonEligibility(personEligibilityValueDto);
		return;
	}

	// arStageProgDao.updateStagePersonLink(iSPValueBean.getIdPerson(),
	// Long.valueOf(idApprover),
	// Long.valueOf(newStageId),
	// Long.valueOf(idCase),ServiceConstants.CROLEALL_PR);
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long updateStagePersonLink(CommonHelperReq CommonHelperReq) {
		return arStageProgDao.updateStagePersonLink(CommonHelperReq.getIdPerson(), CommonHelperReq.getIdApproval(),
				CommonHelperReq.getIdEvent(), CommonHelperReq.getIdCase(), ServiceConstants.CROLEALL_PR);
	}

	// public ContractDto contractAUD(ContractDto contractDto,
	// ServiceReqHeaderDto archInputDto)

	@Override
	public ContractDto contractAUD(ContractDto contractDto, ServiceReqHeaderDto archInputDto) {
		return contractDao.contractAUD(contractDto, archInputDto);
	}

	// public List<PersonEligibility> getPersonEligibilityByIdPersonAndType(Long
	// idPerson, String eligType);
	@Override
	public List<PersonEligibility> getPersonEligibilityByIdPersonAndType(CommonDto commonDto) {
		return personEligibilityDao.getPersonEligibilityByIdPersonAndType(commonDto.getIdPerson(),
				commonDto.getPersonType());
	}

	// public void updateApprovers(ApprovalStatusFacilityIndicatorDto
	// approvalStatusFacilityIndicatorDto)
	@Override
	public void updateApprovers(ApprovalStatusFacilityIndicatorDto approvalStatusFacilityIndicatorDto) {
		approvalStatusDao.updateApprovers(approvalStatusFacilityIndicatorDto);
	}

	// public void updateCapsResource(AprvlStatusUpdateCapsResourceReq
	// aprvlStatusUpdateCapsResourceReq)
	@Override
	public void updateCapsResource(AprvlStatusUpdateCapsResourceReq aprvlStatusUpdateCapsResourceReq) {
		approvalStatusUpdateCapsResourceDao.updateCapsResource(aprvlStatusUpdateCapsResourceReq);
	}

	// public List<SaveApprovalStatusGetCapsRscRes>
	// getCapsRsc(SaveApprovalStatusGetCapsRscReq
	// saveApprovalStatusGetCapsRscReq)
	@Override
	public List<SaveApprovalStatusGetCapsRscRes> getCapsRsc(
			SaveApprovalStatusGetCapsRscReq saveApprovalStatusGetCapsRscReq) {
		return GetCapsResourcesDao.getCapsRsc(saveApprovalStatusGetCapsRscReq);
	}

	// public List<ApprovalStatusResourceHistyRes>
	// fetchResourceHisty(ApprovalStatusResourceHistyReq
	// approvalStatusResourceHistyReq)
	@Override
	public List<ApprovalStatusResourceHistyRes> fetchResourceHisty(
			ApprovalStatusResourceHistyReq approvalStatusResourceHistyReq) {
		return approvalStatusResourceHistyDao.fetchResourceHisty(approvalStatusResourceHistyReq);
	}

	@Override
	public void updateFacilityIndicator(SaveApprovalStatusReq saveApprovalStatusReq) {
		approvalStatusDao.updateFacilityIndicator(saveApprovalStatusReq);
	}

	@Override
	public SaveApprovalStatusNmStageRes getNmStage(SaveApprovalStatusNmStageReq saveApprovalStatusNmStageReq) {
		return AprvlStatusGetNmStageDao.getNmStage(saveApprovalStatusNmStageReq);
	}

	// public EmployeeDetailDto getEmployeeInfo(Long idPerson)
	@Override
	public EmployeeDetailDto getEmployeeInfo(SaveApprovalStatusNmStageReq saveApprovalStatusNmStageReq) {

		return approvalStatusDao.getEmployeeInfo(saveApprovalStatusNmStageReq.getIdStage());
	}

	// public GetResourceHstryRes getResourceCount(GetResourceHstryReq
	// getResourceHstryReq)
	@Override
	public GetResourceHstryRes getResourceCount(GetResourceHstryReq getResourceHstryReq) {
		return GetResourceCountDao.getResourceCount(getResourceHstryReq);
	}
}
