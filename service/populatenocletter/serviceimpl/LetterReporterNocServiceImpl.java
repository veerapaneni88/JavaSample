package us.tx.state.dfps.service.populatenocletter.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.tx.state.dfps.common.domain.ContactNocPerson;
import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.populatenocletter.dto.PopulateNocLetterDto;
import us.tx.state.dfps.service.common.request.PopulateNocLetterReq;
import us.tx.state.dfps.service.contacts.dao.ContactNocPersonDetailsDao;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.PopulateNocLetterPrefillData;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.populatenocletter.service.LetterReporterNocService;
import us.tx.state.dfps.service.workload.dao.ContactDao;
import us.tx.state.dfps.service.workload.dto.ContactDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.web.contact.bean.ContactNocPersonDetailDto;


@Service
@Transactional
public class LetterReporterNocServiceImpl implements LetterReporterNocService {

    @Autowired
    private DisasterPlanDao disasterPlanDao;

    @Autowired
    private PersonDao personDao;

    @Autowired
    private ContactDao contactDao;

    @Autowired
    private ContactNocPersonDetailsDao contactNocPersonDetailsDao;

    @Autowired
    private PopulateNocLetterPrefillData populateNocLetterPrefillData;

    @Override
    public PreFillDataServiceDto populateLetter(PopulateNocLetterReq populateNocLetterReq, boolean spanish) {

        PopulateNocLetterDto populateNocLetterDto = new PopulateNocLetterDto();

        GenericCaseInfoDto genCaseInfoDto = disasterPlanDao.getGenericCaseInfo(populateNocLetterReq.getIdStage());
        PersonDto notifiedPersonDto;
        if (populateNocLetterReq.getNocPerson()) {
            //TO DO get details from ContactNoc Table
            ContactNocPerson  contactNocPerson =  contactNocPersonDetailsDao.getContactNocPersonDetailById(populateNocLetterReq.getIdPerson());
            PersonDto  personDto = new PersonDto();
            personDto.setNmPersonFull(contactNocPerson.getNmPersonFull());
//            personDto.setAddrPersonFull("");
            personDto.setIdPerson(contactNocPerson.getIdContactNocPersons());
            personDto.setIdCase(contactNocPerson.getIdCase());

            notifiedPersonDto = personDto;
        } else {
            notifiedPersonDto = personDao.getPersonById(populateNocLetterReq.getIdPerson());
        }

        ContactDto contactDto = contactDao.getContactByEventId(populateNocLetterReq.getIdEvent());
        PersonDto repPersonDto = personDao.getPersonById(contactDto.getIdLastEmpUpdate());

        populateNocLetterDto.setGenCaseInfoDto(genCaseInfoDto);
        populateNocLetterDto.setPersonNotifiedInfo(notifiedPersonDto);
        populateNocLetterDto.setContactInfo(contactDto);
        populateNocLetterDto.setRepPersonInfo(repPersonDto);
        populateNocLetterDto.setSpanish(spanish);
        return populateNocLetterPrefillData.returnPrefillData(populateNocLetterDto);
    }

    public ContactNocPersonDetailDto getContactNocPerson(long idPerson)
    {
        ContactNocPersonDetailDto contactNocPersonDetailDto=new ContactNocPersonDetailDto();
        ContactNocPerson contactNocPerson =  contactNocPersonDetailsDao.getContactNocPersonDetailById(idPerson);
        contactNocPersonDetailDto.setTxtEmail(contactNocPerson.getTxtEmail());
        return contactNocPersonDetailDto;
    }
}
