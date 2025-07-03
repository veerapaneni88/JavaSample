
package us.tx.state.dfps.service.familytree.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.StringUtil;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.familyTree.bean.FTPersonRelationDto;
import us.tx.state.dfps.service.familytree.dao.FTRelationshipDao;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dto.PersonValueDto;

/**
 * service-common- IMPACT PHASE 2 MODERNIZATION Class Description:<Util class
 * for FTRelationshipSuggestion> Nov 8, 2017- 4:45:15 PM © 2017 Texas Department
 * of Family and Protective Services
 */
@Component
public class FTRelationshipSuggestionUtils {

	@Autowired
	LookupDao lookUpDao;

	@Autowired
	FTRelationshipDao fTRelationshipDao;

	/**
	 * 
	 * Method Name: createRelatedPersonList Method Description:This method
	 * returns the List of Person Ids that are related to the given idPerson
	 * 
	 * @param relations
	 * @param idPerson
	 * @return List<Long>
	 */
	public List<Long> createRelatedPersonList(List<FTPersonRelationDto> relations, Long idPerson) {
		List<Long> relatedPersonIdList = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(relations) && !idPerson.equals(ServiceConstants.Zero_Value)) {
			for (FTPersonRelationDto fTPersonRelationValueDto : relations) {
				if (ServiceConstants.CRELSGOR_EX.equals(fTPersonRelationValueDto.getCdOrigin()))
					continue;
				if (!ServiceConstants.CRELSGOR_EX.equals(fTPersonRelationValueDto.getCdOrigin())
						&& fTPersonRelationValueDto.getIdPerson() == idPerson.longValue()) {
					relatedPersonIdList.add(fTPersonRelationValueDto.getIdRelatedPerson());
				} else if (!ServiceConstants.CRELSGOR_EX.equals(fTPersonRelationValueDto.getCdOrigin())
						&& fTPersonRelationValueDto.getIdRelatedPerson() == idPerson.longValue()) {
					relatedPersonIdList.add(fTPersonRelationValueDto.getIdPerson());
				}
			}
		}
		return relatedPersonIdList;
	}

	/**
	 * 
	 * Method Name: extractAllIdPersons Method Description:This method returns
	 * the List of Person Ids that are related to the given idPerson
	 * 
	 * @param relations
	 * @return List<Long>
	 */
	public List<Long> extractAllIdPersons(List<FTPersonRelationDto> relations) {
		List<Long> personIdList = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(relations)) {
			for (FTPersonRelationDto fTPersonRelationValueDto : relations) {
				Long idPerson = fTPersonRelationValueDto.getIdPerson();
				Long idRelatedPerson = fTPersonRelationValueDto.getIdRelatedPerson();

				if (!personIdList.contains(idPerson)) {
					personIdList.add(idPerson);
				}

				if (!personIdList.contains(idRelatedPerson)) {
					personIdList.add(idRelatedPerson);
				}
			}
		}
		return personIdList;
	}

	public List<FTPersonRelationDto> removeInvlidRelations(List<FTPersonRelationDto> relations) {
		List<FTPersonRelationDto> validRelations = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(relations)) {
			for (FTPersonRelationDto ftPersonRelationValueDto : relations) {

				if (!ServiceConstants.CRELSGOR_EX.equals(ftPersonRelationValueDto.getCdOrigin())
						&& ObjectUtils.isEmpty(ftPersonRelationValueDto.getDtInvalid())) {
					validRelations.add(ftPersonRelationValueDto);
				}
			}
		}
		return validRelations;
	}

	/**
	 * 
	 * Method Name: removeDupRelSuggestions Method Description:This function
	 * checks if Re/Int values will result in possible duplicate relationship.
	 * If yes, do not suggest any relationship.
	 * 
	 * @param personList
	 * @return List<PersonValueDto>
	 */
	public List<PersonValueDto> removeDupRelSuggestions(List<PersonValueDto> personList) {
		List<PersonValueDto> refinedPersonList = new ArrayList<>();
		List<Long> bioParentMaleList = new ArrayList<>();
		List<Long> bioParentFemaleList = new ArrayList<>();
		List<Long> spouseList = new ArrayList<>();
		Map<String, FTPersonRelationDto> relRelintDefMap = fTRelationshipDao.selectRelintRelMappingData();
		for (PersonValueDto personValueDto : personList) {
			Long idPerson = personValueDto.getPersonId();
			String relInt = personValueDto.getCdStagePersRelInt();
			if (StringUtil.isValid(relInt)) {
				FTPersonRelationDto fTPersonRelationDto = (FTPersonRelationDto) relRelintDefMap.get(relInt);
				if (!TypeConvUtil.isNullOrEmpty(fTPersonRelationDto)) {
					String suggestedRel = fTPersonRelationDto.getCdRelation();
					String suggestedRelType = fTPersonRelationDto.getCdType();
					if (ServiceConstants.CFAMLREL_PA.equals(suggestedRel)
							&& ServiceConstants.CRELTYPE_BI.equals(suggestedRelType)) {
						if (ServiceConstants.CSEX_M.equals(personValueDto.getSex()))
							bioParentMaleList.add(idPerson);
						else if (ServiceConstants.CSEX_F.equals(personValueDto.getSex()))
							bioParentFemaleList.add(idPerson);
					} else if (ServiceConstants.CFAMLREL_SP.equals(suggestedRel)) {
						spouseList.add(idPerson);
					}
				}
			}
		}

		List<Long> tmpDeleteList = new ArrayList<>();
		if (bioParentMaleList.size() > ServiceConstants.CNTSVC) {
			tmpDeleteList.addAll(bioParentMaleList);
		}
		if (bioParentFemaleList.size() > ServiceConstants.CNTSVC) {
			tmpDeleteList.addAll(bioParentFemaleList);
		}
		if (spouseList.size() > ServiceConstants.CNTSVC) {
			tmpDeleteList.addAll(spouseList);
		}
		for (PersonValueDto personValueDto : personList) {
			Long idPerson = personValueDto.getPersonId();
			if (!tmpDeleteList.contains(idPerson)) {
				refinedPersonList.add(personValueDto);
			}
		}
		return refinedPersonList;
	}

	/**
	 * 
	 * Method Name: suggestRelationsWithinTheList Method Description:This
	 * function suggests Relationships with in the given list of related
	 * idPersons.
	 * 
	 * @param relatedPersonIdList
	 * @param allRelationsWithinRelated
	 * @param allSuggestedRelations
	 * @param idPerson
	 * @return List<FTPersonRelationValueDto>
	 */
	public List<FTPersonRelationDto> suggestRelationsWithinTheList(List<Long> relatedPersonIdList,
			List<FTPersonRelationDto> allRelationsWithinRelated, List<FTPersonRelationDto> allSuggestedRelations,
			Long idPerson) {
		List<FTPersonRelationDto> suggestedRelations = new ArrayList<>();
		for (Long long1 : relatedPersonIdList) {
			for (Long long2 : relatedPersonIdList) {
				if (!long1.equals(long2) && !long2.equals(idPerson) && !long1.equals(idPerson)) {
					List<FTPersonRelationDto> tmpAllRelations = new ArrayList<>();
					tmpAllRelations.addAll(allRelationsWithinRelated);
					tmpAllRelations.addAll(allSuggestedRelations);
					tmpAllRelations.addAll(suggestedRelations);
					FTPersonRelationDto newRelation = suggestRelationshipBetweenTwo(long1, long2, idPerson,
							allRelationsWithinRelated, tmpAllRelations);
					if (!TypeConvUtil.isNullOrEmpty(newRelation)
							&& ServiceConstants.CRELSGOR_CL.equals(newRelation.getCdOrigin())) {
						suggestedRelations.add(newRelation);
					}
				}
			}
		}
		return suggestedRelations;
	}

	/**
	 * 
	 * Method Name: suggestRelationshipBetweenTwo Method Description:This
	 * function accepts two Related Person Ids, Person Id and suggests the
	 * Relationship.
	 * 
	 * @param idRelatedPerson1
	 * @param idRelatedPerson2
	 * @param idPerson
	 * @param allRelationsWithinRelated
	 * @param allRelations
	 * @return FTPersonRelationValueDto
	 */
	public FTPersonRelationDto suggestRelationshipBetweenTwo(Long idRelatedPerson1, Long idRelatedPerson2,
			Long idPerson, List<FTPersonRelationDto> allRelationsWithinRelated,
			List<FTPersonRelationDto> allRelations) {
		FTPersonRelationDto relValueBean = new FTPersonRelationDto();

		FTPersonRelationDto relation1 = getRelationshipBetweenTwo(allRelationsWithinRelated, idPerson,
				idRelatedPerson1);

		FTPersonRelationDto relation2 = getRelationshipBetweenTwo(allRelationsWithinRelated, idPerson,
				idRelatedPerson2);

		if (relation1.getIdPerson() != idPerson.longValue()) {
			reverseRelationship(relation1);
		}
		String cdRelation1 = relation1.getCdRelation() == null ? ServiceConstants.EMPTY_STRING
				: relation1.getCdRelation();

		if (relation2.getIdPerson() != idPerson.longValue()) {
			reverseRelationship(relation2);
		}
		String cdRelation2 = relation2.getCdRelation() == null ? ServiceConstants.EMPTY_STRING
				: relation2.getCdRelation();

		FTPersonRelationDto relSugDefValueBean = findRelationSuggestion(cdRelation1, cdRelation2);

		if (!TypeConvUtil.isNullOrEmpty(relSugDefValueBean)
				&& validateTobeSuggestedRel(relation1, relation2, relSugDefValueBean, allRelations)) {
			relValueBean.setIdRelatedPerson(idRelatedPerson1);
			relValueBean.setNmRelatedPerson(relation1.getNmRelatedPersonFull());
			relValueBean.setNmRelatedPersonFull(relation1.getNmRelatedPersonFull());
			relValueBean.setAgeRelatedPerson(relation1.getAgeRelatedPerson());
			relValueBean.setCdSexRelatedPerson(relation1.getCdSexRelatedPerson());
			relValueBean.setDtBirthRelatedPerson(relation1.getDtBirthRelatedPerson());
			relValueBean.setDtDeathRelatedPerson(relation1.getDtDeathRelatedPerson());

			relValueBean.setIdPerson(idRelatedPerson2);
			relValueBean.setNmContextPerson(relation2.getNmRelatedPersonFull());
			relValueBean.setNmContextPersonFull(relation2.getNmRelatedPersonFull());
			relValueBean.setAgeContextPerson(relation2.getAgeRelatedPerson());
			relValueBean.setCdSexContextPerson(relation2.getCdSexRelatedPerson());
			relValueBean.setDtBirthContextPerson(relation2.getDtBirthRelatedPerson());
			relValueBean.setDtDeathContextPerson(relation2.getDtDeathRelatedPerson());

			relValueBean.setCdRelation(relSugDefValueBean.getCdResultRelation());
			relValueBean.setCdSeparation(relSugDefValueBean.getCdResultSeparation());
			relValueBean.setCdOrigin(ServiceConstants.CRELSGOR_CL);
		}

		return relValueBean;
	}

	/**
	 * 
	 * Method Name: getRelationshipBetweenTwo Method Description:This function
	 * returns if there is an existing relationship between idPerson1 and
	 * idPerson2. Returns empty FTPersonRelationValueBean if no relationship
	 * found.
	 * 
	 * @param allRelationsWithinRelated
	 * @param idPerson1
	 * @param idPerson2
	 * @return FTPersonRelationValueDto
	 */
	public FTPersonRelationDto getRelationshipBetweenTwo(List<FTPersonRelationDto> allRelationsWithinRelated,
			Long idPerson1, Long idPerson2) {
		FTPersonRelationDto relation = new FTPersonRelationDto();
		for (FTPersonRelationDto fTPersonRelationValueDto : allRelationsWithinRelated) {
			Long idPerson = fTPersonRelationValueDto.getIdPerson();
			Long idRelatedPerson = fTPersonRelationValueDto.getIdRelatedPerson();

			if (((idPerson1.equals(idPerson) && idPerson2.equals(idRelatedPerson))
					|| (idPerson1.equals(idRelatedPerson) && idPerson2.equals(idPerson)))
					&& TypeConvUtil.isNullOrEmpty(fTPersonRelationValueDto.getDtInvalid())) {
				relation = fTPersonRelationValueDto;
				break;
			}
		}
		return relation;
	}

	/**
	 * 
	 * Method Name: findRelationSuggestion Method Description:This function
	 * finds Suggested Relationship from RELATION_SUGG_DEF table given two
	 * Relationships
	 * 
	 * @param cdRelation1
	 * @param cdRelation2
	 * @return FTPersonRelationDto
	 */
	private FTPersonRelationDto findRelationSuggestion(String cdRelation1, String cdRelation2) {
		FTPersonRelationDto fTPersonRelationDto = null;

		List<FTPersonRelationDto> relSugDefList = fTRelationshipDao.selectRelationshipSugMappingData();
		for (FTPersonRelationDto ftPersonRelationDto2 : relSugDefList) {
			String cdNewRelation = ftPersonRelationDto2.getCdNewRelation();
			String cdExistRelation = ftPersonRelationDto2.getCdExistRelation();
			if (cdRelation1.equals(cdNewRelation) && cdRelation2.equals(cdExistRelation)) {
				fTPersonRelationDto = ftPersonRelationDto2;
				break;
			}
		}
		return fTPersonRelationDto;
	}

	/**
	 * 
	 * Method Name: validateTobeSuggestedRel Method Description:This function
	 * performs following additional validations before suggesting the
	 * Relationship between two persons.
	 * 
	 * @param relation1
	 * @param relation2
	 * @param relSugDefValueBean
	 * @param allRelations
	 * @return Boolean
	 */
	private Boolean validateTobeSuggestedRel(FTPersonRelationDto relation1, FTPersonRelationDto relation2,
			FTPersonRelationDto relSugDefValueBean, List<FTPersonRelationDto> allRelations) {
		String relToBeSuggested = relSugDefValueBean.getCdResultRelation();
		Long idPerson1 = relation1.getIdRelatedPerson();
		Long idPerson2 = relation2.getIdRelatedPerson();

		Boolean validRel = (!idPerson1.equals(idPerson2));

		if (validRel) {
			validRel = !isRelationshipExists(idPerson1, idPerson2, relToBeSuggested, allRelations);
		}

		if (validRel) {
			String revRelation = lookUpDao.simpleDecodeSafe(CodesConstant.CRLINVRT, relToBeSuggested);
			validRel = !isRelationshipExists(idPerson2, idPerson1, revRelation, allRelations);
		}

		if (validRel) {
			validRel = validateLineage(relation1, relation2, relSugDefValueBean.getIndLineageUsed());
		}

		if (validRel) {
			validRel = validateSpouseGender(relation1.getCdSexRelatedPerson(), relation2.getCdSexRelatedPerson(),
					relToBeSuggested);
		}

		if (validRel) {
			validRel = validateSposeRel(idPerson1, idPerson2, allRelations, relToBeSuggested);
		}

		if (validRel) {
			validRel = validateNonBioRel(relation1, relation2);
		}

		if (validRel) {
			validRel = validateRelSeparation(relation1, relation2);
		}

		return validRel;
	}

	/**
	 * 
	 * Method Name: validateRelSeparation Method Description:This function
	 * verifies if the Relationship has degree of separation
	 * 
	 * @param relation1
	 * @param relation2
	 * @return Boolean
	 */
	private Boolean validateRelSeparation(FTPersonRelationDto relation1, FTPersonRelationDto relation2) {
		Boolean validRel = ServiceConstants.TRUEVAL;

		if (StringUtil.isValid(relation1.getCdSeparation()) || StringUtil.isValid(relation2.getCdSeparation())) {
			validRel = ServiceConstants.FALSEVAL;
		}

		return validRel;
	}

	/**
	 * 
	 * Method Name: validateNonBioRel Method Description:This function verifies
	 * if the Relationship (Parent, Grand Parent Uncle etc..) is Biological or
	 * blank.
	 * 
	 * @param relation1
	 * @param relation2
	 * @return Boolean
	 */
	private Boolean validateNonBioRel(FTPersonRelationDto relation1, FTPersonRelationDto relation2) {
		boolean validRel = ServiceConstants.TRUEVAL;
		String relType1 = relation1.getCdType();
		String relType2 = relation2.getCdType();

		if ((StringUtil.isValid(relType1) && !ServiceConstants.CRELTYPE_BI.equals(relation1.getCdType()))
				|| (StringUtil.isValid(relType2) && !ServiceConstants.CRELTYPE_BI.equals(relation2.getCdType()))) {
			validRel = ServiceConstants.FALSEVAL;
		}

		return validRel;
	}

	/**
	 * 
	 * Method Name: validateSposeRel Method Description:This function verifies
	 * if either of the persons has spouse before suggesting spouse
	 * relationship.
	 * 
	 * @param idPerson1
	 * @param idPerson2
	 * @param allRelations
	 * @param relToBeSuggested
	 * @return Boolean
	 */
	private Boolean validateSposeRel(Long idPerson1, Long idPerson2, List<FTPersonRelationDto> allRelations,
			String relToBeSuggested) {
		Boolean validRel = ServiceConstants.TRUEVAL;

		if (ServiceConstants.CFAMLREL_SP.equals(relToBeSuggested)) {
			if (isRelationshipExists(idPerson1, allRelations, ServiceConstants.CFAMLREL_SP)
					|| isRelationshipExists(idPerson2, allRelations, ServiceConstants.CFAMLREL_SP)) {
				validRel = ServiceConstants.FALSEVAL;
			}
		}

		return validRel;
	}

	/**
	 * 
	 * Method Name: isRelationshipExists Method Description:This function checks
	 * if the given Relationship exists for the given Person Id.
	 * 
	 * @param idPerson1
	 * @param allRelations
	 * @param cfamlrelSp
	 * @return Boolean
	 */
	private Boolean isRelationshipExists(Long idPerson1, List<FTPersonRelationDto> allRelations, String cfamlrelSp) {
		Boolean relationExists = ServiceConstants.FALSEVAL;
		for (FTPersonRelationDto ftPersonRelationValueDto : allRelations) {
			if (idPerson1.equals(ftPersonRelationValueDto.getIdPerson())
					|| idPerson1.equals(ftPersonRelationValueDto.getIdRelatedPerson())) {
				// Check if valid and not end dated relationship exists
				if (TypeConvUtil.isNullOrEmpty(ftPersonRelationValueDto.getDtInvalid())
						&& TypeConvUtil.isNullOrEmpty(ftPersonRelationValueDto.getDtEnded())
						&& ftPersonRelationValueDto.getCdRelation().equals(cfamlrelSp)) {
					relationExists = ServiceConstants.TRUEVAL;
					break;
				}
			}
		}

		return relationExists;
	}

	/**
	 * 
	 * Method Name: validateSpouseGender Method Description:This method checks
	 * if Spouse relation is between the persons of different gender. For Spouse
	 * relationships, only suggest if they are of different genders.
	 * 
	 * @param cdSexRelatedPerson
	 * @param cdSexRelatedPerson2
	 * @param relToBeSuggested
	 * @return Boolean
	 */
	private Boolean validateSpouseGender(String cdSexRelatedPerson, String cdSexRelatedPerson2,
			String relToBeSuggested) {
		Boolean validSouseRel = ServiceConstants.TRUEVAL;

		if (ServiceConstants.CFAMLREL_SP.equals(relToBeSuggested)
				&& ((ServiceConstants.CSEX_M.equals(cdSexRelatedPerson)
						&& ServiceConstants.CSEX_M.equals(cdSexRelatedPerson2))
						|| (ServiceConstants.CSEX_F.equals(cdSexRelatedPerson)
								&& ServiceConstants.CSEX_F.equals(cdSexRelatedPerson2)))) {
			validSouseRel = ServiceConstants.FALSEVAL;
		}

		return validSouseRel;
	}

	/**
	 * 
	 * Method Name: validateLineage Method Description: This function validates
	 * 
	 * 1. If IND_LINEAGE is set to Y then both the relations needs to have
	 * Lineage and they should match.
	 * 
	 * 2. if One of the Relationships is Parent { Find the Lineage using
	 * Parent's gender.
	 *
	 * Get Lineage of Other Relation.
	 *
	 * if they Do not Match Do not Suggest the Relationship
	 *
	 * }
	 * 
	 * @param relation1
	 * @param relation2
	 * @param indLineageUsed
	 * @return Boolean
	 */
	private Boolean validateLineage(FTPersonRelationDto relation1, FTPersonRelationDto relation2,
			String indLineageUsed) {
		boolean validLineage = ServiceConstants.TRUEVAL;

		boolean isRelValid = ServiceConstants.FALSEVAL;
		if (ServiceConstants.Y.equals(indLineageUsed)) {
			String cdRelation1 = relation1.getCdRelation();
			String cdRelation2 = relation2.getCdRelation();
			if (ServiceConstants.CFAMLREL_PA.equals(cdRelation1)) {
				String lineage1 = findLineage(relation1.getCdSexRelatedPerson());
				String lineage2 = relation2.getCdLineage();
				if (StringUtil.isValid(lineage1) && StringUtil.isValid(lineage2) && lineage1.equals(lineage2)) {
					isRelValid = ServiceConstants.TRUEVAL;
				}
			} else if (ServiceConstants.CFAMLREL_PA.equals(cdRelation2)) {
				String lineage2 = findLineage(relation2.getCdSexRelatedPerson());
				String lineage1 = relation1.getCdLineage();
				if (StringUtil.isValid(lineage2) && StringUtil.isValid(lineage1) && lineage1.equals(lineage2)) {
					isRelValid = ServiceConstants.TRUEVAL;
				}
			}

			if (!isRelValid) {
				String cdLineageRel1 = relation1.getCdLineage();
				String cdLineageRel2 = relation2.getCdLineage();
				if (!StringUtil.isValid(cdLineageRel1) || !StringUtil.isValid(cdLineageRel2)
						|| !cdLineageRel1.equals(cdLineageRel2)) {
					validLineage = ServiceConstants.FALSEVAL;
				}
			}
		}

		return validLineage;
	}

	/**
	 * 
	 * Method Name: findLineage Method Description:This utility function returns
	 * Lineage based on the Gender.
	 * 
	 * @param cdSexRelatedPerson
	 * @return String
	 */
	private String findLineage(String cdSexRelatedPerson) {
		String lineage = ServiceConstants.EMPTY_STRING;

		if (ServiceConstants.CSEX_M.equals(cdSexRelatedPerson)) {
			lineage = ServiceConstants.CRELLING_PAT;
		} else if (ServiceConstants.CSEX_F.equals(cdSexRelatedPerson)) {
			lineage = ServiceConstants.CRELLING_MAT;
		}

		return lineage;
	}

	/**
	 * 
	 * Method Name: isRelationshipExists Method Description:This function checks
	 * if the given Relationships exists between two given persons in the given
	 * Relationship List.
	 * 
	 * @param idPerson1
	 * @param idPerson2
	 * @param relToBeSuggested
	 * @param allRelations
	 * @return Boolean
	 */
	private Boolean isRelationshipExists(Long idPerson1, Long idPerson2, String relToBeSuggested,
			List<FTPersonRelationDto> allRelations) {
		Boolean relationExists = ServiceConstants.FALSEVAL;
		for (FTPersonRelationDto fTPersonRelationValueDto : allRelations) {
			Long idPerson = fTPersonRelationValueDto.getIdPerson();
			Long idRelatedPerson = fTPersonRelationValueDto.getIdRelatedPerson();

			if (((idPerson1.equals(idPerson) && idPerson2.equals(idRelatedPerson))
					|| (idPerson1.equals(idRelatedPerson) && idPerson2.equals(idPerson)))
					&& TypeConvUtil.isNullOrEmpty(fTPersonRelationValueDto.getDtInvalid())
					&& TypeConvUtil.isNullOrEmpty(fTPersonRelationValueDto.getDtEnded())
					&& fTPersonRelationValueDto.getCdRelation().equals(relToBeSuggested)) {
				relationExists = ServiceConstants.TRUEVAL;
				break;
			}
		}

		return relationExists;
	}

	/**
	 * 
	 * Method Name: reverseRelations Method Description:This method reverses
	 * relationships where Context Person is in ID_RELATED_PERSON column so that
	 * the Context Person will always be displayed under the Context Person.
	 * 
	 * @param fTPersonRelationValueDtoList
	 * @param idContextPerson
	 */
	public void reverseRelations(List<FTPersonRelationDto> fTPersonRelationValueDtoList, Long idContextPerson) {
		if (CollectionUtils.isNotEmpty(fTPersonRelationValueDtoList)
				&& idContextPerson != ServiceConstants.Zero_Value) {
			for (FTPersonRelationDto ftPersonRelationValueDto : fTPersonRelationValueDtoList) {
				// Context Person is in ID_RELATED_PERSON column, swap d
				if (ftPersonRelationValueDto.getIdRelatedPerson() == idContextPerson) {
					reverseRelationship(ftPersonRelationValueDto);
				}
			}
		}
	}

	/**
	 * 
	 * Method Name: reverseRelationship Method Description:This method swaps
	 * Context Person with Related Person and also reverses the relationship.
	 * 
	 * @param relation1
	 */
	public void reverseRelationship(FTPersonRelationDto relation1) {
		if (!TypeConvUtil.isNullOrEmpty(relation1) && relation1.getIdPerson() != ServiceConstants.Zero_Value
				&& relation1.getIdRelatedPerson() != ServiceConstants.Zero_Value) {
			Long idPerson = relation1.getIdPerson();
			String nmContextPerson = relation1.getNmContextPersonFull();
			String cdSexContextPerson = relation1.getCdSexContextPerson();
			Integer ageContextPerson = relation1.getAgeContextPerson();
			Date dtBirthContextPerson = relation1.getDtBirthContextPerson();
			Date dtDeathContextPerson = relation1.getDtDeathContextPerson();
			String indDOBApproxContextPerson = relation1.getIndDOBApproxContextPerson();
			String cdContextPersonStatus = relation1.getCdContextPersonStatus();
			String cdRaceContextPerson = relation1.getCdRaceContextPerson();
			String cdEthnicityContextPerson = relation1.getCdEthnicityContextPerson();
			Boolean isContextPersonNotInStagePersonList = relation1.isContextPersonNotInStagePersonList();
			Boolean isContextPersonNotInCasePersonList = relation1.isContextPersonNotInCasePersonList();
			Boolean isContextPersonStaff = relation1.isContextPersonStaff();
			Boolean isContextPersonStaffForSensitive = relation1.isContextPersonStaffForSensitive();

			relation1.setIdPerson(relation1.getIdRelatedPerson());
			relation1.setNmContextPerson(relation1.getNmRelatedPersonFull());
			relation1.setNmContextPersonFull(relation1.getNmRelatedPersonFull());
			relation1.setCdSexContextPerson(relation1.getCdSexRelatedPerson());
			relation1.setAgeContextPerson(relation1.getAgeRelatedPerson());
			relation1.setDtBirthContextPerson(relation1.getDtBirthRelatedPerson());
			relation1.setDtDeathContextPerson(relation1.getDtDeathRelatedPerson());
			relation1.setIndDOBApproxContextPerson(relation1.getIndDOBApproxRelatedPerson());
			relation1.setCdContextPersonStatus(relation1.getCdRelatedPersonStatus());
			relation1.setCdRaceContextPerson(relation1.getCdRaceRelatedPerson());
			relation1.setCdEthnicityContextPerson(relation1.getCdEthnicityRelatedPerson());
			relation1.setContextPersonNotInStagePersonList(relation1.getRelatedPersonNotInStagePersonList());
			relation1.setContextPersonNotInCasePersonList(relation1.isContextPersonNotInCasePersonList());
			relation1.setContextPersonStaff(relation1.isRelatedPersonStaff());
			relation1.setContextPersonStaffForSensitive(relation1.isContextPersonStaffForSensitive());

			relation1.setIdRelatedPerson(idPerson);
			relation1.setNmRelatedPerson(nmContextPerson);
			relation1.setNmRelatedPersonFull(nmContextPerson);
			relation1.setCdSexRelatedPerson(cdSexContextPerson);
			relation1.setAgeRelatedPerson(ageContextPerson);
			relation1.setDtBirthRelatedPerson(dtBirthContextPerson);
			relation1.setDtDeathRelatedPerson(dtDeathContextPerson);
			relation1.setIndDOBApproxRelatedPerson(indDOBApproxContextPerson);
			relation1.setCdRelatedPersonStatus(cdContextPersonStatus);
			relation1.setCdRaceRelatedPerson(cdRaceContextPerson);
			relation1.setCdEthnicityRelatedPerson(cdEthnicityContextPerson);
			relation1.setRelatedPersonNotInStagePersonList(isContextPersonNotInStagePersonList);
			relation1.setRelatedPersonNotInCasePersonList(isContextPersonNotInCasePersonList);
			relation1.setRelatedPersonStaff(isContextPersonStaff);
			relation1.setRelatedPersonStaffForSensitive(isContextPersonStaffForSensitive);

			String cdNewRelation = lookUpDao.simpleDecodeSafe(CodesConstant.CRLINVRT, relation1.getCdRelation());
			relation1.setCdRelation(cdNewRelation);
		}
	}

	/**
	 * 
	 * Method Name: validateRelintBasedSuggestions Method Description:This
	 * function performs validations before Suggesting the Relationship based on
	 * Relint.
	 * 
	 * 1. For Spouse relationships, only suggest if they are of different
	 * genders.
	 * 
	 * 2. Do not suggest spouse relationship if either has already spouse
	 * relationship.
	 * 
	 * @param ctxPerson
	 * @param personValueDto
	 * @param relRelintDefValueBeanDto
	 * @param ctxPersonRelations
	 * @return Boolean
	 */
	public Boolean validateRelintBasedSuggestions(PersonValueDto ctxPerson, PersonValueDto personValueDto,
			FTPersonRelationDto relRelintDefValueBeanDto, List<FTPersonRelationDto> ctxPersonRelations) {
		boolean validRel = false;

		String suggestedRel = relRelintDefValueBeanDto.getCdRelation();

		validRel = validateSpouseGender(ctxPerson.getSex(), personValueDto.getSex(), suggestedRel);

		if (validRel) {
			validRel = validateSposeRel(ctxPerson.getPersonId(), personValueDto.getPersonId(), ctxPersonRelations,
					suggestedRel);
		}

		if (validRel) {
			validRel = validateBioParentOrChild(ctxPerson, personValueDto, ctxPersonRelations,
					relRelintDefValueBeanDto);
		}

		return validRel;
	}

	/**
	 * 
	 * Method Name: validateBioParentOrChild Method Description:This function
	 * verifies if the Child has biological parent of same gender before
	 * suggesting Biological Parent relationship.
	 * 
	 * @param ctxPerson
	 * @param personValueDto
	 * @param ctxPersonRelations
	 * @param relRelintDefValueBeanDto
	 * @return
	 */
	private Boolean validateBioParentOrChild(PersonValueDto ctxPerson, PersonValueDto personValueDto,
			List<FTPersonRelationDto> ctxPersonRelations, FTPersonRelationDto relRelintDefValueBeanDto) {
		Boolean validRel = Boolean.TRUE;
		String suggestedRel = relRelintDefValueBeanDto.getCdRelation();
		String suggestedRelType = relRelintDefValueBeanDto.getCdType();
		Long idContextPerson = ctxPerson.getPersonId();
		Long idRelatedPerson = personValueDto.getPersonId();

		if (ServiceConstants.CFAMLREL_PA.equals(suggestedRel)
				&& ServiceConstants.CRELTYPE_BI.equals(suggestedRelType)) {
			for (FTPersonRelationDto ftPersonRelationDto : ctxPersonRelations) {
				if ((idContextPerson.equals(ftPersonRelationDto.getIdPerson())
						|| idContextPerson.equals(ftPersonRelationDto.getIdRelatedPerson()))
						&& (TypeConvUtil.isNullOrEmpty(ftPersonRelationDto.getDtInvalid())
								&& TypeConvUtil.isNullOrEmpty(ftPersonRelationDto.getDtEnded())
								&& ServiceConstants.CFAMLREL_PA.equals(ftPersonRelationDto.getCdRelation())
								&& ServiceConstants.CRELTYPE_BI.equals(ftPersonRelationDto.getCdType()))) {
					String cdSexRelatedPerson = ftPersonRelationDto.getCdSexRelatedPerson();
					cdSexRelatedPerson = StringUtil.isValid(cdSexRelatedPerson) ? cdSexRelatedPerson
							: ServiceConstants.EMPTY_STRING;
					if (!StringUtil.isValid(cdSexRelatedPerson) || !StringUtil.isValid(personValueDto.getSex())
							|| cdSexRelatedPerson.equals(personValueDto.getSex())) {
						validRel = Boolean.FALSE;
						break;
					}
				}
			}
		}

		if (ServiceConstants.CFAMLREL_CH.equals(suggestedRel)
				&& ServiceConstants.CRELTYPE_BI.equals(suggestedRelType)) {
			for (FTPersonRelationDto ftPersonRelationDto : ctxPersonRelations) {
				if ((idRelatedPerson.equals(ftPersonRelationDto.getIdPerson())
						|| idRelatedPerson.equals(ftPersonRelationDto.getIdRelatedPerson()))
						&& (TypeConvUtil.isNullOrEmpty(ftPersonRelationDto.getDtInvalid())
								&& TypeConvUtil.isNullOrEmpty(ftPersonRelationDto.getDtEnded())
								&& ServiceConstants.CFAMLREL_PA.equals(ftPersonRelationDto.getCdRelation())
								&& ServiceConstants.CRELTYPE_BI.equals(ftPersonRelationDto.getCdType()))) {
					// Check if valid and not end dated relationship exists
					String cdSexContextPerson = ftPersonRelationDto.getCdSexContextPerson();
					cdSexContextPerson = StringUtil.isValid(cdSexContextPerson) ? cdSexContextPerson
							: ServiceConstants.EMPTY_STRING;
					if (!StringUtil.isValid(cdSexContextPerson) || !StringUtil.isValid(ctxPerson.getSex())
							|| cdSexContextPerson.equals(ctxPerson.getSex())) {
						validRel = ServiceConstants.FALSEVAL;
						break;
					}
				}
			}
		}

		return validRel;
	}
}
