package us.tx.state.dfps.service.kinresourceservice.serviceimpl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.kin.dto.KinHomeInfoDto;
import us.tx.state.dfps.service.kin.dto.ResourceServiceDto;
import us.tx.state.dfps.service.kinresourceservice.service.KinResourceServiceService;
import us.tx.state.dfps.service.person.dao.ResourceServiceDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:KinResourceServiceServiceImpl May 14, 2018- 8:140:41 PM © 2018
 * Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class KinResourceServiceServiceImpl implements KinResourceServiceService {

	@Autowired
	private ResourceServiceDao resServiceDao;

	private static final Logger LOG = Logger.getLogger("ServiceBusiness-KinResourceServiceServiceLog");

	/**
	 * Method Name: updateResourceService Method Description: updates the
	 * RESOURCE_SERVICE table.
	 * 
	 * @param resourceServiceDto
	 * @return Long
	 * 
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long updateResourceService(ResourceServiceDto resourceServiceDto) {
		LOG.debug("Entering method updateResourceService in KinResourceServiceService");
		{
			Long noOfRowsaffected = ServiceConstants.ZERO_VAL;
			try {

				noOfRowsaffected = resServiceDao.updateResourceService(resourceServiceDto);
			} catch (DataNotFoundException e) {
				LOG.error(e.getMessage());
			}
			LOG.debug("Exiting method updateResourceService in KinResourceServiceService");

			return noOfRowsaffected;
		}

	}

	/**
	 * Method Name: getResourceService Method Description: Selects from
	 * RESOURCE_SERVICE table.
	 * 
	 * @param resourceServiceDto
	 * @return ResourceServiceDto
	 * 
	 */

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ResourceServiceDto getResourceService(ResourceServiceDto resourceServiceDto) {
		LOG.debug("Entering method getResourceService in KinResourceServiceService");
		{
			ResourceServiceDto resourceServiceDtoRet = new ResourceServiceDto();
			try {

				resourceServiceDtoRet = resServiceDao.getResourceService(resourceServiceDto);
			} catch (DataNotFoundException e) {
				LOG.error(e.getMessage());
			}
			LOG.debug("Exiting method getResourceService in KinResourceServiceService");
			return resourceServiceDtoRet;

		}

	}

	/**
	 * Method Name: getKinTrainCompleted Method Description: Selects from
	 * FA_INDIV_TRAINING , STAGE_PERSON_LINK tables.
	 * 
	 * @param kinHomeInfoDto
	 * @return String
	 * 
	 */

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public String getKinTrainCompleted(KinHomeInfoDto kinHomeInfoDto) {
		LOG.debug("Entering method getKinTrainCompleted in KinResourceServiceService");
		{
			String indTrain = ServiceConstants.EMPTY_STRING;
			try {

				indTrain = resServiceDao.getKinTrainCompleted(kinHomeInfoDto);
			} catch (DataNotFoundException e) {
				LOG.error(e.getMessage());
			}
			LOG.debug("Exiting method getKinTrainCompleted in KinResourceServiceService");

			return indTrain;
		}

	}

	/**
	 * Method Name: insertResourceService Method Description:insert the values
	 * in resource_service table
	 * 
	 * @param resourceServiceDto
	 * @return Long
	 * 
	 */

	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public Long insertResourceService(ResourceServiceDto resourceServiceDto) {
		LOG.debug("Entering method insertResourceService in KinResourceServiceService");
		{
			Long idResource = ServiceConstants.ZERO_VAL;
			try {

				idResource = resServiceDao.insertResourceService(resourceServiceDto);
			} catch (DataNotFoundException e) {
				LOG.error(e.getMessage());
			}
			LOG.debug("Exiting method insertResourceService in KinResourceServiceService");

			return idResource;

		}
	}

}
