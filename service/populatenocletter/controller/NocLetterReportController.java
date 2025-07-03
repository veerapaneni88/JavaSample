package us.tx.state.dfps.service.populatenocletter.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.common.request.PopulateNocLetterReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.populatenocletter.service.LetterReporterNocService;
import us.tx.state.dfps.common.domain.ContactNocPerson;
import us.tx.state.dfps.service.common.response.ContactNocPersonResp;
import us.tx.state.dfps.web.contact.bean.ContactNocPersonDetailDto;
import java.util.ArrayList;
import java.util.List;


import java.util.Locale;

@RestController
@RequestMapping("/populatenocletter")
public class NocLetterReportController {

    @Autowired
    LetterReporterNocService letterReporterNocService;

    @Autowired
    MessageSource messageSource;

    private static final Logger log = Logger.getLogger("ServiceBusiness-PopulateNocLetterControllerLog");


    @RequestMapping(value = "/getPopulateNocLetter", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public CommonFormRes getPopulateNocLetter(@RequestBody PopulateNocLetterReq populateNocLetterReq) {
        if (TypeConvUtil.isNullOrEmpty(populateNocLetterReq.getIdStage()))
            throw new InvalidRequestException(
                    messageSource.getMessage("populateNocLetter.idStage.mandatory", null, Locale.US));
        if (TypeConvUtil.isNullOrEmpty(populateNocLetterReq.getIdPerson()))
            throw new InvalidRequestException(
                    messageSource.getMessage("populateNocLetter.IdPerson.mandatory", null, Locale.US));
        if (TypeConvUtil.isNullOrEmpty(populateNocLetterReq.getIdEvent()))
            throw new InvalidRequestException(
                    messageSource.getMessage("populateNocLetter.idEvent.mandatory", null, Locale.US));

        CommonFormRes commonFormRes = new CommonFormRes();
        commonFormRes.setPreFillData(
                TypeConvUtil.getXMLFormat(letterReporterNocService.populateLetter(populateNocLetterReq, false)));
        log.info("TransactionId :" + populateNocLetterReq.getTransactionId());
        // return letterReporterService.populateLetter(populateLetterReq);

        return commonFormRes;
    }

    @RequestMapping(value = "/getNocPerson", headers = { "Accept=application/json" }, method = RequestMethod.POST)
    public ContactNocPersonResp getNocPerson(@RequestBody ContactNocPerson nocPersonId)
    {
        ContactNocPersonResp ContactNocPersonResp=new ContactNocPersonResp();
        List<ContactNocPersonDetailDto> contactNocPersonDetailDtoList=new ArrayList<ContactNocPersonDetailDto>();
        ContactNocPersonDetailDto contactNocPersonDetailDto= letterReporterNocService.getContactNocPerson(nocPersonId.getIdContactNocPersons());
        contactNocPersonDetailDtoList.add(contactNocPersonDetailDto);
        ContactNocPersonResp.setContactNocPersonDetailDtoList(contactNocPersonDetailDtoList);
        return ContactNocPersonResp;
    }


}
