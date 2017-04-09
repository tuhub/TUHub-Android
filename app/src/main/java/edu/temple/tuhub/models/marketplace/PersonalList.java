package edu.temple.tuhub.models.marketplace;

import java.util.ArrayList;

/**
 * Created by Tom on 4/9/2017.
 */

public class PersonalList {

    public String dbError = "";
    private ArrayList<Personal> personalList = new ArrayList();

    public PersonalList() {
    }

    public void addOption(Personal option) {
        this.personalList.add(option);
    }
}
