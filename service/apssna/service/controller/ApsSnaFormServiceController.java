package us.tx.state.dfps.service.apssna.service.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.apsinhomecontact.controller.ApsInHomeContactSummaryController;
import us.tx.state.dfps.service.apssna.service.ApsSnaFormService;
import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

import java.util.Locale;


/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * ApsSnaService for APSSNA Form Service.
 * July 14, 2022- 1:52:46 PM © 2022 Texas Department of Family and
 * Protective Services
 */
@RestController
@RequestMapping("apsSNADetails")
public class ApsSnaFormServiceController {

    private static final Logger log = Logger.getLogger(ApsInHomeContactSummaryController.class);

    @Autowired
    private ApsSnaFormService apsSnaService;

    @Autowired
    private MessageSource messageSource;


    /**
     * to get ApsSna details for ApsSnaForm Service
     * @param apsCommonReq
     * @return
     */
    @RequestMapping(value = "/getApsSnaFormDetails", headers = {"Accept=application/json"}, method = RequestMethod.POST)
    public CommonFormRes getApsSnaDetails(@RequestBody ApsCommonReq apsCommonReq) {

        CommonFormRes commonFormRes = new CommonFormRes();
        if (TypeConvUtil.isNullOrEmpty(apsCommonReq.getIdStage()) || TypeConvUtil.isNullOrEmpty(apsCommonReq.getIdEvent())) {
            throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
        }
        commonFormRes.setPreFillData(
                TypeConvUtil.getXMLFormat(apsSnaService.getApsSnaDetails(apsCommonReq)));
        log.info("TransactionId :" + apsCommonReq.getTransactionId());
        return commonFormRes;
    }
}
