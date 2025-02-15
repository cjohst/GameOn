import { Entity, PrimaryGeneratedColumn, Column, OneToOne, JoinColumn } from "typeorm";
import { Preferences } from "./Preference";

@Entity()
export class User {
  @PrimaryGeneratedColumn()
  user_id!: number;

  @Column({ unique: true })
  discord_id!: string;

  @Column()
  username!: string;

  @Column({ unique: true })
  email!: string;

  @Column({ type: "timestamp", default: () => "CURRENT_TIMESTAMP" })
  created_at!: Date;

  @Column({ default: false })
  banned!: boolean;

  @OneToOne(() => Preferences, (preferences) => preferences.user)
  preferences!: Preferences;
}