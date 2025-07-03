package us.tx.state.dfps.service.approval.serviceimpl;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.tx.state.dfps.service.approval.service.GroupBeanService;
import us.tx.state.dfps.service.forms.dao.BookmarkDao;
import us.tx.state.dfps.service.forms.dto.FormGroupsDto;
import us.tx.state.dfps.service.webservices.gold.dto.GoldNarrativeDto;
import us.tx.state.dfps.service.webservices.gold.dto.GroupBean;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional
public class GroupBeanServiceImpl implements GroupBeanService {

    public static final Long DEFAULT_GROUP = 2L;
    private static final int PREFIX_LENGTH = 7;
    @Autowired
    BookmarkDao bookmarkDao;
    private String bookmark;
    private Long groupID;
    private List<String> fields;
    private String formName;
    private String title;

    /**
     * Method helps to load the data from back end and set the values and finally returns the XML data
     *
     * @param dto - dto data from Controller
     * @param group - group id
     * @param depth - depth
     * @return - return XML data
     */
    @Override
    public StringBuilder load(GoldNarrativeDto dto, Long group, int depth) {

        groupID = group;
        formName = dto.getDocumentName();
        title = dto.getTitle();
        fields = new ArrayList<>();

        loadRootFields();
        return this.buildFinalString();
    }

    /**
     * Method helps to load the bookmark data from db and set to GroupBean
     *
     * @param bean groupbean
     * @return return groupbean with bookmark data
     */
    private GroupBean loadFields(GroupBean bean) {
        this.fields = new ArrayList<>();

        List<String> fieldSelectList = bookmarkDao.getNmBookMark(bean.getFormName());
        for (String str : fieldSelectList) {
            String fieldBk = str;
            if (str.endsWith("_YES")) {
                fieldBk = str.substring(0, str.length() - 4);
            } else if (str.endsWith("_NO")) {
                fieldBk = str.substring(0, str.length() - 3);
            }
            if (this.fields.contains(fieldBk))
                continue;
            this.fields.add(fieldBk);

        }
        bean.setFields(this.fields);
        return bean;
    }

    /**
     * Method helps to render the subgroups from DB and adding to GroupBean
     *
     * @param bean - request data
     * @return return bean with subgroups fields data
     */
    private GroupBean loadSubGroups(GroupBean bean) {
        List<FormGroupsDto> subGroupList = bookmarkDao.getFormGoupsJoinGroupLink(bean.getGroupID());
        List<String> subFields = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(subGroupList)) {
            for (FormGroupsDto dto : subGroupList) {
                GroupBean groupBean = new GroupBean();
                groupBean.setGroupID(dto.getIdGroup());
                groupBean.setFormName(dto.getNmGroup());
                loadFields(groupBean);
                subFields.addAll(groupBean.getFields());
            }
            bean.setFields(subFields);
        }
        return bean;
    }

    /**
     * Method helps to render the single element values from db and added to list
     */
    private void loadRootFields() {
        List<String> rootFiledSelectList = bookmarkDao.getNmBookMarkFromFormFields(this.formName);
        for (String str : rootFiledSelectList) {
            this.fields.add(str);
        }
    }

    /**
     * Method helps to build final XML as string builder with db elements
     *
     * @return returns db elements as XML string builder
     */
    public StringBuilder buildFinalString() {
        StringBuilder xmlStringOutput = new StringBuilder("\n");
        String prepender = "";
        AtomicInteger childCtr = new AtomicInteger();

        xmlStringOutput.append(prepender + "<" + this.title + ">");
        StringBuilder finalMe = xmlStringOutput;
        fields.stream().forEach(rootElement -> {
            finalMe.append("\n" + prepender + "<" + rootElement + "></" + rootElement + ">");
            childCtr.getAndIncrement();
        });

        List<FormGroupsDto> formGroupsDto = null;
        if (DEFAULT_GROUP.equals(groupID)) {
            formGroupsDto = bookmarkDao.getFormGroupsByNmForm(this.formName);
            this.bookmark = this.title;
        }
        if (CollectionUtils.isNotEmpty(formGroupsDto)) {
            formGroupsDto.stream().forEach(dto -> {
                String element = dto.getNmGroupBk().substring(PREFIX_LENGTH);
                finalMe.append("\n" + prepender + "<" + element + ">");
                GroupBean bean = new GroupBean();
                bean.setFormName(dto.getNmGroup());
                bean.setBookmark(dto.getNmGroupBk().substring(PREFIX_LENGTH));
                bean.setGroupID(dto.getIdGroup());

                GroupBean subGroupBean = loadSubGroups(bean);
                List<String> subGroupFields = subGroupBean.getFields();
                buildXmlWithFields(prepender, childCtr, finalMe, subGroupFields);
                GroupBean fieldBean = loadFields(bean);
                List<String> fieldsList = fieldBean.getFields();
                buildXmlWithFields(prepender, childCtr, finalMe, fieldsList);

                finalMe.append("\n" + prepender + "</" + element + ">");
            });
        }


        if (childCtr.get() > 0)
            xmlStringOutput.append("\n" + prepender);
        xmlStringOutput = finalMe;
        xmlStringOutput.append("</" + this.bookmark + ">");
        return xmlStringOutput;
    }

    /**
     * Method helps add the subgroup fields and initial group field values to xml
     *
     * @param prepender      empty string
     * @param childCtr       counter
     * @param finalMe        String builder
     * @param subGroupFields list of db elements
     */
    private void buildXmlWithFields(String prepender, AtomicInteger childCtr, StringBuilder finalMe, List<String> subGroupFields) {
        if (CollectionUtils.isNotEmpty(subGroupFields)) {
            for (String field : subGroupFields) {
                finalMe.append("\n" + prepender + "<" + field + "></" + field + ">");
                childCtr.getAndIncrement();
            }
        }
    }


}
