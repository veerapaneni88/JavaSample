package us.tx.state.dfps.service.contacts.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.CriminalRecordsDoDto;
import us.tx.state.dfps.service.admin.dto.CriminalRecordsDto;

public interface CriminalRecordsDao {
	/**
	 * 
	 * Method Name: getCriminalCheckRecords Method
	 * Description:getCriminalCheckRecords
	 * 
	 * @param pInputDataRec
	 * @return @
	 */
	public List<CriminalRecordsDoDto> getCriminalCheckRecords(CriminalRecordsDto pInputDataRec);

}
