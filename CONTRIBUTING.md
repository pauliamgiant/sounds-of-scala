# Contributing guidelines

Nothing too draconian here but just on outline of how we can go about working together effectively.

## Coding style

Using the standard Scala3 dialect in Scalafmt as specified in scalafmt.conf should keep our code style uniform. If you'd like to add some rules to this document feel free to raise it on the discord chat to invite any feedback on any proposed style changes. 


## Contributing workflow

This is the general workflow for contributing to Sounds of Scala.

1. Please perform your work in its own Git branch with a descriptive name that explains its intent.
2. When the feature or fix is completed please open a
    [Pull Request](https://help.github.com/articles/about-pull-requests/) on GitHub.
3. The Pull Request can then be reviewed by at least one core developer.
   Any contributor can participate in the review process, and we'd encourage this.
4. After the review, please look to resolve issues brought up by the reviewers as needed
    (amending or adding commits to address reviewers' comments), iterating until
    the Pull Request is approved.
5. Once the code has passed review the Pull Request can be merged into the distribution.

## Pull Request Requirements

In order for a Pull Request to be reviewed it will need to meet these requirements:

1. Adhere to current Scalafmt code standard.
2. Follow the [Boy Scout Rule](https://medium.com/@biratkirat/step-8-the-boy-scout-rule-robert-c-martin-uncle-bob-9ac839778385).
3. Be accompanied by appropriate tests.
4. Be issued from a branch *other than main or master*

## Documentation

For the current scale of the project feel free to add Markdown documentation only in the event someone would need it to proceed.

## Creating Commits And Writing Commit Messages

Follow these guidelines when creating public commits and writing commit messages.

### Prepare meaningful commits

- If your change/feature has multiple commits please squash them.
- Aim for **one commit per useful unit of change**, each accompanied by a detailed commit message.
For more info, see the article:
[Git Workflow](https://sandofsky.com/blog/git-workflow.html).
Additionally, every commit should be able to be used in isolation--that is,
each commit must build and pass all tests.

### First line of the commit message

The first line should start with the ticket number followed by a descriptive sentence about what the commit is
doing, written using the imperative style, e.g., "Change this.", and should
not exceed 70 characters.
It should be possible to fully understand what the commit does by just
reading this single line.
Example:
SS-000001: Add triplets to DSL.


### Body of the commit message

If the commit is a small fix, the first line can be enough.
Otherwise, following the single line description should be a blank line
followed by details of the commit, in the form of free text, or bulleted list.