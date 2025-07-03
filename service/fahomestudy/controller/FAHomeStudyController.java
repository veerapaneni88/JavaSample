package us.tx.state.dfps.service.fahomestudy.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.FAHomeStudyReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.fahomestudy.service.FAHomeStudyService;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.FAHomeStudyPrefillData;
import us.tx.state.dfps.service.homedetails.service.HomeDetailsService;
import us.tx.state.dfps.service.seriousincident.controller.SeriousIncidentDocumentController;

import java.util.Locale;

@RestController
@RequestMapping("fahomestudy")
public class FAHomeStudyController {

    private static final Logger log = Logger.getLogger(SeriousIncidentDocumentController.class);

    @Autowired
    HomeDetailsService homeDetailsService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    FAHomeStudyService faHomeStudyService;

    @Autowired
    FAHomeStudyPrefillData faHomeStudyPrefillData;

    @RequestMapping(value = "/getfahomestudydetail", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public CommonFormRes getHomeStudyDetail(@RequestBody FAHomeStudyReq faHomeStudyReq) {

        if (TypeConvUtil.isNullOrEmpty(faHomeStudyReq.getIdStage())) {
            throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
        }
        CommonFormRes commonFormRes = new CommonFormRes();

        PreFillDataServiceDto preFillDataHomeDetailsData = faHomeStudyService.getHomeStudyDetail(faHomeStudyReq);

        commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(preFillDataHomeDetailsData));

        return commonFormRes;
    }
}
