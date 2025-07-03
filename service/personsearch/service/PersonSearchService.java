package us.tx.state.dfps.service.personsearch.service;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.common.request.PersonSearchReq;
import us.tx.state.dfps.service.common.response.PersonSearchRes;
import us.tx.state.dfps.service.kin.dto.PaginationResultDto;
import us.tx.state.dfps.service.person.dto.AddressValueDto;
import us.tx.state.dfps.service.person.dto.PersonSearchInRecDto;
import us.tx.state.dfps.service.person.dto.PersonSearchOutRecDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Service
 * implementation for functions required for implementing PersonSearch
 * functionality Oct 30, 2017- 6:46:39 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface PersonSearchService {

	/**
	 * 
	 * Method Name: getPersonIdentifier Method Description:This method gets the
	 * person identifier number for a given person identifier type
	 * 
	 * @param idPerson
	 * @param idType
	 * @return String
	 */
	public String getPersonIdentifier(Long idPerson, String idType);

	/**
	 * 
	 * Method Name: getPersonAddress Method Description:This method get person
	 * address details
	 * 
	 * @param idPerson
	 * @return AddressValueDto
	 */
	public AddressValueDto getPersonAddress(Long idPerson);

	/**
	 * 
	 * Method Name: performPartialSearch Method Description:This method returns
	 * result of Partial Search
	 * 
	 * @param paginationResultDto
	 * @param personSearchInRecDto
	 * @return PersonSearchOutRecDto
	 */
	public PersonSearchOutRecDto performPartialSearch(PersonSearchInRecDto personSearchInRecDto,
			PaginationResultDto paginationResultDto);

	/**
	 * Method Name: getSearchPersonView Method Description: Returns result of
	 * person view search
	 * 
	 * @param paginationResultBeanDto
	 * @return PersonSearchRes
	 */
	public PersonSearchRes getSearchPersonView(PersonSearchReq personSearchReq);

	/**
	 * Method Name: populateAddtnlInfoIntakeSearch Method Description: Populates
	 * additional info from intake search
	 * 
	 * @param personSearchReq
	 * @return PersonSearchRes
	 */
	public PersonSearchRes populateAddtnlInfoIntakeSearch(PersonSearchReq personSearchReq);

	/**
	 * Method Name: populateAddtnlInfoRegularSearch Method Description:
	 * Populates additional info from Regular search
	 * 
	 * @param personSearchReq
	 * @return PersonSearchRes
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PersonSearchRes populateAddtnlInfoRegularSearch(PersonSearchReq personSearchReq);

	/**
	 * Method Name: performDOBSearch Method Description: Returns result of DOB
	 * search
	 * 
	 * @param personSearchReq
	 * @return PersonSearchRes
	 */
	public PersonSearchRes performDOBSearch(PersonSearchReq personSearchReq);

}
