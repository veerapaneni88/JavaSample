/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *Aug 14, 2017- 3:08:14 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.investigation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import us.tx.state.dfps.service.common.request.MdclMentalAssmtReq;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.investigation.service.MedicalMentalAssessmentService;
import us.tx.state.dfps.web.mdclMentalAssmnt.bean.MdclSaveDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Aug 14, 2017- 3:08:14 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@RestController
@RequestMapping("/medcialmentalassessment")
public class MedicalMentalAssessmentController {

	/**
	 * 
	 */
	public MedicalMentalAssessmentController() {
	}

	@Autowired
	MedicalMentalAssessmentService medicalMentalAssessmentService;

	@RequestMapping(value = "/medcialmentalassessmentAUD", headers = {
			"Accept=application/json" }, method = RequestMethod.POST)
	public  CommonHelperRes medcialMentalAssessmentAUD(
			@RequestBody MdclMentalAssmtReq mdclMentalAssmtReq) {
		MdclSaveDto mdclSaveDto = medicalMentalAssessmentService.medcialMentalAssessmentAUD(mdclMentalAssmtReq);
		Boolean isOpenedStage = medicalMentalAssessmentService.isOpenStage(mdclMentalAssmtReq.getIdStage());
		CommonHelperRes response = new CommonHelperRes();
		response.setIsOpenedStage(isOpenedStage);
		response.setErrorDto(mdclSaveDto.getErrorDto());
		response.setRowId(mdclSaveDto.getRowId());
		return response;
	}
}
