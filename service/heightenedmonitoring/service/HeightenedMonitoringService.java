package us.tx.state.dfps.service.heightenedmonitoring.service;

import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.FacilityDetailSaveReq;
import us.tx.state.dfps.service.common.request.HeightenedMonitoringInfoReq;
import java.util.Date;
import java.util.List;
import java.util.Set;

import us.tx.state.dfps.service.common.request.HmStatusReq;
import us.tx.state.dfps.service.common.response.*;
import us.tx.state.dfps.service.hmm.dto.HMApproverListDto;
import us.tx.state.dfps.service.resource.detail.dto.ResourceDetailInDto;
import us.tx.state.dfps.service.workload.dto.AssignedDto;
import us.tx.state.dfps.web.placement.bean.HeightenedMonitoringSiblingBean;

public interface HeightenedMonitoringService {

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
     * PPM 60692-artf178536
     * @param idStage
     * @return HeightenedMonitoringEventsRes
     */
    HeightenedMonitoringEventsRes getHmmRequestEventList(Long idStage);

    /**
     * PPM 60692-artf178534 HM resource status changes
     * @param hmStatusReq
     * @return
     */
    HmStatusRes getHmStatusDetails(HmStatusReq hmStatusReq);

    /**
     * PPM 60692-artf178535 - Changes for Facility Detail Notifications
     * Method Description: This method fetches all the regional directors email
     *
     * @return
     */
    CommonStringRes fetchAllRegionalDirectorEmails();

    /**
     * PPM 60692-artf178534 HM resource status changes
     * Method to get Facil type and parent resource Id for a given child Id
     * @param hmStatusReq
     * @return HmStatusRes
     */
    HmStatusRes getFacilTypAndParentRsrc(HmStatusReq hmStatusReq);

    /**
     * PPM 60692-artf179569 - Add or Edit/View HM Request details from HM Request List page.
     * @param idStage
     * @param idEvent
     * @return HeightenedMonitoringInfoRes
     */
    HeightenedMonitoringInfoRes getHmRequestDetails(Long idStage, Long idEvent);

    /**
     * @param idCase
     * @param cdStage
     * @return List<Long></>
     */
    List<Long> getSelectedOpenStages(Long idCase, String cdStage);

    /**
     * @param idStage
     * @return String
     */
    String getChildLegalRegion(Long idStage);

    /**
     * PPM 60692-artf179569  - Get Resource details
     * @param idResource
     * @return HeightenedMonitoringInfoRes
     */
    HeightenedMonitoringRsrcRes getResourceDetails(Long idResource);

    /**
     * PPM 60692-artf179569
     * @param heightenedMonitoringInfoReq
     * @return HeightenedMonitoringInfoRes
     */
    HeightenedMonitoringInfoRes saveHmmRequestDetails(HeightenedMonitoringInfoReq heightenedMonitoringInfoReq);

    /**
     * PPM 60692-artf179569  - Delete HM Request
     * @param idEvent
     * @return HeightenedMonitoringInfoRes
     */
    void deleteHmmRequestDetails(Long idEvent);

    /**
     * @param idStage
     * @param idCase
     * @return List<HeightenedMonitoringSiblingBean>
     */
    HeightenedMonitoringSiblingRes getHmmSiblingList(Long idStage, Long idCase);


    /**
     * PPM 60692-artf179568- Heightened Monitoring Case List
     * Method Description: This method retrieves all the Heightened Monitoring Request for the id case
     *@param idCase
     * @return HeightenedMonitoringEventsRes
     */
    HeightenedMonitoringEventsRes getHmmCaseList(Long idCase);

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
     * Method Description: This method validates a emergency or non emergency placement if the HM Status is active
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
     * Method Description: This method populates the Placement event id which utilizes the HM request and updates to
     * HM_REQ_CHILD_LINK Table
     *
     * @param idStage
     * @param idApproval
     */
    void populatePlacementEventToHmReq(Long idStage, Long idApproval);

    /**
     * PPM 60692-artf179777 - To Do page changes
     * Method Description: This method fetches the Approver for the To Do Detail
     *
     * @param legalRegion
     * @return
     */
    AssignedDto fetchApproverForHMToDo(String legalRegion, boolean isSecondary);

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
     * PM 60692-artf197133 - DEV PCR Approver Email changes
     * Method Description: This method fetches all the details for HM Approver Email
     *
     * @param commonHelperReq
     * @return
     */
    HeightenedMonitoringEmailRes getHMApproverEmailDetails(CommonHelperReq commonHelperReq);

    /**
     * PPM 60692-artf179778 - Maintain Heightened Monitoring Approver changes
     * Method Description: This method fetches the Approver List
     *
     * @return
     */
    HeightenedMonitoringEmailRes fetchApproversList();

    /**
     * PPM 60692-artf179778 - Maintain Heightened Monitoring Approver changes
     * Method Description: This method deletes the approver
     *
     * @param idApprover
     * @param cdRegionTitle
     * @return
     */
    void deleteApprover(Long idApprover, String cdRegionTitle);

    /**
     * PPM 60692-artf179778 - Maintain Heightened Monitoring Approver changes
     * Method Description: This method fetches the Region Title Map
     *
     * @return
     */
    HeightenedMonitoringEmailRes fetchRegionTitleMap();

    /**
     * PPM 60692-artf179778 - Maintain Heightened Monitoring Approver changes
     * Method Description: This method persists the new approver
     *
     * @return
     */
    void saveNewApprover(HMApproverListDto hmApproverListDto);

    /**
     * PPM 60692- - Heightened Monitoring Request validation
     * Method Description: This method validate HM Request available count
     *
     * @param idStage
     * @return Boolean
     */
    Boolean checkResourceAssignedCount(Long idRsrcFacil, Set<Long> idStage);

    /**
     * This method will save or update HM resource status records for a give resourceId.
     * PPM 60692-artf178534. Returns id of row if it was an update.
     * @param facilityDetailSaveReq
     */
    CommonHelperRes updateFacilityDetailsHmStatus(FacilityDetailSaveReq facilityDetailSaveReq);

    /**
     * PPM 60692-artf179570 - Placement Page changes for HM Resource
     * Method Description: This method checks the Doc exist for the given Event ID
     *
     * @param idEvent
     * @return Boolean
     */
    Boolean isDocExist(Long idEvent);

    /**
     * PPM 60692-artf197133-DEV PCR Approver Email changes
     * Method Description: This method retrieves the Resource Details based on the Event or Approval ID
     *
     * @param commonHelperReq
     * @return
     */
    HeightenedMonitoringEmailRes getHMResourceDetails(CommonHelperReq commonHelperReq);

    /**
     * PPM 60692-artf204559 : DEV PCR 045 Mitigate Heightened Monitoring (HM) Vacant Approver Roles
     * Method Description: This method is used to show the confirmation message when the RD for that region does not exist and we need to default TO DO detail to the higher approver
     *
     * @param commonHelperReq
     * @return
     */
    String approverConfirmation(String legalRegion);

    /**
     * PPM 60692-artf204559 - DEV PCR 045 Mitigate HM Vacant Approver Roles
     * Method Description: This method updates the approving status for the Approver
     *
     * @param idApprover
     * @param cdRegionTitle
     * @param indActive
     *
     */
    void updateApprover(Long idApprover, String cdRegionTitle, String indActive);

    /**
     * PPM 60692-artf204559 - DEV PCR 045 Mitigate HM Vacant Approver Roles
     * Method Description: This method verifies the Approver active status
     *
     * @param idApprover
     * @param cdRegDirectorRegion
     * @param statusAvailable
     *
     */
    Boolean verifyApproverActiveStatus(Long idApprover, String cdRegDirectorRegion, Boolean statusAvailable);

    /**Returns active HM resources to send email.
     *
     * @param active
     * @param resourceDetailInDto
     * @param heightenedMonitoringEmailRes
     * @return
     */
    public HeightenedMonitoringEmailRes fetchResourcesEmailInfo(Boolean active, ResourceDetailInDto resourceDetailInDto,HeightenedMonitoringEmailRes heightenedMonitoringEmailRes) ;

    public ResourceDetailInDto fetchResourcesInfo(Long resourceId);

    /**
     * PPM 77728 - artf242940 - Heightened Monitoring
     * Update email send status
     * @param idHmStatus
     * @param status
     */
    void saveEmailStatus(Long idHmStatus, String status);

    /**
     *
     * @param idPerson
     * @return
     */
    boolean checkValidSvcAuthApprover(Long idPerson);

    /**
     *
     * @param idPerson
     * @return
     */
    boolean checkPersonHasSvcAuthApproveAbove750(Long idPerson);

    /**
     *
     * @param idEvent
     * @return
     */
    double getSvcAuthAmount(Long idEvent);
}
