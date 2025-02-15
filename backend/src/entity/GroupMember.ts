import { Entity, PrimaryGeneratedColumn, ManyToOne, JoinColumn, Column } from "typeorm";
import { User } from "./User";
import { Group } from "./Group";

@Entity()
export class GroupMember {
  @PrimaryGeneratedColumn()
  group_member_id!: number;

  @ManyToOne(() => User)
  @JoinColumn({ name: "user_id" })
  user!: User;

  @ManyToOne(() => Group, (group) => group.members)
  @JoinColumn({ name: "group_id" })
  group!: Group;

  @Column({ type: "timestamp", default: () => "CURRENT_TIMESTAMP" })
  joined_at!: Date;
}