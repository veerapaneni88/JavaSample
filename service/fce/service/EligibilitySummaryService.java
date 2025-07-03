/**
 *service-ejb-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Nov 15, 2017- 10:24:25 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.fce.service;

import us.tx.state.dfps.service.common.request.EligibilitySummaryReq;
import us.tx.state.dfps.service.common.response.EligibilitySummaryRes;
import us.tx.state.dfps.service.fce.EligibilitySummaryDto;
import us.tx.state.dfps.service.fce.dto.FceEligibilityDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * EligibilitySummaryService Mar 9, 2018- 12:04:39 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface EligibilitySummaryService {

	public EligibilitySummaryRes read(Long idStage, Long idEvent, Long idLastUpdatePerson);

	void save(EligibilitySummaryDto eligibilitySummaryDto);

	public void delete(EligibilitySummaryDto eligibilitySummaryDto);

	public Boolean isAutoEligibility(Long idEvent);

	public Boolean getAdoProcessStatus(Long idStage);

	public FceEligibilityDto getFceEligibility(Long idFceEligibility);

	public EligibilitySummaryRes getEligibilityByStage(Long idStage);

}
