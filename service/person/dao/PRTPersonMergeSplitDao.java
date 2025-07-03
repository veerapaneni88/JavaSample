package us.tx.state.dfps.service.person.dao;

import java.util.List;

import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PRTPersonMergeSplitDao Sep 22, 2017- 10:51:39 AM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface PRTPersonMergeSplitDao {

	/**
	 * Method Name: isActivePRT Method Description:Gets all the PRT(Active) for
	 * the Person.
	 * 
	 * @param idFwdPerson
	 * @return boolean
	 */
	public boolean isActivePRT(Long idPerson);

	/**
	 * Method Name: updatePersonOnPrtActPlan Method Description:Update person Id
	 * on PRT Person Link This functions is being called during person merge to
	 * update forward person Id on a closed person for PRT.
	 * 
	 * @param idFwdPerson
	 * @param idPrtActPln
	 * @param idClosedPerson
	 */
	public void updatePersonOnPrtActPlan(int idFwdPerson, int idPrtActPln, int idClosedPerson);

	/**
	 * Method Name: isOpenActionPlan Method Description:Gets all the PRT Action
	 * Plan(Active) for the Person.
	 * 
	 * @param idPerson
	 * @return boolean
	 * @throws DataNotFoundException
	 */
	public boolean isOpenActionPlan(Long idPerson);

	/**
	 * 
	 * Method Name: getPRTActPlnForPerson Method Description:Gets all the PRT
	 * Action Plan (closed) in all the OPEN stages for the Person.
	 * 
	 * @param idPerson
	 * @return
	 */
	public List<Long> getPRTActPlnForPerson(Long idPerson);

	/**
	 * 
	 * Method Name: getPRTConnectionForPerson Method Description: Gets all the
	 * PRT Connection (Active and Inactive).
	 * 
	 * @param idPerson
	 * @return
	 */
	public List<Long> getPRTConnectionForPerson(Long idPerson);

}
