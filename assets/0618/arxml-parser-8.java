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
            List<List<ParsedData>> allBatches = new ArrayList<>();
            for (ARPackage arPackage : autosarRoot.getArPackages()) {
                List<ParsedData> batch = new ArrayList<>();
                parseARPackage(arPackage, null, filePath, batch);
                allBatches.add(batch);
            }

            // 모든 배치를 합쳐서 데이터베이스에 삽입
            List<ParsedData> allParsedData = new ArrayList<>();
            for (List<ParsedData> batch : allBatches) {
                allParsedData.addAll(batch);
            }
            insertParsedDataToDB(allParsedData);

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


------------



private static void parseARPackage(ARPackage arPackage, Integer parentId, String path, List<ParsedData> batch) {
    String currentPath = path + "/ARPackage[" + arPackage.getShortName() + "]";
    Integer id = addDataToBatch(parentId, "ARPackage", arPackage.getShortName(), currentPath, batch);
    for (EcucModuleDef ecucModuleDef : arPackage.getEcucDefs()) {
        parseEcucModuleDef(ecucModuleDef, id, currentPath, batch);
    }
    for (SwcImplementation swcImplementation : arPackage.getSwcImplementations()) {
        parseSwcImplementation(swcImplementation, id, currentPath, batch);
    }
    for (ARPackage subPackage : arPackage.getArPackages()) {
        parseARPackage(subPackage, id, currentPath, batch);
    }
    for (ComponentPrototype componentPrototype : arPackage.getComponentPrototypes()) {
        parseComponentPrototype(componentPrototype, id, currentPath, batch);
    }
    for (DataType dataType : arPackage.getDataTypes()) {
        parseDataType(dataType, id, currentPath, batch);
    }
    for (System system : arPackage.getSystems()) {
        parseSystem(system, id, currentPath, batch);
    }
    for (ModeDeclaration modeDeclaration : arPackage.getModeDeclarations()) {
        parseModeDeclaration(modeDeclaration, id, currentPath, batch);
    }
    for (Signal signal : arPackage.getSignals()) {
        parseSignal(signal, id, currentPath, batch);
    }
    for (SignalGroup signalGroup : arPackage.getSignalGroups()) {
        parseSignalGroup(signalGroup, id, currentPath, batch);
    }
    for (Service service : arPackage.getServices()) {
        parseService(service, id, currentPath, batch);
    }
    for (ModeDeclarationGroup modeDeclarationGroup : arPackage.getModeDeclarationGroups()) {
        parseModeDeclarationGroup(modeDeclarationGroup, id, currentPath, batch);
    }
    for (ModeDeclarationGroupPrototype modeDeclarationGroupPrototype : arPackage.getModeDeclarationGroupPrototypes()) {
        parseModeDeclarationGroupPrototype(modeDeclarationGroupPrototype, id, currentPath, batch);
    }
    for (DataPrototype dataPrototype : arPackage.getDataPrototypes()) {
        parseDataPrototype(dataPrototype, id, currentPath, batch);
    }
    for (ImplementationDataType implementationDataType : arPackage.getImplementationDataTypes()) {
        parseImplementationDataType(implementationDataType, id, currentPath, batch);
    }
}

private static void parseEcucModuleDef(EcucModuleDef ecucModuleDef, Integer parentId, String path, List<ParsedData> batch) {
    String currentPath = path + "/EcucModuleDef[" + ecucModuleDef.getShortName() + "]";
    Integer id = addDataToBatch(parentId, "EcucModuleDef", ecucModuleDef.getShortName(), currentPath, batch);
    for (EcucContainerDef container : ecucModuleDef.getContainers()) {
        parseEcucContainerDef(container, id, currentPath, batch);
    }
}

private static void parseEcucContainerDef(EcucContainerDef container, Integer parentId, String path, List<ParsedData> batch) {
    String currentPath = path + "/EcucContainerDef[" + container.getShortName() + "]";
    Integer id = addDataToBatch(parentId, "EcucContainerDef", container.getShortName(), currentPath, batch);
    for (EcucParameterDef parameter : container.getParameters()) {
        parseEcucParameterDef(parameter, id, currentPath, batch);
    }
    for (EcucContainerDef subContainer : container.getSubContainers()) {
        parseEcucContainerDef(subContainer, id, currentPath, batch);
    }
}

private static void parseEcucParameterDef(EcucParameterDef parameter, Integer parentId, String path, List<ParsedData> batch) {
    String currentPath = path + "/EcucParameterDef[" + parameter.getShortName() + "]";
    Integer id = addDataToBatch(parentId, "EcucParameterDef", parameter.getShortName(), currentPath, batch);
    if (parameter instanceof EcucTextualParameterDef) {
        addDataToBatch(id, "DefaultValue", ((EcucTextualParameterDef) parameter).getDefaultValue(), currentPath, batch);
    } else if (parameter instanceof EcucNumericalParameterDef) {
        addDataToBatch(id, "DefaultValue", ((EcucNumericalParameterDef) parameter).getDefaultValue(), currentPath, batch);
    } else if (parameter instanceof EcucFunctionNameDef) {
        addDataToBatch(id, "FunctionName", ((EcucFunctionNameDef) parameter).getFunctionName(), currentPath, batch);
    } else if (parameter instanceof EcucMeasureParameterDef) {
        addDataToBatch(id, "Unit", ((EcucMeasureParameterDef) parameter).getUnit(), currentPath, batch);
    } else if (parameter instanceof EcucBooleanParameterDef) {
        addDataToBatch(id, "DefaultValue", String.valueOf(((EcucBooleanParameterDef) parameter).isDefaultValue()), currentPath, batch);
    }
}

// 다른 파싱 메서드들도 동일한 방식으로 수정합니다.
// ...

private static Integer addDataToBatch(Integer parentId, String name, String value, String path, List<ParsedData> batch) {
    ParsedData data = new ParsedData(parentId, name, value, path);
    batch.add(data);
    return data.getId();
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



---


