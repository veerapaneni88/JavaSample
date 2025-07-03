/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description: This interface interacts with controller and service impl
 *Feb 08, 2018- 10:59:00 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.placement.service;

import us.tx.state.dfps.service.common.request.OutputLaunchRtrvReq;
import us.tx.state.dfps.service.common.request.OutputLaunchSaveReq;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.common.response.OutputLaunchRtrvoRes;
import us.tx.state.dfps.service.placement.dto.OutputLaunchRtrvoDto;

public interface OutputLaunchRtrvService {
	/**
	 * 
	 * Method Description: This method is use to retrieve common Application
	 * detail by giving event_id or Stage_id and Task code Tuxedo Service Name:
	 * CSUB59S
	 * 
	 * @param outputLaunchRtrvReq
	 * @return OutputLaunchRtrvoRes @
	 */
	public OutputLaunchRtrvoRes callOutputLaunchRtrvService(OutputLaunchRtrvReq outputLaunchRtrvReq);

	/**
	 * 
	 * Method Description: This service will add or update the EVENT table using
	 * the Post Event function Tuxedo Service Name: CSUB60S
	 * 
	 * @param outputLaunchRtrvReq
	 * @return OutputLaunchRtrvoRes @
	 */
	public OutputLaunchRtrvoRes saveOutputLaunch(OutputLaunchSaveReq outPutLaunchSaveReq);

	/**
	 * Method Name:deleteDocument Method Description: Service Impl method to
	 * delete the 'PROC' Document and and the associated events when the Delete
	 * button clicked in Common Application Detail page.
	 * 
	 * @param outPutLaunchSaveReq
	 * @return CommonStringRes
	 */
	public CommonStringRes deleteDocument(OutputLaunchSaveReq outPutLaunchSaveReq);
	
	/**
	 * Method copyNarrativeDocForNewUsing Method Description: This Method is
	 * used to copy the existing narrative document to new event.
	 * 
	 * @param outputLaunchRtrvoDto
	 */
	public void copyNarrativeDocForNewUsing(OutputLaunchRtrvoDto outputLaunchRtrvoDto); 

}
