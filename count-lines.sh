#!/bin/bash
find ./src/main/java -name "*.java" | xargs cat | sed '/^\s*$/d' | wc -l
