package us.tx.state.dfps.service.admin.service;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.PersonRecChkDetermHistoryOutDto;
import us.tx.state.dfps.service.admin.dto.RecordsCheckRtrviDto;
import us.tx.state.dfps.service.common.response.RecordsCheckListRes;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:RecordsCheckRtrvService Aug 7, 2017- 3:29:07 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface RecordsCheckRtrvService {

	/**
	 * 
	 * Method Name: callRecordsCheckRtrvService Method Description:This service
	 * will retrieve all rows from the Records Check Table for a given
	 * IdRecCheckPerson(maximum page size retrieved is 11 rows).
	 * 
	 * @param pInputMsg
	 * @return RecordsCheckRtrvRes
	 * 
	 */
	public RecordsCheckListRes callRecordsCheckRtrvService(RecordsCheckRtrviDto pInputMsg);

	/**
	 * 
	 * Method Name: retrieveAllRecordChecks Method Description:This service will
	 * retrieve all rows from the Records Check Table for a given
	 * IdRecCheckPerson(maximum page size retrieved is 11 rows). Equivallent to
	 * Legacy Method CallCLSSB7D.
	 * 
	 * @param pInputMsg
	 * @param recRowsOutDto
	 * @param ulIdRecCheck
	 * @return List<PersonRecChkDetermHistoryOutDto>
	 * 
	 */
	public List<PersonRecChkDetermHistoryOutDto> retrieveAllRecordChecks(RecordsCheckRtrviDto pInputMsg,
			PersonRecChkDetermHistoryOutDto pCLSSB7DOutputRec, long ulIdRecCheck);

	/**
	 * 
	 * Method Name: hasCHActions Method Description:
	 * 
	 * @param ulIdRecCheck
	 * 
	 */
	public void hasCHActions(long ulIdRecCheck);
}
