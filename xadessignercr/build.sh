mvn clean compile assembly:single
rm release/*
cp target/xadessignercr-0.0.1-SNAPSHOT-jar-with-dependencies.jar release/xadessignercr.jar