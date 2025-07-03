package us.tx.state.dfps.service.admin.daoimpl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionImpl;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.service.admin.dao.LegalActionEventDao;
import us.tx.state.dfps.service.admin.dto.LegalActionEventInDto;
import us.tx.state.dfps.service.admin.dto.LegalActionEventOutDto;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.common.response.LegalActionsRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<DAO Impl
 * for fetching legal event details> Aug 8, 2017- 4:10:19 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class LegalActionEventDaoImpl implements LegalActionEventDao {

	/** The message source. */
	@Autowired
	MessageSource messageSource;

	/** The session factory. */
	@Autowired
	private SessionFactory sessionFactory;

	/** The get legal event dtls. */
	@Value("${LegalActionEventDaoImpl.getLegalEventDtls}")
	private transient String getLegalEventDtls;

	@Value("${LegalActionEventDaoImpl.getMostRecentFDTCSubtype}")
	private transient String getMostRecentFDTCSubtype;

	@Value("${LegalActionEventDaoImpl.getOpenFBSSStage}")
	private transient String getOpenFBSSStage;

	@Value("${LegalActionEventDaoImpl.getLegalActionRelFictiveKin}")
	private transient String getLegalActionRelFictiveKin;

	@Value("${LegalActionEventDaoImpl.getLegalActionType}")
	private transient String getLegalActionType;

	@Value("${LegalActionEventDaoImpl.getCCORCCVSLegalAction}")
	private transient String getCCORCCVSLegalAction;

	@Value("${LegalActionEventDaoImpl.selectLatestLegalActionOutcome}")
	private String selectLatestLegalActionOutcome;
	
	/** The Constant log. */
	private static final Logger log = Logger.getLogger(LegalActionEventDaoImpl.class);

	public LegalActionEventDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getLegalEventDtls Method Description: This method will get
	 * data from LEGAL_ACTION and EVENT table.
	 * 
	 * @param pInputDataRec
	 * @return List<LegalActionEventOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<LegalActionEventOutDto> getLegalEventDtls(LegalActionEventInDto pInputDataRec) {
		log.debug("Entering method LegalActionEventQUERYdam in LegalActionEventDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getLegalEventDtls)
				.setResultTransformer(Transformers.aliasToBean(LegalActionEventOutDto.class)));
		sQLQuery1.addScalar("idLegalActEvent", StandardBasicTypes.LONG)
				.addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdLegalActAction", StandardBasicTypes.STRING)
				.addScalar("cdLegalActActnSubtype", StandardBasicTypes.STRING)
				.addScalar("cdLegalActOutcome", StandardBasicTypes.STRING)
				.addScalar("cdQrtpCourtStatus",StandardBasicTypes.STRING)
				.addScalar("dtLegalActDateFiled", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtLegalActOutcomeDt", StandardBasicTypes.TIMESTAMP)
				.addScalar("indLegalActDocsNCase", StandardBasicTypes.STRING)
				.addScalar("legalActComment", StandardBasicTypes.STRING)
				.setParameter("hI_ulIdStage", pInputDataRec.getIdStage());
		List<LegalActionEventOutDto> liClssb2doDto = new ArrayList<>();
		liClssb2doDto = (List<LegalActionEventOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liClssb2doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Clssb2dDaoImpl.not.found.legalevent", null, Locale.US));
		}
		log.debug("Exiting method LegalActionEventQUERYdam in LegalActionEventDaoImpl");
		return liClssb2doDto;
	}

	/**
	 * Gets the most recent FDTC subtype.
	 *
	 * @param personId
	 *            the person id
	 * @return List<LegalActionEventOutDto> the most recent FDTC subtype @ the
	 * service exception
	 */
	public List<LegalActionEventOutDto> getMostRecentFDTCSubtype(LegalActionEventInDto legalActionEventInDto) {
		List<LegalActionEventOutDto> legalActionEventOutDtoList = new ArrayList<>();
		Query mostRecentFDTCSubtype = sessionFactory.getCurrentSession().createSQLQuery(getMostRecentFDTCSubtype)
				.addScalar("idLegalActEvent", StandardBasicTypes.LONG)
				.addScalar("cdLegalActActnSubtype", StandardBasicTypes.STRING)
				.addScalar("dtLegalActOutcomeDt", StandardBasicTypes.DATE).addScalar("idStage", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(LegalActionEventOutDto.class));
		mostRecentFDTCSubtype.setParameter("idPerson", legalActionEventInDto.getIdPerson());
		log.info("getMostRecentFDTCSubtype:getMostRecentFDTCSubtype: " + getMostRecentFDTCSubtype);
		legalActionEventOutDtoList = mostRecentFDTCSubtype.list();
		return legalActionEventOutDtoList;
	}

	/**
	 * Gets the open FBSS stage.
	 *
	 * @param caseId
	 *            the case id
	 * @return List the open FBSS stage @ the service exception
	 */
	public List<StageDto> getOpenFBSSStage(LegalActionEventInDto legalActionEventInDto) {
		List<StageDto> stageDtoList = new ArrayList<>();
		Query openFBSSStage = (Query) sessionFactory.getCurrentSession().createSQLQuery(getOpenFBSSStage)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("cdStage", StandardBasicTypes.STRING)
				.addScalar("dtStageClose", StandardBasicTypes.TIMESTAMP)
				.setResultTransformer(Transformers.aliasToBean(StageDto.class));
		openFBSSStage.setParameter("caseId", legalActionEventInDto.getIdCase());
		log.info("getOpenFBSSStage:openFBSSStage: " + openFBSSStage);
		stageDtoList = openFBSSStage.list();
		return stageDtoList;
	}

	/**
	 * Gets the legal action rel fictive kin. This method fetches Legal Action
	 * Date of PMC to Relative or Fictive Kin
	 *
	 * @param idStage
	 *            the id stage
	 * @return LegalActionEventOutDto the legal action rel fictive kin @ the
	 * service exception
	 */
	public LegalActionEventOutDto getLegalActionRelFictiveKin(LegalActionEventInDto legalActionEventInDto) {
		LegalActionEventOutDto legalActionEventOutDto = new LegalActionEventOutDto();
		Date legalActionDate = null;
		Query legalActionPMCRelFIC = sessionFactory.getCurrentSession().createSQLQuery(getLegalActionRelFictiveKin);
		legalActionPMCRelFIC.setParameter("idStage", legalActionEventInDto.getIdStage());
		log.info("getLegalActionRelFictiveKin:legalActionPMCRelFIC: " + legalActionPMCRelFIC);
		legalActionDate = (Date) legalActionPMCRelFIC.uniqueResult();
		legalActionEventOutDto.setDtLegalActOutcomeDt(legalActionDate);
		return legalActionEventOutDto;
	}

	/**
	 * This is a method to set all input parameters(of the stored procedure).
	 */
	public LegalActionsRes executeStoredProc(List<Object> arrayList) {
		SessionImpl sessionImpl = (SessionImpl) sessionFactory.getCurrentSession();
		Connection connection = sessionImpl.connection();
		ErrorDto errorDto = new ErrorDto();
		CallableStatement callStatement = null;
		LegalActionsRes response = new LegalActionsRes();
		try {
			String procedure = "{call " + "INSERT_LEGAL_ACTION_AUDIT" + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)" + "}";
			callStatement = connection.prepareCall(procedure);
			callStatement = setCallableStatement(arrayList, callStatement);
			callStatement.execute();
		} catch (Exception e) {
			errorDto.setErrorMsg(
					"Validating Name failed. Please contact the CSC and provide them with the following information: Common Application database is down.");
			errorDto.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setErrorDto(errorDto);
		}
		return response;
	}

	/**
	 * 
	 * Method Name: getLegalActionType Method Description:CSESD6D
	 * 
	 * @param idPerson
	 * @param idCase
	 * @param idStage
	 * @return LegalActionEventOutDto
	 */
	public LegalActionEventOutDto getLegalActionType(Long idPerson, Long idCase, Long idStage) {
		Query legalActionType = sessionFactory.getCurrentSession().createSQLQuery(getLegalActionType)
				.addScalar("cdLegalActActnSubtype", StandardBasicTypes.STRING)
				.addScalar("cdLegalActAction", StandardBasicTypes.STRING).setParameter("idPerson", idPerson)
				.setParameter("idCase", idCase).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(LegalActionEventOutDto.class));

		log.info("getMostRecentFDTCSubtype:getMostRecentFDTCSubtype: " + getMostRecentFDTCSubtype);
		LegalActionEventOutDto legalActionEventOutDto = (LegalActionEventOutDto) legalActionType.uniqueResult();
		return legalActionEventOutDto;
	}


	/**
	 * Method Name: getCCORLegalAction
	 * Method Description: gets the most recent CCOR Legal action
	 *
	 * @param idPerson
	 * @param idCase
	 * @param idStage
	 * @return LegalActionEventOutDto
	 */
	@Override
	public LegalActionEventOutDto getCCORCCVSLegalAction(Long idPerson, Long idCase, Long idStage) {
		Query legalActionType = sessionFactory.getCurrentSession().createSQLQuery(getCCORCCVSLegalAction)
				.addScalar("cdLegalActOutcome", StandardBasicTypes.STRING)
				.addScalar("cdQrtpCourtStatus",StandardBasicTypes.STRING)
				.addScalar("cdLegalActAction", StandardBasicTypes.STRING).setParameter("idPerson", idPerson)
				.setParameter("idCase", idCase).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(LegalActionEventOutDto.class));

		log.info("getCCORLegalAction: " + getCCORCCVSLegalAction);
		LegalActionEventOutDto legalActionEventOutDto = (LegalActionEventOutDto) legalActionType.uniqueResult();
		return legalActionEventOutDto;
	}


	/**
	 * This is a private method to set all input parameters(of the stored
	 * procedure) in the CallableStatement
	 * 
	 * @param ArrayList
	 *            - inputValues
	 * @param CallableStatement
	 *            - callStmt
	 * @throws ParseException
	 * @exception -
	 *                SQLException
	 */
	@SuppressWarnings("rawtypes")
	private CallableStatement setCallableStatement(List<Object> inputValues, CallableStatement callStmt)
			throws SQLException, ParseException {
		int i = 1;
		for (Object val : inputValues) {
			Class valClass = val.getClass();
			if (Integer.class == valClass) {
				callStmt.setInt(i, ((Integer) val).intValue());
			} else if (String.class == valClass) {
				if (isValidDate((String) val)) {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					Date dt = dateFormat.parse((String) val);
					java.sql.Date sqlDt = new java.sql.Date(dt.getTime());
					callStmt.setDate(i, sqlDt);
				} else {
					callStmt.setString(i, (String) val);
				}
			}
			i++;
		}

		return callStmt;

	}

	public boolean isValidDate(String inDate) {

		if (inDate == null)
			return false;

		// set the format to use as a constructor argument
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		if (inDate.trim().length() != dateFormat.toPattern().length())
			return false;

		dateFormat.setLenient(false);

		try {
			// parse the inDate parameter
			dateFormat.parse(inDate.trim());
		} catch (ParseException pe) {
			return false;
		}
		return true;
	}
	
	/**
	 * Method Name: selectLatestLegalActionOutcome
	 * Method Description: selects the most recent outcome for Legal action
	 *
	 * @param legalActionEventInDto
	 * @return LegalActionEventOutDto
	 */
	
	@Override
	public LegalActionEventOutDto selectLatestLegalActionOutcome(LegalActionEventInDto legalActionEventInDto) {

		SQLQuery sqlQuery = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(selectLatestLegalActionOutcome)
				.addScalar("cdLegalActOutcome", StandardBasicTypes.STRING)
				.addScalar("cdQrtpCourtStatus",StandardBasicTypes.STRING)
				.addScalar("cdLegalActAction", StandardBasicTypes.STRING)
				.setParameter("idPerson", legalActionEventInDto.getIdPerson())
				.setParameter("idCase", legalActionEventInDto.getIdCase())
				.setParameter("idStage", legalActionEventInDto.getIdStage())
				.setResultTransformer(Transformers.aliasToBean(LegalActionEventOutDto.class)));

		LegalActionEventOutDto legalActionEventOutDto = (LegalActionEventOutDto) sqlQuery.uniqueResult();
		return legalActionEventOutDto;

	}
}
