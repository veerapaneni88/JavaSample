package us.tx.state.dfps.service.personsearch.serviceimpl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.PersonSearchReq;
import us.tx.state.dfps.service.common.response.PersonSearchRes;
import us.tx.state.dfps.service.kin.dto.PaginationResultDto;
import us.tx.state.dfps.service.person.dto.AddressValueDto;
import us.tx.state.dfps.service.person.dto.PersonSearchInRecDto;
import us.tx.state.dfps.service.person.dto.PersonSearchOutRecDto;
import us.tx.state.dfps.service.person.dto.PrsnSearchOutRecArrayDto;
import us.tx.state.dfps.service.person.dto.PrsnSrchListpInitArrayDto;
import us.tx.state.dfps.service.personsearch.dao.PersonSearchDao;
import us.tx.state.dfps.service.personsearch.service.PersonSearchService;
import us.tx.state.dfps.xmlstructs.outputstructs.ServiceOutputDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Service
 * implementation for functions required for implementing PersonSearch
 * functionality Oct 30, 2017- 6:46:39 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Service
@Transactional
public class PersonSearchServiceImpl implements PersonSearchService {

	@Autowired
	private PersonSearchDao personSearchDao;

	private static final Logger log = Logger.getLogger("ServiceBusiness-PersonSearchServiceLog");

	/**
	 * 
	 * Method Name: getPersonIdentifier Method Description:This method gets the
	 * person identifier number for a given person identifier type
	 * 
	 * @param idPerson
	 * @param idType
	 * @return String
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public String getPersonIdentifier(Long idPerson, String idType) {
		log.debug("Entering method getPersonIdentifier in PersonSearchService");

		String personIdentifier = ServiceConstants.EMPTY_STRING;
		personIdentifier = personSearchDao.getPersonIdentifier(idPerson, idType);
		log.debug("Exiting method getPersonIdentifier in PersonSearchService");
		return personIdentifier;
	}

	/**
	 * 
	 * Method Name: getPersonAddress Method Description:This method get person
	 * address details
	 * 
	 * @param idPerson
	 * @return AddressValueDto
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public AddressValueDto getPersonAddress(Long idPerson) {
		log.debug("Entering method getPersonAddress in PersonSearchService");
		AddressValueDto addressValueDto = new AddressValueDto();
		addressValueDto = personSearchDao.getIdPersonAddress(idPerson);
		log.debug("Exiting method getPersonAddress in PersonSearchService");
		return addressValueDto;
	}

	/**
	 * 
	 * Method Name: performPartialSearch Method Description:This method returns
	 * result of Partial Search
	 * 
	 * @param paginationResultDto
	 * @param personSearchInRecDto
	 * @return PersonSearchOutRecDto
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PersonSearchOutRecDto performPartialSearch(PersonSearchInRecDto personSearchInRecDto,
			PaginationResultDto paginationResultDto) {
		log.debug("Entering method performPartialSearch in PersonSearchService");

		PersonSearchOutRecDto personSearchOutRec = new PersonSearchOutRecDto();
		personSearchOutRec = personSearchDao.performPartialSearch(personSearchInRecDto, paginationResultDto);
		log.debug("Exiting method performPartialSearch in PersonSearchService");
		return personSearchOutRec;

	}

	/**
	 * Method Name: getSearchPersonView Method Description: Returns result of
	 * person view search
	 * 
	 * @param paginationResultBeanDto
	 * @return PersonSearchRes
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PersonSearchRes getSearchPersonView(PersonSearchReq personSearchReq) {
		log.debug("Entering method getSearchPersonView in PersonSearchService");
		PersonSearchRes personSearchRes = new PersonSearchRes();
		ServiceOutputDto ServiceOutputDto = new ServiceOutputDto();
		personSearchRes.setServiceOutputDto(ServiceOutputDto);
		PersonSearchOutRecDto personSearchOutRec = personSearchDao.getPersonSearchView(
				personSearchReq.getPersonIdentifierValueNumber(), personSearchReq.getPaginationResultDto());
		personSearchRes.setPersonSearchOutRecDto(personSearchOutRec);
		log.debug("Exiting method getSearchPersonView in PersonSearchService");
		return personSearchRes;
	}

	/**
	 * Method Name: populateAddtnlInfoIntakeSearch Method Description: Populates
	 * additional info from intake search
	 * 
	 * @param personSearchReq
	 * @return PersonSearchRes
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PersonSearchRes populateAddtnlInfoIntakeSearch(PersonSearchReq personSearchReq) {
		log.debug("Entering method populateAddtnlInfoIntakeSearch in PersonSearchService");
		PersonSearchRes personSearchRes = new PersonSearchRes();
		PrsnSrchListpInitArrayDto prsnSrchListpInitArrayDto = personSearchDao
				.populateAddtnlInfoIntakeSearch(personSearchReq.getPrsnSrchListpInitArrayDto());
		personSearchRes.setPrsnSrchListpInitArrayDto(prsnSrchListpInitArrayDto);
		log.debug("Exiting method populateAddtnlInfoIntakeSearch in PersonSearchService");
		return personSearchRes;
	}

	/**
	 * Method Name: populateAddtnlInfoRegularSearch Method Description:
	 * Populates additional info from Regular search
	 * 
	 * @param personSearchReq
	 * @return PersonSearchRes
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PersonSearchRes populateAddtnlInfoRegularSearch(PersonSearchReq personSearchReq) {
		log.debug("Entering method populateAddtnlInfoRegularSearch in PersonSearchService");
		PersonSearchRes personSearchRes = new PersonSearchRes();
		PrsnSearchOutRecArrayDto prsnSearchOutRecArrayDto = personSearchDao.populateAddtnlInfoRegularSearch(
				personSearchReq.getPrsnSearchOutRecArrayDto(), personSearchReq.isFlag());
		personSearchRes.setPrsnSearchOutRecArrayDto(prsnSearchOutRecArrayDto);
		log.debug("Exiting method populateAddtnlInfoRegularSearch in PersonSearchService");
		return personSearchRes;
	}

	/**
	 * Method Name: performDOBSearch Method Description: Returns result of DOB
	 * search
	 * 
	 * @param personSearchReq
	 * @return PersonSearchRes
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PersonSearchRes performDOBSearch(PersonSearchReq personSearchReq) {
		log.debug("Entering method performDOBSearch in PersonSearchService");
		PersonSearchRes personSearchRes = new PersonSearchRes();
		PersonSearchOutRecDto personSearchOutRec = personSearchDao
				.performDOBSearch(personSearchReq.getPersonSearchInRecDto(), personSearchReq.getPaginationResultDto());
		personSearchRes.setPersonSearchOutRecDto(personSearchOutRec);
		log.debug("Exiting method performDOBSearch in PersonSearchService");
		return personSearchRes;
	}

}
