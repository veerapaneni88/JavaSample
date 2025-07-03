package us.tx.state.dfps.service.placement.daoimpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.CapsResource;
import us.tx.state.dfps.common.domain.PersonDtl;
import us.tx.state.dfps.common.domain.Placement;
import us.tx.state.dfps.common.domain.StagePersonLink;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.placement.dao.CvsFaHmDao;
import us.tx.state.dfps.service.placement.dto.CvsFaHomeValueDto;

@Repository
public class CvsFaHmDaoImpl implements CvsFaHmDao {

	@Autowired
	private SessionFactory sessionFactory;

	/*
	 * Updates details of person in Person Detail table
	 */
	@Override
	public long updatePersonDetail(CvsFaHomeValueDto cvsFaHomeValueDto){

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PersonDtl.class);
		criteria.add(Restrictions.eq("idPerson", cvsFaHomeValueDto.getIdPerson()));
		PersonDtl personDtl = (PersonDtl) criteria.uniqueResult();
		long count = 0;
		if ((cvsFaHomeValueDto.getDtLastUpdate() != null)) {

			if (ServiceConstants.SERVER_IMPACT) {
				SimpleDateFormat dateFormat = new SimpleDateFormat(ServiceConstants.DATE_FORMAT_MMddyyyyhhmmss);
				String result = dateFormat.format(cvsFaHomeValueDto.getDtLastUpdate());
				try {
					personDtl.setDtLastUpdate(dateFormat.parse(result));
				} catch (ParseException e) {
					throw new DataLayerException(e.getMessage());
				}

			} else {
				personDtl.setDtLastUpdate(cvsFaHomeValueDto.getDtLastUpdate());
			}

		}
		if ((Double.toString(cvsFaHomeValueDto.getAmtPersonAnnualIncome()) != null)
				&& (!("").equalsIgnoreCase(Double.toString(cvsFaHomeValueDto.getAmtPersonAnnualIncome())))) {
			personDtl.setAmtPersonAnnualIncome((long) cvsFaHomeValueDto.getAmtPersonAnnualIncome());
		}
		if ((cvsFaHomeValueDto.getCdPersonBirthCity() != null)
				&& (!("").equals(cvsFaHomeValueDto.getCdPersonBirthCity()))) {
			personDtl.setCdPersonBirthCity(cvsFaHomeValueDto.getCdPersonBirthCity());
		}
		if ((cvsFaHomeValueDto.getCdPersonBirthCountry() != null)
				&& (!("").equals(cvsFaHomeValueDto.getCdPersonBirthCountry()))) {
			personDtl.setCdPersonBirthCountry(cvsFaHomeValueDto.getCdPersonBirthCountry());
		}
		if ((cvsFaHomeValueDto.getCdPersonBirthCounty() != null)
				&& (!("").equals(cvsFaHomeValueDto.getCdPersonBirthCounty()))) {
			personDtl.setCdPersonBirthCounty(cvsFaHomeValueDto.getCdPersonBirthCounty());
		}
		if ((cvsFaHomeValueDto.getCdPersonBirthState() != null)
				&& (!("").equals(cvsFaHomeValueDto.getCdPersonBirthState()))) {
			personDtl.setCdPersonBirthState(cvsFaHomeValueDto.getCdPersonBirthState());
		}
		if ((cvsFaHomeValueDto.getCdPersonBirthCitizenship() != null)
				&& (!("").equals(cvsFaHomeValueDto.getCdPersonBirthCitizenship()))) {
			personDtl.setCdPersonCitizenship(cvsFaHomeValueDto.getCdPersonBirthCitizenship());
		}
		if ((cvsFaHomeValueDto.getCdPersonEyeColor() != null)
				&& (!("").equals(cvsFaHomeValueDto.getCdPersonEyeColor()))) {
			personDtl.setCdPersonEyeColor(cvsFaHomeValueDto.getCdPersonEyeColor());
		}
		if ((cvsFaHomeValueDto.getCdPersonFaHomeRole() != null)
				&& (!("").equals(cvsFaHomeValueDto.getCdPersonFaHomeRole()))) {
			personDtl.setCdPersonFaHomeRole(cvsFaHomeValueDto.getCdPersonFaHomeRole());
		}
		if ((cvsFaHomeValueDto.getCdPersonHairColor() != null)
				&& (!("").equals(cvsFaHomeValueDto.getCdPersonHairColor()))) {
			personDtl.setCdPersonHairColor(cvsFaHomeValueDto.getCdPersonHairColor());
		}
		if ((cvsFaHomeValueDto.getCdPersonHighestEduc() != null)
				&& (!("").equals(cvsFaHomeValueDto.getCdPersonHighestEduc()))) {
			personDtl.setCdPersonHighestEduc(cvsFaHomeValueDto.getCdPersonHighestEduc());
		}
		if ((cvsFaHomeValueDto.getIndPersonNoUsBrn() != null)
				&& (!("").equals(cvsFaHomeValueDto.getIndPersonNoUsBrn()))) {
			personDtl.setIndPersonNoUsBrn(cvsFaHomeValueDto.getIndPersonNoUsBrn());
		}
		if ((cvsFaHomeValueDto.getNmPersonLastEmployer() != null)
				&& (!("").equals(cvsFaHomeValueDto.getNmPersonLastEmployer()))) {
			personDtl.setNmPersonLastEmployer(cvsFaHomeValueDto.getNmPersonLastEmployer());
		}
		if ((cvsFaHomeValueDto.getNmPersonMaidenName() != null)
				&& (!("").equals(cvsFaHomeValueDto.getNmPersonMaidenName()))) {
			personDtl.setNmPersonMaidenName(cvsFaHomeValueDto.getNmPersonMaidenName());
		}
		if (cvsFaHomeValueDto.getQtyPersonHeightFeet() > 0) {
			personDtl.setQtyPersonHeightFeet(cvsFaHomeValueDto.getQtyPersonHeightFeet().intValue());
		}
		if (cvsFaHomeValueDto.getQtyPersonHeightInches() > 0) {
			personDtl.setQtyPersonHeightInches(cvsFaHomeValueDto.getQtyPersonHeightInches().intValue());
		}
		if (cvsFaHomeValueDto.getQtyPersonWeight() > 0) {
			personDtl.setQtyPersonWeight(cvsFaHomeValueDto.getQtyPersonWeight().intValue());
		}
		if ((cvsFaHomeValueDto.getCdRemovalMothrMarrd() != null)
				&& (!("").equals(cvsFaHomeValueDto.getCdRemovalMothrMarrd()))) {
			personDtl.setCdRemovalMothrMarrd(cvsFaHomeValueDto.getCdRemovalMothrMarrd());
		}

		personDtl.setCdEverAdopted(cvsFaHomeValueDto.getCdEverAdopted());
		if (cvsFaHomeValueDto.getDtMostRecentAdoption() != null) {
			personDtl.setDtMostRecentAdoption(cvsFaHomeValueDto.getDtMostRecentAdoption());
		}

		personDtl.setCdAgencyAdoption(cvsFaHomeValueDto.getCdAgencyAdoption());
		personDtl.setCdEverAdoptInternatl(cvsFaHomeValueDto.getCdEverAdoptInternational());
		personDtl.setIndAdoptDateUnknown(cvsFaHomeValueDto.getIndAdoptDateUnknown());
		count++;
		sessionFactory.getCurrentSession().saveOrUpdate(personDtl);
		return count;
	}

	/*
	 * Inserts details of person into Person Detail table
	 */
	@Override
	public long insertIntoPersonDetail(CvsFaHomeValueDto cvsFaHomeValueDto){

		long updatedRes = 0;
		if (cvsFaHomeValueDto != null) {
			PersonDtl personDtl = new PersonDtl();

			if ((cvsFaHomeValueDto.getDtLastUpdate() != null)) {
				if (ServiceConstants.SERVER_IMPACT) {
					SimpleDateFormat dateFormat = new SimpleDateFormat(ServiceConstants.DATE_FORMAT_MMddyyyyhhmmss);
					String result = dateFormat.format(cvsFaHomeValueDto.getDtLastUpdate());
					try{
					personDtl.setDtLastUpdate(dateFormat.parse(result));
					} catch (ParseException e) {
						throw new DataLayerException(e.getMessage());
					}

				} else {
					personDtl.setDtLastUpdate(cvsFaHomeValueDto.getDtLastUpdate());
				}

			}
			if ((Double.toString(cvsFaHomeValueDto.getAmtPersonAnnualIncome()) != null)
					&& (!("").equalsIgnoreCase(Double.toString(cvsFaHomeValueDto.getAmtPersonAnnualIncome())))) {
				personDtl.setAmtPersonAnnualIncome((long) cvsFaHomeValueDto.getAmtPersonAnnualIncome());
			}
			if ((cvsFaHomeValueDto.getCdPersonBirthCity() != null)
					&& (!("").equals(cvsFaHomeValueDto.getCdPersonBirthCity()))) {
				personDtl.setCdPersonBirthCity(cvsFaHomeValueDto.getCdPersonBirthCity());
			}
			if ((cvsFaHomeValueDto.getCdPersonBirthCountry() != null)
					&& (!("").equals(cvsFaHomeValueDto.getCdPersonBirthCountry()))) {
				personDtl.setCdPersonBirthCountry(cvsFaHomeValueDto.getCdPersonBirthCountry());
			}
			if ((cvsFaHomeValueDto.getCdPersonBirthCounty() != null)
					&& (!("").equals(cvsFaHomeValueDto.getCdPersonBirthCounty()))) {
				personDtl.setCdPersonBirthCounty(cvsFaHomeValueDto.getCdPersonBirthCounty());
			}
			if ((cvsFaHomeValueDto.getCdPersonBirthState() != null)
					&& (!("").equals(cvsFaHomeValueDto.getCdPersonBirthState()))) {
				personDtl.setCdPersonBirthState(cvsFaHomeValueDto.getCdPersonBirthState());
			}
			if ((cvsFaHomeValueDto.getCdPersonBirthCitizenship() != null)
					&& (!("").equals(cvsFaHomeValueDto.getCdPersonBirthCitizenship()))) {
				personDtl.setCdPersonCitizenship(cvsFaHomeValueDto.getCdPersonBirthCitizenship());
			}
			if ((cvsFaHomeValueDto.getCdPersonEyeColor() != null)
					&& (!("").equals(cvsFaHomeValueDto.getCdPersonEyeColor()))) {
				personDtl.setCdPersonEyeColor(cvsFaHomeValueDto.getCdPersonEyeColor());
			}
			if ((cvsFaHomeValueDto.getCdPersonFaHomeRole() != null)
					&& (!("").equals(cvsFaHomeValueDto.getCdPersonFaHomeRole()))) {
				personDtl.setCdPersonFaHomeRole(cvsFaHomeValueDto.getCdPersonFaHomeRole());
			}
			if ((cvsFaHomeValueDto.getCdPersonHairColor() != null)
					&& (!("").equals(cvsFaHomeValueDto.getCdPersonHairColor()))) {
				personDtl.setCdPersonHairColor(cvsFaHomeValueDto.getCdPersonHairColor());
			}
			if ((cvsFaHomeValueDto.getCdPersonHighestEduc() != null)
					&& (!("").equals(cvsFaHomeValueDto.getCdPersonHighestEduc()))) {
				personDtl.setCdPersonHighestEduc(cvsFaHomeValueDto.getCdPersonHighestEduc());
			}
			if ((cvsFaHomeValueDto.getIndPersonNoUsBrn() != null)
					&& (!("").equals(cvsFaHomeValueDto.getIndPersonNoUsBrn()))) {
				personDtl.setIndPersonNoUsBrn(cvsFaHomeValueDto.getIndPersonNoUsBrn());
			}
			if ((cvsFaHomeValueDto.getNmPersonLastEmployer() != null)
					&& (!("").equals(cvsFaHomeValueDto.getNmPersonLastEmployer()))) {
				personDtl.setNmPersonLastEmployer(cvsFaHomeValueDto.getNmPersonLastEmployer());
			}
			if ((cvsFaHomeValueDto.getNmPersonMaidenName() != null)
					&& (!("").equals(cvsFaHomeValueDto.getNmPersonMaidenName()))) {
				personDtl.setNmPersonMaidenName(cvsFaHomeValueDto.getNmPersonMaidenName());
			}
			if (cvsFaHomeValueDto.getQtyPersonHeightFeet() > 0) {
				personDtl.setQtyPersonHeightFeet(cvsFaHomeValueDto.getQtyPersonHeightFeet().intValue());
			}
			if (cvsFaHomeValueDto.getQtyPersonHeightInches() > 0) {
				personDtl.setQtyPersonHeightInches(cvsFaHomeValueDto.getQtyPersonHeightInches().intValue());
			}
			if (cvsFaHomeValueDto.getQtyPersonWeight() > 0) {
				personDtl.setQtyPersonWeight(cvsFaHomeValueDto.getQtyPersonWeight().intValue());
			}
			if ((cvsFaHomeValueDto.getCdRemovalMothrMarrd() != null)
					&& (!("").equals(cvsFaHomeValueDto.getCdRemovalMothrMarrd()))) {
				personDtl.setCdRemovalMothrMarrd(cvsFaHomeValueDto.getCdRemovalMothrMarrd());
			}

			personDtl.setCdEverAdopted(cvsFaHomeValueDto.getCdEverAdopted());
			if (cvsFaHomeValueDto.getDtMostRecentAdoption() != null) {
				personDtl.setDtMostRecentAdoption(cvsFaHomeValueDto.getDtMostRecentAdoption());
			}

			personDtl.setCdAgencyAdoption(cvsFaHomeValueDto.getCdAgencyAdoption());
			personDtl.setCdEverAdoptInternatl(cvsFaHomeValueDto.getCdEverAdoptInternational());
			personDtl.setIndAdoptDateUnknown(cvsFaHomeValueDto.getIndAdoptDateUnknown());
			updatedRes = (long) sessionFactory.getCurrentSession().save(personDtl);
		}
		return updatedRes;

	}

	/*
	 * updates primary Kinship CareGiver Indicator in Stage Person Link table
	 */
	@Override
	public long updatePrimaryKinshipIndicator(CvsFaHomeValueDto cvsFaHomeValueDto) {

		long count = 0;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class);
		criteria.add(Restrictions.eq("idPerson", cvsFaHomeValueDto.getIdPerson()));
		criteria.add(Restrictions.eq("idStage", cvsFaHomeValueDto.getIdStage()));

		StagePersonLink stagePersonLink = (StagePersonLink) criteria.uniqueResult();

		if ((cvsFaHomeValueDto.getIndKinPrCaregiver() != null)
				&& (!("").equals(cvsFaHomeValueDto.getIndKinPrCaregiver()))) {
			count++;
			stagePersonLink.setIndKinPrCaregiver(cvsFaHomeValueDto.getIndKinPrCaregiver());
			sessionFactory.getCurrentSession().saveOrUpdate(stagePersonLink);
		}
		return count;
	}

	/*
	 * Updates resource Name of person in Caps Resource table CapsResource
	 */
	@Override
	public long updateCapsResourceName(CvsFaHomeValueDto cvsFaHomeValueDto) {

		long updatedResult = 0;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CapsResource.class);
		criteria.add(Restrictions.eq("idResource", cvsFaHomeValueDto.getIdResource()));

		CapsResource capsResource = (CapsResource) criteria.uniqueResult();

		if ((cvsFaHomeValueDto.getPersonName() != null) && (!("").equals(cvsFaHomeValueDto.getPersonName()))) {
			capsResource.setNmResource(cvsFaHomeValueDto.getPersonName());
			sessionFactory.getCurrentSession().saveOrUpdate(capsResource);
		}
		return updatedResult;
	}

	/*
	 * Updates resource ID of person in Placement table
	 */
	@SuppressWarnings("unchecked")
	@Override
	public long updateResourceId(CvsFaHomeValueDto cvsFaHomeValueDto){

		long updatedResult = 0;
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Placement.class);

		Criterion condition1 = Restrictions.eq("personByIdPlcmtAdult.idPerson", cvsFaHomeValueDto.getIdPerson());
		Criterion condition2 = Restrictions.isNull("capsResourceByIdRsrcFacil.idResource");
		SimpleDateFormat dateFormat = new SimpleDateFormat(ServiceConstants.DATE_FORMAT_MMddyyyyhhmmss);
		String dtPlcmtEnd = dateFormat.format(DateUtils.getDefaultFutureDate());

		Criterion condition3 = null;
		try {
			condition3 = Restrictions.eq("dtPlcmtEnd", dateFormat.parse(dtPlcmtEnd));
		} catch (ParseException e) {
			throw new DataLayerException(e.getMessage());
		}
		Criterion condition4 = Restrictions.eq("cdPlcmtRemovalRsn", ServiceConstants.PLCMT_REMOVAL_RSN);

		criteria.add(Restrictions.or(condition3, condition4)).add(Restrictions.and(condition1, condition2));
		List<Placement> cusList = criteria.list();

		for (Placement iter : cusList) {

			CapsResource capsResource = new CapsResource();
			capsResource.setIdResource(cvsFaHomeValueDto.getIdResource());
			iter.setCapsResourceByIdRsrcFacil(capsResource);
			sessionFactory.getCurrentSession().saveOrUpdate(iter);
			updatedResult++;
		}
		return updatedResult;

	}
}