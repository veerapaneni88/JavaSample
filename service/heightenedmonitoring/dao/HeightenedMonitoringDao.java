package us.tx.state.dfps.service.heightenedmonitoring.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import us.tx.state.dfps.common.domain.HMRequest;
import us.tx.state.dfps.common.domain.HMStatusNotifEmail;
import us.tx.state.dfps.common.domain.HmReqNarr;
import us.tx.state.dfps.common.dto.EmailDetailDto;
import us.tx.state.dfps.service.common.request.FacilityDetailSaveReq;
import us.tx.state.dfps.service.hmm.dto.HMApproverEmailDto;
import us.tx.state.dfps.service.hmm.dto.HMApproverListDto;
import us.tx.state.dfps.service.hmm.dto.HeightenedMonitoringDto;
import us.tx.state.dfps.service.hmm.dto.HeightenedMonitoringEmailDto;
import us.tx.state.dfps.service.resource.detail.dto.ResourceDetailInDto;
import us.tx.state.dfps.service.workload.dto.AssignedDto;
import us.tx.state.dfps.service.workload.dto.EventListDto;
import us.tx.state.dfps.web.placement.bean.HeightenedMonitoringSiblingBean;

public interface HeightenedMonitoringDao {

    /**
     * PPM 60692-artf178535 - Changes for Facility Detail Notifications
     * Method Description: This method fetches all the resources info for the Resource whose HM status has been set
     * to active/inactive
     *
     * @param active
     * @return
     */
    List<ResourceDetailInDto> fetchResourcesInfo(Boolean active);

    /**
     * PPM 60692-artf178535 - Changes for Facility Detail Notifications
     * Method Description: This method fetches all the resources info for the Resource whose HM status has been set
     * to active/inactive
     *
     * @param idResources
     * @return
     */
    List<HeightenedMonitoringEmailDto> fetchPlacementStaffForResources(Long idResources);

    /**
     * PPM 60692-artf178535 - Changes for Facility Detail Notifications
     * Method Description: This method updates the HM Request if the status is changed to inactive
     *
     * @param idResources
     */
    void updateInactiveResources(Long idResources);

    /**
     * PPM 60692-artf178536- Heightened Monitoring Request Event List
     * @param idStage
     * @return HeightenedMonitoringDto
     */
    List<EventListDto> getHmmRequestEventList(Long idStage);

    /**
     * PPM 60692-artf179569 - Heightened Monitoring Request Info Sibling List
     * @param idStage
     * @return HeightenedMonitoringSiblingBean
     */
    List<HeightenedMonitoringSiblingBean> getHmmRequestSiblingList(Long idStage);

    /**
     * PPM 60692-artf179569 - Heightened Monitoring Request Info Sibling List
     * @param idStage
     * @return HeightenedMonitoringSiblingBean
     */
    List<HeightenedMonitoringSiblingBean> getHmmRequestSiblingList(Long idStage, Long idCase);

    /**
     * PPM 60692-artf179569 - Heightened Monitoring Child Sexual Victimization History Flag
     * @param idStage
     * @return HeightenedMonitoringSiblingBean
     */
    String getHMMChildSexualVictimizationHistoryFlag(Long idStage);

    /**
     * PPM 60692-artf179569 - Heightened Monitoring Child Sexual Aggression History Flag
     * @param idStage
     * @return Boolean
     */
    Boolean indHMMChildSexualAggressionHistoryExists(Long idStage);

    /**
     * PPM 60692-artf179569 - Heightened Monitoring Child's Legal Region value
     * @param idStage
     * @return String
     */
    String getHMMChildLegalRegion(Long idStage);

    /**
     * PPM 60692-artf179569 - Get HMRequest by using Event ID.
     * @param idEvent
     * @return HMRequest
     */
    HMRequest getHmRequestByEventId(Long idEvent);


    /**
     * PPM 60692-artf178536- Heightened Monitoring Request Detail
     * @param hmRequest
     * @return void
     */
    void saveOrUpdateHMRequest(HMRequest hmRequest);

    /**
     * PPM 60692-artf178536- Delete Heightened Monitoring Request Detail
     * @param hmRequest
     * @return void
     */
    void deleteHMRequest(HMRequest hmRequest);

    /**
     * PPM 60692-artf178534
     * This method gets all HM status records for a given Resource ID.
     * @param idResource Long
     * @return List
     */
    List<HeightenedMonitoringDto> getHmStatusDetails(Long idResource);

    /**
     * PPM 60692 artf178534-HM Resource status
     * @param facilityDetailSaveReq
     */
    Long updateFacilityDetailsHmStatus(FacilityDetailSaveReq facilityDetailSaveReq);

	/**
     * PPM 60692-artf178535 - Changes for Facility Detail Notifications
     * Method Description: This method retrieves the Regional Directors info
     *
     * @param
     */
    List<HMStatusNotifEmail> fetchRegionalDirectorsInfo();

    /**
     * PPM 60692 artf178534-HM Resource status
     * Method to get Parent resource  and facility type
     * @param idResource
     * @return HeightenedMonitoringDto
     */
    HeightenedMonitoringDto getFacilTypAndParentRsrc(Long idResource);

    /**
     * PPM 60692-artf179568- Heightened Monitoring Case List
     * Method Description: This method retrieves all the Heightened Monitoring Request for the id case
     *@param idCase
     * @return  List<EventListDto>
     */
    List<EventListDto> getHmmCaseList(Long idCase);

    /**
     * PPM 60692-artf179570 - Placement Page changes for HM Resource
     * Method Description: This method checks the HM status for the given Resource ID on
     * Placement Creation Date
     *
     * @param idResource
     * @return
     */
    Boolean checkHMStatusActive(Long idResource);

    /**
     * PPM 60692-artf179570 - Placement Page changes for HM Resource
     * Method Description: This method validates a new placement
     *
     * @param idResource
     * @param idStage
     * @param dtPlcmtStart
     * @param indEmergency
     * @return
     */
    Boolean validatePlacementDetail(Long idResource, Long idStage, Date dtPlcmtStart, String indEmergency);

    /**
     * PPM 60692-artf179778 - Approve page changes
     * Method Description: This method populates the Placement event id which utilizes the HM request
     *
     * @param idStage
     * @param idApproval
     */
    void updatePlacementEventToHmReq(Long idStage, Long idApproval);

    /**
     * PPM 60692-artf179777 - To Do page changes
     * Method Description: This method fetches the Approver for the To Do Detail
     *
     * @param legalRegion
     * @return
     */
    AssignedDto fetchApproverForHmToDo(String legalRegion, boolean isSecondary);

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
    Boolean validateHmToDoApprover(Long idPerson, String legalRegion, boolean isSecondary, Long idStage);

    /**
     * PPM 60692-artf179777 - To Do page changes
     * Method Description: This method fetches the HM Request details for Email
     *
     * @param idApproval
     * @return
     */
    HMApproverEmailDto fetchHmApproverEmailDetails(Long idApproval);

    /**
     * PPM 60692-artf179777 - To Do page changes
     * Method Description: This method fetches the available Siblings on HM Request
     *
     * @param idApproval
     * @return
     */
    List<String> fetchSiblingsOnHmRequest(Long idApproval);

    /**
     * PPM 60692-artf179778 - Maintain Heightened Monitoring Approver changes
     * Method Description: This method fetches the Approver List
     *
     * @return
     */
    List<HMApproverListDto> fetchApproversList();



    /**
     * PPM 60692-artf179778 - Maintain Heightened Monitoring Approver changes
     * Method Description: This method deletes the approver
     *
     * @param idApprover
     * @param cdRegDirectorRegion
     * @param cdSubRegion
     * @return
     */
    void deleteApprover(Long idApprover, String cdRegDirectorRegion, String cdSubRegion);

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
    void saveNewApprover(Long idApprover, String cdRegDirectorRegion, String cdSubRegion, String txtEmailAddress,
                         String indDefaultApprover);

    /**
     * PPM 60692-artf179778 - Maintain Heightened Monitoring Approver changes
     * Method Description: This method retrieves the approver count
     *
     * @return
     */
    void updateDefaultApproverForSubRegion(String cdRegDirectorRegion, String cdSubRegion, String indDefault);

    /**
     * PPM 60692-artf179569 - Heightened Monitoring Request validation
     * @param idRsrcFacil
     * @param idStage
     * @return Boolean
     */
    Boolean checkResourceAssignedForAllStagesCountSql(Long idRsrcFacil, Set<Long> idStage);

    /**
     * PPM 60692-artf179569 - Heightened Monitoring Narrative check
     * @param idEvent
     * @return Boolean
     */
    Boolean isDocExists(Long idEvent);

    /**
     * PPM 60692-artf197133-DEV PCR Approver Email changes
     * Method Description: This method retrieves the Caseworker Details based on the User Id
     *
     * @param idStage
     * @param idUser
     * @return
     */
    List<EmailDetailDto> getCaseworkerDetails(Long idStage, Long idUser);

    /**
     * PPM 60692-artf197133-DEV PCR Approver Email changes
     * Method Description: This method retrieves the Caseworker Details based on the Approval ID
     *
     * @param idStage
     * @param idApproval
     * @return
     */
    List<EmailDetailDto> getCaseworkerDetailsByApproval(Long idStage, Long idApproval);

    /**
     * PPM 60692-artf197133-DEV PCR Approver Email changes
     * Method Description: This method retrieves the Approver Details for the given Approval ID
     *
     * @param idApproval
     * @return
     */
    List<EmailDetailDto> getApproverDetails(Long idApproval);

    /**
     * PPM 60692-artf197133-DEV PCR Approver Email changes
     * Method Description: This method retrieves the Resource Details based on the Event
     *
     * @param idEvent
     * @return
     */
    HMApproverEmailDto fetchHmApproverEmailDetailsByEvent(Long idEvent);

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
    void updateApprover(Long idApprover, String cdRegDirectorRegion, String cdSubRegion, String indActive);

    /**
     * PPM 60692-artf204559 - DEV PCR 045 Mitigate HM Vacant Approver Roles
     * Method Description: This method verifies the Approver active status
     *
     * @param idApprover
     * @param cdRegDirectorRegion
     *
     */
    Boolean verifyApproverActiveStatus(Long idApprover, String cdRegDirectorRegion);

    /**
     * PPM 77728 - artf242940 - Heightened Monitoring
     *  LocKs row based on idHmStatus
     * @param idHmStatus
     * @return
     */
    public boolean lockAndUpdateHmResourceStatus(Long idHmStatus);

    // 77728 Heightened monitoring enhancements
    public List<ResourceDetailInDto> fetchResourcesInfo(Long resourceId);

    /**
     * PPM 77728 - artf242940 - Heightened Monitoring
     * @param idHmStatus
     * @param status
     */
    void updateHmResourceStatus(Long idHmStatus, String status);

    /**
     *
     * @param idPerson
     * @return
     */
    boolean isValidSvcAuthApprover(Long idPerson);

    /**
     *
     * @param idPerson
     * @return
     */
    boolean isValidSvcAuthApproverAbove750(Long idPerson);

    /**
     *
     * @param idEvent
     * @return
     */
    double getSvcAuthAmountByEventId(Long idEvent);

    HMRequest getHMRequestByResourseIdAndStageId(Long idStage, Long idResource);

    HmReqNarr findHmReqNarrByIdEvent(Long eventId);

    void saveOrUpdateHMReqNarrative(HmReqNarr mmReqNarr);
}
