package us.tx.state.dfps.service.apsIdentifiedProblemsSummary.controller;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.apsIdentifiedProblemsSummary.service.ApsIdentifiedProblemsSummaryService;
import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Aps Identified Problems Summary form Service -- cfiv0700
 * Jan 26, 2022 © 2022 Texas Department of Family and
 * Protective Services
 */

@RestController
@RequestMapping("apsIdentifiedProblemsSummary")
public class ApsIdentifiedProblemsSummaryController {

    @Autowired
    MessageSource messageSource;

    @Autowired
    ApsIdentifiedProblemsSummaryService apsIdentifiedProblemsSummaryService;

    private static final Logger log = Logger.getLogger(ApsIdentifiedProblemsSummaryController.class);

    /**
     * Method Name: getIdentifiedProblemsSummaryData
     * Method Description: API call to retrieve data for Identified Problems Summary form,cfiv0700
     * service to retrieve data
     *
     * @param apsCommonReq
     * @return CommonFormRes
     */
    @PostMapping(value = "/getIdentifiedProblemsSummary", headers = {"Accept=application/json"})
    public CommonFormRes getIdentifiedProblemsSummaryData(@RequestBody ApsCommonReq apsCommonReq) {

        CommonFormRes commonFormRes = new CommonFormRes();
        if (TypeConvUtil.isNullOrEmpty(apsCommonReq.getIdStage()) || TypeConvUtil.isNullOrEmpty(apsCommonReq.getIdEvent())) {
            throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
        }
        commonFormRes.setPreFillData(
                TypeConvUtil.getXMLFormat(apsIdentifiedProblemsSummaryService.getIdentifiedProblemsSummaryDetails(apsCommonReq)));
        log.info("TransactionId :" + apsCommonReq.getTransactionId());
        return commonFormRes;
    }
}
