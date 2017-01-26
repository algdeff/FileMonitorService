package XmlMonitor;

public final class Facade {

    public static final String EVENT_CREATE_ENTRY = "created_new_file";

    private static volatile Facade _instance;
    private static boolean _inited = false;

    private Facade() {
    }

    public static Facade getInstance() {
        if (_instance == null) {
            synchronized (Facade.class) {
                if (_instance == null) {
                    _instance = new Facade();
                }
            }
        }
        return _instance;
    }

    void init() {
        if (_inited) {
            return;
        }


        _inited = true;
    }


}
