package us.tx.state.dfps.service.fahome.controller;

import java.util.ArrayList;
import java.util.Locale;

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
import us.tx.state.dfps.service.common.request.ReVerificationReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.homedetails.service.HomeDetailsService;

@RestController
@RequestMapping("fahome")
public class ReVerificationInfoController {

    private static final Logger logger = Logger.getLogger(ReVerificationInfoController.class);
    @Autowired
    MessageSource messageSource;
    @Autowired
    HomeDetailsService homeDetailsService;

    @RequestMapping(value = "/getreverification", headers = {"Accept=application/json"}, method = RequestMethod.POST)
    public CommonFormRes getReVerification(@RequestBody ReVerificationReq reVerificationReq) {

        if (TypeConvUtil.isNullOrEmpty(reVerificationReq.getIdStage())) {
            throw new InvalidRequestException(messageSource.getMessage("common.stageid.mandatory", null, Locale.US));
        }
        CommonFormRes commonFormRes = new CommonFormRes();
        PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
        preFillData.setBookmarkDtoList(new ArrayList<BookmarkDto>());
        preFillData.setFormDataGroupList(new ArrayList<FormDataGroupDto>());
        FacilityStageInfoReq facilityStageInfoReq = new FacilityStageInfoReq();
        BeanUtils.copyProperties(reVerificationReq, facilityStageInfoReq);

        PreFillDataServiceDto preFillDataHomeDetailsData = homeDetailsService.getHomeDetailsData(facilityStageInfoReq);
        preFillData.getBookmarkDtoList().addAll(preFillDataHomeDetailsData.getBookmarkDtoList());
        preFillData.getFormDataGroupList().addAll(preFillDataHomeDetailsData.getFormDataGroupList());
        commonFormRes
                .setPreFillData(TypeConvUtil.getXMLFormat(preFillData));
        logger.info("StageId :" + reVerificationReq.getIdStage() + ", " + "PersonId : " + reVerificationReq.getIdPerson() + " , " + "EventId : " + reVerificationReq.getIdEvent());

        return commonFormRes;
    }

}
