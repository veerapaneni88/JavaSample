package us.tx.state.dfps.service.apslettertoclient.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.apslettertoclient.service.ApslettertoClientService;
import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.servicauthform.controller.ServiceAuthFormController;

import java.util.Locale;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Aps Letter to Client when cannot locate-- CIV37o00.
 * Dec 02, 2021- 1:52:46 PM © 2021 Texas Department of Family and
 * Protective Services
 */

@RestController
@RequestMapping("apslettertoclientdata")
public class ApslettertoClientServiceController {
    @Autowired
    MessageSource messageSource;

    @Autowired
    ApslettertoClientService apsletterClientService;

    private static final Logger log = Logger.getLogger(ServiceAuthFormController.class);

    /**
     *
     * Method Name: getInhomeServiceAuthFormData Method Description: Request to get
     * the Service Auth Form Data in prefill data format.
     *
     * @param apsCommonReq
     * @return CommonFormRes
     */
    @RequestMapping(value = "/getapslettertoclientdata", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public CommonFormRes getInhomeServiceAuthFormData(@RequestBody ApsCommonReq apsCommonReq) {

        CommonFormRes commonFormRes = new CommonFormRes();
        if (TypeConvUtil.isNullOrEmpty(apsCommonReq.getIdStage())) {
            throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
        }
        commonFormRes.setPreFillData(
                TypeConvUtil.getXMLFormat(apsletterClientService.getApsLettertoClientData(apsCommonReq)));

        log.info("TransactionId :" + apsCommonReq.getTransactionId());
        return commonFormRes;
    }
}
