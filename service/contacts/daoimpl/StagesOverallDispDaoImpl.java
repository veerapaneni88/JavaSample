package us.tx.state.dfps.service.contacts.daoimpl;

import java.util.Locale;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.contacts.dao.StagesOverallDispDao;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.xmlstructs.inputstructs.InvestigationStageDto;
import us.tx.state.dfps.xmlstructs.outputstructs.ApsInvDtlStageDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:StagesOverallDispDaoImpl Aug 2, 2018- 6:31:09 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class StagesOverallDispDaoImpl implements StagesOverallDispDao {

	@Value("${Cinv44dDaoImpl.getStagesOverallDispFromApsInvDtl}")
	private transient String getStagesOverallDispFromApsInvDtl;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	MessageSource messageSource;

	/**
	 * Method Name: getStagesOverallDispFromApsInvDtl Method Description: This
	 * method fetches the stage overall display.
	 * 
	 * @param investigationStageDto
	 * @return ApsInvDtlStageDto
	 */
	@Override
	public ApsInvDtlStageDto getStagesOverallDispFromApsInvDtl(InvestigationStageDto investigationStageDto) {

		SQLQuery sQLQuery1 = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getStagesOverallDispFromApsInvDtl).addScalar("ulIdEvent", StandardBasicTypes.INTEGER)
				.addScalar("ulIdStage", StandardBasicTypes.INTEGER)
				.addScalar("szCdApsInvstFinalPrty", StandardBasicTypes.STRING)
				.addScalar("szcdApsInvstOvrallDisp", StandardBasicTypes.STRING)
				.addScalar("dtDtApsInvstBegun", StandardBasicTypes.DATE)
				.addScalar("dtDtApsInvstCltAssmt", StandardBasicTypes.DATE)
				.addScalar("dtDtApsInvstCmplt", StandardBasicTypes.DATE)
				.addScalar("tsLastUpdate", StandardBasicTypes.DATE)
				.addScalar("szCdClosureType", StandardBasicTypes.STRING)
				.addScalar("bIndExtDoc", StandardBasicTypes.STRING)
				.addScalar("bIndLegalAction", StandardBasicTypes.STRING)
				.addScalar("bIndFamViolence", StandardBasicTypes.STRING).addScalar("bIndECS", StandardBasicTypes.STRING)
				.addScalar("bIndClient", StandardBasicTypes.STRING)
				.addScalar("szTxtClientOther", StandardBasicTypes.STRING)
				.addScalar("szCdInterpreter", StandardBasicTypes.STRING)
				.addScalar("szTxtMethodComm", StandardBasicTypes.STRING)
				.addScalar("szTxtTrnsNameRlt", StandardBasicTypes.STRING)
				.addScalar("szTxtAltComm", StandardBasicTypes.STRING)
				.addScalar("dtClientAdvised", StandardBasicTypes.DATE)
				.addScalar("idProgramAdminPerson", StandardBasicTypes.LONG)
				.addScalar("idEntity", StandardBasicTypes.LONG)
				.setParameter("idApsStage", investigationStageDto.getUlIdStage())
				.setResultTransformer(Transformers.aliasToBean(ApsInvDtlStageDto.class));

		ApsInvDtlStageDto apsInvDtlStageDto = (ApsInvDtlStageDto) sQLQuery1.uniqueResult();
		if (TypeConvUtil.isNullOrEmpty(apsInvDtlStageDto)) {
			throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
		}

		return apsInvDtlStageDto;
	}

}
