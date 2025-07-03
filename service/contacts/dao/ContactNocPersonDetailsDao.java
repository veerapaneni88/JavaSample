package us.tx.state.dfps.service.contacts.dao;


import us.tx.state.dfps.common.domain.ContactNocPerson;
import us.tx.state.dfps.web.contact.bean.ContactNocPersonDetailDto;

import java.util.List;

public interface ContactNocPersonDetailsDao {

    public Long saveContactNocPersonDetail(ContactNocPerson contactNocPersons);

    public List<ContactNocPersonDetailDto> getAllNewPersonDetails(ContactNocPersonDetailDto contactNocPerson);

    public List<ContactNocPersonDetailDto> getAllNewPersonBasedOnEventId(long eventId);

    public ContactNocPerson getContactNocPersonDetailById(long id);
}
