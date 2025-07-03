package us.tx.state.dfps.service.contacts.service;

import us.tx.state.dfps.service.casepackage.dto.ContactNarrativeDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ConGuideFetchInDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ConGuideFetchOutDto;

import java.util.List;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:StageClosureEventService Nov 1, 2017- 5:27:32 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface StageClosureEventService {

	/**
	 * Method Name: getContactDetailCFRes Method Description:Determine if there
	 * is a Stage Conclusion Event in the tables
	 * 
	 * @param conGuideFetchInDto
	 * @return ConGuideFetchOutDto
	 */
	public ConGuideFetchOutDto getContactDetailCFRes(ConGuideFetchInDto conGuideFetchInDto);
	public ConGuideFetchOutDto getAllegedVictimsForStage(ConGuideFetchInDto conGuideFetchInDto);

    public List<ContactNarrativeDto> getContactDetailIntakeReports(List<Long> idStage);
	public ConGuideFetchOutDto getContactDetailStaffingDetail(ConGuideFetchInDto conGuideFetchInDto);
	public ContactNarrativeDto getIntakeReportAlternatives(Long idEvent);
}
