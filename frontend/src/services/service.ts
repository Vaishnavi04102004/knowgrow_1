import type {User,Role,SkillRecord,LearningItem,Notice} from '../types';
const base=(import.meta.env.VITE_API_BASE_URL||'http://localhost:8080/api/v1').replace(/\/$/,'');
async function api<T>(path:string,init?:RequestInit):Promise<T>{
 const response=await fetch(`${base}${path}`,{...init,credentials:'include',headers:{'Content-Type':'application/json',...(init?.headers||{})}});
 if(response.status===401)throw new Error('Your session has expired. Please sign in again.');
 if(response.status===403)throw new Error('You do not have permission to access this information.');
 if(!response.ok){let message=`Request failed (${response.status}).`;try{const body=await response.json();message=body.message||body.error||message}catch{}throw new Error(message)}
 return response.status===204?undefined as T:response.json() as Promise<T>;
}

// Browser-only demo workspace. Demo accounts and records are synthetic and stay in this browser.
const DEMO_USERS_KEY='kg_demo_users_v1';
const DEMO_SESSION_KEY='kg_demo_session_v1';
const demoSeed:User[]=[
 {id:'demo-hr',name:'Avery HR',email:'hr@knowgrow.demo',role:'HR Specialist',department:'People & Culture',team:'HR',title:'HR Specialist',avatar:'AH'},
 {id:'demo-manager',name:'Morgan Manager',email:'manager@knowgrow.demo',role:'Manager',department:'Engineering',team:'Platform',title:'Team Lead',avatar:'MM'},
 {id:'demo-employee',name:'Elliot Employee',email:'employee@knowgrow.demo',role:'Employee',department:'Engineering',team:'Platform',title:'Software Engineer',avatar:'EE'},
 {id:'demo-mentor',name:'Taylor Mentor',email:'mentor@knowgrow.demo',role:'Mentor',department:'Engineering',team:'Platform',title:'Technical Mentor',avatar:'TM'},
 {id:'demo-head',name:'Jordan Head',email:'head@knowgrow.demo',role:'Department Head',department:'Engineering',team:'Leadership',title:'Department Head',avatar:'JH'}
];
function readDemoUsers():User[]{try{const raw=localStorage.getItem(DEMO_USERS_KEY);return raw?JSON.parse(raw) as User[]:demoSeed}catch{return demoSeed}}
function writeDemoUsers(users:User[]){localStorage.setItem(DEMO_USERS_KEY,JSON.stringify(users))}
export const demoService={
 users:readDemoUsers,
 session():User|null{try{const id=localStorage.getItem(DEMO_SESSION_KEY);return readDemoUsers().find(u=>u.id===id)||null}catch{return null}},
 login(email:string,password:string):User{const records=readDemoUsers() as (User & {demoPassword?:string})[];const found=records.find(u=>u.email.toLowerCase()===email.toLowerCase()&&(u.demoPassword||'demo12345')===password);if(!found)throw new Error('Demo login failed. Check your email and password, or register a new demo account.');localStorage.setItem(DEMO_SESSION_KEY,found.id);const {demoPassword,...user}=found;return user},
 loginAs(userId:string):User{const found=readDemoUsers().find(u=>u.id===userId);if(!found)throw new Error('This demo participant is no longer available.');localStorage.setItem(DEMO_SESSION_KEY,found.id);return found},
 register(input:{name:string;email:string;password:string;role:Role;department:string;team:string;title:string}):User{const records=readDemoUsers() as (User & {demoPassword?:string})[];if(records.some(u=>u.email.toLowerCase()===input.email.toLowerCase()))throw new Error('An account with this email already exists.');const user:User & {demoPassword:string}={id:`demo-${Date.now()}`,name:input.name,email:input.email,role:input.role,department:input.department||'General',team:input.team||'Unassigned',title:input.title||input.role,avatar:input.name.split(/\s+/).map(x=>x[0]).join('').slice(0,2).toUpperCase(),demoPassword:input.password};writeDemoUsers([...records,user]);localStorage.setItem(DEMO_SESSION_KEY,user.id);const {demoPassword,...safe}=user;return safe},
 saveUsers(users:User[]){const existing=readDemoUsers() as (User & {demoPassword?:string})[];const passwords=new Map(existing.map(u=>[u.id,u.demoPassword]));writeDemoUsers(users.map(u=>({...u,demoPassword:passwords.get(u.id)||'demo12345'})) as User[])},
 logout(){localStorage.removeItem(DEMO_SESSION_KEY)}
};

export const dataService={
 getSession():Promise<User>{return api('/auth/session')},
 listUsers():Promise<User[]>{return api('/users')},
 createUser(payload:{name:string;email:string;role:Role;department:string;team:string;title:string;password?:string}):Promise<User>{return api('/users',{method:'POST',body:JSON.stringify(payload)})},
 updateUser(id:string,payload:{name:string;email:string;role:Role;department:string;team:string;title:string}):Promise<User>{return api(`/users/${id}`,{method:'PUT',body:JSON.stringify(payload)})},
 login(email:string,password:string):Promise<User>{return api('/auth/login',{method:'POST',body:JSON.stringify({email,password})})},
 register(payload:{name:string;email:string;password:string}):Promise<{message?:string}>{return api('/auth/register',{method:'POST',body:JSON.stringify(payload)})},
 requestPhoneOtp(payload:{name:string;phone:string}):Promise<{message?:string}>{return api('/auth/phone/request-otp',{method:'POST',body:JSON.stringify(payload)})},
 verifyPhoneOtp(payload:{name:string;phone:string;otp:string}):Promise<User>{return api('/auth/phone/verify-otp',{method:'POST',body:JSON.stringify(payload)})},
 beginGoogleAuth():Promise<{authorizationUrl:string}>{return api('/auth/oauth/google',{method:'GET'})},
 logout(){return api('/auth/logout',{method:'POST'})},
 getSkills():Promise<SkillRecord[]>{return api('/skills/me')},
 saveSkills(items:SkillRecord[]){return api('/skills/me',{method:'PUT',body:JSON.stringify(items)})},
 getLearning():Promise<LearningItem[]>{return api('/learning/me')},
 saveLearning(items:LearningItem[]){return api('/learning/me',{method:'PUT',body:JSON.stringify(items)})},
 getNotices():Promise<Notice[]>{return api('/notifications')},
 saveNotices(items:Notice[]){return api('/notifications/read-state',{method:'PUT',body:JSON.stringify(items)})}
};
export function calculateGap(items:SkillRecord[]){const levels=['Unaware','Beginner','Intermediate','Advanced','Expert'];let numerator=0,denominator=0,assessed=0,missing=0,met=0,total=0;for(const skill of items){const required=levels.indexOf(skill.required);if(required<=0)continue;total++;if(!skill.current){missing++;continue;}const current=levels.indexOf(skill.current);numerator+=Math.max(required-current,0);denominator+=required;assessed++;if(current>=required)met++;}return {percentage:denominator?Math.round(100*numerator/denominator):null,assessed,missing,coverage:assessed?Math.round(100*met/assessed):null,numerator,denominator,total};}
