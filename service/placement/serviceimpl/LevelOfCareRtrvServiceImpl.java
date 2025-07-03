/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This service
 * will retrieve an event row and Level of Care row Aug 18, 2017- 2:13:26 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
package us.tx.state.dfps.service.placement.serviceimpl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.dto.NumberOfRowsDto;
import us.tx.state.dfps.common.dto.PersonAssignedIdToDoDto;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.phoneticsearch.IIRHelper.DateHelper;
import us.tx.state.dfps.phoneticsearch.IIRHelper.Messages;
import us.tx.state.dfps.service.admin.dao.EventIdDao;
import us.tx.state.dfps.service.admin.dao.TodoUpdDtTodoCompletedDao;
import us.tx.state.dfps.service.admin.dto.EventIdInDto;
import us.tx.state.dfps.service.admin.dto.EventIdOutDto;
import us.tx.state.dfps.service.admin.dto.StageTaskInDto;
import us.tx.state.dfps.service.admin.dto.TodoUpdDtTodoCompletedInDto;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.admin.service.StageEventStatusCommonService;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.request.LevelOfCareRtrvReq;
import us.tx.state.dfps.service.common.request.PlacementReq;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.event.service.EventService;
import us.tx.state.dfps.service.person.dao.ServicePackageDao;
import us.tx.state.dfps.service.person.dto.ServicePackageDtlDto;
import us.tx.state.dfps.service.placement.dao.PersonLocPersonDao;
import us.tx.state.dfps.service.placement.dto.*;
import us.tx.state.dfps.service.placement.service.LevelOfCareRtrvService;
import us.tx.state.dfps.service.subcare.dao.PlacementDao;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.PostEventDto;
import us.tx.state.dfps.service.workload.dto.PostEventIPDto;
import us.tx.state.dfps.service.workload.dto.PostEventOPDto;
import us.tx.state.dfps.web.todo.bean.ToDoStagePersonDto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class LevelOfCareRtrvServiceImpl implements LevelOfCareRtrvService {

    @Autowired
    MessageSource messageSource;

    @Autowired
    EventIdDao eventIdDao;

    @Autowired
    PersonLocPersonDao personLOCPersonDao;

    @Autowired
    StageEventStatusCommonService stageEventStatusCommonService;

    @Autowired
    EventService eventService;

    @Autowired
    TodoUpdDtTodoCompletedDao todoCompDao;

    @Autowired
    EventDao eventDao;

    @Autowired
    PostEventService postEventService;

    @Autowired
    PlacementDao placementDao;

    @Autowired
    ServicePackageDao servicePackageDao;

    private ServicePackageDtlDto selectedServicePackage;

    private static final Logger log = Logger.getLogger(LevelOfCareRtrvServiceImpl.class);

    private Long idEvent = ServiceConstants.ZERO_VAL;

    private static final String EVENT_TYPE_LOC = "LOC";
    private static final String PLOC_TASKCODE = "3140";
    private static final Integer MSG_ALOC_ST_DT_RNG_WITH_SEL_SVC_PKG = 57396;
    private static final String END = " End ";
    private static final Integer MSG_SRV_LVL_PERIOD_OVERLAP = 57332;
    private static final Integer MSG_BSL_ST_DT_RNG_WITHIN_SEL_SVC_PKG = 57496;

    /**
     *
     * Method Name: callLevelOfCareRtrvService Method Description: This service
     * will retrieve an event row and Level of Care row for event id
     *
     * @param pInputMsg
     * @return List<LevelOfCareRtrvoDto>
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public List<LevelOfCareRtrvoDto> callLevelOfCareRtrvService(LevelOfCareRtrvReq pInputMsg) {
        log.debug("Entering method callLevelOfCareRtrvService in LevelOfCareRtrvServiceImpl");
        List<LevelOfCareRtrvoDto> response = new ArrayList<LevelOfCareRtrvoDto>();
        EventIdInDto eventIdInDto = new EventIdInDto();
        // set event id to get event list
        eventIdInDto.setIdEvent(pInputMsg.getIdEvent());
        List<EventIdOutDto> eventDtoList = eventIdDao.getEventDetailList(eventIdInDto);
        if (!TypeConvUtil.isNullOrEmpty(eventDtoList)) {
            EventIdOutDto eventIdDto = (EventIdOutDto) eventDtoList.get(ServiceConstants.Zero);
            LevelOfCareRtrvoDto levelOfCareDto = new LevelOfCareRtrvoDto();
            levelOfCareDto.setCdEventStatus(eventIdDto.getCdEventStatus());
            levelOfCareDto.setIdEventPerson(eventIdDto.getIdPerson());
            levelOfCareDto.setDtSystemDate(new Date());
            levelOfCareDto.setDtLastUpdateEvent(eventIdDto.getTsLastUpdate());
            if (!ServiceConstants.STATUS_NEW.equalsIgnoreCase(levelOfCareDto.getCdEventStatus())) {
                if (!ObjectUtils.isEmpty(pInputMsg.getIdPlocEvent())
                        && pInputMsg.getIdPlocEvent() > ServiceConstants.ZERO_VAL) {
                    levelOfCareDto = fetchPLOCRecord(pInputMsg.getIdPlocEvent(), levelOfCareDto);
                } else {
                    levelOfCareDto = fetchPLOCRecord(pInputMsg.getIdEvent(), levelOfCareDto);
                }
            }
            response.add(levelOfCareDto);
        }
        log.debug("Exiting method callLevelOfCareRtrvService in LevelOfCareRtrvServiceImpl");
        return response;
    }

    /**
     *
     * Method Name: levelOfCareSaveService Method Description: This service will
     * save an event row and Level of Care row for event id
     *
     * @param LevelOfCareRtrvReq
     * @return LevelOfCareRtrvoDto @
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public LevelOfCareRtrvoDto levelOfCareSaveService(LevelOfCareRtrvReq levelOfCareRtrvReq) {

        // get today's date for dtEventOccurred
        LevelOfCareRtrvoDto levelOfCareRtrvoDto = new LevelOfCareRtrvoDto();

        StageTaskInDto stageEvtStatusDto = new StageTaskInDto();
        stageEvtStatusDto.setReqFuncCd(levelOfCareRtrvReq.getReqFuncCd());
        stageEvtStatusDto.setIdStage(levelOfCareRtrvReq.getIdStage());
        stageEvtStatusDto.setCdTask(levelOfCareRtrvReq.getCdTask());

        /*
         ** Call CCMN06U -Check Stage Event Status
         */
        String eventStatus = stageEventStatusCommonService.checkStageEventStatus(stageEvtStatusDto);
        /*
         * Call modernized Tuxedo CCMN01U Service - Post Event Common Function
         */
        // Analyze return eventStatus
        levelOfCareRtrvoDto = analyzeReturnOutput(eventStatus, levelOfCareRtrvoDto);
        if (!StringUtils.isEmpty(eventStatus) && ServiceConstants.ARC_SUCCESS.equalsIgnoreCase(eventStatus)) {

            /*
             ** If the Retention value (RetVal) is FND_SUCCESS and IdPerson of
             * the service input Message is 0, get the Id Person of the stage's
             * primary child. Initialize CINV51DI DAM Input and Output
             * Structures And Call CINV51DI DAM
             */
            if (ObjectUtils.isEmpty(levelOfCareRtrvoDto.getErrorDto())) {

                // When the Id Person is passed in as Zero, the primary child
                // for the passed stage id is fetched by calling the 51D DAM
                if (ObjectUtils.isEmpty(levelOfCareRtrvReq.getIdPerson())
                        || levelOfCareRtrvReq.getIdPerson() == ServiceConstants.ZERO_VAL) {
                    ToDoStagePersonDto toDoStagePersonDto = new ToDoStagePersonDto();
                    if (!ObjectUtils.isEmpty(levelOfCareRtrvReq)) {
                        toDoStagePersonDto.setIdStage(levelOfCareRtrvReq.getIdStage());
                        // CdStagePersRole is set to "primary child"
                        toDoStagePersonDto.setCdStagePersRole(ServiceConstants.PRIMARY_CHILD);
                    }

                    // 1. Call CINV51D
                    List<PersonAssignedIdToDoDto> personDtoList = personLOCPersonDao
                            .retrievePersonByRoleAndStage(toDoStagePersonDto);
                    levelOfCareRtrvoDto.setIdToDoList(personDtoList);
                    if (!CollectionUtils.isEmpty(personDtoList)) {
                        levelOfCareRtrvReq.setIdPerson(personDtoList.get(0).getIdTodoPersAssigned());
                    }
                }

                /*
                 * Set both service input and output message person ID's to
                 * To-Do person assigned. The service will use this person ID
                 * when calling other DAMs
                 */

                // CSUB81D checks for duplicate dates for ALOC.
                // Populate DAM input structure
                PersonLevelOfCareDto personLocDto = new PersonLevelOfCareDto();
                personLocDto.setIdPerson(levelOfCareRtrvReq.getIdPersUpdt());
                personLocDto.setCdPlocType(levelOfCareRtrvReq.getCdPlocType());

                personLocDto.setDtPlocStart(levelOfCareRtrvReq.getDtPlocStart());
                personLocDto.setDtPlocEnd(levelOfCareRtrvReq.getDtPlocEnd());
                personLocDto.setTxtDfpsComments(levelOfCareRtrvReq.getTxtDfpsComments());

                // 2. Call CSUB81D
                List<NumberOfRowsDto> personLocList = personLOCPersonDao.checkForAuthorizedPLOC(personLocDto);
                levelOfCareRtrvoDto.setNoOfRowsList(personLocList);

                // 3. Post event ccmn01u processing if Return Value. Update/Add
                // Event

                // 3. Set CAUD11D DAM Input and Output Structures
                PLOCDetailInDto locEventDto = new PLOCDetailInDto();
                locEventDto = initiazeInputForPOCInsertUpdateDtl(locEventDto, levelOfCareRtrvReq);

                List<PLOCDetailDto> personLOCRecordList = null;

                // 4. Call CAUD9ED if it an ALOC else call CAUD11D for BLOC and
                // RLOC.
                Integer errorCode = ServiceConstants.Zero;
                boolean leftGapExists = false;
                boolean rightGapExists = false;
                PLOCDetailDto pLOCRecordToUpdate = null;
                if (ServiceConstants.LOC_TYPE_ALOC.equalsIgnoreCase(levelOfCareRtrvReq.getCdPlocType())) {
                    // artf256025: Check for any selected service packages within the ALOC creation date range.
                    if(servicePackageDao.isSelectedServicePackageWithInTheAlocDateRange(levelOfCareRtrvReq)){
                        errorCode = MSG_ALOC_ST_DT_RNG_WITH_SEL_SVC_PKG;
                        if (errorCode > ServiceConstants.Zero) {
                            ErrorDto errorDto = new ErrorDto();
                            errorDto.setErrorCode(errorCode);
                            levelOfCareRtrvoDto.setErrorDto(errorDto);
                        }
                    }
                    else {
                        /*
                         * Description: This dam is a copy of caud11d. IT was
                         * created for SIR 19886.
                         **
                         ** LOGIC: Two operations: INSERT ('A') and UPDATE ('U') are
                         * allowed
                         **
                         ** If StartDate or EndDate is NULL then set it to MAXDATE
                         **
                         ** 1) INSERT: Input criteria: a) CdPlocType and
                         * SysIndPrfrmValidation For a given ID_PERSON
                         * (hI_ulIdPerson) do the following:
                         **
                         ** If SysIndPrfrmValidation ="Y" then checks EXISTS_1 and
                         * EXISTS_2 Both checks "hI_dtDtPlocStart" and
                         * "hI_dtDtPlocEnd" against other existing records If both
                         * are passed: insert new record
                         **
                         **
                         ** If SysIndPrfrmValidation <>"Y" no need to check EXISTS_1
                         * and EXISTS_2. Thus a record can be inserted with its
                         * PlocStart Date and PlocEnd Date overlaps with other
                         * existing records if its PlocType is RLOC and you do not
                         * want to check (set SysIndPrfrmValidation='N')
                         **
                         **
                         ** 2) UPDATE: Input criteria: for a given ID_PLOC_EVENT,
                         * DT_LAST_UPDATE, and PLOC_TYPE These 3 fields must match
                         * in order for that record to be updated (different input
                         * for INSERT)
                         **
                         ** a) CdPlocType and SysIndPrfrmValidation
                         **
                         ** If SysIndPrfrmValidation ="Y" then If new START date is
                         * diff from old START date ==> check GAP_EXIST_1 (set
                         * GAP_EXIST_1 if the gap is >= 1.0 day) If new END date is
                         * diff from old END date ==> check GAP_EXIST_2 (set
                         * GAP_EXIST_2 if the gap is >= 1.0 day)
                         **
                         **
                         ** If both are passed: update record. Note that PLOC_TYPE is
                         * not updateable. You cannot change from one PLOC_TYPE to
                         * another (because eventually after a couple of changing
                         * PLOC_TYPE back and forth, PlocStart Date and PlocEnd Date
                         * might become overlapped.
                         **
                         ** If SysIndPrfrmValidation <>"Y" no need to check EXISTS_1
                         * and EXISTS_2. Thus a record can be updated with its
                         * PlocStart Date and PlocEnd Date overlaps with other
                         * existing records if its PlocType is RLOC and you do not
                         * want to check (set SysIndPrfrmValidation='N')
                         *
                         * This new dam will only process ALOC's. It will disregard
                         * ALOC's from previous stages for the same person. No
                         * information regarding RLOC or BLOC needs to be considered
                         *
                         * When adding an ALOC, do not check dates before checking
                         * overlap. If the dates are not the same day, always check
                         * for an overlap
                         *
                         *
                         **/
                        if (ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(levelOfCareRtrvReq.getReqFuncCd())) {

                            /**************************************************************************/
                            /*
                             * Check if there's any record of this ID_PERSON with an
                             * ALOC with in
                             */
                            /*
                             * this stage. /* If none, No need to go through all the
                             * validations.
                             */
                            /*
                             * If a record is found, then must go through all
                             * checks.
                             */
                            /**************************************************************************/
                            personLOCRecordList = personLOCPersonDao.checkIfALOCServiceRecordExistsForInsert(locEventDto);

                            if (!CollectionUtils.isEmpty(personLOCRecordList) && (levelOfCareRtrvReq.isSkipValidationCheck() == false)) {
                                personLOCRecordList = personLOCPersonDao.alocServiceInsertValidation1(locEventDto);
                                // If the validation fails, there will be a record
                                // returned in the personLOCRecordList Collection.
                                // The error code is set based on which validation
                                // fails. If any validation fails, the control
                                // returns back, else the validation check proceeds
                                // until all validations are completed. For
                                // validation details, check the Dao method
                                if (!CollectionUtils.isEmpty(personLOCRecordList)) {
                                    errorCode = MSG_SRV_LVL_PERIOD_OVERLAP;
                                } else {
                                    personLOCRecordList = personLOCPersonDao.alocServiceInsertValidation2(locEventDto);
                                    if (!CollectionUtils.isEmpty(personLOCRecordList)) {
                                        errorCode = Messages.MSG_SUB_PERIOD_OVERLAP_2;
                                    } else {
                                        personLOCRecordList = personLOCPersonDao.alocServiceInsertValidation3(locEventDto);
                                        if (!CollectionUtils.isEmpty(personLOCRecordList)) {
                                            errorCode = MSG_SRV_LVL_PERIOD_OVERLAP;
                                        } else {
                                            // Do the validations for the date gap
                                            // if the perform validation indicator
                                            // is set to 'Y'. There are two gap
                                            // checks - Left and right gap
                                            // validations
                                            if (ServiceConstants.YES
                                                    .equalsIgnoreCase(levelOfCareRtrvReq.getbSysIndPrfrmValidation())) {
                                                personLOCRecordList = personLOCPersonDao
                                                        .alocServiceInsertValidation4(locEventDto);
                                                if (!ObjectUtils.isEmpty(personLOCRecordList)) {
                                                    leftGapExists = true;
                                                }

                                                personLOCRecordList = personLOCPersonDao
                                                        .alocServiceInsertValidation5(locEventDto);
                                                if (!ObjectUtils.isEmpty(personLOCRecordList)) {
                                                    rightGapExists = true;
                                                }

                                                // Based on gap exists boolean
                                                // fields, set the corresponding
                                                // error codes
                                                if (leftGapExists && rightGapExists) {
                                                    errorCode = Messages.MSG_SUB_GAP_EXISTS_3;
                                                } else if (leftGapExists) {
                                                    errorCode = Messages.MSG_SUB_GAP_EXISTS_1;
                                                } else if (rightGapExists) {
                                                    errorCode = Messages.MSG_SUB_GAP_EXISTS_2;
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // If any of the validation fails, set the error code
                            // into the object which will be set into the response
                            if (errorCode > ServiceConstants.Zero) {
                                ErrorDto errorDto = new ErrorDto();
                                errorDto.setErrorCode(errorCode);
                                levelOfCareRtrvoDto.setErrorDto(errorDto);
                            } else {
                                if (ServiceConstants.ZERO_VAL == levelOfCareRtrvReq.getIdPlocEvent()) {
                                    locEventDto.setIdPlocEvent(personLOCPersonDao.autoGeneratePlocEventId());
                                }
                                // 3. Post event ccmn01u processing if Return Value.
                                // Update/Add Event

                                if (!ObjectUtils.isEmpty(personLocList)) {
                                    idEvent = callPostEvent(levelOfCareRtrvReq);
                                }
                                locEventDto.setIdPlocEvent(idEvent);
                                // Insert the record into the PLOC table if all
                                // the validations pass through
                                personLOCPersonDao.newPlocRecordInsertion(locEventDto);
                            }
                            /***********************************************************/
                            // Start : Update existing ALOC record if input function
                            // code is "Update" and all required validations are
                            // passed
                            // Update an existing record. New dates (Start, End)
                            // could be either 'shrinking' or 'expanding'
                            /***********************************************************/
                        } else if (ServiceConstants.REQ_FUNC_CD_UPDATE
                                .equalsIgnoreCase(levelOfCareRtrvReq.getReqFuncCd())) {
                            /********************************************************/
                            /*
                             * Check if there's any record at all. It should already
                             * exist in order to do an update. If exists, gets START
                             * and END date for other processing (with time stamp
                             * removed.)
                             */
                            /********************************************************/
                            if (levelOfCareRtrvReq.isSkipValidationCheck() == false) {
                                personLOCRecordList = personLOCPersonDao.checkIfPlocExistBeforeUpdate(locEventDto);
                                if (!CollectionUtils.isEmpty(personLOCRecordList)) {
                                    pLOCRecordToUpdate = personLOCRecordList.get(ServiceConstants.Zero);
                                    errorCode = validateBeforeUpdate(locEventDto, pLOCRecordToUpdate);
                                }
                            }

                            if (errorCode > ServiceConstants.ZERO_VAL) {
                                ErrorDto errorDto = new ErrorDto();
                                errorDto.setErrorCode(errorCode);
                                levelOfCareRtrvoDto.setErrorDto(errorDto);
                            } else {
                                // 3. Post event ccmn01u processing if Return Value.
                                // Update/Add Event

                                if (!ObjectUtils.isEmpty(personLocList)) {
                                    idEvent = callPostEvent(levelOfCareRtrvReq);
                                }
                                locEventDto.setIdPlocEvent(idEvent);
                                // If there were no validation errors, the record
                                // will be updated
                                // UPdate the PLOC record if there were no
                                // validation errors
                                personLOCPersonDao.updatePlocRecord(locEventDto);
                            }
                        }
                    }
                } else {
                    /*
                     * LOGIC: Two operations: INSERT ('A') and UPDATE ('U') are
                     * allowed
                     **
                     ** If StartDate or EndDate is NULL then set it to MAXDATE
                     **
                     ** 1) INSERT: Input criteria: a) CdPlocType and
                     * SysIndPrfrmValidation For a given ID_PERSON
                     * (hI_ulIdPerson) do the following:
                     **
                     ** If new CdPlocType!="RLOC" then checks OVERLAP_1 and
                     * OVERLAP_2 If SysIndPrfrmValidation ="Y" then checks
                     * EXISTS_1 and EXISTS_2 Both checks "hI_dtDtPlocStart" and
                     * "hI_dtDtPlocEnd" against other existing records If both
                     * are passed: insert new record
                     **
                     **
                     ** If CdPlocType=="RLOC" then no need to check for OVERLAP_1
                     * and OVERLAP2. If SysIndPrfrmValidation <>"Y" no need to
                     * check EXISTS_1 and EXISTS_2. Thus a record can be
                     * inserted with its PlocStart Date and PlocEnd Date
                     * overlaps with other existing records if its PlocType is
                     * RLOC and you do not want to check (set
                     * SysIndPrfrmValidation='N')
                     **
                     **
                     ** 2) UPDATE: Input criteria: for a given ID_PLOC_EVENT,
                     * DT_LAST_UPDATE, and PLOC_TYPE These 3 fields must match
                     * in order for that record to be updated (different input
                     * for INSERT)
                     **
                     ** a) CdPlocType and SysIndPrfrmValidation If new
                     * CdPlocType!="RLOC" then checks OVERLAP_1 and OVERLAP_2
                     **
                     ** If SysIndPrfrmValidation ="Y" then If new START date is
                     * diff from old START date ==> check GAP_EXIST_1 (set
                     * GAP_EXIST_1 if the gap is >= 1.0 day) If new END date is
                     * diff from old END date ==> check GAP_EXIST_2 (set
                     * GAP_EXIST_2 if the gap is >= 1.0 day)
                     **
                     **
                     ** If both are passed: update record. Note that PLOC_TYPE is
                     * not updateable. You cannot change from one PLOC_TYPE to
                     * another (because eventually after a couple of changing
                     * PLOC_TYPE back and forth, PlocStart Date and PlocEnd Date
                     * might become overlapped.
                     **
                     ** If CdPlocType=="RLOC" then no need to check for OVERLAP_1
                     * and OVERLAP2. If SysIndPrfrmValidation <>"Y" no need to
                     * check EXISTS_1 and EXISTS_2. Thus a record can be updated
                     * with its PlocStart Date and PlocEnd Date overlaps with
                     * other existing records if its PlocType is RLOC and you do
                     * not want to check (set SysIndPrfrmValidation='N')
                     */
                    if (ServiceConstants.LOC_TYPE_BLOC.equalsIgnoreCase(levelOfCareRtrvReq.getCdPlocType())) {
                        Date plocEndDt = levelOfCareRtrvReq.getDtPlocEnd();
                        if (ObjectUtils.isEmpty(plocEndDt)) {
                            plocEndDt = ServiceConstants.MAX_DATE;
                        }
                        if (servicePackageDao.isSelectedServicePackageWithinTheBSLDateRange(
                            levelOfCareRtrvReq, plocEndDt)) {
                            errorCode = MSG_BSL_ST_DT_RNG_WITHIN_SEL_SVC_PKG;
                            if (errorCode > ServiceConstants.Zero) {
                                ErrorDto errorDto = new ErrorDto();
                                errorDto.setErrorCode(errorCode);
                                levelOfCareRtrvoDto.setErrorDto(errorDto);
                                return levelOfCareRtrvoDto;
                            }
                        }
                    }

                    if (ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(locEventDto.getcReqFuncCd())) {
                        // Check to see if there is any record of the LOC type
                        // for the person. If not present, then no validations
                        // are required since it is the first record. If record
                        // exist then all the validations have to be run. If any
                        // validation fails, the control will be returned back
                        // without inserting the record

                        personLOCRecordList = personLOCPersonDao.checkIfPlocExistBeforeInsertion(
                                locEventDto.getIdPerson(), locEventDto.getCdPlocType());
                        if (!CollectionUtils.isEmpty(personLOCRecordList)
                                && !locEventDto.getCdPlocType().equalsIgnoreCase(ServiceConstants.RLOC_TYPE)) {
                            // 1. Check if new records overlaps other records on
                            // LEFT
                            personLOCRecordList = personLOCPersonDao.plocInsertionValidation1(locEventDto);
                            if (!CollectionUtils.isEmpty(personLOCRecordList)) {
                                errorCode = MSG_SRV_LVL_PERIOD_OVERLAP;
                            } else {
                                if (levelOfCareRtrvReq.isSkipValidationCheck() == false) {
                                    // 2. Check if new records overlaps other records on RIGHT
                                    personLOCRecordList = personLOCPersonDao.plocInsertionValidation2(locEventDto);
                                    if (!CollectionUtils.isEmpty(personLOCRecordList)) {
                                        errorCode = Messages.MSG_SUB_PERIOD_OVERLAP_2;
                                    } else {
                                        // 3. Check if new records is either
                                        // identical OR within a record
                                        /*
                                         * if got a record, then fails -- same
                                         * failure as OVERLAP_1
                                         */
                                        personLOCRecordList = personLOCPersonDao.plocInsertionValidation3(locEventDto);
                                        if (!CollectionUtils.isEmpty(personLOCRecordList)) {
                                            errorCode = MSG_SRV_LVL_PERIOD_OVERLAP;
                                            // Check if input bSysIndPrfrmValidation
                                            // is 'Y' then check further validations
                                        } else if (ServiceConstants.YES
                                                .equalsIgnoreCase(levelOfCareRtrvReq.getbSysIndPrfrmValidation())) {
                                            /*
                                             * 4. Check if the gap on LEFT of
                                             * hI_dtDtPlocStart is bigger than 1
                                             * day. SELECT statement will return
                                             * record if it finds one, which means
                                             * gap is >= 1.0 day ==> ERROR!
                                             */
                                            personLOCRecordList = personLOCPersonDao.plocInsertionValidation4(locEventDto);
                                            if (!CollectionUtils.isEmpty(personLOCRecordList)) {
                                                leftGapExists = true;
                                            }

                                            /*
                                             * 5. Check if the gap on RIGHT of
                                             * hI_dtDtPlocStart is bigger than 1
                                             * day. SELECT statement will return
                                             * record if it finds one, which means
                                             * gap is >= 1.0 day ==> ERROR!
                                             */
                                            personLOCRecordList = personLOCPersonDao.plocInsertionValidation5(locEventDto);
                                            if (!CollectionUtils.isEmpty(personLOCRecordList)) {
                                                rightGapExists = true;
                                            }

                                            // based on gap exists boolean fields
                                            // values set error messages in response
                                            if (leftGapExists && rightGapExists) {
                                                errorCode = Messages.MSG_SUB_GAP_EXISTS_3;
                                            } else if (leftGapExists) {
                                                errorCode = Messages.MSG_SUB_GAP_EXISTS_1;
                                            } else if (rightGapExists) {
                                                errorCode = Messages.MSG_SUB_GAP_EXISTS_2;
                                            }
                                        }
                                    }
                                }
                            }

                            if (errorCode > ServiceConstants.ZERO_VAL) {
                                ErrorDto errorDto = new ErrorDto();
                                errorDto.setErrorCode(errorCode);
                                levelOfCareRtrvoDto.setErrorDto(errorDto);
                            } else {
                                if (ServiceConstants.ZERO_VAL == levelOfCareRtrvReq.getIdPlocEvent()) {
                                    locEventDto.setIdPlocEvent(personLOCPersonDao.autoGeneratePlocEventId());
                                }
                                // 3. Post event ccmn01u processing if Return
                                // Value. Update/Add Event

                                if (!ObjectUtils.isEmpty(personLocList)) {
                                    idEvent = callPostEvent(levelOfCareRtrvReq);
                                }
                                locEventDto.setIdPlocEvent(idEvent);
                                // Insert the record into the PLOC table if all
                                // the validations pass through
                                personLOCPersonDao.newPlocRecordInsertion(locEventDto);
                            }
                            /*
                             * If there was no record of this PLOC type in the
                             * table, then this is the first record for the type
                             * and the record will be inserted into the table
                             */
                        } else {
                            if (ServiceConstants.ZERO_VAL == levelOfCareRtrvReq.getIdPlocEvent()) {
                                locEventDto.setIdPlocEvent(personLOCPersonDao.autoGeneratePlocEventId());
                            }
                            // 3. Post event ccmn01u processing if Return Value.
                            // Update/Add Event

                            if (!ObjectUtils.isEmpty(personLocList)) {
                                idEvent = callPostEvent(levelOfCareRtrvReq);
                            }
                            locEventDto.setIdPlocEvent(idEvent);
                            // Insert the record into the PLOC table if all
                            // the validations pass through
//artf256025-Need to revisit in sprint 7
							/*PlacementDto placementDto = placementDao.selectLatestPlacement(locEventDto.getIdStage());
							if (Objects.nonNull(placementDto) && (ObjectUtils.isEmpty(placementDto.getDtPlcmtEnd()) || new SimpleDateFormat("yyyy-MM-dd")
									.format(placementDto.getDtPlcmtEnd()).equals("4712-12-31"))) {
								ErrorDto errorDto = new ErrorDto();
								errorDto.setErrorCode(ServiceConstants.MSG_END_CUR_T3C_PLC);
								levelOfCareRtrvoDto.setErrorDto(errorDto);

							} else {*/
                            personLOCPersonDao.newPlocRecordInsertion(locEventDto);
								/*List<ServicePackageDtlDto> servicePackageDtls = servicePackageDao
										.getActiveServicePackages(levelOfCareRtrvReq.getIdCase(),
												levelOfCareRtrvReq.getIdStage(),
												levelOfCareRtrvReq.getDtPlocStart());
								servicePackageDtls.forEach(servicePackage -> {
									EventDto event = eventDao.getEventByid(servicePackage.getEventId());
									String desc = (event.getEventDescr().contains("End")) ?
											event.getEventDescr().replaceAll("( End.*)$", END + DateUtils.dateStringInSlashFormat(levelOfCareRtrvReq.getDtPlocStart())) :
											event.getEventDescr() + END + DateUtils.dateStringInSlashFormat(levelOfCareRtrvReq.getDtPlocStart());
									eventDao.updateEventDescForTA(event.getIdEvent(), desc);
									servicePackageDao.updateSelServicePackageDetails(servicePackage.getSvcPkgId(), levelOfCareRtrvReq.getDtPlocStart());
								});*/


                        }
//						}
                    } else if (ServiceConstants.REQ_FUNC_CD_UPDATE.equalsIgnoreCase(locEventDto.getcReqFuncCd())) {
                        // Update an existing record. New dates (Start, End)
                        // could be either 'shrinking'
                        // or 'expanding'

                        /*
                         * =====================================================
                         * ============
                         */
                        /*
                         * Check if there's any record at all. It should already
                         * exist
                         */
                        /* in order to do an update. */
                        /*                                                                 */
                        /*
                         * If exists, gets START and END date for other
                         * processing (with
                         */
                        /* timestamp removed.) */

                        if (levelOfCareRtrvReq.isSkipValidationCheck() == false) {
                            personLOCRecordList = personLOCPersonDao.checkIfPlocExistBeforeUpdate(locEventDto);
                            Integer overlapErrorCode = 0;
                            if (!CollectionUtils.isEmpty(personLOCRecordList)) {
                                pLOCRecordToUpdate = personLOCRecordList.get(0);
                                overlapErrorCode = validateBeforeUpdate(locEventDto, pLOCRecordToUpdate);
                                if(overlapErrorCode > 0){
                                    errorCode = overlapErrorCode;
                                }
                            }
                        }

                        if (errorCode > ServiceConstants.ZERO_VAL) {
                            ErrorDto errorDto = new ErrorDto();
                            errorDto.setErrorCode(errorCode);
                            levelOfCareRtrvoDto.setErrorDto(errorDto);
                        } else {
                            // 3. Post event ccmn01u processing if Return Value.
                            // Update/Add Event

                            if (!ObjectUtils.isEmpty(personLocList)) {
                                idEvent = callPostEvent(levelOfCareRtrvReq);
                            }
                            locEventDto.setIdPlocEvent(idEvent);
                            // If there were no validation errors, the record
                            // will be updated
                            // Update the PLOC record if there were no
                            // validation errors
                            personLOCPersonDao.updatePlocRecord(locEventDto);
                        }
                    }
                }
                /* validation error processing */
                // To do

                // 5. Initialize CSES15D DAM Input and Output Structures
                // Retrieve from Person LOC

                // Set CSES15D IdEvent
                if (errorCode == ServiceConstants.Zero) {

                    // Call CSES15D

                    /*******************************************
                     ** 6. (BEGIN): DAM: cinv43d ** To Do Completed Processing
                     *******************************************/
                    if (levelOfCareRtrvReq.getIdPlocEvent() != ServiceConstants.ZERO_VAL) {

                        // Initialize DAM Input Structure and call cinv43d
                        TodoUpdDtTodoCompletedInDto toDoUpdateCompleteDto = new TodoUpdDtTodoCompletedInDto();
                        toDoUpdateCompleteDto.setIdEvent(levelOfCareRtrvReq.getIdPlocEvent());
                        todoCompDao.updateTODOEvent(toDoUpdateCompleteDto);

                    }
                }
            }
        }

        return levelOfCareRtrvoDto;
    }

    /**
     * Method Name: callPostEvent Method Description: This function will save or
     * update the event detail
     *
     * @param LevelOfCareRtrvReq
     * @return long @
     */
    // Post Event Functionality
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Long callPostEvent(LevelOfCareRtrvReq levelOfCareRtrvReq) {
        // Populate Service input structure
        PostEventIPDto postEventReq = new PostEventIPDto();
        postEventReq.setIdCase(levelOfCareRtrvReq.getIdCase());
        ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();

        if (!ObjectUtils.isEmpty(levelOfCareRtrvReq)) {
            if (levelOfCareRtrvReq.getIdPlocEvent() == ServiceConstants.ZERO_VAL) {
                postEventReq.setDtEventOccurred(new Date());
                serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
            } else {
                EventDto eventDto = eventDao.getEventByid(levelOfCareRtrvReq.getIdPlocEvent());
                serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_UPDATE);
                // check initally it was dteventoccured
                postEventReq.setDtEventOccurred(eventDto.getDtEventOccurred());
            }
            /*
             * Copy Event Person Link information only if the a record is to be
             * inserted in the Event person Link
             */
            if (!StringUtils.isEmpty(levelOfCareRtrvReq.getReqFuncCd())
                    && ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(levelOfCareRtrvReq.getReqFuncCd())) {
                List<PostEventDto> postEventPersonList = new ArrayList<PostEventDto>();
                PostEventDto personLinkDto = new PostEventDto();
                personLinkDto.setIdPerson(levelOfCareRtrvReq.getIdPerson());
                personLinkDto.setCdScrDataAction(ServiceConstants.REQ_IND_AUD_ADD);
                postEventPersonList.add(personLinkDto);
                postEventReq.setPostEventDto(postEventPersonList);
            }
            if (!ObjectUtils.isEmpty(levelOfCareRtrvReq.getIdPlocEvent())) {
                postEventReq.setIdEvent(levelOfCareRtrvReq.getIdPlocEvent());
            } else {
                postEventReq.setIdEvent(ServiceConstants.ZERO_VAL);
            }
            postEventReq.setIdStage(levelOfCareRtrvReq.getIdStage());

            // Check this - id its person id
            postEventReq.setIdPerson(levelOfCareRtrvReq.getIdPersUpdt());
            postEventReq.setCdTask(levelOfCareRtrvReq.getCdTask());

            // Check
            // postEventReq.setSzCdEventType(levelOfCareRtrvReq.getCdEventType());
            if (!StringUtils.isEmpty(levelOfCareRtrvReq.getCdEventType())) {
                postEventReq.setCdEventType(levelOfCareRtrvReq.getCdEventType());
            } else {
                postEventReq.setCdEventType(CodesConstant.CEVNTTYP_LOC);
            }
            postEventReq.setEventDescr(levelOfCareRtrvReq.getEventDescr());
            postEventReq.setTsLastUpdate(levelOfCareRtrvReq.getLastUpdate());

            // Need to check
            // its value is "STATUS_PROCESS" if the end date is null and the
            // type is billing, otherwise
            // "STATUS_COMPLETE".
            if (ObjectUtils.isEmpty(levelOfCareRtrvReq.getDtPlocEnd())
                    && ServiceConstants.LOC_TYPE_BLOC.equalsIgnoreCase(levelOfCareRtrvReq.getCdPlocType())) {
                postEventReq.setCdEventStatus(CodesConstant.CEVTSTAT_PROC);
            } else {
                postEventReq.setCdEventStatus(CodesConstant.CEVTSTAT_COMP);
            }

            // Call PostEvent Service CCMN01U
            // PostEventRes response = eventService.postEvent(postEventReq);

            PostEventOPDto response = postEventService.checkPostEventStatus(postEventReq, serviceReqHeaderDto);

            if (!TypeConvUtil.isNullOrEmpty(response)) {
                idEvent = response.getIdEvent();
            }
        }
        return idEvent;
    }

    /**
     * Method Name: analyzeReturnOutput Method Description: This method will
     * check for eventstatus and set the error code
     *
     * @param LevelOfCareRtrvReq
     * @return long @
     */
    private LevelOfCareRtrvoDto analyzeReturnOutput(String eventStatus, LevelOfCareRtrvoDto levelOfCareRtrvoDto) {

        ErrorDto errorDto = new ErrorDto();
        // set error code for Multiple Instance
        if (eventStatus.equals(ServiceConstants.MSG_SYS_MULT_INST)) {
            errorDto.setErrorCode(Messages.MSG_SYS_MULT_INST);
            levelOfCareRtrvoDto.setErrorDto(errorDto);
            // set error code for Event MisMatch
        } else if (eventStatus.equals(ServiceConstants.MSG_SYS_EVENT_STS_MSMTCH)) {
            errorDto.setErrorCode(Messages.MSG_SYS_EVENT_STS_MSMTCH);
            levelOfCareRtrvoDto.setErrorDto(errorDto);
            // set error code for Stage Closed
        } else if (eventStatus.equals(ServiceConstants.MSG_SYS_STAGE_CLOSED)) {
            errorDto.setErrorCode(Messages.MSG_SYS_STAGE_CLOSED);
            levelOfCareRtrvoDto.setErrorDto(errorDto);
        }
        return levelOfCareRtrvoDto;
    }

    /**
     * Method Name: callPostEvent Method Description: This function is used to
     * fetch single ploc record for given PLOCEventId
     *
     * @param LevelOfCareRtrvReq
     * @param idPlocEvent
     * @return LevelOfCareRtrvoDto @
     */
    private LevelOfCareRtrvoDto fetchPLOCRecord(Long idPlocEvent, LevelOfCareRtrvoDto locRtrvDto) {
        PersonLocPersonInDto personLocInDto = new PersonLocPersonInDto();
        personLocInDto.setIdPLOCEvent(idPlocEvent);
        List<PersonLocPersonOutDto> personLocDtoList = null;
        try {
            personLocDtoList = personLOCPersonDao.fetchPersonLOCByIdPlocEvent(idPlocEvent);
            if (!TypeConvUtil.isNullOrEmpty(personLocDtoList)) {
                List<String> szTxtPlcmtRec = new ArrayList<String>();
                for (Integer count = ServiceConstants.Zero; count < personLocDtoList.size(); count++) {
                    // set the LevelOfCareRtrvoDto from the
                    // fetchPersonLOCByIdPlocEvent
                    PersonLocPersonOutDto personLocPersonOutDto = (PersonLocPersonOutDto) personLocDtoList.get(count);
                    locRtrvDto.setCdPLOCChild(personLocPersonOutDto.getCdPLOCChild());
                    locRtrvDto.setCdPLOCType(personLocPersonOutDto.getCdPLOCType());
                    locRtrvDto.setIdPerson(personLocPersonOutDto.getIdPerson());
                    locRtrvDto.setDtPLOCEnd(personLocPersonOutDto.getDtPLOCEnd());
                    locRtrvDto.setDtPLOCStart(personLocPersonOutDto.getDtPLOCStart());
                    locRtrvDto.setTxtComments(personLocPersonOutDto.getTxtComments());
                    locRtrvDto.setDtSubmitTPR(personLocPersonOutDto.getDtSubmitTPR());
                    locRtrvDto.setDtReviewCompleted(personLocPersonOutDto.getDtReviewCompleted());
                    locRtrvDto.setNmTPRConsultant(personLocPersonOutDto.getNmTPRConsultant());
                    locRtrvDto.setDtReviewConducted(personLocPersonOutDto.getDtReviewConducted());
                    locRtrvDto.setCdLevelChange(personLocPersonOutDto.getCdLvlChange());
                    locRtrvDto.setCdPlcmtSetting(personLocPersonOutDto.getCdPlcmtSetting());
                    locRtrvDto.setCdReviewType(personLocPersonOutDto.getCdReviewType());
                    locRtrvDto.setDtLastUpdatePLOC(personLocPersonOutDto.getDtLastUpdate());
                    locRtrvDto.setTxtDfpsComments(personLocPersonOutDto.getTxtDfpsComments());
                    locRtrvDto.setDtQrtpAssessmentCompleted(personLocPersonOutDto.getDtQrtpAssessmentCompleted());
                    locRtrvDto.setQrtpRecommended(personLocPersonOutDto.getQrtpRecommended());
                    szTxtPlcmtRec.add(personLocPersonOutDto.getTxtPlcmtRecommendation1());
                    szTxtPlcmtRec.add(personLocPersonOutDto.getTxtPlcmtRecommendation2());
                    szTxtPlcmtRec.add(personLocPersonOutDto.getTxtPlcmtRecommendation3());
                    szTxtPlcmtRec.add(personLocPersonOutDto.getTxtPlcmtRecommendation4());
                    szTxtPlcmtRec.add(personLocPersonOutDto.getTxtPlcmtRecommendation5());
                    szTxtPlcmtRec.add(personLocPersonOutDto.getTxtPlcmtRecommendation6());
                    locRtrvDto.setTxtPlacementRecord(szTxtPlcmtRec);
                    locRtrvDto.setNmLastUpdatedBy(personLocPersonOutDto.getNmLastUpdatedBy());
                }
            }
        } catch (DataAccessException e) {
            log.error(messageSource.getMessage("Cses15dDaoImpl.not.found", null, Locale.US));
        }
        return locRtrvDto;
    }

    /**
     * Method Name: initiazeInputForPOCInsertUpdateDtl Method Description: This
     * function is used to Initialize input object for caud11d & caude9d dam
     * calls
     *
     * @param plocDetailInDto
     * @param levelOfCareRtrvReq
     * @return PLOCDetailInDto @
     */
    private PLOCDetailInDto initiazeInputForPOCInsertUpdateDtl(PLOCDetailInDto plocDetailInDto,
                                                               LevelOfCareRtrvReq levelOfCareRtrvReq) {

        plocDetailInDto.setcReqFuncCd(levelOfCareRtrvReq.getReqFuncCd());

        /*
         ** if the service is in "add" mode, move the IdEvent from the service
         * output message to the DAM input message. Else, the DAM input IdEvent
         * is set to the IdEvent in the service input message.
         */
        /* Set IdEvent to common function IdEvent if in "add" mode */

        plocDetailInDto.setIdPlocEvent(levelOfCareRtrvReq.getIdPlocEvent());

        /*
         ** if LOC type is "billing", the window is in modify mode, and current
         * event status is "complete", set the Write history indicator flag
         *
         * the indicator to be set regardless if the event is closed or in
         * process. The indicator should have no effects on payment.
         */
        if (ServiceConstants.LOC_TYPE_BLOC.equalsIgnoreCase(levelOfCareRtrvReq.getCdPlocType())
                && (ServiceConstants.REQ_FUNC_CD_UPDATE).equalsIgnoreCase(levelOfCareRtrvReq.getReqFuncCd())) {

            plocDetailInDto.setcIndPlocWriteHistory(ServiceConstants.YES);
        } else {
            plocDetailInDto.setcIndPlocWriteHistory(ServiceConstants.NO);
        }

        plocDetailInDto.setDtLastUpdate(levelOfCareRtrvReq.getLastUpdate());
        // check
        plocDetailInDto.setIdPerson(levelOfCareRtrvReq.getIdPerson());
        plocDetailInDto.setCdPlocType(levelOfCareRtrvReq.getCdPlocType());
        plocDetailInDto.setCdPlocChild(levelOfCareRtrvReq.getCdPlocChild());
        plocDetailInDto.setbSysIndPrfrmValidation(levelOfCareRtrvReq.getbSysIndPrfrmValidation());
        plocDetailInDto.setcIndPlocCsupSend(levelOfCareRtrvReq.getcIndPlocCsupSend());
        plocDetailInDto.setDtPlocEnd(levelOfCareRtrvReq.getDtPlocEnd());
        plocDetailInDto.setDtPlocStart(levelOfCareRtrvReq.getDtPlocStart());

        // Save the ID for the person who is updating the record
        plocDetailInDto.setIdPersUpdt(levelOfCareRtrvReq.getIdPersUpdt());

        // Save the Review type
        plocDetailInDto.setCdRevType(levelOfCareRtrvReq.getCdRevType());

        plocDetailInDto.setIdStage(levelOfCareRtrvReq.getIdStage());
        plocDetailInDto.setTxtDfpsComments(levelOfCareRtrvReq.getTxtDfpsComments());
        if (ServiceConstants.LOC_TYPE_ALOC.equalsIgnoreCase(levelOfCareRtrvReq.getCdPlocType())
                && (ServiceConstants.LVL_TYPE_QRTP).equalsIgnoreCase(levelOfCareRtrvReq.getCdPlocChild())) {
            plocDetailInDto.setDtQrtpAssessmentCompleted(levelOfCareRtrvReq.getDtQrtpAssessmentCompleted());
            plocDetailInDto.setQrtpRecommended(levelOfCareRtrvReq.getQrtpRecommended());
        }
        return plocDetailInDto;
    }

    /**
     * Method Name: validateBeforeUpdate Method description: If its overlaps
     * some, then it must at least overlaps its immediate previous record, and
     * that's what we want to know
     *
     * @param plocDetailInDto
     * @param plocDetailDto
     * @return Integer
     */
    private Integer validateBeforeUpdate(PLOCDetailInDto plocDetailInDto, PLOCDetailDto plocDetailDto) {

        /* ================================================================= */
        /* VALIDATE 1: check for LEFT-SIDE OVERLAP */
        /* If new START_DATE overlaps any of its LEFT record(s) */
        /* (If its overlaps some, then it must at least overlaps its */
        /* immediate previous record, and that's what we want to know) */
        /* ================================================================= */

        /* ================================================================= */
        /* Unlike checking for GAP_EXIST_1 and GAP_EXIST_2 (where we check */
        /* for these gaps only if the new date for that end is different */
        /* from the corresponding date in the existing record) we must */
        /* always check for OVERLAP_1 and OVERLAP_2. */
        /* ================================================================= */

        Integer errorCode = ServiceConstants.Zero;
        Boolean leftGapExists = Boolean.FALSE;
        Boolean rightGapExists = Boolean.FALSE;
        Date currPLOCStart = DateUtils.getDateWithoutTime(plocDetailDto.getCurrPlocStart());
        Date currPLOCEnd = DateUtils.getDateWithoutTime(plocDetailDto.getCurrPlocEnd());
        String dtPLOCEnd;
        if (!ObjectUtils.isEmpty(plocDetailInDto.getDtPlocEnd())) {
            dtPLOCEnd = DateUtils.stringDt(plocDetailInDto.getDtPlocEnd());
        } else {
            dtPLOCEnd = ServiceConstants.MAX_JAVA_DATE;
        }

        List<PLOCDetailDto> personLOCRecordList = null;

        // Before Updating of the record, there are a set of
        // validations that have to be done. Only if all the
        // validations get through, the record will be
        // updated
        if (!plocDetailInDto.getCdPlocType().equalsIgnoreCase(ServiceConstants.RLOC_TYPE)) {
            personLOCRecordList = personLOCPersonDao.checkPlocUpdateValidation1(plocDetailInDto, currPLOCStart);
            if (!CollectionUtils.isEmpty(personLOCRecordList)) {
                errorCode = MSG_SRV_LVL_PERIOD_OVERLAP;
            } else {
                personLOCRecordList = personLOCPersonDao.checkPlocUpdateValidation2(plocDetailInDto, currPLOCEnd);
                if (!CollectionUtils.isEmpty(personLOCRecordList)) {
                    errorCode = Messages.MSG_SUB_PERIOD_OVERLAP_2;
                } else if (ServiceConstants.YES.equalsIgnoreCase(plocDetailInDto.getbSysIndPrfrmValidation())) {
                    personLOCRecordList = personLOCPersonDao.checkPlocUpdateValidation3(plocDetailInDto, currPLOCStart);
                    if (!CollectionUtils.isEmpty(personLOCRecordList)) {
                        leftGapExists = Boolean.TRUE;
                    }

                    personLOCRecordList = personLOCPersonDao.checkPlocUpdateValidation4(plocDetailInDto, dtPLOCEnd,
                            currPLOCEnd);
                    if (!CollectionUtils.isEmpty(personLOCRecordList)) {
                        rightGapExists = Boolean.TRUE;
                    }

                    // based on gap exists boolean fields values set error
                    // messages in response
                    if (leftGapExists && rightGapExists) {
                        errorCode = Messages.MSG_SUB_GAP_EXISTS_3;
                    } else if (leftGapExists) {
                        errorCode = Messages.MSG_SUB_GAP_EXISTS_1;
                    } else if (rightGapExists) {
                        errorCode = Messages.MSG_SUB_GAP_EXISTS_2;
                    }
                }
            }
        }
        return errorCode;
    }

    /**
     *
     * Method Name: getPersonLocAddUpdate Method Description: This service will
     * update and add service level aloc and bloc for TFC placement
     *
     * @param placementReq
     * @return LevelOfCareRtrvoDto
     *
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public LevelOfCareRtrvoDto getPersonLocAddUpdate(PlacementReq placementReq) {

        LevelOfCareRtrvoDto levelOfCareRtrvoDto = new LevelOfCareRtrvoDto();
        placementReq.setIdPerson(placementReq.getCommonDto().getIdPerson());
        placementReq.setIdCase(placementReq.getCommonDto().getIdCase());
        placementReq.setIdStage(placementReq.getCommonDto().getIdStage());

        if (ObjectUtils.isEmpty(placementReq.getIdPerson())
                || placementReq.getIdPerson() == ServiceConstants.ZERO_VAL) {
            ToDoStagePersonDto toDoStagePersonDto = new ToDoStagePersonDto();
            if (!ObjectUtils.isEmpty(placementReq)) {
                toDoStagePersonDto.setIdStage(placementReq.getIdStage());
                // CdStagePersRole is set to "primary child"
                toDoStagePersonDto.setCdStagePersRole(ServiceConstants.PRIMARY_CHILD);
            }
            // 1. Call CINV51D
            List<PersonAssignedIdToDoDto> personDtoList = personLOCPersonDao
                    .retrievePersonByRoleAndStage(toDoStagePersonDto);
            if (!CollectionUtils.isEmpty(personDtoList)) {
                placementReq.setIdPerson(personDtoList.get(0).getIdTodoPersAssigned());
            }
        }
        // call to update the Bloc service level max event id with end date
        LevelOfCareRtrvReq levelOfCareRtrvReqBlocUpdate = personLOCPersonDao.updateServiceLevel(placementReq,
                ServiceConstants.CPLOCELG_BLOC);
        // update event status
        if (!ObjectUtils.isEmpty(levelOfCareRtrvReqBlocUpdate)) {
            callPostEvent(levelOfCareRtrvReqBlocUpdate);
        }
        // after end bloc exist service level create the new bloc event
        LevelOfCareRtrvReq levelOfCareRtrvReqBlocAdd = addServiceLevel(placementReq, ServiceConstants.CPLOCELG_BLOC);
        levelOfCareSaveService(levelOfCareRtrvReqBlocAdd);

        // call to update the Aloc service level max event id with end date
        LevelOfCareRtrvReq levelOfCareRtrvReqAlocUpdate = personLOCPersonDao.updateServiceLevel(placementReq,
                ServiceConstants.CPLOCELG_ALOC);
        if (!ObjectUtils.isEmpty(levelOfCareRtrvReqAlocUpdate)) {
            callPostEvent(levelOfCareRtrvReqAlocUpdate);
        }
        // after end aloc exist service level create the new aloc event
        LevelOfCareRtrvReq levelOfCareRtrvReqAlocAdd = addServiceLevel(placementReq, ServiceConstants.CPLOCELG_ALOC);
        levelOfCareRtrvoDto = levelOfCareSaveService(levelOfCareRtrvReqAlocAdd);
        return levelOfCareRtrvoDto;
    }

    /**
     *
     * Method Name: getPersonLocAddUpdate Method Description: This service will
     * update and add service level aloc and bloc for QRTP placement
     *
     * @param placementReq
     * @return LevelOfCareRtrvoDto
     *
     */
    @Override
    public LevelOfCareRtrvoDto getPersonLocAddUpdateforQRTP(PlacementReq placementReq) {

        LevelOfCareRtrvoDto levelOfCareRtrvoDto = new LevelOfCareRtrvoDto();
        placementReq.setIdPerson(placementReq.getCommonDto().getIdPerson());
        placementReq.setIdCase(placementReq.getCommonDto().getIdCase());
        placementReq.setIdStage(placementReq.getCommonDto().getIdStage());

        if (ObjectUtils.isEmpty(placementReq.getIdPerson())
                || placementReq.getIdPerson() == ServiceConstants.ZERO_VAL) {
            ToDoStagePersonDto toDoStagePersonDto = new ToDoStagePersonDto();
            if (!ObjectUtils.isEmpty(placementReq)) {
                toDoStagePersonDto.setIdStage(placementReq.getIdStage());
                // CdStagePersRole is set to "primary child"
                toDoStagePersonDto.setCdStagePersRole(ServiceConstants.PRIMARY_CHILD);
            }
            // 1. Call CINV51D
            List<PersonAssignedIdToDoDto> personDtoList = personLOCPersonDao
                    .retrievePersonByRoleAndStage(toDoStagePersonDto);
            if (!CollectionUtils.isEmpty(personDtoList)) {
                placementReq.setIdPerson(personDtoList.get(0).getIdTodoPersAssigned());
            }
        }
        //Existing ALOC or BLOC (of any Level), it will be ended with End Date same as Start Date of the new LOCs.
        if (placementReq.getIsNewPlacement()) {
            //Fetch open LOC and update the open loc enddate with startdt of new loc(Aloc and BLOC)
            List<LevelOfCareRtrvReq> levelsOfCareRtrvReqList = personLOCPersonDao.
                    fetchOpenServiceLevels(placementReq);
            // update Event
            for (LevelOfCareRtrvReq levelsOfCareRtrvReq : levelsOfCareRtrvReqList) {
                if (!ObjectUtils.isEmpty(levelsOfCareRtrvReq)) {
                    callPostEvent(levelsOfCareRtrvReq);
                }
            }
        }

        if (!placementReq.getIsNewPlacement()) {
            // call to update the Bloc service level max event id with end date
            LevelOfCareRtrvReq levelOfCareRtrvReqBlocUpdate = personLOCPersonDao.updateServiceLevelForQRTP(placementReq,
                    ServiceConstants.CPLOCELG_BLOC);
            // update event status
            if (!ObjectUtils.isEmpty(levelOfCareRtrvReqBlocUpdate)) {
                callPostEvent(levelOfCareRtrvReqBlocUpdate);
            }
        }
        // after end bloc exist service level create the new bloc event
        if (placementReq.getIsNewPlacement()) {
            LevelOfCareRtrvReq levelOfCareRtrvReqBlocAdd = addServiceLevelforQRTP(placementReq, ServiceConstants.CPLOCELG_BLOC);
            levelOfCareSaveService(levelOfCareRtrvReqBlocAdd);
        }

        if (!placementReq.getIsNewPlacement()) {
            // call to update the Aloc service level max event id with end date
            LevelOfCareRtrvReq levelOfCareRtrvReqAlocUpdate = personLOCPersonDao.
                    updateServiceLevelForQRTP(placementReq,
                            ServiceConstants.CPLOCELG_ALOC);
            if (!ObjectUtils.isEmpty(levelOfCareRtrvReqAlocUpdate)) {
                callPostEvent(levelOfCareRtrvReqAlocUpdate);
            }
        }

        if (placementReq.getIsNewPlacement()) {
            // after end aloc exist service level create the new aloc event
            LevelOfCareRtrvReq levelOfCareRtrvReqAlocAdd = addServiceLevelforQRTP(placementReq, ServiceConstants.CPLOCELG_ALOC);
            levelOfCareRtrvoDto = levelOfCareSaveService(levelOfCareRtrvReqAlocAdd);
        }
        return levelOfCareRtrvoDto;
    }


    @Override
    public Date getPlacementStartDt(long caseId, Long stageId) {
        return personLOCPersonDao
                .getQrtpPlacementStartDate(caseId, stageId);
    }

    /**
     *
     * Method Name: addServiceLevel Method Description: This service will add
     * service level aloc and bloc for TFC placement
     *
     * @param placementReq
     * @param placementType
     * @return LevelOfCareRtrvoDto
     *
     */
    private LevelOfCareRtrvReq addServiceLevel(PlacementReq placementReq, String placementType) {

        LevelOfCareRtrvReq levelOfCareRtrvReq = new LevelOfCareRtrvReq();
        levelOfCareRtrvReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
        levelOfCareRtrvReq.setIdEvent(null);
        levelOfCareRtrvReq.setIdPerson(placementReq.getIdPerson());
        if (ServiceConstants.CPLOCELG_BLOC.equals(placementType)) {
            levelOfCareRtrvReq.setcIndPlocCsupSend(ServiceConstants.YES);
            levelOfCareRtrvReq.setbSysIndPrfrmValidation(ServiceConstants.NO);
        } else {
            levelOfCareRtrvReq.setcIndPlocCsupSend(ServiceConstants.N);
            levelOfCareRtrvReq.setbSysIndPrfrmValidation(ServiceConstants.Y);
        }
        levelOfCareRtrvReq.setIdCase(placementReq.getIdCase());
        SimpleDateFormat simpleDate = new SimpleDateFormat("MM/dd/yyyy");
        if (ServiceConstants.CPLOCELG_BLOC.equals(placementType)) {
            levelOfCareRtrvReq.setEventDescr(placementReq.getBlocEventDesc()
                    + simpleDate.format(placementReq.getDtPlcmtStart()));
        } else {
            Date dtDtPlocEnd = DateHelper.addToDate(placementReq.getDtPlcmtStart(), 0, 9, 0);
            levelOfCareRtrvReq.setEventDescr(placementReq.getAlocEventDesc()
                    + simpleDate.format(placementReq.getDtPlcmtStart())
                    + ServiceConstants.SPACE + END + simpleDate.format(dtDtPlocEnd));
        }
        levelOfCareRtrvReq.setCdTask(PLOC_TASKCODE);
        levelOfCareRtrvReq.setIdStage(placementReq.getIdStage());
        levelOfCareRtrvReq.setIdEventPerson(placementReq.getCommonDto().getIdUser());
        levelOfCareRtrvReq.setIdPersUpdt(placementReq.getCommonDto().getIdUser());
        if (ServiceConstants.CPLOCELG_BLOC.equals(placementType)) {
            levelOfCareRtrvReq.setCdPlocType(ServiceConstants.CPLOCELG_BLOC);
        } else {
            levelOfCareRtrvReq.setCdPlocType(ServiceConstants.CPLOCELG_ALOC);
        }
        levelOfCareRtrvReq.setCdPlocChild(ServiceConstants.CATHPLOC_260);
        levelOfCareRtrvReq.setCdEventStatus(ServiceConstants.CEVTSTAT_PROC);
        levelOfCareRtrvReq.setIndDateModified(ServiceConstants.N);
        levelOfCareRtrvReq.setDtPlocStart(placementReq.getDtPlcmtStart());

        // bloc the end date is max date for Aloc the end date must be placement
        // start date + 9 months
        if (ServiceConstants.CPLOCELG_BLOC.equals(placementType)) {
            levelOfCareRtrvReq.setDtPlocEnd(ServiceConstants.MAX_DATE);
        } else {
            Date dtDtPlocEnd = placementReq.getDtPlcmtStart();
            dtDtPlocEnd = DateHelper.addToDate(dtDtPlocEnd, 0, 9, 0);
            levelOfCareRtrvReq.setDtPlocEnd(dtDtPlocEnd);
        }

        levelOfCareRtrvReq.setIdPlocEvent(0l);
        levelOfCareRtrvReq.setCdRevType("UR");
        levelOfCareRtrvReq.setCdEventType(EVENT_TYPE_LOC);
        levelOfCareRtrvReq.setSkipValidationCheck(true);

        return levelOfCareRtrvReq;
    }

    /**
     *
     * Method Name: addServiceLevel Method Description: This service will add
     * service level aloc and bloc for QRTP placement
     *
     * @param placementReq
     * @param placementType
     * @return LevelOfCareRtrvoDto
     *
     */
    private LevelOfCareRtrvReq addServiceLevelforQRTP(PlacementReq placementReq, String placementType) {

        LevelOfCareRtrvReq levelOfCareRtrvReq = new LevelOfCareRtrvReq();
        levelOfCareRtrvReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
        levelOfCareRtrvReq.setIdEvent(null);
        levelOfCareRtrvReq.setIdPerson(placementReq.getIdPerson());
        if (ServiceConstants.CPLOCELG_BLOC.equals(placementType)) {
            levelOfCareRtrvReq.setcIndPlocCsupSend(ServiceConstants.YES);
            levelOfCareRtrvReq.setbSysIndPrfrmValidation(ServiceConstants.NO);
            levelOfCareRtrvReq.setCdPlocType(ServiceConstants.CPLOCELG_BLOC);
            levelOfCareRtrvReq.setEventDescr(placementReq.getBlocEventDesc());
        } else {
            levelOfCareRtrvReq.setcIndPlocCsupSend(ServiceConstants.N);
            levelOfCareRtrvReq.setbSysIndPrfrmValidation(ServiceConstants.Y);
            levelOfCareRtrvReq.setCdPlocType(ServiceConstants.CPLOCELG_ALOC);
            levelOfCareRtrvReq.setEventDescr(placementReq.getAlocEventDesc());

        }
        levelOfCareRtrvReq.setIdCase(placementReq.getIdCase());


        levelOfCareRtrvReq.setCdTask(PLOC_TASKCODE);
        levelOfCareRtrvReq.setIdStage(placementReq.getIdStage());
        levelOfCareRtrvReq.setIdEventPerson(placementReq.getCommonDto().getIdUser());
        levelOfCareRtrvReq.setIdPersUpdt(placementReq.getCommonDto().getIdUser());

        levelOfCareRtrvReq.setCdPlocChild(ServiceConstants.CATHPLOC_280);
        levelOfCareRtrvReq.setCdEventStatus(ServiceConstants.CEVTSTAT_PROC);
        levelOfCareRtrvReq.setIndDateModified(ServiceConstants.N);
        levelOfCareRtrvReq.setDtPlocStart(placementReq.getDtPlcmtStart());

        // bloc the end date is max date for Aloc the end date must be placement
        // start date + 9 months
        if (ServiceConstants.CPLOCELG_BLOC.equals(placementType)) {
            //if placement end date is present use that
            if (!placementReq.getBlocEventDesc().contains(ServiceConstants.END))
                levelOfCareRtrvReq.setDtPlocEnd(ServiceConstants.MIN_CONSTANTS_DATE);
            else
                levelOfCareRtrvReq.setDtPlocEnd(null != placementReq.getDtPlcmtEnd() ? placementReq.getDtPlcmtEnd() : ServiceConstants.MAX_DATE);

        } else {

            levelOfCareRtrvReq.setDtPlocEnd(null != placementReq.getDtPlcmtEnd() ? placementReq.getDtPlcmtEnd() : ServiceConstants.MAX_DATE);
            levelOfCareRtrvReq.setCdRevType("GW");
        }

        levelOfCareRtrvReq.setIdPlocEvent(0l);
        levelOfCareRtrvReq.setCdEventType(EVENT_TYPE_LOC);
        levelOfCareRtrvReq.setSkipValidationCheck(true);

        return levelOfCareRtrvReq;
    }
}
