package us.tx.state.dfps.service.approval.serviceimpl;

import org.apache.xpath.CachedXPathAPI;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import us.tx.state.dfps.phoneticsearch.exception.RuntimeWrappedException;
import us.tx.state.dfps.service.approval.service.GoldWebService;
import us.tx.state.dfps.service.approval.service.GroupBeanService;
import us.tx.state.dfps.service.apscontactlognarrative.dao.ApsContactLogNarrativeDao;
import us.tx.state.dfps.service.common.utils.Base64;
import us.tx.state.dfps.service.webservices.gold.dto.GoldCommunicationDto;
import us.tx.state.dfps.service.webservices.gold.dto.GoldNarrativeDto;
import us.tx.state.dfps.service.webservices.gold.dto.XMLTranslationUtil;
import us.tx.state.dfps.service.workload.dao.WebsvcFormTransDao;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.sql.SQLException;
import java.util.*;

@Service
public class GoldWebServiceImpl implements GoldWebService {

    public static final String VALUE = "Value";
    private static final String GROUP_NAME_PREFIX = "TMPLAT_";
    private static final String FORM_DATA_GROUP_XPATH = "//preFillData/formDataGroup";
    private static final String SINGLETON_XPATH = "//preFillData/bookmark";
    private static final String COMM_ID_TAG_NAME = "COMMUNICATION_ID";
    private static final String[] TAGS = {"span", "P", "SPAN", "BLOCKQUOTE", "U", "UL", "OL", "LI", "EM", "STRONG", "FONT"};
    private static final Logger log = Logger.getLogger(GoldWebServiceImpl.class);
    int commID;
    Map<String, List<Node>> formGroupMap;
    Map<String, String> singleElementsMap;
    Map<String, Node> ueMap;
    @Autowired
    WebsvcFormTransDao websvcFormTransDao;
    @Autowired
    ApsContactLogNarrativeDao apsContactLogNarrativeDao;
    @Autowired
    GroupBeanService groupBeanService;
    @Autowired
    DADSWebServiceClient dadsWebServiceClient;

    @Override
    public Long insertFormSendData(Long eventId) {

        return websvcFormTransDao.insertFormSendData(eventId);
    }

    @Override
    public boolean updateFormSendData(GoldCommunicationDto goldCommunicationDto) {

        return websvcFormTransDao.updateFormSendData(goldCommunicationDto);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public GoldCommunicationDto sendAndSave(Long idEvent) throws SQLException {
        log.info("Entering into GoldWebServiceImpl.sendAndSave method");
        GoldNarrativeDto dto = getNarrativeData(idEvent);
        byte[] narrativeArray = convertNarrativeBlobToByteArray(dto);
        Document doc = XMLTranslationUtil.byteArrayToXMLDoc(narrativeArray);
        Long webTransId = insertFormSendData(idEvent);
        Document outputXML = null;
        String outputXMLString = null;
        outputXML = translateXML(doc, dto, webTransId);
        outputXMLString = XMLTranslationUtil.xmlDocToString(outputXML);

        GoldCommunicationDto goldCommunicationDto = dadsWebServiceClient.sendForm(outputXMLString, webTransId);
        goldCommunicationDto.setIdComm(Integer.parseInt(webTransId.toString()));
        updateFormSendData(goldCommunicationDto);
        log.info("Exiting into GoldWebServiceImpl.sendAndSave method");
        return goldCommunicationDto;
    }

    /**
     * Method helps to call the DAO and get the contract narrative data
     *
     * @param idEvent - selected id event
     */
    private GoldNarrativeDto getNarrativeData(Long idEvent) {

        return apsContactLogNarrativeDao.getGuardianShipReferralNarrativeByEventId(idEvent);
    }

    /**
     * Method helps to convert the narrative blob data to byte array
     *
     * @param dto - db return data
     * @return - return array byte
     */
    private byte[] convertNarrativeBlobToByteArray(GoldNarrativeDto dto) throws SQLException {
        byte[] narrativeArray;
        narrativeArray = null != dto.getNarrative() ? (dto.getNarrative().getBytes(1, (int) dto.getNarrative().length())) : new byte[0];
        return narrativeArray;
    }

    private Document translateXML(Document narrativeDoc, GoldNarrativeDto dto, Long webTransId) {
        log.info("Entering into GoldWebServiceImpl.translateXML method");
        Document xmlOutPut;

        CachedXPathAPI xpathAPI = new CachedXPathAPI();
        NodeList nodes;
        Map<String, List<Node>> fgMap = buildFormGroupMap(narrativeDoc);
        singleElementsMap = getSingleElementMap(narrativeDoc);
        try {
            // Create the new document with a root node named after the form
            xmlOutPut = getNewDocument();
            Element root = xmlOutPut.createElement(dto.getTitle());
            xmlOutPut.appendChild(root);

            //add the communication id
            Element commIDTag = xmlOutPut.createElement(COMM_ID_TAG_NAME);
            commIDTag.appendChild(xmlOutPut.createTextNode(String.valueOf(webTransId)));
            root.appendChild(commIDTag);

            nodes = xpathAPI.selectNodeList(getBoilerplateXML(dto), "/" + dto.getTitle() + "/*");

            for (int i = 0; i < nodes.getLength(); i++) {
                Node currentNode = nodes.item(i);
                String bpNodeName = currentNode.getNodeName();
                if (currentNode.hasChildNodes()) {
                    List<Node> counterparts = fgMap.get(GROUP_NAME_PREFIX + bpNodeName);

                    // formDataGroup may not exist in blob
                    if (null == counterparts) continue;
                    fillAndAddNodes(root, counterparts, currentNode);
                } else {
                    remapNode(root, currentNode);
                }
            }
            ueMap = buildUEMap(xmlOutPut);
            doUserEdits(xmlOutPut, dto);
            removeIDNodes(xmlOutPut);
            removeExtraNodes(xmlOutPut.getFirstChild());
        } catch (Exception ex) {
            throw new RuntimeWrappedException(ex);
        }
        log.info("Exiting into GoldWebServiceImpl.translateXML method");
        return xmlOutPut;
    }

    /**
     * Method helps to communicate with GroupBeanService and load the xml data from db
     *
     * @param dto - Narrative data
     * @return document
     */
    private Document getBoilerplateXML(GoldNarrativeDto dto) {

        StringBuilder boilerPlateXMLStr = groupBeanService.load(dto, 2L, 0);

        return XMLTranslationUtil.stringToXMLDoc(boilerPlateXMLStr.toString());
    }


    /**
     * Method helps to build the Form group data
     *
     * @param doc - Document
     * @return - Map
     */
    private Map<String, List<Node>> buildFormGroupMap(Document doc) {
        log.info("Entering into GoldWebServiceImpl.buildFormGroupMap method");
        Map<String, List<Node>> fgMap = new HashMap<>();
        CachedXPathAPI xpathAPI = new CachedXPathAPI();
        NodeList nodes;
        try {
            nodes = xpathAPI.selectNodeList(doc, FORM_DATA_GROUP_XPATH);
            for (int i = 0; i < nodes.getLength(); i++) {
                Node curr = nodes.item(i);
                String groupName = curr.getLastChild().getFirstChild().getNodeValue();

                if (fgMap.containsKey(groupName)) // add this node to its sibling
                {
                    fgMap.get(groupName).add(curr);
                } else
                // start a new list and add this node
                {
                    List<Node> list = new ArrayList<>();
                    list.add(curr);
                    fgMap.put(groupName, list);
                }
            }
        } catch (Exception ex) {
            throw new RuntimeWrappedException(ex);
        }
        log.info("Exiting into GoldWebServiceImpl.buildFormGroupMap method");
        return fgMap;
    }

    /**
     * Build new Document builder factory
     *
     * @return - return new Document
     * @throws ParserConfigurationException
     */
    private Document getNewDocument() throws ParserConfigurationException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        return docBuilder.newDocument();
    }


    /**
     * For the passed-in Document, clone a template node (happy XML) for each
     * fgNode (yucky XML), populate it with the bookmark data in the fgNode, and
     * attach it to the outputXML Document's root
     *
     * @param root     - node root data
     * @param fgNodes  - form gourp nodes data
     * @param template - node template
     * @return - return node
     * @throws javax.xml.transform.TransformerException
     */
    private Node fillAndAddNodes(Node root, List<Node> fgNodes, Node template) throws javax.xml.transform.TransformerException {
        log.info("Entering into GoldWebServiceImpl.fillAndAddNodes method");
        Iterator<Node> it = fgNodes.stream().iterator();
        CachedXPathAPI xpa = new CachedXPathAPI();

        while (it.hasNext()) {
            Node curr = it.next();
            Node target = template.cloneNode(true);
            if (null != target) {
                populateBookmarks(target, curr);
                populateFGChildren(target, curr, xpa);
                // attach target node to document
                Node imported = root.getOwnerDocument().importNode(target, true);
                root.appendChild(imported);
            }
        }
        log.info("Exiting into GoldWebServiceImpl.fillAndAddNodes method");
        return root;
    }

    /**
     * Method helps to loop the bookmark data and set the tag name and tag values into Node
     *
     * @param target - target node
     * @param fgNode form group node
     * @return - return target node
     */
    private Node populateBookmarks(Node target, Node fgNode) throws javax.xml.transform.TransformerException {
        CachedXPathAPI xpa = new CachedXPathAPI();
        NodeList bookmarks = xpa.selectNodeList(fgNode, "bookmark");


        // transfer bookmark data to target node
        for (int i = 0; i < bookmarks.getLength(); i++) {
            Node bookmark = bookmarks.item(i);
            String tagVal = getTextContent(bookmark.getFirstChild());
            String tagName = getTextContent(bookmark.getLastChild());

            // having to do this is SOOOO silly -- distill the
            // redundent yes/no tags down to one

            if (tagName.endsWith("_YES") || tagName.endsWith("_NO")) {
                int tagEndLength = 4;
                String state = "Yes";
                if (tagName.endsWith("_NO")) {
                    tagEndLength = 3;
                    state = "No";
                }

                tagName = tagName.substring(0, tagName.length() - tagEndLength);
                if ("checked".equals(tagVal)) {
                    tagVal = state;
                } else {
                    continue;
                }
            }

            // Collapse node where parent has on child with the same name
            updateTextContentCondition(target, xpa, tagVal, tagName);
        }

        return target;
    }

    /**
     * Method helps to remove the duplicate tag names and set the tag content to Node
     *
     * @param target  - target node
     * @param xpa     - Cached path read xml
     * @param tagVal  - string tag value
     * @param tagName - string tag name
     */
    private void updateTextContentCondition(Node target, CachedXPathAPI xpa, String tagVal, String tagName) throws TransformerException {
        if (target.getNodeName().equals(tagName)) {
            if (null != target.getFirstChild()) target.removeChild(target.getFirstChild());
            setTextContent(target, tagVal);
        } else {
            Node targetChild = xpa.selectSingleNode(target, tagName);
            if (null != targetChild) setTextContent(targetChild, tagVal);
        }
    }

    /**
     * Method helps to populate the Forms Data group data to node
     *
     * @param target - target node
     * @param fgNode - Form Group node
     * @param xpa    - xPath api for read xml
     * @return - return node with data
     * @throws javax.xml.transform.TransformerException
     */
    private Node populateFGChildren(Node target, Node fgNode, CachedXPathAPI xpa) throws javax.xml.transform.TransformerException {

        NodeList groups = xpa.selectNodeList(fgNode, "formDataGroup");

        for (int i = 0; i < groups.getLength(); i++) {
            Node curr = groups.item(i);
            Node moniker = curr.getLastChild(); // formDataGroupBookmark
            String groupName = getTextContent(moniker).replace(GROUP_NAME_PREFIX, "");
            Node targetChild = xpa.selectSingleNode(target, groupName);
            if (null != targetChild) populateBookmarks(targetChild, curr);

        }
        return target;
    }


    /**
     * this performs the job of Node.getTextContent(),
     * which is not available in Java 1.4
     *
     * @param el - Node element
     * @return - text content data
     */
    private String getTextContent(Node el) {
        Node child = el.getFirstChild();
        String textContent = "";
        if (child != null) textContent = child.getNodeValue();

        return textContent;
    }

    /**
     * set the text content to document
     *
     * @param el      - Node data
     * @param textVal - text value
     */
    private void setTextContent(Node el, String textVal) {
        Document doc = el.getOwnerDocument();

        if("ALLEGATIONS".equals(el.getNodeName())){
            el.appendChild(doc.createElement(el.getNodeName()));
            el.appendChild(doc.createTextNode(textVal));
        }else{
            el.appendChild(doc.createTextNode(textVal));
        }
    }

    /**
     * Find the matching bookmark for this template node, and set the node's text
     * content with the associated value
     *
     * @param root     - root node
     * @param template - template node
     * @return - node with data
     */
    private Node remapNode(Node root, Node template) {

        Node target = template.cloneNode(false);
        String val = singleElementsMap.get(target.getNodeName());
        Document rootDoc = root.getOwnerDocument();
        Document templateDoc = template.getOwnerDocument();
        target.appendChild(templateDoc.createTextNode(val));

        // attach target node to document
        Node imported = rootDoc.importNode(target, true);
        root.appendChild(imported);

        return root;
    }

    /**
     * Method helps to load the root elements' data like non-repeating single elements
     *
     * @param narrativeDoc - narrative data
     * @return return map
     */
    public Map<String, String> getSingleElementMap(Node narrativeDoc) {
            return buildSingleElementMap(narrativeDoc);
    }


    /**
     * * Build entries which are non-repeating single element
     * * bookmarks off the root, i.e., not part of a form group
     *
     * @param narrativeDoc - DB data
     * @return - map
     */
    private HashMap<String, String> buildSingleElementMap(Node narrativeDoc) {
        HashMap<String, String> sMap = new HashMap<>();
        CachedXPathAPI xpathAPI = new CachedXPathAPI();
        NodeList nodes;
        try {
            nodes = xpathAPI.selectNodeList(narrativeDoc, SINGLETON_XPATH);
            for (int i = 0; i < nodes.getLength(); i++) {
                Node curr = nodes.item(i);
                String value = getTextContent(curr.getFirstChild());
                String key = getTextContent(curr.getLastChild());
                sMap.put(key, value);
            }
        } catch (Exception ex) {
            throw new RuntimeWrappedException(ex);
        }

        return sMap;
    }


    /**
     * This method recursively removes empty nodes where parent has only one child
     * with the same name
     *
     * @param node - data node
     * @throws TransformerException
     */
    private void removeExtraNodes(Node node) throws TransformerException {

        CachedXPathAPI xpathAPI = new CachedXPathAPI();
        NodeList parents = xpathAPI.selectNodeList(node, "*");

        for (int j = 0; j < parents.getLength(); j++) {
            Node curr = parents.item(j);
            NodeList children = xpathAPI.selectNodeList(curr, "*");
            int len = children.getLength();
            if (1 == len) {
                Node child = children.item(0);
                curr.removeChild(child);
            } else {
                removeExtraNodes(curr);
            }
        }
    }

    /**
     * Method helps to remove the UE Group id data
     *
     * @param doc Document
     */
    private void removeIDNodes(Document doc) {
        CachedXPathAPI xpa = new CachedXPathAPI();
        try {
            NodeList nodes = xpa.selectNodeList(doc, "//UE_GROUPID");
            for (int j = 0; j < nodes.getLength(); j++) {
                Node curr = nodes.item(j);
                Node parent = curr.getParentNode();
                parent.removeChild(curr);
            }
        } catch (TransformerException tex) {
            // non-critical - just let it go
        }
    }

    /**
     * Method helps to get the UserEdit data from narrative data and adding into final Docuemnt
     *
     * @param doc - document
     * @param dto - db narrative data
     * @return processed document
     * @throws TransformerException
     */
    private Document doUserEdits(Document doc, GoldNarrativeDto dto) throws TransformerException, SQLException {
        CachedXPathAPI xpa = new CachedXPathAPI();
        byte[] narrativeArray = convertNarrativeBlobToByteArray(dto);
        Document narr = XMLTranslationUtil.byteArrayToXMLDoc(narrativeArray);
        NodeList userEdits = xpa.selectNodeList(narr, "//userEdit");
        Node root = doc.getDocumentElement();
        for (int i = 0; i < userEdits.getLength(); i++) {
            // grab the components of the userEdit node
            Node curr = userEdits.item(i);
            String name = getTextContent(curr.getFirstChild());
            String val = getTextContent(curr.getLastChild());


            String ueID = null;

            // decode the input text using Base64 decoder
            byte[] bytes = Base64.decode(val);
            val = new String(bytes).trim();

            // pasting into fields sometimes comes with a hazardous html, so remove it
            val = removeHTML(val);

            // look for an underscore, indicating the presence of a repeater group ID
            int split1 = name.indexOf('_', 2);
            if (split1 >= 0) {
                // look for a second occurence of the ID - this is unnecessary
                // and should be eliminated from the doc arch someday
                int split2 = name.indexOf('_', split1 + 2);
                ueID = getUeID(name, split1, split2);

                name = name.substring(0, split1);

                if (name.startsWith("repchk")) {
                    name = name.replace("repchk", "");
                    val = "Yes";
                } else if (name.startsWith("reptxt")) {
                    name = name.replace("reptxt", "");

                    // clean up email address
                    val = getMailToValue(val);
                } else if (name.startsWith("reprdo")) {
                    val = val.replace(name, "");
                    val = val.replace(VALUE, "");
                    val = val.substring(0, val.indexOf("_"));
                    name = name.replace("reprdo", "");
                }

                setUEMapToDoc(doc, name, val, ueID);
            } else if (name.startsWith("rdo")) {
                val = val.replace(name, "");
                val = val.replace(VALUE, "");
                name = name.replace("rdo", "");
                Element el = doc.createElement(name);
                root.appendChild(el);
                el.appendChild(doc.createTextNode(val));
            } else {
                // the rest were not repeaters, so just remap them
                name = name.replace("txt", "");

                // Parts of XML API don't like name to start with a number
                // if it starts with an alpha, parseInt will throw an exception
                //      so prepend an underscore to it
                name = checkForIntAndParse(name);

                val = val.replace("rdo", "");
                val = val.replace(VALUE, "");

                // clean up email address
                getMailToValue(val);

                // checkboxes
                if (val.startsWith("chk")) {
                    name = name.replace("chk", "");
                    val = "Yes";
                }

                Element el = doc.createElement(name);
                root.appendChild(el);
                el.appendChild(doc.createTextNode(val));
            }
        }

        return doc;
    }

    /**
     * Method helps to get the UE data nad set to document element
     *
     * @param doc  - Document data
     * @param name - Tag name
     * @param val  - tag value
     * @param ueID - ue id name
     */
    private void setUEMapToDoc(Document doc, String name, String val, String ueID) {
        // build the new element and insert it into the parent
        Node parent = ueMap.get(ueID);
        if (null != parent) {
            Element el = doc.createElement(name);
            parent.appendChild(el);
            el.appendChild(doc.createTextNode(val));
        }
    }

    /**
     * Method helps to check name contains number and update the name
     *
     * @param name
     * @return
     */
    private String checkForIntAndParse(String name) {
        boolean badName = true;
        try {
            Integer.parseInt(name.substring(0, 1));
        } catch (NumberFormatException nfex) {
            badName = false;  // so mark it "ok"
        }
        if (badName) name = "_" + name;
        return name;
    }

    /**
     * Method helps to remove the quotes
     *
     * @param val - string value to get the index and trim it
     * @return - return value with sub string data
     */
    private String getMailToValue(String val) {
        if (val.indexOf("mailto:") > -1) {
            int firstQuote = val.indexOf("\"") + 8;
            val = val.substring(firstQuote, val.indexOf("\"", firstQuote + 5));
        }
        return val;
    }

    /**
     * Method helps to get the UE id data
     *
     * @param name   - name of the ue id
     * @param split1 - split the ue id data start value
     * @param split2 -split the ue id data start value
     * @return return file ue id value
     */
    private String getUeID(String name, int split1, int split2) {
        String ueID;
        if (split2 >= 0) {
            ueID = name.substring(split1 + 1, split2);
        } else {
            ueID = name.substring(split1 + 1, name.length());
        }
        return ueID;
    }

    /**
     * Method helps to process the narrative document and extract the UE group id data
     *
     * @param outputXML narrative doc
     * @return return processed map
     * @throws TransformerException
     */
    private HashMap<String, Node> buildUEMap(Document outputXML) throws TransformerException {
        CachedXPathAPI xpa = new CachedXPathAPI();
        HashMap<String, Node> map = new HashMap<>();
        NodeList nodes = xpa.selectNodeList(outputXML, "//UE_GROUPID");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node curr = nodes.item(i);
            Node keyNode = null;
            if (null != curr)
                keyNode = curr.getFirstChild();
            if (null != keyNode) {
                String key = keyNode.getNodeValue();
                Node value = curr.getParentNode();
                map.put(key, value);
            }
        }
        return map;
    }


    /**
     * Pasting from a "web-aware" app to IE brings superfulous MS formatting
     * garbage which we don't want in the XM
     *
     * @param val - value
     * @return - return after remove HTML tags
     */
    private String removeHTML(String val) {
        if (!val.equals("")) {
            //start tags may have attributes, so determine the entire extent and remove it
            for (int i = 0; i < TAGS.length; i++) {
                int start;
                while ((start = val.indexOf("<" + TAGS[i])) > -1) {
                    int end = val.indexOf(">", start);
                    String tag = val.substring(start, end + 1);
                    val = val.replace(tag, "");
                }
            }

            //end tags are simpler
            for (int i = 0; i < TAGS.length; i++) {
                String tag = "</" + TAGS[i] + ">";
                while (val.indexOf(tag) > -1) {
                    val = val.replace(tag, "");
                }
            }
            //and the odd outlier...
            val = val.replace("&nbsp;", "");
        }
        return val;
    }
}
