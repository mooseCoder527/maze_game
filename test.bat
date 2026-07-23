@echo off
setlocal
if exist out\tests rmdir /s /q out\tests
mkdir out\tests
javac -encoding UTF-8 -d out\tests ^
  core\src\main\java\com\maze\GridEntity.java ^
  core\src\main\java\com\maze\MazeMap.java ^
  core\src\main\java\com\maze\MazeRules.java ^
  tests\com\maze\MazeTests.java
if errorlevel 1 exit /b 1
java -cp out\tests com.maze.MazeTests
