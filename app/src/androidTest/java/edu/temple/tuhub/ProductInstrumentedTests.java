package edu.temple.tuhub;

import android.support.test.runner.AndroidJUnit4;

import com.androidnetworking.error.ANError;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.LinkedHashMap;

import edu.temple.tuhub.models.marketplace.Listing;
import edu.temple.tuhub.models.marketplace.Product;

import static org.junit.Assert.assertEquals;

/**
 * Created by Ben on 4/27/2017.
 */

@RunWith(AndroidJUnit4.class)
public class ProductInstrumentedTests {
    @Test
    public void insertProduct() throws Exception {
        Product product = new Product("", "UnitTestInsert", "", "3.00", "false", "tue94788", "", "");
        final StringBuffer sb = new StringBuffer("");
        product.insert(new Product.ProductRequestListener() {
            @Override
            public void onResponse(Product product) {

                assertEquals(product.getError(), "");
                sb.append("done");

            }

            @Override
            public void onError(ANError error) {
                assertEquals(2, 1);
                sb.append("done");
            }
        });

        while(!sb.toString().equals("done")){

        }

    }

    @Test
    public void updateProduct() throws Exception {
        Product product = new Product("85", "UnitTestUpdate", "DO NOT DELETE (FOR UNIT TEST)", "3.50", "false", "tue94788", "", "142");
        final StringBuffer sb = new StringBuffer("");
        product.update(new Listing.ListingUpdateListener() {
            @Override
            public void onResponse(boolean success) {
                assertEquals(true, success);
                sb.append("done");
            }

            @Override
            public void onError(Error error) {
                assertEquals(true, false);
                sb.append("done");
            }

            @Override
            public void onError(ANError anError) {
                assertEquals(true, false);
                sb.append("done");
            }
        });

        while(!sb.toString().equals("done")){

        }

    }

    @Test
    public void getPicFolderAfterInsert() {
        Product product = new Product();
        String ownerId = "tue94788";
        final StringBuffer sb = new StringBuffer("");
        product.getPicFolderAfterInsert(ownerId, new Product.ProductRequestListener() {
            @Override
            public void onResponse(Product product) {

                assertEquals(product.getError(), "");
                sb.append("done");

            }

            @Override
            public void onError(ANError error) {
                assertEquals(2, 1);
                sb.append("done");
            }
        });

        while (!sb.toString().equals("done")) {

        }
    }

    @Test
    public void fromHashMap(){
        Product product = new Product("", "UnitTestInsert", "", "3.00", "false", "tue94788", "", "");
        LinkedHashMap map = product.toHashMap();
        Product fromMap = (Product)(new Product()).fromMap(map);
        assertEquals(product.getOwnerId(), fromMap.getOwnerId());
    }

    @Test
    public void toHashMap(){
        Product product = new Product("", "UnitTestInsert", "", "3.00", "false", "tue94788", "", "");
        LinkedHashMap map = product.toHashMap();
        assertEquals(product.getOwnerId(), map.get(Listing.OWNER));
    }

}