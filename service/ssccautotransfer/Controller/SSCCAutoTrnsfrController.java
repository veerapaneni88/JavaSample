package us.tx.state.dfps.service.ssccautotransfer.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import us.tx.state.dfps.service.common.request.SSCCAutoTransferReq;
import us.tx.state.dfps.service.common.response.SSCCAutoTransferRes;
import us.tx.state.dfps.service.ssccautotransfer.service.SSCCAutoTrnsfrService;
import us.tx.state.dfps.web.workload.bean.SSCCAutoTransferBean;
import us.tx.state.dfps.web.workload.bean.SSCCAutoTransferEvalBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/ssccAutoTransfer")
public class SSCCAutoTrnsfrController {

    @Autowired
    SSCCAutoTrnsfrService ssccAutoTrnsfrService;

    @RequestMapping(value = "/getActiveRegions", headers = {
            "Accept=application/json" }, method = RequestMethod.GET)
    public SSCCAutoTransferRes getActiveRegions() throws Exception {
        return ssccAutoTrnsfrService.getActiveRegions();
    }

    @RequestMapping(value = "/getVendorDtls", headers = {
            "Accept=application/json" }, method = RequestMethod.POST)
    public SSCCAutoTransferRes getVendorDetails(@RequestBody SSCCAutoTransferReq request) throws Exception {
        return ssccAutoTrnsfrService.getVendorDtls(request);
    }

    @GetMapping(value = "/insertTrnsfrGrp/{fromIdSsccParameter}/{toIdSsccParameter}/{staffId}/{transferDt}")
    public SSCCAutoTransferRes evaluation(@PathVariable(value = "fromIdSsccParameter") Long fromIdSsccParameter,
                                          @PathVariable(value = "toIdSsccParameter") Long toIdSsccParameter,
                                          @PathVariable(value = "staffId") Long staffId,
                                          @PathVariable(value = "transferDt") Date transferDt) throws Exception {

        Long grpId = ssccAutoTrnsfrService.insertTransferGroup(fromIdSsccParameter,toIdSsccParameter,staffId,transferDt);
        ssccAutoTrnsfrService.callEvaluation(grpId);
        List<SSCCAutoTransferEvalBean> trnsfrEvalLst = ssccAutoTrnsfrService.getEvaluationRes(grpId);
        SSCCAutoTransferRes response = new SSCCAutoTransferRes();
        response.setEvaluation(trnsfrEvalLst);
        return response;
    }

    @GetMapping(value = "/evaluationRes/{idTrnsfrGrp}")
    public SSCCAutoTransferRes evaluationRes(@PathVariable(value = "idTrnsfrGrp") Long idTrnsfrGrp) throws Exception {

        List<SSCCAutoTransferEvalBean> trnsfrEvalLst = ssccAutoTrnsfrService.getEvaluationRes(idTrnsfrGrp);
        SSCCAutoTransferRes response = new SSCCAutoTransferRes();
        response.setEvaluation(trnsfrEvalLst);
        return response;
    }


    @GetMapping(value = "/insertTransfer/{idTrnsfrGrp}/{transferDt}")
    public SSCCAutoTransferRes transfer(@PathVariable(value = "idTrnsfrGrp") Long idTrnsfrGrp,@PathVariable(value = "transferDt") Date transferDt) throws Exception {

        ssccAutoTrnsfrService.callTransfer(idTrnsfrGrp,transferDt);

        SSCCAutoTransferRes response = new SSCCAutoTransferRes();
        return response;
    }

    @GetMapping(value = "/checkStatus")
    public SSCCAutoTransferRes checkEvalTransferStatus() throws Exception {
        List<SSCCAutoTransferEvalBean> evalList = new ArrayList();
        SSCCAutoTransferEvalBean evalBean = ssccAutoTrnsfrService.checkEvalTransferStatus();
        SSCCAutoTransferRes response = new SSCCAutoTransferRes();
        evalList.add(evalBean);
        response.setEvaluation(evalList);
        return response;
    }

    @GetMapping(value = "/display/{idTrnsfrGrp}")
    public SSCCAutoTransferRes getDisplayDetails(@PathVariable(value = "idTrnsfrGrp") Long idTrnsfrGrp) throws Exception {
        return ssccAutoTrnsfrService.displayDtls(idTrnsfrGrp);
    }

    @GetMapping(value = "/deleteEvalTrnsfr/{idTrnsfrGrp}")
    public SSCCAutoTransferRes deleteEvalTrnsfr(@PathVariable(value = "idTrnsfrGrp") Long idTrnsfrGrp) throws Exception {

        ssccAutoTrnsfrService.deleteEvalTrnsfr(idTrnsfrGrp);

        SSCCAutoTransferRes response = new SSCCAutoTransferRes();
        return response;
    }

    @RequestMapping(value = "/callTrnsfr", headers = {
            "Accept=application/json" }, method = RequestMethod.GET)
    public SSCCAutoTransferRes callTransferProc() throws Exception {
        ssccAutoTrnsfrService.createTrnsfrLock();
        SSCCAutoTransferRes res = new SSCCAutoTransferRes();
        return res;
    }

    @RequestMapping(value = "/getTrnsfrDt", headers = {
            "Accept=application/json" }, method = RequestMethod.GET)
    public SSCCAutoTransferRes getBpTrnsfrDt() throws Exception {
        return ssccAutoTrnsfrService.getBpTrnsfrDt();
    }

    @RequestMapping(value = "/caseAssignable/{staffId}", headers = {
            "Accept=application/json" }, method = RequestMethod.GET)
    public SSCCAutoTransferRes caseAssignable(@PathVariable(value = "staffId") Long staffId) throws Exception {
        return ssccAutoTrnsfrService.caseAssignable(staffId);
    }

}
