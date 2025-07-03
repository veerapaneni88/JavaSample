package us.tx.state.dfps.service.investigation.daoimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;


import us.tx.state.dfps.common.domain.AllegedSxVctmztn;
import us.tx.state.dfps.common.domain.LicensingInvstDtl;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.service.investigation.dao.LicensingInvstDtlDao;
import us.tx.state.dfps.service.investigation.dao.LicensingInvstSumDao;
import us.tx.state.dfps.service.investigation.dto.InvstRestraintDto;
import us.tx.state.dfps.service.investigation.dto.LicensingInvstDtlDto;
import us.tx.state.dfps.service.investigation.dto.AllegedSxVctmztnDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Mar 27, 2018- 3:57:20 PM © 2017 Texas Department of
 * Family and Protective Services
 * * *************** Change History ****************************************
 * 11/1/2019  kanakas artf129782: Licensing Investigation Conclusion
 * 06/09/2020 kanakas artf152402 : Prior investigation overwritten by later 
 */
@Repository
public class LicensingInvstSumDaoImpl implements LicensingInvstSumDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private LicensingInvstDtlDao licensingInvstDtlDao;

	@Autowired
	MessageSource messageSource;

	@Value("${LicensingInvstSumDaoImpl.getInvstConclusionResByIdsql}")
	private String getInvstConclusionResByIdsql;
	
	@Value("${LicensingInvstSumDaoImpl.getSuspectSxVctmztnDtoListSql}")
	private String getSuspectSxVctmztnDtoListSql;

	@Value("${LicensingInvstSumDaoImpl.getSuspectSxVctmztnDtoListWithNoIdSql}")
	private String getSuspectSxVctmztnDtoListWithNoIdSql;
	
	@Value("${LicensingInvstSumDaoImpl.getAllegdedSxVctmztnSql}")
    private String getAllegdedSxVctmztnSql;
	
	public LicensingInvstSumDaoImpl() {

	}

	/**
	 * artf129782: Licensing Investigation Conclusion
	 * Method Name: getAllegedSxVctmztnDtoListByStageId 
	 * @return List
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AllegedSxVctmztnDto> getAllegedSxVctmztnDtoListByStageId(Long idStage){
		List<AllegedSxVctmztnDto> allegedSxVctmztnDtoList = new ArrayList<AllegedSxVctmztnDto>();
		allegedSxVctmztnDtoList = (ArrayList<AllegedSxVctmztnDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getSuspectSxVctmztnDtoListSql).setParameter("idStage", idStage))
						.addScalar("idStage", StandardBasicTypes.LONG)
						.addScalar("idSubStage", StandardBasicTypes.LONG )
						.addScalar("idVictim", StandardBasicTypes.LONG )
						.addScalar("nameVictim", StandardBasicTypes.STRING)
						.addScalar("idWorkerPerson", StandardBasicTypes.LONG )
						.addScalar("nameSubWorker", StandardBasicTypes.STRING)
						.addScalar("idSupervisorPerson", StandardBasicTypes.LONG )
						.addScalar("nameSupervisor", StandardBasicTypes.STRING)
						.addScalar("indAllegedVctmCsa", StandardBasicTypes.STRING)
						.addScalar("indAllegedSxBehaviorProblem", StandardBasicTypes.STRING)
						.addScalar("indAllegedSxAggression", StandardBasicTypes.STRING)
						.addScalar("indAllegedHumanTrafficking", StandardBasicTypes.STRING)
						.addScalar("idAllegedSxVctmztn", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(AllegedSxVctmztnDto.class)).list();
		return allegedSxVctmztnDtoList;
	}
	
	/**
     * artf152402 : Prior investigation overwritten by later 
     * Method Name: getAllegedSxVctmztnDtoListByStageId 
     * To get a row from ALLEGED_SX_VCTMZTN using stage id and id_person 
     * @return List
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<AllegedSxVctmztnDto> getAllegedSxVctmztnDtoByStageIdPid(Long idStage, Long idPerson){

        List<AllegedSxVctmztnDto> allegedSxVctmztnDtoList  =  (ArrayList<AllegedSxVctmztnDto>)((SQLQuery) sessionFactory.getCurrentSession()
                .createSQLQuery(getAllegdedSxVctmztnSql).setParameter("idStage", idStage)
                .setParameter("idPerson", idPerson))
                        .addScalar("idStage", StandardBasicTypes.LONG)
                        .addScalar("idSubStage", StandardBasicTypes.LONG )
                        .addScalar("idVictim", StandardBasicTypes.LONG )
                        .addScalar("idWorkerPerson", StandardBasicTypes.LONG )
                        .addScalar("idSupervisorPerson", StandardBasicTypes.LONG )
                        .addScalar("indAllegedVctmCsa", StandardBasicTypes.STRING)
                        .addScalar("indAllegedSxBehaviorProblem", StandardBasicTypes.STRING)
                        .addScalar("indAllegedSxAggression", StandardBasicTypes.STRING)
                        .addScalar("indAllegedHumanTrafficking", StandardBasicTypes.STRING)
                        .setResultTransformer(Transformers.aliasToBean(AllegedSxVctmztnDto.class)).list();
                        
        return allegedSxVctmztnDtoList;
    }
	
	
	/**
	 * artf129782: Licensing Investigation Conclusion
	 * Method Name: getAllegedSxVctmztnDtoListByStageId 
	 * @return List
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AllegedSxVctmztnDto> getAllegedSxVctmztnDtoByStageId(Long idStage){
		List<AllegedSxVctmztnDto> allegedSxVctmztnDtoList = new ArrayList<AllegedSxVctmztnDto>();
		allegedSxVctmztnDtoList = (ArrayList<AllegedSxVctmztnDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getSuspectSxVctmztnDtoListWithNoIdSql).setParameter("idStage", idStage))
						.addScalar("idStage", StandardBasicTypes.LONG)
						.addScalar("idSubStage", StandardBasicTypes.LONG )
						.addScalar("idVictim", StandardBasicTypes.LONG )
						.addScalar("nameVictim", StandardBasicTypes.STRING)
						.addScalar("idWorkerPerson", StandardBasicTypes.LONG )
						.addScalar("nameSubWorker", StandardBasicTypes.STRING)
						.addScalar("idSupervisorPerson", StandardBasicTypes.LONG )
						.addScalar("nameSupervisor", StandardBasicTypes.STRING)
						.addScalar("indAllegedVctmCsa", StandardBasicTypes.STRING)
						.addScalar("indAllegedSxBehaviorProblem", StandardBasicTypes.STRING)
						.addScalar("indAllegedSxAggression", StandardBasicTypes.STRING)
						.addScalar("indAllegedHumanTrafficking", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(AllegedSxVctmztnDto.class)).list();
		return allegedSxVctmztnDtoList;
	}
	/**
	 * artf129782: Licensing Investigation Conclusion
	 * artf152402 : Prior investigation overwritten by later 
	 * Method Name: getAllegedSxVctmztnDtoByStageId 
	 * @return InvstRestraintDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public AllegedSxVctmztn getAllegedSxVctmztnDtoById(Long idPerson, Long idStage){
		AllegedSxVctmztn allegedSxVctmztn = new AllegedSxVctmztn();
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(AllegedSxVctmztn.class)
				.add(Restrictions.eq("victim", (Person) sessionFactory.getCurrentSession().get(Person.class, idPerson)))
				.add(Restrictions.eq("stage", (Stage) sessionFactory.getCurrentSession().get(Stage.class, idStage)));
		allegedSxVctmztn = (AllegedSxVctmztn) cr.uniqueResult();
		return allegedSxVctmztn;
	}
	
	/**
	 * Method Name: getInvstConclusionResById Method Description: Given an
	 * ID_STAGE as input, retrieves all from INV_RESTRAINT CSESE1D
	 * getInvstConclusionResById
	 * 
	 * @return InvstRestraintDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<InvstRestraintDto> getInvstConclusionResById(Long idStage) {
		List<InvstRestraintDto> invstRestrainDto = new ArrayList<InvstRestraintDto>();
		invstRestrainDto = (ArrayList<InvstRestraintDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getInvstConclusionResByIdsql).setParameter("idStage", idStage))
						.addScalar("idInvstConclusionRestraint", StandardBasicTypes.LONG)
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.addScalar("idStage", StandardBasicTypes.LONG)
						.addScalar("cdRstraint", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(InvstRestraintDto.class)).list();
		return invstRestrainDto;
	}

	/**
	 * Method Name: getLicensingInvstDtlDaobyParentId Method Description: calls
	 * domain class to retrieve needed info.
	 * 
	 * @return InvstRestraintDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public LicensingInvstDtlDto getLicensingInvstDtlDaobyParentId(Long idStage) {
		List<LicensingInvstDtl> licensingInvstDtl = licensingInvstDtlDao.getLicensingInvstDtlDaobyParentId(idStage);
		LicensingInvstDtlDto licensingInvstDtlDto = new LicensingInvstDtlDto();
		BeanUtils.copyProperties(licensingInvstDtl.get(0), licensingInvstDtlDto);
		licensingInvstDtlDto.setIdLicngInvstStage(licensingInvstDtl.get(0).getStage().getIdStage());
		return licensingInvstDtlDto;
	}

}
