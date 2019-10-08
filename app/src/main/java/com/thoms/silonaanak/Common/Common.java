package com.thoms.silonaanak.Common;

import com.thoms.silonaanak.Remote.IGoogleAPI;
import com.thoms.silonaanak.Remote.RetrofitClient;

public class Common {

    public static final String baseURL = "https://maps.googleapis.com";
    public static IGoogleAPI getGoogleAPI()
    {
        return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);
    }
}
