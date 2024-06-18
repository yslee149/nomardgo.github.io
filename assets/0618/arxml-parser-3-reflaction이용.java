import org.artop.aal.common.metamodel.AutosarReleaseDescriptor;
import org.artop.aal.common.resource.AutosarResourceFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import autosar40.util.Autosar40ReleaseDescriptor;
import autosar30.util.Autosar30ReleaseDescriptor;
import autosar40.arpackage.ARPackage;
import autosar40.autosar.AUTOSAR;

import java.io.File;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ARXMLParser {

    private static Connection connection;

    public static void main(String[] args) {
        try {
            // SQLite 데이터베이스 연결
            connection = DriverManager.getConnection("jdbc:sqlite:arxml_data.db");
            createTable();

            // ARXML 파일 경로 설정
            File arxmlFile = new File("/path/to/your/file.arxml");
            String version = ARXMLVersionDetector.detectVersion(arxmlFile);

            // AUTOSAR 리소스 설정
            ResourceSet resourceSet = new ResourceSetImpl();
            resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("arxml", new AutosarResourceFactory());

            if ("4.x".equals(version)) {
                EPackage.Registry.INSTANCE.put(Autosar40ReleaseDescriptor.INSTANCE.getNamespace(), Autosar40ReleaseDescriptor.INSTANCE.getEPackage());
                resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("arxml", new XMIResourceFactoryImpl());
                System.out.println("Using AUTOSAR 4.x settings");
            } else if ("3.x".equals(version)) {
                EPackage.Registry.INSTANCE.put(Autosar30ReleaseDescriptor.INSTANCE.getNamespace(), Autosar30ReleaseDescriptor.INSTANCE.getEPackage());
                resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("arxml", new XMIResourceFactoryImpl());
                System.out.println("Using AUTOSAR 3.x settings");
            } else {
                System.out.println("Unknown AUTOSAR version");
                return;
            }

            // ARXML 파일 로드
            URI uri = URI.createFileURI(arxmlFile.getAbsolutePath());
            Resource resource = resourceSet.getResource(uri, true);

            // 로드된 리소스 확인
            if (resource.isLoaded()) {
                System.out.println("ARXML 파일이 성공적으로 로드되었습니다.");

                // 모든 객체 탐색 및 속성 파싱
                for (EObject eObject : resource.getContents()) {
                    if (eObject instanceof AUTOSAR) {
                        AUTOSAR autosarRoot = (AUTOSAR) eObject;
                        for (ARPackage arPackage : autosarRoot.getArPackages()) {
                            parseEObject(arPackage, null, arxmlFile.getAbsolutePath());
                        }
                    }
                }
            } else {
                System.out.println("ARXML 파일 로드에 실패했습니다.");
            }
        } catch (Exception e) {
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

    private static void createTable() throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS arxml_data ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "parent_id INTEGER, "
                + "name TEXT, "
                + "value TEXT, "
                + "path TEXT, "
                + "FOREIGN KEY (parent_id) REFERENCES arxml_data (id)"
                + ")";
        try (PreparedStatement stmt = connection.prepareStatement(createTableSQL)) {
            stmt.execute();
        }
    }

    private static void parseEObject(EObject eObject, Integer parentId, String path) throws SQLException {
        String className = eObject.eClass().getName();
        String shortName = (String) eObject.eGet(eObject.eClass().getEStructuralFeature("shortName"));
        String currentPath = path + "/" + className + "[" + shortName + "]";
        Integer id = insertData(parentId, className, shortName, currentPath);

        for (EStructuralFeature feature : eObject.eClass().getEAllStructuralFeatures()) {
            Object value = eObject.eGet(feature);
            if (value instanceof List) {
                for (Object item : (List<?>) value) {
                    if (item instanceof EObject) {
                        parseEObject((EObject) item, id, currentPath);
                    }
                }
            } else if (value instanceof EObject) {
                parseEObject((EObject) value, id, currentPath);
            } else {
                insertData(id, feature.getName(), value != null ? value.toString() : "null", currentPath);
            }
        }
    }

    private static Integer insertData(Integer parentId, String name, String value, String path) throws SQLException {
        String insertSQL = "INSERT INTO arxml_data (parent_id, name, value, path) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setObject(1, parentId);
            pstmt.setString(2, name);
            pstmt.setString(3, value);
            pstmt.setString(4, path);
            pstmt.executeUpdate();
            try (var rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return null;
    }
}
