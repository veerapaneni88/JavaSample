package us.tx.state.dfps.service.centralregistry.centralregistrydao;

import java.util.List;

import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.xmlstructs.inputstructs.PersonRoleDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * CentralRegistryDao will have all operation which are mapped to CentralRegisty
 * module. Apr 27, 2018- 2:01:02 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface CentralRegistryDao {

	/**
	 * 
	 * Method Name: getPersonRoleDtoli DAM Name : CLSC91D Method
	 * Description:This dam will select the person roles of DB, DP, VP, AP in
	 * open inv stages and place a check mark in the first box.
	 * 
	 * @param idPerson
	 * @return
	 */
	public List<PersonRoleDto> getPersonRolesForOpenInv(Long idPerson);

	/**
	 * 
	 * Method Name: getPersonRolesForOpenARIn DAM Name : CLSC9AD Method
	 * Description:This dam will select the person roles of VP, AP of intakes
	 ** tied to the open A-R stage and place a check mark in the box.
	 **
	 * @param idPerson
	 * @return
	 */
	public List<PersonRoleDto> getPersonRolesForOpenARIn(Long idPerson);

	/**
	 * 
	 * Method Name: getSpPersonRole DAM Name : CLSC92D Method Description:This
	 * dam will select the role of Sustained Perpetrator and place a check mark
	 * the 3rd question on the form.
	 * 
	 * @param idPerson
	 * @return
	 */
	public List<PersonRoleDto> getSpPersonRole(Long idPerson);

	/**
	 * 
	 * Method Name: getVictimPersonRoles DAM Name : CLSC93D Method
	 * Description:This dam will select the role of designated victim or perp
	 ** and place a check mark the 4th question on the form.
	 **
	 * @param idPerson
	 * @return
	 */
	public List<PersonRoleDto> getVictimPersonRoles(Long idPerson);

	/**
	 * 
	 * Method Name: getPersonInfo DAM Name: CSES96D Method Description: This dam
	 * will return person information.
	 * 
	 * @param idPerson
	 * @return
	 */
	public PersonDto getPersonInfo(Long idPerson);

}
