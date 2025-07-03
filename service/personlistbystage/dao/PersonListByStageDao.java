package us.tx.state.dfps.service.personlistbystage.dao;

import java.util.List;

import us.tx.state.dfps.service.contact.dto.ContactPrincipalsCollateralsDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Fetches
 * Person Details for Stage Information Oct 31, 2017- 10:21:37 AM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface PersonListByStageDao {

	/**
	 * Method Name: getPersonDetailsForStage Method Description:The
	 * getPersonDetailsForStage method returns PersonListStageDIDto person
	 * detail for stage information.
	 * 
	 * @param idStage
	 * @param cdStagePersType
	 * @return List<ContactPrincipalsCollateralsDto>
	 */
	public List<ContactPrincipalsCollateralsDto> getPersonDetailsForStage(long idStage, String cdStagePersType);
	
	/**
	 * Method Name: getPRNPersonDetailsForStage Method Description:The
	 * getPersonDetailsForStage method returns PersonListStageDIDto person
	 * detail for stage information for PRN.
	 * 
	 * @param idStage
	 * @param cdStagePersType
	 * @return List<ContactPrincipalsCollateralsDto>
	 */
	public List<ContactPrincipalsCollateralsDto> getPRNPersonDetailsForStage(long idStage, String stageType);
	
	
	/**
	 * Method Name: getPRNPersonDetailsForStage Method Description:The
	 * getPersonDetailsForStage method returns PersonListStageDIDto person
	 * detail for stage information for PRN.
	 * 
	 * @param idStage
	 * @param cdStagePersType
	 * @return List<ContactPrincipalsCollateralsDto>
	 */
	public boolean getIndChildSxVctmzinHistory(long idPerson);
	
	

	
}
