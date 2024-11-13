package com.albsig.stundenmanager.common.callbacks;

public interface ResultCallback<T> {
    void onSuccess(Result<T> response);

    void onError(Result<T> error);
}