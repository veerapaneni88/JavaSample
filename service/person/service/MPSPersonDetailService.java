package us.tx.state.dfps.service.person.service;

import us.tx.state.dfps.mobile.IncomingPersonMpsDto;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.PersonDetailsReq;
import us.tx.state.dfps.service.common.response.IncomingPersonMpsRes;

public interface MPSPersonDetailService {

    IncomingPersonMpsRes retrievePersonDetails(PersonDetailsReq personDetailsReq);

    Long savePersonDetails(IncomingPersonMpsDto incomingPersonMpsDto);

    void deletePersonDetails(CommonHelperReq commonHelperReq);
}
