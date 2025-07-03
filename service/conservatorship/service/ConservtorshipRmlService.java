package us.tx.state.dfps.service.conservatorship.service;

import us.tx.state.dfps.service.common.response.ConservtorshipRmlRes;
import us.tx.state.dfps.service.person.dto.PersonValueDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: service for
 * ConservtorshipRmlService Sep 8, 2017- 12:14:38 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface ConservtorshipRmlService {

	/**
	 * Method Name: updateDenyDate Method Description:
	 * 
	 * @param idPerson
	 * @return ConservtorshipRmlRes @
	 */
	public ConservtorshipRmlRes updateDenyDate(long idPerson);

	/**
	 * Method Name: rlnqushQuestionAnsweredY Method Description:
	 * 
	 * @param idStage
	 * @param idVictim
	 * @return boolean @
	 */
	public boolean rlnqushQuestionAnsweredY(long idStage, long idVictim);

	/**
	 * Method Name: prsnCharSelected Method Description:
	 * 
	 * @param idStage
	 * @param idVictim
	 * @return boolean @
	 */
	public boolean prsnCharSelected(long idStage, long idVictim);

	/**
	 * Method Name: getCnsrvtrRmvlPersonId Method Description:
	 * 
	 * @param idCase
	 * @param idRemovalEvent
	 * @return PersonValueDto @
	 */
	public PersonValueDto getCnsrvtrRmvlPersonId(long idCase, long idRemovalEvent);

}