package us.tx.state.dfps.service.admin.daoimpl;

import java.util.ArrayList;
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

import us.tx.state.dfps.service.admin.dao.AllegationStageDao;
import us.tx.state.dfps.service.admin.dto.AllegationStageInDto;
import us.tx.state.dfps.service.admin.dto.AllegationStageOutDto;
import us.tx.state.dfps.service.admin.dto.CpsInvCnclsnValidationDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:DAO Impl to
 * retrieve Allegation Details Aug 8, 2017- 3:29:05 PM © 2017 Texas Department
 * of Family and Protective Services
 */
@Repository
public class AllegationStageDaoImpl implements AllegationStageDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${AllegationStageDaoImpl.getAllegationDtls}")
	private transient String getAllegationDtls;

	@Value("${AllegationStageDaoImpl.getCoSleepingData}")
	private transient String coSleepingDataSql;

	@Value("${AllegationStageDaoImpl.getPriorStageInReverseChronologicalOrder}")
	private transient String priorStageInReverseChronologicalOrderSql;

	private static final Logger log = Logger.getLogger(AllegationStageDaoImpl.class);

	public AllegationStageDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getAllegationDtls Method Description: This method will get
	 * data from ALLEGATION and STAGE table.
	 * 
	 * @param pInputDataRec
	 * @return List<AllegationStageOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AllegationStageOutDto> getAllegationDtls(AllegationStageInDto pInputDataRec) {
		log.debug("Entering method AllegationStageQUERYdam in AllegationStageDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getAllegationDtls)
				.setResultTransformer(Transformers.aliasToBean(AllegationStageOutDto.class)));
		sQLQuery1.addScalar("idAllegation", StandardBasicTypes.LONG).addScalar("indFatality", StandardBasicTypes.STRING)
				.addScalar("cdAllegSeverity", StandardBasicTypes.STRING)
				.setParameter("hI_ulIdStage", pInputDataRec.getIdStage())
				.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson());
		List<AllegationStageOutDto> liCses97doDto = new ArrayList<>();
		liCses97doDto = (List<AllegationStageOutDto>) sQLQuery1.list();
		return liCses97doDto;
	}

	/**
	 * Method Description: This method gets data used in validation of CPS INV
	 * stage closure. Method Name: fetchCoSleepingData
	 * 
	 * @param idStage
	 * @return List<CPSInvConclValBeanRes> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CpsInvCnclsnValidationDto> getCoSleepingData(Long idStage) {
		log.debug("Entering method getCoSleepingData in AllegationStageDaoImpl");
		List<CpsInvCnclsnValidationDto> cpsInvCnclsnValValueDtoList = (List<CpsInvCnclsnValidationDto>) ((SQLQuery) sessionFactory
				.getCurrentSession().createSQLQuery(coSleepingDataSql).setParameter("idStage", idStage))
						.addScalar("idVictim", StandardBasicTypes.LONG)
						.addScalar("dtVictimBirth", StandardBasicTypes.TIMESTAMP)
						.addScalar("dtVictimDeath", StandardBasicTypes.TIMESTAMP)
						.addScalar("dtIntake", StandardBasicTypes.TIMESTAMP)
						.addScalar("indCoSleepingChildDeath", StandardBasicTypes.STRING)
						.addScalar("indCoSleepingSubstance", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(CpsInvCnclsnValidationDto.class)).list();
		log.debug("Exiting method getCoSleepingData in AllegationStageDaoImpl");
		return cpsInvCnclsnValValueDtoList;
	}

	/**
	 * Method Description: This method Returns any prior stage ID for any given
	 * stage ID and a type request. Method Name:
	 * fetchPriorStageInReverseChronologicalOrder
	 * 
	 * @param idStage
	 * @param cdStageType
	 * @return StageDto @
	 */
	@Override
	public StageDto fetchPriorStageInReverseChronologicalOrder(Long idStage, String cdStageType) {
		log.debug("Entering method fetchPriorStageInReverseChronologicalOrder in AllegationStageDaoImpl");
		StageDto stageDtoDetails = (StageDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(priorStageInReverseChronologicalOrderSql).setParameter("idStage", idStage)
				.setParameter("cdStageType", cdStageType)).addScalar("idStage", StandardBasicTypes.LONG)
						.addScalar("idCase", StandardBasicTypes.LONG).addScalar("idSituation", StandardBasicTypes.LONG)
						.addScalar("nmStage", StandardBasicTypes.STRING).addScalar("cdStage", StandardBasicTypes.STRING)
						.addScalar("cdStageType", StandardBasicTypes.STRING)
						.addScalar("cdStageProgram", StandardBasicTypes.STRING)
						.addScalar("cdStageClassification", StandardBasicTypes.STRING)
						.addScalar("dtStageStart", StandardBasicTypes.TIMESTAMP)
						.addScalar("indStageClose", StandardBasicTypes.STRING)
						.addScalar("cdStageReasonClosed", StandardBasicTypes.STRING)
						.addScalar("idUnit", StandardBasicTypes.LONG)
						.addScalar("dtStageClose", StandardBasicTypes.TIMESTAMP)
						.setResultTransformer(Transformers.aliasToBean(StageDto.class)).uniqueResult();
		log.debug("Exiting method fetchPriorStageInReverseChronologicalOrder in AllegationStageDaoImpl");
		return stageDtoDetails;
	}
}
