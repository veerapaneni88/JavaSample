package us.tx.state.dfps.service.admin.daoimpl;

import java.util.Date;
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

import us.tx.state.dfps.service.admin.dao.EligibilityByPersonDao;
import us.tx.state.dfps.service.admin.dto.EligibilityByPersonInDto;
import us.tx.state.dfps.service.admin.dto.EligibilityByPersonOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Caud18dDao
 * impl implementation Aug 12, 2017- 5:03:59 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class EligibilityByPersonDaoImpl implements EligibilityByPersonDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${EligibilityByPersonDaoImpl.insertQuery1}")
	private String insertQuery1;

	@Value("${EligibilityByPersonDaoImpl.insertQuery2}")
	private String insertQuery2;

	@Value("${EligibilityByPersonDaoImpl.insertQuery3}")
	private String insertQuery3;

	@Value("${EligibilityByPersonDaoImpl.insertQuery4}")
	private String insertQuery4;

	@Value("${EligibilityByPersonDaoImpl.insertQuery5}")
	private String insertQuery5;

	@Value("${EligibilityByPersonDaoImpl.insertQuery6}")
	private String insertQuery6;

	@Value("${EligibilityByPersonDaoImpl.insertQuery7}")
	private String insertQuery7;

	@Value("${EligibilityByPersonDaoImpl.insertQuery8}")
	private String insertQuery8;

	@Value("${EligibilityByPersonDaoImpl.insertQuery9}")
	private String insertQuery9;

	@Value("${EligibilityByPersonDaoImpl.updateQuery1}")
	private String updateQuery1;

	@Value("${EligibilityByPersonDaoImpl.updateQuery2}")
	private String updateQuery2;

	@Value("${EligibilityByPersonDaoImpl.updateQuery3}")
	private String updateQuery3;

	@Value("${EligibilityByPersonDaoImpl.updateQuery4}")
	private String updateQuery4;

	@Value("${EligibilityByPersonDaoImpl.updateQuery5}")
	private String updateQuery5;

	@Value("${EligibilityByPersonDaoImpl.updateQuery6}")
	private String updateQuery6;

	private static final Logger log = Logger.getLogger(EligibilityByPersonDaoImpl.class);

	public EligibilityByPersonDaoImpl() {
		super();
	}

	/**
	 * Method Name: updateEligibiltyPeriod Method Description: Inserts and
	 * updates based on elgibility dates
	 * 
	 * @param pInputDataRec
	 * @param pOutputDataRec
	 * @return Caud18doDto @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public EligibilityByPersonOutDto updateEligibiltyPeriod(EligibilityByPersonInDto pInputDataRec) {
		log.debug("Entering method EligibilityByPersonQUERYdam in EligibilityByPersonDaoImpl");
		boolean bLeftGapExists = false;
		boolean bRightGapExists = false;
		boolean bPrevCourtOrdered = false;
		EligibilityByPersonOutDto pOutputDataRec = new EligibilityByPersonOutDto();
		int rowCount = 0;
		pOutputDataRec.setSysNbrValidationMsg(0);
		switch (pInputDataRec.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(insertQuery1)
					.addScalar("tempInt", StandardBasicTypes.INTEGER)
					.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson())
					.setResultTransformer(Transformers.aliasToBean(EligibilityByPersonOutDto.class)));
			List<EligibilityByPersonOutDto> liCaud18doDto = (List<EligibilityByPersonOutDto>) sQLQuery1.list();
			if ((!TypeConvUtil.isNullOrEmpty(liCaud18doDto)) && liCaud18doDto.size() > 0) {
				SQLQuery sQLQuery2 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(insertQuery2)
						.addScalar("tempInt", StandardBasicTypes.INTEGER)
						.setParameter("hiDtEligStart", TypeConvUtil.isDateNullCheck(pInputDataRec.getDtEligStart()))
						.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson())
						.setParameter("hiDtEligEnd", TypeConvUtil.isDateNullCheck(pInputDataRec.getDtEligEnd()))
						.setResultTransformer(Transformers.aliasToBean(EligibilityByPersonOutDto.class)));
				sQLQuery2.list();
				SQLQuery sQLQuery3 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(insertQuery3)
						.addScalar("tempInt", StandardBasicTypes.INTEGER)
						.setParameter("hiDtEligStart", TypeConvUtil.isDateNullCheck(pInputDataRec.getDtEligStart()))
						.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson())
						.setParameter("hiDtEligEnd", TypeConvUtil.isDateNullCheck(pInputDataRec.getDtEligEnd()))
						.setResultTransformer(Transformers.aliasToBean(EligibilityByPersonOutDto.class)));
				sQLQuery3.list();
				SQLQuery sQLQuery4 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(insertQuery4)
						.addScalar("tempInt", StandardBasicTypes.INTEGER)
						.setParameter("hiDtEligStart", TypeConvUtil.isDateNullCheck(pInputDataRec.getDtEligStart()))
						.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson())
						.setParameter("hiDtEligEnd", TypeConvUtil.isDateNullCheck(pInputDataRec.getDtEligEnd()))
						.setResultTransformer(Transformers.aliasToBean(EligibilityByPersonOutDto.class)));
				sQLQuery4.list();
				if (pInputDataRec.getSysIndPrfrmValidation().equals(ServiceConstants.STRING_IND_Y)) {
					SQLQuery sQLQuery5 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(insertQuery5)
							.addScalar("tempInt", StandardBasicTypes.INTEGER)
							.addScalar("dtTemp", StandardBasicTypes.TIMESTAMP)
							.addScalar("tempFloat", StandardBasicTypes.FLOAT)
							.setParameter("hiDtEligStart", TypeConvUtil.isDateNullCheck(pInputDataRec.getDtEligStart()))
							.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson())
							.setResultTransformer(Transformers.aliasToBean(EligibilityByPersonOutDto.class)));
					List<EligibilityByPersonOutDto> liCaud18doDto111 = (List<EligibilityByPersonOutDto>) sQLQuery5
							.list();
					if (!TypeConvUtil.isNullOrEmpty(liCaud18doDto111)) {
						bLeftGapExists = true;
					}
				}
				if (pInputDataRec.getSysIndPrfrmValidation().equals(ServiceConstants.STRING_IND_Y)) {
					SQLQuery sQLQuery6 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(insertQuery6)
							.addScalar("tempInt", StandardBasicTypes.INTEGER)
							.addScalar("dtTemp", StandardBasicTypes.TIMESTAMP)
							.addScalar("tempFloat", StandardBasicTypes.FLOAT)
							.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson())
							.setParameter("hiDtEligEnd", TypeConvUtil.isDateNullCheck(pInputDataRec.getDtEligEnd()))
							.setResultTransformer(Transformers.aliasToBean(EligibilityByPersonOutDto.class)));
					List<EligibilityByPersonOutDto> liCaud18doDto13 = (List<EligibilityByPersonOutDto>) sQLQuery6
							.list();
					if (!TypeConvUtil.isNullOrEmpty(liCaud18doDto13)) {
						bRightGapExists = true;
					}
				}
				if (pInputDataRec.getSysIndGeneric().equals(ServiceConstants.STRING_IND_Y)) {
					SQLQuery sQLQuery7 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(insertQuery7)
							.addScalar("cdEligCsupQuest1").addScalar("cdEligCsupQuest2").addScalar("cdEligCsupQuest3")
							.addScalar("cdEligCsupQuest4").addScalar("cdEligCsupQuest5").addScalar("cdEligCsupQuest6")
							.addScalar("cdEligCsupQuest7")
							.setParameter("hI_ulIdEligibilityEvent", pInputDataRec.getIdEligibilityEvent())
							.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson())
							.setResultTransformer(Transformers.aliasToBean(EligibilityByPersonOutDto.class)));
					List<EligibilityByPersonOutDto> liCaud18doDto4 = (List<EligibilityByPersonOutDto>) sQLQuery7.list();
					if (!TypeConvUtil.isNullOrEmpty(liCaud18doDto4)) {
						if ((liCaud18doDto4.get(0).getCdEligCsupQuest1().substring(3)
								.equalsIgnoreCase(ServiceConstants.LT_THIRTY))
								|| (liCaud18doDto4.get(0).getCdEligCsupQuest2().substring(3)
										.equalsIgnoreCase(ServiceConstants.LT_THIRTY))
								|| (liCaud18doDto4.get(0).getCdEligCsupQuest3().substring(3)
										.equalsIgnoreCase(ServiceConstants.LT_THIRTY))
								|| (liCaud18doDto4.get(0).getCdEligCsupQuest4().substring(3)
										.equalsIgnoreCase(ServiceConstants.LT_THIRTY))
								|| (liCaud18doDto4.get(0).getCdEligCsupQuest5().substring(3)
										.equalsIgnoreCase(ServiceConstants.LT_THIRTY))
								|| (liCaud18doDto4.get(0).getCdEligCsupQuest6().substring(3)
										.equalsIgnoreCase(ServiceConstants.LT_THIRTY))
								|| (liCaud18doDto4.get(0).getCdEligCsupQuest7().substring(3)
										.equalsIgnoreCase(ServiceConstants.LT_THIRTY))) {
							bPrevCourtOrdered = true;
						}
					}
				}
				SQLQuery sQLQuery8 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(insertQuery8)
						.addScalar("tempInt", StandardBasicTypes.INTEGER)
						.setParameter("hiDtEligStart", TypeConvUtil.isDateNullCheck(pInputDataRec.getDtEligStart()))
						.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson())
						.setParameter("hiDtEligEnd", TypeConvUtil.isDateNullCheck(pInputDataRec.getDtEligEnd()))
						.setResultTransformer(Transformers.aliasToBean(EligibilityByPersonOutDto.class)));
				sQLQuery8.list();
				if (bLeftGapExists && bRightGapExists)
					pOutputDataRec.setSysNbrValidationMsg(ServiceConstants.MSG_SUB_GAP_EXISTS_3);
				else if (bLeftGapExists)
					pOutputDataRec.setSysNbrValidationMsg(ServiceConstants.MSG_SUB_GAP_EXISTS_1);
				else if (bRightGapExists)
					pOutputDataRec.setSysNbrValidationMsg(ServiceConstants.MSG_SUB_GAP_EXISTS_2);
				else if (bPrevCourtOrdered)
					pOutputDataRec.setSysNbrValidationMsg(ServiceConstants.MSG_SUB_COURT_ORDERED);
				break;
			}
			SQLQuery sQLQuery10 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(insertQuery9)
					.setParameter("hiDtEligStart", TypeConvUtil.isDateNullCheck(pInputDataRec.getDtEligStart()))
					.setParameter("hI_szCdEligCsupQuest1", pInputDataRec.getCdEligCsupQuest1())
					.setParameter("hI_szCdEligCsupQuest2", pInputDataRec.getCdEligCsupQuest2())
					.setParameter("hI_szCdEligCsupQuest3", pInputDataRec.getCdEligCsupQuest3())
					.setParameter("hI_szTxtEligComment", pInputDataRec.getEligComment())
					.setParameter("hI_ulIdPersonUpdate", pInputDataRec.getIdPersonUpdate())
					.setParameter("hI_tsLastUpdate", pInputDataRec.getTsLastUpdate())
					.setParameter("hI_szCdEligCsupQuest4", pInputDataRec.getCdEligCsupQuest4())
					.setParameter("hI_szCdEligCsupQuest5", pInputDataRec.getCdEligCsupQuest5())
					.setParameter("hI_cIndEligWriteHistory", pInputDataRec.getIndEligWriteHistory())
					.setParameter("hI_szCdEligCsupQuest6", pInputDataRec.getCdEligCsupQuest6())
					.setParameter("hI_dtDtEligCsupReferral",
							TypeConvUtil.isDateNullCheck(pInputDataRec.getDtEligCsupReferral()))
					.setParameter("hI_szCdEligCsupQuest7", pInputDataRec.getCdEligCsupQuest7())
					.setParameter("hI_dtDtEligReview", TypeConvUtil.isDateNullCheck(pInputDataRec.getDtEligReview()))
					.setParameter("hI_szCdEligActual", pInputDataRec.getCdEligActual())
					.setParameter("hI_szCdEligSelected", pInputDataRec.getCdEligSelected())
					.setParameter("hI_cIndEligCsupSend", pInputDataRec.getIndEligCsupSend())
					.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson())
					.setParameter("hI_szCdEligMedEligGroup", pInputDataRec.getCdEligMedEligGroup())
					.setParameter("hiDtEligEnd", TypeConvUtil.isDateNullCheck(pInputDataRec.getDtEligEnd())));
			rowCount = sQLQuery10.executeUpdate();
			break;
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			rowCount = updateMethod(pInputDataRec, bLeftGapExists, bRightGapExists, pOutputDataRec);
			break;
		}
		pOutputDataRec.setTotalRecCount((long) rowCount);
		log.debug("Exiting method EligibilityByPersonQUERYdam in EligibilityByPersonDaoImpl");
		return pOutputDataRec;
	}

	/**
	 * Method Name: updateMethod Method Description: updates the eligibility
	 * dates
	 * 
	 * @param pInputDataRec
	 * @param bLeftGapExists
	 * @param bRightGapExists
	 * @param pOutputDataRec
	 * @return int
	 */
	private int updateMethod(EligibilityByPersonInDto pInputDataRec, boolean bLeftGapExists, boolean bRightGapExists,
			EligibilityByPersonOutDto pOutputDataRec) {
		int rowCount;
		Date curr_ploc_start = null;
		Date curr_ploc_end = null;
		SQLQuery sQLQuery11 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(updateQuery1)
				.addScalar("tempInt", StandardBasicTypes.INTEGER)
				.addScalar("dtCurrPlocStart", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtCurrPlocEnd", StandardBasicTypes.TIMESTAMP)
				.addScalar("hiDtEligStart", StandardBasicTypes.TIMESTAMP)
				.addScalar("hiDtEligEnd", StandardBasicTypes.TIMESTAMP)
				.setParameter("hiDtEligStart", TypeConvUtil.isDateNullCheck(pInputDataRec.getDtEligStart()))
				.setParameter("hI_ulIdEligibilityEvent", pInputDataRec.getIdEligibilityEvent())
				.setParameter("hI_tsLastUpdate", pInputDataRec.getTsLastUpdate())
				.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson())
				.setParameter("hiDtEligEnd", TypeConvUtil.isDateNullCheck(pInputDataRec.getDtEligEnd()))
				.setResultTransformer(Transformers.aliasToBean(EligibilityByPersonOutDto.class)));
		List<EligibilityByPersonOutDto> liCaud18doDto7 = (List<EligibilityByPersonOutDto>) sQLQuery11.list();
		if (!TypeConvUtil.isNullOrEmpty(liCaud18doDto7)) {
			curr_ploc_start = liCaud18doDto7.get(0).getDtCurrPlocStart();
			curr_ploc_end = liCaud18doDto7.get(0).getDtCurrPlocEnd();
		}
		SQLQuery sQLQuery12 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(updateQuery2)
				.addScalar("tempInt", StandardBasicTypes.INTEGER)
				.setParameter("hiDtEligStart", TypeConvUtil.isDateNullCheck(pInputDataRec.getDtEligStart()))
				.setParameter("hI_ulIdEligibilityEvent", pInputDataRec.getIdEligibilityEvent())
				.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson())
				.setParameter("dtCurrPlocStart", TypeConvUtil.isDateNullCheck(curr_ploc_start))
				.setResultTransformer(Transformers.aliasToBean(EligibilityByPersonOutDto.class)));
		sQLQuery12.list();
		SQLQuery sQLQuery13 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(updateQuery3)
				.addScalar("tempInt", StandardBasicTypes.INTEGER)
				.setParameter("hI_ulIdEligibilityEvent", pInputDataRec.getIdEligibilityEvent())
				.setParameter("dtCurrPlocEnd", TypeConvUtil.isDateNullCheck(curr_ploc_end))
				.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson())
				.setParameter("hiDtEligEnd", TypeConvUtil.isDateNullCheck(pInputDataRec.getDtEligEnd()))
				.setResultTransformer(Transformers.aliasToBean(EligibilityByPersonOutDto.class)));
		sQLQuery13.list();
		if (pInputDataRec.getSysIndPrfrmValidation().equals(ServiceConstants.STRING_IND_Y)) {
			SQLQuery sQLQuery14 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(updateQuery4)
					.addScalar("tempInt", StandardBasicTypes.INTEGER).addScalar("dtTemp", StandardBasicTypes.TIMESTAMP)
					.addScalar("tempFloat", StandardBasicTypes.FLOAT)
					.setParameter("hiDtEligStart", TypeConvUtil.isDateNullCheck(pInputDataRec.getDtEligStart()))
					.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson())
					.setParameter("dtCurrPlocStart", TypeConvUtil.isDateNullCheck(curr_ploc_start))
					.setResultTransformer(Transformers.aliasToBean(EligibilityByPersonOutDto.class)));
			List<EligibilityByPersonOutDto> liCaud18doDto10 = (List<EligibilityByPersonOutDto>) sQLQuery14.list();
			if (!TypeConvUtil.isNullOrEmpty(liCaud18doDto10)) {
				bLeftGapExists = true;
			}
		}
		if (pInputDataRec.getSysIndPrfrmValidation().equals(ServiceConstants.STRING_IND_Y)) {
			SQLQuery sQLQuery15 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(updateQuery5)
					.addScalar("tempInt", StandardBasicTypes.INTEGER).addScalar("dtTemp", StandardBasicTypes.TIMESTAMP)
					.addScalar("tempFloat", StandardBasicTypes.FLOAT)
					.setParameter("dtCurrPlocEnd", TypeConvUtil.isDateNullCheck(curr_ploc_end))
					.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson())
					.setParameter("hiDtEligEnd", TypeConvUtil.isDateNullCheck(pInputDataRec.getDtEligEnd()))
					.setResultTransformer(Transformers.aliasToBean(EligibilityByPersonOutDto.class)));
			List<EligibilityByPersonOutDto> liCaud18doDto11 = (List<EligibilityByPersonOutDto>) sQLQuery15.list();
			if (!TypeConvUtil.isNullOrEmpty(liCaud18doDto11)) {
				bRightGapExists = true;
			}
		}
		if (bLeftGapExists && bRightGapExists)
			pOutputDataRec.setSysNbrValidationMsg(ServiceConstants.MSG_SUB_GAP_EXISTS_3);
		else if (bLeftGapExists)
			pOutputDataRec.setSysNbrValidationMsg(ServiceConstants.MSG_SUB_GAP_EXISTS_1);
		else if (bRightGapExists)
			pOutputDataRec.setSysNbrValidationMsg(ServiceConstants.MSG_SUB_GAP_EXISTS_2);
		SQLQuery sQLQuery16 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(updateQuery6)
				.setParameter("hiDtEligStart", TypeConvUtil.isDateNullCheck(pInputDataRec.getDtEligStart()))
				.setParameter("hI_szCdEligCsupQuest1", pInputDataRec.getCdEligCsupQuest1())
				.setParameter("hI_ulIdEligibilityEvent", pInputDataRec.getIdEligibilityEvent())
				.setParameter("hI_szCdEligCsupQuest2", pInputDataRec.getCdEligCsupQuest2())
				.setParameter("hI_szCdEligCsupQuest3", pInputDataRec.getCdEligCsupQuest3())
				.setParameter("hI_szTxtEligComment", pInputDataRec.getEligComment())
				.setParameter("hI_ulIdPersonUpdate", pInputDataRec.getIdPersonUpdate())
				.setParameter("hI_tsLastUpdate", pInputDataRec.getTsLastUpdate())
				.setParameter("hI_szCdEligCsupQuest4", pInputDataRec.getCdEligCsupQuest4())
				.setParameter("hI_szCdEligCsupQuest5", pInputDataRec.getCdEligCsupQuest5())
				.setParameter("hI_cIndEligWriteHistory", pInputDataRec.getIndEligWriteHistory())
				.setParameter("hI_szCdEligCsupQuest6", pInputDataRec.getCdEligCsupQuest6())
				.setParameter("hI_dtDtEligCsupReferral",
						TypeConvUtil.isDateNullCheck(pInputDataRec.getDtEligCsupReferral()))
				.setParameter("hI_szCdEligCsupQuest7", pInputDataRec.getCdEligCsupQuest7())
				.setParameter("hI_dtDtEligReview", TypeConvUtil.isDateNullCheck(pInputDataRec.getDtEligReview()))
				.setParameter("hI_szCdEligActual", pInputDataRec.getCdEligActual())
				.setParameter("hI_szCdEligSelected", pInputDataRec.getCdEligSelected())
				.setParameter("hI_cIndEligCsupSend", pInputDataRec.getIndEligCsupSend())
				.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson())
				.setParameter("hI_szCdEligMedEligGroup", pInputDataRec.getCdEligMedEligGroup())
				.setParameter("hiDtEligEnd", TypeConvUtil.isDateNullCheck(pInputDataRec.getDtEligEnd())));
		rowCount = sQLQuery16.executeUpdate();
		return rowCount;
	}
}
