package us.tx.state.dfps.service.person.serviceimpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.common.domain.PersonEthnicity;
import us.tx.state.dfps.common.domain.PersonRace;
import us.tx.state.dfps.service.admin.dto.PersonEthnicityDto;
import us.tx.state.dfps.service.admin.dto.PersonRaceDto;
import us.tx.state.dfps.service.common.request.SearchPersonRaceEthnicityReq;
import us.tx.state.dfps.service.common.response.SearchPersonRaceEthnicityRes;
import us.tx.state.dfps.service.person.dao.PersonEthnicityDao;
import us.tx.state.dfps.service.person.dao.PersonRaceDao;
import us.tx.state.dfps.service.person.service.PersonRaceEthnicityService;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Service
 * Implement class for Person Race Ethnicity May 31, 2018- 11:37:42 AM © 2017
 * Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class PersonRaceEthnicityServiceImpl implements PersonRaceEthnicityService {

	@Autowired
	PersonRaceDao personRaceDao;

	@Autowired
	PersonEthnicityDao personEthnicityDao;

	private static final Logger log = Logger.getLogger(PersonRaceEthnicityServiceImpl.class);

	/**
	 * 
	 * Method Description:searchPersonRaceEthnicity
	 * 
	 * @param searchPersonRaceEthnicityReq
	 * @return
	 */
	// CCMN95S
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public SearchPersonRaceEthnicityRes searchPersonRaceEthnicity(
			SearchPersonRaceEthnicityReq searchPersonRaceEthnicityReq) {
		SearchPersonRaceEthnicityRes searchPersonRaceEthnicityRes = new SearchPersonRaceEthnicityRes();
		List<PersonRaceDto> personRaceDtoList = null;
		List<PersonEthnicityDto> personEthnicityDtoList = null;
		if (searchPersonRaceEthnicityReq != null) {
			if (searchPersonRaceEthnicityReq.getIdPerson() != null) {
				personRaceDtoList = personRaceDao.getPersonRaceByPersonId(searchPersonRaceEthnicityReq.getIdPerson());
				personEthnicityDtoList = personEthnicityDao
						.getPersonEthnicityByPersonId(searchPersonRaceEthnicityReq.getIdPerson());
			}
		}
		searchPersonRaceEthnicityRes.setPersonRaceDtoList(personRaceDtoList);
		searchPersonRaceEthnicityRes.setPersonEthnicityDtoList(personEthnicityDtoList);
		if (null != searchPersonRaceEthnicityReq) {
			searchPersonRaceEthnicityRes.setTransactionId(searchPersonRaceEthnicityReq.getTransactionId());
			log.info("TransactionId :" + searchPersonRaceEthnicityReq.getTransactionId());
		}
		return searchPersonRaceEthnicityRes;
	}

	/**
	 * 
	 * Method Description:getPersonRaceDto
	 * 
	 * @param personRace
	 * @return
	 */
	@Override
	public PersonRaceDto getPersonRaceDto(PersonRace personRace) {
		PersonRaceDto personRaceDto = new PersonRaceDto();
		if (personRace != null) {
			if (personRace.getDtLastUpdate() != null) {
				personRaceDto.setDtRaceUpdate(personRace.getDtLastUpdate());
			}
			if (personRace.getCdRace() != null) {
				personRaceDto.setCdPersonRace(personRace.getCdRace());
			}
			if (personRace.getPerson() != null) {
				if (personRace.getPerson().getIdPerson() != null) {
					personRaceDto.setIdPerson(personRace.getPerson().getIdPerson());
				}
			}
			if (personRace.getIdPersonRace() != null) {
				personRaceDto.setIdPersonRace(personRace.getIdPersonRace());
			}
		}
		return personRaceDto;
	}

	/**
	 * 
	 * Method Description:getPersonEthnicityDto
	 * 
	 * @param personEthnicity
	 * @return
	 */
	@Override
	public PersonEthnicityDto getPersonEthnicityDto(PersonEthnicity personEthnicity) {
		PersonEthnicityDto personEthnicityDto = new PersonEthnicityDto();
		if (personEthnicity != null) {
			if (personEthnicity.getDtLastUpdate() != null) {
				personEthnicityDto.setDtEthnicityUpdate(personEthnicity.getDtLastUpdate());
			}
			if (personEthnicity.getCdEthnicity() != null) {
				personEthnicityDto.setCdPersonEthnicity(personEthnicity.getCdEthnicity());
			}
			if (personEthnicity.getPerson() != null) {
				if (personEthnicity.getPerson().getIdPerson() != null) {
					personEthnicityDto.setIdPerson(personEthnicity.getPerson().getIdPerson());
				}
			}
			if (personEthnicity.getIdPersonEthnicity() != null) {
				personEthnicityDto.setIdPersonEthnicity(personEthnicity.getIdPersonEthnicity());
			}
		}
		return personEthnicityDto;
	}

	/**
	 * Method Name: getPersonRaceList Method Description:Fetches Race List for a
	 * person from Snapshot table ( SS_PERSON_RACE)
	 * 
	 * @param idPerson
	 * @param idReferenceData
	 * @param cdActionType
	 * @param cdSnapshotType
	 * @return List<PersonRaceDto>
	 * @throws DataNotFoundException
	 */

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public List<PersonRaceDto> getPersonRaceList(Long idPerson, Long idReferenceData, String cdActionType,
			String cdSnapshotType) {
		List<PersonRaceDto> personRaceDtoList = null;
		personRaceDtoList = personEthnicityDao.getPersonRaceList(idPerson, idReferenceData, cdActionType,
				cdSnapshotType);
		return personRaceDtoList;
	}
}
