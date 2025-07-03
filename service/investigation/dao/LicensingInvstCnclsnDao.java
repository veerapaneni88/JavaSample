package us.tx.state.dfps.service.investigation.dao;

import us.tx.state.dfps.common.domain.LicensingInvstDtl;
import us.tx.state.dfps.service.investigation.dto.ClassFacilityDto;
import us.tx.state.dfps.service.investigation.dto.InvstRestraintDto;
import us.tx.state.dfps.service.investigation.dto.LicensingInvstDtlDto;
import us.tx.state.dfps.service.resource.dto.ResourceValueBeanDto;

import java.util.Date;
import java.util.List;

/**
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Class
 * Description: Apr 3, 2017 - 12:22:48 PM
 */
public interface LicensingInvstCnclsnDao {
	/**
	 * Method Description: This method is to check for the existence of a contact with the specified type.
	 * artf128755 - CCI Reporter letter
	 * @param idStage stage to check for contacts
	 * @param cdContactType four letter type code from CCNTCTYP
	 * @param idPerson id of the person that would be contected.
	 * @returnBoolean -- true or False
	 *
	 */
	Boolean hasContactTypeToPerson(Long idStage, String cdContactType, Long idPerson);

	/**
	 * Method Name: isChildSexLaborTrafficking Method Description:This method
	 * returns TRUE if the case has answered Child Sex/Labor Trafficking
	 * question in the current stage or there is Allegation of Child Sex/Labor
	 * Trafficking
	 *
	 * @param idStage
	 * @return Boolean
	 */
	public Boolean isChildSexLaborTrafficking(Long idStage);
}
