package edu.temple.tuhub;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;
import org.junit.Test;

import java.util.Map;

import edu.temple.tuhub.models.NetworkManager;
import edu.temple.tuhub.models.marketplace.Product;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by Ben on 4/29/2017.
 */

public class NetworkManagerInstrumentedTests {
    @Test
    public void requestFromEndpoint(){
        final StringBuffer sb = new StringBuffer("");
        NetworkManager networkManager = new NetworkManager();
        networkManager.requestFromEndpoint(NetworkManager.Endpoint.NEWS, null, null, null, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                assertNotNull(response);
                sb.append("done");
            }

            @Override
            public void onError(ANError anError) {
                assertEquals(1, 2);
                sb.append("done");
            }
        });

        while(!sb.toString().equals("done")){
            //wait for the network response
        }

    }

    @Test
    public void requestFromUrl(){
        final StringBuffer sb = new StringBuffer("");
        NetworkManager networkManager = new NetworkManager();
        networkManager.requestFromUrl(NetworkManager.Endpoint.NEWS.toString(), null, null, null, new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                assertNotNull(response);
                sb.append("done");
            }

            @Override
            public void onError(ANError anError) {
                assertEquals(1, 2);
                sb.append("done");
            }
        });

        while(!sb.toString().equals("done")){
            //wait for the network response
        }

    }

}
