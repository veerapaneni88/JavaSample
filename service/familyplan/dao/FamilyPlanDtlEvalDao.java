/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: Dao Class to fetch data from stored Procedure
 *Jul 18, 2018- 3:04:03 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.familyplan.dao;

import us.tx.state.dfps.familyplan.request.FamilyPlanDtlEvalReq;
import us.tx.state.dfps.familyplan.response.FamilyPlanDtlEvalRes;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Dao Class
 * to fetch data from stored Procedure> Jul 18, 2018- 3:04:03 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface FamilyPlanDtlEvalDao {

	/**
	 * Method Name: addFamilyPlan Method Description: refill read only
	 * information to Family Plan detail details after store procedure call.
	 * 
	 * @param familyPlanInDto
	 * @return FamilyPlanDtlEvalRes
	 */
	public FamilyPlanDtlEvalRes getFamilyPlanDetails(FamilyPlanDtlEvalReq familyPlanReq);

	/**
	 * Method Name: fetchLatestLegalStatusForCase Method Description: This
	 * method is used to get latest legal status for the case.
	 * 
	 * @param idCase
	 * @return String
	 */
	public String fetchLatestLegalStatusForCase(Long idCase);
	
	
	/**
	 * Method Name: legalActionPresent Method Description: This
	 * method is used to fetch Legal Action for stage.
	 * 
	 * @param idCase
	 * @param idStage
	 * @return Boolean
	 */
	public Boolean legalActionPresent(Long idCase, Long idStage);

}
