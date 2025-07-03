package us.tx.state.dfps.service.cciinvreport.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.cciinvreport.service.CciInvReportService;
import us.tx.state.dfps.service.common.request.CciInvReportReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;


import java.util.Locale;
@RestController
@RequestMapping("cciinvreport")
public class CciInvReportController {
    @Autowired
    MessageSource messageSource;

    @Autowired
    CciInvReportService cciInvReportService;

    private static final Logger log = Logger.getLogger(CciInvReportController.class);

    /**
     *
     * Method Description:
     *
     * @param cciInvReportReq
     * @return commonFormRes
     */

    @RequestMapping(value = "/getcciinvreport", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public CommonFormRes getCciInvReport(@RequestBody CciInvReportReq cciInvReportReq){
        log.info("TransactionId :" + cciInvReportReq.getTransactionId());

        CommonFormRes commonFormRes = new CommonFormRes();
        commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(cciInvReportService.getCciInvReport(cciInvReportReq)));
        return commonFormRes;
    }
}
