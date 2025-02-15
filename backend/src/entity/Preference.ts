import { Entity, PrimaryGeneratedColumn, Column, ManyToOne, JoinColumn } from "typeorm";
import { User } from "./User";
import { Game } from "./Game";

@Entity()
export class Preferences {
  @PrimaryGeneratedColumn()
  preference_id: number;

  @Column()
  spoken_language: string;

  @Column()
  time_zone: string;

  @Column({ type: "enum", enum: ["casual", "competitive"] })
  skill_level: "casual" | "competitive";

  @ManyToOne(() => User, user => user.preferences)
  @JoinColumn({ name: "user_id" })
  user: User;

  @ManyToOne(() => Game)
  @JoinColumn({ name: "game_id" })
  game: Game;
}