package us.tx.state.dfps.service.servicedlvryclosure.service;

import us.tx.state.dfps.service.common.request.ServiceDlvryClosureReq;
import us.tx.state.dfps.service.common.response.ServiceDlvryClosureSaveSubmitRes;
import us.tx.state.service.servicedlvryclosure.dto.DlvryClosureEventDto;
import us.tx.state.service.servicedlvryclosure.dto.DlvryClosureSaveDto;
import us.tx.state.service.servicedlvryclosure.dto.DlvryClosureValidationDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This
 * interface save the CSVC18D,CSVC22D,CSVC36D,CINV43D Details Aug 23, 2017-
 * 5:03:32 PM © 2017 Texas Department of Family and Protective Services
 */
public interface DlvryClosureSaveService {

	/**
	 * Method Name: saveDlvryClosureService Method Description:This interface
	 * Saves the CSVC18D,CSVC22D,CSVC36D,CINV43D Dam Details
	 * 
	 * @param DlvryClosureSaveDto
	 * @return DlvryClosureEventDto
	 */
	public DlvryClosureEventDto saveDlvryClosureService(DlvryClosureSaveDto pInputMsg);

	/**
	 * Method Name: saveStageDetails Method Description:This interface Saves the
	 * CSVC18D Dam Details
	 * 
	 * @param DlvryClosureSaveDto
	 */
	public void saveStageDetails(DlvryClosureSaveDto pInputMsg);

	/**
	 * Method Name: saveDlvryClosureDetails Method Description:This interface
	 * Saves the CSVC22D Dam Details
	 * 
	 * @param DlvryClosureSaveDto
	 */
	public void saveDlvryClosureDetails(DlvryClosureSaveDto pInputMsg);

	/**
	 * Method Name: updateTodoDetails Method Description:This interface fetch
	 * the CSVC36D Dam Details
	 * 
	 * @param DlvryClosureSaveDto
	 */
	public void reteriveDlvryClosureEvent(DlvryClosureSaveDto pInputMsg, DlvryClosureEventDto pOutputMsg);

	/**
	 * Method Name: updateTodoDetails Method Description:This interface Saves
	 * the CINV43D Dam Details
	 * 
	 * @param DlvryClosureSaveDto
	 */
	public void updateTodoDetails(DlvryClosureSaveDto pInputMsg);

	/**
	 * Method Name: saveAndSubmitDlvryClosure Method Description: Save and
	 * Submit method
	 * 
	 * @param dlvryClosureSaveDto
	 * @param serviceDlvryClosureReq
	 * @param dlvryClosureValidationDto
	 * @return
	 */
	ServiceDlvryClosureSaveSubmitRes saveAndSubmitDlvryClosure(DlvryClosureSaveDto dlvryClosureSaveDto,
			ServiceDlvryClosureReq serviceDlvryClosureReq, DlvryClosureValidationDto dlvryClosureValidationDto);
}
