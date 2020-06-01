@REM ************************************************************************************
@REM Description: run Pingclient
@REM Author: Rui Moreira
@REM Date: 20/02/2005
@REM ************************************************************************************
@REM Script usage: runsetup <role> (where role should be: server / client)
@REM call setenv
call setenv server

@cd %ABSPATH2CLASSES%
@cls

java -cp %CLASSPATH% -Djava.rmi.server.codebase=%SERVER_CODEBASE% -Djava.rmi.server.hostname=%SERVER_RMI_HOST% -Djava.security.policy=%SERVER_SECURITY_POLICY% %JAVAPACKAGEROLE%.%SERVER_CLASS_PREFIX%%SERVER_CLASS_POSTFIX% %REGISTRY_HOST% %REGISTRY_PORT% %SERVICE_NAME_ON_REGISTRY%

@cd %ABSPATH2SRC%\%JAVASCRIPTSPATH%