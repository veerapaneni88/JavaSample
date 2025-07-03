package us.tx.state.dfps.service.resourcedetail.daoimpl;

import static us.tx.state.dfps.service.common.ServiceConstants.REQ_FUNC_CD_ADD;
import static us.tx.state.dfps.service.common.ServiceConstants.REQ_FUNC_CD_ADD_KIN;
import static us.tx.state.dfps.service.common.ServiceConstants.REQ_FUNC_CD_DELETE;
import static us.tx.state.dfps.service.common.ServiceConstants.REQ_FUNC_CD_UPDATE;

import java.util.Date;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CapsResource;
import us.tx.state.dfps.common.domain.ResourcePhone;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.resource.detail.dto.ResourceDetailInDto;
import us.tx.state.dfps.service.resource.detail.dto.ResourceDetailOutDto;
import us.tx.state.dfps.service.resourcedetail.dao.ResourcePhoneDao;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Resource
 * Phone table implementation for PhoneInsUpdDelDao Jan 30, 2018- 12:07:21 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Repository
public class ResourcePhoneDaoImpl implements ResourcePhoneDao {

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(ResourcePhoneDaoImpl.class);

	/**
	 * Description: Resource Phone table updation
	 * 
	 * @param resourceDetailInDto
	 * @return rowCount @
	 */
	/**
	 * 
	 * Method Name: saveResourceAddress Method Description: This method used for
	 * resource phone insert,update and delete operations resourceDetailInDto
	 * request
	 * 
	 * @param resourceDetailInDto
	 * @return result @
	 */
	@Override
	public ResourceDetailOutDto saveResourcePhone(ResourceDetailInDto resourceDetailInDto) {
		log.debug("Entering method resourcePhoneKAUD in PhoneInsUpdDelDaoImpl");
		ResourceDetailOutDto resourceDetailOutDto = new ResourceDetailOutDto();
		Long result = 0L;
		ResourcePhone resourcePhone = null;
		Date lastUpdatedDt = resourceDetailInDto.getDtLastUpdate();
		switch (resourceDetailInDto.getCdScrDataAction()) {
		case REQ_FUNC_CD_ADD:
			resourcePhone = new ResourcePhone();
			CapsResource capsResource = new CapsResource();
			capsResource.setIdResource(resourceDetailInDto.getIdResource());
			resourcePhone.setCapsResource(capsResource);
			resourcePhone.setTxtRsrcPhoneComments(resourceDetailInDto.getRsrcPhoneComments());
			resourcePhone.setNbrRsrcPhoneExt(resourceDetailInDto.getNbrFacilPhoneExt());
			resourcePhone.setCdRsrcPhoneType(resourceDetailInDto.getCdFacilPhoneType());
			resourcePhone.setNbrRsrcPhone(resourceDetailInDto.getNbrFacilPhone());
			if (!ObjectUtils.isEmpty(resourceDetailInDto.getDtLastUpdate())) {
				resourcePhone.setDtLastUpdate(resourceDetailInDto.getDtLastUpdate());
			} else {
				resourcePhone.setDtLastUpdate(new Date());
			}

			result = (Long) sessionFactory.getCurrentSession().save(resourcePhone);
			break;
		case REQ_FUNC_CD_UPDATE:
			resourcePhone = (ResourcePhone) sessionFactory.getCurrentSession().load(ResourcePhone.class,
					resourceDetailInDto.getIdRsrcPhone());

			if (resourcePhone.getDtLastUpdate().compareTo(lastUpdatedDt) != 0) {
				ErrorDto errorDto = new ErrorDto();
				errorDto.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
				resourceDetailOutDto.setErrorDto(errorDto);
			} else {

				if (!ObjectUtils.isEmpty(resourcePhone)
						&& lastUpdatedDt.compareTo(resourcePhone.getDtLastUpdate()) == 0) {
					resourcePhone.setTxtRsrcPhoneComments(resourceDetailInDto.getRsrcPhoneComments());
					resourcePhone.setIdRsrcPhone(resourceDetailInDto.getIdRsrcPhone());
					resourcePhone.setNbrRsrcPhoneExt(resourceDetailInDto.getNbrFacilPhoneExt());
					resourcePhone.setCdRsrcPhoneType(resourceDetailInDto.getCdFacilPhoneType());
					resourcePhone.setDtLastUpdate(resourceDetailInDto.getDtLastUpdate());
					resourcePhone.setNbrRsrcPhone(resourceDetailInDto.getNbrFacilPhone());
					sessionFactory.getCurrentSession().update(resourcePhone);
					result = resourceDetailInDto.getIdRsrcPhone();
				}
			}

			break;
		case REQ_FUNC_CD_ADD_KIN:
			resourcePhone = (ResourcePhone) sessionFactory.getCurrentSession().load(ResourcePhone.class,
					resourceDetailInDto.getIdResource());
			if (!ObjectUtils.isEmpty(resourcePhone) && !ObjectUtils.isEmpty(resourcePhone.getCdRsrcPhoneType())
					&& (resourcePhone.getCdRsrcPhoneType().equals(resourceDetailInDto.getCdFacilPhoneType()))
					&& lastUpdatedDt.compareTo(resourcePhone.getDtLastUpdate()) == 0) {
				resourcePhone.setTxtRsrcPhoneComments(resourceDetailInDto.getRsrcPhoneComments());
				resourcePhone.setIdRsrcPhone(resourceDetailInDto.getIdRsrcPhone());
				resourcePhone.setNbrRsrcPhoneExt(resourceDetailInDto.getNbrFacilPhoneExt());
				resourcePhone.setCdRsrcPhoneType(resourceDetailInDto.getCdFacilPhoneType());
				resourcePhone.setDtLastUpdate(resourceDetailInDto.getDtLastUpdate());
				resourcePhone.setNbrRsrcPhone(resourceDetailInDto.getNbrFacilPhone());
				sessionFactory.getCurrentSession().update(resourcePhone);
				result = resourceDetailInDto.getIdRsrcPhone();
			}
			break;
		case REQ_FUNC_CD_DELETE:
			resourcePhone = (ResourcePhone) sessionFactory.getCurrentSession().load(ResourcePhone.class,
					resourceDetailInDto.getIdRsrcPhone());

			if (!ObjectUtils.isEmpty(resourcePhone) && lastUpdatedDt.compareTo(resourcePhone.getDtLastUpdate()) == 0) {
				sessionFactory.getCurrentSession().delete(resourcePhone);
				result = resourceDetailInDto.getIdRsrcPhone();
			}
			break;
		}
		resourceDetailOutDto.setResult(result);
		log.debug("Exiting method resourcePhoneKAUD in PhoneInsUpdDelDaoImpl");
		return resourceDetailOutDto;
	}
}
