package co.unruly.junit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static co.unruly.junit.QuarantineRule.QuarantinedStatement;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class QuarantineStatementTest {

    QuarantinedStatement quarantineStatement;

    @Mock
    Statement mockStatement;

    @Mock
    FrameworkMethod mockFrameworkMethod;

    @Mock
    Description mockDescription;

    @Mock
    QuarantineRuleLogger mockLogger;

    static class ThrowsExceptionForNRunsThenDoNothing implements Answer<Object> {
        private int n;

        public ThrowsExceptionForNRunsThenDoNothing(int n) {
            this.n = n;
        }

        @Override
        public Object answer(InvocationOnMock invocation) throws Throwable {
            if (--n < 0) {
                return null;
            } else {
                throw new IllegalStateException();
            }
        }
    }

    static class AlwaysFails implements Answer<Object> {
        @Override
        public Object answer(InvocationOnMock invocation) throws Throwable {
            throw new IllegalStateException();
        }
    }


    @Before
    public void setup() throws SecurityException, NoSuchMethodException {
        quarantineStatement = new QuarantinedStatement(mockStatement, mockDescription, 2, mockLogger);
        Mockito.<Class<?>>when(mockDescription.getTestClass()).thenReturn(QuarantineStatementTest.class);
        when(mockDescription.getMethodName()).thenReturn("MethodName");
    }

    @Test
    public void shouldNotExceedRetryCount() throws Throwable {
        doAnswer(new ThrowsExceptionForNRunsThenDoNothing(1)).when(mockStatement).evaluate();

        quarantineStatement.evaluate();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowIllegalStateIfRuleExceedsTheRetryCount() throws Throwable {
        doAnswer(new ThrowsExceptionForNRunsThenDoNothing(3)).when(mockStatement).evaluate();

        quarantineStatement.evaluate();
    }

    @Test
    public void shouldNotLogIfTestFailsEveryTime() throws Throwable {
        doAnswer(new AlwaysFails()).when(mockStatement).evaluate();

        try {
            quarantineStatement.evaluate();
        } catch (IllegalStateException e) {

        }

        verifyZeroInteractions(mockLogger);
    }

    @Test
    public void shouldLogErrorIfTestFailsThenPasses() throws Throwable {

        doAnswer(new ThrowsExceptionForNRunsThenDoNothing(1)).when(mockStatement).evaluate();

        quarantineStatement.evaluate();

        verify(mockLogger, times(1)).output(anyString());
    }

}
