package us.tx.state.dfps.service.personsearch.dao;

import java.util.HashMap;

import us.tx.state.dfps.service.kin.dto.PaginationResultDto;
import us.tx.state.dfps.service.person.dto.AddressValueDto;
import us.tx.state.dfps.service.person.dto.PersonSearchInRecDto;
import us.tx.state.dfps.service.person.dto.PersonSearchOutRecDto;
import us.tx.state.dfps.service.person.dto.PrsnSearchOutRecArrayDto;
import us.tx.state.dfps.service.person.dto.PrsnSrchListpInitArrayDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Dao
 * Interface for functions required for implementing PersonSearch functionality
 * Oct 30, 2017- 6:37:45 PM Â© 2017 Texas Department of Family and Protective
 * Services
 */
public interface PersonSearchDao {

	/**
	 * 
	 * Method Name: getPersonIdentifier Method Description: This method gets the
	 * person identifier number for a given person identifier type
	 * 
	 * @param idPerson
	 * @param idType
	 * @return String
	 */
	public String getPersonIdentifier(Long idPerson, String idType);

	/**
	 * 
	 * Method Name: getIdPersonAddress Method Description:This method get person
	 * address details for a person
	 * 
	 * @param idPerson
	 * @return AddressValueDto
	 */
	public AddressValueDto getIdPersonAddress(Long idPerson);

	/**
	 * 
	 * Method Name: performPartialSearch Method Description: This method returns
	 * result of Partial Search
	 * 
	 * @param personSearchInRecDto
	 * @param paginationResultDto
	 * @return PersonSearchOutRecDto
	 */
	public PersonSearchOutRecDto performPartialSearch(PersonSearchInRecDto personSearchInRecDto,
			PaginationResultDto paginationResultDto);

	/**
	 * Method Name: getPersonSearchView Method Description: Returns result of
	 * person view search
	 * 
	 * @param personSearchReq
	 * @return PersonSearchOutRecDto
	 */
	public PersonSearchOutRecDto getPersonSearchView(HashMap personIdentifierValueNumber,
			PaginationResultDto paginationResultDto);

	/**
	 * Method Name: populateAddtnlInfoIntakeSearch Method Description: Populates
	 * additional info from intake search
	 * 
	 * @param prsnSrchListpInitArrayDto
	 */
	public PrsnSrchListpInitArrayDto populateAddtnlInfoIntakeSearch(
			PrsnSrchListpInitArrayDto prsnSrchListpInitArrayDto);

	/**
	 * Method Name: populateAddtnlInfoRegularSearch Method Description:
	 * Populates additional info from intake search
	 * 
	 * @param prsnSearchOutRecArrayDto\
	 * @return PrsnSearchOutRecArrayDto
	 */
	public PrsnSearchOutRecArrayDto populateAddtnlInfoRegularSearch(PrsnSearchOutRecArrayDto prsnSearchOutRecArrayDto,
			boolean flag);

	/**
	 * Method Name: performDOBSearch Method Description: Returns result of DOB
	 * search
	 * 
	 * @param personSearchReq
	 * @return PersonSearchOutRecDto
	 */
	public PersonSearchOutRecDto performDOBSearch(PersonSearchInRecDto personSearchInRecDto,
			PaginationResultDto paginationResultDto);
	/**
	 *
	 * Method Name: getForwardPersonInMerge
	 * Method Description: This method
	 * returns the forward person id for a person
	 *
	 * @param idPerson
	 * @return Long
	 */
	public Long getForwardPersonInMerge(Long idPerson);

}
