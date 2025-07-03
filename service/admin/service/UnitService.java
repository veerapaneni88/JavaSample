package us.tx.state.dfps.service.admin.service;

import java.util.List;

import us.tx.state.dfps.common.domain.Unit;
import us.tx.state.dfps.common.domain.UnitEmpLink;
import us.tx.state.dfps.service.admin.dto.UnitEmpLinkDto;
import us.tx.state.dfps.service.common.request.ExternalUserUnitReq;
import us.tx.state.dfps.service.common.request.SaveUnitReq;
import us.tx.state.dfps.service.common.request.SearchUnitSupervisorReq;
import us.tx.state.dfps.service.common.request.UnitDetailReq;
import us.tx.state.dfps.service.common.request.UnitListReq;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.SaveUnitRes;
import us.tx.state.dfps.service.common.response.SearchUnitSupervisorRes;
import us.tx.state.dfps.service.common.response.UnitDetailRes;
import us.tx.state.dfps.service.common.response.UnitListRes;
import us.tx.state.dfps.service.person.dto.UnitDto;

/**
 * 
 * ImpactWSIntegration - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:
 * CCMN23S, CCMN24S Class Description: Unit Detail Service class. Apr 14, 2017 -
 * 5:27:58 PM
 */

public interface UnitService {

	/**
	 * 
	 * Method Description: This Service receives input to get unit detail
	 * through query. Tuxedo Service Name: CCMN23S
	 * 
	 * @param unitDetailReq
	 * @
	 */
	public UnitDetailRes getUnitDetail(UnitDetailReq unitDetailReq);

	/**
	 * '
	 * 
	 * Method Description: This service receives input to get list of unit from
	 * dao query Tuxedo Service Name: CCMN24S
	 * 
	 * @param unitListReq
	 * @
	 */

	public UnitListRes getUnitList(UnitListReq unitListReq);

	/**
	 * 
	 * Method Description:saveUnit
	 * 
	 * @param saveUnitReq
	 * @return
	 */
	// CCMN22S
	public SaveUnitRes saveUnit(SaveUnitReq saveUnitReq);

	/**
	 * 
	 * Method Description:getUnitEmpLinkDto
	 * 
	 * @param uel
	 * @return
	 */
	// CCMN05S
	public UnitEmpLinkDto getUnitEmpLinkDto(UnitEmpLink uel);

	/**
	 * 
	 * Method Description:getUnitDto
	 * 
	 * @param unit
	 * @return
	 */
	// CCMN22S
	public UnitDto getUnitDto(Unit unit);

	/**
	 * 
	 * Method Description:searchUnitSupervisor
	 * 
	 * @param searchUnitSupervisorReq
	 * @return
	 */
	// CCMN08S
	public SearchUnitSupervisorRes searchUnitSupervisor(SearchUnitSupervisorReq searchUnitSupervisorReq);

	/**
	 * 
	 * Method Description:saveUnitEmpLink
	 * 
	 * @param unitEmpLinkDtoList
	 * @param unitId
	 */
	// CCMN22S
	public void saveUnitEmpLink(List<UnitEmpLinkDto> unitEmpLinkDtoList, Long unitId, UnitDto unitDto);

	/**
	 * 
	 * Method Description:editUnitEmpLinkDto
	 * 
	 * @param unitEmpLinkDto
	 * @param action
	 * @return
	 */
	// CCMN49D
	public UnitEmpLinkDto editUnitEmpLinkDto(UnitEmpLinkDto unitEmpLinkDto, String action);

	/**
	 * 
	 * Method Description:editUnitDto
	 * 
	 * @param unitDto
	 * @param action
	 * @return
	 */
	// CCMN48D
	public UnitDto editUnitDto(UnitDto unitDto, String action);

	/**
	 * unitAccess This is a common function designed to determine whether or not
	 * a set of employees (the user + designees) has access for unit
	 * modification. This is performed by comparing the Unit Member Roles of the
	 * set of employees against that of the unit's approver and checks up the
	 ** unit heirarchy via the Parent Unit, if necessary. The function receives
	 * ID PERSON for the user, ID PERSON for the user's designees and either ID
	 * UNIT or CD UNIT PROGRAM, CD UNIT REGION, and NBR UNIT. It returns either
	 * TRUE or FALSE.
	 *
	 * @param ulidUnit
	 * @return
	 */
	// CCMN04U
	public boolean unitAccess(Long ulIdUnit, String cdUnitProgram, String cdUnitRegion, String nbrUnit,
			List<Long> ulIdPersons);

	/**
	 * Method Name: saveNewExternalUnitDetail Method Description: The Service to
	 * save New External Unit Details.
	 * 
	 * @param externalUserUnitReq
	 */
	public void saveNewExternalUnitDetail(ExternalUserUnitReq externalUserUnitReq);

	public List<String> fetchCatchmentsForRegion(String cdRegion);

	public CommonHelperRes isExteralUnit(String program, String region, String unitNbr);
}
