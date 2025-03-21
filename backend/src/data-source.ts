import { DataSource } from "typeorm";
import { User } from "./entity/User";
import { Admin } from "./entity/Admin";
import { Game } from "./entity/Game";
import { Group } from "./entity/Group";
import { GroupMember } from "./entity/GroupMember";
import { Preferences } from "./entity/Preference";
import { Report } from "./entity/Report";
import * as dotenv from "dotenv";

// Load environment variables from .env file
dotenv.config();

export const AppDataSource = new DataSource({
  type: "mysql",
  host: process.env.MYSQL_HOST,
  port: 3306, //used internally in docker image
  username: "root", // Use root instead of a custom user
  password: "", // Empty password
  database: process.env.MYSQL_DB,
  synchronize: true,
  logging: false,
  entities: [User, Admin, Game, Group, Report, GroupMember, Preferences],
  migrations: [],
  subscribers: [],
});