package org.lunker.matcher_batch.model;

/**
 * Created by dongqlee on 2018. 2. 14..
 */


import org.json.JSONObject;

import javax.persistence.*;

/**
 * Created by dongqlee on 2018. 2. 11..
 */

@Entity
@SequenceGenerator(name = "CITY_SEQ_GEN", sequenceName = "CITY_SEQ", initialValue = 0, allocationSize = 1)
public class City {
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CITY_SEQ_GEN")
    private int id;

    @Id
    private String code;
    private String name;

    public City() {
    }

    public City(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static City serialize(JSONObject object){
        City city=new City();
        city.setCode(object.getString(OAConstants.CITY_CODE));
        city.setName(object.getString(OAConstants.CITY_NAME));

        return city;
    }
}


