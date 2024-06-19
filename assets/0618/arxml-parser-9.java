public static void main(String[] args) {
    // 예제 IFile 객체 생성 (실제 프로젝트에서는 IFile 객체를 받아서 사용)
    IFile arxmlFile = getIFile("/path/to/your/file.arxml"); // 또는 "/path/to/your/file.bmd"

    // 파일 경로 얻기
    String filePath = getFilePath(arxmlFile);

    // 루트 객체 로드
    AUTOSAR autosarRoot = getRoot(arxmlFile);

    if (autosarRoot != null) {
        // 데이터 파싱 및 생성
        List<ParsedData> parsedDataList = new ArrayList<>();
        for (ARPackage arPackage : autosarRoot.getArPackages()) {
            parseARPackage(arPackage, null, filePath, parsedDataList);
        }

        // 데이터를 활용하는 코드 (DB 삽입 또는 다른 로직)
        // insertParsedDataToDB(parsedDataList);
    } else {
        System.out.println("Failed to load ARXML/BMD file.");
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

private static AUTOSAR getRoot(IFile file) {
    if (file == null || !file.exists())
        return null;
    Resource resource = EcorePlatformUtil.getResource(file);
    if (resource != null && resource.getContents() != null) {
        for (EObject object : resource.getContents()) {
            if (object instanceof AUTOSAR)
                return (AUTOSAR) object;
        }
    }
    return null;
}
-----------

private static void parseARPackage(ARPackage arPackage, Integer parentId, String path, List<ParsedData> batch) {
    EAttribute featureShortName = IdentifiablePackage.eINSTANCE.getReferrable_ShortName();
    EReference refFeatureSubPkg = ArpackagePackage.eINSTANCE.getARPackage_ArPackages();
    EReference refFeatureElement = ArpackagePackage.eINSTANCE.getARPackage_Elements();

    String currentPath = path + "/ARPackage[" + arPackage.getShortName() + "]";
    Integer id = addDataToBatch(parentId, "ARPackage", arPackage.getShortName(), path, currentPath, batch);
    
    for (ARPackage subPackage : arPackage.getArPackages()) {
        parseARPackage(subPackage, id, currentPath, batch);
    }
    for (PackageableElement element : arPackage.getElements()) {
        parsePackageableElement(element, id, currentPath, batch);
    }
}

private static void parsePackageableElement(PackageableElement element, Integer parentId, String path, List<ParsedData> batch) {
    String currentPath = path + "/" + element.eClass().getName() + "[" + element.getShortName() + "]";
    Integer id = addDataToBatch(parentId, element.eClass().getName(), element.getShortName(), path, currentPath, batch);
    
    for (EStructuralFeature feature : element.eClass().getEAllStructuralFeatures()) {
        if (element.eIsSet(feature)) {
            Object value = element.eGet(feature);
            if (feature instanceof EAttribute) {
                addDataToBatch(id, feature.getName(), value.toString(), currentPath, currentPath + "/" + feature.getName(), batch);
            } else if (feature instanceof EReference && ((EReference) feature).isContainment()) {
                if (value instanceof List) {
                    for (Object child : (List<?>) value) {
                        if (child instanceof EObject) {
                            parseEObject((EObject) child, id, currentPath, batch);
                        }
                    }
                } else if (value instanceof EObject) {
                    parseEObject((EObject) value, id, currentPath, batch);
                }
            }
        }
    }
}

private static void parseEObject(EObject eObject, Integer parentId, String path, List<ParsedData> batch) {
    String currentPath = path + "/" + eObject.eClass().getName() + "[" + getShortName(eObject) + "]";
    Integer id = addDataToBatch(parentId, eObject.eClass().getName(), getShortName(eObject), path, currentPath, batch);
    
    for (EStructuralFeature feature : eObject.eClass().getEAllStructuralFeatures()) {
        if (eObject.eIsSet(feature)) {
            Object value = eObject.eGet(feature);
            if (feature instanceof EAttribute) {
                addDataToBatch(id, feature.getName(), value.toString(), currentPath, currentPath + "/" + feature.getName(), batch);
            } else if (feature instanceof EReference && ((EReference) feature).isContainment()) {
                if (value instanceof List) {
                    for (Object child : (List<?>) value) {
                        if (child instanceof EObject) {
                            parseEObject((EObject) child, id, currentPath, batch);
                        }
                    }
                } else if (value instanceof EObject) {
                    parseEObject((EObject) value, id, currentPath, batch);
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

private static Integer addDataToBatch(Integer parentId, String name, String value, String path, String newPath, List<ParsedData> batch) {
    ParsedData data = new ParsedData(parentId, name, value, path, newPath);
    batch.add(data);
    return data.getId();
}


