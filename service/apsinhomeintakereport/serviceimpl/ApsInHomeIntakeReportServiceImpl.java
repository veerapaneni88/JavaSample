package us.tx.state.dfps.service.apsinhomeintakereport.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.IncmgDetermFactors;
import us.tx.state.dfps.common.dto.*;
import us.tx.state.dfps.service.apsinhomeintakereport.dto.ApsInHomeIntakeReportDto;
import us.tx.state.dfps.service.apsinhomeintakereport.service.ApsInHomeIntakeReportService;
import us.tx.state.dfps.service.casemanagement.dao.CpsIntakeNotificationDao;
import us.tx.state.dfps.service.childplan.dao.ChildServicePlanFormDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.common.util.mobile.MobileUtil;
import us.tx.state.dfps.service.cpsintakereport.dao.CpsIntakeReportDao;
import us.tx.state.dfps.service.cpsintakereport.service.CpsIntakeReportService;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.ApsInHomeIntakeReportPrefillData;
import us.tx.state.dfps.service.intake.dao.IncmgDtrmFactorsDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.EventDto;
import us.tx.state.dfps.service.person.dto.IntakeAllegationDto;
import us.tx.state.dfps.service.person.dto.PersonAddrLinkDto;
import us.tx.state.dfps.service.workload.dao.StageWorkloadDao;
import us.tx.state.dfps.service.workload.dto.PersonDto;

import java.util.List;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Declares
 * service Implementation for APS Inhome Intake Report Nov 02, 2021- 3:43:13 PM © 2017 Texas
 * Department of Family and Protective Services
 *  * **********  Change History *********************************
 * 10/22/2021 rayanv artf204701 : initial modernization APS Intake Report forms CFIN0300,CFIN0700
 * 10/05/2023 thompswa artf251139 : remove unused mref indicator
 */
@Service
@Transactional
public class ApsInHomeIntakeReportServiceImpl implements ApsInHomeIntakeReportService {

    @Autowired
    private StageWorkloadDao stageWorkloadDao;

    @Autowired
    private CpsIntakeReportDao cpsIntakeReportDao;

    @Autowired
    private CpsIntakeReportService cpsIntakeReportService;

    @Autowired
    private ApsInHomeIntakeReportPrefillData prefillData;

    @Autowired
    private CpsIntakeNotificationDao cpsIntakeNotificationDao;

    @Autowired
    private PersonDao personDao;

    @Autowired
    private IncmgDtrmFactorsDao incmgDtrmFactorsDao;

    @Autowired
    private ChildServicePlanFormDao childServicePlanFormDao;

    @Autowired
    private MobileUtil mobileUtil;

    /**
     * Method Name: getIntakeReport Method Description: Gets data for APS In Home Intake
     * Report and returns prefill data
     *
     * @param apsCommonReq
     * @return PreFillDataServiceDto @
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
    public PreFillDataServiceDto getIntakeReport(ApsCommonReq apsCommonReq) {
        // Call DAMS
        ApsInHomeIntakeReportDto apsInHomeIntakeReportDto = callDaos(apsCommonReq, "cfin0300");

        return prefillData.returnPrefillData(apsInHomeIntakeReportDto);
    }

    /**
     * Method Name: getapsinhomereportnorep Method Description: Gets data for AIntake Report Adult Protective Services (Minus
     * 	Reporter) and returns prefill data
     *
     * @param apsCommonReq
     * @return PreFillDataServiceDto @
     */

    @Override
    public PreFillDataServiceDto getapsinhomereportnorep(ApsCommonReq apsCommonReq) {
        // Call DAMS
        ApsInHomeIntakeReportDto apsInHomeIntakeReportDto = callDaos(apsCommonReq, "cfin0700");

        return prefillData.returnPrefillData(apsInHomeIntakeReportDto);
    }

    private ApsInHomeIntakeReportDto callDaos(ApsCommonReq apsCommonReq, String formName) {
        // Declare global variables and Prefill dto
        ApsInHomeIntakeReportDto apsInHomeIntakeReportDto = new ApsInHomeIntakeReportDto();
        Long idEvent;
        // CINT65D
        IncomingStageDetailsDto incomingStageDetailsDto = cpsIntakeReportDao
                .getStageIncomingDetails(apsCommonReq.getIdStage());

        if (!ObjectUtils.isEmpty(incomingStageDetailsDto)) {
            idEvent = incomingStageDetailsDto.getIdEvent();
            apsInHomeIntakeReportDto.setIncomingStageDetailsDto(incomingStageDetailsDto);

            // CINT66D - Victims
            List<PersonDto> victimsList = cpsIntakeReportDao.getPersonList(apsCommonReq.getIdStage(),
                    ServiceConstants.VICTIM_TYPE);
            if (!ObjectUtils.isEmpty(victimsList)) {
                apsInHomeIntakeReportDto.setVictimsList(victimsList);
            }

            // CINT66D - Perpetrators
            List<PersonDto> perpsList = cpsIntakeReportDao.getPersonList(apsCommonReq.getIdStage(),
                    ServiceConstants.PERPETRATOR_TYPE);
            if (!ObjectUtils.isEmpty(perpsList)) {
                apsInHomeIntakeReportDto.setPerpsList(perpsList);
            }

            // CINT66D - Other Principles
            List<PersonDto> othersList = cpsIntakeReportDao.getPersonList(apsCommonReq.getIdStage(),
                    ServiceConstants.OTHER_PRN_TYPE);
            if (!ObjectUtils.isEmpty(othersList)) {
                apsInHomeIntakeReportDto.setOthersList(othersList);
            }

            // CINT66D - Reporters
            List<PersonDto> reporters = cpsIntakeReportDao.getPersonList(apsCommonReq.getIdStage(), ServiceConstants.REPORTER_TYPE);
                reporters = cpsIntakeReportService.getFpsStaffReporter(reporters, incomingStageDetailsDto); // artf164166 If has FPS Staff reporter, add from incoming details
                if (!ObjectUtils.isEmpty(reporters)) {
                    apsInHomeIntakeReportDto.setReportersList(reporters);
                }

            // CINT66D - Collaterals
            List<PersonDto> collateralsList = cpsIntakeReportDao.getPersonList(apsCommonReq.getIdStage(),
                    ServiceConstants.COLLATERAL_TYPE);
            if (!ObjectUtils.isEmpty(collateralsList)) {
                apsInHomeIntakeReportDto.setCollateralsList(collateralsList);
            }

            // CINT62D
            List<PhoneInfoDto> phoneInfoList = cpsIntakeReportDao.getPhoneInfo(apsCommonReq.getIdStage());
            if (!ObjectUtils.isEmpty(phoneInfoList)) {
                apsInHomeIntakeReportDto.setPhoneInfoList(phoneInfoList);
            }

            // CINT63D
            List<PersonAddrLinkDto> personAddrLinkList = cpsIntakeNotificationDao
                    .getAddrInfoByStageId(apsCommonReq.getIdStage());
            if (!ObjectUtils.isEmpty(personAddrLinkList)) {
                apsInHomeIntakeReportDto.setPersonAddrLinkList(personAddrLinkList);
            }

            // CINT19D
            List<IntakeAllegationDto> intakeAllegationList = personDao
                    .getIntakeAllegationByStageId(apsCommonReq.getIdStage());
            if (!ObjectUtils.isEmpty(intakeAllegationList)) {
                apsInHomeIntakeReportDto.setIntakeAllegationList(intakeAllegationList);
            }

            if(!mobileUtil.isMPSEnvironment()) {
                // CINT15D
                List<IncmgDetermFactors> incmgDetermFactorsList = incmgDtrmFactorsDao
                        .getincmgDetermFactorsById(apsCommonReq.getIdStage());
                if (!ObjectUtils.isEmpty(incmgDetermFactorsList)) {
                    apsInHomeIntakeReportDto.setIncmgDetermFactorsList(incmgDetermFactorsList);
                }
            }

            // CINT64D
            List<NameDto> namesList = cpsIntakeNotificationDao.getNameAliases(apsCommonReq.getIdStage());
            if (!ObjectUtils.isEmpty(namesList)) {
                apsInHomeIntakeReportDto.setNamesList(namesList);
            }

//            only for CFIN0700
            if ("cfin0700".equals(formName)) {
                // CINT67D
                List<WorkerInfoDto> workerInfoList = cpsIntakeReportDao.getWorkerInfo(apsCommonReq.getIdStage(),
                        ServiceConstants.STATUS_STG);
                if (!ObjectUtils.isEmpty(workerInfoList)) {
                    apsInHomeIntakeReportDto.setWorkerInfoList(workerInfoList);
                }
            }


 // Only for CFIN0300
            if ("cfin0300".equals(formName) && !ObjectUtils.isEmpty(idEvent)) {
                List<EventDto> eventDetailsList = childServicePlanFormDao.fetchEventDetails(idEvent);
                if (!ObjectUtils.isEmpty(eventDetailsList)) {
                    apsInHomeIntakeReportDto.setEventDetailsList(eventDetailsList);
                }
                // CLSC52D
                if (ServiceConstants.EVENT_STATUS_APRV.equalsIgnoreCase(eventDetailsList.get(0).getCdEventStatus())) {
                    List<ApprovalDto> approvalList = cpsIntakeReportDao.getApprovalList(idEvent);

                    if (!ObjectUtils.isEmpty(approvalList)) {
                        apsInHomeIntakeReportDto.setApprovalList(approvalList);
                    }
                }
            }
        }

        // Send form name for reference in prefill data
        apsInHomeIntakeReportDto.setFormName(formName);

        return apsInHomeIntakeReportDto;
    }
}
