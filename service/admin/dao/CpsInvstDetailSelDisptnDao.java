package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.CpsInvstDetailInDto;
import us.tx.state.dfps.service.admin.dto.CpsInvstDetailOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Interface
 * for Cses0a Aug 5, 2017- 10:42:05 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface CpsInvstDetailSelDisptnDao {

	/**
	 * 
	 * Method Name: retCPSInvest Method Description: This method will retrieve
	 * CD_CPS_INVST_DTL_OVRLL_DISPTN from CPS_INVST_DTL table.
	 * 
	 * @param cpsInvstDetailInDto
	 * @return List<CpsInvstDetailOutDto>
	 */
	public List<CpsInvstDetailOutDto> retCPSInvest(CpsInvstDetailInDto cpsInvstDetailInDto);
}
