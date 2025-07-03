package us.tx.state.dfps.service.apsintakereport.serviceimpl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.IncmgDetermFactors;
import us.tx.state.dfps.common.dto.ApprovalDto;
import us.tx.state.dfps.common.dto.IncomingStageDetailsDto;
import us.tx.state.dfps.common.dto.NameDto;
import us.tx.state.dfps.common.dto.PhoneInfoDto;
import us.tx.state.dfps.common.dto.WorkerInfoDto;
import us.tx.state.dfps.common.dto.AgencyHomeInfoDto;
import us.tx.state.dfps.common.dto.ClassIntakeDto;
import us.tx.state.dfps.service.apsintakereport.dto.ApsIntakeReportDto;
import us.tx.state.dfps.service.apsintakereport.service.ApsIntakeReportService;
import us.tx.state.dfps.service.casemanagement.dao.CpsIntakeNotificationDao;
import us.tx.state.dfps.service.casemanagement.dao.FetchIncomingFacilityDao;
import us.tx.state.dfps.service.casepackage.dto.RetreiveIncomingFacilityInputDto;
import us.tx.state.dfps.service.casepackage.dto.RetreiveIncomingFacilityOutputDto;
import us.tx.state.dfps.service.casepackage.serviceimpl.CaseSummaryServiceImpl;
import us.tx.state.dfps.service.childplan.dao.ChildServicePlanFormDao;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.ApsIntakeReportReq;
import us.tx.state.dfps.service.cpsintakereport.dao.CpsIntakeReportDao;
import us.tx.state.dfps.service.cpsintakereport.service.CpsIntakeReportService;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.ApsIntakeReportPrefillData;
import us.tx.state.dfps.service.intake.dao.IncmgDtrmFactorsDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.EventDto;
import us.tx.state.dfps.service.person.dto.IntakeAllegationDto;
import us.tx.state.dfps.service.person.dto.PersonAddrLinkDto;
import us.tx.state.dfps.service.workload.dao.StageWorkloadDao;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.RcciMrefDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Service for
 * form CFIN0200 Mar 16, 2018- 3:56:00 PM © 2017 Texas Department of Family and
 * Protective Services
 *  * **********  Change History *********************************
 * 09/28/2020 thompswa artf164166 : Use incoming_detail when Reporter Rel-Int is FPS Staff
 * 05/05/2020 kurmav artf220333 Populate class information, agency information in cfin0200
 */
@Service
@Transactional
public class ApsIntakeReportServiceImpl implements ApsIntakeReportService {

	@Autowired
	private CpsIntakeReportDao cpsIntakeReportDao;

	@Autowired
	private CpsIntakeNotificationDao cpsIntakeNotificationDao;

	@Autowired
	private PersonDao personDao;

	@Autowired
	private IncmgDtrmFactorsDao incmgDtrmFactorsDao;

	@Autowired
	private FetchIncomingFacilityDao fetchIncomingFacilityDao;

	@Autowired
	private ChildServicePlanFormDao childServicePlanFormDao;

	@Autowired
	private ApsIntakeReportPrefillData prefillData;

	@Autowired
	private CpsIntakeReportService cpsIntakeReportService;

	@Autowired
	private StageWorkloadDao stageWorkloadDao;

	/**
	 * Method Name: getIntakeReport Method Description: Gets data for APS Intake
	 * Report and returns prefill data
	 * 
	 * @param apsIntakeReportReq
	 * @return PreFillDataServiceDto @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getIntakeReport(ApsIntakeReportReq apsIntakeReportReq) {
		// Call DAMS
		ApsIntakeReportDto apsIntakeReportDto = callDaos(apsIntakeReportReq, "cfin0200");

		return prefillData.returnPrefillData(apsIntakeReportDto);
	}

	/**
	 * Method Name: getIntakeReportFac Method Description: Gets data for APS
	 * Intake Report Facility and returns prefill data (form CFIN0400)
	 * 
	 * @param apsIntakeReportReq
	 * @return PreFillDataServiceDto @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getIntakeReportFac(ApsIntakeReportReq apsIntakeReportReq) {
		// Call DAMS
		ApsIntakeReportDto apsIntakeReportDto = callDaos(apsIntakeReportReq, "cfin0400");

		return prefillData.returnPrefillData(apsIntakeReportDto);
	}

	/**
	 * Method Name: getIntakeReportNoRep Method Description: Gets data for APS
	 * Intake Report Facility Minus Reporter and returns prefill data (form
	 * CFIN0800)
	 * 
	 * @param apsIntakeReportReq
	 * @return PreFillDataServiceDto @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getIntakeReportNoRep(ApsIntakeReportReq apsIntakeReportReq) {
		// Call DAMS
		ApsIntakeReportDto apsIntakeReportDto = callDaos(apsIntakeReportReq, "cfin0800");

		return prefillData.returnPrefillData(apsIntakeReportDto);
	}

	private ApsIntakeReportDto callDaos(ApsIntakeReportReq apsIntakeReportReq, String formName) {
		// Declare global variables and Prefill dto
		ApsIntakeReportDto apsIntakeReportDto = new ApsIntakeReportDto();
		Long idEvent = ServiceConstants.ZERO_VAL;
		// CINT65D
		IncomingStageDetailsDto incomingStageDetailsDto = cpsIntakeReportDao
				.getStageIncomingDetails(apsIntakeReportReq.getIdStage());
		List<Long> singleCaseIdList = new LinkedList<>();
		singleCaseIdList.add(incomingStageDetailsDto.getIdCase());
		List<RcciMrefDto> mrefDtoList = stageWorkloadDao.getRcciMrefDataByCaseList(singleCaseIdList);
		if (!ObjectUtils.isEmpty(mrefDtoList)) {
			// get the one entry6
			RcciMrefDto mrefDto = mrefDtoList.get(0);
			// apply RCCI Mref data to result based on known thresholds. cciMrefCnt of null means it did not make the threshold.
			incomingStageDetailsDto.setNbrMultiRefInvCnt(
					CaseSummaryServiceImpl.applyRcciMrefThresholds(mrefDto.getRcciMrefCnt(), incomingStageDetailsDto.getCdStageProgram(),
							mrefDto.getNbrRsrcFacilCapacity(), mrefDto.getCdRsrcFacilType()));
		}

		if (!ObjectUtils.isEmpty(incomingStageDetailsDto)) {
			// only perform this check if cfin0200
			if ((ServiceConstants.CAPS_UNIT_SWI.equals(incomingStageDetailsDto.getCdStageReasonClosed())
					|| ServiceConstants.PRIMARY_ADDRESS_TYPE.equals(incomingStageDetailsDto.getCdStageReasonClosed())
					|| ServiceConstants.AGENCY_LINK_TYPE.equals(incomingStageDetailsDto.getCdStageReasonClosed()))
					&& "cfin0200".equals(formName)) {
				incomingStageDetailsDto.setCdStageReasonClosed(ServiceConstants.EMPTY_STR);
			}
			idEvent = incomingStageDetailsDto.getIdEvent();
			apsIntakeReportDto.setIncomingStageDetailsDto(incomingStageDetailsDto);

			// CINT66D - Victims
			List<PersonDto> victimsList = cpsIntakeReportDao.getPersonList(apsIntakeReportReq.getIdStage(),
					ServiceConstants.VICTIM_TYPE);
			if (!ObjectUtils.isEmpty(victimsList)) {
				apsIntakeReportDto.setVictimsList(victimsList);
			}

			// CINT66D - Perpetrators
			List<PersonDto> perpsList = cpsIntakeReportDao.getPersonList(apsIntakeReportReq.getIdStage(),
					ServiceConstants.PERPETRATOR_TYPE);
			if (!ObjectUtils.isEmpty(perpsList)) {
				apsIntakeReportDto.setPerpsList(perpsList);
			}

			// CINT66D - Other Principles
			List<PersonDto> othersList = cpsIntakeReportDao.getPersonList(apsIntakeReportReq.getIdStage(),
					ServiceConstants.OTHER_PRN_TYPE);
			if (!ObjectUtils.isEmpty(othersList)) {
				apsIntakeReportDto.setOthersList(othersList);
			}

			// Don't call this DAM (CINT66D - Reporters) for cfin0800
			if (!"cfin0800".equals(formName)) {
				List<PersonDto> reporters = new ArrayList<PersonDto>();
				reporters = cpsIntakeReportDao.getPersonList(apsIntakeReportReq.getIdStage(), ServiceConstants.REPORTER_TYPE);
				reporters = cpsIntakeReportService.getFpsStaffReporter(reporters, incomingStageDetailsDto); // artf164166 If has FPS Staff reporter, add from incoming details
				if (!ObjectUtils.isEmpty(reporters)) {
					apsIntakeReportDto.setReportersList(reporters);
				}
			}

			// CINT66D - Collaterals
			List<PersonDto> collateralsList = cpsIntakeReportDao.getPersonList(apsIntakeReportReq.getIdStage(),
					ServiceConstants.COLLATERAL_TYPE);
			if (!ObjectUtils.isEmpty(collateralsList)) {
				apsIntakeReportDto.setCollateralsList(collateralsList);
			}

			// CINT62D
			List<PhoneInfoDto> phoneInfoList = cpsIntakeReportDao.getPhoneInfo(apsIntakeReportReq.getIdStage());
			if (!ObjectUtils.isEmpty(phoneInfoList)) {
				apsIntakeReportDto.setPhoneInfoList(phoneInfoList);
			}

			// CINT63D
			List<PersonAddrLinkDto> personAddrLinkList = cpsIntakeNotificationDao
					.getAddrInfoByStageId(apsIntakeReportReq.getIdStage());
			if (!ObjectUtils.isEmpty(personAddrLinkList)) {
				apsIntakeReportDto.setPersonAddrLinkList(personAddrLinkList);
			}

			// CINT19D
			List<IntakeAllegationDto> intakeAllegationList = personDao
					.getIntakeAllegationByStageId(apsIntakeReportReq.getIdStage());
			if (!ObjectUtils.isEmpty(intakeAllegationList)) {
				apsIntakeReportDto.setIntakeAllegationList(intakeAllegationList);
			}

			// CINT15D
			List<IncmgDetermFactors> incmgDetermFactorsList = incmgDtrmFactorsDao
					.getincmgDetermFactorsById(apsIntakeReportReq.getIdStage());
			if (!ObjectUtils.isEmpty(incmgDetermFactorsList)) {
				apsIntakeReportDto.setIncmgDetermFactorsList(incmgDetermFactorsList);
			}

			// CINT09D
			RetreiveIncomingFacilityInputDto inputDto = new RetreiveIncomingFacilityInputDto();
			inputDto.setIdStage(apsIntakeReportReq.getIdStage());
			RetreiveIncomingFacilityOutputDto outputDto = new RetreiveIncomingFacilityOutputDto();
			fetchIncomingFacilityDao.fetchIncomingFacility(inputDto, outputDto);
			if (!ObjectUtils.isEmpty(outputDto)) {
				apsIntakeReportDto.setIncomingFacilityDto(outputDto);
			}
			// CINT64D
			List<NameDto> namesList = cpsIntakeNotificationDao.getNameAliases(apsIntakeReportReq.getIdStage());
			if (!ObjectUtils.isEmpty(namesList)) {
				apsIntakeReportDto.setNamesList(namesList);
			}
			// Don't call these DAMs for cfin0800
			if (!"cfin0800".equals(formName)) {

				// CCMN45D
				if(!ObjectUtils.isEmpty(idEvent)) {
				List<EventDto> eventDetailsList = childServicePlanFormDao.fetchEventDetails(idEvent);
				if (!ObjectUtils.isEmpty(eventDetailsList)) {
					apsIntakeReportDto.setEventDetailsList(eventDetailsList);
				}
				

				// CLSC52D
				if (ServiceConstants.EVENT_STATUS_APRV.equalsIgnoreCase(eventDetailsList.get(0).getCdEventStatus())) {
					List<ApprovalDto> approvalList = cpsIntakeReportDao.getApprovalList(idEvent);

					if (!ObjectUtils.isEmpty(approvalList)) {
						apsIntakeReportDto.setApprovalList(approvalList);
					}
				}
				}
			}

			// Don't call this DAM for cfin0400
			if (!"cfin0400".equals(formName)) {
				// CINT67D
				List<WorkerInfoDto> workerInfoList = cpsIntakeReportDao.getWorkerInfo(apsIntakeReportReq.getIdStage(),
						ServiceConstants.STATUS_STG);
				if (!ObjectUtils.isEmpty(workerInfoList)) {
					apsIntakeReportDto.setWorkerInfoList(workerInfoList);
				}
			}

			// Call this DAM only for form CFIN0200
			if ("cfin0200".equals(formName)) {
				// CINT68D
				apsIntakeReportDto.setPriorityChangeInfoList(
						cpsIntakeReportDao.getPriorityChangeInfo(apsIntakeReportReq.getIdStage()));
                // artf220333
				if (!ObjectUtils.isEmpty(incomingStageDetailsDto.getIdCase())){
					ClassIntakeDto intakeDto = cpsIntakeReportDao.getIntakeClassData(incomingStageDetailsDto.getIdCase());
					apsIntakeReportDto.setIntakeDto(intakeDto);
					AgencyHomeInfoDto resourceInfoDto = null;
					if (!ObjectUtils.isEmpty(outputDto) && !ObjectUtils.isEmpty(outputDto.getIdResource()) && 0L < outputDto.getIdResource()) {
						resourceInfoDto = cpsIntakeReportDao.getResourceInfoDto(outputDto.getIdResource());
						apsIntakeReportDto.setResourceInfoDto(resourceInfoDto);
					}
					// get the agency home info if incoming facility is "Child Placing Agency"
					if (!ObjectUtils.isEmpty(intakeDto) && !ObjectUtils.isEmpty(intakeDto.getFacilityId())
							&& !ObjectUtils.isEmpty(outputDto) && CodesConstant.CFACTYP2_60.equalsIgnoreCase(outputDto.getCdIncmgFacilType())) {
						AgencyHomeInfoDto agencyHomeInfoDto = cpsIntakeReportDao.getAgencyHomeInfoDto(intakeDto.getFacilityId());
						apsIntakeReportDto.setAgencyHomeInfoDto(agencyHomeInfoDto);
					}
				}
			}
		}

		// Send form name for reference in prefill data
		apsIntakeReportDto.setFormName(formName);

		return apsIntakeReportDto;
	}

}
