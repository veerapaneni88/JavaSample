package us.tx.state.dfps.service.servicedlvryclosure.service;

import java.util.List;

import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.ServiceDlvryClosureReq;
import us.tx.state.dfps.service.common.response.CommonBooleanRes;
import us.tx.state.service.servicedlvryclosure.dto.ClosureNotificationLettersDto;
import us.tx.state.service.servicedlvryclosure.dto.RGStageDto;
import us.tx.state.service.servicedlvryclosure.dto.ServiceDlvryClosureDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This
 * interface Retrieves the Ccmn45dDao,Cint40dDao,Csvc21dDao,Csesc2dDao Dam
 * Details Aug 23, 2017- 5:03:32 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface DlvryClosureService {

	/**
	 * Method Name: dlvryClosureService Method Description:This interface
	 * Retrieves the Ccmn45dDao,Cint40dDao,Csvc21dDao,Csesc2dDao Dam Details
	 * 
	 * @param serviceDlvryClosureReq
	 * @return ServiceDlvryClosureDto
	 */
	ServiceDlvryClosureDto dlvryClosureService(ServiceDlvryClosureReq serviceDlvryClosureReq);

	/**
	 * Method Name: saveDlvryClosureService Method Description:This method Saves
	 * the CSVC18D,CSVC22D,CSVC36D,CINV43D Dam Details
	 * 
	 * @param DlvryClosureSaveDto
	 * @return DlvryClosureEventDto
	 */
	RGStageDto getStageDetails(long idStage);

	/**
	 * Method Name: getClosureNotificationLetter Method Description: This method
	 * is to retrieve all closure Notification records from Data base
	 * 
	 * @param idStage
	 * @return
	 */
	public List<ClosureNotificationLettersDto> getClosureNotificationLetter(Long idStage);

	/**
	 * 
	 * Method Name: getPrintTaskExists Method Description:Printtask method is
	 * used to verify is a print task exists for the logged in user based on the
	 * idcase , idsateg and idevent.
	 * 
	 * @param commonHelperReq
	 * @return
	 */
	public CommonBooleanRes getPrintTaskExists(CommonHelperReq commonHelperReq);
	
	/**
	 * Method Name: getPCHasOpenSUBStage Method Description: This method is to check
	 * if there is any principal child with an open SUB Stage on the case.
	 * 
	 * @param idCase
	 * @return
	 */
	public boolean getPCHasOpenSUBStage(Long idCase);

}
