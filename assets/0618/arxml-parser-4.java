import org.artop.aal.common.metamodel.AutosarReleaseDescriptor;
import org.artop.aal.common.resource.AutosarResourceFactoryImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import autosar40.util.Autosar40ReleaseDescriptor;
import autosar30.util.Autosar30ReleaseDescriptor;

import javax.swing.*;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ARXMLTreeTable {

    private static Connection connection;

    public static void main(String[] args) {
        try {
            // SQLite 데이터베이스 연결
            connection = DriverManager.getConnection("jdbc:sqlite:arxml_data.db");

            // ARXML 파일 경로 설정
            String arxmlFilePath = "/path/to/your/file.arxml";

            // 루트 객체 로드
            EObject root = getRoot(arxmlFilePath);

            if (root != null) {
                // 트리 구조 생성
                TreeNode rootNode = parseTree((EObject) root);

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
                System.out.println("Failed to load ARXML file.");
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

    private static EObject getRoot(String filePath) {
        // 리소스 팩토리 등록
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("arxml", new AutosarResourceFactoryImpl());

        // 리소스 세트 생성
        ResourceSet resourceSet = new ResourceSetImpl();

        // Autosar 패키지 초기화
        Autosar40Package.eINSTANCE.eClass();

        // ARXML 파일 로드
        Resource resource = resourceSet.getResource(URI.createFileURI(filePath), true);

        if (resource != null && resource.getContents() != null) {
            return resource.getContents().get(0);
        }
        return null;
    }

    private static TreeNode parseTree(EObject root) {
        TreeNode rootNode = new TreeNode("AUTOSAR");
        for (EObject pkg : ((EList<EObject>)root.eGet(root.eClass().getEStructuralFeature("arPackages")))) {
            TreeNode pkgNode = parsePackage(pkg);
            rootNode.addChild(pkgNode);
        }
        return rootNode;
    }

    private static TreeNode parsePackage(EObject pkg) {
        TreeNode pkgNode = new TreeNode((String) pkg.eGet(pkg.eClass().getEStructuralFeature("shortName")));
        for (EObject element : ((EList<EObject>)pkg.eGet(pkg.eClass().getEStructuralFeature("elements")))) {
            TreeNode elementNode = parseElement(element);
            pkgNode.addChild(elementNode);
        }
        return pkgNode;
    }

    private static TreeNode parseElement(EObject element) {
        String elementName = element.eClass().getName();
        TreeNode elementNode = new TreeNode(elementName);

        // Parse attributes
        for (EAttribute attribute : element.eClass().getEAllAttributes()) {
            Object value = element.eGet(attribute);
            String attributeName = attribute.getName();
            elementNode.addChild(new TreeNode(attributeName + " = " + value));
        }

        // Parse references
        for (EReference reference : element.eClass().getEAllReferences()) {
            Object refValue = element.eGet(reference);
            String referenceName = reference.getName();
            if (refValue instanceof List) {
                List<?> refList = (List<?>) refValue;
                for (Object refItem : refList) {
                    if (refItem instanceof EObject) {
                        TreeNode refNode = parseElement((EObject) refItem);
                        refNode.setName(referenceName + " -> " + refNode.getName());
                        elementNode.addChild(refNode);
                    }
                }
            } else if (refValue instanceof EObject) {
                TreeNode refNode = parseElement((EObject) refValue);
                refNode.setName(referenceName + " -> " + refNode.getName());
                elementNode.addChild(refNode);
            }
        }

        // Parse containment references
        for (EReference containment : element.eClass().getEAllContainments()) {
            Object contValue = element.eGet(containment);
            String containmentName = containment.getName();
            if (contValue instanceof List) {
                List<?> contList = (List<?>) contValue;
                for (Object contItem : contList) {
                    if (contItem instanceof EObject) {
                        TreeNode contNode = parseElement((EObject) contItem);
                        contNode.setName(containmentName + " -> " + contNode.getName());
                        elementNode.addChild(contNode);
                    }
                }
            } else if (contValue instanceof EObject) {
                TreeNode contNode = parseElement((EObject) contValue);
                contNode.setName(containmentName + " -> " + contNode.getName());
                elementNode.addChild(contNode);
            }
        }

        return elementNode;
    }

    static class TreeNode {
        private String name;
        private List<TreeNode> children = new ArrayList<>();

        TreeNode(String name) {
            this.name = name;
        }

        void addChild(TreeNode child) {
            children.add(child);
        }

        List<TreeNode> getChildren() {
            return children;
        }

        String getName() {
            return name;
        }

        void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
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
            return 3;
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return "Short Name";
                case 1:
                    return "Definition";
                case 2:
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
                    return treeNode.getName(); // Placeholder, replace with actual definition if available
                case 2:
                    return treeNode.getName(); // Placeholder, replace with actual value if available
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
