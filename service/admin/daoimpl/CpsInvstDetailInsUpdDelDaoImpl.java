package us.tx.state.dfps.service.admin.daoimpl;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.AssessmentHouseholdLink;
import us.tx.state.dfps.common.domain.CpsInvstDetail;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.service.admin.dao.CpsInvstDetailInsUpdDelDao;
import us.tx.state.dfps.service.admin.dto.CpsInvstDetailInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.CpsInvstDetailInsUpdDelOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This Class
 * saves Cps investment details Aug 11, 2017- 3:41:22 PM © 2017 Texas Department
 * of Family and Protective Services
 */
@Repository
public class CpsInvstDetailInsUpdDelDaoImpl implements CpsInvstDetailInsUpdDelDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${CpsInvstDetailInsUpdDelDaoImpl.saveCpsInstDetail}")
	private String saveCpsInstDetail;

	@Value("${CpsInvstDetailInsUpdDelDaoImpl.updateCpsInvstDetail}")
	private String updateCpsInvstDetail;

	@Value("${CpsInvstDetailInsUpdDelDaoImpl.getCpsInvstId}")
	private String getCpsInvstId;

	private static final Logger log = Logger.getLogger(CpsInvstDetailInsUpdDelDaoImpl.class);

	public CpsInvstDetailInsUpdDelDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: saveCpsInvstDetail Method Description: This method will
	 * perform SAVE on CPS_INVST_DTL table.
	 * 
	 * @param pInputDataRec
	 * @return CpsInvstDetailInsUpdDelOutDto
	 */
	@Override
	public CpsInvstDetailInsUpdDelOutDto saveCpsInvstDetail(CpsInvstDetailInsUpdDelInDto pInputDataRec) {
		log.debug("Entering method CpsInvstDetailInsUpdDelQUERYdam in CpsInvstDetailInsUpdDelDaoImpl");
		CpsInvstDetail cpsInvstDetail = new CpsInvstDetail();
		cpsInvstDetail.setIdEvent(pInputDataRec.getIdEvent());
		Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, pInputDataRec.getIdStage());
		cpsInvstDetail.setStage(stage);
		cpsInvstDetail.setDtLastUpdate(pInputDataRec.getTsLastUpdate());
		cpsInvstDetail.setDtCpsInvstDtlComplt(pInputDataRec.getDtCpsInvstDtlComplt());
		cpsInvstDetail.setDtCpsInvstDtlBegun(pInputDataRec.getDtCPSInvstDtlBegun());
		cpsInvstDetail.setIndCpsInvstSafetyPln(pInputDataRec.getIndCpsInvstSafetyPln());
		cpsInvstDetail.setIndCpsInvstDtlRaNa(pInputDataRec.getIndCpsInvstDtlRaNa());
		cpsInvstDetail.setDtCpsInvstDtlAssigned(pInputDataRec.getDtCPSInvstDtlAssigned());
		cpsInvstDetail.setDtCpsInvstDtlIntake(pInputDataRec.getDtCPSInvstDtlIntake());
		cpsInvstDetail.setCdCpsInvstDtlFamIncm(pInputDataRec.getCdCpsInvstDtlFamIncm());
		cpsInvstDetail.setIndCpsInvstDtlEaConcl(pInputDataRec.getIndCpsInvstEaConcl());
		cpsInvstDetail.setCdCpsInvstDtlOvrllDisptn(pInputDataRec.getCdCpsOverallDisptn());
		// cpsInvstDetail.setIndCpsInvstDtlAbbrv(pInputDataRec.get);
		cpsInvstDetail.setIndCpsLeJntCntct(pInputDataRec.getIndCpsInvstCpsLeJointContact());
		// cpsInvstDetail.setCdReasonNoJntCntct(pInputDataRec);
		// cpsInvstDetail.setTxtReasonNoJntCntct(pInputDataRec.getSZTxtR);
		cpsInvstDetail.setIndVictimTaped(pInputDataRec.getIndVictimTaped());
		cpsInvstDetail.setCdVictimTaped(pInputDataRec.getCdVictimTaped());
		cpsInvstDetail.setTxtVictimTaped(pInputDataRec.getVictimTaped());
		// cpsInvstDetail.setIndMeth(pInputDataRec.getBind);
		cpsInvstDetail.setIndVictimPhoto(pInputDataRec.getIndVictimPhoto());
		// cpsInvstDetail.setCdVictimNoPhotoRsn(pInputDataRec.getSzcd);
		cpsInvstDetail.setTxtVictimPhoto(pInputDataRec.getVictimPhoto());
		cpsInvstDetail.setIndParentGivenGuide(pInputDataRec.getIndParentGivenGuide());
		cpsInvstDetail.setIndParentNotify24h(pInputDataRec.getIndParentNotify());
		cpsInvstDetail.setIndMultPersFound(pInputDataRec.getIndMultiPersFound());
		cpsInvstDetail.setIndMultPersMerged(pInputDataRec.getIndMultiPersMerged());
		cpsInvstDetail.setIndFtmOffered(pInputDataRec.getIndFTMOffered());
		cpsInvstDetail.setIndFtmOccurred(pInputDataRec.getIndFTMOccurred());
		cpsInvstDetail.setIndReqOrders(pInputDataRec.getIndReqOrders());
		cpsInvstDetail.setIndAbsentParent(pInputDataRec.getIndAbsentParent());
		cpsInvstDetail.setTxtAbsentParent(pInputDataRec.getAbsentParent());
		cpsInvstDetail.setTxtRsnOvrllDisptn(pInputDataRec.getRsnOvrllDisptn());
		cpsInvstDetail.setTxtRsnOpenServices(pInputDataRec.getRsnOpenServices());
		cpsInvstDetail.setTxtRsnInvClosed(pInputDataRec.getRsnInvClosed());
		cpsInvstDetail.setIndChildSexTraffic(pInputDataRec.getIndChildSexTraffic());
		cpsInvstDetail.setIndChildLaborTraffic(pInputDataRec.getIndChildLaborTraffic());
		if (null != pInputDataRec.getIdHouseHold()) {
			AssessmentHouseholdLink householdAssessmentLink = (AssessmentHouseholdLink) sessionFactory
					.getCurrentSession().get(AssessmentHouseholdLink.class, pInputDataRec.getIdHouseHold());
			cpsInvstDetail.setIdAssessmentHouseHoldLink(householdAssessmentLink);
		}

		//PPM 69915 - Alcohol Substance Tracker changes
		cpsInvstDetail.setIndSubstancePrnt(pInputDataRec.getIndSubstancePrnt());
		cpsInvstDetail.setIndSubstanceChild(pInputDataRec.getIndSubstanceChild());
		cpsInvstDetail.setIndVrblWrtnNotifRights(pInputDataRec.getIndVrblWrtnNotifRights());
		cpsInvstDetail.setIndNotifRightsUpld(pInputDataRec.getIndNotifRightsUpld());
		sessionFactory.getCurrentSession().persist(cpsInvstDetail);
		CpsInvstDetailInsUpdDelOutDto cinv12doDto = new CpsInvstDetailInsUpdDelOutDto();
		cinv12doDto.setTotalRecCount(1L);
		return cinv12doDto;
	}

	/**
	 * 
	 * Method Name: updateCpsInvstDetail Method Description: This method will
	 * perform UPDATE on CPS_INVST_DTL table.
	 * 
	 * @param pInputDataRec
	 * @return CpsInvstDetailInsUpdDelOutDto
	 */
	@Override
	public CpsInvstDetailInsUpdDelOutDto updateCpsInvstDetail(CpsInvstDetailInsUpdDelInDto pInputDataRec) {
		Query queryUpdateDetails = sessionFactory.getCurrentSession().createSQLQuery(updateCpsInvstDetail);
		queryUpdateDetails.setParameter("hI_szCdCpsInvstCpsLeJointContact",
				pInputDataRec.getCdCpsInvstCpsLeJointContact());
		queryUpdateDetails.setParameter("hI_szCdVictimTaped", pInputDataRec.getCdVictimTaped());
		queryUpdateDetails.setParameter("hI_bIndParentGivenGuide", pInputDataRec.getIndParentGivenGuide());
		queryUpdateDetails.setParameter("hI_szTxtVictimPhoto", pInputDataRec.getVictimPhoto());
		queryUpdateDetails.setParameter("hI_szTxtRsnOpenServices", pInputDataRec.getRsnOpenServices());
		queryUpdateDetails.setParameter("hI_szTxtRsnOvrllDisptn", pInputDataRec.getRsnOvrllDisptn());
		queryUpdateDetails.setParameter("hI_bIndMultiPersFound", pInputDataRec.getIndMultiPersFound());
		queryUpdateDetails.setParameter("hI_cIndCpsInvstAbbrv", pInputDataRec.getIndCpsInvstAbbrv());
		queryUpdateDetails.setParameter("hI_ulIdStage", pInputDataRec.getIdStage());
		queryUpdateDetails.setParameter("hI_cIndMeth", pInputDataRec.getIndMeth());
		queryUpdateDetails.setParameter("hI_szTxtRsnInvClosed", pInputDataRec.getRsnInvClosed());
		queryUpdateDetails.setParameter("hI_bIndFTMOffered", pInputDataRec.getIndFTMOffered());
		queryUpdateDetails.setParameter("hI_bIndAbsentParent", pInputDataRec.getIndAbsentParent());
		queryUpdateDetails.setParameter("hI_dtDtCPSInvstDtlIntake",
				pInputDataRec.getDtCPSInvstDtlIntake() != null ? pInputDataRec.getDtCPSInvstDtlIntake() : "");
		queryUpdateDetails.setParameter("hI_dtDtCPSInvstDtlAssigned", pInputDataRec.getDtCPSInvstDtlAssigned());
		queryUpdateDetails.setParameter("hI_bIndCpsInvstSafetyPln", pInputDataRec.getIndCpsInvstSafetyPln());
		queryUpdateDetails.setParameter("hI_cIndCpsInvstDtlRaNa", pInputDataRec.getIndCpsInvstDtlRaNa());
		queryUpdateDetails.setParameter("hI_bIndCpsInvstCpsLeJointContact",
				pInputDataRec.getIndCpsInvstCpsLeJointContact());
		queryUpdateDetails.setParameter("hI_szCdVictimPhoto", pInputDataRec.getCdVictimPhoto());
		queryUpdateDetails.setParameter("hI_bIndReqOrders", pInputDataRec.getIndReqOrders());
		queryUpdateDetails.setParameter("hI_szCdCpsInvstDtlFamIncm", pInputDataRec.getCdCpsInvstDtlFamIncm());
		queryUpdateDetails.setParameter("hI_bIndCpsInvstEaConcl", pInputDataRec.getIndCpsInvstEaConcl());
		queryUpdateDetails.setParameter("hI_bIndMultiPersMerged", pInputDataRec.getIndMultiPersMerged());
		queryUpdateDetails.setParameter("hI_szTxtAbsentParent", pInputDataRec.getAbsentParent());
		queryUpdateDetails.setParameter("hI_CdCpsOverallDisptn", pInputDataRec.getCdCpsOverallDisptn());
		queryUpdateDetails.setParameter("hI_szTxtCpsInvstCpsLeJointContact",
				pInputDataRec.getCpsInvstCpsLeJointContact());
		queryUpdateDetails.setParameter("hI_bIndVictimTaped", pInputDataRec.getIndVictimTaped());
		queryUpdateDetails.setParameter("hI_bIndParentNotify", pInputDataRec.getIndParentNotify());
		queryUpdateDetails.setParameter("hI_bIndVictimPhoto", pInputDataRec.getIndVictimPhoto());
		queryUpdateDetails.setParameter("hI_bIndChildSexTraffic", pInputDataRec.getIndChildSexTraffic());
		queryUpdateDetails.setParameter("hI_dtDtCPSInvstDtlBegun",
				pInputDataRec.getDtCPSInvstDtlBegun() != null ? pInputDataRec.getDtCPSInvstDtlBegun() : "");
		queryUpdateDetails.setParameter("hI_ulIdEvent", pInputDataRec.getIdEvent());
		queryUpdateDetails.setParameter("hI_bIndFTMOccurred", pInputDataRec.getIndFTMOccurred());
		queryUpdateDetails.setParameter("hI_szTxtVictimTaped", pInputDataRec.getVictimTaped());
		queryUpdateDetails.setParameter("hI_bIndChildLaborTraffic", pInputDataRec.getIndChildLaborTraffic());
		queryUpdateDetails.setParameter("hI_dtDtCpsInvstDtlComplt",
				pInputDataRec.getDtCpsInvstDtlComplt() != null ? pInputDataRec.getDtCpsInvstDtlComplt() : "");
		queryUpdateDetails.setParameter("hI_idHouseHold",
				pInputDataRec.getIdHouseHold() != null ? pInputDataRec.getIdHouseHold() : "");
		queryUpdateDetails.setParameter("hI_indNoNoticeSelected",
				pInputDataRec.getIndNoNoticeSelected() != null ? pInputDataRec.getIndNoNoticeSelected() : "N");
		queryUpdateDetails.setParameter("hI_indSubstancePrnt",
				pInputDataRec.getIndSubstancePrnt() != null ? pInputDataRec.getIndSubstancePrnt() : "N");
		queryUpdateDetails.setParameter("hI_indSubstanceChild",
				pInputDataRec.getIndSubstanceChild() != null ? pInputDataRec.getIndSubstanceChild() : "N");
		queryUpdateDetails.setParameter("hI_indVrblWrtnNotifRights",pInputDataRec.getIndVrblWrtnNotifRights());
		queryUpdateDetails.setParameter("hI_indNotifRightsUpld",pInputDataRec.getIndNotifRightsUpld());

		long rowCountOne = queryUpdateDetails.executeUpdate();
		if (TypeConvUtil.isNullOrEmpty(rowCountOne)) {
			throw new DataNotFoundException(
					messageSource.getMessage("cpsinvstdetail.not.found.attributes", null, Locale.US));
		}
		CpsInvstDetailInsUpdDelOutDto cinv12doDto = new CpsInvstDetailInsUpdDelOutDto();
		cinv12doDto.setTotalRecCount(rowCountOne);
		return cinv12doDto;
	}

	/**
	 * 
	 * Method Name: getNewCpsInvstId Method Description: This method will give
	 * the ID_CPS_INVST.
	 * 
	 * @return Long
	 */
	@Override
	public Long getNewCpsInvstId() {
		log.debug("Entering method CpsInvstDetailInsUpdDelQUERYdam in CpsInvstDetailInsUpdDelDaoImpl");
		Query queryEventId = sessionFactory.getCurrentSession().createSQLQuery(getCpsInvstId)
				.addScalar("idEvent", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(CpsInvstDetailInsUpdDelOutDto.class));
		CpsInvstDetailInsUpdDelOutDto cinv12doDto = (CpsInvstDetailInsUpdDelOutDto) queryEventId.uniqueResult();
		if (TypeConvUtil.isNullOrEmpty(cinv12doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("cpsinvstid.not.found.attributes", null, Locale.US));
		}
		return cinv12doDto.getIdEvent();
	}
}
