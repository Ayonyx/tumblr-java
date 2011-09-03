tumblr-java is a java library for the Tumblr API.

Notes:
- xAuth is the only supported authentication.
- The library uses a mix of the v1 and v2 Tumblr APIs. The v1 API is used for creating new posts. Everything else uses the v2 API. This is mainly because file uploads using the v2 API are problematic.
- Creating a post returns the HTTP status of the request. Everything else returns a JSONObject.
- Full API docs: [v1 API](http://www.tumblr.com/docs/en/api/v1), [v2 API](http://www.tumblr.com/docs/en/api/v2).
