package us.tx.state.dfps.service.person.dao;

import us.tx.state.dfps.common.domain.IncomingPersonMps;
import us.tx.state.dfps.common.domain.PersonRaceMps;

import java.util.List;

public interface MPSPersonDetailDao {

    IncomingPersonMps fetchIncomingPersonById(Long idIncomingPersonMps);

    List<PersonRaceMps> fetchPersonRaceMPSByIncomingPersonId(Long idIncomingPersonMps);

    Long saveIncomingPersonDetails(IncomingPersonMps incomingPersonMps);

    void savePersonRaceMps(PersonRaceMps personRaceMps);

    void deletePersonRaceMps(Long idIncomingPersonMps);

    void deleteIncomingPersonDetails(Long idIncomingPersonMps);
}
