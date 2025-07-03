package us.tx.state.dfps.service.casepackage.dao;

import us.tx.state.dfps.common.domain.MailCode;
import us.tx.state.dfps.common.domain.Office;
import us.tx.state.dfps.service.admin.dto.EmpOfficeDto;
import us.tx.state.dfps.service.casepackage.dto.OfficeDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCFC21S Class
 * Description: Office DAO Interface Mar 24, 2017 - 6:45:39 PM
 */

public interface OfficeDao {

	/**
	 * 
	 * Method Description: This Method will retrive the office details from
	 * Office table DAM:CCMN00D Service: CCFC21S
	 * 
	 * @param ulIdOffice
	 * @return @
	 */
	public OfficeDto getOfficeDetails(Long ulIdOffice);

	/**
	 * 
	 * Method Description:getOfficeById
	 * 
	 * @param id
	 * @return
	 * @throws DataNotFoundException
	 * @
	 */
	// CCMN05S
	public EmpOfficeDto getOfficeById(Long id);

	/**
	 * Method getOfficeEntityById
	 * 
	 * @param id
	 * @return
	 * @throws DataNotFoundException
	 * @
	 */

	// CCMN05S
	public Office getOfficeEntityById(Long id);

	// CCMNA5D
	Office getOfficeName(String cdOfficeMail, String cdOfficeRegion, String cdOfficeProgram);

	Office createOffice(String cdOfficeMail, String cdOfficeRegion, String cdOfficeProgram, String cdOfficeName);

	MailCode getMailCode(String cdMailCode);
}
