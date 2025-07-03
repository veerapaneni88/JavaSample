package us.tx.state.dfps.service.conservatorship.dao;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import us.tx.state.dfps.common.domain.Characteristics;
import us.tx.state.dfps.service.conservatorship.dto.CharacteristicsDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CSUB14S Class
 * Description: CharacterisitcsDao Interface May 1, 2017 - 2:55:39 PM
 */
public interface CharacteristicsDao {
	/**
	 * Method Description: This method will give full row select of the
	 * CHARACTERISTICS table for a given date for a given ID PERSON. Service
	 * Name: CSUB14S DAM: CLSS60D
	 * 
	 * @param personId
	 * @param startDate
	 * @param endDate
	 * @return CharacteristicsDto
	 * @ @throws
	 *       ParseException
	 */
	public List<CharacteristicsDto> getCharDtls(Long personId, Date startDate, Date endDate);

	/**
	 * This DAM update of PERSON CHARACTERISTICS table. It does a full row add,
	 * or updates the end date. There is no delete functionality.
	 * 
	 * Service Name : CCMN03U, DAM Name : CINV48D
	 * 
	 * @param personId
	 * @return @
	 */
	public List<Long> getCharacteristicsIdsByPersonId(Long personId);

	/**
	 * This DAM update of PERSON CHARACTERISTICS table. It does a full row add,
	 * or updates the end date. There is no delete functionality.
	 * 
	 * Service Name : CCMN03U, DAM Name : CINV48D
	 * 
	 * @param characteristics
	 * @
	 */
	public void characteristicsSave(Characteristics characteristics);

	/**
	 * This DAM update of PERSON CHARACTERISTICS table. It does a full row add,
	 * or updates the end date. There is no delete functionality.
	 * 
	 * Service Name : CCMN03U, DAM Name : CINV48D
	 * 
	 * @param characteristics
	 * @
	 */
	public void characteristicsUpdate(Characteristics characteristics);

	/**
	 * This DAM update of PERSON CHARACTERISTICS table. It does a full row add,
	 * or updates the end date. There is no delete functionality.
	 * 
	 * Service Name : CCMN03U, DAM Name : CINV48D
	 * 
	 * @param idChara
	 * @return @
	 */
	public Characteristics getCharacteristicsById(Long idChara);

	/**
	 * 
	 * Method Name: getCharByPersonIdAndCategory Method Description: DAM CLSS46D
	 * select all the characteristics for a child given ID PERSON and the CD
	 * CHAR CATEG which are effective today.
	 * 
	 * @param idPerson
	 * @param cdCharCategory
	 * @return @
	 */
	public List<CharacteristicsDto> getCharByPersonIdAndCategory(Long idPerson, String cdCharCategory);

	/**
	 * 
	 * Method Name: getCharacteristicDetails Method Description:Retrieves the
	 * characteristic details from the CHARACTERISTIC table based on the Person
	 * ID and the effective end characteristic date
	 * 
	 * @param idpersonId
	 * @return List
	 */
	public List<CharacteristicsDto> getCharacteristicDetails(Long idpersonId);

	/**
	 * 
	 * Method Name: getCharacteristicDetailsDate Method Description:Retrieves
	 * the characteristic details from the CHARACTERISTIC table based on the
	 * Person ID, the effective start characteristic date and the effective end
	 * characteristic date.
	 * 
	 * @param idpersonId
	 * @param effectiveDate
	 * @return List
	 */
	public List<CharacteristicsDto> getCharacteristicDetailsDate(Long idpersonId, Date effectiveDate);

}
