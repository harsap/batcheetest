package myjob;

import javax.batch.api.AbstractBatchlet;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.context.JobContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@Dependent
public class HelloBatchlet extends AbstractBatchlet {
    @Inject
    JobContext jobContext;

    @Override
    public String process() throws Exception {
        System.out.println("Hello world!");
        System.out.println("Job Parameters: " + BatchRuntime.getJobOperator().getJobExecution(jobContext.getExecutionId()).getJobParameters());
        return null;
    }
}
