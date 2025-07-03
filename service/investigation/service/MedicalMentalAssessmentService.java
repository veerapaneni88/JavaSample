/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Aug 14, 2017- 3:09:25 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.investigation.service;

import us.tx.state.dfps.service.common.request.MdclMentalAssmtReq;
import us.tx.state.dfps.web.mdclMentalAssmnt.bean.MdclSaveDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Aug 14, 2017- 3:09:25 PM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface MedicalMentalAssessmentService {

	public Boolean isOpenStage(Long idStage);

	public boolean isCVSStages(MdclMentalAssmtReq mdclMentalAssmtReq);

	/**
	 *Method Name:	medcialMentalAssessmentAUD
	 *Method Description:
	 *@param mdclMentalAssmtReq
	 *@return
	 */
	public MdclSaveDto medcialMentalAssessmentAUD(MdclMentalAssmtReq mdclMentalAssmtReq);


}
