package us.tx.state.dfps.service.inhomeservicauthform.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.inhomeservicauthform.service.InhomeServiceAuthFormService;
import us.tx.state.dfps.service.servicauthform.controller.ServiceAuthFormController;

import java.util.Locale;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Inhome Service
 * Authorization form-CCN02O00 used by APS to refer clients for paid services under PRS
 * contracts. Nov 5, 2021- 1:31:27 PM © 2021 Texas Department of Family and
 * Protective Services
 */

@RestController
@RequestMapping("inhomeserviceauthform")
public class InHomeServiceAuthFormController {

    @Autowired
    MessageSource messageSource;

    @Autowired
    InhomeServiceAuthFormService inhomeserviceAuthFormService;

    private static final Logger log = Logger.getLogger(InHomeServiceAuthFormController.class);

    /**
     *
     * Method Name: getServiceAuthFormData Method Description: Request to get
     * the Service Auth Form Data in prefill data format.
     *
     * @param apsCommonReq
     * @return CommonFormRes
     */
    @RequestMapping(value = "/getinhomeserviceauthdata", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public CommonFormRes getInhomeServiceAuthFormData(@RequestBody ApsCommonReq apsCommonReq) {

        CommonFormRes commonFormRes = new CommonFormRes();
        if (TypeConvUtil.isNullOrEmpty(apsCommonReq.getIdEvent())) {
            throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
        }
        commonFormRes.setPreFillData(
                TypeConvUtil.getXMLFormat(inhomeserviceAuthFormService.getInhomeServiceAuthFormData(apsCommonReq)));

        log.info("TransactionId :" + apsCommonReq.getTransactionId());
        return commonFormRes;
    }
}
