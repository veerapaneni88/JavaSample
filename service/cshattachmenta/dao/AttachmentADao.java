/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: This is DAO Interface for CSA Page.
 *Sep 17, 2018- 4:40:34 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.cshattachmenta.dao;

import org.springframework.stereotype.Repository;

import us.tx.state.dfps.web.fcl.dto.SexualVictimHistoryDto;

/**
 * service-business- FCL Class 
 * Description: This is DAO
 * Interface for Child Sexual Victimization Page.
 * Texas Department of Family and Protective Services
 * © 2019 Texas Department of Family and Protective Services
 * Artifact ID: artf128756
 */
@Repository
public interface AttachmentADao {
	
	/**
	 * Method to fetch Child sexual victimization history incidents
	 * And 
	 * @param Long idPerson
	 * Artifact ID: artf128756
	 * */
	public SexualVictimHistoryDto fetchSexualVictimHistory(Long idPerson);

}
