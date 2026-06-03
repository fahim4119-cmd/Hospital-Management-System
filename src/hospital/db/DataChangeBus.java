package hospital.db;

import java.util.ArrayList;
import java.util.List;

public class DataChangeBus {
    public interface DataChangeListener {
        void onDataChanged(String source);
    }

    private static final DataChangeBus INSTANCE = new DataChangeBus();
    private final List<DataChangeListener> listeners = new ArrayList<>();

    private DataChangeBus() {}

    public static DataChangeBus getInstance() {
        return INSTANCE;
    }

    public synchronized void addListener(DataChangeListener listener) {
        listeners.add(listener);
    }

    public synchronized void removeListener(DataChangeListener listener) {
        listeners.remove(listener);
    }

    public void fireChanged(String source) {
        List<DataChangeListener> snapshot;
        synchronized (this) {
            snapshot = new ArrayList<>(listeners);
        }
        for (DataChangeListener listener : snapshot) {
            listener.onDataChanged(source);
        }
    }
}
