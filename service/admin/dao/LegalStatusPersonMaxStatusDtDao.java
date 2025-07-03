package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.LegalStatusPersonMaxStatusDtInDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusPersonMaxStatusDtOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Cses32 Aug 11, 2017- 8:44:08 AM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface LegalStatusPersonMaxStatusDtDao {

	/**
	 * Method Name: getRecentLegelStatusRecord Method Description: Fetch recent
	 * legal status for the person
	 * 
	 * @param pInputDataRec
	 * @return List<Cses32doDto>
	 * @,DataNotFoundException
	 */
	public List<LegalStatusPersonMaxStatusDtOutDto> getRecentLegelStatusRecord(
			LegalStatusPersonMaxStatusDtInDto pInputDataRec);
}
