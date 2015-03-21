package myjob;

import org.apache.batchee.jaxrs.client.BatchEEJAXRSClientFactory;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Properties;

// This won't work with 0.2-incubating
@RunWith(Arquillian.class)
public class MyJobArquillianIT {
    @ArquillianResource
    private URL url;
    private JobOperator jobOperator;

    @Deployment(testable = false)
    public static Archive<?> war() {
        final File[] files = Maven.configureResolver()
                .workOffline()
                .loadPomFromFile("pom.xml")
                .resolve("org.apache.batchee:batchee-jaxrs-server")
                .withTransitivity()
                .asFile();
        return ShrinkWrap.create(WebArchive.class)
                .addClass(HelloBatchlet.class)
                .addAsResource("META-INF/batch-jobs/myjob.xml")
                .addAsLibraries(files);
    }

    @Before
    public void before() {
        jobOperator = BatchEEJAXRSClientFactory.newClient(url.toExternalForm() + "jbatch");
    }

    @Test
    public void test() {
        Properties jobParameters = new Properties();
        jobParameters.setProperty("someKey", "someValue");
        final JobExecution jobExecution = waitForFinish(jobOperator.start("myjob", jobParameters));
        Assert.assertEquals(BatchStatus.COMPLETED, jobExecution.getBatchStatus());
    }

    private static final Collection<BatchStatus> BATCH_END_STATUSES
            = EnumSet.of(BatchStatus.COMPLETED, BatchStatus.FAILED, BatchStatus.STOPPED, BatchStatus.ABANDONED);

    private JobExecution waitForFinish(long executionId) {
        JobExecution jobExecution;
        while (!BATCH_END_STATUSES.contains((jobExecution = jobOperator.getJobExecution(executionId)).getBatchStatus())) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return jobExecution;
    }
}
