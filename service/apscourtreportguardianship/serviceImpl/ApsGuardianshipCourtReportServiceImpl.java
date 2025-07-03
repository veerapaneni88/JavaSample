package us.tx.state.dfps.service.apscourtreportguardianship.serviceImpl;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.populateletter.dto.CodesTablesDto;
import us.tx.state.dfps.service.admin.dao.AddrPersonLinkPhoneDao;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dao.SvcAuthEventLinkDao;
import us.tx.state.dfps.service.admin.dto.AddrPersonLinkPhoneInDto;
import us.tx.state.dfps.service.admin.dto.AddrPersonLinkPhoneOutDto;
import us.tx.state.dfps.service.admin.dto.PersonEthnicityDto;
import us.tx.state.dfps.service.apsCourtReportGuardianship.dto.ApsCourtReportGuardianshipDataDto;
import us.tx.state.dfps.service.apsGuardianshipDetails.dto.ApsGuardianshipDetailsServiceDto;
import us.tx.state.dfps.service.apscourtreportguardianship.service.ApsGuardianshipCourtReportService;
import us.tx.state.dfps.service.casepackage.dao.ServiceAuthorizationDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.EventPersonLinkDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.contacts.dao.ContactDetailsDao;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.ApsGuardianshipCourtReportPrefillData;
import us.tx.state.dfps.service.forms.util.ApsGuardianshipDetailsPrefillData;
import us.tx.state.dfps.service.person.dao.PersonEthnicityDao;
import us.tx.state.dfps.service.placement.dao.CommonApplicationDao;
import us.tx.state.dfps.service.placement.dto.NameDetailDto;
import us.tx.state.dfps.service.populateletter.dao.PopulateLetterDao;
import us.tx.state.dfps.service.servicauthform.dao.ServiceAuthFormDao;
import us.tx.state.dfps.service.servicedlvryclosure.dao.ServiceDlvryClosureDao;
import us.tx.state.dfps.service.subcare.dao.CapsResourceDao;
import us.tx.state.dfps.service.workload.dao.WorkLoadDao;
import us.tx.state.dfps.service.workload.dto.*;

import java.util.List;

@Service
@Transactional
public class ApsGuardianshipCourtReportServiceImpl implements ApsGuardianshipCourtReportService {

    private static final String GUA = "GUA";

    @Autowired
    ServiceAuthFormDao serviceAuthFormDao;


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
    EventPersonLinkDao eventPersonLinkDao;

    @Autowired
    WorkLoadDao workLoadDao;

    @Autowired
    private PopulateLetterDao populateLetterDao;

    @Autowired
    ApsGuardianshipCourtReportPrefillData apsGuardianshipCourtReportPrefillData;

    @Autowired
    ServiceDlvryClosureDao dlvryClosureValidationDao;

    @Autowired
    private ContactDetailsDao contactDetailsDao;

    @Autowired
    private PersonEthnicityDao ethnicityDao;

    @Autowired
    private EventDao eventDao;

    @Autowired
    DisasterPlanDao disasterPlanDao;

    @Autowired
    ApsGuardianshipDetailsPrefillData apsGuardianshipDetailsPrefillData;

    /**
     * Method Name: getServiceAuthFormData Method Description: Inhome Service
     * Authorization to get Aps Guardianship report data.
     *
     * @param apsCommonReq
     * @return PreFillDataServiceDto
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public PreFillDataServiceDto getApsGuardianshipCourtReport(ApsCommonReq apsCommonReq) {
        ApsCourtReportGuardianshipDataDto apsCourtReportGuardianshipDataDto = new ApsCourtReportGuardianshipDataDto();

        Long idPerson = workLoadDao.getPersonIdByRole(
                apsCommonReq.getIdStage(), ServiceConstants.STAGE_PERS_ROLE_PR);

        // Call CCMN19D Retreives information about the primary caseworker
        if (null != apsCommonReq.getIdStage()) {
            StagePersonDto stagePersonDto = stageDao.getStagePersonLinkDetails(apsCommonReq.getIdStage(),
                    ServiceConstants.PRIMARY);
            apsCourtReportGuardianshipDataDto.setStagePersonDto(stagePersonDto);
        }

        // Call CSEC01D Retreives Employee information about the primary caseworker
        if (null != idPerson) {
            EmployeePersPhNameDto employeePersPhNameDto = employeeDao
                    .searchPersonPhoneName(idPerson);
            apsCourtReportGuardianshipDataDto.setEmployeePersPhNameDto(employeePersPhNameDto);
        }

        // DAM CLSC0DD - Retrieves all the records in the STAGE_PERSON_LINK
        List<PersonDto> personList = contactDetailsDao.getAllPersonRecords(apsCommonReq.getIdStage());
        PersonDto wardInfoDto = personList.stream().filter(p -> (ServiceConstants.SELF.equals(p.getCdStagePersRelInt())
                || ServiceConstants.PERSON_ROLE_CLIENT.equals(p.getCdStagePersRelInt()))).findFirst().orElseThrow(RuntimeException::new);

        List<PersonEthnicityDto> personEthnicityDtos = ethnicityDao.getPersonEthnicityByPersonId(wardInfoDto.getIdPerson());
        if (CollectionUtils.isNotEmpty(personEthnicityDtos)) {
            wardInfoDto.setCdEthniCity(personEthnicityDtos.get(0).getCdPersonEthnicity());
        }

        // CLSC1BD
        List<PersonDto> wardAddressList = contactDetailsDao.getPersonsAddrDtls(wardInfoDto.getIdPerson());
            PersonDto wardAddressDto = wardAddressList.stream().filter(p -> ((ServiceConstants.RESIDENCE.contains(p.getAddrPersonLink()))
                    || (ServiceConstants.FACILITY.equals(p.getAddrPersonLink()))
                    || (ServiceConstants.BUSINESS.equals(p.getAddrPersonLink()))
                    || (ServiceConstants.FAMILY_TYPE.equals(p.getAddrPersonLink()))
            )).findFirst().orElse(null);

           apsCourtReportGuardianshipDataDto.setNameDetailDto(new NameDetailDto());
            // call CSEC35D Retrieve a name from PERSON using input ID PRIMARY CLIENT.
            // send personId of the ward
            if (null !=wardAddressDto && null != wardAddressDto.getIdPerson()) {
                NameDetailDto nameDetailDto = commonApplicationDao.getNameDetails(wardAddressDto.getIdPerson());
                apsCourtReportGuardianshipDataDto.setNameDetailDto(nameDetailDto);
            }

            // Call CINV46D for Physician Address
            // send personId of the ward
            if (null !=wardAddressDto && null != wardAddressDto.getIdPerson()) {
                AddrPersonLinkPhoneInDto phyAddrDto = new AddrPersonLinkPhoneInDto();
                phyAddrDto.setUlIdPerson(wardAddressDto.getIdPerson());
                List<AddrPersonLinkPhoneOutDto> phyAddr = addrPersonLinkPhoneDao.cinv46dQUERYdam(phyAddrDto);
                apsCourtReportGuardianshipDataDto.setPhyAddr(phyAddr);
            }

        // CLSC03D
        CodesTablesDto codesTablesDto = populateLetterDao
                .getPersonInfoByCode(ServiceConstants.TITLE, ServiceConstants.NAME).get(0);
        apsCourtReportGuardianshipDataDto.setCodesTablesDto(codesTablesDto);

        // CLSC41D
        List<GuardianDetailsDto> guardianshipIdList = dlvryClosureValidationDao.getGuardianDetailsbyEventStage(GUA,
                apsCommonReq.getIdStage());
        apsCourtReportGuardianshipDataDto.setGuardianDetailsDtoList(guardianshipIdList);

        return apsGuardianshipCourtReportPrefillData.returnPrefillData(apsCourtReportGuardianshipDataDto);
    }

    /**
     * Method Name: getApsGuardianshipDetailsReport Method Description: Get the Aps guardianship details report data.
     *
     * @param apsGuardianshipDetailReportReq
     * @return PreFillDataServiceDto
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public PreFillDataServiceDto getApsGuardianshipDetailsReport(ApsCommonReq apsGuardianshipDetailReportReq) {

        ApsGuardianshipDetailsServiceDto apsGuardianshipDetailsServiceDto = new ApsGuardianshipDetailsServiceDto();

        // getGenericCaseInfo (DAm Name : CallCSEC02D) Method
        GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(apsGuardianshipDetailReportReq.getIdStage());
        apsGuardianshipDetailsServiceDto.setGenericCaseInfoDto(genericCaseInfoDto);

        // getGuardianshipDtoByIdEvent (DAm Name : CallCSEC09D) Method
        GuardianshipDto guardianshipDto = eventDao.getGuardianshipDtoByIdEvent(apsGuardianshipDetailReportReq.getIdEvent());
        apsGuardianshipDetailsServiceDto.setGuardianshipDto(guardianshipDto);

        return apsGuardianshipDetailsPrefillData.returnPrefillData(apsGuardianshipDetailsServiceDto);

    }
}
