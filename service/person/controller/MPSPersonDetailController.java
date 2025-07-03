package us.tx.state.dfps.service.person.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.mobile.IncomingPersonMpsDto;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.PersonDetailsReq;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.IncomingPersonMpsRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.person.service.MPSPersonDetailService;

@RestController
@RequestMapping("/mpsPersonDetail")
public class MPSPersonDetailController {

    @Autowired
    MessageSource messageSource;

    @Autowired
    MPSPersonDetailService mpsPersonDetailService;

    @RequestMapping(value = "/mpsPersonDetailRetrieve", headers = {
            "Accept=application/json"}, method = RequestMethod.POST)
    public IncomingPersonMpsRes getPersonDetails(@RequestBody PersonDetailsReq personDetailsReq) {
        if (TypeConvUtil.isNullOrEmpty(personDetailsReq.getIdPerson())) {
            throw new InvalidRequestException(
                    messageSource.getMessage("person.retrieve.details.IdPerson.mandatory", null, Locale.US));
        }
        IncomingPersonMpsRes incomingPersonMpsRes = mpsPersonDetailService.retrievePersonDetails(personDetailsReq);
        return incomingPersonMpsRes;
    }

    @RequestMapping(value = "/mpsPersonDetailSave", headers = {
            "Accept=application/json"}, method = RequestMethod.POST)
    public CommonHelperRes savePersonDetails(@RequestBody IncomingPersonMpsDto incomingPersonMpsDto) {

        CommonHelperRes commonHelperRes = new CommonHelperRes();
        Long incomingPersonId = mpsPersonDetailService.savePersonDetails(incomingPersonMpsDto);
        commonHelperRes.setUlIdPerson(incomingPersonId);
        return commonHelperRes;
    }

    @RequestMapping(value = "/mpsPersonDetailDelete", headers = {
            "Accept=application/json"}, method = RequestMethod.POST)
    public CommonHelperRes deletePersonDetails(@RequestBody CommonHelperReq commonHelperReq) {
        mpsPersonDetailService.deletePersonDetails(commonHelperReq);
        return null;
    }
}
