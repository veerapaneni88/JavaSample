/**
 * 
 */
package us.tx.state.dfps.service.person.service;

import java.util.Date;

import us.tx.state.dfps.service.common.request.TraffickingReq;
import us.tx.state.dfps.service.common.response.TraffickingRes;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Mar 22, 2018- 5:29:42 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface TraffickingService {

	/**
	 * Method Description: This Method will retrieve trafficking records Service
	 * Name : getTraffickingList
	 * 
	 * @param traffickingReq
	 * @return
	 */
	public TraffickingRes getTraffickingList(TraffickingReq traffickingReq);

	/**
	 * Method Description: This Method will save trafficking details Service
	 * Name : saveTraffickingDetails
	 * 
	 * @param traffickingReq
	 * @return TraffickingRes
	 * 
	 */
	public TraffickingRes saveTraffickingDetails(TraffickingReq traffickingReq);

	/**
	 * Method Description: This Method will update trafficking details Service
	 * Name : updateTraffickingDetails
	 *
	 * @param traffickingReq
	 * @return TraffickingRes
	 *
	 */
	public TraffickingRes updateTraffickingDetails(TraffickingReq traffickingReq);

	/**
	 * Method Description: This Method will delete trafficking details Service
	 * Name : deleteTraffickingDetails
	 *
	 * @param idTraffickingDtl
	 * @return TraffickingRes
	 *
	 */
	public TraffickingRes deleteTraffickingDetails(TraffickingReq traffickingReq);

	/**
	 * Method Description: This Method will retrieve information for Trafficking
	 * Details Service Name : TRFCKNG_DTL
	 * 
	 * @param traffickingReq
	 * @return TraffickingRes
	 */
	public TraffickingRes getTraffickingDtl(TraffickingReq traffickingReq);

	/**
	 * 
	 * Method Name: getIntakeDate Method Description: This Method is to fetch
	 * intakeDate
	 * 
	 * @param idPerson
	 * @return Date
	 */
	public Date getIntakeDate(Long idPerson);
	
	/**
	 * 
	 * Method Name: getNMPerson Method Description: This Method is 
	 * to get Person Name for the Runaway Person to navigate 
	 * to Trafficking Detail Page.
	 * @param idPerson
	 * @return String
	 */
	public String getNmPerson(long idPerson);
	
	/**
	 * 
	 * Method Name: getPerson Method Description: This Method is 
	 * to get Person Object to validate the Date of Incident before save Trafficking Dtl 
	 * to Trafficking Detail Page.
	 * @param TraffickingReq
	 * @return TraffickingRes
	 */
	public TraffickingRes getPerson(TraffickingReq traffickingReq);
	
	
	/**
	 * Method Description: This method returns true if there are any
	 * confirmed Sex Trafficking Incidents for a person
	 * 
	 * 
	 * @param idPerson
	 * @return Boolean
	 */
	public boolean getConfirmedSxTraffickingIndicator(long idPerson);

}
