import e, { Request, Response } from "express";
import axios from "axios";
import dotenv from "dotenv";
import { User } from "../entity/User";
import { Preferences } from "../entity/Preference";

dotenv.config();

const DISCORD_AUTH_URL = "https://discord.com/api/oauth2/authorize";
const DISCORD_TOKEN_URL = "https://discord.com/api/oauth2/token";
const DISCORD_USER_URL = "https://discord.com/api/users/@me";
const DISCORD_CLIENT_ID = process.env.DISCORD_CLIENT_ID || "";
const DISCORD_CLIENT_SECRET = process.env.DISCORD_CLIENT_SECRET || "";
const DISCORD_REDIRECT_URI = process.env.DISCORD_REDIRECT_URI || "";

const DB_SERVICE_URL = process.env.DB_SERVICE_URL || "";


/** Simply used to tell the frontend to redirect to the Preferences Activity.\
 * This link must be intercepted on the frontend.\
 * This link not currently deep-linked to any app.
*/
const FRONTEND_PREFERENCES_URL = "gameoncpen://preferences"

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
export async function exchangeCodeForAccessToken(code: string): Promise<any> {
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
export async function fetchDiscordUser(accessToken: string): Promise<any> {
  const response = await axios.get(DISCORD_USER_URL, {
    headers: { Authorization: `Bearer ${accessToken}` },
  });
  return response.data;
}

/**
 * 
 * 1. Checks if a session already exists.
 *    - If a session exists:
 *      - Determines the session type:
 *        - If the session indicates a logged-in user, fetch their profile from the database.
 *        - If the session is temporary (user needs to register), redirect them to the frontend registration page.
 * 
 * 2. If no session exists:
 *    - Redirects the client to Discord's OAuth2 login.
 *    - After successful authentication:
 *      - Checks if the user exists in the database.
 *        - If the user exists, create a session for them and log them in.
 *        - If the user does not exist, create a temporary session indicating registration is needed and redirect them to the frontend registration page.
 */
export async function handleLoginOrRedirect(req: Request, res: Response): Promise<void> {
  if (req.session.user){
    if(req.session.user.temp_session){
      console.log("User has a temp session. Redirecting to register page.");
      //This link must be intercepted on the frontend
      //This is not currently deep-linked to any app however
      res.redirect(FRONTEND_PREFERENCES_URL+`?discord_id=${req.session.user.discord_id}`);
      return

    }else{
      console.log("User has a permanent session. Redirecting to home page.");
      try {
        const response = await axios.get<User>(`${DB_SERVICE_URL}/users/${req.session.user.discord_id}`, {
          responseType: 'json'
        });
        
        if(response.status == 200){
          const userProfileData = response.data
          res.send(userProfileData);
        }else{
          console.log("Something has went horribly wrong.");
          res.status(response.status).send(response.data);
        }
      } catch (error) {
        console.error("Error fetching user profile:", error);
        res.status(500).json({ message: "Internal server error" });
      }

      return;
    }
  }
  console.log("No Session User");

  const authURL = `${DISCORD_AUTH_URL}?client_id=${DISCORD_CLIENT_ID}&redirect_uri=${DISCORD_REDIRECT_URI}&response_type=code&scope=guilds.join+identify+email+guilds`;
  res.redirect(authURL);
  return;
}

/**
 * Handles the Discord OAuth2 callback.
 * 
 * 1. Exchanges the authorization code for an access token.
 * 2. Fetches the user's profile from Discord.
 * 3. Checks if the user exists in the database:
 *    - If yes, creates a session and logs them in.
 *    - If no, creates a temporary session for registration.
 * 4. Sends the user data or redirects as needed.
 */
export async function handleDiscordCallback(req: Request, res: Response): Promise<void> {
  const code = req.query.code as string;
  if (!code) {
    console.log("Something has went wrong with the discord auth code.");
    res.status(400).send("Error fetching auth code");
    return;
  }

  try {
    const tokenData = await exchangeCodeForAccessToken(code);
    console.log("Token Data:", tokenData);

    if (tokenData.error) {
      console.log("Something has went wrong with the Discord token exchange code.");
      res.status(400).send(tokenData.error);
      return;
    }

    const discordUserData = await fetchDiscordUser(tokenData.access_token);
    console.log("User Data:", discordUserData);
    if (!discordUserData.id) {
      res.status(400).send("Error fetching user data");
      return;
    }

    const dbResponse = await axios.get<User>(`${DB_SERVICE_URL}/users/${discordUserData.id}`, {
      responseType: 'json',
      validateStatus: (status) => status < 500
    });
    
    //User Profile Does Not Exist, Create Temp Session Return User To Registration
    if(dbResponse.status == 404){
      req.session.user = {
        discord_id: discordUserData.id,
        discord_access_token: tokenData.access_token,
        discord_refresh_token: tokenData.refresh_token,
        discord_email: discordUserData.email,
        discord_username: discordUserData.username,
        discord_avatar: discordUserData.avatar,
        temp_session: true
      }

      //This link must be intercepted on the frontend
      //This is not currently deep-linked to any app however
      res.redirect(FRONTEND_PREFERENCES_URL+`?discord_id=${discordUserData.id}`);
      return;
      
      //User Profile Exists, Create Session Return User Profile
    }else if(dbResponse.status == 200){
      req.session.user = {
        discord_id: discordUserData.id,
        discord_access_token: tokenData.access_token,
        discord_refresh_token: tokenData.refresh_token,
        temp_session: false
      }

      const userProfileData = dbResponse.data
      res.send(userProfileData);
    }else{
      res.status(dbResponse.status).send(dbResponse.data);
      return;
    }
    
    // else if status 200 block sends response twice
    // which crashes the server
    // res.send(discordUserData);
  } catch (error) {
    console.error("Error during Discord OAuth process:", error);
    res.status(500).send("Internal Server Error");
  }
}

/**
 * Handles user registration.
 * 
 * 1. Redirects to the homepage if no session user exists.
 * 2. Extracts user data from the request body and attaches Discord info from the session.
 * 3. Sends a request to the database service to create a new user.
 * 4. If successful, updates the session and responds with the user data.
 * 5. Otherwise, forwards the error response.
 */
export async function handleRegister(req: Request, res: Response): Promise<void> {
  const sessionData = req.session.user!;
  if (sessionData.discord_id != req.body.discord_id as String|null) {
    res.status(403).send("Discord ID does not match session information.")
    return
  }

  console.log("User has session and is in register");
  const userData = {
    discord_id: sessionData.discord_id,
    email: sessionData.discord_email!,
    username: sessionData.discord_username!,
    avatar: sessionData.discord_avatar || null
  } as User

  try {
    const userResponse = await axios.post<User>(
      `${DB_SERVICE_URL}/users`, userData, { responseType: 'json' }
    );

    if(userResponse.status != 201) {
      res.status(userResponse.status).send(userResponse.data);
      return
    }

    const preferencesResponse = await axios.post<Preferences>(
      `${DB_SERVICE_URL}/preferences`, req.body as Preferences, { responseType: 'json' }
    )

    if(preferencesResponse.status == 201) {
      sessionData.temp_session = false;
      res.send(userResponse.data);
    } else {
      res.status(preferencesResponse.status).send(preferencesResponse.data);
    }
  } catch (error) {
    console.error("Error during registration process:", error);
    res.status(500).json({ message: "Internal server error" });
  }
}

export async function handleLogout(req: Request, res: Response): Promise<void> {
  // Clear session
  req.session.regenerate((err) => {
    if(err) res.sendStatus(500)
    else {
      console.log("User logged out")
      res.sendStatus(204)
    }
  })
}

export async function protectEndpoint(req: Request, res: Response, next: e.NextFunction): Promise<void> {
  if(req.session.user){
    next();
  }else{
    res.status(401).send("Unauthorized. Requires a session to access this endpoint.");
  }
}