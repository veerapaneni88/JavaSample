package us.tx.state.dfps.service.monthlyassessmentadoption.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.FacilityStageInfoReq;
import us.tx.state.dfps.service.common.request.MonthlyAssessmentAdoptionReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.homedetails.service.HomeDetailsService;
import us.tx.state.dfps.service.monthlyassessmentadoption.service.MonthlyAssessmentAdoptionService;

import java.util.ArrayList;
import java.util.Locale;

/**
 * service-business - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CFAD01S
 * Class Description: Controller class for services related to monthlyassessmentadoption. Apr
 * 20,2017 - 4:29:18 PM
 */

@RestController
@RequestMapping("monthlyassessmentadoption")
public class MonthlyAssessmentAdoptionController {

    @Autowired
    MessageSource messageSource;

    @Autowired
    MonthlyAssessmentAdoptionService monthlyAssessmentAdoptionService;
    @Autowired
    HomeDetailsService homeDetailsService;

    private static final Logger logger = Logger.getLogger(MonthlyAssessmentAdoptionController.class);

    /**
     *
     * Method Description: Method to update person details in the CVS Home
     * window. This method is also retrieve the saved data. EJB - CVS FA HOME
     *
     * @param monthlyAssessmentAdoptionReq
     * @return CommonFormRes
     */

    @RequestMapping(value = "/getmonthlyassessmentadoption", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public  CommonFormRes getMonthlyAssessmentAdoption(@RequestBody MonthlyAssessmentAdoptionReq monthlyAssessmentAdoptionReq) {

        if (TypeConvUtil.isNullOrEmpty(monthlyAssessmentAdoptionReq.getIdStage())) {
            throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
        }
        CommonFormRes commonFormRes = new CommonFormRes();
        PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
        preFillData.setBookmarkDtoList(new ArrayList<BookmarkDto>());
        preFillData.setFormDataGroupList(new ArrayList<FormDataGroupDto>());

        FacilityStageInfoReq facilityStageInfoReq = new FacilityStageInfoReq();
        BeanUtils.copyProperties(monthlyAssessmentAdoptionReq, facilityStageInfoReq);

        PreFillDataServiceDto preFillDataHomeDetailsData = homeDetailsService.getHomeDetailsData(facilityStageInfoReq);
        preFillData.getBookmarkDtoList().addAll(preFillDataHomeDetailsData.getBookmarkDtoList());
        preFillData.getFormDataGroupList().addAll(preFillDataHomeDetailsData.getFormDataGroupList());

        commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(preFillData));

        return commonFormRes;
    }

}
