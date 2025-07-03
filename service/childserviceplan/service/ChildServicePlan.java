package us.tx.state.dfps.service.childserviceplan.service;

import us.tx.state.dfps.service.common.request.ChildServicePlanReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Mar 21,
 * 2018- 11:06:34 AM
 *
 */
public interface ChildServicePlan {

	/**
	 * 
	 * Method Name: getChildServicePlan Service Name: CSUB21S Method
	 * Description:The Child's Service Plan establishes detailed case plans for
	 * providing services to children in substitute care and their families. The
	 * case plan identifies needs, formulates structured time limited tasks, and
	 * identifies service providers. This process ensures progres toward the
	 * child's safe return home or alternate permanent placement if the child
	 * cannot return home safely. The Child's service Plan forms have been
	 * combined into one form that will print continuosly. This form consists of
	 * subtemplates that contain the different sections of data.
	 * 
	 * @param childServicePlanReq
	 * @return
	 * 
	 */
	public PreFillDataServiceDto getChildServicePlan(ChildServicePlanReq childServicePlanReq);

}
