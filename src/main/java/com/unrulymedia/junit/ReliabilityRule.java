package com.unrulymedia.junit;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class ReliabilityRule implements TestRule {

	private final int retries;

	public ReliabilityRule(int retries) {
		this.retries = retries;
	}

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            public void evaluate() throws Throwable {
                for (int i = 0; i < retries; i++) {
                    System.out.println("*** TEST " + (i + 1) + " of " + retries);
                    base.evaluate();
                }
            }
        };
    }
}
