package us.tx.state.dfps.service.medicalconsenter.dao;

import us.tx.state.dfps.service.medcareconsenter.dto.PersonPhoneMedCareDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Name Person
 * Phone to get the phone records. Feb 9, 2018- 1:44:55 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface NamePersonPhoneDao {

	public PersonPhoneMedCareDto getPersonPhoneRecords(Long idPerson);
}
