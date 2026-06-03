package hospital.db;

import java.util.List;

public abstract class BaseDAO<T> {
    public abstract List<T> getAll();
    public abstract List<T> search(String keyword);
    public abstract boolean add(T entity);
    public abstract boolean update(T entity);
    public abstract boolean delete(int id);

    protected void notifyChanged(String source) {
        DataChangeBus.getInstance().fireChanged(source);
    }
}
