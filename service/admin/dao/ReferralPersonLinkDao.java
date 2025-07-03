package us.tx.state.dfps.service.admin.dao;

import java.util.List;

import us.tx.state.dfps.service.admin.dto.ReferralPersonLinkInDto;
import us.tx.state.dfps.service.admin.dto.ReferralPersonLinkOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Interface
 * for Cses47 Aug 5, 2017- 4:07:19 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface ReferralPersonLinkDao {

	/**
	 * 
	 * Method Name: getPersonReferralLink Method Description: This method will
	 * get data from REFERRAL_PERSON_LINK table.
	 * 
	 * @param referralPersonLinkInDto
	 * @return List<ReferralPersonLinkOutDto>
	 */
	public List<ReferralPersonLinkOutDto> getPersonReferralLink(ReferralPersonLinkInDto referralPersonLinkInDto);
}
