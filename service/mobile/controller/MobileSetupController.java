package us.tx.state.dfps.service.mobile.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.SyncMPSStatisticsReq;
import us.tx.state.dfps.service.common.request.UserTokenDetailReq;
import us.tx.state.dfps.service.common.response.CommonBooleanRes;
import us.tx.state.dfps.service.common.response.CommonCountRes;
import us.tx.state.dfps.service.common.response.CommonDateRes;
import us.tx.state.dfps.service.common.response.UserTokenDetailRes;
import us.tx.state.dfps.service.mobile.service.MobileSetupService;

@RestController
@RequestMapping("/mobile")
public class MobileSetupController {

    private static final Logger logger = Logger.getLogger(MobileSetupController.class);
    @Autowired
    MobileSetupService mobileSetupService;
    @Autowired
    MessageSource messageSource;

    @RequestMapping(value = "/completeUserSetup", headers = {
            "Accept=application/json"}, method = RequestMethod.POST)
    public CommonBooleanRes completeMPSUserSetup(@RequestBody CommonHelperReq commonHelperReq) {
        CommonBooleanRes response = new CommonBooleanRes();
        response.setExists(Boolean.TRUE);
        mobileSetupService.completeSetup(commonHelperReq);
        return response;
    }

    @RequestMapping(value = "/getCodesTablesRowCount", headers = {
            "Accept=application/json"}, method = RequestMethod.POST)
    public CommonCountRes getCodesTablesRowCount() {
        CommonCountRes resp = new CommonCountRes();
        Long count;
        count = mobileSetupService.getCodesTablesRowCount();
        resp.setCount(count);
        return resp;
    }

    @RequestMapping(value = "/getCodesTablesEndDatedRowCount", headers = {
            "Accept=application/json"}, method = RequestMethod.POST)
    public CommonCountRes getCodesTablesEndDatedRowCount() {
        CommonCountRes resp = new CommonCountRes();
        Long count;
        count = mobileSetupService.getCodesTablesEndDatedRowCount();
        resp.setCount(count);
        return resp;
    }

    @PostMapping(value = "/saveTokenBody")
    public void saveTokenBody(@RequestBody UserTokenDetailReq body) {
        mobileSetupService.saveTokenBody(body.getUserDetailsDto());
    }

    @GetMapping(value = "/getTokenBody")
    public UserTokenDetailRes getTokenBody() {
        UserTokenDetailRes res = new UserTokenDetailRes();
        res.setUserDetailsDto(mobileSetupService.getTokenBody());
        return res;
    }

    @GetMapping(value = "/getSyncStats")
    public CommonDateRes getSyncStats() {
        CommonDateRes dateRes = new CommonDateRes();
        dateRes.setDate(mobileSetupService.getMPSSyncStats());
        return dateRes;
    }

    @RequestMapping(value = "/saveMpsSyncStats", headers = {
            "Accept=application/json"}, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonBooleanRes saveMpsSyncStatistics(@RequestBody SyncMPSStatisticsReq syncMPSStatisticsReq) {
        mobileSetupService.saveMpsSyncStatistics(syncMPSStatisticsReq.getSyncStatisticsDto());
        CommonBooleanRes response = new CommonBooleanRes();
        response.setExists(Boolean.TRUE);
        return response;
    }


}
