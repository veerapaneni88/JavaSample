package us.tx.state.dfps.service.person.dao;

import java.util.List;

import us.tx.state.dfps.common.domain.FaIndivTraining;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.kin.dto.KinHomeInfoDto;
import us.tx.state.dfps.service.kin.dto.ResourceServiceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ResourceServiceDao Oct 31, 2017- 6:18:30 PM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface ResourceServiceDao {

	/**
	 * @param personId
	 * @return
	 */
	public List<FaIndivTraining> getFATrainingList(long personId);

	/**
	 * @param faIndivTraining
	 * @return
	 */
	public int saveFATrainingRecord(FaIndivTraining faIndivTraining);

	/**
	 * Method Name: updateResourceService Method Description: The following
	 * method updates IND_KNSHP_TRAINING, IND_KNSHP_HOME_ASSMNT,
	 * IND_KNSHP_INCOME, IND_KNSHP_AGREEMENT
	 * 
	 * @param resourceServiceDto
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long updateResourceService(ResourceServiceDto resourceServiceDto) throws DataNotFoundException;

	/**
	 * Method Name:getResourceService Method Description: Selects from
	 * RESOURCE_SERVICE table
	 * 
	 * @param resourceServiceDto
	 * @return ResourceServiceDto
	 * @throws DataNotFoundException
	 */
	public ResourceServiceDto getResourceService(ResourceServiceDto resourceServiceDto) throws DataNotFoundException;

	/**
	 * Method Name: getKinTrainCompleted Method Description:Selects from
	 * FA_INDIV_TRAINING , STAGE_PERSON_LINK tables.
	 * 
	 * @param kinHomeInfoDto
	 * @return String
	 * @throws DataNotFoundException
	 */
	public String getKinTrainCompleted(KinHomeInfoDto kinHomeInfoDto) throws DataNotFoundException;

	/**
	 * Method Name: insertResourceService Method Description:insert the values
	 * in resource_service table
	 * 
	 * @param resourceServiceDto
	 * @return Long
	 * @throws DataNotFoundException
	 */
	public Long insertResourceService(ResourceServiceDto resourceServiceDto) throws DataNotFoundException;
}
