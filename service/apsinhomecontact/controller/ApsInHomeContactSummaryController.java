package us.tx.state.dfps.service.apsinhomecontact.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.apsinhomecontact.service.ApsInHomeContactSummaryService;
import us.tx.state.dfps.service.common.request.ApsInHomeContactSummaryReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

import us.tx.state.dfps.common.exception.FormsException;
import java.util.List;

import java.util.Locale;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:  Makes
 * * service call and sends prefill string to form  OCTOBER 15, 2021- 2:36:56 PM
 * © 2017 Texas Department of Family and Protective Services
 */
@RestController
@RequestMapping("apsinhomecontactsummary")
public class ApsInHomeContactSummaryController {

    private static final Logger log = Logger.getLogger(ApsInHomeContactSummaryController.class);
    @Autowired
    private ApsInHomeContactSummaryService apsInHomeContactSummaryService;
    @Autowired
    private MessageSource messageSource;

    @RequestMapping(value = "/getContactSummary", headers = {"Accept=application/json"}, method = RequestMethod.POST)
    public CommonFormRes getContactSummaryInfo(@RequestBody ApsInHomeContactSummaryReq request) {
        log.info("Entering method  getContactSummaryInfo (CINV66S) in APSInHomeContactSummaryController:" + request.getIdStage());
        if (TypeConvUtil.isNullOrEmpty(request)) {
            throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
        }
        CommonFormRes commonFormRes = new CommonFormRes();
        commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(apsInHomeContactSummaryService.getLawEnforcementDetails(request)));
        return commonFormRes;
    }

    @RequestMapping(value = "/getGuardianshipReferralSummary", headers = {"Accept=application/json"}, method = RequestMethod.POST)
    public CommonFormRes getGuardianshipReferralSummary(@RequestBody ApsInHomeContactSummaryReq request) {
        log.info("Entering method  getGuardianshipReferralSummary (CCON30S) in APSInHomeContactSummaryController:" + request.getIdStage());
        if (TypeConvUtil.isNullOrEmpty(request)) {
            throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
        }
        List<String> errorMessageList = apsInHomeContactSummaryService.getValidationMessages(request);
        for (String errorMessage : errorMessageList) {
            if(!TypeConvUtil.isNullOrEmpty(errorMessage)) {
                throw new FormsException(messageSource.getMessage(errorMessage, null, Locale.US));
            }
        }
        CommonFormRes commonFormRes = new CommonFormRes();
        commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(apsInHomeContactSummaryService.getGuardianshipReferralDetails(request)));
        log.info("Entering method  getGuardianshipReferralSummary (CCON30S) in APSInHomeContactSummaryController:prefill data:" + commonFormRes.getPreFillData());

        return commonFormRes;
    }

}
