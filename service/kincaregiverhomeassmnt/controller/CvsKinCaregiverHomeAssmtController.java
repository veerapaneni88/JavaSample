package us.tx.state.dfps.service.kincaregiverhomeassmnt.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.PopulateFormReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.kincaregiverhomeassmnt.service.CvsKinCaregiverHomeAssmtService;

import java.util.Locale;

/**
 * service-business - Kinship CareGiver Home Assessment (CVSKINHOMEASSESSMENT)
 * 02/20/2025 thompswa ppm84014 : Prefill Service for CVSKINHOMEASSESSMENT
 */
@RestController
@RequestMapping("cvskincaregiverhomeassmt")
public class CvsKinCaregiverHomeAssmtController {

    @Autowired
    MessageSource messageSource;

    @Autowired
    private CvsKinCaregiverHomeAssmtService cvsKinCaregiverHomeAssmtService;

    private static final Logger log = Logger.getLogger(CvsKinCaregiverHomeAssmtController.class);

    /**
     *
     * Method Name: getKinCareGiverHomeDetails Method Description: Request to get
     * the Home Assessment Template Data in prefill data format.
     *
     * @param kinCaregiverReq
     * @return CommonFormRes
     */
    @RequestMapping(value = "/getassmtresult", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public CommonFormRes getCvsKinCaregiverHomeAssmt(@RequestBody PopulateFormReq kinCaregiverReq) {

        CommonFormRes commonFormRes = new CommonFormRes();
        if (TypeConvUtil.isNullOrEmpty(kinCaregiverReq.getIdEvent())) {
            throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
        }
        commonFormRes.setPreFillData(
                TypeConvUtil.getXMLFormat(cvsKinCaregiverHomeAssmtService.getCvsKinCaregiverHomeAssmt(kinCaregiverReq)));

        return commonFormRes;
    }
}
