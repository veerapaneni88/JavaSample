package us.tx.state.dfps.service.common.utils;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.MpsUsageStats;
import us.tx.state.dfps.service.baseriskassmt.dto.MPSStatsValueDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Utility
 * class for LegalActionSaveServiceImpl Oct 20, 2017- 3:05:20 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class MPSStatsUtils {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * 
	 * Method Name: logStatsToDB Method Description:Insert a record in the
	 * MPS_USAGE_STATS table for statistical purposeInserts the Legal Action.
	 * 
	 * @param mpsStatsValueDto
	 * @return
	 * @throws DataNotFoundException
	 */
	public Long logStatsToDB(MPSStatsValueDto mpsStatsValueDto) throws DataNotFoundException {
		// Checkf if MPS THEN only put
		MpsUsageStats mpsUsageStats = new MpsUsageStats();

		mpsUsageStats.setIdStage(mpsStatsValueDto.getIdStage());
		mpsUsageStats.setIdEvent(mpsStatsValueDto.getIdEvent());
		mpsUsageStats.setIdCase(mpsStatsValueDto.getIdCase());
		mpsUsageStats.setDmlType(mpsStatsValueDto.getCdDmlType());
		mpsUsageStats.setIdReference(mpsStatsValueDto.getIdReference());
		mpsUsageStats.setCdReference(mpsStatsValueDto.getCdReference());
		Long idMpsUsageStats = (Long) sessionFactory.getCurrentSession().save(mpsUsageStats);
		return idMpsUsageStats;
	}
}
