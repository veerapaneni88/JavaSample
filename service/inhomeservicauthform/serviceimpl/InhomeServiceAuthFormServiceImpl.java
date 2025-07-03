package us.tx.state.dfps.service.inhomeservicauthform.serviceimpl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.approval.dto.ApprovalFormDataDto;
import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.service.admin.dao.AddrPersonLinkPhoneDao;
import us.tx.state.dfps.service.admin.dao.ApsInhomeTasksDao;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dao.SvcAuthEventLinkDao;
import us.tx.state.dfps.service.admin.dto.*;
import us.tx.state.dfps.service.casepackage.dao.ServiceAuthorizationDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.EventPersonLinkDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.common.utils.TypeConvUtil;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.financial.dao.SvcAuthDetailDao;
import us.tx.state.dfps.service.forms.dao.SubcareLOCFormDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.InhomeServiceAuthorizationFormPrefillData;
import us.tx.state.dfps.service.inhomeservicauthform.service.InhomeServiceAuthFormService;
import us.tx.state.dfps.service.inhomeserviceAuth.dto.InhomeServiceAuthFormDataDto;
import us.tx.state.dfps.service.legal.dao.PersonDetailsDao;
import us.tx.state.dfps.service.legal.dto.PersonDetailsdiDto;
import us.tx.state.dfps.service.legal.dto.PersonDetailsdoDto;
import us.tx.state.dfps.service.person.dto.PersonIdDto;
import us.tx.state.dfps.service.placement.dao.CommonApplicationDao;
import us.tx.state.dfps.service.placement.dto.NameDetailDto;
import us.tx.state.dfps.service.resource.dto.ResourceDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.*;
import us.tx.state.dfps.service.servicauthform.dao.ServiceAuthFormDao;
import us.tx.state.dfps.service.subcare.dao.CapsResourceDao;
import us.tx.state.dfps.service.workload.dto.*;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;

import java.util.List;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Service
 * Authorization form used by APS to refer clients for paid services under PRS
 * contracts. Nov 30, 2021- 1:52:54 PM © 2021 Texas Department of Family and
 * Protective Services
 */
@Repository
public class InhomeServiceAuthFormServiceImpl implements InhomeServiceAuthFormService {

    @Autowired
    private EventDao eventDao;

    @Autowired
    private DisasterPlanDao disasterPlanDao;

    @Autowired
    ServiceAuthFormDao serviceAuthFormDao;

    @Autowired
    private SvcAuthDetailDao serviceAuthorizationDetailDao;

    @Autowired
    SvcAuthEventLinkDao svcAuthEventLinkDao;

    @Autowired
    ServiceAuthorizationDao serviceAuthorizationDao;

    @Autowired
    CapsResourceDao capsResourceDao;

    @Autowired
    AddrPersonLinkPhoneDao addrPersonLinkPhoneDao;

    @Autowired
    CommonApplicationDao commonApplicationDao;

    @Autowired
    StageDao stageDao;

    @Autowired
    EmployeeDao employeeDao;

    @Autowired
    SubcareLOCFormDao subcareLOCFormDao;

    @Autowired
    InhomeServiceAuthorizationFormPrefillData inhomeServiceAuthorizationFormPrefillData;

    @Autowired
    EventPersonLinkDao eventPersonLinkDao;

    @Autowired
    ApsInhomeTasksDao apsInhomeTasksDao;

    @Autowired
    PersonDetailsDao personDetailsDao;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public PreFillDataServiceDto getInhomeServiceAuthFormData(ApsCommonReq apsCommonReq) {

        InhomeServiceAuthFormDataDto inhomeServiceAuthFormDataDto = new InhomeServiceAuthFormDataDto();

        GenericCaseInfoDto genericCaseInfoDto = null;
        boolean svcAuthDetails = false;
        Long idResource = null;
        Long idPrimaryClient = null;
        List<SvcAuthEventLinkOutDto> svcAuthEventLinkOutDtoList = null;
        List<SVCAuthDetailDto> svcAuthDetailDtoList = null;
        List<SVCAuthDetailRecDto> svcAuthDetailRecDtoList = null;
        StagePersonDto stagePersonDto = null;
        ServiceAuthorizationDto serviceAuthorizationDto = null;
        ServiceAuthDetailDto serviceAuthDetailDto = null;
        List<ApsInHomeTasksDto> apsInHomeTasks;

        // CallCCMN45D Retrieves ID STAGE from the EVENT table using input ID
        // EVENT.
        EventDto eventValueDto = eventDao.getEventByid(apsCommonReq.getIdEvent());
        inhomeServiceAuthFormDataDto.setEventValueDto(eventValueDto);
        if (!TypeConvUtil.isNullOrEmpty(eventValueDto) && !ServiceConstants.Zero_Value.equals(eventValueDto.getIdStage())) {

            // call CallCSEC02D Retreives ID CASE and NM CASE from STAGE and
            // CAPS CASE tables using input ID STAGE from DAM CCMN45D.
            genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(eventValueDto.getIdStage());
            if (StringUtils.isEmpty(genericCaseInfoDto.getCdStageReasonClosed())) {
                genericCaseInfoDto.setCdStageReasonClosed("");
            }
            inhomeServiceAuthFormDataDto.setGenericCaseInfoDto(genericCaseInfoDto);

        }
        // call CallCSES24D Retrieves ID SVC AUTH from the SVC AUTH EVENT LINK
        // table using input ID EVENT.
        SvcAuthEventLinkInDto svcAuthEventLink = new SvcAuthEventLinkInDto();
        svcAuthEventLink.setIdSvcAuthEvent(apsCommonReq.getIdEvent());
        svcAuthEventLinkOutDtoList = svcAuthEventLinkDao.getAuthEventLink(svcAuthEventLink);
        inhomeServiceAuthFormDataDto.setSvcAuthEventLinkOutDtoList(svcAuthEventLinkOutDtoList);

        if (!TypeConvUtil.isNullOrEmpty(svcAuthEventLinkOutDtoList)) {

            for (SvcAuthEventLinkOutDto svcAuth : svcAuthEventLinkOutDtoList) {
                if (!TypeConvUtil.isNullOrEmpty(svcAuth.getIdSvcAuth())) {
                    // CallCSES23D Retrieves a row from SVC AUTH table using ID
                    // SVC AUTH as input from DAM CSES24D.
                    serviceAuthorizationDto = serviceAuthorizationDao
                            .getServiceAuthorizationById(svcAuth.getIdSvcAuth());
                    svcAuthDetails = true;
                    inhomeServiceAuthFormDataDto.setServiceAuthorizationDto(serviceAuthorizationDto);

                    if (!TypeConvUtil.isNullOrEmpty(serviceAuthorizationDto.getIdPrimaryClient())
                            && !TypeConvUtil.isNullOrEmpty(serviceAuthorizationDto.getIdResource())) {
                        idResource = serviceAuthorizationDto.getIdResource();
                        idPrimaryClient = serviceAuthorizationDto.getIdPrimaryClient();
                    }

                    if (!TypeConvUtil.isNullOrEmpty(serviceAuthorizationDto.getIdContract())) {
                        // CallCMSC63D Retrieves nbr_contract_scor from
                        // contract_period to pass using TxtStagePriorityCmnts
                        if (!TypeConvUtil.isNullOrEmpty(serviceAuthorizationDto.getDtSvcAuthEff())) {
                            String txScorContractNumber = serviceAuthFormDao.getNbrContractScor(
                                    serviceAuthorizationDto.getIdContract(), serviceAuthorizationDto.getDtSvcAuthEff());
                            inhomeServiceAuthFormDataDto.setTxScorContractNumber(txScorContractNumber);
                        } else {
                            String txScorContractNumber = serviceAuthFormDao
                                    .getNbrContractScor(serviceAuthorizationDto.getIdContract(), null);
                            inhomeServiceAuthFormDataDto.setTxScorContractNumber(txScorContractNumber);
                        }

                    }
                    // CallCLSS24D

                    svcAuthDetailDtoList = serviceAuthorizationDao.getSVCAuthDetailDtoById(svcAuth.getIdSvcAuth());
                    inhomeServiceAuthFormDataDto.setSvcAuthDetailDtoList(svcAuthDetailDtoList);

                    svcAuthDetailRecDtoList = serviceAuthorizationDao
                            .getSVCAuthDetailRecord(svcAuth.getIdSvcAuthEvent());
                    inhomeServiceAuthFormDataDto.setSVCDetailRec(svcAuthDetailRecDtoList);

                    if (!TypeConvUtil.isNullOrEmpty(svcAuthDetailDtoList)) {
                        for (SVCAuthDetailDto svcAuthDetail : svcAuthDetailDtoList) {
                            //artf255040 --Serv Auth Form - Vendor details are not displayed
                            if (!TypeConvUtil.isNullOrEmpty(serviceAuthorizationDto.getIdSvcAuth())) {
                                serviceAuthDetailDto = serviceAuthorizationDetailDao
                                        .retrieveServiceAuthDetail(svcAuthDetail.getIdSvcAuthDtl());
                                inhomeServiceAuthFormDataDto.setServiceAuthDetailDto(serviceAuthDetailDto);
                                apsInHomeTasks = apsInhomeTasksDao.getInhomeTasks(svcAuthDetail.getIdSvcAuth());
                                inhomeServiceAuthFormDataDto.setApsInHomeTasks(apsInHomeTasks);
                                //artf255319 -- Serv Auth Form - Latest Vendor details are displayed in P2 while Legacy displays the Initial Vendor details
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (null != idResource) {
            // call to get Provider Information
            ResourceDto resourceDto = capsResourceDao.getResourceById(idResource);
            inhomeServiceAuthFormDataDto.setResourceDto(resourceDto);
        }

//        int rowCount = 0;
//        // supposed to loop over the CSES23D Pass ID PERSON as input from DAM
//        // CSES23D(has been copied to pCLSC36DOutputRec).
//        if (null != svcAuthDetailDtoList && ServiceConstants.Zero < svcAuthDetailDtoList.size()) {
//            for (SVCAuthDetailDto svcauth : svcAuthDetailDtoList) {
//                if (null != svcauth && null != svcauth.getIdPerson()) {
//                    // CallCLSS71D Determine if the SVC AUTH DETAIL person
//                    // exists on the PERSON MERGE table.
//                    List<SelectForwardPersonDto> persMergeFwdList = serviceAuthFormDao
//                            .getSelectForwardPerson(svcauth.getIdPerson());
//                    if (!ObjectUtils.isEmpty(persMergeFwdList)) {
//                        for (SelectForwardPersonDto idPerson : persMergeFwdList) {
//                                // call CSES86D This DAM checks to see if a
//                                // given PersMergeForward ID is on the Stage
//                                // Person Link
//                                // table for a given Stage.
//                                if (null != idPerson.getIdPersonMergeForward()) {
//                                    rowCount = serviceAuthFormDao.getPersonInStageLink(
//                                            idPerson.getIdPersonMergeForward(), eventValueDto.getIdStage());
//
//                                }
//                        }
//                    }
//
//                }
//            }
//
//        }// end of the CLSS71D and

        if (svcAuthDetails && !TypeConvUtil.isNullOrEmpty(svcAuthDetailDtoList)) {
            for (SVCAuthDetailDto svcAuthDetail : svcAuthDetailDtoList) {
                // CallCSEC77D For each row Retrieved from a SVC AUTH DETAIL
                // record Retrieve the corresponding client information from
                // the PERSON)
               /* ClientInfoServiceAuthDto clientDto = *//*serviceAuthFormDao
                .getClientInfoServiceAuth(svcAuthDetail.getIdPerson(), eventValueDto.getIdStage());*/

                //artf256122 - CSEC35D getting the data using personid for client data
                NameDetailDto nameDetailDto = commonApplicationDao.getNameDetails(svcAuthDetail.getIdPerson());
                setClientData(inhomeServiceAuthFormDataDto, nameDetailDto );
                // CSES51D get medicaid info
                if (!ObjectUtils.isEmpty(inhomeServiceAuthFormDataDto.getClienDto())) {
                    List<MedicaidServiceAuthDto> medicaidServiceAuthDto = serviceAuthFormDao
                            .getMedicaidServiceAuth(svcAuthDetail.getIdPerson());
                    inhomeServiceAuthFormDataDto.getClienDto().setMedicaidServiceAuthDtoList(medicaidServiceAuthDto);
                }
                /*
                 ** Call DAM CCMN72D to get the Social Security Number of the primary
                 * client based on the id person from CSEC15D.
                 */

                PersonIdDto personIdDtoSsn = subcareLOCFormDao.getMedicaidNbrByPersonId(
                        svcAuthDetail.getIdPerson(), ServiceConstants.SOCIAL_SECURITY,
                        ServiceConstants.SPL_REQ_TYPE_N, ServiceConstants.MAX_DATE);
                inhomeServiceAuthFormDataDto.setPersonIdDtoSsn(personIdDtoSsn);
                /*
                 ** Call DAM CCMN72D to get the Medicaid Number of the primary client
                 * based on the id person from CSEC15D.
                 */
                PersonIdDto personIdDtoMed = subcareLOCFormDao.getMedicaidNbrByPersonId(
                        svcAuthDetail.getIdPerson(), ServiceConstants.CNUMTYPE_MEDICAID_NUMBER,
                        ServiceConstants.SPL_REQ_TYPE_N, ServiceConstants.GENERIC_END_DATE);
                inhomeServiceAuthFormDataDto.setPersonIdDtoMed(personIdDtoMed);
            }
        }

        if (null != serviceAuthorizationDto.getIdPerson()) {
            // call CallCSEC35D Retrieve a name from PERSON using input ID
            // PRIMARY CLIENT.
            NameDetailDto nameDetailDto = commonApplicationDao.getNameDetails(serviceAuthorizationDto.getIdPerson());
            inhomeServiceAuthFormDataDto.setNameDetailDto(nameDetailDto);
        }

        // CallCINV46D for Client Address
        if (null != idPrimaryClient) {
            AddrPersonLinkPhoneInDto addrDto = new AddrPersonLinkPhoneInDto();
            addrDto.setUlIdPerson(idPrimaryClient);
            List<AddrPersonLinkPhoneOutDto> addr = addrPersonLinkPhoneDao.cinv46dQUERYdam(addrDto);
            inhomeServiceAuthFormDataDto.setClntAddr(addr);
        }

        // CallCINV46D for Physician Address
        if (null != serviceAuthorizationDto.getIdPerson()) {
            AddrPersonLinkPhoneInDto phyAddrDto = new AddrPersonLinkPhoneInDto();
            phyAddrDto.setUlIdPerson(serviceAuthorizationDto.getIdPerson());
            List<AddrPersonLinkPhoneOutDto> phyAddr = addrPersonLinkPhoneDao.cinv46dQUERYdam(phyAddrDto);
            inhomeServiceAuthFormDataDto.setPhyAddr(phyAddr);
        }

        if (!TypeConvUtil.isNullOrEmpty( genericCaseInfoDto.getIdStage())) {
            // CallCCMN19D Retreives information about the primary caseworker
            stagePersonDto = stageDao.getStagePersonLinkDetails(genericCaseInfoDto.getIdStage(),
                    ServiceConstants.PRIMARY);
            inhomeServiceAuthFormDataDto.setStagePersonDto(stagePersonDto);
        }
        // CallCSEC01D Retreives Employee information about the primary
        // caseworker
        if (!TypeConvUtil.isNullOrEmpty(stagePersonDto.getIdTodoPersWorker())) {
            EmployeePersPhNameDto employeePersPhNameDto = employeeDao
                    .searchPersonPhoneName(stagePersonDto.getIdTodoPersWorker());
            inhomeServiceAuthFormDataDto.setEmployeePersPhNameDto(employeePersPhNameDto);
        }

        // CallCSECD1D RETRIEVE PERSON ID OF CASEWORKER WHEN SVC AUTH CREATED
        if (null != genericCaseInfoDto.getIdStage() && null != eventValueDto.getIdEvent()) {
            Long resultId = serviceAuthFormDao.getPRPersonId(eventValueDto.getIdEvent());
            inhomeServiceAuthFormDataDto.setResultId(resultId);

        }
        // Original worker details
        if (!ObjectUtils.isEmpty(inhomeServiceAuthFormDataDto.getResultId())) {
            EmployeePersPhNameDto employeePersPhNameDto = employeeDao
                    .searchPersonPhoneName(inhomeServiceAuthFormDataDto.getResultId());
            inhomeServiceAuthFormDataDto.setHistoyEmployeePersPhNameDto(employeePersPhNameDto);
        }

        // Call CSEC23D Retrieves approval information for the authorization
        ApprovalFormDataDto approvalFormDto = serviceAuthFormDao.getApprovalFormData(eventValueDto.getIdEvent());
        if (!ObjectUtils.isEmpty(approvalFormDto) && ServiceConstants.APPROVE_EVENT_STATUS.equalsIgnoreCase(approvalFormDto.getCdApproversStatus())) {
            inhomeServiceAuthFormDataDto.setApprovalFormDataDto(approvalFormDto);
        }

        return inhomeServiceAuthorizationFormPrefillData.returnPrefillData(inhomeServiceAuthFormDataDto);

    }


    /**
     * CSEC35D, CCMN44D - Setting the Client data. Fixing defect artf256122
     * Method helps to set the client information data
     * @param inhomeServiceAuthFormDataDto - data for passing to prefill data
     * @param nameDetailDto - name details like first, last, middle and suffix data
     */
    private void setClientData(InhomeServiceAuthFormDataDto inhomeServiceAuthFormDataDto, NameDetailDto nameDetailDto) {
        PersonDetailsdiDto personDetailsdiDto = new PersonDetailsdiDto();
        personDetailsdiDto.setIdPerson(inhomeServiceAuthFormDataDto.getServiceAuthDetailDto().getIdPerson());
        List<PersonDetailsdoDto> personDetailsdoDto =  personDetailsDao.getPersonRecord(personDetailsdiDto);
        ClientInfoServiceAuthDto clientDto = new ClientInfoServiceAuthDto();
        if(!ObjectUtils.isEmpty(nameDetailDto)) {
            clientDto.setNmNameFirst(nameDetailDto.getNmNameFirst());
            clientDto.setNmNameLast(nameDetailDto.getNmNameLast());
            clientDto.setCdNameSuffix(nameDetailDto.getCdNameSuffix());
            clientDto.setNmNameMiddle(nameDetailDto.getNmNameMiddle());
        }
        if(!CollectionUtils.isEmpty(personDetailsdoDto)) {
            clientDto.setDtPersonBirth(personDetailsdoDto.get(0).getDtPersonBirth());
            clientDto.setCdPersonLivArr(personDetailsdoDto.get(0).getCdPersonLivArr());
            clientDto.setCdPersonSex(personDetailsdoDto.get(0).getCdPersonSex());
        }
        inhomeServiceAuthFormDataDto.setClienDto(clientDto);
    }
}
