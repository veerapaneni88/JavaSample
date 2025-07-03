package us.tx.state.dfps.service.apscontractLogNarrative.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.apscontractLogNarrative.service.ApsContactLogNarrativeService;
import us.tx.state.dfps.service.common.request.ApsContactLogNarrativeReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

import java.util.Locale;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Declares
 * service method for APSCLN-Log of Contact Narratives December 29 2021, 2021- 2:36:56 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@RestController
@RequestMapping("/apscontactlognarrative")
public class ApsContactLogNarrativeController {

    @Autowired
    ApsContactLogNarrativeService apsContactLogNarrativeService;

    @Autowired
    MessageSource messageSource;

    private static final Logger log = Logger.getLogger(ApsContactLogNarrativeController.class);

    /**
     * @param apsContactLogNarrativeReq
     * @return CommonFormRes
     */
    // apscln
    @RequestMapping(value = "/getapscontactlognarrative", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public CommonFormRes getapscontactlognarrative(@RequestBody ApsContactLogNarrativeReq apsContactLogNarrativeReq) {

        CommonFormRes commonFormRes = new CommonFormRes();
        log.debug("Entering method apscln getapscontactlognarrative in APSContactLogNarrativeController");
        if (TypeConvUtil.isNullOrEmpty(apsContactLogNarrativeReq.getIdStage())) {
            throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
        }
        if (TypeConvUtil.isNullOrEmpty(apsContactLogNarrativeReq.getDtSampleFrom())) {
            throw new InvalidRequestException(messageSource.getMessage("common.DtSampleFrom.mandatory", null, Locale.US));
        }
        if (TypeConvUtil.isNullOrEmpty(apsContactLogNarrativeReq.getDtSampleTo())) {
            throw new InvalidRequestException(messageSource.getMessage("common.DtSampleTo.mandatory", null, Locale.US));
        }

        PreFillDataServiceDto prefillDto = apsContactLogNarrativeService.getapscontactlognarrative(apsContactLogNarrativeReq);
        commonFormRes.setPreFillData( TypeConvUtil.getXMLFormat(prefillDto));
        log.info("TransactionId :" + apsContactLogNarrativeReq.getTransactionId());
        return commonFormRes;
    }

}