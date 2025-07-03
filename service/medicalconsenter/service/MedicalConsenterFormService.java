package us.tx.state.dfps.service.medicalconsenter.service;

import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.person.dto.MedicalConsenterDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This service
 * is used to launch the Medical Consenter forms. Oct 30, 2017- 5:13:13 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
public interface MedicalConsenterFormService {

	/**
	 * Method Name:getMedicalConsentForms Method Description: This method is
	 * used to call the service for fetching Medical Consenter form
	 * 
	 * @param medicalConsentInDto
	 * @return @
	 */
	public PreFillDataServiceDto getMedicalConsentForms(MedicalConsenterDto medicalConsentInDto);

}
