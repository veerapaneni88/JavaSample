package us.tx.state.dfps.service.person.service;

import java.util.List;
import java.util.Map;

import us.tx.state.dfps.service.common.request.AfcarsReq;
import us.tx.state.dfps.service.common.request.AllegationVictimReq;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.request.PersonDtlReq;
import us.tx.state.dfps.service.common.response.*;
import us.tx.state.dfps.service.conservatorship.dto.CharacteristicsDto;
import us.tx.state.dfps.service.personmergesplit.dto.PersonMergeSplitValueDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PersonDetailService Apr 30, 2018- 5:42:54 PM © 2017 Texas
 * Department of Family and Protective Services
 * ****************  Change History *********************
 * 05/24/2021 nairl Artifact artf185349 : Security -Records check tab is displaying for merged DFPS employee with Person in Closed case Legacy IMPACT and IMPACT2.0
 */
public interface PersonDetailService {

	/**
	 * Method Name: isPersonEmpOrFormerEmp Method Description: Checks if person
	 * is an employee or former employee
	 * 
	 * @param idPerson
	 * @return Boolean
	 */
	public Boolean isPersonEmpOrFormerEmp(Long idPerson);

	/**
	 * Method Name: hasSSN Method Description: Check if the Person has a Non End
	 * Dated SSN
	 * 
	 * @param commonHelperReq
	 * @return HasSSNRes
	 */
	public HasSSNRes hasSSN(CommonHelperReq commonHelperReq);

	/**
	 * 
	 * Method Name: getPersonIdAndFullName Method Description: Method to get
	 * Fullname and person Id
	 * 
	 * @param personDtlReq
	 * @return PersonFullNameRes
	 */
	public PersonFullNameRes getPersonIdAndFullName(PersonDtlReq personDtlReq);

	/**
	 * Method Name: isSSNVerifiedByInterface Method Description:Check if the SSN
	 * has been Verified by DHS Interface
	 * 
	 * @param szNbrPersonIdNbr
	 * @return Boolean
	 */
	public Boolean isSSNVerifiedByInterface(String szNbrPersonIdNbr);

	/**
	 * Method Name: fetchPersonCharacDetails Method Description:Provides the
	 * list of characteristics for a person along with person details
	 * 
	 * @param idPerson
	 * @return HashMap
	 */
	public PersonEthnicityRes fetchPersonCharacDetails(CharacteristicsDto characteristicsDto);
	/**
	 * Method Name: allegationVictimInformation Method Description:Provides the
	 * list of victimId details
	 *
	 * @param allegationVictimReq
	 * @return AllegationVictimRes
	 */
	public AllegationVictimRes allegationVictimInformation(AllegationVictimReq allegationVictimReq);
	/**
	 * Method Name: getPersonMergeHierarchyList Method Description: This method
	 * returns the merge hierarchy list for a forward person.
	 * 
	 * @param ulIdPerson
	 * @return List<PersonMergeSplitValueDto>
	 */
	public List<PersonMergeSplitValueDto> getPersonMergeHierarchyList(Long ulIdPerson);

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
	 * Method Name: getEmployeeTypeWithMerge
	 * Method Description:retrieve employee type info given person id
	 *
	 * @param idPerson
	 * @return Map
	 */
	@SuppressWarnings("rawtypes")
	public Map getEmployeeTypeWithMerge(Long idPerson);

/* End of code changes for artifact artf185349 */


	/**
	 * Method Name: fetchAfcarsData Method Description:Retrieve the row with the
	 * latest end date for the Person ID, from AFCARS_RESPONSE.
	 * 
	 * @param afcarsReq
	 * @return AfcarsRes
	 */
	public AfcarsRes fetchAfcarsData(AfcarsReq afcarsReq);

	/**
	 * Method Name: savePersonAudit Method Description:Insert relevant data into
	 * PERSON_AUDIT table, Do this only if the page is in NEW mode
	 * 
	 * @param arrayList
	 * @return CommonStringRes
	 */
	public CommonStringRes savePersonAudit(List<Object> arrayList);

	/**
	 * Method Name: executeStoredProc Method Description:This is a method to set
	 * all input parameters(of the stored procedure).
	 * 
	 * @param arrayList
	 * @return CommonStringRes
	 */
	public CommonStringRes savePersonAuditReasonDeath(List<Object> arrayList);
}
