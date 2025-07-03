package us.tx.state.dfps.service.person.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import us.tx.state.dfps.service.admin.dto.PersonEthnicityDto;
import us.tx.state.dfps.service.admin.dto.PersonRaceDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.person.dto.RaceEthnicityDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PersonRaceEthnicityDao Oct 5, 2017- 11:03:10 AM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface PersonRaceEthnicityDao {

	/**
	 * Method Name: getPersonRaceList Method Description: Get Person Race for
	 * input person id.
	 * 
	 * @param idPerson
	 * @return ArrayList<PersonRaceDto>
	 * @throws DataNotFoundException
	 */
	public ArrayList<PersonRaceDto> getPersonRaceList(Long idPerson);

	/**
	 * Method Name: getPersonEthnicityList Method Description: This function
	 * fetches the Person Ethnicity list for a Person
	 * 
	 * @param idPerson
	 * @return List<PersonEthnicityDto>
	 */
	public List<PersonEthnicityDto> getPersonEthnicityList(Long idPerson);

	/**
	 * Method Name: queryPersonRace Method Description:fetch cdRace
	 * 
	 * @param personId
	 * @param reb
	 * @return RaceEthnicityDto
	 */
	public RaceEthnicityDto queryPersonRace(Long personId, RaceEthnicityDto reb);

	/**
	 * Method Name: queryPersonEthnicity Method Description:fetch cdEthnicity
	 * 
	 * @param personId
	 * @return String
	 */
	public String queryPersonEthnicity(Long personId);

	/**
	 * Method Name: selectPersonRace Method Description: This method returns
	 * Person Races separated by comma.
	 *
	 * @param personIdList1
	 * @return Map
	 * @throws DataNotFoundException
	 */
	public Map selectPersonRace(List<Integer> personIdList1);

	/**
	 * Method Name: selectPersonEthnicity Method Description: This method
	 * returns Person Ethnicity separated by comma.
	 *
	 * @param personIdList1
	 * @return Map
	 * @throws DataNotFoundException
	 */
	public Map selectPersonEthnicity(List<Integer> personIdList1);

}