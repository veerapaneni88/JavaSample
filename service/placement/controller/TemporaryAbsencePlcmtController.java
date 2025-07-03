package us.tx.state.dfps.service.placement.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.TemporaryAbsenceInfoReq;
import us.tx.state.dfps.service.common.response.PlacementRes;
import us.tx.state.dfps.service.common.response.TemporaryAbsenceEventsRes;
import us.tx.state.dfps.service.common.response.TemporaryAbsenceInfoRes;
import us.tx.state.dfps.service.common.response.TemporaryAbsenceRsrcRes;
import us.tx.state.dfps.service.placement.service.TemporaryAbsenceService;
@RestController
@Api(tags = { "placements" })
@RequestMapping("/temporaryAbsence")
public class TemporaryAbsencePlcmtController {

    @Autowired
    TemporaryAbsenceService temporaryAbsenceService;

    private static final Logger log = Logger.getLogger(TemporaryAbsencePlcmtController.class);

    /**
     * @param commonHelperReq
     * @return
     */
    @ApiOperation(value = "Get temporary absence list", tags = { "placements" })
    @RequestMapping(value = "/getTemporaryAbsenceList", headers = {"Accept=application/json" }, method = RequestMethod.POST)
    public TemporaryAbsenceEventsRes getTemporaryAbsenceList(@RequestBody CommonHelperReq commonHelperReq) {
        TemporaryAbsenceEventsRes temporaryAbsenceRes = new TemporaryAbsenceEventsRes();
        log.debug("Entering method getTemporaryAbsenceList in TemporaryAbsencePlcmtController");
        temporaryAbsenceRes = temporaryAbsenceService.getTemporaryAbsenceList(commonHelperReq);
        return temporaryAbsenceRes;
    }

    /**
     * @param idStage
     * @param idPlacementTa
     * @return
     */
    @ApiOperation(value = "Get temporary absence details", tags = { "placements" })
    @GetMapping(value = "/getTemporaryAbsenceInfo/{idStage}/{idPlacementTa}")
    public TemporaryAbsenceInfoRes getTARequestDetails(@PathVariable(value = "idStage") Long idStage, @PathVariable(value = "idPlacementTa") Long idPlacementTa) {
        if(null == idStage && null == idPlacementTa){
            throw new IllegalArgumentException("Stage ID or Placement TA ID are not available");
        }
        return temporaryAbsenceService.getTARequestDetails(idStage, idPlacementTa);
    }

    @GetMapping(value = "/{idChldMsngDtl}")
    public TemporaryAbsenceInfoRes getTARequestDetails(@PathVariable(value = "idChldMsngDtl") Long idChldMsngDtl) {
        if(null == idChldMsngDtl){
            throw new IllegalArgumentException("child missing id cannot be null");
        }
        return temporaryAbsenceService.getTemporaryAbsenceByMissingChild(idChldMsngDtl);
    }

    @GetMapping(value = "/getTemporaryAbsence/{idPlacementTa}")
    public TemporaryAbsenceInfoRes getOpenTA(@PathVariable(value = "idPlacementTa") Long idPlacementTa) {
        if(null == idPlacementTa){
            throw new IllegalArgumentException("Placement TA ID is not available");
        }
        return temporaryAbsenceService.getOpenTAForActivePlacement(idPlacementTa);
    }

    @ApiOperation(value = "Delete temporary absence details", tags = { "placements" })
    @RequestMapping(value = "/{idPlacementTa}/{loginUserId}", method = RequestMethod.DELETE)
    public void delete(@PathVariable(value = "idPlacementTa") Long idPlacementTa,@PathVariable(value = "loginUserId")Long loginUserId) {
        temporaryAbsenceService.deleteTAInfo(idPlacementTa,loginUserId);
    }

    /**
     * @param idResource
     * @return
     */
    @GetMapping(value = "/getResource/{idResource}")
    public TemporaryAbsenceRsrcRes getResourceDetails(@PathVariable(value = "idResource") Long idResource) {
        return temporaryAbsenceService.getResourceDetails(idResource);
    }

    /**
     * @param taInfoReq
     * @return
     */
    @RequestMapping(value = "/saveTaDetailInfo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public TemporaryAbsenceInfoRes saveTaDetailInfo(@RequestBody TemporaryAbsenceInfoReq taInfoReq) {
        return temporaryAbsenceService.saveTaDetailInfo(taInfoReq);
    }

    /**
     * @param placementEventId
     * @return
     */
    @RequestMapping(value = "/count/{placementEventId}")
    public PlacementRes getActiveTAsCountForPlacement(@PathVariable(value = "placementEventId") Long placementEventId) {
        return temporaryAbsenceService.getActiveTAsCountForPlacement(placementEventId);
    }

    /**
     * @param taInfoReq
     * @return
     */
    @RequestMapping(value = "/checkTaDtBeforePlcmtStart")
    public TemporaryAbsenceInfoRes isTAStartOrEndDtBeforePlcmtStart(@RequestBody TemporaryAbsenceInfoReq taInfoReq) {
        TemporaryAbsenceInfoRes res = new TemporaryAbsenceInfoRes();
        boolean isTAdtBeforePlcmtStart = temporaryAbsenceService.isTAStartOrEndDtBeforePlcmtStart(taInfoReq);
        res.setTAdtBeforePlcmtStart(isTAdtBeforePlcmtStart);
        return res;
    }

    /**
     * @param taInfoReq
     * @return
     */
    @RequestMapping(value = "/checkTaDtBetweenRange")
    public TemporaryAbsenceInfoRes isTAStartOrEndDtBetweenRange(@RequestBody TemporaryAbsenceInfoReq taInfoReq) {
        TemporaryAbsenceInfoRes res = new TemporaryAbsenceInfoRes();
        boolean isTADtBetweenRange = temporaryAbsenceService.isTAStartOrEndDtBetweenRange(taInfoReq);
        res.setTADtBetweenRange(isTADtBetweenRange);
        return res;
    }

    /**
     * @param taInfoReq
     * @return
     */
    @RequestMapping(value = "/checkTaEndDtAfterPlcmtEnd")
    public TemporaryAbsenceInfoRes isTAEndDtAfterPlcmtEnd(@RequestBody TemporaryAbsenceInfoReq taInfoReq) {
        TemporaryAbsenceInfoRes res = new TemporaryAbsenceInfoRes();
        boolean isTAEndDtAfterPlcmtEnd = temporaryAbsenceService.isTAEndDtAfterPlcmtEnd(taInfoReq);
        res.setTAEndDtAfterPlcmtEnd(isTAEndDtAfterPlcmtEnd);
        return res;
    }

    @RequestMapping(value = "/checkPlacementEnded")
    public TemporaryAbsenceInfoRes isPlacementEnded(@RequestBody TemporaryAbsenceInfoReq taInfoReq) {
        TemporaryAbsenceInfoRes res = new TemporaryAbsenceInfoRes();
        boolean isPlcmtEnded = temporaryAbsenceService.isPlacementEnded(taInfoReq);
        res.setPlacementEnded(isPlcmtEnded);
        return res;
    }

    @ApiOperation(value = "Get temporary absence details", tags = { "placements" })
    @GetMapping(value = "/getTemporaryAbsenceById/{idPlacementTa}")
    public TemporaryAbsenceInfoRes getTemporaryAbsenceById(@PathVariable(value = "idPlacementTa") Long idPlacementTa) {
        if(null == idPlacementTa){
            throw new IllegalArgumentException("Placement TA ID is not available");
        }
        return temporaryAbsenceService.getTemporaryAbsenceById(idPlacementTa);
    }

}
