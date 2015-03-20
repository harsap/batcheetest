package myjob;

import org.apache.batchee.jaxrs.client.BatchEEJAXRSClientFactory;
import org.junit.Assert;
import org.junit.Test;

import javax.batch.operations.JobOperator;
import javax.batch.runtime.JobExecution;
import java.util.Properties;

public class MyJobIT {

    @Test
    public void test() {
        final JobOperator jobOperator = BatchEEJAXRSClientFactory.newClient("http://localhost:8080/batcheetest/jbatch");
        Properties jobParameters = new Properties();
        jobParameters.setProperty("someKey", "someValue");
        final long executionId = jobOperator.start("myjob", jobParameters);
        final JobExecution jobExecution = jobOperator.getJobExecution(executionId);
        System.out.println(jobExecution);
        Assert.assertEquals(jobParameters, jobExecution.getJobParameters());
        Assert.assertTrue(executionId > 0);
    }
}
