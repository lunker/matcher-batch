package org.lunker.matcher_batch.repository;

import org.lunker.matcher_batch.model.City;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by dongqlee on 2018. 2. 14..
 */
@Repository
public interface CityRepository extends CrudRepository<City, Long>{
}
