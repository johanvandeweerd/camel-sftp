# Bug in FTP-component regarding known_hosts
## Run it
To simulate the problem:
```
# mvn clean spring-boot:run
```
To "fix it", remove the useUserKnownHostsFile=false option in SftpRouteBuilder on line 11. And run the project again.

The application is going to a public sftp server and list all the files in a directory.
### The problem
I have a known_hosts file on my classpath and I want the ftp-component to use this known_hosts file when connecting to an sftp server (and not be dependent on the ~/.ssh/known_hosts file).
The documentation (http://camel.apache.org/ftp2.html) mentions that u can use the option knownHostsUri to configure the (classpath) location of a known_hosts file.
When using this option (in combination with the option useUserKnownHostsFile=false), camel fails to connect to the server because of a NullPointerException (see below).
When debugging, it seems that the list of known_hosts from the classpath are first loaded succesfully (SftpOperations:268) but afterwards the known_hosts are ALWAYS overridden (SftpOperations:284). When setting the option useUserKnownHostsFile to false, there is no file to load (no fallback to the known_hosts file from the home directory) and the exception is thrown.

```
2017-01-17 18:19:33.397  WARN 2652 --- [           main] o.a.c.c.file.remote.SftpConsumer         : Error auto creating directory: pub/example due Cannot connect to sftp://demo@test.rebex.net:22. This exception is ignored.

org.apache.camel.component.file.GenericFileOperationFailedException: Cannot connect to sftp://demo@test.rebex.net:22
	at org.apache.camel.component.file.remote.SftpOperations.connect(SftpOperations.java:146) ~[camel-ftp-2.18.1.jar:2.18.1]
	at org.apache.camel.component.file.remote.RemoteFileConsumer.connectIfNecessary(RemoteFileConsumer.java:203) ~[camel-ftp-2.18.1.jar:2.18.1]
	at org.apache.camel.component.file.remote.SftpConsumer.doStart(SftpConsumer.java:53) ~[camel-ftp-2.18.1.jar:2.18.1]
	at org.apache.camel.support.ServiceSupport.start(ServiceSupport.java:61) [camel-core-2.18.1.jar:2.18.1]
	at org.apache.camel.impl.DefaultCamelContext.startService(DefaultCamelContext.java:3371) [camel-core-2.18.1.jar:2.18.1]
	at org.apache.camel.impl.DefaultCamelContext.doStartOrResumeRouteConsumers(DefaultCamelContext.java:3688) [camel-core-2.18.1.jar:2.18.1]
	at org.apache.camel.impl.DefaultCamelContext.doStartRouteConsumers(DefaultCamelContext.java:3624) [camel-core-2.18.1.jar:2.18.1]
	at org.apache.camel.impl.DefaultCamelContext.safelyStartRouteServices(DefaultCamelContext.java:3544) [camel-core-2.18.1.jar:2.18.1]
	at org.apache.camel.impl.DefaultCamelContext.doStartOrResumeRoutes(DefaultCamelContext.java:3308) [camel-core-2.18.1.jar:2.18.1]
	at org.apache.camel.impl.DefaultCamelContext.doStartCamel(DefaultCamelContext.java:3162) [camel-core-2.18.1.jar:2.18.1]
	at org.apache.camel.impl.DefaultCamelContext.access$000(DefaultCamelContext.java:182) [camel-core-2.18.1.jar:2.18.1]
	at org.apache.camel.impl.DefaultCamelContext$2.call(DefaultCamelContext.java:2957) [camel-core-2.18.1.jar:2.18.1]
	at org.apache.camel.impl.DefaultCamelContext$2.call(DefaultCamelContext.java:2953) [camel-core-2.18.1.jar:2.18.1]
	at org.apache.camel.impl.DefaultCamelContext.doWithDefinedClassLoader(DefaultCamelContext.java:2976) [camel-core-2.18.1.jar:2.18.1]
	at org.apache.camel.impl.DefaultCamelContext.doStart(DefaultCamelContext.java:2953) [camel-core-2.18.1.jar:2.18.1]
	at org.apache.camel.support.ServiceSupport.start(ServiceSupport.java:61) [camel-core-2.18.1.jar:2.18.1]
	at org.apache.camel.impl.DefaultCamelContext.start(DefaultCamelContext.java:2920) [camel-core-2.18.1.jar:2.18.1]
	at org.apache.camel.spring.boot.RoutesCollector.maybeStart(RoutesCollector.java:141) [camel-spring-boot-2.18.1.jar:2.18.1]
	at org.apache.camel.spring.boot.RoutesCollector.onApplicationEvent(RoutesCollector.java:116) [camel-spring-boot-2.18.1.jar:2.18.1]
	at org.apache.camel.spring.boot.RoutesCollector.onApplicationEvent(RoutesCollector.java:41) [camel-spring-boot-2.18.1.jar:2.18.1]
	at org.springframework.context.event.SimpleApplicationEventMulticaster.invokeListener(SimpleApplicationEventMulticaster.java:166) [spring-context-4.3.5.RELEASE.jar:4.3.5.RELEASE]
	at org.springframework.context.event.SimpleApplicationEventMulticaster.multicastEvent(SimpleApplicationEventMulticaster.java:138) [spring-context-4.3.5.RELEASE.jar:4.3.5.RELEASE]
	at org.springframework.context.support.AbstractApplicationContext.publishEvent(AbstractApplicationContext.java:383) [spring-context-4.3.5.RELEASE.jar:4.3.5.RELEASE]
	at org.springframework.context.support.AbstractApplicationContext.publishEvent(AbstractApplicationContext.java:337) [spring-context-4.3.5.RELEASE.jar:4.3.5.RELEASE]
	at org.springframework.context.support.AbstractApplicationContext.finishRefresh(AbstractApplicationContext.java:882) [spring-context-4.3.5.RELEASE.jar:4.3.5.RELEASE]
	at org.springframework.boot.context.embedded.EmbeddedWebApplicationContext.finishRefresh(EmbeddedWebApplicationContext.java:144) [spring-boot-1.4.3.RELEASE.jar:1.4.3.RELEASE]
	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:545) [spring-context-4.3.5.RELEASE.jar:4.3.5.RELEASE]
	at org.springframework.boot.context.embedded.EmbeddedWebApplicationContext.refresh(EmbeddedWebApplicationContext.java:122) [spring-boot-1.4.3.RELEASE.jar:1.4.3.RELEASE]
	at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:761) [spring-boot-1.4.3.RELEASE.jar:1.4.3.RELEASE]
	at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:371) [spring-boot-1.4.3.RELEASE.jar:1.4.3.RELEASE]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:315) [spring-boot-1.4.3.RELEASE.jar:1.4.3.RELEASE]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1186) [spring-boot-1.4.3.RELEASE.jar:1.4.3.RELEASE]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1175) [spring-boot-1.4.3.RELEASE.jar:1.4.3.RELEASE]
	at be.engine31.camel.Application.main(Application.java:10) [classes/:na]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_91]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_91]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_91]
	at java.lang.reflect.Method.invoke(Method.java:498) ~[na:1.8.0_91]
	at com.intellij.rt.execution.application.AppMain.main(AppMain.java:147) [idea_rt.jar:na]
Caused by: java.lang.NullPointerException: null
	at com.jcraft.jsch.Util.checkTilde(Util.java:489) ~[jsch-0.1.54.jar:na]
	at com.jcraft.jsch.KnownHosts.setKnownHosts(KnownHosts.java:54) ~[jsch-0.1.54.jar:na]
	at com.jcraft.jsch.JSch.setKnownHosts(JSch.java:317) ~[jsch-0.1.54.jar:na]
	at org.apache.camel.component.file.remote.SftpOperations.createSession(SftpOperations.java:284) ~[camel-ftp-2.18.1.jar:2.18.1]
	at org.apache.camel.component.file.remote.SftpOperations.connect(SftpOperations.java:115) ~[camel-ftp-2.18.1.jar:2.18.1]
	... 38 common frames omitted
```

### References
* http://camel.apache.org/file2.html
* http://www.sftp.net/public-online-sftp-servers
* http://docs.spring.io/spring-boot/docs/current/reference/html/
