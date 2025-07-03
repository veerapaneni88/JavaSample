package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.CriminalHistoryRecsCheckCountInDto;
import us.tx.state.dfps.service.admin.dto.CriminalHistoryRecsCheckCountOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * gives the count of criminal records. Aug 7, 2017- 3:23:13 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface CriminalHistoryRecsCheckCountDao {

	/**
	 * 
	 * Method Name:rtrvCriminalHistoryRecords Method Description: This method
	 * retrieves data from CRIMINAL_HISTORY and RECORDS_CHECK tables.
	 * Equivivelent Legacy Method : cinvf1dQUERYdam
	 * 
	 * @param pInputDataRec
	 * @param pOutputDataRec
	 * @return List<CriminalHistoryRecsCheckCountOutDto>
	 */
	public List<CriminalHistoryRecsCheckCountOutDto> rtrvCriminalHistoryRecords(
			CriminalHistoryRecsCheckCountInDto pInputDataRec, CriminalHistoryRecsCheckCountOutDto pOutputDataRec);
}
