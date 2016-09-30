Introduction
------------

`Tornado <http://www.tornadoweb.org>`_ is a Python web framework and
asynchronous networking library, originally developed at `FriendFeed
<http://friendfeed.com>`_.  By using non-blocking network I/O, Tornado
can scale to tens of thousands of open connections, making it ideal for
`long polling <http://en.wikipedia.org/wiki/Push_technology#Long_polling>`_,
`WebSockets <http://en.wikipedia.org/wiki/WebSocket>`_, and other
applications that require a long-lived connection to each user.

Tornado can be roughly divided into four major components:

* A web framework (including `.RequestHandler` which is subclassed to
  create web applications, and various supporting classes).
* Client- and server-side implementions of HTTP (`.HTTPServer` and
  `.AsyncHTTPClient`).
* An asynchronous networking library including the classes `.IOLoop`
  and `.IOStream`, which serve as the building blocks for the HTTP
  components and can also be used to implement other protocols.
* A coroutine library (`tornado.gen`) which allows asynchronous
  code to be written in a more straightforward way than chaining
  callbacks.

The Tornado web framework and HTTP server together offer a full-stack
alternative to `WSGI <http://www.python.org/dev/peps/pep-3333/>`_.
While it is possible to use the Tornado web framework in a WSGI
container (`.WSGIAdapter`), or use the Tornado HTTP server as a
container for other WSGI frameworks (`.WSGIContainer`), each of these
combinations has limitations and to take full advantage of Tornado you
will need to use the Tornado's web framework and HTTP server together.
