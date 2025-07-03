package us.tx.state.dfps.service.apscareformsnarrative.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.apscareformsnarrative.service.ApsCareFormsNarrativeService;
import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

import java.util.Locale;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Aps Care Forms Narrative - civ35o00.
 * Jan 19th, 2022- 1:52:46 PM © 2022 Texas Department of Family and
 * Protective Services
 */

@RestController
@RequestMapping("apsCareFormsNarrative")
public class ApsCareFormsNarrativeServiceController {

    public static final String COMMON_INPUT_MANDATORY = "common.input.mandatory";
    @Autowired
    MessageSource messageSource;

    @Autowired
    ApsCareFormsNarrativeService apsCareFormsNarrativeService;

    /**
     * Method Name: getApsCareFormsNarrativedata Method Description: Request to get
     * Aps Care Forms Narrative - civ35o00 in prefill data format.
     *
     * @param apsCommonReq
     * @return CommonFormRes
     */

    @RequestMapping(value = "/getApsCareFormsNarrativeData", headers = {"Accept=application/json"}, method = RequestMethod.POST)
    public CommonFormRes getApsCareFormsNarrativeData(@RequestBody ApsCommonReq apsCommonReq) {

        CommonFormRes commonFormRes = new CommonFormRes();
        if (TypeConvUtil.isNullOrEmpty(apsCommonReq.getIdCase())) {
            throw new InvalidRequestException(messageSource.getMessage(COMMON_INPUT_MANDATORY, null, Locale.US));
        }
        if (TypeConvUtil.isNullOrEmpty(apsCommonReq.getIdPerson())) {
            throw new InvalidRequestException(messageSource.getMessage(COMMON_INPUT_MANDATORY, null, Locale.US));
        }
        if (TypeConvUtil.isNullOrEmpty(apsCommonReq.getIdEvent())) {
            throw new InvalidRequestException(messageSource.getMessage(COMMON_INPUT_MANDATORY, null, Locale.US));
        }

        commonFormRes.setPreFillData( TypeConvUtil.getXMLFormat(apsCareFormsNarrativeService.getApsCareFormsNarrativeData(apsCommonReq)));
        return commonFormRes;
    }
}
