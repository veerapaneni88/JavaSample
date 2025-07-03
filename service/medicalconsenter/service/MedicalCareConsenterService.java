package us.tx.state.dfps.service.medicalconsenter.service;

import us.tx.state.dfps.service.common.request.MedicalCareConsenterReq;
import us.tx.state.dfps.service.common.request.MedicalConsenterFormLogReq;
import us.tx.state.dfps.service.common.response.MedicalConsenterRes;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:MedicalCareConsenterService Interface class. Feb 9, 2018- 1:44:16
 * PM © 2017 Texas Department of Family and Protective Services
 */
public interface MedicalCareConsenterService {

	/**
	 * Method Description: The service is used to launch the Medical Consenter
	 * forms.
	 * 
	 * @param medCareConsenterReq
	 * @return PreFillDataServiceDto @
	 */
	public PreFillDataServiceDto callMedicalCareConsenterService(MedicalCareConsenterReq medCareConsenterReq);

	/**
	 * 
	 * Method Name: getMedCnsntrFormLog Method Description: This method is to
	 * get MedCnsntrFormLog
	 *
	 * @param medicalConsenterFormLogReq
	 * @return CommonStringRes
	 */
	public MedicalConsenterRes getMedCnsntrFormLog(MedicalConsenterFormLogReq medicalConsenterFormLogReq);

}
