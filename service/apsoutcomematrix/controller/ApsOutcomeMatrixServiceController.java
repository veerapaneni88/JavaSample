package us.tx.state.dfps.service.apsoutcomematrix.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.apsoutcomematrix.service.ApsOutcomeMatrixService;
import us.tx.state.dfps.service.common.request.ApsCommonReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

import java.util.Locale;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Outcome Matrix Form/Narrative - civ34o00.
 * Jan 28th, 2022- 1:52:46 PM © 2022 Texas Department of Family and
 * Protective Services
 */

@RestController
@RequestMapping("apsOutcomeMatrix")
public class ApsOutcomeMatrixServiceController {

    @Autowired
    MessageSource messageSource;

    @Autowired
    ApsOutcomeMatrixService apsOutcomeMatrixService;

    /**
     * Method Name: getApsOutcomeMatrixData Method Description: Outcome Matrix Form/Narrative - civ34o00.
     *
     * @param apsCommonReq
     * @return PreFillDataServiceDto
     */
    @RequestMapping(value = "/getApsOutcomeMatrixData", headers = {"Accept=application/json"}, method = RequestMethod.POST)
    public CommonFormRes getApsCareFormsNarrativeData(@RequestBody ApsCommonReq apsCommonReq) {

        CommonFormRes commonFormRes = new CommonFormRes();
        if (TypeConvUtil.isNullOrEmpty(apsCommonReq.getIdCase())) {
            throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
        }
        commonFormRes.setPreFillData( TypeConvUtil.getXMLFormat(apsOutcomeMatrixService.getApsOutcomeMatrixData(apsCommonReq)));
        return commonFormRes;
    }
}
