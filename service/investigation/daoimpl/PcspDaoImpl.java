package us.tx.state.dfps.service.investigation.daoimpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.arinvconclusion.dto.PCSPDto;
import us.tx.state.dfps.common.domain.ChildSafetyPlacement;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.casepackage.dto.PcspDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.investigation.dao.PcspDao;
import us.tx.state.dfps.service.person.dto.ChildSafetyPlacementDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<This is DAO
 * implement class for PCSP> May 31, 2018- 11:02:19 AM © 2017 Texas Department
 * of Family and Protective Services
 */
@Repository
public class PcspDaoImpl implements PcspDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${PcspDaoImpl.getPCSPListForPerson}")
	private String getPCSPListForPersonSql;

	@Value("${PcspDaoImpl.displayPCSPList}")
	private String displayPCSPListSql;

	@Value("${PcspDaoImpl.getPCSPPersName}")
	private String getPCSPPersNameSql;

	private static final String BY_CAREGVR_PERSON = " AND ID_CAREGVR_PERSON = :idCareGiver";
	private static final String BY_PERSON = " AND ID_PERSON = :idPerson";
	private static final String AND_CD_INV_STAGE = " AND STG.CD_STAGE IN ( 'INV', 'A-R')";
	private static final String AND_CD_AR_STAGE = " AND STG.CD_STAGE = 'A-R'";
	private static final String ORDER_BY = "ORDER BY CS.DT_END DESC, P1.NM_PERSON_FULL ASC ";

	/**
	 * 
	 * Method Name: updateChildIdOnPCSP Method Description: Update Child ID on
	 * PCSP table
	 * 
	 * @param fwdPersonId
	 * @param pcspDto
	 * @return
	 */
	@Override
	public long updateChildIdOnPCSP(int fwdPersonId, PCSPDto pcspDto) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ChildSafetyPlacement.class);

		criteria.add(Restrictions.eq("idChildSafetyPlcmt", pcspDto.getIdChildSafetyPlcmt()));

		Date date = pcspDto.getTsLastUpdate();
		SimpleDateFormat dateFormatter = new SimpleDateFormat("mm/dd/yyyy HH24:mi:ss");
		String dateString = dateFormatter.format(date);
		criteria.add(Restrictions.eq("dtLastUpdate", dateString));

		List<ChildSafetyPlacement> childList = criteria.list();

		if (CollectionUtils.isNotEmpty(childList)) {
			for (ChildSafetyPlacement childSafetyPlacement : childList) {
				Person person = new Person();
				person.setIdPerson((long) fwdPersonId);
				childSafetyPlacement.setPersonByIdPerson(person);
			}
		} else {
			throw new DataLayerException("Time Stamp Mismatch");
		}
		return childList.size();

	}

	/**
	 * 
	 * Method Name: updateCaregiverIdOnPCSP Method Description: Update Caregiver
	 * ID on PCSP table
	 * 
	 * @param fwdPersonId
	 * @param pcspDto
	 * @return
	 */
	@Override
	public long updateCaregiverIdOnPCSP(int fwdPersonId, PCSPDto pcspDto) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ChildSafetyPlacement.class);

		criteria.add(Restrictions.eq("idChildSafetyPlcmt", pcspDto.getIdChildSafetyPlcmt()));

		Date date = pcspDto.getTsLastUpdate();
		SimpleDateFormat dateFormatter = new SimpleDateFormat("mm/dd/yyyy HH24:mi:ss");
		String dateString = dateFormatter.format(date);
		criteria.add(Restrictions.eq("dtLastUpdate", dateString));

		List<ChildSafetyPlacement> childList = criteria.list();

		if (CollectionUtils.isNotEmpty(childList)) {
			for (ChildSafetyPlacement childSafetyPlacement : childList) {
				Person person = new Person();
				person.setIdPerson((long) fwdPersonId);
				childSafetyPlacement.setPersonByIdCaregvrPerson(person);
			}
		} else {
			throw new DataLayerException("Time Stamp Mismatch");
		}

		return childList.size();

	}

	/**
	 * 
	 * Method Name: getPCSPListForPerson Method Description: Get PCSP List for
	 * Person
	 * 
	 * @param idChild
	 * @param idCareGiver
	 * @return
	 */
	@Override
	public List<ChildSafetyPlacementDto> getPCSPListForPerson(int idChild, int idCareGiver) {

		List<ChildSafetyPlacementDto> pcspList = new ArrayList();
		StringBuilder queryString = new StringBuilder(getPCSPListForPersonSql);
		if (idCareGiver > 0) {
			queryString.append(BY_CAREGVR_PERSON);
		} else {
			queryString.append(BY_PERSON);
		}
		Query query1 = sessionFactory.getCurrentSession().createSQLQuery(queryString.toString())
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idChildSafetyPlcmt", StandardBasicTypes.LONG)
				.addScalar("idCareGiver", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("cdEndRsn", StandardBasicTypes.STRING).addScalar("dtStart", StandardBasicTypes.DATE)
				.addScalar("dtEnd", StandardBasicTypes.DATE).addScalar("indCaregvrManual", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("txtPcspComments", StandardBasicTypes.STRING)
				.addScalar("cdStatus", StandardBasicTypes.STRING).addScalar("cdStage", StandardBasicTypes.STRING)
				.addScalar("dtStageClose", StandardBasicTypes.DATE)
				.setResultTransformer(Transformers.aliasToBean(ChildSafetyPlacementDto.class));

		if (idCareGiver > 0) {
			query1.setParameter("idCareGiver", idCareGiver);
		} else {
			query1.setParameter("idPerson", idChild);
		}

		pcspList = (List<ChildSafetyPlacementDto>) query1.list();
		return pcspList;

	}

	/**
	 * 
	 * Method Name: displayPCSPList Method Description: Get PCSP List for AR
	 * Conclusion Page Display
	 * 
	 * @param idCase
	 * @param cdStage
	 * @return List<PcspDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PcspDto> displayPCSPList(Long idCase, String cdStage) {

		List<PcspDto> pcspDtoList = new ArrayList<PcspDto>();

		StringBuilder queryString = new StringBuilder(displayPCSPListSql);
		if (ServiceConstants.CSTAGES_INV.equals(cdStage)) {
			queryString.append(AND_CD_INV_STAGE);
		} else if (ServiceConstants.CSTAGES_AR.equals(cdStage)) {
			queryString.append(AND_CD_AR_STAGE);
		}
		queryString.append(ORDER_BY);
		Query displayPCSPListquery = sessionFactory.getCurrentSession().createSQLQuery(displayPCSPListSql)
				.addScalar("idChildSafetyPlcmt", StandardBasicTypes.LONG).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("idCaregvrPerson", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("cdEndRsn", StandardBasicTypes.STRING)
				.addScalar("dtStart", StandardBasicTypes.DATE).addScalar("dtEnd", StandardBasicTypes.DATE)
				.addScalar("indCaregvrManual", StandardBasicTypes.STRING)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE).addScalar("pcspComments", StandardBasicTypes.STRING)
				.addScalar("cdStatus", StandardBasicTypes.STRING).addScalar("cdStage", StandardBasicTypes.STRING)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("nmCaregvrFull", StandardBasicTypes.STRING).addScalar("cdStage", StandardBasicTypes.STRING)
				.setParameter("idCase", idCase).setResultTransformer(Transformers.aliasToBean(PcspDto.class));
		pcspDtoList = (List<PcspDto>) displayPCSPListquery.list();
		return pcspDtoList;

	}

	/**
	 * 
	 * Method Name: getPersonDetails Method Description: Retrieve PCSP child
	 * name and cargiver name
	 * 
	 * @param idStage
	 * @return List<PcspDto>
	 */

	public List<PcspDto> getPersonDetails(Long idStage) {

		List<PcspDto> pcspDtoList = new ArrayList<PcspDto>();

		Query getPersonDetailsquery = sessionFactory.getCurrentSession().createSQLQuery(getPCSPPersNameSql)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("dtPersonBirth", StandardBasicTypes.DATE)
				.addScalar("cdPersMaritalStatus", StandardBasicTypes.STRING)
				.addScalar("cdPersRole", StandardBasicTypes.STRING).addScalar("cdPersType", StandardBasicTypes.STRING)
				.addScalar("cdPersRelation", StandardBasicTypes.STRING).addScalar("idStage", StandardBasicTypes.LONG)
				.setParameter("idStage", idStage).setResultTransformer(Transformers.aliasToBean(PcspDto.class));
		pcspDtoList = (List<PcspDto>) getPersonDetailsquery.list();
		return pcspDtoList;

	}
}
