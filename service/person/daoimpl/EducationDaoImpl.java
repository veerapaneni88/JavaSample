package us.tx.state.dfps.service.person.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.CapsResource;
import us.tx.state.dfps.common.domain.EducationalHistory;
import us.tx.state.dfps.common.domain.EducationalNeed;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.person.dao.EducationDao;
import us.tx.state.dfps.service.person.dto.EducationHistoryDto;
import us.tx.state.dfps.service.person.dto.EducationalNeedDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:DAO
 * Implement class for Education May 31, 2018- 11:13:51 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class EducationDaoImpl implements EducationDao {
	@Autowired
	public SessionFactory sessionFactory;

	@Value("${EducationalDaoImpl.getEducationalNeedListForHist}")
	private String getEducationalNeedListForHistSql;

	@Value("${EducationalDaoImpl.getCurrentEducationHistory}")
	private String getCurrentEducationHistory;

	@Value("${EducationalDaoImpl.getCurrentEducationHistoryById}")
	private String getCurrentEducationHistoryById;

	/**
	 * 
	 * Method Name: getEducationHistoryList Method Description: get Education
	 * History List by person Id
	 * 
	 * @param idPerson
	 * @return
	 */
	@Override
	public List<EducationalHistory> getEducationHistoryList(long idPerson) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EducationalHistory.class);
		criteria.add(Restrictions.eq("person.idPerson", idPerson));
		List<EducationalHistory> educationalHistories = criteria.list();
		return educationalHistories;
	}

	/**
	 * 
	 * Method Name: getEducationHistory Method Description: get education
	 * history by education history id
	 * 
	 * @param idEdhist
	 * @return
	 */
	@Override
	public EducationalHistory getEducationHistory(long idEdhist) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EducationalHistory.class);
		criteria.add(Restrictions.eq("idEdhist", idEdhist));
		return (EducationalHistory) criteria.uniqueResult();
	}

	/**
	 * 
	 * Method Name: updateEducationalHistory Method Description: Method to
	 * update Educational History record
	 * 
	 * @param educationHistoryDto
	 * @return
	 */
	@Override
	public long updateEducationalHistory(EducationHistoryDto educationHistoryDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EducationalHistory.class);

		criteria.add(Restrictions.eq("idEdhist", educationHistoryDto.getIdEdHist()));
		criteria.add(Restrictions.le("dtLastUpdate", educationHistoryDto.getDtLastUpdate()));

		EducationalHistory educationalHistory = (EducationalHistory) criteria.uniqueResult();
		if (educationalHistory != null) {
			educationalHistory.getPerson().setIdPerson(educationHistoryDto.getIdPerson());
			if (educationHistoryDto.getIdResource() > 0) {
				educationalHistory.getCapsResource().setIdResource(educationHistoryDto.getIdResource());
			}
			if (educationHistoryDto.getAddrEdHistCity() != null) {
				educationalHistory.setAddrEdhistCity(educationHistoryDto.getAddrEdHistCity());
			}
			if (educationHistoryDto.getAddrEdHistCnty() != null) {
				educationalHistory.setAddrEdhistCnty(educationHistoryDto.getAddrEdHistCnty());
			}
			if (educationHistoryDto.getAddrEdHistState() != null) {
				educationalHistory.setAddrEdhistState(educationHistoryDto.getAddrEdHistState());
			}
			if (educationHistoryDto.getAddrEdHistStreetLn1() != null) {
				educationalHistory.setAddrEdhistStreetLn1(educationHistoryDto.getAddrEdHistStreetLn1());
			}
			if (educationHistoryDto.getAddrEdHistStreetLn2() != null) {
				educationalHistory.setAddrEdhistStreetLn2(educationHistoryDto.getAddrEdHistStreetLn2());
			}
			if (educationHistoryDto.getAddrEdHistZip() != null) {
				educationalHistory.setAddrEdhistZip(educationHistoryDto.getAddrEdHistZip());
			}
			if (educationHistoryDto.getCdEdHistEnrollGrade() != null) {
				educationalHistory.setCdEdhistEnrollGrade(educationHistoryDto.getCdEdHistEnrollGrade());
			}
			if (educationHistoryDto.getCdEdhistNeeds1() != null) {
				educationalHistory.setCdEdhistNeeds1(educationHistoryDto.getCdEdhistNeeds1());
			}
			if (educationHistoryDto.getCdEdhistNeeds2() != null) {
				educationalHistory.setCdEdhistNeeds2(educationHistoryDto.getCdEdhistNeeds2());
			}
			if (educationHistoryDto.getCdEdhistNeeds3() != null) {
				educationalHistory.setCdEdhistNeeds3(educationHistoryDto.getCdEdhistNeeds3());
			}
			if (educationHistoryDto.getCdEdhistNeeds4() != null) {
				educationalHistory.setCdEdhistNeeds4(educationHistoryDto.getCdEdhistNeeds4());
			}
			if (educationHistoryDto.getCdEdhistNeeds5() != null) {
				educationalHistory.setCdEdhistNeeds5(educationHistoryDto.getCdEdhistNeeds5());
			}
			if (educationHistoryDto.getCdEdhistNeeds6() != null) {
				educationalHistory.setCdEdhistNeeds6(educationHistoryDto.getCdEdhistNeeds6());
			}
			if (educationHistoryDto.getCdEdhistNeeds7() != null) {
				educationalHistory.setCdEdhistNeeds7(educationHistoryDto.getCdEdhistNeeds7());
			}
			if (educationHistoryDto.getCdEdhistNeeds8() != null) {
				educationalHistory.setCdEdhistNeeds8(educationHistoryDto.getCdEdhistNeeds8());
			}
			if (educationHistoryDto.getCdEdHistWithdrawnGrade() != null) {
				educationalHistory.setCdEdhistWithdrawnGrade(educationHistoryDto.getCdEdHistWithdrawnGrade());
			}
			if (educationHistoryDto.getDtEdHistEnrollDate() != null) {
				educationalHistory.setDtEdhistEnrollDate(educationHistoryDto.getDtEdHistEnrollDate());
			}
			if (educationHistoryDto.getDtEdHistWithdrawn() != null) {
				educationalHistory.setDtEdhistWithdrawnDate(educationHistoryDto.getDtEdHistWithdrawn());
			}
			if (educationHistoryDto.getIndEdHistTeaSchool() != null) {
				educationalHistory.setIndEdhistTeaSchool(educationHistoryDto.getIndEdHistTeaSchool().trim().charAt(0));
			}
			if (educationHistoryDto.getEdHistPhone() != null) {
				educationalHistory.setNbrEdhistPhone(educationHistoryDto.getEdHistPhone());
			}
			if (educationHistoryDto.getEdHistPhoneExt() != null) {
				educationalHistory.setNbrEdhistPhoneExt(educationHistoryDto.getEdHistPhoneExt());
			}
			if (educationHistoryDto.getNmEdHistSchool() != null) {
				educationalHistory.setNmEdhistSchool(educationHistoryDto.getNmEdHistSchool());
			}
			if (educationHistoryDto.getNmEdHistSchDist() != null) {
				educationalHistory.setNmEdhistSchDist(educationHistoryDto.getNmEdHistSchDist());
			}
			if (educationHistoryDto.getEdHistAddrCmnt() != null) {
				educationalHistory.setTxtEdhistAddrCmnt(educationHistoryDto.getEdHistAddrCmnt());
			}
			if (educationHistoryDto.getDtLastArdiep() != null) {
				educationalHistory.setDtLastArdiep(educationHistoryDto.getDtLastArdiep());
			}
			if (educationHistoryDto.getSpecialAccmdtns() != null) {
				educationalHistory.setTxtSpecialAccmdtns(educationHistoryDto.getSpecialAccmdtns());
			}

			sessionFactory.getCurrentSession().saveOrUpdate(educationalHistory);
			return 1;
		}
		return 0;
	}

	/**
	 * 
	 * Method Name: saveEducation Method Description: save education detail
	 * 
	 * @param educationHistoryDto
	 * @return
	 */
	@Override
	public long saveEducation(EducationHistoryDto educationHistoryDto) {
		EducationalHistory educationalHistory = new EducationalHistory();
		if (educationHistoryDto.getDtLastUpdate() != null) {
			educationalHistory.setDtLastUpdate(educationHistoryDto.getDtLastUpdate());
		} else {
			educationalHistory.setDtLastUpdate(new Date());
		}
		Person person = new Person();
		person.setIdPerson(educationHistoryDto.getIdPerson());
		educationalHistory.setPerson(person);
		if (educationHistoryDto.getIdResource() != null) {
			CapsResource capsResource = new CapsResource();
			capsResource.setIdResource(educationHistoryDto.getIdResource());
		}
		if (educationHistoryDto.getAddrEdHistCity() != null) {
			educationalHistory.setAddrEdhistCity(educationHistoryDto.getAddrEdHistCity());
		}
		if (educationHistoryDto.getAddrEdHistCnty() != null) {
			educationalHistory.setAddrEdhistCnty(educationHistoryDto.getAddrEdHistCnty());
		}
		if (educationHistoryDto.getAddrEdHistState() != null) {
			educationalHistory.setAddrEdhistState(educationHistoryDto.getAddrEdHistState());
		}
		if (educationHistoryDto.getAddrEdHistStreetLn1() != null) {
			educationalHistory.setAddrEdhistStreetLn1(educationHistoryDto.getAddrEdHistStreetLn1());
		}
		if (educationHistoryDto.getAddrEdHistStreetLn2() != null) {
			educationalHistory.setAddrEdhistStreetLn2(educationHistoryDto.getAddrEdHistStreetLn2());
		}
		if (educationHistoryDto.getAddrEdHistZip() != null) {
			educationalHistory.setAddrEdhistZip(educationHistoryDto.getAddrEdHistZip());
		}
		if (educationHistoryDto.getCdEdHistEnrollGrade() != null) {
			educationalHistory.setCdEdhistEnrollGrade(educationHistoryDto.getCdEdHistEnrollGrade());
		}
		if (educationHistoryDto.getCdEdhistNeeds1() != null) {
			educationalHistory.setCdEdhistNeeds1(educationHistoryDto.getCdEdhistNeeds1());
		}
		if (educationHistoryDto.getCdEdhistNeeds2() != null) {
			educationalHistory.setCdEdhistNeeds2(educationHistoryDto.getCdEdhistNeeds2());
		}
		if (educationHistoryDto.getCdEdhistNeeds3() != null) {
			educationalHistory.setCdEdhistNeeds3(educationHistoryDto.getCdEdhistNeeds3());
		}
		if (educationHistoryDto.getCdEdhistNeeds4() != null) {
			educationalHistory.setCdEdhistNeeds4(educationHistoryDto.getCdEdhistNeeds4());
		}
		if (educationHistoryDto.getCdEdhistNeeds5() != null) {
			educationalHistory.setCdEdhistNeeds5(educationHistoryDto.getCdEdhistNeeds5());
		}
		if (educationHistoryDto.getCdEdhistNeeds6() != null) {
			educationalHistory.setCdEdhistNeeds6(educationHistoryDto.getCdEdhistNeeds6());
		}
		if (educationHistoryDto.getCdEdhistNeeds7() != null) {
			educationalHistory.setCdEdhistNeeds7(educationHistoryDto.getCdEdhistNeeds7());
		}
		if (educationHistoryDto.getCdEdhistNeeds8() != null) {
			educationalHistory.setCdEdhistNeeds8(educationHistoryDto.getCdEdhistNeeds8());
		}
		if (educationHistoryDto.getCdEdHistWithdrawnGrade() != null) {
			educationalHistory.setCdEdhistWithdrawnGrade(educationHistoryDto.getCdEdHistWithdrawnGrade());
		}
		if (educationHistoryDto.getDtEdHistEnrollDate() != null) {
			educationalHistory.setDtEdhistEnrollDate(educationHistoryDto.getDtEdHistEnrollDate());
		}
		if (educationHistoryDto.getDtEdHistWithdrawn() != null) {
			educationalHistory.setDtEdhistWithdrawnDate(educationHistoryDto.getDtEdHistWithdrawn());
		}
		if (educationHistoryDto.getIndEdHistTeaSchool() != null) {
			educationalHistory.setIndEdhistTeaSchool(educationHistoryDto.getIndEdHistTeaSchool().trim().charAt(0));
		}
		if (educationHistoryDto.getEdHistPhone() != null) {
			educationalHistory.setNbrEdhistPhone(educationHistoryDto.getEdHistPhone());
		}
		if (educationHistoryDto.getEdHistPhoneExt() != null) {
			educationalHistory.setNbrEdhistPhoneExt(educationHistoryDto.getEdHistPhoneExt());
		}
		if (educationHistoryDto.getNmEdHistSchool() != null) {
			educationalHistory.setNmEdhistSchool(educationHistoryDto.getNmEdHistSchool());
		}
		if (educationHistoryDto.getNmEdHistSchDist() != null) {
			educationalHistory.setNmEdhistSchDist(educationHistoryDto.getNmEdHistSchDist());
		}
		if (educationHistoryDto.getEdHistAddrCmnt() != null) {
			educationalHistory.setTxtEdhistAddrCmnt(educationHistoryDto.getEdHistAddrCmnt());
		}
		if (educationHistoryDto.getDtLastArdiep() != null) {
			educationalHistory.setDtLastArdiep(educationHistoryDto.getDtLastArdiep());
		}
		if (educationHistoryDto.getSpecialAccmdtns() != null) {
			educationalHistory.setTxtSpecialAccmdtns(educationHistoryDto.getSpecialAccmdtns());
		}

		return (long) sessionFactory.getCurrentSession().save(educationalHistory);
	}

	/**
	 * 
	 * Method Name: saveEducationalNeed Method Description: Inserts a new record
	 * into EducationalNeed Table
	 * 
	 * @param educationalNeedDto
	 * @return
	 */
	@Override
	public long saveEducationalNeed(EducationalNeedDto educationalNeedDto) {
		EducationalNeed educationalNeed = new EducationalNeed();

		if (educationalNeedDto.getDtLastUpdate() != null) {
			educationalNeed.setDtLastUpdate(educationalNeedDto.getDtLastUpdate());
		} else {
			educationalNeed.setDtLastUpdate(new Date());
		}
		EducationalHistory educationalHistory = new EducationalHistory();
		educationalHistory.setIdEdhist((long) educationalNeedDto.getIdEducationHistory());
		educationalNeed.setEducationalHistory(educationalHistory);
		Person person = new Person();
		person.setIdPerson((long) educationalNeedDto.getIdPerson());
		educationalNeed.setPerson(person);
		if (educationalNeedDto.getCdEducationalNeed() != null) {
			educationalNeed.setCdEducationalNeed(educationalNeedDto.getCdEducationalNeed());
		}

		return (long) sessionFactory.getCurrentSession().save(educationalNeed);
	}

	/**
	 * 
	 * Method Name: getEducationalNeedListForHist Method Description:Fetches the
	 * person current educational needs from snapshot table
	 * (SS_EDUCATIONAL_NEED)
	 * 
	 * @param idPerson
	 * @param idEduHist
	 * @param idReferenceData
	 * @param cdActionType
	 * @param cdSnapshotType
	 * @return
	 */
	@Override
	public List<EducationalNeedDto> getEducationalNeedListForHist(int idPerson, int idEduHist, int idReferenceData,
			String cdActionType, String cdSnapshotType) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getEducationalNeedListForHistSql)
				.addScalar("idEducationalNeed", StandardBasicTypes.INTEGER)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idEducationHistory", StandardBasicTypes.INTEGER)
				.addScalar("idPerson", StandardBasicTypes.INTEGER)
				.addScalar("cdEducationalNeed", StandardBasicTypes.STRING)
				.setParameter("idReferenceData", new Integer(idReferenceData))
				.setParameter("cdSnapshotType", cdSnapshotType).setParameter("cdActionType", cdActionType)
				.setParameter("idObject", new Integer(idPerson)).setParameter("idEdhist", new Integer(idEduHist))
				.setResultTransformer(Transformers.aliasToBean(EducationalNeedDto.class));

		return (List<EducationalNeedDto>) query.list();
	}

	/**
	 * Method Name: getEducationalNeedListForHist Method Description: This
	 * method fetches the education need records for a education history record.
	 * 
	 * @param idEduHist
	 * @return ArrayList<EducationalNeedDto>
	 */
	@Override
	public ArrayList<EducationalNeedDto> getEducationalNeedListForHist(Long idEduHist) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(EducationalNeed.class);

		criteria.add(Restrictions.eq("educationalHistory.idEdhist", idEduHist));
		ArrayList<EducationalNeedDto> educationalNeedDtos = new ArrayList<EducationalNeedDto>();

		List<EducationalNeed> educationalNeeds = criteria.list();
		for (EducationalNeed educationalNeed : educationalNeeds) {
			EducationalNeedDto educationalNeedDto = new EducationalNeedDto();
			educationalNeedDto.setIdEducationalNeed((int) educationalNeed.getIdEducationalNeed());
			educationalNeedDto.setDtLastUpdate(educationalNeed.getDtLastUpdate());
			educationalNeedDto.setIdEducationHistory((int) educationalNeed.getIdEducationalNeed());
			educationalNeedDto.setIdPerson(educationalNeed.getPerson().getIdPerson().intValue());
			educationalNeedDto.setCdEducationalNeed(educationalNeed.getCdEducationalNeed());
			educationalNeedDtos.add(educationalNeedDto);
		}
		return educationalNeedDtos;
	}

	/**
	 * 
	 * Method Name: getCurrentEducationHistory Method Description:
	 * 
	 * @param idPerson
	 * @param idReferenceData
	 * @param cdActionType
	 * @param cdSnapshotType
	 * @return
	 * @throws DataNotFoundException
	 */
	@Override
	public EducationHistoryDto getCurrentEducationHistory(Long idPerson, Long idReferenceData, String cdActionType,
			String cdSnapshotType) {

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getCurrentEducationHistory)
				.addScalar("idEdHist", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idResource", StandardBasicTypes.LONG)
				.addScalar("addrEdHistCity", StandardBasicTypes.STRING)
				.addScalar("addrEdHistCnty", StandardBasicTypes.STRING)
				.addScalar("addrEdHistState", StandardBasicTypes.STRING)
				.addScalar("addrEdHistStreetLn1", StandardBasicTypes.STRING)
				.addScalar("addrEdHistStreetLn2", StandardBasicTypes.STRING)
				.addScalar("addrEdHistZip", StandardBasicTypes.STRING)
				.addScalar("cdEdHistEnrollGrade", StandardBasicTypes.STRING)
				.addScalar("cdEdhistNeeds1", StandardBasicTypes.STRING)
				.addScalar("cdEdhistNeeds2", StandardBasicTypes.STRING)
				.addScalar("cdEdhistNeeds3", StandardBasicTypes.STRING)
				.addScalar("cdEdhistNeeds4", StandardBasicTypes.STRING)
				.addScalar("cdEdhistNeeds5", StandardBasicTypes.STRING)
				.addScalar("cdEdhistNeeds6", StandardBasicTypes.STRING)
				.addScalar("cdEdhistNeeds7", StandardBasicTypes.STRING)
				.addScalar("cdEdhistNeeds8", StandardBasicTypes.STRING)
				.addScalar("cdEdHistWithdrawnGrade", StandardBasicTypes.STRING)
				.addScalar("dtEdHistEnrollDate", StandardBasicTypes.DATE)
				.addScalar("dtEdHistWithdrawn", StandardBasicTypes.DATE)
				.addScalar("indEdHistTeaSchool", StandardBasicTypes.STRING)
				.addScalar("edHistPhone", StandardBasicTypes.STRING)
				.addScalar("edHistPhoneExt", StandardBasicTypes.STRING)
				.addScalar("nmEdHistSchool", StandardBasicTypes.STRING)
				.addScalar("nmEdHistSchDist", StandardBasicTypes.STRING)
				.addScalar("edHistAddrCmnt", StandardBasicTypes.STRING)
				.addScalar("dtLastArdiep", StandardBasicTypes.DATE)
				.addScalar("specialAccmdtns", StandardBasicTypes.STRING)
				.setParameter("idReferenceData", idReferenceData).setParameter("cdSnapshotType", cdSnapshotType)
				.setParameter("cdActionType", cdActionType).setParameter("idObject", idPerson)
				.setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(EducationHistoryDto.class));
		List<EducationHistoryDto> educationHistoryList = query.list();
		if (CollectionUtils.isNotEmpty(educationHistoryList))
			return educationHistoryList.get(0);
		return null;
	}

	/**
	 * Method Name: getCurrentEducationHistoryById Method Description: This
	 * method gets current Education for input person id
	 * 
	 * @param idPerson
	 * @return EducationHistoryDto
	 */
	@Override
	public EducationHistoryDto getCurrentEducationHistoryById(Long idPerson) {

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getCurrentEducationHistoryById)
				.addScalar("idEdHist", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idResource", StandardBasicTypes.LONG)
				.addScalar("addrEdHistCity", StandardBasicTypes.STRING)
				.addScalar("addrEdHistCnty", StandardBasicTypes.STRING)
				.addScalar("addrEdHistState", StandardBasicTypes.STRING)
				.addScalar("addrEdHistStreetLn1", StandardBasicTypes.STRING)
				.addScalar("addrEdHistStreetLn2", StandardBasicTypes.STRING)
				.addScalar("addrEdHistZip", StandardBasicTypes.STRING)
				.addScalar("cdEdHistEnrollGrade", StandardBasicTypes.STRING)
				.addScalar("cdEdhistNeeds1", StandardBasicTypes.STRING)
				.addScalar("cdEdhistNeeds2", StandardBasicTypes.STRING)
				.addScalar("cdEdhistNeeds3", StandardBasicTypes.STRING)
				.addScalar("cdEdhistNeeds4", StandardBasicTypes.STRING)
				.addScalar("cdEdhistNeeds5", StandardBasicTypes.STRING)
				.addScalar("cdEdhistNeeds6", StandardBasicTypes.STRING)
				.addScalar("cdEdhistNeeds7", StandardBasicTypes.STRING)
				.addScalar("cdEdhistNeeds8", StandardBasicTypes.STRING)
				.addScalar("cdEdHistWithdrawnGrade", StandardBasicTypes.STRING)
				.addScalar("dtEdHistEnrollDate", StandardBasicTypes.DATE)
				.addScalar("dtEdHistWithdrawn", StandardBasicTypes.DATE)
				.addScalar("indEdHistTeaSchool", StandardBasicTypes.STRING)
				.addScalar("edHistPhone", StandardBasicTypes.STRING)
				.addScalar("edHistPhoneExt", StandardBasicTypes.STRING)
				.addScalar("nmEdHistSchool", StandardBasicTypes.STRING)
				.addScalar("nmEdHistSchDist", StandardBasicTypes.STRING)
				.addScalar("edHistAddrCmnt", StandardBasicTypes.STRING)
				.addScalar("dtLastArdiep", StandardBasicTypes.DATE)
				.addScalar("specialAccmdtns", StandardBasicTypes.STRING).setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(EducationHistoryDto.class));
		List<EducationHistoryDto> educationHistoryList = query.list();
		if (CollectionUtils.isNotEmpty(educationHistoryList))
			return educationHistoryList.get(0);
		return null;
	}

}
