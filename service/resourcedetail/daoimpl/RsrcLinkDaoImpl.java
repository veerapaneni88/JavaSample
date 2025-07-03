package us.tx.state.dfps.service.resourcedetail.daoimpl;

import static us.tx.state.dfps.service.common.ServiceConstants.REQ_FUNC_CD_ADD;
import static us.tx.state.dfps.service.common.ServiceConstants.REQ_FUNC_CD_DELETE;
import static us.tx.state.dfps.service.common.ServiceConstants.REQ_FUNC_CD_UPDATE;

import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CapsResource;
import us.tx.state.dfps.common.domain.RsrcLink;
import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.response.SubcontrListSaveRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.resource.detail.dto.RsrcLinkInsUpdDelInDto;
import us.tx.state.dfps.service.resourcedetail.dao.RsrcLinkDao;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Update Rsrc
 * link table Jan 30, 2018- 12:09:19 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Repository
public class RsrcLinkDaoImpl implements RsrcLinkDao {

	private static final Integer MSG_DUPLICATE_RECORD = 9029;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(RsrcLinkDaoImpl.class);

	/**
	 * 
	 * Method Name: getRsrcLink Method Description: This method is used for
	 * insert, update and delete resourceLink Dam : CAUD26D
	 * 
	 * @param rsrcLinkInsUpdDelInDto
	 * @return subcontrListSaveRes
	 */
	@Override
	public SubcontrListSaveRes saveRsrcLink(RsrcLinkInsUpdDelInDto rsrcLinkInsUpdDelInDto) {
		log.debug("Entering method getRsrcLink in RsrcLinkInsUpdDelDaoImpl");
		switch (rsrcLinkInsUpdDelInDto.getReqFuncCd()) {
		case REQ_FUNC_CD_ADD:
			return insertRsrcLink(rsrcLinkInsUpdDelInDto);
		case REQ_FUNC_CD_UPDATE:
			return updateRsrcLink(rsrcLinkInsUpdDelInDto);
		case REQ_FUNC_CD_DELETE:
			return deleteRsrcLink(rsrcLinkInsUpdDelInDto);
		}
		return null;
	}

	/**
	 * 
	 * Method Name: insertRsrcLink Method Description: inserts data into Rsrc
	 * Link table
	 * 
	 * @param rsrcLinkInsUpdDelInDto
	 * @return subcontrListSaveRes
	 */
	private SubcontrListSaveRes insertRsrcLink(RsrcLinkInsUpdDelInDto rsrcLinkInsUpdDelInDto) {
		SubcontrListSaveRes subcontrListSaveRes = new SubcontrListSaveRes();
		boolean compositeExists = checkIfExists(rsrcLinkInsUpdDelInDto);
		if (compositeExists) {
			ErrorDto errorDto = new ErrorDto();
			errorDto.setErrorCode(MSG_DUPLICATE_RECORD);
			subcontrListSaveRes.setErrorDto(errorDto);
			return subcontrListSaveRes;
		}

		RsrcLink rsrcLink = new RsrcLink();
		rsrcLink = setRsrcLinkDts(rsrcLinkInsUpdDelInDto, rsrcLink);
		sessionFactory.getCurrentSession().save(rsrcLink);
		subcontrListSaveRes.setIdRsrcLink(rsrcLink.getIdRsrcLink());
		return subcontrListSaveRes;
	}

	/**
	 * 
	 * Method Name: checkIfExists Method Description: This method is used to
	 * check the record exist in rsrc link table
	 * 
	 * @param rsrcLinkInsUpdDelInDto
	 * @return ifExists
	 */
	private boolean checkIfExists(RsrcLinkInsUpdDelInDto rsrcLinkInsUpdDelInDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(RsrcLink.class);
		criteria.add(Restrictions.eq("capsResourceByIdRsrcLinkChild.idResource",
				rsrcLinkInsUpdDelInDto.getIdRsrcLinkChild()));
		criteria.add(Restrictions.eq("capsResourceByIdRsrcLinkParent.idResource",
				rsrcLinkInsUpdDelInDto.getIdRsrcLinkParent()));
		criteria.add(Restrictions.eq("cdRsrcLinkService", rsrcLinkInsUpdDelInDto.getCdRsrcLinkService()));
		RsrcLink rsrcLink = (RsrcLink) criteria.uniqueResult();
		if (!ObjectUtils.isEmpty(rsrcLink)) {
			return true;
		}
		return false;

	}

	/**
	 * Method Name: updateRsrcLink Method Description: updates data in Rsrc Link
	 * table
	 * 
	 * @param rsrcLinkInsUpdDelInDto
	 * @return subcontrListSaveRes
	 */
	private SubcontrListSaveRes updateRsrcLink(RsrcLinkInsUpdDelInDto rsrcLinkInsUpdDelInDto) {
		SubcontrListSaveRes subcontrListSaveRes = new SubcontrListSaveRes();
		if (!TypeConvUtil.isNullOrEmpty(rsrcLinkInsUpdDelInDto.getIdRsrcLink())
				&& !TypeConvUtil.isNullOrEmpty(rsrcLinkInsUpdDelInDto.getDtLastUpdate())) {
			RsrcLink rsrcLink = getRsrcLinkDtls(rsrcLinkInsUpdDelInDto);
			if(!ObjectUtils.isEmpty(rsrcLink)){
				rsrcLink = setRsrcLinkDts(rsrcLinkInsUpdDelInDto, rsrcLink);
				sessionFactory.getCurrentSession().saveOrUpdate(rsrcLink);
			}else{
				ErrorDto error = new ErrorDto();
				error.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
				subcontrListSaveRes.setErrorDto(error);
			}
		}
		return subcontrListSaveRes;
	}

	/**
	 * Method Name: deleteRsrcLink Method Description: delete data from RsrcLink
	 * 
	 * @param rsrcLinkInsUpdDelInDto
	 * @return subcontrListSaveRes
	 */
	private SubcontrListSaveRes deleteRsrcLink(RsrcLinkInsUpdDelInDto rsrcLinkInsUpdDelInDto) {
		SubcontrListSaveRes subcontrListSaveRes = new SubcontrListSaveRes();
		if (!TypeConvUtil.isNullOrEmpty(rsrcLinkInsUpdDelInDto.getIdRsrcLink())
				&& !TypeConvUtil.isNullOrEmpty(rsrcLinkInsUpdDelInDto.getDtLastUpdate())) {
			RsrcLink rsrcLink = getRsrcLinkDtls(rsrcLinkInsUpdDelInDto);
			if(!ObjectUtils.isEmpty(rsrcLink)){
				sessionFactory.getCurrentSession().delete(rsrcLink);
			}else{
				ErrorDto error = new ErrorDto();
				error.setErrorCode(ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH);
				subcontrListSaveRes.setErrorDto(error);
			}			
		}
		return subcontrListSaveRes;
	}

	/**
	 * Method Name: getRsrcLink
	 * 
	 * Method Description: This method RsrcLink details if present in database
	 * 
	 * @param rsrcLinkInsUpdDelInDto
	 * @return RsrcLink
	 */
	private RsrcLink getRsrcLinkDtls(RsrcLinkInsUpdDelInDto rsrcLinkInsUpdDelInDto) {
		RsrcLink rsrcLink = null;
		if (!TypeConvUtil.isNullOrEmpty(rsrcLinkInsUpdDelInDto.getIdRsrcLink())) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(RsrcLink.class);
			criteria.add(Restrictions.eq("idRsrcLink", rsrcLinkInsUpdDelInDto.getIdRsrcLink()));
			// criteria.add(Restrictions.eq("dtLastUpdate",
			// rsrcLinkInsUpdDelInDto.getDtLastUpdate()));
			rsrcLink = (RsrcLink) criteria.uniqueResult();
			/*if (!ObjectUtils.isEmpty(rsrcLink)) {
				return rsrcLink;
			} else {
				throw new DataNotFoundException(messageSource
						.getMessage("rsrcLinkInsUpdDelDaoImpl.resource.not.found.tsLastUpdate", null, Locale.US));
			}
		} else {
			throw new DataNotFoundException(messageSource
					.getMessage("rsrcLinkInsUpdDelDaoImpl.resource.not.found.tsLastUpdate", null, Locale.US));*/
		}
		return rsrcLink;
	}

	/**
	 * Method Name: setRsrcLinkDts Method Description: This method populate rsrc
	 * link details
	 * 
	 * @param rsrcLinkInsUpdDelInDto
	 * @param rsrcLink
	 * @return RsrcLink
	 */
	private RsrcLink setRsrcLinkDts(RsrcLinkInsUpdDelInDto rsrcLinkInsUpdDelInDto, RsrcLink rsrcLink) {
		if (!TypeConvUtil.isNullOrEmpty(rsrcLinkInsUpdDelInDto.getIdRsrcLinkChild())) {
			CapsResource linkChild = (CapsResource) sessionFactory.getCurrentSession().get(CapsResource.class,
					rsrcLinkInsUpdDelInDto.getIdRsrcLinkChild());
			if (TypeConvUtil.isNullOrEmpty(linkChild)) {
				throw new DataNotFoundException(messageSource
						.getMessage("rsrcLinkInsUpdDelDaoImpl.resource.not.found.idRsrcLinkChild", null, Locale.US));
			}
			rsrcLink.setCapsResourceByIdRsrcLinkChild(linkChild);
		}
		if (!TypeConvUtil.isNullOrEmpty(rsrcLinkInsUpdDelInDto.getIdRsrcLinkParent())) {
			CapsResource linkParent = (CapsResource) sessionFactory.getCurrentSession().get(CapsResource.class,
					rsrcLinkInsUpdDelInDto.getIdRsrcLinkParent());
			if (TypeConvUtil.isNullOrEmpty(linkParent)) {
				throw new DataNotFoundException(messageSource
						.getMessage("rsrcLinkInsUpdDelDaoImpl.resource.not.found.idRsrcLinkParent", null, Locale.US));
			}
			rsrcLink.setCapsResourceByIdRsrcLinkParent(linkParent);
		}
		if (!TypeConvUtil.isNullOrEmpty(rsrcLinkInsUpdDelInDto.getCdRsrcLinkType())) {
			rsrcLink.setCdRsrcLinkType(rsrcLinkInsUpdDelInDto.getCdRsrcLinkType());
		}
		if (!TypeConvUtil.isNullOrEmpty(rsrcLinkInsUpdDelInDto.getCdRsrcLinkService())) {
			rsrcLink.setCdRsrcLinkService(rsrcLinkInsUpdDelInDto.getCdRsrcLinkService());
		}
		rsrcLink.setDtLastUpdate(new Date());
		return rsrcLink;
	}
}
