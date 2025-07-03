package us.tx.state.dfps.service.person.dao;

import java.util.ArrayList;
import java.util.List;

import us.tx.state.dfps.common.domain.EducationalHistory;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.person.dto.EducationHistoryDto;
import us.tx.state.dfps.service.person.dto.EducationalNeedDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:DAO
 * Interface class for Education May 31, 2018- 11:11:04 AM © 2017 Texas
 * Department of Family and Protective Services
 */
public interface EducationDao {

	/**
	 * 
	 * Method Name: getEducationHistoryList Method Description: get Education
	 * History List by person Id
	 * 
	 * @param idPerson
	 * @return
	 */
	public List<EducationalHistory> getEducationHistoryList(long idPerson);

	/**
	 * 
	 * Method Name: getEducationHistory Method Description: get education
	 * history by education history id
	 * 
	 * @param idEdhist
	 * @return
	 */
	public EducationalHistory getEducationHistory(long idEdhist);

	/**
	 * 
	 * Method Name: updateEducationalHistory Method Description: Method to
	 * update Educational History record
	 * 
	 * @param educationHistoryDto
	 * @return
	 */
	public long updateEducationalHistory(EducationHistoryDto educationHistoryDto);

	/**
	 * 
	 * Method Name: saveEducation Method Description: save education detail
	 * 
	 * @param educationHistoryDto
	 * @return
	 */
	public long saveEducation(EducationHistoryDto educationHistoryDto);

	/**
	 * 
	 * Method Name: saveEducationalNeed Method Description: Inserts a new record
	 * into EducationalNeed Table
	 * 
	 * @param educationalNeedDto
	 * @return
	 */
	public long saveEducationalNeed(EducationalNeedDto educationalNeedDto);

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
	public List<EducationalNeedDto> getEducationalNeedListForHist(int idPerson, int idEduHist, int idReferenceData,
			String cdActionType, String cdSnapshotType);

	/**
	 * Method Name: getEducationalNeedListForHist Method Description: This
	 * smethod fetches the education need records for a education history
	 * record.
	 * 
	 * @param idEduHist
	 * @return ArrayList<EducationalNeedDto>
	 */
	public ArrayList<EducationalNeedDto> getEducationalNeedListForHist(Long idEduHist);

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
	public EducationHistoryDto getCurrentEducationHistory(Long idPerson, Long idReferenceData, String cdActionType,
			String cdSnapshotType);

	/**
	 * Method Name: getCurrentEducationHistoryById Method Description: This
	 * method gets current Education for input person id
	 * 
	 * @param idPerson
	 * @return EducationHistoryDto
	 * @throws DataNotFoundException
	 */
	public EducationHistoryDto getCurrentEducationHistoryById(Long idPerson);
}
