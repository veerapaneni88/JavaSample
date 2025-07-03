package us.tx.state.dfps.service.person.dao;

import java.util.ArrayList;
import java.util.List;

import us.tx.state.dfps.service.conservatorship.dto.CharacteristicsDto;
import us.tx.state.dfps.service.person.dto.PersCharsDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Dao class
 * for person characters> May 7, 2018- 10:38:20 AM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface PersonCharDao {
	/**
	 * 
	 * Method Name: getPersonCharList Method Description: get person char list
	 * by idPerson
	 * 
	 * @param idPerson
	 * @return ArrayList<CharacteristicsDto>
	 */
	public ArrayList<CharacteristicsDto> getPersonCharList(int idPerson);

	/**
	 * 
	 * Method Name: savePersonChar Method Description: save person char
	 * 
	 * @param characteristicsDto
	 * @return long
	 */
	public long savePersonChar(CharacteristicsDto characteristicsDto);

	/**
	 * 
	 * Method Name: updatePersonChar Method Description: update person char
	 * 
	 * @param characteristicsDto
	 * @return long
	 */
	public long updatePersonChar(CharacteristicsDto characteristicsDto);

	/**
	 * Method Name: getPersonCharList Method Description:Fetches the person
	 * characteristics from snapshot table ( SS_CHARACTERISTICS )
	 * 
	 * @param idPerson
	 * @param cdCharCategory
	 * @param idReferenceData
	 * @param cdActionType
	 * @param cdSnapshotType
	 * @return List<PersCharsDto>
	 */
	public List<PersCharsDto> getPersonCharData(Long idPerson, String cdCharCategory, Long idReferenceData,
			String cdActionType, String cdSnapshotType);

	/**
	 * Method Name: getPersonCharData Method Description:Takes person ID and a
	 * characteristics category ( = CHARACTERISTICS.CD_CHAR_CATEGORY ) and
	 * retrieves the characteristics rows with the given category, which the
	 * person has now or had in the past, with status code for each row, but
	 * including only the most recent row for each characteristic.
	 * 
	 * @param idPerson
	 * @param cdCharCategory
	 * @return List<PersCharsDto>
	 */
	public List<PersCharsDto> getPersonCharData(long idPerson, String cdCharCategory);

}
