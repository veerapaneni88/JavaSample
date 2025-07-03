package us.tx.state.dfps.service.admin.daoimpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CnsrvtrshpRemoval;
import us.tx.state.dfps.service.admin.dao.FetchEventStatusDao;
import us.tx.state.dfps.service.admin.dto.FetchEventStatusdiDto;
import us.tx.state.dfps.service.admin.dto.FetchEventStatusdoDto;
import us.tx.state.dfps.service.admin.dto.StageEventdiDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.contact.dto.FetchEventStatusDto;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.xmlstructs.inputstructs.InvestigationConclusionDto;
import us.tx.state.dfps.xmlstructs.outputstructs.CaseEventDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ResultSetDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This class
 * fetches event details Aug 5, 2017- 3:12:14 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class FetchEventStatusDaoImpl implements FetchEventStatusDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	@Value("${FetchEventStatusDaoImpl.fourTableBaseSql}")
	private transient String fourTableBaseSql;

	@Autowired
	@Value("${FetchEventStatusDaoImpl.sqlThreeTablesStmtCaseSearch}")
	private transient String sqlThreeTablesStmtCaseSearch;

	@Autowired
	@Value("${FetchEventStatusDaoImpl.threeTableBaseSql}")
	private transient String threeTableBaseSql;

	@Autowired
	@Value("${Ccmn87dDaoImpl.getRemovalDate}")
	private transient String getRemovalDate;

	@Autowired
	@Value("${Ccmn87dDaoImpl.selectRemovalDate}")
	private transient String selectRemovalDate;

	/**
	 * Method Name: searchEvents Method Description: The searchEvents method
	 * returns search events information.
	 * 
	 * @param fetchEventStatusdiDto
	 * @return FetchEventStatusdoDto
	 */
	@Override
	public FetchEventStatusdoDto searchEvents(FetchEventStatusdiDto fetchEventStatusdiDto) {

		String selectSql = getSelectSql(fetchEventStatusdiDto);
		SQLQuery sqlQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectSql)
				.setResultTransformer(Transformers.aliasToBean(FetchEventStatusdoDto.class)));
		sqlQuery1.addScalar("szCdEventStatus", StandardBasicTypes.STRING);
		sqlQuery1.addScalar("szCdEventType", StandardBasicTypes.STRING);
		sqlQuery1.addScalar("szCdStage", StandardBasicTypes.STRING);
		sqlQuery1.addScalar("dtDtEventOccurred", StandardBasicTypes.DATE);
		sqlQuery1.addScalar("szCdStageReasonClosed", StandardBasicTypes.STRING);
		sqlQuery1.addScalar("ulIdCase", StandardBasicTypes.LONG);
		sqlQuery1.addScalar("ulIdEvent", StandardBasicTypes.LONG);
		sqlQuery1.addScalar("ulIdStage", StandardBasicTypes.LONG);
		sqlQuery1.addScalar("szNmStage", StandardBasicTypes.STRING);
		sqlQuery1.addScalar("szScrCaseWorker", StandardBasicTypes.STRING);
		sqlQuery1.addScalar("szTxtEventDescr", StandardBasicTypes.STRING);
		sqlQuery1.addScalar("szCdTask", StandardBasicTypes.STRING);
		sqlQuery1.addScalar("dtDtEventCreated", StandardBasicTypes.DATE);
		String cReqFuncCd = fetchEventStatusdiDto.getArchInputStruct().getCreqFuncCd();
		if (ServiceConstants.REQ_FUNC_CD_ADD.equals(cReqFuncCd))
			sqlQuery1.addScalar("bindCaseSensitive", StandardBasicTypes.STRING);
		List<FetchEventStatusdoDto> liFetchEventStatusdoDto = (List<FetchEventStatusdoDto>) sqlQuery1.list();
		FetchEventStatusdoDto fetchEventStatusdoDto = new FetchEventStatusdoDto();
		ResultSetDto resultSetDto = new ResultSetDto();
		ArrayList<CaseEventDto> liCaseEventDto = new ArrayList<>();
		if (liFetchEventStatusdoDto.size() > 0 && liFetchEventStatusdoDto != null)
			for (FetchEventStatusdoDto fetchEventStatusdoDto1 : liFetchEventStatusdoDto) {

				CaseEventDto caseEvent = getDataFromResultSet(fetchEventStatusdoDto1);
				liCaseEventDto.add(caseEvent);
			}
		resultSetDto.setResultSetList(liCaseEventDto);

		fetchEventStatusdoDto.setRowccmn87doArrayDto(resultSetDto);
		return fetchEventStatusdoDto;

	}

	private CaseEventDto getDataFromResultSet(FetchEventStatusdoDto fetchEventStatusdoDto) {
		int uIdEvent = 0;
		String txtEventDescription = ServiceConstants.EMPTY_STRING;

		CaseEventDto caseEventDto = new CaseEventDto();
		caseEventDto.setSzCdEventStatus(fetchEventStatusdoDto.getSzCdEventStatus());
		caseEventDto.setSzCdEventType(fetchEventStatusdoDto.getSzCdEventType());
		caseEventDto.setSzCdStage(fetchEventStatusdoDto.getSzCdStage());

		caseEventDto.setDtDtEventOccurred(DateUtils.toCastorDate(fetchEventStatusdoDto.getDtDtEventOccurred()));
		caseEventDto.setSzCdStageReasonClosed(fetchEventStatusdoDto.getSzCdStageReasonClosed());
		if(!ObjectUtils.isEmpty(fetchEventStatusdoDto.getUlIdCase()))
			caseEventDto.setUlIdCase(fetchEventStatusdoDto.getUlIdCase().intValue());
		uIdEvent = fetchEventStatusdoDto.getUlIdEvent().intValue();
		caseEventDto.setUlIdEvent(uIdEvent);
		caseEventDto.setUlIdStage(fetchEventStatusdoDto.getUlIdStage().intValue());
		caseEventDto.setSzNmStage(fetchEventStatusdoDto.getSzNmStage());
		caseEventDto.setSzScrCaseWorker(fetchEventStatusdoDto.getSzScrCaseWorker());
		caseEventDto.setDtDtEventCreated(DateUtils.toCastorDate(fetchEventStatusdoDto.getDtDtEventCreated()));
		txtEventDescription = fetchEventStatusdoDto.getSzTxtEventDescr();

		Date removalDate = getRemovalDate(uIdEvent);

		if (!TypeConvUtil.isNullOrEmpty(removalDate)) {
			// txtEventDescription = txtEventDescription + " Removal Date:" +
			// DateUtils.slashFormat.format( removalDate );
			txtEventDescription = txtEventDescription + " Removal Date:" + DateUtils.formatDatetoString(removalDate);

		}
		caseEventDto.setSzTxtEventDescr(txtEventDescription);
		caseEventDto.setSzCdTask(fetchEventStatusdoDto.getSzCdTask());
		caseEventDto.setBIndCaseSensitive(fetchEventStatusdoDto.getBindCaseSensitive());

		return caseEventDto;
	}

	/**
	 * 
	 * Method Name: getRemovalDate Method Description: Gets Conservatorship
	 * removal date
	 * 
	 * @return Date
	 * @param ulIdEvent
	 * @
	 */
	@Override
	public Date getRemovalDate(Integer uIdEvent) {
		Integer uIdPerson = ServiceConstants.Zero;
		String szCdLegalStatStatus = ServiceConstants.EMPTY_STRING;
		Date dtDateLegalStatStatusDt = null;
		Date removalDate = null;

		SQLQuery sqlQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getRemovalDate)
				.setResultTransformer(Transformers.aliasToBean(FetchEventStatusDto.class)));
		sqlQuery1.addScalar("idPerson", StandardBasicTypes.LONG);
		sqlQuery1.addScalar("cdLegalStatStatus", StandardBasicTypes.STRING);
		sqlQuery1.addScalar("dtLegalStatStatusDt", StandardBasicTypes.DATE);

		sqlQuery1.setParameter("idLegalStatEvent", uIdEvent);

		FetchEventStatusDto fetchEventStatusDto = (FetchEventStatusDto) sqlQuery1.uniqueResult();

		if (fetchEventStatusDto != null)

		{
			uIdPerson = fetchEventStatusDto.getIdPerson().intValue();
			szCdLegalStatStatus = fetchEventStatusDto.getCdLegalStatStatus();
			dtDateLegalStatStatusDt = fetchEventStatusDto.getDtLegalStatStatusDt();
		}

		// get Removal Date
		SimpleDateFormat format = new SimpleDateFormat(ServiceConstants.DATE_FORMAT_ddMMMyyyy);

		if (ServiceConstants.CLEGSTAT_020.equals(szCdLegalStatStatus)
				&& !TypeConvUtil.isNullOrEmpty(dtDateLegalStatStatusDt) && uIdPerson != ServiceConstants.Zero) {
			SQLQuery sqlQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectRemovalDate)
					.setResultTransformer(Transformers.aliasToBean(CnsrvtrshpRemoval.class)));
			sqlQuery.setParameter("idperson", uIdPerson);
			sqlQuery.setParameter("idPers", uIdPerson);

			sqlQuery.setParameter("dtLegalStatus", format.format(dtDateLegalStatStatusDt));
			sqlQuery.setParameter("idpersn", uIdPerson);
			sqlQuery.setParameter("dtLegalStats", format.format(dtDateLegalStatStatusDt));
			sqlQuery.addScalar("dtRemoval", StandardBasicTypes.DATE);

			List<CnsrvtrshpRemoval> cnsrvtrshpRemoval = (List<CnsrvtrshpRemoval>) sqlQuery.list();

			if (!TypeConvUtil.isNullOrEmpty(cnsrvtrshpRemoval))
				for (CnsrvtrshpRemoval cnsrvtrshpRemvl : cnsrvtrshpRemoval) {
					removalDate = cnsrvtrshpRemvl.getDtRemoval();
					DateUtils.toCastorDate(cnsrvtrshpRemvl.getDtRemoval());
				}
		}
		return removalDate;
	}

	/**
	 * 
	 * Method Name: getSelectSql Method Description: The method returns the sql
	 * 
	 * @param fetchEventStatusdiDto
	 * @return String
	 */
	private String getSelectSql(FetchEventStatusdiDto fetchEventStatusdiDto) {
		StringBuilder selectSql = new StringBuilder();
		boolean threeTable = false;
		String cReqFuncCd = fetchEventStatusdiDto.getArchInputStruct().getCreqFuncCd();
		if (ServiceConstants.REQ_FUNC_CD_ADD.equals(cReqFuncCd)) {
			selectSql.append(fourTableBaseSql);
		} else if (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(cReqFuncCd)) {
			threeTable = true;

			if (!TypeConvUtil.isNullOrEmpty(fetchEventStatusdiDto.getSzAomActionEcs())
					&& fetchEventStatusdiDto.getSzAomActionEcs().equals(ServiceConstants.AOM_ACTION_ECS)) {
				selectSql.append(sqlThreeTablesStmtCaseSearch);
			} else {
				selectSql.append(threeTableBaseSql);
			}

		} else {
			throw new DataLayerException(ServiceConstants.ARC_ERR_BAD_FUNC_CD);
		}

		if (!threeTable && !TypeConvUtil.isNullOrEmpty(fetchEventStatusdiDto.getUlIdPerson())) {
			selectSql.append(ServiceConstants.PERSON_MERGE_VIEW_SQL);
			selectSql.append(ServiceConstants.CHAR_SPACE);
			selectSql.append(ServiceConstants.WHERE);
			selectSql.append(ServiceConstants.CHAR_SPACE);
			selectSql.append(ServiceConstants.WHERE_ID_PERSON_INPUT);
			selectSql.append(fetchEventStatusdiDto.getUlIdPerson());
			selectSql.append(ServiceConstants.CHAR_SPACE);
			selectSql.append(ServiceConstants.AND);

		} else {
			selectSql.append(ServiceConstants.CHAR_SPACE);
			selectSql.append(ServiceConstants.WHERE);
		}
		InvestigationConclusionDto investigationConclusionDto = fetchEventStatusdiDto.getInvestigationConclusionDto();

		if (!TypeConvUtil.isNullOrEmpty(investigationConclusionDto)) {

			Integer countEvent = ServiceConstants.Zero;
			Integer countStage = ServiceConstants.Zero;

			Integer len = investigationConclusionDto.getInvestigateList().size();
			Integer eventTypeExists = ServiceConstants.Zero;
			Integer stageExists = ServiceConstants.Zero;

			for (StageEventdiDto stageEventdiDto : investigationConclusionDto.getInvestigateList()) {
				if (!TypeConvUtil.isNullOrEmpty(stageEventdiDto.getSzCdEventType())) {
					eventTypeExists++;
				}

				if (!TypeConvUtil.isNullOrEmpty(stageEventdiDto.getSzCdStage())) {
					stageExists++;
				}
			}

			if (eventTypeExists > ServiceConstants.Zero) {
				selectSql.append(ServiceConstants.SPACE);
				selectSql.append(ServiceConstants.WHERE_CD_EVENT_TYPE);

				for (StageEventdiDto stageEventdiDto : investigationConclusionDto.getInvestigateList())

				{
					if (!TypeConvUtil.isNullOrEmpty(stageEventdiDto.getSzCdEventType())) {
						selectSql.append(ServiceConstants.SINGLE_QUOTES);
						selectSql.append(stageEventdiDto.getSzCdEventType());
						if (countEvent < len - 1)
							selectSql.append("',");
						else
							selectSql.append(ServiceConstants.SINGLE_QUOTES);
					}

					countEvent++;

				}

				selectSql.append(ServiceConstants.SQUARE_BRACKET_CLOSE).append(ServiceConstants.SPACE)
						.append(ServiceConstants.AND);
			}

			if (stageExists > ServiceConstants.Zero) {
				selectSql.append(ServiceConstants.CHAR_SPACE);
				selectSql.append(ServiceConstants.WHERE_CD_STAGE);

				// mycode
				for (StageEventdiDto stageEventdiDto : investigationConclusionDto.getInvestigateList())

				{
					if (!TypeConvUtil.isNullOrEmpty(stageEventdiDto.getSzCdStage())) {
						selectSql.append(ServiceConstants.SINGLE_QUOTES);
						selectSql.append(stageEventdiDto.getSzCdStage());
						if (countStage < len - 1)
							selectSql.append("',");
						else
							selectSql.append(ServiceConstants.SINGLE_QUOTES);
					}

					countStage++;

				}
				selectSql.append(ServiceConstants.SQUARE_BRACKET_CLOSE).append(ServiceConstants.CHAR_SPACE)
						.append(ServiceConstants.AND);
			}

		}
		String szCdTask = fetchEventStatusdiDto.getSzCdTask();
		if (!TypeConvUtil.isNullOrEmpty(szCdTask)) {
			selectSql.append(ServiceConstants.CHAR_SPACE);
			selectSql.append(ServiceConstants.WHERE_CD_TASK);
			selectSql.append(szCdTask);
			selectSql.append(ServiceConstants.CHAR_SPACE);
			selectSql.append(ServiceConstants.AND);

		}

		if (!TypeConvUtil.isNullOrEmpty(fetchEventStatusdiDto.getUlIdCase())) {
			selectSql.append(ServiceConstants.CHAR_SPACE);
			selectSql.append(ServiceConstants.WHERE_ID_CASE);
			selectSql.append(String.valueOf(fetchEventStatusdiDto.getUlIdCase()));
			selectSql.append(ServiceConstants.CHAR_SPACE);
			selectSql.append(ServiceConstants.AND);

		}

		if (!TypeConvUtil.isNullOrEmpty(fetchEventStatusdiDto.getUlIdEventPerson())) {
			selectSql.append(ServiceConstants.CHAR_SPACE);
			selectSql.append(ServiceConstants.WHERE_ID_EVENT_PERSON);
			selectSql.append(String.valueOf(fetchEventStatusdiDto.getUlIdEventPerson()));
			selectSql.append(ServiceConstants.CHAR_SPACE);
			selectSql.append(ServiceConstants.AND);

		}

		if (!TypeConvUtil.isNullOrEmpty(fetchEventStatusdiDto.getUlIdSituation())) {
			selectSql.append(ServiceConstants.CHAR_SPACE);
			selectSql.append(ServiceConstants.WHERE_ID_SITUATION);
			selectSql.append(String.valueOf(fetchEventStatusdiDto.getUlIdSituation()));
			selectSql.append(ServiceConstants.CHAR_SPACE);
			selectSql.append(ServiceConstants.AND);

		}

		if (!TypeConvUtil.isNullOrEmpty(fetchEventStatusdiDto.getUlIdStage())) {
			selectSql.append(ServiceConstants.CHAR_SPACE);
			selectSql.append(ServiceConstants.WHERE_ID_EVENT_STAGE);
			selectSql.append(String.valueOf(fetchEventStatusdiDto.getUlIdStage()));
			selectSql.append(ServiceConstants.CHAR_SPACE);
			selectSql.append(ServiceConstants.AND);

		}
		Date dtScrDtStartDt = fetchEventStatusdiDto.getDtScrDtStartDt();
		String pattern = ServiceConstants.DATE_FORMAT_ddMMMyyyy;
		SimpleDateFormat frmat = new SimpleDateFormat(pattern);

		if (!TypeConvUtil.isNullOrEmpty(dtScrDtStartDt)) {
			selectSql.append(ServiceConstants.CHAR_SPACE);
			selectSql.append(ServiceConstants.WHERE_DT_EVENT_OCCURRED_FROM);
			selectSql.append(
					ServiceConstants.SINGLE_QUOTES + frmat.format(dtScrDtStartDt) + ServiceConstants.SINGLE_QUOTES);
			selectSql.append(ServiceConstants.CHAR_SPACE);
			selectSql.append(ServiceConstants.AND);

		}
		Date dtScrDtEventEnd = fetchEventStatusdiDto.getDtScrDtEventEnd();
		if (!TypeConvUtil.isNullOrEmpty(dtScrDtEventEnd)) {
			selectSql.append(ServiceConstants.CHAR_SPACE);
			selectSql.append(ServiceConstants.WHERE_DT_EVENT_OCCURRED_TO);
			selectSql.append(
					ServiceConstants.SINGLE_QUOTES + frmat.format(dtScrDtEventEnd) + ServiceConstants.SINGLE_QUOTES);
			selectSql.append(ServiceConstants.CHAR_SPACE);
			selectSql.append(ServiceConstants.AND);
		}

		// Chop the last AND clause off because we do not need it.
		selectSql.delete(selectSql.length() - ServiceConstants.AND.length(), selectSql.length());
		selectSql.append(ServiceConstants.ORDER_BY_SQL);
		if (selectSql.toString().matches(".*" + "AND" + "[ ]*" + "ORDER" + ".*")) {
			String finalString = selectSql.toString().replace("ANDORDER", "ORDER");
			return finalString;
		}
		return selectSql.toString();
	}

	/**
	 * 
	 * Method Name: searchEventsWithPagination Method Description: The
	 * searchEventsWithPagination method returns CCMN87DO search events
	 * information with pagination details.
	 * 
	 * @param fetchEventStatusdiDto
	 * @return FetchEventStatusdoDto
	 */
	@Override
	public FetchEventStatusdoDto searchEventsWithPagination(FetchEventStatusdiDto fetchEventStatusdiDto) {

		FetchEventStatusdoDto fetchEventStatusdoDto = new FetchEventStatusdoDto();

		String selectSql = getSelectSql(fetchEventStatusdiDto);
		SQLQuery sqlQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(selectSql)
				.setResultTransformer(Transformers.aliasToBean(FetchEventStatusdoDto.class)));
		sqlQuery1.addScalar("szCdEventStatus", StandardBasicTypes.STRING);
		sqlQuery1.addScalar("szCdEventType", StandardBasicTypes.STRING);
		sqlQuery1.addScalar("szCdStage", StandardBasicTypes.STRING);
		sqlQuery1.addScalar("dtDtEventOccurred", StandardBasicTypes.DATE);
		sqlQuery1.addScalar("szCdStageReasonClosed", StandardBasicTypes.STRING);
		sqlQuery1.addScalar("ulIdCase", StandardBasicTypes.LONG);
		sqlQuery1.addScalar("ulIdEvent", StandardBasicTypes.LONG);
		sqlQuery1.addScalar("ulIdStage", StandardBasicTypes.LONG);
		sqlQuery1.addScalar("szNmStage", StandardBasicTypes.STRING);
		sqlQuery1.addScalar("szScrCaseWorker", StandardBasicTypes.STRING);
		sqlQuery1.addScalar("szTxtEventDescr", StandardBasicTypes.STRING);
		sqlQuery1.addScalar("szCdTask", StandardBasicTypes.STRING);
		sqlQuery1.addScalar("dtDtEventCreated", StandardBasicTypes.DATE);
		String cReqFuncCd = fetchEventStatusdiDto.getArchInputStruct().getCreqFuncCd();
		if (ServiceConstants.REQ_FUNC_CD_ADD.equals(cReqFuncCd))
			sqlQuery1.addScalar("bindCaseSensitive", StandardBasicTypes.STRING);
		List<FetchEventStatusdoDto> liFetchEventStatusdoDto = (List<FetchEventStatusdoDto>) sqlQuery1.list();

		if (TypeConvUtil.isNullOrEmpty(liFetchEventStatusdoDto)) {
			throw new DataNotFoundException(messageSource.getMessage("Carc06d.not.found.IdPerson", null, Locale.US));

		}

		int pageNum = fetchEventStatusdiDto.getArchInputStruct().getUsPageNbr();
		if (pageNum <= 0)
			pageNum = 1;

		int pageSize = fetchEventStatusdiDto.getArchInputStruct().getUlPageSizeNbr();
		if (pageSize <= 0)
			pageSize = 50;

		int firstRow = 1 + ((pageNum - 1) * pageSize);
		int lastRow = pageNum * pageSize;
		int index = 0;
		ArrayList<CaseEventDto> liCaseEventDto = new ArrayList<>();
		ResultSetDto resultSetDto = new ResultSetDto();

		if (!TypeConvUtil.isNullOrEmpty(liFetchEventStatusdoDto)) {
			for (FetchEventStatusdoDto fetchEventStatusdoDto1 : liFetchEventStatusdoDto) {
				index++;

				if (index <= lastRow && index >= firstRow) {
					CaseEventDto caseEvent = getDataFromResultSet(fetchEventStatusdoDto1);
					liCaseEventDto.add(caseEvent);
				}
			}
		}
		// pagination logic

		resultSetDto.setResultSetList(liCaseEventDto);
		fetchEventStatusdoDto.setRowccmn87doArrayDto(resultSetDto);
		return fetchEventStatusdoDto;

	}

}
