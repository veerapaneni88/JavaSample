package us.tx.state.dfps.service.casepackage.dao;

import java.util.List;

import us.tx.state.dfps.service.casepackage.dto.SSCCTimelineDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: CasePackage
 * Controller Dao Oct 4, 2017- 4:25:52 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface SSCCTimelineDao {

	/**
	 * Method Name: getSSCCTimelineList Method Description: This method is used
	 * to getSSCCTimelineList
	 * 
	 * @param ssccTimelineDto
	 * @return List<SSCCTimelineDto>
	 */
	public List<SSCCTimelineDto> getSSCCTimelineList(SSCCTimelineDto ssccTimelineDto);

	/**
	 * Method Name: insertSSCCTimeline Method Description: Inserts record into
	 * the SSCC Timeline table
	 * 
	 * @param ssccTimelineDto
	 * @return void
	 */
	public Long insertSSCCTimeline(SSCCTimelineDto ssccTimelineDto);
}
