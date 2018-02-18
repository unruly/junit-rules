package co.unruly.junit;

import co.unruly.junit.annotations.IgnoreUntil;
import org.joda.time.DateTime;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import static org.joda.time.format.DateTimeFormat.forPattern;

public class IgnoreUntilRule implements TestRule {

    static final String DEFAULT_IGNORE = "2000-01-01";

    @Override
    public Statement apply(Statement base, Description description) {

        IgnoreUntil ignoreUntil = description.getAnnotation(IgnoreUntil.class);

        if (ignoreUntil != null && ignoreUntil.value().equals(DEFAULT_IGNORE) && description.getTestClass() != null) {
            ignoreUntil = description.getTestClass().getAnnotation(IgnoreUntil.class);
        }

        if (ignoreUntil == null) {
            return base;
        }

        String ignoreUntilDate = ignoreUntil.value();

        DateTime annotationDate = forPattern("yyyy-MM-dd").parseDateTime(ignoreUntilDate);

        return annotationDate.isAfterNow() ? new AlwaysPassesStatement() : base;

    }

    public static class AlwaysPassesStatement extends Statement {
        @Override
        public void evaluate() throws Throwable {

        }
    }

}


