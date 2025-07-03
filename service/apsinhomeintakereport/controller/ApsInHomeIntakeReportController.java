package us.tx.state.dfps.service.apsinhomeintakereport.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.apsinhomeintakereport.service.ApsInHomeIntakeReportService;
import us.tx.state.dfps.service.apsintakereport.controller.ApsIntakeReportController;
import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

import java.util.Locale;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Sends input
 * data to service for APS Inhome Intake Reports Nov 02, 2021- 3:43:13 PM © 2017 Texas Department of Family
 * and Protective Services
 */
@RestController
@RequestMapping("apsinhomeintake")
public class ApsInHomeIntakeReportController {

    @Autowired
    private ApsInHomeIntakeReportService apsInHomeIntakeReportService;

    @Autowired
    private MessageSource messageSource;

    private static final Logger log = Logger.getLogger(ApsInHomeIntakeReportController.class);

    /**
     * CFIN0300
     * Method Name: getReportDataNoRep Method Description: Request to get
     *  Aps Inhome Intake Report Minus no report Data in prefill data format.
     * @param apsCommonReq
     * @return
     */
    @RequestMapping(value = "/getapsinhomereport", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public CommonFormRes getReportData(@RequestBody ApsCommonReq apsCommonReq) {
        log.debug("Entering method CINT40S getReportDataNoRep in ApsIntakeReportController");
        if (TypeConvUtil.isNullOrEmpty(apsCommonReq)) {
            throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
        }
        CommonFormRes commonFormRes = new CommonFormRes();
        commonFormRes.setPreFillData(
                TypeConvUtil.getXMLFormat(apsInHomeIntakeReportService.getIntakeReport(apsCommonReq)));

        return commonFormRes;
    }

    /**
     * CFIN0700
     * Method Name: getReportDataNoRep Method Description: Request to get
     *  Aps Inhome Intake Report Minus no report Data in prefill data format.
     * @param apsCommonReq
     * @return
     */
    @RequestMapping(value = "/getapsinhomereportnorep", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public CommonFormRes getReportDataNoRep(@RequestBody ApsCommonReq apsCommonReq) {
        log.debug("Entering method CINT44S getReportDataNoRep in ApsIntakeReportController");
        if (TypeConvUtil.isNullOrEmpty(apsCommonReq)) {
            throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
        }
        CommonFormRes commonFormRes = new CommonFormRes();
        commonFormRes.setPreFillData(
                TypeConvUtil.getXMLFormat(apsInHomeIntakeReportService.getapsinhomereportnorep(apsCommonReq)));

        return commonFormRes;
    }


}
