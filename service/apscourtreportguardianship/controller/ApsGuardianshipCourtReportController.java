package us.tx.state.dfps.service.apscourtreportguardianship.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.apscourtreportguardianship.service.ApsGuardianshipCourtReportService;
import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;


import java.util.Locale;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Aps Guardianship Court Report Service -- aps court report for guardianship CIV22O00.
 * Dec 28, 2021- 1:52:46 PM © 2021 Texas Department of Family and
 * Protective Services
 */

@RestController
@RequestMapping("apsGuardianship")
public class ApsGuardianshipCourtReportController {

    @Autowired
    MessageSource messageSource;

    @Autowired
    ApsGuardianshipCourtReportService apsGuardianshipCourtReportService;

    private static final Logger log = Logger.getLogger(ApsGuardianshipCourtReportController.class);

    private static final String mandatoryString ="common.input.mandatory";

    /**
     *
     * Method Name: getApsGuardianshipCourtReport Method Description: Aps Guardianship Court report data  in prefill data format.
     * @param apsCommonReq
     * @return CommonFormRes
     */
    @PostMapping(value = "/getApsGuardianshipCourtReport", headers = { "Accept=application/json" })
    public CommonFormRes getApsGuardianshipCourtReport(@RequestBody ApsCommonReq apsCommonReq) {

        CommonFormRes commonFormRes = new CommonFormRes();
        if (TypeConvUtil.isNullOrEmpty(apsCommonReq.getIdStage())) {
            throw new InvalidRequestException(messageSource.getMessage(mandatoryString, null, Locale.US));
        }
        commonFormRes.setPreFillData(
                TypeConvUtil.getXMLFormat(apsGuardianshipCourtReportService.getApsGuardianshipCourtReport(apsCommonReq)));

        log.info("TransactionId :" + apsCommonReq.getTransactionId());
        return commonFormRes;
    }


    /**
     *
     * Method Name: getApsGuardianshipDetailsReport Method Description: Request to get
     * the Service Auth Form Data in prefill data format.
     * @param apsGuardianshipDetailReportReq
     * @return CommonFormRes
     */
    @PostMapping(value = "/getApsGuardianshipDetailsReport", headers = { "Accept=application/json" })
    public CommonFormRes getApsGuardianshipDetailsReport(@RequestBody ApsCommonReq apsGuardianshipDetailReportReq) {

        CommonFormRes commonFormRes = new CommonFormRes();
        if (TypeConvUtil.isNullOrEmpty(apsGuardianshipDetailReportReq.getIdStage())) {
            throw new InvalidRequestException(messageSource.getMessage(mandatoryString, null, Locale.US));
        }
        if (TypeConvUtil.isNullOrEmpty(apsGuardianshipDetailReportReq.getIdEvent())) {
            throw new InvalidRequestException(messageSource.getMessage(mandatoryString, null, Locale.US));
        }
        commonFormRes.setPreFillData(
                TypeConvUtil.getXMLFormat(apsGuardianshipCourtReportService.getApsGuardianshipDetailsReport(apsGuardianshipDetailReportReq)));

        return commonFormRes;
    }
}
