package us.tx.state.dfps.service.person.daoimpl;

import static us.tx.state.dfps.service.approval.serviceimpl.ApprovalStatusServiceImpl.KINSHIP_AUTOMATED_SYSTEM_ID;
import static us.tx.state.dfps.service.common.ServiceConstants.STRING_IND_N;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.CapsResource;
import us.tx.state.dfps.common.domain.FaIndivTraining;
import us.tx.state.dfps.common.domain.ResourceService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.kin.dto.KinHomeInfoDto;
import us.tx.state.dfps.service.kin.dto.ResourceServiceDto;
import us.tx.state.dfps.service.person.dao.ResourceServiceDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ResourceServiceDaoImpl May 14, 2018- 8:44:08 PM © 2018 Texas
 * Department of Family and Protective Services
 */
@Repository
public class ResourceServiceDaoImpl implements ResourceServiceDao {
	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	@Value("${ResourceServiceDaoImpl.getKinTrainCompleted}")
	private String getKinTrainCompletedSql;

	@Override
	public List<FaIndivTraining> getFATrainingList(long personId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FaIndivTraining.class);
		criteria.add(Restrictions.eq("person.idPerson", personId));
		List<FaIndivTraining> faIndivTrainings = criteria.list();
		return faIndivTrainings;
	}

	@Override
	public int saveFATrainingRecord(FaIndivTraining faIndivTraining) {
		return (int) sessionFactory.getCurrentSession().save(faIndivTraining);
	}

	/**
	 * Method Name: updateResourceService Method Description: The following
	 * method updates IND_KNSHP_TRAINING, IND_KNSHP_HOME_ASSMNT,
	 * IND_KNSHP_INCOME, IND_KNSHP_AGREEMENT
	 * 
	 * @param resourceServiceDto
	 * @return Long
	 * @throws DataNotFoundException
	 */

	@Override
	public Long updateResourceService(ResourceServiceDto resourceServiceDto) throws DataNotFoundException {
		Long noOfAffected = ServiceConstants.ZERO_VAL;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ResourceService.class);
		criteria.add(Restrictions.eq("capsResource.idResource", Long.valueOf(resourceServiceDto.getIdResource())));
		criteria.add(Restrictions.eq("cdRsrcSvcService", resourceServiceDto.getCdRsrcSvcService()));
		if ((!TypeConvUtil.isNullOrEmpty(resourceServiceDto))
				&& (!TypeConvUtil.isNullOrEmpty(resourceServiceDto.getCdRsrcSvcCnty()))
				&& (!(ServiceConstants.EMPTY_STRING).equalsIgnoreCase(resourceServiceDto.getCdRsrcSvcCnty()))) {
			criteria.add(Restrictions.eq("cdRsrcSvcCnty", resourceServiceDto.getCdRsrcSvcCnty()));
		} else {
			criteria.add(Restrictions.eq("cdRsrcSvcCnty", ServiceConstants.NULL_VALUE));
		}
		ResourceService resourceService = (ResourceService) criteria.uniqueResult();
		if (TypeConvUtil.isNullOrEmpty(resourceService)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		} else {
			noOfAffected = ServiceConstants.ONE_VAL;
		}

		if ((!TypeConvUtil.isNullOrEmpty(resourceServiceDto))
				&& (!TypeConvUtil.isNullOrEmpty(resourceServiceDto.getIndKinshipTraining()))
				&& (!(ServiceConstants.EMPTY_STRING).equalsIgnoreCase(resourceServiceDto.getIndKinshipTraining()))) {
			resourceService
					.setIndKnshpTraining(resourceServiceDto.getIndKinshipTraining().charAt(ServiceConstants.Zero));
		} else {
			resourceService.setIndKnshpTraining(null);
		}

		if ((!TypeConvUtil.isNullOrEmpty(resourceServiceDto))
				&& (!TypeConvUtil.isNullOrEmpty(resourceServiceDto.getIndKinshipHomeAssmnt()))
				&& (!(ServiceConstants.EMPTY_STRING).equalsIgnoreCase(resourceServiceDto.getIndKinshipHomeAssmnt()))) {
			resourceService
					.setIndKnshpHomeAssmnt(resourceServiceDto.getIndKinshipHomeAssmnt().charAt(ServiceConstants.Zero));
		} else {
			resourceService.setIndKnshpHomeAssmnt(null);
		}

		if ((!TypeConvUtil.isNullOrEmpty(resourceServiceDto))
				&& (!TypeConvUtil.isNullOrEmpty(resourceServiceDto.getIndKinshipIncome()))
				&& (!(ServiceConstants.EMPTY_STRING).equalsIgnoreCase(resourceServiceDto.getIndKinshipIncome()))) {
			resourceService.setIndKnshpIncome(resourceServiceDto.getIndKinshipIncome().charAt(ServiceConstants.Zero));
		} else {
			resourceService.setIndKnshpIncome(null);
		}

		if ((!TypeConvUtil.isNullOrEmpty(resourceServiceDto))
				&& (!TypeConvUtil.isNullOrEmpty(resourceServiceDto.getIndKinshipAgreement()))
				&& (!(ServiceConstants.EMPTY_STRING).equalsIgnoreCase(resourceServiceDto.getIndKinshipAgreement()))) {
			resourceService
					.setIndKnshpAgreement(resourceServiceDto.getIndKinshipAgreement().charAt(ServiceConstants.Zero));
		} else {
			resourceService.setIndKnshpAgreement(null);
		}
		resourceService.setDtLastUpdate(new Date());
		resourceService.setLastUpdatedPersonId(KINSHIP_AUTOMATED_SYSTEM_ID);

		sessionFactory.getCurrentSession().saveOrUpdate(resourceService);

		return noOfAffected;
	}

	/**
	 * Method Name:getResourceService Method Description: Selects from
	 * RESOURCE_SERVICE table
	 * 
	 * @param resourceServiceDto
	 * @return ResourceServiceDto
	 * @throws DataNotFoundException
	 */

	@Override
	public ResourceServiceDto getResourceService(ResourceServiceDto resourceServiceDto) throws DataNotFoundException {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ResourceService.class);

		if (resourceServiceDto.getIdResource() >= ServiceConstants.ZERO_VAL) {
			criteria.add(Restrictions.eq("capsResource.idResource", Long.valueOf(resourceServiceDto.getIdResource())));
		} else {
			criteria.add(Restrictions.eq("capsResource.idResource", ServiceConstants.NULL_VALUE));
		}

		if ((!TypeConvUtil.isNullOrEmpty(resourceServiceDto))
				&& (!TypeConvUtil.isNullOrEmpty(resourceServiceDto.getCdRsrcSvcService()))
				&& (!(ServiceConstants.EMPTY_STRING).equalsIgnoreCase(resourceServiceDto.getCdRsrcSvcService()))) {
			criteria.add(Restrictions.eq("cdRsrcSvcService", resourceServiceDto.getCdRsrcSvcService()));
		} else {
			criteria.add(Restrictions.eq("cdRsrcSvcService", ServiceConstants.NULL_VALUE));
		}
		if ((!TypeConvUtil.isNullOrEmpty(resourceServiceDto))
				&& (!TypeConvUtil.isNullOrEmpty(resourceServiceDto.getCdRsrcSvcCnty()))
				&& (!(ServiceConstants.EMPTY_STRING).equalsIgnoreCase(resourceServiceDto.getCdRsrcSvcCnty()))) {
			criteria.add(Restrictions.eq("cdRsrcSvcCnty", resourceServiceDto.getCdRsrcSvcCnty()));
		} else {
			criteria.add(Restrictions.eq("cdRsrcSvcCnty", ServiceConstants.NULL_VALUE));
		}

		ResourceService resourceService = (ResourceService) criteria.uniqueResult();

		if (!ObjectUtils.isEmpty(resourceService)) {
			ResourceServiceDto returnBean = new ResourceServiceDto();
			returnBean.setIdResourceService(resourceService.getIdResourceService());
			returnBean.setDtLastUpdate(resourceService.getDtLastUpdate());
			returnBean.setCreatedDate(resourceService.getCreatedDate());
			returnBean.setCreatedPersonId(resourceService.getCreatedPersonId());
			returnBean.setLastUpdatedPersonId(resourceService.getLastUpdatedPersonId());
			returnBean.setIdResource(resourceService.getCapsResource().getIdResource());
			returnBean.setIndRsrcSvcShowRow(resourceService.getIndRsrcSvcShowRow());
			returnBean.setIndRsrcSvcIncomeBsed(resourceService.getIndRsrcSvcIncomeBsed());
			returnBean.setCdRsrcSvcCategRsrc(resourceService.getCdRsrcSvcCategRsrc());
			returnBean.setCdRsrcSvcCnty(resourceService.getCdRsrcSvcCnty());
			returnBean.setCdRsrcSvcProgram(resourceService.getCdRsrcSvcProgram());
			returnBean.setCdRsrcSvcRegion(resourceService.getCdRsrcSvcRegion());
			returnBean.setCdRsrcSvcService(resourceService.getCdRsrcSvcService());
			returnBean.setCdRsrcSvcState(resourceService.getCdRsrcSvcState());
			returnBean.setIndRsrcSvcCntyPartial(resourceService.getIndRsrcSvcCntyPartial());
			returnBean.setIndKinshipTraining(
					!ObjectUtils.isEmpty(resourceService.getIndKnshpTraining()) ?
							String.valueOf(resourceService.getIndKnshpTraining()) : null);
			returnBean.setIndKinshipHomeAssmnt(
					!ObjectUtils.isEmpty(resourceService.getIndKnshpHomeAssmnt()) ?
							String.valueOf(resourceService.getIndKnshpHomeAssmnt()) : null);
			returnBean.setIndKinshipIncome(
					!ObjectUtils.isEmpty(resourceService.getIndKnshpIncome()) ?
							String.valueOf(resourceService.getIndKnshpIncome()) : null);
			returnBean.setIndKinshipAgreement(
					!ObjectUtils.isEmpty(resourceService.getIndKnshpAgreement()) ?
							String.valueOf(resourceService.getIndKnshpAgreement()) : null);

			return returnBean;
		}

		return null;
	}

	/**
	 * Method Name: getKinTrainCompleted Method Description:Selects from
	 * FA_INDIV_TRAINING , STAGE_PERSON_LINK tables.
	 * 
	 * @param kinHomeInfoDto
	 * @return String
	 * @throws DataNotFoundException
	 */

	@Override
	public String getKinTrainCompleted(KinHomeInfoDto kinHomeInfoDto) throws DataNotFoundException {
		String indTrain = ServiceConstants.EMPTY_STRING;
		SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getKinTrainCompletedSql));
		if (kinHomeInfoDto.getIdHomeStage() >= ServiceConstants.ZERO_VAL) {
			sQLQuery.setParameter("idStage", kinHomeInfoDto.getIdHomeStage());
		} else {
			sQLQuery.setParameter("idStage", null);
		}
		indTrain = (String) sQLQuery.uniqueResult();
		return !ObjectUtils.isEmpty(indTrain) ? indTrain : STRING_IND_N;
	}

	/**
	 * Method Name: insertResourceService Method Description:insert the values
	 * in resource_service table
	 * 
	 * @param resourceServiceDto
	 * @return Long
	 * @throws DataNotFoundException
	 */

	@Override
	public Long insertResourceService(ResourceServiceDto resourceServiceDto) throws DataNotFoundException {
		ResourceService resourceService = new ResourceService();
		resourceService.setIndKnshpTraining(!TypeConvUtil.isNullOrEmpty(resourceServiceDto.getIndKinshipTraining()) ? resourceServiceDto.getIndKinshipTraining().charAt(ServiceConstants.Zero) : null);
		resourceService
				.setIndKnshpHomeAssmnt(resourceServiceDto.getIndKinshipHomeAssmnt().charAt(ServiceConstants.Zero));
		resourceService.setIndKnshpIncome(!StringUtils.isEmpty(resourceServiceDto.getIndKinshipIncome())?resourceServiceDto.getIndKinshipIncome().charAt(ServiceConstants.Zero):null);
		resourceService.setIndKnshpAgreement(resourceServiceDto.getIndKinshipAgreement().charAt(ServiceConstants.Zero));
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CapsResource.class);
		criteria.add(Restrictions.eq("idResource", Long.valueOf(resourceServiceDto.getIdResource())));
		CapsResource capsResource = (CapsResource) criteria.uniqueResult();
		if (TypeConvUtil.isNullOrEmpty(capsResource)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Cres42diDto.capsResource.not.found", null, Locale.US));
		}
		resourceService.setCapsResource(capsResource);
		resourceService.setCdRsrcSvcCategRsrc(resourceServiceDto.getCdRsrcSvcCategRsrc());
		resourceService.setCdRsrcSvcCnty(resourceServiceDto.getCdRsrcSvcCnty());
		resourceService.setCdRsrcSvcProgram(resourceServiceDto.getCdRsrcSvcProgram());
		resourceService.setCdRsrcSvcRegion(resourceServiceDto.getCdRsrcSvcRegion());
		resourceService.setCdRsrcSvcService(resourceServiceDto.getCdRsrcSvcService());
		resourceService.setCdRsrcSvcState(resourceServiceDto.getCdRsrcSvcState());
		resourceService.setIndRsrcSvcCntyPartial(resourceServiceDto.getIndRsrcSvcCntyPartial());
		resourceService.setIndRsrcSvcIncomeBsed(resourceServiceDto.getIndRsrcSvcIncomeBsed());
		resourceService.setIndRsrcSvcShowRow(resourceServiceDto.getIndRsrcSvcShowRow());
		resourceService.setCreatedDate(new Date());
		resourceService.setCreatedPersonId(KINSHIP_AUTOMATED_SYSTEM_ID);
		resourceService.setDtLastUpdate(new Date());
		resourceService.setLastUpdatedPersonId(KINSHIP_AUTOMATED_SYSTEM_ID);
		sessionFactory.getCurrentSession().save(resourceService);
		return (long) resourceServiceDto.getIdResource();
	}

}
