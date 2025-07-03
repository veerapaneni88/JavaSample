package us.tx.state.dfps.service.placement.daoimpl;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.CapsResource;
import us.tx.state.dfps.common.domain.ContractCounty;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.admin.dto.ContractCountyInDto;
import us.tx.state.dfps.service.admin.dto.ContractCountyOutDto;
import us.tx.state.dfps.service.casemanagement.daoimpl.CpsIntakeNotificationDaoImpl;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.placement.dao.ContractCountyDao;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION
 * 
 * Class Description: It retrieves a row(s) from CONTRACT_COUNTY table. County
 * Name: CCMN35S; DAM Name - CLSS68D
 * 
 * Jun 17, 2018 - 12:06:30 PM © 2017 Texas Department of Family and Protective
 * Countys
 */
@Repository
public class ContractCountyDaoImpl implements ContractCountyDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ContractCountyDaoImpl.getContractCounty}")
	private String getContractCountySql;

	@Value("${ContractCountyDaoImpl.getContractCountyNextVal}")
	private String getContractCountyNextValSql;

	@Value("${ContractCountyDaoImpl.deleteContractCounty}")
	private String deleteContractCounty;

	public static final Logger log = Logger.getLogger(CpsIntakeNotificationDaoImpl.class);

	protected static final Date MAX_DATE = new Date(Long.MAX_VALUE);

	/**
	 * Method Name: getContractCounty
	 * 
	 * Method Description: retrieves a full row from CONTRACT COUNTY given the
	 * ID_CONTRACT, NBR_CNVER_PERIOD and NBR_CNCNTY_VERSION. DAM NAME: CLSS68D
	 *
	 * @param contractCountyInDto
	 *            -the contract county in dto
	 * @return ContractCountyOutDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ContractCountyOutDto> getContractCounty(ContractCountyInDto contractCountyInDto) {
		log.info("Entering getContractCounty in ContractCountyDaoImpl");
		SQLQuery sqlQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getContractCountySql)
				.addScalar("idCncnty", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idContract", StandardBasicTypes.LONG)
				.addScalar("nbrCncntyPeriod", StandardBasicTypes.INTEGER)
				.addScalar("nbrCncntyVersion", StandardBasicTypes.INTEGER)
				.addScalar("nbrCncntyLineItem", StandardBasicTypes.INTEGER)
				.addScalar("cdCncnctyCounty", StandardBasicTypes.STRING)
				.addScalar("idCntrctWkr", StandardBasicTypes.LONG).addScalar("idResource", StandardBasicTypes.LONG)
				.addScalar("cdCncnctyCounty", StandardBasicTypes.STRING)
				.addScalar("dtCncnctyEffective", StandardBasicTypes.DATE)
				.addScalar("dtCncnctyEnd", StandardBasicTypes.DATE)
				.setParameter("idContract", contractCountyInDto.getIdContract())
				.setParameter("nbrCncntyPeriod", contractCountyInDto.getNbrCncntyPeriod())
				.setParameter("nbrCncntyVersion", contractCountyInDto.getNbrCncntyVersion())
				.setResultTransformer(Transformers.aliasToBean(ContractCountyOutDto.class)));
		log.info("Exiting getContractCounty in ContractCountyDaoImpl");
		return (List<ContractCountyOutDto>) sqlQuery.list();
	}

	/**
	 * Method Name: contractCountyAUD; DAM Name: CAUD08D
	 ** 
	 ** Method Description: This method perform CRUD Operation on CONTRACT_COUNTY
	 * table
	 * 
	 * @param contractCountyInDto
	 * @param archInputDto
	 * @return ContractCountyOutDto
	 */
	@Override
	public ContractCountyOutDto contractCountyAUD(ContractCountyInDto contractCountyInDto,
			ServiceReqHeaderDto archInputDto) {

		if (!TypeConvUtil.isNullOrEmpty(archInputDto) && !TypeConvUtil.isNullOrEmpty(archInputDto.getReqFuncCd())) {
			SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(getContractCountyNextValSql)
					.addScalar("idCncnt", StandardBasicTypes.LONG);

			Long autoGenIdCncnt = (Long) sqlQuery.uniqueResult();

			ContractCountyOutDto contractCountyOutDto = new ContractCountyOutDto();
			Criteria criteria = null;
			ContractCounty contractCounty = null;

			switch (archInputDto.getReqFuncCd()) {

			case ServiceConstants.REQ_FUNC_CD_ADD:
				ContractCounty newContractCounty = new ContractCounty();
				Long primaryKey = 0l;

				if (!TypeConvUtil.isNullOrEmpty(autoGenIdCncnt)) {
					// Populate auto generated primary key
					newContractCounty.setIdCncnty(autoGenIdCncnt);
					// Populate rest of the fields
					if (!TypeConvUtil.isNullOrEmpty(contractCountyInDto.getIdResource())) {
						CapsResource capsResource = new CapsResource();
						capsResource.setIdResource(contractCountyInDto.getIdResource());
						newContractCounty.setCapsResource(capsResource);
					}
					if (!TypeConvUtil.isNullOrEmpty(contractCountyInDto.getDtLastUpdate())) {
						newContractCounty.setDtLastUpdate(contractCountyInDto.getDtLastUpdate());
					}
					if (!TypeConvUtil.isNullOrEmpty(contractCountyInDto.getIdContract())) {
						newContractCounty.setIdContract(contractCountyInDto.getIdContract());
					}
					if (!TypeConvUtil.isNullOrEmpty(contractCountyInDto.getNbrCncntyPeriod())) {
						newContractCounty.setNbrCncntyPeriod((contractCountyInDto.getNbrCncntyPeriod()).byteValue());
					}
					if (!TypeConvUtil.isNullOrEmpty(contractCountyInDto.getNbrCncntyVersion())) {
						newContractCounty.setNbrCncntyVersion((contractCountyInDto.getNbrCncntyVersion()).byteValue());
					}
					if (!TypeConvUtil.isNullOrEmpty(contractCountyInDto.getNbrCncntyLineItem())) {
						newContractCounty
								.setNbrCncntyLineItem((contractCountyInDto.getNbrCncntyLineItem()).byteValue());
					}
					if (!TypeConvUtil.isNullOrEmpty(contractCountyInDto.getCdCncntyCounty())) {
						newContractCounty.setCdCncntyCounty(contractCountyInDto.getCdCncntyCounty());
					}
					if (!TypeConvUtil.isNullOrEmpty(contractCountyInDto.getIdCntrctWkr())) {
						Person person = new Person();
						person.setIdPerson(contractCountyInDto.getIdCntrctWkr());
						newContractCounty.setPerson(person);
					}
					if (!TypeConvUtil.isNullOrEmpty(contractCountyInDto.getCdCncntyService())) {
						newContractCounty.setCdCncntyService(contractCountyInDto.getCdCncntyService());
					}
					if (!TypeConvUtil.isNullOrEmpty(contractCountyInDto.getDtCncntyEffective())) {
						newContractCounty.setDtCncntyEffective(contractCountyInDto.getDtCncntyEffective());
					}
					if (!TypeConvUtil.isNullOrEmpty(contractCountyInDto.getDtCncntyEnd())) {
						newContractCounty.setDtCncntyEnd(contractCountyInDto.getDtCncntyEnd());
					}

					// will return the inserted record primary key
					primaryKey = (Long) sessionFactory.getCurrentSession().save(newContractCounty);
				}
				contractCountyOutDto.setIdCncnty(primaryKey);
				break;

			case ServiceConstants.REQ_FUNC_CD_UPDATE:
				criteria = sessionFactory.getCurrentSession().createCriteria(ContractCounty.class);
				criteria.add(Restrictions.eq("idCncnty", contractCountyInDto.getIdCncnty()));
				contractCounty = (ContractCounty) criteria.uniqueResult();
				if (TypeConvUtil.isNullOrEmpty(contractCounty)) {
					throw new DataNotFoundException(messageSource
							.getMessage("ContractCountyDaoImpl.contractCounty.not.found", null, Locale.US));
				}

				// Populate fields to update
				if (!TypeConvUtil.isNullOrEmpty(contractCountyInDto.getIdContract())) {
					contractCounty.setIdContract(contractCountyInDto.getIdContract());
				}
				if (!TypeConvUtil.isNullOrEmpty(contractCountyInDto.getIdCntrctWkr())) {
					Person person = new Person();
					person.setIdPerson(contractCountyInDto.getIdCntrctWkr());
					contractCounty.setPerson(person);
				}
				if (!TypeConvUtil.isNullOrEmpty(contractCountyInDto.getIdResource())) {
					CapsResource capsResource = new CapsResource();
					capsResource.setIdResource(contractCountyInDto.getIdResource());
					contractCounty.setCapsResource(capsResource);
				}
				if (!TypeConvUtil.isNullOrEmpty(contractCountyInDto.getCdCncntyCounty())) {
					contractCounty.setCdCncntyCounty(contractCountyInDto.getCdCncntyCounty());
				}
				if (!TypeConvUtil.isNullOrEmpty(contractCountyInDto.getCdCncntyService())) {
					contractCounty.setCdCncntyService(contractCountyInDto.getCdCncntyService());
				}
				if (!TypeConvUtil.isNullOrEmpty(contractCountyInDto.getDtCncntyEffective())) {
					contractCounty.setDtCncntyEffective(contractCountyInDto.getDtCncntyEffective());
				}
				if (!TypeConvUtil.isNullOrEmpty(contractCountyInDto.getDtCncntyEnd())) {
					contractCounty.setDtCncntyEnd(contractCountyInDto.getDtCncntyEnd());
				}
				if (!TypeConvUtil.isNullOrEmpty(contractCountyInDto.getNbrCncntyPeriod())) {
					contractCounty.setNbrCncntyPeriod((contractCountyInDto.getNbrCncntyPeriod()).byteValue());
				}
				if (!TypeConvUtil.isNullOrEmpty(contractCountyInDto.getNbrCncntyLineItem())) {
					contractCounty.setNbrCncntyLineItem((contractCountyInDto.getNbrCncntyLineItem()).byteValue());
				}
				if (!TypeConvUtil.isNullOrEmpty(contractCountyInDto.getNbrCncntyVersion())) {
					contractCounty.setNbrCncntyVersion((contractCountyInDto.getNbrCncntyVersion()).byteValue());
				}
				if (!TypeConvUtil.isNullOrEmpty(contractCountyInDto.getDtLastUpdate())) {
					contractCounty.setDtLastUpdate(contractCountyInDto.getDtLastUpdate());
				}

				sessionFactory.getCurrentSession().saveOrUpdate(contractCounty);

				// Setting the updated record primary key
				contractCountyOutDto.setIdCncnty(contractCounty.getIdCncnty());
				break;

			case ServiceConstants.REQ_FUNC_CD_DELETE:
				criteria = sessionFactory.getCurrentSession().createCriteria(ContractCounty.class);
				criteria.add(Restrictions.eq("idCncnty", contractCountyInDto.getIdCncnty()));
				contractCounty = (ContractCounty) criteria.uniqueResult();
				if (TypeConvUtil.isNullOrEmpty(contractCounty)) {
					throw new DataNotFoundException(messageSource
							.getMessage("ContractCountyDaoImpl.contractCounty.not.found", null, Locale.US));
				}

				sessionFactory.getCurrentSession().delete(contractCounty);
				// Setting the deleted record primary key
				contractCountyOutDto.setIdCncnty(contractCounty.getIdCncnty());
				break;

			default:
				break;

			}
			return contractCountyOutDto;
		} else {
			throw new DataLayerException("INVALID REQUEST");
		}
	}
	/**
	 * Method Name: deleteContractCounties
	 * Method Description: This method deletes entries from CONTRACT_COUNTY table
	 * @param contractCountyIdList
	 */
	public void deleteContractCounties(List<Long> contractCountyIdList){
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(deleteContractCounty);
		sqlQuery.setParameterList("countyIdList", contractCountyIdList);
		sqlQuery.executeUpdate();
		sessionFactory.getCurrentSession().flush();
	}
}
