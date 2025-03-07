import { Entity, PrimaryColumn, Column, OneToOne, JoinColumn, OneToMany } from "typeorm";
import { Preferences } from "./Preference";
import { GroupMember } from "./GroupMember";

@Entity()
export class User {
  @PrimaryColumn({ unique: true })
  discord_id!: string;

  @Column()
  username!: string;

  @Column({ unique: true })
  email!: string;

  @Column({ type: "timestamp", default: () => "CURRENT_TIMESTAMP" })
  created_at!: Date;

  @Column({ default: false })
  banned!: boolean;

  @Column({ default: null })
  avatar?: string;

  @OneToOne(() => Preferences, (preferences) => preferences.user)
  preferences!: Preferences;

  @OneToMany(() => GroupMember, (groupMember) => groupMember.user)
  groupMembers!: GroupMember[];
}