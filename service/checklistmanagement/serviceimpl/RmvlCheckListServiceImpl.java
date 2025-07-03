package us.tx.state.dfps.service.checklistmanagement.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.rmvlchecklist.dto.RmvlChcklstLinkDto;
import us.tx.state.dfps.rmvlchecklist.dto.RmvlChcklstLookupDto;
import us.tx.state.dfps.rmvlchecklist.dto.RmvlChcklstRspnDto;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.checklistmanagement.dao.RmvlCheckListDao;
import us.tx.state.dfps.service.checklistmanagement.service.RmvlCheckListService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.response.CnsrvtrshpRemovalRes;
import us.tx.state.dfps.service.common.response.RmvlCheckListRes;

@Service
@Transactional
public class RmvlCheckListServiceImpl implements RmvlCheckListService {

	@Autowired
	RmvlCheckListDao removalChecklistDao;
	@Autowired
	PostEventService postEventService;

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, rollbackFor = Exception.class)
	public RmvlCheckListRes getRmvlChcklsts() {
		RmvlCheckListRes result = new RmvlCheckListRes();
		result.setAllRmvlCheckList(removalChecklistDao.getRmvlChcklsts());
		return result;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, rollbackFor = Exception.class)
	public RmvlCheckListRes getRmvlChcklstDtl(Long checklistId,Long idPerson,Long idRmvlChcklstLink, Long idStage, Long idRemovalEvent) {
		RmvlCheckListRes result = new RmvlCheckListRes();
		result.setRmvlChcklstLookupDto(removalChecklistDao.getRmvlChcklstDtl(checklistId,idPerson,idRmvlChcklstLink));
		result.setLinks(removalChecklistDao.getRmvlChcklstLink(idPerson, idStage, idRemovalEvent));
		return result;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, rollbackFor = Exception.class)
	public RmvlCheckListRes copyRmvlChcklst(Long checklistId) {
		RmvlCheckListRes result = new RmvlCheckListRes();
		result.setRmvlChcklstLookupDto(removalChecklistDao.copyRmvlChcklst(checklistId));
		return result;

	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, rollbackFor = Exception.class)
	public RmvlCheckListRes deleteRmvlChcklstTaskDtl(List<Long> taskId) {

		RmvlCheckListRes response = new RmvlCheckListRes();
		for (Long idTask : taskId) {
			removalChecklistDao.deleteRmvlChcklstTaskLookupDtl(idTask);
		}
		response.setMessage(ServiceConstants.SUCCESS);
		return response;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, rollbackFor = Exception.class)
	public RmvlCheckListRes saveRmvlCheckList(RmvlChcklstLookupDto checklist, String IndSave) {
		Long idRmvlChcklstLookup = removalChecklistDao.saveRmvlChcklst(checklist, IndSave);
		;
		RmvlCheckListRes response = new RmvlCheckListRes();
		RmvlChcklstLookupDto rmvlChcklstLookupDto = new RmvlChcklstLookupDto();
		rmvlChcklstLookupDto.setIdRmvlChcklstLookup(idRmvlChcklstLookup);
		response.setRmvlChcklstLookupDto(rmvlChcklstLookupDto);
		response.setMessage(ServiceConstants.SUCCESS);
		return response;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, rollbackFor = Exception.class)
	public RmvlCheckListRes updateRmvlCheckList(RmvlChcklstLookupDto checklist, Long idPerson, Long idRmvlChcklstLink) {
		RmvlCheckListRes result = new RmvlCheckListRes();
		result.setRmvlChcklstLookupDto(removalChecklistDao.updateRmvlChcklst(checklist,idPerson,idRmvlChcklstLink));
		return result;
	}

	/**
	 * Method Description: Service Layer method to fetch the Removal Checklist
	 * Link Records for the passed Person ID and Stage ID. If no records are
	 * fetched then it means that the checklist is being created for the first
	 * time.
	 * 
	 * @param idPerson
	 *            - Person ID
	 * @param idStage
	 *            - Stage ID
	 * @return - RmvlCheckListRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, rollbackFor = Exception.class)
	public RmvlCheckListRes getRmvlChcklstLink(Long idPerson, Long idStage, Long idRemovalEvent) {
		RmvlCheckListRes result = new RmvlCheckListRes();
		result.setLinks(removalChecklistDao.getRmvlChcklstLink(idPerson, idStage, idRemovalEvent));
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.checklistmanagement.service.RmvlCheckListService
	 * #isRecordExist(java.lang.Long)
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, rollbackFor = Exception.class)
	public boolean indRecordExist(Long idRmvlEvent) {

		return removalChecklistDao.indRecordExist(idRmvlEvent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.checklistmanagement.service.RmvlCheckListService
	 * #getPersonList(java.lang.Long)
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, rollbackFor = Exception.class)
	public CnsrvtrshpRemovalRes getPersonList(Long idRmvlEvent) {
		CnsrvtrshpRemovalRes result = new CnsrvtrshpRemovalRes();
		result.setCnsrvtrshpRmvlLists(removalChecklistDao.getPersonList(idRmvlEvent));
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.checklistmanagement.service.RmvlCheckListService
	 * #saveRmvlChcklstRspn(us.tx.state.dfps.rmvlchecklist.dto.
	 * RmvlChcklstRspnDto)
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, rollbackFor = Exception.class)
	public RmvlCheckListRes saveRmvlChcklstRspn(List<RmvlChcklstRspnDto> checklstRspn) {
		RmvlCheckListRes result = new RmvlCheckListRes();
		result.setIds(removalChecklistDao.saveRmvlChcklstRspn(checklstRspn));
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.checklistmanagement.service.RmvlCheckListService
	 * #updateRmvlChcklstRspn(us.tx.state.dfps.rmvlchecklist.dto.
	 * RmvlChcklstRspnDto)
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, rollbackFor = Exception.class)
	public RmvlCheckListRes updateRmvlChcklstRspn(List<RmvlChcklstRspnDto> checklstRspn) {
		RmvlCheckListRes result = new RmvlCheckListRes();

		result.setMessage(removalChecklistDao.updateRmvlChcklstRspn(checklstRspn));
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.checklistmanagement.service.RmvlCheckListService
	 * #getRmvlChcklstRspn(java.lang.Long)
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, rollbackFor = Exception.class)
	public RmvlCheckListRes getRmvlChcklstRspn(Long idPerson) {

		RmvlCheckListRes result = new RmvlCheckListRes();
		result.setRmvlChcklstRspnDto(removalChecklistDao.getRmvlChcklstRspn(idPerson));
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.checklistmanagement.service.RmvlCheckListService
	 * #saveRmvlChcklstLink(us.tx.state.dfps.rmvlchecklist.dto.
	 * RmvlChcklstLinkDto)
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, rollbackFor = Exception.class)
	public RmvlCheckListRes saveRmvlChcklstLink(List<RmvlChcklstLinkDto> rmvlChcklstLinkDto) {
		RmvlCheckListRes result = new RmvlCheckListRes();
		result.setIdLinkPersonMap(removalChecklistDao.saveRmvlChcklstLink(rmvlChcklstLinkDto));
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.checklistmanagement.service.RmvlCheckListService
	 * #updateRmvlChcklstLink(us.tx.state.dfps.rmvlchecklist.dto.
	 * RmvlChcklstLinkDto)
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, rollbackFor = Exception.class)
	public RmvlCheckListRes updateRmvlChcklstLink(List<RmvlChcklstLinkDto> rmvlChcklstLinkDto) {
		RmvlCheckListRes result = new RmvlCheckListRes();
		result.setMessage(removalChecklistDao.updateRmvlChcklstLink(rmvlChcklstLinkDto));
		return result;
	}

}
