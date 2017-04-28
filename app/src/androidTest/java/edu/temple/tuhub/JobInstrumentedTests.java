package edu.temple.tuhub;

import android.util.Log;

import com.androidnetworking.error.ANError;

import org.junit.Test;

import java.util.LinkedHashMap;

import edu.temple.tuhub.models.marketplace.Job;
import edu.temple.tuhub.models.marketplace.Listing;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Ben on 4/27/2017.
 */

public class JobInstrumentedTests {
    @Test
    public void insertJob() throws Exception {
        Job job = new Job("", "UnitTestInsert", "", "5", "", "unit test", "5", "false", "tue94788", "", "");
        final StringBuffer sb = new StringBuffer("");
        job.insert(new Job.JobRequestListener() {
            @Override
            public void onResponse(Job job) {

                assertEquals(job.getError(), "");
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
    public void updateJob() throws Exception {
        Job job = new Job("25", "UnitTestUpdate", "DO NOT DELETE", "6", "05-05-2017", "unit test", "5", "false", "tue94788", "", "159");
        final StringBuffer sb = new StringBuffer("");
        job.update(new Listing.ListingUpdateListener() {
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
        Job job = new Job();
        String ownerId = "tue94788";
        final StringBuffer sb = new StringBuffer("");
        job.getPicFolderAfterInsert(ownerId, new Job.JobRequestListener() {
            @Override
            public void onResponse(Job job) {

                assertEquals(job.getError(), "");
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
        Job job = new Job("", "UnitTestUpdate", "DO NOT DELETE", "6", "", "unit test", "5", "false", "tue94788", "", "");
        LinkedHashMap map = job.toHashMap();
        Job fromMap = (Job)(new Job()).fromMap(map);
        assertEquals(job.getOwnerId(), fromMap.getOwnerId());
    }

    @Test
    public void toHashMap(){
        Job job = new Job("", "UnitTestUpdate", "DO NOT DELETE", "6", "", "unit test", "5", "false", "tue94788", "", "");
        LinkedHashMap map = job.toHashMap();
        assertEquals(job.getOwnerId(), map.get(Listing.OWNER));
    }
}
