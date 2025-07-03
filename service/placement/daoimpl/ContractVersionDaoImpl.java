package us.tx.state.dfps.service.placement.daoimpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dto.ContractVersionInDto;
import us.tx.state.dfps.service.admin.dto.ContractVersionOutDto;
import us.tx.state.dfps.service.casemanagement.daoimpl.CpsIntakeNotificationDaoImpl;
import us.tx.state.dfps.service.placement.dao.ContractVersionDao;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION
 * 
 * Class Description: It retrieves a row(s) from CONTRACT_VERSION table. Service
 * Name: CCMN35S; DAM Name - CCMN43D
 * 
 * Jun 17, 2018 - 12:06:30 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@Repository
public class ContractVersionDaoImpl implements ContractVersionDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ContractVersionDaoImpl.getContractVersionByNbrCnverPeriod}")
	private String getContractVersionByNbrCnverPeriodSql;

	@Value("${ContractVersionDaoImpl.getLatestContractVersion}")
	private String getLatestContractVersionSql;

	public static final Logger log = Logger.getLogger(CpsIntakeNotificationDaoImpl.class);

	/**
	 * Method Name: getContractVersionByNbrCnverPeriod
	 * 
	 * Method Description: retrieves a full row from CONTRACT VERSION given the
	 * ID_CONTRACT and NBR_CNVER_PERIOD. DAM NAME: CSES81D
	 *
	 * @param contractVersionInDto
	 *            -the contract version in dto
	 * @return ContractVersionOutDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ContractVersionOutDto> getContractVersionByNbrCnverPeriod(ContractVersionInDto contractVersionInDto) {
		log.info("Entering getContractVersionByNbrCnverPeriod in ContractVersionDaoImpl");
		SQLQuery sqlQuery = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getContractVersionByNbrCnverPeriodSql).addScalar("idCnver", StandardBasicTypes.LONG)
				.addScalar("idContract", StandardBasicTypes.LONG).addScalar("idCntrctWkr", StandardBasicTypes.LONG)
				.addScalar("nbrCnverPeriod", StandardBasicTypes.INTEGER)
				.addScalar("nbrCnverVersion", StandardBasicTypes.INTEGER)
				.addScalar("nbrCnverNoShowPct", StandardBasicTypes.INTEGER)
				.addScalar("indCnverVerLock", StandardBasicTypes.BOOLEAN)
				.addScalar("txtCnverComment", StandardBasicTypes.STRING)
				.addScalar("dtCnverCreate", StandardBasicTypes.DATE)
				.addScalar("dtCnverEffective", StandardBasicTypes.DATE).addScalar("dtCnverEnd", StandardBasicTypes.DATE)
				.addScalar("tsLastUpdate", StandardBasicTypes.DATE)
				.setParameter("idContract", contractVersionInDto.getIdContract())
				.setParameter("nbrCnverPeriod", contractVersionInDto.getNbrCnverPeriod())
				.setResultTransformer(Transformers.aliasToBean(ContractVersionOutDto.class)));
		log.info("Exiting getContractVersionByNbrCnverPeriod in ContractVersionDaoImpl");
		return (List<ContractVersionOutDto>) sqlQuery.list();
	}

	/**
	 * Method Name: getLatestContractVersion
	 * 
	 * Method Description: This DAM will receive ID CONTRACT and NBR CNPER
	 * PERIOD (of the previous period) and will return all columns for the
	 * latest contract version record in the CONTRACT VERSION table. DAM:
	 * CSES01D
	 * 
	 * @param contractVersionInDto
	 * @return ContractVersionOutDto
	 */
	@Override
	public ContractVersionOutDto getLatestContractVersion(ContractVersionInDto contractVersionInDto) {
		log.info("Entering getLatestContractVersion in ContractVersionDaoImpl");
		SQLQuery sqlQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getLatestContractVersionSql)
				.addScalar("idCnver", StandardBasicTypes.LONG).addScalar("idContract", StandardBasicTypes.LONG)
				.addScalar("idCntrctWkr", StandardBasicTypes.LONG)
				.addScalar("nbrCnverPeriod", StandardBasicTypes.INTEGER)
				.addScalar("nbrCnverVersion", StandardBasicTypes.INTEGER)
				.addScalar("nbrCnverNoShowPct", StandardBasicTypes.INTEGER)
				.addScalar("indCnverVerLock", StandardBasicTypes.BOOLEAN)
				.addScalar("txtCnverComment", StandardBasicTypes.STRING)
				.addScalar("dtCnverCreate", StandardBasicTypes.DATE)
				.addScalar("dtCnverEffective", StandardBasicTypes.DATE).addScalar("dtCnverEnd", StandardBasicTypes.DATE)
				.addScalar("tsLastUpdate", StandardBasicTypes.DATE)
				.setParameter("idContract", contractVersionInDto.getIdContract())
				.setParameter("nbrCnverPeriod", contractVersionInDto.getNbrCnverPeriod())
				.setResultTransformer(Transformers.aliasToBean(ContractVersionOutDto.class)));
		log.info("Exiting getLatestContractVersion in ContractVersionDaoImpl");
		return (ContractVersionOutDto) sqlQuery.uniqueResult();
	}
}
