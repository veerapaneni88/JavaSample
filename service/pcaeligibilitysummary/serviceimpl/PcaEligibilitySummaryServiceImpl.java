package us.tx.state.dfps.service.pcaeligibilitysummary.serviceimpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.pca.dto.PcaEligSummaryValueBeanDto;
import us.tx.state.dfps.service.pcaeligibilitysummary.dao.PcaEligibilitySummaryDao;
import us.tx.state.dfps.service.pcaeligibilitysummary.service.PcaEligibilitySummaryService;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Service
 * Implement Class for PcaEligibilitySummary May 31, 2018- 11:08:07 AM © 2017
 * Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class PcaEligibilitySummaryServiceImpl implements PcaEligibilitySummaryService {

	@Autowired
	private PcaEligibilitySummaryDao pcaEligibilitySummaryDao;

	private static final Logger LOG = Logger.getLogger("ServiceBusiness-PcaEligibilitySummaryServiceLog");

	/**
	 * Method Name: fetchActivePcaList Method Description:Get Person Active PCA
	 * Eligibilities for input person id
	 * 
	 * @param idPerson
	 * @return List<PcaEligSummaryValueBeanDto>
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<PcaEligSummaryValueBeanDto> fetchActivePcaList(Long idPerson) {
		LOG.info("Entering method fetchActivePcaList in PcaEligibilitySummaryService");
		return pcaEligibilitySummaryDao.fetchActivePcaForPerson(idPerson);
	}

}
