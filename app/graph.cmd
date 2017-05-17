rem utility script to start the application

rem script directory
SET mypath=%~dp0
SET javapath=java
SET mainclass=ca.empowered.nms.graph.Main
SET classpath=%mypath%..\;%mypath%..\conf\;%mypath%..\data\;%mypath%..\libs\*
SET jvmopts=-Dlog4j.debug

%javapath% %jvmopts% -cp %classpath% %mainclass%