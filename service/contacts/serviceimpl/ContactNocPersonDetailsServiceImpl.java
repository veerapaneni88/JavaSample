package us.tx.state.dfps.service.contacts.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import us.tx.state.dfps.common.domain.ContactNocPerson;
import us.tx.state.dfps.common.dto.ContactPersonNarrValueDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.ContactDetailReq;
import us.tx.state.dfps.service.common.request.ContactNocPersonReq;
import us.tx.state.dfps.service.contacts.dao.ContactDetailsDao;
import us.tx.state.dfps.service.contacts.dao.ContactNocPersonDetailsDao;
import us.tx.state.dfps.service.contacts.service.ContactNocPersonDetailsService;
import us.tx.state.dfps.web.contact.bean.ContactDetailDto;
import us.tx.state.dfps.web.contact.bean.ContactNocPersonDetailDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ContactAUDDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ChildContactDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class ContactNocPersonDetailsServiceImpl implements ContactNocPersonDetailsService {

    @Autowired
    ContactNocPersonDetailsDao contactNocPersonDetailsDao;

    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
            Exception.class })
    @Override
    public List<Long> saveContactNocPersonDetailRecord(ContactNocPersonReq contactNocPersonReqList) {

        List<Long> ids = new ArrayList<>();
        for (ContactNocPersonDetailDto contactNocPerson : contactNocPersonReqList.getContactNocPersonDetailDtoList()) {
            ContactNocPerson cnp = new ContactNocPerson();

            cnp.setIdCase(contactNocPerson.getIdCase());
            cnp.setCdStage(contactNocPerson.getCdStage());
            cnp.setCdStageProgram(contactNocPerson.getCdStageProgram());

            cnp.setCdStagePersRelInt(contactNocPerson.getCdStagePersRelInt());
            cnp.setNmStage(contactNocPerson.getNmStage());
            cnp.setNmPersonFull(contactNocPerson.getNmPersonFull());
            cnp.setIndLanguage(contactNocPerson.getIndLanguage());
            cnp.setIndDistMthd(contactNocPerson.getIndDistMthd());
            cnp.setTxtEmail(contactNocPerson.getTxtEmail());
            cnp.setDtPurged(contactNocPerson.getDtPurged());
            cnp.setDtLastUpdate(contactNocPerson.getDtLastUpdate());
            cnp.setIdCreatedPerson(contactNocPerson.getIdCreatedPerson());
            cnp.setDtCreated(contactNocPerson.getDtCreated());
            cnp.setIdLastUpdatePerson(contactNocPerson.getIdLastUpdatedPerson());
            cnp.setEventId(contactNocPerson.getEventId());
            ids.add(contactNocPersonDetailsDao.saveContactNocPersonDetail(cnp));
        }
        return ids;
    }

    @Override
    public List<ContactNocPersonDetailDto> getContactNocPersonDetails(ContactNocPersonDetailDto contactNocPersonDetailDto) {


        return contactNocPersonDetailsDao.getAllNewPersonDetails(contactNocPersonDetailDto);
    }


//    @Override
//    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
//            Exception.class })
//    public Long saveContact(ContactDetailReq contactDetailReq) {
//        ContactAUDDto contactDto = contactDetailReq.getContactAUDDto();
//        List<ContactPersonNarrValueDto> contactPersonNarrList = contactDetailReq.getContactPersonNarrList();
//        ChildContactDto childContactDto = contactDetailSaveService.audContactDetailRecord(contactDto);
//        if (ServiceConstants.CCNTCTYP_GKIN.equals(contactDto.getCdContactType())
//                || ServiceConstants.CCNTCTYP_GKNS.equals(contactDto.getCdContactType())) {
//            saveContactPersonNarrInfo(contactPersonNarrList, childContactDto.getIdEvent(), contactDto.getIdCase());
//        }
//        return childContactDto.getIdEvent();
//    }
}
