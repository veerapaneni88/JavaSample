package us.tx.state.dfps.service.workload.dao;

import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CCMN19S Class
 * Description: This Method extends BaseDao and implements FamilyAssmtDao. This
 * is used to perform the add, update & delete operations in table. Apr 3 , 2017
 * - 3:50:30 PM
 */
public interface FamilyAssmtDao {

	/**
	 * 
	 * Method Description: Method is implemented in FamilyAssmtDaoImpl to
	 * perform AUD operations Service Name: CCMN19S
	 * 
	 * @param archInputDto
	 * @param idEvent
	 * @param idStage
	 * @return ServiceResHeaderDto @
	 */
	public String getFamilyAssmtAUD(ServiceReqHeaderDto serviceReqHeaderDto, Long idEvent, Long idStage);
}
