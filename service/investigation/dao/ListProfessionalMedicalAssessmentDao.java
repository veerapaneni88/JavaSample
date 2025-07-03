package us.tx.state.dfps.service.investigation.dao;

import java.util.List;

import us.tx.state.dfps.service.casepackage.dto.MdclMentalAssmntDtlDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.TodoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jan 4, 2017- 1:22:20 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface ListProfessionalMedicalAssessmentDao {

	public MdclMentalAssmntDtlDto getProfessionalAssesmentByEventId(Long idEvent);

	public List<PersonDto> getPersonDetails(List<String> personType, Long idStage);

	public TodoDto getTodoDtl(Long idEvent);
	
	boolean checkNarrExists(Long idEvent);

}
