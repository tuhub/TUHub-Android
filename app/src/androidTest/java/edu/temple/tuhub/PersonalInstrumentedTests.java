package edu.temple.tuhub;

import com.androidnetworking.error.ANError;

import org.junit.Test;

import java.util.LinkedHashMap;

import edu.temple.tuhub.models.marketplace.Listing;
import edu.temple.tuhub.models.marketplace.Personal;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Ben on 4/27/2017.
 */

public class PersonalInstrumentedTests {
    @Test
    public void insertPersonal() throws Exception {
        Personal personal = new Personal("", "UnitTestInsert", "", "unit test", "false", "tue94788", "", "");
        final StringBuffer sb = new StringBuffer("");
        personal.insert(new Personal.PersonalRequestListener() {
            @Override
            public void onResponse(Personal personal) {

                assertEquals(personal.getError(), "");
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
    public void updatePersonal() throws Exception {
        Personal personal = new Personal("25", "UnitTestUpdate", "DO NOT DELETE", "DO NOT DELETE", "false", "tue94788", "", "156");
        final StringBuffer sb = new StringBuffer("");
        personal.update(new Listing.ListingUpdateListener() {
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
        Personal personal = new Personal();
        String ownerId = "tue94788";
        final StringBuffer sb = new StringBuffer("");
        personal.getPicFolderAfterInsert(ownerId, new Personal.PersonalRequestListener() {
            @Override
            public void onResponse(Personal personal) {

                assertEquals(personal.getError(), "");
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
        Personal personal = new Personal("", "UnitTestInsert", "", "unit test", "false", "tue94788", "", "");
        LinkedHashMap map = personal.toHashMap();
        Personal fromMap = (Personal)(new Personal()).fromMap(map);
        assertEquals(personal.getOwnerId(), fromMap.getOwnerId());
    }

    @Test
    public void toHashMap(){
        Personal personal = new Personal("", "UnitTestInsert", "", "unit test", "false", "tue94788", "", "");
        LinkedHashMap map = personal.toHashMap();
        assertEquals(personal.getOwnerId(), map.get(Listing.OWNER));
    }
}
