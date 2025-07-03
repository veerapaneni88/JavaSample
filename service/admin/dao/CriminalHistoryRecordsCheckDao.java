package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.CriminalHistoryRecordsCheckInDto;
import us.tx.state.dfps.service.admin.dto.CriminalHistoryRecordsCheckOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<DAO
 * Interface to fetch Criminal Records> Aug 8, 2017- 3:41:00 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface CriminalHistoryRecordsCheckDao {

	/**
	 * 
	 * Method Name: getCriminalCheckRecords Method Description: This method will
	 * get data from Criminal History table.
	 * 
	 * @param pInputDataRec
	 * @return List<CriminalHistoryRecordsCheckOutDto> @
	 */
	public List<CriminalHistoryRecordsCheckOutDto> getCriminalCheckRecords(
			CriminalHistoryRecordsCheckInDto pInputDataRec);
}
