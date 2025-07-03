package us.tx.state.dfps.service.populatenocletter.service;

import us.tx.state.dfps.service.common.request.PopulateLetterReq;
import us.tx.state.dfps.service.common.request.PopulateNocLetterReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.web.contact.bean.ContactNocPersonDetailDto;

public interface LetterReporterNocService {

    /**
     * Method Name: PopulateLetter Method Description:
     *
     * @param populateNocLetterReq
     * @return PreFillDataServiceDto @
     */
    public PreFillDataServiceDto populateLetter(PopulateNocLetterReq populateNocLetterReq, boolean spanish);
    public ContactNocPersonDetailDto getContactNocPerson(long idPerson);

}
