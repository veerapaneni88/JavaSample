package us.tx.state.dfps.service.checklistmanagement.daoimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.checklist.mapper.RmvlChcklstLookupDtoMapper;
import us.tx.state.dfps.common.domain.CnsrvtrshpRemoval;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.RmvlChcklstLink;
import us.tx.state.dfps.common.domain.RmvlChcklstLookup;
import us.tx.state.dfps.common.domain.RmvlChcklstRspn;
import us.tx.state.dfps.common.domain.RmvlChcklstSctnLookup;
import us.tx.state.dfps.common.domain.RmvlChcklstTaskLookup;
import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.rmvlchecklist.dto.RmvlChcklstLinkDto;
import us.tx.state.dfps.rmvlchecklist.dto.RmvlChcklstLookupDto;
import us.tx.state.dfps.rmvlchecklist.dto.RmvlChcklstRspnDto;
import us.tx.state.dfps.rmvlchecklist.dto.RmvlChcklstSctnLookupDto;
import us.tx.state.dfps.rmvlchecklist.dto.RmvlChcklstTaskLookupDto;
import us.tx.state.dfps.service.checklistmanagement.dao.RmvlCheckListDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.conservatorship.dao.CnsrvtrshpRemovalDao;
import us.tx.state.dfps.service.conservatorship.dto.CnsrvtrshpRemovalDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:RmvlCheckListDaoImpl will implemented all operation defined in
 * RmvlCheckListDao Interface related RmvlCheckList module.. Feb 9, 2018-
 * 2:02:51 PM © 2017 Texas Department of Family and Protective Services
 *********************************  Change History *********************************
 * 05/18/2020 ramasn artf137716 CPI Project#46802
 * 07/17/2020 thompswa artf159646 CPI Project#46802
 */
@Repository
public class RmvlCheckListDaoImpl implements RmvlCheckListDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${RmvlCheckListDaoImpl.updateCheckLists}")
	private String updateCheckListsSql;

	@Autowired
	CnsrvtrshpRemovalDao cnsrvtrshpRemovalDao;

	@Autowired
	LookupDao lookupDao;

	private static final Logger logger =
			Logger.getLogger(RmvlCheckListDaoImpl.class.getName());

	@SuppressWarnings("unchecked")
	@Override
	public List<RmvlChcklstLookupDto> getRmvlChcklsts() {
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(RmvlChcklstLookup.class)
				.setProjection(Projections.projectionList()
						.add(Projections.property("idRmvlChcklstLookup"), "idRmvlChcklstLookup")
						.add(Projections.property("cdRmvlChcklstStatus"), "cdRmvlChcklstStatus")
						.add(Projections.property("dtCreated"), "dtCreated")
						.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
						.add(Projections.property("idCreatedPerson"), "idCreatedPerson")
						.add(Projections.property("idLastUpdatePerson"), "idLastUpdatePerson")
						.add(Projections.property("nmChcklst"), "nmChcklst")
						.add(Projections.property("txtInstrctns"), "instrctns")
						.add(Projections.property("txtNote"), "note").add(Projections.property("txtNote"), "note")
						.add(Projections.property("txtPurps"), "purps"))
				.setResultTransformer(Transformers.aliasToBean(RmvlChcklstLookupDto.class));
		List<RmvlChcklstLookupDto> allChecklists = cr.list();
		return allChecklists;
	}

	@SuppressWarnings("unchecked")
	@Override
	public RmvlChcklstLookupDto getRmvlChcklstDtl(Long idRmvlChcklstLookup, Long idPerson, Long idRmvlChcklstLink) {
		RmvlChcklstLookupDto checklist = new RmvlChcklstLookupDto();
		if (!TypeConvUtil.isNullOrEmpty(idRmvlChcklstLookup)) {
			Criteria cr = sessionFactory.getCurrentSession().createCriteria(RmvlChcklstLookup.class)
					.add(Restrictions.eq("idRmvlChcklstLookup", idRmvlChcklstLookup))
					.setProjection(Projections.projectionList()
							.add(Projections.property("idRmvlChcklstLookup"), "idRmvlChcklstLookup")
							.add(Projections.property("cdRmvlChcklstStatus"), "cdRmvlChcklstStatus")
							.add(Projections.property("dtCreated"), "dtCreated")
							.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
							.add(Projections.property("idCreatedPerson"), "idCreatedPerson")
							.add(Projections.property("idLastUpdatePerson"), "idLastUpdatePerson")
							.add(Projections.property("nmChcklst"), "nmChcklst")
							.add(Projections.property("txtInstrctns"), "instrctns")
							.add(Projections.property("txtNote"), "note").add(Projections.property("txtNote"), "note")
							.add(Projections.property("txtPurps"), "purps"))
					.setResultTransformer(Transformers.aliasToBean(RmvlChcklstLookupDto.class));
			checklist = (RmvlChcklstLookupDto) cr.uniqueResult();
			Criteria cr2 = sessionFactory.getCurrentSession().createCriteria(RmvlChcklstSctnLookup.class, "section")
					.createAlias("section.rmvlChcklstLookup", "chcklst")
					.add(Restrictions.eq("chcklst.idRmvlChcklstLookup", idRmvlChcklstLookup))
					.setProjection(Projections.projectionList()
							.add(Projections.property("section.idRmvlChcklstSctnLookup"), "idRmvlChcklstSctnLookup")
							.add(Projections.property("section.dtCreated"), "dtCreated")
							.add(Projections.property("section.dtLastUpdate"), "dtLastUpdate")
							.add(Projections.property("section.idCreatedPerson"), "idCreatedPerson")
							.add(Projections.property("section.idLastUpdatePerson"), "idLastUpdatePerson")
							.add(Projections.property("section.nbrOrder"), "order"))
					.setResultTransformer(Transformers.aliasToBean(RmvlChcklstSctnLookupDto.class));
			List<RmvlChcklstSctnLookupDto> sections = cr2.list();
			for (RmvlChcklstSctnLookupDto rmvlChcklstSctnLookupDto : sections) {
				Criteria temp = sessionFactory.getCurrentSession().createCriteria(RmvlChcklstTaskLookup.class, "task")
						.createAlias("task.rmvlChcklstSctnLookup", "sctn")
						.add(Restrictions.eq("sctn.idRmvlChcklstSctnLookup", rmvlChcklstSctnLookupDto.getIdRmvlChcklstSctnLookup()))
						.setProjection(Projections.projectionList()
								.add(Projections.property("task.idRmvlChcklstTaskLookup"), "idRmvlChcklstTaskLookup")
								.add(Projections.property("task.dtCreated"), "dtCreated")
								.add(Projections.property("task.dtLastUpdate"), "dtLastUpdate")
								.add(Projections.property("task.cdTrigger"), "cdTrigger")
								.add(Projections.property("task.cdTriggerInterval"), "cdTriggerInterval")
								.add(Projections.property("task.dtEnd"), "dtEnd")
								.add(Projections.property("task.idCreatedPerson"), "idCreatedPerson")
								.add(Projections.property("task.idLastUpdatePerson"), "idLastUpdatePerson")
								.add(Projections.property("task.idRmvlChcklstTaskGroup"), "idRmvlChcklstTaskGroup")
								.add(Projections.property("task.indHeader"), "indHeader")
								.add(Projections.property("task.nbrOrder"), "order")
								.add(Projections.property("task.txtDesc"), "desc")
								.add(Projections.property("task.indTaskDltd"), "indTaskDltd")
								.add(Projections.property("task.nbrTriggerValue"), "triggerValue"))
						.setResultTransformer(Transformers.aliasToBean(RmvlChcklstTaskLookupDto.class));
				temp.add(Restrictions.or(Restrictions.isNull("task.indTaskDltd"),
						Restrictions.ne("task.indTaskDltd", ServiceConstants.YES)));
				List<RmvlChcklstTaskLookupDto> taskList = temp.list();

					for (RmvlChcklstTaskLookupDto rmvlCheckTaskDtlDto : taskList) {

						Criteria cr3 = sessionFactory.getCurrentSession().createCriteria(RmvlChcklstRspn.class, "rspn")
								.createAlias("rspn.rmvlChcklstTaskLookup", "tsk")
								.add(Restrictions.eq("tsk.idRmvlChcklstTaskLookup",
										rmvlCheckTaskDtlDto.getIdRmvlChcklstTaskLookup()))
								.add(Restrictions.eq("rspn.idPerson",idPerson))
								.setProjection(Projections.projectionList()
										.add(Projections.property("rspn.idRmvlChcklstRspns"), "idRmvlChcklstRspns")
										.add(Projections.property("rspn.dtCreated"), "dtCreated")
										.add(Projections.property("rspn.dtLastUpdate"), "dtLastUpdate")
										.add(Projections.property("rspn.dtPurge"), "dtPurge")
										.add(Projections.property("rspn.idCreatedPerson"), "idCreatedPerson")
										.add(Projections.property("rspn.idLastUpdatePerson"), "idLastUpdatePerson")
										.add(Projections.property("rspn.idPerson"), "idPerson")
										.add(Projections.property("rspn.idRmvlChcklstTaskGroup"), "idRmvlChcklstTaskGroup")
										.add(Projections.property("rspn.txtRspns"), "rspns")
										.add(Projections.property("rspn.indTaskCmpltd"), "indTaskCmpltd"))
								.setResultTransformer(Transformers.aliasToBean(RmvlChcklstRspnDto.class));
						// artf137716 for tasks after june 2020 cpi project, rspns will have idRvmlChcklstLink
						if (null != idRmvlChcklstLink) {
							cr3.add(Restrictions.eq("rspn.idRmvlChcklstLink", idRmvlChcklstLink));
						}
						rmvlCheckTaskDtlDto.setRmvlChcklstRspns(cr3.list());
					}
				rmvlChcklstSctnLookupDto.setRmvlChcklstTaskLookups(taskList);
			}
			checklist.setRmvlChcklstSctnLookups(sections);
		} else {
			Criteria cr = sessionFactory.getCurrentSession().createCriteria(RmvlChcklstLookup.class)
					.add(Restrictions.eq("cdRmvlChcklstStatus", ServiceConstants.RMVL_CHECKLIST_STATUS_ACTIVE))
					.setProjection(Projections.projectionList()
							.add(Projections.property("idRmvlChcklstLookup"), "idRmvlChcklstLookup")
							.add(Projections.property("cdRmvlChcklstStatus"), "cdRmvlChcklstStatus")
							.add(Projections.property("dtCreated"), "dtCreated")
							.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
							.add(Projections.property("idCreatedPerson"), "idCreatedPerson")
							.add(Projections.property("idLastUpdatePerson"), "idLastUpdatePerson")
							.add(Projections.property("nmChcklst"), "nmChcklst")
							.add(Projections.property("txtInstrctns"), "instrctns")
							.add(Projections.property("txtNote"), "note").add(Projections.property("txtNote"), "note")
							.add(Projections.property("txtPurps"), "purps"))
					.setResultTransformer(Transformers.aliasToBean(RmvlChcklstLookupDto.class));
			checklist = (RmvlChcklstLookupDto) cr.uniqueResult();
			Criteria cr2 = sessionFactory.getCurrentSession().createCriteria(RmvlChcklstSctnLookup.class, "section")
					.add(Restrictions.eq("section.rmvlChcklstLookup.idRmvlChcklstLookup",
							checklist.getIdRmvlChcklstLookup()))
					.setProjection(Projections.projectionList()
							.add(Projections.property("section.idRmvlChcklstSctnLookup"), "idRmvlChcklstSctnLookup")
							.add(Projections.property("section.dtCreated"), "dtCreated")
							.add(Projections.property("section.dtLastUpdate"), "dtLastUpdate")
							.add(Projections.property("section.idCreatedPerson"), "idCreatedPerson")
							.add(Projections.property("section.idLastUpdatePerson"), "idLastUpdatePerson")
							.add(Projections.property("section.nbrOrder"), "order"))
					.setResultTransformer(Transformers.aliasToBean(RmvlChcklstSctnLookupDto.class));
			List<RmvlChcklstSctnLookupDto> sections = cr2.list();
			for (RmvlChcklstSctnLookupDto rmvlChcklstSctnLookupDto : sections) {
				Criteria temp = sessionFactory.getCurrentSession().createCriteria(RmvlChcklstTaskLookup.class, "task")
						.add(Restrictions.eq("task.rmvlChcklstSctnLookup.idRmvlChcklstSctnLookup",
								rmvlChcklstSctnLookupDto.getIdRmvlChcklstSctnLookup()))
						.setProjection(Projections.projectionList()
								.add(Projections.property("task.idRmvlChcklstTaskLookup"), "idRmvlChcklstTaskLookup")
								.add(Projections.property("task.dtCreated"), "dtCreated")
								.add(Projections.property("task.dtLastUpdate"), "dtLastUpdate")
								.add(Projections.property("task.cdTrigger"), "cdTrigger")
								.add(Projections.property("task.cdTriggerInterval"), "cdTriggerInterval")
								.add(Projections.property("task.dtEnd"), "dtEnd")
								.add(Projections.property("task.idCreatedPerson"), "idCreatedPerson")
								.add(Projections.property("task.idLastUpdatePerson"), "idLastUpdatePerson")
								.add(Projections.property("task.idRmvlChcklstTaskGroup"), "idRmvlChcklstTaskGroup")
								.add(Projections.property("task.indHeader"), "indHeader")
								.add(Projections.property("task.nbrOrder"), "order")
								.add(Projections.property("task.txtDesc"), "desc")
								.add(Projections.property("task.indTaskDltd"), "indTaskDltd")
								.add(Projections.property("task.nbrTriggerValue"), "triggerValue"))
						.setResultTransformer(Transformers.aliasToBean(RmvlChcklstTaskLookupDto.class));
				temp.add(Restrictions.or(Restrictions.isNull("task.indTaskDltd"),
						Restrictions.ne("task.indTaskDltd", ServiceConstants.YES)));
				List<RmvlChcklstTaskLookupDto> taskList = temp.list();
				for (RmvlChcklstTaskLookupDto rmvlCheckTaskDtlDto : taskList) {
					Criteria cr3 = sessionFactory.getCurrentSession().createCriteria(RmvlChcklstRspn.class, "rspn")
							.add(Restrictions.eq("rspn.rmvlChcklstTaskLookup.idRmvlChcklstTaskLookup",
									rmvlCheckTaskDtlDto.getIdRmvlChcklstTaskLookup()))
							.add(Restrictions.eq("rspn.idPerson",idPerson))
							.setProjection(Projections.projectionList()
									.add(Projections.property("rspn.idRmvlChcklstRspns"), "idRmvlChcklstRspns")
									.add(Projections.property("rspn.dtCreated"), "dtCreated")
									.add(Projections.property("rspn.dtLastUpdate"), "dtLastUpdate")
									.add(Projections.property("rspn.dtPurge"), "dtPurge")
									.add(Projections.property("rspn.idCreatedPerson"), "idCreatedPerson")
									.add(Projections.property("rspn.idLastUpdatePerson"), "idLastUpdatePerson")
									.add(Projections.property("rspn.idPerson"), "idPerson")
									.add(Projections.property("rspn.idRmvlChcklstTaskGroup"), "idRmvlChcklstTaskGroup")
									.add(Projections.property("rspn.txtRspns"), "rspns")
									.add(Projections.property("rspn.indTaskCmpltd"), "indTaskCmpltd"))
							.setResultTransformer(Transformers.aliasToBean(RmvlChcklstRspnDto.class));
					rmvlCheckTaskDtlDto.setRmvlChcklstRspns(cr3.list());
				}
				rmvlChcklstSctnLookupDto.setRmvlChcklstTaskLookups(taskList);
			}
			checklist.setRmvlChcklstSctnLookups(sections);
		}
		return checklist;
	}

	@Override
	public void deleteRmvlChcklstTaskLookupDtl(Long taskId) {
		// load the RmvlCheckList with ID = checklistid
		RmvlChcklstTaskLookup rmvlChcklstTaskLookup = (RmvlChcklstTaskLookup) sessionFactory.getCurrentSession()
				.get(RmvlChcklstTaskLookup.class, taskId);
		// get section with sectionId = sectionId
		// update above task the above task to indicate deleted status
		rmvlChcklstTaskLookup.setIndTaskDltd(ServiceConstants.RMVL_CHECK_INDTASKDELETED);
		sessionFactory.getCurrentSession().update(rmvlChcklstTaskLookup);
		sessionFactory.getCurrentSession().flush();
	}

	@Override
	public RmvlChcklstLookupDto copyRmvlChcklst(Long checklistId) {
		RmvlChcklstLookup rmvlCheckList = (RmvlChcklstLookup) sessionFactory.getCurrentSession()
				.get(RmvlChcklstLookup.class, checklistId);
		rmvlCheckList.setIdRmvlChcklstLookup(null);
		List<RmvlChcklstSctnLookup> rmvlCheckSecDtls = rmvlCheckList.getRmvlChcklstSctnLookups();
		for (RmvlChcklstSctnLookup section : rmvlCheckSecDtls) {
			section.setIdRmvlChcklstSctnLookup(null);
			List<RmvlChcklstTaskLookup> rmvlCheckTaskDtls = section.getRmvlChcklstTaskLookups();
			for (RmvlChcklstTaskLookup task : rmvlCheckTaskDtls) {
				task.setIdRmvlChcklstTaskLookup(null);
				List<RmvlChcklstRspn> rmvlCheckListRsps = task.getRmvlChcklstRspns();
				for (RmvlChcklstRspn rsps : rmvlCheckListRsps) {
					sessionFactory.getCurrentSession().evict(rsps);
					rsps.setIdRmvlChcklstRspns(null);
				}
				task.setRmvlChcklstRspns(null);
				sessionFactory.getCurrentSession().evict(task); // artf159646 move evict here
			}
			sessionFactory.getCurrentSession().evict(section); // artf159646 move evict here
		}
		List<RmvlChcklstLink> checkListLinks = rmvlCheckList.getRmvlChcklstLinks();
		for (RmvlChcklstLink link : checkListLinks) {
			sessionFactory.getCurrentSession().evict(link);
			link.setIdRmvlChcklstLink(null);
		}
		rmvlCheckList.setRmvlChcklstLinks(null);
		sessionFactory.getCurrentSession().evict(rmvlCheckList); // artf159646 move evict here
		rmvlCheckList.setCdRmvlChcklstStatus(ServiceConstants.RMVL_CHECKLIST_STATUS_PENDING);
		Long result = (Long) sessionFactory.getCurrentSession().save(rmvlCheckList);
		sessionFactory.getCurrentSession().flush();
		return getRmvlChcklstDtl(result,ServiceConstants.ZERO, ServiceConstants.NULL_VAL);
	}

	@Override
	public Long saveRmvlChcklst(RmvlChcklstLookupDto checklist, String IndSave) {
		RmvlChcklstLookupDtoMapper mapper = new RmvlChcklstLookupDtoMapper();
		mapper.setRmvlChcklstLookupDto(checklist);
		RmvlChcklstLookup rmvlCheckList = mapper.getRmvlCheckListLookup();
		Long idRmvlChcklstLookup = ServiceConstants.NULL_VAL;
		if (IndSave.equals(ServiceConstants.RMVL_CHECK_SAVENPUBLISH)) {
			Query query = sessionFactory.getCurrentSession().createQuery(updateCheckListsSql);
			query.setParameter(ServiceConstants.Zero_INT, ServiceConstants.RMVL_CHECKLIST_STATUS_INACTIVE);
			query.setParameter(ServiceConstants.One, ServiceConstants.RMVL_CHECKLIST_STATUS_ACTIVE);
			query.executeUpdate();
			rmvlCheckList.setCdRmvlChcklstStatus(ServiceConstants.RMVL_CHECKLIST_STATUS_ACTIVE);
			RmvlChcklstLookup temp;
			if (checklist.getIdRmvlChcklstLookup() != null) {
				temp = (RmvlChcklstLookup) sessionFactory.getCurrentSession().get(RmvlChcklstLookup.class,
						checklist.getIdRmvlChcklstLookup());
				sessionFactory.getCurrentSession().evict(temp);
				if (temp != null) {
					sessionFactory.getCurrentSession().update(rmvlCheckList);
					idRmvlChcklstLookup = rmvlCheckList.getIdRmvlChcklstLookup();
				}
			} else {
				idRmvlChcklstLookup = (Long) sessionFactory.getCurrentSession().save(rmvlCheckList);
			}
		} else {
			rmvlCheckList.setCdRmvlChcklstStatus(ServiceConstants.RMVL_CHECKLIST_STATUS_PENDING);
			idRmvlChcklstLookup = (Long) sessionFactory.getCurrentSession().save(rmvlCheckList);
		}
		sessionFactory.getCurrentSession().flush();
		return idRmvlChcklstLookup;
	}

	@Override
	public RmvlChcklstLookupDto updateRmvlChcklst(RmvlChcklstLookupDto checklist, Long idPerson, Long idRmvlChcklstLink) {
		RmvlChcklstLookup chcklst = (RmvlChcklstLookup) sessionFactory.getCurrentSession().get(RmvlChcklstLookup.class,
				checklist.getIdRmvlChcklstLookup());
		chcklst.setCdRmvlChcklstStatus(checklist.getCdRmvlChcklstStatus());
		chcklst.setNmChcklst(checklist.getNmChcklst());
		chcklst.setTxtInstrctns(checklist.getInstrctns());
		chcklst.setTxtNote(checklist.getNote());
		chcklst.setTxtPurps(checklist.getPurps());
		chcklst.setIdCreatedPerson(checklist.getIdCreatedPerson());
		chcklst.setIdLastUpdatePerson(checklist.getIdLastUpdatePerson());
		sessionFactory.getCurrentSession().update(chcklst);
		sessionFactory.getCurrentSession().flush();
		List<RmvlChcklstSctnLookupDto> rmvlCheckSecDtls = checklist.getRmvlChcklstSctnLookups();
		if (null != rmvlCheckSecDtls) {
			for (RmvlChcklstSctnLookupDto rmvlSecDtlDto : rmvlCheckSecDtls) {
				RmvlChcklstSctnLookup temp = null;
				if (rmvlSecDtlDto.getIdRmvlChcklstSctnLookup() != null) {
					temp = (RmvlChcklstSctnLookup) sessionFactory.getCurrentSession().get(RmvlChcklstSctnLookup.class,
							rmvlSecDtlDto.getIdRmvlChcklstSctnLookup());
				} else {
					temp = new RmvlChcklstSctnLookup();
					temp.setRmvlChcklstLookup(chcklst);
					temp.setIdCreatedPerson(chcklst.getIdCreatedPerson());
				}
				temp.setNbrOrder(rmvlSecDtlDto.getOrder());
				temp.setIdLastUpdatePerson(checklist.getIdLastUpdatePerson());
				sessionFactory.getCurrentSession().saveOrUpdate(temp);
				;
				sessionFactory.getCurrentSession().flush();
				List<RmvlChcklstTaskLookupDto> rmvlCheckTaskDtls = rmvlSecDtlDto.getRmvlChcklstTaskLookups();
				if (null != rmvlCheckTaskDtls) {
					for (RmvlChcklstTaskLookupDto rmvlCheckTaskDtl : rmvlCheckTaskDtls) {
						RmvlChcklstTaskLookup task = null;
						if (null != rmvlCheckTaskDtl.getIdRmvlChcklstTaskLookup()) {
							task = (RmvlChcklstTaskLookup) sessionFactory.getCurrentSession()
									.get(RmvlChcklstTaskLookup.class, rmvlCheckTaskDtl.getIdRmvlChcklstTaskLookup());
						} else {
							task = new RmvlChcklstTaskLookup();
							task.setRmvlChcklstSctnLookup(temp);
						}
						task.setCdTrigger(rmvlCheckTaskDtl.getCdTrigger());
						task.setCdTriggerInterval(rmvlCheckTaskDtl.getCdTriggerInterval());
						task.setIdLastUpdatePerson(checklist.getIdLastUpdatePerson());
						task.setIndHeader(rmvlCheckTaskDtl.getIndHeader());
						task.setNbrOrder(rmvlCheckTaskDtl.getOrder());
						task.setIdCreatedPerson(chcklst.getIdCreatedPerson());
						task.setNbrTriggerValue(rmvlCheckTaskDtl.getTriggerValue());
						task.setTxtDesc(rmvlCheckTaskDtl.getDesc());
						task.setIdRmvlChcklstTaskGroup(rmvlCheckTaskDtl.getIdRmvlChcklstTaskGroup());
						task.setIndTaskDltd(rmvlCheckTaskDtl.getIndTaskDltd());
						task.setDtEnd(rmvlCheckTaskDtl.getDtEnd());
						sessionFactory.getCurrentSession().saveOrUpdate(task);
						sessionFactory.getCurrentSession().flush();
					}
				}
			}
		}
		return getRmvlChcklstDtl(checklist.getIdRmvlChcklstLookup(),idPerson, idRmvlChcklstLink);
	}

	/**
	 * Method Description: DAO Layer method to fetch the Removal Checklist Link
	 * Records for the passed Person ID and Stage ID. If no records are fetched
	 * then it means that the checklist is being created for the first time.
	 *
	 * @param idPerson
	 *            - Person ID
	 * @param idStage
	 *            - Stage ID
	 * @return - List<RmvlChcklstLinkDto>
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<RmvlChcklstLinkDto> getRmvlChcklstLink(Long idPerson, Long idStage, Long idRemovalEvent) {
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(RmvlChcklstLink.class, "link")
				.createAlias("link.person", "person").createAlias("link.stage", "stage").createAlias("link.cnsrvtrshpRemoval", "cnsrvtrshpRemoval")
				.setProjection(Projections.projectionList()
						.add(Projections.property("link.idRmvlChcklstLink"), "idRmvlChcklstLink")
						.add(Projections.property("link.dtCreated"), "dtCreated")
						.add(Projections.property("link.dtLastUpdate"), "dtLastUpdate")
						.add(Projections.property("link.dtPurge"), "dtPurge")
						.add(Projections.property("link.idCreatedPerson"), "idCreatedPerson")
						.add(Projections.property("link.idLastUpdatePerson"), "idLastUpdatePerson")
						.add(Projections.property("person.idPerson"), "idPerson")
						.add(Projections.property("stage.idStage"), "idStage")
						.add(Projections.property("link.cnsrvtrshpRemoval.idRemovalEvent"), "idRmvlEvent")
						.add(Projections.property("link.rmvlChcklstLookup.idRmvlChcklstLookup"), "idRmvlChcklstLookup")
						.add(Projections.property("link.txtChildList"), "txtChildList"))
				.setResultTransformer(Transformers.aliasToBean(RmvlChcklstLinkDto.class));
		cr.add(Restrictions.eq("person.idPerson", idPerson));
		cr.add(Restrictions.eq("stage.idStage", idStage));
		cr.add(Restrictions.eq("cnsrvtrshpRemoval.idRemovalEvent", idRemovalEvent));
		List<RmvlChcklstLinkDto> links = cr.list();
		return links;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see us.tx.state.dfps.service.checklistmanagement.dao.RmvlCheckListDao#
	 * isRecordExist(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean indRecordExist(Long idRmvlEvent) {
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CnsrvtrshpRemoval.class)
				.add(Restrictions.eq("idRemovalEvent", idRmvlEvent))
				.setProjection(Projections.projectionList().add(Projections.property("idRmvlGroup"), "idRmvlGroup"))
				.setResultTransformer(Transformers.aliasToBean(CnsrvtrshpRemovalDto.class));
		CnsrvtrshpRemovalDto cnsrvtrshpRemovalDto = (CnsrvtrshpRemovalDto) cr.uniqueResult();
		if (null == cnsrvtrshpRemovalDto.getIdRmvlGroup()) {
			Query query = sessionFactory.getCurrentSession()
					.createQuery("From RmvlChcklstLink	A where A.cnsrvtrshpRemoval.idRemovalEvent=?");
			query.setLong(0, idRmvlEvent);
			RmvlChcklstLink rmvlChcklstLink = (RmvlChcklstLink) query.uniqueResult();
			return rmvlChcklstLink != null;
		}
		sessionFactory.getCurrentSession().createCriteria(CnsrvtrshpRemoval.class)
				.add(Restrictions.eq("idRmvlGroup", cnsrvtrshpRemovalDto.getIdRmvlGroup()))
				.setProjection(
						Projections.projectionList().add(Projections.property("idRemovalEvent"), "idRemovalEvent"))
				.setResultTransformer(Transformers.aliasToBean(CnsrvtrshpRemovalDto.class));
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see us.tx.state.dfps.service.checklistmanagement.dao.RmvlCheckListDao#
	 * getPersonList(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CnsrvtrshpRemovalDto> getPersonList(Long idRmvlEvent) {
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CnsrvtrshpRemoval.class)
				.createAlias("person", "person").add(Restrictions.eq("idRemovalEvent", idRmvlEvent))
				.setProjection(Projections.projectionList().add(Projections.property("idRmvlGroup"), "idRmvlGroup")
						.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
						.add(Projections.property("indRemovalNaCare"), "indRemovalNaCare")
						.add(Projections.property("indRemovalNaChild"), "indRemovalNaChild")
						.add(Projections.property("nbrRemovalAgeMo"), "removalAgeMo")
						.add(Projections.property("nbrRemovalAgeYr"), "removalAgeYr")
						.add(Projections.property("idRemovalEvent"), "idRemovalEvent")
						.add(Projections.property("idVictim"), "idVictim")
						.add(Projections.property("dtRemoval"), "dtRemoval")
						.add(Projections.property("person.idPerson"), "idPerson")
						.add(Projections.property("person.nmPersonFull"), "nmPersonFull"))
				.setResultTransformer(Transformers.aliasToBean(CnsrvtrshpRemovalDto.class));
		CnsrvtrshpRemovalDto cnsrvtrshpRemovalDto = (CnsrvtrshpRemovalDto) cr.uniqueResult();
		if (null == cnsrvtrshpRemovalDto.getIdRmvlGroup()) {
			List<CnsrvtrshpRemovalDto> result = new ArrayList<>();
			result.add(cnsrvtrshpRemovalDto);
			return result;
		}
		Criteria cr2 = sessionFactory.getCurrentSession().createCriteria(CnsrvtrshpRemoval.class)
				.createAlias("person", "person")
				.add(Restrictions.eq("idRmvlGroup", cnsrvtrshpRemovalDto.getIdRmvlGroup()))
				.setProjection(Projections.projectionList().add(Projections.property("idRmvlGroup"), "idRmvlGroup")
						.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
						.add(Projections.property("indRemovalNaCare"), "indRemovalNaCare")
						.add(Projections.property("indRemovalNaChild"), "indRemovalNaChild")
						.add(Projections.property("nbrRemovalAgeMo"), "removalAgeMo")
						.add(Projections.property("nbrRemovalAgeYr"), "removalAgeYr")
						.add(Projections.property("idRemovalEvent"), "idRemovalEvent")
						.add(Projections.property("person.idPerson"), "idPerson")
						.add(Projections.property("idVictim"), "idVictim")
						.add(Projections.property("dtRemoval"), "dtRemoval")
						.add(Projections.property("person.nmPersonFull"), "nmPersonFull"))
				.setResultTransformer(Transformers.aliasToBean(CnsrvtrshpRemovalDto.class));
		List<CnsrvtrshpRemovalDto> result = cr2.list();
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see us.tx.state.dfps.service.checklistmanagement.dao.RmvlCheckListDao#
	 * saveRmvlChcklstRspn(us.tx.state.dfps.rmvlchecklist.dto.
	 * RmvlChcklstRspnDto)
	 */
	@Override
	public List<Long> saveRmvlChcklstRspn(List<RmvlChcklstRspnDto> rspns) {
		List<Long> result = new ArrayList<>();
		for (RmvlChcklstRspnDto checklstRspn : rspns) {
			RmvlChcklstRspn rmvlChcklstRspn = new RmvlChcklstRspn();
			rmvlChcklstRspn.setIdCreatedPerson(checklstRspn.getIdCreatedPerson());
			rmvlChcklstRspn.setIdPerson(checklstRspn.getIdPerson());
			rmvlChcklstRspn.setIdRmvlChcklstTaskGroup(checklstRspn.getIdRmvlChcklstTaskGroup());
			rmvlChcklstRspn.setIndTaskCmpltd(checklstRspn.getIndTaskCmpltd());
			rmvlChcklstRspn.setTxtRspns(checklstRspn.getRspns());
			rmvlChcklstRspn.setIdLastUpdatePerson(checklstRspn.getIdLastUpdatePerson());
			rmvlChcklstRspn.setDtPurge(checklstRspn.getDtPurge());

			RmvlChcklstTaskLookup rmvlChcklstTaskLookup = (RmvlChcklstTaskLookup) sessionFactory.getCurrentSession()
					.get(RmvlChcklstTaskLookup.class, checklstRspn.getIdRmvlChcklstTaskLookup());
			rmvlChcklstRspn.setRmvlChcklstTaskLookup(rmvlChcklstTaskLookup);

			rmvlChcklstRspn.setIdRmvlChcklstLink(checklstRspn.getIdRmvlChcklstLink());

			Long temp = (Long) sessionFactory.getCurrentSession().save(rmvlChcklstRspn);
			result.add(temp);
			sessionFactory.getCurrentSession().flush();
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see us.tx.state.dfps.service.checklistmanagement.dao.RmvlCheckListDao#
	 * updateRmvlChcklstRspn(us.tx.state.dfps.rmvlchecklist.dto.
	 * RmvlChcklstRspnDto)
	 */
	@Override
	public String updateRmvlChcklstRspn(List<RmvlChcklstRspnDto> rspns) {
		for (RmvlChcklstRspnDto checklstRspn : rspns) {
			RmvlChcklstRspn rmvlChcklstRspn = (RmvlChcklstRspn) sessionFactory.getCurrentSession()
					.get(RmvlChcklstRspn.class, checklstRspn.getIdRmvlChcklstRspns());
			rmvlChcklstRspn.setIdRmvlChcklstRspns(checklstRspn.getIdRmvlChcklstRspns());
			rmvlChcklstRspn.setIdCreatedPerson(checklstRspn.getIdCreatedPerson());
			rmvlChcklstRspn.setIdPerson(checklstRspn.getIdPerson());
			rmvlChcklstRspn.setIdRmvlChcklstTaskGroup(checklstRspn.getIdRmvlChcklstTaskGroup());
			rmvlChcklstRspn.setIndTaskCmpltd(checklstRspn.getIndTaskCmpltd());
			rmvlChcklstRspn.setTxtRspns(checklstRspn.getRspns());
			rmvlChcklstRspn.setIdLastUpdatePerson(checklstRspn.getIdLastUpdatePerson());
			rmvlChcklstRspn.setDtPurge(checklstRspn.getDtPurge());
			RmvlChcklstTaskLookup rmvlChcklstTaskLookup = (RmvlChcklstTaskLookup) sessionFactory.getCurrentSession()
					.get(RmvlChcklstTaskLookup.class, checklstRspn.getIdRmvlChcklstTaskLookup());
			rmvlChcklstRspn.setRmvlChcklstTaskLookup(rmvlChcklstTaskLookup);
			sessionFactory.getCurrentSession().update(rmvlChcklstRspn);
			sessionFactory.getCurrentSession().flush();
		}
		return ServiceConstants.SUCCESS;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see us.tx.state.dfps.service.checklistmanagement.dao.RmvlCheckListDao#
	 * getRmvlChcklstRspn(java.lang.Long)
	 */
	@Override
	public List<RmvlChcklstRspnDto> getRmvlChcklstRspn(Long idPerson) {
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(RmvlChcklstRspn.class, "rspn")
				.add(Restrictions.eq("rspn.idPerson", idPerson))
				.setProjection(Projections.projectionList()
						.add(Projections.property("rspn.idRmvlChcklstRspns"), "idRmvlChcklstRspns")
						.add(Projections.property("rspn.dtCreated"), "dtCreated")
						.add(Projections.property("rspn.rmvlChcklstTaskLookup.idRmvlChcklstTaskLookup"),
								"idRmvlChcklstTaskLookup")
						.add(Projections.property("rspn.dtLastUpdate"), "dtLastUpdate")
						.add(Projections.property("rspn.dtPurge"), "dtPurge")
						.add(Projections.property("rspn.idCreatedPerson"), "idCreatedPerson")
						.add(Projections.property("rspn.idLastUpdatePerson"), "idLastUpdatePerson")
						.add(Projections.property("rspn.idPerson"), "idPerson")
						.add(Projections.property("rspn.idRmvlChcklstTaskGroup"), "idRmvlChcklstTaskGroup")
						.add(Projections.property("rspn.txtRspns"), "rspns")
						.add(Projections.property("rspn.indTaskCmpltd"), "indTaskCmpltd"))
				.setResultTransformer(Transformers.aliasToBean(RmvlChcklstRspnDto.class));
		return cr.list();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see us.tx.state.dfps.service.checklistmanagement.dao.RmvlCheckListDao#
	 * saveRmvlChcklstLink(us.tx.state.dfps.rmvlchecklist.dto.
	 * RmvlChcklstLinkDto)
	 */
	@Override
	public Map<Long,Long> saveRmvlChcklstLink(List<RmvlChcklstLinkDto> links) {
		Map<Long,Long> idLinkPersonMap = new HashMap<>();
		for (RmvlChcklstLinkDto rmvlCheckListLinkDto : links) {
			RmvlChcklstLink link = new RmvlChcklstLink();
			link.setDtCreated(rmvlCheckListLinkDto.getDtCreated());
			link.setIdLastUpdatePerson(rmvlCheckListLinkDto.getIdLastUpdatePerson());
			Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class,
					rmvlCheckListLinkDto.getIdStage());
			link.setStage(stage);
			link.setIdCreatedPerson(rmvlCheckListLinkDto.getIdCreatedPerson());
			link.setTxtChildList(rmvlCheckListLinkDto.getTxtChildList());
			Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
					rmvlCheckListLinkDto.getIdPerson());
			link.setPerson(person);
			CnsrvtrshpRemoval cnsrvtrshpRemoval = (CnsrvtrshpRemoval) sessionFactory.getCurrentSession()
					.get(CnsrvtrshpRemoval.class, rmvlCheckListLinkDto.getIdRmvlEvent());
			link.setCnsrvtrshpRemoval(cnsrvtrshpRemoval);
			RmvlChcklstLookup rmvlCheckList = (RmvlChcklstLookup) sessionFactory.getCurrentSession()
					.get(RmvlChcklstLookup.class, rmvlCheckListLinkDto.getIdRmvlChcklstLookup());
			link.setRmvlChcklstLookup(rmvlCheckList);
			Long temp = (Long) sessionFactory.getCurrentSession().save(link);
			idLinkPersonMap.put(rmvlCheckListLinkDto.getIdPerson(), temp);
			sessionFactory.getCurrentSession().flush();
		}
		return idLinkPersonMap;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see us.tx.state.dfps.service.checklistmanagement.dao.RmvlCheckListDao#
	 * updateRmvlChcklstLink(us.tx.state.dfps.rmvlchecklist.dto.
	 * RmvlChcklstLinkDto)
	 */
	@Override
	public String updateRmvlChcklstLink(List<RmvlChcklstLinkDto> links) {
		for (RmvlChcklstLinkDto rmvlCheckListLinkDto : links) {
			RmvlChcklstLink link = (RmvlChcklstLink) sessionFactory.getCurrentSession().get(RmvlChcklstLink.class,
					rmvlCheckListLinkDto.getIdRmvlChcklstLink());
			link.setIdLastUpdatePerson(rmvlCheckListLinkDto.getIdLastUpdatePerson());
			Stage stage = (Stage) sessionFactory.getCurrentSession().get(CnsrvtrshpRemoval.class,
					rmvlCheckListLinkDto.getIdStage());
			link.setStage(stage);
			link.setIdCreatedPerson(rmvlCheckListLinkDto.getIdCreatedPerson());
			link.setTxtChildList(rmvlCheckListLinkDto.getTxtChildList());
			Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
					rmvlCheckListLinkDto.getIdPerson());
			link.setPerson(person);
			CnsrvtrshpRemoval cnsrvtrshpRemoval = (CnsrvtrshpRemoval) sessionFactory.getCurrentSession()
					.get(CnsrvtrshpRemoval.class, rmvlCheckListLinkDto.getIdRmvlEvent());
			link.setCnsrvtrshpRemoval(cnsrvtrshpRemoval);
			RmvlChcklstLookup rmvlCheckList = (RmvlChcklstLookup) sessionFactory.getCurrentSession()
					.get(RmvlChcklstLookup.class, rmvlCheckListLinkDto.getIdRmvlChcklstLookup());
			link.setRmvlChcklstLookup(rmvlCheckList);
			sessionFactory.getCurrentSession().update(link);
			sessionFactory.getCurrentSession().flush();
		}
		return ServiceConstants.SUCCESS;
	}
}
