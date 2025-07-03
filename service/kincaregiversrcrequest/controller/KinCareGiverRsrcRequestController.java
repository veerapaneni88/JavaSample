package us.tx.state.dfps.service.kincaregiversrcrequest.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.KinCareGiverResourceReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.kincaregiverrsrcrequest.service.KinCareGiverResourceRequestService;

import java.util.Locale;

/**
 * Kinship Caregiver Resource/Contract Request Template (KIN10O00)
 * 07/21/2021 kurmav Artifact artf192721 : Prefill Service for KIN10O00
 */
@RestController
@RequestMapping("kincaregiverresourcerequest")
public class KinCareGiverRsrcRequestController {

    @Autowired
    MessageSource messageSource;

    @Autowired
    KinCareGiverResourceRequestService kinCareGiverRsrcReqService;

    private static final Logger log = Logger.getLogger(KinCareGiverRsrcRequestController.class);

    /**
     * Method Name: getKinCareGiverResource Method Description: Request to get
     * Kinship Caregiver Resource/Contract Request Template in prefill data format
     *
     * @param careGiverResourceReq
     * @return
     */
    @RequestMapping(value = "/getresource", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public CommonFormRes getKinCareGiverResource(@RequestBody KinCareGiverResourceReq careGiverResourceReq) {

        CommonFormRes commonFormRes = new CommonFormRes();
        if (TypeConvUtil.isNullOrEmpty(careGiverResourceReq.getIdStage())) {
            throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
        }
        if (TypeConvUtil.isNullOrEmpty(careGiverResourceReq.getIdResource())) {
            throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
        }
        if (TypeConvUtil.isNullOrEmpty(careGiverResourceReq.getIdPerson())) {
            throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
        }
        if (TypeConvUtil.isNullOrEmpty(careGiverResourceReq.getIdCase())) {
            throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
        }
        commonFormRes.setPreFillData(
                TypeConvUtil.getXMLFormat(kinCareGiverRsrcReqService.getKinCareGiverResource(careGiverResourceReq)));

        return commonFormRes;
    }
}
