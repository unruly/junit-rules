package co.unruly.junit;

import co.unruly.junit.annotations.IgnoreUntil;
import org.joda.time.DateTime;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import static org.joda.time.format.DateTimeFormat.forPattern;

public class IgnoreUntilRule implements TestRule {

    @Override
    public Statement apply(Statement base, Description description) {

        IgnoreUntil ignoreUntil = description.getAnnotation(IgnoreUntil.class);

        if (ignoreUntil != null && description.getTestClass() != null) {
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


