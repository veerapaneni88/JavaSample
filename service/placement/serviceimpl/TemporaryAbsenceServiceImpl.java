package us.tx.state.dfps.service.placement.serviceimpl;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.Placement;
import us.tx.state.dfps.common.domain.PlacementTa;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.admin.dto.AddrPhoneDto;
import us.tx.state.dfps.service.admin.service.AddrPhoneRtrvService;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.TemporaryAbsenceInfoReq;
import us.tx.state.dfps.service.common.response.PlacementRes;
import us.tx.state.dfps.service.common.response.TemporaryAbsenceEventsRes;
import us.tx.state.dfps.service.common.response.TemporaryAbsenceInfoRes;
import us.tx.state.dfps.service.common.response.TemporaryAbsenceRsrcRes;
import us.tx.state.dfps.service.common.utils.PersonUtil;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.placement.dao.RunawayMissingChildDao;
import us.tx.state.dfps.service.placement.dao.TemporaryAbsenceDao;
import us.tx.state.dfps.service.placement.dto.ChildRecoveryDetailDto;
import us.tx.state.dfps.service.placement.dto.MissingChildDetailDto;
import us.tx.state.dfps.service.placement.dto.TemporaryAbsenceDto;
import us.tx.state.dfps.service.placement.service.TemporaryAbsenceService;
import us.tx.state.dfps.service.resource.dto.ResourceDto;
import us.tx.state.dfps.service.stageutility.dao.StageUtilityDao;
import us.tx.state.dfps.service.subcare.dao.CapsResourceDao;
import us.tx.state.dfps.service.subcare.dao.PlacementDao;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;
import us.tx.state.dfps.service.workload.dto.PostEventDto;
import us.tx.state.dfps.service.workload.dto.PostEventIPDto;
import us.tx.state.dfps.service.workload.dto.PostEventOPDto;

@Service
@Transactional
public class TemporaryAbsenceServiceImpl implements TemporaryAbsenceService {
    @Autowired
    PersonDao personDao;

    @Autowired
    CapsResourceDao capsResourceDao;

    @Autowired
    TemporaryAbsenceDao temporaryAbsenceDao;

    @Autowired
    AddrPhoneRtrvService addrPhoneRtrvSvc;

    @Autowired
    PersonUtil personUtil;

    @Autowired
    PostEventService postEventService;

    @Autowired
	LookupDao lookupDao;

    @Autowired
	private EventDao eventDao;

    @Autowired
    RunawayMissingChildDao runawayMissingChildDao;

    @Autowired
    StageUtilityDao stageUtilityDao;

    @Autowired
    PlacementDao placementDao;


    private static final Logger log = Logger.getLogger(TemporaryAbsenceServiceImpl.class);

    /**
     * @param commonHelperReq
     * @return
     */
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public TemporaryAbsenceEventsRes getTemporaryAbsenceList(CommonHelperReq commonHelperReq) {
        log.debug("Start method getTemporaryAbsenceList in TemporaryAbsenceServiceImpl");
        TemporaryAbsenceEventsRes temporaryAbsenceRes = new TemporaryAbsenceEventsRes();
        temporaryAbsenceRes.setTemporaryAbsenceEventList(temporaryAbsenceDao.getTemporaryAbsenceList(commonHelperReq.getIdCase(),commonHelperReq.getIdStage()));
        if(!ObjectUtils.isEmpty(commonHelperReq.getIdCase())) {
            //dont need additional calls in api layer
            temporaryAbsenceRes.setIdChild(temporaryAbsenceDao.getPrimaryChildId(commonHelperReq.getIdCase(), commonHelperReq.getIdStage()));
            temporaryAbsenceRes.setAnyActivePlacements(temporaryAbsenceDao.checkActivePlacementsCountForStageId(commonHelperReq.getIdStage()) > 0L);
            temporaryAbsenceRes.setAnyOpenTAs(temporaryAbsenceDao.getActiveTemporaryAbsencesForActivePlacements(commonHelperReq.getIdStage()) > 0L);
            checkNoTAForPlcmtLivArr(temporaryAbsenceRes, commonHelperReq.getIdStage());
        }
        log.debug("end method getTemporaryAbsenceList in TemporaryAbsenceServiceImpl");
        return temporaryAbsenceRes;
    }

    /**
     * @param idStage
     * @param idPlacementTa
     * @return
     */
    @Override
    @Transactional
    public TemporaryAbsenceInfoRes getTARequestDetails(Long idStage, Long idPlacementTa){
        log.debug("Start method getTARequestDetails in TemporaryAbsenceServiceImpl");
        TemporaryAbsenceInfoRes temporaryAbsenceInfoRes = new TemporaryAbsenceInfoRes();
        TemporaryAbsenceDto taDto = new TemporaryAbsenceDto();
        if( idPlacementTa != null && idPlacementTa > 0){//get existing Temporary Absence
            PlacementTa placementTa = temporaryAbsenceDao.getTemporaryAbsenceById(idPlacementTa);
            if(!ObjectUtils.isEmpty(placementTa) && !ObjectUtils.isEmpty(placementTa.getidChldMsngDtl()) && placementTa.getidChldMsngDtl() > 0L) {
                MissingChildDetailDto missingChildDetailDto = runawayMissingChildDao.fetchMissingChildDetail(placementTa.getidChldMsngDtl(), 0L);
                taDto.setIdChldMsngEvent(missingChildDetailDto.getIdEvent());
                taDto.setIdChldMsngDtl(missingChildDetailDto.getIdChldMsngDtl());
                ChildRecoveryDetailDto childRecoveryDetailDto = runawayMissingChildDao.fetchChildRecoveryDetail(placementTa.getidChldMsngDtl(), 0L);
                taDto.setChildRecoveryExist(!ObjectUtils.isEmpty(childRecoveryDetailDto)
                        && !ObjectUtils.isEmpty(childRecoveryDetailDto.getIdChldRecoveryDtl()));
            }
            populateTaDtoFromTaPlacement(placementTa, taDto);
        }else{
            //Set the linked placement event id and description for a new Temporary Absence
            PlacementDto placementDto = temporaryAbsenceDao.getOpenPlacementForStage(idStage);
            if(!ObjectUtils.isEmpty(placementDto)){
                taDto.setIdLinkedPlcmtEvent(placementDto.getIdPlcmtEvent());
                taDto.setLinkedPlacementDesc(placementDto.getTxtEventDescr());
                taDto.setPlcmtType(placementDto.getCdPlcmtType());
                taDto.setLivingArrangement(placementDto.getCdPlcmtLivArr());
                taDto.setDtPlcmtStart(placementDto.getDtPlcmtStart());
            }
        }
        temporaryAbsenceInfoRes.setTemporaryAbsenceDto(taDto);
        return temporaryAbsenceInfoRes;
    }

    @Override
    @Transactional
    public TemporaryAbsenceInfoRes getTemporaryAbsenceByMissingChild(Long idChildMsngDtl) {
        log.debug("Start method getTemporaryAbsenceByMissingChild in TemporaryAbsenceServiceImpl");
        TemporaryAbsenceInfoRes temporaryAbsenceInfoRes = new TemporaryAbsenceInfoRes();
        TemporaryAbsenceDto taDto = new TemporaryAbsenceDto();
        if (idChildMsngDtl != null && idChildMsngDtl > 0) {//get existing Temporary Absence
            PlacementTa placementTa = temporaryAbsenceDao.getPlacementTAByMissingChild(idChildMsngDtl);
            populateTaDtoFromTaPlacement(placementTa, taDto);
            //get placement info
            if(placementTa!=null && placementTa.getEventByIdPlcmtEvent().getIdEvent()>0) {
                Placement placement = placementDao.selectPlacement(placementTa.getEventByIdPlcmtEvent().getIdEvent());
                taDto.setDtPlcmtStart(placement!=null?placement.getDtPlcmtStart():null);
            }
        }
        temporaryAbsenceInfoRes.setTemporaryAbsenceDto(taDto);
        return temporaryAbsenceInfoRes;

    }


    /**
     * @param idResource
     * @return
     */
    @Override
    @Transactional
    public TemporaryAbsenceRsrcRes getResourceDetails(Long idResource){
        log.debug("Start method getResourceDetails in TemporaryAbsenceServiceImpl");
        TemporaryAbsenceRsrcRes resourceRes = new TemporaryAbsenceRsrcRes();
        ResourceDto resourceDto = capsResourceDao.getResourceById(idResource);
        resourceRes.setResourceDto(resourceDto);
        return resourceRes;
    }

    /**
     * @param taInfoReq
     * @return
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public TemporaryAbsenceInfoRes saveTaDetailInfo(TemporaryAbsenceInfoReq taInfoReq){
        TemporaryAbsenceInfoRes taInfoRes = new TemporaryAbsenceInfoRes();
        TemporaryAbsenceDto taDto = taInfoReq.getTaDto();
        Long idPlacementTa = taDto.getIdPlacementTa();
        String cReqFun = ServiceConstants.EMPTY_STRING;
        if(ObjectUtils.isEmpty(idPlacementTa)){
           //Add new Temporary Absence
            if(!ObjectUtils.isEmpty(taInfoReq)){
                taInfoReq.getTaDto().setIdCreatedPerson(Long.parseLong(taInfoReq.getUserId()));
            }
            cReqFun = ServiceConstants.REQ_FUNC_CD_ADD;
            Long idEvent = createEvent( taInfoReq);
            if(idEvent!=null)taInfoReq.getTaDto().setIdEvent(idEvent);
        }else{
            // Update existing Temporary Absence
            if(!ObjectUtils.isEmpty(taInfoReq)){
                taInfoReq.getTaDto().setIdLastUpdatePerson(Long.parseLong(taInfoReq.getUserId()));
            }
            cReqFun = ServiceConstants.REQ_FUNC_CD_UPDATE;
            //did the type of TA change
            if(!ObjectUtils.isEmpty(taDto) && isTATypeUpdated(taDto)){
            	eventDao.updateEventDescForTA(taDto.getIdEvent(), lookupDao.simpleDecodeSafe(ServiceConstants.CCTATYPE,taDto.getTemporaryAbsenceType()));
            }
        }
        taDto =temporaryAbsenceDao.saveOrUpdateTaInfo(taInfoReq.getTaDto(),cReqFun);
        if (!ObjectUtils.isEmpty(taDto) && !ObjectUtils.isEmpty(taDto.getErrorDto())) {
            taInfoRes.setErrorDto(taDto.getErrorDto());
        }
        taInfoRes.setTemporaryAbsenceDto(taDto);
        return taInfoRes;
    }


	private boolean isTATypeUpdated(TemporaryAbsenceDto taDto) {
		PlacementTa placementTa = temporaryAbsenceDao.getTemporaryAbsenceById(taDto.getIdPlacementTa());
		if(!ObjectUtils.isEmpty(placementTa) && !placementTa.getCdType().equals(taDto.getTemporaryAbsenceType())){
			return true;
		}
		return false;
	}

	/**
	 * Method Name: createEvent Method Description: This method is used to
	 * create an event.
	 *
	 * @param TemporaryAbsenceInfoReq taInfoReq
	 * @return
	 */
	private Long createEvent(TemporaryAbsenceInfoReq taInfoReq) {
		if(taInfoReq!=null && taInfoReq.getTaDto()!=null){
			Long idPerson = 0L;
			if(taInfoReq.getIdStage()>0){
				idPerson = stageUtilityDao.findPrimaryChildForStage(taInfoReq.getIdStage());
			}
			PostEventIPDto postEventIPDto = new PostEventIPDto();
	        Date date = new Date(System.currentTimeMillis());
	        ServiceReqHeaderDto archInputDto = new ServiceReqHeaderDto();
	        postEventIPDto.setEventDescr(lookupDao.simpleDecodeSafe(ServiceConstants.CCTATYPE,taInfoReq.getTaDto().getTemporaryAbsenceType()));
	        postEventIPDto.setCdTask(ServiceConstants.TEMP_ABSENCE_TASK);
	        postEventIPDto.setIdPerson(Long.parseLong(taInfoReq.getUserId()));
	        postEventIPDto.setIdStage(taInfoReq.getIdStage());
	        postEventIPDto.setIdCase(taInfoReq.getIdCase());
	        postEventIPDto.setDtEventOccurred(date);
	        postEventIPDto.setUserId(taInfoReq.getUserId());
	        archInputDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
	        postEventIPDto.setDtEventOccurred(date);
	        postEventIPDto.setCdEventStatus(ServiceConstants.CEVTSTAT_COMP);
	        postEventIPDto.setTsLastUpdate(date);
	        postEventIPDto.setCdEventType(ServiceConstants.CEVNTTYP_TAP);

	        PostEventDto postEventDto = new PostEventDto();
	        List<PostEventDto> postEventDtoList = new ArrayList<>();
	        postEventDto.setIdPerson(idPerson);
	        postEventDto.setCdScrDataAction(ServiceConstants.REQ_FUNC_CD_ADD);
	        postEventDtoList.add(postEventDto);
	        postEventIPDto.setPostEventDto(postEventDtoList);
	        PostEventOPDto postEventOPDto = postEventService.checkPostEventStatus(postEventIPDto, archInputDto);
	        return postEventOPDto.getIdEvent();
		}
		return null;
	}

    @Override
    public void deleteTAInfo(Long placementTaId,Long loginUserId) {
        temporaryAbsenceDao.deleteTaInfo(placementTaId,loginUserId);
    }

     /**
     * @param placementTa
     * @param taDto
     */
    private void populateTaDtoFromTaPlacement(PlacementTa placementTa, TemporaryAbsenceDto taDto) {
        if(!ObjectUtils.isEmpty(placementTa)){
            taDto.setTemporaryAbsenceType(placementTa.getCdType());
            taDto.setDtTemporaryAbsenceStart(placementTa.getDtStart());
            taDto.setDtTemporaryAbsenceEnd(placementTa.getDtEnd());
            taDto.setDt2279BReviewed(placementTa.getDt2279B());
            taDto.setDtAttachAReviewed(placementTa.getDtAttachA());
            taDto.setIdPlacementTa(placementTa.getIdPlacementTa());
            taDto.setIndDt2279bNA(placementTa.getInd2279BNa());
            taDto.setIndDtAttachANA(placementTa.getIndAttachANa());
            taDto.setPointOfContact(placementTa.getNmPointOfContact());
            taDto.setTxtComments(placementTa.getTxtComments());
            if(ObjectUtils.isEmpty(placementTa.getIdResource()) && ObjectUtils.isEmpty(placementTa.getIdRespitePerson())){
                taDto.setNmTemporaryAbsence(placementTa.getNmResource());
                taDto.setTaAddrLn1(placementTa.getAddrStLn1());
                taDto.setTaAddrLn2(placementTa.getAddrStLn2());
                taDto.setTaAddrCity(placementTa.getAddrCity());
                taDto.setTaAddrSt(placementTa.getCdAddrState());
                taDto.setTaAddrCnty(placementTa.getCdAddrCounty());
                if(!ObjectUtils.isEmpty(placementTa.getAddrAddrZip())){
                    if(placementTa.getAddrAddrZip().length() >5){
                        taDto.setTaAddrZip(placementTa.getAddrAddrZip().substring(0,5));
                        taDto.setTaAddrZipExtension(placementTa.getAddrAddrZip().substring(5));
                    }else{
                        taDto.setTaAddrZip(placementTa.getAddrAddrZip());
                    }
                }
                taDto.setNbrPhone(placementTa.getNbrPhn());
                taDto.setNbrPhoneExt(placementTa.getNbrPhoneExt());
            }
            if(!ObjectUtils.isEmpty(placementTa.getEventByIdPlcmtEvent())){
                Event event = placementTa.getEventByIdPlcmtEvent();
                taDto.setIdLinkedPlcmtEvent(event.getIdEvent());
                taDto.setLinkedPlacementDesc(event.getTxtEventDescr());
            }
            if(!ObjectUtils.isEmpty(placementTa.getIdResource())){
                populateResourceDetails(placementTa.getIdResource(),taDto);
            }
            if(!ObjectUtils.isEmpty(placementTa.getIdRespitePerson())){
                populatePersonDetails(placementTa.getIdRespitePerson(), taDto);
            }

            taDto.setCdGcdRtrn(placementTa.getCdGcdRtrn());
            taDto.setCdAddrRtrn(placementTa.getCdAddrRtrn());
            taDto.setTxtMailbltyScore(placementTa.getTxtMailbltyScore());
            taDto.setNbrGcdLong(placementTa.getNbrGcdLong());
            taDto.setNbrGcdLat(placementTa.getNbrGcdLat());
            taDto.setNmCnty(placementTa.getNmCnty());
            taDto.setNmCntry(placementTa.getNmCntry());
            taDto.setIndValdtd(placementTa.getIndValdtd());
            taDto.setDtValdtd(placementTa.getDtValdtd());
            taDto.setIdEvent(placementTa.getIdEvent());
            taDto.setDtCreated(placementTa.getDtCreated());
            taDto.setDtLastUpdate(placementTa.getDtLastUpdate());
            taDto.setIdCreatedPerson(placementTa.getIdCreatedPerson());
            taDto.setIdLastUpdatePerson(placementTa.getIdLastUpdatePerson());
        }
    }


    /**
     * @param idRespitePerson
     * @param taDto
     */
    private void populatePersonDetails(Long idRespitePerson, TemporaryAbsenceDto taDto) {
        AddrPhoneDto addressPhoneDto = addrPhoneRtrvSvc.callAddrPhoneRtrvService(idRespitePerson);
        String personFullName = personUtil.getPersonFullName(idRespitePerson);
        taDto.setIdPersonRespiteVisit(idRespitePerson);
        taDto.setNmTemporaryAbsence(personFullName);
        if(!ObjectUtils.isEmpty(addressPhoneDto)){
            taDto.setTaAddrLn1(addressPhoneDto.getAddrPersAddrStLn1());
            taDto.setTaAddrLn2(addressPhoneDto.getAddrPersAddrStLn2());
            taDto.setTaAddrCity(addressPhoneDto.getAddrCity());
            taDto.setTaAddrSt(addressPhoneDto.getCdAddrState());
            taDto.setTaAddrCnty(addressPhoneDto.getCdAddrCounty());
            taDto.setNbrPhone(addressPhoneDto.getNbrPhone());
            taDto.setNbrPhoneExt(addressPhoneDto.getNbrPhoneExtension());
            if(!ObjectUtils.isEmpty(addressPhoneDto.getAddrZip())){
                if(addressPhoneDto.getAddrZip().length() >5){
                    taDto.setTaAddrZip(addressPhoneDto.getAddrZip().substring(0,5));
                    taDto.setTaAddrZipExtension(addressPhoneDto.getAddrZip().substring(6));
                }else{
                    taDto.setTaAddrZip(addressPhoneDto.getAddrZip());
                }
            }
        }
    }


    /**
     * @param idResource
     * @param taDto
     */
    private void populateResourceDetails(Long idResource, TemporaryAbsenceDto taDto) {
        TemporaryAbsenceRsrcRes rsrcRes = getResourceDetails(idResource);
        taDto.setIdTaRsrcAgency(idResource);
        if(!ObjectUtils.isEmpty(rsrcRes)){
            ResourceDto rsrcDto = rsrcRes.getResourceDto();
            if(!ObjectUtils.isEmpty(rsrcDto)){
                taDto.setNmTemporaryAbsence(rsrcDto.getNmResource());
                taDto.setTaAddrLn1(rsrcDto.getAddrRsrcStLn1());
                taDto.setTaAddrLn2(rsrcDto.getAddrRsrcStLn2());
                taDto.setTaAddrCity(rsrcDto.getAddrRsrcCity());
                taDto.setTaAddrSt(rsrcDto.getCdRsrcState());
                taDto.setTaAddrCnty(rsrcDto.getCdRsrcCnty());
                taDto.setNbrPhone(rsrcDto.getNbrRsrcPhn());
                taDto.setNbrPhoneExt(rsrcDto.getNbrRsrcPhoneExt());
                if(!ObjectUtils.isEmpty(rsrcDto.getAddrRsrcZip())){
                    if(rsrcDto.getAddrRsrcZip().length() >5){
                        taDto.setTaAddrZip(rsrcDto.getAddrRsrcZip().substring(0,5));
                        taDto.setTaAddrZipExtension(rsrcDto.getAddrRsrcZip().substring(6));
                    }else{
                        taDto.setTaAddrZip(rsrcDto.getAddrRsrcZip());
                    }
                }
            }
        }
    }

    /**
     * @param placementEventId
     * @return
     */
    @Override
    public PlacementRes getActiveTAsCountForPlacement(Long placementEventId) {
        PlacementRes placementRes = new PlacementRes();
        placementRes.setPlacementDto(new PlacementDto());
        placementRes.getPlacementDto().setActiveTAsCount(temporaryAbsenceDao.getActiveTemporaryAbsencesForActivePlacement(placementEventId));
        return placementRes;
    }

    @Override
    public TemporaryAbsenceInfoRes getOpenTAForActivePlacement(Long placementEventId) {
        TemporaryAbsenceInfoRes temporaryAbsenceInfoRes = new TemporaryAbsenceInfoRes();
        TemporaryAbsenceDto taDto = new TemporaryAbsenceDto();
        taDto.setIdPlacementTa(temporaryAbsenceDao.getActiveTAForActivePlacement(placementEventId));
        temporaryAbsenceInfoRes.setTemporaryAbsenceDto(taDto);
        return temporaryAbsenceInfoRes;
    }


    /**
     * @param taInfoReq
     * @return
     */
    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public boolean isTAStartOrEndDtBeforePlcmtStart(TemporaryAbsenceInfoReq taInfoReq) {
        log.debug("Entering method isTAStartOrEndDtBeforePlcmtStart in TemporaryAbsenceServiceImpl");
        boolean result = false;
        if(!ObjectUtils.isEmpty(taInfoReq)){
            Long idPlcmtEvent = (ObjectUtils.isEmpty(taInfoReq.getTaDto()))?0L:taInfoReq.getTaDto().getIdLinkedPlcmtEvent();
            Date taStartorEndDate = (ObjectUtils.isEmpty(taInfoReq.getTaDto()))?null:taInfoReq.getTaDto().getTaStartOrEndDate();
            result = temporaryAbsenceDao.isTAStartOrEndDtBeforePlcmtStart(idPlcmtEvent, taStartorEndDate);
        }


        log.debug("Exiting method isTAStartOrEndDtBeforePlcmtStart in TemporaryAbsenceServiceImpl");
        return result;

    }

    /**
     * @param taInfoReq
     * @return
     */
    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public boolean isTAStartOrEndDtBetweenRange(TemporaryAbsenceInfoReq taInfoReq) {
        log.debug("Entering method isTAStartOrEndDtBetweenRange in TemporaryAbsenceServiceImpl");
        boolean result = false;
        if(!ObjectUtils.isEmpty(taInfoReq)){
            Long idStage = taInfoReq.getIdStage();
            Date dtStart = (ObjectUtils.isEmpty(taInfoReq.getTaDto().getDtTemporaryAbsenceStart()))?null:taInfoReq.getTaDto().getDtTemporaryAbsenceStart();
            Date dtEnd = (ObjectUtils.isEmpty(taInfoReq.getTaDto().getDtTemporaryAbsenceEnd()))?null:taInfoReq.getTaDto().getDtTemporaryAbsenceEnd();
            Long idPlacementTa =(ObjectUtils.isEmpty(taInfoReq.getTaDto().getIdPlacementTa()))?0L:taInfoReq.getTaDto().getIdPlacementTa();
            String temporaryAbsenceType = taInfoReq.getTaDto().getTemporaryAbsenceType();
            try {
                result = temporaryAbsenceDao.isTAStartOrEndDtBetweenRange(idStage, dtStart, dtEnd, idPlacementTa, temporaryAbsenceType);
            }  catch (ParseException e) {
                new ServiceLayerException(e.getMessage());
            }
        }

        TemporaryAbsenceInfoRes res = new TemporaryAbsenceInfoRes();
        res.setTADtBetweenRange(result);
        log.debug("Exiting method isTAStartOrEndDtBetweenRange in TemporaryAbsenceServiceImpl");
        return result;

    }

    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public boolean isTAEndDtAfterPlcmtEnd(TemporaryAbsenceInfoReq taInfoReq) {
        log.debug("Entering method isTAEndDtAfterPlcmtEnd in TemporaryAbsenceServiceImpl");
        boolean result = false;
        if(!ObjectUtils.isEmpty(taInfoReq)){
            Long idPlcmtEvent = (ObjectUtils.isEmpty(taInfoReq.getTaDto()))?0L:taInfoReq.getTaDto().getIdLinkedPlcmtEvent();
            Date taEndDate = (ObjectUtils.isEmpty(taInfoReq.getTaDto()))?null:taInfoReq.getTaDto().getDtTemporaryAbsenceEnd();
            result = temporaryAbsenceDao.isTAEndDtAfterPlcmtEnd(idPlcmtEvent, taEndDate);
        }
        log.debug("Exiting method isTAEndDtAfterPlcmtEnd in TemporaryAbsenceServiceImpl");
        return result;

    }

    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public boolean isPlacementEnded(TemporaryAbsenceInfoReq taInfoReq) {
        log.debug("Entering method isPlacementEnded in TemporaryAbsenceServiceImpl");
        boolean result = false;
        if(!ObjectUtils.isEmpty(taInfoReq)){
            Long idPlcmtEvent = (ObjectUtils.isEmpty(taInfoReq.getTaDto()))?0L:taInfoReq.getTaDto().getIdLinkedPlcmtEvent();
            result = temporaryAbsenceDao.isPlacementEnded(idPlcmtEvent);
        }
        log.debug("Exiting method isPlacementEnded in TemporaryAbsenceServiceImpl");
        return result;

    }

    @Override
    @Transactional
    public TemporaryAbsenceInfoRes getTemporaryAbsenceById(Long idPlacementTa){
        TemporaryAbsenceInfoRes temporaryAbsenceInfoRes = new TemporaryAbsenceInfoRes();
        TemporaryAbsenceDto taDto = new TemporaryAbsenceDto();
        PlacementTa placementTa = temporaryAbsenceDao.getTemporaryAbsenceById(idPlacementTa);
        taDto.setDtTemporaryAbsenceStart(placementTa.getDtStart());
        taDto.setDtTemporaryAbsenceEnd(placementTa.getDtEnd());
        taDto.setIdPlacementTa(placementTa.getIdPlacementTa());
        temporaryAbsenceInfoRes.setTemporaryAbsenceDto(taDto);
        return temporaryAbsenceInfoRes;
    }

    private void checkNoTAForPlcmtLivArr(TemporaryAbsenceEventsRes temporaryAbsenceRes,Long stageId){
        boolean isNonPaidUnauthPlcmt = false;
        PlacementDto plcmtDto = temporaryAbsenceDao.getOpenPlacementForStage(stageId);
        if(!ObjectUtils.isEmpty(plcmtDto)){
            String plcmtLivArr = plcmtDto.getCdPlcmtLivArr();
            if(!ObjectUtils.isEmpty(plcmtLivArr)){
                if (ServiceConstants.CPLLAFRM_UK.equals(plcmtLivArr) || ServiceConstants.CPLLAFRM_UL.equals(plcmtLivArr)
                        || ServiceConstants.CPLLAFRM_UR.equals(plcmtLivArr) || ServiceConstants.CPLLAFRM_01.equals(plcmtLivArr)
                        || ServiceConstants.CPLLAFRM_24.equals(plcmtLivArr) || ServiceConstants.CPLLAFRM_28.equals(plcmtLivArr)
                        || ServiceConstants.CPLLAFRM_29.equals(plcmtLivArr) || ServiceConstants.CPLLAFRM_47.equals(plcmtLivArr)
                        || ServiceConstants.CPLLAFRM_76.equals(plcmtLivArr) ){
                     isNonPaidUnauthPlcmt = true;
                }
            }
            temporaryAbsenceRes.setNoTaForPlacement(isNonPaidUnauthPlcmt);
            temporaryAbsenceRes.setCdLivingArrangement(plcmtLivArr);
        }

    }

}