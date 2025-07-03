package us.tx.state.dfps.service.admin.dao;

import us.tx.state.dfps.common.domain.IncomingPersonMps;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.mobile.IncomingPersonMpsDto;
import us.tx.state.dfps.service.admin.dto.PersonDtlUpdateDto;
import us.tx.state.dfps.service.admin.dto.PersonStageLinkCatgIdInDto;
import us.tx.state.dfps.service.admin.dto.PersonStageLinkCatgIdOutDto;
import us.tx.state.dfps.service.person.dto.EditPersonAddressDto;
import us.tx.state.dfps.service.person.dto.PersonIdDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Cinv41dDao Aug 12, 2017- 10:27:14 AM © 2017 Texas Department of Family
 * and Protective Services
 */
public interface PersonStageLinkCatgIdDao {

	/**
	 * 
	 * Method Name: updateInvestigationPersonDetail Method Description:updates
	 * all table related with investigation person. Cinv41d
	 * 
	 * @param personStageLinkCatgIdInDto
	 * @return PersonStageLinkCatgIdOutDto
	 */
	public PersonStageLinkCatgIdOutDto updateInvestigationPersonDetail(
			PersonStageLinkCatgIdInDto personStageLinkCatgIdInDto);

    public PersonDtlUpdateDto updateInvestigationPersonDetailForMPS(PersonDtlUpdateDto personDetailUpdateoDto);

	public EditPersonAddressDto updateMPSPersonAddress(EditPersonAddressDto personAddressDto);

	public IncomingPersonMpsDto updateMPSPersonDetail(IncomingPersonMpsDto incomingPersonMpsDto);

	public PersonIdDto addMPSPersonDetails(Long idMPSPerson);

	public Person getPersonDtl(long idPerson);

	public void saveMPSPersonRace(long idMPSPerson, Person personDtl);

	public void saveMPSPersonEthniciy(Person personDtl , IncomingPersonMps incomingPersonMps);

	public void saveMPSPersonAddress(long idPerson, IncomingPersonMps incomingPersonMps);

	public void saveMPSPhone (Person personDtl, IncomingPersonMps incomingPersonMps);

	public void saveMPSEmail(Person personDtl,  IncomingPersonMps incomingPersonMps);

	public void saveMPSPersonInd(Person personDtl,  IncomingPersonMps incomingPersonMps);

	public IncomingPersonMps getIncomingMPSPersonDetail(long idMPSPerson);
}
