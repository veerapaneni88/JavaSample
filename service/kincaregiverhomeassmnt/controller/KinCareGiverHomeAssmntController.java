package us.tx.state.dfps.service.kincaregiverhomeassmnt.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.KinCareGiverHomeDetailsReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.kincaregiverhomeassmnt.service.KinCareGiverHomeAssmntService;

import java.util.Locale;

/**
 * service-business - Kinship CareGiver Home Assessment Template (KIN12O00)
 * 07/19/2021 kurmav Artifact artf192718 : Prefill Service for KIN12O00
 */
@RestController
@RequestMapping("kincaregiverhomeassessment")
public class KinCareGiverHomeAssmntController {

    @Autowired
    MessageSource messageSource;

    @Autowired
    private KinCareGiverHomeAssmntService kinCareGiverHomeAssmntService;

    private static final Logger log = Logger.getLogger(KinCareGiverHomeAssmntController.class);

    /**
     *
     * Method Name: getKinCareGiverHomeDetails Method Description: Request to get
     * the Home Assessment Template Data in prefill data format.
     *
     * @param careGiverDetailsReq
     * @return CommonFormRes
     */
    @RequestMapping(value = "/gethomedetails", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public CommonFormRes getKinCareGiverHomeDetails(@RequestBody KinCareGiverHomeDetailsReq careGiverDetailsReq) {

        CommonFormRes commonFormRes = new CommonFormRes();
        if (TypeConvUtil.isNullOrEmpty(careGiverDetailsReq.getIdStage())) {
            throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
        }
        if (TypeConvUtil.isNullOrEmpty(careGiverDetailsReq.getIdResource())) {
            throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
        }
        commonFormRes.setPreFillData(
                TypeConvUtil.getXMLFormat(kinCareGiverHomeAssmntService.getKinCareGiverHomeDetails(careGiverDetailsReq)));

        return commonFormRes;
    }

}
