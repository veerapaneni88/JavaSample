package us.tx.state.dfps.service.subcare.service;

import us.tx.state.dfps.service.common.request.CvsFaHomeReq;
import us.tx.state.dfps.service.common.response.CvsFaHomeRes;

/**
 * service-business - IMPACT PHASE 2 MODERNIZATION Description: Service Layer to
 * call methods related to Subcare. Apr 20, 2017 - 4:26:30 PM
 */

public interface CvsFaHomeService {

	/**
	 * 
	 * Method Description: Method to retrieve Person details to populate the CVS
	 * Home window. This method is also called in the save and update
	 * functionalities. EJB - CVS FA HOME
	 * 
	 * @param cvsFaHomeReq
	 * @return cvsFaHomeRes @
	 */
	public CvsFaHomeRes getCvsFaHomeDetails(CvsFaHomeReq cvsFaHomeReq);

	/**
	 * 
	 * Method Description: Method to save person details in the CVS Home window.
	 * This method is also retrieve the saved data. EJB - CVS FA HOME
	 * 
	 * @param cvsFaHomeReq
	 * @return cvsFaHomeRes @
	 */
	public CvsFaHomeRes saveCvsFaHome(CvsFaHomeReq cvsFaHomeReq);

	/**
	 * 
	 * Method Description: Method to update person details in the CVS Home
	 * window. This method is also retrieve the saved data. EJB - CVS FA HOME
	 * 
	 * @param cvsFaHomeReq
	 * @return cvsFaHomeRes @
	 */
	public CvsFaHomeRes updateCvsFaHome(CvsFaHomeReq cvsFaHomeReq);

}
