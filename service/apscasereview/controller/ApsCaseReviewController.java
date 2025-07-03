package us.tx.state.dfps.service.apscasereview.controller;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.apscasereview.service.ApsCaseReviewLegacyService;
import us.tx.state.dfps.service.apscasereview.service.ApsCaseReviewService;
import us.tx.state.dfps.service.common.request.ApsCaseReviewRequest;
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
 * Aps case review
 * Jan 21, 2022- 1:52:46 PM © 2022 Texas Department of Family and
 * Protective Services
 */
@RestController
@RequestMapping("apsCaseReview")
public class ApsCaseReviewController {

    @Autowired
    MessageSource messageSource;

    @Autowired
    ApsCaseReviewService apsCaseReviewService;

    @Autowired
    ApsCaseReviewLegacyService apsCaseReviewLegacyService;


    /**
     * Method Name: getApsCaseReviewShieldVersionInformation Method Description: Request to get
     * the Aps case review shield version in prefill data format.
     *
     * @param request
     * @return CommonFormRes
     */
    @PostMapping(value = "/getApsCaseReviewShieldVersion", headers = {"Accept=application/json"})
    public CommonFormRes getApsCaseReviewShieldVersionInformation(@RequestBody ApsCaseReviewRequest request) {

        CommonFormRes commonFormRes = new CommonFormRes();
        if (TypeConvUtil.isNullOrEmpty(request.getIdCase())) {
            throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
        }
        commonFormRes.setPreFillData(
                TypeConvUtil.getXMLFormat(apsCaseReviewService.getApsCaseReviewShieldVersionInformation(request)));

        return commonFormRes;
    }

    @PostMapping(value = "/getApsCaseReviewLegacyVersion", headers = {"Accept=application/json"})
    public CommonFormRes getApsCaseReviewLegacyVersionInformation(@RequestBody ApsCaseReviewRequest request) {

        CommonFormRes commonFormRes = new CommonFormRes();
        if (TypeConvUtil.isNullOrEmpty(request.getIdCase())) {
            throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
        }
        commonFormRes.setPreFillData(
                TypeConvUtil.getXMLFormat(apsCaseReviewLegacyService.getApsCaseReviewLegacyVersionInformation(request)));

        return commonFormRes;
    }
}
