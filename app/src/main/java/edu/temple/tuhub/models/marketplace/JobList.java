package edu.temple.tuhub.models.marketplace;

import java.util.ArrayList;

/**
 * Created by laurenlezberg on 4/9/17.
 */

public class JobList {
    public String dbError = "";
    private ArrayList<Personal> jobList = new ArrayList();

    public JobList() {
    }

    public void addOption(Personal option) {
        this.jobList.add(option);
    }
}
