export type Role='Employee'|'Manager'|'Mentor'|'HR Specialist'|'Department Head';
export type SkillLevel='Unaware'|'Beginner'|'Intermediate'|'Advanced'|'Expert';
export interface User {id:string;name:string;email:string;role:Role;department:string;team:string;title:string;avatar:string}
export interface SkillRecord {id:string;name:string;category:string;current:SkillLevel|null;required:SkillLevel;status:string}
export interface LearningItem {id:string;title:string;skill:string;status:'Not Started'|'In Progress'|'Completed'|'Certified'|'Expired / Renewal Required';progress:number;due:string;type:string}
export interface Notice {id:string;title:string;detail:string;date:string;read:boolean;kind:string}
