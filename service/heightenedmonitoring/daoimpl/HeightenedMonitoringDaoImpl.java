package us.tx.state.dfps.service.heightenedmonitoring.daoimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.CapsResource;
import us.tx.state.dfps.common.domain.HMDirectorRegionLink;
import us.tx.state.dfps.common.domain.HMRequest;
import us.tx.state.dfps.common.domain.HMResourceStatus;
import us.tx.state.dfps.common.domain.HMStatusNotifEmail;
import us.tx.state.dfps.common.domain.HmReqNarr;
import us.tx.state.dfps.common.dto.EmailDetailDto;
import us.tx.state.dfps.service.casepackage.dao.CaseSummaryDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.FacilityDetailSaveReq;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.heightenedmonitoring.dao.HeightenedMonitoringDao;
import us.tx.state.dfps.service.hmm.dto.HMApproverEmailDto;
import us.tx.state.dfps.service.hmm.dto.HMApproverListDto;
import us.tx.state.dfps.service.hmm.dto.HeightenedMonitoringDto;
import us.tx.state.dfps.service.hmm.dto.HeightenedMonitoringEmailDto;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.resource.detail.dto.ResourceDetailInDto;
import us.tx.state.dfps.service.workload.dto.AssignedDto;
import us.tx.state.dfps.service.workload.dto.EventListDto;
import us.tx.state.dfps.web.placement.bean.HeightenedMonitoringSiblingBean;

@Repository
public class HeightenedMonitoringDaoImpl implements HeightenedMonitoringDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Value("${HeightenedMonitoringDaoImpl.getCurrentActiveResources}")
    private String getCurrentActiveResourcesSql;

    @Value("${HeightenedMonitoringDaoImpl.getCurrentInactiveResources}")
    private String getCurrentInactiveResourcesSql;

    @Value("${HeightenedMonitoringDaoImpl.getResourceSql}")
    private String getResourceSql;

    @Value("${HeightenedMonitoringDaoImpl.getPlacementStaffResources}")
    private String getPlacementStaffResourcesSql;

    @Value("${HeightenedMonitoringDaoImpl.updateInactiveResources}")
    private String updateInactiveResources;

    @Value("${HeightenedMonitoringDaoImpl.getHmStatusDetails}")
    private String getHmStatusDetailsSql;

    @Value("${HeightenedMonitoringDaoImpl.getHMMRequestEventList}")
    private String getHmmRequestEventListSql;

    @Value("${HeightenedMonitoringDaoImpl.getHMMRequestSiblingList}")
    private String getHmmRequestSiblingListSql;

    @Value("${HeightenedMonitoringDaoImpl.getHMMRequestSiblingListForAdd}")
    private String getHmmRequestSiblingLisForAddSql;

    @Value("${HeightenedMonitoringDaoImpl.getHMMChildSexualVictimizationHistoryFlag}")
    private String getHMMChildSexualVictimizationHistoryFlagSql;

    @Value("${HeightenedMonitoringDaoImpl.indHMMChildSexualAggressionHistoryExists}")
    private String indHMMChildSexualAggressionHistoryExistsSql;

    @Value("${HeightenedMonitoringDaoImpl.getHMMChildLegalRegion}")
    private String getHMMChildLegalRegionSql;

    @Value("${HeightenedMonitoringDaoImpl.getFacilTypAndParentRsrc}")
    private String getFacilTypAndParentRsrcSql;

     @Value("${HeightenedMonitoringDaoImpl.getHmmCaseList}")
    private String getHmmCaseListSql;

    @Value("${HeightenedMonitoringDaoImpl.checkHMStatusActive}")
    private String checkHMStatusActiveSql;

    @Value("${HeightenedMonitoringDaoImpl.validateNonEmergencyPlcmt}")
    private String validateNonEmergencyPlcmtSql;

    @Value("${HeightenedMonitoringDaoImpl.validateEmergencyPlcmt}")
    private String validateEmergencyPlcmtSql;

    @Value("${HeightenedMonitoringDaoImpl.updatePlacementEventToHM}")
    private String updatePlacementEventToHMSql;

    @Value("${HeightenedMonitoringDaoImpl.fetchRegionalDirectorByRegion}")
    private String fetchRegionalDirectorByRegionSql;

    @Value("${HeightenedMonitoringDaoImpl.fetchToDoSecondaryApprover}")
    private String fetchToDoSecondaryApproverSql;

    @Value("${HeightenedMonitoringDaoImpl.validateToDoApprover}")
    private String validateToDoApproverSql;

    @Value("${HeightenedMonitoringDaoImpl.validateToDoSecondaryApprover}")
    private String validateToDoSecondaryApproverSql;

    @Value("${HeightenedMonitoringDaoImpl.getHmApproverEmailDetail}")
    private String getHmApproverEmailDetailSql;

    @Value("${HeightenedMonitoringDaoImpl.getSiblingsOnHmRequest}")
    private String getSiblingsOnHmRequestSql;

    @Value("${HeightenedMonitoringDaoImpl.getApproversList}")
    private String getApproversListSql;

    @Value("${HeightenedMonitoringDaoImpl.checkApproverCount}")
    private String checkApproverCountSql;

    @Value("${HeightenedMonitoringDaoImpl.deleteDLAC}")
    private String deleteDLACSql;

    @Value("${HeightenedMonitoringDaoImpl.deleteRD}")
    private String deleteRDSql;

    @Value("${HeightenedMonitoringDaoImpl.deleteDirectorRegionLink}")
    private String deleteDirectorRegionLinkSql;

    @Value("${HeightenedMonitoringDaoImpl.deleteDirectorRegionLinkWithSubRegion}")
    private String deleteDirectorRegionLinkWithSubRegionSql;

    @Value("${HeightenedMonitoringDaoImpl.findByIdPerson}")
    private String findByIdPersonSql;

    @Value("${HeightenedMonitoringDaoImpl.insertHmDirectorRegionLink}")
    private String insertHmDirectorRegionLinkSql;

    @Value("${HeightenedMonitoringDaoImpl.updateDefaultApproverForSubRegion}")
    private String updateDefaultApproverForSubRegionSql;

    @Value("${HeightenedMonitoringDaoImpl.checkResourceAssignedForAllStagesCount}")
    private String checkResourceAssignedForAllStagesCountSql;

    @Value("${HeightenedMonitoringDaoImpl.checkNarrativeCountSql}")
    private String checkNarrativeCountSql;

    @Value("${HeightenedMonitoringDaoImpl.getCaseworkerDetails}")
    private String getCaseworkerDetailsSql;

    @Value("${HeightenedMonitoringDaoImpl.getCaseworkerDetailsByApproval}")
    private String getCaseworkerDetailsByApprovalSql;

    @Value("${HeightenedMonitoringDaoImpl.getApproverDetails}")
    private String getApproverDetailsSql;

    @Value("${HeightenedMonitoringDaoImpl.getHmApproverEmailDetailByEvent}")
    private String getHmApproverEmailDetailByEventSql;

    @Value("${HeightenedMonitoringDaoImpl.getSvcAuthAmount}")
    private String getSvcAuthAmountByEventIdSql;

    @Value("${HeightenedMonitoringDaoImpl.checkPersonHasSvcAuthApprovalByIdPerson}")
    private String isValidSvcAuthApproverSql;

    @Value("${HeightenedMonitoringDaoImpl.checkApproverForSvcAuthAbove750}")
    private String isValidsvcAuthApproveAbove750Sql;

    @Autowired
    CaseSummaryDao caseSummaryDao;

    @Autowired
    PersonDao personDao;

    public static final Logger log = Logger.getLogger(HeightenedMonitoringDaoImpl.class);

    public static final Map<String, String> HM_DEFAULT_APPROVER_SUB = ImmutableMap.<String, String>builder()
            .put("E", "W")
            .put("W", "E")
            .put("A", "B")
            .put("B", "A")
            .build();

    // PPM 77728 Heightened Monitoring enhancements (duplicates WebConstants since DAO doesn't have access)
    public static final String HMSTATUS_ACTIVE = "Active";
    public static final String HMSTATUS_INACTIVE = "Inactive";
    private static final String STATUS_PROCESS = "PROC";
    private static final String STATUS_PEND = "PEND";
    public static final Long BATCH_USER = 999999996L;

    /**
     * PPM 60692-artf178535 - Changes for Facility Detail Notifications
     * Method Description: This method fetches all the resources info for the Resource whose HM status has been set
     * to active/inactive
     *
     * @param active
     * @return
     */
    @Override
    public List<ResourceDetailInDto> fetchResourcesInfo(Boolean active) {

        List<ResourceDetailInDto> resourceDetailInDtos = null;
        String sqlQuery = getCurrentActiveResourcesSql;

        if (!active) {
            sqlQuery = getCurrentInactiveResourcesSql;
        }

        resourceDetailInDtos = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery)
                .addScalar("idResource", StandardBasicTypes.LONG)
                .addScalar("nmResource", StandardBasicTypes.STRING)
                .addScalar("idHmStatus",StandardBasicTypes.LONG)
                .addScalar("dtRsrcEmailStart",StandardBasicTypes.DATE)
                .addScalar("dtRsrcEmailEnd",StandardBasicTypes.DATE)
                .setResultTransformer(Transformers.aliasToBean(ResourceDetailInDto.class)).list();

        return resourceDetailInDtos;
    }

    @Override
    public List<ResourceDetailInDto> fetchResourcesInfo(Long resourceId) {

        List<ResourceDetailInDto> resourceDetailInDtos = null;
        String sqlQuery = getResourceSql;

        resourceDetailInDtos = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery)
                .addScalar("idResource", StandardBasicTypes.LONG)
                .addScalar("nmResource", StandardBasicTypes.STRING)
                .addScalar("idHmStatus",StandardBasicTypes.LONG)
                .addScalar("dtRsrcEmailStart",StandardBasicTypes.DATE)
                .addScalar("dtRsrcEmailEnd",StandardBasicTypes.DATE)
                .setParameter("idResource", resourceId)
                .setResultTransformer(Transformers.aliasToBean(ResourceDetailInDto.class)).list();

        return resourceDetailInDtos;
    }

    /**
     * PPM 60692-artf178535 - Changes for Facility Detail Notifications
     * Method Description: This method fetches all the resources info for the Resource whose HM status has been set
     * to active/inactive
     *
     * @param idResources
     * @return
     */
    @Override
    public List<HeightenedMonitoringEmailDto> fetchPlacementStaffForResources(Long idResources) {

        return sessionFactory.getCurrentSession().createSQLQuery(getPlacementStaffResourcesSql)
                .addScalar("idResource", StandardBasicTypes.LONG)
                .addScalar("nmResource", StandardBasicTypes.STRING)
                .addScalar("idStage", StandardBasicTypes.LONG)
                .addScalar("idCase", StandardBasicTypes.LONG)
                .addScalar("nmStage", StandardBasicTypes.STRING)
                .addScalar("idPersonStaff", StandardBasicTypes.LONG)
                .addScalar("txtEmailStaff", StandardBasicTypes.STRING)
                .addScalar("idPersonSupervisor", StandardBasicTypes.LONG)
                .addScalar("txtEmailSupervisor", StandardBasicTypes.STRING)
                .setParameter("idResources", idResources)
                .setResultTransformer(Transformers.aliasToBean(HeightenedMonitoringEmailDto.class)).list();
    }

    /**
     * PPM 60692-artf178535 - Changes for Facility Detail Notifications
     * Method Description: This method updates the HM Request if the status is changed to inactive
     *
     * @param idResources
     */
    @Override
    public void updateInactiveResources(Long idResources) {
            Query hmRequestUpdateQuery = sessionFactory.getCurrentSession().createSQLQuery(updateInactiveResources)
                    .setParameter("idResources", idResources);
            hmRequestUpdateQuery.executeUpdate();
    }


    /**
     * This method will select Heightened Monitoring Request Event records for a give idStage.
     * PPM 60692-artf178536
     * @param idStage
     * @return List<EventListDto>
     */
    public List<EventListDto> getHmmRequestEventList(Long idStage){
        List<EventListDto> eventListDtos = sessionFactory.getCurrentSession().createSQLQuery(getHmmRequestEventListSql)
                .addScalar("idEvent", StandardBasicTypes.LONG)
                .addScalar("idStage", StandardBasicTypes.LONG)
                .addScalar("cdEventStatus")
                .addScalar("cdStage")
                .addScalar("nmPersonFull")
                .addScalar("eventDescr")
                .addScalar("dtEventCreated", StandardBasicTypes.TIMESTAMP)
                .addScalar("dtEventExpire", StandardBasicTypes.TIMESTAMP)
                .setParameter("idStage", idStage)
                .setResultTransformer(Transformers.aliasToBean(EventListDto.class))
                .list();

        return eventListDtos;
    }

    /**
     * This method will fetch Heightened Monitoring Request Sibling records for a give idStage.
     * PPM 60692-artf178536
     * @param idStage
     * @return List<EventListDto>
     */
    public List<HeightenedMonitoringSiblingBean> getHmmRequestSiblingList(Long idStage){
        List<HeightenedMonitoringSiblingBean> heightenedMonitoringSiblingBeans = sessionFactory.getCurrentSession().createSQLQuery(getHmmRequestSiblingListSql)
                .addScalar("cdStage")
                .addScalar("nmStage")
                .addScalar("idStage", StandardBasicTypes.LONG)
                .addScalar("indSexualAggrsnHist")
                .addScalar("indSexualVictmsnHist")
                .addScalar("idPerson", StandardBasicTypes.LONG)
                .addScalar("cdStagePersRole")
                .addScalar("cdStagePersRelInt")
                .setParameter("idStage", idStage)
                .setResultTransformer(Transformers.aliasToBean(HeightenedMonitoringSiblingBean.class))
                .list();
        return heightenedMonitoringSiblingBeans;
    }

    /**
     * This method will fetch Heightened Monitoring Request Sibling records for a give Stage ID and Case ID for Add.
     * PPM 60692-artf178536
     * @param idStage
     * @return List<EventListDto>
     */
    public List<HeightenedMonitoringSiblingBean> getHmmRequestSiblingList(Long idStage, Long idCase){
        List<HeightenedMonitoringSiblingBean> heightenedMonitoringSiblingBeans = sessionFactory.getCurrentSession().createSQLQuery(getHmmRequestSiblingLisForAddSql)
                .addScalar("cdStage")
                .addScalar("nmStage")
                .addScalar("idStage", StandardBasicTypes.LONG)
                .addScalar("indSexualAggrsnHist")
                .addScalar("indSexualVictmsnHist")
                .addScalar("idPerson", StandardBasicTypes.LONG)
                .addScalar("cdStagePersRole")
                .addScalar("cdStagePersRelInt")
                .setParameter("idStage", idStage)
                .setParameter("idCase", idCase)
                .setResultTransformer(Transformers.aliasToBean(HeightenedMonitoringSiblingBean.class))
                .list();
        return heightenedMonitoringSiblingBeans;
    }

    /**
     * This method will fetch Heightened Monitoring Request Sibling records for a give idStage.
     * PPM 60692-artf178536
     * @param idStage
     * @return List<EventListDto>
     */
    public String getHMMChildSexualVictimizationHistoryFlag(Long idStage) {
        String historyFlag = (String) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getHMMChildSexualVictimizationHistoryFlagSql)
                .setParameter("idStage", idStage))
                .addScalar("sxVctmztnHist").uniqueResult();
        return historyFlag;
    }

    /**
     * This method will fetch Heightened Monitoring Request  for a give idStage.
     * PPM 60692-artf178536
     * @param idStage
     * @return List<EventListDto>
     */
    public Boolean indHMMChildSexualAggressionHistoryExists(Long idStage) {
        Boolean indHMMChildSexualAggrHistExists = Boolean.FALSE;
        BigDecimal count = (BigDecimal)  ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(indHMMChildSexualAggressionHistoryExistsSql)
                .setParameter("idStage", idStage))
                .addScalar("cnt").uniqueResult();

        if (!TypeConvUtil.isNullOrEmpty(count) && count.intValue() > ServiceConstants.Zero) {
            indHMMChildSexualAggrHistExists = Boolean.TRUE;
        }
        return indHMMChildSexualAggrHistExists;
    }

    /**
     * This method will fetch Heightened Monitoring Request Child's Legal Region for a give idStage.
     * PPM 60692-artf178536
     * @param idStage
     * @return String
     */
    public String getHMMChildLegalRegion(Long idStage) {
        String county = (String) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getHMMChildLegalRegionSql)
                .setParameter("idStage", idStage))
                .addScalar("legalStatusCounty").uniqueResult();
        return caseSummaryDao.getRegionByCounty(county);
    }

    /**
     * PPM 60692-artf178536- Heightened Monitoring Request Detail
     * @param hmRequest
     * @return void
     */
    @Override
    public void saveOrUpdateHMRequest(HMRequest hmRequest){
        sessionFactory.getCurrentSession().saveOrUpdate(hmRequest);
    }

    @Override
    public void deleteHMRequest(HMRequest hmRequest) {
        Long idEvent = hmRequest.getIdEvent().getIdEvent();
        HmReqNarr hmReqNarr = (HmReqNarr) sessionFactory.getCurrentSession().createCriteria(HmReqNarr.class).add(Restrictions.eq("event.idEvent", idEvent))
                .uniqueResult();
        if(null != hmReqNarr) {
            sessionFactory.getCurrentSession().delete(hmReqNarr);
        }
        sessionFactory.getCurrentSession().delete(hmRequest);
    }

    /**
     * This method will select all HM resource status records for a give resourceId.
     * PPM 60692-artf178534
     * @param idResource
     * @return HeightenedMonitoringDto
     */
    @Override
    public List<HeightenedMonitoringDto> getHmStatusDetails(Long idResource) {
        List<HeightenedMonitoringDto> hmDtoList = new ArrayList<>();
        Query query = sessionFactory.getCurrentSession().createSQLQuery(getHmStatusDetailsSql)
                .addScalar("dtStart", StandardBasicTypes.DATE)
                .addScalar("dtEnd", StandardBasicTypes.DATE)
                .addScalar("cdSendEmailOnComp", StandardBasicTypes.STRING)
                .addScalar("staffName", StandardBasicTypes.STRING)
                .addScalar("createdStaffName", StandardBasicTypes.STRING)
                .setParameter("idResource", idResource)
                .setResultTransformer(Transformers.aliasToBean(HeightenedMonitoringDto.class));

        hmDtoList = query.list();
        return hmDtoList;
    }

    /**
     * This method will save or update HM resource status records for a give resourceId.
     * PPM 60692-artf178534. If we update an existing, we return the ID so we can updae it
     * again later if needed. If the resource status ID is passed, it is used to find the
     * resource status to be updated.
     * @param facilityDetailSaveReq
     */
    @Override
    public Long updateFacilityDetailsHmStatus(FacilityDetailSaveReq facilityDetailSaveReq) {
        Long hmResourceStatusId = null;
        Criteria crCapsResource = sessionFactory.getCurrentSession().createCriteria(CapsResource.class)
                .add(Restrictions.eq("idResource", facilityDetailSaveReq.getIdResource()));
        CapsResource capsResource = (CapsResource) crCapsResource.uniqueResult();

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(HMResourceStatus.class);
        criteria.add(Restrictions.eq("capsResource.idResource", facilityDetailSaveReq.getIdResource()));
        if (facilityDetailSaveReq.getIdHmResourceStatus() == null) {
            Criterion orCond1 = Restrictions.isNull("dtEnd");
            Criterion orCond2 = Restrictions.eq("dtEnd", DateUtils.getDefaultFutureDate());
            criteria.add(Restrictions.or(orCond1, orCond2));
        } else {
            Criterion idHmStatusCond = Restrictions.eq("idHmStatus", facilityDetailSaveReq.getIdHmResourceStatus());
            criteria.add(idHmStatusCond);
        }
        HMResourceStatus hmResourceStatus = (HMResourceStatus) criteria.uniqueResult();
        if (null != hmResourceStatus) {
            hmResourceStatusId = hmResourceStatus.getIdHmStatus();
            // If HM is Inactive and we're updating an existing record, then we've saved the status once and are
            // updating it with email success or fail. This update should not end HM.
            if (HMSTATUS_INACTIVE.equals(facilityDetailSaveReq.getMonitoringStatus())) {
                hmResourceStatus.setDtEnd(null);
            } else {
                hmResourceStatus.setDtEnd(facilityDetailSaveReq.getEndDate());
            }
            hmResourceStatus.setDtLastUpdated(new Date());
            hmResourceStatus.setIdLastUpdatedPerson(facilityDetailSaveReq.getUserID());
            hmResourceStatus.setCdSendEmailOnComp(facilityDetailSaveReq.getCdSendEmailOnComp());
            hmResourceStatus.setCdSendEmailStatus(facilityDetailSaveReq.getCdSendEmailStatus());
            sessionFactory.getCurrentSession().update(hmResourceStatus);
        } else {
            HMResourceStatus newHmResourceStatus = new HMResourceStatus();
            newHmResourceStatus.setCapsResource(capsResource);
            newHmResourceStatus.setDtStart(DateUtils.addToDate(facilityDetailSaveReq.getEndDate(), 0, 0, 1));
            newHmResourceStatus.setDtCreated(new Date());
            newHmResourceStatus.setIdCreatedPerson(facilityDetailSaveReq.getUserID());
            newHmResourceStatus.setDtLastUpdated(new Date());
            newHmResourceStatus.setIdLastUpdatedPerson(facilityDetailSaveReq.getUserID());
            newHmResourceStatus.setCdSendEmailStatus(facilityDetailSaveReq.getCdSendEmailStatus());
            sessionFactory.getCurrentSession().save(newHmResourceStatus);
        }
        return hmResourceStatusId;
    }

	/**
     * PPM 60692-artf178535 - Changes for Facility Detail Notifications
     * Method Description: This method retrieves the Regional Directors info
     *
     */
    @Override
    public List<HMStatusNotifEmail> fetchRegionalDirectorsInfo() {

        Criteria cr = sessionFactory.getCurrentSession().createCriteria(HMStatusNotifEmail.class)
                .add(Restrictions.eq("cdStaffType", "RD"));

        List<HMStatusNotifEmail> hmStatusNotifEmailList = cr.list();

        return hmStatusNotifEmailList;
    }
    /**
     * This method will fetch facility type and parent resource for a give child resourceId.
     * PPM 60692-artf178534
     * @param idResource
     */
    @Override
    public HeightenedMonitoringDto getFacilTypAndParentRsrc(Long idResource) {
        HeightenedMonitoringDto hmDto = null;
        Query query = sessionFactory.getCurrentSession().createSQLQuery(getFacilTypAndParentRsrcSql)
                .addScalar("facilTyp", StandardBasicTypes.STRING)
                .addScalar("parentId", StandardBasicTypes.LONG)
                .setParameter("idResource", idResource)
                .setResultTransformer(Transformers.aliasToBean(HeightenedMonitoringDto.class));

        hmDto = (HeightenedMonitoringDto) query.uniqueResult();
        return hmDto;
    }

    /**
     * PPM 60692-artf179568- Heightened Monitoring Case List
     * Method Description: This method retrieves all the Heightened Monitoring Request for the id case
     *
     * @param idCase
     *
     * @return List<EventListDto>
     */
    @Override
    public List<EventListDto> getHmmCaseList(Long idCase) {
        List<EventListDto> eventListDtos = sessionFactory.getCurrentSession().createSQLQuery(getHmmCaseListSql)
                .addScalar("idEvent", StandardBasicTypes.LONG)
                .addScalar("idStage", StandardBasicTypes.LONG)
                .addScalar("cdEventStatus")
                .addScalar("cdStage")
                .addScalar("nmPersonFull")
                .addScalar("eventDescr").addScalar("cdEventType")
                .addScalar("dtEventCreated", StandardBasicTypes.TIMESTAMP)
                .addScalar("dtEventExpire", StandardBasicTypes.TIMESTAMP)
                .setParameter("idCase", idCase)
                .setResultTransformer(Transformers.aliasToBean(EventListDto.class))
                .list();

        return eventListDtos;
    }

    /**
     * PPM 60692-artf179570 - Placement Page changes for HM Resource
     * Method Description: This method checks the HM status for the given Resource ID on
     * Placement Creation Date
     *
     * @param idResource
     * @return
     */
    @Override
    public Boolean checkHMStatusActive(Long idResource) {

        boolean active = false;

        Query query = sessionFactory.getCurrentSession().createSQLQuery(checkHMStatusActiveSql)
                .addScalar("idResource", StandardBasicTypes.LONG)
                .addScalar("dtStart", StandardBasicTypes.DATE)
                .addScalar("dtEnd", StandardBasicTypes.DATE)
                .setParameter("idResource", idResource)
                .setResultTransformer(Transformers.aliasToBean(HeightenedMonitoringDto.class));

        HeightenedMonitoringDto heightenedMonitoringDto = (HeightenedMonitoringDto) query.uniqueResult();

        if (!ObjectUtils.isEmpty(heightenedMonitoringDto)) {
            active = true;
        }

        return active;
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

        boolean valid = false;
        String sqlQuery = validateNonEmergencyPlcmtSql;

        if (ServiceConstants.STRING_IND_Y.equals(indEmergency)) {
            sqlQuery = validateEmergencyPlcmtSql;
        }

        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery)
                .addScalar("idResource", StandardBasicTypes.LONG)
                .setParameter("idResource", idResource)
                .setParameter("dtPlcmtStart", dtPlcmtStart)
                .setParameter("idStage", idStage);

        Long retIdResource = (Long) query.uniqueResult();
        if (!ObjectUtils.isEmpty(retIdResource) && retIdResource.intValue() > ServiceConstants.Zero) {
            valid = true;
        }

        return valid;
    }

    /**
     * PPM 60692-artf179569 - Get HMRequest and Child by using Event ID.
     * @param idEvent
     * @return HMRequest
     */
    @Override
    public HMRequest getHmRequestByEventId(Long idEvent) {
        Criteria cr = sessionFactory.getCurrentSession().createCriteria(HMRequest.class)
                .add(Restrictions.eq("idEvent.idEvent", idEvent));
        return (HMRequest) cr.uniqueResult();
    }

    /**
     * PPM 60692-artf179778 - Approve page changes
     * Method Description: This method populates the Placement event id which utilizes the HM request
     *
     * @param idStage
     * @param idApproval
     * @return
     */
    @Override
    public void updatePlacementEventToHmReq(Long idStage, Long idApproval) {

        Query hmRequestUpdateQuery = sessionFactory.getCurrentSession().createSQLQuery(updatePlacementEventToHMSql)
                    .setParameter("idStage", idStage)
                    .setParameter("idApproval", idApproval);

        hmRequestUpdateQuery.executeUpdate();

    }

    /**
     * PPM 60692-artf179777 - To Do page changes
     * Method Description: This method fetches the Approver for the To Do Detail
     *
     * @param legalRegion
     * @return
     */
    @Override
    public AssignedDto fetchApproverForHmToDo(String legalRegion, boolean isSecondary) {

        String querySql = fetchRegionalDirectorByRegionSql;
        String role="RD";
        AssignedDto assignedDto =null;
        Query query = sessionFactory.getCurrentSession().createSQLQuery(querySql)
                .addScalar("idTodoPersAssigned", StandardBasicTypes.LONG)
                .addScalar("nmPersonFull", StandardBasicTypes.STRING)
                .setResultTransformer(Transformers.aliasToBean(AssignedDto.class));

        if (!isSecondary) {
            query.setParameter("legalRegion", legalRegion);
            assignedDto = (AssignedDto) query.uniqueResult();
            if(assignedDto == null) {
                role="DL";
                assignedDto =  fetchHigherRoleApproverForHmToDo(role);
                if(assignedDto == null) {
                    role="AC";
                    assignedDto =  fetchHigherRoleApproverForHmToDo(role);
                }
            }
        }else {
            role="DL";
            assignedDto =  fetchHigherRoleApproverForHmToDo(role);
            if(assignedDto == null) {
                role="AC";
                assignedDto =  fetchHigherRoleApproverForHmToDo(role);
            }
        }
        if(assignedDto !=null)
        assignedDto.setRole(role);
        return assignedDto;
    }


    private AssignedDto fetchHigherRoleApproverForHmToDo(String role) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery(fetchToDoSecondaryApproverSql)
                .addScalar("idTodoPersAssigned", StandardBasicTypes.LONG)
                .addScalar("nmPersonFull", StandardBasicTypes.STRING)
                .setResultTransformer(Transformers.aliasToBean(AssignedDto.class));
        query.setParameter("role", role);
       return (AssignedDto) query.uniqueResult();
    }
    /**
     * PPM 60692-artf179777 - To Do page changes
     * Method Description: This method validates the assigned Approver for To Do
     *
     * @param idPerson
     * @param legalRegion
     * @param isSecondary
     * @param idStage
     * @return
     */
    @Override
    public Boolean validateHmToDoApprover(Long idPerson, String legalRegion, boolean isSecondary, Long idStage) {

        String querySql = validateToDoApproverSql;

        if (isSecondary) {
            querySql = validateToDoSecondaryApproverSql;
        }

        Boolean valid = Boolean.FALSE;
        Query query = sessionFactory.getCurrentSession().createSQLQuery(querySql)
                .addScalar("recExists", StandardBasicTypes.LONG)
                .setParameter("idPerson", idPerson);

        if (!isSecondary) {
            if (ObjectUtils.isEmpty(legalRegion)) {
                legalRegion = getHMMChildLegalRegion(idStage);
            }
            query.setParameter("legalRegion", legalRegion);
        }

        Long recExists = (Long) query.uniqueResult();

        if (!ObjectUtils.isEmpty(recExists) && recExists.intValue() > ServiceConstants.Zero) {
            valid = true;
        }
        return valid;
    }

    /**
     * PPM 60692-artf179777 - To Do page changes
     * Method Description: This method validates the assigned approver for To Do
     *
     * @param idApproval
     * @return
     */
    @Override
    public HMApproverEmailDto fetchHmApproverEmailDetails(Long idApproval) {

        Query query = sessionFactory.getCurrentSession().createSQLQuery(getHmApproverEmailDetailSql)
                .addScalar("idResource", StandardBasicTypes.LONG)
                .addScalar("nmResource", StandardBasicTypes.STRING)
                .addScalar("idPersonChild", StandardBasicTypes.LONG)
                .addScalar("nmChild", StandardBasicTypes.STRING)
                .addScalar("dtPlacementStart", StandardBasicTypes.DATE)
                .addScalar("idStage", StandardBasicTypes.LONG)
                .addScalar("cdEventStatus", StandardBasicTypes.STRING)
                .addScalar("idCase", StandardBasicTypes.LONG)
                .setParameter("idApproval", idApproval)
                .setResultTransformer(Transformers.aliasToBean(HMApproverEmailDto.class));

        return (HMApproverEmailDto) query.uniqueResult();
    }

    /**
     * PPM 60692-artf179777 - To Do page changes
     * Method Description: This method fetches the available Siblings on HM Request
     *
     * @param idApproval
     * @return
     */
    @Override
    public List<String> fetchSiblingsOnHmRequest(Long idApproval) {

        return sessionFactory.getCurrentSession().createSQLQuery(getSiblingsOnHmRequestSql)
                .addScalar("nmPersonFull", StandardBasicTypes.STRING)
                .setParameter("idApproval", idApproval)
                .list();
    }

    /**
     * PPM 60692-artf179778 - Maintain Heightened Monitoring Approver changes
     * Method Description: This method fetches the Approver List
     *
     * @return
     */
    @Override
    public List<HMApproverListDto> fetchApproversList() {

        Query query = sessionFactory.getCurrentSession().createSQLQuery(getApproversListSql)
                .addScalar("idApprover", StandardBasicTypes.LONG)
                .addScalar("nmApprover", StandardBasicTypes.STRING)
                .addScalar("cdRegionTitle", StandardBasicTypes.STRING)
                .addScalar("indDefault", StandardBasicTypes.STRING)
                .addScalar("txtApproverEmail", StandardBasicTypes.STRING)
                .addScalar("indActive", StandardBasicTypes.STRING)
                .setResultTransformer(Transformers.aliasToBean(HMApproverListDto.class));

        return query.list();
    }

    /**
     * PPM 60692-artf179778 - Maintain Heightened Monitoring Approver changes
     * Method Description: This method deletes the approver
     *
     * @param idApprover
     * @param cdRegDirectorRegion
     * @param cdSubRegion
     * @return
     */
    @Override
    public void deleteApprover(Long idApprover, String cdRegDirectorRegion, String cdSubRegion) {


        if (!NumberUtils.isNumber(cdRegDirectorRegion)) {
            sessionFactory.getCurrentSession().createSQLQuery(deleteDLACSql)
                    .setParameter("idPerson", idApprover)
                    .setParameter("cdStaffType", cdRegDirectorRegion).executeUpdate();
        } else {

            Long approverCount = (Long) sessionFactory.getCurrentSession().createSQLQuery(checkApproverCountSql)
                    .addScalar("count", StandardBasicTypes.LONG)
                    .setParameter("idApprover", idApprover)
                    .uniqueResult();

            String sqlQuery = deleteDirectorRegionLinkSql;

            if (!ObjectUtils.isEmpty(cdSubRegion)) {
                sqlQuery = deleteDirectorRegionLinkWithSubRegionSql;
            }

            Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery)
                    .setParameter("cdRegion", cdRegDirectorRegion);

            if (!ObjectUtils.isEmpty(cdSubRegion)) {
                query.setParameter("cdSubRegion", cdSubRegion);
            }

            query.executeUpdate();

            if (approverCount == 1) {
                sessionFactory.getCurrentSession().flush();

                sessionFactory.getCurrentSession().createSQLQuery(deleteRDSql)
                        .setParameter("idPerson", idApprover).executeUpdate();
            }
            if (!ObjectUtils.isEmpty(cdSubRegion)) {
                HMDirectorRegionLink regionLinkEntity = (HMDirectorRegionLink) sessionFactory.getCurrentSession().createCriteria(HMDirectorRegionLink.class)
                        .add(Restrictions.eq("cdRegDirectorRegion",cdRegDirectorRegion))
                        .add(Restrictions.eq("cdSubRegion",HM_DEFAULT_APPROVER_SUB.get(cdSubRegion)))
                        .add(Restrictions.eq("indDefaultAssignRegion","N"))
                        .uniqueResult();
                if(regionLinkEntity!=null){
                    regionLinkEntity.setIndDefaultAssignRegion("Y");
                    sessionFactory.getCurrentSession().update(regionLinkEntity);
                }

            }
        }
    }

    /**
     * PPM 60692-artf179778 - Maintain Heightened Monitoring Approver changes
     * Method Description: This method persists the new approver
     *
     * @param idApprover
     * @param cdRegDirectorRegion
     * @param cdSubRegion
     * @param txtEmailAddress
     * @param indDefaultApprover
     * @return
     */
    @Override
    public void saveNewApprover(Long idApprover, String cdRegDirectorRegion, String cdSubRegion, String txtEmailAddress,
                         String indDefaultApprover) {

        Long idHmStatusNotifEmail = (Long) sessionFactory.getCurrentSession().createSQLQuery(findByIdPersonSql)
                .addScalar("idHmStatus", StandardBasicTypes.LONG)
                .setParameter("idApprover", idApprover)
                .uniqueResult();

        if (ObjectUtils.isEmpty(idHmStatusNotifEmail) || idHmStatusNotifEmail == 0) {
            HMStatusNotifEmail hmStatusNotifEmail = new HMStatusNotifEmail();
            hmStatusNotifEmail.setPerson(personDao.getPerson(idApprover));
            hmStatusNotifEmail.setCdStaffType(!NumberUtils.isNumber(cdRegDirectorRegion) ? cdRegDirectorRegion : "RD");
            hmStatusNotifEmail.setTxtEmailAddress(txtEmailAddress);
            hmStatusNotifEmail.setDtCreated(new Date());
            hmStatusNotifEmail.setDtLastUpdated(new Date());

            sessionFactory.getCurrentSession().saveOrUpdate(hmStatusNotifEmail);
            idHmStatusNotifEmail = hmStatusNotifEmail.getIdHmStatusNotifEmail();
        }

        if (NumberUtils.isNumber(cdRegDirectorRegion)) {
            sessionFactory.getCurrentSession().flush();

            sessionFactory.getCurrentSession().createSQLQuery(insertHmDirectorRegionLinkSql)
                    .setParameter("idHmStatusNotifEmail", idHmStatusNotifEmail)
                    .setParameter("cdRegDirectorRegion", cdRegDirectorRegion)
                    .setParameter("cdSubRegion", cdSubRegion)
                    .setParameter("indDefaultApprover", indDefaultApprover)
                    .executeUpdate();
        }
    }

    /**
     * PPM 60692-artf179778 - Maintain Heightened Monitoring Approver changes
     * Method Description: This method retrieves the approver count
     *
     * @return
     */
    @Override
    public void updateDefaultApproverForSubRegion(String cdRegDirectorRegion, String cdSubRegion, String indDefault) {

        sessionFactory.getCurrentSession().createSQLQuery(updateDefaultApproverForSubRegionSql)
                .setParameter("cdRegDirectorRegion", cdRegDirectorRegion)
                .setParameter("cdSubRegion", cdSubRegion)
                .setParameter("indDefault", indDefault)
                .executeUpdate();
    }

    /**
     * PPM 60692-artf179569 - Heightened Monitoring Request Check
     * Method Description: Check for Resource already assigned, expired, status for all given stage id's
     *
     * @param idStage
     * @return Boolean
     */
    @Override
    public Boolean checkResourceAssignedForAllStagesCountSql(Long idRsrcFacil, Set<Long> idStage) {
        Long count = (Long)  ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(checkResourceAssignedForAllStagesCountSql)
                .setParameterList("idStage", idStage)
                .setParameter("idRsrcFacil", idRsrcFacil))
                .addScalar("resourceCount", StandardBasicTypes.LONG).uniqueResult();
        return count.intValue() > 0;
    }

    /**
     * PPM 60692-artf179569 - Heightened Monitoring Narrative check exits
     * @param idEvent
     * @return Boolean
     */
    public Boolean isDocExists(Long idEvent){
        Long count = (Long)  ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(checkNarrativeCountSql)
                .setParameter("idEvent", idEvent))
                .addScalar("narrativeCount", StandardBasicTypes.LONG).uniqueResult();
        return count.intValue() > 0;
    }

    /**
     * PPM 60692-artf197133-DEV PCR Approver Email changes
     * Method Description: This method retrieves the Caseworker Details based on the User Id
     *
     * @param idStage
     * @param idUser
     * @return
     */
    @Override
    public List<EmailDetailDto> getCaseworkerDetails(Long idStage, Long idUser) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery(getCaseworkerDetailsSql)
                .addScalar("idPerson", StandardBasicTypes.LONG)
                .addScalar("email", StandardBasicTypes.STRING)
                .setParameter("idStage", idStage)
                .setParameter("idUser", idUser)
                .setResultTransformer(Transformers.aliasToBean(EmailDetailDto.class));

        return query.list();
    }

    /**
     * PPM 60692-artf197133-DEV PCR Approver Email changes
     * Method Description: This method retrieves the Caseworker Details based on the Approval ID
     *
     * @param idStage
     * @param idApproval
     * @return
     */
    @Override
    public List<EmailDetailDto> getCaseworkerDetailsByApproval(Long idStage, Long idApproval) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery(getCaseworkerDetailsByApprovalSql)
                .addScalar("idPerson", StandardBasicTypes.LONG)
                .addScalar("email", StandardBasicTypes.STRING)
                .setParameter("idStage", idStage)
                .setParameter("idApproval", idApproval)
                .setResultTransformer(Transformers.aliasToBean(EmailDetailDto.class));

        return query.list();
    }

    /**
     * PPM 60692-artf197133-DEV PCR Approver Email changes
     * Method Description: This method retrieves the Approver Details for the given Approval ID
     *
     * @param idApproval
     * @return
     */
    @Override
    public List<EmailDetailDto> getApproverDetails(Long idApproval) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery(getApproverDetailsSql)
                .addScalar("idPerson", StandardBasicTypes.LONG)
                .addScalar("email", StandardBasicTypes.STRING)
                .addScalar("comments", StandardBasicTypes.STRING)
                .setParameter("idApproval", idApproval)
                .setResultTransformer(Transformers.aliasToBean(EmailDetailDto.class));

        return query.list();
    }

    /**
     * PPM 60692-artf197133-DEV PCR Approver Email changes
     * Method Description: This method retrieves the Resource Details based on the Event
     *
     * @param idEvent
     * @return
     */
    @Override
    public HMApproverEmailDto fetchHmApproverEmailDetailsByEvent(Long idEvent) {

        Query query = sessionFactory.getCurrentSession().createSQLQuery(getHmApproverEmailDetailByEventSql)
                .addScalar("idResource", StandardBasicTypes.LONG)
                .addScalar("nmResource", StandardBasicTypes.STRING)
                .addScalar("idPersonChild", StandardBasicTypes.LONG)
                .addScalar("nmChild", StandardBasicTypes.STRING)
                .addScalar("dtPlacementStart", StandardBasicTypes.DATE)
                .addScalar("idStage", StandardBasicTypes.LONG)
                .addScalar("cdEventStatus", StandardBasicTypes.STRING)
                .addScalar("idCase", StandardBasicTypes.LONG)
                .setParameter("idEvent", idEvent)
                .setResultTransformer(Transformers.aliasToBean(HMApproverEmailDto.class));

        return (HMApproverEmailDto) query.uniqueResult();
    }

    /**
     * PPM 60692-artf204559 - DEV PCR 045 Mitigate HM Vacant Approver Roles
     * Method Description: This method updates the approving status for the Approver
     *
     * @param idApprover
     * @param cdRegDirectorRegion
     * @param cdSubRegion
     * @param indActive
     *
     */
    @Override
    public void updateApprover(Long idApprover, String cdRegDirectorRegion, String cdSubRegion, String indActive) {

        Date dtEnd = ServiceConstants.Y.equals(indActive) ? null : new Date();

        if (!NumberUtils.isNumber(cdRegDirectorRegion)) {
            HMStatusNotifEmail hmStatusNotifEmail = (HMStatusNotifEmail) sessionFactory.getCurrentSession()
                    .createCriteria(HMStatusNotifEmail.class, "hmStatusNotifEmail")
                    .createAlias("hmStatusNotifEmail.person", "person")
                    .add(Restrictions.eq("person.idPerson", idApprover))
                    .add(Restrictions.eq("hmStatusNotifEmail.cdStaffType", cdRegDirectorRegion))
                    .uniqueResult();

            if (!ObjectUtils.isEmpty(hmStatusNotifEmail)) {
                hmStatusNotifEmail.setDtEnd(dtEnd);
                sessionFactory.getCurrentSession().saveOrUpdate(hmStatusNotifEmail);
            }
        } else {

            HMDirectorRegionLink hmDirectorRegionLink = (HMDirectorRegionLink) sessionFactory.getCurrentSession()
                    .createCriteria(HMDirectorRegionLink.class)
                    .add(Restrictions.eq("cdRegDirectorRegion", cdRegDirectorRegion))
                    .add(!ObjectUtils.isEmpty(cdSubRegion) ? Restrictions.eq("cdSubRegion", cdSubRegion)
                            : Restrictions.isNull("cdSubRegion"))
                    .uniqueResult();

            if (!ObjectUtils.isEmpty(hmDirectorRegionLink)) {
                hmDirectorRegionLink.setDtEnd(dtEnd);
                sessionFactory.getCurrentSession().saveOrUpdate(hmDirectorRegionLink);
            }
        }
    }

    /**
     * PPM 60692-artf204559 - DEV PCR 045 Mitigate HM Vacant Approver Roles
     * Method Description: This method updates the approving status for the Approver
     *
     * @param idApprover
     * @param cdRegDirectorRegion
     *
     */
    @Override
    public Boolean verifyApproverActiveStatus(Long idApprover, String cdRegDirectorRegion) {

        boolean result = false;

        HMStatusNotifEmail hmStatusNotifEmail = (HMStatusNotifEmail) sessionFactory.getCurrentSession()
                .createCriteria(HMStatusNotifEmail.class, "hmStatusNotifEmail")
                .createAlias("hmStatusNotifEmail.person", "person")
                .add(Restrictions.eq("person.idPerson", idApprover))
                .uniqueResult();

        if (!ObjectUtils.isEmpty(hmStatusNotifEmail)) {

            if (!CollectionUtils.isEmpty(hmStatusNotifEmail.getHmDirectorRegionLinks())) {

                result = hmStatusNotifEmail.getHmDirectorRegionLinks()
                        .stream()
                        .anyMatch(link -> cdRegDirectorRegion.equals(link.getCdRegDirectorRegion())
                                && ObjectUtils.isEmpty(link.getDtEnd()));

            } else if (!"RD".equals(hmStatusNotifEmail.getCdStaffType())) {
                result = ObjectUtils.isEmpty(hmStatusNotifEmail.getDtEnd());
            }
        }

        return result;
    }

    /**
     * PPM 77728 - artf242940 - Heightened Monitoring
     * Locks row by idHmStatus
     * @param idHmStatus
     * @return
     */
    @Override
    public boolean lockAndUpdateHmResourceStatus(Long idHmStatus) {
        try {
            log.info("Inside lockAndUpdateHmResourceStatus for id - "+idHmStatus);
            LockOptions lockOption = new LockOptions(LockMode.PESSIMISTIC_WRITE);
            HMResourceStatus hMResourceStatus = (HMResourceStatus) sessionFactory.getCurrentSession().get(HMResourceStatus.class, idHmStatus, lockOption.setTimeOut(0));
            if(STATUS_PEND.equalsIgnoreCase(hMResourceStatus.getCdSendEmailStatus()) ){
                hMResourceStatus.setCdSendEmailStatus(STATUS_PROCESS);
                hMResourceStatus.setIdLastUpdatedPerson(BATCH_USER);
                sessionFactory.getCurrentSession().update(hMResourceStatus);
                return true;
            }
            return false;
        } catch(org.hibernate.exception.LockTimeoutException e){
            log.error("HM LockTimeoutException for id - "+idHmStatus);
            return false;
        }  catch(Exception e){
            log.error("HM Exception for id - "+idHmStatus,e);
            return false;
        }
    }

    /**
     * PPM 77728 - artf242940 - Heightened Monitoring
     * @param idHmStatus
     * @param status
     */
    @Override
    public void updateHmResourceStatus(Long idHmStatus, String status) {
        HMResourceStatus hMResourceStatus = (HMResourceStatus) sessionFactory.getCurrentSession().get(HMResourceStatus.class, idHmStatus);
        hMResourceStatus.setCdSendEmailStatus(status);
        hMResourceStatus.setIdLastUpdatedPerson(BATCH_USER);
        sessionFactory.getCurrentSession().update(hMResourceStatus);
    }
    /**
     *
     * @param idPerson
     * @return
     *Checking the selected person has Service Authorziation approveal
     */
    @Override
    public boolean isValidSvcAuthApprover(Long idPerson) {

        // artf282254 - PPM 89165 Modified query to add the Job Code 5018A
        Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(isValidSvcAuthApproverSql)
                .setParameter("idPerson", idPerson);
        Long count;
        count = ((BigDecimal) query.uniqueResult()).longValue();
        return (count > 0) ? ServiceConstants.TRUEVAL : ServiceConstants.FALSEVAL;
    }

    /**
     *
     * @param idPerson
     * @return
     * Checking the selected person has Service Authorziation approveal for amount more than 750
     */
    @Override
    public boolean isValidSvcAuthApproverAbove750(Long idPerson) {
        Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(isValidsvcAuthApproveAbove750Sql)
                .setParameter("idPerson", idPerson);
        Long count;
        count = ((BigDecimal) query.uniqueResult()).longValue();
        return (count > 0) ? ServiceConstants.TRUEVAL : ServiceConstants.FALSEVAL;
    }

    /**
     *
     * @param idEvent
     * @return
     * querying for the Service authorization amount data
     */
    @Override
    public double getSvcAuthAmountByEventId(Long idEvent) {
        Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getSvcAuthAmountByEventIdSql)
                .setParameter("idEvent", idEvent);
        return ((BigDecimal) query.uniqueResult()).doubleValue();

    }

    @Override
    public HMRequest getHMRequestByResourseIdAndStageId(Long idStage, Long idResource) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(HMRequest.class, "hmRequest")
                .createAlias("hmRequest.idEvent", "event")
                .createAlias("event.stage", "stage")
                .createAlias("hmRequest.capsResourceByIdRsrcFacil", "resource")
                .add(Restrictions.eq("resource.idResource",idResource))
                .add(Restrictions.eq("stage.idStage", idStage))
                .add(Restrictions.eq("event.cdEventStatus",ServiceConstants.APPROVAL))
                .addOrder(Order.desc("hmRequest.hmRequestId"))
                .setMaxResults(1);
        return (HMRequest) criteria.uniqueResult();
    }

    @Override
    public HmReqNarr findHmReqNarrByIdEvent(Long event) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(HmReqNarr.class, "hmReqNarr")
                .createAlias("hmReqNarr.event", "event")
                .add(Restrictions.eq("event.idEvent",event));
        return (HmReqNarr) criteria.uniqueResult();
    }

    @Override
    public void saveOrUpdateHMReqNarrative(HmReqNarr hmReqNarr){
        sessionFactory.getCurrentSession().saveOrUpdate(hmReqNarr);
    }

}
