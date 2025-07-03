package us.tx.state.dfps.service.apssafetyassmt.controller;


import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.apssafetyassmt.service.ApsSafetyAssessmentService;
import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * service-business- IMPACT APS MODERNIZATION Class Description:
 * Aps Safety Assessment -- apssa.
 * Jan 04, 2022- 1:52:46 PM © 2022 Texas Department of Family and
 * Protective Services
 */
@RestController
@RequestMapping("apsSafetyAssessment")
public class ApsSafetyAssessmentController {

    @Autowired
    MessageSource messageSource;

    @Autowired
    ApsSafetyAssessmentService apsSafetyAssessmentService;

    /**
     * Method Name: getApsSafetyAssessmentFormInformation Method Description: Request to get
     * the Aps Safety Assessment in prefill data format.
     *
     * @param request
     * @return CommonFormRes
     */
    @PostMapping(value = "/getApsSafetyAssessmentReport", headers = {"Accept=application/json"})
    public CommonFormRes getApsSafetyAssessmentFormInformation(@RequestBody ApsCommonReq request) {

        CommonFormRes commonFormRes = new CommonFormRes();
        if (TypeConvUtil.isNullOrEmpty(request.getIdStage())) {
            throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
        }
        if (TypeConvUtil.isNullOrEmpty(request.getIdEvent())) {
            throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
        }
        commonFormRes.setPreFillData(
                TypeConvUtil.getXMLFormat(apsSafetyAssessmentService.getApsSafetyAssessmentFormInformation(request)));

        return commonFormRes;
    }

}
