import { Entity, PrimaryGeneratedColumn, ManyToOne, JoinColumn, Column } from "typeorm";
import { User } from "./User";
import { Group } from "./Group";

@Entity()
export class Report {
  @PrimaryGeneratedColumn()
  report_id!: number;

  @ManyToOne(() => User)
  @JoinColumn({ name: "reporter_id" })
  reporter!: User;

  @ManyToOne(() => User)
  @JoinColumn({ name: "reported_user_id" })
  reported_user!: User;

  @ManyToOne(() => Group)
  @JoinColumn({ name: "group_id" })
  group!: Group;

  @Column()
  reason!: string;

  @Column({ type: "timestamp", default: () => "CURRENT_TIMESTAMP" })
  reported_at!: Date;

  @Column({ default: false })
  resolved!: boolean;
}