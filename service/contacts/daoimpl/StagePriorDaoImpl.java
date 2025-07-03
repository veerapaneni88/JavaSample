package us.tx.state.dfps.service.contacts.daoimpl;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.contacts.dao.StagePriorDao;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.xmlstructs.inputstructs.StageUpdateDto;

@Repository
public class StagePriorDaoImpl implements StagePriorDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${Cinvc5dDaoImpl.updateStage}")
	private String updateStageSql;

	/**
	 * 
	 * Method Name: updateStage Method Description:this method update stage
	 * table
	 * 
	 * @param stageUpdateDto
	 * @return long @
	 */
	@Override
	public long updateStage(StageUpdateDto stageUpdateDto) {
		long rowCountOne = ServiceConstants.Zero;
		if (ServiceConstants.REQ_FUNC_CD_UPDATE.equals(stageUpdateDto.getArchInputStructDto().getCreqFuncCd())) {
			Query updateDetails = sessionFactory.getCurrentSession().createSQLQuery(updateStageSql);
			updateDetails.setParameter("idStage", stageUpdateDto.getUlIdStage());
			updateDetails.setParameter("dtStageClose", stageUpdateDto.getDtDtStageClose());
			rowCountOne = updateDetails.executeUpdate();

			if (rowCountOne == ServiceConstants.Zero) {
				throw new DataNotFoundException(ServiceConstants.SQL_NOT_FOUND);
			}
		} else {
			throw new DataNotFoundException(ServiceConstants.ARC_ERR_BAD_FUNC_CD);
		}
		return rowCountOne;
	}
}
