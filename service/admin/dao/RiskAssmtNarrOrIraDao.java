package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.RiskAssmtNarrOrIraInDto;
import us.tx.state.dfps.service.admin.dto.RiskAssmtNarrOrIraOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:DAO
 * Interface for fetching Event Dtls Aug 6, 2017- 4:37:27 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface RiskAssmtNarrOrIraDao {
	/**
	 * 
	 * Method Name: getEventDtls Method Description: This method will retrieves
	 * Event Details.
	 * 
	 * @param pInputDataRec
	 * @return List<RiskAssmtNarrOrIraOutDto> @
	 */
	public List<RiskAssmtNarrOrIraOutDto> getEventDtls(RiskAssmtNarrOrIraInDto pInputDataRec);

}
