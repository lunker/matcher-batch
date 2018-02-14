package org.lunker.matcher_batch.processor;

import org.lunker.matcher_batch.model.City;
import org.springframework.batch.item.ItemProcessor;

import java.util.List;

/**
 * Created by dongqlee on 2018. 2. 14..
 */
public class CityItemProcessor implements ItemProcessor<List<City>, List<City>> {

    public List<City> process(List<City> item) throws Exception {
        return item;
    }
}
