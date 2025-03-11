const url = require('url');
const booksController = require('../controllers/booksController');

function sendJsonResponse(res, statusCode, data) {
  res.writeHead(statusCode, { 'Content-Type': 'application/json' });
  res.end(JSON.stringify(data));
}

function parseRequestBody(req, callback) {
  let body = '';
  req.on('data', chunk => { body += chunk.toString(); });
  req.on('end', () => {
    try {
      const parsed = JSON.parse(body);
      callback(null, parsed);
    } catch (err) {
      callback(err, null);
    }
  });
}



module.exports = (req, res) => {
  const parsedUrl = url.parse(req.url, true);
  const pathname = parsedUrl.pathname;
  
  if (!pathname.startsWith('/books')) {
    sendJsonResponse(res, 404, { error: 'Not Found' });
    return;
  }

  const parts = pathname.split('/').filter(Boolean);


  if (parts.length === 1) {
    if (req.method === 'GET') {
      booksController.getAllBooks()
        .then(books => sendJsonResponse(res, 200, books))
        .catch(err => sendJsonResponse(res, 500, { error: 'Internal Server Error' }));
    } else if (req.method === 'POST') {
      parseRequestBody(req, (err, data) => {
        if (err || !data.title || !data.author) {
          sendJsonResponse(res, 400, { error: 'Missing title or author' });
          return;
        }
        booksController.createBook(data.title, data.author)
          .then(book => sendJsonResponse(res, 201, book))
          .catch(err => sendJsonResponse(res, 500, { error: 'Internal Server Error' }));
      });
    }
    else if (req.method === 'DELETE') {
      booksController.deleteAllBooks()
        .then(() => sendJsonResponse(res, 200, { message: 'All books deleted' }))
        .catch(err => sendJsonResponse(res, 500, { error: 'Internal Server Error' }));
    }
    else {
      sendJsonResponse(res, 405, { error: 'Method Not Allowed' });
    }
  }
  // books/Author

  else if (parts.length === 2 && parts[1] === 'changeAuthor' && req.method === 'PUT') {
    parseRequestBody(req, (err, data) => {
      if (err || !data.oldAuthor || !data.newAuthor) {
        sendJsonResponse(res, 400, { error: 'Missing oldAuthor or newAuthor' });
        return;
      }
      booksController.changeAuthor(data.oldAuthor, data.newAuthor)
        .then(updatedCount => {
          sendJsonResponse(res, 200, { message: `Updated ${updatedCount} book(s).` });
        })
        .catch(() => sendJsonResponse(res, 500, { error: 'Internal Server Error' }));
    });
  }

  
// books/{id}

  else if (parts.length === 2) {
    const id = parseInt(parts[1]);
    if (isNaN(id)) {
      sendJsonResponse(res, 400, { error: 'Invalid ID format' });
      return;
    }
    if (req.method === 'GET') {
      booksController.getBookById(id)
        .then(book => {
          if (!book) sendJsonResponse(res, 404, { error: 'Book not found' });
          else sendJsonResponse(res, 200, book);
        })
        .catch(err => sendJsonResponse(res, 500, { error: 'Internal Server Error' }));
    } else if (req.method === 'PUT') {
      parseRequestBody(req, (err, data) => {
        if (err || !data.title || !data.author) {
          sendJsonResponse(res, 400, { error: 'Missing title or author' });
          return;
        }
        booksController.updateBook(id, data.title, data.author)
          .then(updatedBook => {
            if (!updatedBook) sendJsonResponse(res, 404, { error: 'Book not found' });
            else sendJsonResponse(res, 200, updatedBook);
          })
          .catch(err => sendJsonResponse(res, 500, { error: 'Internal Server Error' }));
      });
    } 
    
    else if (req.method === 'DELETE') {
      booksController.deleteBook(id)
        .then(deletedBook => {
          if (!deletedBook) sendJsonResponse(res, 404, { error: 'Book not found' });
          else sendJsonResponse(res, 200, { message: 'Book deleted' });
        })
        .catch(err => sendJsonResponse(res, 500, { error: 'Internal Server Error' }));
    } else {
      sendJsonResponse(res, 405, { error: 'Method Not Allowed' });
    }
  } 

  else {
    sendJsonResponse(res, 404, { error: 'Not Found' });
  }
};
