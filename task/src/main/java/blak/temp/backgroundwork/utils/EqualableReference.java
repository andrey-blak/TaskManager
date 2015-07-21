package blak.temp.backgroundwork.utils;

import org.apache.commons.lang3.ObjectUtils;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public class EqualableReference<T> extends WeakReference<T> {
    public EqualableReference(T referent) {
        super(referent);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Reference) {
            Reference reference = (Reference) o;
            return ObjectUtils.equals(get(), reference.get());
        }
        return super.equals(o);
    }
}
