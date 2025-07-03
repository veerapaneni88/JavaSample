/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: Interface for Data Access Implementation Class for select FSNA and select FSNA Evaluation Class
 *Jul 6, 2018- 10:25:30 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.familyplanfsna.dao;

import java.util.List;

import us.tx.state.dfps.service.familyplanfsna.dto.SelectFSNADto;
import us.tx.state.dfps.service.familyplanfsna.dto.SelectFSNAValidationDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Interface
 * for Data Access Implementation Class for select FSNA and select FSNA
 * Evaluation Class> Jul 6, 2018- 10:25:30 AM © 2017 Texas Department of Family
 * and Protective Services
 */
public interface FPSelectFSNADao {

	public List<SelectFSNADto> getFPFsnaList(Long idStage, String cdStage);

	public List<SelectFSNAValidationDto> getFSNAValidation(Long idStage);

}
