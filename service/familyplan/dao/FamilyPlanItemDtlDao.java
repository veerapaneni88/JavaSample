package us.tx.state.dfps.service.familyplan.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.service.familyplan.dto.FamilyPlanEvalItemDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanItemDto;
import us.tx.state.dfps.service.forms.dto.FamilyPlanTaskDto;

/**
 * common-domain- IMPACT PHASE 2 MODERNIZATION Class FamilyPlanItemDtlDao define
 * all operation defined which is implemented in FamilyPlanItemDtlDaoImpl. Jul
 * 6, 2018- 2:02:21 PM © 2017 Texas Department of Family and Protective Services
 */
public interface FamilyPlanItemDtlDao {

	/**
	 * Method Name: getFamilyPlanItemDtl
	 * 
	 * Method Description:This method is used to get the latest family plan item
	 * detail for the version 2
	 * 
	 * @param familyPlanItemDto
	 * @return
	 */
	FamilyPlanItemDto getFamilyPlanItemDtl(FamilyPlanItemDto familyPlanItemDto);

	/**
	 * Method Name: getFamilyPlanItemDtlLegacy
	 * 
	 * Method Description:This method is used to get the legacy family plan item
	 * detail for the version 1
	 * 
	 * @param familyPlanItemDto
	 * @return familyPlanItemDto
	 */
	FamilyPlanItemDto getFamilyPlanItemDtlLegacy(FamilyPlanItemDto familyPlanItemDto);

	/**
	 * Method Name: populateGoalsForItem Method Description: This method is used
	 * to populateGoalsForItem
	 * 
	 * @param familyPlanItemDtoResp
	 * @return FamilyPlanItemDto
	 */
	FamilyPlanItemDto populateGoalsForItem(FamilyPlanItemDto familyPlanItemDtoResp);

	/**
	 * Method Name: updateFamilyPlanItem Method Description:This method updates
	 * the FAMILY_PLAN_ITEM table.
	 * 
	 * @param familyPlanItemDto
	 */
	void updateFamilyPlanItem(FamilyPlanItemDto familyPlanItemDto, String ReqFunc);

	/**
	 * Method Name: deleteFamilyPlanEvalItem Method Description:deletes the
	 * record from FAMILY_PLAN_ITEM table
	 * 
	 * @param familyPlanItemDto
	 */
	void deleteFamilyPlanEvalItem(FamilyPlanEvalItemDto familyPlanEvalItemDto);

	/**
	 * Method Name: updateFamilyPlanEvalItem Method Description:Updates the
	 * FAMILY_PLAN_EVAL_ITEM table
	 * 
	 * @param familyPlanItemDto
	 */
	void updateFamilyPlanEvalItem(FamilyPlanEvalItemDto familyPlanEvalItemDto);

	/**
	 * Method Name: updateFamilPlanTasks Method Description:Query the goals for
	 * a particular item from the database and updates the FAMILY_PLAN_TASK
	 * table
	 * 
	 * @param familyPlanTaskDto
	 * @param idEvalEvent
	 */
	void updateFamilPlanTasks(List<FamilyPlanTaskDto> familyPlanTaskDto, Long idEvalEvent);

	/**
	 * Method Name: updateEventStatusWithoutTimestamp Method Description:Updates
	 * the event information without using a Timestamp check.
	 * 
	 * @param cdEventStatus
	 * @param idEvent
	 */
	void updateEventStatusWithoutTimestamp(String cdEventStatus, Long idEvent);

	/**
	 * Method Name: deleteFamilyPlanItem Method Description: This helps to reset
	 * the family plan item record
	 * 
	 * @param familyPlanItemDto
	 */
	void deleteFamilyPlanItem(FamilyPlanItemDto familyPlanItemDto);

	/**
	 * Method Name: getFamilyPlanItemLastUpdateTime Method Description:This
	 * helps to get last update time of family plan item
	 * 
	 * @param idFamilyPlanItem
	 * @return
	 */
	Date getFamilyPlanItemLastUpdateTime(Long idFamilyPlanItem);

}
