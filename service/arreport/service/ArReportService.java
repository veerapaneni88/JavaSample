package us.tx.state.dfps.service.arreport.service;

import java.util.List;

import us.tx.state.dfps.service.arreport.dto.ArPrincipalsHistoryDto;
import us.tx.state.dfps.service.arreport.dto.ArReportDto;
import us.tx.state.dfps.service.common.request.CommonApplicationReq;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvContactSdmSafetyAssessDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvReportMergedDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.workload.dto.ContactDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Declares
 * service method Apr 4, 2018- 4:45:43 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface ArReportService {

	/**
	 * Method Name: getArReport Method Description: Gets information about AR
	 * report from database and returns prefill string
	 * 
	 * @param req
	 * @return PreFillDataServiceDto @
	 */
	public ArReportDto getArReport(CommonApplicationReq req, ArReportDto prefillDto);
	
	public ArReportDto getGenericCaseInfo(Long idStage);

	public List<ContactDto> getContactList(Long idStage, String mergedStages);

	public List<CpsInvContactSdmSafetyAssessDto> getContacts(List<CpsInvReportMergedDto> cpsInvReportMergedDtoList);
	
	public ArReportDto getSDMCareGiverList(ArReportDto arReportDto);

	public PreFillDataServiceDto returnPrefillData(ArReportDto arReportDto);

	public List<ArPrincipalsHistoryDto> getHistoricalPrincipals(Long idStage);

}
