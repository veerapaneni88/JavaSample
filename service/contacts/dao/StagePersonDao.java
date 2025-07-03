package us.tx.state.dfps.service.contacts.dao;

import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.xmlstructs.inputstructs.PersonRoleDto;
import us.tx.state.dfps.xmlstructs.outputstructs.PersonRoleResultDto;

public interface StagePersonDao {
	/**
	 * 
	 * Method Name: getIdPersonForDifferentRole Method
	 * Description:PersonRoleResultDto
	 * 
	 * @param personRoleDto
	 * @return PersonRoleResultDto
	 * @throws DataNotFoundException
	 */
	public PersonRoleResultDto getIdPersonForDifferentRole(PersonRoleDto personRoleDto);

	/**
	 * Method Name: getPrimaryClientIdForStage Method Description:This method
	 * returns the primary child in the particular stage.
	 * 
	 * @param idStage
	 *            -current stage id
	 * @return Long - person id of the child
	 */
	public Long getPrimaryClientIdForStage(Long idStage);

	/**
	 * Method Name: isActiveReferral Method Description: This method Returns
	 * true/false if there is an active child referral for stage id
	 * 
	 * @param idStage
	 * @return
	 */
	public boolean isActiveReferral(Long idStage);

	/**
	 * Method Name: getPrtActiveActionPlan Method Description: This method
	 * returns a boolean value based on whether or not a sub stage is currently
	 * in open status.
	 * 
	 * @param idPerson
	 * @return
	 */
	public boolean getPrtActiveActionPlan(Long idPerson);

	/**
	 * Method Name: getPrtActionPlanInProcStatus Method Description: This method
	 * returns a boolean value based on whether or not a sub stage is currently
	 * in Proc status.
	 * 
	 * @param idPerson
	 * @return
	 */
	public boolean getPrtActionPlanInProcStatus(Long idPerson);

}
