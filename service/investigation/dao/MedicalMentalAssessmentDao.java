
package us.tx.state.dfps.service.investigation.dao;

import us.tx.state.dfps.common.domain.ProfessionalAssmt;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.service.casepackage.dto.MdclMentalAssmntDtlDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Aug 15, 2017- 6:22:20 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface MedicalMentalAssessmentDao {

	public Long medicalAssessmentAUD(MdclMentalAssmntDtlDto mdclMentalAssmntDtlDto, String action);

	public Boolean verifyOpenStage(Long idStage);

	ProfessionalAssmt getProfessionalAssmt(Long idEvent);
	
	public Stage getStageDtls(Long idStage);
}
