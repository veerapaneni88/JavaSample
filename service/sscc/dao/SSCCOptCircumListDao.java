/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Aug 9, 2018- 4:08:35 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.sscc.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.SSCCPlcmtOptCircumListDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This
 * interface has all the methods that retrieve or modify SSCC Placement Options
 * and circumstances entities Aug 9, 2018- 4:08:35 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface SSCCOptCircumListDao {

	/**
	 * Method Name: retrieveSSCCOptCircumList Method Description: This method
	 * retrieves the SSCC Placement options and circumstances related to the
	 * stage
	 * 
	 * @param idStage
	 * @return List<SSCCPlcmtOptCircumListDto>
	 */
	public List<SSCCPlcmtOptCircumListDto> retrieveSSCCOptCircumList(Long idStage);

}
