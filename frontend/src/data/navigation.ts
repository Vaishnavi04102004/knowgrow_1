import type {Role} from '../types';
export const navByRole:Record<Role,string[]>={
Employee:['Dashboard','My Profile','My Skills','Skill Assessment','My Skill Gaps','Learning Recommendations','My Learning','Mentors','Mentorship Requests','Messages / Chatbox','Knowledge Hub','Discussion Forum','My Progress','Certificates','Notifications','Help & Support'],
Manager:['Dashboard','My Team','Team Skill Inventory','Team Gap Analysis','Skill Gap Heatmap','Employee Assessments','Learning Recommendations','Learning Progress','Mentorship Support','Messages / Team Chat','Alerts & Notifications','Team Reports','Calendar'],
Mentor:['Dashboard','Mentor Profile','Mentorship Requests','My Mentees','Mentee Skill Overview','Development Goals','Mentoring Sessions','Messages / Chatbox','Shared Resources','Knowledge Contributions','Feedback','Notifications'],
'HR Specialist':['Dashboard','Employee Directory','Competency Framework','Role Requirements','Organisation Skill Gap Analysis','Learning Catalogue','Learning Paths','Training Programmes','Mentor Directory','Learning Progress','Feedback & Effectiveness','Reports & Analytics','Notifications'],
'Department Head':['Dashboard','Department Overview','Department Skill Matrix','Gap Heatmap','Workforce Capability','Learning & Mentoring Overview','Risk & Priority Alerts','Reports & Analytics','Announcements','Notifications']};
