package us.tx.state.dfps.service.admin.daoimpl;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dao.StageInsUpdDelDao;
import us.tx.state.dfps.service.admin.dto.StageInsUpdDelInDto;
import us.tx.state.dfps.service.admin.dto.StageInsUpdDelOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:CINV16S Aug
 * 11, 2017- 1:12:44 AM © 2017 Texas Department of Family and Protective
 * Services
 */
@Repository
public class StageInsUpdDelDaoImpl implements StageInsUpdDelDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${StageInsUpdDelDaoImpl.insertStageDetails}")
	private String insertStageDetails;

	@Value("${StageInsUpdDelDaoImpl.modifyStageDetails}")
	private String modifyStageDetails;

	@Value("${StageInsUpdDelDaoImpl.getIncomingDetails}")
	private String getIncomingDetails;

	@Value("${StageInsUpdDelDaoImpl.deleteStageDetls}")
	private String deleteStageDetls;

	/*
	 * public static final String STAGE_TYPE_INVESTIGATION = "INV";
	 * 
	 * public static final String CD_INV_CLOSED_AND_RECLASS = "99";
	 * 
	 * public static final String CD_INT_CLOSED_AND_RECLASS = "25";
	 */
	private static final Logger log = Logger.getLogger(StageInsUpdDelDaoImpl.class);

	public StageInsUpdDelDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: saveStageDetail Method Description: This method will perform
	 * SAVE operation on Stage table.
	 * 
	 * @param pInputDataRec
	 * @return StageInsUpdDelOutDto @
	 */
	@Override
	public StageInsUpdDelOutDto saveStageDetail(StageInsUpdDelInDto pInputDataRec) {
		log.debug("Entering method StageInsUpdDelQUERYdam in StageInsUpdDelDaoImpl");
		StageInsUpdDelOutDto csvc18doDto = new StageInsUpdDelOutDto();
		Query queryStageDtls = sessionFactory.getCurrentSession().createSQLQuery(insertStageDetails);
		queryStageDtls.setParameter("hI_ulIdSituation", pInputDataRec.getIdSituation());
		queryStageDtls.setParameter("hI_bIndEcs", pInputDataRec.getIndEcs());
		queryStageDtls.setParameter("hI_dtDtStageStart", pInputDataRec.getDtStageStart());
		queryStageDtls.setParameter("hI_dtDtStageClose", pInputDataRec.getDtStageClose());
		queryStageDtls.setParameter("hI_szCdStageReasonClosed", pInputDataRec.getCdStageReasonClosed());
		queryStageDtls.setParameter("hI_dtDtClientAdvised", pInputDataRec.getDtClientAdvised());
		queryStageDtls.setParameter("hI_szNmStage", pInputDataRec.getNmStage());
		queryStageDtls.setParameter("hI_szCdStage", pInputDataRec.getCdStage());
		queryStageDtls.setParameter("hI_szTxtStageClosureCmnts", pInputDataRec.getStageClosureCmnts());
		queryStageDtls.setParameter("hI_szCdStageCurrPriority", pInputDataRec.getCdStageCurrPriority());
		queryStageDtls.setParameter("hI_szCdStageProgram", pInputDataRec.getCdStageProgram());
		queryStageDtls.setParameter("hI_bIndStageClose", pInputDataRec.getIndStageClose());
		queryStageDtls.setParameter("hI_szCdStageInitialPriority", pInputDataRec.getCdStageInitialPriority());
		queryStageDtls.setParameter("hI_szCdStageCnty", pInputDataRec.getCdStageCnty());
		queryStageDtls.setParameter("hI_bIndEcsVer", pInputDataRec.getIndEcsVer());
		queryStageDtls.setParameter("hI_ulIdUnit", pInputDataRec.getIdUnit());
		queryStageDtls.setParameter("hI_ulIdStage", pInputDataRec.getIdStage());
		queryStageDtls.setParameter("hI_szCdStageType", pInputDataRec.getCdStageType());
		queryStageDtls.setParameter("hI_szCdStageRegion", pInputDataRec.getCdStageRegion());
		queryStageDtls.setParameter("hI_szCdStageClassification", pInputDataRec.getCdStageClassification());
		queryStageDtls.setParameter("hI_szCdStageRsnPriorityChgd", pInputDataRec.getCdStageRsnPriorityChgd());
		queryStageDtls.setParameter("hI_ulIdCase", pInputDataRec.getIdCase());
		queryStageDtls.setParameter("hI_szCdClientAdvised", pInputDataRec.getCdClientAdvised());
		queryStageDtls.setParameter("hI_szTxtStagePriorityCmnts", pInputDataRec.getStagePriorityCmnts());
		long rowCount = queryStageDtls.executeUpdate();
		csvc18doDto.setTotalRecCount(rowCount);
		if (TypeConvUtil.isNullOrEmpty(rowCount)) {
			throw new DataNotFoundException(
					messageSource.getMessage("stagevalues.not.found.attributes", null, Locale.US));
		}
		return csvc18doDto;
	}

	/**
	 * 
	 * Method Name: updateStageDetail Method Description: This method will
	 * perform UPDATE operation on Stage table.
	 * 
	 * @param pInputDataRec
	 * @return StageInsUpdDelOutDto @
	 */
	@Override
	public StageInsUpdDelOutDto updateStageDetail(StageInsUpdDelInDto pInputDataRec) {
		Query queryStageVals = sessionFactory.getCurrentSession().createSQLQuery(modifyStageDetails);
		queryStageVals.setParameter("hI_ulIdSituation", pInputDataRec.getIdSituation());
		queryStageVals.setParameter("hI_bIndEcs", pInputDataRec.getIndEcs());
		queryStageVals.setParameter("hI_dtDtStageStart",
				pInputDataRec.getDtStageStart() == null ? "" : pInputDataRec.getDtStageStart());
		queryStageVals.setParameter("hI_dtDtStageClose",
				pInputDataRec.getDtStageClose() == null ? "" : pInputDataRec.getDtStageClose());
		queryStageVals.setParameter("hI_szCdStageReasonClosed", pInputDataRec.getCdStageReasonClosed());
		queryStageVals.setParameter("hI_dtDtClientAdvised",
				pInputDataRec.getDtClientAdvised() == null ? "" : pInputDataRec.getDtClientAdvised());
		queryStageVals.setParameter("hI_szCdStage", pInputDataRec.getCdStage());
		queryStageVals.setParameter("hI_szTxtStageClosureCmnts", pInputDataRec.getStageClosureCmnts());
		queryStageVals.setParameter("hI_szCdStageCurrPriority", pInputDataRec.getCdStageCurrPriority());
		queryStageVals.setParameter("hI_bIndStageClose", pInputDataRec.getIndStageClose());
		queryStageVals.setParameter("hI_bIndEcsVer", pInputDataRec.getIndEcsVer());
		queryStageVals.setParameter("hI_ulIdUnit", pInputDataRec.getIdUnit());
		queryStageVals.setParameter("hI_szCdStageType", pInputDataRec.getCdStageType());
		queryStageVals.setParameter("hI_szCdStageRegion", pInputDataRec.getCdStageRegion());
		queryStageVals.setParameter("hI_ulIdStage", pInputDataRec.getIdStage());
		queryStageVals.setParameter("hI_szCdClientAdvised", pInputDataRec.getCdClientAdvised());
		queryStageVals.setParameter("hI_szTxtStagePriorityCmnts", pInputDataRec.getStagePriorityCmnts());
		queryStageVals.setParameter("hI_szNmStage", pInputDataRec.getNmStage());
		// queryStageVals.setParameter("hI_tsLastUpdate", new Date());
		queryStageVals.setParameter("hI_szCdStageProgram", pInputDataRec.getCdStageProgram());
		queryStageVals.setParameter("hI_szCdStageInitialPriority", pInputDataRec.getCdStageInitialPriority());
		queryStageVals.setParameter("hI_szCdStageCnty", pInputDataRec.getCdStageCnty());
		queryStageVals.setParameter("hI_szCdStageClassification", pInputDataRec.getCdStageClassification());
		queryStageVals.setParameter("hI_szCdStageRsnPriorityChgd", pInputDataRec.getCdStageRsnPriorityChgd());
		queryStageVals.setParameter("hI_ulIdCase", pInputDataRec.getIdCase());
		long rowCountOne = queryStageVals.executeUpdate();
		StageInsUpdDelOutDto csvc18doDto = new StageInsUpdDelOutDto();
		csvc18doDto.setTotalRecCount(rowCountOne);
		if (TypeConvUtil.isNullOrEmpty(rowCountOne)) {
			throw new DataNotFoundException(
					messageSource.getMessage("stagevalues.not.found.attributes", null, Locale.US));
		}
		return csvc18doDto;
	}

	/**
	 * 
	 * Method Name: updateIncomingDetail Method Description: This method will
	 * perform UPDATE operation on Incoming Detail table.
	 * 
	 * @param pInputDataRec
	 * @return StageInsUpdDelOutDto @
	 */
	@Override
	public StageInsUpdDelOutDto updateIncomingDetail(StageInsUpdDelInDto pInputDataRec) {
		Query queryIncomeDtls = sessionFactory.getCurrentSession().createSQLQuery(getIncomingDetails);
		queryIncomeDtls.setParameter("hI_ulIdStage", pInputDataRec.getIdStage());
		long rowCountOne = queryIncomeDtls.executeUpdate();
		StageInsUpdDelOutDto csvc18doDto = new StageInsUpdDelOutDto();
		csvc18doDto.setTotalRecCount(rowCountOne);
		if (TypeConvUtil.isNullOrEmpty(rowCountOne)) {
			throw new DataNotFoundException(
					messageSource.getMessage("incomeingdetail.not.found.attributes", null, Locale.US));
		}
		return csvc18doDto;
	}

	/**
	 * 
	 * Method Name: deleteStageDetails Method Description: This method will
	 * perform DELETE operation on Stage table.
	 * 
	 * @param pInputDataRec
	 * @return StageInsUpdDelOutDto @
	 */
	@Override
	public StageInsUpdDelOutDto deleteStageDetails(StageInsUpdDelInDto pInputDataRec) {
		Query queryStagDtls = sessionFactory.getCurrentSession().createSQLQuery(deleteStageDetls);
		queryStagDtls.setParameter("hI_ulIdStage", pInputDataRec.getIdStage());
		queryStagDtls.setParameter("hI_tsLastUpdate", pInputDataRec.getTsLastUpdate());
		long rowCountOne = queryStagDtls.executeUpdate();
		StageInsUpdDelOutDto csvc18doDto = new StageInsUpdDelOutDto();
		csvc18doDto.setTotalRecCount(rowCountOne);
		if (TypeConvUtil.isNullOrEmpty(rowCountOne)) {
			throw new DataNotFoundException(
					messageSource.getMessage("stagevaluesondelete.not.found.attributes", null, Locale.US));
		}
		return csvc18doDto;
	}
}
