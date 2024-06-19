import org.artop.aal.common.resource.Autosar40ResourceFactoryImpl;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import autosar40.util.Autosar40ReleaseDescriptor;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ARXMLParser {

    private static Connection connection;

    public static void main(String[] args) {
        // 예제 IFile 객체 생성 (실제 프로젝트에서는 IFile 객체를 받아서 사용)
        IFile arxmlFile = getIFile("/path/to/your/file.arxml"); // 또는 "/path/to/your/file.bmd"

        try {
            // SQLite 데이터베이스 연결
            connection = DriverManager.getConnection("jdbc:sqlite:arxml_data.db");
            createTable();

            // 파일 경로 얻기
            String filePath = getFilePath(arxmlFile);

            // 루트 객체 로드
            AUTOSAR autosarRoot = getAutosarRoot(arxmlFile);

            if (autosarRoot != null) {
                // 데이터 파싱 및 생성
                List<ParsedData> parsedDataList = parseTree(autosarRoot, "AUTOSAR", null);

                // 데이터베이스에 데이터 삽입
                insertParsedDataToDB(parsedDataList);

                // 데이터베이스에서 트리 구조 로드
                TreeNode rootNode = loadTreeFromDatabase();

                // JXTreeTable 모델 생성
                ARXMLTreeTableModel treeTableModel = new ARXMLTreeTableModel(rootNode);

                // JXTreeTable 생성 및 설정
                JXTreeTable treeTable = new JXTreeTable(treeTableModel);
                treeTable.setRootVisible(true);

                // 스크롤 팬에 추가하여 프레임에 표시
                JFrame frame = new JFrame("ARXML Tree Table");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(new JScrollPane(treeTable));
                frame.setSize(800, 600);
                frame.setVisible(true);
            } else {
                System.out.println("Failed to load ARXML/BMD file.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static IFile getIFile(String filePath) {
        // IFile 객체 생성 로직
        // 실제 프로젝트에서는 IWorkspaceRoot와 같은 Eclipse 리소스 API를 사용하여 IFile 객체를 얻습니다.
        // 이 예제에서는 단순히 null을 반환합니다.
        return null; // 실제 IFile 객체로 교체
    }

    private static String getFilePath(IFile file) {
        if (file == null) {
            return null;
        }

        IPath location = file.getLocation();
        return location != null ? location.toOSString() : null;
    }

    private static void createTable() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS arxml_data ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "parent_id INTEGER, "
                + "name TEXT, "
                + "path TEXT, "
                + "xml_path TEXT, "
                + "key TEXT, "
                + "value TEXT, "
                + "FOREIGN KEY (parent_id) REFERENCES arxml_data (id)"
                + ")";
        try (PreparedStatement stmt = connection.prepareStatement(createTableSQL)) {
            stmt.execute();
        }
    }

    private static AUTOSAR getAutosarRoot(IFile file) {
        Resource resource = EcorePlatformUtil.getResource(file);
        if (resource != null && !resource.getContents().isEmpty()) {
            return resource.getContents().stream()
                    .filter(object -> object instanceof AUTOSAR)
                    .map(object -> (AUTOSAR) object)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    private static List<ParsedData> parseTree(EObject element, String path, Integer parentId) {
        List<ParsedData> parsedDataList = new ArrayList<>();

        String elementName = element.eClass().getName();
        String shortName = element.eClass().getEStructuralFeature("shortName") != null
                ? (String) element.eGet(element.eClass().getEStructuralFeature("shortName"))
                : elementName;
        String newPath = path + "/" + elementName + "[" + shortName + "]";
        parsedDataList.add(new ParsedData(null, shortName, newPath, path, null, null, parentId));

        // Add attributes
        for (EAttribute attribute : element.eClass().getEAllAttributes()) {
            Object value = element.eGet(attribute);
            String attributeName = attribute.getName();
            parsedDataList.add(new ParsedData(null, attributeName, newPath + "/" + attributeName, newPath, attributeName, value != null ? value.toString() : null, null));
        }

        // Parse references
        for (EReference reference : element.eClass().getEAllReferences()) {
            Object refValue = element.eGet(reference);
            if (refValue instanceof List) {
                List<?> refList = (List<?>) refValue;
                for (Object refItem : refList) {
                    if (refItem instanceof EObject) {
                        parsedDataList.addAll(parseTree((EObject) refItem, newPath, null));
                    }
                }
            } else if (refValue instanceof EObject) {
                parsedDataList.addAll(parseTree((EObject) refValue, newPath, null));
            }
        }

        // Parse containment references
        for (EReference containment : element.eClass().getEAllContainments()) {
            Object contValue = element.eGet(containment);
            if (contValue instanceof List) {
                List<?> contList = (List<?>) contValue;
                for (Object contItem : contList) {
                    if (contItem instanceof EObject) {
                        parsedDataList.addAll(parseTree((EObject) contItem, newPath, null));
                    }
                }
            } else if (contValue instanceof EObject) {
                parsedDataList.addAll(parseTree((EObject) contValue, newPath, null));
            }
        }

        return parsedDataList;
    }

    private static void insertParsedDataToDB(List<ParsedData> parsedDataList) throws SQLException {
        String insertSQL = "INSERT INTO arxml_data (name, path, xml_path, key, value, parent_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
            for (ParsedData data : parsedDataList) {
                stmt.setString(1, data.getName());
                stmt.setString(2, data.getPath());
                stmt.setString(3, data.getXmlPath());
                stmt.setString(4, data.getKey());
                stmt.setString(5, data.getValue());
                stmt.setObject(6, data.getParentId());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    private static TreeNode loadTreeFromDatabase() throws SQLException {
        String query = "SELECT id, parent_id, name, xml_path, key, value FROM arxml_data ORDER BY xml_path";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            List<TreeNode> nodes = new ArrayList<>();
            while (rs.next()) {
                TreeNode node = new TreeNode(
                    rs.getString("name"),
                    rs.getString("xml_path"),
                    rs.getString("key"),
                    rs.getString("value")
                );
                node.setId(rs.getInt("id"));
                node.setParentId(rs.getObject("parent_id") != null ? rs.getInt("parent_id") : null);
                nodes.add(node);
            }

            return buildTree(nodes);
        }
    }

    private static TreeNode buildTree(List<TreeNode> nodes) {
        TreeNode root = null;
        for (TreeNode node : nodes) {
            if (node.getParentId() == null) {
                root = node;
                break;
            }
        }
        if (root == null) {
            throw new IllegalStateException("Root node not found");
        }

        addChildren(root, nodes);
        return root;
    }

    private static void addChildren(TreeNode parent, List<TreeNode> nodes) {
        for (TreeNode node : nodes) {
            if (parent.getId().equals(node.getParentId())) {
                parent.addChild(node);
                addChildren(node, nodes);
            }
        }
    }

    static class TreeNode {
        private Integer id;
        private Integer parentId;
        private String name;
        private String path;
        private String xmlPath;
        private String key;
        private String value;
        private List<TreeNode> children = new ArrayList<>();

        TreeNode(String name, String xmlPath) {
            this.name = name;
            this.xmlPath = xmlPath;
        }

        TreeNode(String name, String xmlPath, String key, String value) {
            this.name = name;
            this.xmlPath = xmlPath;
            this.key = key;
            this.value = value;
        }

        void addChild(TreeNode child) {
            children.add(child);
        }

        List<TreeNode> getChildren() {
            return children;
        }

        Integer getId() {
            return id;
        }

        void setId(Integer id) {
            this.id = id;
        }

        Integer getParentId() {
            return parentId;
        }

        void setParentId(Integer parentId) {
            this.parentId = parentId;
        }

        String getName() {
            return name;
        }

        String getPath() {
            return path;
        }

        String getXmlPath() {
            return xmlPath;
        }

        String getKey() {
            return key;
        }

        String getValue() {
            return value;
        }

        void setName(String name) {
            this.name = name;
        }

        void setPath(String path) {
            this.path = path;
        }

        void setXmlPath(String xmlPath) {
            this.xmlPath = xmlPath;
        }

        void setKey(String key) {
            this.key = key;
        }

        void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    static class ParsedData {
        private Integer id;
        private String name;
        private String path;
        private String xmlPath;
        private String key;
        private String value;
        private Integer parentId;

        ParsedData(Integer id, String name, String path, String xmlPath, String key, String value, Integer parentId) {
            this.id = id;
            this.name = name;
            this.path = path;
            this.xmlPath = xmlPath;
            this.key = key;
            this.value = value;
            this.parentId = parentId;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getXmlPath() {
            return xmlPath;
        }

        public void setXmlPath(String xmlPath) {
            this.xmlPath = xmlPath;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Integer getParentId() {
            return parentId;
        }

        public void setParentId(Integer parentId) {
            this.parentId = parentId;
        }
    }

    static class ARXMLTreeTableModel extends AbstractTreeTableModel {

        private final TreeNode root;

        ARXMLTreeTableModel(TreeNode root) {
            super(root);
            this.root = root;
        }

        @Override
        public int getColumnCount() {
            return 4; // Name, Path, Key, Value
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return "Name";
                case 1:
                    return "XML Path";
                case 2:
                    return "Key";
                case 3:
                    return "Value";
                default:
                    return "";
            }
        }

        @Override
        public Object getChild(Object parent, int index) {
            return ((TreeNode) parent).getChildren().get(index);
        }

        @Override
        public int getChildCount(Object parent) {
            return ((TreeNode) parent).getChildren().size();
        }

        @Override
        public int getIndexOfChild(Object parent, Object child) {
            return ((TreeNode) parent).getChildren().indexOf(child);
        }

        @Override
        public Object getValueAt(Object node, int column) {
            TreeNode treeNode = (TreeNode) node;
            switch (column) {
                case 0:
                    return treeNode.getName();
                case 1:
                    return treeNode.getXmlPath();
                case 2:
                    return treeNode.getKey();
                case 3:
                    return treeNode.getValue();
                default:
                    return "";
            }
        }

        @Override
        public boolean isLeaf(Object node) {
            return ((TreeNode) node).getChildren().isEmpty();
        }
    }
}
