package us.tx.state.dfps.service.contactnarrlog.Service;

import us.tx.state.dfps.service.common.request.CpsIntakeReportReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ContactNarrLogAppendService Feb 14, 2018- 3:06:14 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface ContactNarrLogAppendService {

	public PreFillDataServiceDto getContactNarrLogDetails(CpsIntakeReportReq cpsIntakeReportReq);

	/**
	 * Method Description: This method is used to retrieve the information for
	 * Contact Narrative form
	 * 
	 * 
	 * @param contactNarrativeReq
	 * @return PreFillDataServiceDto
	 * 
	 */
	public PreFillDataServiceDto getContactNarr(Long idEvent);

}
