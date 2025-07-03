package us.tx.state.dfps.service.contacts.dao;

import us.tx.state.dfps.service.admin.dto.ContactDetailSaveDiDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ContactAUDDto;
import us.tx.state.dfps.xmlstructs.outputstructs.SimpleEventStageDto;

import java.util.List;
import java.util.Map;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ContactProcessDao Aug 2, 2018- 12:41:51 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface ContactProcessDao {

	/**
	 * 
	 * Method Name: updateContactAndContactNarrative Method Description:update
	 * Contact And Narrative
	 * 
	 * @param contactDetailSaveDiDto
	 */
	public void updateContactAndContactNarrative(ContactDetailSaveDiDto contactDetailSaveDiDto);

	/**
	 * 
	 * Method Name: audContact Method Description:This DAO is an AUD for the
	 * CONTACT table.(csys07d)
	 * 
	 * @param contactDetailSaveDiDto
	 * @return long
	 */
	public long audContact(ContactDetailSaveDiDto contactDetailSaveDiDto);

	// CANIRSP-68 Alleged Victims
    public Long getInrGroupNum(Long eventId);
	public Long getNextInrGroupNum();
	public Long createInrGroup(String cdInrProviderRegType, String txtNarrativeRpt, Long idPerson);
	public boolean updateInrGroup(Long idGroup, String txtNarrative, String cdNarrative, Long idPerson);
	public void audInrGrouping(ContactAUDDto contactAUDDto);
	public Map<Long, SimpleEventStageDto> getInrPersonIdToDataMap(Long nbrGroup);
    public void mergeGroupLinkToStages(String cdRegTyp, String txtNarr, List<Long> intakeStageIds, Long idInrGroup, Long idPerson, boolean inrGroupIsNew);
	public String getIntakeStageListByEventId(Long eventId);
	List<Long> getIntakeStageListByGroupId(Long groupId);
	public void deleteInstakeStageListForGroup(Long nbrGroup);
	public void deleteInrGroup(Long idGroup);
}
