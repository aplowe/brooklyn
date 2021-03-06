package brooklyn;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrooklynVersion {

  private static final Logger log = LoggerFactory.getLogger(BrooklynVersion.class);
  
  private static final String VERSION_RESOURCE_FILE = "META-INF/maven/io.brooklyn/brooklyn-core/pom.properties";
  private static final String VERSION_PROPERTY_NAME = "version";

  public static final BrooklynVersion INSTANCE = new BrooklynVersion();

  private final String versionFromClasspath;
  // static useful when running from the IDE
  // TODO is the classpath version ever useful? should we always use the static?
  private final String versionFromStatic = "0.6.0-SNAPSHOT"; // BROOKLYN_VERSION
  private final String version;

  public BrooklynVersion() {
    this.versionFromClasspath = readVersionPropertyFromClasspath(BrooklynVersion.class.getClassLoader());
    if (isValid(versionFromClasspath)) {
        this.version = versionFromClasspath;
        if (!this.version.equals(versionFromStatic)) {
            // should always be the same, and we can drop classpath detection ...
            log.warn("Version detected from classpath as "+versionFromClasspath+" (preferring that), but in code it is recorded as "+versionFromStatic);
        }
    } else {
        this.version = versionFromStatic;
    }
  }
  
  public String getVersionFromClasspath() {
    return versionFromClasspath;
  }
  
  public String getVersion() {
    return version;
  }
  
  public String getVersionFromStatic() {
    return versionFromStatic;
  }

  private static boolean isValid(String v) {
    if (v==null) return false;
    if (v.equals("0.0.0") || v.equals("0.0")) return false;
    if (v.startsWith("0.0.0-") || v.startsWith("0.0-")) return false;
    return true;
  }

  private String readVersionPropertyFromClasspath(ClassLoader resourceLoader) {
    Properties versionProperties = new Properties();
    try {
      InputStream versionStream = resourceLoader.getResourceAsStream(VERSION_RESOURCE_FILE);
      if (versionStream==null) return null;
      versionProperties.load(checkNotNull(versionStream));
    } catch (IOException exception) {
      throw new IllegalStateException(format("Unable to load version resource file '%s'", VERSION_RESOURCE_FILE), exception);
    }
    return checkNotNull(versionProperties.getProperty(VERSION_PROPERTY_NAME), VERSION_PROPERTY_NAME);
  }

  public static String get() {
    return INSTANCE.version;
  }

}
