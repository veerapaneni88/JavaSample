package us.tx.state.dfps.service.contacts.service;

import us.tx.state.dfps.service.common.request.ContactNocPersonReq;
import us.tx.state.dfps.web.contact.bean.ContactNocPersonDetailDto;

import java.util.List;

public interface ContactNocPersonDetailsService {



    public List<Long> saveContactNocPersonDetailRecord(ContactNocPersonReq contactNocPersonReq);

    public List<ContactNocPersonDetailDto> getContactNocPersonDetails(ContactNocPersonDetailDto contactNocPersonDetailDto);
}
