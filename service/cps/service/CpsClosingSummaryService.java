package us.tx.state.dfps.service.cps.service;

import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: CPS Closing
 * Summary service Jan 27, 2018- 1:26:14 PM © 2017 Texas Department of Family
 * and Protective Services
 */
public interface CpsClosingSummaryService {
	/**
	 * 
	 * Method Name: getCpsClosingSummaryData Method Description: This method is
	 * used for populates the CPS Closing Summary Form.
	 * 
	 * @param idStage
	 * @return PreFillDataServiceDto @
	 */
	public PreFillDataServiceDto getCpsClosingSummaryData(Long idStage);
}
