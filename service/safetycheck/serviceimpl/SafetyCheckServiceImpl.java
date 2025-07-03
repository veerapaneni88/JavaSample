package us.tx.state.dfps.service.safetycheck.serviceimpl;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.CapsResource;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.HMSafetyCheck;
import us.tx.state.dfps.common.domain.HMSafetyCheckAttachment;
import us.tx.state.dfps.common.domain.HMSafetyCheckLink;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Placement;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.response.SafetyCheckRes;
import us.tx.state.dfps.service.formreferrals.dao.FormReferralsDao;
import us.tx.state.dfps.service.heightenedmonitoring.dao.HeightenedMonitoringDao;
import us.tx.state.dfps.service.hmm.dto.SafetyCheckAttachmentDto;
import us.tx.state.dfps.service.hmm.dto.SafetyCheckChildInfoDto;
import us.tx.state.dfps.service.hmm.dto.SafetyCheckDto;
import us.tx.state.dfps.service.hmm.dto.SafetyCheckListDto;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.resource.dto.ResourceDto;
import us.tx.state.dfps.service.safetycheck.dao.SafetyCheckDao;
import us.tx.state.dfps.service.safetycheck.service.SafetyCheckService;
import us.tx.state.dfps.service.subcare.dao.CapsResourceDao;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.service.workload.dao.WorkLoadDao;
import us.tx.state.dfps.service.workload.dto.PostEventDto;
import us.tx.state.dfps.service.workload.dto.PostEventIPDto;
import us.tx.state.dfps.service.workload.dto.PostEventOPDto;
import us.tx.state.dfps.service.workload.dto.TodoDto;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * Class Description:SafetyCheckServiceImpl is the ServiceImpl class for Safety Check Detail Page
 */
@Service
@Transactional
public class SafetyCheckServiceImpl implements SafetyCheckService {

    @Autowired
    SafetyCheckDao safetyCheckDao;

    @Autowired
    CapsResourceDao capsResourceDao;

    @Autowired
    MessageSource messageSource;

    @Autowired
    PostEventService postEventService;

    @Autowired
    TodoDao todoDao;

    @Autowired
    FormReferralsDao formReferralsDao;

    @Autowired
    WorkLoadDao workLoadDao;

    @Autowired
    PersonDao personDao;

    @Autowired
    HeightenedMonitoringDao heightenedMonitoringDao;

    @Autowired
    EventDao eventDao;

    private static final Logger LOG = Logger.getLogger("SafetyCheckServiceImpl");
    private static final String SAFETY_CHECK_EVENT_DESC= "Safety Check";
    /**
     * PPM 60692-artf178537-Start-Changes for Safety check List
     * Method Description: This method retrieves all the safety checks done for a resource
     *
     * @param idResource
     * @return SafetyCheckRes
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
    public SafetyCheckDto getSafetyCheckDetail(Long idResource) {
        SafetyCheckDto safetyCheckDto=new SafetyCheckDto();
        populateRsrcDetails( idResource,  safetyCheckDto);
        //fetch safety check details
        List<SafetyCheckListDto> safetyCheckListDtoList=safetyCheckDao.getSafetyCheckDetails(idResource);
        safetyCheckListDtoList.forEach(dto->dto.setCdEventType(CodesConstant.CEVNTTYP_SFC));
        safetyCheckDto.setSafetyCheckList(safetyCheckListDtoList);
        return safetyCheckDto;
    }

    /**
     * PPM 60692-artf179776- Safety Check Details for Placement
     * This method fetched all safety check records for a placement
     * @param idStage
     * @return List
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
    public SafetyCheckRes getSafetyCheckListForPlcmnt(Long idStage) {
        SafetyCheckRes safetyCheckRes = new SafetyCheckRes();
        List<SafetyCheckListDto> safetyCheckListDtoList=safetyCheckDao.getSafetyCheckListForPlcmnt(idStage);
        safetyCheckListDtoList.forEach(dto->dto.setCdEventType(CodesConstant.CEVNTTYP_SFC));
        SafetyCheckDto safetyCheckDto = new SafetyCheckDto();
        safetyCheckDto.setSafetyCheckList(safetyCheckListDtoList);
        safetyCheckRes.setSafetyCheckDto(safetyCheckDto);
        return safetyCheckRes;
    }

    /**
     * PPM 60692-artf179566- loadSafetyDetail
     * Method Description: This method is used to fetch safety check Details
     *
     * @param idResource
     * @param idHmSafetyCheck
     * @return SafetyCheckDto
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
    public SafetyCheckDto loadSafetyDetail(Long idResource,Long idHmSafetyCheck) {
        SafetyCheckDto safetyCheckDto=new SafetyCheckDto();
        populateRsrcDetails( idResource,  safetyCheckDto);

        if (ObjectUtils.isEmpty(idHmSafetyCheck)) {
            // add safety check
            //load the details of kids placed in this operation
            safetyCheckDto.setSafetyCheckChildInfoDtoList(safetyCheckDao.getChildInfoForRsrc(idResource));
        }else {
            // edit safety check
            processLoad(safetyCheckDto,idHmSafetyCheck);
            // if  event status is PROC then merge the saved child list and the initial childinfo list
            if (ServiceConstants.PROCESS_EVENT_STATUS.equalsIgnoreCase(safetyCheckDto.getCdEventStatus())) {
               List<SafetyCheckChildInfoDto> childDefaultList=safetyCheckDao.getChildInfoForRsrc(safetyCheckDto.getIdResource());
               //in case there are saved children info then remove them from initial list and load from the HM child table with their details
                if(!ObjectUtils.isEmpty(safetyCheckDto.getSavedChildrenStageId())){
                    childDefaultList=childDefaultList.stream().filter(dto-> {
                        return !safetyCheckDto.getSavedChildrenStageId().contains(dto.getIdStage());
                    }).collect(Collectors.toList());
                    //after removing from the default list load from HM child table details
                    childDefaultList.addAll(0,safetyCheckDto.getSafetyCheckChildInfoDtoList());
                }
                safetyCheckDto.setSafetyCheckChildInfoDtoList(childDefaultList);
            }

        }
        return safetyCheckDto;
    }

    /**
     * PPM 60692-artf179566- saveSafetyCheck
     * Method Description: This method is used to save safety check Details
     *
     * @param safetyCheckDto
     * @return safetyCheckDto
     */
    @Override
    @Transactional(rollbackFor = {
            Exception.class })
    public SafetyCheckDto saveSafetyCheck(SafetyCheckDto safetyCheckDto,Long idUser) {

         HMSafetyCheck hmSafetyCheck =null;
        Set<HMSafetyCheckLink> hmSafetyCheckLinks=null;
         Set<HMSafetyCheckAttachment> hmSafetyCheckAttachments=null;
        //check if it is first save or edit
        if (ObjectUtils.isEmpty(safetyCheckDto.getIdHMSafetyCheck())) {
            //first save-save to the safety check ,safety check link,attachment table. Create event for each selected stage id in PROC
            hmSafetyCheck = new HMSafetyCheck();
            Person person=new Person();
            person.setIdPerson(idUser);
            hmSafetyCheck.setCreatedPerson(person);
            hmSafetyCheck.setDtCreated(new Date());
        } else {
            //load saved entities
            hmSafetyCheck=safetyCheckDao.loadHMSafetyCheck(safetyCheckDto.getIdHMSafetyCheck());
            hmSafetyCheckLinks=hmSafetyCheck.getHmSafetyCheckLink();
            hmSafetyCheckAttachments =hmSafetyCheck.getHmSafetyCheckAttachment();
        }
        Long idHMSafetyCheck=processSave(safetyCheckDto,idUser,hmSafetyCheck,hmSafetyCheckLinks,hmSafetyCheckAttachments);
        safetyCheckDto.setIdHMSafetyCheck(idHMSafetyCheck);
        return safetyCheckDto;
    }

    /**
     * PPM 60692-artf179566- deleteSafetyCheck
     * Method Description: This method is used to delete safety check Details
     *
     * @param safetyCheckDto
     * @return safetyCheckDto
     */
    @Override
    public void deleteSafetyCheck(SafetyCheckDto safetyCheckDto) {
        //delete saved entities
        safetyCheckDao.deleteSafetyCheck(safetyCheckDao.loadHMSafetyCheck(safetyCheckDto.getIdHMSafetyCheck()));
    }

    /**
     * PPM 60692-artf179566- deleteAttachment
     * Method Description: This method is used to delete Attachment
     *
     * @param idHMSafetyCheckAttachment
     * @return void
     */
    @Override
    public void deleteAttachment(Long idHMSafetyCheckAttachment) {
        //delete saved attachment
        safetyCheckDao.deleteAttachment(idHMSafetyCheckAttachment);
    }

    /**
     * Artifact artf179776 : Safety Check Details for Placement
     * getSafetyCheckDetailForPlcmnt
     * This method gets details of the safety check record selected from the list or from the event list
     * @param idHmSafetyCheck
     * @param idEvent
     * @param idResource
     * @return safetyCheckDto
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
    public SafetyCheckDto getSafetyCheckDetailForPlcmnt(Long idHmSafetyCheck, Long idEvent, Long idResource) {
        SafetyCheckDto safetyCheckDto = new SafetyCheckDto();
        List<SafetyCheckChildInfoDto> dtoList = new ArrayList<SafetyCheckChildInfoDto>();
        SafetyCheckChildInfoDto safetyCheckChildInfoDto;
        if(idResource != 0L) {
            safetyCheckChildInfoDto = safetyCheckDao.getSafetyCheckDetailForPlcmnt(idHmSafetyCheck, idEvent);
        } else{
            safetyCheckChildInfoDto = safetyCheckDao.getSafetyCheckDetailForPlcmnt(idEvent);
            idResource = safetyCheckChildInfoDto.getIdResource();
        }
        populateRsrcDetails(idResource, safetyCheckDto);
        dtoList.add(safetyCheckChildInfoDto);
        safetyCheckDto.setSafetyCheckChildInfoDtoList(dtoList);
        return safetyCheckDto;
    }

    /**
     * populateRsrcDetails
     * This method fetches resource details by an idResource
     * @param idResource
     * @param safetyCheckDto
     */
      private void populateRsrcDetails (Long idResource, SafetyCheckDto safetyCheckDto){
            //Fetch resource details
            ResourceDto resourceDto = capsResourceDao.getResourceById(idResource);
            safetyCheckDto.setIdResource(idResource);
            safetyCheckDto.setNmResource(resourceDto.getNmResource());
            safetyCheckDto.setCdRsrcType(resourceDto.getCdRsrcType());
            safetyCheckDto.setCdRsrcFacilType(resourceDto.getCdRsrcFacilType());
            safetyCheckDto.setRsrcActive(CodesConstant.CRSCSTAT_01.equalsIgnoreCase(resourceDto.getCdRsrcStatus()));
        }

    /**
     * Method Name: createAndReturnEventid Method Description:Method to generate
     * event id in event table.
     *
     * @param safetyCheckChildInfoDto,idUser,eventStatus
     * @return Long
     */
    private Long createOrUpdateEventid(SafetyCheckChildInfoDto safetyCheckChildInfoDto,Long idUser,String eventStatus) {
        PostEventIPDto postEventIPDto = new PostEventIPDto();
        Date date = new Date(System.currentTimeMillis());
        ServiceReqHeaderDto archInputDto = new ServiceReqHeaderDto();
        PostEventDto postEventDto = new PostEventDto();
        List<PostEventDto> postEventDtoList = new ArrayList<>();
        postEventIPDto.setIdPerson(idUser);
        postEventIPDto.setIdStage(safetyCheckChildInfoDto.getIdStage());
        postEventIPDto.setUserId(""+idUser);
        if (!(ObjectUtils.isEmpty(safetyCheckChildInfoDto.getIdEvent()))) {
            archInputDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_UPDATE);
            postEventIPDto.setIdEvent(safetyCheckChildInfoDto.getIdEvent());
           //Fetch the event entity
            Event eventEntity=eventDao.getEventById(safetyCheckChildInfoDto.getIdEvent());
            postEventIPDto.setDtEventOccurred(eventEntity.getDtEventOccurred());
            postEventIPDto.setEventDescr(eventEntity.getTxtEventDescr());
            postEventIPDto.setCdEventType(eventEntity.getCdEventType());
            //IdCase needs to be set only on UPDATE as EVENT table trigger adds idCase on INSERT
            postEventIPDto.setIdCase(eventEntity.getIdCase());
            if(eventEntity.getEventPersonLinks() != null)
            eventEntity.getEventPersonLinks().forEach(personLinkEntity-> postEventDto.setIdEventPersonLink(personLinkEntity.getIdEventPersLink()));
        } else {
            postEventIPDto.setDtEventOccurred(date);
            postEventIPDto.setEventDescr(SAFETY_CHECK_EVENT_DESC);
            archInputDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
            postEventIPDto.setCdEventType(CodesConstant.CEVNTTYP_SFC);
        }
        postEventIPDto.setTsLastUpdate(date);
        postEventIPDto.setCdEventStatus(eventStatus);
        //Setting the list to write to event_person_link table
        postEventDto.setIdPerson(safetyCheckChildInfoDto.getIdPerson());
        postEventDto.setCdScrDataAction(archInputDto.getReqFuncCd());
        postEventDtoList.add(postEventDto);
        postEventIPDto.setPostEventDto(postEventDtoList);
        //calling method to create/update an event and create/update entry in event_person_link table
        PostEventOPDto postEventOPDto = postEventService.checkPostEventStatus(postEventIPDto, archInputDto);
        return postEventOPDto.getIdEvent();
    }

    private Long processSave(SafetyCheckDto safetyCheckDto,Long idUser,HMSafetyCheck hmSafetyCheck,Set<HMSafetyCheckLink> hmSafetyCheckLinks,Set<HMSafetyCheckAttachment> hmSafetyCheckAttachments) {
        Function<Long, Person> idperson = id -> {
            Person person = new Person();
            person.setIdPerson(id);
            return person;
        };
        Function<Long, Event> idEvent = id -> {
            Event event = new Event();
            event.setIdEvent(id);
            return event;
        };
        Function<Long, Placement> idPlacementEvent = id -> {
            Placement event = new Placement();
            event.setIdPlcmtEvent(id);
            return event;
        };
        Set<HMSafetyCheckLink> hmSafetyCheckLinkSet=new HashSet<>();
        Set<HMSafetyCheckAttachment> hmSafetyCheckAttachmentSet=new HashSet<>();
        //set resource id
        CapsResource resource = new CapsResource();
        resource.setIdResource(safetyCheckDto.getIdResource());
        hmSafetyCheck.setCapsResource(resource);
        //set status
        hmSafetyCheck.setCdStatus(safetyCheckDto.getCdEventStatus());
        hmSafetyCheck.setLastUpdatedPerson(idperson.apply(idUser));
        hmSafetyCheck.setDtLastUpdated(new Date());
        final List<Long> childInfoDtoRemovalIdList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(safetyCheckDto.getSafetyCheckChildInfoDtoList())) {
            safetyCheckDto.getSafetyCheckChildInfoDtoList().forEach(childdto -> {
                HMSafetyCheckLink hmSafetyCheckLink =null;
                if(ObjectUtils.isEmpty(childdto.getIdHMSafetyCheckLink())) {
                    hmSafetyCheckLink =new HMSafetyCheckLink();
                    hmSafetyCheckLink.setCreatedPerson(idperson.apply(idUser));
                    hmSafetyCheckLink.setDtCreated(new Date());
                }else {
                    hmSafetyCheckLink=hmSafetyCheckLinks.stream().filter(entity->  entity.getIdHMSafetyCheckLink().equals(childdto.getIdHMSafetyCheckLink())).findFirst().get();
                    childInfoDtoRemovalIdList.add(childdto.getIdHMSafetyCheckLink());
                }
                Long eventId = createOrUpdateEventid(childdto,idUser,safetyCheckDto.getCdEventStatus());
                hmSafetyCheckLink.setHmSafetyCheck(hmSafetyCheck);

                hmSafetyCheckLink.setLastUpdatedPerson(idperson.apply(idUser));
                hmSafetyCheckLink.setDtLastUpdated(new Date());
                hmSafetyCheckLink.setChild(idperson.apply(childdto.getIdPerson()));
                Stage stage = new Stage();
                stage.setIdStage(childdto.getIdStage());
                hmSafetyCheckLink.setStage(stage);
                hmSafetyCheckLink.setEvent(idEvent.apply(eventId));
                hmSafetyCheckLink.setPlacementEvent(idPlacementEvent.apply(childdto.getIdPlacementEvent()));
                hmSafetyCheckLink.setDtSafetyCheck(childdto.getSafetyCheckDate());
                hmSafetyCheckLink.setIndAbuseNeglectReportInitiated(childdto.getAbuseNeglectReportInitiated());
                hmSafetyCheckLink.setIndChildSafe(childdto.getChildSafe());
                hmSafetyCheckLink.setIdCall(childdto.getCallIdNumber());
                hmSafetyCheckLinkSet.add(hmSafetyCheckLink);
               //Create alerts for caseworker and supervisor on save and submit
                if(ServiceConstants.COMPLETE_EVENT_STATUS.equalsIgnoreCase(safetyCheckDto.getCdEventStatus())){
                    //primary worker alert
                    List<Long> assignedCaseWorkerList = workLoadDao.getAssignedWorkersForStage(childdto.getIdStage());
                    String nmChildFull=personDao.getPerson(childdto.getIdPerson()).getNmPersonFull();
                    String nmrsrc=capsResourceDao.getResourceById(safetyCheckDto.getIdResource()).getNmResource();
                    ServiceReqHeaderDto serviceReqHeaderDto=new ServiceReqHeaderDto();
                    serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
                    assignedCaseWorkerList.forEach(idCaseWorker->{
                        todoDao.todoAUD(populateToDo(childdto.getIdCase(),childdto.getIdStage(),idCaseWorker,nmChildFull,safetyCheckDto.getIdResource(),nmrsrc,eventId), serviceReqHeaderDto);
                        Long idSupervisor=formReferralsDao.getSupervisorId(idCaseWorker);
                        //Supervisor alert
                        todoDao.todoAUD(populateToDo(childdto.getIdCase(),childdto.getIdStage(),idSupervisor,nmChildFull,safetyCheckDto.getIdResource(),nmrsrc,eventId), serviceReqHeaderDto);
                    });

                }
            });
        }
            if (!ObjectUtils.isEmpty(safetyCheckDto.getSafetyCheckAttachmentDtoList())) {
                safetyCheckDto.getSafetyCheckAttachmentDtoList().forEach(attachmentDto -> {

                    HMSafetyCheckAttachment hmSafetyCheckAttachment =null;
                    if(ObjectUtils.isEmpty(attachmentDto.getIdHMSafetyCheckAttachment())) {
                        hmSafetyCheckAttachment = new HMSafetyCheckAttachment();
                        hmSafetyCheckAttachment.setCreatedPerson(idperson.apply(idUser));
                        hmSafetyCheckAttachment.setDtCreated(new Date());
                    }else {
                        hmSafetyCheckAttachment=hmSafetyCheckAttachments.stream().filter(entity-> entity.getIdHMSafetyCheckAttachment().equals(attachmentDto.getIdHMSafetyCheckAttachment())).findFirst().get();
                    }
                    hmSafetyCheckAttachment.setHmSafetyCheck(hmSafetyCheck);
                    hmSafetyCheckAttachment.setLastUpdatedPerson(idperson.apply(idUser));
                    hmSafetyCheckAttachment.setDtLastUpdated(new Date());
                    hmSafetyCheckAttachment.setNmFile(attachmentDto.getFileName());
                    hmSafetyCheckAttachment.setSysMimeType(attachmentDto.getMimeType());
                    try {
                        hmSafetyCheckAttachment.setFileDocumentData(new SerialBlob(attachmentDto.getAttachmentData()));
                    } catch (SQLException e) {
                        LOG.error("Exception on setting blob to attachment" + e.getMessage());
                    }
                    hmSafetyCheckAttachmentSet.add(hmSafetyCheckAttachment);
                });

            }
        hmSafetyCheck.setHmSafetyCheckLink(hmSafetyCheckLinkSet);
        hmSafetyCheck.setHmSafetyCheckAttachment(hmSafetyCheckAttachmentSet);
        safetyCheckDao.saveOrUpdate(hmSafetyCheck);
        // Delete those child records that were previously saved but unselected in the subsequent save
        if(!ObjectUtils.isEmpty(hmSafetyCheckLinks)) {
            List<Long> savedStageIds=hmSafetyCheckLinks.stream().map(entity -> entity.getIdHMSafetyCheckLink()).collect(Collectors.toList());
            savedStageIds.removeAll(childInfoDtoRemovalIdList);
           safetyCheckDao.deleteHmSafetyCheckChildInfo(new ArrayList<>(savedStageIds));
        }

        return hmSafetyCheck.getIdHMSafetyCheck();
    }

    private void processLoad(SafetyCheckDto safetyCheckDto,Long idHmSafetyCheck) {
        List<SafetyCheckChildInfoDto> safetyCheckChildInfoDtoList = new ArrayList<>();
        List<SafetyCheckAttachmentDto> safetyCheckAttachmentDtoList = new ArrayList<>();
        Set<Long> savedChildrenStageId=new HashSet<>();
        HMSafetyCheck hmSafetyCheck = safetyCheckDao.loadHMSafetyCheck(idHmSafetyCheck);
        safetyCheckDto.setIdHMSafetyCheck(hmSafetyCheck.getIdHMSafetyCheck());
        safetyCheckDto.setCdEventStatus(hmSafetyCheck.getCdStatus());
        //load saved entities
        Set<HMSafetyCheckLink> hmSafetyCheckLinks = hmSafetyCheck.getHmSafetyCheckLink();
        Set<HMSafetyCheckAttachment> hmSafetyCheckAttachments = hmSafetyCheck.getHmSafetyCheckAttachment();
        if(!ObjectUtils.isEmpty(hmSafetyCheckLinks)) {
            hmSafetyCheckLinks.forEach(childentity->{
                SafetyCheckChildInfoDto safetyCheckChildInfoDto=new SafetyCheckChildInfoDto();
                safetyCheckChildInfoDto.setIdHmSafetyCheck(childentity.getHmSafetyCheck().getIdHMSafetyCheck());
                safetyCheckChildInfoDto.setIdHMSafetyCheckLink(childentity.getIdHMSafetyCheckLink());
                safetyCheckChildInfoDto.setIdEvent(childentity.getEvent().getIdEvent());
                safetyCheckChildInfoDto.setDocExist(safetyCheckDao.hmSafetyCheckNarrExists(childentity.getEvent().getIdEvent()));
                safetyCheckChildInfoDto.setIdPerson(childentity.getChild().getIdPerson());
                safetyCheckChildInfoDto.setIdPlacementEvent(childentity.getPlacementEvent().getIdPlcmtEvent());
                safetyCheckChildInfoDto.setIdStage(childentity.getStage().getIdStage());
                safetyCheckChildInfoDto.setIdCase(childentity.getStage().getCapsCase().getIdCase());
                safetyCheckChildInfoDto.setCdStage(childentity.getStage().getCdStage());
                safetyCheckChildInfoDto.setNmStage(childentity.getStage().getNmStage());
                safetyCheckChildInfoDto.setPlacementStartDate(childentity.getPlacementEvent().getDtPlcmtStart());
                safetyCheckChildInfoDto.setSafetyCheckDate(childentity.getDtSafetyCheck());
                safetyCheckChildInfoDto.setChildSafe(childentity.getIndChildSafe());
                safetyCheckChildInfoDto.setAbuseNeglectReportInitiated(childentity.getIndAbuseNeglectReportInitiated());
                safetyCheckChildInfoDto.setCallIdNumber(childentity.getIdCall());
                //to show the row enabled as the checkbox is checked
                safetyCheckChildInfoDto.setDisableRow(false);
                safetyCheckChildInfoDtoList.add(safetyCheckChildInfoDto);
                savedChildrenStageId.add(childentity.getStage().getIdStage());
            });
        }

        if(!ObjectUtils.isEmpty(hmSafetyCheckAttachments)){
            hmSafetyCheckAttachments.forEach(attachmentEntity->{
                SafetyCheckAttachmentDto attachmentDto=new SafetyCheckAttachmentDto();
                attachmentDto.setIdHMSafetyCheck(attachmentEntity.getHmSafetyCheck().getIdHMSafetyCheck());
                attachmentDto.setIdHMSafetyCheckAttachment(attachmentEntity.getIdHMSafetyCheckAttachment());
                attachmentDto.setMimeType(attachmentEntity.getSysMimeType());
                attachmentDto.setFileName(attachmentEntity.getNmFile());
                try {
                    attachmentDto.setAttachmentData(attachmentEntity.getFileDocumentData().getBytes(1,(int)attachmentEntity.getFileDocumentData().length()));
                    attachmentEntity.getFileDocumentData().free();
                } catch (SQLException e) {
                    LOG.error("Exception on getting blob " + e.getMessage());
                }
                safetyCheckAttachmentDtoList.add(attachmentDto);
            });
        }
        safetyCheckDto.setSafetyCheckAttachmentDtoList(safetyCheckAttachmentDtoList);
        safetyCheckDto.setSafetyCheckChildInfoDtoList(safetyCheckChildInfoDtoList);
        safetyCheckDto.setSavedChildrenStageId(savedChildrenStageId);
    }

    /**
     *Method Name:	populateToDo
     *Method Description:Populate the TO DO for safety check alerts
     *@param subStageCaseId
     *@param idToDoAssigned
     *@param idStage
     *@param nmChildFull
     *@param idResource
     *@param nmrsrc
     *@param idEvent
     *@return
     */
    private TodoDto populateToDo(Long subStageCaseId,Long idStage,Long idToDoAssigned,String  nmChildFull,Long idResource,String nmrsrc,Long idEvent) {

        TodoDto todoDto= new TodoDto();
        todoDto.setCdTodoType(ServiceConstants.TODO_ACTIONS_ALERT);
        todoDto.setCdTodoTask(null);
        todoDto.setDtTodoCompleted(new Date());
        todoDto.setDtTodoCreated(new Date());
        todoDto.setDtTodoDue(new Date());
        todoDto.setIdTodoCase(subStageCaseId);
        todoDto.setIdTodoPersCreator(null);
        todoDto.setIdTodoPersAssigned(idToDoAssigned);
        todoDto.setIdTodoStage(idStage);
        todoDto.setIdTodoEvent(idEvent);
        todoDto.setTodoDesc(String.format(ServiceConstants.SAFETY_CHECK_ALERT_SHORT,nmChildFull,nmrsrc,idResource));
        todoDto.setTodoLongDesc(String.format(ServiceConstants.SAFETY_CHECK_ALERT_LONG,nmChildFull,nmrsrc,idResource));
        return todoDto;
    }

    @Override
    public Boolean isDocExist(Long idEvent){
        return safetyCheckDao.hmSafetyCheckNarrExists(idEvent);
    }
}
