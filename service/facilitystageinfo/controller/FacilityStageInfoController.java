package us.tx.state.dfps.service.facilitystageinfo.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.FacilityStageInfoReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.facilitystageinfo.service.FacilityStageInfoService;
import us.tx.state.dfps.service.forms.util.FacilityStageInfoPrefillData;

import java.util.Locale;

/**
 * service-business - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CFAD01S
 * Class Description: Controller class for services related to facilitystageinfo. Apr
 * 20,2017 - 4:29:18 PM
 */

@RestController
@RequestMapping("/facilitystageinfo")
public class FacilityStageInfoController {

    @Autowired
    MessageSource messageSource;

    @Autowired
    FacilityStageInfoService facilityStageInfoService;
    @Autowired
    FacilityStageInfoPrefillData facilityStageInfoPrefillData;

    private static final Logger logger = Logger.getLogger(FacilityStageInfoController.class);

    /**
     *
     * Method Description: Method to update person details in the CVS Home
     * window. This method is also retrieve the saved data. EJB - CVS FA HOME
     *
     * @param facilityStageInfoReq
     * @return CommonFormRes
     */

    @RequestMapping(value = "/getfacilitystageinfo", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public  CommonFormRes getFacilityStageInfo(@RequestBody FacilityStageInfoReq facilityStageInfoReq) {

        if (TypeConvUtil.isNullOrEmpty(facilityStageInfoReq.getIdStage())) {
            throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
        }
        CommonFormRes commonFormRes = new CommonFormRes();
        commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(facilityStageInfoPrefillData.returnPrefillData(
                facilityStageInfoService.getFacilityInfo(facilityStageInfoReq))));
        logger.info("StageId :" + facilityStageInfoReq.getIdStage());

        return commonFormRes;
    }

}
