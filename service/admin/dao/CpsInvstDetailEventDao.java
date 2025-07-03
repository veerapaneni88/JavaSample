package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.CpsInvstDetailEventInDto;
import us.tx.state.dfps.service.admin.dto.CpsInvstDetailEventOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION
 *
 * Class Description:DAO Interface for fetching Investment Details
 *
 * Aug 6, 2017- 3:16:58 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface CpsInvstDetailEventDao {

	/**
	 * 
	 * Method Name: getCPSInvestmentDtl Method Description: This method retrieve
	 * data from CPS_INVESTMENT_DTL table.
	 * 
	 * @param pInputDataRec
	 * @return List<CpsInvstDetailEventOutDto> @
	 */
	public List<CpsInvstDetailEventOutDto> getCPSInvestmentDtl(CpsInvstDetailEventInDto pInputDataRec);
}
