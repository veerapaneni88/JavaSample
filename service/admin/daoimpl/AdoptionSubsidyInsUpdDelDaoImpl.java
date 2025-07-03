package us.tx.state.dfps.service.admin.daoimpl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.AdoptionSubsidy;
import us.tx.state.dfps.common.domain.CapsResource;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Placement;
import us.tx.state.dfps.service.admin.dao.AdoptionSubsidyInsUpdDelDao;
import us.tx.state.dfps.service.admin.dto.AdoptionSubsidyInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.AdoptionSubsidyInsUpdDelOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Updates the
 * Adoption subsidy record. Aug 10, 2017- 6:38:06 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class AdoptionSubsidyInsUpdDelDaoImpl implements AdoptionSubsidyInsUpdDelDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(AdoptionSubsidyInsUpdDelDaoImpl.class);

	public AdoptionSubsidyInsUpdDelDaoImpl() {
		super();
	}

	/**
	 * Method Name: updateAdoptionSubsidy Method Description: Updates or
	 * insertes Adoption subsidy record
	 * 
	 * @param pInputDataRec
	 * @param pOutputDataRec
	 * @return Caud81doDto @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public AdoptionSubsidyInsUpdDelOutDto updateAdoptionSubsidy(AdoptionSubsidyInsUpdDelInDto pInputDataRec) {
		log.debug("Entering method AdoptionSubsidyInsUpdDelQUERYdam in AdoptionSubsidyInsUpdDelDaoImpl");
		int rowCount = 0;
		AdoptionSubsidyInsUpdDelOutDto pOutputDataRec = new AdoptionSubsidyInsUpdDelOutDto();
		switch (pInputDataRec.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			saveAdoptionsubsidy(pInputDataRec);
			break;
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			updateAdoptionsubsidy(pInputDataRec);
			break;
		case ServiceConstants.REQ_FUNC_CD_DELETE:
			deleteAdoptionsubsidy(pInputDataRec);
			break;
		}
		pOutputDataRec.setTotalRecCount((long) rowCount);
		log.debug("Exiting method AdoptionSubsidyInsUpdDelQUERYdam in AdoptionSubsidyInsUpdDelDaoImpl");
		return pOutputDataRec;
	}

	/**
	 * 
	 * Method Name: deleteAdoptionsubsidy Method Description: deletes
	 * Adoptionsubsidy for a person
	 * 
	 * @param pInputDataRec
	 */
	public void deleteAdoptionsubsidy(AdoptionSubsidyInsUpdDelInDto pInputDataRec) {
		if (pInputDataRec != null) {
			AdoptionSubsidy adoptionSubsidy = new AdoptionSubsidy();
			if (pInputDataRec.getIdAdptSub() != 0) {
				adoptionSubsidy = (AdoptionSubsidy) sessionFactory.getCurrentSession().get(AdoptionSubsidy.class,
						pInputDataRec.getIdAdptSub());
				if (TypeConvUtil.isNullOrEmpty(adoptionSubsidy)) {
					throw new DataNotFoundException(messageSource
							.getMessage("Caud81dDaoImpl.AdoptionSubsidy.not.found.IdAdptSubId", null, Locale.US));
				}
			}
			if (null != pInputDataRec.getTsLastUpdate()) {
				adoptionSubsidy.setDtLastUpdate(pInputDataRec.getTsLastUpdate());
			}
			sessionFactory.getCurrentSession().delete(adoptionSubsidy);
		}
	}

	/**
	 * 
	 * Method Name: updateAdoptionsubsidy Method Description: update
	 * Adoptionsubsidy for a person
	 * 
	 * @param pInputDataRec
	 */
	public void updateAdoptionsubsidy(AdoptionSubsidyInsUpdDelInDto pInputDataRec) {
		if (pInputDataRec != null) {
			Date minDate = pInputDataRec.getTsLastUpdate();
			Date maxDate = new Date(minDate.getTime() + TimeUnit.DAYS.toMillis(1));
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AdoptionSubsidy.class);
			criteria.add(Restrictions.eq("idAdptSub", pInputDataRec.getIdAdptSub()));
			criteria.add(Restrictions.ge("dtLastUpdate", pInputDataRec.getTsLastUpdate()));
			criteria.add(Restrictions.lt("dtLastUpdate", maxDate));
			AdoptionSubsidy adoptionSubsidy = (AdoptionSubsidy) criteria.uniqueResult();
			if (TypeConvUtil.isNullOrEmpty(adoptionSubsidy)) {
				throw new DataNotFoundException(messageSource
						.getMessage("Caud81dDaoImpl.AdoptionSubsidy.not.found.IdAdptSubId", null, Locale.US));
			}
			if (!ObjectUtils.isEmpty(pInputDataRec.getDtAdptSubAppReturned())) {
				adoptionSubsidy.setDtAdptSubAppReturned(pInputDataRec.getDtAdptSubAppReturned());
			}
			if (!ObjectUtils.isEmpty(pInputDataRec.getCdAdptSubDeterm())) {
				adoptionSubsidy.setCdAdptSubDeterm(pInputDataRec.getCdAdptSubDeterm());
			}
			if (!ObjectUtils.isEmpty(pInputDataRec.getIndAdptSubThirdParty())) {
				adoptionSubsidy.setIndAdptSubThirdParty(pInputDataRec.getIndAdptSubThirdParty());
			}
			if (0 != pInputDataRec.getAdptSubPerson()) {
				Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
						Long.valueOf(pInputDataRec.getAdptSubPerson()));
				if (TypeConvUtil.isNullOrEmpty(person)) {
					throw new DataNotFoundException(messageSource
							.getMessage("Caud81dDaoImpl.AdoptionSubsidy.not.found.personId", null, Locale.US));
				}
				adoptionSubsidy.setPerson(person);
			}
			if (!ObjectUtils.isEmpty(pInputDataRec.getDtAdptSubAgreeSent())) {
				adoptionSubsidy.setDtAdptSubAgreeSent(pInputDataRec.getDtAdptSubAgreeSent());
			}
			if (!ObjectUtils.isEmpty(pInputDataRec.getAdptSubRsn())) {
				adoptionSubsidy.setTxtAdptSubRsn(pInputDataRec.getAdptSubRsn());
			}
			if (0 != (int) pInputDataRec.getAmtAdptSub()) {
				adoptionSubsidy.setAmtAdptSub(BigDecimal.valueOf(pInputDataRec.getAmtAdptSub()));
			}
			if (!ObjectUtils.isEmpty(pInputDataRec.getDtAdptSubApprvd())) {
				adoptionSubsidy.setDtAdptSubApprvd(pInputDataRec.getDtAdptSubApprvd());
			}
			if (!ObjectUtils.isEmpty(pInputDataRec.getDtAdptSubLastInvc())) {
				adoptionSubsidy.setDtAdptSubLastInvc(pInputDataRec.getDtAdptSubLastInvc());
			}
			if (0 != pInputDataRec.getIdPlcmtEvent()) {
				Placement placement = (Placement) sessionFactory.getCurrentSession().get(Placement.class,
						pInputDataRec.getIdPlcmtEvent());
				if (TypeConvUtil.isNullOrEmpty(placement)) {
					throw new DataNotFoundException(messageSource
							.getMessage("Caud81dDaoImpl.AdoptionSubsidy.not.found.placementId", null, Locale.US));
				}
				adoptionSubsidy.setPlacement(placement);
			}
			if (!ObjectUtils.isEmpty(pInputDataRec.getDtAdptSubEnd())) {
				adoptionSubsidy.setDtAdptSubEnd(pInputDataRec.getDtAdptSubEnd());
			}
			if (!ObjectUtils.isEmpty(pInputDataRec.getDtAdptSubAppSent())) {
				adoptionSubsidy.setDtAdptSubAppSent(pInputDataRec.getDtAdptSubAppSent());
			}
			if (!ObjectUtils.isEmpty(pInputDataRec.getDtAdptSubEffective())) {
				adoptionSubsidy.setDtAdptSubEffective(pInputDataRec.getDtAdptSubEffective());
			}
			if (!ObjectUtils.isEmpty(pInputDataRec.getCdAdptSubCloseRsn())) {
				adoptionSubsidy.setCdAdptSubCloseRsn(pInputDataRec.getCdAdptSubCloseRsn());
			}
			if (!ObjectUtils.isEmpty(pInputDataRec.getIndAdptSubProcess())) {
				adoptionSubsidy.setIndAdptSubProcess(pInputDataRec.getIndAdptSubProcess());
			}
			if (0 != pInputDataRec.getIdAdptSubPayee()) {
				CapsResource capsResource = (CapsResource) sessionFactory.getCurrentSession().get(CapsResource.class,
						Long.valueOf(pInputDataRec.getIdAdptSubPayee()));
				if (TypeConvUtil.isNullOrEmpty(capsResource)) {
					throw new DataNotFoundException(messageSource
							.getMessage("Caud81dDaoImpl.AdoptionSubsidy.not.found.capsResourceId", null, Locale.US));
				}
				adoptionSubsidy.setCapsResource(capsResource);
			}
			if (!ObjectUtils.isEmpty(pInputDataRec.getDtAdptSubAgreeRetn())) {
				adoptionSubsidy.setDtAdptSubAgreeRetn(pInputDataRec.getDtAdptSubAgreeRetn());
			}
			sessionFactory.getCurrentSession().saveOrUpdate(adoptionSubsidy);
		}
	}

	/**
	 * 
	 * Method Name: saveAdoptionsubsidy Method Description: save Adoptionsubsidy
	 * for a person
	 * 
	 * @param pInputDataRec
	 */
	public void saveAdoptionsubsidy(AdoptionSubsidyInsUpdDelInDto pInputDataRec) {
		if (pInputDataRec != null) {
			AdoptionSubsidy adoptionSubsidy = new AdoptionSubsidy();
			if (null != pInputDataRec.getDtAdptSubAppReturned()) {
				adoptionSubsidy.setDtAdptSubAppReturned(pInputDataRec.getDtAdptSubAppReturned());
			}
			if (null != pInputDataRec.getCdAdptSubDeterm()) {
				adoptionSubsidy.setCdAdptSubDeterm(pInputDataRec.getCdAdptSubDeterm());
			}
			if (null != pInputDataRec.getIndAdptSubThirdParty()) {
				adoptionSubsidy.setIndAdptSubThirdParty(pInputDataRec.getIndAdptSubThirdParty());
			}
			if (0 != pInputDataRec.getAdptSubPerson()) {
				Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
						Long.valueOf(pInputDataRec.getAdptSubPerson()));
				if (TypeConvUtil.isNullOrEmpty(person)) {
					throw new DataNotFoundException(messageSource
							.getMessage("Caud81dDaoImpl.AdoptionSubsidy.not.found.personId", null, Locale.US));
				}
				adoptionSubsidy.setPerson(person);
			}
			if (null != pInputDataRec.getDtAdptSubAgreeSent()) {
				adoptionSubsidy.setDtAdptSubAgreeSent(pInputDataRec.getDtAdptSubAgreeSent());
			}
			if (null != pInputDataRec.getAdptSubRsn()) {
				adoptionSubsidy.setTxtAdptSubRsn(pInputDataRec.getAdptSubRsn());
			}
			if (null != pInputDataRec.getTsLastUpdate()) {
				adoptionSubsidy.setDtLastUpdate(pInputDataRec.getTsLastUpdate());
			}
			if (0 != (int) pInputDataRec.getAmtAdptSub()) {
				adoptionSubsidy.setAmtAdptSub(BigDecimal.valueOf(pInputDataRec.getAmtAdptSub()));
			}
			if (null != pInputDataRec.getDtAdptSubApprvd()) {
				adoptionSubsidy.setDtAdptSubApprvd(pInputDataRec.getDtAdptSubApprvd());
			}
			if (null != pInputDataRec.getDtAdptSubLastInvc()) {
				adoptionSubsidy.setDtAdptSubLastInvc(pInputDataRec.getDtAdptSubLastInvc());
			}
			if (0 != pInputDataRec.getIdPlcmtEvent()) {
				Placement placement = (Placement) sessionFactory.getCurrentSession().get(Placement.class,
						Long.valueOf(pInputDataRec.getIdPlcmtEvent()));
				if (TypeConvUtil.isNullOrEmpty(placement)) {
					throw new DataNotFoundException(messageSource
							.getMessage("Caud81dDaoImpl.AdoptionSubsidy.not.found.placementId", null, Locale.US));
				}
				adoptionSubsidy.setPlacement(placement);
			}
			if (null != pInputDataRec.getDtAdptSubEnd()) {
				adoptionSubsidy.setDtAdptSubEnd(pInputDataRec.getDtAdptSubEnd());
			}
			if (null != pInputDataRec.getDtAdptSubAppSent()) {
				adoptionSubsidy.setDtAdptSubAppSent(pInputDataRec.getDtAdptSubAppSent());
			}
			if (null != pInputDataRec.getDtAdptSubEffective()) {
				adoptionSubsidy.setDtAdptSubEffective(pInputDataRec.getDtAdptSubEffective());
			}
			if (null != pInputDataRec.getCdAdptSubCloseRsn()) {
				adoptionSubsidy.setCdAdptSubCloseRsn(pInputDataRec.getCdAdptSubCloseRsn());
			}
			if (null != pInputDataRec.getIndAdptSubProcess()) {
				adoptionSubsidy.setIndAdptSubProcess(pInputDataRec.getIndAdptSubProcess());
			}
			if (0 != pInputDataRec.getIdAdptSubPayee()) {
				CapsResource capsResource = (CapsResource) sessionFactory.getCurrentSession().get(CapsResource.class,
						Long.valueOf(pInputDataRec.getIdAdptSubPayee()));
				if (TypeConvUtil.isNullOrEmpty(capsResource)) {
					throw new DataNotFoundException(messageSource
							.getMessage("Caud81dDaoImpl.AdoptionSubsidy.not.found.capsResourceId", null, Locale.US));
				}
				adoptionSubsidy.setCapsResource(capsResource);
			}
			if (null != pInputDataRec.getDtAdptSubAgreeRetn()) {
				adoptionSubsidy.setDtAdptSubAgreeRetn(pInputDataRec.getDtAdptSubAgreeRetn());
			}
			sessionFactory.getCurrentSession().save(adoptionSubsidy);
		}
	}
}
