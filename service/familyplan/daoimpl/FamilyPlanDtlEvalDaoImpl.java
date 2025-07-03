/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:DAO Class
 * for fetching information from DataBase
 *Jul 18, 2018- 3:05:41 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.familyplan.daoimpl;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import oracle.jdbc.OracleTypes;
import us.tx.state.dfps.common.domain.CpsFsna;
import us.tx.state.dfps.common.domain.LegalStatus;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.familyplan.dto.FamilyPlanActnRsrcDto;
import us.tx.state.dfps.familyplan.dto.FamilyPlanDtlEvalDto;
import us.tx.state.dfps.familyplan.dto.FamilyPlanNeedsDto;
import us.tx.state.dfps.familyplan.dto.FamilyPlanPartcpntDto;
import us.tx.state.dfps.familyplan.dto.FamilyPlanReqrdActnsDto;
import us.tx.state.dfps.familyplan.dto.FamilyPlanStrengthsDto;
import us.tx.state.dfps.familyplan.request.FamilyPlanDtlEvalReq;
import us.tx.state.dfps.familyplan.response.FamilyPlanDtlEvalRes;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.ResultSetMapper;
import us.tx.state.dfps.service.common.util.ResultSetMapper.ResultSetMapperException;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.familyplan.dao.FamilyPlanDtlEvalDao;
import us.tx.state.dfps.service.fsna.dao.FSNADao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<DAO Class
 * for fetching information from DataBase> Jul 18, 2018- 3:05:41 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class FamilyPlanDtlEvalDaoImpl implements FamilyPlanDtlEvalDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${FamilyPlanDtlEvalDaoImpl.getFamilyPlanDetails}")
	private String getFamilyPlanDetailsSql;
	
	@Value("${FamilyPlanDtlEvalDaoImpl.getLecalActionDetails}")
	private String getLecalActionDetailsSql;

	@Autowired
	private FSNADao fSNADao;
	
	private static final Logger LOG = Logger.getLogger(FamilyPlanDtlEvalDaoImpl.class);
	private static final ResultSetMapper<?> resultSetMapper = new ResultSetMapper<>();

	/**
	 * Method Name: addFamilyPlan Method Description: refill read only
	 * information to Family Plan detail details after store procedure call.
	 * 
	 * @param familyPlanInDto
	 * @return FamilyPlanDtlEvalRes
	 */
	@Override
	public FamilyPlanDtlEvalRes getFamilyPlanDetails(FamilyPlanDtlEvalReq familyPlanReq) {

		FamilyPlanDtlEvalRes familyPlanRes = new FamilyPlanDtlEvalRes();
		SessionImplementor sessionImplementor = (SessionImplementor) sessionFactory.getCurrentSession();
		Connection connection = null;
		CallableStatement callStatement = null;
		try {

			// Opening JDBC Connection
			connection = sessionImplementor.getJdbcConnectionAccess().obtainConnection();
			callStatement = connection.prepareCall(getFamilyPlanDetailsSql);

			// Call statement to register Input Parameters
			if (!ObjectUtils.isEmpty(familyPlanReq)) {
				if (!ObjectUtils.isEmpty(familyPlanReq.getIdFsnaEvent())) {
					callStatement.setLong(1, familyPlanReq.getIdFsnaEvent());
				} else {
					callStatement.setLong(1, 0l);
				}
				if (!ObjectUtils.isEmpty(familyPlanReq.getIdFamilyPlanEvent())) {
					callStatement.setLong(2, familyPlanReq.getIdFamilyPlanEvent());
				} else {
					callStatement.setLong(2, 0l);
				}
				if (!ObjectUtils.isEmpty(familyPlanReq.getIdFamilyPlanEvalEvent())) {
					callStatement.setLong(3, familyPlanReq.getIdFamilyPlanEvalEvent());
				} else {
					callStatement.setLong(3, 0l);
				}
				if (!ObjectUtils.isEmpty(familyPlanReq.getIndAddOrModify())) {
					callStatement.setString(4, familyPlanReq.getIndAddOrModify());
				} else {
					callStatement.setString(4, "");
				}
				if (!ObjectUtils.isEmpty(familyPlanReq.getCdStage())) {
					callStatement.setString(5, familyPlanReq.getCdStage());
				} else {
					callStatement.setString(5, "");
				}
				if (!ObjectUtils.isEmpty(familyPlanReq.getIdPrmryPrntCrgvr())) {
					callStatement.setLong(6, familyPlanReq.getIdPrmryPrntCrgvr());
				} else {
					callStatement.setLong(6, 0l);
				}
				if (!ObjectUtils.isEmpty(familyPlanReq.getIdScndryPrntCrgvr())) {
					callStatement.setLong(7, familyPlanReq.getIdScndryPrntCrgvr());
				} else {
					callStatement.setLong(7, 0l);
				}
			}
			// Call Statement to register out parameters
			callStatement.registerOutParameter(8, OracleTypes.CURSOR);
			callStatement.registerOutParameter(9, OracleTypes.CURSOR);
			callStatement.registerOutParameter(10, OracleTypes.CURSOR);
			callStatement.registerOutParameter(11, OracleTypes.CURSOR);
			callStatement.registerOutParameter(12, OracleTypes.CURSOR);
			callStatement.registerOutParameter(13, OracleTypes.CURSOR);
			callStatement.registerOutParameter(14, OracleTypes.NUMBER);
			callStatement.registerOutParameter(15, OracleTypes.VARCHAR);

			// execute proc_get_family_plan_dtl_eval store procedure
			callStatement.executeUpdate();

			// get cursor and cast it to ResultSet
			long errorCode = callStatement.getLong(14);
			String errorMsg = callStatement.getString(15);
			if (0 != errorCode && !ObjectUtils.isEmpty(errorCode) && !ObjectUtils.isEmpty(errorMsg)) {
				throw new SQLException("Error occured in stored proc :" + errorCode + errorMsg);
			}
			
			// Setting Result Set to respective DTO's
			FamilyPlanDtlEvalDto familyPlanDtlEvalDto = null;
			List<FamilyPlanDtlEvalDto> familyPlanDtlEvalDtoList = mapResultSetToBean(
					(ResultSet) callStatement.getObject(8), FamilyPlanDtlEvalDto.class);
			Optional<FamilyPlanDtlEvalDto> familyPlanDtlEvalDtoTemp = familyPlanDtlEvalDtoList.stream()
					.filter(o -> !ObjectUtils.isEmpty(o)).findFirst();
			
			if (familyPlanDtlEvalDtoTemp.isPresent()) {
				familyPlanDtlEvalDto = familyPlanDtlEvalDtoTemp.get();
			}
			if(Objects.nonNull(familyPlanDtlEvalDto)) {
				if (Objects.isNull(familyPlanDtlEvalDto.getTxtFosCarePrevPlan())) {
					familyPlanDtlEvalDto.setTxtFosCarePrevPlan(ServiceConstants.NOT_APPLICABLE_WITH_SLASH);
				}
			}
			
			Date dtEventCreated = !ObjectUtils.isEmpty(familyPlanDtlEvalDto) && !ObjectUtils.isEmpty(familyPlanDtlEvalDto.getDtEventOccurred()) ? familyPlanDtlEvalDto.getDtEventOccurred(): new Date();
			List<FamilyPlanPartcpntDto> familyPlanPartcpntDtoList = mapResultSetToBean(
					(ResultSet) callStatement.getObject(9), FamilyPlanPartcpntDto.class);
			List<FamilyPlanStrengthsDto> familyPlanStrengthsDtoList = mapResultSetToBean(
					(ResultSet) callStatement.getObject(10), FamilyPlanStrengthsDto.class);
			List<FamilyPlanNeedsDto> familyPlanNeedsDtoList = mapResultSetToBean(
					(ResultSet) callStatement.getObject(11), FamilyPlanNeedsDto.class);
			List<FamilyPlanReqrdActnsDto> familyPlanReqrdActnsDtoList = mapResultSetToBean(
					(ResultSet) callStatement.getObject(12), FamilyPlanReqrdActnsDto.class);
			List<FamilyPlanActnRsrcDto> familyPlanActnRsrcDtoList = mapResultSetToBean(
					(ResultSet) callStatement.getObject(13), FamilyPlanActnRsrcDto.class);
			if (!ObjectUtils.isEmpty(familyPlanReq.getIdFsnaEvent())) {
				familyPlanDtlEvalDto.setTxtCauseNbr(fetchLatestLegalStatusForCase(familyPlanReq.getIdCase()));
			}
			//Defect#13748			
			if(familyPlanReq.getIdFsnaEvent() == null){
				CpsFsna cpsFsna = fSNADao.getFSNAAsmtById(familyPlanDtlEvalDto.getIdCpsFsna());
				familyPlanReq.setIdFsnaEvent(cpsFsna.getEvent().getIdEvent());
			}
			
			CpsFsna cpsFsna = fSNADao.getFSNAAsmt(familyPlanReq.getIdFsnaEvent());
            
            if(!ObjectUtils.isEmpty(cpsFsna)){
            	familyPlanDtlEvalDto.setIdEvent(cpsFsna.getEvent().getIdEvent());
            	familyPlanDtlEvalDto.setIdCpsFsna(cpsFsna.getIdCpsFsna());
            }
            


			/*
			 * Constructing Response like as below resource inside required
			 * actions required actions inside needs needs inside the
			 * participants.
			 */

			familyPlanPartcpntDtoList.forEach(o1 -> {
				List<FamilyPlanStrengthsDto> familyPlanStrengthsDtoListTemp = familyPlanStrengthsDtoList.stream()
						.filter(strngts -> strngts.getIdrltdperson().equals(o1.getIdPerson()))
						.collect(Collectors.toList());
				o1.setFamilyPlanStrengthsDtoList(familyPlanStrengthsDtoListTemp);
				if(ObjectUtils.isEmpty(o1.getAge()) || ServiceConstants.Zero.equals(o1.getAge())){
					Person person = (Person) sessionFactory.getCurrentSession().get(Person.class, o1.getIdPerson());
					o1.setAge(DateUtils.getPersonListAge(person.getDtPersonBirth(),dtEventCreated ));
				}
				
				List<FamilyPlanNeedsDto> familyPlanNeedsDtoListTemp = familyPlanNeedsDtoList.stream()
						.filter(n -> n.getIdPerson().equals(o1.getIdPerson())).collect(Collectors.toList());
				familyPlanNeedsDtoListTemp.forEach(nds -> {
					if (!ObjectUtils.isEmpty(nds.getNbrSortOrder())
							&& nds.getNbrSortOrder().equals(ServiceConstants.ZERO)) {
						nds.setNbrSortOrder(ServiceConstants.NULL_VAL);
					}
					List<FamilyPlanReqrdActnsDto> familyPlanReqrdActnsDtoListTemp = null;
					if ((ServiceConstants.ADD.equals(familyPlanReq.getIndAddOrModify()))
							&& (StringUtils.isEmpty(familyPlanReq.getIdFamilyPlanEvent())
									|| ServiceConstants.ZERO.equals(familyPlanReq.getIdFamilyPlanEvent()) && (StringUtils.isEmpty(familyPlanReq.getIdFamilyPlanEvalEvent())
											|| ServiceConstants.ZERO.equals(familyPlanReq.getIdFamilyPlanEvalEvent())))) {
						familyPlanReqrdActnsDtoListTemp = familyPlanReqrdActnsDtoList.stream()
								.filter(a -> a.getIdRltdPerson().equals(o1.getIdPerson())
										&& a.getCdDomain().equals(nds.getCdDomain()))
								.collect(Collectors.toList());
					} else if ((ServiceConstants.ADD.equals(familyPlanReq.getIndAddOrModify()))
							&& ((!ObjectUtils.isEmpty(familyPlanReq.getIdFamilyPlanEvent())
									&& !ServiceConstants.ZERO.equals(familyPlanReq.getIdFamilyPlanEvent()))
									|| (!ObjectUtils.isEmpty(familyPlanReq.getIdFamilyPlanEvalEvent())
											&& !ServiceConstants.ZERO.equals(familyPlanReq.getIdFamilyPlanEvalEvent())))
							&& ServiceConstants.NO.equals(nds.getIndPriorNeeds())) {
						familyPlanReqrdActnsDtoListTemp = familyPlanReqrdActnsDtoList.stream()
								.filter(a -> (ObjectUtils.isEmpty(a.getIdFamilyPlanNeeds()) || ServiceConstants.ZERO.equals(a.getIdFamilyPlanNeeds()))
										&& a.getIdRltdPerson().equals(o1.getIdPerson())
										&& a.getCdDomain().equals(nds.getCdDomain()))
								.collect(Collectors.toList());
					} else {
						familyPlanReqrdActnsDtoListTemp = familyPlanReqrdActnsDtoList.stream()
								.filter(a -> a.getIdFamilyPlanNeeds().equals(nds.getIdFamilyPlanNeeds()))
								.collect(Collectors.toList());
					}
					familyPlanReqrdActnsDtoListTemp.forEach(actn -> {
						List<FamilyPlanActnRsrcDto> familyPlanActnRsrcDtoListTemp = familyPlanActnRsrcDtoList.stream()
								.filter(ar -> ar.getIdFamilyPlanReqrdActns().equals(actn.getIdFamilyPlanReqrdActns()))
								.collect(Collectors.toList());
						actn.setFamilyPlanActnRsrcDtoList(familyPlanActnRsrcDtoListTemp);
					});
					nds.setFamilyPlanReqrdActnsDtoList(familyPlanReqrdActnsDtoListTemp);
				});
				o1.setFamilyPlanNeedsDtoList(familyPlanNeedsDtoListTemp);
				if (ServiceConstants.PARENT_PERSON_CHAR.equals(o1.getIndPartcpntType())) {
					if (!ObjectUtils.isEmpty(cpsFsna.getIdPrmryCrgvrPrnt()) && cpsFsna.getIdPrmryCrgvrPrnt().equals(o1.getIdPerson())) {
						o1.setRelationship(ServiceConstants.FSNA_PRIMARY);
					} else if (!ObjectUtils.isEmpty(cpsFsna.getIdSecndryCrgvrPrnt()) && cpsFsna.getIdSecndryCrgvrPrnt().equals(o1.getIdPerson())) {
						o1.setRelationship(ServiceConstants.FSNA_SECONDARY);
					}
				}
			});

			if (familyPlanReq.getIndAddOrModify().equals(ServiceConstants.ADD)) {
				familyPlanPartcpntDtoList.forEach(familyPlanPartcpntDto -> {
					if (CollectionUtils.isNotEmpty(familyPlanPartcpntDto.getFamilyPlanNeedsDtoList())) {
						familyPlanPartcpntDto.getFamilyPlanNeedsDtoList().stream().forEach(familyPlanNeedsDto -> {
							familyPlanNeedsDto.setIdFamilyPlanNeeds(null);
							if (CollectionUtils.isNotEmpty(familyPlanNeedsDto.getFamilyPlanReqrdActnsDtoList())) {
								familyPlanNeedsDto.getFamilyPlanReqrdActnsDtoList().stream()
										.forEach(familyPlanReqrdActnsDto -> {
											familyPlanReqrdActnsDto.setIdFamilyPlanReqrdActns(null);
											familyPlanReqrdActnsDto.setIdFamilyPlanNeeds(null);
											if (CollectionUtils.isNotEmpty(
													familyPlanReqrdActnsDto.getFamilyPlanActnRsrcDtoList())) {
												familyPlanReqrdActnsDto.getFamilyPlanActnRsrcDtoList().stream()
														.forEach(familyPlanActnRsrcDto -> {
															familyPlanActnRsrcDto.setIdFamilyPlanReqrdActns(null);
															familyPlanActnRsrcDto.setIdFamilyPlanActnRsrc(null);
														});
											}
										});
							}
						});
					}

				});
			}
			if(!ObjectUtils.isEmpty(familyPlanReq.getIdCase()) && !ObjectUtils.isEmpty(familyPlanReq.getIdStage())) {
				familyPlanRes.setLegalActionPresent(legalActionPresent(familyPlanReq.getIdCase(), familyPlanReq.getIdStage()));
			}
			
			familyPlanRes.setFamilyPlanDtlEvalDto(familyPlanDtlEvalDto);
			familyPlanRes.setFamilyPlanPartcpntDto(familyPlanPartcpntDtoList);
		} catch (SQLException sqlExpception) {
			DataLayerException dataLayerException = new DataLayerException(sqlExpception.getMessage());
			dataLayerException.initCause(sqlExpception);
			throw dataLayerException;
		} finally {
			try {
				if (!ObjectUtils.isEmpty(callStatement) && null != callStatement) {
					callStatement.close();
				}
				if (!ObjectUtils.isEmpty(connection) && null != connection) {
					connection.close();
				}
			} catch (SQLException e) {
				DataLayerException dataLayerException = new DataLayerException(e.toString());
				dataLayerException.initCause(e);
				LOG.error("Unable to close the connection: " + e);
			}
		}
		return familyPlanRes;
	}

	/**
	 * Method name: fetchLatestLegalStatusForCase Method Description: This
	 * method is used to get latest legal status for the case.
	 */
	@Override
	public String fetchLatestLegalStatusForCase(Long idCase) {
		if(!ObjectUtils.isEmpty(idCase)) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LegalStatus.class);
			criteria.add(Restrictions.eq("idCase", idCase));
			criteria.addOrder(Order.desc("dtLegalStatStatusDt"));
			criteria.setMaxResults(1);
			LegalStatus legalStatus = (LegalStatus) criteria.uniqueResult();
			if (!ObjectUtils.isEmpty(legalStatus)) {
				return legalStatus.getTxtLegalStatCauseNbr();
			}
		}
		return null;
	}

	/**
	 * Method Name: mapResultSetToBean Method Description: Common method to
	 * fetch Result Set from respective cursors
	 * 
	 * @param resultSet
	 * @param beanClass
	 * @return List<T>
	 */
	@SuppressWarnings("unchecked")
	private <T> List<T> mapResultSetToBean(final ResultSet resultSet, final Class<T> beanClass) {
		try {
			return (List<T>) resultSetMapper.mapToList(resultSet, beanClass);
		} catch (ResultSetMapperException e) {
			LOG.error("Unable to Map Result Set :: " + beanClass.getName());
			return new ArrayList<>();
		}
	}

	/**
	 * Method Name: legalActionPresent Method Description: This method is to 
	 * fetch whether legal action present for the case
	 * 
	 * @param resultSet
	 * @param beanClass
	 * @return List<T>
	 */
	@Override
	public Boolean legalActionPresent(Long idCase, Long idStage) {
		Long queryResult = 0L;
		Boolean isLegalActionPresent = Boolean.FALSE;
		if(!ObjectUtils.isEmpty(idCase) && !ObjectUtils.isEmpty(idStage)) {
			SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getLecalActionDetailsSql)
					.setParameter("idCase", idCase).setParameter("idStage", idStage);
			if (!ObjectUtils.isEmpty(sqlQuery.uniqueResult()) && ((BigDecimal) sqlQuery.uniqueResult()).longValue() > 0) {
				queryResult = ((BigDecimal) sqlQuery.uniqueResult()).longValue();
			}
			if (queryResult > 0) {
				isLegalActionPresent = Boolean.TRUE;
			}
		}
		return isLegalActionPresent;
	}
}
