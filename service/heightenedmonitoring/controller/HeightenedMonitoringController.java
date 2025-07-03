package us.tx.state.dfps.service.heightenedmonitoring.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.*;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.*;
import us.tx.state.dfps.service.common.response.*;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.heightenedmonitoring.service.HeightenedMonitoringService;
import us.tx.state.dfps.service.hmm.dto.HMApproverListDto;
import us.tx.state.dfps.service.resource.detail.dto.ResourceDetailInDto;
import us.tx.state.dfps.service.safetycheck.service.SafetyCheckService;
import us.tx.state.dfps.web.placement.bean.HeightenedMonitoringSiblingBean;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Api(tags = { "heightenedMonitoring" })
@RestController
@RequestMapping("/heightened-monitoring")
public class HeightenedMonitoringController {

    @Autowired
    MessageSource messageSource;

    @Autowired
    HeightenedMonitoringService heightenedMonitoringService;

    @Autowired
    SafetyCheckService safetyCheckService;


    /**
     * PPM 60692-artf178535 - Changes for Facility Detail Notifications
     * Method Description: This method retrieves all the resources info whose HM status has active start date as of today
     *
     * @return
     */
    @GetMapping(value = "/resources/active")
    public HeightenedMonitoringEmailRes getActiveResourceInfo() {
        HeightenedMonitoringEmailRes heightenedMonitoringEmailRes = new HeightenedMonitoringEmailRes();
        heightenedMonitoringEmailRes.setResources(heightenedMonitoringService.fetchResourcesInfo(true));
        return heightenedMonitoringEmailRes;
    }

    @PostMapping(value = "/resources/activeHeightenedMonitoringEmailInfo")
    public HeightenedMonitoringEmailRes getActiveHeightenedMonitoringEmailInfo(@RequestBody ResourceDetailInDto resourceDetailInDto) {
        HeightenedMonitoringEmailRes heightenedMonitoringEmailRes = new HeightenedMonitoringEmailRes();
        heightenedMonitoringService.fetchResourcesEmailInfo(true,resourceDetailInDto,heightenedMonitoringEmailRes);
        return heightenedMonitoringEmailRes;
    }

    /**
     * PPM 60692-artf178535 - Changes for Facility Detail Notifications
     * Method Description: This method retrieves all the resources info whose HM status has changed from active to
     * inactive
     *
     * @return
     */
    @GetMapping(value = "/resources/inactive")
    public HeightenedMonitoringEmailRes getInActiveResourceInfo() {
        HeightenedMonitoringEmailRes heightenedMonitoringEmailRes = new HeightenedMonitoringEmailRes();
        heightenedMonitoringEmailRes.setResources(heightenedMonitoringService.fetchResourcesInfo(false));
        return heightenedMonitoringEmailRes;
    }

    /**
     * PPM 77728 - artf242940 - Heightened Monitoring
     * Get inactive resource email info
     * @param resourceDetailInDto
     * @return
     */
    @PostMapping (value = "/resources/inactiveHeightenedMonitoringEmailInfo")
    public HeightenedMonitoringEmailRes getInActiveHeightenedMonitoringEmailInfo(@RequestBody ResourceDetailInDto resourceDetailInDto) {
        HeightenedMonitoringEmailRes heightenedMonitoringEmailRes = new HeightenedMonitoringEmailRes();
        heightenedMonitoringService.fetchResourcesEmailInfo(false,resourceDetailInDto,heightenedMonitoringEmailRes);
        return heightenedMonitoringEmailRes;
    }

    /**
     * PPM 77728 - artf242940 - Heightened Monitoring
     * Save email status by id
     * @param idHmStatus
     * @param status
     */
    @PostMapping(value = "/resources/saveEmailStatus")
    public void saveEmailStatus( @RequestParam(name = "idHmStatus") Long idHmStatus,
                                 @RequestParam(name = "status") String status ) {
        heightenedMonitoringService.saveEmailStatus(idHmStatus,status);
    }

    /**
     * PPM 60692-artf178536- Heightened Monitoring Request Event List
     * Method Description: This method retrieves all the resources of Heightened Monitoring request event list
     *
     * @return HeightenedMonitoringEventsRes
     */
    @ApiOperation(value = "Get heightened Monitoring list for a stageId", tags = { "heightenedMonitoring" })
    @GetMapping(value = "/eventList/{idStage}")
    public HeightenedMonitoringEventsRes getHmmRequestEventList(@PathVariable(value = "idStage") Long idStage) {
        return heightenedMonitoringService.getHmmRequestEventList(idStage);
    }

    /**
     * PPM 60692-artf178535 - Changes for Facility Detail Notifications
     * Method Description: This method retrieves all the resources info whose HM status has changed from active to
     * inactive
     *
     * @return
     */
    @GetMapping(value = "/regional-directors")
    public CommonStringRes getAllRegionalDirectorEmails() {
        return heightenedMonitoringService.fetchAllRegionalDirectorEmails();
    }

    /**
     * PPM 60692-artf179568- Heightened Monitoring Case List
     * Method Description: This method retrieves all the Heightened Monitoring Request for the id case
     *
     * @return HeightenedMonitoringEventsRes
     */
    @ApiOperation(value = "Get heightened Monitoring list for caseId", tags = { "heightenedMonitoring" })
    @GetMapping(value = "/caseList/{idCase}")
    public HeightenedMonitoringEventsRes getHmmCaseList(@PathVariable(value = "idCase") Long idCase) {
        return heightenedMonitoringService.getHmmCaseList(idCase);
    }

     /* Method Description: This method retrieves open stages details in a case using cdStage and Event ID.
     *
     * @param idCase
     * @param cdStage
     * @return List<Long>
     */
     @GetMapping(value = "/getSelectedOpenStages/{idCase}/{cdStage}")
    public List<Long> getSelectedOpenStages(@PathVariable(value = "idCase") Long idCase, @PathVariable(value = "cdStage") String cdStage) {
        if(null == idCase || null == cdStage){
            throw new IllegalArgumentException("Case ID or cdStage are not available");
        }
        return heightenedMonitoringService.getSelectedOpenStages(idCase,cdStage);
    }

    @ResponseBody
    @GetMapping(value = "/getChildLegalRegion/{idStage}")
    public RegionCdReq getChildLegalRegion(@PathVariable(value = "idStage") Long idStage){
         if(null==idStage){
             throw new IllegalArgumentException("Stage id is not available");
         }
        RegionCdReq res =new RegionCdReq();
         res.setRegionCd(heightenedMonitoringService.getChildLegalRegion(idStage));
         return res;
    }

    @GetMapping(value = "/addOrEditHmmRequest/{idStage}/{idEvent}")
    public HeightenedMonitoringInfoRes addOrEditHmmRequest(@PathVariable(value = "idStage") Long idStage, @PathVariable(value = "idEvent") Long idEvent) {
        if(null == idStage && null == idEvent){
            throw new IllegalArgumentException("Stage ID or Event ID are not available");
        }
        return heightenedMonitoringService.getHmRequestDetails(idStage, idEvent);
    }

    @GetMapping(value = "/rsrcDetails/{idResource}")
    public HeightenedMonitoringRsrcRes getResourceDetails(@PathVariable(value = "idResource") Long idResource) {
        return heightenedMonitoringService.getResourceDetails(idResource);
    }

    /**
     *
     * Method Name: This method add new Heightened Monitoring request details
     *
     * @param heightenedMonitoringInfoReq
     * @return HeightenedMonitoringInfoRes
     */
    @RequestMapping(value = "/saveHmmRequestDetails", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public HeightenedMonitoringInfoRes saveHmmRequestDetails(@RequestBody HeightenedMonitoringInfoReq heightenedMonitoringInfoReq) {
       return heightenedMonitoringService.saveHmmRequestDetails(heightenedMonitoringInfoReq);
    }

    /**
     *
     * Method Name: This method delete Heightened Monitoring request details
     *
     * @param idEvent
     * @return void
     */
    @ApiOperation(value = "Delete Heightened Monitoring for an EventId", tags = { "heightenedMonitoring" })
    @RequestMapping(value = "/deleteHmmRequestDetails/{idEvent}", headers = { "Accept=application/json" }, method = RequestMethod.DELETE)
    public void deleteHmmRequest(@PathVariable(value = "idEvent") Long idEvent) {
        heightenedMonitoringService.deleteHmmRequestDetails(idEvent);
    }

    @ApiOperation(value = "Get heightened Monitoring Siblings list for stageId and caseId", tags = { "heightenedMonitoring" })
    @GetMapping(value = "/getHmmSiblingList/{idCase}/{idStage}")
    public HeightenedMonitoringSiblingRes getHmmSiblingList(@PathVariable(value = "idStage") Long idStage, @PathVariable(value = "idCase") Long idCase){
        if(null == idStage || null == idCase){
            throw new IllegalArgumentException("Case ID or Stage ID are not available");
        }
        return heightenedMonitoringService.getHmmSiblingList(idStage, idCase);
    }

    /**
     * PPM 60692-artf179570 - Placement Page changes for HM Resource
     * Method Description: This method checks the HM Status Active for the given Resource ID and the date
     *
     * @param commonHelperReq
     * @return
     */
    @PostMapping(value = "/status/active")
    public CommonHelperRes checkHMStatusActive(@RequestBody CommonHelperReq commonHelperReq) {

        CommonHelperRes commonHelperRes = new CommonHelperRes();
        commonHelperRes.setResult(heightenedMonitoringService.checkHMStatusActive(commonHelperReq.getIdResource()));

        return commonHelperRes;
    }

    /**
     * PPM 60692-artf179570 - Placement Page changes for HM Resource
     * Method Description: This method checks the HM Status Active for the given Resource ID and the date
     *
     * @param placementReq
     * @return
     */
    @PostMapping(value = "/placement/validate")
    public CommonHelperRes validatePlacement(@RequestBody PlacementReq placementReq) {

        CommonHelperRes commonHelperRes = new CommonHelperRes();
        commonHelperRes.setResult(heightenedMonitoringService.validatePlacementDetail(placementReq.getIdResource(),
                placementReq.getIdStage(), placementReq.getDtPlcmtStart(), placementReq.getPlacementValueDto().getIndPlcmtEmerg()));

        return commonHelperRes;
    }

    /**
     * PPM 60692-artf179776- Safety Check Details for Placement
     * This method displays list of safety checks for placement
     * @param idStage
     * @return SafetyCheckRes
     */
    @ApiOperation(value = "Get Safety Check list for stageId", tags = { "heightenedMonitoring" })
    @GetMapping(value = "/displaySafetyCheckListForPlcmnt/{idStage}")
    public SafetyCheckRes displaySafetyCheckListForPlcmnt(@PathVariable(value = "idStage") Long idStage) {
        return safetyCheckService.getSafetyCheckListForPlcmnt(idStage);
    }

    /**
     * PPM 60692-artf179777 - To Do page changes
     * Method Description: This method validates the assigned Approver on the HM To Do
     *
     * @param idPerson
     * @param isSecondary
     * @param legalRegion
     * @return
     */
    @GetMapping(value = "/todo/approver/validate")
    public CommonHelperRes validateToDoApprover(@RequestParam(name = "idPerson") Long idPerson,
                                             @RequestParam(name = "isSecondary") boolean isSecondary,
                                             @RequestParam(name = "legalRegion", required = false) String legalRegion,
                                                @RequestParam(name = "idStage", required = false) Long idStage) {

        CommonHelperRes commonHelperRes = new CommonHelperRes();
        commonHelperRes.setResult(heightenedMonitoringService.validateHmToDoApprover(idPerson, legalRegion, isSecondary,
                idStage));

        return commonHelperRes;
    }

    /**
     * PPM 60692-artf179776-Start : Safety Check Details for Placement
     * This method displays safety check details for a given record
     * @param idHmSafetyCheck
     * @param idEvent
     * @param idResource
     * @return
     */
    @ApiOperation(value = "Get Safety Check info", tags = { "heightenedMonitoring" })
    @GetMapping(value = "/displaySafetyCheckDetailForPlcmnt/{idHmSafetyCheck}/{idEvent}/{idResource}")
    public SafetyCheckRes getSafetyCheckDetailForPlcmnt(@PathVariable(value = "idHmSafetyCheck") Long idHmSafetyCheck, @PathVariable(value = "idEvent") Long idEvent, @PathVariable(value = "idResource") Long idResource) {
        SafetyCheckRes response = new SafetyCheckRes();
        response.setSafetyCheckDto(safetyCheckService.getSafetyCheckDetailForPlcmnt(idHmSafetyCheck,idEvent,idResource));
        return response;
    }

    /**
     * PPM 60692-artf179777 - To Do page changes
     * Method Description: This method fetches all the details for HM Approver Email
     *
     * @param commonHelperReq
     * @return
     */
    @PostMapping(value = "/todo/approver/email")
    public HeightenedMonitoringEmailRes getApproverEmailDetails(@RequestBody CommonHelperReq commonHelperReq) {

        if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdApproval())) {
            throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
        }

        if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
            throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
        }

        if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getUserID())) {
            throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
        }

        if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getCaseStatus())) {
            throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
        }

        return heightenedMonitoringService.getHMApproverEmailDetails(commonHelperReq);
    }

    /**
     * PPM 60692-artf179778 - Maintain Heightened Monitoring Approver changes
     * Method Description: This method fetches the Approver List
     *
     * @return
     */
    @GetMapping(value = "/approvers")
    public HeightenedMonitoringEmailRes getApproversList() {

        return heightenedMonitoringService.fetchApproversList();
    }

    /**
     * PPM 60692-artf179778 - Maintain Heightened Monitoring Approver changes
     * Method Description: This method fetches the Approver List
     *
     * @return
     */
    @PostMapping(value = "/approvers/delete")
    public void deleteApprover(@RequestParam(name = "idApprover") Long idApprover,
                                                       @RequestParam(name = "cdRegionTitle") String cdRegionTitle) {

        heightenedMonitoringService.deleteApprover(idApprover, cdRegionTitle);
    }

    /**
     * PPM 60692-artf179778 - Maintain Heightened Monitoring Approver changes
     * Method Description: This method fetches the Approver List
     *
     * @return
     */
    @GetMapping(value = "/approvers/regions/unavailable")
    public HeightenedMonitoringEmailRes getUnavailable() {

        return heightenedMonitoringService.fetchRegionTitleMap();
    }

    /**
     * PPM 60692-artf179778 - Maintain Heightened Monitoring Approver changes
     * Method Description: This method fetches the Approver List
     *
     * @return
     */
    @PostMapping(value = "/approvers/add")
    public void addNewApprover(@RequestBody HMApproverListDto hmApproverListDto) {

       heightenedMonitoringService.saveNewApprover(hmApproverListDto);
    }

    /**
     * PPM 60692- artf179569 - Heightened Monitoring validation
     * Method Description: This method check HM Request already exists
     *
     * @return
     */
    @GetMapping(value = "/validateHmmResourceUsageForIds")
    public CommonHelperRes checkResourceAssignedForAllStages(@RequestParam(name = "idRsrcFacil") Long idRsrcFacil, @RequestParam(name = "idStage") Set<Long> idStage) {
        CommonHelperRes commonHelperRes = new CommonHelperRes();
        commonHelperRes.setResult(heightenedMonitoringService.checkResourceAssignedCount(idRsrcFacil, idStage));
        return commonHelperRes;
    }

    /**
     * PPM 60692-artf178534 This method will save or update HM resource status records for a give resourceId.
     *
     * @param facilityDetailSaveReq
     */
    @PostMapping(value = "/resources/save")
    public CommonHelperRes saveHMMonitoringStatus(@RequestBody FacilityDetailSaveReq facilityDetailSaveReq) {
        return heightenedMonitoringService.updateFacilityDetailsHmStatus(facilityDetailSaveReq);
    }

    /**
     * PPM 60692-artf179570 - Placement Page changes for HM Resource
     * Method Description: This method checks the doc exist for the given Event ID (Narrative check)
     *
     * @param idEvent
     * @return CommonHelperRes
     */
    @GetMapping(value = "/docExist")
    public CommonHelperRes checkDocExist(@RequestParam(name = "idEvent") Long idEvent) {
        CommonHelperRes commonHelperRes = new CommonHelperRes();
        commonHelperRes.setResult(heightenedMonitoringService.isDocExist(idEvent));
        return commonHelperRes;
    }

    /**
     * PPM 60692-artf197133-DEV PCR Approver Email changes
     * Method Description: This method retrieves the Resource Details based on the Event or Approval ID
     *
     * @param idEvent
     * @param idApproval
     * @return
     */
    @PostMapping(value = "/resource")
    public HeightenedMonitoringEmailRes getHMResourceDetails(@RequestBody CommonHelperReq commonHelperReq) {
        return heightenedMonitoringService.getHMResourceDetails(commonHelperReq);
    }

    /**
     * PPM 60692-artf204559 : DEV PCR 045 Mitigate Heightened Monitoring (HM) Vacant Approver Roles
     * Method Description: This method is used to show the confirmation message when the RD for that region does not exist and we need to default TO DO detail to the higher approver
     *
     *
     * @param legalRegion
     * @return
     */
    @GetMapping(value = "/higherlevelapprover")
    public CommonHelperRes validateToDoApprover( @RequestParam(name = "legalRegion", required = true) String legalRegion) {

        CommonHelperRes commonHelperRes = new CommonHelperRes();
        commonHelperRes.setMessage(heightenedMonitoringService.approverConfirmation(legalRegion));
        return commonHelperRes;
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
    @PostMapping(value = "/approvers/update")
    public void updateApprover(@RequestParam(name = "idApprover") Long idApprover,
                               @RequestParam(name = "cdRegionTitle") String cdRegionTitle,
                               @RequestParam(name = "indActive") String indActive) {

        heightenedMonitoringService.updateApprover(idApprover, cdRegionTitle, indActive);
    }

    /**
     * PPM 60692-artf204559 - DEV PCR 045 Mitigate HM Vacant Approver Roles
     * Method Description: This method verifies the Approver active status
     *
     * @param idPerson
     * @param legalRegion
     * @param statusAvailable
     * @return
     */
    @GetMapping(value = "/todo/approver/verify")
    public CommonHelperRes validateToDoApprover(@RequestParam(name = "idPerson") Long idPerson,
                                                @RequestParam(name = "legalRegion", required = false) String legalRegion,
                                                @RequestParam(name = "statusAvailable") Boolean statusAvailable) {

        CommonHelperRes commonHelperRes = new CommonHelperRes();
        commonHelperRes.setResult(heightenedMonitoringService.verifyApproverActiveStatus(idPerson, legalRegion, statusAvailable));

        return commonHelperRes;
    }

    @GetMapping(value = "/resources/{idResource}")
    public HeightenedMonitoringEmailRes getResourceEmailInfo(@PathVariable(value = "idResource") Long idResource,
                                                             @RequestParam(name = "active") Boolean active) {
        ResourceDetailInDto resourceDetailInDto = heightenedMonitoringService.fetchResourcesInfo(idResource);
        HeightenedMonitoringEmailRes heightenedMonitoringEmailRes = new HeightenedMonitoringEmailRes();
        // fetchResourcesEmailInfo also generates alerts and update
        heightenedMonitoringService.fetchResourcesEmailInfo(active,resourceDetailInDto,heightenedMonitoringEmailRes);

        return heightenedMonitoringEmailRes;
    }

    /**
     *
     * @param idEvent
     * Get the Service Atuh amount from DB
     */
    @GetMapping(value = "/svcAuthAmount")
    public CommonHelperRes getSvcAuthAmount(@RequestParam(name = "idEvent") Long idEvent) {
        CommonHelperRes commonHelperRes = new CommonHelperRes();
        commonHelperRes.setAmount(heightenedMonitoringService.getSvcAuthAmount(idEvent));
        return commonHelperRes;
    }

    /**
     *Checking the person can approve the Service Authorization
     * @param idPerson
     */
    @GetMapping(value = "/checkValidSvcAuthApprover")
    public CommonHelperRes isValidSvcAuthApprover(@RequestParam(name = "idPerson") Long idPerson) {
        CommonHelperRes commonHelperRes = new CommonHelperRes();
        commonHelperRes.setResult(heightenedMonitoringService.checkValidSvcAuthApprover(idPerson));
        return commonHelperRes;
    }

    /**
     * Checking the person can approve the Service Authorization when service auth amount more than 750
     *@param idPerson
     *
     */
    @GetMapping(value = "/checkValidSvcAuthApproverAbove750")
    public CommonHelperRes isValidSvcAuthApproverAbove750(@RequestParam(name = "idPerson") Long idPerson) {

        CommonHelperRes commonHelperRes = new CommonHelperRes();
        commonHelperRes.setResult(heightenedMonitoringService.checkPersonHasSvcAuthApproveAbove750(idPerson));
        return commonHelperRes;
    }
}
