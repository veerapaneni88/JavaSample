package us.tx.state.dfps.service.placement.daoimpl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CapsResource;
import us.tx.state.dfps.common.domain.Contract;
import us.tx.state.dfps.common.domain.ContractPeriod;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.ResourceAddress;
import us.tx.state.dfps.common.dto.ContractDto;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.common.response.ContractPeriodAUDRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.placement.dao.ContractDao;
import us.tx.state.dfps.service.placement.dto.ContractContractPeriodInDto;
import us.tx.state.dfps.service.placement.dto.ContractContractPeriodOutDto;
import us.tx.state.dfps.service.placement.dto.ContractCountyOutDto;
import us.tx.state.dfps.service.placement.dto.ContractCountyPeriodInDto;
import us.tx.state.dfps.service.placement.dto.ContractCountyPeriodOutDto;
import us.tx.state.dfps.service.resource.dto.ContractPeriodDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION
 * 
 * Class Description:It retrieves a single row from the Person_Loc table
 * 
 * Aug 18, 2017- 12:06:30 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@Repository
public class ContractDaoImpl implements ContractDao {

	private static final String CD_CNSVC_SERVICE = "'64X', '66F', '66G', '66H', '66L', '66M', '66N', '66P', '64O', '64Y'";

	private static final String ACT = "ACT";

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ContractDaoImpl.getContractCountyPeriodInfo}")
	private String getContractCountyPeriodInfo;

	@Value("${ContractDaoImpl.getContractCounty}")
	private String getContractCounty;
	
	@Value("${ContractDaoImpl.getContractCountyWithoutService}")
	private String getContractCountyWithoutService;
	

	@Value("${ContractDaoImpl.getContractContractPeriod}")
	private String getContractContractPeriod;

	@Value("${ContractDaoImpl.getContractIdByVendorAndService}")
	private String getContractIdByVendorAndService;

	@Value("${ContractDaoImpl.getContractIdByVendor}")
	private String getContractIdByVendor;

	@Value("${ContractDaoImpl.getCountyContracted}")
	private String getCountyContracted;

	@Value("${ContractDaoImpl.getCntrctByIdPeriodAndStatus}")
	private String getCntrctByIdPeriodAndStatus;

	@Value("${ContractDaoImpl.getContractNextValSql}")
	private String getContractNextVal;

	@Value("${ContractDaoImpl.strQuery2}")
	private String strQuery2;

	@Value("${ContractDaoImpl.strQuery3}")
	private String strQuery3;

	@Value("${ContractDaoImpl.strQuery4}")
	private String strQuery4;

	@Value("${ContractDaoImpl.contractPeriodAUDsQLQuery1}")
	private String contractPeriodAUDsQLQuery1;

	@Value("${ContractDaoImpl.contractPeriodAUDAddSql}")
	private String contractPeriodAUDAddSql;

	@Value("${ContractDaoImpl.contractPeriodAUDUpdateSql}")
	private String contractPeriodAUDUpdateSql;

	@Value("${ContractDaoImpl.getIdContractForResourceId}")
	private String getIdContractForResourceIdSql;

	@Value("${ContractDaoImpl.getLatestCPForResourceId}")
	private String getLatestCPForResourceIdSql;

	/*
	 * @Value("${ContractDaoImpl.contractPeriodAUDDeleteSql1}") private String
	 * contractPeriodAUDDeleteSql1;
	 * 
	 * @Value("${ContractDaoImpl.contractPeriodAUDDeleteSql2}") private String
	 * contractPeriodAUDDeleteSql2;
	 * 
	 * @Value("${ContractDaoImpl.contractPeriodAUDDeleteSql3}") private String
	 * contractPeriodAUDDeleteSql3;
	 * 
	 * @Value("${ContractDaoImpl.contractPeriodAUDDeleteSql4}") private String
	 * contractPeriodAUDDeleteSql4;
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.placement.dao.ContractDao#csec26dQUERYdam(us.tx.
	 * state.dfps.service.placement.dto.ContractCountyPeriodInDto)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ContractCountyPeriodOutDto getContractCountyPeriod(ContractCountyPeriodInDto pInputDataRec) {
		SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getContractCountyPeriodInfo)
				.addScalar("idContract", StandardBasicTypes.LONG).addScalar("idContractWrkr", StandardBasicTypes.LONG)
				.addScalar("nbrCnperPeriod", StandardBasicTypes.LONG)
				.addScalar("cdCnperStatus", StandardBasicTypes.STRING)
				.addScalar("dtCnperStart", StandardBasicTypes.DATE).addScalar("dtCnperTerm", StandardBasicTypes.DATE)
				.addScalar("dtCnperClosure", StandardBasicTypes.DATE)
				.addScalar("indCnperRenewal", StandardBasicTypes.STRING)
				.addScalar("indCnperSigned", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("idCncnty", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdateCnty", StandardBasicTypes.DATE)
				.addScalar("nbrCncntyPeriod", StandardBasicTypes.LONG)
				.addScalar("nbrCncntyVersion", StandardBasicTypes.LONG)
				.addScalar("nbrCncntyLineItem", StandardBasicTypes.LONG)
				.addScalar("cdCncntyCounty", StandardBasicTypes.STRING).addScalar("idResource", StandardBasicTypes.LONG)
				.addScalar("cdCncntyService", StandardBasicTypes.STRING)
				.addScalar("dtCncntyEffective", StandardBasicTypes.DATE)
				.addScalar("dtCncntyEnd", StandardBasicTypes.DATE)
				.setParameter("idResource", pInputDataRec.getIdResource())
				.setParameter("cdCncntyCounty", pInputDataRec.getCdCncntyCounty())
				.setParameter("cdCncntyService", pInputDataRec.getCdCncntyService())
				.setParameter("dtScrCurrentDate", pInputDataRec.getDtScrCurrentDate())
				.setResultTransformer(Transformers.aliasToBean(ContractCountyPeriodOutDto.class)));
		List<ContractCountyPeriodOutDto> contractCntyPeriodOutDtoList = (List<ContractCountyPeriodOutDto>) sQLQuery
				.list();
		if (!ObjectUtils.isEmpty(contractCntyPeriodOutDtoList)) {
			return contractCntyPeriodOutDtoList.get(0);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.placement.dao.ContractDao#getContarctCounty(us.
	 * tx.state.dfps.service.placement.dto.ContractCountyPeriodInDto)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<ContractCountyOutDto> getContarctCounty(ContractCountyPeriodInDto contractCntyPeriodInDto) {
		SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getContractCounty)
				.addScalar("idCncnty", StandardBasicTypes.LONG).addScalar("idContract", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdateCnty", StandardBasicTypes.DATE)
				.addScalar("nbrCncntyPeriod", StandardBasicTypes.LONG)
				.addScalar("nbrCncntyVersion", StandardBasicTypes.LONG)
				.addScalar("nbrCncntyLineItem", StandardBasicTypes.LONG)
				.addScalar("cdCncntyCounty", StandardBasicTypes.STRING)
				.addScalar("idCnctWorker", StandardBasicTypes.LONG).addScalar("idResource", StandardBasicTypes.LONG)
				.addScalar("cdCncntyService", StandardBasicTypes.STRING)
				.addScalar("dtCncntyEffective", StandardBasicTypes.DATE)
				.addScalar("dtCncntyEnd", StandardBasicTypes.DATE)
				.setParameter("idResource", contractCntyPeriodInDto.getIdResource())
				.setParameter("cdCncntyCounty", contractCntyPeriodInDto.getCdCncntyCounty())
				.setParameter("cdCncntyService", contractCntyPeriodInDto.getCdCncntyService())
				.setParameter("dtPlcmtStart", contractCntyPeriodInDto.getDtplcmtStart())
				.setResultTransformer(Transformers.aliasToBean(ContractCountyOutDto.class)));
		return (List<ContractCountyOutDto>) sQLQuery.list();

	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<ContractCountyOutDto> getContarctCountyWithoutService(ContractCountyPeriodInDto contractCntyPeriodInDto) {
		SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getContractCountyWithoutService)
				.addScalar("idCncnty", StandardBasicTypes.LONG).addScalar("idContract", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdateCnty", StandardBasicTypes.DATE)
				.addScalar("nbrCncntyPeriod", StandardBasicTypes.LONG)
				.addScalar("nbrCncntyVersion", StandardBasicTypes.LONG)
				.addScalar("nbrCncntyLineItem", StandardBasicTypes.LONG)
				.addScalar("cdCncntyCounty", StandardBasicTypes.STRING)
				.addScalar("idCnctWorker", StandardBasicTypes.LONG).addScalar("idResource", StandardBasicTypes.LONG)
				.addScalar("cdCncntyService", StandardBasicTypes.STRING)
				.addScalar("dtCncntyEffective", StandardBasicTypes.DATE)
				.addScalar("dtCncntyEnd", StandardBasicTypes.DATE)
				.setParameter("idResource", contractCntyPeriodInDto.getIdResource())
				.setParameter("cdCncntyCounty", contractCntyPeriodInDto.getCdCncntyCounty())
				.setParameter("dtPlcmtStart", contractCntyPeriodInDto.getDtplcmtStart())
				.setResultTransformer(Transformers.aliasToBean(ContractCountyOutDto.class)));
		return (List<ContractCountyOutDto>) sQLQuery.list();

	}

	@Override
	public Long getIdContractForResourceId(Long resourceId) {
		Long contractId = null;
		Query query = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(getIdContractForResourceIdSql)
				.addScalar("idContract", StandardBasicTypes.LONG)
				.setParameter("resourceId", resourceId));
		List<Long> contractIdList = (List<Long>) query.list();
		if (!ObjectUtils.isEmpty(contractIdList)) {
			contractId = contractIdList.get(0).longValue();
		}
		return contractId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.placement.dao.ContractDao#
	 * getContarctContractPeriod(us.tx.state.dfps.service.placement.dto.
	 * ContractContractPeriodInDto)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public ContractContractPeriodOutDto getContarctContractPeriod(
			ContractContractPeriodInDto contractContractPeriodInDto) {
		SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getContractContractPeriod)
				.addScalar("idContract", StandardBasicTypes.LONG).addScalar("idCntrctWkr", StandardBasicTypes.LONG)
				.addScalar("idCntrctManager", StandardBasicTypes.LONG).addScalar("idResource", StandardBasicTypes.LONG)
				.addScalar("idRsrcAddress", StandardBasicTypes.LONG)
				.addScalar("cdCntrctFuncType", StandardBasicTypes.STRING)
				.addScalar("cdCntrctProgramType", StandardBasicTypes.STRING)
				.addScalar("cdCntrctProcureType", StandardBasicTypes.STRING)
				.addScalar("cdCntrctRegion", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("indCntrctBudgLimit", StandardBasicTypes.STRING)
				.addScalar("nbrCnperPeriod", StandardBasicTypes.LONG)
				.addScalar("cdCnperStatus", StandardBasicTypes.STRING)
				.addScalar("dtCnperStart", StandardBasicTypes.DATE).addScalar("dtCnperTerm", StandardBasicTypes.DATE)
				.addScalar("dtCnperClosure", StandardBasicTypes.DATE)
				.addScalar("indCnperRenewal", StandardBasicTypes.STRING)
				.addScalar("indCnperSigned", StandardBasicTypes.STRING)
				.setParameter("idContract", contractContractPeriodInDto.getIdContract())
				.setParameter("nbrCnperPeriod", contractContractPeriodInDto.getNbrCnperPeriod())
				.setParameter("cdCnperStatusACT", ServiceConstants.CNPER_STATUS_ACT)
				.setParameter("cdCnperStatusCLS", ServiceConstants.CNPER_STATUS_CLS)
				.setParameter("cdCnperStatusCLT", ServiceConstants.CNPER_STATUS_CLT)
				.setParameter("cdCnperStatusPNT", ServiceConstants.CNPER_STATUS_PNT)
				.setParameter("cdCnperStatusPSH", ServiceConstants.CNPER_STATUS_PSH)
				.setParameter("cdCnperStatusPYH", ServiceConstants.CNPER_STATUS_PYH)
				.setParameter("cdCnperStatusSVH", ServiceConstants.CNPER_STATUS_SVH)
				.setResultTransformer(Transformers.aliasToBean(ContractContractPeriodOutDto.class)));
		List<ContractContractPeriodOutDto> contractContractPeriodOutDtoList = (List<ContractContractPeriodOutDto>) sQLQuery
				.list();
		if (!contractContractPeriodOutDtoList.isEmpty()) {
			return contractContractPeriodOutDtoList.get(0);
		}
		return null;
	}

	/**
	 * This method is used to get contract and contract period by contract id,
	 * period and status.
	 * 
	 * @param contractContractPeriodInDto
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public ContractContractPeriodOutDto getCntrctByIdPeriodAndStatus(
			ContractContractPeriodInDto contractContractPeriodInDto) {
		SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getCntrctByIdPeriodAndStatus)
				.addScalar("idContract", StandardBasicTypes.LONG).addScalar("idCntrctWkr", StandardBasicTypes.LONG)
				.addScalar("idCntrctManager", StandardBasicTypes.LONG).addScalar("idResource", StandardBasicTypes.LONG)
				.addScalar("idRsrcAddress", StandardBasicTypes.LONG)
				.addScalar("cdCntrctFuncType", StandardBasicTypes.STRING)
				.addScalar("cdCntrctProgramType", StandardBasicTypes.STRING)
				.addScalar("cdCntrctProcureType", StandardBasicTypes.STRING)
				.addScalar("cdCntrctRegion", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("indCntrctBudgLimit", StandardBasicTypes.STRING)
				.addScalar("nbrCnperPeriod", StandardBasicTypes.LONG)
				.addScalar("cdCnperStatus", StandardBasicTypes.STRING)
				.addScalar("dtCnperStart", StandardBasicTypes.DATE).addScalar("dtCnperTerm", StandardBasicTypes.DATE)
				.addScalar("dtCnperClosure", StandardBasicTypes.DATE)
				.addScalar("indCnperRenewal", StandardBasicTypes.STRING)
				.addScalar("indCnperSigned", StandardBasicTypes.STRING)
				.setParameter("idContract", contractContractPeriodInDto.getIdContract())
				.setParameter("nbrCnperPeriod", contractContractPeriodInDto.getNbrCnperPeriod())
				.setParameter("cdCnperStatusPND", ServiceConstants.CNPER_STATUS_PND)
				.setParameter("cdCnperStatusPSH", ServiceConstants.CNPER_STATUS_PSH)
				.setParameter("cdCnperStatusSVH", ServiceConstants.CNPER_STATUS_SVH)
				.setResultTransformer(Transformers.aliasToBean(ContractContractPeriodOutDto.class)));
		ContractContractPeriodOutDto contractContractPeriodOutDto = (ContractContractPeriodOutDto) sQLQuery
				.uniqueResult();
		return contractContractPeriodOutDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.placement.dao.ContractDao#
	 * getContractIdByVendorandPlcmtDate(java. lang.String, java.util.Date)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Long> getContractIdByVendorandPlcmtDate(String nmBatchParam, Date dtPlcmtStart) {
		Query queryCharacteristics = sessionFactory.getCurrentSession().createSQLQuery(getContractIdByVendorAndService);
		queryCharacteristics.setParameter("nmBatchParam", nmBatchParam);
		queryCharacteristics.setParameter("dtPlcmtStart", dtPlcmtStart);
		return queryCharacteristics.list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.placement.dao.ContractDao#
	 * getContractByVendorandPlcmtDate(java. lang.String, java.util.Date)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Long> getContractByVendorandPlcmtDate(String nmBatchParam, Date dtPlcmtStart) {
		Query queryCharacteristics = sessionFactory.getCurrentSession().createSQLQuery(getContractIdByVendorAndService);
		queryCharacteristics.setParameter("nmBatchParam", nmBatchParam);
		queryCharacteristics.setParameter("dtPlcmtStart", dtPlcmtStart);
		queryCharacteristics.setParameter("cdCnsvcService", CD_CNSVC_SERVICE);
		return queryCharacteristics.list();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.placement.dao.ContractDao#getCountyContracted(us
	 * .tx.state.dfps.service.placement.dto.ContractCountyPeriodInDto)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public CommonStringRes getCountyContracted(ContractCountyPeriodInDto contractCntyPeriodInDto) {
		CommonStringRes commonStringRes = new CommonStringRes();
		commonStringRes.setCommonRes(ServiceConstants.STRING_IND_N);
		Date dtSystemDate = new Date();
		SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getCountyContracted)
				.addScalar("idContract", StandardBasicTypes.LONG)
				.addScalar("dtCncntyEffective", StandardBasicTypes.DATE)
				.addScalar("dtCncntyEnd", StandardBasicTypes.DATE)
				.setParameter("idResource", contractCntyPeriodInDto.getIdResource())
				.setParameter("cdCncntyCounty", contractCntyPeriodInDto.getCdCncntyCounty())
				.setParameter("cdCncntyService", contractCntyPeriodInDto.getCdCncntyService())
				.setParameter("cdCnperStatus", ACT).setParameter("indCnperSigned", ServiceConstants.STRING_IND_Y)
				.setResultTransformer(Transformers.aliasToBean(ContractCountyPeriodOutDto.class)));
		List<ContractCountyPeriodOutDto> contractCountyPeriodOutDtoList = (List<ContractCountyPeriodOutDto>) sQLQuery
				.list();
		for (ContractCountyPeriodOutDto ccpOutDto : contractCountyPeriodOutDtoList) {
			if (ccpOutDto.getDtCncntyEnd() == null || dtSystemDate.before(ccpOutDto.getDtCncntyEnd())) {
				commonStringRes.setCommonRes(ServiceConstants.STRING_IND_Y);
				break;
			}

		}

		return commonStringRes;

	}

	/**
	 * Method Name:getContractById Method Description: This method gets contract
	 * entity using contract id
	 * 
	 * @param contractId
	 * @return
	 */
	@Override
	public Contract getContractById(Long contractId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Contract.class)
				.add(Restrictions.eq("idContract", contractId));
		Contract contract = (Contract) criteria.uniqueResult();
		return contract;
	}

	/**
	 * Method Name: getContractPeriodById Method Description: This method is
	 * used to get contract period information using contract id and
	 * cnperPeriod.
	 * 
	 * @param idContract
	 * @param nbrCnperPeriod
	 * @return
	 */
	@Override
	public ContractPeriod getContractPeriodById(Long idContract, byte nbrCnperPeriod) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ContractPeriod.class)
				.add(Restrictions.eq("contract.idContract", idContract))
				.add(Restrictions.eq("id.nbrCnperPeriod", nbrCnperPeriod));
		ContractPeriod contractPeriod = (ContractPeriod) criteria.uniqueResult();
		return contractPeriod;
	}

	/**
	 * 
	 * Method Name: contractAUD Method Description:This DAM is called by
	 * services: CCMN35S. DAM NAME: CAUD01D. Used to perform CRUD operation on
	 * Contract tables
	 * 
	 * @param contractDto
	 * @param archInputDto
	 */
	@Override
	public ContractDto contractAUD(ContractDto contractDto, ServiceReqHeaderDto archInputDto) {
		ContractDto resultContractDto = new ContractDto();
		Criteria criteria = null;
		if (!TypeConvUtil.isNullOrEmpty(archInputDto) && !TypeConvUtil.isNullOrEmpty(archInputDto.getReqFuncCd())) {
			switch (archInputDto.getReqFuncCd()) {
			case ServiceConstants.REQ_FUNC_CD_ADD:

				Contract contract = new Contract();
				// idContract is primary key (Auto gen)
				Long primaryKey = 0l;

				if (!TypeConvUtil.isNullOrEmpty(contractDto.getCdCntrctRegion())) {
					contract.setCdCntrctRegion(contractDto.getCdCntrctRegion());
				}

				// Need to set idResource
				if (!TypeConvUtil.isNullOrEmpty(contractDto.getIdResourse())) {
					CapsResource capsResource = new CapsResource();
					capsResource.setIdResource(contractDto.getIdResourse());
					contract.setCapsResource(capsResource);
				}

				if (!TypeConvUtil.isNullOrEmpty(contractDto.getDtLastUpdate())) {
					contract.setDtLastUpdate(contractDto.getDtLastUpdate());
				}

				if (!TypeConvUtil.isNullOrEmpty(contractDto.getIndCntrctBudgLimit())) {
					contract.setIndCntrctBudgLimit(contractDto.getIndCntrctBudgLimit());
				}

				if (!TypeConvUtil.isNullOrEmpty(contractDto.getCdCntrctFuncType())) {
					contract.setCdCntrctFuncType(contractDto.getCdCntrctFuncType());
				}

				if (!TypeConvUtil.isNullOrEmpty(contractDto.getCdCntrctProgramType())) {
					contract.setCdCntrctProgramType(contractDto.getCdCntrctProgramType());
				}

				if (!TypeConvUtil.isNullOrEmpty(contractDto.getCdCntrctProcureType())) {
					contract.setCdCntrctProcureType(contractDto.getCdCntrctProcureType());
				}

				if (!TypeConvUtil.isNullOrEmpty(contractDto.getIdContractWkr())) {
					Person worker = new Person();
					worker.setIdPerson(contractDto.getIdContractWkr());
					contract.setPersonByIdCntrctWkr(worker);
				}

				if (!TypeConvUtil.isNullOrEmpty(contractDto.getIdContractMngr())) {
					Person manager = new Person();
					manager.setIdPerson(contractDto.getIdContractMngr());
					contract.setPersonByIdCntrctManager(manager);
				}

				if (!TypeConvUtil.isNullOrEmpty(contractDto.getIdRsrcAddress())) {
					ResourceAddress rsrcAddresss = new ResourceAddress();
					rsrcAddresss.setIdRsrcAddress(contractDto.getIdRsrcAddress());
					contract.setResourceAddress(rsrcAddresss);
				}

				primaryKey = (Long) sessionFactory.getCurrentSession().save(contract);
				sessionFactory.getCurrentSession().flush();
				resultContractDto.setIdContract(primaryKey);

				break;

			case ServiceConstants.REQ_FUNC_CD_UPDATE:
				criteria = sessionFactory.getCurrentSession().createCriteria(Contract.class);
				criteria.add(Restrictions.eq("idContract", contractDto.getIdContract()));
				contract = (Contract) criteria.uniqueResult();
				if (TypeConvUtil.isNullOrEmpty(contract)) {
					throw new DataNotFoundException(
							messageSource.getMessage("ContractDaoImpl.contract.not.found", null, Locale.US));
				}

				if (!TypeConvUtil.isNullOrEmpty(contractDto.getCdCntrctRegion())) {
					contract.setCdCntrctRegion(contractDto.getCdCntrctRegion());
				}

				// Need to set idResource
				if (!TypeConvUtil.isNullOrEmpty(contractDto.getIdResourse())) {
					CapsResource capsResource = new CapsResource();
					capsResource.setIdResource(contractDto.getIdResourse());
					contract.setCapsResource(capsResource);
				}

				if (!TypeConvUtil.isNullOrEmpty(contractDto.getIdResourse())) {
					contract.setIndCntrctBudgLimit(contractDto.getIndCntrctBudgLimit());
				}

				if (!TypeConvUtil.isNullOrEmpty(contractDto.getCdCntrctFuncType())) {
					contract.setCdCntrctFuncType(contractDto.getCdCntrctFuncType());
				}

				if (!TypeConvUtil.isNullOrEmpty(contractDto.getCdCntrctProgramType())) {
					contract.setCdCntrctProgramType(contractDto.getCdCntrctProgramType());
				}

				if (!TypeConvUtil.isNullOrEmpty(contractDto.getDtLastUpdate())) {
					contract.setDtLastUpdate(contractDto.getDtLastUpdate());
				}

				if (!TypeConvUtil.isNullOrEmpty(contractDto.getCdCntrctProcureType())) {
					contract.setCdCntrctProcureType(contractDto.getCdCntrctProcureType());
				}

				if (!TypeConvUtil.isNullOrEmpty(contractDto.getIdContractWkr())) {
					Person worker = new Person();
					worker.setIdPerson(contractDto.getIdContractWkr());
					contract.setPersonByIdCntrctWkr(worker);
				}

				if (!TypeConvUtil.isNullOrEmpty(contractDto.getIdContractMngr())) {
					Person manager = new Person();
					manager.setIdPerson(contractDto.getIdContractMngr());
					contract.setPersonByIdCntrctManager(manager);
				}

				if (!TypeConvUtil.isNullOrEmpty(contractDto.getIdRsrcAddress())) {
					ResourceAddress rsrcAddresss = new ResourceAddress();
					rsrcAddresss.setIdRsrcAddress(contractDto.getIdRsrcAddress());
					contract.setResourceAddress(rsrcAddresss);
				}

				sessionFactory.getCurrentSession().saveOrUpdate(contract);

				// Setting the updated record primary key
				resultContractDto.setIdContract(contract.getIdContract());
				break;
			case ServiceConstants.REQ_FUNC_CD_DELETE:

				criteria = sessionFactory.getCurrentSession().createCriteria(Contract.class);
				criteria.add(Restrictions.eq("idContract", contractDto.getIdContract()));
				contract = (Contract) criteria.uniqueResult();
				if (TypeConvUtil.isNullOrEmpty(contract)) {
					throw new DataNotFoundException(
							messageSource.getMessage("ContractDaoImpl.contract.not.found", null, Locale.US));
				}

				sessionFactory.getCurrentSession().delete(contract);
				resultContractDto.setIdContract(contractDto.getIdContract());
				break;

			default:
				break;
			}
		}
		return resultContractDto;
	}

	/**
	 * Method Name: contractPeriodAUD DAM Name: CAUD20D
	 ** 
	 ** Method Description: This method perform CRUD Operation on CONTRACT_PERIOD
	 * table and its children records. 1) Add: Simple Insert full row Set
	 * pOutputDataRec->ulSysCdGenericReturnCode=TRUE
	 **
	 ** 2) Update: Simple Update full row, then Check for CLOSURE_DATE of
	 * CONTRACT_PERIOD against Max EFFECTIVE_DATE of CONTRACT_VERSION If
	 * CLOSURE_DATE > EFFECTIVE_DATE ==> return TRUE Else return FALSE in host
	 * output variable pOutputDataRec->ulSysCdGenericReturnCode
	 **
	 ** 3) Delete: Also delete 3 other tables: CONTRACT_VERSION,
	 * CONTRACT_SERVICE, CONTRACT_COUNTY besides deleting CONTRACT_PERIOD Set
	 * pOutputDataRec->ulSysCdGenericReturnCode=TRUE
	 * 
	 * @param contractPeriodInDto
	 * @param archInputDto
	 */
	@Override
	public ContractPeriodAUDRes contractPeriodAUD(ContractContractPeriodInDto contractPeriodInDto,
			ServiceReqHeaderDto archInputDto) {
		ContractPeriodAUDRes contractPeriodAUDRes = new ContractPeriodAUDRes();
		if (!TypeConvUtil.isNullOrEmpty(archInputDto) && !TypeConvUtil.isNullOrEmpty(archInputDto.getReqFuncCd())) {

			SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession()
					.createSQLQuery(contractPeriodAUDsQLQuery1);
			sqlQuery.addScalar("tempFloat", StandardBasicTypes.INTEGER)
					.setLong("nbrCnperPeriod", contractPeriodInDto.getNbrCnperPeriod())
					.setLong("idContract", contractPeriodInDto.getIdContract());

			Integer tempFloat = (Integer) sqlQuery.uniqueResult();

			switch (archInputDto.getReqFuncCd()) {

			case ServiceConstants.REQ_FUNC_CD_ADD:
				((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(contractPeriodAUDAddSql)
						.setParameter("indCnperRenewal", contractPeriodInDto.getIndCnperRenewal())
						.setParameter("dtDtCnperTerm", contractPeriodInDto.getDtDtCnperTerm())
						.setParameter("dtDtCnperClosure", contractPeriodInDto.getDtDtCnperClosure())
						.setParameter("indCnperSigned", contractPeriodInDto.getIndCnperSigned())
						.setString("txtProcureNbr", contractPeriodInDto.getProcureNbr())
						.setLong("nbrCnperPeriod", contractPeriodInDto.getNbrCnperPeriod())
						.setParameter("lastUpdate", contractPeriodInDto.getLastUpdate())
						.setLong("idContract", contractPeriodInDto.getIdContract())
						.setParameter("dtDtCnperStart", contractPeriodInDto.getDtDtCnperStart())
						.setLong("idCntrctWkr", contractPeriodInDto.getIdCntrctWkr())
						.setString("cdCnperStatus", contractPeriodInDto.getCdCnperStatus())).executeUpdate();
				contractPeriodAUDRes.setSysCdGenericReturnCode(ServiceConstants.TRUEVAL);
				break;

			case ServiceConstants.REQ_FUNC_CD_UPDATE:

				((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(contractPeriodAUDUpdateSql)
						.setLong("idContract", contractPeriodInDto.getIdContract())
						.setLong("nbrCnperPeriod", contractPeriodInDto.getNbrCnperPeriod())
						.setParameter("indCnperRenewal", contractPeriodInDto.getIndCnperRenewal())
						.setParameter("dtDtCnperTerm", contractPeriodInDto.getDtDtCnperTerm())
						.setParameter("dtDtCnperClosure", contractPeriodInDto.getDtDtCnperClosure())
						.setParameter("indCnperSigned", contractPeriodInDto.getIndCnperSigned())
						.setString("txtProcureNbr", contractPeriodInDto.getProcureNbr())
						.setParameter("dtDtCnperStart", contractPeriodInDto.getDtDtCnperStart())
						.setLong("idCntrctWkr", contractPeriodInDto.getIdCntrctWkr())
						.setString("cdCnperStatus", contractPeriodInDto.getCdCnperStatus())).executeUpdate();

				/*
				 * If CLOSURE_DATE > EFFECTIVE_DATE ==> return TRUE Else return
				 * FALSE
				 */
				if (tempFloat > 0) {
					contractPeriodAUDRes.setSysCdGenericReturnCode(ServiceConstants.TRUEVAL);
				} else {
					contractPeriodAUDRes.setSysCdGenericReturnCode(ServiceConstants.FALSEVAL);
				}
				break;

			case ServiceConstants.REQ_FUNC_CD_DELETE:
				contractPeriodAUDRes.setSysCdGenericReturnCode(ServiceConstants.TRUEVAL);
				break;

			default:
				contractPeriodAUDRes.setSysCdGenericReturnCode(ServiceConstants.FALSEVAL);
				break;
			}
		}
		return contractPeriodAUDRes;
	}

	public ContractPeriodDto getLatestCPForResourceId(String resourceId) {
		Query query = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(getLatestCPForResourceIdSql)
				.addScalar("idContract", StandardBasicTypes.LONG)
				.addScalar("dtCnperStart", StandardBasicTypes.DATE)
				.addScalar("dtCnperTerm", StandardBasicTypes.DATE)
				.addScalar("dtCnperClosure", StandardBasicTypes.DATE)
				.addScalar("nbrContractScor", StandardBasicTypes.STRING)
				.addScalar("cdCnperStatus", StandardBasicTypes.STRING)
				.setParameter("resourceId", resourceId))
				.setResultTransformer(Transformers.aliasToBean(ContractPeriodDto.class));
		;
		List<ContractPeriodDto> contractIdList = (List<ContractPeriodDto>) query.list();
		return contractIdList != null && !contractIdList.isEmpty() ? contractIdList.get(0) : null;
	}

}
