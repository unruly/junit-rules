package com.unrulymedia.junit;

import org.junit.Test;
import org.junit.runners.model.Statement;

import static org.mockito.Mockito.*;

public class ReliabilityRuleTest {

    @Test
    public void shouldRunTestSpecifiedNumberOfTimes() throws Throwable {
        int wantedNumberOfInvocations = 10;

        ReliabilityRule reliabilityRule = new ReliabilityRule(wantedNumberOfInvocations);
        Statement mockStatement = mock(Statement.class);

        reliabilityRule.apply(mockStatement, null).evaluate();

        verify(mockStatement, times(wantedNumberOfInvocations)).evaluate();
    }

}