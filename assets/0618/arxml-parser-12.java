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


--


private int addDataToBatch(int parentId, String name, String path, String xmlPath, String key, String value, List<ArxmlParsedData> batch) {
    ArxmlParsedData data = new ArxmlParsedData(parentId, name, path, xmlPath, key, value);
    batch.add(data);
    return data.getId();
}


--


private void parseARPackage(ARPackage arPackage, int parentId, String path, String xmlPath, List<ArxmlParsedData> batch) {
    EAttribute featureShortName = IdentifiablePackage.eINSTANCE.getReferrable_ShortName();
    EReference refFeatureSubPkg = ArpackagePackage.eINSTANCE.getARPackage_ArPackages();
    EReference refFeatureElement = ArpackagePackage.eINSTANCE.getARPackage_Elements();

    String currentPath = path + "/ARPackage[" + arPackage.getShortName() + "]";
    String currentXmlPath = xmlPath + "/" + arPackage.getShortName();
    int id = addDataToBatch(parentId, "ARPackage", currentPath, currentXmlPath, featureShortName.getName(), arPackage.getShortName(), batch);

    for (ARPackage subPackage : (List<ARPackage>) arPackage.eGet(refFeatureSubPkg)) {
        parseARPackage(subPackage, id, currentPath, currentXmlPath, batch);
    }
    for (PackageableElement element : (List<PackageableElement>) arPackage.eGet(refFeatureElement)) {
        parsePackageableElement(element, id, currentPath, currentXmlPath, batch);
    }
}

private void parsePackageableElement(PackageableElement element, int parentId, String path, String xmlPath, List<ArxmlParsedData> batch) {
    EAttribute featureShortName = IdentifiablePackage.eINSTANCE.getReferrable_ShortName();

    String currentPath = path + "/" + element.eClass().getName() + "[" + element.getShortName() + "]";
    String currentXmlPath = xmlPath + "/" + element.getShortName();
    int id = addDataToBatch(parentId, element.eClass().getName(), currentPath, currentXmlPath, featureShortName.getName(), element.getShortName(), batch);

    collectValues(element, batch, id, currentPath, currentXmlPath);
}

private void parseEObject(EObject eObject, int parentId, String path, String xmlPath, List<ArxmlParsedData> batch) {
    String currentPath = path + "/" + eObject.eClass().getName() + "[" + getShortName(eObject) + "]";
    String currentXmlPath = xmlPath + "/" + getShortName(eObject);
    int id = addDataToBatch(parentId, eObject.eClass().getName(), currentPath, currentXmlPath, "shortName", getShortName(eObject), batch);

    collectValues(eObject, batch, id, currentPath, currentXmlPath);
}

private void collectValues(EObject eObject, List<ArxmlParsedData> batch, int parentId, String path, String xmlPath) {
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

private String getShortName(EObject eObject) {
    EStructuralFeature shortNameFeature = eObject.eClass().getEStructuralFeature("shortName");
    if (shortNameFeature != null && eObject.eIsSet(shortNameFeature)) {
        return eObject.eGet(shortNameFeature).toString();
    }
    return eObject.eClass().getName();
}


--

