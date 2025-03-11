
const http = require('http');
const booksRouter = require('./routes/booksRoutes');
const reviewsRouter = require('./routes/reviewsRoutes');

const server = http.createServer((req, res) => {
  if (req.url.startsWith('/books')) {
    booksRouter(req, res);
  } else if (req.url.startsWith('/reviews')) {
    reviewsRouter(req, res);
  } else {
    res.writeHead(404, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ error: 'Not Found' }));
  }
});

server.listen(8000, () => {
  console.log('Server is running on http://localhost:8000');
});
