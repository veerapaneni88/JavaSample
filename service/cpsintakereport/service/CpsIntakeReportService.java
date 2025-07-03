/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Tuxedo Service Name : CINT39S
 *Class Description:Main Service Function for Intake CPS report.
 *Jan 11, 2018- 3:29:17 PM
 *
 */
package us.tx.state.dfps.service.cpsintakereport.service;

import java.util.List;

import us.tx.state.dfps.common.dto.IncomingStageDetailsDto;
import us.tx.state.dfps.service.common.request.CpsIntakeReportReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.web.fcl.dto.SexualVictimHistoryDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * CpsIntakeReportService will have all operation which are mapped to
 * CpsIntakeReport module. Service Name: CINT39S - CPS INTAKE REPORT Feb 9,
 * 2018- 2:01:02 PM © 2017 Texas Department of Family and Protective Services
 */
public interface CpsIntakeReportService {

	public static final Long FPS_STAFF_REPORTER = 0L; // artf164166
	
	public PreFillDataServiceDto getCpsIntakeReport(CpsIntakeReportReq cpsIntakeReportReq);
	/**
	 * 
	 *Method Name:	getFpsStaffReporter
	 *Method Description: If has FPS Staff reporter, add from incoming details
	 *@param reporters
	 *@param incomingStageDetailsDto
	 *@return reporters
	 */
	public List<PersonDto> getFpsStaffReporter(List<PersonDto> reporters, IncomingStageDetailsDto incomingStageDetailsDto);
}
