## Setup Instructions
1. **Install Dependencies:**
    ```
    npm install
    ```
2. **Enviornment Variables:**
   Create a .env file in the project root (backend folder) with the following variables:
    ```
    DISCORD_CLIENT_ID=your_discord_client_id
    DISCORD_CLIENT_SECRET=your_discord_client_secret
    DISCORD_REDIRECT_URI=http://localhost:3000/auth/callback_discord
    PORT=3000
    ```
3. **Running the Project:**
   ```
   npx ts-node src/index.ts
   ```

## Example Folder
There is an example_frontend folder that replicates a simple frontend. Open the html file in your browser and make sure the backend is running on the specified port.

## Database Setup and Running the Server

### Step 1: Set Up the Database

**Install PostgreSQL:**

If you don't already have PostgreSQL installed, download and install it from the official website.

Alternatively, you can use a Docker container for PostgreSQL:
```bash
docker run --name gameon-db -e POSTGRES_PASSWORD=yourpassword -p 5432:5432 -d postgres
```

**Create a Database:**

Connect to your PostgreSQL instance using a client like psql or a GUI tool like pgAdmin.

Create a database for your project:
```sql
CREATE DATABASE gameon_db;
```

**Update Database Configuration:**

Create or update your `.env` file with the following database connection details:

```
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_USER=postgres
POSTGRES_PASSWORD=yourpassword
POSTGRES_DB=gameon_db
```

### Step 2: Install Dependencies

**Install Node.js:**

If you don't have Node.js installed, download and install it from the official website.

**Install Project Dependencies:**

Navigate to your project directory and run:
```bash
npm install
```

### Step 3: Start the Server

**Compile TypeScript:**

Run the TypeScript compiler to generate the JavaScript files:
```bash
npx tsc
```

**Start the Server:**

Run the compiled JavaScript files:
```bash
node dist/index.js
```

Alternatively, you can use ts-node to run the server directly without compiling:
```bash
npx ts-node src/index.ts
```

**Verify the Server:**

The server should start and log a message like:
```
Server is running on http://localhost:3000
```

### Step 4: Test the API Endpoints

You can use tools like Postman, cURL, or Thunder Client (VSCode extension) to test your API endpoints.

**Example: Create a User**

**Endpoint:** `POST /users`

**Request Body:**
```json
{
    "discord_id": "123456789012345678",
    "username": "gamer123",
    "email": "gamer123@example.com"
}
```

**Response:**
```json
{
    "user_id": 1,
    "discord_id": "123456789012345678",
    "username": "gamer123",
    "email": "gamer123@example.com",
    "created_at": "2023-10-01T12:00:00Z",
    "banned": false
}
```

**Example: Create Preferences**

**Endpoint:** `POST /users/:id/preferences`

**Request Body:**
```json
{
    "spoken_language": "English",
    "time_zone": "UTC+1",
    "skill_level": "competitive",
    "game_id": 1
}
```

**Response:**
```json
{
    "preference_id": 1,
    "spoken_language": "English",
    "time_zone": "UTC+1",
    "skill_level": "competitive",
    "user": {
        "user_id": 1,
        "username": "gamer123",
        "email": "gamer123@example.com"
    },
    "game": {
        "game_id": 1,
        "game_name": "League of Legends"
    }
}
```

**Example: Create a Group**

**Endpoint:** `POST /groups`

**Request Body:**
```json
{
    "game_id": 1,
    "group_name": "LoL Competitive Group",
    "max_players": 5
}
```

**Response:**
```json
{
    "group_id": 1,
    "game_id": 1,
    "group_name": "LoL Competitive Group",
    "created_at": "2023-10-01T12:00:00Z",
    "max_players": 5
}
```

### Step 5: Stop the Server

To stop the server, press `Ctrl + C` in the terminal where the server is running.

### Step 6: Clean Up

**Stop the Database:**

If you're using Docker, stop the PostgreSQL container:
```bash
docker stop gameon-db
```

**Delete the Database (Optional):**

If you want to start fresh, you can drop the database:
```sql
DROP DATABASE gameon_db;
```