package us.tx.state.dfps.service.casemanagement.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.dto.CloseOpenStageInputDto;
import us.tx.state.dfps.common.dto.CloseOpenStageOutputDto;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.casemanagement.service.AdminReviewService;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.response.CloseOpenStageRes;
import us.tx.state.dfps.service.common.response.CommonAppShortFormRes;
import us.tx.state.dfps.service.common.response.CpsInvNoticesRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.workload.service.CloseOpenStageService;

import java.util.Locale;

@RestController
@RequestMapping("/adminreview")
public class AdminReviewController {

    @Autowired
    AdminReviewService adminReviewService;

    @Autowired
    MessageSource messageSource;

    @RequestMapping(value = "/createARI", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public CloseOpenStageRes createAdminReview(@RequestBody CloseOpenStageInputDto closeOpenStageInputDto) {

        if (TypeConvUtil.isNullOrEmpty(closeOpenStageInputDto.getIdStage())) {
            throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
        }

        CloseOpenStageRes closeOpenStageRes = adminReviewService.createAdminReview(closeOpenStageInputDto);
        return closeOpenStageRes;

    }
}
