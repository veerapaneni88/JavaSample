/**
 * 
 */
package us.tx.state.dfps.service.forms.daoimpl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.FormFields;
import us.tx.state.dfps.common.domain.FormGroups;
import us.tx.state.dfps.service.forms.dao.BookmarkDao;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormGroupsDto;
import us.tx.state.dfps.service.forms.dto.GroupBookmarkDto;

@Repository
public class BookmarkDaoImpl implements BookmarkDao {

	public static final String NM_FORM = "nmForm";
	public static final String NM_GROUP_BK = "nmGroupBk";
	@Autowired
	private SessionFactory sessionFactory;


	@Value("${BookmarkDaoImpl.getFormsGroupsByNmForm}")
	private String getFormGroupsByNmFormSql;

	@Value("${BookmarkDaoImpl.getSubLevelSelectForm}")
	private String getSubLevelSelectSql;

	@Value("${BookmarkDaoImpl.getFieldSelect}")
	private String getFieldSelectSql;

	@Value("${BookmarkDaoImpl.getRootFieldSelect}")
	private String getRootFieldSelectSql;
	@Override
	public List<BookmarkDto> selectFormFieldsBookmarks(String formName) {
		List<BookmarkDto> bookmarkList = null;

		bookmarkList = (List<BookmarkDto>) sessionFactory.getCurrentSession().createCriteria(FormFields.class)
				.setProjection(Projections.projectionList().add(Projections.property("nmBookmark"), "bookmarkName"))
				.add(Restrictions.eq(NM_FORM, formName))
				.setResultTransformer(Transformers.aliasToBean(BookmarkDto.class)).list();
		return bookmarkList;
	}

	@Override
	public List<GroupBookmarkDto> selectGroupBookmarks(String formName) {
		List<GroupBookmarkDto> bookmarkList = null;

		bookmarkList = (List<GroupBookmarkDto>) sessionFactory.getCurrentSession().createCriteria(FormGroups.class)
				.setProjection(Projections.projectionList().add(Projections.property(NM_GROUP_BK), "bookmarkName")
						.add(Projections.property("nmGroupHeaderBk"), "groupHeaderBookmarkName"))
				.add(Restrictions.eq(NM_FORM, formName))
				.setResultTransformer(Transformers.aliasToBean(GroupBookmarkDto.class)).list();
		return bookmarkList;
	}

	/**
	 * TOP_LEVEL_SELECT
	 *
	 * @param documentName
	 * @return
	 */
	@Override
	public List<FormGroupsDto> getFormGroupsByNmForm(String documentName) {
		SQLQuery sqlQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getFormGroupsByNmFormSql)
				.addScalar("nmGroup", StandardBasicTypes.STRING)
				.addScalar(NM_GROUP_BK, StandardBasicTypes.STRING)
				.addScalar("idGroup", StandardBasicTypes.LONG)
				.setParameter(NM_FORM, documentName)
				.setResultTransformer(Transformers.aliasToBean(FormGroupsDto.class)));

		return sqlQuery.list();
	}

	/**
	 * SUB_LEVEL_SELECT
	 *
	 * @param groupId
	 * @return
	 */
	@Override
	public List<FormGroupsDto> getFormGoupsJoinGroupLink(Long groupId) {
		SQLQuery sqlQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getSubLevelSelectSql)
				.addScalar("nmGroup", StandardBasicTypes.STRING)
				.addScalar(NM_GROUP_BK, StandardBasicTypes.STRING)
				.addScalar("idGroup", StandardBasicTypes.LONG)
				.setParameter("groupId", groupId)
				.setResultTransformer(Transformers.aliasToBean(FormGroupsDto.class)));
		return  sqlQuery.list();
	}

	/**
	 * FIELD_SELECT
	 *
	 * @param formName
	 * @return
	 */
	@Override
	public List<String> getNmBookMark(String formName) {
		SQLQuery sqlQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getFieldSelectSql)
				.addScalar("nmBookMark", StandardBasicTypes.STRING)
				.setParameter(NM_FORM, formName));
		return  sqlQuery.list();
	}

	/**
	 * ROOT_FIELD_SELECT
	 *
	 * @param formName
	 * @return
	 */
	@Override
	public List<String> getNmBookMarkFromFormFields(String formName) {
		SQLQuery sqlQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getRootFieldSelectSql)
				.addScalar("nmBookMark", StandardBasicTypes.STRING)
				.setParameter(NM_FORM, formName))
				;
		return sqlQuery.list();
	}

}
