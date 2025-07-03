package us.tx.state.dfps.service.apsinvsummary.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.apsinvsummary.service.ApsInvSummaryService;
import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

import java.util.Locale;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Aps Investigation Summary -- CFIV1200.
 * Dec 14, 2021- 1:52:46 PM © 2021 Texas Department of Family and
 * Protective Services
 */

@RestController
@RequestMapping("apsinvsummarydata")
public class ApsInvSummaryServiceController {

    @Autowired
    MessageSource messageSource;

    @Autowired
    ApsInvSummaryService apsInvSummaryService;

    private static final Logger log = Logger.getLogger(ApsInvSummaryServiceController.class);

    /**
     * Method Name: getapslettertoclientdata Method Description: Request to get
     * the Aps investigation Summary in prefill data format.
     *
     * @param apsCommonReq
     * @return CommonFormRes
     */
    @RequestMapping(value = "/getapsinvsummarydata", headers = {"Accept=application/json"}, method = RequestMethod.POST)
    public CommonFormRes getapsinvsummarydata(@RequestBody ApsCommonReq apsCommonReq) {

        CommonFormRes commonFormRes = new CommonFormRes();
        if (TypeConvUtil.isNullOrEmpty(apsCommonReq.getIdStage())) {
            throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
        }
        commonFormRes.setPreFillData(
                TypeConvUtil.getXMLFormat(apsInvSummaryService.getapsinvsummarydata(apsCommonReq)));

        log.info("TransactionId :" + apsCommonReq.getTransactionId());
        return commonFormRes;
    }
}
