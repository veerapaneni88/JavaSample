package us.tx.state.dfps.service.mobile.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.MPSVersionUpdateReq;
import us.tx.state.dfps.service.common.response.CommonBooleanRes;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.common.response.SyncErrorRes;
import us.tx.state.dfps.service.mobile.service.MobileWorkloadService;

@RestController
@RequestMapping("/mobile")
public class MobileWorkloadController {

    private static final Logger logger = Logger.getLogger(MobileSetupController.class);
    @Autowired
    MobileWorkloadService mobileService;

    @RequestMapping(value = "/saveConfirm", headers = {
            "Accept=application/json"}, method = RequestMethod.POST)
    public CommonBooleanRes saveConfirm(@RequestBody CommonHelperReq commonHelperReq) {
        CommonBooleanRes response = new CommonBooleanRes();
        response.setExists(Boolean.TRUE);
        mobileService.saveConfirm(commonHelperReq.getIdPerson(), commonHelperReq.getTaskCodes());
        return response;
    }

    @RequestMapping(value = "/getSyncErrors", headers = {
            "Accept=application/json"}, method = RequestMethod.POST)
    public SyncErrorRes getSyncErrors(@RequestBody CommonHelperReq commonHelperReq) {
        SyncErrorRes response = mobileService.getSyncErrors(commonHelperReq.getIdPerson());
        return response;
    }


    @RequestMapping(value = "/deleteSyncErrors", headers = {
            "Accept=application/json"}, method = RequestMethod.POST)
    public void deleteSyncErrors(@RequestBody CommonHelperReq commonHelperReq) {
        mobileService.deleteSyncErrors(commonHelperReq.getTaskCodes());
    }

    @RequestMapping(value = "/deleteSyncErrorsByType", headers = {
            "Accept=application/json"}, method = RequestMethod.POST)
    public void deleteSyncErrorsByType(@RequestBody CommonHelperReq commonHelperReq) {
        mobileService.deleteSyncErrorsByType(commonHelperReq.getUserID(), commonHelperReq.getCdMsgType());

    }

    @RequestMapping(value = "/authenticateMpsUser", headers = {
            "Accept=application/json"}, method = RequestMethod.POST)
    public CommonStringRes authenticateMpsUser(@RequestBody MPSVersionUpdateReq mpsVersionUpdateReq) {
        CommonStringRes commonStringRes = new CommonStringRes();
        String version = mpsVersionUpdateReq.getMpsVersion();
        String result = ServiceConstants.OUTLOOK_SUCCESS;
        String latestVersion = version;
        try {
            mobileService.authenticateMpsUser(mpsVersionUpdateReq.getUserID(), version, latestVersion);
        } catch (Exception e) {
            result = ServiceConstants.OUTLOOK_FAILURE;
        }
        commonStringRes.setCommonRes(result);
        return commonStringRes;
    }

}