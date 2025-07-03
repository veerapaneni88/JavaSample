/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Aug 9, 2018- 2:40:37 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.sscc.service;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.SSCCPlcmtOptCircumListDto;
import us.tx.state.dfps.service.sscc.dto.SSCCPlcmtOptCircumDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This class
 * contains all the service calls needed by the SSCC Placement Option and
 * Circumstance List page Aug 9, 2018- 2:40:37 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface SSCCOptCircumListService {

	/**
	 * Method Name: fetchSSCCOptCircumList Method Description: This service
	 * calls the Dao method to retrieve the SSCC options and circumstances
	 * belonging to the passed stage Id.
	 * 
	 * @param idStage
	 * @param userDto
	 * @return List<SSCCPlcmtOptCircumListDto>
	 */
	public List<SSCCPlcmtOptCircumListDto> fetchSSCCOptCircumList(Long idStage);

	/**
	 * Method Name: rescindAndRetrieveList Method Description: This service
	 * method calls the rescind and fetchSSCCOptCircumList
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return List<SSCCPlcmtOptCircumListDto>
	 */
	public List<SSCCPlcmtOptCircumListDto> rescindAndRetrieveList(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto);

	/**
	 * Method Name: rescind Method Description: This service updates the
	 * Placement Header with rescind status, SSCC List and creates an SSCC
	 * Timeline
	 * 
	 * @param ssccPlcmtOptCircumDto
	 * @return
	 */
	public void rescind(SSCCPlcmtOptCircumDto ssccPlcmtOptCircumDto);

}
