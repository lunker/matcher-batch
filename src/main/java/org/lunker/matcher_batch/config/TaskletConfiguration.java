package org.lunker.matcher_batch.config;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.lunker.matcher_batch.model.City;
import org.lunker.matcher_batch.model.Gu;
import org.lunker.matcher_batch.repository.CityRepository;
import org.lunker.matcher_batch.repository.GuRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by dongqlee on 2018. 2. 14..
 */
@Component
@EnableBatchProcessing
public class TaskletConfiguration {

    private Logger logger= LoggerFactory.getLogger(TaskletConfiguration.class);


    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private CityRepository cityRepository;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private GuRepository guRepository;

    private String endpoint = "http://openapi.epost.go.kr/postal/retrieveLotNumberAdressAreaCdService/retrieveLotNumberAdressAreaCdService";

    @Bean
    public Step load1stDepth(){
        return this.stepBuilderFactory.get("#MatcherBatch-load1stDepth").tasklet(load1stDepthTask()).build();
    }

    @Bean
    protected Tasklet load1stDepthTask() {

        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext context) {
                logger.info("#MatcherBatch-step1 Tasklet Batch Job");

                try{
                    String endpoint = "http://openapi.epost.go.kr/postal/retrieveLotNumberAdressAreaCdService/retrieveLotNumberAdressAreaCdService";
                    String requestUrl = endpoint + "/getBorodCityList";
                    String dataApiKey = System.getProperty("dataApiKey");
                    requestUrl += "?ServiceKey=" + dataApiKey;

                    List<City> list=new ArrayList<City>();

                    CloseableHttpClient client= HttpClients.createDefault();
                    HttpGet httpGet=new HttpGet(requestUrl);


                    CloseableHttpResponse response=client.execute(httpGet);
                    HttpEntity httpEntity=response.getEntity();

                    String strResponse= EntityUtils.toString(httpEntity);

                    System.out.println(strResponse);

                    EntityUtils.consume(httpEntity);

                    org.json.JSONObject xmlJSONObj = XML.toJSONObject(strResponse);
                    JSONArray array=new JSONArray();

                    array=xmlJSONObj.getJSONObject("BorodCityResponse").getJSONArray("borodCity");

                    for(int idx=0; idx<array.length(); idx++){
                        City city=City.serialize(array.getJSONObject(idx));
                        cityRepository.save(city);
                    }

                    logger.info("Finish!");
                }
                catch (IOException ioe){
                    ioe.printStackTrace();
                }

                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean
    public Step load2ndDepth(){
        return this.stepBuilderFactory.get("#MatcherBatch-load2ndDepth").tasklet(load2ndDepthTask()).build();
    }

    @Bean
    protected Tasklet load2ndDepthTask() {

        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext context) {
                logger.info("#MatcherBatch-load2ndDepth Batch Job");
                String dataApiKey = System.getProperty("dataApiKey");
                final String requestUrl=endpoint + "/getSiGunGuList" + "?ServiceKey=" + dataApiKey ;

                Stream<City> cityStream= StreamSupport.stream(cityRepository.findAll().spliterator(), false);

                cityStream.forEach((city) -> {

                    try{
                        String url=requestUrl + "&brtcCd="+ city.getName();

                        CloseableHttpClient client= HttpClients.createDefault();
                        HttpGet httpGet=new HttpGet(url);


                        CloseableHttpResponse response=client.execute(httpGet);
                        HttpEntity httpEntity=response.getEntity();

                        String strResponse= EntityUtils.toString(httpEntity);

                        org.json.JSONObject xmlJSONObj = XML.toJSONObject(strResponse);
                        logger.info("Requested URL: " + url);
                        logger.info(xmlJSONObj.toString());

                        JSONArray array=new JSONArray();

                        try{
                            array=xmlJSONObj.getJSONObject("SiGunGuListResponse").getJSONArray("siGunGuList");
                        }
                        catch (JSONException jsonE){
                            logger.warn("No sigungu in " + city.getName());
                            return ;
                        }


                        JSONObject item=null;
                        Gu gu=null;
                        List<Gu> guList=new ArrayList<>();

                        for(int idx=0; idx<array.length(); idx++){
                            item=array.getJSONObject(idx);
                            gu=new Gu(city.getName(), item.getString("signguCd"));
                            guList.add(gu);
                        }

                        guRepository.saveAll(guList);

                        EntityUtils.consume(httpEntity);
                    }
                    catch (IOException ioe){
                        ioe.printStackTrace();
                        logger.error(ioe.getMessage());
                    }
                });

                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean
    public Job updateAddress(){
        return this.jobBuilderFactory.get("#MatcherBatch-UpdateAddress").incrementer(new RunIdIncrementer()).start(load1stDepth()).build();
    }

    @Bean
    public Job load2ndDepthJob(){
        return this.jobBuilderFactory.get("#MatcherBatch-load2ndDepthJob").incrementer(new RunIdIncrementer()).start(load2ndDepth()).build();
    }


}
