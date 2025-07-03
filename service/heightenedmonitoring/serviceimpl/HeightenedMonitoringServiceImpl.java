package us.tx.state.dfps.service.heightenedmonitoring.serviceimpl;

import org.apache.commons.lang.math.NumberUtils;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import us.tx.state.dfps.common.domain.CapsResource;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.HMReqChildLink;
import us.tx.state.dfps.common.domain.HMRequest;
import us.tx.state.dfps.common.domain.HMStatusNotifEmail;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.common.dto.EmailDetailDto;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.admin.dao.ApprovalEventDao;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonInDto;
import us.tx.state.dfps.service.admin.dto.ApprovalCommonOutDto;
import us.tx.state.dfps.service.admin.dto.ApprovalEventLinkDto;
import us.tx.state.dfps.service.admin.dto.ApprovalEventOutputDto;
import us.tx.state.dfps.service.admin.dto.EmployeeDetailDto;
import us.tx.state.dfps.service.admin.service.ApprovalCommonService;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.alert.service.AlertService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.FacilityDetailSaveReq;
import us.tx.state.dfps.service.common.request.HeightenedMonitoringInfoReq;
import us.tx.state.dfps.service.common.request.HmStatusReq;
import us.tx.state.dfps.service.common.response.*;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.heightenedmonitoring.dao.HeightenedMonitoringDao;
import us.tx.state.dfps.service.heightenedmonitoring.service.HeightenedMonitoringService;
import us.tx.state.dfps.service.hmm.dto.HMAddApproverDto;
import us.tx.state.dfps.service.hmm.dto.HMApproverEmailDto;
import us.tx.state.dfps.service.hmm.dto.HMApproverListDto;
import us.tx.state.dfps.service.hmm.dto.HeightenedMonitoringEmailDto;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.resource.detail.dto.ResourceDetailInDto;
import us.tx.state.dfps.service.workload.dto.AssignedDto;
import us.tx.state.dfps.service.resource.dto.ResourceDto;
import us.tx.state.dfps.service.subcare.dao.CapsResourceDao;
import us.tx.state.dfps.service.subcare.dto.CapsResourceLinkDto;
import us.tx.state.dfps.service.workload.dto.PostEventDto;
import us.tx.state.dfps.service.workload.dto.PostEventIPDto;
import us.tx.state.dfps.service.workload.dto.PostEventOPDto;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.web.placement.bean.HeightenedMonitoringInfoBean;
import us.tx.state.dfps.web.placement.bean.HeightenedMonitoringSiblingBean;
import us.tx.state.dfps.web.placement.bean.HeightenedMonitoringUtilizationHistoryBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class HeightenedMonitoringServiceImpl implements HeightenedMonitoringService {

    private static final Logger log = Logger.getLogger(HeightenedMonitoringServiceImpl.class);

    private static final String CPA = "02";
    private static final String HM_SUBMIT = "SAVESUBMIT";

    @Autowired
    HeightenedMonitoringDao heightenedMonitoringDao;

    @Autowired
    AlertService alertService;

    @Autowired
    EventDao eventDao;

    @Autowired
    StageDao stageDao;

    @Autowired
    PostEventService postEventService;

    @Autowired
    PersonDao personDao;

    @Autowired
    CapsResourceDao capsResourceDao;

    @Autowired
    EmployeeDao employeeDao;

    @Autowired
    ApprovalCommonService approvalCommonService;

    @Autowired
    ApprovalEventDao approvalEventDao;

    /**
     * PPM 60692-artf178535 - Changes for Facility Detail Notifications
     * Method Description: This method fetches all the resources info for the Resource whose HM status has been set
     * to active/inactive
     *
     * @param active
     * @return
     */
    @Override
    @Transactional
    public List<ResourceDetailInDto> fetchResourcesInfo(Boolean active) {
            List<ResourceDetailInDto> resourceDetailInDtos = heightenedMonitoringDao.fetchResourcesInfo(active);
            return resourceDetailInDtos;
    }

    /**
     * PPM 77728 - artf242940 - Heightened Monitoring
     * This method fetches all the resources info for the Resource whose HM status has been set
     *      * to active/inactive
     * @param active
     * @param resourceDetailInDto
     * @param heightenedMonitoringEmailRes
     * @return
     */
    @Override
    @Transactional
    public HeightenedMonitoringEmailRes fetchResourcesEmailInfo(Boolean active, ResourceDetailInDto resourceDetailInDto,HeightenedMonitoringEmailRes heightenedMonitoringEmailRes) {
        // Fetches the Resource Info with Staff and Supervisor emails
        try {
            if (null!=resourceDetailInDto && heightenedMonitoringDao.lockAndUpdateHmResourceStatus(resourceDetailInDto.getIdHmStatus())) {

                heightenedMonitoringEmailRes.setHmResourceEmailDtos(heightenedMonitoringDao
                            .fetchPlacementStaffForResources(resourceDetailInDto.getIdResource()));

                if (!CollectionUtils.isEmpty(heightenedMonitoringEmailRes.getHmResourceEmailDtos())) {
                    Map<Long, List<HeightenedMonitoringEmailDto>> hmResourceEmailMap = heightenedMonitoringEmailRes.getHmResourceEmailDtos().stream()
                                .collect(Collectors.groupingBy(HeightenedMonitoringEmailDto::getIdResource));
                        // Generate Alerts for status changed Resources
                    hmResourceEmailMap.keySet().forEach(id -> {
                        Map<Long, List<HeightenedMonitoringEmailDto>> hmResourceStageMap = hmResourceEmailMap.get(id).stream()
                                    .collect(Collectors.groupingBy(HeightenedMonitoringEmailDto::getIdStage));
                        hmResourceStageMap.keySet().forEach(idStage -> {
                            String nmStage = hmResourceStageMap.get(idStage).get(0).getNmStage();
                            Long idCase = hmResourceStageMap.get(idStage).get(0).getIdCase();
                            // Fetch the Staff person id and Supervisor person id from list of objects
                             List<Long> idPersonList = hmResourceStageMap.get(idStage).stream()
                                        .flatMap(dto -> Stream.of(dto.getIdPersonStaff(), dto.getIdPersonSupervisor()))
                                        .collect(Collectors.toList());
                             String longDesc = null;
                             String shortDesc = null;
                             SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy");
                             if (active) {
                                    // Facility status is active
                                    shortDesc = String.format(ServiceConstants.HM_ALERT_ACTIVE_FACILITY_SHORT, id);
                                    longDesc = String.format(ServiceConstants.HM_ALERT_ACTIVE_FACILITY_LONG, nmStage, id,
                                            simpleDateFormat.format(new Date()));
                             } else {
                                    // Facility status is changed to inactive
                                    shortDesc = String.format(ServiceConstants.HM_ALERT_INACTIVE_FACILITY_SHORT, id);
                                    longDesc = String.format(ServiceConstants.HM_ALERT_INACTIVE_FACILITY_LONG, nmStage, id,
                                            simpleDateFormat.format(new Date()));
                             }
                             for (Long idPerson : idPersonList) {
                                 alertService.createFbssAlert(idStage, idPerson, null, idCase, longDesc, shortDesc);
                                }
                            });
                        });

                       if( !hmResourceEmailMap.containsKey(resourceDetailInDto.getIdResource())) {
                           HeightenedMonitoringEmailDto heightenedMonitoringEmailDto = new HeightenedMonitoringEmailDto();
                           heightenedMonitoringEmailDto.setIdResource(resourceDetailInDto.getIdResource());
                           heightenedMonitoringEmailDto.setNmResource(resourceDetailInDto.getNmResource());
                           heightenedMonitoringEmailRes.getHmResourceEmailDtos().add(heightenedMonitoringEmailDto);
                       }
                    } else {
                        List<HeightenedMonitoringEmailDto> heightenedMonitoringEmailDtos = new ArrayList<>();
                        HeightenedMonitoringEmailDto heightenedMonitoringEmailDto = new HeightenedMonitoringEmailDto();
                        heightenedMonitoringEmailDto.setIdResource(resourceDetailInDto.getIdResource());
                        heightenedMonitoringEmailDto.setNmResource(resourceDetailInDto.getNmResource());
                        heightenedMonitoringEmailDtos.add(heightenedMonitoringEmailDto);
                        heightenedMonitoringEmailRes.setHmResourceEmailDtos(heightenedMonitoringEmailDtos);
                    }

                    // Updates the Resource inactive HM status so that all the existing/pending HM requests are invalidated
                    if (!active) {
                        heightenedMonitoringDao.updateInactiveResources(resourceDetailInDto.getIdResource());
                    }

                heightenedMonitoringEmailRes.getHmResourceEmailDtos().forEach(dto->{
                    if (active) {
                        dto.setEffDate(resourceDetailInDto.getDtRsrcEmailStart());
                    }else{
                        dto.setEffDate(resourceDetailInDto.getDtRsrcEmailEnd());
                    }
                });
            }
        }catch(Exception e){
            log.warn("Error while reading while processing " +resourceDetailInDto.getIdResource() + " record "  + e.getMessage());
        }
        return heightenedMonitoringEmailRes;
    }

    /**
     * PPM 77728 - artf242940 - Heightened Monitoring
     * @param idHmStatus
     * @param status
     */
    @Override
    public void saveEmailStatus(Long idHmStatus, String status) {
        heightenedMonitoringDao.updateHmResourceStatus(idHmStatus,status);
    }


    // PPM 60692-artf178536- Heightened Monitoring Request Event List
    @Override
    @Transactional
    public HeightenedMonitoringEventsRes getHmmRequestEventList(Long idStage){
        HeightenedMonitoringEventsRes heightenedMonitoringEventsRes = new HeightenedMonitoringEventsRes();
        heightenedMonitoringEventsRes.setHeightenedMonitoringEventList(heightenedMonitoringDao.getHmmRequestEventList(idStage));
        return heightenedMonitoringEventsRes;
    }

    /**
     * PPM 60692-artf178535 - Changes for Facility Detail Notifications
     * Method Description: This method fetches all the regional directors email
     *
     * @return
     */
    @Override
    @Transactional
    public CommonStringRes fetchAllRegionalDirectorEmails() {

        List<String> emailList = null;

        List<HMStatusNotifEmail> hmStatusNotifEmailList = heightenedMonitoringDao.fetchRegionalDirectorsInfo();

        if (!CollectionUtils.isEmpty(hmStatusNotifEmailList)) {
            emailList = hmStatusNotifEmailList.stream().map(HMStatusNotifEmail::getTxtEmailAddress)
                    .collect(Collectors.toList());
        }

        CommonStringRes commonStringRes = new CommonStringRes();
        commonStringRes.setCommonResList(emailList);

        return commonStringRes;
    }

    /**
     * PPM 60692-artf179568- Heightened Monitoring Case List
     * Method Description: This method retrieves all the Heightened Monitoring Request for the id case
     *
     * @param idCase
     * @return HeightenedMonitoringEventsRes
     */
    @Override
    @Transactional
    public HeightenedMonitoringEventsRes getHmmCaseList(Long idCase) {
        HeightenedMonitoringEventsRes heightenedMonitoringEventsRes = new HeightenedMonitoringEventsRes();
        heightenedMonitoringEventsRes.setHeightenedMonitoringEventList(heightenedMonitoringDao.getHmmCaseList(idCase));
        return heightenedMonitoringEventsRes;
    }

    //PPM 60692-artf178534 - HM status
    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public HmStatusRes getHmStatusDetails(HmStatusReq hmStatusReq) {
        HmStatusRes result = new HmStatusRes();
        result.setHmStatusDtoList(heightenedMonitoringDao.getHmStatusDetails(hmStatusReq.getIdResource()));
        return result;
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public HmStatusRes getFacilTypAndParentRsrc(HmStatusReq hmStatusReq) {
        HmStatusRes result = new HmStatusRes();
        result.setHmDto(heightenedMonitoringDao.getFacilTypAndParentRsrc(hmStatusReq.getIdResource()));
        return result;
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public List<Long> getSelectedOpenStages(Long idCase, String cdStage){
        List<Long> openStages = stageDao.getSelectedOpenStages(idCase,cdStage);
        return openStages;
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public String getChildLegalRegion(Long idStage){
        String legalRegion = heightenedMonitoringDao.getHMMChildLegalRegion(idStage);
        return legalRegion;
    }


    /**
     * PPM 60692-artf179569 - Add or Edit/View HM Request details from HM Request List page.
     * @param idStage
     * @param idEvent
     * @return HeightenedMonitoringInfoRes
     */
    @Override
    @Transactional
    public HeightenedMonitoringInfoRes getHmRequestDetails(Long idStage, Long idEvent){
        HeightenedMonitoringInfoRes heightenedMonitoringInfoRes = new HeightenedMonitoringInfoRes();
        HeightenedMonitoringInfoBean heightenedMonitoringInfoBean = new HeightenedMonitoringInfoBean();
        heightenedMonitoringInfoBean.setIdStage(idStage);

        StageDto stageDto = stageDao.getStageById(idStage);
        // All Sibling list
        List<HeightenedMonitoringSiblingBean> siblingBeanList = heightenedMonitoringDao.getHmmRequestSiblingList(idStage, stageDto.getIdCase());
        HMRequest hmRequest = null;
        // Edit or View HM Request Info based on event ID values exists
        if( idEvent != null && idEvent > 0){
            hmRequest = heightenedMonitoringDao.getHmRequestByEventId(idEvent);
            if(null == hmRequest){
                ApprovalEventLinkDto approvalEventLinkDto = new ApprovalEventLinkDto();
                approvalEventLinkDto.setUlIdApproval(idEvent);

                List<ApprovalEventOutputDto> approvalEventOutputDtos = approvalEventDao.getApprovalEventLink(approvalEventLinkDto);
                if(!CollectionUtils.isEmpty(approvalEventOutputDtos)) {
                    // Fetch Host event id using approval event id
                    Long hostIdEvent = approvalEventOutputDtos.get(0).getHostIdEvent();
                    hmRequest = heightenedMonitoringDao.getHmRequestByEventId(hostIdEvent);
                    idEvent = hostIdEvent;
                }
            }
            if(null != hmRequest) {
                String eventStatus = hmRequest.getIdEvent().getCdEventStatus();
                Set<Long> idStageSiblings = new HashSet<>();
                Set<HMReqChildLink> hmReqChildLinkSet = hmRequest.getHmReqChildLinks();
                if (!CollectionUtils.isEmpty(siblingBeanList)) {
                    siblingBeanList.forEach(heightenedMonitoringSiblingBean -> {
                        Optional<HMReqChildLink> childLinkOptional = hmReqChildLinkSet.stream().filter(hmReqChildLink -> hmReqChildLink.getStage().getIdStage().equals(heightenedMonitoringSiblingBean.getIdStage())).findFirst();
                        if (childLinkOptional.isPresent()) {
                            HMReqChildLink hmReqChildLink = childLinkOptional.get();
                            //artf202240 -Warranty defect-18199-Get from  destination column only in PEND or APRV status else the latest from source is shown
                            if("PEND".equals(eventStatus)  || "APRV".equals(eventStatus)){
                                heightenedMonitoringSiblingBean.setIndSexualVictmsnHist(getIndDescription(hmReqChildLink.getIndSexualVictmsnHist()));
                                heightenedMonitoringSiblingBean.setIndSexualAggrsnHist(getIndDescription(hmReqChildLink.getIndSexualAggrsnHist()));
                            }
                            heightenedMonitoringSiblingBean.setIdPerson(hmReqChildLink.getPerson().getIdPerson());
                            heightenedMonitoringSiblingBean.setSiblingSelected(true);
                            heightenedMonitoringSiblingBean.setIdHmReqChildLink(hmReqChildLink.getHmReqChildLinkId());
                            idStageSiblings.add(hmReqChildLink.getStage().getIdStage());
                        }
                    });
                }
                heightenedMonitoringInfoBean.setHeightenedMonitoringSiblingList(siblingBeanList);
                heightenedMonitoringInfoBean.setIdStageSiblings(idStageSiblings);
                heightenedMonitoringInfoBean.setHmRequestId(hmRequest.getHmRequestId());
                heightenedMonitoringInfoBean.setDtCreated(hmRequest.getDtCreated());
                heightenedMonitoringInfoBean.setDtLastUpdated(hmRequest.getDtLastUpdated());
                heightenedMonitoringInfoBean.setIdCreatedPerson(hmRequest.getIdCreatedPerson());
                heightenedMonitoringInfoBean.setDtInactivated(hmRequest.getDtInactivated());
                heightenedMonitoringInfoBean.setDtExpire(hmRequest.getDtExpire());
                heightenedMonitoringInfoBean.setIdLastUpdatedPerson(hmRequest.getIdLastUpdatedPerson());



                if (ServiceConstants.APRV_STATUS_APPROVED.equalsIgnoreCase(eventStatus)) {
                    heightenedMonitoringInfoBean.setHeightenedMonitoringUtilizationHistoryList(getHmmUtilizationHistory(hmRequest));
                }
                populateHmReqInfoBean(hmRequest, heightenedMonitoringInfoBean);
            }
            // Add new HM request details
        }else {
            heightenedMonitoringInfoBean.setIndChildSexVictHistory(getIndDescription(heightenedMonitoringDao.getHMMChildSexualVictimizationHistoryFlag(idStage)));
            heightenedMonitoringInfoBean.setIndChildSexAggrHistory(heightenedMonitoringDao.indHMMChildSexualAggressionHistoryExists(idStage) ? ServiceConstants.HMM_YES : ServiceConstants.HMM_NO);
            heightenedMonitoringInfoBean.setChildLegalRegion(heightenedMonitoringDao.getHMMChildLegalRegion(idStage));
            heightenedMonitoringInfoBean.setHeightenedMonitoringSiblingList(siblingBeanList);
        }
        heightenedMonitoringInfoRes.setHeightenedMonitoringInfoBean(heightenedMonitoringInfoBean);
        return heightenedMonitoringInfoRes;
    }

    private String getIndDescription(String indicator) {
        if(null == indicator) {
            return "";
        }
        return ServiceConstants.Y.equalsIgnoreCase(indicator) ? ServiceConstants.HMM_YES : ServiceConstants.HMM_NO;
    }

    public HeightenedMonitoringRsrcRes getResourceDetails(Long idResource){
        HeightenedMonitoringRsrcRes heightenedMonitoringRsrcRes = new HeightenedMonitoringRsrcRes();
        CapsResourceLinkDto capsResourceLinkDto = capsResourceDao.getCapsResourceLink(idResource, CPA);
        if (!ObjectUtils.isEmpty(capsResourceLinkDto) && capsResourceLinkDto != null) {
            heightenedMonitoringRsrcRes.setIdRsrcAgency(capsResourceLinkDto.getIdRsrcLinkParent());
            ResourceDto rsrcDto = capsResourceDao.getResourceById(capsResourceLinkDto.getIdRsrcLinkParent());
            if (!ObjectUtils.isEmpty(rsrcDto)) {
                heightenedMonitoringRsrcRes.setNmRsrcAgency(rsrcDto.getNmResource());
            }
        }
        return heightenedMonitoringRsrcRes;
    }

    /*
     *  Get Utilization history from the child link.
     */
    private List<HeightenedMonitoringUtilizationHistoryBean> getHmmUtilizationHistory(HMRequest hmRequest){
        List<HeightenedMonitoringUtilizationHistoryBean> heightenedMonitoringUtilizationHistoryBeanList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(hmRequest.getHmReqChildLinks()) ) {
            hmRequest.getHmReqChildLinks().forEach(hmReqChildLink -> {
                HeightenedMonitoringUtilizationHistoryBean historyBean = new HeightenedMonitoringUtilizationHistoryBean();
                historyBean.setIdStage(hmReqChildLink.getStage().getIdStage());
                historyBean.setNmStage(hmReqChildLink.getStage().getNmStage());
                historyBean.setCdStage(hmReqChildLink.getStage().getCdStage());
                historyBean.setIdPlcmtEvent(hmReqChildLink.getPlacement() != null ? hmReqChildLink.getPlacement().getIdPlcmtEvent(): null);
                historyBean.setUtilized(historyBean.getIdPlcmtEvent() != null ? ServiceConstants.HMM_YES : ServiceConstants.HMM_NO);
                heightenedMonitoringUtilizationHistoryBeanList.add(historyBean);
            });
        }
        return heightenedMonitoringUtilizationHistoryBeanList;
    }

    /*
     *  Populate HM Request values to HMInfoBean in edit/view mode
     */
    private void populateHmReqInfoBean(HMRequest hmRequest, HeightenedMonitoringInfoBean heightenedMonitoringInfoBean){
        heightenedMonitoringInfoBean.setDtPlcmtStart(hmRequest.getDtStart());
        heightenedMonitoringInfoBean.setIndPlcmtEmerg(hmRequest.getIndHmPlcmntEmrgncy());
        heightenedMonitoringInfoBean.setIndCourtOrdered(hmRequest.getIndCourtOrdered());
        heightenedMonitoringInfoBean.setDtCourtOrder(hmRequest.getDtCourtOrdered());
        // Assign Child Sexual Victimization History and Child Sexual Aggression History
        Optional<HMReqChildLink> childLinkOptional = hmRequest.getHmReqChildLinks().stream().filter(hmReqChildLink ->
                hmReqChildLink.getHmRequest().getHmRequestId().equals(hmRequest.getHmRequestId())
                        && hmReqChildLink.getStage().getIdStage().equals(heightenedMonitoringInfoBean.getIdStage())).findFirst();
        if(childLinkOptional.isPresent()) {
            HMReqChildLink childLink = childLinkOptional.get();
            String eventStatus = hmRequest.getIdEvent().getCdEventStatus();
            // Get from source table, if status is COMP or PROC
             if("COMP".equals(eventStatus)  || "PROC".equals(eventStatus)){
                 Long idStage = heightenedMonitoringInfoBean.getIdStage();
                 heightenedMonitoringInfoBean.setIndChildSexVictHistory(getIndDescription(heightenedMonitoringDao.getHMMChildSexualVictimizationHistoryFlag(idStage)));
                 heightenedMonitoringInfoBean.setIndChildSexAggrHistory(heightenedMonitoringDao.indHMMChildSexualAggressionHistoryExists(idStage) ? ServiceConstants.HMM_YES : ServiceConstants.HMM_NO);
                 heightenedMonitoringInfoBean.setChildLegalRegion(heightenedMonitoringDao.getHMMChildLegalRegion(idStage));
             } else {
                 heightenedMonitoringInfoBean.setIndChildSexAggrHistory(getIndDescription(childLink.getIndSexualAggrsnHist()));
                 heightenedMonitoringInfoBean.setIndChildSexVictHistory(getIndDescription(childLink.getIndSexualVictmsnHist()));
                 heightenedMonitoringInfoBean.setChildLegalRegion(childLink.getCdChildLegalRegion());
             }
        }
        if(null != hmRequest.getCapsResourceByIdRsrcFacil() && !ObjectUtils.isEmpty(hmRequest.getCapsResourceByIdRsrcFacil().getIdResource())) {
            heightenedMonitoringInfoBean.setIdRsrcFacil(hmRequest.getCapsResourceByIdRsrcFacil().getIdResource());
            heightenedMonitoringInfoBean.setNmPlcmtFacil(hmRequest.getCapsResourceByIdRsrcFacil().getNmResource());
        }
        if(null != hmRequest.getCapsResourceByIdRsrcAgency() && !ObjectUtils.isEmpty(hmRequest.getCapsResourceByIdRsrcAgency().getIdResource())) {
            heightenedMonitoringInfoBean.setIdRsrcAgency(hmRequest.getCapsResourceByIdRsrcAgency().getIdResource());
            heightenedMonitoringInfoBean.setNmPlcmtAgency(hmRequest.getCapsResourceByIdRsrcAgency().getNmResource());
        }
        heightenedMonitoringInfoBean.setBestInterestStatement(hmRequest.getTxtBestInterestDescr());
        if(null != hmRequest.getIdEvent()) {
            heightenedMonitoringInfoBean.setIdEvent(hmRequest.getIdEvent().getIdEvent());
            heightenedMonitoringInfoBean.setCdEventStatus(hmRequest.getIdEvent().getCdEventStatus());
            // Call HM Request Narrative
            heightenedMonitoringInfoBean.setDocExist(heightenedMonitoringDao.isDocExists(hmRequest.getIdEvent().getIdEvent()));
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public HeightenedMonitoringInfoRes saveHmmRequestDetails(HeightenedMonitoringInfoReq heightenedMonitoringInfoReq){
        HeightenedMonitoringInfoRes heightenedMonitoringInfoRes = new HeightenedMonitoringInfoRes();
        HeightenedMonitoringInfoBean heightenedMonitoringInfoBean = new  HeightenedMonitoringInfoBean();
        Long idStage = heightenedMonitoringInfoReq.getIdStage();

        if(heightenedMonitoringInfoReq.isCallFromSSCCAPI()){
            String indChildSexVict = heightenedMonitoringDao.getHMMChildSexualVictimizationHistoryFlag(idStage);
            Boolean siblingsIndChildSexVictNull = Boolean.FALSE;
      //            for(Long siblingStage : heightenedMonitoringInfoReq.getIdStageSiblings()){
//                if(ObjectUtils.isEmpty(heightenedMonitoringDao.getHMMChildSexualVictimizationHistoryFlag(siblingStage))){
      //                    siblingsIndChildSexVictNull = true;
      //                    break;
      //                }
      //            }

      if (!ObjectUtils.isEmpty(heightenedMonitoringInfoReq.getIdStageSiblings())) {
        StageDto stageDto = stageDao.getStageById(idStage);
        // All Sibling list
        List<HeightenedMonitoringSiblingBean> siblingBeanList =
            heightenedMonitoringDao.getHmmRequestSiblingList(idStage, stageDto.getIdCase());
        for (HeightenedMonitoringSiblingBean siblingBean : siblingBeanList) {
          if (heightenedMonitoringInfoReq.getIdStageSiblings().contains(siblingBean.getIdStage())
              && ObjectUtils.isEmpty(siblingBean.getIndSexualVictmsnHist())) {
            siblingsIndChildSexVictNull = true;
            break;
          }
                }
            }
            if(ObjectUtils.isEmpty(indChildSexVict) || siblingsIndChildSexVictNull){
                ErrorDto err = new ErrorDto();
                err.setErrorCode(400);
                err.setErrorMsg("Child Sexual Victimization History: NULL value(s) found. Please complete the specific Child's Sexual Victimization History before submitting the Heightened Monitoring Placement Request.");
                heightenedMonitoringInfoRes.setErrorDto(err);
                return heightenedMonitoringInfoRes;
            }

            heightenedMonitoringInfoReq.setIndChildSexVictHistory(getIndDescription(indChildSexVict));
            heightenedMonitoringInfoReq.setIndChildSexAggrHistory(heightenedMonitoringDao.indHMMChildSexualAggressionHistoryExists(idStage) ? ServiceConstants.HMM_YES : ServiceConstants.HMM_NO);
        }

        if(ObjectUtils.isEmpty(heightenedMonitoringInfoReq.getIdEvent())) {
            Long eventId = createEvent(heightenedMonitoringInfoReq);
            // Create HM Request and Siblings
            HMRequest hmRequest = getHmRequestForCreateFromHmmInfoReq(heightenedMonitoringInfoReq, eventId);
            if(HM_SUBMIT.equals(heightenedMonitoringInfoReq.getFunCode())){
                Event eventExisting = eventDao.getEventById(eventId);
                eventExisting.setCdEventStatus(ServiceConstants.EVENTSTATUS_COMPLETE);
                eventDao.updateEvent(eventExisting, ServiceConstants.REQ_FUNC_CD_UPDATE);
            }
            if(!"Y".equals(hmRequest.getIndCourtOrdered())){
                hmRequest.setDtCourtOrdered(null);
            }
            heightenedMonitoringDao.saveOrUpdateHMRequest(hmRequest);
            heightenedMonitoringInfoBean.setIdEvent(eventId);
            heightenedMonitoringInfoRes.setHeightenedMonitoringInfoBean(heightenedMonitoringInfoBean);
        }else{
            HMRequest hmRequestExisting =  heightenedMonitoringDao.getHmRequestByEventId(heightenedMonitoringInfoReq.getIdEvent());
            updateHmRequestFromHmmInfoReq(heightenedMonitoringInfoReq, hmRequestExisting);
            updateHMReqChildLink(heightenedMonitoringInfoReq, hmRequestExisting);
            // Update description
            Event existingEvent = hmRequestExisting.getIdEvent();
            existingEvent.setTxtEventDescr(buildEventDescription(heightenedMonitoringInfoReq));

            //for invalidating the approval in case event is PEND and user not navigating from Task
            Long eventId = heightenedMonitoringInfoReq.getIdEvent();
            Consumer<Long> invalidateEvent = idEvent -> {
                ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
                ApprovalCommonOutDto approvalCommonOutDto = new ApprovalCommonOutDto();
                approvalCommonInDto.setIdEvent(idEvent);
                // Call Service to invalidate approval
                approvalCommonService.InvalidateAprvl(approvalCommonInDto, approvalCommonOutDto);
            };
            if (!ObjectUtils.isEmpty(eventId) && eventId > ServiceConstants.ZERO ) {
                String cdEventStatus = eventDao.getEventStatus(eventId);
                if (ServiceConstants.EVENT_STATUS_PEND.equals(cdEventStatus)) {
                    invalidateEvent.accept(eventId);
                }
            }
            if(!"Y".equals(hmRequestExisting.getIndCourtOrdered())){
                hmRequestExisting.setDtCourtOrdered(null);
            }
            // update modified data
            heightenedMonitoringDao.saveOrUpdateHMRequest(hmRequestExisting);
            heightenedMonitoringInfoBean.setIdEvent(heightenedMonitoringInfoReq.getIdEvent());
            heightenedMonitoringInfoRes.setHeightenedMonitoringInfoBean(heightenedMonitoringInfoBean);
        }
        return heightenedMonitoringInfoRes;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void deleteHmmRequestDetails(Long idEvent) {
        if(!ObjectUtils.isEmpty(idEvent)) {
            HMRequest hmRequest = heightenedMonitoringDao.getHmRequestByEventId(idEvent);
            heightenedMonitoringDao.deleteHMRequest(hmRequest);
            log.info("Deleted HMRequest and HMReqChildLink data and Event data");
        }
    }


    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public HeightenedMonitoringSiblingRes getHmmSiblingList(Long idStage, Long idCase){
        HeightenedMonitoringSiblingRes heightenedMonitoringSiblingRes = new HeightenedMonitoringSiblingRes();
        heightenedMonitoringSiblingRes.setHeightenedMonitoringSiblingList(heightenedMonitoringDao.getHmmRequestSiblingList(idStage, idCase));
        return heightenedMonitoringSiblingRes;
    }

    private HMRequest getHmRequestForCreateFromHmmInfoReq(HeightenedMonitoringInfoReq heightenedMonitoringInfoReq, Long eventId){
        HMRequest hmRequest = new HMRequest();
        setDatesAndResourceIds(heightenedMonitoringInfoReq, hmRequest);
        if(!ObjectUtils.isEmpty(eventId)) {
            Event event = new Event();
            event.setIdEvent(eventId);
            hmRequest.setIdEvent(event);
        }

        hmRequest.setTxtBestInterestDescr(heightenedMonitoringInfoReq.getBestInterestStatement());
        hmRequest.setDtCreated(new Date());
        hmRequest.setIdCreatedPerson(Long.parseLong(heightenedMonitoringInfoReq.getUserId()));
        // Create HM Req Child links
        createHMReqChildLinks(hmRequest, heightenedMonitoringInfoReq);
        return hmRequest;
    }

    private void setDatesAndResourceIds(HeightenedMonitoringInfoReq heightenedMonitoringInfoReq, HMRequest hmRequest) {
        if(null != heightenedMonitoringInfoReq.getDtPlcmtStart()) {
            hmRequest.setDtStart(heightenedMonitoringInfoReq.getDtPlcmtStart());
            hmRequest.setDtExpire(createExpiryDate(heightenedMonitoringInfoReq.getDtPlcmtStart()));
        }
        if(!ObjectUtils.isEmpty(heightenedMonitoringInfoReq.getIdRsrcAgency())) {
            CapsResource capsResourceAgency = new CapsResource();
            capsResourceAgency.setIdResource(heightenedMonitoringInfoReq.getIdRsrcAgency());
            hmRequest.setCapsResourceByIdRsrcAgency(capsResourceAgency);
        }
        if(!ObjectUtils.isEmpty(heightenedMonitoringInfoReq.getIdRsrcFacil())) {
            CapsResource capsResourceFacil = new CapsResource();
            capsResourceFacil.setIdResource(heightenedMonitoringInfoReq.getIdRsrcFacil());
            hmRequest.setCapsResourceByIdRsrcFacil(capsResourceFacil);
        }
        hmRequest.setIndHmPlcmntEmrgncy(ServiceConstants.Y.equals(heightenedMonitoringInfoReq.getIndPlcmtEmerg()) ? ServiceConstants.Y : ServiceConstants.N);
        hmRequest.setIndCourtOrdered(ServiceConstants.Y.equals(heightenedMonitoringInfoReq.getIndCourtOrdered()) ? ServiceConstants.Y : ServiceConstants.N);
        if("Y".equalsIgnoreCase(heightenedMonitoringInfoReq.getIndCourtOrdered()) && null != heightenedMonitoringInfoReq.getDtCourtOrder()) {
            hmRequest.setDtCourtOrdered(heightenedMonitoringInfoReq.getDtCourtOrder());
        }
        hmRequest.setTxtBestInterestDescr(heightenedMonitoringInfoReq.getBestInterestStatement());
        hmRequest.setDtLastUpdated(new Date());
        hmRequest.setIdLastUpdatedPerson(Long.parseLong(heightenedMonitoringInfoReq.getUserId()));
    }

    private void updateHmRequestFromHmmInfoReq(HeightenedMonitoringInfoReq heightenedMonitoringInfoReq, HMRequest hmRequest){
        setDatesAndResourceIds(heightenedMonitoringInfoReq, hmRequest);
    }

    private void updateHMReqChildLink(HeightenedMonitoringInfoReq heightenedMonitoringInfoReq, HMRequest hmRequest) {
        Set<HMReqChildLink> hmReqChildLinkSetExisting = hmRequest.getHmReqChildLinks();
        Set<Long> idStageSiblingsSelected = heightenedMonitoringInfoReq.getIdStageSiblings();
        // Check if the existing siblings are present in the selected ids. If not, they need to be removed from the list
        Set<Long> idStageSiblingsExisting = !CollectionUtils.isEmpty(hmReqChildLinkSetExisting) ?
                hmReqChildLinkSetExisting.stream().map(HMReqChildLink::getStage).map(Stage::getIdStage).collect(Collectors.toSet()) : new HashSet<>();
        if (!CollectionUtils.isEmpty(hmReqChildLinkSetExisting)) {
            Set<Long> idStageSiblingsToBeRemoved = idStageSiblingsExisting.stream().filter(idStageSibling -> !idStageSiblingsSelected.contains(idStageSibling)).collect(Collectors.toSet());
            if (!CollectionUtils.isEmpty(idStageSiblingsToBeRemoved)) {
                idStageSiblingsToBeRemoved.remove(heightenedMonitoringInfoReq.getIdStage());
                idStageSiblingsToBeRemoved.forEach(idStageToBeRemoved -> {
                    Optional<HMReqChildLink> childLinkOptional  = hmReqChildLinkSetExisting.stream().filter(hmReqChildLink -> hmReqChildLink.getStage().getIdStage().equals(idStageToBeRemoved)).findFirst();
                    childLinkOptional.ifPresent(hmReqChildLinkSetExisting::remove);
                });
            }
        }

        if (!CollectionUtils.isEmpty(idStageSiblingsSelected)) {
            Set<Long> idStageSiblingsToBeAdded = !CollectionUtils.isEmpty(idStageSiblingsExisting) ?
                    idStageSiblingsSelected.stream().filter(idStageSibling -> !idStageSiblingsExisting.contains(idStageSibling)).collect(Collectors.toSet()) : idStageSiblingsSelected;
            if(!CollectionUtils.isEmpty(idStageSiblingsToBeAdded)){
                List<HeightenedMonitoringSiblingBean> hmmRequestSiblingList = heightenedMonitoringDao.getHmmRequestSiblingList(heightenedMonitoringInfoReq.getIdStage(), heightenedMonitoringInfoReq.getIdCase());
                idStageSiblingsToBeAdded.forEach(idStageSiblingToAdd -> {
                    Optional<HeightenedMonitoringSiblingBean> siblingBeanOptional = hmmRequestSiblingList.stream().filter(hmSiblingBean -> hmSiblingBean.getIdStage().equals(idStageSiblingToAdd)).findFirst();
                    if(siblingBeanOptional.isPresent()){
                        HeightenedMonitoringSiblingBean siblingBean = siblingBeanOptional.get();
                        HMReqChildLink hmReqSiblingChildLink = getHMReqChildLinkFromFields(idStageSiblingToAdd, siblingBean.getIdPerson(),
                                heightenedMonitoringInfoReq.getChildLegalRegion(), siblingBean.getIndSexualVictmsnHist(), siblingBean.getIndSexualAggrsnHist(),
                                heightenedMonitoringInfoReq.getUserId());
                        hmReqChildLinkSetExisting.add(hmReqSiblingChildLink);
                        hmReqSiblingChildLink.setHmRequest(hmRequest);
                    }
                });
            }
        }
        //artf202240 -Warranty defect-18199-Update the details for main child and siblings .When the legal region or other indicators are changed after the HM request was saved those details are not getting updated in table,
        // hence the APRV request is missing the updated details
       hmReqChildLinkSetExisting.stream().filter(hmReqChildLink -> null != hmReqChildLink.getHmReqChildLinkId()).forEach(childentity-> {
          Boolean siblingObj = childentity.getStage().getIdStage().equals(heightenedMonitoringInfoReq.getIdStage()) ? Boolean.FALSE : Boolean.TRUE;
          childentity.setCdChildLegalRegion(siblingObj ? heightenedMonitoringDao.getHMMChildLegalRegion(childentity.getStage().getIdStage()): heightenedMonitoringInfoReq.getChildLegalRegion());
          childentity.setIndSexualAggrsnHist(siblingObj ? heightenedMonitoringDao.indHMMChildSexualAggressionHistoryExists(childentity.getStage().getIdStage()) ? ServiceConstants.Y : ServiceConstants.N :getIndicatorCode(heightenedMonitoringInfoReq.getIndChildSexAggrHistory()) );
          childentity.setIndSexualVictmsnHist(siblingObj ? getIndicatorCode(heightenedMonitoringDao.getHMMChildSexualVictimizationHistoryFlag(childentity.getStage().getIdStage())) : getIndicatorCode(heightenedMonitoringInfoReq.getIndChildSexVictHistory()) );
       });
        //end artf202240
    }

    /*
    *  Create HM Request Child Link records based on Siblings and Primary child.
    */
    private void createHMReqChildLinks(HMRequest hmRequest, HeightenedMonitoringInfoReq heightenedMonitoringInfoReq) {
        if(!StringUtils.isEmpty(heightenedMonitoringInfoReq.getIdStageSiblings()) ) {
            Set<HMReqChildLink> hmReqChildLinkSet = new HashSet<>();
            HMReqChildLink hmReqPrimaryChildLink = getHMReqChildLinkFromFields(heightenedMonitoringInfoReq.getIdStage(), heightenedMonitoringInfoReq.getIdPerson(),
                    heightenedMonitoringInfoReq.getChildLegalRegion(), heightenedMonitoringInfoReq.getIndChildSexVictHistory(), heightenedMonitoringInfoReq.getIndChildSexAggrHistory(), heightenedMonitoringInfoReq.getUserId());
            hmReqPrimaryChildLink.setHmRequest(hmRequest);
            hmReqChildLinkSet.add(hmReqPrimaryChildLink);

            Set<Long> idStageSiblings = heightenedMonitoringInfoReq.getIdStageSiblings();
            if(!CollectionUtils.isEmpty(idStageSiblings)) {
                List<HeightenedMonitoringSiblingBean> hmmRequestSiblingList = heightenedMonitoringDao.getHmmRequestSiblingList(heightenedMonitoringInfoReq.getIdStage(), heightenedMonitoringInfoReq.getIdCase());
                idStageSiblings.forEach(idStageSibling -> {
                    Optional<HeightenedMonitoringSiblingBean>  siblingBeanOptional = hmmRequestSiblingList.stream().filter(heightenedMonitoringSiblingBean ->
                            heightenedMonitoringSiblingBean.getIdStage().equals(idStageSibling)).findFirst();
                    if(siblingBeanOptional.isPresent()){
                        HeightenedMonitoringSiblingBean siblingBean = siblingBeanOptional.get();
                        HMReqChildLink hmReqSiblingChildLink = getHMReqChildLinkFromFields(idStageSibling, siblingBean.getIdPerson(),
                                heightenedMonitoringInfoReq.getChildLegalRegion(), siblingBean.getIndSexualVictmsnHist(), siblingBean.getIndSexualAggrsnHist(),
                                heightenedMonitoringInfoReq.getUserId());
                        hmReqSiblingChildLink.setHmRequest(hmRequest);
                        hmReqChildLinkSet.add(hmReqSiblingChildLink);
                    }
                } );
            }
            // Add All child links to HM Request
            hmRequest.setHmReqChildLinks(hmReqChildLinkSet);
        }
    }

    private HMReqChildLink getHMReqChildLinkFromFields(Long idStage, Long idPerson, String legalRegion, String indVicmHist, String indAggrsHist, String userId) {
        HMReqChildLink hmReqChildLink = new HMReqChildLink();
        Person person = new Person();
        person.setIdPerson(idPerson);
        hmReqChildLink.setPerson(person);
        Stage stage = new Stage();
        stage.setIdStage(idStage);
        hmReqChildLink.setStage(stage);
        hmReqChildLink.setCdChildLegalRegion(legalRegion);
        hmReqChildLink.setIndSexualAggrsnHist(getIndicatorCode(indAggrsHist));
        hmReqChildLink.setIndSexualVictmsnHist(getIndicatorCode(indVicmHist));
        hmReqChildLink.setDtLastUpdated(new Date());
        hmReqChildLink.setIdLastUpdatedPerson(Long.valueOf(userId));
        hmReqChildLink.setDtCreated(new Date());
        hmReqChildLink.setIdCreatedPerson(Long.valueOf(userId));
        return hmReqChildLink;
    }

    private String getIndicatorCode(String indCodeOrDesc) {
        if(null == indCodeOrDesc || indCodeOrDesc.length() == 0) {
            return "";
        }
        if("Yes".equalsIgnoreCase(indCodeOrDesc) || "Y".equalsIgnoreCase(indCodeOrDesc)) {
            return "Y";
        }
        return "N";
    }

    /**
     * Method Name: createAndReturnEventid
     * Method Description:Method to generate event id in event table.
     *
     * @param heightenedMonitoringInfoReq
     * @return @
     */
    private Long createEvent(HeightenedMonitoringInfoReq heightenedMonitoringInfoReq) {
        PostEventIPDto postEventIPDto = new PostEventIPDto();
        Date date = new Date(System.currentTimeMillis());
        ServiceReqHeaderDto archInputDto = new ServiceReqHeaderDto();
        postEventIPDto.setEventDescr(buildEventDescription(heightenedMonitoringInfoReq));
        postEventIPDto.setCdTask(ServiceConstants.HMM_SERV_REF_TASK);
        postEventIPDto.setIdPerson(Long.parseLong(heightenedMonitoringInfoReq.getUserId()));
        postEventIPDto.setIdStage(heightenedMonitoringInfoReq.getIdStage());
        postEventIPDto.setDtEventOccurred(date);
        postEventIPDto.setUserId(heightenedMonitoringInfoReq.getUserId());
        archInputDto.setReqFuncCd(ServiceConstants.REQ_IND_AUD_ADD);
        postEventIPDto.setDtEventOccurred(date);
        postEventIPDto.setCdEventStatus(ServiceConstants.CEVTSTAT_PROC);
        postEventIPDto.setTsLastUpdate(date);
        postEventIPDto.setCdEventType(ServiceConstants.CEVNTTYP_HMM);

        PostEventDto postEventDto = new PostEventDto();
        List<PostEventDto> postEventDtoList = new ArrayList<>();
        postEventDto.setIdPerson(heightenedMonitoringInfoReq.getIdPerson());
        postEventDto.setCdScrDataAction(ServiceConstants.REQ_FUNC_CD_ADD);
        postEventDtoList.add(postEventDto);
        postEventIPDto.setPostEventDto(postEventDtoList);
        PostEventOPDto postEventOPDto = postEventService.checkPostEventStatus(postEventIPDto, archInputDto);
        return postEventOPDto.getIdEvent();
    }

    // Build Event Description
    private String buildEventDescription(HeightenedMonitoringInfoReq heightenedMonitoringInfoReq) {
        StringBuffer eventDescr = new StringBuffer();
        eventDescr.append("Placement Start Date " + DateUtils.stringDt(heightenedMonitoringInfoReq.getDtPlcmtStart()) + " ");
        Person person = personDao.getPerson(heightenedMonitoringInfoReq.getIdPerson());
        eventDescr.append(person.getNmPersonFull());
        eventDescr.append(" ");
        eventDescr.append(!StringUtils.isEmpty(heightenedMonitoringInfoReq.getNmPlcmtFacil()) ? heightenedMonitoringInfoReq.getNmPlcmtFacil() :heightenedMonitoringInfoReq.getNmPlcmtAgency());
        return eventDescr.toString();
    }

    /**
     * PPM 60692-artf179570 - Placement Page changes for HM Resource
     * Method Description: This method checks the HM status for the given Resource ID on
     * Placement Creation Date
     *
     * @param idResource
     * @return
     **/
    @Override
    public Boolean checkHMStatusActive(Long idResource) {

       return heightenedMonitoringDao.checkHMStatusActive(idResource);
    }

    /**
     * PPM 60692-artf179570 - Placement Page changes for HM Resource
     * Method Description: This method validates a emergency or non emergency placement if the HM Status is active
     *
     * @param idResource
     * @param idStage
     * @param dtPlcmtStart
     * @param indEmergency
     * @return
     */
    @Override
    public Boolean validatePlacementDetail(Long idResource, Long idStage, Date dtPlcmtStart, String indEmergency) {

        return heightenedMonitoringDao.validatePlacementDetail(idResource, idStage, dtPlcmtStart, indEmergency);
    }

    /**
     * PPM 60692-artf179778-Start-Approve page changes
     * Method Description: This method populates the Placement event id which utilizes the HM request and updates to
     * HM_REQ_CHILD_LINK Table
     *
     * @param idStage
     * @param idApproval
     */
    @Override
    @Transactional
    public void populatePlacementEventToHmReq(Long idStage, Long idApproval) {

        heightenedMonitoringDao.updatePlacementEventToHmReq(idStage, idApproval);

    }

    // Add 14 days to the input date.
    public Date createExpiryDate(Date jdate) {
        Integer noOfDays = 13;
        Calendar cal = Calendar.getInstance();
        cal.setTime(jdate);
        cal.add(Calendar.DATE, noOfDays);
        return cal.getTime();
    }

    /**
     * PPM 60692-artf179777 - To Do page changes
     * Method Description: This method fetches the Approver for the To Do Detail
     *
     * @param legalRegion
     * @return
     */
    @Override
    public AssignedDto fetchApproverForHMToDo(String legalRegion, boolean isSecondary) {

        return heightenedMonitoringDao.fetchApproverForHmToDo(legalRegion, isSecondary);
    }

    /**
     * PPM 60692-artf179777 - To Do page changes
     * Method Description: This method validates the assigned approver for To Do
     *
     * @param idPerson
     * @param legalRegion
     * @param isSecondary
     * @param idStage
     * @return
     */
    public Boolean validateHmToDoApprover(Long idPerson, String legalRegion, boolean isSecondary, Long idStage) {

        return heightenedMonitoringDao.validateHmToDoApprover(idPerson, legalRegion, isSecondary, idStage);
    }

    /**
     * PPM 60692-artf179777 - To Do page changes
     * PM 60692-artf197133 - DEV PCR Approver Email changes
     * Method Description: This method fetches all the details for HM Approver Email
     *
     * @param commonHelperReq
     * @return
     */
    @Override
    public HeightenedMonitoringEmailRes getHMApproverEmailDetails(CommonHelperReq commonHelperReq) {

        HeightenedMonitoringEmailRes heightenedMonitoringEmailRes = new HeightenedMonitoringEmailRes();
        HMApproverEmailDto hmApproverEmailDto = heightenedMonitoringDao.fetchHmApproverEmailDetails(commonHelperReq.getIdApproval());

        if (!ObjectUtils.isEmpty(hmApproverEmailDto)) {

            // Retrieves the Siblings on HM Request by idEvent
            hmApproverEmailDto.setNmSibilings(heightenedMonitoringDao.fetchSiblingsOnHmRequest(commonHelperReq.getIdApproval()));

            //PPM 60692-artf197133-Start-DEV PCR Approver Email changes
            Map<Long, String> toEmails = new HashMap<>();
            Map<Long, String> ccEmails = new HashMap<>();

            switch (commonHelperReq.getCaseStatus()) {
                case ServiceConstants.PEND: {
                    // Retrieve the Approver Details based on the Approval ID
                    List<EmailDetailDto> approverDetails = heightenedMonitoringDao.getApproverDetails(commonHelperReq.getIdApproval());
                    if (!CollectionUtils.isEmpty(approverDetails)) {
                        approverDetails.forEach(dto -> {
                            if (commonHelperReq.getSecondary() && dto.getIdPerson().equals(commonHelperReq.getUserID())) {
                                // Adding the First Approver to CC if this is Secondary submission
                                ccEmails.put(dto.getIdPerson(), dto.getEmail());
                            } else {
                                toEmails.put(dto.getIdPerson(), dto.getEmail());
                            }
                        });
                    }
                    // Retrieve the Caseworker details based on Approval ID if secondary else based on User Id
                    List<EmailDetailDto> caseworkerDetails = commonHelperReq.getSecondary() ?
                            heightenedMonitoringDao.getCaseworkerDetailsByApproval(commonHelperReq.getIdStage(), commonHelperReq.getIdApproval()) :
                            heightenedMonitoringDao.getCaseworkerDetails(commonHelperReq.getIdStage(), commonHelperReq.getUserID());
                    if (!CollectionUtils.isEmpty(caseworkerDetails)) {
                        ccEmails.putAll(caseworkerDetails.stream()
                                .collect(Collectors.toMap(EmailDetailDto::getIdPerson, EmailDetailDto::getEmail)));
                    }
                    hmApproverEmailDto.setTxtToEmails(toEmails);
                    hmApproverEmailDto.setTxtCcEmails(ccEmails);
                }
                break;
                case ServiceConstants.APRV_STATUS_APPROVED:
                case ServiceConstants.APRV_STATUS_REJECT: {

                    // Retrieve the Caseworker details based on Approval ID
                    List<EmailDetailDto> caseworkerDetails = heightenedMonitoringDao
                            .getCaseworkerDetailsByApproval(commonHelperReq.getIdStage(), commonHelperReq.getIdApproval());
                    if (!CollectionUtils.isEmpty(caseworkerDetails)) {
                        toEmails.putAll(caseworkerDetails.stream()
                                .collect(Collectors.toMap(EmailDetailDto::getIdPerson, EmailDetailDto::getEmail)));
                    }

                    // Retrieve the Approver Details based on the Approval ID
                    List<EmailDetailDto> approverDetails = heightenedMonitoringDao.getApproverDetails(commonHelperReq.getIdApproval());
                    if (!CollectionUtils.isEmpty(approverDetails)) {
                        approverDetails.forEach(dto -> {
                            if (dto.getIdPerson().equals(commonHelperReq.getUserID())) {
                                // Assign the Second Approver's comment
                                hmApproverEmailDto.setComments(dto.getComments());
                            }
                            ccEmails.put(dto.getIdPerson(), dto.getEmail());
                        });
                    }
                    hmApproverEmailDto.setTxtToEmails(toEmails);
                    hmApproverEmailDto.setTxtCcEmails(ccEmails);

                    String shortDesc = null;
                    String longDesc = null;

                    if (ServiceConstants.APRV_STATUS_APPROVED.equals(commonHelperReq.getCaseStatus())) {
                        shortDesc = String.format(ServiceConstants.HM_PLCMT_REQ_APPROVAL_SHORT, hmApproverEmailDto.getNmResource(),
                                hmApproverEmailDto.getIdResource());
                        longDesc = String.format(ServiceConstants.HM_PLCMT_REQ_APPROVAL_LONG, hmApproverEmailDto.getNmResource(),
                                hmApproverEmailDto.getIdResource());
                    } else {
                        shortDesc = String.format(ServiceConstants.HM_PLCMT_REQ_REJECT_SHORT, hmApproverEmailDto.getNmResource(),
                                hmApproverEmailDto.getIdResource());
                        longDesc = String.format(ServiceConstants.HM_PLCMT_REQ_REJECT_LONG, hmApproverEmailDto.getNmResource(),
                                hmApproverEmailDto.getIdResource());
                    }
                    // Alert Notification
                    for (Long idPerson : toEmails.keySet()) {
                        alertService.createFbssAlert(hmApproverEmailDto.getIdStage(), idPerson, null,
                                hmApproverEmailDto.getIdCase(), longDesc, shortDesc);
                    }
                }
                break;
            }

            //PPM 60692-artf197133-End
        }

        heightenedMonitoringEmailRes.setHmApproverEmailDto(hmApproverEmailDto);

        return heightenedMonitoringEmailRes;

    }

    /**
     * PPM 60692-artf179778 - Maintain Heightened Monitoring Approver changes
     * Method Description: This method fetches the Approver List
     *
     * @return
     */
    @Override
    public HeightenedMonitoringEmailRes fetchApproversList() {

        HeightenedMonitoringEmailRes heightenedMonitoringEmailRes = new HeightenedMonitoringEmailRes();

        List<HMApproverListDto> hmApproverListDtos = heightenedMonitoringDao.fetchApproversList();

        if (!CollectionUtils.isEmpty(hmApproverListDtos)) {
            hmApproverListDtos.forEach(dto -> dto.setTxtRegionTitle(ServiceConstants.HM_REGION_TITLE_MAP
                    .get(dto.getCdRegionTitle())));
        }

        heightenedMonitoringEmailRes.setHmApproverListDtos(hmApproverListDtos);

        return heightenedMonitoringEmailRes;
    }

    /**
     * PPM 60692-artf179778 - Maintain Heightened Monitoring Approver changes
     * Method Description: This method deletes the approver
     *
     * @param idApprover
     * @param cdRegionTitle
     * @return
     */
    @Override
    public void deleteApprover(Long idApprover, String cdRegionTitle) {

        Pair<String, String> regionDetails = retrieveRegionDetails(cdRegionTitle);

        String cdRegDirectorRegion = regionDetails.getLeft();
        String cdSubRegion = regionDetails.getRight();

        heightenedMonitoringDao.deleteApprover(idApprover, cdRegDirectorRegion, cdSubRegion);

    }

    /**
     * PPM 60692-artf179778 - Maintain Heightened Monitoring Approver changes
     * Method Description: This method fetches the Region Title Map
     *
     * @return
     */
    @Override
    public HeightenedMonitoringEmailRes fetchRegionTitleMap() {

        HeightenedMonitoringEmailRes heightenedMonitoringEmailRes = new HeightenedMonitoringEmailRes();
        HMAddApproverDto hmAddApproverDto = new HMAddApproverDto();
        List<HMApproverListDto> hmApproverListDtos = heightenedMonitoringDao.fetchApproversList();

        if (!CollectionUtils.isEmpty(hmApproverListDtos)) {

            List<String> availableReigonTitles = hmApproverListDtos.stream().map(HMApproverListDto::getCdRegionTitle)
                    .collect(Collectors.toList());

            Map<String, String> regionTitleMap = new HashMap<>();
            ServiceConstants.HM_REGION_TITLE_MAP.forEach((k,v) -> {
                if (!availableReigonTitles.contains(k)) {
                    regionTitleMap.put(k, v);
                }
            });
            hmAddApproverDto.setRegionTitleMap(regionTitleMap);

            Map<String, String> defaultApproverSub = new HashMap<>();
            ServiceConstants.HM_DEFAULT_APPROVER_SUB.forEach(k -> {
                HMApproverListDto hmApproverListDto = hmApproverListDtos.stream().filter(dto -> k.equals(dto.getCdRegionTitle()))
                        .findFirst().orElse(null);

                defaultApproverSub.put(k, !ObjectUtils.isEmpty(hmApproverListDto) ? hmApproverListDto.getIndDefault() : null);
            });
            hmAddApproverDto.setDefaultApproverForSubRegionMap(defaultApproverSub);
        }

        heightenedMonitoringEmailRes.setHmAddApproverDto(hmAddApproverDto);

        return heightenedMonitoringEmailRes;
    }

    /**
     * PPM 60692-artf179778 - Maintain Heightened Monitoring Approver changes
     * Method Description: This method persists the new approver
     *
     * @return
     */
    @Override
    public void saveNewApprover(HMApproverListDto hmApproverListDto) {

        Pair<String, String> regionDetails = retrieveRegionDetails(hmApproverListDto.getCdRegionTitle());

        String cdRegDirectorRegion = regionDetails.getLeft();
        String cdSubRegion = regionDetails.getRight();

        heightenedMonitoringDao.saveNewApprover(hmApproverListDto.getIdApprover(), cdRegDirectorRegion, cdSubRegion,
                hmApproverListDto.getTxtApproverEmail(), hmApproverListDto.getIndDefault());


        if (ServiceConstants.HM_DEFAULT_APPROVER_SUB.contains(hmApproverListDto.getCdRegionTitle())) {

             String updateIndDefault = ServiceConstants.STRING_IND_Y.equals(hmApproverListDto.getIndDefault()) ? "N" : "Y";

            switch (hmApproverListDto.getCdRegionTitle()) {
                case "03W" :
                    cdRegDirectorRegion = "03";
                    cdSubRegion = "E";
                    break;
                case "03E" :
                    cdRegDirectorRegion = "03";
                    cdSubRegion = "W";
                    break;
                case "06A" :
                    cdRegDirectorRegion = "06";
                    cdSubRegion = "B";
                    break;
                case "06B" :
                    cdRegDirectorRegion = "06";
                    cdSubRegion = "A";
                    break;
            }

            heightenedMonitoringDao.updateDefaultApproverForSubRegion(cdRegDirectorRegion, cdSubRegion, updateIndDefault);
        }
    }

    /**
     * PPM 60692-artf179569 - Heightened Monitoring Request validation
     * Method Description: HM Request validation check
     *
     * @return
     */
    @Override
    public Boolean checkResourceAssignedCount(Long idRsrcFacil, Set<Long> idStage) {
        return heightenedMonitoringDao.checkResourceAssignedForAllStagesCountSql(idRsrcFacil, idStage);
    }

    /**
     * This method will save or update HM resource status records for a give resourceId.
     * PPM 60692-artf178534
     * @param facilityDetailSaveReq
     */
    @Override
    public CommonHelperRes updateFacilityDetailsHmStatus(FacilityDetailSaveReq facilityDetailSaveReq) {
        CommonHelperRes retVal = new CommonHelperRes();
        retVal.setIdHmResourceStatus(heightenedMonitoringDao.updateFacilityDetailsHmStatus(facilityDetailSaveReq));
        return retVal;
    }

    /**
     * PPM 60692-artf179569 - Heightened Monitoring Request validation
     * Method Description: HM Request validation check for Doc Exist (Narrative)
     *
     * @return
     */
    @Override
    public Boolean isDocExist(Long idEvent){
        return heightenedMonitoringDao.isDocExists(idEvent);
    }

    /**
     * PPM 60692-artf197133-DEV PCR Approver Email changes
     * Method Description: This method retrieves the Resource Details based on the Event or Approval ID
     *
     * @param commonHelperReq
     * @return
     */
    public HeightenedMonitoringEmailRes getHMResourceDetails(CommonHelperReq commonHelperReq) {

        HeightenedMonitoringEmailRes heightenedMonitoringEmailRes = new HeightenedMonitoringEmailRes();
        HMApproverEmailDto hmApproverEmailDto = null;

        if (!ObjectUtils.isEmpty(commonHelperReq.getIdApproval())) {
            hmApproverEmailDto = heightenedMonitoringDao.fetchHmApproverEmailDetails(commonHelperReq.getIdApproval());
        } else if (!ObjectUtils.isEmpty(commonHelperReq.getIdEvent())) {
            hmApproverEmailDto = heightenedMonitoringDao.fetchHmApproverEmailDetailsByEvent(commonHelperReq.getIdEvent());
        }

        heightenedMonitoringEmailRes.setHmApproverEmailDto(hmApproverEmailDto);

        return heightenedMonitoringEmailRes;
    }

    /**
     * PPM 60692-artf204559 : DEV PCR 045 Mitigate Heightened Monitoring (HM) Vacant Approver Roles
     * Method Description: This method is used to show the confirmation message when the RD for that region does not exist and we need to default TO DO detail to the higher approver
     *
     * @param legalRegion@return
     */
    @Override
    public String approverConfirmation(String legalRegion) {
        String confirmMessage="";
       AssignedDto dto= heightenedMonitoringDao.fetchApproverForHmToDo(legalRegion,false);
       if(dto == null){
           //error no AC exist
           confirmMessage=ServiceConstants.HM_PLCMT_REQ_NONE_EXIST;
       }else if("DL".equalsIgnoreCase(dto.getRole())){
           //show confirm message with DL name
           confirmMessage=String.format(ServiceConstants.HM_PLCMT_REQ_DL_EXIST, dto.getNmPersonFull());
       }else if("AC".equalsIgnoreCase(dto.getRole())){
           //show confirm message with AC name
           confirmMessage=String.format(ServiceConstants.HM_PLCMT_REQ_AC_EXIST, dto.getNmPersonFull());
       }
        return confirmMessage;
    }

    /**
     * PPM 60692-artf204559 - DEV PCR 045 Mitigate HM Vacant Approver Roles
     * Method Description: This method updates the approving status for the Approver
     *
     * @param idApprover
     * @param cdRegionTitle
     * @param indActive
     *
     */
    @Override
    public void updateApprover(Long idApprover, String cdRegionTitle, String indActive) {

        Pair<String, String> regionDetails = retrieveRegionDetails(cdRegionTitle);

        String cdRegDirectorRegion = regionDetails.getLeft();
        String cdSubRegion = regionDetails.getRight();

        heightenedMonitoringDao.updateApprover(idApprover, cdRegDirectorRegion, cdSubRegion, indActive);

    }

    /**
     * PPM 60692-artf204559 - DEV PCR 045 Mitigate HM Vacant Approver Roles
     * Method Description: This method breaks down the Region Title to Director Region and Sub Region
     *
     * @param cdRegionTitle
     *
     */
    private Pair<String, String> retrieveRegionDetails(String cdRegionTitle) {

        // Default values for DL & AC
        String cdRegDirectorRegion = cdRegionTitle;
        String cdSubRegion = null;

        // Assign values for other Regions
        if (NumberUtils.isDigits(cdRegionTitle)) {
            cdRegDirectorRegion = cdRegionTitle;
        } else {
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(cdRegionTitle);

            // Assign values for Regions with Sub region
            if (matcher.find()) {
                cdRegDirectorRegion = matcher.group(0);
                String[] subRegions = cdRegionTitle.split("\\d+");
                cdSubRegion = subRegions[1];
            }

        }

        return Pair.of(cdRegDirectorRegion, cdSubRegion);
    }

    /**
     * PPM 60692-artf204559 - DEV PCR 045 Mitigate HM Vacant Approver Roles
     * Method Description: This method verifies the Approver active status
     *
     * @param idApprover
     * @param cdRegDirectorRegion
     * @param statusAvailable
     *
     */
    @Override
    public Boolean verifyApproverActiveStatus(Long idApprover, String cdRegDirectorRegion, Boolean statusAvailable) {

        Boolean activeStatus = heightenedMonitoringDao.verifyApproverActiveStatus(idApprover, cdRegDirectorRegion);

        if (!statusAvailable && activeStatus) {
            EmployeeDetailDto employeeDetailDto = employeeDao.getEmployeeById(idApprover);
            activeStatus = !ObjectUtils.isEmpty(employeeDetailDto)
                    && (ObjectUtils.isEmpty(employeeDetailDto.getDtEmpTermination())
                        || (DateUtils.isAfterToday(employeeDetailDto.getDtEmpTermination())
                            && !DateUtils.isBeforeToday(employeeDetailDto.getDtEmpTermination())));
        }

        return activeStatus;
    }

    // 77728 Heightened monitoring enhancements
    public ResourceDetailInDto fetchResourcesInfo(Long resourceId) {
        List<ResourceDetailInDto> resourceDetailInDtoList = heightenedMonitoringDao.fetchResourcesInfo(resourceId);

        ResourceDetailInDto returnValue = null;
        if (resourceDetailInDtoList != null && resourceDetailInDtoList.size() > 0) {
            returnValue = resourceDetailInDtoList.get(0);
        }
        return returnValue;
    }


    /**
     *
     * @param idPerson
     * @return
     * Checking person has Service Auth approval or not
     */
    @Override
    public boolean checkValidSvcAuthApprover(Long idPerson){
        return heightenedMonitoringDao.isValidSvcAuthApprover(idPerson);
    }

    /**
     *
     * @param idPerson
     * @return
     */
    @Override
    public boolean checkPersonHasSvcAuthApproveAbove750(Long idPerson) {
        return heightenedMonitoringDao.isValidSvcAuthApproverAbove750(idPerson);
    }

    /**
     *
     * @param idEvent
     * @return
     */
    @Override
    public double getSvcAuthAmount(Long idEvent) {
        return heightenedMonitoringDao.getSvcAuthAmountByEventId(idEvent);

    }
}
