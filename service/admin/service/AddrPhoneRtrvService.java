package us.tx.state.dfps.service.admin.service;

import us.tx.state.dfps.service.admin.dto.AddrPhoneDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This
 * interface helps to fetch the person's address and phone details for the given
 * person ID. Aug 9, 2017- 10:03:42 AM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface AddrPhoneRtrvService {

	public AddrPhoneDto callAddrPhoneRtrvService(Long idPerson);
}
