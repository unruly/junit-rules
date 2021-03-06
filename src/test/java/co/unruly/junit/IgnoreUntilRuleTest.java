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

import java.lang.annotation.Annotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IgnoreUntilRuleTest {

    @Mock Statement mockStatement;
    @Mock IgnoreUntil mockIgnoreUntilAnnotation;
    @Mock Description mockDescription;
    private final static String DATE_PATTERN = "yyyy-MM-dd";
    private final static String DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    @InjectMocks
    IgnoreUntilRule rule = new IgnoreUntilRule();

    @Test
    public void shouldReturnOriginalStatementIfAnnotationNotPresent() {
        when(mockDescription.getAnnotation(IgnoreUntil.class)).thenReturn(null);
        assertEquals(mockStatement, rule.apply(mockStatement, mockDescription));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfDateTimeFormatInvalid() {
        when(mockDescription.getAnnotation(IgnoreUntil.class)).thenReturn(mockIgnoreUntilAnnotation);
        when(mockIgnoreUntilAnnotation.value()).thenReturn("invalid");

        rule.apply(mockStatement, mockDescription);
    }

    @Test
    public void shouldReturnOriginalStatementIfIgnoreUntilDateTimeIsInThePast() {
        String oneSecondBefore = new DateTime().minusSeconds(1).toString(DATETIME_PATTERN);

        when(mockDescription.getAnnotation(IgnoreUntil.class)).thenReturn(mockIgnoreUntilAnnotation);
        when(mockIgnoreUntilAnnotation.value()).thenReturn(oneSecondBefore);

        assertEquals(mockStatement, rule.apply(mockStatement, mockDescription));
    }

    @Test
    public void shouldReturnAlwaysPassesStatementIfIgnoreUntilDateTimeIsInTheFuture() {
        String oneSecondLater = new DateTime().plusSeconds(1).toString(DATETIME_PATTERN);

        when(mockDescription.getAnnotation(IgnoreUntil.class)).thenReturn(mockIgnoreUntilAnnotation);
        when(mockIgnoreUntilAnnotation.value()).thenReturn(oneSecondLater);

        assertTrue(rule.apply(mockStatement, mockDescription) instanceof IgnoreUntilRule.AlwaysPassesStatement);
    }

    @Test
    public void shouldReturnOriginalStatementIfIgnoreUntilDateIsInThePast() {
        String twoDaysAgo = new DateTime().minusDays(2).toString(DATE_PATTERN);

        when(mockDescription.getAnnotation(IgnoreUntil.class)).thenReturn(mockIgnoreUntilAnnotation);
        when(mockIgnoreUntilAnnotation.value()).thenReturn(twoDaysAgo);

        assertEquals(mockStatement, rule.apply(mockStatement, mockDescription));
    }

    @Test
    public void shouldReturnOriginalStatementIfIgnoreUntilDateSameAsCurrentDate() {
        String sameDay = new DateTime().toString(DATE_PATTERN);

        when(mockDescription.getAnnotation(IgnoreUntil.class)).thenReturn(mockIgnoreUntilAnnotation);
        when(mockIgnoreUntilAnnotation.value()).thenReturn(sameDay);

        assertEquals(mockStatement, rule.apply(mockStatement, mockDescription));
    }

    @Test
    public void shouldReturnAlwaysPassesStatementIfIgnoreUntilDateIsInTheFuture() {
        String tomorrow = new DateTime().plusDays(1).toString(DATE_PATTERN);

        when(mockDescription.getAnnotation(IgnoreUntil.class)).thenReturn(mockIgnoreUntilAnnotation);
        when(mockIgnoreUntilAnnotation.value()).thenReturn(tomorrow);

        assertTrue(rule.apply(mockStatement, mockDescription) instanceof IgnoreUntilRule.AlwaysPassesStatement);
    }

    @Test
    public void shouldReturnOriginalStatementIfMethodAnnotationWithNonDefaultValueExists() throws NoSuchMethodException {
        Annotation[] methodAnnotations = (MethodOverrideClassAnnotationTest.class).getMethod("shouldNowExecute").getDeclaredAnnotations();
        Description testMethodDescription = Description.createTestDescription(MethodOverrideClassAnnotationTest.class, "shouldNowExecute", methodAnnotations);
        assertEquals(2, testMethodDescription.getAnnotations().size());

        assertEquals(mockStatement, rule.apply(mockStatement, testMethodDescription));
    }

    @IgnoreUntil("2099-01-01")
    static class MethodOverrideClassAnnotationTest {

        @IgnoreUntil("2018-01-01")
        @Test
        public void shouldNowExecute() { }
    }

    @Test
    public void shouldReturnAlwaysPassesIfMethodAnnotationInheritsFromClassAnnotation() throws NoSuchMethodException {
        Annotation[] methodAnnotations = (MethodInheritFromClassAnnotationTest.class).getMethod("shouldNowExecute").getDeclaredAnnotations();
        Description testMethodDescription = Description.createTestDescription(MethodInheritFromClassAnnotationTest.class, "shouldNowExecute", methodAnnotations);
        assertEquals(2, testMethodDescription.getAnnotations().size());

        assertTrue(rule.apply(mockStatement, testMethodDescription) instanceof IgnoreUntilRule.AlwaysPassesStatement);
    }

    @IgnoreUntil("2099-01-01")
    static class MethodInheritFromClassAnnotationTest {

        @IgnoreUntil
        @Test
        public void shouldNowExecute() { }
    }

    @Test
    public void shouldReturnAlwaysPassesIfOnlyMethodAnnotationExists() throws NoSuchMethodException {
        Annotation[] methodAnnotations = (MethodAnnotationOnly.class).getMethod("shouldNowExecute").getDeclaredAnnotations();
        Description testMethodDescription = Description.createTestDescription(MethodAnnotationOnly.class, "shouldNowExecute", methodAnnotations);
        assertEquals(2, testMethodDescription.getAnnotations().size());

        assertTrue(rule.apply(mockStatement, testMethodDescription) instanceof IgnoreUntilRule.AlwaysPassesStatement);
    }

    static class MethodAnnotationOnly {

        @IgnoreUntil("2099-01-01")
        @Test
        public void shouldNowExecute() { }
    }
}
