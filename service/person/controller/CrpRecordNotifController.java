package us.tx.state.dfps.service.person.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.CrpRecordNotifReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.response.FormsServiceRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.CrpRecordNotifAndDetailsDto;
import us.tx.state.dfps.service.person.service.CrpRecordNotifService;

import java.util.Locale;

/**
 *
 * ImpactWS - IMPACT PHASE 2 enhancement for public Central Registry check:
 * CrpRecordController Class Description: A class to map the appropriate
 * service call for populating Central Registry Portal notifications.
 * List January 1, 2024 - 12:00:00 PM
 *
 * ********Change History**********
 *  01/24/2024 thompswa artf172946 : Initial.
 */

@RestController
@RequestMapping("/crp")
public class CrpRecordNotifController {

    @Autowired
    MessageSource messageSource;

    @Autowired
    CrpRecordNotifService crpRecordNotifService;

    private static final Logger logger = Logger.getLogger(CrpRecordNotifController.class);

    /**
     * Method Description: This Service is used to retrieve the notification
     * form for the crp record check detail screen.
     *
     * @param crpRecordNotifReq
     * @return CommonFormRes
     */
    @RequestMapping(value = "/getCrpRecordNotif", headers = {
            "Accept=application/json" }, method = RequestMethod.POST)
    public CommonFormRes getCrpRecordNotif(@RequestBody CrpRecordNotifReq crpRecordNotifReq) {

        if (TypeConvUtil.isNullOrEmpty(crpRecordNotifReq.getIdCrpCheck()))
            throw new InvalidRequestException(messageSource.getMessage("common.crpCheckId.mandatory", null, Locale.US));

        if (TypeConvUtil.isNullOrEmpty(crpRecordNotifReq.getDocType()))
            throw new InvalidRequestException(messageSource.getMessage("common.formname.mandatory", null, Locale.US));

        if (TypeConvUtil.isNullOrEmpty(crpRecordNotifReq.getIdUser()))
            throw new InvalidRequestException(messageSource.getMessage("common.idUser.mandatory", null, Locale.US));

        logger.info("TransactionId :" + crpRecordNotifReq.getTransactionId());
        return crpRecordNotifService.getCrpRecordNotif(crpRecordNotifReq);
    }

    /**
     * Method Description: This Service is used to retrieve the notification
     * form for the crp record check detail screen.
     *
     * @param crpRecordNotifReq
     * @return CommonFormRes
     */
    @RequestMapping(value = "/getCrpNotfDetails", headers = {
            "Accept=application/json" }, method = RequestMethod.POST)
    public FormsServiceRes getCrpRecordNotifAndDetails(@RequestBody CrpRecordNotifReq crpRecordNotifReq) {
        FormsServiceRes formsServiceRes = new FormsServiceRes();
        formsServiceRes.setCrpRecordNotifAndDetailsDto( crpRecordNotifService.getCrpRecordNotifAndDetails(crpRecordNotifReq));
                logger.info("TransactionId :" + crpRecordNotifReq.getTransactionId());
        return formsServiceRes;
    }
}
