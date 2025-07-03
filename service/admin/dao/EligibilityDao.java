package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.common.domain.Eligibility;
import us.tx.state.dfps.service.admin.dto.EligibilityInDto;
import us.tx.state.dfps.service.admin.dto.EligibilityOutDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Cses38d Aug 9, 2017- 2:58:30 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface EligibilityDao {

	/**
	 * 
	 * Method Name: getEligibilityRecord Method Description: It retrieves the
	 * currently active record of ELIGIBILITY
	 * 
	 * @param pInputDataRec
	 * @return List<Cses38doDto> @
	 */
	public List<EligibilityOutDto> getEligibilityRecord(EligibilityInDto pInputDataRec) throws DataNotFoundException;

	Eligibility getEligibilityByEligibilityEventId(Long idEligibilityEvent);
}
