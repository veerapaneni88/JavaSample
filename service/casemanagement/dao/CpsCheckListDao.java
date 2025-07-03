package us.tx.state.dfps.service.casemanagement.dao;

import java.util.List;

import us.tx.state.dfps.service.casemanagement.dto.CpsCheckListInDto;
import us.tx.state.dfps.service.casemanagement.dto.CpsCheckListOutDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:CSUB64S Aug
 * 22, 2017- 11:07:41 AM © 2017 Texas Department of Family and Protective
 * Services.
 */
public interface CpsCheckListDao {

	/**
	 * This retrieves an entire row from the CPS_CHECKLIST table containing all
	 * the information for the Services and Referrals Checklist window and
	 * checks to see if the SFI questions have been answered to allow the stage
	 * type to be changed to/from SFI.
	 *
	 * @param pInputDataRec
	 *            the input data rec
	 * @return the list
	 */
	List<CpsCheckListOutDto> csesc9dQUERYdam(CpsCheckListInDto pInputDataRec);
}
