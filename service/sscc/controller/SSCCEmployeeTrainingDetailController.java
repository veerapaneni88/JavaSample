package us.tx.state.dfps.service.sscc.controller;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.SSCCEmployeeTrainingDetailReq;
import us.tx.state.dfps.service.common.response.SSCCEmployeeTrainingDetailRes;
import us.tx.state.dfps.service.sscc.service.SSCCEmployeeTrainingDetailService;

import java.util.Locale;

@RestController
@RequestMapping("/ssccEmployeeTrainingDetail")
public class SSCCEmployeeTrainingDetailController {

    private static final Logger log = Logger.getLogger(SSCCEmployeeTrainingDetailController.class);

    @Autowired
    MessageSource messageSource;

    @Autowired
    SSCCEmployeeTrainingDetailService ssccEmployeeTrainingDetailService;

    @RequestMapping(value = "/geEmployeeTrainingDetailsList", headers = {"Accept=application/json"}, method = RequestMethod.POST)
    public SSCCEmployeeTrainingDetailRes getEmployeeTrainingDetails(@RequestBody SSCCEmployeeTrainingDetailReq request) throws Exception {
        if (ObjectUtils.isEmpty(request) || ObjectUtils.isEmpty(request.getPersonId())) {
            throw new InvalidRequestException(
                    messageSource.getMessage("sscc.employee.details.mandatory", null, Locale.US));
        }
        return ssccEmployeeTrainingDetailService.getEmployeeTrainingDetailsList(request.getPersonId());
    }

    @RequestMapping(value = "/geEmployeeTrainingDetailsByIdList", headers = {"Accept=application/json"}, method = RequestMethod.POST)
    public SSCCEmployeeTrainingDetailRes getEmployeeTrainingDetailsById(@RequestBody SSCCEmployeeTrainingDetailReq request) throws Exception {
        if (ObjectUtils.isEmpty(request) || ObjectUtils.isEmpty(request.getFieldName()) || ObjectUtils.isEmpty(request.getFieldId()) ) {
            throw new InvalidRequestException(
                    messageSource.getMessage("sscc.employee.details.mandatory", null, Locale.US));
        }
        return ssccEmployeeTrainingDetailService.getEmployeeTrainingDetailsByIdList(request.getFieldName(),request.getFieldId());
    }

    @RequestMapping(value = "/deleteEmployeeTrainingDetailsById", headers = {"Accept=application/json"}, method = RequestMethod.POST)
    public SSCCEmployeeTrainingDetailRes deleteEmployeeTrainingDetailsById(@RequestBody SSCCEmployeeTrainingDetailReq request) throws Exception {
        if (ObjectUtils.isEmpty(request) || ObjectUtils.isEmpty(request.getPersonId()) || ObjectUtils.isEmpty(request.getFieldName()) || ObjectUtils.isEmpty(request.getFieldId())) {
            throw new InvalidRequestException(
                    messageSource.getMessage("sscc.employee.details.mandatory", null, Locale.US));
        }
        ssccEmployeeTrainingDetailService.deleteSSCCEmployeeDetailById(request.getFieldName(),request.getFieldId());
        return ssccEmployeeTrainingDetailService.getEmployeeTrainingDetailsByIdList("personId",request.getPersonId());
    }

    @RequestMapping(value = "/saveEmployeeTrainingDetails", headers = {"Accept=application/json"}, method = RequestMethod.POST)
    public SSCCEmployeeTrainingDetailRes saveEmployeeTrainingDetails(@RequestBody SSCCEmployeeTrainingDetailReq request) throws Exception {
        if (ObjectUtils.isEmpty(request) || ObjectUtils.isEmpty(request.getEmpTrainingDetailsBean())) {
            throw new InvalidRequestException(
                    messageSource.getMessage("sscc.employee.details.mandatory", null, Locale.US));
        }
        SSCCEmployeeTrainingDetailRes response = new SSCCEmployeeTrainingDetailRes();
            response = ssccEmployeeTrainingDetailService.saveSSCCEmployeeTrainingDetails(request.getEmpTrainingDetailsBean());
        if (CollectionUtils.isEmpty(response.getErrors())) {
            response = ssccEmployeeTrainingDetailService.getEmployeeTrainingDetailsList(request.getPersonId());
        }
        return response;
    }
}
