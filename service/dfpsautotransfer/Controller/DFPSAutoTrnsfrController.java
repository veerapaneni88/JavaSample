package us.tx.state.dfps.service.dfpsautotransfer.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import us.tx.state.dfps.service.common.response.SSCCAutoTransferRes;
import us.tx.state.dfps.service.dfpsautotransfer.service.DFPSAutoTrnsfrService;
import us.tx.state.dfps.web.workload.bean.SSCCAutoTransferEvalBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/dfpsAutoTransfer")
public class DFPSAutoTrnsfrController {

    @Autowired
    DFPSAutoTrnsfrService dfpsAutoTrnsfrService;

    @RequestMapping(value = "/getActiveRegions", headers = {
            "Accept=application/json" }, method = RequestMethod.GET)
    public SSCCAutoTransferRes getActiveRegions() throws Exception {
        return dfpsAutoTrnsfrService.getActiveRegions();
    }


    @GetMapping(value = "/insertTrnsfrGrp/{fromIdSsccParameter}/{staffId}/{transferDt}")
    public SSCCAutoTransferRes evaluation(@PathVariable(value = "fromIdSsccParameter") Long fromIdSsccParameter,
                                          @PathVariable(value = "staffId") Long staffId,
                                          @PathVariable(value = "transferDt") Date transferDt) throws Exception {

        Long grpId = dfpsAutoTrnsfrService.insertTransferGroup(fromIdSsccParameter, staffId, transferDt);
        dfpsAutoTrnsfrService.callEvaluation(grpId);
        List<SSCCAutoTransferEvalBean> trnsfrEvalLst = dfpsAutoTrnsfrService.getEvaluationRes(grpId);
        SSCCAutoTransferRes response = new SSCCAutoTransferRes();
        response.setEvaluation(trnsfrEvalLst);
        return response;
    }

    @GetMapping(value = "/evaluationRes/{idTrnsfrGrp}")
    public SSCCAutoTransferRes evaluationRes(@PathVariable(value = "idTrnsfrGrp") Long idTrnsfrGrp) throws Exception {

        List<SSCCAutoTransferEvalBean> trnsfrEvalLst = dfpsAutoTrnsfrService.getEvaluationRes(idTrnsfrGrp);
        SSCCAutoTransferRes response = new SSCCAutoTransferRes();
        response.setEvaluation(trnsfrEvalLst);
        return response;
    }


    @GetMapping(value = "/insertTransfer/{idTrnsfrGrp}/{transferDt}")
    public SSCCAutoTransferRes transfer(@PathVariable(value = "idTrnsfrGrp") Long idTrnsfrGrp,@PathVariable(value = "transferDt") Date transferDt) throws Exception {

        dfpsAutoTrnsfrService.callTransfer(idTrnsfrGrp,transferDt);

        SSCCAutoTransferRes response = new SSCCAutoTransferRes();
        return response;
    }

    @GetMapping(value = "/checkStatus")
    public SSCCAutoTransferRes checkEvalTransferStatus() throws Exception {
        List<SSCCAutoTransferEvalBean> evalList = new ArrayList();
        SSCCAutoTransferEvalBean evalBean = dfpsAutoTrnsfrService.checkEvalTransferStatus();
        SSCCAutoTransferRes response = new SSCCAutoTransferRes();
        evalList.add(evalBean);
        response.setEvaluation(evalList);
        return response;
    }

    @GetMapping(value = "/display/{idTrnsfrGrp}")
    public SSCCAutoTransferRes getDisplayDetails(@PathVariable(value = "idTrnsfrGrp") Long idTrnsfrGrp) throws Exception {
        return dfpsAutoTrnsfrService.displayDtls(idTrnsfrGrp);
    }

    @GetMapping(value = "/deleteEvalTrnsfr/{idTrnsfrGrp}")
    public SSCCAutoTransferRes deleteEvalTrnsfr(@PathVariable(value = "idTrnsfrGrp") Long idTrnsfrGrp) throws Exception {

        dfpsAutoTrnsfrService.deleteEvalTrnsfr(idTrnsfrGrp);

        SSCCAutoTransferRes response = new SSCCAutoTransferRes();
        return response;
    }

    @RequestMapping(value = "/callTrnsfr", headers = {
            "Accept=application/json" }, method = RequestMethod.GET)
    public SSCCAutoTransferRes callTransferProc() throws Exception {
        dfpsAutoTrnsfrService.createTrnsfrLock();
        SSCCAutoTransferRes res = new SSCCAutoTransferRes();
        return res;
    }

    @RequestMapping(value = "/getTrnsfrDt", headers = {
            "Accept=application/json" }, method = RequestMethod.GET)
    public SSCCAutoTransferRes getBpTrnsfrDt() throws Exception {
        return dfpsAutoTrnsfrService.getBpTrnsfrDt();
    }

    @RequestMapping(value = "/caseAssignable/{staffId}", headers = {
            "Accept=application/json" }, method = RequestMethod.GET)
    public SSCCAutoTransferRes caseAssignable(@PathVariable(value = "staffId") Long staffId) throws Exception {
        return dfpsAutoTrnsfrService.caseAssignable(staffId);
    }

}
