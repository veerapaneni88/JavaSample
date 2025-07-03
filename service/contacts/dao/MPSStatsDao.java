package us.tx.state.dfps.service.contacts.dao;

import us.tx.state.dfps.service.baseriskassmt.dto.MPSStatsValueDto;

public interface MPSStatsDao {
	/**
	 * 
	 * Method Name: logStatsToDB Method Description:insert into MpsUsageStats
	 * 
	 * @param statsValueBean
	 * @return long @
	 */
	public long logStatsToDB(MPSStatsValueDto statsValueBean);

}
