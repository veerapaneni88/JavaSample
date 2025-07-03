package us.tx.state.dfps.service.fostercarereview.dao;

import java.util.List;

import us.tx.state.dfps.service.fce.dto.FceReasonNotEligibleDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Feb 16, 2018- 4:08:27 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface FceReasonNotEligibleDao {

	List<FceReasonNotEligibleDto> findReasonsNotEligible(Long idFceEligibility);

}
