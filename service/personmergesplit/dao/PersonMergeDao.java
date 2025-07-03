package us.tx.state.dfps.service.personmergesplit.dao;

import us.tx.state.dfps.service.person.dto.PersonMergeSplitDto;
import us.tx.state.dfps.web.person.bean.SelectForwardPersonValueBean;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<DAO Level
 * class for Person Merge> May 30, 2018- 11:12:56 AM © 2017 Texas Department of
 * Family and Protective Services
 */
public interface PersonMergeDao {

	/**
	 * 
	 * Method Name: mergePersons Method Description: merge person
	 * 
	 * @param persMergeValueBean
	 * @param sfPersValueBean
	 */
	public void mergePersons(PersonMergeSplitDto persMergeValueBean, SelectForwardPersonValueBean sfPersValueBean);

	/**
	 * 
	 * Method Name: processOpenStagePersonData Method Description:This method
	 * updates the forward Person ID on various data which contain the closed
	 * Person ID. Only the data pertaining to open stages is updated.
	 * 
	 * @param personMergeSplitDB
	 */
	public void processOpenStagePersonData(PersonMergeSplitDto personMergeSplitDB);

	/**
	 * 
	 * Method Name: processSSCCPersonData Method Description:Merge SSCC Records
	 * in OPEN Stages. Gets all the Referrals (Active and Inactive) for the
	 * Closed Person in any Open Stage, and update Closed Person ID with Forward
	 * Person ID for these tables.
	 * 
	 * @param persMergeValueBean
	 */
	public void processSSCCPersonData(PersonMergeSplitDto persMergeValueBean);

}
