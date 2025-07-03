package us.tx.state.dfps.service.seriousincident.controller;

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
import us.tx.state.dfps.service.common.request.SeriousIncidentReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.homedetails.service.HomeDetailsService;
import us.tx.state.dfps.service.seriousincident.service.SeriousIncidentDocumentService;

import java.util.ArrayList;
import java.util.Locale;

@RestController
@RequestMapping("seriousincidentdocument")
public class SeriousIncidentDocumentController {

    private static final Logger log = Logger.getLogger(SeriousIncidentDocumentController.class);

    @Autowired
    MessageSource messageSource;

    @Autowired
    SeriousIncidentDocumentService seriousIncidentService;

    @Autowired
    HomeDetailsService homeDetailsService;

    @RequestMapping(value = "/getseroiusincidentdocument", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public CommonFormRes getSeriousIncidentData(@RequestBody SeriousIncidentReq seriousIncidentReq) {

        if (TypeConvUtil.isNullOrEmpty(seriousIncidentReq.getIdStage())) {
            throw new InvalidRequestException(messageSource.getMessage("common.input.mandatory", null, Locale.US));
        }

        CommonFormRes commonFormRes = new CommonFormRes();
        PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
        preFillData.setBookmarkDtoList(new ArrayList<BookmarkDto>());
        preFillData.setFormDataGroupList(new ArrayList<FormDataGroupDto>());
        FacilityStageInfoReq facilityStageInfoReq = new FacilityStageInfoReq();
        BeanUtils.copyProperties(seriousIncidentReq, facilityStageInfoReq);

        PreFillDataServiceDto preFillDataHomeDetailsData = homeDetailsService.getHomeDetailsData(facilityStageInfoReq);
        preFillData.getBookmarkDtoList().addAll(preFillDataHomeDetailsData.getBookmarkDtoList());
        preFillData.getFormDataGroupList().addAll(preFillDataHomeDetailsData.getFormDataGroupList());

        PreFillDataServiceDto preFillDataSeriousIncidentData = seriousIncidentService.getSeriousIncidentData(seriousIncidentReq);
        preFillData.getBookmarkDtoList().addAll(preFillDataSeriousIncidentData.getBookmarkDtoList());
        preFillData.getFormDataGroupList().addAll(preFillDataSeriousIncidentData.getFormDataGroupList());

        commonFormRes.setPreFillData(TypeConvUtil.getXMLFormat(preFillData));

        return commonFormRes;
    }
}
