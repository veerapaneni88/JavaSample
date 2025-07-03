package us.tx.state.dfps.service.sscc.controller;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.admin.dto.PersonDiDto;
import us.tx.state.dfps.service.common.request.SSCCCatchmentsReq;
import us.tx.state.dfps.service.common.response.SSCCCatchmentsRes;
import us.tx.state.dfps.service.sscc.service.SSCCCatchmentsService;
import us.tx.state.dfps.web.sscc.bean.SSCCEmployeeDetailsBean;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/ssccCatchments")
public class SSCCCatchmentsController {

    @Autowired
    SSCCCatchmentsService service;

    @Autowired
    MessageSource messageSource;

    private static final Logger log = Logger.getLogger(SSCCEmployeeDetailController.class);

    @RequestMapping(value = "/getCatchmentsByRegion", headers = {"Accept=application/json"}, method = RequestMethod.POST)
    public SSCCCatchmentsRes getCatchmentsByRegion(@RequestBody SSCCCatchmentsReq request) throws Exception {



        if (ObjectUtils.isEmpty(request) || ObjectUtils.isEmpty(request.getRegion())) {
            throw new InvalidRequestException(
                    "Invalid Catchment source");
        }


        SSCCCatchmentsRes response = new SSCCCatchmentsRes();
        response.setCatchments(service.getCatchmentsForRegion(request.getRegion()));
        return response;
    }

    @RequestMapping(value = "/getCatchmentsByRegionMap", headers = {"Accept=application/json"}, method = RequestMethod.POST)
    public SSCCCatchmentsRes getCatchmentsByRegionMap(@RequestBody SSCCCatchmentsReq request) throws Exception {



        if (ObjectUtils.isEmpty(request) || ObjectUtils.isEmpty(request.getRegion())) {
            throw new InvalidRequestException(
                    "Invalid Catchment source");
        }


        SSCCCatchmentsRes response = new SSCCCatchmentsRes();
        response.setCatchmentsMap(service.getCatchmentsByRegionMap(request.getRegion()));
        return response;
    }

}
