package us.tx.state.dfps.service.conservatorship.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.common.dto.EmailDetailsDto;
import us.tx.state.dfps.rmvlchecklist.dto.RmvlChcklstSctnTaskValueDto;
import us.tx.state.dfps.rmvlchecklist.dto.RmvlChcklstValueDto;
import us.tx.state.dfps.service.common.request.CommonEventIdReq;
import us.tx.state.dfps.service.common.response.CommonBooleanRes;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.conservatorship.dto.CnsrvtrshpRemovalDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CSUB14S Class
 * Description: CnsrvtrshpRemovalDao Interface Apr 29, 2017 - 3:55:39 PM
 *  * **********  Change History *********************************
 * 05/04/2020 thompswa artf147748 : CPI June 2020 Project - adjustment for removal checklist data model change
 */

public interface CnsrvtrshpRemovalDao {

	/**
	 * Method Description: This Method will be used to retrieve a full row from
	 * the REMOVAL. Service Name:CSUB14S DAM:CSES20D
	 * 
	 * @param idEvent
	 * @return CnsrvtrshpRemovalDto @
	 */

	public List<CnsrvtrshpRemovalDto> getCnsrvtrshpRemovalDtl(List<Long> idEvents);

	CommonHelperRes updateIdRmvlGroup(CommonEventIdReq eventIdList);

	/**
	 * 
	 * Method Name: babyMosesRemovalReasonExists Method Description:This Method
	 * is used to check if the removal reason is close babymoses
	 * 
	 * @param idCase
	 * @return @
	 */
	public CommonBooleanRes babyMosesRemovalReasonExists(Long idCase);

	/**
	 * 
	 * Method Name: fetchEmployeeEmail Method Description: This Method is used
	 * to get the Email Id of Employee for given id event
	 * 
	 * @param commonHelperReq
	 * @return @
	 */
	public List<EmailDetailsDto> fetchEmployeeEmail(List<Long> idEventList);

	/**
	 * Method Name: getRmvlDtForEarliestEvent Method Description:This method is
	 * used to fetch the removal date from the earliest conservatorship removal.
	 * 
	 * @param idPriorStage
	 * @return Date
	 */
	public Date getRmvlDtForEarliestEvent(Long idPriorStage);

	public List<RmvlChcklstValueDto> getRmvlGroupsByStage(String stageString);

	public List<RmvlChcklstSctnTaskValueDto> getRmvlSctnTaskByStage(String stageString);
}
