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

import us.tx.state.dfps.service.admin.dao.CpsInvstDetailStageIdDao;
import us.tx.state.dfps.service.admin.dto.CpsInvstDetailStageIdInDto;
import us.tx.state.dfps.service.admin.dto.CpsInvstDetailStageIdOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This Class
 * Fetches Investment Details Using StageID Aug 5, 2017- 7:35:39 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class CpsInvstDetailStageIdDaoImpl implements CpsInvstDetailStageIdDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	@Value("${CpsInvstDetailStageIdDaoImpl.getInvstDtls}")
	private transient String getInvstDtls;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(CpsInvstDetailStageIdDaoImpl.class);

	public CpsInvstDetailStageIdDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getInvstDtls Method Description: This Method Fetches
	 * Investment Details Using StageID
	 * 
	 * @param pInputDataRec
	 * @return List<CpsInvstDetailStageIdOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CpsInvstDetailStageIdOutDto> getInvstDtls(CpsInvstDetailStageIdInDto pInputDataRec) {
		log.debug("Entering method CpsInvstDetailStageIdQUERYdam in CpsInvstDetailStageIdDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getInvstDtls)
				.setResultTransformer(Transformers.aliasToBean(CpsInvstDetailStageIdOutDto.class)));
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
		sQLQuery1.addScalar("idEvent", StandardBasicTypes.LONG);
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
		sQLQuery1.setParameter("hI_ulIdStage", pInputDataRec.getIdStage());
		List<CpsInvstDetailStageIdOutDto> liCinv95doDto = (List<CpsInvstDetailStageIdOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liCinv95doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Cinv95dDaoImpl.not.found.ulIdStage", null, Locale.US));
		}
		log.debug("Exiting method CpsInvstDetailStageIdQUERYdam in CpsInvstDetailStageIdDaoImpl");
		return liCinv95doDto;
	}
}
