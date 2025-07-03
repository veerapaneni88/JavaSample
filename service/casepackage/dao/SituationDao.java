package us.tx.state.dfps.service.casepackage.dao;

import us.tx.state.dfps.common.domain.Situation;
import us.tx.state.dfps.service.casepackage.dto.SituationDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN03U Class
 * Description: Situation Dao Interface Mar 24, 2017 - 7:15:39 PM
 */
public interface SituationDao {

	/**
	 * Performs AUD functions on the SITUATION table. Note: This DAM was not
	 * written to account for intervening updates. Warning: There is
	 * non-GENDAM'd generated code in this file.
	 * 
	 * Service Name : CCMN03U, DAM Name : CINT13D
	 * 
	 * @param situation
	 * @
	 */
	public void saveSituation(Situation situation);

	/**
	 * Performs AUD functions on the SITUATION table. Note: This DAM was not
	 * written to account for intervening updates. Warning: There is
	 * non-GENDAM'd generated code in this file.
	 * 
	 * Service Name : CCMN03U, DAM Name : CINT13D
	 * 
	 * @param situation
	 * @
	 */
	public void updateSituation(Situation situation);

	/**
	 * Performs AUD functions on the SITUATION table. Note: This DAM was not
	 * written to account for intervening updates. Warning: There is
	 * non-GENDAM'd generated code in this file.
	 * 
	 * Service Name : CCMN03U, DAM Name : CINT13D
	 * 
	 * @param situation
	 * @
	 */
	public void deleteSituation(Situation situation);

	/**
	 * Performs AUD functions on the SITUATION table. Note: This DAM was not
	 * written to account for intervening updates. Warning: There is
	 * non-GENDAM'd generated code in this file.
	 * 
	 * Service Name : CCMN03U, DAM Name : CINT13D
	 * 
	 * @param idSituation
	 * @return @
	 */
	public Situation getSituationEntityById(Long idSituation);

	/**
	 * 
	 * Method Name: insertIntoSituation Method Description:This method inserts
	 * record into SITUATION table.
	 * 
	 * @param situationDto
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long insertIntoSituation(SituationDto situationDto);

	/**
	 * Method Name: closeSituation Method Description: This method closes the
	 * Situation Record
	 * 
	 * @param idSituation
	 * @return Long
	 * 
	 */
	public Long closeSituation(Long idSituation);
}
