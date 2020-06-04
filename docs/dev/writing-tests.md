# Testing with JUnit 5

## Writing Service tests

To test services and repositories, write a test class inheriting from `AtlasDatabaseTestBase` in the `org.planqk.atlas.core` the services you want to use can be requested using Springs dependency injection for example by using the `@Autowired` annotation on a attribute in the same way it can be done in services or controllers. These tests are considered integration tests, since they get executed on a real PostgreSQL database.

Such a test may look like this, for example:

```java
public class DatabaseTest extends AtlasDatabaseTestBase {

    @Autowired
    private TagRepository repository;

    @Test
    void modelLoads() {
        var inputTag = new Tag();
        inputTag.setKey("Test");
        inputTag.setValue("test-value");
        var t = repository.save(inputTag);
        assertNotNull(t.getId());

        var outputTag = repository.findById(t.getId());
        assertTrue(outputTag.isPresent());
        var ot = outputTag.orElseThrow();
        assertEquals(t.getId(), ot.getId());
        assertEquals(t.getKey(), ot.getKey());
        assertEquals(t.getValue(), ot.getValue());
    }
}
```

If you inherit from the `AtlasDatabaseTestBase` class a test will only be executed if you have a local PostgreSQL instance.

Please be aware that running these tests locally, when PostgreSQL is available, all your previous entries in the database will be dropped.

## Writing Controller tests

### Injecting assemblers and other unmocked dependencies

While controller tests are considered unit tests some components, like the Assemblers, might be injected in their real form instead of a mocked one. This can be done by defining a `@TestConfiguration` as a static child class. This class then has to define methods with `@Bean` annotations to inject the dependencies. For example:

```java
@WebMvcTest(controllers = {CloudServiceController.class})
@ExtendWith({MockitoExtension.class})
@AutoConfigureMockMvc
public class CloudServiceControllerTest {

    @TestConfiguration
    public static class TestConfig {
        @Bean
        public CloudServiceAssembler getCloudServiceAssembler() {
            return new CloudServiceAssembler();
        }

        @Bean
        public PagedResourcesAssembler<CloudServiceDto> getPagedResourcesAssembler() {
            return new PagedResourcesAssembler<>(null, null);
        }
    }
    @MockBean
    private CloudServiceService cloudServiceService;

    @Autowired
    private MockMvc mockMvc;
...
}
```

This example also shows the three annotations mandatory to define a WebMvc Test:

- `@WebMvcTest(controllers = {<ControllerToTest>.class})`: Identifies this test as a WebMvcTest for the controllers defined within the annotation. Multiple ones can be used here.
- `@ExtendWith({MockitoExtension.class})`: Loads the MockitoExtension which allows the usage of `@MockBean` to inject mocked dependencies in the controller
- `@AutoConfigureMockMvc`: Automatically configures a instance of `MockMvc` which can be retrieved using Springs dependency injection. 

### Unmarshalling (deserializing) Objects

Since HATEOAS uses a different schema internally, compared to what is exposed using HTTP, as shown [here](#difference-between-internal-and-exposed-hateoas-represenations). Unmarshalling a response of an object may fail to prevent this a special implementation of the object mapper should be used. This one can be obtained using `ObjectMapperUtils.newTestMapper()`. An example how the mapper can be initialized is shown below:


```java
private ObjectMapper mapper;

@BeforeEach
public void init() {
   mapper = ObjectMapperUtils.newTestMapper();
}
```

The following sections present an approach on how to validate `_links` and `PagedModels` (`_embedded`), since these sections cannot be verified using the customized `ObjectMapper`.

#### Getting the objects from a `PagedModel`

If you want to verify the contents of a PagedModel you can use a small helper method in the `ObjectMapperUtils` class: `ObjectMapperUtils.mapResponseToList()`. To retrieve a list of DTOs, just pass the response as a string, returned from MockMvc, as well as the key under which the resources are located. The key in the case, the class name ends with DTO usually is `<ClassName>es` where the class name is written in Camel case. For example: `cloudServiceDtoes` is the key for Cloud service objects. Apart from these two values you must pass the an Instance of the `Class` object for the type of the elements. This can be obtained by calling `<ClassName>.class`

Also an optional instance of the ObjectMapper may be passed to the method, however this mapper must be configured to not fail on unknown properties. By default a mapper is built by calling the `newTestMapper()` method.

#### Verifying `_links`

Verifying the links of an object is not possible using the previous two methods. Verifying links can be done using JsonPath or Json objects, as the example below shows:

```java
var responseObject = new JSONObject(result.getResponse().getContentAsString());
var linkArray = responseObject.getJSONObject("_links");
assertEquals(4, linkArray.length());
assertTrue(linkArray.has("self"));
assertTrue(linkArray.has(Constants.ALGORITHMS));
assertTrue(linkArray.has(Constants.PROVIDERS));
assertTrue(linkArray.has(Constants.TAGS));
assertFalse(linkArray.has("randomLink"));
```

## Difference between internal and exposed HATEOAS represenations

Internal representation of links:

```json
{
   "links": [
      {
         "rel": "self",
         "href": "http:\/\/localhost\/"
      },
      {
         "rel": "algorithms",
         "href": "http:\/\/localhost\/algorithms\/?page=0&size=50"
      },
      {
         "rel": "providers",
         "href": "http:\/\/localhost\/providers\/?page=0&size=50"
      },
      {
         "rel": "tags",
         "href": "http:\/\/localhost\/tags\/?page=0&size=50"
      }
   ]
}
```

External Representation of links:

```json
{
  "_links": {
    "algorithms": {
      "href": "http:\/\/localhost\/algorithms\/?page=0&size=50"
    },
    "self": {
      "href": "http:\/\/localhost\/"
    },
    "providers": {
      "href": "http:\/\/localhost\/providers\/?page=0&size=50"
    },
    "tags": {
      "href": "http:\/\/localhost\/tags\/?page=0&size=50"
    }
  }
}
```