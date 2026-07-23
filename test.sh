#!/usr/bin/env bash
set -euo pipefail
rm -rf out/tests
mkdir -p out/tests
javac -encoding UTF-8 -d out/tests   core/src/main/java/com/maze/GridEntity.java   core/src/main/java/com/maze/MazeMap.java   core/src/main/java/com/maze/MazeRules.java   tests/com/maze/MazeTests.java
java -cp out/tests com.maze.MazeTests
