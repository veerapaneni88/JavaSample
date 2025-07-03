package us.tx.state.dfps.service.approval.daoimpl;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.approval.dto.ContractVersionAudReq;
import us.tx.state.dfps.approval.dto.ContractVersionAudRes;
import us.tx.state.dfps.common.domain.ContractPeriod;
import us.tx.state.dfps.common.domain.ContractVersion;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.approval.dao.ContractVersionAudDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.placement.dao.ContractDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: June 19,
 * 2018- 1:05:32 PM © 2018 Texas Department of Family and Protective Services
 */

@Repository
public class ContractVersionAudDaoImpl implements ContractVersionAudDao {

	public static final Logger log = Logger.getLogger(ContractVersionAudDaoImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	@Value("${ContractVersionAudDaoImpl.getContractVersionNextSeqSql}")
	private String getContractVersionNextSeqSql;

	@Value("${ContractVersionAudDaoImpl.contractVersionAudAddSql}")
	private String contractVersionAudAdd;

	@Value("${ContractVersionAudDaoImpl.contractVersionAudUpdateSql}")
	private String contractVersionAudUpdate;

	@Autowired
	ContractDao contractDao;

	/**
	 * Method Name: ContractVersionAud Method Description: AUD on
	 * CONTRACT_VERSION table. DELETE: also delete CONTRACT_COUNTY and
	 * CONTRACT_SERVICE based on this combination (ID_CONTRACT, PERIOD,VERSION)
	 * (i.e., delete all LINE_ITEM)
	 * 
	 * Called by Service Name: CCMN35S, DAM NAME: CAUD15D
	 */
	@Override
	public ContractVersionAudRes contractVersionAud(ContractVersionAudReq contractVersionAudReq) {
		log.debug("Entering method contractVersionAud in ContractVersionAudDaoImpl");
		ContractVersionAudRes contractVersionAudRes = new ContractVersionAudRes();

		switch (contractVersionAudReq.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			// idCnver is primary key
			Long primaryKey = (Long) sessionFactory.getCurrentSession().createSQLQuery(getContractVersionNextSeqSql)
					.addScalar("idCnver", StandardBasicTypes.LONG).uniqueResult();

			if (!TypeConvUtil.isNullOrEmpty(primaryKey)) {
				ContractVersion newContractVersion = new ContractVersion();
				// set primary key
				newContractVersion.setIdCnver(primaryKey);

				if (!TypeConvUtil.isNullOrEmpty(contractVersionAudReq.getTxtCnverComment())) {
					newContractVersion.setTxtCnverComment(contractVersionAudReq.getTxtCnverComment());
				}

				if (!TypeConvUtil.isNullOrEmpty(contractVersionAudReq.getDtCnverCreate())) {
					newContractVersion.setDtCnverCreate(contractVersionAudReq.getDtCnverCreate());
				}

				if (!TypeConvUtil.isNullOrEmpty(contractVersionAudReq.getNbrCnverVersion())) {
					newContractVersion.setNbrCnverVersion((contractVersionAudReq.getNbrCnverVersion()).intValue());
				}

				if (!TypeConvUtil.isNullOrEmpty(contractVersionAudReq.getDtCnverEnd())) {
					newContractVersion.setDtCnverEnd(contractVersionAudReq.getDtCnverEnd());
				}

				if (!TypeConvUtil.isNullOrEmpty(contractVersionAudReq.getIdContract())
						&& !TypeConvUtil.isNullOrEmpty(contractVersionAudReq.getNbrCnverPeriod())) {
					ContractPeriod contractPeriod = contractDao.getContractPeriodById(
							contractVersionAudReq.getIdContract(), (byte) contractVersionAudReq.getNbrCnverPeriod());
					if (ObjectUtils.isEmpty(contractPeriod)) {
						throw new DataNotFoundException(messageSource
								.getMessage("ContractVersionAudDaoImpl.contractperiod.not.found", null, Locale.US));
					}
					newContractVersion.setContractPeriod(contractPeriod);
				}

				if (!TypeConvUtil.isNullOrEmpty(contractVersionAudReq.getNbrCnverNoShowPct())) {
					newContractVersion.setNbrCnverNoShowPct((short) contractVersionAudReq.getNbrCnverNoShowPct());
				}

				if (!TypeConvUtil.isNullOrEmpty(contractVersionAudReq.getDtCnverEffective())) {
					newContractVersion.setDtCnverEffective(contractVersionAudReq.getDtCnverEffective());
				}

				if (!TypeConvUtil.isNullOrEmpty(contractVersionAudReq.getLastUpdate())) {
					newContractVersion.setDtLastUpdate(contractVersionAudReq.getLastUpdate());
				}

				if (!TypeConvUtil.isNullOrEmpty(contractVersionAudReq.getIdCntrctWkr())) {
					Person worker = new Person();
					worker.setIdPerson(contractVersionAudReq.getIdCntrctWkr());
					newContractVersion.setPerson(worker);
				}

				if (!TypeConvUtil.isNullOrEmpty(contractVersionAudReq.getIndCnverVerLock())) {
					newContractVersion.setIndCnverVerLock(contractVersionAudReq.getIndCnverVerLock());
				}

				primaryKey = (Long) sessionFactory.getCurrentSession().save(newContractVersion);
				contractVersionAudRes.setIdCnver(primaryKey);
			}
			// return primary key below in response object
			break;
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			SQLQuery sqlQueryContractVersionAudUpdate = ((SQLQuery) sessionFactory.getCurrentSession()
					.createSQLQuery(contractVersionAudUpdate)
					.setLong("hI_ulIdCnver", contractVersionAudReq.getIdCnver())
					.setLong("idContract", contractVersionAudReq.getIdContract())
					.setLong("idCntrctWkr", contractVersionAudReq.getIdCntrctWkr())
					.setLong("nbrCnverPeriod", contractVersionAudReq.getNbrCnverPeriod())
					.setLong("nbrCnverVersion", contractVersionAudReq.getNbrCnverVersion())
					.setLong("nbrCnverNoShowPct", contractVersionAudReq.getNbrCnverNoShowPct())
					.setParameter("indCnverVerLock", contractVersionAudReq.getIndCnverVerLock())
					.setString("txtCnverComment", contractVersionAudReq.getTxtCnverComment())
					.setParameter("dtCnverCreate", contractVersionAudReq.getDtCnverCreate())
					.setParameter("dtCnverEffective", contractVersionAudReq.getDtCnverEffective())
					.setParameter("dtCnverEnd", contractVersionAudReq.getDtCnverEnd())
					.setParameter("lastUpdate", contractVersionAudReq.getLastUpdate()));
			Integer rowsUpdated = sqlQueryContractVersionAudUpdate.executeUpdate();
			// return no. of rows updated below in response object
			contractVersionAudRes.setNumberOfRowsUpdated(rowsUpdated);
			break;
		case ServiceConstants.REQ_FUNC_CD_DELETE:
			break;
		default:
			break;
		}

		log.debug("Exiting method contractVersionAud in ContractVersionAudDaoImpl");
		return contractVersionAudRes;
	}

}
