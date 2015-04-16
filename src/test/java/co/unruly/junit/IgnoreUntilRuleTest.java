package co.unruly.junit;

import co.unruly.junit.annotations.IgnoreUntil;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IgnoreUntilRuleTest {

    @Mock Statement mockStatement;
    @Mock IgnoreUntil mockIgnoreUntilAnnotation;
    @Mock Description mockDescription;

    @InjectMocks
    IgnoreUntilRule rule = new IgnoreUntilRule();

    @Test
    public void shouldReturnOriginalStatementIfAnnotationNotPresent() {
        when(mockDescription.getAnnotation(IgnoreUntil.class)).thenReturn(null);

        assertEquals(mockStatement, rule.apply(mockStatement, mockDescription));
    }

    @Test
    public void shouldReturnOriginalStatementIfIgnoreUntilDateIsInThePast() {
        String twoDaysAgo = new DateTime().minusDays(2).toString("yyyy-MM-dd");

        when(mockDescription.getAnnotation(IgnoreUntil.class)).thenReturn(mockIgnoreUntilAnnotation);
        when(mockIgnoreUntilAnnotation.value()).thenReturn(twoDaysAgo);

        assertEquals(mockStatement, rule.apply(mockStatement, mockDescription));
    }

    @Test
    public void shouldReturnAlwaysPassesStatementIfIgnoreUntilDateIsInTheFuture() {
        String tomorrow = new DateTime().plusDays(1).toString("yyyy-MM-dd");

        when(mockDescription.getAnnotation(IgnoreUntil.class)).thenReturn(mockIgnoreUntilAnnotation);
        when(mockIgnoreUntilAnnotation.value()).thenReturn(tomorrow);

        assertTrue(rule.apply(mockStatement, mockDescription) instanceof IgnoreUntilRule.AlwaysPassesStatement);
    }
}
