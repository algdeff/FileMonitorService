package XmlMonitor;

import XmlMonitor.Logic.ConfigManager;
import XmlMonitor.Logic.ThreadPoolManager;
import XmlMonitor.Logic.FileSystemMonitor;
import XmlMonitor.Logic.Workers.LogFileWorker;
import XmlMonitor.Logic.db.DatabaseManager;

public class ServerStarter {

    private static String _serverName = "XmlMonitorService";
    private static ServerStarter _instance;

    private FileSystemMonitor _fileSystemMonitor;

    private ServerStarter() {
    }

    public static void main(String[] args) {

        //InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());

        try {
            _instance = new ServerStarter();
            _instance.start();

        } catch (Throwable ex) {
            //ConsoleUtils.tprintf("Failed start server: %s", ex.getMessage());
            ex.printStackTrace(System.err);
            stopServerInstance();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            //_timer.cancel();
            stopServerInstance();
            //LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
            //lc.stop();
        }));
    }


    private void start() {

        Facade.getInstance().init();
        ConfigManager.init();
        DatabaseManager.getInstance().init();
        ThreadPoolManager.getInstance()
                .init(ConfigManager.getThreadPoolSize());

        _fileSystemMonitor = new FileSystemMonitor();
        _fileSystemMonitor.start();

        LogFileWorker.getInstance().init();
    }

    private void stop() {

        System.err.println("Server STOP");
//        ThreadPoolManager.reset();
//        Facade.shutdown();
//        DatabaseManager.closeAll();
    }

    private static void stopServerInstance() {
        if (_instance != null) {
            final ServerStarter serverStarter = _instance;
            _instance = null;
            serverStarter.stop();
        }
    }

    public static void stopAndExit(int status) {
        stopServerInstance();
        System.exit(status);
    }

    public static String getServerName() {
        return _serverName;
    }
}
