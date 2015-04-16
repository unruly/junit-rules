package co.unruly.junit;

import co.unruly.junit.annotations.NonDeterministic;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.PrintWriter;
import java.io.StringWriter;

public class QuarantineRule implements TestRule {

    private final QuarantineRuleLogger logger;

    public QuarantineRule() {
        this.logger = new QuarantineRuleConsoleLogger();
    }

    public QuarantineRule(QuarantineRuleLogger logger) {
        this.logger = logger;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        NonDeterministic nonDeterministic = description.getAnnotation(NonDeterministic.class);

        if (nonDeterministic == null) {
            return base;
        }

        return new QuarantinedStatement(base, description, nonDeterministic.retries(), logger);
    }

    private class QuarantineRuleConsoleLogger implements QuarantineRuleLogger {
        @Override
        public void output(String message) {
            System.err.println(message);
        }
    }

    public static class QuarantinedStatement extends Statement {

        private final int maxRetries;
        private final Statement base;
        private final Description description;
        private QuarantineRuleLogger logger;

        public QuarantinedStatement(Statement base, Description description, int maxFailures, QuarantineRuleLogger logger) {
            this.base = base;
            this.description = description;
            this.maxRetries = maxFailures;
            this.logger = logger;
        }

        public void evaluate() throws Throwable {
            int failures = 0;
            Throwable lastException = null;
            try {
                for (int i = 0; i < 10; i++) {
                    try {
                        base.evaluate();
                        return;
                    } catch (Throwable e) {
                        failures++;
                        lastException = e;
                        if (failures > maxRetries) {
                            throw e;
                        }
                    }
                }
                throw new IllegalStateException();
            } finally {
                reportFailures(failures, lastException);
            }
        }

        private void reportFailures(int failures, Throwable lastException) {
            int totalTestRuns = maxRetries + 1;

            if (failures > 0 && failures < totalTestRuns) {

                StringWriter stackTraceWriter = new StringWriter();

                stackTraceWriter
                        .append("Test ")
                        .append(description.getMethodName())
                        .append(" failed ")
                        .append(Integer.toString(failures))
                        .append(" times");

                lastException.printStackTrace(new PrintWriter(stackTraceWriter));

                logger.output(stackTraceWriter.toString());
            }
        }

    }

}