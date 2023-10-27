import jakarta.servlet.http.HttpSession;

class SessionAttribute<T>{
        private final Class<T> clazz;
        private final String name;
        SessionAttribute (Class<T> clazz, String name) {
            this.name = name;
            this.clazz = clazz;
        }
        T get (HttpSession session) {
        return clazz.cast (session.getAttribute (name) ) ;
        }
}