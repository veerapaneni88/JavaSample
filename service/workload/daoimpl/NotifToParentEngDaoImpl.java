package us.tx.state.dfps.service.workload.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.dto.PersonAddressDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.workload.dao.NotifToParentEngDao;
import us.tx.state.dfps.service.workload.dto.AdminReviewDto;
import us.tx.state.dfps.service.workload.dto.PrimaryWorkerDto;
import us.tx.state.dfps.service.workload.dto.StageReviewDto;

import javax.persistence.EntityNotFoundException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Mar 5, 2018- 12:12:57 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class NotifToParentEngDaoImpl implements NotifToParentEngDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${NotifToParentEngDaoImpl.getStageReviewed}")
	private String getStageReviewedSql;

	@Value("${NotifToParentEngDaoImpl.getPrimaryWorker}")
	private String getPrimaryWorkersql;

	@Value("${NotifToParentEngDaoImpl.getAdminReview}")
	private String getAdminReviewSql;

	@Value("${NotifToParentEngDaoImpl.getPersonAddress}")
	private String getPersonAddressSql;

	@Autowired
	MessageSource messageSource;

	private static final Logger LOG = Logger.getLogger(NotifToParentEngDaoImpl.class);

	/**
	 * Method Name: getStageReviewed Method Description:This dam will retrieve
	 * all records from the Nam and Admin Allegation tables that are for a
	 * specified Admin Review stage. Dam Method: CLSC65D
	 * 
	 * @param notifToParentEngReq
	 * @return List<StageReviewDto>
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<StageReviewDto> getStageReviewed(Long idStage, Date dtNameEndDate, String allegPrior) {
		List<StageReviewDto> stageReviewDto = new ArrayList<StageReviewDto>();
		stageReviewDto = (ArrayList<StageReviewDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getStageReviewedSql).setParameter("idStage", idStage)
				.setParameter("dtNameEndDate", dtNameEndDate).setParameter("indAdminAllegPrior", allegPrior))
						.addScalar("cdAdminAllegType", StandardBasicTypes.STRING)
						.addScalar("cdAdminAllegDispostion", StandardBasicTypes.STRING)
						.addScalar("cdAdminAllegSeverity", StandardBasicTypes.STRING)
						.addScalar("cdAdiminAllegClss", StandardBasicTypes.STRING)
						.addScalar("idPerson", StandardBasicTypes.LONG)
						.addScalar("nmNameFirst", StandardBasicTypes.STRING)
						.addScalar("nmNameLast", StandardBasicTypes.STRING)
						.addScalar("nmNameMiddle", StandardBasicTypes.STRING)
						.addScalar("cdNameSuffix", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(StageReviewDto.class)).list();
		return Optional.ofNullable(stageReviewDto).orElseThrow(()->new EntityNotFoundException("List<StageReviewDto> Entity not found for idStage:" +idStage));
	}

	/**
	 * Method Name: getPrimaryWorker Method Description: This DAM retrieves the
	 * Primary Worker (or Historical Primary if the stage is closed) for the
	 * input ID_STAGe. Dam Method: CSEC53D
	 * 
	 * @return PrimaryWorkerDto
	 */

	@Override
	public PrimaryWorkerDto getPrimaryWorker(Long idStage) {
		PrimaryWorkerDto primaryWorkerDto = new PrimaryWorkerDto();
		primaryWorkerDto = (PrimaryWorkerDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getPrimaryWorkersql).setParameter("idStage", idStage)
				.setParameter("cdHpStagePersRole", ServiceConstants.HIST_PRIM_WORKER)
				.setParameter("cdPrStagePersRole", ServiceConstants.PRIMARY_WORKER))
						.addScalar("idPerson", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(PrimaryWorkerDto.class)).uniqueResult();
		return Optional.ofNullable(primaryWorkerDto).orElseThrow(()->new EntityNotFoundException("PrimaryWorkerDto Entity not found for idStage:" +idStage));
	}

	/**
	 * Method Name: getAdminReview Method Description: Retrieves admin review
	 * information Dam Method: CSES65D
	 * 
	 * @param idStage
	 * @return
	 */
	@Override
	public AdminReviewDto getAdminReview(Long idStage) {
		AdminReviewDto adminReviewDto = (AdminReviewDto) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getAdminReviewSql).setParameter("idStage", idStage))
						.addScalar("idEvent", StandardBasicTypes.LONG)
						.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
						.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
						.addScalar("idStageRelated", StandardBasicTypes.LONG)
						.addScalar("cdAdminRvAppealResult", StandardBasicTypes.STRING)
						.addScalar("cdAdminRvAppealType", StandardBasicTypes.STRING)
						.addScalar("cdAdminRvAuth", StandardBasicTypes.STRING)
						.addScalar("cdAdminRvStatus", StandardBasicTypes.STRING)
						.addScalar("dtAdminRvAppealNotif", StandardBasicTypes.DATE)
						.addScalar("dtAdminRvAppealReview", StandardBasicTypes.DATE)
						.addScalar("dtAdminRvDue", StandardBasicTypes.DATE)
						.addScalar("dtAdminRvEmgcyRel", StandardBasicTypes.DATE)
						.addScalar("dtAdminRvHearing", StandardBasicTypes.DATE)
						.addScalar("dtAdminRvReqAppeal", StandardBasicTypes.DATE)
						.addScalar("indAdminRvEmgcyRel", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(AdminReviewDto.class)).list().get(0);
		return Optional.ofNullable(adminReviewDto).orElseThrow(()->new EntityNotFoundException("AdminReviewDto Entity not found for idStage:" +idStage));
	}

	/**
	 * 
	 * Method Name: getPersonAddress Method Description: Dam will retrieve an
	 * address of a specified type(CD PERS ADDR LINK TYPE) from the Person
	 * Address table
	 * 
	 * NOTE: Only use this method if query returns multiple rows Otherwise use
	 * method in DisasterPlanDao
	 * 
	 * @param idPerson
	 * @return
	 */
	@Override
	public PersonAddressDto getPersonAddress(Long idPerson, String cdPersAddrLinkType, Date dtScrDtCurrentDate) {
		PersonAddressDto personDto = new PersonAddressDto();

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPersonAddressSql)
				.addScalar("idPersonAddr", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("addrPersonAddrZip", StandardBasicTypes.STRING)
				.addScalar("cdPersonAddrState", StandardBasicTypes.STRING)
				.addScalar("nbrPersonAddrHash", StandardBasicTypes.INTEGER)
				.addScalar("addrPersonAddrCity", StandardBasicTypes.STRING)
				.addScalar("addrPersonAddrAttn", StandardBasicTypes.STRING)
				.addScalar("addrPersAddrStLn1", StandardBasicTypes.STRING)
				.addScalar("addrPersAddrStLn2", StandardBasicTypes.STRING)
				.addScalar("cdPersonAddrCounty", StandardBasicTypes.STRING)
				.addScalar("idAddrPersonLink", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdPersAddrLinkType", StandardBasicTypes.STRING)
				.addScalar("indPersAddrLinkInvalid", StandardBasicTypes.STRING)
				.addScalar("indPersAddrLinkPrimary", StandardBasicTypes.STRING)
				.addScalar("txtPersAddrCmnts", StandardBasicTypes.STRING)
				.addScalar("dtPersAddrLinkStart", StandardBasicTypes.DATE)
				.setParameter("cdPersAddrLinkType", cdPersAddrLinkType).setParameter("idPerson", idPerson)
				.setParameter("indPersAddrLinkInvalid", ServiceConstants.Character_IND_N)
				.setParameter("dtScrDtCurrentDate", dtScrDtCurrentDate)
				.setResultTransformer(Transformers.aliasToBean(PersonAddressDto.class));

		try {
			List<PersonAddressDto> personAddressDtoList  = query.list();
			if (!ObjectUtils.isEmpty(personAddressDtoList)) {

				personDto = personAddressDtoList.get(0);
			}

		} catch (DataNotFoundException | DataLayerException e) {
			LOG.error("PersonAddress data not found");
		}
		return Optional.ofNullable(personDto).orElseThrow(()->new EntityNotFoundException("PersonAddressDto Entity not found for idStage:" +idPerson));
	}
}
