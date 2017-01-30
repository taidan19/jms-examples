# JMS Examples

I don't often use JMS, but whenever I do, I end up forgetting everything I know
about the subject. When this happens, I find myself reading through the [*Java Message Service* book published by O'Reilly Media](http://shop.oreilly.com/product/9780596522056.do), in addition to the book's sample code.

There are two problems with this approach. Firstly, in addition to showing readers how to program against the JMS API, the book itself also goes into detail about general concepts of message services, message service architecture and deployment considerations, SOA, ESB's, and more. This is all well and good, but usually I just need to be reminded of how to connect to a JMS broker and send and receive content.

For this, I need example code that I can stand up and run without much fuss, and which contains some detailed documentation that explains what's going on. That's where the second problem comes into play; the book's sample code (as it is currently hosted on the O'Reilly website) leaves much to be desired. The code has so-so documentation, numerous commented out lines of code, and some bad stylistic practices (such as not using curly braces on a one-line if statement), all of which makes it harder to read and understand what's going on.

Furthermore, all of the samples require you to launch a seprate instance of Apache ActiveMQ in the background. For most of them, this is overkill.

This project, then, is my attempt to make the book's examples into something usable for me (and, hopefully, anyone else reading this).

## Changes

* *Code structure* - I am planning on breaking up the code into individual Maven submodules, rather than break them up by package name (or as individual, top level projects). This makes it so that the reader can examine each Chapter's examples in isolation, without code or configuration from any other chapter getting in the way.

* *Dependencies* - I want each chapter to include the bare minimum of Maven
dependencies. Taking the "kitchen sink" approach might lead to me (or another user) to assume that certain unecessary deps are in fact required to get an example running.

* *Code Cleanup* - I've updated the formatting used by the sample code. All if-else statements are wrapped in curly braces, and all indentation is uniform.  The line width of the code itself is around 120 characters, but the line width of comments is 80 chars. I may eventually shift them both to a width of 80.

* *Formatting/Spacing* - I added excessive amounts of white space in some methods. This helps me concentrate on one section of the code at a time, rather than have it all run together in my mind; doing that causes my brain to filter most of the content out as if it were junk. I don't normally write production code like this, but for instructional purposes I find it useful.

* *Documentation* - I added additional comments and documentation where needed in order to clarify what is happening in certain sections. This is to help jog my memory when coming back to JMS after a hiatus.

* *Standalone* - When possible, I added the ability to launch the examples using nothing but the contents of this project (in other words, unless otherwise stated, you do not need to download and run a standalone instance of ActiveMQ to run these examples).

## Misc

*  Currently, the project uses the same version of ActiveMQ that is used in the book (5.2.0). I did this for compatability purposes. I may eventually upgrade the examples to use a newer version, as 5.2 is extremely old. If this ends up happening, I will move the 5.2.0 compatible version of the examples to a separate Git branch.
