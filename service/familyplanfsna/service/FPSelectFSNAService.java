/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: Service Interface for select FSNA and select FSNA Evaluation
 *Jul 6, 2018- 10:23:49 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.familyplanfsna.service;

import java.util.List;

import us.tx.state.dfps.service.familyplanfsna.dto.SelectFSNADto;
import us.tx.state.dfps.service.familyplanfsna.dto.SelectFSNAValidationDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Service
 * Interface for select FSNA and select FSNA Evaluation> Jul 6, 2018- 10:23:49
 * AM © 2017 Texas Department of Family and Protective Services
 */
public interface FPSelectFSNAService {

	public List<SelectFSNADto> getFsnaList(Long idStage, String cdStage);

	public List<SelectFSNAValidationDto> getFsnaValidationList(Long idStage);

}
