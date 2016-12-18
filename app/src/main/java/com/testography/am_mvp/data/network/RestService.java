package com.testography.am_mvp.data.network;

import com.testography.am_mvp.data.network.res.ProductRes;
import com.testography.am_mvp.utils.ConstantsManager;

import java.util.List;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import rx.Observable;

public interface RestService {
    @GET("products")
//    @GET("error/400")
    Observable<Response<List<ProductRes>>> getProductResObs
            (@Header(ConstantsManager.IF_MODIFIED_SINCE_HEADER) String
                     lastEntityUpdate);
}
