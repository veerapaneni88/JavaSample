package us.tx.state.dfps.service.apsintakereport.service;

import us.tx.state.dfps.service.common.request.ApsIntakeReportReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Declares
 * service method for APS Intake Report Mar 16, 2018- 3:43:13 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface ApsIntakeReportService {

	/**
	 * Method Name: getIntakeReport Method Description: Gets data for APS Intake
	 * Report and returns prefill data (form CFIN0200)
	 * 
	 * @param apsIntakeReportReq
	 * @return PreFillDataServiceDto @
	 */
	public PreFillDataServiceDto getIntakeReport(ApsIntakeReportReq apsIntakeReportReq);

	/**
	 * Method Name: getIntakeReportFac Method Description: Gets data for APS
	 * Intake Report Facility and returns prefill data (form CFIN0400)
	 * 
	 * @param apsIntakeReportReq
	 * @return PreFillDataServiceDto @
	 */
	public PreFillDataServiceDto getIntakeReportFac(ApsIntakeReportReq apsIntakeReportReq);

	/**
	 * Method Name: getIntakeReportNoRep Method Description: Gets data for APS
	 * Intake Report Facility Minus Reporter and returns prefill data (form
	 * CFIN0800)
	 * 
	 * @param apsIntakeReportReq
	 * @return PreFillDataServiceDto @
	 */
	public PreFillDataServiceDto getIntakeReportNoRep(ApsIntakeReportReq apsIntakeReportReq);

}
