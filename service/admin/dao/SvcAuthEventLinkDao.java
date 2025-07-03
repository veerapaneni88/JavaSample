package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.SvcAuthEventLinkInDto;
import us.tx.state.dfps.service.admin.dto.SvcAuthEventLinkOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<DAO
 * Interface for fetching auth event link> Aug 4, 2017- 12:47:51 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface SvcAuthEventLinkDao {

	/**
	 * 
	 * Method Name: getAuthEventLink Method Description: This method will get
	 * data from SVC_AUTH_EVENT_LINK table.
	 * 
	 * @param pInputDataRec
	 * @return List<SvcAuthEventLinkOutDto> @
	 */
	public List<SvcAuthEventLinkOutDto> getAuthEventLink(SvcAuthEventLinkInDto pInputDataRec);

	int checkSvcAuthExists(Long idSvcAuth);
	
	//added for defect 2337 artf55938
	public List<SvcAuthEventLinkOutDto> getAuthEventLinkByCase(SvcAuthEventLinkInDto pInputDataRec);
}
