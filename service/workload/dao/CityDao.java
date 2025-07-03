package us.tx.state.dfps.service.workload.dao;

import java.util.List;

import us.tx.state.dfps.service.workload.dto.CityDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Class
 * Description:interface class for RetrieveCountyDaoImpl Apr 12, 2017 - 7:39:38
 * PM
 */
public interface CityDao {

	/**
	 * 
	 * Method Description: getCountyList
	 * 
	 * @param szAddrCity
	 * @return List<CityDto> @
	 */
	public List<CityDto> getCountyList(String szAddrCity);

}
