package us.tx.state.dfps.service.approval.service;

import java.util.List;

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
import us.tx.state.dfps.service.admin.dto.EmployeeDetailDto;
import us.tx.state.dfps.service.admin.dto.SvcAuthEventLinkInDto;
import us.tx.state.dfps.service.alternativeresponse.dto.ARSafetyAssmtValueDto;
import us.tx.state.dfps.service.alternativeresponse.dto.EventValueDto;
import us.tx.state.dfps.service.casepackage.dto.CaseFileManagementDto;
import us.tx.state.dfps.service.casepackage.dto.StageValueBeanDto;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.SaveApprovalStatusReq;
import us.tx.state.dfps.service.cpsinv.dto.RiskAssmtValueDto;
import us.tx.state.dfps.service.person.dto.AllegationDto;
import us.tx.state.dfps.service.person.dto.EventPersonDto;
import us.tx.state.dfps.service.person.dto.PersonEligibilityValueDto;

public interface ApprovalStatusDaoTestService {

	public Long deleteIncompleteTodos(int idFromStage);

	public void closeStage(StageValueBeanDto commonDto);

	public List<StagePersonValueDto> selectStagePersonLink(int idFromStage, List<String> invRolesList);

	public void updateStagePersonLink(StagePersonValueDto stagePersonValueDto);

	public void deleteStagePersonLink(StagePersonValueDto stagePersonValueDto);

	public void fetchForwardPersonsForStagePersons(Long idStage);

	public Integer updateStagePersonLinks(List<StagePersonValueDto> spLinkBeans);

	public Integer insertIntoStagePersonLinks(List<StagePersonValueDto> stagePersonValueDtoList);

	public List<AllegationDto> fetchIntakeAllegations(StagePersonValueDto stagePersonValueDtoList);

	public Long[] createInvestigationAllegations(Long idToStage, Long idCase, List<AllegationDto> allegationDtoList);

	public int createEvent(EventValueDto eventValueDto);

	public List<SvcAuthEventLinkInDto> getServiceAuthorizationEventDetails(EventValueDto eventValueDto);

	public List<Long> insertIntoServiceAuthorizationEventLinks(
			List<SvcAuthEventLinkInDto> serviceAuthEventLinkValueBeans);

	public List<Long> insertIntoEventPersonLinks(List<EventPersonDto> spLinkBeans);

	public IncomingDetail getincomingDetailbyId(CommonDto commonDto);

	public void updatePriorStageAndIncomingCallDate(CommonDto commonDto);

	public StageValueBeanDto retrieveStageInfo(CommonDto commonDto);

	public ARSafetyAssmtValueDto getARSafetyAssmt(CommonDto commonDto);

	public Long updateINVSafetyAssignmentWithARSafetyAssignment(CommonDto commonDto);

	public RiskAssmtValueDto populateRiskData(CommonDto commonDto);

	public long addRiskAssmtDetails(RiskAssmtValueDto riskAssmtValueDto);

	public long addAreaDetails(long idRiskEvent, RiskAssmtValueDto formFactorBean);

	public long addCategoryDetails(long idRiskEvent, long newRiskAreaId, RiskAssmtValueDto formFactorBean);

	public long addFactorDetails(long idRiskEvent, long newRiskAreaId, long newRiskCategoryId,
			RiskAssmtValueDto formFactorBean);

	public void addFactorDetails(StageValueBeanDto stageValueBeanDto, Long newStageId);

	public CaseFileManagementDto getSelectCaseFileManagement(CaseFileManagementDto caseFileManagementDto);

	public long getEmployeeOfficeIdentifier(CommonDto commonDto);

	public long insertCaseFileManagement(CaseFileManagementDto caseFileManagementDto);

	public long deleteTodosForACase(CommonDto commonDto);

	public List<Long> getActiveStagesForPerson(CommonDto commonDto);

	public List<Long> makePersonsInactive(List<Long> id);

	public Long updateEventStatus(CommonDto commonDto);

	public StageValueBeanDto retrieveStageInfoList(CommonDto commonDto);

	public Boolean getSubStageOpen(SafetyAssmtDto safetyAssmtDto);

	public Long getTodoId(CommonDto commonDto);

	public long getPrimaryWorkerIdForStage(CommonDto commonDto);

	public void updateIndSecondApprover(SaveApprovalStatusReq saveApprovalStatusReq);

	// public List<ApprovalCommonInDto> getEmployeeInfo(SaveApprovalStatusReq
	// saveApprovalStatusReq);

	public void updateApprovers(SaveApprovalStatusReq saveApprovalStatusReq);

	public void closePlacement(ClosePlacementReq closePlacementReq);

	public void updatePersonEligibility(PersonEligibilityValueDto personEligibilityValueDto);

	public Long updateStagePersonLink(CommonHelperReq CommonHelperReq);

	public ContractDto contractAUD(ContractDto contractDto, ServiceReqHeaderDto archInputDto);

	public List<PersonEligibility> getPersonEligibilityByIdPersonAndType(CommonDto commonDto);

	public void updateApprovers(ApprovalStatusFacilityIndicatorDto ApprovalStatusFacilityIndicatorDto);

	public void updateCapsResource(AprvlStatusUpdateCapsResourceReq aprvlStatusUpdateCapsResourceReq);

	public List<SaveApprovalStatusGetCapsRscRes> getCapsRsc(
			SaveApprovalStatusGetCapsRscReq saveApprovalStatusGetCapsRscReq);

	public List<ApprovalStatusResourceHistyRes> fetchResourceHisty(
			ApprovalStatusResourceHistyReq approvalStatusResourceHistyReq);

	public void updateFacilityIndicator(SaveApprovalStatusReq saveApprovalStatusReq);

	public SaveApprovalStatusNmStageRes getNmStage(SaveApprovalStatusNmStageReq saveApprovalStatusNmStageReq);

	public EmployeeDetailDto getEmployeeInfo(SaveApprovalStatusNmStageReq saveApprovalStatusNmStageReq);

	public GetResourceHstryRes getResourceCount(GetResourceHstryReq getResourceHstryReq);

}
