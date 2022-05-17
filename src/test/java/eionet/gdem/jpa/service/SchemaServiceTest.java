package eionet.gdem.jpa.service;

import eionet.gdem.jpa.Entities.SchemaEntry;
import eionet.gdem.jpa.repositories.SchemaRepository;
import eionet.gdem.test.ApplicationTestContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigInteger;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class SchemaServiceTest {

    @Mock
    private SchemaRepository schemaRepository;

    @InjectMocks
    private SchemaServiceImpl schemaService;

    private SchemaEntry schemaEntry;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        schemaEntry = new SchemaEntry().setSchemaId(1).setXmlSchema("http://dd.eionet.europa.eu/GetSchema?id=TBL4564").setDescription("Groundwater schema")
                .setValidate("1").setSchemaLang("XSD").setBlocker("0").setMaxExecutionTime(BigInteger.valueOf(360000));
    }

    @Test
    public void testFindById() {
        when(schemaRepository.findBySchemaId(anyInt())).thenReturn(schemaEntry);
        SchemaEntry result = schemaService.findById(1);
        assertThat(result.getXmlSchema(), is("http://dd.eionet.europa.eu/GetSchema?id=TBL4564"));
        assertThat(result.getSchemaLang(), is("XSD"));
        assertThat(result.getMaxExecutionTime(), is(BigInteger.valueOf(360000)));
    }
}






















