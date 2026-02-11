const mysql = require("mysql2/promise");
require("dotenv").config();

const pool = mysql.createPool({
  host: "localhost",
  user: "root",
  password: "1234",
  database: "prueba_auth"
});

module.exports = pool;
