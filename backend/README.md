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

