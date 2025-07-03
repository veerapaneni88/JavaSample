package us.tx.state.dfps.service.childplan.service;

import java.lang.reflect.InvocationTargetException;

import us.tx.state.dfps.service.common.request.ChildPlanDtlReq;
import us.tx.state.dfps.service.common.response.ChildPlanDtlRes;
import us.tx.state.dfps.service.common.response.CommonIdRes;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This class
 * will make call to dao layer to the fetch, save, save&submit, delete and
 * business logic are implemented for ChildPlan Detail Screen. May 4, 2018-
 * 10:29:48 AM © 2017 Texas Department of Family and Protective Services
 */
public interface ChildPlanDtlService {

	/**
	 * Method Name: getChildPlanDtl Method Description: This Method is used to
	 * retrieve all the pre-fill editable and pre-fill readable values for
	 * display.
	 * 
	 * @param childPlanDtlReq
	 * @return ChildPlanDtlRes
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public ChildPlanDtlRes getChildPlanDtl(ChildPlanDtlReq childPlanDtlReq);

	/**
	 * Method Name: saveChildPlanDtl Method Description: This method is used to
	 * save the fields in child plan page.
	 * 
	 * @param childPlanDtlReq
	 * @return ChildPlanDtlRes
	 */
	public ChildPlanDtlRes saveChildPlanDtl(ChildPlanDtlReq childPlanDtlReq);

	/**
	 * Method Name: deleteChildPlanDtl Method Description: This is method is
	 * used to delete the child plan data based on inputs.
	 * 
	 * @param childPlanOfServiceDtlDto
	 * @return ChildPlanDtlRes
	 */
	public ChildPlanDtlRes deleteChildPlanDtl(ChildPlanDtlReq childPlanDtlReq);

	/**
	 * Method Name: saveAndSubmitChildPlanDtl Method Description: This method is
	 * used to save the fields in child plan page detail screen and submit the
	 * child plan for supervisor approval.
	 * 
	 * @param childPlanDtlReq
	 * @return ChildPlanDtlRes
	 */
	public ChildPlanDtlRes saveAndSubmitChildPlanDtl(ChildPlanDtlReq childPlanDtlReq);

	/**
	 * Method Name: getChildPlanDtlForm Method Description: This Method is used
	 * to retrieve all the pre-fill editable and pre-fill readable values for
	 * displaying in the form.
	 * 
	 * @param childPlanDtlReq
	 * @return PreFillData
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public PreFillDataServiceDto getChildPlanDtlForm(ChildPlanDtlReq childPlanDtlReq);

	/**
	 * Method Name: alertReadyForReview
	 * 
	 * Method Description: This Method is used to alert the primary worker when
	 * the child plan is ready for review
	 * 
	 * @param childPlanDtlReq
	 * @return
	 */
	public CommonIdRes alertReadyForReview(ChildPlanDtlReq childPlanDtlReq);

}
