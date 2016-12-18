package com.testography.am_mvp.data.network.res;

import com.fernandocejas.frodo.annotation.RxLogObservable;
import com.testography.am_mvp.data.managers.DataManager;
import com.testography.am_mvp.data.network.error.ErrorUtils;
import com.testography.am_mvp.data.network.error.NetworkAvailableError;
import com.testography.am_mvp.utils.ConstantsManager;
import com.testography.am_mvp.utils.NetworkStatusChecker;

import retrofit2.Response;
import rx.Observable;

public class RestCallTransformer<R> implements Observable
        .Transformer<Response<R>, R> {

    @Override
    @RxLogObservable
    public Observable<R> call(Observable<Response<R>> responseObservable) {
        return NetworkStatusChecker.isInternetAvailable()
                .flatMap(aBoolean -> aBoolean ? responseObservable : Observable.error(new
                        NetworkAvailableError()))
                .flatMap(rResponse -> {
                    switch (rResponse.code()) {
                        case 200:
                            String lastModified = rResponse.headers().get
                                    (ConstantsManager.LAST_MODIFIED_HEADER);
                            if (lastModified != null) {
                                DataManager.getInstance().getPreferencesManager()
                                        .saveLastProductUpdate(lastModified);
                            }
                            return Observable.just(rResponse.body());
                        case 304:
                            return Observable.empty();
                        default:
                            return Observable.error(ErrorUtils.parseError(rResponse));
                    }
                });
    }
}
