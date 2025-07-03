package us.tx.state.dfps.service.waivervariance.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.WaiverVarianceReq;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.waivervariance.service.WaiverVarianceService;
import us.tx.state.dfps.web.waivervariance.dto.WaiverVarianceBean;

@RestController
@RequestMapping("/waiverVariance")
public class WaiverVarianceController {

    @Autowired
    MessageSource messageSource;
    @Autowired
    WaiverVarianceService waiverVarianceService;

    @RequestMapping(value = "/getWaiverVariance", headers = {
            "Accept=application/json"}, method = RequestMethod.POST)
    public WaiverVarianceBean getWaiverVarianceDetails(
            @RequestBody WaiverVarianceReq waiverVarianceReq) {
        if (TypeConvUtil.isNullOrEmpty(waiverVarianceReq.getIdStage())) {
            throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
        }

        return waiverVarianceService.getWaiverVariance(waiverVarianceReq);
    }


}
