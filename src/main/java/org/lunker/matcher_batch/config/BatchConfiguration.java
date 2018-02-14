package org.lunker.matcher_batch.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Configuration;

/**
 * Created by dongqlee on 2018. 2. 14..
 */
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    /*
    private Logger logger= LoggerFactory.getLogger(BatchConfiguration.class);
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

    // tag::readerwriterprocessor[]
    @Bean
    public ItemReader<List<City>> reader() {

        ItemReader<List<City>> reader=new ItemReader<List<City>>() {
            public List<City> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

                logger.info("On read!");

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
                return list;
            }
        };

        return reader;
    }

    @Bean
    public CityItemProcessor processor() {
        return new CityItemProcessor();
    }

    @Bean
    public ItemWriter<City> writer() {

//        JdbcBatchItemWriter<City> writer = new JdbcBatchItemWriter<City>();
//        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<City>());
//        writer.setSql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)");
//        writer.setDataSource(dataSource);

        ItemWriter<City> writer=new ItemWriter<City>() {
            public void write(List<? extends City> items) throws Exception {
                items.stream().forEach((city)->{
                    cityRepository.save(city);
                });

            }
        };
//        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }
    // end::readerwriterprocessor[]

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<List<City>, List<City>> chunk(10)
                .reader(reader()) // read
                .processor(processor()) // process
                .writer(writer()) // save
                .build();
    }

    @Bean
    public Job getChunkBatchJob() throws Exception {
        return this.jobBuilderFactory.get("#matcher-batch").start(step1()).build();
    }

    // end::jobstep[]
    */

}
