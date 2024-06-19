import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

class ArxmlParsedData {
    private static int nextId = 1;
    private int id;
    private String name;
    private String path;
    private String xmlPath;
    private String key;
    private String value;
    private int parentId;

    public ArxmlParsedData(int parentId, String name, String path, String xmlPath, String key, String value) {
        this.id = nextId++;
        this.parentId = parentId;
        this.name = name;
        this.path = path;
        this.xmlPath = xmlPath;
        this.key = key;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public int getParentId() {
        return parentId;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getXmlPath() {
        return xmlPath;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}

public class ArxmlParser {

    public static void main(String[] args) {
        // 샘플 파일 경로
        String filePath = "sample.arxml";
        
        // ARXML 파일을 파싱하여 데이터를 저장할 리스트
        List<ArxmlParsedData> batch = new ArrayList<>();

        // 리소스 팩토리 등록
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("arxml", new XMIResourceFactoryImpl());

        // 리소스 세트 생성
        ResourceSetImpl resourceSet = new ResourceSetImpl();

        // ARXML 파일 로드
        Resource resource = resourceSet.getResource(EcoreUtil.createFileURI(filePath), true);

        // 루트 요소 파싱
        if (resource != null && !resource.getContents().isEmpty()) {
            for (EObject eObject : resource.getContents()) {
                if (eObject instanceof AUTOSAR) {
                    AUTOSAR autosarRoot = (AUTOSAR) eObject;
                    for (ARPackage arPackage : autosarRoot.getArPackages()) {
                        parseARPackage(arPackage, 0, "/AUTOSAR", "/AUTOSAR", batch);
                    }
                }
            }
        }

        // 테이블 데이터를 출력
        printTableData(batch);
    }

    private static void parseARPackage(ARPackage arPackage, int parentId, String path, String xmlPath, List<ArxmlParsedData> batch) {
        String currentPath = path + "/ARPackage[" + arPackage.getShortName() + "]";
        String currentXmlPath = xmlPath + "/" + arPackage.getShortName();
        int id = addDataToBatch(parentId, "ARPackage", currentPath, currentXmlPath, "shortName", arPackage.getShortName(), batch);

        for (ARPackage subPackage : arPackage.getArPackages()) {
            parseARPackage(subPackage, id, currentPath, currentXmlPath, batch);
        }
        for (PackageableElement element : arPackage.getElements()) {
            parsePackageableElement(element, id, currentPath, currentXmlPath, batch);
        }
    }

    private static void parsePackageableElement(PackageableElement element, int parentId, String path, String xmlPath, List<ArxmlParsedData> batch) {
        String currentPath = path + "/" + element.eClass().getName() + "[" + element.getShortName() + "]";
        String currentXmlPath = xmlPath + "/" + element.getShortName();
        int id = addDataToBatch(parentId, element.eClass().getName(), currentPath, currentXmlPath, "shortName", element.getShortName(), batch);

        collectValues(element, batch, id, currentPath, currentXmlPath);
    }

    private static void parseEObject(EObject eObject, int parentId, String path, String xmlPath, List<ArxmlParsedData> batch) {
        String currentPath = path + "/" + eObject.eClass().getName() + "[" + getShortName(eObject) + "]";
        String currentXmlPath = xmlPath + "/" + getShortName(eObject);
        int id = addDataToBatch(parentId, eObject.eClass().getName(), currentPath, currentXmlPath, "shortName", getShortName(eObject), batch);

        collectValues(eObject, batch, id, currentPath, currentXmlPath);
    }

    private static void collectValues(EObject eObject, List<ArxmlParsedData> batch, int parentId, String path, String xmlPath) {
        for (EStructuralFeature feature : eObject.eClass().getEAllStructuralFeatures()) {
            if (eObject.eIsSet(feature)) {
                Object value = eObject.eGet(feature);
                if (feature instanceof EAttribute) {
                    String currentPath = path + "/" + feature.getName();
                    String currentXmlPath = xmlPath + "/" + feature.getName();
                    addDataToBatch(parentId, eObject.eClass().getName(), currentPath, currentXmlPath, feature.getName(), value.toString(), batch);
                } else if (feature instanceof EReference && ((EReference) feature).isContainment()) {
                    if (value instanceof List) {
                        for (Object child : (List<?>) value) {
                            if (child instanceof EObject) {
                                parseEObject((EObject) child, parentId, path, xmlPath, batch);
                            }
                        }
                    } else if (value instanceof EObject) {
                        parseEObject((EObject) value, parentId, path, xmlPath, batch);
                    }
                }
            }
        }
    }

    private static String getShortName(EObject eObject) {
        EStructuralFeature shortNameFeature = eObject.eClass().getEStructuralFeature("shortName");
        if (shortNameFeature != null && eObject.eIsSet(shortNameFeature)) {
            return eObject.eGet(shortNameFeature).toString();
        }
        return eObject.eClass().getName();
    }

    private static int addDataToBatch(int parentId, String name, String path, String xmlPath, String key, String value, List<ArxmlParsedData> batch) {
        ArxmlParsedData data = new ArxmlParsedData(parentId, name, path, xmlPath, key, value);
        batch.add(data);
        return data.getId();
    }

    private static void printTableData(List<ArxmlParsedData> batch) {
        System.out.printf("%-5s %-8s %-30s %-50s %-50s %-15s %-20s%n", "ID", "ParentID", "Name", "Path", "XML Path", "Key", "Value");
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------");
        for (ArxmlParsedData data : batch) {
            System.out.printf("%-5d %-8d %-30s %-50s %-50s %-15s %-20s%n", data.getId(), data.getParentId(), data.getName(), data.getPath(), data.getXmlPath(), data.getKey(), data.getValue());
        }
    }
}
