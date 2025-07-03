package us.tx.state.dfps.service.contacts.dao;

import java.util.List;
import java.util.Map;

import us.tx.state.dfps.service.admin.dto.StageEventDto;
import us.tx.state.dfps.service.casepackage.dto.*;
import us.tx.state.dfps.xmlstructs.inputstructs.EventSearchStageInDto;
import us.tx.state.dfps.xmlstructs.outputstructs.EventSearchStageOutDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:StageSearchEventDao Nov 1, 2017- 5:11:55 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface StageSearchEventDao {

	/**
	 * Method Name: getDateCaseOpenedForStage Method Description:This dam
	 * retrieves the DT INT START given the ID CASE from the STAGE Table.
	 * 
	 * @param eventSearchStageInDto
	 * @return EventSearchStageOutDto
	 */
	public EventSearchStageOutDto getDateCaseOpenedForStage(EventSearchStageInDto eventSearchStageInDto);

	/**
	 * Method Name: getAssesmentListByStageEvent Method Description:This dam
	 * retrieves Assessment list from stage, event and task table
	 * 
	 * @param stageEventDto
	 * @return assesmentListByStageEvent
	 */
	List<StageEventDto> getAssessmentListByStageEvent(StageEventDto stageEventDto);

	public List<ContactNarrativeDto> getContactDetailIntakeReports(List<Long> idStage);

	public ContactNarrativeDto getIntakeReportAlternatives(Long idEvent);
}
