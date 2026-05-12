# Flight Arrange

A flight arrangement application built with Vue.js, Spring Boot, and MySQL.

---

## Prerequisites

### Mac

- **Java 17** — download from [oracle.com](https://www.oracle.com/java/technologies/downloads/#java17) or install via Homebrew:
  ```bash
  brew install openjdk@17
  ```
- **Node.js + npm** — download the LTS version from [nodejs.org](https://nodejs.org)
- **MySQL** — download from [mysql.com](https://dev.mysql.com/downloads/mysql/) or via Homebrew:
  ```bash
  brew install mysql
  brew services start mysql
  ```
- (Optional) **MySQL Workbench** or **DBeaver** — UI to interact with your database

### Windows

- **Java 17** — download from [oracle.com](https://www.oracle.com/java/technologies/downloads/#java17) and run the installer
- **Node.js + npm** — download the LTS version from [nodejs.org](https://nodejs.org) and run the installer
- **MySQL** — download the installer from [mysql.com](https://dev.mysql.com/downloads/installer/) and follow the setup wizard
- (Optional) **MySQL Workbench** or **DBeaver** — UI to interact with your database

---

## Database Setup

### Mac

Open a terminal and run:

```bash
mysql -u root -p
```

### Windows

Open **MySQL Command Line Client** from the Start menu.

Then for both Mac and Windows run:

```sql
CREATE DATABASE flight_arrange;
exit;
```

---

## Project Setup

**1. Clone the repository:**

```bash
git clone <your-repo-url>
cd flight-arrange
```

**2. Configure the backend database connection:**

Open `backend/src/main/resources/application.properties` and update with your local credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/flight_arrange
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
```

> Do not commit this file with your real password. Each developer should update this locally with their own credentials.

---

## Running the App

### Mac

From the project root you can use the Makefile to start everything at once:

```bash
make start    # starts both backend and frontend
make stop     # stops the backend
make restart  # restarts the backend
```

Or manually in two separate terminals:

**Terminal 1 — Backend:**
```bash
cd backend
./mvnw spring-boot:run
```

**Terminal 2 — Frontend:**
```bash
cd frontend
npm install
npm run dev
```

### Windows

`make` is not available on Windows by default. Open two separate terminals (Command Prompt or PowerShell) and run each manually:

**Terminal 1 — Backend:**
```bash
cd backend
mvnw.cmd spring-boot:run
```

**Terminal 2 — Frontend:**
```bash
cd frontend
npm install
npm run dev
```

Backend runs at `http://localhost:8080/api/home`  
Frontend runs at `http://localhost:5173`

---

## Project Structure

```
flight-arrange/
├── backend/       # Spring Boot API
├── frontend/      # Vue.js frontend
├── Makefile       # Mac only
└── README.md
```