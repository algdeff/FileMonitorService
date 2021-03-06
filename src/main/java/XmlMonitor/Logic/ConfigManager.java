package XmlMonitor.Logic;

import XmlMonitor.ServerStarter;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public final class ConfigManager {

    /**
     ConfigManager

     Property keys from config file
     default: xmlmonitor.conf.xml
     */

    private static final String CONFIG_FILE_PATH =              "xmlmonitor.conf.xml";

    private static final String INPUT_PATH =                    "monitoring_path";
    private static final String OUTPUT_PATH =                   "processed_files_path";
    private static final String INCORRECT_PATH =                "incorrect_files_path";
    private static final String LOG_FILE_PATH =                 "log_file_path_name";
    private static final String DIRECTORY_POLLING_INTERVAL =    "directory_polling_interval";
    private static final String THREAD_POOL_SIZE =              "thread_pool_size";
    private static final String TARGET_FILE_TYPE_GLOB =         "target_file_type_glob";
    private static final String ENTRY_NAME =                    "entry_name";
    private static final String ENTRY_CONTENT =                 "entry_content";
    private static final String ENTRY_DATE =                    "entry_date";

    private static final ConcurrentHashMap<String, String> _properties; // = new ConcurrentHashMap<>();

    static {
        Configurations configs = new Configurations();
        _properties = new ConcurrentHashMap<>();
        try {
            FileBasedConfigurationBuilder<XMLConfiguration> builder = configs.xmlBuilder(CONFIG_FILE_PATH);
            XMLConfiguration config = builder.getConfiguration();
            Iterator<String> iterator = config.getKeys();
            while (iterator.hasNext()) {
                String propertyName = iterator.next();
                System.err.println(propertyName + ": " + config.getString(propertyName));
                _properties.put(propertyName, config.getString(propertyName));
            }

        } catch (Exception cex) {
            System.err.println("Correct config file not found: " + CONFIG_FILE_PATH);
            ServerStarter.stopAndExit(1);
        }
    }

//    private static class SingletonInstance {
//        private static final ConfigManager INSTANCE = new ConfigManager();
//    }

    private ConfigManager() {
    }

//    public static ConfigManager getInstance() {
//        return SingletonInstance.INSTANCE;
//    }

    public static void init() {
    }

    public static Path getMonitoringPath() {
        return Paths.get(_properties.get(INPUT_PATH));
    }

    public static Path getProcessedFilesPath() {
        return Paths.get(_properties.get(OUTPUT_PATH));
    }

    public static Path getIncorrectFilesPath() {
        return Paths.get(_properties.get(INCORRECT_PATH));
    }

    public static Path getLogFilePath() {
        return Paths.get(_properties.get(LOG_FILE_PATH));
    }

    public static int getDirectoryPollingInterval() {
        return Integer.parseInt(_properties.get(DIRECTORY_POLLING_INTERVAL));
    }

    public static int getThreadPoolSize() {
        return Integer.parseInt(_properties.get(THREAD_POOL_SIZE));
    }

    public static String getTargetFileTypeGlob() {
        return _properties.get(TARGET_FILE_TYPE_GLOB);
    }

    public static String getEntryName() {
        return _properties.get(ENTRY_NAME);
    }

    public static String getEntryContent() {
        return _properties.get(ENTRY_CONTENT);
    }

    public static String getEntryDate() {
        return _properties.get(ENTRY_DATE);
    }

}