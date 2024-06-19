static class ParsedData {
    private static int nextId = 1;
    private Integer id;
    private String name;
    private String path;
    private String xmlPath;
    private String key;
    private String value;
    private Integer parentId;

    public ParsedData(Integer parentId, String name, String path, String xmlPath, String key, String value) {
        this.id = nextId++;
        this.parentId = parentId;
        this.name = name;
        this.path = path;
        this.xmlPath = xmlPath;
        this.key = key;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public Integer getParentId() {
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

----------


private static void parseARPackage(ARPackage arPackage, Integer parentId, String path, List<ParsedData> batch) {
    EAttribute featureShortName = IdentifiablePackage.eINSTANCE.getReferrable_ShortName();
    EReference refFeatureSubPkg = ArpackagePackage.eINSTANCE.getARPackage_ArPackages();
    EReference refFeatureElement = ArpackagePackage.eINSTANCE.getARPackage_Elements();

    String currentPath = path + "/ARPackage[" + arPackage.getShortName() + "]";
    Integer id = addDataToBatch(parentId, "ARPackage", currentPath, currentPath, "shortName", arPackage.getShortName(), batch);
    
    for (ARPackage subPackage : arPackage.getArPackages()) {
        parseARPackage(subPackage, id, currentPath, batch);
    }
    for (PackageableElement element : arPackage.getElements()) {
        parsePackageableElement(element, id, currentPath, batch);
    }
}

private static void parsePackageableElement(PackageableElement element, Integer parentId, String path, List<ParsedData> batch) {
    String currentPath = path + "/" + element.eClass().getName() + "[" + element.getShortName() + "]";
    Integer id = addDataToBatch(parentId, element.eClass().getName(), path, currentPath, "shortName", element.getShortName(), batch);
    
    for (EStructuralFeature feature : element.eClass().getEAllStructuralFeatures()) {
        if (element.eIsSet(feature)) {
            Object value = element.eGet(feature);
            if (feature instanceof EAttribute) {
                addDataToBatch(id, element.eClass().getName(), currentPath, currentPath + "/" + feature.getName(), feature.getName(), value.toString(), batch);
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
    Integer id = addDataToBatch(parentId, eObject.eClass().getName(), path, currentPath, "shortName", getShortName(eObject), batch);
    
    for (EStructuralFeature feature : eObject.eClass().getEAllStructuralFeatures()) {
        if (eObject.eIsSet(feature)) {
            Object value = eObject.eGet(feature);
            if (feature instanceof EAttribute) {
                addDataToBatch(id, eObject.eClass().getName(), currentPath, currentPath + "/" + feature.getName(), feature.getName(), value.toString(), batch);
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

private static Integer addDataToBatch(Integer parentId, String name, String path, String xmlPath, String key, String value, List<ParsedData> batch) {
    ParsedData data = new ParsedData(parentId, name, path, xmlPath, key, value);
    batch.add(data);
    return data.getId();
}
