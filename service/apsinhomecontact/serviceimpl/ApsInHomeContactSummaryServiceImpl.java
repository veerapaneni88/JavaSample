package us.tx.state.dfps.service.apsinhomecontact.serviceimpl;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dto.EmpPersonIdDto;
import us.tx.state.dfps.service.admin.dto.FetchEventDetailDto;
import us.tx.state.dfps.service.admin.dto.PersonEthnicityDto;
import us.tx.state.dfps.service.apsinhomecontact.dto.ApsInHomeContactSummaryDto;
import us.tx.state.dfps.service.apsinhomecontact.dto.ApsInHomeGuardianReferralDto;
import us.tx.state.dfps.service.apsinhomecontact.service.ApsInHomeContactSummaryService;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.ApsInHomeContactSummaryReq;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.conservatorship.dao.CharacteristicsDao;
import us.tx.state.dfps.service.conservatorship.dto.CharacteristicsDto;
import us.tx.state.dfps.service.contacts.dao.ContactDetailsDao;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.ApsInHomeContactSummaryPrefillData;
import us.tx.state.dfps.service.forms.util.ApsInHomeGuardianshipPrefillData;
import us.tx.state.dfps.service.investigation.dao.ApsInvstDetailDao;
import us.tx.state.dfps.service.person.dao.*;
import us.tx.state.dfps.service.person.dto.AllegationDto;
import us.tx.state.dfps.service.person.dto.PersonIncomeResourceDto;
import us.tx.state.dfps.service.person.dto.PersonListDto;
import us.tx.state.dfps.service.populateletter.dao.PopulateLetterDao;
import us.tx.state.dfps.service.recordscheck.dto.EmployeePersonDto;
import us.tx.state.dfps.service.sscc.dao.SSCCPlcmtOptCircumDao;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StageLinkDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;
import us.tx.state.dfps.service.admin.dto.EmployeeDetailDto;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Service
 * implementation of CINV66S OCTOBER 15, 2021- 2:36:56 PM
 * © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class ApsInHomeContactSummaryServiceImpl implements ApsInHomeContactSummaryService {


    @Autowired
    ApsInvstDetailDao apsInvstDetailDao;
    @Autowired
    private DisasterPlanDao disasterPlanDao;
    @Autowired
    private PopulateLetterDao populateLetterDao;
    @Autowired
    private StageDao stageDao;
    @Autowired
    private EmployeeDao employeeDao;
    @Autowired
    private ApsInHomeContactSummaryPrefillData prefillData;
    @Autowired
    private ApsInHomeGuardianshipPrefillData apsInHomeGuardianshipPrefillData;
    @Autowired
    private AbcsRecordsCheckDao abcsRecordsCheckDao;
    @Autowired
    private ContactDetailsDao contactDetailsDao;
    @Autowired
    private PersonEthnicityDao ethnicityDao;
    @Autowired
    private SSCCPlcmtOptCircumDao ssccPlcmtOptCircumDao;
    @Autowired
    private PersonComparisonDao personComparisonDao;

    @Autowired
    private PersonIdDao personIdDao;

    @Autowired
    private CharacteristicsDao characDao;

    @Autowired
    private PersonDao personDao;

    private static List<String> REAL_PROPERTY_LIST = Arrays.asList(new String[]{"REA", "HOM", "REN"});
    private static List<String> PERSONAL_PROPERTY_LIST = Arrays.asList(new String[]{"PPR", "CAS", "STK", "MNL", "CGC", "LUS", "NOT"});
    private static List<String> VICTIM_PERP_LIST = Arrays.asList(new String[]{ServiceConstants.DESIGNATED_VICTIM, ServiceConstants.DESIGNATED_BOTH, ServiceConstants.VICTIM, ServiceConstants.AV_PERP});

    public static final String DEFAULT_CITIZENSHIP = "Undetermined Immigration Status";

    public static final String CITIZEN = "U.S. Citizen";


    /**
     * Method Name: getLawEnforcementDetails Method Description: Makes DAO calls
     * and returns prefill string for form CFIV1430
     *
     * @param request
     * @return PreFillDataServiceDto
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
    public PreFillDataServiceDto getLawEnforcementDetails(ApsInHomeContactSummaryReq request) {
        // Declare global variables and Prefill dto
        ApsInHomeContactSummaryDto prefillDto = new ApsInHomeContactSummaryDto();

        // CLSC01D - Principal/Reporter
        List<CaseInfoDto> caseInfoPrincipalList = populateLetterDao.getCaseInfoById(request.getIdStage(),
                ServiceConstants.PRINCIPAL);

        if (!ObjectUtils.isEmpty(caseInfoPrincipalList)) {

            for (CaseInfoDto caseInfoDto : caseInfoPrincipalList) {
                if(ServiceConstants.SELF.equals(caseInfoDto.getCdStagePersRelInt())){
                    if (null == caseInfoDto.getIndPersonDobApprox()) {
                        caseInfoDto.setIndPersonDobApprox(ServiceConstants.N); // artf151738
                    }
                    String role = caseInfoDto.getCdStagePersRole();
                    // Determine whether victim or perpetrator
                    if (VICTIM_PERP_LIST.contains(role)) {
                        caseInfoDto.setIndPersCancelHist(ServiceConstants.VIEW);
                    } else if (ServiceConstants.DESIGNATED_PERP.equals(role) || ServiceConstants.ALLEG_PERP.equals(role)) {
                        caseInfoDto.setIndPersCancelHist(ServiceConstants.PHONE);
                    } else {
                        caseInfoDto.setIndPersCancelHist(ServiceConstants.OTHER_O);
                    }
                    // Calculate age
                    if (!ObjectUtils.isEmpty(caseInfoDto.getDtPersonBirth())) {
                        caseInfoDto.setNbrPersonAge(
                                Integer.valueOf(DateUtils.getAge(caseInfoDto.getDtPersonBirth())).shortValue());
                    }
                }
            }
            prefillDto.setCaseInfoPrincipalList(caseInfoPrincipalList);
        }
        // CCMN19D
        StagePersonDto stagePersonDto = stageDao.getStagePersonLinkDetails(request.getIdStage(),
                ServiceConstants.PRIMARY_WORKER);
        if (!ObjectUtils.isEmpty(stagePersonDto)) {
            // CSEC01D - Worker; needs idPerson from CCMN19D
            EmployeePersPhNameDto empWorkerDto = employeeDao.searchPersonPhoneName(stagePersonDto.getIdTodoPersWorker());
            if (!ObjectUtils.isEmpty(empWorkerDto)) {
                prefillDto.setEmpWorkerDto(empWorkerDto);
            }
        }

        // CSEC02D
        prefillDto.setGenericCaseInfoDto(disasterPlanDao.getGenericCaseInfo(request.getIdStage()));

        return prefillData.returnPrefillData(prefillDto);
    }

    /**
     * Method Name: getGuardianshipReferralDetails
     * Method Description: Makes DAO calls and returns prefill string for form CCN07O00 (APS Guardianship Referral Form)
     *
     * @param request
     * @return PreFillDataServiceDto
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
    public PreFillDataServiceDto getGuardianshipReferralDetails(ApsInHomeContactSummaryReq request) {
        // Declaring local variables.
        Long idAllegationStage = request.getIdStage();
        // Declare global variables and Prefill dto
        ApsInHomeGuardianReferralDto prefillDto = new ApsInHomeGuardianReferralDto();
        FetchEventDetailDto eventDto = new FetchEventDetailDto();
        Long ulIdJobPersSupv = ServiceConstants.ZERO;

        // CSEC01D - Worker; needs idPerson from CCMN19D (Retrieves active primary address, phone number &  name for an employee alias supervisor. Namesuffix of CASEWORKER_SUFFIX)
        StagePersonDto stagePersonDto = stageDao.getStagePersonLinkDetails(request.getIdStage(),
                ServiceConstants.PRIMARY_WORKER);
        if (!ObjectUtils.isEmpty(stagePersonDto)) {
            // CSEC01D - Worker; needs idPerson from CCMN19D
            EmployeePersPhNameDto empWorkerDto = employeeDao.searchPersonPhoneName(stagePersonDto.getIdTodoPersWorker());
            if (!ObjectUtils.isEmpty(empWorkerDto)) {
                prefillDto.setEmpWorkerDto(empWorkerDto);
                // EmployeePersonDto To retrieve email information
                EmployeePersonDto employeePersonDto = abcsRecordsCheckDao.getStaffContactInfo(empWorkerDto.getIdPerson());
                prefillDto.setWorkerEmailDto(employeePersonDto);
                ulIdJobPersSupv = empWorkerDto.getIdJobPersSupv();
            }
        }

        // CSEC01D get the supervisor information
        if (!ObjectUtils.isEmpty(ulIdJobPersSupv)) {
            EmployeePersPhNameDto supervisorPersPhNameDto = employeeDao.searchPersonPhoneName(ulIdJobPersSupv);
            prefillDto.setSupervisorDto(supervisorPersPhNameDto);
            EmployeePersonDto employeePersonDto = abcsRecordsCheckDao.getStaffContactInfo(supervisorPersPhNameDto.getIdPerson());
            prefillDto.setSupervisorEmailDto(employeePersonDto);
        }

        // CSEC02D  Intake form id, case date etc
        prefillDto.setGenericCaseInfoDto(disasterPlanDao.getGenericCaseInfo(request.getIdStage()));

        // DAM CLSC0DD - Retrieves all the records in the STAGE_PERSON_LINK
        // Retrieve person alias ward information
        List<PersonDto> personList = contactDetailsDao.getAllPersonRecords(request.getIdStage());
        PersonDto wardInfoDto = personList.stream().filter(p -> (ServiceConstants.SELF.equals(p.getCdStagePersRelInt())
                || ServiceConstants.PERSON_ROLE_CLIENT.equals(p.getCdStagePersRelInt()))).findFirst().orElse(null);

        if (wardInfoDto != null) {
            List<PersonEthnicityDto> personEthnicityDtos = ethnicityDao.getPersonEthnicityByPersonId(wardInfoDto.getIdPerson());
            if (CollectionUtils.isNotEmpty(personEthnicityDtos)) {
                wardInfoDto.setCdEthniCity(personEthnicityDtos.get(0).getCdPersonEthnicity());
            }

            List<CharacteristicsDto> characList = characDao.getCharacteristicDetails(wardInfoDto.getIdPerson());
            if (CollectionUtils.isNotEmpty(characList)) {
                wardInfoDto.setCdPersonCitizenShip(characList.stream().filter(p->ServiceConstants.CITIZEN.equals(p.getCdCharacteristic())).collect(Collectors.toList()).size() > 0?CITIZEN:DEFAULT_CITIZENSHIP);
            }

            prefillDto.setWardInfo(wardInfoDto);

            //To retrieve ward identifier type and identifier name
            String identifierSSNStr = ssccPlcmtOptCircumDao.getSSNByPerson(wardInfoDto.getIdPerson());
            prefillDto.setIdentifierStr(identifierSSNStr);

            String identifierMedicaidStr = ssccPlcmtOptCircumDao.getMedicaidByPerson(wardInfoDto.getIdPerson());
            prefillDto.setMedicaidNumStr(identifierMedicaidStr);

            //To retrieve State Photo ID #
            EmpPersonIdDto empPersonIdDto = personIdDao.getPersonIdByPersonId(wardInfoDto.getIdPerson(),
                    CodesConstant.CNUMTYPE_STATE_PHOTO_ID_NUMBER, ServiceConstants.PERSON_ID_NOT_INVALID,
                    ServiceConstants.GENERIC_END_DATE);

            if (!ObjectUtils.isEmpty(empPersonIdDto)) {
                prefillDto.setStateIdStr(empPersonIdDto.getPersonIdNumber());
            }

            //To retrieve Driver's License #
            EmpPersonIdDto identifierDriverLicenceDto = personIdDao.getPersonIdByPersonId(wardInfoDto.getIdPerson(),
                    CodesConstant.CNUMTYPE_DRIVERS_LICENSE_NUMBER, ServiceConstants.PERSON_ID_NOT_INVALID,
                    ServiceConstants.GENERIC_END_DATE);

            if (!ObjectUtils.isEmpty(identifierDriverLicenceDto)) {
                prefillDto.setDriverLicenceStr(identifierDriverLicenceDto.getPersonIdNumber());
            }

            //CLSC1BD To retrieve ward address and phone info
            List<PersonDto> wardAddressList = contactDetailsDao.getPersonsAddrDtls(wardInfoDto.getIdPerson());
            if (CollectionUtils.isNotEmpty(wardAddressList)) {
                prefillDto.setWardAddressInfoList(wardAddressList.stream().filter(p -> ((ServiceConstants.RESIDENCE.equals(p.getAddrPersonLink()))
                        || (ServiceConstants.FACILITY.equals(p.getAddrPersonLink()))
                        || (ServiceConstants.BUSINESS.equals(p.getAddrPersonLink()))
                        || (ServiceConstants.FAMILY_TYPE.equals(p.getAddrPersonLink()))
                        || (ServiceConstants.FRIEND_NEIGHBOR.equals(p.getAddrPersonLink()))
                        || (ServiceConstants.SCHOOL.equals(p.getAddrPersonLink()))
                        || (ServiceConstants.OTHERXX.equals(p.getAddrPersonLink()))
                )).collect(Collectors.toList()));
            }

            PersonDto wardAddressDto = wardAddressList.stream().filter(p -> ((ServiceConstants.RESIDENCE.equals(p.getAddrPersonLink()))
                    || (ServiceConstants.FACILITY.equals(p.getAddrPersonLink()))
                    || (ServiceConstants.BUSINESS.equals(p.getAddrPersonLink()))
                    || (ServiceConstants.FAMILY_TYPE.equals(p.getAddrPersonLink()))
                    || (ServiceConstants.FRIEND_NEIGHBOR.equals(p.getAddrPersonLink()))
                    || (ServiceConstants.SCHOOL.equals(p.getAddrPersonLink()))
                    || (ServiceConstants.OTHERXX.equals(p.getAddrPersonLink()))
            )).findFirst().orElse(null);
            prefillDto.setWardAddressInfo(wardAddressDto);

            // CLSS58D Retrieve asset information using person id
            List<PersonIncomeResourceDto> assetIncomeResourceList = personComparisonDao.getPersIncomeResrc(wardInfoDto.getIdPerson());
            if (CollectionUtils.isNotEmpty(assetIncomeResourceList)) {
                prefillDto.setAssetInfoDtoList(assetIncomeResourceList.stream().filter(asset -> ("INC".equalsIgnoreCase(asset.getCdIncRsrcIncome()))).collect(Collectors.toList()));
                prefillDto.setAssetRealInfoDtoList(assetIncomeResourceList.stream().filter(asset -> REAL_PROPERTY_LIST.contains(asset.getCdIncRsrcType())).collect(Collectors.toList()));
                prefillDto.setAssetPersonalInfoDtoList(assetIncomeResourceList.stream().filter(asset -> PERSONAL_PROPERTY_LIST.contains(asset.getCdIncRsrcType())).collect(Collectors.toList()));
            }
        }

        if (ServiceConstants.CSTAGES_SVC.equals(prefillDto.getGenericCaseInfoDto().getCdStage())) {
            // CCMNB5D - This returns the prior stage ID for a SVC stage
            // which will be the INV stage where are the allegations.Get prior
            // stage id from if cd_stage is SVC.
            StageLinkDto stageLinkDto = contactDetailsDao.getRecentClosedIdStage(request.getIdStage());
            idAllegationStage = stageLinkDto.getIdPriorStage();
        }
        // CLSSABD  Allegation details
        List<AllegationDto> allegationList = contactDetailsDao.getDistinctAllgtnList(idAllegationStage);
        if (CollectionUtils.isNotEmpty(allegationList)) {
            prefillDto.setAllegationStrList(allegationList.stream().map(a -> a.getCdAllegType()).collect(Collectors.toList()));
        }

        List<PersonDto> medicalInfoDtoList = personList.stream().filter(p -> (ApsInHomeGuardianReferralDto.getDoctorsList().contains(p.getCdStagePersRelInt()))).collect(Collectors.toList());
        prefillDto.setMedicalInfoDtoList(medicalInfoDtoList);

        List<PersonDto> professionalInfoDtoList = personList.stream().filter(p -> (ApsInHomeGuardianReferralDto.getProfessionalList().contains(p.getCdStagePersRelInt()))).collect(Collectors.toList());
        prefillDto.setProfessionalInfoDtoList(professionalInfoDtoList);

        List<PersonDto> personalInfoDtoList = personList.stream().filter(p -> (ApsInHomeGuardianReferralDto.getPersonalList().contains(p.getCdStagePersRelInt()))).collect(Collectors.toList());
        prefillDto.setPersonalInfoDtoList(personalInfoDtoList);

        eventDto.setIdEvent(request.getIdEvent());
        prefillDto.setEventDto(eventDto);

        return apsInHomeGuardianshipPrefillData.returnPrefillData(prefillDto);
    }
    /**
     * Method Name: getGuardianshipReferralDetails
     * Method Description: Makes DAO calls and returns prefill string for form CCN07O00 (APS Guardianship Referral Form)
     *
     * @param request
     * @return PreFillDataServiceDto
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
    public List<String> getValidationMessages(ApsInHomeContactSummaryReq request) {
        // Declaring local variables.
        EmployeeDetailDto employeeDetailDto = null;
        Long idPerson = ServiceConstants.ZERO;
        List<String> errorMsgList = new ArrayList<>();

        List<PersonListDto> personList = personDao.getStaffListByIdStage(request.getIdStage()).stream()
                .filter(o -> ServiceConstants.PRIMARY_ROLE.equals(o.getStagePersRole())).collect(Collectors.toList());

        for (PersonListDto personDto : personList) {
            idPerson = personDto.getIdPerson();
            employeeDetailDto = employeeDao.getEmployeeById(idPerson);

            if (!ObjectUtils.isEmpty(employeeDetailDto)) {
                if(ObjectUtils.isEmpty(employeeDetailDto.getEmployeeEmailAddress()) )
                {
                    errorMsgList.add(ServiceConstants.MSG_GUA_REF_STAFF_REQD);
                }
            }

        }

        return errorMsgList;
    }

}
