package us.tx.state.dfps.service.sscc.controller;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.SSCCEmployeeDetailReq;
import us.tx.state.dfps.service.common.response.SSCCEmployeeDetailRes;
import us.tx.state.dfps.service.sscc.service.SSCCEmployeeDetailService;
import us.tx.state.dfps.web.sscc.bean.SSCCEmployeeDetailsBean;

import java.util.Locale;

@RestController
@RequestMapping("/ssccEmployeeDetail")
public class SSCCEmployeeDetailController {

    private static final Logger log = Logger.getLogger(SSCCEmployeeDetailController.class);

    @Autowired
    SSCCEmployeeDetailService SSCCEmployeeDetailService;

    @Autowired
    MessageSource messageSource;

    @RequestMapping(value = "/getWorkerCountyCodes", headers = {"Accept=application/json"}, method = RequestMethod.POST)
    public SSCCEmployeeDetailRes getWorkerCountyCodes(@RequestBody SSCCEmployeeDetailReq request) throws Exception {
        if (ObjectUtils.isEmpty(request) || ObjectUtils.isEmpty(request.getPersonId())) {
            throw new InvalidRequestException(
                    messageSource.getMessage("sscc.employee.details.mandatory", null, Locale.US));
        }
        return SSCCEmployeeDetailService.getWorkerCountyCodes(request.getPersonId());
    }

    @RequestMapping(value = "/getCBCAreaRegion", headers = {"Accept=application/json"}, method = RequestMethod.POST)
    public SSCCEmployeeDetailRes getCBCAreaRegion(@RequestBody SSCCEmployeeDetailReq request) throws Exception {
        if (ObjectUtils.isEmpty(request) || ObjectUtils.isEmpty(request.getPersonId())) {
            throw new InvalidRequestException(
                    messageSource.getMessage("sscc.employee.details.mandatory", null, Locale.US));
        }
        return SSCCEmployeeDetailService.getCBCAreaRegion(request.getPersonId());
    }

    @RequestMapping(value = "/saveSSCCEmployeeDetails", headers = {"Accept=application/json"}, method = RequestMethod.POST)
    public SSCCEmployeeDetailRes saveSSCCEmployeeDetails(@RequestBody SSCCEmployeeDetailReq request) throws Exception {
        if (ObjectUtils.isEmpty(request) || ObjectUtils.isEmpty(request.getSsccEmployeeDetails())) {
            throw new InvalidRequestException(
                    messageSource.getMessage("sscc.employee.details.mandatory", null, Locale.US));
        }
        SSCCEmployeeDetailRes response = new SSCCEmployeeDetailRes();
        SSCCEmployeeDetailsBean ssccEmployeeDetailsSavedBean = SSCCEmployeeDetailService.saveSSCCEmployeeDetails(request.getSsccEmployeeDetails());
        if(!ObjectUtils.isEmpty(ssccEmployeeDetailsSavedBean) && CollectionUtils.isEmpty(ssccEmployeeDetailsSavedBean.getErrors())){
            response =  SSCCEmployeeDetailService.getSSCCEmployeeDetailslUsingPersonId(request.getSsccEmployeeDetails().getPersonId());
        }else{
            response.setSsccEmployeeDetails(ssccEmployeeDetailsSavedBean);
        }
        return response;
    }

    @RequestMapping(value = "/getSSCCEmployeeDetails", headers = {"Accept=application/json"}, method = RequestMethod.POST)
    public SSCCEmployeeDetailRes getSSCCEmployeeDetails(@RequestBody SSCCEmployeeDetailReq request) throws Exception {
        if (ObjectUtils.isEmpty(request) || ObjectUtils.isEmpty(request.getPersonId())) {
            throw new InvalidRequestException(
                    messageSource.getMessage("sscc.employee.details.mandatory", null, Locale.US));
        }
        return SSCCEmployeeDetailService.getSSCCEmployeeDetailslUsingPersonId(request.getPersonId());
    }

    @RequestMapping(value = "/getEmployeeDetailDao", headers = {"Accept=application/json"}, method = RequestMethod.POST)
    public SSCCEmployeeDetailRes getEmployeeDetailDao(@RequestBody SSCCEmployeeDetailReq request) throws Exception {
        if (ObjectUtils.isEmpty(request) || ObjectUtils.isEmpty(request.getPersonId())) {
            throw new InvalidRequestException(
                    messageSource.getMessage("sscc.employee.details.mandatory", null, Locale.US));
        }
        return SSCCEmployeeDetailService.getEmployeeInfo(request.getPersonId());
    }


}
