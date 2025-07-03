package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.AddrPersonLinkPhoneInDto;
import us.tx.state.dfps.service.admin.dto.AddrPersonLinkPhoneOutDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This
 * Interface fetches details from
 * ADDRESS_PERSON_LINK,PERSON_ADDRESS,PERSON_PHONE table Aug 18, 2017- 3:35:43
 * PM © 2017 Texas Department of Family and Protective Services
 */
public interface AddrPersonLinkPhoneDao {

	/**
	 * 
	 * Method Name: cinv46dQUERYdam Method Description:This method fetches
	 * details from ADDRESS_PERSON_LINK,PERSON_ADDRESS,PERSON_PHONE table
	 * 
	 * @param Cinv46diDto
	 * @return List<Cinv46doDto>
	 * @throws DataNotFoundException
	 */
	public List<AddrPersonLinkPhoneOutDto> cinv46dQUERYdam(AddrPersonLinkPhoneInDto pInputDataRec)
			throws DataNotFoundException;
}
