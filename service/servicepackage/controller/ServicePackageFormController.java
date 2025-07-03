package us.tx.state.dfps.service.servicepackage.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.ServicePackageReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.servicepackage.service.ServicePackageFormService;

import java.util.Locale;

/**
 *
 */
@RestController
@RequestMapping("/ServicePackage")
public class ServicePackageFormController {

    @Autowired
    MessageSource messageSource;

    @Autowired
    ServicePackageFormService servicePackageFormService;

    private static final Logger log = Logger.getLogger(ServicePackageFormController.class);
    /**
     *
     * @param subcareLOCFromReq
     * @return
     */
    @RequestMapping(value = "/getServicePackageDetails", headers = {
            "Accept=application/json" }, method = RequestMethod.POST)
    public CommonFormRes getServicePackageDetails(@RequestBody ServicePackageReq servicePackageReq) {

        if (TypeConvUtil.isNullOrEmpty(servicePackageReq.getIdStage())) {
            throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
        }
        if (TypeConvUtil.isNullOrEmpty(servicePackageReq.getCaseId())) {
            throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
        }
        if (TypeConvUtil.isNullOrEmpty(servicePackageReq.getSvcPkgId())) {
            throw new InvalidRequestException(messageSource.getMessage("common.personid.mandatory", null, Locale.US));
        }
        CommonFormRes commonFormRes = new CommonFormRes();
        commonFormRes.setPreFillData(
                TypeConvUtil.getXMLFormat(servicePackageFormService.getServicePackageDetails(servicePackageReq)));
        log.info("TransactionId :" + servicePackageReq.getTransactionId());
        return commonFormRes;
    }
}
