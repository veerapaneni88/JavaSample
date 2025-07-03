package us.tx.state.dfps.service.financial.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.ApsOutcomeMatrix;
import us.tx.state.dfps.common.domain.CapsResource;
import us.tx.state.dfps.common.domain.Contract;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.ServiceAuthorization;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.service.casepackage.dto.ServiceAuthorizationHeaderDto;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.financial.dao.ServiceAuthHeaderDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.placement.dao.ContractDao;
import us.tx.state.dfps.service.subcare.dao.CapsResourceDao;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.service.servicedlvryclosure.dto.OutcomeMatrixDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Dao
 * implementation for all service calls in service authorization Header> June
 * 27, 2018- 3:05:39 PM © 2017 Texas Department of Family and Protective
 * Services.
 */
@Repository
public class ServiceAuthHeaderDaoImpl implements ServiceAuthHeaderDao {

	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private StageDao stageDao;
	@Autowired
	private PersonDao personDao;
	@Autowired
	private CapsResourceDao capsResourceDao;
	@Autowired
	private ContractDao contractDao;

	@Value("${ServiceAuthHeaderDaoImpl.getTotalCompletedSFIAuths}")
	private String getTotalCompletedSFIAuthsSql;

	@Value("${ServiceAuthHeaderDaoImpl.deleteServiceAuth}")
	private String deleteServiceAuth;

	/**
	 * Method name: getOutcomeMatrixListBystageId Method Description: retrieve
	 * OUTCOME MATRIX LIST FOR GIVEN STAGE ID
	 * 
	 * @param idStage
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<OutcomeMatrixDto> getOutcomeMatrixListBystageId(Long idStage) {
		List<OutcomeMatrixDto> outcomeMatrixDtoList = new ArrayList<>();
		StageDto stageDto = stageDao.getStageById(idStage);
		Criteria apsOutComeMatrixList = sessionFactory.getCurrentSession().createCriteria(ApsOutcomeMatrix.class)
				.add(Restrictions.eq("idCase", stageDto.getIdCase()));
		List<ApsOutcomeMatrix> apsOutcomeMatrix = apsOutComeMatrixList.list();
		apsOutcomeMatrix.forEach(o -> {
			OutcomeMatrixDto outcomeMatrixDto = new OutcomeMatrixDto();
			BeanUtils.copyProperties(o, outcomeMatrixDto);
			outcomeMatrixDtoList.add(outcomeMatrixDto);
		});
		return outcomeMatrixDtoList;
	}

	/**
	 * Method Name: getDtSituationOpened Method Description: retrieves situation
	 * opened date from Stage entity
	 * 
	 * @param idStage
	 * @return
	 */
	@Override
	public Date getDtSituationOpened(Long idStage) {
		Stage stage = stageDao.getStageEntityById(idStage);
		return stage.getSituation().getDtSituationOpened();
	}

	/**
	 * calls the DELETE_SERVICE_AUTH procedure in the COMPLEX_DELETE package,
	 * given an ID_SVC_AUTH
	 * 
	 * @param idSvcAuth
	 * @return
	 */
	@Override
	public Long deleteServiceAuth(Long idSvcAuth) {
		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(deleteServiceAuth)
				.setParameter("idSvcAuth", idSvcAuth);
		Long value = (long) sQLQuery1.executeUpdate();
		return value;
	}

	/**
	 * Method Name: saveServiceAuthorization Method Description: This method is
	 * used to save the service Authorization Header information
	 * 
	 * @param serviceAuthorizationHeaderDto
	 * @param idPerson
	 *            CAUD33D
	 */
	@Override
	public Long saveServiceAuthorization(ServiceAuthorizationHeaderDto serviceAuthorizationHeaderDto, Long idPerson) {
		ServiceAuthorization serviceAuthorization;
		// retrieve the service authorization
		if (!ObjectUtils.isEmpty(serviceAuthorizationHeaderDto.getIdSvcAuth())
				&& serviceAuthorizationHeaderDto.getIdSvcAuth() != 0) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ServiceAuthorization.class)
					.add(Restrictions.eq("idSvcAuth", serviceAuthorizationHeaderDto.getIdSvcAuth().longValue()));
			serviceAuthorization = (ServiceAuthorization) criteria.uniqueResult();
		} else {
			serviceAuthorization = new ServiceAuthorization();
		}
		// retrieves the person information
		if (!ObjectUtils.isEmpty(serviceAuthorizationHeaderDto.getPhysicianPersonId())) {
			Person personByIdPerson = personDao.getPersonByPersonId(serviceAuthorizationHeaderDto.getPhysicianPersonId());
			serviceAuthorization.setPersonByIdPerson(personByIdPerson);
		}
		// retrieves the person information using primary client
		Person personByIdPrimaryClient = personDao
				.getPersonByPersonId(Long.valueOf(serviceAuthorizationHeaderDto.getPrimaryClient()));
		serviceAuthorization.setPersonByIdPrimaryClient(personByIdPrimaryClient);
		// retrieves the resouce based on idResource.
		CapsResource capsResource = capsResourceDao
				.getCapsResourceById(serviceAuthorizationHeaderDto.getIdResource().longValue());
		serviceAuthorization.setCapsResource(capsResource);
		// retrieves the contract based on idContract.
		Contract contract = contractDao.getContractById(serviceAuthorizationHeaderDto.getIdContract());
		serviceAuthorization.setContract(contract);
		serviceAuthorization.setDtLastUpdate(new Date());
		serviceAuthorization.setCdSvcAuthCounty(serviceAuthorizationHeaderDto.getCdSvcAuthCounty());
		serviceAuthorization.setCdSvcAuthCategory(serviceAuthorizationHeaderDto.getCdSvcAuthCategory());
		serviceAuthorization.setCdSvcAuthRegion(serviceAuthorizationHeaderDto.getCdSvcAuthRegion());
		serviceAuthorization.setCdSvcAuthService(serviceAuthorizationHeaderDto.getCdSvcAuthService());
		serviceAuthorization.setIndSvcAuthComplete(serviceAuthorizationHeaderDto.getIndSvcAuthComplete());
		serviceAuthorization.setTxtSvcAuthComments(serviceAuthorizationHeaderDto.getCommentsSerAuthHeader());
		serviceAuthorization.setTxtSvcAuthSecProvdr(serviceAuthorizationHeaderDto.getPreferredSubCon());
		serviceAuthorization.setDtSvcAuthEff(serviceAuthorizationHeaderDto.getDtSvcAuthEff());
		serviceAuthorization.setIndDontdComntySvc(serviceAuthorizationHeaderDto.getIndDntdCmmtySvc());
		//Set the Service Authorization APS details
		serviceAuthorization.setDtSvcAuthVerbalReferl(serviceAuthorizationHeaderDto.getTxtDtDtSvcAuthVerbalReferl());
		serviceAuthorization.setTxtSvcAuthDirToHome(serviceAuthorizationHeaderDto.getTxtSzTxtDirectToHome());
		serviceAuthorization.setTxtSvcAuthHomeEnviron(serviceAuthorizationHeaderDto.getTxtSzTxtHomeEnviron());
		serviceAuthorization.setTxtSvcAuthMedCond(serviceAuthorizationHeaderDto.getTxtSzTxtMedicalConditions());
		serviceAuthorization.setCdSvcAuthAbilToRespond(serviceAuthorizationHeaderDto.getSelSzCdSvcAuthAbilToRespond());
		sessionFactory.getCurrentSession().saveOrUpdate(serviceAuthorization);
		return serviceAuthorization.getIdSvcAuth();
	}

	/**
	 * 
	 * Method Name: getDtLastUpdateForServAuthHeader Method Description: This
	 * method gets the latest last update date for service Authorization
	 * 
	 * @param idSvcAuth
	 * @return
	 */
	@Override
	public Date getDtLastUpdateForServAuthHeader(Long idSvcAuth) {
		ServiceAuthorization serviceAuthorization = (ServiceAuthorization) sessionFactory.getCurrentSession()
				.load(ServiceAuthorization.class, idSvcAuth);
		return serviceAuthorization.getDtLastUpdate();
	}
}
