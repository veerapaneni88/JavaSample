package us.tx.state.dfps.service.person.service;

import java.util.List;

import us.tx.state.dfps.common.domain.PersonEthnicity;
import us.tx.state.dfps.common.domain.PersonRace;
import us.tx.state.dfps.service.admin.dto.PersonEthnicityDto;
import us.tx.state.dfps.service.admin.dto.PersonRaceDto;
import us.tx.state.dfps.service.common.request.SearchPersonRaceEthnicityReq;
import us.tx.state.dfps.service.common.response.SearchPersonRaceEthnicityRes;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN95S,
 * CCMN04S,CCMN05S Class Description: Opeerations for Person Race Ethnicity Apr
 * 14, 2017 - 11:44:33 AM
 */

public interface PersonRaceEthnicityService {

	/**
	 * 
	 * Method Description:searchPersonRaceEthnicity
	 * 
	 * @param searchPersonRaceEthnicityReq
	 * @return
	 * @throws DataNotFoundException
	 * @
	 */
	// CCMN95S
	public SearchPersonRaceEthnicityRes searchPersonRaceEthnicity(
			SearchPersonRaceEthnicityReq searchPersonRaceEthnicityReq);

	/**
	 * 
	 * Method Description:getPersonRaceDto
	 * 
	 * @param personRace
	 * @return
	 * @throws DataNotFoundException
	 * @
	 */
	// CCMN95S
	public PersonRaceDto getPersonRaceDto(PersonRace personRace);

	/**
	 * 
	 * Method Description:getPersonEthnicityDto
	 * 
	 * @param personEthnicity
	 * @return
	 * @throws DataNotFoundException
	 * @
	 */
	// CCMN95S
	public PersonEthnicityDto getPersonEthnicityDto(PersonEthnicity personEthnicity);

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
	public List<PersonRaceDto> getPersonRaceList(Long idPerson, Long idReferenceData, String cdActionType,
			String cdSnapshotType);

}
