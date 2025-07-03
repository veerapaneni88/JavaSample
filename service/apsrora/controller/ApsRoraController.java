package us.tx.state.dfps.service.apsrora.controller;


import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.apsrora.service.ApsRoraService;
import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;


import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Aps Risk of recidivism Assessment -- apsrora.
 * Dec 29, 2021- 1:52:46 PM © 2021 Texas Department of Family and
 * Protective Services
 */
@RestController
@RequestMapping("apsRiskOfRecidivismAssessment")
public class ApsRoraController {

    @Autowired
    MessageSource messageSource;

    @Autowired
    ApsRoraService apsRoraService;

    /**
     * Method Name: getapsroradata Method Description: Request to get
     * the Aps Risk of recidivism Assessment in prefill data format.
     *
     * @param request
     * @return CommonFormRes
     */
    @PostMapping(value = "/getApsRoraReport", headers = {"Accept=application/json"})
    public CommonFormRes getApsRoraFormInformation(@RequestBody ApsCommonReq request) {

        CommonFormRes commonFormRes = new CommonFormRes();
        if (TypeConvUtil.isNullOrEmpty(request.getIdStage())) {
            throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
        }
        if (TypeConvUtil.isNullOrEmpty(request.getIdEvent())) {
            throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
        }
        commonFormRes.setPreFillData(
                TypeConvUtil.getXMLFormat(apsRoraService.getApsRoraFormInformation(request)));

        return commonFormRes;
    }

}
