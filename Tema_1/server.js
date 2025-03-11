const http = require('http');
const booksRoutes = require('./routes/booksRoutes');



const PORT = 8000;

const server = http.createServer((req, res) => {
  booksRoutes(req, res);
});

server.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
