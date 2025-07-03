package us.tx.state.dfps.service.contacts.daoimpl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.MpsUsageStats;
import us.tx.state.dfps.service.baseriskassmt.dto.MPSStatsValueDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.contacts.dao.MPSStatsDao;
import us.tx.state.dfps.service.exception.DataLayerException;

@Repository
public class MPSStatsDaoImpl implements MPSStatsDao {
	@Autowired
	MessageSource messageSource;
	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * 
	 * Method Name: logStatsToDB Method Description:insert into MpsUsageStats
	 * 
	 * @param statsValueBean
	 * @return longS @
	 */
	@Override
	public long logStatsToDB(MPSStatsValueDto statsValueBean) {
		MpsUsageStats mpsUsageStats = new MpsUsageStats();

		mpsUsageStats.setIdStage(statsValueBean.getIdStage());
		mpsUsageStats.setIdEvent(statsValueBean.getIdEvent());
		mpsUsageStats.setIdCase(statsValueBean.getIdCase());
		mpsUsageStats.setDmlType(statsValueBean.getCdDmlType());
		mpsUsageStats.setIdReference(statsValueBean.getIdReference());
		mpsUsageStats.setCdReference(statsValueBean.getCdReference());
		long pk = (long) sessionFactory.getCurrentSession().save(mpsUsageStats);

		if (pk == ServiceConstants.Zero) {
			throw new DataLayerException(ServiceConstants.SQL_NOT_FOUND);
		}
		return pk;
	}

}
