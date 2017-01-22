package XmlMonitor;

public final class Facade {

    public static final String EVENT_CREATE_ENTRY = "created_new_file";

    private static volatile Facade instance;
    private static boolean _inited = false;

    private Facade() {
    }

    public static synchronized Facade getInstance() {
        if (instance == null) {
            instance = new Facade();
        }
        return instance;
    }

    void init() {
        if (_inited) {
            return;
        }


        _inited = true;
    }


}
