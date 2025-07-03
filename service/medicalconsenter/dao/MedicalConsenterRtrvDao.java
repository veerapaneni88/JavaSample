package us.tx.state.dfps.service.medicalconsenter.dao;

import java.util.List;

import us.tx.state.dfps.service.person.dto.MedicalConsenterDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * MedicalConsenterRtrvDao Oct 25, 2017- 4:33:08 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface MedicalConsenterRtrvDao {

	/**
	 * Method Name:getMedicalConsenterDtls Method Description: This method
	 * retrieves the Names of the Medical Consenter associated with a Primary
	 * Child.
	 * 
	 * @param medicalInDto
	 * @return List<MedicalConsenterDto> @
	 */
	public List<MedicalConsenterDto> getMedicalConsenterDtls(MedicalConsenterDto medicalInDto);

	/**
	 * Method getMedicalConsenterRecords Method Description: This method
	 * retrieves the Name and effective Start date of the Medical Consenter
	 * assigned to a Primary Child. associated with a Primary Child.
	 * 
	 * @param pInputDataRec
	 * @return List<MedicalConsenterDto> @
	 */
	public List<MedicalConsenterDto> getMedicalConsenterRecords(MedicalConsenterDto medicalDtoDt);
}
