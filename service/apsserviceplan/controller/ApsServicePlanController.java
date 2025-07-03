package us.tx.state.dfps.service.apsserviceplan.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.apsserviceplan.service.ApsServicePlanService;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.response.CommonCountRes;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

import java.util.Locale;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Aps Service Paln -- APSSP.
 */

@RestController
@RequestMapping("apsServicePlan")
public class ApsServicePlanController {

    @Autowired
    MessageSource messageSource;

    @Autowired
    ApsServicePlanService apsServicePlanService;

    private static final Logger log = Logger.getLogger(ApsServicePlanController.class);

    /**
     * Method Name: getApsServicePlanReport Method Description: Request to get
     * the Aps Service Paln in prefill data format.
     *
     * @param commonHelperReq
     * @return CommonFormRes
     */
    @PostMapping(value = "/getApsServicePlanReport", headers = {"Accept=application/json"})
    public CommonFormRes getApsServicePlanReport(@RequestBody CommonHelperReq commonHelperReq) {

        CommonFormRes commonFormRes = new CommonFormRes();
        if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
            throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
        }
        commonFormRes.setPreFillData(
                TypeConvUtil.getXMLFormat(apsServicePlanService.getApsServicePlanReport(commonHelperReq)));

        log.info("TransactionId :" + commonHelperReq.getTransactionId());
        log.info("commonFormRes :" + commonFormRes.getPreFillData());
        return commonFormRes;

    }

    /**
     * Method helps to get the service call from Web and communicate with service
     *
     * @param commonHelperReq - requested common helper data
     * @return - return the common counter data
     */
    @PostMapping(value = "/pcsActionCategoryCount", headers = {"Accept=application/json"})
    public CommonCountRes getPcsActionCategoryCount(@RequestBody CommonHelperReq commonHelperReq) {
        CommonCountRes commonCountRes = new CommonCountRes();
        if (TypeConvUtil.isNullOrEmpty(commonHelperReq.getIdStage())) {
            throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
        }
        commonCountRes.setCount(apsServicePlanService.getPcsActionCategoryCount(commonHelperReq.getIdStage()));
        return commonCountRes;
    }
}
