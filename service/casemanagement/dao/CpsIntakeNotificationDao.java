package us.tx.state.dfps.service.casemanagement.dao;

import java.util.List;

import us.tx.state.dfps.common.dto.NameDto;
import us.tx.state.dfps.service.cpsinv.dto.CaseDtlsDto;
import us.tx.state.dfps.service.cpsinv.dto.OfficePhoneDto;
import us.tx.state.dfps.service.intake.dto.IncmgDetermFactorsDto;
import us.tx.state.dfps.service.person.dto.IntakeAllegationDto;
import us.tx.state.dfps.service.person.dto.PersonAddrLinkDto;

/**
 * Class Name:CpsIntakeNotificationDao Class
 * Description:CpsIntakeNotificationDao performs some of the database activities
 * to retrieve the CPS Intake Notifications based on the Stage ID. Oct 10, 2017-
 * 1:42:10 PM © 2017 Texas Department of Family and Protective Services
 */
public interface CpsIntakeNotificationDao {

	/**
	 * Method Name:getAllegationTypeByStageId Method Description:This method
	 * retrieves AllegationTypes
	 * 
	 * @param idStage
	 * @return @
	 */
	public List<IntakeAllegationDto> getAllegationTypeByStageId(Long idStage);

	/**
	 * Method Name:getResidenceAddress Method Description:This method retrieves
	 * the residence Address
	 * 
	 * @param idStage
	 * @return @
	 */
	public PersonAddrLinkDto getResidenceAddress(Long idStage);

	/**
	 * Method Name:getWorkerOfficeDetail Method Description:This method
	 * retrieves the office Details
	 * 
	 * @param idStage
	 * @param indOfficePrimary
	 * @return @
	 */
	public OfficePhoneDto getWorkerOfficeDetail(Long idStage, String indOfficePrimary);

	/**
	 * Method Name:getCaseDetails Method Description: This method retrieves the
	 * case details
	 * 
	 * @param idStage
	 * @return @
	 */
	public CaseDtlsDto getCaseDetails(Long idStage);

	/**
	 * Method Name:getNameAliases Method Description:This method retreives the
	 * alias name
	 * 
	 * @param idStage
	 * @return @
	 */
	public List<NameDto> getNameAliases(Long idStage);

	/**
	 * Method Name:getAddrInfoByStageId Method Description:This method retreives
	 * the Address Information
	 * 
	 * @param idStage
	 * @return @
	 */
	public List<PersonAddrLinkDto> getAddrInfoByStageId(Long idStage);

	/**
	 * Method Name:getincmgDetermFactorsById Method Description:This method
	 * retrieves the Incoming Factors
	 * 
	 * @param idStage
	 * @return @
	 */
	public List<IncmgDetermFactorsDto> getincmgDetermFactorsById(Long idStage);
}
