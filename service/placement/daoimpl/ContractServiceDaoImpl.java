package us.tx.state.dfps.service.placement.daoimpl;

import java.math.BigDecimal;
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

import us.tx.state.dfps.common.domain.ContractService;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.admin.dto.ContractServiceInDto;
import us.tx.state.dfps.service.admin.dto.ContractServiceOutDto;
import us.tx.state.dfps.service.casemanagement.daoimpl.CpsIntakeNotificationDaoImpl;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.placement.dao.ContractServiceDao;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION
 * 
 * Class Description: It retrieves a row(s) from CONTRACT_SERVICE table. Service
 * Name: CCMN35S; DAM Name - CLSS13D
 * 
 * Jun 17, 2018 - 12:06:30 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@Repository
public class ContractServiceDaoImpl implements ContractServiceDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ContractServiceDaoImpl.getContractService}")
	private String getContractServiceSql;

	@Value("${ContractServiceDaoImpl.getContractServiceNextVal}")
	private String getContractServiceNextValSql;

	@Value("${ContractServiceDaoImpl.contractServiceAUDInsertSql}")
	private String contractServiceAUDInsertSql;

	public static final Logger log = Logger.getLogger(CpsIntakeNotificationDaoImpl.class);

	/**
	 * Method Name: getContractService
	 * 
	 * Method Description: retrieves a full row from CONTRACT_SERVICE given the
	 * ID_CONTRACT. DAM Name - CLSS13D
	 *
	 * @param contactServiceInDto
	 *            -the contract service in dto
	 * @return ContractServiceOutDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ContractServiceOutDto> getContractService(ContractServiceInDto contactServiceInDto) {
		log.info("Entering getContractService in ContractServiceDaoImpl");
		SQLQuery sqlQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getContractServiceSql)
				.addScalar("idCnsvc", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idContract", StandardBasicTypes.LONG).addScalar("idCntrctWkr", StandardBasicTypes.LONG)
				.addScalar("nbrCnsvcPeriod", StandardBasicTypes.BYTE)
				.addScalar("nbrCnsvcVersion", StandardBasicTypes.BYTE)
				.addScalar("nbrCnsvcLineItem", StandardBasicTypes.BYTE)
				.addScalar("cdCnsvcService", StandardBasicTypes.STRING)
				.addScalar("cdCnsvcPaymentType", StandardBasicTypes.STRING)
				.addScalar("indCnsvcNewRow", StandardBasicTypes.STRING)
				.addScalar("cdCnsvcUnitType", StandardBasicTypes.STRING)
				.addScalar("nbrCnsvcFedMatch", StandardBasicTypes.INTEGER)
				.addScalar("nbrCnsvcLocalMatch", StandardBasicTypes.INTEGER)
				.addScalar("nbrCnsvcUnitRate", StandardBasicTypes.INTEGER)
				.addScalar("amtCnsvcAdminAllUsed", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("amtCnsvcEquip", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("amtCnsvcEquipUsed", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("amtCnsvcFrgBenft", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("amtCnsvcFrgBenftUsed", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("amtCnsvcOffItemUsed", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("amtCnsvcOther", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("amtCnsvcOtherUsed", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("amtCnsvcSalary", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("amtCnsvcSalaryUsed", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("amtCnsvcSupply", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("amtCnsvcSupplyUsed", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("amtCnsvcTravel", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("amtCnsvcTravelUsed", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("amtCnsvcUnitRate", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("amtCnsvcUnitRateUsed", StandardBasicTypes.BIG_DECIMAL)
				.setParameter("idContract", contactServiceInDto.getIdContract())
				.setResultTransformer(Transformers.aliasToBean(ContractServiceOutDto.class)));
		log.info("Exiting getContractService in ContractServiceDaoImpl");
		return (List<ContractServiceOutDto>) sqlQuery.list();
	}

	/**
	 * Method Name: contractServiceAUD DAM Name: CAUD17D
	 ** 
	 ** Method Description: This method perform CRUD Operation on
	 * CONTRACT_SERVICE table
	 * 
	 * @param contractServiceInDto
	 * @param archInputDto
	 * @return ContractServiceOutDto
	 */
	@Override
	public ContractServiceOutDto contractServiceAUD(ContractServiceInDto contractServiceInDto,
			ServiceReqHeaderDto archInputDto) {

		if (!TypeConvUtil.isNullOrEmpty(archInputDto) && !TypeConvUtil.isNullOrEmpty(archInputDto.getReqFuncCd())) {
			SQLQuery sqlQuery = sessionFactory.getCurrentSession().createSQLQuery(getContractServiceNextValSql)
					.addScalar("idCnsvc", StandardBasicTypes.LONG);

			Long autoGenIdCnsvc = (Long) sqlQuery.uniqueResult();
			ContractServiceOutDto contractServiceOutDto = new ContractServiceOutDto();

			switch (archInputDto.getReqFuncCd()) {

			case ServiceConstants.REQ_FUNC_CD_ADD:
				ContractService newContractService = new ContractService();
				Long primaryKey = 0l;

				if (!TypeConvUtil.isNullOrEmpty(autoGenIdCnsvc)) {
					// Populate auto generated primary key
					newContractService.setIdCnsvc(autoGenIdCnsvc);
					// Populate rest of the fields
					if (!TypeConvUtil.isNullOrEmpty(contractServiceInDto.getDtLastUpdate())) {
						newContractService.setDtLastUpdate(contractServiceInDto.getDtLastUpdate());
					}
					if (!TypeConvUtil.isNullOrEmpty(contractServiceInDto.getIdContract())) {
						newContractService.setIdContract(contractServiceInDto.getIdContract());
					}
					if (!TypeConvUtil.isNullOrEmpty(contractServiceInDto.getIdCntrctWkr())) {
						Person person = new Person();
						person.setIdPerson(contractServiceInDto.getIdCntrctWkr());
						newContractService.setPerson(person);
					}
					if (!TypeConvUtil.isNullOrEmpty(contractServiceInDto.getCnsvcPeriod())) {
						newContractService.setNbrCnsvcPeriod((contractServiceInDto.getCnsvcPeriod()).byteValue());
					}
					if (!TypeConvUtil.isNullOrEmpty(contractServiceInDto.getCnsvcVersion())) {
						newContractService.setNbrCnsvcVersion((contractServiceInDto.getCnsvcVersion()).byteValue());
					}
					if (!TypeConvUtil.isNullOrEmpty(contractServiceInDto.getCnsvcLineItem())) {
						newContractService.setNbrCnsvcLineItem(contractServiceInDto.getCnsvcLineItem().byteValue());
					}
					if (!TypeConvUtil.isNullOrEmpty(contractServiceInDto.getCdCnsvcService())) {
						newContractService.setCdCnsvcService(contractServiceInDto.getCdCnsvcService());
					}
					if (!TypeConvUtil.isNullOrEmpty(contractServiceInDto.getCdCnsvcPaymentType())) {
						newContractService.setCdCnsvcPaymentType(contractServiceInDto.getCdCnsvcPaymentType());
					}
					if (!TypeConvUtil.isNullOrEmpty(contractServiceInDto.getIndCnsvcNewRow())) {
						newContractService.setIndCnsvcNewRow(contractServiceInDto.getIndCnsvcNewRow().charAt(0));
					}
					if (!TypeConvUtil.isNullOrEmpty(contractServiceInDto.getCnsvcUnitType())) {
						newContractService.setCdCnsvcUnitType(contractServiceInDto.getCnsvcUnitType());
					}
					if (!TypeConvUtil.isNullOrEmpty(contractServiceInDto.getCnsvcFedMatch())) {
						newContractService.setNbrCnsvcFedMatch(contractServiceInDto.getCnsvcFedMatch());
					}
					if (!TypeConvUtil.isNullOrEmpty(contractServiceInDto.getCnsvcLocalMatch())) {
						newContractService.setNbrCnsvcLocalMatch(contractServiceInDto.getCnsvcLocalMatch());
					}
					if (!TypeConvUtil.isNullOrEmpty(contractServiceInDto.getCnsvcUnitRate())) {
						newContractService
								.setNbrCnsvcUnitRate(BigDecimal.valueOf(contractServiceInDto.getCnsvcUnitRate()));
					}
					if (!TypeConvUtil.isNullOrEmpty(contractServiceInDto.getAmtCnsvcAdminAllUsed())) {
						newContractService.setAmtCnsvcAdminAllUsed(contractServiceInDto.getAmtCnsvcAdminAllUsed());
					}
					if (!TypeConvUtil.isNullOrEmpty(contractServiceInDto.getAmtCnsvcEquip())) {
						newContractService.setAmtCnsvcEquip(contractServiceInDto.getAmtCnsvcEquip());
					}
					if (!TypeConvUtil.isNullOrEmpty(contractServiceInDto.getAmtCnsvcEquipUsed())) {
						newContractService.setAmtCnsvcEquipUsed(contractServiceInDto.getAmtCnsvcEquipUsed());
					}
					if (!TypeConvUtil.isNullOrEmpty(contractServiceInDto.getAmtCnsvcFrgBenft())) {
						newContractService.setAmtCnsvcFrgBenft(contractServiceInDto.getAmtCnsvcFrgBenft());
					}
					if (!TypeConvUtil.isNullOrEmpty(contractServiceInDto.getAmtCnsvcFrgBenftUsed())) {
						newContractService.setAmtCnsvcFrgBenftUsed(contractServiceInDto.getAmtCnsvcFrgBenftUsed());
					}
					if (!TypeConvUtil.isNullOrEmpty(contractServiceInDto.getAmtCnsvcOffItemUsed())) {
						newContractService.setAmtCnsvcOffItemUsed(contractServiceInDto.getAmtCnsvcOffItemUsed());
					}
					if (!TypeConvUtil.isNullOrEmpty(contractServiceInDto.getAmtCnsvcOther())) {
						newContractService.setAmtCnsvcOther(contractServiceInDto.getAmtCnsvcOther());
					}
					if (!TypeConvUtil.isNullOrEmpty(contractServiceInDto.getAmtCnsvcOtherUsed())) {
						newContractService.setAmtCnsvcOtherUsed(contractServiceInDto.getAmtCnsvcOtherUsed());
					}
					if (!TypeConvUtil.isNullOrEmpty(contractServiceInDto.getAmtCnsvcSalary())) {
						newContractService.setAmtCnsvcSalary(contractServiceInDto.getAmtCnsvcSalary());
					}
					if (!TypeConvUtil.isNullOrEmpty(contractServiceInDto.getAmtCnsvcSalaryUsed())) {
						newContractService.setAmtCnsvcSalaryUsed(contractServiceInDto.getAmtCnsvcSalaryUsed());
					}
					if (!TypeConvUtil.isNullOrEmpty(contractServiceInDto.getAmtCnsvcSupply())) {
						newContractService.setAmtCnsvcSupply(contractServiceInDto.getAmtCnsvcSupply());
					}
					if (!TypeConvUtil.isNullOrEmpty(contractServiceInDto.getAmtCnsvcSupplyUsed())) {
						newContractService.setAmtCnsvcSupplyUsed(contractServiceInDto.getAmtCnsvcSupplyUsed());
					}
					if (!TypeConvUtil.isNullOrEmpty(contractServiceInDto.getAmtCnsvcTravel())) {
						newContractService.setAmtCnsvcTravel(contractServiceInDto.getAmtCnsvcTravel());
					}
					if (!TypeConvUtil.isNullOrEmpty(contractServiceInDto.getAmtCnsvcTravelUsed())) {
						newContractService.setAmtCnsvcTravelUsed(contractServiceInDto.getAmtCnsvcTravelUsed());
					}
					if (!TypeConvUtil.isNullOrEmpty(contractServiceInDto.getAmtCnsvcUnitRate())) {
						newContractService.setAmtCnsvcUnitRate(contractServiceInDto.getAmtCnsvcUnitRate());
					}
					if (!TypeConvUtil.isNullOrEmpty(contractServiceInDto.getAmtCnsvcUnitRateUsed())) {
						newContractService.setAmtCnsvcUnitRateUsed(contractServiceInDto.getAmtCnsvcUnitRateUsed());
					}

					// will return the inserted record primary key
					primaryKey = (Long) sessionFactory.getCurrentSession().save(newContractService);
				}
				contractServiceOutDto.setIdCnsvc(primaryKey);
				break;

			case ServiceConstants.REQ_FUNC_CD_UPDATE:
				// TODO: Implement on need basis
				break;

			case ServiceConstants.REQ_FUNC_CD_DELETE:
				// TODO: Implement on need basis
				break;

			default:
				break;
			}
			return contractServiceOutDto;
		} else {
			throw new DataLayerException("INVALID REQUEST");
		}
	}
}
