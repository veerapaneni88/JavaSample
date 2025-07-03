package us.tx.state.dfps.service.conservatorship.dao;

import java.util.List;

import us.tx.state.dfps.service.cvs.dto.ConservtorshipRmlDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: dao for
 * ConservtorshipRmlDao Sep 8, 2017- 12:18:55 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface ConservtorshipRmlDao {

	/**
	 * Method Name: updateDenyDate Method Description:
	 * 
	 * @param idPerson
	 * @return long @
	 */
	public long updateDenyDate(long idPerson);

	/**
	 * Method Name: rlnqushQuestionAnsweredY Method Description:
	 * 
	 * @param idStage
	 * @param idVictim
	 * @return long @
	 */
	public long rlnqushQuestionAnsweredY(long idStage, long idVictim);

	/**
	 * Method Name: prsnCharSelected Method Description:
	 * 
	 * @param idStage
	 * @param idVictim
	 * @return long @
	 */
	public long prsnCharSelected(long idStage, long idVictim);

	/**
	 * Method Name: getCnsrvtrRmvlPersonId Method Description:
	 * 
	 * @param idCase
	 * @param idRemovalEvent
	 * @return List<String> @
	 */
	public List<ConservtorshipRmlDto> getCnsrvtrRmvlPersonId(long idCase, long idRemovalEvent);

}
