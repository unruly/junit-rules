package co.unruly.junit;

import co.unruly.junit.annotations.IgnoreUntil;
import org.joda.time.DateTime;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import java.util.regex.Pattern;

import static org.joda.time.format.DateTimeFormat.forPattern;

public class IgnoreUntilRule implements TestRule {

    private static final Pattern DATE_REGEX =     Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private static final Pattern DATETIME_REGEX = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}");

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
        DateTime annotationDate = parseDateTime(ignoreUntilDate);

        return annotationDate.isAfterNow() ? new AlwaysPassesStatement() : base;

    }

    private DateTime parseDateTime(String datetime) {
        if (DATE_REGEX.matcher(datetime).matches()) {
            return forPattern("yyyy-MM-dd").parseDateTime(datetime);
        } else if (DATETIME_REGEX.matcher(datetime).matches()) {
            return forPattern("yyyy-MM-dd'T'HH:mm:ss").parseDateTime(datetime);
        } else {
            throw new IllegalArgumentException("Please provide correct datetime pattern, one of: \nyyyy-MM-dd\nyyyy-MM-ddTHH:mm:ss");
        }
    }

    public static class AlwaysPassesStatement extends Statement {
        @Override
        public void evaluate() throws Throwable {

        }
    }

}


