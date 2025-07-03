/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:Interface for family plan item detail
 *Jul 6, 2018- 12:15:26 PM
 *
 */
package us.tx.state.dfps.service.familyplan.service;

import us.tx.state.dfps.service.common.response.FamilyPlanItemDtlRes;
import us.tx.state.dfps.service.forms.dto.FamilyPlanDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanItemDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:FamilyPlanItemDtlServiceImpl will implemented all operation
 * defined in FamilyPlanItemDtlService Interface related FamilyPlanItem module.
 * Jul 6, 2018- 2:02:21 PM © 2017 Texas Department of Family and Protective
 * Services
 */
public interface FamilyPlanItemDtlService {

	/**
	 * Method Name: getFamilyPlanItemDtl Method Description:This method is used
	 * to get the family plan item detail based on the selected family plan .
	 * 
	 * @param familyPlanDto
	 * @param pageMode
	 * @return FamilyPlanItemDto
	 */
	public FamilyPlanItemDto getFamilyPlanItemDtl(FamilyPlanDto familyPlanDto, String pageMode);

	/**
	 * Method Name: saveFamilyPlanItemDtl Method Description:Saves the family
	 * plan item details to the database.
	 * 
	 * @param familyPlanItemDto
	 * @param familyPlanDto
	 */
	FamilyPlanItemDtlRes saveFamilyPlanItemDtl(FamilyPlanItemDto familyPlanItemDto, FamilyPlanDto familyPlanDto);

	/**
	 * Method Name: deleteFamilyPlanItem Method Description:resets the values
	 * from family_plan_item table.
	 * 
	 * @param familyPlanItemDto
	 * @param familyPlanDto
	 */
	Long deleteFamilyPlanItem(FamilyPlanItemDto familyPlanItemDto, FamilyPlanDto familyPlanDto);

}
