package us.tx.state.dfps.service.resourcedetail.daoimpl;

import static us.tx.state.dfps.service.common.ServiceConstants.REQ_FUNC_CD_ADD;
import static us.tx.state.dfps.service.common.ServiceConstants.REQ_FUNC_CD_DELETE;
import static us.tx.state.dfps.service.common.ServiceConstants.REQ_FUNC_CD_UPDATE;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.ResourceLanguage;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.resource.detail.dto.ResourceDetailInDto;
import us.tx.state.dfps.service.resourcedetail.dao.ResourceLanguageDao;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:: This class
 * inserts,updates,deletes into Resource Language Jan 30, 2018- 12:00:08 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Repository
public class ResourceLanguageDaoImpl implements ResourceLanguageDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private MessageSource messageSource;

	private static final Logger log = Logger.getLogger(ResourceLanguageDaoImpl.class);

	/**
	 * 
	 * Method Name: saveResourceLanguage Method Description: This method used
	 * for resource language insert,update and delete operations
	 * resourceDetailInDto request
	 * 
	 * @param resourceDetailInDto
	 * @return result @
	 */
	@Override
	public Long saveResourceLanguage(ResourceDetailInDto resourceDetailInDto) {
		log.debug("Entering method saveResourceLanguage in ResourceLanguageDaoImpl");
		Long result = 0L;
		String operation = resourceDetailInDto.getCdScrDataAction();
		ResourceLanguage resourceLanguage = null;
		Date lastUpdatedDt = resourceDetailInDto.getDtLastUpdate();
		if (REQ_FUNC_CD_ADD.equalsIgnoreCase(operation)) {
			resourceLanguage = new ResourceLanguage();
			resourceLanguage.setCdLanguage(resourceDetailInDto.getCdRsrcLanguage());
			resourceLanguage.setIdResource(resourceDetailInDto.getIdResource());
			resourceLanguage.setIdEmpLastUpdate(resourceDetailInDto.getIdEmpLastUpdate());
			resourceLanguage.setDtEnd(resourceDetailInDto.getDtRsrcLanguageEnd());
			resourceLanguage.setDtLastUpdate(Calendar.getInstance().getTime());
			resourceLanguage.setDtStart(Calendar.getInstance().getTime());
			result = (Long) sessionFactory.getCurrentSession().save(resourceLanguage);
		} else if (REQ_FUNC_CD_UPDATE.equalsIgnoreCase(operation)) {
			resourceLanguage = (ResourceLanguage) sessionFactory.getCurrentSession().load(ResourceLanguage.class,
					resourceDetailInDto.getIdRsrcLanguage());
			if (!ObjectUtils.isEmpty(resourceLanguage) && ObjectUtils.isEmpty(resourceLanguage.getDtEnd())) {
				resourceLanguage.setCdLanguage(resourceDetailInDto.getCdRsrcLanguage());
				resourceLanguage.setIdResource(resourceDetailInDto.getIdResource());
				resourceLanguage.setIdEmpLastUpdate(resourceDetailInDto.getIdEmpLastUpdate());
				resourceLanguage.setDtEnd(resourceDetailInDto.getDtRsrcLanguageEnd());
				resourceLanguage.setDtLastUpdate(lastUpdatedDt);
				sessionFactory.getCurrentSession().update(resourceLanguage);
				result = resourceDetailInDto.getIdRsrcLanguage();
			}else{
				return Long.valueOf(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
			}
		} else if (REQ_FUNC_CD_DELETE.equalsIgnoreCase(operation)) {
			resourceLanguage = (ResourceLanguage) sessionFactory.getCurrentSession().load(ResourceLanguage.class,
					resourceDetailInDto.getIdRsrcLanguage());
			if (!ObjectUtils.isEmpty(resourceLanguage)
					&& (lastUpdatedDt.compareTo(resourceLanguage.getDtLastUpdate()) == 0)) {
				sessionFactory.getCurrentSession().delete(resourceLanguage);
				result = resourceDetailInDto.getIdRsrcLanguage();
			}
		}
		if (result == 0) {
			throw new DataNotFoundException(
					messageSource.getMessage("caps.resource.language.nodata.update", null, Locale.US));
		}
		log.debug("Exiting method saveResourceLanguage in ResourceLanguageDaoImpl");
		return result;
	}
}
