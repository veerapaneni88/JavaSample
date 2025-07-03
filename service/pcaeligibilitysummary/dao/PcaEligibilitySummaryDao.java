package us.tx.state.dfps.service.pcaeligibilitysummary.dao;

import java.util.List;

import us.tx.state.dfps.service.pca.dto.PcaEligSummaryValueBeanDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Interface
 * for PcaEligibilitySummaryDao Oct 19, 2017- 3:24:03 PM © 2017 Texas Department
 * of Family and Protective Services
 */
public interface PcaEligibilitySummaryDao {

	/**
	 * Method Name: fetchActivePcaForPerson Method Description:This method
	 * returns Active PCA's for Person.
	 * 
	 * @param idPerson
	 * @return List<PcaEligSummaryValueBeanDto>
	 * @throws DataNotFoundException
	 */
	public List<PcaEligSummaryValueBeanDto> fetchActivePcaForPerson(Long idPerson);
}
