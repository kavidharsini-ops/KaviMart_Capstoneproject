package com.kavi.kavimart;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

/** Runs the packaged WAR under embedded Tomcat 9 for local development only. */
public final class DevTomcat {
  private DevTomcat() { }

  /** Start the local Tomcat server using PORT (default 8080). */
  public static void main(String[] args) throws Exception {
    int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
    File war = new File("target/kavimart.war");
    if (!war.isFile()) {
      throw new IllegalStateException("Build the WAR first: mvn clean package -DskipTests");
    }

    File baseDir = new File(war.getParentFile(), "tomcat-dev").getAbsoluteFile();
    Files.createDirectories(Path.of(baseDir.getPath(), "webapps"));

    Tomcat tomcat = new Tomcat();
    tomcat.setBaseDir(baseDir.getPath());
    tomcat.getHost().setAppBase("webapps");
    tomcat.getHost().setParentClassLoader(Tomcat.class.getClassLoader());

    Connector connector = tomcat.getConnector();
    connector.setPort(port);
    connector.setURIEncoding("UTF-8");

    tomcat.addWebapp("", war.getAbsolutePath());
    tomcat.start();
    System.out.println("KaviMart running at http://localhost:" + port);
    tomcat.getServer().await();
  }
}
