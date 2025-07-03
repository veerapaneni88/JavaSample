package us.tx.state.dfps.service.workload.dao;

import java.util.List;

import us.tx.state.dfps.service.webservices.gold.dto.GoldCommunicationDto;
import us.tx.state.dfps.service.workload.dto.WebsvcFormTransSearchDto;

/**
 * 
 * service-business - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CCMN34S
 * Class Description:surface layer for WebsvcFormTransSearchDaoImpl Apr 21, 2017
 * - 1:01:07 PM
 */

public interface WebsvcFormTransDao {
	/**
	 * 
	 * Method Description:call dao to excute query for dam-CSESF6D
	 * 
	 * @param idEvent
	 * @return @
	 */
	public List<WebsvcFormTransSearchDto> websvcFormTransSearch(Long idEvent);

	/**
	 * artf246077 Gold Interface - Transaction table updates
	 * Insert a row into WEBSVC_FORM_TRANS and return the primary key
	 * @param eventId
	 */
	Long insertFormSendData(Long eventId);

	/**
	 * artf246077 Gold Interface - Transaction table updates
	 * Update WEBSVC_FORM_TRANS table
	 * @param goldCommunicationDto
	 */
	boolean updateFormSendData(GoldCommunicationDto goldCommunicationDto);
}
