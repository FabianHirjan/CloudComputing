class Book {
    constructor(id, title, author) {
      this.id = id;
      this.title = title;
      this.author = author;
    }
  
    toJSON() {
      return {
        id: this.id,
        title: this.title,
        author: this.author
      };
    }
  }
  
  module.exports = Book;
  