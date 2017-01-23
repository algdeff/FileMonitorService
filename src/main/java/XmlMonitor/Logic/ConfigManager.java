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

public final class ConfigManager {

    /**
     ConfigManager

     Property keys from config file
     default: server.conf.xml
     */

    private static final String CONFIG_FILE_PATH =              "config/server.conf.xml";

    private static final String INPUT_FOLDER =                  "monitoring_folder_path";
    private static final String OUTPUT_FOLDER =                 "processed_folder_path";
    private static final String WARNING_FOLDER =                "warning_folder_path";
    private static final String RESULT_FILE =                   "result_file_path_name";
    private static final String DIRECTORY_POLLING_INTERVAL =    "directory_polling_interval";
    private static final String THREAD_POOL_SIZE =              "thread_pool_size";

    private static final String TARGET_FILE_TYPE_GLOB =         "target_file_type_glob";


    private static volatile ConfigManager _instance;
    private static boolean _inited = false;

    private Map<String, String> _properties;

    private ConfigManager() {
        _properties = new HashMap<>();
    }

    public static synchronized ConfigManager getInstance() {
        if (_instance == null) {
            _instance = new ConfigManager();
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
//            config.addProperty("input_folder_path", "newValue");
//            config.addProperty("output_folder_path", "newValue222");
//            builder.save();

            Iterator<String> iterator = config.getKeys();
            while (iterator.hasNext()) {
                String propertyName = iterator.next();
                System.out.println(propertyName + "/" + config.getString(propertyName));
                _properties.put(propertyName, config.getString(propertyName));
            }

        } catch (ConfigurationException cex) {
            // Something went wrong
        }

        _inited = true;
    }

    public Path getInputFolder() {
        return Paths.get(_properties.get(INPUT_FOLDER));
    }

    public Path getOutputFolder() {
        return Paths.get(_properties.get(OUTPUT_FOLDER));
    }

    public Path getWarningFolder() {
        return Paths.get(_properties.get(WARNING_FOLDER));
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
}
