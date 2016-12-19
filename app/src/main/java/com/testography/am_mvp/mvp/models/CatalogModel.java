package com.testography.am_mvp.mvp.models;

import com.fernandocejas.frodo.annotation.RxLogObservable;
import com.testography.am_mvp.data.network.error.ApiError;
import com.testography.am_mvp.data.network.error.NetworkAvailableError;
import com.testography.am_mvp.data.network.res.ProductRes;
import com.testography.am_mvp.data.storage.dto.ProductDto;
import com.testography.am_mvp.data.storage.dto.ProductLocalInfo;

import java.util.List;

import rx.Observable;
import rx.subjects.BehaviorSubject;

public class CatalogModel extends AbstractModel {

    private BehaviorSubject<List<ProductDto>> mProductListObs = BehaviorSubject
            .create();

    public CatalogModel() {
        mProductListObs.onNext(getProductList());
    }

    //region ==================== Products ===================

    public Observable<List<ProductDto>> getProductListObs() {
        return mProductListObs;
    }

    public List<ProductDto> getProductList() {
        return mDataManager.getProductList();
    }

    //endregion

    public boolean isUserAuth() {
        return mDataManager.isAuthUser();
    }

    public ProductDto getProductById(int productId) {

//        return mDataManager.getProductById(productId);
        return mDataManager.getPreferencesManager().getProductById(productId);
    }

    public void updateProduct(ProductDto product) {
        mDataManager.updateProduct(product);
    }

    public Observable<ProductDto> getProductObs() {
        Observable<ProductDto> disk = fromDisk();
        Observable<ProductRes> network = fromNetwork();
        Observable<ProductLocalInfo> local = network.flatMap(productRes ->
                mDataManager.getProductLocalInfoObs(productRes));

        Observable<ProductDto> productFromNetwork =
                Observable.zip(network, local, ProductDto::new);

        return Observable.merge(disk, productFromNetwork)
                .onErrorResumeNext(throwable -> ((throwable instanceof
                        NetworkAvailableError) || (throwable instanceof ApiError))
                        ? disk : Observable.error(throwable))
                .distinct(ProductDto::getId);
    }

    @RxLogObservable
    public Observable<ProductRes> fromNetwork() {
        return mDataManager.getProductsObsFromNetwork();
    }

    @RxLogObservable
    public Observable<ProductDto> fromDisk() {
        return Observable.defer(() -> {
            List<ProductDto> diskData = mDataManager.fromDisk();
            return diskData == null ?
                    Observable.empty() :
                    Observable.from(diskData);
        });
    }

    public void updateProductLocalInfo(ProductLocalInfo pli) {
        mDataManager.getPreferencesManager().updateOrInsertLocalInfo(pli);
    }
}
