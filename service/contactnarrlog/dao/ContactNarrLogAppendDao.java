package us.tx.state.dfps.service.contactnarrlog.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.service.contact.dto.ContactNarrGuideDto;
import us.tx.state.dfps.service.contact.dto.ContactNarrLogPerDateDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ContactNarrLogAppendDao Feb 14, 2018- 3:09:31 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface ContactNarrLogAppendDao {

	/**
	 * 
	 * Method Name: getContactInfo Method Description: This retrieves rows from
	 * the contact table based upon a date range and an id_stage passed in. Dam
	 * :CLSCD5D
	 * 
	 * @param idStage
	 * @param dtMonthlySummBegin
	 * @param dtMonthlySummEnd
	 * @return ContactNarrLogPerDateDto
	 */
	public List<ContactNarrLogPerDateDto> getContactInfo(Long idStage, Date dtMonthlySummBegin, Date dtMonthlySummEnd);

	/**
	 * 
	 * Method Name: getNameContact Method Description: DAM that retrieves the
	 * Names and Others Contacted for all the events in a particular stage Dam:
	 * CLSCD6D
	 * 
	 * @param idStage
	 * @param dtDtMonthlySummBegin
	 * @param dtDtMonthlySummEnd
	 * @return List<ContactNarrLogPerDateDto>
	 */
	public List<ContactNarrLogPerDateDto> getNameContact(Long idStage, Date dtDtMonthlySummBegin,
			Date dtDtMonthlySummEnd);

	/**
	 * 
	 * Method Name: getContactGuide Method Description:
	 * 
	 * @param idCase
	 * @param dtDtMonthlySummBegin
	 * @param dtDtMonthlySummEnd
	 */
	public List<ContactNarrGuideDto> getContactGuide(Long idCase, Date dtDtMonthlySummBegin, Date dtDtMonthlySummEnd);

}
