package us.tx.state.dfps.service.rcciscreening.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.dto.RCCIScreeningDto;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.CommonCaseIdReq;
import us.tx.state.dfps.service.common.request.CommonStageIdReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.rcciscreening.service.RCCIScreeningService;

import java.util.Locale;

@RestController
@RequestMapping("/rcciscreening")
public class RCCIScreeningController {

    @Autowired
    MessageSource messageSource;

    @Autowired
    RCCIScreeningService rcciScreeningService;

    private static final Logger log = Logger.getLogger(RCCIScreeningController.class);


    /**
     *
     * Method Description: Method to prepopulate the intake data
     * in RCCI formal screening narrative
     * @param commonCaseIdReq
     * @return CommonFormRes
     */
    @RequestMapping(value = "/getIntakeData", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public CommonFormRes getIntakeData(@RequestBody CommonCaseIdReq commonCaseIdReq) {
        log.debug("Retrieving Intake Data :: getIntakeData");
        if (TypeConvUtil.isNullOrEmpty(commonCaseIdReq) || TypeConvUtil.isNullOrEmpty(commonCaseIdReq.getUlIdCase())) {
            throw new InvalidRequestException(
                    messageSource.getMessage("common.caseid.mandatory", null, Locale.US));
        }
        CommonFormRes commonFormRes = new CommonFormRes();
        commonFormRes
                .setPreFillData(TypeConvUtil.getXMLFormat(rcciScreeningService.getIntakeData(commonCaseIdReq.getUlIdCase())));
        return commonFormRes;
    }
}
