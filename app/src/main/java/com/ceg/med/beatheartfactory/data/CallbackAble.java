package com.ceg.med.beatheartfactory.data;

public interface CallbackAble<T, I> {

    void callback(T value, I id);

}
