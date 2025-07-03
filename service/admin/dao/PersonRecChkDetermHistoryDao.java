package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.PersonRecChkDetermHistoryInDto;
import us.tx.state.dfps.service.admin.dto.PersonRecChkDetermHistoryOutDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Interface
 * for PersonRecChkDetermHistoryDaoImpl Aug 7, 2017- 4:29:42 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface PersonRecChkDetermHistoryDao {

	/**
	 * 
	 * Method Name: getRecordCheckDeterminationDtls Method Description: This
	 * method will query the RecordCheckDeter table to fetch the record check
	 * details for a given record ID
	 * 
	 * @param pInputDataRec
	 * @return List<PersonRecChkDetermHistoryOutDto>
	 * 
	 */
	public List<PersonRecChkDetermHistoryOutDto> getRecordCheckDeterminationDtls(
			PersonRecChkDetermHistoryInDto pInputDataRec);
}
