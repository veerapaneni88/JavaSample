package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.CpsInvstDetailStageIdInDto;
import us.tx.state.dfps.service.admin.dto.CpsInvstDetailStageIdOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This Class
 * Fetches Investment Details Using StageID Aug 5, 2017- 7:36:42 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface CpsInvstDetailStageIdDao {

	/**
	 * 
	 * Method Name: getInvstDtls Method Description: This Method Fetches
	 * Investment Details Using StageID
	 * 
	 * @param pInputDataRec
	 * @return List<CpsInvstDetailStageIdOutDto> @
	 */
	public List<CpsInvstDetailStageIdOutDto> getInvstDtls(CpsInvstDetailStageIdInDto pInputDataRec);
}
