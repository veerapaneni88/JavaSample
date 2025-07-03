package us.tx.state.dfps.service.pcaeligibilitysummary.service;

import java.util.List;

import us.tx.state.dfps.service.pca.dto.PcaEligSummaryValueBeanDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Service
 * Interface for PcaEligibilitySummary May 31, 2018- 11:08:07 AM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface PcaEligibilitySummaryService {

	/**
	 * Method Name: fetchActivePcaList Method Description:Get Person Active PCA
	 * Eligibilities for input person id
	 * 
	 * @param idPerson
	 * @return List<PcaEligSummaryValueBeanDto>
	 * 
	 */
	public List<PcaEligSummaryValueBeanDto> fetchActivePcaList(Long idPerson);
}
