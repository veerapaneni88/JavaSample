package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.EventRiskAssessmentInDto;
import us.tx.state.dfps.service.admin.dto.EventRiskAssessmentOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<DAO
 * Interface for Event Details> Aug 4, 2017- 11:10:29 AM © 2017 Texas Department
 * of Family and Protective Services
 */
public interface EventRiskAssessmentDao {

	/**
	 * 
	 * Method Name: getEvent Method Description: This method will get data from
	 * Event table.
	 * 
	 * @param pInputDataRec
	 * @return List<EventRiskAssessmentOutDto> @
	 */
	public List<EventRiskAssessmentOutDto> getEvent(EventRiskAssessmentInDto pInputDataRec);
}
