
-- You should create these tables in your MySQL database 
CREATE TABLE admins (
  id INT AUTO_INCREMENT PRIMARY KEY,
  email  VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL
);


CREATE TABLE denied_commands (
  id INT AUTO_INCREMENT PRIMARY KEY,
  command VARCHAR(255) NOT NULL UNIQUE,
  created_by INT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (created_by) REFERENCES admins(id) ON DELETE SET NULL
);

CREATE TABLE denied_queries (
  id INT AUTO_INCREMENT PRIMARY KEY,
  query TEXT NOT NULL,
  created_by INT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (query(255)),
  FOREIGN KEY (created_by) REFERENCES admins(id) ON DELETE SET NULL
);
