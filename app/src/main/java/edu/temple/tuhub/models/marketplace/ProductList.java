package edu.temple.tuhub.models.marketplace;

import java.util.ArrayList;

/**
 * Created by Ben on 4/5/2017.
 */

public class ProductList {
    public String dbError = "";
    private ArrayList<Product> productList = new ArrayList();

    public ProductList() {
    }

    public void addOption(Product option) {
        this.productList.add(option);
    }
}