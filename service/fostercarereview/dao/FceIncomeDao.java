package us.tx.state.dfps.service.fostercarereview.dao;

import java.util.List;

import us.tx.state.dfps.common.domain.FceIncome;
import us.tx.state.dfps.service.fce.dto.FceIncomeDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Feb 16, 2018- 3:59:47 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface FceIncomeDao {

	public Long saveFceIncome(FceIncomeDto fceIncomeDto);

	public void deleteFceIncome(Long idFceIncome);

	void deleteFceIncome(FceIncome fceIncome);

	List<FceIncomeDto> getFceIncomeDtosByIdElig(Long idFceEligibility);

	List<FceIncome> getFceIncomesByIdElig(Long idFceEligibility);

}
