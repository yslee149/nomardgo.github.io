@echo off
setlocal

:: Define the base directory
set BASE_DIR=com\mutecsoft\arxml\compare

:: Create directories
echo Creating directories...
mkdir %BASE_DIR%
mkdir %BASE_DIR%\command
mkdir %BASE_DIR%\common
mkdir %BASE_DIR%\comparison
mkdir %BASE_DIR%\comparison\folder
mkdir %BASE_DIR%\comparison\file
mkdir %BASE_DIR%\di
mkdir %BASE_DIR%\factory
mkdir %BASE_DIR%\handler
mkdir %BASE_DIR%\model
mkdir %BASE_DIR%\navigation
mkdir %BASE_DIR%\service
mkdir %BASE_DIR%\service\impl
mkdir %BASE_DIR%\view
mkdir %BASE_DIR%\viewmodel

:: Create empty files
echo Creating files...

type nul > %BASE_DIR%\command\ExampleCommand.java
type nul > %BASE_DIR%\common\Constants.java
type nul > %BASE_DIR%\common\GlobalSettings.java
type nul > %BASE_DIR%\common\Utility.java
type nul > %BASE_DIR%\comparison\folder\FolderComparator.java
type nul > %BASE_DIR%\comparison\folder\FolderComparisonResult.java
type nul > %BASE_DIR%\comparison\file\FileComparator.java
type nul > %BASE_DIR%\comparison\file\FileComparisonResult.java
type nul > %BASE_DIR%\di\AppModule.java
type nul > %BASE_DIR%\di\InjectorProvider.java
type nul > %BASE_DIR%\factory\ModelFactory.java
type nul > %BASE_DIR%\handler\ExampleHandler.java
type nul > %BASE_DIR%\model\MyModel.java
type nul > %BASE_DIR%\model\MyModelNode.java
type nul > %BASE_DIR%\navigation\Navigator.java
type nul > %BASE_DIR%\service\ComparisonService.java
type nul > %BASE_DIR%\service\ExampleService.java
type nul > %BASE_DIR%\service\impl\ComparisonServiceImpl.java
type nul > %BASE_DIR%\service\impl\ExampleServiceImpl.java
type nul > %BASE_DIR%\view\TreeViewPanel.java
type nul > %BASE_DIR%\view\MyContentProvider.java
type nul > %BASE_DIR%\view\MyLabelProvider.java
type nul > %BASE_DIR%\viewmodel\TreeViewModel.java
type nul > %BASE_DIR%\Activator.java
type nul > %BASE_DIR%\Main.java

echo Project structure created successfully.
endlocal
