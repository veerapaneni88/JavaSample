package us.tx.state.dfps.service.kinhomeinfo.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.service.admin.dto.EmployeeDto;
import us.tx.state.dfps.service.casepackage.dto.RecordsRetentionDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.kin.dto.KinCaregiverEligibilityDto;
import us.tx.state.dfps.service.kin.dto.KinChildDto;
import us.tx.state.dfps.service.kin.dto.KinHomeInfoDto;
import us.tx.state.dfps.service.kin.dto.KinMonthlyPaymentExtensionDto;
import us.tx.state.dfps.service.kin.dto.KinPaymentEligibilityDto;
import us.tx.state.dfps.service.kin.dto.KinHomeAssessmentDto;
import us.tx.state.dfps.service.subcare.dto.StgPersonLinkDto;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.service.workload.dto.TodoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Interface
 * for KinHomeInfo May 14, 2018- 9:08:40 AM © 2018 Texas Department of Family
 * and Protective Services
 */
public interface KinHomeInfoDao {

	/**
	 * Method Name: getResServiceInfo Method Description:Get Resource Service
	 * Info for given Home Stage ID
	 * 
	 * @param kinHomeInfoDto
	 * @return KinHomeInfoDto
	 * @throws DataNotFoundException
	 */
	public KinHomeInfoDto getResServiceInfo(KinHomeInfoDto kinHomeInfoDto) throws DataNotFoundException;

	public KinHomeInfoDto getKinHomeInfo(Long stageId) throws DataNotFoundException;

	public double getFosterCareRate(String serviceCode, Date paymentStartDate);

	public boolean getIsResourceExists(Long resourceId, String resourceServiceCode);

	public String getKinTrainCompleted(Long stageId);

	public Long getResourceAddressId(Long resourceId);

	public int updateResourceForEligibility(KinHomeInfoDto homeInfoDto);

	public String getKinshipConstants(String nameVariable);

	public KinPaymentEligibilityDto getPaymentEligibilityHistory(Long resourceId, Long placementEventId);

	public List<KinPaymentEligibilityDto> getPaymentEligibilityHistoryForResource(Long resourceId);

	public List<KinCaregiverEligibilityDto> getKinCareEligibilityHistoryForResource(Long resourceId);

	public List<KinChildDto>  getLegalStatusInfo(Long personId);

	public List<Long> getChildrensPid(Long resourceId);

	public Date getMaxServiceAuthTermDate(Long childId, Date legalStartDate);

	public int getContractLineItem(Long contractId, String serviceCode, Date paymentStartDate);

	public Long getSAEventLinkCaseId(Long serviceAuthId);

	public String getChildLivingArrangement(Long childId);

	public Date getPaymentStartDate(Long placementEventId, Long resourceId);

	public boolean isStageOpen(Long serviceAuthId);

	public Date getMinPlacementDate(Long resourceId);

	public Long addContractService(KinHomeInfoDto homeInfoDto, Long contractId, KinHomeInfoDto savedBean,
								  String paymentType,String unitType, String serviceCode,
								   int lineItem, Double rate);

	public Long addResourceService(KinHomeInfoDto homeInfoDto, String serviceCode, KinHomeInfoDto khVb, String indTrain);

	public Long addContract(KinHomeInfoDto homeInfoDto, KinHomeInfoDto savedBean, Long resourceAddressId);

	public Long addContractCounty(KinHomeInfoDto homeInfoDto, Long contractId,
								 KinHomeInfoDto savedBean, String serviceCode, int lineItem);

	public void addContractPeriod(KinHomeInfoDto homeInfoDto, Long contractId);

	public Long addContractVersion(KinHomeInfoDto homeInfoDto, Long contractId);

	public Long saveKinPaymentHistory (KinHomeInfoDto homeInfoDto, KinChildDto childBean, KinPaymentEligibilityDto savedPayBean);

	public boolean isFinalApproval (Long globalUIEventId);

	public int getPendingApprovalCount(Long idApproval);

	public KinMonthlyPaymentExtensionDto getMonthlyExtnBean(Long eventId);

	public Long getContractId(Long resourceId);

	public Long getCaseIdWithPersonId(Long personId);

	public void approveKinMonthlyPayment (KinHomeInfoDto kinHomeInfoDto, Long eventId,
										  Long appEventId, TodoDto todoDto);

	public int updateMonthlyPayment(Long serviceAuthDtlId, Long eventId);

	public Long  insertMonthlyExtensionSADetail(KinMonthlyPaymentExtensionDto childBean,
											   Long serviceAuthId, double rate,
											   int totalUnitsAllowed, int lineItem );

	public Long getCaseIdForHomeInfo(Long personId);

	public String getLegalRegion(Long resourceId);

	public List<String> getCdRegion(Long childId);

	public List<String> getLegalRegionFromContract(Long resourceId, Long contractId);

	public List <KinChildDto> getPlacementInfoList(Long resourceId);

	public Integer getValidContractCount(Long resourceId);

	public boolean getResourceService(Long resourceId, String countyCode, String regionCode,
									  String serviceCode, String state);

	public boolean getContractCountyId(Long contractId, int numContractPeriod, int contractVersionId,
									   String serviceCode, String countryCode, int lineIem);

	public int getLineItemNumber(Long contractId, int numContractPeriod, int contractVersionId,
								 String serviceCode, String paymentType, String unitType);

	public boolean getActiveContractPeriod(Long contractId);

	public boolean getActiveContractVersion(Long contractId, int activeContractPeriodNumber);

	public int updateKinHomeStatus(KinHomeInfoDto homeInfoDto);

	public int updateKinHomeInfrmtnStatus(KinHomeInfoDto homeInfoDto);

	public int closeStage(StageDto stageDto, Date closedDate);

	public StgPersonLinkDto getStagePersonInfo(StgPersonLinkDto stgPersonLinkDto);

	public boolean checkPersonInOpenStage(Long stageId, Long personId);

	public boolean checkOtherStageOpen(Long stageId, Long caseId);

	public int updatePersonStatus(Long personId, String status);

	public Date getCaseClosedDate(Long caseId);

	public EmployeeDto getSelectEmployee(Long personId);

	public int updateStagePersonLink(StgPersonLinkDto stgPersonLinkDto, String personRole);

	public int deleteStagePersonRecords(Long stageId);

	public int updateCase(Date dtCaseClosed, Long caseId);

	public int updateSituation(Date dtSituationClosed, Long caseId);

	public RecordsRetentionDto getRecordsRetention(Long idRecRtnCase);

	public int updateRecordsRetention(RecordsRetentionDto recordsRetentionDto);

	public int savePaymentInfo(KinHomeInfoDto kinHomeInfoDto);

	public KinHomeAssessmentDto getHomeAssessmentOverallStatusDate(Long caseId);

	public KinHomeAssessmentDto getDeclineStatusDate(Long caseId);

	public KinHomeAssessmentDto getIntermediateStatusDate(String  intermediateStatusString, Long idCase);

	public int updateCapsResource(KinHomeAssessmentDto kinHomeAssessmentDto, Long idStage);

	public KinHomeAssessmentDto getLatestStatusCode(Long caseId, Date dateStatusChanged);

	public KinHomeAssessmentDto getStatusDate(Long caseId);
}