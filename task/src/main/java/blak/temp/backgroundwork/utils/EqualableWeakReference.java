package blak.temp.backgroundwork.utils;

import org.apache.commons.lang3.ObjectUtils;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public class EqualableWeakReference<T> extends WeakReference<T> {
    public EqualableWeakReference(T r) {
        super(r);
    }

    public EqualableWeakReference(T r, ReferenceQueue<? super T> q) {
        super(r, q);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Reference) {
            WeakReference reference = (WeakReference) o;
            return ObjectUtils.equals(get(), reference.get());
        }
        return super.equals(o);
    }
}
