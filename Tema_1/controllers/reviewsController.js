const pool = require('../db');

async function getAllReviews() {
  const result = await pool.query('SELECT * FROM reviews ORDER BY id');
  return result.rows;
}

async function getReviewById(id) {
  const result = await pool.query('SELECT * FROM reviews WHERE id = $1', [id]);
  return result.rows[0];
}

async function getBookReviews(id){
    const result = await pool.query('SELECT * FROM reviews WHERE book_id = $1', [id]);
    return result.rows;
  }

async function createReview(bookId, rating, comment) {
  const result = await pool.query(
    'INSERT INTO reviews (book_id, rating, comment) VALUES ($1, $2, $3) RETURNING *',
    [bookId, rating, comment]
  );
  return result.rows[0];
}

async function updateReview(id, bookId, rating, comment) {
  const result = await pool.query(
    'UPDATE reviews SET book_id = $1, rating = $2, comment = $3 WHERE id = $4 RETURNING *',
    [bookId, rating, comment, id]
  );
  return result.rows[0];
}

async function deleteReview(id) {
  const result = await pool.query(
    'DELETE FROM reviews WHERE id = $1 RETURNING *',
    [id]
  );
  return result.rows[0];
}

async function deleteAllReviews() {
  await pool.query('DELETE FROM reviews');
}

async function getReviewsByBookId(bookId) {
  const result = await pool.query(
    'SELECT * FROM reviews WHERE book_id = $1 ORDER BY id',
    [bookId]
  );
  return result.rows;
}

module.exports = {
  getAllReviews,
  getReviewById,
  createReview,
  updateReview,
  deleteReview,
  getBookReviews,
  deleteAllReviews,
  getReviewsByBookId
};
