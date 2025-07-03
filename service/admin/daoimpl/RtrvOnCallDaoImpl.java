package us.tx.state.dfps.service.admin.daoimpl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import us.tx.state.dfps.service.admin.dao.RtrvOnCallDao;
import us.tx.state.dfps.service.admin.dto.RtrvOnCallInDto;
import us.tx.state.dfps.service.admin.dto.RtrvOnCallOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * contains method implementations to check for overlap with existing on call
 * schedules and also used to fetch the counties for a particular region for an
 * on call schedule Aug 17, 2017- 5:56:34 PM © 2017 Texas Department of Family
 * and Protective Services
 */
@Repository
public class RtrvOnCallDaoImpl implements RtrvOnCallDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(RtrvOnCallDaoImpl.class);

	@Value("${RtrvOnCallDaoImpl.SQL_SELECT_STATEMENT}")
	private String SQL_SELECT_STATEMENT;

	@Value("${RtrvOnCallDaoImpl.SQL_CONSTANT_ORDER_BY}")
	private String SQL_CONSTANT_ORDER_BY;

	@Value("${RtrvOnCallDaoImpl.SQL_CONSTANT_GROUP_BY}")
	private String SQL_CONSTANT_GROUP_BY;

	@Value("${RtrvOnCallDaoImpl.SQL_COUNTY_WHERE}")
	private String SQL_COUNTY_WHERE;

	@Value("${RtrvOnCallDaoImpl.SQL_PROGRAM_WHERE}")
	private String SQL_PROGRAM_WHERE;

	@Value("${RtrvOnCallDaoImpl.SQL_TYPE_WHERE}")
	private String SQL_TYPE_WHERE;

	@Value("${RtrvOnCallDaoImpl.SQL_START_WHERE}")
	private String SQL_START_WHERE;

	@Value("${RtrvOnCallDaoImpl.SQL_START_TIME_WHERE}")
	private String SQL_START_TIME_WHERE;

	@Value("${RtrvOnCallDaoImpl.SQL_END_WHERE}")
	private String SQL_END_WHERE;

	@Value("${RtrvOnCallDaoImpl.SQL_END_TIME_WHERE}")
	private String SQL_END_TIME_WHERE;

	@Value("${RtrvOnCallDaoImpl.SQL_COUNTY_COUNT}")
	private String SQL_COUNTY_COUNT;

	@Value("${RtrvOnCallDaoImpl.SQL_ON_CALL_OVERLAP_BLOCK}")
	private String SQL_ON_CALL_OVERLAP_BLOCK;

	@Value("${RtrvOnCallDaoImpl.SQL_ON_CALL_ID_ON_CALL_WHERE}")
	private String SQL_ON_CALL_ID_ON_CALL_WHERE;

	@Value("${RtrvOnCallDaoImpl.SQL_ON_CALL_OVERLAP_SHIFT}")
	private String SQL_ON_CALL_OVERLAP_SHIFT;

	@Value("${RtrvOnCallDaoImpl.SQL_ON_CALL_ID_ON_CALL_UNION}")
	private String SQL_ON_CALL_ID_ON_CALL_UNION;

	@Value("${RtrvOnCallDaoImpl.SQL_ON_CALL_ID_ON_CALL_SHIFT_WHERE}")
	private String SQL_ON_CALL_ID_ON_CALL_SHIFT_WHERE;

	private static final String BLOCK = "BL";

	private static final String SHIFT = "SH";

	/**
	 * Method Name: getOnCallForCountyProgram Method Description:This method is
	 * used to retrieve the list of on call for a particular combination of
	 * county, region and program
	 * 
	 * @param rtrvOnCallInDto
	 *            - This dto is used to hold the input values for retrieving the
	 *            list of oncall schedule.
	 * @return List<RtrvOnCallOutDto> - This collection holds the list of on
	 *         call schedules. @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RtrvOnCallOutDto> getOnCallForCountyProgram(RtrvOnCallInDto rtrvOnCallInDto) {
		log.debug("Entering method RtrvOnCallQUERYdam in RtrvOnCallDaoImpl");
		StringBuilder hostszDynamicSQL = new StringBuilder(SQL_SELECT_STATEMENT);
		// If the counties are selected , then adding the county in the where
		// clause.
		if (rtrvOnCallInDto.getCdCountyCounter() > 0) {
			hostszDynamicSQL.append(SQL_COUNTY_WHERE);
		}
		hostszDynamicSQL.append(SQL_PROGRAM_WHERE);
		// If the type is not empty , then appending the type to the query
		if (!TypeConvUtil.isNullOrEmpty(rtrvOnCallInDto.getCdOnCallType())) {

			hostszDynamicSQL.append(" AND ");
			hostszDynamicSQL.append(SQL_TYPE_WHERE);
		}
		// If the start date is not empty , then appending the start date to the
		// query
		if (!TypeConvUtil.isNullOrEmpty(rtrvOnCallInDto.getStrOnCallStart())) {

			hostszDynamicSQL.append(" AND ");
			if (!TypeConvUtil.isNullOrEmpty(rtrvOnCallInDto.getTmOnCallStart())) {
				hostszDynamicSQL.append(SQL_START_TIME_WHERE);
			} else {
				hostszDynamicSQL.append(SQL_START_WHERE);
			}

		}
		// If the end date is not empty , then appending the end date to the
		// query
		if (!TypeConvUtil.isNullOrEmpty(rtrvOnCallInDto.getStrOnCallEnd())) {

			hostszDynamicSQL.append(" AND ");
			if (!TypeConvUtil.isNullOrEmpty(rtrvOnCallInDto.getTmOnCallEnd())) {
				hostszDynamicSQL.append(SQL_END_TIME_WHERE);
			} else {
				hostszDynamicSQL.append(SQL_END_WHERE);
			}
		} else {
			// Per legacy implementation, if only the end time is entered,
			// return no results
			if (!TypeConvUtil.isNullOrEmpty(rtrvOnCallInDto.getTmOnCallEnd())) {
				return Collections.EMPTY_LIST;
			}
		}
		hostszDynamicSQL.append(SQL_COUNTY_COUNT);
		hostszDynamicSQL.append(SQL_CONSTANT_ORDER_BY);

		SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(hostszDynamicSQL.toString())

				.addScalar("cdOnCallProgram", StandardBasicTypes.STRING)
				.addScalar("cdOnCallType", StandardBasicTypes.STRING)
				.addScalar("dtOnCallStartStr", StandardBasicTypes.STRING)
				.addScalar("dtOnCallEndStr", StandardBasicTypes.STRING).addScalar("idOnCall", StandardBasicTypes.LONG)
				.addScalar("onCallFilled", StandardBasicTypes.STRING)
				.addScalar("tsLastUpdate", StandardBasicTypes.STRING)
				.addScalar("cdOnCallCounty", StandardBasicTypes.STRING));
		// Adding the input values to the parameters in the query.
		if (rtrvOnCallInDto.getCdCountyCounter() > 0) {
			sQLQuery.setParameterList("names", rtrvOnCallInDto.getCdOnCallCounty());
		}

		if (!TypeConvUtil.isNullOrEmpty(rtrvOnCallInDto.getCdOnCallProgram())) {
			sQLQuery.setParameter("cdOnCallProgram", rtrvOnCallInDto.getCdOnCallProgram());
		}
		if (!TypeConvUtil.isNullOrEmpty(rtrvOnCallInDto.getCdOnCallType())) {
			sQLQuery.setParameter("cdOnCallType", rtrvOnCallInDto.getCdOnCallType());
		}
		if (!TypeConvUtil.isNullOrEmpty(rtrvOnCallInDto.getStrOnCallStart())) {
			if (!TypeConvUtil.isNullOrEmpty(rtrvOnCallInDto.getTmOnCallStart())) {
				sQLQuery.setParameter("dtOnCallStart",
						rtrvOnCallInDto.getStrOnCallStart() + " " + rtrvOnCallInDto.getTmOnCallStart());
			} else {
				sQLQuery.setParameter("dtOnCallStart", rtrvOnCallInDto.getStrOnCallStart());
			}
		}
		if (!TypeConvUtil.isNullOrEmpty(rtrvOnCallInDto.getStrOnCallEnd())) {
			if (!TypeConvUtil.isNullOrEmpty(rtrvOnCallInDto.getTmOnCallEnd())) {
				sQLQuery.setParameter("dtOnCallEnd",
						rtrvOnCallInDto.getStrOnCallEnd() + " " + rtrvOnCallInDto.getTmOnCallEnd());
			} else {
				sQLQuery.setParameter("dtOnCallEnd", rtrvOnCallInDto.getStrOnCallEnd());
			}
		}
		if (!TypeConvUtil.isNullOrEmpty(rtrvOnCallInDto.getCdRegion())) {
			sQLQuery.setParameter("cdRegion", rtrvOnCallInDto.getCdRegion());
		}
		sQLQuery.setResultTransformer(Transformers.aliasToBean(RtrvOnCallOutDto.class));
		List<RtrvOnCallOutDto> onCallScheduleList = (List<RtrvOnCallOutDto>) sQLQuery.list();

		log.debug("Exiting method RtrvOnCallQUERYdam in RtrvOnCallDaoImpl");
		return onCallScheduleList;
	}

	/**
	 * Method Name: checkOverLapExists Method Description:This method is used to
	 * check if the on call schedule which is tried to be modified/added
	 * overlaps with the existing on call schedule available.
	 * 
	 * @param rtrvOnCallInDto-
	 *            This dto is used to hold the input values for checking if an
	 *            overlap exists. list of oncall schedule.
	 * @return boolean - This boolean value is to determine if an overlap exists
	 *         or not.
	 */
	@SuppressWarnings("unchecked")
	public boolean checkOverLapExists(RtrvOnCallInDto rtrvOnCallInDto) {
		boolean overlapExists = false;
		Date newStartDate = rtrvOnCallInDto.getDtOnCallStart();
		Date newEndDate = rtrvOnCallInDto.getDtOnCallEnd();

		LocalDateTime newStartDateTime = newStartDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		LocalDateTime newEndDateTime = newEndDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		rtrvOnCallInDto.setDtOnCallStart(DateUtils.addMinutes(rtrvOnCallInDto.getDtOnCallStart(), 1));
		rtrvOnCallInDto.setDtOnCallEnd(DateUtils.addMinutes(rtrvOnCallInDto.getDtOnCallEnd(), -1));

		StringBuffer sqlQuery = new StringBuffer(SQL_ON_CALL_OVERLAP_SHIFT);
		if (!TypeConvUtil.isNullOrEmpty(rtrvOnCallInDto.getIdOnCall())) {
			sqlQuery.append(" ").append(SQL_ON_CALL_ID_ON_CALL_SHIFT_WHERE);
		}

		sqlQuery.append(" " + SQL_ON_CALL_ID_ON_CALL_UNION);
		sqlQuery.append(" " + SQL_ON_CALL_OVERLAP_BLOCK);

		if (!TypeConvUtil.isNullOrEmpty(rtrvOnCallInDto.getIdOnCall())) {
			sqlQuery.append(" ").append(SQL_ON_CALL_ID_ON_CALL_WHERE);
		}

		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(sqlQuery.toString())
				.addScalar("idOnCall", StandardBasicTypes.LONG));

		if (!TypeConvUtil.isNullOrEmpty(rtrvOnCallInDto.getIdOnCall())) {
			sQLQuery1.setParameter("idOnCall", rtrvOnCallInDto.getIdOnCall());
		}
		sQLQuery1.setParameterList("names", rtrvOnCallInDto.getCdOnCallCounty());
		sQLQuery1.setParameter("cdOnCallProgram", rtrvOnCallInDto.getCdOnCallProgram());
		sQLQuery1.setParameter("dtEnd", rtrvOnCallInDto.getDtOnCallEnd());
		sQLQuery1.setParameter("dtStart", rtrvOnCallInDto.getDtOnCallStart());
		sQLQuery1.setParameter("cdOnCallType", rtrvOnCallInDto.getCdOnCallType());

		if (SHIFT.equals(rtrvOnCallInDto.getCdOnCallType())) {
			if (newStartDateTime.getHour() > newEndDateTime.getHour()
					|| (newStartDateTime.getHour() == newEndDateTime.getHour()
							&& (newStartDateTime.getMinute() > newEndDateTime.getMinute()))) {
				newStartDateTime = newStartDateTime.plusDays(1l);
			}
			LocalDateTime modifiedDate = LocalDateTime.of(newStartDateTime.getYear(), newStartDateTime.getMonthValue(),
					newStartDateTime.getDayOfMonth(), newEndDateTime.getHour(), newEndDateTime.getMinute());
			modifiedDate = modifiedDate.minusMinutes(1l);
			sQLQuery1.setParameter("dtRangeStart", Date.from(modifiedDate.atZone(ZoneId.systemDefault()).toInstant()));
		} else if (BLOCK.equals(rtrvOnCallInDto.getCdOnCallType())) {
			// consider the entire 24 hour period in case of block
			newStartDateTime = newStartDateTime.plusDays(1l);
			newStartDateTime = newStartDateTime.plusMinutes(1l);
			sQLQuery1.setParameter("dtRangeStart",
					Date.from(newStartDateTime.atZone(ZoneId.systemDefault()).toInstant()));
		}
		List<RtrvOnCallOutDto> overlapList = (List<RtrvOnCallOutDto>) sQLQuery1.list();
		if (!CollectionUtils.isEmpty(overlapList)) {
			overlapExists = true;
		}
		return overlapExists;
	}
}
