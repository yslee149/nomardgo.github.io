import org.artop.aal.common.metamodel.AutosarReleaseDescriptor;
import org.artop.aal.common.resource.AutosarResourceFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import autosar40.util.Autosar40ReleaseDescriptor;
import autosar30.util.Autosar30ReleaseDescriptor;
import autosar40.arpackage.ARPackage;
import autosar40.autosar.AUTOSAR;
import autosar40.ecucparameterdef.*;
import autosar40.swc.*;
import autosar40.system.*;
import autosar40.data.*;
import autosar40.modedecclaration.*;
import autosar40.signal.*;
import autosar40.service.*;
import autosar40.hardware.*;
import autosar40.com.*;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
                            parseARPackage(arPackage, null, arxmlFile.getAbsolutePath());
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

    private static void parseARPackage(ARPackage arPackage, Integer parentId, String path) throws SQLException {
        String currentPath = path + "/ARPackage[" + arPackage.getShortName() + "]";
        Integer id = insertData(parentId, "ARPackage", arPackage.getShortName(), currentPath);
        for (EcucModuleDef ecucModuleDef : arPackage.getEcucDefs()) {
            parseEcucModuleDef(ecucModuleDef, id, currentPath);
        }
        for (SwcImplementation swcImplementation : arPackage.getSwcImplementations()) {
            parseSwcImplementation(swcImplementation, id, currentPath);
        }
        for (ARPackage subPackage : arPackage.getArPackages()) {
            parseARPackage(subPackage, id, currentPath);
        }
        for (ComponentPrototype componentPrototype : arPackage.getComponentPrototypes()) {
            parseComponentPrototype(componentPrototype, id, currentPath);
        }
        for (DataType dataType : arPackage.getDataTypes()) {
            parseDataType(dataType, id, currentPath);
        }
        for (System system : arPackage.getSystems()) {
            parseSystem(system, id, currentPath);
        }
        for (ModeDeclaration modeDeclaration : arPackage.getModeDeclarations()) {
            parseModeDeclaration(modeDeclaration, id, currentPath);
        }
        for (Signal signal : arPackage.getSignals()) {
            parseSignal(signal, id, currentPath);
        }
        for (SignalGroup signalGroup : arPackage.getSignalGroups()) {
            parseSignalGroup(signalGroup, id, currentPath);
        }
        for (Service service : arPackage.getServices()) {
            parseService(service, id, currentPath);
        }
        for (ModeDeclarationGroup modeDeclarationGroup : arPackage.getModeDeclarationGroups()) {
            parseModeDeclarationGroup(modeDeclarationGroup, id, currentPath);
        }
        for (ModeDeclarationGroupPrototype modeDeclarationGroupPrototype : arPackage.getModeDeclarationGroupPrototypes()) {
            parseModeDeclarationGroupPrototype(modeDeclarationGroupPrototype, id, currentPath);
        }
        for (DataPrototype dataPrototype : arPackage.getDataPrototypes()) {
            parseDataPrototype(dataPrototype, id, currentPath);
        }
        for (ImplementationDataType implementationDataType : arPackage.getImplementationDataTypes()) {
            parseImplementationDataType(implementationDataType, id, currentPath);
        }
        // 추가적인 파싱 대상 포함
    }

    private static void parseEcucModuleDef(EcucModuleDef ecucModuleDef, Integer parentId, String path) throws SQLException {
        String currentPath = path + "/EcucModuleDef[" + ecucModuleDef.getShortName() + "]";
        Integer id = insertData(parentId, "EcucModuleDef", ecucModuleDef.getShortName(), currentPath);
        for (EcucContainerDef container : ecucModuleDef.getContainers()) {
            parseEcucContainerDef(container, id, currentPath);
        }
    }

    private static void parseEcucContainerDef(EcucContainerDef container, Integer parentId, String path) throws SQLException {
        String currentPath = path + "/EcucContainerDef[" + container.getShortName() + "]";
        Integer id = insertData(parentId, "EcucContainerDef", container.getShortName(), currentPath);
        for (EcucParameterDef parameter : container.getParameters()) {
            parseEcucParameterDef(parameter, id, currentPath);
        }
        for (EcucContainerDef subContainer : container.getSubContainers()) {
            parseEcucContainerDef(subContainer, id, currentPath);
        }
    }

    private static void parseEcucParameterDef(EcucParameterDef parameter, Integer parentId, String path) throws SQLException {
        String currentPath = path + "/EcucParameterDef[" + parameter.getShortName() + "]";
        Integer id = insertData(parentId, "EcucParameterDef", parameter.getShortName(), currentPath);
        if (parameter instanceof EcucTextualParameterDef) {
            insertData(id, "DefaultValue", ((EcucTextualParameterDef) parameter).getDefaultValue(), currentPath);
        } else if (parameter instanceof EcucNumericalParameterDef) {
            insertData(id, "DefaultValue", ((EcucNumericalParameterDef) parameter).getDefaultValue(), currentPath);
        } else if (parameter instanceof EcucFunctionNameDef) {
            insertData(id, "FunctionName", ((EcucFunctionNameDef) parameter).getFunctionName(), currentPath);
        } else if (parameter instanceof EcucMeasureParameterDef) {
            insertData(id, "Unit", ((EcucMeasureParameterDef) parameter).getUnit(), currentPath);
        } else if (parameter instanceof EcucBooleanParameterDef) {
            insertData(id, "DefaultValue", String.valueOf(((EcucBooleanParameterDef) parameter).isDefaultValue()), currentPath);
        }
    }

    private static void parseSwcImplementation(SwcImplementation swcImplementation, Integer parentId, String path) throws SQLException {
        String currentPath = path + "/SwcImplementation[" + swcImplementation.getShortName() + "]";
        Integer id = insertData(parentId, "SwcImplementation", swcImplementation.getShortName(), currentPath);
        for (RunnableEntity runnable : swcImplementation.getRunnableEntities()) {
            insertData(id, "RunnableEntity", runnable.getShortName(), currentPath);
        }
        for (Port port : swcImplementation.getPorts()) {
            insertData(id, "Port", port.getShortName(), currentPath);
        }
    }

    private static void parseComponentPrototype(ComponentPrototype componentPrototype, Integer parentId, String path) throws SQLException {
        String currentPath = path + "/ComponentPrototype[" + componentPrototype.getShortName() + "]";
        Integer id = insertData(parentId, "ComponentPrototype", componentPrototype.getShortName(), currentPath);
        for (Port port : componentPrototype.getPorts()) {
            insertData(id, "Port", port.getShortName(), currentPath);
        }
    }

    private static void parseDataType(DataType dataType, Integer parentId, String path) throws SQLException {
        String currentPath = path + "/DataType[" + dataType.getShortName() + "]";
        Integer id = insertData(parentId, "DataType", dataType.getShortName(), currentPath);
        if (dataType instanceof ImplementationDataType) {
            parseImplementationDataType((ImplementationDataType) dataType, id, currentPath);
        } else if (dataType instanceof ApplicationPrimitiveDataType) {
            insertData(id, "BaseType", ((ApplicationPrimitiveDataType) dataType).getBaseType().getShortName(), currentPath);
        } else if (dataType instanceof ApplicationArrayDataType) {
            parseApplicationArrayDataType((ApplicationArrayDataType) dataType, id, currentPath);
        } else if (dataType instanceof ApplicationRecordDataType) {
            parseApplicationRecordDataType((ApplicationRecordDataType) dataType, id, currentPath);
        } else if (dataType instanceof ApplicationCompositeDataType) {
            parseApplicationCompositeDataType((ApplicationCompositeDataType) dataType, id, currentPath);
        }
    }

    private static void parseImplementationDataType(ImplementationDataType dataType, Integer parentId, String path) throws SQLException {
        insertData(parentId, "BaseType", dataType.getBaseType().getShortName(), path);
    }

    private static void parseApplicationArrayDataType(ApplicationArrayDataType dataType, Integer parentId, String path) throws SQLException {
        String currentPath = path + "/ApplicationArrayDataType[" + dataType.getShortName() + "]";
        Integer id = insertData(parentId, "ApplicationArrayDataType", dataType.getShortName(), currentPath);
        for (ApplicationArrayElement element : dataType.getElements()) {
            parseApplicationArrayElement(element, id, currentPath);
        }
    }

    private static void parseApplicationArrayElement(ApplicationArrayElement element, Integer parentId, String path) throws SQLException {
        insertData(parentId, "Element", element.getShortName(), path);
    }

    private static void parseApplicationRecordDataType(ApplicationRecordDataType dataType, Integer parentId, String path) throws SQLException {
        String currentPath = path + "/ApplicationRecordDataType[" + dataType.getShortName() + "]";
        Integer id = insertData(parentId, "ApplicationRecordDataType", dataType.getShortName(), currentPath);
        for (ApplicationDataTypeElement element : dataType.getElements()) {
            parseApplicationDataTypeElement(element, id, currentPath);
        }
    }

    private static void parseApplicationCompositeDataType(ApplicationCompositeDataType dataType, Integer parentId, String path) throws SQLException {
        String currentPath = path + "/ApplicationCompositeDataType[" + dataType.getShortName() + "]";
        Integer id = insertData(parentId, "ApplicationCompositeDataType", dataType.getShortName(), currentPath);
        for (ApplicationDataTypeElement element : dataType.getElements()) {
            parseApplicationDataTypeElement(element, id, currentPath);
        }
    }

    private static void parseApplicationDataTypeElement(ApplicationDataTypeElement element, Integer parentId, String path) throws SQLException {
        insertData(parentId, "DataTypeElement", element.getShortName(), path);
    }

    private static void parseSystem(System system, Integer parentId, String path) throws SQLException {
        String currentPath = path + "/System[" + system.getShortName() + "]";
        Integer id = insertData(parentId, "System", system.getShortName(), currentPath);
        for (ComponentPrototype componentPrototype : system.getComponentPrototypes()) {
            parseComponentPrototype(componentPrototype, id, currentPath);
        }
    }

    private static void parseModeDeclaration(ModeDeclaration modeDeclaration, Integer parentId, String path) throws SQLException {
        String currentPath = path + "/ModeDeclaration[" + modeDeclaration.getShortName() + "]";
        Integer id = insertData(parentId, "ModeDeclaration", modeDeclaration.getShortName(), currentPath);
        for (ModeDeclarationGroupPrototype modeDeclarationGroupPrototype : modeDeclaration.getModeDeclarationGroupPrototypes()) {
            parseModeDeclarationGroupPrototype(modeDeclarationGroupPrototype, id, currentPath);
        }
    }

    private static void parseModeDeclarationGroup(ModeDeclarationGroup modeDeclarationGroup, Integer parentId, String path) throws SQLException {
        String currentPath = path + "/ModeDeclarationGroup[" + modeDeclarationGroup.getShortName() + "]";
        Integer id = insertData(parentId, "ModeDeclarationGroup", modeDeclarationGroup.getShortName(), currentPath);
        for (ModeDeclaration modeDeclaration : modeDeclarationGroup.getModeDeclarations()) {
            parseModeDeclaration(modeDeclaration, id, currentPath);
        }
    }

    private static void parseModeDeclarationGroupPrototype(ModeDeclarationGroupPrototype modeDeclarationGroupPrototype, Integer parentId, String path) throws SQLException {
        insertData(parentId, "ModeDeclarationGroupPrototype", modeDeclarationGroupPrototype.getShortName(), path);
    }

    private static void parseSignal(Signal signal, Integer parentId, String path) throws SQLException {
        String currentPath = path + "/Signal[" + signal.getShortName() + "]";
        Integer id = insertData(parentId, "Signal", signal.getShortName(), currentPath);
        for (SignalGroup signalGroup : signal.getSignalGroups()) {
            parseSignalGroup(signalGroup, id, currentPath);
        }
    }

    private static void parseSignalGroup(SignalGroup signalGroup, Integer parentId, String path) throws SQLException {
        insertData(parentId, "SignalGroup", signalGroup.getShortName(), path);
    }

    private static void parseService(Service service, Integer parentId, String path) throws SQLException {
        String currentPath = path + "/Service[" + service.getShortName() + "]";
        Integer id = insertData(parentId, "Service", service.getShortName(), currentPath);
        for (Operation operation : service.getOperations()) {
            parseOperation(operation, id, currentPath);
        }
    }

    private static void parseOperation(Operation operation, Integer parentId, String path) throws SQLException {
        String currentPath = path + "/Operation[" + operation.getShortName() + "]";
        Integer id = insertData(parentId, "Operation", operation.getShortName(), currentPath);
        for (OperationPrototype operationPrototype : operation.getOperationPrototypes()) {
            parseOperationPrototype(operationPrototype, id, currentPath);
        }
    }

    private static void parseOperationPrototype(OperationPrototype operationPrototype, Integer parentId, String path) throws SQLException {
        insertData(parentId, "OperationPrototype", operationPrototype.getShortName(), path);
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
