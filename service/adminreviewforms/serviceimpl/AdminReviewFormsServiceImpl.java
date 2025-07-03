package us.tx.state.dfps.service.adminreviewforms.serviceimpl;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.common.dto.PersonAddressDto;
import us.tx.state.dfps.notiftoparentprofsp.dto.NotifToParentProfSpCpsDto;
import us.tx.state.dfps.notiftoparentprofsp.dto.NotifToParentProfSpDto;
import us.tx.state.dfps.populateletter.dto.CodesTablesDto;
import us.tx.state.dfps.service.AdminReviewFindings.dto.AdminReviewFindingsDto;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dao.FetchStageDao;
import us.tx.state.dfps.service.admin.dao.StagePersonLinkRecordDao;
import us.tx.state.dfps.service.admin.dto.FetchStagediDto;
import us.tx.state.dfps.service.admin.dto.FetchStagedoDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkRecordInDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkRecordOutDto;
import us.tx.state.dfps.service.adminreviewforms.dao.AdminReviewFormsDao;
import us.tx.state.dfps.service.adminreviewforms.service.AdminReviewFormsService;
import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceSelectStageDao;
import us.tx.state.dfps.service.casepackage.dao.CapsCaseDao;
import us.tx.state.dfps.service.casepackage.dto.CapsCaseDto;
import us.tx.state.dfps.service.casepackage.dto.StageRtrvInDto;
import us.tx.state.dfps.service.casepackage.dto.StageRtrvOutDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.*;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.response.NotificationsRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.*;
import us.tx.state.dfps.service.legal.dao.PersonDetailsDao;
import us.tx.state.dfps.service.legal.dto.PersonDetailsdiDto;
import us.tx.state.dfps.service.legal.dto.PersonDetailsdoDto;
import us.tx.state.dfps.service.pca.dao.PcaDao;
import us.tx.state.dfps.service.placement.dao.CommonApplicationDao;
import us.tx.state.dfps.service.placement.dto.NameDetailDto;
import us.tx.state.dfps.service.populateletter.dao.PopulateLetterDao;
import us.tx.state.dfps.service.workload.dao.AdminReviewDao;
import us.tx.state.dfps.service.workload.dao.NotifToParentEngDao;
import us.tx.state.dfps.service.workload.dto.*;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class AdminReviewFormsServiceImpl implements AdminReviewFormsService {

    private static final Logger logger = Logger.getLogger(AdminReviewFormsServiceImpl.class.getName());

    @Autowired
    AdminReviewFormsDao adminReviewFormsDao;

    @Autowired
    NotifToParentEngDao notifToParentEngDao;

    @Autowired
    FetchStageDao fetchStageDao;

    @Autowired
    PopulateLetterDao populateLetterDao;

    @Autowired
    PersonDetailsDao personDetailsDao;

    @Autowired
    CommonApplicationDao commonApplicationDao;

    @Autowired
    DisasterPlanDao disasterPlanDao;

    @Autowired
    NotifRequestorSpPrefillData notifRequestorSpPrefillData;

    @Autowired
    AdminReviewDao adminReviewDao;

    @Autowired
    CaseMaintenanceSelectStageDao caseMaintenanceSelectStageDao;

    @Autowired
    EmployeeDao employeeDao;

    @Autowired
    NotifToRequestorSpPrefillData notifToRequestorSpPrefillData;

    @Autowired
    @Qualifier("notifToParentProfSpCpsPrefillData")
    NotifToParentProfSpCpsPrefillData notifToParentProfSpCpsPrefillData;

    @Autowired
    private NotifToParentProfSpPrefillData notifToParentProfSpPrefillData;
    @Autowired
    ReleaseHrngLicAnUpheldPrefillData releaseHrngLicAnUpheldPrefillData;

    @Autowired
    private PcaDao pcaDao;

    @Autowired
    AdminReviewFindingsPrefillData adminprefillData;

    @Autowired
    StagePersonLinkRecordDao objCinv39dDao;

    @Autowired
    CapsCaseDao capsCaseDao;

    @Autowired
    AdminReviewNotifToReqPrefillData adminReviewNotifToReqPrefillData;


    @Autowired
    CoverLettertoRequestorCpsPrefillData coverLettertoRequestorCpsPrefillData;

    @Autowired
    NoticeOfAdminReviewForLicensingPrefillData noticeOfAdminReviewForLicensingPrefillData;

    @Autowired
    AdminReviewNoticeLicAnUpheldPrefillData adminReviewNoticeLicAnUpheldPrefillData;
    
    @Autowired
    AdminReviewNoticeLicAndOverturnedPrefillData adminReviewNoticeLicAndOverturnedPrefillData;

    @Autowired
    NotifToRequestorARFPrefillData notifToRequestorARFPrefillData;

    @Autowired
    NotifParentProfReporterCpsPrefillData notifParentProfReporterCpsPrefillData;

    @Autowired
    NotifParentProfReporterApsPrefillData notifParentProfReporterApsPrefillData;

    @Override
    public NotificationsRes getNotificationRequest(NotificationsReq notificationsReq) {
        NotificationsRes notificationsRes = new NotificationsRes();

        adminReviewFormsDao.getNotificationRequest(notificationsRes.getIdEvent());

        return notificationsRes;
    }


    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
            Exception.class })
    public CommonFormRes getNotifParentProfReporterCps(NotificationsReq notificationsReq) {
        NotifToRequestorDto prefillDto = new NotifToRequestorDto();
        long idCaseworker = 0l;
        long idPerson = 0l;
        long idStageRelated = 0l;
        Date dtStageCloseDate = null;
        Date currDate = DateUtils.getCurrentDate();

        // CSES63D
        AdminReviewDto adminReviewDto = notifToParentEngDao.getAdminReview(notificationsReq.getIdStage());
        if (!TypeConvUtil.isNullOrEmpty(adminReviewDto)) {
            idStageRelated = adminReviewDto.getIdStageRelated();
           // idPerson = adminReviewDto.getIdPerson();
            idPerson = notificationsReq.getIdPerson();
            prefillDto.setAdminReviewDto(adminReviewDto);

            // CSEC35D
            prefillDto.setNameDetailDto(commonApplicationDao.getNameDetails(notificationsReq.getIdPerson()));

            prefillDto.setRequestorNameDetailDto(commonApplicationDao.getNameDetails(adminReviewDto.getIdPerson()));

            // CSEC34D
            PersonAddressDto personAddressDto = notifToParentEngDao.getPersonAddress(idPerson,
                    ServiceConstants.PERSON_PHONE_TYPE_HOME, currDate);
            personAddressDto.setDtPersAddrLinkEnd(currDate);
            prefillDto.setPersonAddressDto(personAddressDto);

            // CINT21D
            StageRtrvInDto stageRtrvInDto = new StageRtrvInDto();
            StageRtrvOutDto stageRtrvOutDto = new StageRtrvOutDto();
            stageRtrvInDto.setUlIdStage(idStageRelated);
            caseMaintenanceSelectStageDao.selectStage(stageRtrvInDto, stageRtrvOutDto);
            if (!TypeConvUtil.isNullOrEmpty(stageRtrvOutDto)) {
                prefillDto.setStageRtrvOutDto(stageRtrvOutDto);
                dtStageCloseDate = stageRtrvOutDto.getDtStageClose();

                // CLSC65 call 1
                if(! ObjectUtils.isEmpty(dtStageCloseDate)) {
                    prefillDto.setStageReviewYesDto(notifToParentEngDao.getStageReviewed(notificationsReq.getIdStage(),
                            dtStageCloseDate, ServiceConstants.Y));

                    if (!ServiceConstants.CCOR_060.equals(adminReviewDto.getCdAdminRvAppealResult())) {
                        // CLSC65 call 2
                        prefillDto.setStageReviewNoDto(notifToParentEngDao
                                .getStageReviewed(notificationsReq.getIdStage(), dtStageCloseDate, ServiceConstants.N));
                    }
                }
            }
        }

        // CSEC53D
        PrimaryWorkerDto primaryWorkerDto = notifToParentEngDao.getPrimaryWorker(notificationsReq.getIdStage());
        if (!TypeConvUtil.isNullOrEmpty(primaryWorkerDto)) {
            prefillDto.setPrimaryWorkerDto(primaryWorkerDto);
            idCaseworker = primaryWorkerDto.getIdPerson();

            // CSEC01D
            EmployeePersPhNameDto employeePersPhNameDto = employeeDao.searchPersonPhoneName(idCaseworker);
            if (!TypeConvUtil.isNullOrEmpty(employeePersPhNameDto)) {
                if ((ServiceConstants.PERSON_PHONE_TYPE_BUSINESS).equals(employeePersPhNameDto.getCdPhoneType())
                        || (ServiceConstants.BUSINESS_CELL).equals(employeePersPhNameDto.getCdPhoneType())) {

                    employeePersPhNameDto.setNbrMailCodePhone(employeePersPhNameDto.getNbrPhone());
                    employeePersPhNameDto.setNbrMailCodePhoneExt(employeePersPhNameDto.getNbrPhoneExtension());

                }
                employeePersPhNameDto.setDtEmpTermination(currDate);
                prefillDto.setEmployeePersPhNameDto(employeePersPhNameDto);
            }
        }
        CommonFormRes commonFormRes = new CommonFormRes();
        PreFillDataServiceDto preFillDataServiceDto =  notifParentProfReporterCpsPrefillData.returnPrefillData(prefillDto);
        commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(preFillDataServiceDto));
        return commonFormRes;
    }


    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
            Exception.class })
    public CommonFormRes getNotifParentProfReporterAps(NotificationsReq notificationsReq) {
        NotifToRequestorDto prefillDto = new NotifToRequestorDto();
        long idCaseworker = 0l;
        long idPerson = 0l;
        long idStageRelated = 0l;
        Date dtStageCloseDate = null;
        Date currDate = DateUtils.getCurrentDate();

        // CSES63D
        AdminReviewDto adminReviewDto = notifToParentEngDao.getAdminReview(notificationsReq.getIdStage());
        if (!TypeConvUtil.isNullOrEmpty(adminReviewDto)) {
            idStageRelated = adminReviewDto.getIdStageRelated();
            idPerson = notificationsReq.getIdPerson();
            prefillDto.setAdminReviewDto(adminReviewDto);

            // CSEC35D
            prefillDto.setNameDetailDto(commonApplicationDao.getNameDetails(notificationsReq.getIdPerson()));

            prefillDto.setRequestorNameDetailDto(commonApplicationDao.getNameDetails(adminReviewDto.getIdPerson()));

            // CSEC34D
            PersonAddressDto personAddressDto = notifToParentEngDao.getPersonAddress(idPerson,
                    ServiceConstants.PERSON_PHONE_TYPE_HOME, currDate);
            personAddressDto.setDtPersAddrLinkEnd(currDate);
            prefillDto.setPersonAddressDto(personAddressDto);

            // CINT21D
            StageRtrvInDto stageRtrvInDto = new StageRtrvInDto();
            StageRtrvOutDto stageRtrvOutDto = new StageRtrvOutDto();
            stageRtrvInDto.setUlIdStage(idStageRelated);
            caseMaintenanceSelectStageDao.selectStage(stageRtrvInDto, stageRtrvOutDto);
            if (!TypeConvUtil.isNullOrEmpty(stageRtrvOutDto)) {
                prefillDto.setStageRtrvOutDto(stageRtrvOutDto);
                dtStageCloseDate = stageRtrvOutDto.getDtStageClose();

                // CLSC65 call 1
                if(! ObjectUtils.isEmpty(dtStageCloseDate)) {
                    prefillDto.setStageReviewYesDto(notifToParentEngDao.getStageReviewed(notificationsReq.getIdStage(),
                            dtStageCloseDate, ServiceConstants.Y));

                    if (!ServiceConstants.CCOR_060.equals(adminReviewDto.getCdAdminRvAppealResult())) {
                        // CLSC65 call 2
                        prefillDto.setStageReviewNoDto(notifToParentEngDao
                                .getStageReviewed(notificationsReq.getIdStage(), dtStageCloseDate, ServiceConstants.N));
                    }
                }
            }
        }

        // CSEC53D
        PrimaryWorkerDto primaryWorkerDto = notifToParentEngDao.getPrimaryWorker(notificationsReq.getIdStage());
        if (!TypeConvUtil.isNullOrEmpty(primaryWorkerDto)) {
            prefillDto.setPrimaryWorkerDto(primaryWorkerDto);
            idCaseworker = primaryWorkerDto.getIdPerson();

            // CSEC01D
            EmployeePersPhNameDto employeePersPhNameDto = employeeDao.searchPersonPhoneName(idCaseworker);
            if (!TypeConvUtil.isNullOrEmpty(employeePersPhNameDto)) {
                if ((ServiceConstants.PERSON_PHONE_TYPE_BUSINESS).equals(employeePersPhNameDto.getCdPhoneType())
                        || (ServiceConstants.BUSINESS_CELL).equals(employeePersPhNameDto.getCdPhoneType())) {

                    employeePersPhNameDto.setNbrMailCodePhone(employeePersPhNameDto.getNbrPhone());
                    employeePersPhNameDto.setNbrMailCodePhoneExt(employeePersPhNameDto.getNbrPhoneExtension());

                }
                employeePersPhNameDto.setDtEmpTermination(currDate);
                prefillDto.setEmployeePersPhNameDto(employeePersPhNameDto);
            }
        }
        CommonFormRes commonFormRes = new CommonFormRes();
        PreFillDataServiceDto preFillDataServiceDto =  notifParentProfReporterApsPrefillData.returnPrefillData(prefillDto);
        commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(preFillDataServiceDto));
        return commonFormRes;
    }


    /**
     *
     * Method Description: This Method getApsNotifToParentProfSp calls multiple DAOs to populate
     * the data for Notification to Parent Professional Reporter Spanish to generate the form
     * by passing the input request object(NotifToParentProfSpReq)
     * Service Name: CCFC35S
     *
     * @param notifToParentProfSpReq
     * @return preFillData
     * @throws Exception
     */

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED,rollbackFor = {
            Exception.class })
    public PreFillDataServiceDto getApsNotifToParentProfSp(NotifToParentProfSpReq notifToParentProfSpReq) {
        NotifToParentProfSpDto prefillDto = new NotifToParentProfSpDto();
        PersonDetailsdiDto personDetailsdiDto = new PersonDetailsdiDto();
        FetchStagediDto fetchStagediDto = new FetchStagediDto();
        Date currDate = DateUtils.getCurrentDate();
        long idPrimaryCaseworker = 0l;
        Date dtStageCloseDate = null;
        long idStageRelated = 0l;
        long idPerson = 0l;


        //CLSC03D
        List<CodesTablesDto> codesTablesList = populateLetterDao.getPersonInfoByCode(ServiceConstants.TITLE,
                ServiceConstants.NAME);
        if (!ObjectUtils.isEmpty(codesTablesList) &&
                ((codesTablesList.size()) > 0)) {
            prefillDto.setCodesTablesDtoList(codesTablesList);
        }

        //CSES63D : passing this ulIdStageRelated to CINT21D from CSES63D as per Legacy
        AdminReviewDto adminReview = notifToParentEngDao.getAdminReview(notifToParentProfSpReq.getIdStage());
        if (!TypeConvUtil.isNullOrEmpty(adminReview)) {
            idStageRelated = adminReview.getIdStageRelated();
           // idPerson = adminReview.getIdPerson();
            idPerson = notifToParentProfSpReq.getIdPerson();
            prefillDto.setAdminReviewDto(adminReview);


            //CSEC35D
            NameDetailDto nameDetailDto = commonApplicationDao.getNameDetails(idPerson);
            if (!TypeConvUtil.isNullOrEmpty(nameDetailDto)) {
                prefillDto.setNameDetailDto(nameDetailDto);
            }

            NameDetailDto requestorNameDetailDto = commonApplicationDao.getNameDetails(adminReview.getIdPerson());
            if (!TypeConvUtil.isNullOrEmpty(requestorNameDetailDto)) {
                prefillDto.setRequestorNameDetailDto(requestorNameDetailDto);
            }

            //CCMN44D
            personDetailsdiDto.setIdPerson(idPerson);
            List<PersonDetailsdoDto> getPersonRecord = personDetailsDao.getPersonRecord(personDetailsdiDto);
            if (!ObjectUtils.isEmpty(getPersonRecord) &&
                    ((getPersonRecord.size()) > 0)) {
                prefillDto.setPersonDetailsdoDtoList(getPersonRecord);
            }


            //CSEC34D
            PersonAddressDto personAddressDto = notifToParentEngDao.getPersonAddress(idPerson,
                    ServiceConstants.PERSON_PHONE_TYPE_HOME, currDate);
            if (!TypeConvUtil.isNullOrEmpty(personAddressDto)) {
                personAddressDto.setDtPersAddrLinkEnd(currDate);
                prefillDto.setPersonAddressDto(personAddressDto);
            }


            //CINT21D : setting the ulIdStageRelated here
            fetchStagediDto.setUlIdStage(idStageRelated);
            List<FetchStagedoDto> fetchStagedoDtoList = fetchStageDao.getStageDetails(fetchStagediDto);
            if (!ObjectUtils.isEmpty(fetchStagedoDtoList) &&
                    ((fetchStagedoDtoList.size()) > 0)) {
                prefillDto.setFetchStagedoDtoList(fetchStagedoDtoList);
            }

            //CLSC65D Call 1 for Prior Allegations
            dtStageCloseDate = fetchStagedoDtoList.get(0).getDtDtStageClose();
            logger.info("Retrieving the dtStageCloseDate:" + dtStageCloseDate);
            if(! ObjectUtils.isEmpty(dtStageCloseDate)) {
                List<StageReviewDto> stageReViewYesDto = notifToParentEngDao.getStageReviewed(notifToParentProfSpReq.getIdStage(),
                        dtStageCloseDate, ServiceConstants.YES);
                if (!ObjectUtils.isEmpty(stageReViewYesDto)) {
                    prefillDto.setStageReviewYesDto(stageReViewYesDto);
                }

                //CLSC65D Call 2 for Current Allegations
                if (!ServiceConstants.CCOR_060.equals(adminReview.getCdAdminRvAppealResult())) {
                    List<StageReviewDto> stageReViewNoDto = notifToParentEngDao.getStageReviewed(notifToParentProfSpReq.getIdStage(),
                            dtStageCloseDate, ServiceConstants.NO);
                    if (!ObjectUtils.isEmpty(stageReViewNoDto)) {
                        prefillDto.setStageReviewNoDto(stageReViewNoDto);
                    }
                }
            }
        }



        //CSEC53D
        PrimaryWorkerDto primaryWorkerDto = notifToParentEngDao.getPrimaryWorker(notifToParentProfSpReq.getIdStage());
        if (!TypeConvUtil.isNullOrEmpty(primaryWorkerDto)) {
            idPrimaryCaseworker=primaryWorkerDto.getIdPerson();
            prefillDto.setPrimaryWorkerDto(primaryWorkerDto);

            // CSEC01D
            EmployeePersPhNameDto employeePersPhNameDto = employeeDao.searchPersonPhoneName(idPrimaryCaseworker);
            if (!TypeConvUtil.isNullOrEmpty(employeePersPhNameDto)) {
                if ((ServiceConstants.PERSON_PHONE_TYPE_BUSINESS).equals(employeePersPhNameDto.getCdPhoneType())
                        || (ServiceConstants.BUSINESS_CELL).equals(employeePersPhNameDto.getCdPhoneType())) {

                    employeePersPhNameDto.setNbrMailCodePhone(employeePersPhNameDto.getNbrPhone());
                    employeePersPhNameDto.setNbrMailCodePhoneExt(employeePersPhNameDto.getNbrPhoneExtension());

                }
                employeePersPhNameDto.setDtEmpTermination(currDate);
                prefillDto.setEmployeePersPhNameDto(employeePersPhNameDto);
            }
        }

        if (prefillDto != null) {
            logger.info("NotifToParentProfSpDto main DTO is populated from the Back-End Rest Service call successfully.");
        } else {
            logger.info("NotifToParentProfSpDto main DTO is set to empty as DTO from the Back-End Rest Service call is null.");
        }
        return notifToParentProfSpPrefillData.returnPrefillData(prefillDto);
    }


    /**
     *
     * Method Description: This Method getCpsNotifToParentProfSp calls multiple DAOs to populate
     * the data for Notification to Parent Professional Reporter Spanish CPS version to generate the form
     * by passing the input request object(NotifToParentProfSpCpsReq)
     * Service Name: CCFC35S
     *
     * @param notifToParentProfSpCpsReq
     * @return preFillData
     * @throws Exception
     */

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED,rollbackFor = {
            Exception.class })
    public PreFillDataServiceDto getCpsNotifToParentProfSp(NotifToParentProfSpCpsReq notifToParentProfSpCpsReq) {
        NotifToParentProfSpCpsDto prefillDto = new NotifToParentProfSpCpsDto();
        PersonDetailsdiDto personDetailsdiDto = new PersonDetailsdiDto();
        FetchStagediDto fetchStagediDto = new FetchStagediDto();
        Date currDate = DateUtils.getCurrentDate();
        Date dtStageCloseDate = null;
        long idPrimaryCaseworker = 0l;
        long idStageRelated = 0l;
        long idPerson = 0l;


        //CLSC03D
        List<CodesTablesDto> codesTablesList = populateLetterDao.getPersonInfoByCode(ServiceConstants.TITLE,
                ServiceConstants.NAME);
        if (!ObjectUtils.isEmpty(codesTablesList) &&
                ((codesTablesList.size()) > 0)) {
            prefillDto.setCodesTablesDtoList(codesTablesList);
        }

        //CSES63D : passing this ulIdStageRelated to CINT21D from CSES63D as per Legacy
        AdminReviewDto adminReview = notifToParentEngDao.getAdminReview(notifToParentProfSpCpsReq.getIdStage());
        if (!TypeConvUtil.isNullOrEmpty(adminReview)) {
            idStageRelated = adminReview.getIdStageRelated();
         //   idPerson = adminReview.getIdPerson();
            idPerson = notifToParentProfSpCpsReq.getIdPerson();
            prefillDto.setAdminReviewDto(adminReview);


            //CSEC35D
            NameDetailDto nameDetailDto = commonApplicationDao.getNameDetails(idPerson);
            if (!TypeConvUtil.isNullOrEmpty(nameDetailDto)) {
                prefillDto.setNameDetailDto(nameDetailDto);
            }

            NameDetailDto requestorNameDetailDto = commonApplicationDao.getNameDetails(adminReview.getIdPerson());
            if (!TypeConvUtil.isNullOrEmpty(requestorNameDetailDto)) {
                prefillDto.setRequestorNameDetailDto(requestorNameDetailDto);
            }

            //CCMN44D
            personDetailsdiDto.setIdPerson(idPerson);
            List<PersonDetailsdoDto> getPersonRecord = personDetailsDao.getPersonRecord(personDetailsdiDto);
            if (!ObjectUtils.isEmpty(getPersonRecord) &&
                    ((getPersonRecord.size()) > 0)) {
                prefillDto.setPersonDetailsdoDtoList(getPersonRecord);
            }


            //CSEC34D
            PersonAddressDto personAddressDto = notifToParentEngDao.getPersonAddress(idPerson,
                    ServiceConstants.PERSON_PHONE_TYPE_HOME, currDate);
            if (!TypeConvUtil.isNullOrEmpty(personAddressDto)) {
                personAddressDto.setDtPersAddrLinkEnd(currDate);
                prefillDto.setPersonAddressDto(personAddressDto);
            }


            //CINT21D : setting the ulIdStageRelated here
            fetchStagediDto.setUlIdStage(idStageRelated);
            List<FetchStagedoDto> fetchStagedoDtoList = fetchStageDao.getStageDetails(fetchStagediDto);
            if (!ObjectUtils.isEmpty(fetchStagedoDtoList) &&
                    ((fetchStagedoDtoList.size()) > 0)) {
                prefillDto.setFetchStagedoDtoList(fetchStagedoDtoList);
            }

            //CLSC65D Call 1 for Prior Allegations
            dtStageCloseDate = fetchStagedoDtoList.get(0).getDtDtStageClose();
            logger.info("Retrieving the dtStageCloseDate:" + dtStageCloseDate);
            if(! ObjectUtils.isEmpty(dtStageCloseDate)) {
                List<StageReviewDto> stageReViewYesDto = notifToParentEngDao.getStageReviewed(notifToParentProfSpCpsReq.getIdStage(),
                        dtStageCloseDate, ServiceConstants.YES);
                if (!ObjectUtils.isEmpty(stageReViewYesDto)) {
                    prefillDto.setStageReviewYesDto(stageReViewYesDto);
                }

                //CLSC65D Call 2 for Current Allegations
                if (!ServiceConstants.CCOR_060.equals(adminReview.getCdAdminRvAppealResult())) {
                    List<StageReviewDto> stageReViewNoDto = notifToParentEngDao.getStageReviewed(notifToParentProfSpCpsReq.getIdStage(),
                            dtStageCloseDate, ServiceConstants.NO);
                    if (!ObjectUtils.isEmpty(stageReViewNoDto)) {
                        prefillDto.setStageReviewNoDto(stageReViewNoDto);
                    }
                }
            }
        }



        //CSEC53D
        PrimaryWorkerDto primaryWorkerDto = notifToParentEngDao.getPrimaryWorker(notifToParentProfSpCpsReq.getIdStage());
        if (!TypeConvUtil.isNullOrEmpty(primaryWorkerDto)) {
            idPrimaryCaseworker = primaryWorkerDto.getIdPerson();
            prefillDto.setPrimaryWorkerDto(primaryWorkerDto);

            // CSEC01D
            EmployeePersPhNameDto employeePersPhNameDto = employeeDao.searchPersonPhoneName(idPrimaryCaseworker);
            if (!TypeConvUtil.isNullOrEmpty(employeePersPhNameDto)) {
                if ((ServiceConstants.PERSON_PHONE_TYPE_BUSINESS).equals(employeePersPhNameDto.getCdPhoneType())
                        || (ServiceConstants.BUSINESS_CELL).equals(employeePersPhNameDto.getCdPhoneType())) {

                    employeePersPhNameDto.setNbrMailCodePhone(employeePersPhNameDto.getNbrPhone());
                    employeePersPhNameDto.setNbrMailCodePhoneExt(employeePersPhNameDto.getNbrPhoneExtension());

                }
                employeePersPhNameDto.setDtEmpTermination(currDate);
                prefillDto.setEmployeePersPhNameDto(employeePersPhNameDto);
            }
        }

        if (prefillDto != null) {
            logger.info("NotifToParentProfSpCpsDto main DTO is populated from the Back-End Rest Service call successfully.");
        } else {
            logger.info("NotifToParentProfSpCpsDto main DTO is set to empty as DTO from the Back-End Rest Service call is null.");
        }
        return notifToParentProfSpCpsPrefillData.returnPrefillData(prefillDto);
    }

    /**
     * Method Description: This Method getNotificationToRequestor calls multiple DAOs to populate
     * the data for Notification to Requester English to generate the form
     * by passing the input request object(NotificationsReq)
     * Service Name:CCFC52S
     *
     * @param notificationsReq
     * @return prefillDto
     **/

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
            Exception.class })
    public CommonFormRes getNotificationToRequestor(NotificationsReq notificationsReq) {
        NotifToRequestorDto prefillDto = new NotifToRequestorDto();
        // long idPerson = 0L;
        long idStageRelated = 0L;
        Date dtStageCloseDate = null;
        // CSES63D
        AdminReviewDto adminReviewDto = notifToParentEngDao.getAdminReview(notificationsReq.getIdStage());
        if (!TypeConvUtil.isNullOrEmpty(adminReviewDto)) {
            idStageRelated = adminReviewDto.getIdStageRelated();
            // idPerson = adminReviewDto.getIdPerson();
            prefillDto.setAdminReviewDto(adminReviewDto);
        }
        // CINT21D
        StageRtrvInDto stageRtrvInDto = new StageRtrvInDto();
        StageRtrvOutDto stageRtrvOutDto = new StageRtrvOutDto();
        stageRtrvInDto.setUlIdStage(idStageRelated);
        caseMaintenanceSelectStageDao.selectStage(stageRtrvInDto, stageRtrvOutDto);
        //prefillDto.setStageRtrvOutDto(stageRtrvOutDto);

        //CLSC65D
        dtStageCloseDate = stageRtrvOutDto.getDtStageClose();
        // CLSC65D Prior Alleg
        if(! ObjectUtils.isEmpty(dtStageCloseDate)) {
            prefillDto.setStageReviewYesDto(notifToParentEngDao.getStageReviewed(notificationsReq.getIdStage(),
                    dtStageCloseDate, ServiceConstants.Y));
            // CLSC65D Current Alleg
            prefillDto.setStageReviewNoDto(notifToParentEngDao
                    .getStageReviewed(notificationsReq.getIdStage(), dtStageCloseDate, ServiceConstants.N));
        }
        CommonFormRes commonFormRes = new CommonFormRes();
        PreFillDataServiceDto preFillDataServiceDto =  adminReviewNotifToReqPrefillData.returnPrefillData(prefillDto);
        commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(preFillDataServiceDto));
        return commonFormRes;
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
            Exception.class })
    public CommonFormRes getReleaseHrngLicANUpheld(PopulateFormReq populateFormReq)
    {


        NotifToRequestorDto prefillDto = new NotifToRequestorDto();
        long idCaseworker = 0l;
        long idPerson = 0l;
        Date currDate = DateUtils.getCurrentDate();

        // CSES63D
        AdminReviewDto adminReviewDto = notifToParentEngDao.getAdminReview(populateFormReq.getIdStage());
        if (!TypeConvUtil.isNullOrEmpty(adminReviewDto)) {
            idPerson = adminReviewDto.getIdPerson();
            prefillDto.setAdminReviewDto(adminReviewDto);

            // CSEC35D
            prefillDto.setNameDetailDto(commonApplicationDao.getNameDetails(idPerson));

            // CSEC34D
            PersonAddressDto personAddressDto = notifToParentEngDao.getPersonAddress(idPerson,
                    ServiceConstants.PERSON_PHONE_TYPE_HOME, currDate);
            personAddressDto.setDtPersAddrLinkEnd(currDate);
            prefillDto.setPersonAddressDto(personAddressDto);

        }

        // CSEC53D
        PrimaryWorkerDto primaryWorkerDto = notifToParentEngDao.getPrimaryWorker(populateFormReq.getIdStage());
        if (!TypeConvUtil.isNullOrEmpty(primaryWorkerDto)) {
            prefillDto.setPrimaryWorkerDto(primaryWorkerDto);
            idCaseworker = primaryWorkerDto.getIdPerson();

            // CSEC01D
            EmployeePersPhNameDto employeePersPhNameDto = employeeDao.searchPersonPhoneName(idCaseworker);
            if (!TypeConvUtil.isNullOrEmpty(employeePersPhNameDto)) {
                if ((ServiceConstants.PERSON_PHONE_TYPE_BUSINESS).equals(employeePersPhNameDto.getCdPhoneType())
                        || (ServiceConstants.BUSINESS_CELL).equals(employeePersPhNameDto.getCdPhoneType())) {

                    employeePersPhNameDto.setNbrMailCodePhone(employeePersPhNameDto.getNbrPhone());
                    employeePersPhNameDto.setNbrMailCodePhoneExt(employeePersPhNameDto.getNbrPhoneExtension());

                }
                employeePersPhNameDto.setDtEmpTermination(currDate);
                prefillDto.setEmployeePersPhNameDto(employeePersPhNameDto);
            }
        }

        CommonFormRes commonFormRes = new CommonFormRes();
        PreFillDataServiceDto preFillDataServiceDto =  releaseHrngLicAnUpheldPrefillData.returnPrefillData(prefillDto);
        commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(preFillDataServiceDto));
        return commonFormRes;

    }

    /**
     * Method Name: getCoverLetterReqCps
     * Method Description: pulls data from DAO and set in Dto
     * ccf20o00
     * @param coverLetterCpsReq
     * @return CommonFormRes
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
            Exception.class })
    public CommonFormRes getCoverLetterReqCps(CoverLetterCpsReq coverLetterCpsReq) {

        CoverLetterToRequestorDto prefillDto = new CoverLetterToRequestorDto();
        long idCaseworker = 0l;
        long idPerson = 0l;
        long idStageRelated = 0l;
        Date dtStageCloseDate = null;
        Date currDate = DateUtils.getCurrentDate();

        // CSES63D
        AdminReviewDto adminReviewDto = notifToParentEngDao.getAdminReview(coverLetterCpsReq.getIdStage());
        if (!TypeConvUtil.isNullOrEmpty(adminReviewDto)) {
            idStageRelated = adminReviewDto.getIdStageRelated();
            idPerson = adminReviewDto.getIdPerson();
            prefillDto.setAdminReviewDto(adminReviewDto);
        }

        //CSEC34D
        PersonAddressDto personAddressDto = notifToParentEngDao.getPersonAddress(idPerson,
                ServiceConstants.PERSON_PHONE_TYPE_HOME, currDate);
        if (!TypeConvUtil.isNullOrEmpty(personAddressDto)) {
            prefillDto.setPersonAddressDto(personAddressDto);
        }

        // CSEC35D
        prefillDto.setNameDetailDto(commonApplicationDao.getNameDetails(idPerson));

        // CSEC53D
        PrimaryWorkerDto primaryWorkerDto = notifToParentEngDao.getPrimaryWorker(coverLetterCpsReq.getIdStage());
        if (!TypeConvUtil.isNullOrEmpty(primaryWorkerDto)) {
            prefillDto.setPrimaryWorkerDto(primaryWorkerDto);
            idCaseworker = primaryWorkerDto.getIdPerson();
        }

        // CSEC01D
        EmployeePersPhNameDto employeePersPhNameDto = employeeDao.searchPersonPhoneName(idCaseworker);
        if (!TypeConvUtil.isNullOrEmpty(employeePersPhNameDto)) {
            if ((ServiceConstants.PERSON_PHONE_TYPE_BUSINESS).equals(employeePersPhNameDto.getCdPhoneType())
                    || (ServiceConstants.BUSINESS_CELL).equals(employeePersPhNameDto.getCdPhoneType())) {
                employeePersPhNameDto.setNbrMailCodePhone(employeePersPhNameDto.getNbrPhone());
                employeePersPhNameDto.setNbrMailCodePhoneExt(employeePersPhNameDto.getNbrPhoneExtension());
            }
            employeePersPhNameDto.setDtEmpTermination(currDate);
            prefillDto.setEmployeePersPhNameDto(employeePersPhNameDto);
        }

        // CINT21D
        StageRtrvInDto stageRtrvInDto = new StageRtrvInDto();
        StageRtrvOutDto stageRtrvOutDto = new StageRtrvOutDto();
        stageRtrvInDto.setUlIdStage(idStageRelated);
        caseMaintenanceSelectStageDao.selectStage(stageRtrvInDto, stageRtrvOutDto);
        if (!TypeConvUtil.isNullOrEmpty(stageRtrvOutDto)) {
            prefillDto.setStageRtrvOutDto(stageRtrvOutDto);
            dtStageCloseDate = stageRtrvOutDto.getDtStageClose();
        }

        // CLSC65D call 1
        if(! ObjectUtils.isEmpty(dtStageCloseDate)) {
            prefillDto.setStageReviewYesDto(notifToParentEngDao.getStageReviewed(coverLetterCpsReq.getIdStage(),
                    dtStageCloseDate, ServiceConstants.Y));

            if (!ServiceConstants.CCOR_060.equals(adminReviewDto.getCdAdminRvAppealResult())) {
                // CLSC65D call 2
                prefillDto.setStageReviewNoDto(notifToParentEngDao
                        .getStageReviewed(coverLetterCpsReq.getIdStage(), dtStageCloseDate, ServiceConstants.N));
            }
        }


        CommonFormRes commonFormRes = new CommonFormRes();
        PreFillDataServiceDto preFillDataServiceDto = coverLettertoRequestorCpsPrefillData.returnPrefillData(prefillDto);
        commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(preFillDataServiceDto));
        return commonFormRes;

    }

    /**
     * Method Name: callAdminReveiwFinding
     * Method Description: pulls data from DAO and set in Dto
     * ccf09o00
     * @param adminReviewFindingsReq
     * @return CommonFormRes
     */
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    @Override
    public CommonFormRes callAdminReveiwFinding(AdminReviewFindingsReq adminReviewFindingsReq) {

        AdminReviewFindingsDto adminReviewFindingsDto = new AdminReviewFindingsDto();
        long idStageRelated = 0l;
        long idPerson = 0l;
        Stage stageEntity = null;
        Date dtStageCloseDate = null;
        AdminReviewDto adminReviewDto = new AdminReviewDto();
       // EmployeePersPhNameDto employeePersPhNameDto = new EmployeePersPhNameDto();
        CapsCaseDto capsCaseDto = new CapsCaseDto();
      //  PrimaryWorkerDto primaryWorkerDto = new PrimaryWorkerDto();
        Date currDate = DateUtils.getCurrentDate();


        //CSE65D
            if (!ObjectUtils.isEmpty(adminReviewFindingsReq.getEvent())) {
                adminReviewDto = adminReviewFormsDao.getAdminReviewByEventId(adminReviewFindingsReq.getEvent());
                adminReviewFindingsDto.setAdminReviewDto(adminReviewDto);
            }


        //CINV39D
        StagePersonLinkRecordInDto stagePersonLinkRecordInDto = new StagePersonLinkRecordInDto();
        stagePersonLinkRecordInDto.setIdPerson(adminReviewDto.getIdPerson());
        stagePersonLinkRecordInDto.setIdStage(adminReviewDto.getIdStage());
        List<StagePersonLinkRecordOutDto> stageRecDto = objCinv39dDao
                .getStagePersonLinkRecord(stagePersonLinkRecordInDto);
        if (!TypeConvUtil.isNullOrEmpty(stageRecDto) && 0 < stageRecDto.size()) {
            adminReviewFindingsDto.setStagePersonLinkRecordOutDto(stageRecDto.get(0));
        }

        adminReviewFindingsDto.setStageCaseDtlDto(pcaDao.getStageAndCaseDtls(adminReviewDto.getIdStage()));

        //CSEC35D

        if(! ObjectUtils.isEmpty(adminReviewDto.getIdPerson()))
        adminReviewFindingsDto.setNameDetailDto(commonApplicationDao.getNameDetails(adminReviewDto.getIdPerson()));

        // CINT21D
        StageRtrvInDto stageRtrvInDto = new StageRtrvInDto();
        StageRtrvOutDto stageRtrvOutDto = new StageRtrvOutDto();
        stageRtrvInDto.setUlIdStage(adminReviewFindingsReq.getStage());
        caseMaintenanceSelectStageDao.selectStage(stageRtrvInDto, stageRtrvOutDto);
        if (!TypeConvUtil.isNullOrEmpty(stageRtrvOutDto)) {
            adminReviewFindingsDto.setStageRtrvOutDto(stageRtrvOutDto);
            dtStageCloseDate = stageRtrvOutDto.getDtStageClose();

            // CCMNC5D
            capsCaseDto = capsCaseDao.getCaseDetails(stageRtrvOutDto.getIdCase());
            adminReviewFindingsDto.setCapsCaseDto(capsCaseDto);

            //CSEC53D
            if(! ObjectUtils.isEmpty(dtStageCloseDate)) {
                List<StageReviewDto> stageReviewDtoList = notifToParentEngDao.getStageReviewed(adminReviewFindingsReq.getStage(),
                        dtStageCloseDate, ServiceConstants.YES);
                adminReviewFindingsDto.setStageReviewDtolist(stageReviewDtoList);
                List<StageReviewDto> stageReviewNotDtoList = notifToParentEngDao.getStageReviewed(adminReviewFindingsReq.getStage(),
                        dtStageCloseDate, ServiceConstants.NO);
                adminReviewFindingsDto.setStageNotReviewDto(stageReviewNotDtoList);
            }



            // CSEC53D
            PrimaryWorkerDto primaryWorkerDto = notifToParentEngDao.getPrimaryWorker(adminReviewFindingsReq.getStage());
            if (!TypeConvUtil.isNullOrEmpty(primaryWorkerDto)) {
                adminReviewFindingsDto.setPrimaryWorkerDto(primaryWorkerDto);

                // CSEC01D
                EmployeePersPhNameDto employeePersPhNameDto = employeeDao.searchPersonPhoneName(primaryWorkerDto.getIdPerson());
                if (!TypeConvUtil.isNullOrEmpty(employeePersPhNameDto)) {
                    if ((ServiceConstants.PERSON_PHONE_TYPE_BUSINESS).equals(employeePersPhNameDto.getCdPhoneType())
                            || (ServiceConstants.BUSINESS_CELL).equals(employeePersPhNameDto.getCdPhoneType())) {

                        employeePersPhNameDto.setNbrMailCodePhone(employeePersPhNameDto.getNbrPhone());
                        employeePersPhNameDto.setNbrMailCodePhoneExt(employeePersPhNameDto.getNbrPhoneExtension());

                    }
                    employeePersPhNameDto.setDtEmpTermination(currDate);
                    adminReviewFindingsDto.setEmployeePersPhNameDto(employeePersPhNameDto);
                }
            }

        }

        CommonFormRes commonFormRes = new CommonFormRes();
        PreFillDataServiceDto preFillDataServiceDto = adminprefillData.returnPrefillData(adminReviewFindingsDto);
        commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(preFillDataServiceDto));
        return commonFormRes;
    }
    /**
     * Method Name: getNotifToRequestorSpanish
     * Method Description: pulls data from DAO and set in Dto for generating form CCF16O00
     *
     * @param notificationsReq
     * @return CommonFormRes
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
            Exception.class })
    public CommonFormRes getNotifToRequestorSpanish(NotificationsReq notificationsReq) {

        NotifToRequestorDto notifToRequestorDto = new NotifToRequestorDto();
        Long idStageRelated = null;
        Date dtStageCloseDate = null;
        // CLSC03D
        notifToRequestorDto.setCodesTablesDtoList(
                populateLetterDao.getPersonInfoByCode(ServiceConstants.TITLE, ServiceConstants.NAME));
        //CSEC02D
        GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(notificationsReq.getIdStage());
        notifToRequestorDto.setGenericCaseInfoDto(genericCaseInfoDto);

        // CSES63D
        AdminReviewDto adminReviewDto = notifToParentEngDao.getAdminReview(notificationsReq.getIdStage());
        notifToRequestorDto.setAdminReviewDto(adminReviewDto);

        if (!us.tx.state.dfps.service.common.utils.TypeConvUtil.isNullOrEmpty(adminReviewDto)) {

            idStageRelated = adminReviewDto.getIdStageRelated();

            // CINT21D
            StageRtrvInDto stageRtrvInDto = new StageRtrvInDto();
            StageRtrvOutDto stageRtrvOutDto = new StageRtrvOutDto();
            stageRtrvInDto.setUlIdStage(idStageRelated);
            caseMaintenanceSelectStageDao.selectStage(stageRtrvInDto, stageRtrvOutDto);
            if (!us.tx.state.dfps.service.common.utils.TypeConvUtil.isNullOrEmpty(stageRtrvOutDto)) {
                notifToRequestorDto.setStageRtrvOutDto(stageRtrvOutDto);
                dtStageCloseDate = stageRtrvOutDto.getDtStageClose();

                // CLSC65 call 1
                if(! ObjectUtils.isEmpty(dtStageCloseDate)) {
                    notifToRequestorDto.setStageReviewYesDto(notifToParentEngDao.getStageReviewed(notificationsReq.getIdStage(),
                            dtStageCloseDate, ServiceConstants.Y));

                    // CLSC65 call 2
                    notifToRequestorDto.setStageReviewNoDto(notifToParentEngDao
                            .getStageReviewed(notificationsReq.getIdStage(), dtStageCloseDate, ServiceConstants.N));
                }
            }
        }

        CommonFormRes commonFormRes = new CommonFormRes();
        PreFillDataServiceDto preFillDataServiceDto =  notifToRequestorSpPrefillData.returnPrefillData(notifToRequestorDto);
        commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(preFillDataServiceDto));
        return commonFormRes;
    }

    /**
     * Method Name: getNotifRequestorSp
     * Method Description: pulls data from DAO and set in Dto
     *
     * @param populateFormReq
     * @return CommonFormRes
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
    public CommonFormRes getNotifRequestorSp(PopulateFormReq populateFormReq) {
        NotifToRequestorDto prefillDto = new NotifToRequestorDto();
        long idCaseworker = 0l;
        long idPerson = 0l;
        long idStageRelated = 0l;
        Date dtStageCloseDate = null;
        Date currDate = DateUtils.getCurrentDate();




        // CLSC03D
        prefillDto.setCodesTablesDtoList(
                populateLetterDao.getPersonInfoByCode(ServiceConstants.TITLE, ServiceConstants.NAME));

        // CSES63D
        AdminReviewDto adminReviewDto = notifToParentEngDao.getAdminReview(populateFormReq.getIdStage());
        if (!us.tx.state.dfps.service.common.utils.TypeConvUtil.isNullOrEmpty(adminReviewDto)) {
            if (ServiceConstants.CCOR_030.equals(adminReviewDto.getCdAdminRvAppealResult())
                    || ServiceConstants.CCOR_050.equals(adminReviewDto.getCdAdminRvAppealResult())
                    || ServiceConstants.CCOR_040.equals(adminReviewDto.getCdAdminRvAppealResult())
                    || ServiceConstants.CCOR_070.equals(adminReviewDto.getCdAdminRvAppealResult())) {
                adminReviewDto.setCdAdminRvAppealResult(ServiceConstants.CCOR_010);
            } else if (ServiceConstants.CCOR_060.equals(adminReviewDto.getCdAdminRvAppealResult())) {
                adminReviewDto.setCdAdminRvAppealResult(ServiceConstants.CCOR_020);
            }
            idStageRelated = adminReviewDto.getIdStageRelated();
            idPerson = adminReviewDto.getIdPerson();
            prefillDto.setAdminReviewDto(adminReviewDto);

            // CSEC35D
            prefillDto.setNameDetailDto(commonApplicationDao.getNameDetails(idPerson));

            // CSEC34D
            PersonAddressDto personAddressDto = notifToParentEngDao.getPersonAddress(idPerson,
                    ServiceConstants.PERSON_PHONE_TYPE_HOME, currDate);
            personAddressDto.setDtPersAddrLinkEnd(currDate);
            prefillDto.setPersonAddressDto(personAddressDto);

            // CINT21D
            StageRtrvInDto stageRtrvInDto = new StageRtrvInDto();
            StageRtrvOutDto stageRtrvOutDto = new StageRtrvOutDto();
            stageRtrvInDto.setUlIdStage(idStageRelated);
            caseMaintenanceSelectStageDao.selectStage(stageRtrvInDto, stageRtrvOutDto);
            if (!us.tx.state.dfps.service.common.utils.TypeConvUtil.isNullOrEmpty(stageRtrvOutDto)) {
                prefillDto.setStageRtrvOutDto(stageRtrvOutDto);
                dtStageCloseDate = stageRtrvOutDto.getDtStageClose();

                // CLSC65 call 1
                if(! ObjectUtils.isEmpty(dtStageCloseDate)) {
                    prefillDto.setStageReviewYesDto(notifToParentEngDao.getStageReviewed(populateFormReq.getIdStage(),
                            dtStageCloseDate, ServiceConstants.Y));

                    if (!ServiceConstants.CCOR_060.equals(adminReviewDto.getCdAdminRvAppealResult())) {
                        // CLSC65 call 2
                        prefillDto.setStageReviewNoDto(notifToParentEngDao
                                .getStageReviewed(populateFormReq.getIdStage(), dtStageCloseDate, ServiceConstants.N));
                    }
                }
            }
        }

        // CSEC53D
        PrimaryWorkerDto primaryWorkerDto = notifToParentEngDao.getPrimaryWorker(populateFormReq.getIdStage());
        if (!us.tx.state.dfps.service.common.utils.TypeConvUtil.isNullOrEmpty(primaryWorkerDto)) {
            prefillDto.setPrimaryWorkerDto(primaryWorkerDto);
            idCaseworker = primaryWorkerDto.getIdPerson();

            // CSEC01D
            EmployeePersPhNameDto employeePersPhNameDto = employeeDao.searchPersonPhoneName(idCaseworker);
            if (!us.tx.state.dfps.service.common.utils.TypeConvUtil.isNullOrEmpty(employeePersPhNameDto)) {
                if ((ServiceConstants.PERSON_PHONE_TYPE_BUSINESS).equals(employeePersPhNameDto.getCdPhoneType())
                        || (ServiceConstants.BUSINESS_CELL).equals(employeePersPhNameDto.getCdPhoneType())) {

                    employeePersPhNameDto.setNbrMailCodePhone(employeePersPhNameDto.getNbrPhone());
                    employeePersPhNameDto.setNbrMailCodePhoneExt(employeePersPhNameDto.getNbrPhoneExtension());

                }
                employeePersPhNameDto.setDtEmpTermination(currDate);
                prefillDto.setEmployeePersPhNameDto(employeePersPhNameDto);
            }
        }

        CommonFormRes commonFormRes = new CommonFormRes();
        PreFillDataServiceDto preFillDataServiceDto =  notifRequestorSpPrefillData.returnPrefillData(prefillDto);
        commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(preFillDataServiceDto));
        return commonFormRes;
    }

  /**
     * Method Name: getCoverLetterReqCpsSp
     * Method Description: pulls data from DAO and set in Dto
     *
     * @param populateFormReq
     * @return CommonFormRes
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
            Exception.class })
    public PreFillDataServiceDto getCoverLetterReqCpsSp(PopulateFormReq populateFormReq) {


        NotifToRequestorDto prefillDto = new NotifToRequestorDto();
        long idCaseworker = 0l;
        long idPerson = 0l;
        long idStageRelated = 0l;
        Date dtStageCloseDate = null;
        Date currDate = DateUtils.getCurrentDate();



        //CLSC03D
        List<CodesTablesDto> codesTablesList = populateLetterDao.getPersonInfoByCode(ServiceConstants.TITLE,
                ServiceConstants.NAME);
        if (!ObjectUtils.isEmpty(codesTablesList) &&
                ((codesTablesList.size()) > 0)) {
            prefillDto.setCodesTablesDtoList(codesTablesList);
        }

        // CSES63D
        AdminReviewDto adminReviewDto = notifToParentEngDao.getAdminReview(populateFormReq.getIdStage());
        if (!TypeConvUtil.isNullOrEmpty(adminReviewDto)) {
            idStageRelated = adminReviewDto.getIdStageRelated();
            idPerson = adminReviewDto.getIdPerson();
            prefillDto.setAdminReviewDto(adminReviewDto);
        }

        //CSEC34D

        PersonAddressDto personAddressDto = notifToParentEngDao.getPersonAddress(idPerson,
                ServiceConstants.PERSON_PHONE_TYPE_HOME, currDate);
        if (!TypeConvUtil.isNullOrEmpty(personAddressDto)) {
            prefillDto.setPersonAddressDto(personAddressDto);
        }



        // CSEC35D
        prefillDto.setNameDetailDto(commonApplicationDao.getNameDetails(idPerson));

        // CSEC53D
        PrimaryWorkerDto primaryWorkerDto = notifToParentEngDao.getPrimaryWorker(populateFormReq.getIdStage());
        if (!TypeConvUtil.isNullOrEmpty(primaryWorkerDto)) {
            prefillDto.setPrimaryWorkerDto(primaryWorkerDto);
            idCaseworker = primaryWorkerDto.getIdPerson();
        }

        // CSEC01D
        EmployeePersPhNameDto employeePersPhNameDto = employeeDao.searchPersonPhoneName(idCaseworker);
        if (!TypeConvUtil.isNullOrEmpty(employeePersPhNameDto)) {
            if ((ServiceConstants.PERSON_PHONE_TYPE_BUSINESS).equals(employeePersPhNameDto.getCdPhoneType())
                    || (ServiceConstants.BUSINESS_CELL).equals(employeePersPhNameDto.getCdPhoneType())) {
                employeePersPhNameDto.setNbrMailCodePhone(employeePersPhNameDto.getNbrPhone());
                employeePersPhNameDto.setNbrMailCodePhoneExt(employeePersPhNameDto.getNbrPhoneExtension());
            }
            employeePersPhNameDto.setDtEmpTermination(currDate);
            prefillDto.setEmployeePersPhNameDto(employeePersPhNameDto);
        }

        // CINT21D
        StageRtrvInDto stageRtrvInDto = new StageRtrvInDto();
        StageRtrvOutDto stageRtrvOutDto = new StageRtrvOutDto();
        stageRtrvInDto.setUlIdStage(idStageRelated);
        caseMaintenanceSelectStageDao.selectStage(stageRtrvInDto, stageRtrvOutDto);
        if (!TypeConvUtil.isNullOrEmpty(stageRtrvOutDto)) {
            prefillDto.setStageRtrvOutDto(stageRtrvOutDto);
            dtStageCloseDate = stageRtrvOutDto.getDtStageClose();
        }

        // CLSC65D call 1
        if(! ObjectUtils.isEmpty(dtStageCloseDate)) {
            prefillDto.setStageReviewYesDto(notifToParentEngDao.getStageReviewed(populateFormReq.getIdStage(),
                    dtStageCloseDate, ServiceConstants.Y));

            if (!ServiceConstants.CCOR_060.equals(adminReviewDto.getCdAdminRvAppealResult())) {
                // CLSC65D call 2
                prefillDto.setStageReviewNoDto(notifToParentEngDao
                        .getStageReviewed(populateFormReq.getIdStage(), dtStageCloseDate, ServiceConstants.N));
            }
        }

        CommonFormRes commonFormRes = new CommonFormRes();
        PreFillDataServiceDto preFillDataServiceDto = notifRequestorSpPrefillData.returnPrefillData(prefillDto);
        return preFillDataServiceDto;

    }

    /**
     * Method Name: getAdminReviewLicANUpheld
     * Method Description: pulls data from DAO and set in Dto
     *
     * @param populateFormReq
     * @return CommonFormRes
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
            Exception.class })
    public CommonFormRes getAdminReviewLicANUpheld(PopulateFormReq populateFormReq)
    {

        NotifToRequestorDto prefillDto = new NotifToRequestorDto();
        long idCaseworker;
        long idPerson;
        long idStageRelated = 0L;
        Date dtStageCloseDate = null;
        Date currDate = DateUtils.getCurrentDate();

        // CSES63D
        AdminReviewDto adminReviewDto = notifToParentEngDao.getAdminReview(populateFormReq.getIdStage());
        if (!TypeConvUtil.isNullOrEmpty(adminReviewDto)) {
            idStageRelated = adminReviewDto.getIdStageRelated();
            idPerson = adminReviewDto.getIdPerson();
            prefillDto.setAdminReviewDto(adminReviewDto);
            // CSEC35D
            prefillDto.setNameDetailDto(commonApplicationDao.getNameDetails(idPerson));
            // CSEC34D
            PersonAddressDto personAddressDto = notifToParentEngDao.getPersonAddress(idPerson,
                    ServiceConstants.PERSON_PHONE_TYPE_HOME, currDate);
            personAddressDto.setDtPersAddrLinkEnd(currDate);
            prefillDto.setPersonAddressDto(personAddressDto);
            // CINT21D
            StageRtrvInDto stageRtrvInDto = new StageRtrvInDto();
            StageRtrvOutDto stageRtrvOutDto = new StageRtrvOutDto();
            stageRtrvInDto.setUlIdStage(idStageRelated);
            caseMaintenanceSelectStageDao.selectStage(stageRtrvInDto, stageRtrvOutDto);
            dtStageCloseDate = stageRtrvOutDto.getDtStageClose();

            if(! ObjectUtils.isEmpty(dtStageCloseDate)) {
                //CLSC65D
                if (!CodesConstant.CARVWRES_060.equals(adminReviewDto.getCdAdminRvAppealResult())) {
                    // CLSC65D Prior Alleg
                    prefillDto.setStageReviewYesDto(notifToParentEngDao.getStageReviewed(populateFormReq.getIdStage(),
                            dtStageCloseDate, ServiceConstants.Y));
                } else {
                    dtStageCloseDate = stageRtrvOutDto.getDtStageClose();
                    // CLSC65D Prior Alleg
                    prefillDto.setStageReviewYesDto(notifToParentEngDao.getStageReviewed(populateFormReq.getIdStage(),
                            dtStageCloseDate, ServiceConstants.N));
                }
            }
            // CSEC53D
            PrimaryWorkerDto primaryWorkerDto = notifToParentEngDao.getPrimaryWorker(populateFormReq.getIdStage());
            if (!TypeConvUtil.isNullOrEmpty(primaryWorkerDto)) {
                prefillDto.setPrimaryWorkerDto(primaryWorkerDto);
                idCaseworker = primaryWorkerDto.getIdPerson();
                // CSEC01D
                EmployeePersPhNameDto employeePersPhNameDto = employeeDao.searchPersonPhoneName(idCaseworker);
                if (!TypeConvUtil.isNullOrEmpty(employeePersPhNameDto)) {
                    if ((ServiceConstants.PERSON_PHONE_TYPE_BUSINESS).equals(employeePersPhNameDto.getCdPhoneType())
                            || (ServiceConstants.BUSINESS_CELL).equals(employeePersPhNameDto.getCdPhoneType())) {

                        employeePersPhNameDto.setNbrMailCodePhone(employeePersPhNameDto.getNbrPhone());
                        employeePersPhNameDto.setNbrMailCodePhoneExt(employeePersPhNameDto.getNbrPhoneExtension());

                    }
                    employeePersPhNameDto.setDtEmpTermination(currDate);
                    prefillDto.setEmployeePersPhNameDto(employeePersPhNameDto);
                }
            }
        }
        CommonFormRes commonFormRes = new CommonFormRes();
        PreFillDataServiceDto preFillDataServiceDto =  adminReviewNoticeLicAnUpheldPrefillData.returnPrefillData(prefillDto);
        commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(preFillDataServiceDto));
        return commonFormRes;
    }

    @Override
    public CommonFormRes getReleaseHrngLicANDOverTurned(RHOverturnedReq rhOverturnedReq) {
        NotifToRequestorDto prefillDto = new NotifToRequestorDto();
        long idCaseworker;
        long idPerson;
        long idStageRelated = 0L;
        Date dtStageCloseDate = null;
        Date currDate = DateUtils.getCurrentDate();

        // CSES63D
        AdminReviewDto adminReviewDto = notifToParentEngDao.getAdminReview(rhOverturnedReq.getIdStage());
        if (!TypeConvUtil.isNullOrEmpty(adminReviewDto)) {
            idStageRelated = adminReviewDto.getIdStageRelated();
            idPerson = adminReviewDto.getIdPerson();
            prefillDto.setAdminReviewDto(adminReviewDto);
            // CSEC35D
            prefillDto.setNameDetailDto(commonApplicationDao.getNameDetails(idPerson));
            // CSEC34D
            PersonAddressDto personAddressDto = notifToParentEngDao.getPersonAddress(idPerson,
                    ServiceConstants.PERSON_PHONE_TYPE_HOME, currDate);
            personAddressDto.setDtPersAddrLinkEnd(currDate);
            prefillDto.setPersonAddressDto(personAddressDto);
            // CINT21D
            StageRtrvInDto stageRtrvInDto = new StageRtrvInDto();
            StageRtrvOutDto stageRtrvOutDto = new StageRtrvOutDto();
            stageRtrvInDto.setUlIdStage(idStageRelated);
            caseMaintenanceSelectStageDao.selectStage(stageRtrvInDto, stageRtrvOutDto);
            dtStageCloseDate = stageRtrvOutDto.getDtStageClose();
            //CLSC65D
            if(! ObjectUtils.isEmpty(dtStageCloseDate)) {
                if (!CodesConstant.CARVWRES_060.equals(adminReviewDto.getCdAdminRvAppealResult())) {
                    // CLSC65D Prior Alleg
                    prefillDto.setStageReviewYesDto(notifToParentEngDao.getStageReviewed(rhOverturnedReq.getIdStage(),
                            dtStageCloseDate, ServiceConstants.Y));
                } else {
                    dtStageCloseDate = stageRtrvOutDto.getDtStageClose();
                    // CLSC65D Prior Alleg
                    prefillDto.setStageReviewYesDto(notifToParentEngDao.getStageReviewed(rhOverturnedReq.getIdStage(),
                            dtStageCloseDate, ServiceConstants.N));
                }
            }
            // CSEC53D
            PrimaryWorkerDto primaryWorkerDto = notifToParentEngDao.getPrimaryWorker(rhOverturnedReq.getIdStage());
            if (!TypeConvUtil.isNullOrEmpty(primaryWorkerDto)) {
                prefillDto.setPrimaryWorkerDto(primaryWorkerDto);
                idCaseworker = primaryWorkerDto.getIdPerson();
                // CSEC01D
                EmployeePersPhNameDto employeePersPhNameDto = employeeDao.searchPersonPhoneName(idCaseworker);
                if (!TypeConvUtil.isNullOrEmpty(employeePersPhNameDto)) {
                    if ((ServiceConstants.PERSON_PHONE_TYPE_BUSINESS).equals(employeePersPhNameDto.getCdPhoneType())
                            || (ServiceConstants.BUSINESS_CELL).equals(employeePersPhNameDto.getCdPhoneType())) {

                        employeePersPhNameDto.setNbrMailCodePhone(employeePersPhNameDto.getNbrPhone());
                        employeePersPhNameDto.setNbrMailCodePhoneExt(employeePersPhNameDto.getNbrPhoneExtension());

                    }
                    employeePersPhNameDto.setDtEmpTermination(currDate);
                    prefillDto.setEmployeePersPhNameDto(employeePersPhNameDto);
                }
            }
        }
        CommonFormRes commonFormRes = new CommonFormRes();
        PreFillDataServiceDto preFillDataServiceDto =  adminReviewNoticeLicAndOverturnedPrefillData.returnPrefillData(prefillDto);
        commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(preFillDataServiceDto));
        return commonFormRes;
    }

    /**
     * Method Name: getNoticeOfAdminReviewForLicensing
     * Method Description: pulls data from DAO and set in Dto
     *
     * @param populateFormReq
     * @return CommonFormRes
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
            Exception.class })
    public CommonFormRes getNoticeOfAdminReviewForLicensing(PopulateFormReq populateFormReq) {

        NotifToParentDto notifToParentDto = new NotifToParentDto();
        Long idStageRelated = null;
        Date dtStageCloseDate = null;
        Long idPersonRequestor = null;
        // CLSC03D
        notifToParentDto.setCodesTablesDtolist(
                populateLetterDao.getPersonInfoByCode(ServiceConstants.TITLE, ServiceConstants.NAME));

        // CSES63D // No Prefill Data for 63D but use the feilds for other DAMS
        AdminReviewDto adminReviewDto = notifToParentEngDao.getAdminReview(populateFormReq.getIdStage());

        if (!us.tx.state.dfps.service.common.utils.TypeConvUtil.isNullOrEmpty(adminReviewDto)) {

            idPersonRequestor = adminReviewDto.getIdPerson();

            idStageRelated = adminReviewDto.getIdStageRelated();
            Date currDate = DateUtils.getCurrentDate();

            //CSEC35D
            NameDetailDto nameDetail = commonApplicationDao.getNameDetails(idPersonRequestor);
            notifToParentDto.setNameDetailDto(nameDetail);

            //CSEC34D
            PersonAddressDto personAddressDto = notifToParentEngDao.getPersonAddress(idPersonRequestor,
                    ServiceConstants.PERSON_PHONE_TYPE_HOME, currDate);
            if (!us.tx.state.dfps.service.common.utils.TypeConvUtil.isNullOrEmpty(personAddressDto)) {
                personAddressDto.setDtPersAddrLinkEnd(currDate);
            }
            notifToParentDto.setPersonAddressDto(personAddressDto);

            // CINT21D
            StageRtrvInDto stageRtrvInDto = new StageRtrvInDto();
            StageRtrvOutDto stageRtrvOutDto = new StageRtrvOutDto();
            stageRtrvInDto.setUlIdStage(idStageRelated);
            caseMaintenanceSelectStageDao.selectStage(stageRtrvInDto, stageRtrvOutDto);
            if (!us.tx.state.dfps.service.common.utils.TypeConvUtil.isNullOrEmpty(stageRtrvOutDto)) {
                notifToParentDto.setStageRtrvOutDto(stageRtrvOutDto);
                dtStageCloseDate = stageRtrvOutDto.getDtStageClose();
                // CLSC65 call 1
                if(! ObjectUtils.isEmpty(dtStageCloseDate)) {
                    if (!CodesConstant.CARVWRES_060.equals(adminReviewDto.getCdAdminRvAppealResult())) {
                        // CLSC65D Prior Alleg
                        notifToParentDto.setStageReviewDtolist(notifToParentEngDao.getStageReviewed(populateFormReq.getIdStage(),
                                dtStageCloseDate, ServiceConstants.Y));
                    } else {
                        dtStageCloseDate = stageRtrvOutDto.getDtStageClose();
                        // CLSC65D Prior Alleg
                        notifToParentDto.setStageReviewDtolist(notifToParentEngDao.getStageReviewed(populateFormReq.getIdStage(),
                                dtStageCloseDate, ServiceConstants.N));
                    }
                }


            }
        }
                PrimaryWorkerDto primaryWorkerDto = notifToParentEngDao.getPrimaryWorker(populateFormReq.getIdStage());
                if (!us.tx.state.dfps.service.common.utils.TypeConvUtil.isNullOrEmpty(primaryWorkerDto)) {

                    //Call to the pCSEC01D dam

                    EmployeePersPhNameDto employeePersPhNameDto = employeeDao
                            .searchPersonPhoneName(primaryWorkerDto.getIdPerson());

                    if (!us.tx.state.dfps.service.common.utils.TypeConvUtil.isNullOrEmpty(employeePersPhNameDto)
                            && !(ServiceConstants.PERSON_PHONE_TYPE_BUSINESS.equals(employeePersPhNameDto.getCdPhoneType())
                            || ServiceConstants.BUSINESS_CELL.equals(employeePersPhNameDto.getCdPhoneType()))) {

                        employeePersPhNameDto.setNbrPhone(employeePersPhNameDto.getNbrMailCodePhone());
                        employeePersPhNameDto.setNbrPhoneExtension(employeePersPhNameDto.getNbrMailCodePhoneExt());
                    }

                    notifToParentDto.setEmployeePersPhNameDto(employeePersPhNameDto);

                }

        CommonFormRes commonFormRes = new CommonFormRes();
        PreFillDataServiceDto preFillDataServiceDto =  noticeOfAdminReviewForLicensingPrefillData.returnPrefillData(notifToParentDto);
        commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(preFillDataServiceDto));
        return commonFormRes;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
    public CommonFormRes getNotifToRequestorAdminReviewFindings(NotifToRequestorReq notifToRequestorReq) {
        NotifToRequestorDto prefillDto = new NotifToRequestorDto();
        long idCaseworker = 0l;
        long idPerson = 0l;
        long idStageRelated = 0l;
        Date dtStageCloseDate = null;
        Date currDate = DateUtils.getCurrentDate();

        // CSES63D
        AdminReviewDto adminReviewDto = notifToParentEngDao.getAdminReview(notifToRequestorReq.getIdStage());
        if (!TypeConvUtil.isNullOrEmpty(adminReviewDto)) {
            idStageRelated = adminReviewDto.getIdStageRelated();
            idPerson = adminReviewDto.getIdPerson();
            prefillDto.setAdminReviewDto(adminReviewDto);

            //CSEC34D
            PersonAddressDto personAddressDto = notifToParentEngDao.getPersonAddress(idPerson,
                    ServiceConstants.PERSON_PHONE_TYPE_HOME, currDate);
            if (!us.tx.state.dfps.service.common.util.TypeConvUtil.isNullOrEmpty(personAddressDto)) {
                prefillDto.setPersonAddressDto(personAddressDto);
            }

            // CSEC35D
            prefillDto.setNameDetailDto(commonApplicationDao.getNameDetails(idPerson));


            // CSEC53D
            PrimaryWorkerDto primaryWorkerDto = notifToParentEngDao.getPrimaryWorker(notifToRequestorReq.getIdStage());
            if (!us.tx.state.dfps.service.common.util.TypeConvUtil.isNullOrEmpty(primaryWorkerDto)) {
                prefillDto.setPrimaryWorkerDto(primaryWorkerDto);
                idCaseworker = primaryWorkerDto.getIdPerson();
            }

            // CSEC01D
            EmployeePersPhNameDto employeePersPhNameDto = employeeDao.searchPersonPhoneName(idCaseworker);
            if (!us.tx.state.dfps.service.common.util.TypeConvUtil.isNullOrEmpty(employeePersPhNameDto)) {
                if ((ServiceConstants.PERSON_PHONE_TYPE_BUSINESS).equals(employeePersPhNameDto.getCdPhoneType())
                        || (ServiceConstants.BUSINESS_CELL).equals(employeePersPhNameDto.getCdPhoneType())) {
                    employeePersPhNameDto.setNbrMailCodePhone(employeePersPhNameDto.getNbrPhone());
                    employeePersPhNameDto.setNbrMailCodePhoneExt(employeePersPhNameDto.getNbrPhoneExtension());
                }
                employeePersPhNameDto.setDtEmpTermination(currDate);
                prefillDto.setEmployeePersPhNameDto(employeePersPhNameDto);
            }

            // CINT21D
            StageRtrvInDto stageRtrvInDto = new StageRtrvInDto();
            StageRtrvOutDto stageRtrvOutDto = new StageRtrvOutDto();
            stageRtrvInDto.setUlIdStage(idStageRelated);
            caseMaintenanceSelectStageDao.selectStage(stageRtrvInDto, stageRtrvOutDto);
            if (!us.tx.state.dfps.service.common.util.TypeConvUtil.isNullOrEmpty(stageRtrvOutDto)) {
                prefillDto.setStageRtrvOutDto(stageRtrvOutDto);
                dtStageCloseDate = stageRtrvOutDto.getDtStageClose();
            }

            // CLSC65D call 1
            if(! ObjectUtils.isEmpty(dtStageCloseDate)) {
                prefillDto.setStageReviewYesDto(notifToParentEngDao.getStageReviewed(notifToRequestorReq.getIdStage(),
                        dtStageCloseDate, ServiceConstants.Y));

                if (!CodesConstant.CARVWRES_060.equals(adminReviewDto.getCdAdminRvAppealResult())) {
                    // CLSC65D call 2
                    prefillDto.setStageReviewNoDto(notifToParentEngDao
                            .getStageReviewed(notifToRequestorReq.getIdStage(), dtStageCloseDate, ServiceConstants.N));
                }
            }

        }


        CommonFormRes commonFormRes = new CommonFormRes();
        PreFillDataServiceDto preFillDataServiceDto = notifToRequestorARFPrefillData.returnPrefillData(prefillDto);
        commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(preFillDataServiceDto));
        return commonFormRes;

    }






}
