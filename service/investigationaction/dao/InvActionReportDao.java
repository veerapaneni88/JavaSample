package us.tx.state.dfps.service.investigationaction.dao;

import java.util.List;

import us.tx.state.dfps.riskandsafetyassmt.dto.InvstActionQuestionDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * InvActionReportDao will have all operation which are mapped to
 * InvActionReport module. Apr 30, 2018- 2:01:02 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface InvActionReportDao {

	/**
	 * 
	 * Method Name: getInvstActionQuestions DAM Name : CINV04D Method
	 * Description: This dam retrieves seven rows from the invst_action_
	 ** question table based upon an id_event.
	 * 
	 * @param idEvent
	 * @return
	 */
	public List<InvstActionQuestionDto> getInvstActionQuestions(Long idEvent);

}
