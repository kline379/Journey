package backend;



public class Lazy<T> {

    public interface Init<E> {
        public E Initialize() throws Exception;
    }

    private Init<T> _InitFunc;
    private Object _Lock = new Object();
    private T _Instance = null;

    public Lazy(Init<T> init) {
        _InitFunc = init;
    }

    public T get() {
        if(_Instance == null) {
            synchronized(_Lock) {
                _Instance = _InitFunc.Initialize();
            }
        }
        return _Instance;
    }

    public Boolean Initialized() {
        return _Instance != null;
    }

    public void SetInit(Init<T> init) {
        _InitFunc = init;
    }

    public Lazy<T> Set() {
        _Instance = _InitFunc.Initialize();
        return this;
    }
} 
