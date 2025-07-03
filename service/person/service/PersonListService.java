package us.tx.state.dfps.service.person.service;

import java.util.List;

import us.tx.state.dfps.service.common.request.CaseExtendedPersonReq;
import us.tx.state.dfps.service.common.request.GroupUpdateReq;
import us.tx.state.dfps.service.common.request.PersonListReq;
import us.tx.state.dfps.service.common.request.UpdateSearchPersonIndReq;
import us.tx.state.dfps.service.common.response.CaseExtendedPersonRes;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.GroupUpdateRes;
import us.tx.state.dfps.service.common.response.IDListRes;
import us.tx.state.dfps.service.workload.dto.AdminReviewDto;
import us.tx.state.dfps.service.common.response.PersonListRes;
import us.tx.state.dfps.service.common.response.UpdateSearchPersonIndRes;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.common.dto.StagePersonValueDto;


/**
 * 
 * service-business - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CINV01S
 * Class Description: Person List Service interface Apr 20, 2017 - 6:12:09 PM
 */

public interface PersonListService {

	/**
	 * 
	 * Method Description: This method is used to retreive person list based on
	 * input provided to PersonListReq Tuxedo Service Name: CINV01S
	 * 
	 * @param retrievePersonListReq
	 * @ @throws
	 *       ParseException
	 */
	public PersonListRes getPersonList(PersonListReq retrievePersonListReq);

	/**
	 * This service will update only the person search indicator on the stage
	 * person link table. Service Name - CINV50S
	 * 
	 * @param updateSearchPersonIndReq
	 * @return @
	 */
	public UpdateSearchPersonIndRes personSearchIndUpdate(UpdateSearchPersonIndReq updateSearchPersonIndReq);

	/**
	 * This Method will retrieve the list of citizenship status for the person
	 * within the stage. Ejb Service name : PersonList
	 *
	 * @param personListReq
	 * @return PersonListRes @
	 */
	public PersonListRes fetchPersonCitizenshipDtls(PersonListReq personListReq);

	/**
	 * This Method will retrieve the case person list for the person
	 * information. Ejb Service name : PersonList
	 *
	 * @param personListReq
	 * @return PersonListRes @
	 */
	public PersonListRes fetchCasePersonList(PersonListReq personListReq);

	/**
	 * Method Name:getExtendedPersonList Method Description:This method returns
	 * list of extended case persons list related to a single person
	 * 
	 * @param caseExtendedPersonReq
	 * @return CaseExtendedPersonRes
	 */
	public CaseExtendedPersonRes fetchExtendedPersonList(CaseExtendedPersonReq caseExtendedPersonReq);

	/**
	 * 
	 * Method Name: getPersonAddress Method Description:This method get person
	 * address details
	 * 
	 * @param idPerson
	 * @return PersonDto @
	 */
	public PersonDto fetchPersonAddress(Long idPerson);

	GroupUpdateRes getGroupUpdateList(PersonListReq retrieveGroupUpdateListReq);

	GroupUpdateRes saveGroupUpdate(GroupUpdateReq saveGroupUpdateReq);

	/**
	 * Method Name: isPersPlcmtWithSameCareGiver Method Description:
	 * 
	 * @param personIds
	 * @return
	 */
	public String isPersPlcmtWithSameCareGiver(List<Long> personIds);

	/**
	 * Method Name: getDayCarePersonIdList Method Description:
	 * 
	 * @param idEvent
	 * @return
	 */
	public List<Long> getDayCarePersonIdList(Long idEvent);

	/**
	 * Method Name: isPersAtSameAddr :his service checks if all persons reside
	 * at the same location address Method Description:
	 * 
	 * @param personIds
	 * @return Boolean
	 */
	public Boolean isPersAtSameAddr(List<Long> personIds);

	public CommonHelperRes getPrimaryCaseworkerForStage(Long idStage);

	/**
	 * Method Name: getPersonLists Method Description:
	 * 
	 * @param retrievePersonListReq
	 * @return
	 */
	public PersonListRes getPersonLists(PersonListReq retrievePersonListReq);

	/**
	 * Method Name: getPersonListByStage Method Description:
	 * 
	 * @param personListReq
	 * @return PersonListRes
	 */
	PersonListRes getPersonListByStage(PersonListReq personListReq);

	public StagePersonValueDto selectStagePersonLink(Long idEvent, Long idStage);


   public IDListRes getAdminReviewOpenStagesByPerson(Long idPerson, Long idStage);

	public  IDListRes getAdminReviewOpenExists(Long idStage);

	public AdminReviewDto getAdminReviewDetails(Long idStage);
}
