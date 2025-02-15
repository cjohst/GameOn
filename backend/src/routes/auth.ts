import express, { Request, Response, Router } from "express";
import dotenv from "dotenv";
import axios from "axios";

dotenv.config();

export const authRouter: Router = Router();

const DISCORD_AUTH_URL = "https://discord.com/api/oauth2/authorize";
const DISCORD_TOKEN_URL = "https://discord.com/api/oauth2/token";
const DISCORD_USER_URL = "https://discord.com/api/users/@me";
const DISCORD_CLIENT_ID = process.env.DISCORD_CLIENT_ID || "";
const DISCORD_CLIENT_SECRET = process.env.DISCORD_CLIENT_SECRET || "";
const DISCORD_REDIRECT_URI = process.env.DISCORD_REDIRECT_URI || "";

/**
 * Creates URL search parameters for exchanging the auth code.
 * @param code - The authorization code received from Discord.
 * @returns URLSearchParams
 */
function createSearchParamsAuthToken(code: string): URLSearchParams {
  return new URLSearchParams({
    grant_type: "authorization_code",
    code: code,
    redirect_uri: DISCORD_REDIRECT_URI,
    client_id: DISCORD_CLIENT_ID,
    client_secret: DISCORD_CLIENT_SECRET,
  });
}

/**
 * Exchanges the Discord authorization code for an access token.
 * @param code - The authorization code.
 * @returns A promise with token data.
 */
async function exchangeCodeForAccessToken(code: string): Promise<any> {
  const params = createSearchParamsAuthToken(code);
  const response = await axios.post(DISCORD_TOKEN_URL, params.toString(), {
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
  });
  return response.data;
}

/**
 * Fetches Discord user data using the provided access token.
 * @param accessToken - The access token.
 * @returns A promise with user data.
 */
async function fetchDiscordUser(accessToken: string): Promise<any> {
  const response = await axios.get(DISCORD_USER_URL, {
    headers: { Authorization: `Bearer ${accessToken}` },
  });
  return response.data;
}

/**
 * Route: GET /auth/login
 * Redirects the user to Discord's authorization URL.
 */
authRouter.get("/login", (req: Request, res: Response) => {
  //TODO: Check if valid session exists
  const authURL = `${DISCORD_AUTH_URL}?client_id=${DISCORD_CLIENT_ID}&redirect_uri=${DISCORD_REDIRECT_URI}&response_type=code&scope=identify%20email`;
  res.redirect(authURL);
});

/**
 * Route: GET /auth/callback_discord
 * Handles the Discord callback by exchanging the code and retrieving user info.
 */
authRouter.get("/callback_discord", async (req: Request, res: Response) => {
  const code = req.query.code as string;

  if (!code) {
    res.status(400).send("Error fetching auth_code");
    return;
  }

  try {
    const tokenData = await exchangeCodeForAccessToken(code);
    console.log("Token Data:", tokenData);

    if (tokenData.error) {
      res.status(400).send(tokenData.error);
      return;
    }

    const userData = await fetchDiscordUser(tokenData.access_token);
    console.log("User Data:", userData);
    
    //TODO: Check if user registered with system if not redirect to registration page
    res.send(userData);
  } catch (error) {
    console.error("Error during Discord OAuth process:", error);
    res.status(500).send("Internal Server Error");
  }
});
