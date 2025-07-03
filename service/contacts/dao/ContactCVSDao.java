package us.tx.state.dfps.service.contacts.dao;

import java.util.Date;
import java.util.List;

import us.tx.state.dfps.service.contact.dto.ContactEventDto;
import us.tx.state.dfps.service.contact.dto.ContactGuideDto;
import us.tx.state.dfps.service.contact.dto.ContactPersonDto;
import us.tx.state.dfps.web.contact.bean.ContactDetailDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Dao methods
 * for cvs report Mar 27, 2018- 3:48:21 PM © 2017 Texas Department of Family and
 * Protective Services
 */
public interface ContactCVSDao {
	/**
	 * DAM Name: CLSCG5D
	 * 
	 * Method Name: Retrieves child info for monthly eval The requirement of
	 * this DAM cannot be fulfilled in a single query for performance reasons.
	 * Therefore, required data is fetched for the selected children using
	 * multiple subqueries. The other option would be write multiple DAMS, one
	 * for main query and 6 other DAMS for subqueries. In this way form design
	 ** (mappings) would also be very complex. Therefore, it is better to fetch
	 * all the data elements using this single DAM.
	 * 
	 * @param Date
	 *            dtMonthlyBeginSumm, Long idCase
	 * @return List<ContactPersonDto>
	 */
	public List<ContactPersonDto> getChildInfo(Date dtMonthlyBeginSumm, Long idCase);

	/**
	 * DAM Name: CLSCG6D
	 * 
	 * Method Name: getParentInfo Method Description:Parent information for
	 * monthly eval. The requirement of this DAM cannot be fulfilled in a single
	 * query for performance reasons. Therefore, required data is fetched for
	 * the selected parents using multiple subqueries. The other option would be
	 * write multiple DAMS, one for main query and 2 other DAMS for subqueries.
	 * The logic to retrieve the Family Plan data would still be very complex
	 * even with multiple DAMs, and the form mapping would be more complex with
	 * multiple DAMs.
	 * 
	 * @param Date
	 *            dtMonthlyBeginSumm, Long idCase
	 * @return List<ContactPersonDto>
	 */
	public List<ContactPersonDto> getParentInfo(Date dtMonthlyBeginSumm, Long idCase,
			List<ContactDetailDto> contactListDB);

	/**
	 * DAM Name: CLSCG8D
	 * 
	 * Method Name: getContactInfo Method Description:Contact guide topics
	 * 
	 * @param Long
	 *            idCase
	 * @return List<ContactGuideDto>
	 */
	public List<ContactGuideDto> getContactInfo(Long idCase,List<Long> idContactGuideNarrList);

	/**
	 * DAM Name: CLSCGGD
	 * 
	 * Method Name: getEventInfo Method Description:Kinship Notification contact
	 * event info
	 * 
	 * @param Long
	 *            idCase
	 * @return List<ContactEventDto>
	 */
	public List<ContactEventDto> getEventInfo(Long idCase);

}
