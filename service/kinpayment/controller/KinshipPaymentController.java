package us.tx.state.dfps.service.kinpayment.controller;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.service.common.request.KinCareGiverResourceReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.kinpayment.service.KinshipPaymentService;

@RestController
@RequestMapping("kinshippayment")
public class KinshipPaymentController {

    private static final Logger log = Logger.getLogger(KinshipPaymentController.class);
    @Autowired
    MessageSource messageSource;
    @Autowired
    KinshipPaymentService kinshipPaymentService;

    @RequestMapping(value = "/getcaregiverchilddetails", headers = {"Accept=application/json"}, method = RequestMethod.POST)
    public CommonFormRes getCareGiverChildDetails(@RequestBody KinCareGiverResourceReq kinCareGiverResourceReq) {

        CommonFormRes commonFormRes = new CommonFormRes();
        commonFormRes.setPreFillData(
                TypeConvUtil.getXMLFormat(kinshipPaymentService.getKinReimbursementPaymentDetails(kinCareGiverResourceReq)));
        return commonFormRes;
    }

    @RequestMapping(value = "/getcaregiverdetails", headers = {"Accept=application/json"}, method = RequestMethod.POST)
    public CommonFormRes getCareGiverDetails(@RequestBody KinCareGiverResourceReq kinCareGiverResourceReq) {

        CommonFormRes commonFormRes = new CommonFormRes();
        commonFormRes.setPreFillData(
                TypeConvUtil.getXMLFormat(kinshipPaymentService.getKinIntegrationPaymentDetails(kinCareGiverResourceReq)));
        return commonFormRes;
    }

}
