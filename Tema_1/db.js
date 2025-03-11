const { Pool } = require('pg');

const pool = new Pool({
  user: 'fabianhirjan',     
  host: 'localhost',
  database: 'Bookself',
  password: '',
  port: 5432,
});

module.exports = pool;
