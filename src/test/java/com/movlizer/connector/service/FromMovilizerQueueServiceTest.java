package com.movlizer.connector.service;

import com.movilitas.movilizer.v14.MovilizerGenericDataContainer;
import com.movilitas.movilizer.v14.MovilizerGenericDataContainerEntry;
import com.movilitas.movilizer.v14.MovilizerGenericUploadDataContainer;
import com.movilitas.movilizer.v14.MovilizerUploadDataContainer;
import com.movilizer.connector.persistence.entities.DatacontainerFromMovilizerQueue;
import com.movilizer.connector.service.queues.DCFromQueueService;
import com.movlizer.connector.config.MovilizerV12TestConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.datatype.DatatypeFactory;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Tests for queries retrieving movilizer users.
 *
 * @author Jesús de Mula Cano <jesus.demula@movilizer.com>
 * @version 0.1-SNAPSHOT, 2014.11.10
 * @since 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {MovilizerV12TestConfig.class})
public class FromMovilizerQueueServiceTest {

    @Autowired
    private DCFromQueueService service;

    @Before
    public void before() throws Exception {

    }

    @After
    public void after() {
    }

    @Test
    public void testAddOneDatacontainer() throws Exception {

        //Plain data
        Short priority = new Short("1");

        String key = "key";
        String moveletKey = "com.example.movelet.key";
        String moveletKeyExtension = "";
        Long moveletVersion = 1L;
        String participantKey = "123456";
        String deviceAddress = "@john@example.com";
        String creationTimestamp = "2001-07-06T12:08:56.435Z";
        String syncTimestamp = "2001-07-10T12:08:56.335Z";
        Integer timedif = 0;
        String encryptionAlgorithm = "";
        String encryptionIV = "";
        String encryptionHMAC = "";

        //Setup object data
        MovilizerUploadDataContainer dataContainer = new MovilizerUploadDataContainer();
        dataContainer.setContainerUploadPriority(priority);
        MovilizerGenericUploadDataContainer genericUploadDataContainer = new MovilizerGenericUploadDataContainer();
        genericUploadDataContainer.setKey(key);
        genericUploadDataContainer.setMoveletKey(moveletKey);
        genericUploadDataContainer.setMoveletKeyExtension(moveletKeyExtension);
        genericUploadDataContainer.setMoveletVersion(moveletVersion);
        genericUploadDataContainer.setParticipantKey(participantKey);
        genericUploadDataContainer.setDeviceAddress(deviceAddress);
        genericUploadDataContainer.setCreationTimestamp(DatatypeFactory.newInstance().newXMLGregorianCalendar(
                creationTimestamp));
        genericUploadDataContainer.setSyncTimestamp(DatatypeFactory.newInstance().newXMLGregorianCalendar(
                syncTimestamp));
        genericUploadDataContainer.setTimedif(timedif);
        genericUploadDataContainer.setEncryptionAlgorithm(encryptionAlgorithm);
        genericUploadDataContainer.setEncryptionIV(encryptionIV);
        genericUploadDataContainer.setEncryptionHMAC(encryptionHMAC);

        MovilizerGenericDataContainer data = new MovilizerGenericDataContainer();
        List<MovilizerGenericDataContainerEntry> entryList = data.getEntry();

        MovilizerGenericDataContainerEntry entryChild = new MovilizerGenericDataContainerEntry();
        entryChild.setName("child");
        entryChild.setValstr("childValue");
        MovilizerGenericDataContainerEntry entryParent = new MovilizerGenericDataContainerEntry();
        entryParent.setName("parent");
        entryParent.setValstr("parentValue");
        entryParent.getEntry().add(entryChild);
        MovilizerGenericDataContainerEntry entryUncle = new MovilizerGenericDataContainerEntry();
        entryUncle.setName("uncle");
        entryUncle.setValstr("uncleValue");

        entryList.add(entryParent);
        entryList.add(entryUncle);
        genericUploadDataContainer.setData(data);
        dataContainer.setContainer(genericUploadDataContainer);

        //Insert in queue
        DatacontainerFromMovilizerQueue in = new DatacontainerFromMovilizerQueue(dataContainer);
        service.offer(in);

        List<DatacontainerFromMovilizerQueue> out = service.getAllOrdered();

        assertThat(out.isEmpty(), is(false));
        assertThat(out.get(0), is(in));
        assertThat(out.get(0).getDatacontainer(), is(not(nullValue())));
        assertThat(out.get(0).getDatacontainer().getContainer().getKey(), is(key));
    }
}
