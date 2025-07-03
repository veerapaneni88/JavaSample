package us.tx.state.dfps.service.contacts.dao;

import us.tx.state.dfps.xmlstructs.inputstructs.InvestigationStageDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ApsInvDtlStageDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:StagesOverallDispDao Aug 2, 2018- 6:30:38 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface StagesOverallDispDao {
	/**
	 * Method Name: getStagesOverallDispFromApsInvDtl Method Description: This
	 * method fetches the stage overall display.
	 * 
	 * @param investigationStageDto
	 * @return ApsInvDtlStageDto
	 */
	public ApsInvDtlStageDto getStagesOverallDispFromApsInvDtl(InvestigationStageDto investigationStageDto);

}
