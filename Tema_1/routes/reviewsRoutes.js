
const url = require('url');
const reviewsController = require('../controllers/reviewsController');
const booksController = require('../controllers/booksController');

function sendJsonResponse(res, statusCode, data) {
  res.writeHead(statusCode, { 'Content-Type': 'application/json' });
  res.end(JSON.stringify(data));
}

function parseRequestBody(req, callback) {
  let body = '';
  req.on('data', chunk => {
    body += chunk.toString();
  });
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

  if (!pathname.startsWith('/reviews')) {
    sendJsonResponse(res, 404, { error: 'Not Found' });
    return;
  }

  const parts = pathname.split('/').filter(Boolean); 

  if (parts.length === 1) {
    if (req.method === 'GET') {
      reviewsController.getAllReviews()
        .then(reviews => sendJsonResponse(res, 200, reviews))
        .catch(err => sendJsonResponse(res, 500, { error: 'Internal Server Error' }));
    } 
    else if (req.method === 'POST') {
      parseRequestBody(req, async (err, data) => {
        if (err || !data.bookId || !data.rating || !data.comment) {
          sendJsonResponse(res, 400, { error: 'Missing bookId, rating, or comment' });
          return;
        }
        
        const book = await booksController.getBookById(data.bookId);
        if (!book) {
          sendJsonResponse(res, 400, { error: 'Book not found' });
          return;
        }

        reviewsController.createReview(data.bookId, data.rating, data.comment)
          .then(review => sendJsonResponse(res, 201, review))
          .catch(err => sendJsonResponse(res, 500, { error: 'Internal Server Error' }));
      });
    } 
    else if (req.method === 'DELETE') {
      reviewsController.deleteAllReviews()
        .then(() => sendJsonResponse(res, 200, { message: 'All reviews deleted' }))
        .catch(err => sendJsonResponse(res, 500, { error: 'Internal Server Error' }));
    } 
    else {
      sendJsonResponse(res, 405, { error: 'Method Not Allowed' });
    }
  }

  else if (parts.length === 2) {
    const id = parseInt(parts[1]);
    if (isNaN(id)) {
      sendJsonResponse(res, 400, { error: 'Invalid ID format' });
      return;
    }

    if (req.method === 'GET') {
      reviewsController.getReviewById(id)
        .then(review => {
          if (!review) {
            sendJsonResponse(res, 404, { error: 'Review not found' });
          } else {
            sendJsonResponse(res, 200, review);
          }
        })
        .catch(err => sendJsonResponse(res, 500, { error: 'Internal Server Error' }));
    } 
    else if (req.method === 'PUT') {
      parseRequestBody(req, (err, data) => {
        if (err || !data.bookId || !data.rating || !data.comment) {
          sendJsonResponse(res, 400, { error: 'Missing bookId, rating, or comment' });
          return;
        }
        reviewsController.updateReview(id, data.bookId, data.rating, data.comment)
          .then(updatedReview => {
            if (!updatedReview) {
              sendJsonResponse(res, 404, { error: 'Review not found' });
            } else {
              sendJsonResponse(res, 200, updatedReview);
            }
          })
          .catch(err => sendJsonResponse(res, 500, { error: 'Internal Server Error' }));
      });
    } 
    else if (req.method === 'DELETE') {
      reviewsController.deleteReview(id)
        .then(deletedReview => {
          if (!deletedReview) {
            sendJsonResponse(res, 404, { error: 'Review not found' });
          } else {
            sendJsonResponse(res, 200, { message: 'Review deleted' });
          }
        })
        .catch(err => sendJsonResponse(res, 500, { error: 'Internal Server Error' }));
    } 
    else {
      sendJsonResponse(res, 405, { error: 'Method Not Allowed' });
    }
  }

  else if (parts.length === 3 && parts[1] === 'book') {
    const bookId = parseInt(parts[2]);
    if (isNaN(bookId)) {
      sendJsonResponse(res, 400, { error: 'Invalid bookId format' });
      return;
    }

    if (req.method === 'GET') {
      reviewsController.getBookReviews(bookId)
        .then(reviews => {
          if (!reviews || reviews.length === 0) {
            sendJsonResponse(res, 404, { error: 'No reviews found for this book or the book does not exist' });
          } else {
            sendJsonResponse(res, 200, reviews);
          }
        })
        .catch(err => sendJsonResponse(res, 500, { error: 'Internal Server Error' }));
    } 
    else {
      sendJsonResponse(res, 405, { error: 'Method Not Allowed' });
    }
  }

  else {
    sendJsonResponse(res, 404, { error: 'Not Found' });
  }
};
