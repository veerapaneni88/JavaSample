package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.AllegationStageCaseInDto;
import us.tx.state.dfps.service.admin.dto.AllegationStageCaseOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Cinvg1dDao
 * Aug 6, 2017- 6:52:41 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface AllegationStageCaseDao {

	/**
	 * 
	 * Method Name: caseExistDtls Method Description: Retrieves data from
	 * Allegation and Stage tables.
	 * 
	 * @param pInputDataRec
	 * @return List<AllegationStageCaseOutDto> @
	 */
	public List<AllegationStageCaseOutDto> caseExistDtls(AllegationStageCaseInDto pInputDataRec);
}
