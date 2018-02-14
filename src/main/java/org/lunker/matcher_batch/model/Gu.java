package org.lunker.matcher_batch.model;

import javax.persistence.*;

/**
 * Created by dongqlee on 2018. 2. 14..
 */

@Entity
@SequenceGenerator(name = "GU_SEQ_GEN", sequenceName = "GU_SEQ", initialValue = 0, allocationSize = 1)
public class Gu {

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GU_SEQ_GEN")
    private int id;

    private String cityCode;
    @Id
    private String code;

    public Gu() {
    }

    public Gu(String cityCode, String code) {
        this.cityCode = cityCode;
        this.code = code;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
