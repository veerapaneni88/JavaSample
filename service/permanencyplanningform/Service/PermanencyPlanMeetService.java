package us.tx.state.dfps.service.permanencyplanningform.Service;

import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.common.request.PpmReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PermanencyPlanMeetService for Notice for Permanency Plan Team
 * Meeting Service. Feb 10, 2018- 9:39:33 PM © 2017 Texas Department of Family
 * and Protective Services
 */
@Repository
public interface PermanencyPlanMeetService {

	/**
	 * 
	 * Method Name: getPermanencyPlanTeamMeeting Method Description: Notice for
	 * Permanency Plan Team Meeting Service Service Name : CSUB52S
	 * 
	 * @param PpmReq
	 * @return PreFillDataServiceDto @
	 */
	public PreFillDataServiceDto getPermanencyPlanTeamMeeting(PpmReq ppmReq);

}
