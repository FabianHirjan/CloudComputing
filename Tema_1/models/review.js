class Review {
    constructor(id, bookId, rating, comment) {
      this.id = id;
      this.bookId = bookId;
      this.rating = rating;
      this.comment = comment;
    }
  
    toJSON() {
      return {
        id: this.id,
        bookId: this.bookId,
        rating: this.rating,
        comment: this.comment
      };
    }
  }
  
  module.exports = Review;
  