package us.tx.state.dfps.service.person.dao;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.mobile.IncomingPersonMpsDto;
import us.tx.state.dfps.service.common.request.AllegationVictimReq;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.person.dto.AfcarsDto;
import us.tx.state.dfps.service.person.dto.PersonBean;
import us.tx.state.dfps.service.person.dto.PersonFullNameDto;
import us.tx.state.dfps.service.personmergesplit.dto.PersonMergeSplitValueDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Apr 30, 2018- 5:43:40 PM © 2017 Texas Department of
 * Family and Protective Services
 * ****************  Change History *********************
 * 05/24/2021 nairl Artifact artf185349 : Security -Records check tab is displaying for merged DFPS employee with Person in Closed case Legacy IMPACT and IMPACT2.0
 */
public interface PersonDetailDao {

	/**
	 * Method Name: isPersonEmpOrFormerEmp Method Description:Checks if person
	 * is an employee or former employee
	 *
	 * @param idPerson
	 * @return List<PersonBean>
	 */
	public List<PersonBean> isPersonEmpOrFormerEmp(Long idPerson);

	/**
	 * Method Name: hasSSN Method Description: Check if the Person has a Non End
	 * Dated SSN
	 *
	 * @param idPerson
	 * @return boolean
	 */
	public boolean hasSSN(Long idPerson);

	/**
	 *
	 * Method Name: getPersonIdAndFullName Method Description: get PersonId and
	 * full name of person
	 *
	 * @param szNbrPersonIdNbr
	 * @param ulIdPerson
	 * @return List<PersonFullNameDto>
	 */
	public List<PersonFullNameDto> getPersonIdAndFullName(String szNbrPersonIdNbr, Long ulIdPerson);

	/**
	 * Method Name: isSSNVerifiedByInterface Method Description: Check if the
	 * SSN has been Verified by DHS Interface
	 *
	 * @param szNbrPersonIdNbr
	 * @return Boolean
	 */
	public Boolean isSSNVerifiedByInterface(String szNbrPersonIdNbr);

	/**
	 * Method Name: getPersonDetails Method Description: Retrieves the person
	 * details from the PERSON table based on the Person ID
	 *
	 * @param idForwardPerson
	 * @return Person
	 */
	public Person getPersonDetails(Long idForwardPerson);

	/**
	 * Method Name: getForwardPersonInMerge Method Description: This method
	 * returns the forward person Id for a person
	 *
	 * @param ulIdPerson
	 * @return Long
	 */
	public Long getForwardPersonInMerge(Long ulIdPerson);

	/**
	 * Method Name: checkIfMergeListLegacy Method Description: This method
	 * checks if the merge list to be fetched in a legacy stye for a person
	 * forward
	 *
	 * @param fwdPersonId
	 * @return Boolean
	 */
	public Boolean checkIfMergeListLegacy(Long fwdPersonId);

	/**
	 * Method Name: getPersonMergeHierarchyList Method Description: This method
	 * returns the merge hierarchy list for a forward person.
	 *
	 * @param fwdPersonId
	 * @param mergeListLegacy
	 * @return List<PersonMergeSplitValueDto>
	 */
	public List<PersonMergeSplitValueDto> getPersonMergeHierarchyList(Long fwdPersonId, Boolean mergeListLegacy);

	/**
	 * Method Name: getEmployeeTypeDetail Method Description:retrieve employee
	 * type info given person id
	 *
	 * @param idPerson
	 * @return Map
	 */
	@SuppressWarnings("rawtypes")
	public Map getEmployeeTypeDetail(Long idPerson);
	/* Added to fix defect artf185349 */
	/**
	 * Method Name: getEmployeeTypeWithMerge Method Description:retrieve employee
	 * type info given person id
	 *
	 * @param idPerson
	 * @return Map
	 */
	@SuppressWarnings("rawtypes")
	public Map getEmployeeTypeWithMerge(Long idPerson);
	/* End of code changes for artifact artf185349 */

	/**
	 * Method Name: getAfcarsData Method Description:retrieves the one row with
	 * the latest end date from the AFCARS_RESPONSE table for the input Person
	 * ID, for the Person Characteristics page.
	 *
	 * @param idPerson
	 * @return AfcarsDto
	 */
	public AfcarsDto getAfcarsData(Long idPerson);

	/**
	 * Method Name: savePersonAudit Method Description: this method Calls the
	 * stored procedure. Input to the stored procedure are in the ArrayList.
	 *
	 * @param arrayList
	 * @return CommonStringRes
	 */
	public CommonStringRes savePersonAudit(List<Object> arrayList);

	/**
	 * Method Name: savePersonAuditReasonDeath Method Description: this method
	 * Calls the stored procedure. Input to the stored procedure are in the
	 * ArrayList.
	 *
	 * @param arrayList
	 * @return CommonStringRes
	 */
	public CommonStringRes savePersonAuditReasonDeath(List<Object> arrayList);

	/**
	 * Method Name: getAllegationVictimList Method Description:
	 * ArrayList.
	 *
	 * @param allegationVictimReq
	 * @return ArrayList
	 */
	List<String> getAllegationVictimList(AllegationVictimReq allegationVictimReq);

	IncomingPersonMpsDto getMPSPersonDetails(Long idIncominPerson) throws InvocationTargetException, IllegalAccessException;
}
