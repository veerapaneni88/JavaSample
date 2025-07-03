package us.tx.state.dfps.service.admin.daoimpl;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dao.CpsInvstDetailEventDao;
import us.tx.state.dfps.service.admin.dto.CpsInvstDetailEventInDto;
import us.tx.state.dfps.service.admin.dto.CpsInvstDetailEventOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:DAO Impl for
 * fetching Investment Details Aug 6, 2017- 3:18:43 PM © 2017 Texas Department
 * of Family and Protective Services
 */
@Repository
public class CpsInvstDetailEventDaoImpl implements CpsInvstDetailEventDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${CpsInvstDetailEventDaoImpl.cpsInvstDtl}")
	private transient String cpsInvstDtl;

	private static final Logger log = Logger.getLogger(CpsInvstDetailEventDaoImpl.class);

	public CpsInvstDetailEventDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getCPSInvestmentDtl Method Description: This method retrieve
	 * data from CPS_INVESTMENT_DTL table.
	 * 
	 * @param pInputDataRec
	 * @return List<CpsInvstDetailEventOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CpsInvstDetailEventOutDto> getCPSInvestmentDtl(CpsInvstDetailEventInDto pInputDataRec) {
		log.debug("Entering method CpsInvstDetailEventQUERYdam in CpsInvstDetailEventDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(cpsInvstDtl)
				.setResultTransformer(Transformers.aliasToBean(CpsInvstDetailEventOutDto.class)));
		sQLQuery1.addScalar("cdCpsInvstDtlFamIncm", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdCpsOverallDisptn", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("dtCPSInvstDtlAssigned", StandardBasicTypes.TIMESTAMP);
		sQLQuery1.addScalar("dtCPSInvstDtlBegun", StandardBasicTypes.TIMESTAMP);
		sQLQuery1.addScalar("dtCpsInvstDtlComplt", StandardBasicTypes.TIMESTAMP);
		sQLQuery1.addScalar("dtCPSInvstDtlIntake", StandardBasicTypes.TIMESTAMP);
		sQLQuery1.addScalar("idStage", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("indCpsInvstEaConcl", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indCpsInvstSafetyPln", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indCpsInvstDtlRaNa", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indCpsInvstAbbrv", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indCpsInvstCpsLeJointContact", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdCpsInvstCpsLeJointContact", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cpsInvstCpsLeJointContact", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indVictimTaped", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdVictimTaped", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("victimTaped", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP);
		sQLQuery1.addScalar("indMeth", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indVictimPhoto", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("cdVictimPhoto", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("victimPhoto", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indParentGivenGuide", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indParentNotify", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indMultiPersFound", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indMultiPersMerged", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indFTMOffered", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indFTMOccurred", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indReqOrders", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("rsnOvrllDisptn", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("rsnOpenServices", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("rsnInvClosed", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("absentParent", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indAbsentParent", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indChildSexTraffic", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indChildLaborTraffic", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("idHouseHold", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("indNoNoticeSelected", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indSubstancePrnt", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indSubstanceChild", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indVrblWrtnNotifRights", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indNotifRightsUpld", StandardBasicTypes.STRING);
		sQLQuery1.setParameter("hI_ulIdEvent", pInputDataRec.getIdEvent());
		List<CpsInvstDetailEventOutDto> liCinv10doDto = (List<CpsInvstDetailEventOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liCinv10doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Cinv10dDaoImpl.not.found.ulIdEvent", null, Locale.US));
		}
		log.debug("Exiting method CpsInvstDetailEventQUERYdam in CpsInvstDetailEventDaoImpl");
		return liCinv10doDto;
	}
}
