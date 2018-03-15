# How to contribute

Thanks for taking a look at and expressing interest in contributing to Unruly’s open-source projects!

[Our blog](https://medium.com/unruly-engineering) gives a bit of an insight into how Product Development works at Unruly, and you can find the rest of our projects on [GitHub](https://github.com/unruly)

## Testing
We have JUnit tests against all of our Java code - please include relevant tests with pull requests!

## Submitting changes
Please send a [GitHub Pull Request to junit-rules](https://github.com/unruly/junit-rules) with a clear list of what you've done (read more about [pull requests](http://help.github.com/pull-requests/)).

We can always use more test coverage or documentation!

Please make sure all of your commits are atomic (one feature per commit).

Always write a clear log message for your commits.

We encourage using the Karma commit message style, explained [here](http://karma-runner.github.io/2.0/dev/git-commit-msg.html).

One-line messages are fine for small changes, but bigger changes should look something like this:
> fix(sprocket): Make sprockets work with widgets
> 
> Sprockets caused widgets to malfunction when the hour-of-day is even, this commit fixes this bug and adds tests to prevent this regression occurring again.
