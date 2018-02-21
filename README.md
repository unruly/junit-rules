junit-rules
===========

[![Build Status](https://travis-ci.org/unruly/junit-rules.svg?branch=master)](https://travis-ci.org/unruly/junit-rules)

A collection of useful JUnit rules from Unruly's codebases

## Install from Maven Central

```xml
<dependency>
    <groupId>co.unruly</groupId>
    <artifactId>junit-rules</artifactId>
    <version>1.0</version>
</dependency>
```

## Ignore tests until a certain date or datetime.

This allows you to write an acceptance/integration test before implementing a feature, and integrate it into your codebase before the implementation is complete.

`@IgnoreUntil` must be present on the test method you wish to ignore. 

The date/datetime value of the class level annotation can be shared across methods in the class or overridden by the method annotation.

```java

@IgnoreUntil("2099-01-01")
public class MyIgnorableTest {

    @Rule public IgnoreUntilRule rule = new IgnoreUntilRule();

    @IgnoreUntil
    @Test
    public void ignoredUntil20990101() {
    }

    @IgnoreUntil
    @Test
    public void alsoIgnoredUntil20990101() {
    }

    @IgnoreUntil("2014-10-30")
    @Test
    public void ignoredUntil20180101() {
    }

    @IgnoreUntil("2014-10-30T17:30:00")
    @Test
    public void ignoredUntil20141030T173000() {
    }

    @Test
    public void notIgnored() {
    }
}
```

The class annotation is optional, you can just annotate the method.

```java

public class MyIgnorableTest {

    @Rule public IgnoreUntilRule rule = new IgnoreUntilRule();

    @IgnoreUntil("2014-10-30T17:30:00")
    @Test
    public void ignoredUntil20141030T173000() {
    }

    @Test
    public void notIgnored() {
    }
}
```

## Quarantine non-deterministic tests

```java
@Rule QuarantineRule rule = new QuarantineRule();

@NonDeterministic(retries=3)
public void some_sporadically_failing_test() {

}
```

QuarantineRule supports a functional interface called QuarantineRuleLogger as a constructor argument for additional logging capabilities. For example, we email ourselves failures so it's harder to ignore.

```java
@Rule QuarantineRule rule = new QuarantineRule(msg -> System.err.println(msg));
```

## Make sure tests pass reliably

We use this to diagnose tests as being non-deterministic. To run each test 10 times:

```java
@Rule ReliabilityRule rule = new ReliabilityRule(10);
```
