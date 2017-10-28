package backend;

import java.util.function.*;

public class Lazy<T> {

    private Supplier<T> _InitFunc;
    private Object _Lock = new Object();
    private T _Instance = null;

    public Lazy(Supplier<T> init) {
        _InitFunc = init;
    }

    public T get() throws Exception {
        if(_Instance == null) {
            synchronized(_Lock) {
                _Instance = _InitFunc.get();
            }
        }
        return _Instance;
    }

    public Boolean Initialized() {
        return _Instance != null;
    }

    public void SetInit(Supplier<T> init) {
        _InitFunc = init;
    }

    public Lazy<T> Set() throws Exception {
        _Instance = _InitFunc.get();
        return this;
    }
} 
