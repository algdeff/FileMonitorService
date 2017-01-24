package XmlMonitor.Logic;

import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
    private static final String RESULT_FILE =                   "result_file_path_name";
    private static final String DIRECTORY_POLLING_INTERVAL =    "directory_polling_interval";
    private static final String THREAD_POOL_SIZE =              "thread_pool_size";
    private static final String TARGET_FILE_TYPE_GLOB =         "target_file_type_glob";
    private static final String ENTRY_NAME =                    "entry_name";
    private static final String ENTRY_CONTENT =                 "entry_content";
    private static final String ENTRY_DATE =                    "entry_date";

    private static volatile ConfigManager _instance;
    private static boolean _inited = false;

    private ConcurrentHashMap<String, String> _properties;

    private ConfigManager() {
        _properties = new ConcurrentHashMap<>();
    }

    public static synchronized ConfigManager getInstance() {
        if (_instance == null) {
            synchronized (ConfigManager.class) {
                if (_instance == null) {
                    _instance = new ConfigManager();
                }
            }
        }
        return _instance;
    }

    public void init() {
        if (_inited) {
            return;
        }

        Configurations configs = new Configurations();
        try {
            FileBasedConfigurationBuilder<XMLConfiguration> builder = configs.xmlBuilder(CONFIG_FILE_PATH);
            XMLConfiguration config = builder.getConfiguration();
            Iterator<String> iterator = config.getKeys();
            while (iterator.hasNext()) {
                String propertyName = iterator.next();
                System.out.println(propertyName + " = " + config.getString(propertyName));
                _properties.put(propertyName, config.getString(propertyName));
            }

        } catch (Exception cex) {
            System.err.println("Correct config file not found: " + CONFIG_FILE_PATH);
        }

        _inited = true;
    }

    public Path getMonitoringPath() {
        return Paths.get(_properties.get(INPUT_PATH));
    }

    public Path getProcessedFilesPath() {
        return Paths.get(_properties.get(OUTPUT_PATH));
    }

    public Path getIncorrectFilesPath() {
        return Paths.get(_properties.get(INCORRECT_PATH));
    }

    public Path getResultFilePath() {
        return Paths.get(_properties.get(RESULT_FILE));
    }

    public int getDirectoryPollingInterval() {
        return Integer.parseInt(_properties.get(DIRECTORY_POLLING_INTERVAL));
    }

    public int getThreadPoolSize() {
        return Integer.parseInt(_properties.get(THREAD_POOL_SIZE));
    }

    public String getTargetFileTypeGlob() {
        return _properties.get(TARGET_FILE_TYPE_GLOB);
    }

    public String getEntryName() {
        return _properties.get(ENTRY_NAME);
    }

    public String getEntryContent() {
        return _properties.get(ENTRY_CONTENT);
    }

    public String getEntryDate() {
        return _properties.get(ENTRY_DATE);
    }

}
